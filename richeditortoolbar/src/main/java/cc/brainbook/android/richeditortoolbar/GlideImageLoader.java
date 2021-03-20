package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class GlideImageLoader {
    ///Glide要求的Image最大尺寸
    ///注意：如果宽高都限制1000，则可能出现(1000, 1800)的情况！
    private static final int DEFAULT_IMAGE_OVERRIDE_WIDTH = 200;//////////////////
    private static final int DEFAULT_IMAGE_OVERRIDE_HEIGHT = 200;//////////////////
    private int mImageOverrideWidth, mImageOverrideHeight;

    private Context mContext;

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
        this(context, DEFAULT_IMAGE_OVERRIDE_WIDTH, DEFAULT_IMAGE_OVERRIDE_HEIGHT);
    }

    public GlideImageLoader(Context context, int imageOverrideWidth, int imageOverrideHeight) {
        mContext = context;
        mImageOverrideWidth = imageOverrideWidth;
        mImageOverrideHeight = imageOverrideHeight;
    }

    public GlideImageLoader(Context context, int imageOverrideWidth, int imageOverrideHeight, @Nullable Drawable placeholderDrawable) {
        this(context, imageOverrideWidth, imageOverrideHeight);
        mPlaceholderDrawable = placeholderDrawable;
    }

    public GlideImageLoader(Context context, int imageOverrideWidth, int imageOverrideHeight, @DrawableRes int placeholderResourceId) {
        this(context, imageOverrideWidth, imageOverrideHeight);
        mPlaceholderResourceId = placeholderResourceId;
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
        } else  {
            options.placeholder(mPlaceholderResourceId);
        }

        Glide.with(mContext)
                .load(viewTagSrc)
                .apply(options)
                .override(mImageOverrideWidth, mImageOverrideHeight) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
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
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (mCallback != null) {
                            mCallback.onResourceReady(resource);
                        }

                        ///[ImageSpan#Glide#GifDrawable]
                        ///https://muyangmin.github.io/glide-docs-cn/doc/targets.html
                        if (resource instanceof GifDrawable) {
                            ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                            ((GifDrawable) resource).start();

                            ///For animated GIFs inside span you need to assign bounds and callback (which is TextView holding that span) to GifDrawable
                            ///https://github.com/koral--/android-gif-drawable/issues/516
                            if (mDrawableCallback != null) {
                                resource.setCallback(mDrawableCallback);
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

}
