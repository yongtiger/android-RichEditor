package cc.brainbook.android.richeditortoolbar.util;

import android.os.Parcel;
import android.os.Parcelable;

///保存Parcelable：marshall/unmarshall
///https://blog.csdn.net/jielundewode/article/details/78342191
public abstract class ParcelUtil {
    public static byte[] marshall(Object object) {
        if (object instanceof Parcelable) {
            final Parcel parcel = Parcel.obtain();
            ((Parcelable) object).writeToParcel(parcel, 0);
            parcel.setDataPosition(0);  ///注意：在writeToParcel()之后执行parcel.setDataPosition(0)
            final byte[] bytes = parcel.marshall();

            parcel.recycle();   ///使用完parcel后应及时回收
            return bytes;
        }

        return null;
    }

    public static Parcel unmarshall(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        final Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);  ///注意：在parcel.unmarshall()之后执行parcel.setDataPosition(0)

        return parcel;
    }

}
