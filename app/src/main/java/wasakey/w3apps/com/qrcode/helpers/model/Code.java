package wasakey.w3apps.com.qrcode.helpers.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import wasakey.w3apps.com.qrcode.helpers.constant.TableNames;
import wasakey.w3apps.com.qrcode.helpers.util.database.BaseEntity;

@Entity(tableName = TableNames.CODES)
public class Code extends BaseEntity {

    /**
     * Constants
     */
    public static final int QR_CODE = 1;
    public static final int BAR_CODE = 2;
    public static final Parcelable.Creator<Code> CREATOR = new Parcelable.Creator<Code>() {
        @Override
        public Code createFromParcel(Parcel source) {
            return new Code(source);
        }

        @Override
        public Code[] newArray(int size) {
            return new Code[size];
        }
    };

    /**
     * Fields
     */
    private String mContent;
    private int mType;
    private String mCodeImagePath;
    private long mTimeStamp;

    public Code() {
    }

    @Ignore
    public Code(String content, int type) {
        mContent = content;
        mType = type;
    }

    @Ignore
    public Code(String content, int type, long timeStamp) {
        mContent = content;
        mType = type;
        mTimeStamp = timeStamp;
    }

    @Ignore
    public Code(String content, int type, String codeImagePath, long timeStamp) {
        mContent = content;
        mType = type;
        mCodeImagePath = codeImagePath;
        mTimeStamp = timeStamp;
    }

    @Ignore
    protected Code(Parcel in) {
        this.mContent = in.readString();
        this.mType = in.readInt();
        this.mTimeStamp = in.readLong();
        this.mCodeImagePath = in.readString();
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getCodeImagePath() {
        return mCodeImagePath;
    }

    public void setCodeImagePath(String codeImagePath) {
        mCodeImagePath = codeImagePath;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    /**
     * Below codes are written in order to make the object parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mContent);
        dest.writeInt(this.mType);
        dest.writeLong(this.mTimeStamp);
        dest.writeString(this.mCodeImagePath);
    }
}
