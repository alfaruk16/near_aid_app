package com.nearaid.core.database

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import androidx.room.RoomOpenHelper
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.nearaid.core.database.dao.ConversationCacheDao
import com.nearaid.core.database.dao.ConversationCacheDao_Impl
import com.nearaid.core.database.dao.ListingCacheDao
import com.nearaid.core.database.dao.ListingCacheDao_Impl
import java.lang.Class
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import javax.`annotation`.processing.Generated
import kotlin.Any
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.Set

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION"])
public class NearAidDatabase_Impl : NearAidDatabase() {
  private val _listingCacheDao: Lazy<ListingCacheDao> = lazy {
    ListingCacheDao_Impl(this)
  }


  private val _conversationCacheDao: Lazy<ConversationCacheDao> = lazy {
    ConversationCacheDao_Impl(this)
  }


  protected override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
    val _openCallback: SupportSQLiteOpenHelper.Callback = RoomOpenHelper(config, object :
        RoomOpenHelper.Delegate(1) {
      public override fun createAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cached_listings` (`id` TEXT NOT NULL, `feedType` TEXT NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `categoryKey` TEXT, `categoryNameEn` TEXT, `categoryNameBn` TEXT, `urgency` TEXT, `availableUntil` TEXT, `quantity` TEXT, `distanceKm` REAL, `areaLabel` TEXT, `thumbnailUrl` TEXT, `authorId` TEXT NOT NULL, `authorName` TEXT, `authorPhoto` TEXT, `authorTrust` REAL, `authorVerified` INTEGER NOT NULL, `status` TEXT NOT NULL, `createdAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `cached_conversations` (`threadId` TEXT NOT NULL, `claimId` TEXT NOT NULL, `listingId` TEXT NOT NULL, `listingType` TEXT NOT NULL, `listingTitle` TEXT NOT NULL, `listingStatus` TEXT NOT NULL, `counterpartId` TEXT NOT NULL, `counterpartName` TEXT, `counterpartPhoto` TEXT, `counterpartTrust` REAL, `counterpartVerified` INTEGER NOT NULL, `role` TEXT NOT NULL, `lastMessageBody` TEXT, `lastMessageAt` TEXT, `unreadCount` INTEGER NOT NULL, PRIMARY KEY(`threadId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '85f87c2932c56981b2895b2997f50304')")
      }

      public override fun dropAllTables(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `cached_listings`")
        db.execSQL("DROP TABLE IF EXISTS `cached_conversations`")
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onDestructiveMigration(db)
          }
        }
      }

      public override fun onCreate(db: SupportSQLiteDatabase) {
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onCreate(db)
          }
        }
      }

      public override fun onOpen(db: SupportSQLiteDatabase) {
        mDatabase = db
        internalInitInvalidationTracker(db)
        val _callbacks: List<RoomDatabase.Callback>? = mCallbacks
        if (_callbacks != null) {
          for (_callback: RoomDatabase.Callback in _callbacks) {
            _callback.onOpen(db)
          }
        }
      }

      public override fun onPreMigrate(db: SupportSQLiteDatabase) {
        dropFtsSyncTriggers(db)
      }

      public override fun onPostMigrate(db: SupportSQLiteDatabase) {
      }

      public override fun onValidateSchema(db: SupportSQLiteDatabase):
          RoomOpenHelper.ValidationResult {
        val _columnsCachedListings: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(20)
        _columnsCachedListings.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("feedType", TableInfo.Column("feedType", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("categoryKey", TableInfo.Column("categoryKey", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("categoryNameEn", TableInfo.Column("categoryNameEn", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("categoryNameBn", TableInfo.Column("categoryNameBn", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("urgency", TableInfo.Column("urgency", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("availableUntil", TableInfo.Column("availableUntil", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("quantity", TableInfo.Column("quantity", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("distanceKm", TableInfo.Column("distanceKm", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("areaLabel", TableInfo.Column("areaLabel", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("thumbnailUrl", TableInfo.Column("thumbnailUrl", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("authorId", TableInfo.Column("authorId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("authorName", TableInfo.Column("authorName", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("authorPhoto", TableInfo.Column("authorPhoto", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("authorTrust", TableInfo.Column("authorTrust", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("authorVerified", TableInfo.Column("authorVerified", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedListings.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCachedListings: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesCachedListings: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoCachedListings: TableInfo = TableInfo("cached_listings", _columnsCachedListings,
            _foreignKeysCachedListings, _indicesCachedListings)
        val _existingCachedListings: TableInfo = read(db, "cached_listings")
        if (!_infoCachedListings.equals(_existingCachedListings)) {
          return RoomOpenHelper.ValidationResult(false, """
              |cached_listings(com.nearaid.core.database.entity.CachedListingEntity).
              | Expected:
              |""".trimMargin() + _infoCachedListings + """
              |
              | Found:
              |""".trimMargin() + _existingCachedListings)
        }
        val _columnsCachedConversations: HashMap<String, TableInfo.Column> =
            HashMap<String, TableInfo.Column>(15)
        _columnsCachedConversations.put("threadId", TableInfo.Column("threadId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("claimId", TableInfo.Column("claimId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("listingId", TableInfo.Column("listingId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("listingType", TableInfo.Column("listingType", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("listingTitle", TableInfo.Column("listingTitle", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("listingStatus", TableInfo.Column("listingStatus", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("counterpartId", TableInfo.Column("counterpartId", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("counterpartName", TableInfo.Column("counterpartName",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("counterpartPhoto", TableInfo.Column("counterpartPhoto",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("counterpartTrust", TableInfo.Column("counterpartTrust",
            "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("counterpartVerified",
            TableInfo.Column("counterpartVerified", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("lastMessageBody", TableInfo.Column("lastMessageBody",
            "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("lastMessageAt", TableInfo.Column("lastMessageAt", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedConversations.put("unreadCount", TableInfo.Column("unreadCount", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCachedConversations: HashSet<TableInfo.ForeignKey> =
            HashSet<TableInfo.ForeignKey>(0)
        val _indicesCachedConversations: HashSet<TableInfo.Index> = HashSet<TableInfo.Index>(0)
        val _infoCachedConversations: TableInfo = TableInfo("cached_conversations",
            _columnsCachedConversations, _foreignKeysCachedConversations,
            _indicesCachedConversations)
        val _existingCachedConversations: TableInfo = read(db, "cached_conversations")
        if (!_infoCachedConversations.equals(_existingCachedConversations)) {
          return RoomOpenHelper.ValidationResult(false, """
              |cached_conversations(com.nearaid.core.database.entity.CachedConversationEntity).
              | Expected:
              |""".trimMargin() + _infoCachedConversations + """
              |
              | Found:
              |""".trimMargin() + _existingCachedConversations)
        }
        return RoomOpenHelper.ValidationResult(true, null)
      }
    }, "85f87c2932c56981b2895b2997f50304", "a5fa1a50fded968f2afbf922395d9210")
    val _sqliteConfig: SupportSQLiteOpenHelper.Configuration =
        SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build()
    val _helper: SupportSQLiteOpenHelper = config.sqliteOpenHelperFactory.create(_sqliteConfig)
    return _helper
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: HashMap<String, String> = HashMap<String, String>(0)
    val _viewTables: HashMap<String, Set<String>> = HashMap<String, Set<String>>(0)
    return InvalidationTracker(this, _shadowTablesMap, _viewTables,
        "cached_listings","cached_conversations")
  }

  public override fun clearAllTables() {
    super.assertNotMainThread()
    val _db: SupportSQLiteDatabase = super.openHelper.writableDatabase
    try {
      super.beginTransaction()
      _db.execSQL("DELETE FROM `cached_listings`")
      _db.execSQL("DELETE FROM `cached_conversations`")
      super.setTransactionSuccessful()
    } finally {
      super.endTransaction()
      _db.query("PRAGMA wal_checkpoint(FULL)").close()
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM")
      }
    }
  }

  protected override fun getRequiredTypeConverters(): Map<Class<out Any>, List<Class<out Any>>> {
    val _typeConvertersMap: HashMap<Class<out Any>, List<Class<out Any>>> =
        HashMap<Class<out Any>, List<Class<out Any>>>()
    _typeConvertersMap.put(ListingCacheDao::class.java,
        ListingCacheDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ConversationCacheDao::class.java,
        ConversationCacheDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecs(): Set<Class<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: HashSet<Class<out AutoMigrationSpec>> =
        HashSet<Class<out AutoMigrationSpec>>()
    return _autoMigrationSpecsSet
  }

  public override
      fun getAutoMigrations(autoMigrationSpecs: Map<Class<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = ArrayList<Migration>()
    return _autoMigrations
  }

  public override fun listingCacheDao(): ListingCacheDao = _listingCacheDao.value

  public override fun conversationCacheDao(): ConversationCacheDao = _conversationCacheDao.value
}
