package wasakey.w3apps.com.qrcode.helpers.util.database;


import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import io.reactivex.Completable;

public interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(T entity);

    @Delete
    int delete(T entity);
}
