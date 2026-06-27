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
import com.nearaid.core.database.entity.CachedListingEntity
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
public class ListingCacheDao_Impl(
  __db: RoomDatabase,
) : ListingCacheDao {
  private val __db: RoomDatabase

  private val __insertionAdapterOfCachedListingEntity: EntityInsertionAdapter<CachedListingEntity>

  private val __preparedStmtOfClearByType: SharedSQLiteStatement
  init {
    this.__db = __db
    this.__insertionAdapterOfCachedListingEntity = object :
        EntityInsertionAdapter<CachedListingEntity>(__db) {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `cached_listings` (`id`,`feedType`,`type`,`title`,`categoryKey`,`categoryNameEn`,`categoryNameBn`,`urgency`,`availableUntil`,`quantity`,`distanceKm`,`areaLabel`,`thumbnailUrl`,`authorId`,`authorName`,`authorPhoto`,`authorTrust`,`authorVerified`,`status`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SupportSQLiteStatement, entity: CachedListingEntity) {
        statement.bindString(1, entity.id)
        statement.bindString(2, entity.feedType)
        statement.bindString(3, entity.type)
        statement.bindString(4, entity.title)
        val _tmpCategoryKey: String? = entity.categoryKey
        if (_tmpCategoryKey == null) {
          statement.bindNull(5)
        } else {
          statement.bindString(5, _tmpCategoryKey)
        }
        val _tmpCategoryNameEn: String? = entity.categoryNameEn
        if (_tmpCategoryNameEn == null) {
          statement.bindNull(6)
        } else {
          statement.bindString(6, _tmpCategoryNameEn)
        }
        val _tmpCategoryNameBn: String? = entity.categoryNameBn
        if (_tmpCategoryNameBn == null) {
          statement.bindNull(7)
        } else {
          statement.bindString(7, _tmpCategoryNameBn)
        }
        val _tmpUrgency: String? = entity.urgency
        if (_tmpUrgency == null) {
          statement.bindNull(8)
        } else {
          statement.bindString(8, _tmpUrgency)
        }
        val _tmpAvailableUntil: String? = entity.availableUntil
        if (_tmpAvailableUntil == null) {
          statement.bindNull(9)
        } else {
          statement.bindString(9, _tmpAvailableUntil)
        }
        val _tmpQuantity: String? = entity.quantity
        if (_tmpQuantity == null) {
          statement.bindNull(10)
        } else {
          statement.bindString(10, _tmpQuantity)
        }
        val _tmpDistanceKm: Double? = entity.distanceKm
        if (_tmpDistanceKm == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpDistanceKm)
        }
        val _tmpAreaLabel: String? = entity.areaLabel
        if (_tmpAreaLabel == null) {
          statement.bindNull(12)
        } else {
          statement.bindString(12, _tmpAreaLabel)
        }
        val _tmpThumbnailUrl: String? = entity.thumbnailUrl
        if (_tmpThumbnailUrl == null) {
          statement.bindNull(13)
        } else {
          statement.bindString(13, _tmpThumbnailUrl)
        }
        statement.bindString(14, entity.authorId)
        val _tmpAuthorName: String? = entity.authorName
        if (_tmpAuthorName == null) {
          statement.bindNull(15)
        } else {
          statement.bindString(15, _tmpAuthorName)
        }
        val _tmpAuthorPhoto: String? = entity.authorPhoto
        if (_tmpAuthorPhoto == null) {
          statement.bindNull(16)
        } else {
          statement.bindString(16, _tmpAuthorPhoto)
        }
        val _tmpAuthorTrust: Double? = entity.authorTrust
        if (_tmpAuthorTrust == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpAuthorTrust)
        }
        val _tmp: Int = if (entity.authorVerified) 1 else 0
        statement.bindLong(18, _tmp.toLong())
        statement.bindString(19, entity.status)
        statement.bindString(20, entity.createdAt)
      }
    }
    this.__preparedStmtOfClearByType = object : SharedSQLiteStatement(__db) {
      public override fun createQuery(): String {
        val _query: String = "DELETE FROM cached_listings WHERE feedType = ?"
        return _query
      }
    }
  }

  public override suspend fun upsertAll(items: List<CachedListingEntity>): Unit =
      CoroutinesRoom.execute(__db, true, object : Callable<Unit> {
    public override fun call() {
      __db.beginTransaction()
      try {
        __insertionAdapterOfCachedListingEntity.insert(items)
        __db.setTransactionSuccessful()
      } finally {
        __db.endTransaction()
      }
    }
  })

  public override suspend fun clearByType(type: String): Unit = CoroutinesRoom.execute(__db, true,
      object : Callable<Unit> {
    public override fun call() {
      val _stmt: SupportSQLiteStatement = __preparedStmtOfClearByType.acquire()
      var _argIndex: Int = 1
      _stmt.bindString(_argIndex, type)
      try {
        __db.beginTransaction()
        try {
          _stmt.executeUpdateDelete()
          __db.setTransactionSuccessful()
        } finally {
          __db.endTransaction()
        }
      } finally {
        __preparedStmtOfClearByType.release(_stmt)
      }
    }
  })

  public override suspend fun getByType(type: String): List<CachedListingEntity> {
    val _sql: String = "SELECT * FROM cached_listings WHERE feedType = ? ORDER BY distanceKm"
    val _statement: RoomSQLiteQuery = acquire(_sql, 1)
    var _argIndex: Int = 1
    _statement.bindString(_argIndex, type)
    val _cancellationSignal: CancellationSignal? = createCancellationSignal()
    return execute(__db, false, _cancellationSignal, object : Callable<List<CachedListingEntity>> {
      public override fun call(): List<CachedListingEntity> {
        val _cursor: Cursor = query(__db, _statement, false, null)
        try {
          val _cursorIndexOfId: Int = getColumnIndexOrThrow(_cursor, "id")
          val _cursorIndexOfFeedType: Int = getColumnIndexOrThrow(_cursor, "feedType")
          val _cursorIndexOfType: Int = getColumnIndexOrThrow(_cursor, "type")
          val _cursorIndexOfTitle: Int = getColumnIndexOrThrow(_cursor, "title")
          val _cursorIndexOfCategoryKey: Int = getColumnIndexOrThrow(_cursor, "categoryKey")
          val _cursorIndexOfCategoryNameEn: Int = getColumnIndexOrThrow(_cursor, "categoryNameEn")
          val _cursorIndexOfCategoryNameBn: Int = getColumnIndexOrThrow(_cursor, "categoryNameBn")
          val _cursorIndexOfUrgency: Int = getColumnIndexOrThrow(_cursor, "urgency")
          val _cursorIndexOfAvailableUntil: Int = getColumnIndexOrThrow(_cursor, "availableUntil")
          val _cursorIndexOfQuantity: Int = getColumnIndexOrThrow(_cursor, "quantity")
          val _cursorIndexOfDistanceKm: Int = getColumnIndexOrThrow(_cursor, "distanceKm")
          val _cursorIndexOfAreaLabel: Int = getColumnIndexOrThrow(_cursor, "areaLabel")
          val _cursorIndexOfThumbnailUrl: Int = getColumnIndexOrThrow(_cursor, "thumbnailUrl")
          val _cursorIndexOfAuthorId: Int = getColumnIndexOrThrow(_cursor, "authorId")
          val _cursorIndexOfAuthorName: Int = getColumnIndexOrThrow(_cursor, "authorName")
          val _cursorIndexOfAuthorPhoto: Int = getColumnIndexOrThrow(_cursor, "authorPhoto")
          val _cursorIndexOfAuthorTrust: Int = getColumnIndexOrThrow(_cursor, "authorTrust")
          val _cursorIndexOfAuthorVerified: Int = getColumnIndexOrThrow(_cursor, "authorVerified")
          val _cursorIndexOfStatus: Int = getColumnIndexOrThrow(_cursor, "status")
          val _cursorIndexOfCreatedAt: Int = getColumnIndexOrThrow(_cursor, "createdAt")
          val _result: MutableList<CachedListingEntity> =
              ArrayList<CachedListingEntity>(_cursor.getCount())
          while (_cursor.moveToNext()) {
            val _item: CachedListingEntity
            val _tmpId: String
            _tmpId = _cursor.getString(_cursorIndexOfId)
            val _tmpFeedType: String
            _tmpFeedType = _cursor.getString(_cursorIndexOfFeedType)
            val _tmpType: String
            _tmpType = _cursor.getString(_cursorIndexOfType)
            val _tmpTitle: String
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle)
            val _tmpCategoryKey: String?
            if (_cursor.isNull(_cursorIndexOfCategoryKey)) {
              _tmpCategoryKey = null
            } else {
              _tmpCategoryKey = _cursor.getString(_cursorIndexOfCategoryKey)
            }
            val _tmpCategoryNameEn: String?
            if (_cursor.isNull(_cursorIndexOfCategoryNameEn)) {
              _tmpCategoryNameEn = null
            } else {
              _tmpCategoryNameEn = _cursor.getString(_cursorIndexOfCategoryNameEn)
            }
            val _tmpCategoryNameBn: String?
            if (_cursor.isNull(_cursorIndexOfCategoryNameBn)) {
              _tmpCategoryNameBn = null
            } else {
              _tmpCategoryNameBn = _cursor.getString(_cursorIndexOfCategoryNameBn)
            }
            val _tmpUrgency: String?
            if (_cursor.isNull(_cursorIndexOfUrgency)) {
              _tmpUrgency = null
            } else {
              _tmpUrgency = _cursor.getString(_cursorIndexOfUrgency)
            }
            val _tmpAvailableUntil: String?
            if (_cursor.isNull(_cursorIndexOfAvailableUntil)) {
              _tmpAvailableUntil = null
            } else {
              _tmpAvailableUntil = _cursor.getString(_cursorIndexOfAvailableUntil)
            }
            val _tmpQuantity: String?
            if (_cursor.isNull(_cursorIndexOfQuantity)) {
              _tmpQuantity = null
            } else {
              _tmpQuantity = _cursor.getString(_cursorIndexOfQuantity)
            }
            val _tmpDistanceKm: Double?
            if (_cursor.isNull(_cursorIndexOfDistanceKm)) {
              _tmpDistanceKm = null
            } else {
              _tmpDistanceKm = _cursor.getDouble(_cursorIndexOfDistanceKm)
            }
            val _tmpAreaLabel: String?
            if (_cursor.isNull(_cursorIndexOfAreaLabel)) {
              _tmpAreaLabel = null
            } else {
              _tmpAreaLabel = _cursor.getString(_cursorIndexOfAreaLabel)
            }
            val _tmpThumbnailUrl: String?
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl)
            }
            val _tmpAuthorId: String
            _tmpAuthorId = _cursor.getString(_cursorIndexOfAuthorId)
            val _tmpAuthorName: String?
            if (_cursor.isNull(_cursorIndexOfAuthorName)) {
              _tmpAuthorName = null
            } else {
              _tmpAuthorName = _cursor.getString(_cursorIndexOfAuthorName)
            }
            val _tmpAuthorPhoto: String?
            if (_cursor.isNull(_cursorIndexOfAuthorPhoto)) {
              _tmpAuthorPhoto = null
            } else {
              _tmpAuthorPhoto = _cursor.getString(_cursorIndexOfAuthorPhoto)
            }
            val _tmpAuthorTrust: Double?
            if (_cursor.isNull(_cursorIndexOfAuthorTrust)) {
              _tmpAuthorTrust = null
            } else {
              _tmpAuthorTrust = _cursor.getDouble(_cursorIndexOfAuthorTrust)
            }
            val _tmpAuthorVerified: Boolean
            val _tmp: Int
            _tmp = _cursor.getInt(_cursorIndexOfAuthorVerified)
            _tmpAuthorVerified = _tmp != 0
            val _tmpStatus: String
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus)
            val _tmpCreatedAt: String
            _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt)
            _item =
                CachedListingEntity(_tmpId,_tmpFeedType,_tmpType,_tmpTitle,_tmpCategoryKey,_tmpCategoryNameEn,_tmpCategoryNameBn,_tmpUrgency,_tmpAvailableUntil,_tmpQuantity,_tmpDistanceKm,_tmpAreaLabel,_tmpThumbnailUrl,_tmpAuthorId,_tmpAuthorName,_tmpAuthorPhoto,_tmpAuthorTrust,_tmpAuthorVerified,_tmpStatus,_tmpCreatedAt)
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
