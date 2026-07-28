package com.nearaid.core.proximity

/**
 * Identifies a single in-person handoff. Both devices on the same claim independently construct
 * the token from the shared [claimId] — no server round-trip is needed for the Tier-0 (client-only)
 * proximity check. The BLE payload two phones exchange is [payload]; matching payloads plus a
 * near-enough signal are what "we are physically together on this claim" reduces to.
 *
 * This is deliberately *not* a security token: it proves proximity, not identity. A server-issued,
 * single-use signed nonce can later replace the derived payload without changing the radio code —
 * see the module KDoc and the feature roadmap.
 */
data class HandoffToken(val claimId: String) {

    /**
     * A stable 4-byte code derived from [claimId], identical on every platform. Uses FNV-1a (a
     * fixed, well-defined hash) rather than [String.hashCode] so Android and iOS always agree
     * regardless of any runtime differences.
     */
    fun payload(): ByteArray {
        var hash = FNV_OFFSET_BASIS
        for (byte in claimId.encodeToByteArray()) {
            hash = hash xor byte.toUByte().toUInt()
            hash *= FNV_PRIME
        }
        val value = hash.toInt()
        return byteArrayOf(
            (value ushr 24).toByte(),
            (value ushr 16).toByte(),
            (value ushr 8).toByte(),
            value.toByte(),
        )
    }

    private companion object {
        const val FNV_OFFSET_BASIS = 2166136261u
        const val FNV_PRIME = 16777619u
    }
}
