package com.nearaid.core.database.dao

import android.database.Cursor
import android.os.CancellationSignal
import androidx.room.CoroutinesRoom
import androidx.room.CoroutinesRoom.Companion.execute
import androidx.room.EntityInsertionAdapter
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.RoomSQLiteQuery.Companion.acquire
import androidx.room.SharedSQLiteStatement
import androidx.room.util.createCancellationSignal
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.query
import androidx.sqlite.db.SupportSQLiteStatement
import com.nearaid.core.database.entity.CachedConversationEntity
import java.lang.Class
import java.util.ArrayList
import java.util.concurrent.Callable
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.jvm.JvmStatic

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class ConversationCacheDao_Impl(
  __db: RoomDatabase,
) : ConversationCacheDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfCachedConversationEntity:
      EntityInsertionAdapter<CachedConversationEntity>

  private val __preparedStmtOfClear: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfCachedConversationEntity = object :
        EntityInsertionAdapter<CachedConversationEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `cached_conversations` (`threadId`,`claimId`,`listingId`,`listingType`,`listingTitle`,`listingStatus`,`counterpartId`,`counterpartName`,`counterpartPhoto`,`counterpartTrust`,`counterpartVerified`,`role`,`lastMessageBody`,`lastMessageAt`,`unreadCount`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement,
          entity: CachedConversationEntity) {
        statement.bindString(1, entity.threadId)
        statement.bindString(2, entity.claimId)
        statement.bindString(3, entity.listingId)
        statement.bindString(4, entity.listingType)
        statement.bindString(5, entity.listingTitle)
        statement.bindString(6, entity.listingStatus)
        statement.bindString(7, entity.counterpartId)
        val _tmpCounterpartName: String? = entity.counterpartName
        if (_tmpCounterpartName == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpCounterpartName)
        }
        val _tmpCounterpartPhoto: String? = entity.counterpartPhoto
        if (_tmpCounterpartPhoto == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpCounterpartPhoto)
        }
        val _tmpCounterpartTrust: Double? = entity.counterpartTrust
        if (_tmpCounterpartTrust == null) {
          statement.bindNull(10)
        } else {
          statement.bindDouble(10, _tmpCounterpartTrust)
        }
        val _tmp: Int = if (entity.counterpartVerified) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindString(12, entity.role)
        val _tmpLastMessageBody: String? = entity.lastMessageBody
        if (_tmpLastMessageBody == null) {
          statement.bindNull(13)
        } else {
          statement.bindString(13, _tmpLastMessageBody)
        }
        val _tmpLastMessageAt: String? = entity.lastMessageAt
        if (_tmpLastMessageAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindString(14, _tmpLastMessageAt)
        }
        statement.bindLong(15, entity.unreadCount.toLong())
      }
    }
    this.__preparedStmtOfClear = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM cached_conversations"
        return _query
      }
    }
  }

  public override suspend fun upsertAll(items: List<CachedConversationEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCachedConversationEntity.insert(items)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun clear(): Unit = CoroutinesRoom.execute(__db, true, object :
      Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfClear.acquire()
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfClear.release(_stmt)
      }
    }
  })

  public override suspend fun getAll(): List<CachedConversationEntity> {
    val _sql: String = "SELECT * FROM cached_conversations ORDER BY lastMessageAt DESC"
    val _statement: RoomSQLiteQuery = acquire(_sql, 0)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object :
        Callable<List<CachedConversationEntity>> {
      public override fun call(): List<CachedConversationEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfThreadId: Int = getColumnIndexOrThrow(_cursor, "threadId")
          val _cursorIndexOfClaimId: Int = getColumnIndexOrThrow(_cursor, "claimId")
          val _cursorIndexOfListingId: Int = getColumnIndexOrThrow(_cursor, "listingId")
          val _cursorIndexOfListingType: Int = getColumnIndexOrThrow(_cursor, "listingType")
          val _cursorIndexOfListingTitle: Int = getColumnIndexOrThrow(_cursor, "listingTitle")
          val _cursorIndexOfListingStatus: Int = getColumnIndexOrThrow(_cursor, "listingStatus")
          val _cursorIndexOfCounterpartId: Int = getColumnIndexOrThrow(_cursor, "counterpartId")
          val _cursorIndexOfCounterpartName: Int = getColumnIndexOrThrow(_cursor, "counterpartName")
          val _cursorIndexOfCounterpartPhoto: Int = getColumnIndexOrThrow(_cursor,
              "counterpartPhoto")
          val _cursorIndexOfCounterpartTrust: Int = getColumnIndexOrThrow(_cursor,
              "counterpartTrust")
          val _cursorIndexOfCounterpartVerified: Int = getColumnIndexOrThrow(_cursor,
              "counterpartVerified")
          val _cursorIndexOfRole: Int = getColumnIndexOrThrow(_cursor, "role")
          val _cursorIndexOfLastMessageBody: Int = getColumnIndexOrThrow(_cursor, "lastMessageBody")
          val _cursorIndexOfLastMessageAt: Int = getColumnIndexOrThrow(_cursor, "lastMessageAt")
          val _cursorIndexOfUnreadCount: Int = getColumnIndexOrThrow(_cursor, "unreadCount")
          val _result: MutableList<CachedConversationEntity> =
              ArrayList<CachedConversationEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CachedConversationEntity
            val _tmpThreadId: String
            _tmpThreadId = _cursor.getString(_cursorIndexOfThreadId)
            val _tmpClaimId: String
            _tmpClaimId = _cursor.getString(_cursorIndexOfClaimId)
            val _tmpListingId: String
            _tmpListingId = _cursor.getString(_cursorIndexOfListingId)
            val _tmpListingType: String
            _tmpListingType = _cursor.getString(_cursorIndexOfListingType)
            val _tmpListingTitle: String
            _tmpListingTitle = _cursor.getString(_cursorIndexOfListingTitle)
            val _tmpListingStatus: String
            _tmpListingStatus = _cursor.getString(_cursorIndexOfListingStatus)
            val _tmpCounterpartId: String
            _tmpCounterpartId = _cursor.getString(_cursorIndexOfCounterpartId)
            val _tmpCounterpartName: String?
            if (_cursor.isNull(_cursorIndexOfCounterpartName)) {
              _tmpCounterpartName = null
            } else {
              _tmpCounterpartName = _cursor.getString(_cursorIndexOfCounterpartName)
            }
            val _tmpCounterpartPhoto: String?
            if (_cursor.isNull(_cursorIndexOfCounterpartPhoto)) {
              _tmpCounterpartPhoto = null
            } else {
              _tmpCounterpartPhoto = _cursor.getString(_cursorIndexOfCounterpartPhoto)
            }
            val _tmpCounterpartTrust: Double?
            if (_cursor.isNull(_cursorIndexOfCounterpartTrust)) {
              _tmpCounterpartTrust = null
            } else {
              _tmpCounterpartTrust = _cursor.getDouble(_cursorIndexOfCounterpartTrust)
            }
            val _tmpCounterpartVerified: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfCounterpartVerified)
            _tmpCounterpartVerified = _tmp != 0
            val _tmpRole: String
            _tmpRole = _cursor.getString(_cursorIndexOfRole)
            val _tmpLastMessageBody: String?
            if (_cursor.isNull(_cursorIndexOfLastMessageBody)) {
              _tmpLastMessageBody = null
            } else {
              _tmpLastMessageBody = _cursor.getString(_cursorIndexOfLastMessageBody)
            }
            val _tmpLastMessageAt: String?
            if (_cursor.isNull(_cursorIndexOfLastMessageAt)) {
              _tmpLastMessageAt = null
            } else {
              _tmpLastMessageAt = _cursor.getString(_cursorIndexOfLastMessageAt)
            }
            val _tmpUnreadCount: Int
            _tmpUnreadCount = _cursor.getInt(_cursorIndexOfUnreadCount)
            _item =
                CachedConversationEntity(_tmpThreadId,_tmpClaimId,_tmpListingId,_tmpListingType,_tmpListingTitle,_tmpListingStatus,_tmpCounterpartId,_tmpCounterpartName,_tmpCounterpartPhoto,_tmpCounterpartTrust,_tmpCounterpartVerified,_tmpRole,_tmpLastMessageBody,_tmpLastMessageAt,_tmpUnreadCount)
            _result.add(_item)
          }
          return _result
        } finally {
          _cursor.close()
          _statement.release()
        }
      }
    })
  }

  public companion object {
    @JvmStatic
    public fun getRequiredConverters(): List<Class<*>> = emptyList()
  }
}
