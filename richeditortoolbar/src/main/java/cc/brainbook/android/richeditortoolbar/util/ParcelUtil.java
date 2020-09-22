package cc.brainbook.android.richeditortoolbar.util;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import android.util.Base64;

///保存Parcelable：marshall/unmarshall
///https://blog.csdn.net/jielundewode/article/details/78342191
///https://www.programcreek.com/java-api-examples/?code=hortonworks/nifi-android-s2s/nifi-android-s2s-master/s2s/src/main/java/com/hortonworks/hdf/android/sitetosite/util/SerializationUtils.java
public abstract class ParcelUtil {
    public static <T extends Parcelable> byte[] marshall(T parcelable) {
        if (parcelable == null) {
            return null;
        }

        final Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        try {
            return parcel.marshall();
        } finally {
            parcel.recycle();   ///使用完parcel后应及时回收
        }
    }

    public static Parcel unmarshall(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        final Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);  ///注意：必须执行parcel.setDataPosition(0)，而且在parcel.unmarshall()之后

        return parcel;
    }

    ///https://stackoverflow.com/questions/18000093/how-to-marshall-and-unmarshall-a-parcelable-to-a-byte-array-with-help-of-parcel
    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        if (bytes == null) {
            return null;
        }

        final Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    ///https://pastebin.com/rjHnK7Th
    public static <T extends Parcelable> void parcelableToPreferences (@NonNull SharedPreferences preferences, String key, @NonNull T object) {
        byte[] objectBytes = marshall(object);
        String valueToSave = Base64.encodeToString(objectBytes, Base64.DEFAULT);
        preferences.edit().putString(key, valueToSave).apply();
    }
    public static <T extends Parcelable> T parcelableFromPreferences (@NonNull SharedPreferences preferences, String key, Parcelable.Creator<T> creator) {
        String valueToRead = preferences.getString(key, "");
        byte[] objectBytes = Base64.decode(valueToRead, Base64.DEFAULT);
        return unmarshall(objectBytes, creator);
    }

}
