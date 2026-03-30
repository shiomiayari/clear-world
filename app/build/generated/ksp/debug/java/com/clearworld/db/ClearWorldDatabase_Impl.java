package com.clearworld.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.clearworld.db.dao.AquariumStateDao;
import com.clearworld.db.dao.AquariumStateDao_Impl;
import com.clearworld.db.dao.DailyBlockDao;
import com.clearworld.db.dao.DailyBlockDao_Impl;
import com.clearworld.db.dao.FishDao;
import com.clearworld.db.dao.FishDao_Impl;
import com.clearworld.db.dao.ViewingSessionDao;
import com.clearworld.db.dao.ViewingSessionDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ClearWorldDatabase_Impl extends ClearWorldDatabase {
  private volatile ViewingSessionDao _viewingSessionDao;

  private volatile DailyBlockDao _dailyBlockDao;

  private volatile AquariumStateDao _aquariumStateDao;

  private volatile FishDao _fishDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `viewing_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER, `blockType` TEXT NOT NULL, `date` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_blocks` (`date` TEXT NOT NULL, `blockType` TEXT NOT NULL, `totalSeconds` INTEGER NOT NULL, PRIMARY KEY(`date`, `blockType`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `aquarium_state` (`id` INTEGER NOT NULL, `transparency` REAL NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fish` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `createdAt` INTEGER NOT NULL, `blockType` TEXT NOT NULL, `savedSeconds` INTEGER NOT NULL, `size` TEXT NOT NULL, `colorHue` INTEGER NOT NULL, `colorSaturation` REAL NOT NULL, `isAlive` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '11b96255550bf56cd2240de8432182e5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `viewing_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `daily_blocks`");
        db.execSQL("DROP TABLE IF EXISTS `aquarium_state`");
        db.execSQL("DROP TABLE IF EXISTS `fish`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsViewingSessions = new HashMap<String, TableInfo.Column>(5);
        _columnsViewingSessions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViewingSessions.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViewingSessions.put("endTime", new TableInfo.Column("endTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViewingSessions.put("blockType", new TableInfo.Column("blockType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsViewingSessions.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysViewingSessions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesViewingSessions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoViewingSessions = new TableInfo("viewing_sessions", _columnsViewingSessions, _foreignKeysViewingSessions, _indicesViewingSessions);
        final TableInfo _existingViewingSessions = TableInfo.read(db, "viewing_sessions");
        if (!_infoViewingSessions.equals(_existingViewingSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "viewing_sessions(com.clearworld.db.entity.ViewingSession).\n"
                  + " Expected:\n" + _infoViewingSessions + "\n"
                  + " Found:\n" + _existingViewingSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyBlocks = new HashMap<String, TableInfo.Column>(3);
        _columnsDailyBlocks.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyBlocks.put("blockType", new TableInfo.Column("blockType", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyBlocks.put("totalSeconds", new TableInfo.Column("totalSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyBlocks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyBlocks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyBlocks = new TableInfo("daily_blocks", _columnsDailyBlocks, _foreignKeysDailyBlocks, _indicesDailyBlocks);
        final TableInfo _existingDailyBlocks = TableInfo.read(db, "daily_blocks");
        if (!_infoDailyBlocks.equals(_existingDailyBlocks)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_blocks(com.clearworld.db.entity.DailyBlock).\n"
                  + " Expected:\n" + _infoDailyBlocks + "\n"
                  + " Found:\n" + _existingDailyBlocks);
        }
        final HashMap<String, TableInfo.Column> _columnsAquariumState = new HashMap<String, TableInfo.Column>(3);
        _columnsAquariumState.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAquariumState.put("transparency", new TableInfo.Column("transparency", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAquariumState.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAquariumState = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAquariumState = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAquariumState = new TableInfo("aquarium_state", _columnsAquariumState, _foreignKeysAquariumState, _indicesAquariumState);
        final TableInfo _existingAquariumState = TableInfo.read(db, "aquarium_state");
        if (!_infoAquariumState.equals(_existingAquariumState)) {
          return new RoomOpenHelper.ValidationResult(false, "aquarium_state(com.clearworld.db.entity.AquariumState).\n"
                  + " Expected:\n" + _infoAquariumState + "\n"
                  + " Found:\n" + _existingAquariumState);
        }
        final HashMap<String, TableInfo.Column> _columnsFish = new HashMap<String, TableInfo.Column>(8);
        _columnsFish.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFish.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFish.put("blockType", new TableInfo.Column("blockType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFish.put("savedSeconds", new TableInfo.Column("savedSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFish.put("size", new TableInfo.Column("size", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFish.put("colorHue", new TableInfo.Column("colorHue", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFish.put("colorSaturation", new TableInfo.Column("colorSaturation", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFish.put("isAlive", new TableInfo.Column("isAlive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFish = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFish = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFish = new TableInfo("fish", _columnsFish, _foreignKeysFish, _indicesFish);
        final TableInfo _existingFish = TableInfo.read(db, "fish");
        if (!_infoFish.equals(_existingFish)) {
          return new RoomOpenHelper.ValidationResult(false, "fish(com.clearworld.db.entity.Fish).\n"
                  + " Expected:\n" + _infoFish + "\n"
                  + " Found:\n" + _existingFish);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "11b96255550bf56cd2240de8432182e5", "3fc92c3be494d9db38fa94b260fb5ac8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "viewing_sessions","daily_blocks","aquarium_state","fish");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `viewing_sessions`");
      _db.execSQL("DELETE FROM `daily_blocks`");
      _db.execSQL("DELETE FROM `aquarium_state`");
      _db.execSQL("DELETE FROM `fish`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ViewingSessionDao.class, ViewingSessionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyBlockDao.class, DailyBlockDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AquariumStateDao.class, AquariumStateDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FishDao.class, FishDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ViewingSessionDao viewingSessionDao() {
    if (_viewingSessionDao != null) {
      return _viewingSessionDao;
    } else {
      synchronized(this) {
        if(_viewingSessionDao == null) {
          _viewingSessionDao = new ViewingSessionDao_Impl(this);
        }
        return _viewingSessionDao;
      }
    }
  }

  @Override
  public DailyBlockDao dailyBlockDao() {
    if (_dailyBlockDao != null) {
      return _dailyBlockDao;
    } else {
      synchronized(this) {
        if(_dailyBlockDao == null) {
          _dailyBlockDao = new DailyBlockDao_Impl(this);
        }
        return _dailyBlockDao;
      }
    }
  }

  @Override
  public AquariumStateDao aquariumStateDao() {
    if (_aquariumStateDao != null) {
      return _aquariumStateDao;
    } else {
      synchronized(this) {
        if(_aquariumStateDao == null) {
          _aquariumStateDao = new AquariumStateDao_Impl(this);
        }
        return _aquariumStateDao;
      }
    }
  }

  @Override
  public FishDao fishDao() {
    if (_fishDao != null) {
      return _fishDao;
    } else {
      synchronized(this) {
        if(_fishDao == null) {
          _fishDao = new FishDao_Impl(this);
        }
        return _fishDao;
      }
    }
  }
}
