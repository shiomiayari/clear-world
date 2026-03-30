package com.clearworld.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.clearworld.db.entity.Fish;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FishDao_Impl implements FishDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Fish> __insertionAdapterOfFish;

  private final SharedSQLiteStatement __preparedStmtOfKillAllFish;

  public FishDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFish = new EntityInsertionAdapter<Fish>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `fish` (`id`,`createdAt`,`blockType`,`savedSeconds`,`size`,`colorHue`,`colorSaturation`,`isAlive`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Fish entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindString(3, entity.getBlockType());
        statement.bindLong(4, entity.getSavedSeconds());
        statement.bindString(5, entity.getSize());
        statement.bindLong(6, entity.getColorHue());
        statement.bindDouble(7, entity.getColorSaturation());
        final int _tmp = entity.isAlive() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__preparedStmtOfKillAllFish = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE fish SET isAlive = 0 WHERE isAlive = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Fish fish, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFish.insertAndReturnId(fish);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object killAllFish(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfKillAllFish.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfKillAllFish.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Fish>> observeAliveFish() {
    final String _sql = "SELECT * FROM fish WHERE isAlive = 1 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"fish"}, new Callable<List<Fish>>() {
      @Override
      @NonNull
      public List<Fish> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfBlockType = CursorUtil.getColumnIndexOrThrow(_cursor, "blockType");
          final int _cursorIndexOfSavedSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "savedSeconds");
          final int _cursorIndexOfSize = CursorUtil.getColumnIndexOrThrow(_cursor, "size");
          final int _cursorIndexOfColorHue = CursorUtil.getColumnIndexOrThrow(_cursor, "colorHue");
          final int _cursorIndexOfColorSaturation = CursorUtil.getColumnIndexOrThrow(_cursor, "colorSaturation");
          final int _cursorIndexOfIsAlive = CursorUtil.getColumnIndexOrThrow(_cursor, "isAlive");
          final List<Fish> _result = new ArrayList<Fish>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Fish _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpBlockType;
            _tmpBlockType = _cursor.getString(_cursorIndexOfBlockType);
            final long _tmpSavedSeconds;
            _tmpSavedSeconds = _cursor.getLong(_cursorIndexOfSavedSeconds);
            final String _tmpSize;
            _tmpSize = _cursor.getString(_cursorIndexOfSize);
            final int _tmpColorHue;
            _tmpColorHue = _cursor.getInt(_cursorIndexOfColorHue);
            final float _tmpColorSaturation;
            _tmpColorSaturation = _cursor.getFloat(_cursorIndexOfColorSaturation);
            final boolean _tmpIsAlive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAlive);
            _tmpIsAlive = _tmp != 0;
            _item = new Fish(_tmpId,_tmpCreatedAt,_tmpBlockType,_tmpSavedSeconds,_tmpSize,_tmpColorHue,_tmpColorSaturation,_tmpIsAlive);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAliveFishCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM fish WHERE isAlive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
