package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class GlideImageLoader {

    private final Context mContext;

    @Nullable
    private Drawable mPlaceholderDrawable;
    public void setPlaceholderDrawable(@Nullable Drawable placeholderDrawable) {
        mPlaceholderDrawable = placeholderDrawable;
    }
    @DrawableRes
    private int mPlaceholderResourceId;
    public void setPlaceholderResourceId(@DrawableRes int placeholderResourceId) {
        mPlaceholderResourceId = placeholderResourceId;
    }

    public GlideImageLoader(Context context) {
        mContext = context;
    }

    public interface Callback {
        void onLoadStarted(@Nullable Drawable placeholderDrawable);
        void onResourceReady(@NonNull Drawable drawable);
    }
    private Callback mCallback;
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private Drawable.Callback mDrawableCallback;
    public void setDrawableCallback(Drawable.Callback drawableCallback) {
        mDrawableCallback = drawableCallback;
    }

    public void loadImage(final String viewTagSrc) {
        ///Glide下载图片（使用已经缓存的图片）给imageView
        ///https://muyangmin.github.io/glide-docs-cn/doc/getting-started.html
        //////??????placeholer（占位符）、error（错误符）、fallback（后备回调符）
        ///placeholder(new ColorDrawable(Color.BLACK))   // 或者可以直接使用ColorDrawable
        final RequestOptions options = new RequestOptions();

        ///注意：mPlaceholderDrawable和mPlaceholderResourceId必须至少设置其中一个！如都设置则mPlaceholderDrawable优先
        if (mPlaceholderDrawable != null) {
            options.placeholder(mPlaceholderDrawable);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            ///[FIX#Android KITKAT 4.4 (API 19及以下)使用Vector Drawable出现异常：android.content.res.Resources$NotFoundException:  See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info]
            ///https://stackoverflow.com/questions/34417843/how-to-use-vectordrawables-in-android-api-lower-than-21
            ///https://stackoverflow.com/questions/39419596/resourcesnotfoundexception-file-res-drawable-abc-ic-ab-back-material-xml/41965285
            final Drawable placeholderDrawable = VectorDrawableCompat.create(mContext.getResources(),
                    ///[FIX#Android KITKAT 4.4 (API 19及以下)使用layer-list Drawable出现异常：org.xmlpull.v1.XmlPullParserException: Binary XML file line #2<vector> tag requires viewportWidth > 0
//                    R.drawable.layer_list_placeholder,
                    R.drawable.placeholder,
                    mContext.getTheme());

            options.placeholder(placeholderDrawable);
        } else  {
            options.placeholder(mPlaceholderResourceId);
        }

        Glide.with(mContext)
                .load(viewTagSrc)
                .apply(options)
//                .override(200, 200) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
//                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
//                .fitCenter()    ///fitCenter()会缩放图片让两边都相等或小于ImageView的所需求的边框。图片会被完整显示，可能不能完全填充整个ImageView。
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {	///placeholder
                        if (mCallback != null) {
                            mCallback.onLoadStarted(placeholder);
                        }
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        if (mCallback != null) {
                            mCallback.onResourceReady(drawable);
                        }

                        ///[ImageSpan#Glide#GifDrawable]
                        ///https://muyangmin.github.io/glide-docs-cn/doc/targets.html
                        if (drawable instanceof GifDrawable) {
                            ((GifDrawable) drawable).setLoopCount(GifDrawable.LOOP_FOREVER);
                            ((GifDrawable) drawable).start();

                            ///For animated GIFs inside span you need to assign bounds and callback (which is TextView holding that span) to GifDrawable
                            ///https://github.com/koral--/android-gif-drawable/issues/516
                            if (mDrawableCallback != null) {
                                drawable.setCallback(mDrawableCallback);
                            }
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(mContext.getApplicationContext(),
                                String.format(mContext.getString(R.string.message_image_load_fails), viewTagSrc),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        Toast.makeText(mContext.getApplicationContext(),
//                                String.format(mContext.getString(R.string.message_image_load_is_cancelled), viewTagSrc),
//                                Toast.LENGTH_LONG).show();
                    }
                });
    }

}
