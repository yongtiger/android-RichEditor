package cc.brainbook.android.richeditor;

import android.content.res.Configuration;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {

    static {
        ///[FIX#Android KITKAT 4.4 (API 19及以下)使用Vector Drawable出现异常：android.content.res.Resources$NotFoundException:  See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info]
        ///https://stackoverflow.com/questions/34417843/how-to-use-vectordrawables-in-android-api-lower-than-21
        ///https://stackoverflow.com/questions/39419596/resourcesnotfoundexception-file-res-drawable-abc-ic-ab-back-material-xml/41965285
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
