package cc.brainbook.android.richeditortoolbar.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import cc.brainbook.android.richeditortoolbar.R;

public class ImageUtil {
    public static Drawable getImageDrawable(Context context, String src) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.a);
        drawable.setBounds(0, 0, 50, 50); //必须设置图片大小，否则不显示
//        Bitmap drawable = BitmapFactory.decodeResource(context.getResources(), R.drawable.a);
//        drawable.setDensity(160);  ///设置默认缩放比，否则会取手机的density
        return drawable;
    }

    private ImageUtil() {}
}
