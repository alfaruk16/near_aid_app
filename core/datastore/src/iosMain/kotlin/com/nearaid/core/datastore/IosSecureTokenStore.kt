package com.nearaid.core.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

/**
 * iOS [SecureTokenStore] backed by the Keychain. Each field is a `kSecClassGenericPassword` item keyed
 * by (service, account); the Keychain encrypts items at rest and gates access to this app.
 */
@OptIn(ExperimentalForeignApi::class)
class IosSecureTokenStore(
    private val service: String = "com.nearaid.secure.tokens",
) : SecureTokenStore {

    override fun readSession(): StoredSession? {
        val access = read(KEY_ACCESS) ?: return null
        val refresh = read(KEY_REFRESH) ?: return null
        val userId = read(KEY_USER_ID) ?: return null
        return StoredSession(access, refresh, userId)
    }

    override fun save(session: StoredSession) {
        write(KEY_ACCESS, session.accessToken)
        write(KEY_REFRESH, session.refreshToken)
        write(KEY_USER_ID, session.userId)
    }

    override fun updateAccessToken(accessToken: String) = write(KEY_ACCESS, accessToken)

    override fun clear() {
        delete(KEY_ACCESS)
        delete(KEY_REFRESH)
        delete(KEY_USER_ID)
    }

    private fun write(account: String, value: String) {
        delete(account)
        val bytes = value.encodeToByteArray()
        if (bytes.isEmpty()) return
        val cfService = cfString(service)
        val cfAccount = cfString(account)
        val cfData = bytes.usePinned { pinned ->
            CFDataCreate(kCFAllocatorDefault, pinned.addressOf(0).reinterpret(), bytes.size.toLong())
        }
        // Null key/value callbacks: the dictionary does not retain its entries, which is safe because
        // it is used and discarded before we release the CF objects below.
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 0, null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, cfService)
        CFDictionaryAddValue(query, kSecAttrAccount, cfAccount)
        CFDictionaryAddValue(query, kSecValueData, cfData)
        SecItemAdd(query, null)
        CFRelease(query)
        CFRelease(cfData)
        CFRelease(cfService)
        CFRelease(cfAccount)
    }

    private fun read(account: String): String? {
        val cfService = cfString(service)
        val cfAccount = cfString(account)
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 0, null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, cfService)
        CFDictionaryAddValue(query, kSecAttrAccount, cfAccount)
        CFDictionaryAddValue(query, kSecReturnData, kCFBooleanTrue)
        CFDictionaryAddValue(query, kSecMatchLimit, kSecMatchLimitOne)
        return memScoped {
            val result = alloc<CFTypeRefVar>()
            val status = SecItemCopyMatching(query, result.ptr)
            CFRelease(query)
            CFRelease(cfService)
            CFRelease(cfAccount)
            if (status != errSecSuccess) return@memScoped null
            val value = result.value ?: return@memScoped null
            // Inferred to CFDataRef from the target type — reinterpret's argument is the pointee.
            val data: CFDataRef? = value.reinterpret()
            val length = CFDataGetLength(data).toInt()
            val bytePtr = CFDataGetBytePtr(data)
            val decoded = if (bytePtr != null && length > 0) {
                bytePtr.readBytes(length).decodeToString()
            } else {
                null
            }
            CFRelease(data)
            decoded
        }
    }

    private fun delete(account: String) {
        val cfService = cfString(service)
        val cfAccount = cfString(account)
        val query = CFDictionaryCreateMutable(kCFAllocatorDefault, 0, null, null)
        CFDictionaryAddValue(query, kSecClass, kSecClassGenericPassword)
        CFDictionaryAddValue(query, kSecAttrService, cfService)
        CFDictionaryAddValue(query, kSecAttrAccount, cfAccount)
        SecItemDelete(query)
        CFRelease(query)
        CFRelease(cfService)
        CFRelease(cfAccount)
    }

    private fun cfString(value: String) =
        CFStringCreateWithCString(kCFAllocatorDefault, value, kCFStringEncodingUTF8)

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_USER_ID = "user_id"
    }
}
