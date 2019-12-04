package cc.brainbook.android.richeditortoolbar.util;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ParcelableUtil {
    ///保存Parcelable：marshall/unmarshall
    ///https://blog.csdn.net/jielundewode/article/details/78342191
    public static byte[] marshall(Object object) {
        byte[] bytes = new byte[0];
        if (object instanceof Parcelable) {
            Parcelable parcelable = (Parcelable) object;
            Parcel parcel = Parcel.obtain();
            parcel.setDataPosition(0);
            parcelable.writeToParcel(parcel, 0);
            bytes = parcel.marshall();
            parcel.recycle();

        }
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }

}
