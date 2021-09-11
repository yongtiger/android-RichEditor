package cc.brainbook.android.richeditortoolbar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.util.AttributeSet;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cc.brainbook.android.richeditortoolbar.R;

/**
 * @创建者 CSDN_LQR
 * @时间 2018/9/4
 * @描述 ImageView扩展控件
 * <p>
 * 1、对 ScaleType.MATRIX 进行封装拓展
 */
///https://juejin.cn/post/6844903670174351374
public class ImageViewExt extends AppCompatImageView {

    public static final int SCALE_TYPE_MATRIX_LEFT_CROP   = 1; // 等比例缩放，当图像超出控件尺寸时，保留左边，其余部分剪切掉。
    public static final int SCALE_TYPE_MATRIX_RIGHT_CROP  = 2; // 等比例缩放，当图像超出控件尺寸时，保留右边，其余部分剪切掉。
    public static final int SCALE_TYPE_MATRIX_CENTER_CROP = 3; // 等比例缩放，当图像超出控件尺寸时，保留中间，其余部分剪切掉。

    @IntDef({SCALE_TYPE_MATRIX_LEFT_CROP, SCALE_TYPE_MATRIX_RIGHT_CROP, SCALE_TYPE_MATRIX_CENTER_CROP})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ScaleTypeMatrixExt {
    }

    private int mScaleTypeMatrixExt;

    public ImageViewExt(Context context) {
        this(context, null);
    }

    public ImageViewExt(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewExt(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageViewExt);
        mScaleTypeMatrixExt = typedArray.getInt(R.styleable.ImageViewExt_ive_scale_type_matrix_ext, -1);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        handScaleTypeMatrixExt();
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        handScaleTypeMatrixExt();
    }

    private void handScaleTypeMatrixExt() {
        if (this.getScaleType() == ScaleType.MATRIX && mScaleTypeMatrixExt != -1) {
            // 图片实际尺寸
            final int dwidth = getDrawable().getIntrinsicWidth();
            final int dheight = getDrawable().getIntrinsicHeight();
            // ImageView图片显示尺寸
            final int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
            final int vheight = getHeight() - getPaddingTop() - getPaddingBottom();
            float scale;
            float dx = 0, dy = 0;
            if (dwidth * vheight > vwidth * dheight) {      // 图片宽高比 > 控件宽高比
                scale = (float) vheight / (float) dheight;
                switch (mScaleTypeMatrixExt) {
                    case SCALE_TYPE_MATRIX_LEFT_CROP:
                        dx = 0;                             // 保留左边
                        break;
                    case SCALE_TYPE_MATRIX_RIGHT_CROP:
                        dx = (vwidth - dwidth * scale);     // 保留右边
                        break;
                    case SCALE_TYPE_MATRIX_CENTER_CROP:
                        dx = (vwidth - dwidth * scale) * 0.5f; // 保留中间（效果与 CENTER_CROP 一样）
                        break;
                    default:
                        break;
                }
            } else {
                scale = (float) vwidth / (float) dwidth;
                // dy = (vheight - dheight * scale) * 0.5f; // 根据实际情况编写，默认为0，保留上边
            }
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            matrix.postTranslate(Math.round(dx), Math.round(dy));
            this.setImageMatrix(matrix);
        }
    }

    public void setScaleTypeMatrixExt(@ScaleTypeMatrixExt int scaleTypeMatrixExt) {
        this.mScaleTypeMatrixExt = scaleTypeMatrixExt;
        requestLayout();
    }
}
