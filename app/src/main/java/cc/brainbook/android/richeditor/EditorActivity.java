package cc.brainbook.android.richeditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class EditorActivity extends AppCompatActivity {
    public static final String FILE_PROVIDER_AUTHORITIES_SUFFIX = ".file.path.share";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "onCreate: =========================");
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, EditorFragment.newInstance("param1", "param2"))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        Log.d("TAG", "onStart()# =========================");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "onResume()# =========================");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("TAG", "onPause()# =========================");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("TAG", "onStop()# =========================");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG", "onDestroy()# =========================");
        super.onDestroy();
    }

    ///当设置了android:configChanges="smallestScreenSize|screenLayout|orientation|keyboardHidden|screenSize"不会重启Activity，将调用onConfigurationChanged()
    ///但必须在onConfigurationChanged(Configuration newConfig)中根据新的newConfig来配置新布局中各个控件！
    ///Note: To use android:configChanges attribute is also not recommended by Android!
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d("TAG", "onConfigurationChanged()# =========================");
        super.onConfigurationChanged(newConfig);

        //////??????[含有RichEditor的Activity#重启时如有对话框则不再显示，造成无法接收拍照等，暂时禁止重启！以后考虑优化]
//        recreate();
    }

}