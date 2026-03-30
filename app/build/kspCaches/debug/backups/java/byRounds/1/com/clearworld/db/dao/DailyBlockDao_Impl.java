package com.clearworld.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.clearworld.db.entity.DailyBlock;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DailyBlockDao_Impl implements DailyBlockDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyBlock> __insertionAdapterOfDailyBlock;

  public DailyBlockDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyBlock = new EntityInsertionAdapter<DailyBlock>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_blocks` (`date`,`blockType`,`totalSeconds`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyBlock entity) {
        statement.bindString(1, entity.getDate());
        statement.bindString(2, entity.getBlockType());
        statement.bindLong(3, entity.getTotalSeconds());
      }
    };
  }

  @Override
  public Object upsert(final DailyBlock dailyBlock, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyBlock.insert(dailyBlock);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final String date, final String blockType,
      final Continuation<? super DailyBlock> $completion) {
    final String _sql = "SELECT * FROM daily_blocks WHERE date = ? AND blockType = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    _argIndex = 2;
    _statement.bindString(_argIndex, blockType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyBlock>() {
      @Override
      @Nullable
      public DailyBlock call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfBlockType = CursorUtil.getColumnIndexOrThrow(_cursor, "blockType");
          final int _cursorIndexOfTotalSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSeconds");
          final DailyBlock _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpBlockType;
            _tmpBlockType = _cursor.getString(_cursorIndexOfBlockType);
            final long _tmpTotalSeconds;
            _tmpTotalSeconds = _cursor.getLong(_cursorIndexOfTotalSeconds);
            _result = new DailyBlock(_tmpDate,_tmpBlockType,_tmpTotalSeconds);
          } else {
            _result = null;
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
