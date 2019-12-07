package cc.brainbook.android.richeditortoolbar.util;

import android.os.Parcel;
import android.os.Parcelable;

///保存Parcelable：marshall/unmarshall
///https://blog.csdn.net/jielundewode/article/details/78342191
public abstract class ParcelableUtil {
    public static byte[] marshall(Parcel parcel) {
        final byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        final Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }

    public static Parcel getParcel(Object object) {
        if (object instanceof Parcelable) {
            final Parcelable parcelable = (Parcelable) object;
            final Parcel parcel = Parcel.obtain();
            parcel.setDataPosition(0);
            parcelable.writeToParcel(parcel, 0);
            return parcel;
        } else {
            return null;
        }
    }
}
