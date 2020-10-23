package wasakey.w3apps.com.qrcode.helpers.model;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import io.reactivex.Flowable;
import wasakey.w3apps.com.qrcode.helpers.constant.TableNames;
import wasakey.w3apps.com.qrcode.helpers.util.database.BaseDao;

@Dao

public interface CodeDao extends BaseDao<Code> {
    @Query("SELECT * FROM " + TableNames.CODES)
    Flowable<List<Code>> getAllFlowableCodes();

}
