package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.support.annotation.StringDef;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.LineHeightSpan;

import com.google.gson.annotations.Expose;

///D:\AndroidStudioProjects\_demo_module\_rich_editor\zzhoujay-RichEditor\richeditor\src\main\java\com\zzhoujay\richeditor\span\HeadSpan.java
public class HeadSpan extends AbsoluteSizeSpan implements LineHeightSpan {
    //////??????参考Html.HEADING_SIZES
    public static String getHeadText(int level) {
        switch (level) {
            case 0:
                return "H1";
            case 1:
                return "H2";
            case 2:
                return "H3";
            case 3:
                return "H4";
            case 4:
                return "H5";
            case 5:
                return "H6";
            default:
                return "H";
        }
    }

    private static final int H1_SIZE = 280;
    private static final int H2_SIZE = 240;
    private static final int H3_SIZE = 200;
    private static final int H4_SIZE = 160;
    private static final int H5_SIZE = 120;
    private static final int H6_SIZE = 80;
    private static int getHeadSize(int level) {
        switch (level) {
            case 0:
                return H1_SIZE;
            case 1:
                return H2_SIZE;
            case 2:
                return H3_SIZE;
            case 3:
                return H4_SIZE;
            case 4:
                return H5_SIZE;
            case 5:
                return H6_SIZE;
            default:
                return 0;
        }
    }

    private static final int H1_DEFAULT_MARGIN_TOP = 60;
    private static final int H2_DEFAULT_MARGIN_TOP = 50;
    private static final int H3_DEFAULT_MARGIN_TOP = 40;
    private static final int H4_DEFAULT_MARGIN_TOP = 30;
    private static final int H5_DEFAULT_MARGIN_TOP = 20;
    private static final int H6_DEFAULT_MARGIN_TOP = 10;
    private static int getHeadDefaultMarginTop(int level) {
        switch (level) {
            case 0:
                return H1_DEFAULT_MARGIN_TOP;
            case 1:
                return H2_DEFAULT_MARGIN_TOP;
            case 2:
                return H3_DEFAULT_MARGIN_TOP;
            case 3:
                return H4_DEFAULT_MARGIN_TOP;
            case 4:
                return H5_DEFAULT_MARGIN_TOP;
            case 5:
                return H6_DEFAULT_MARGIN_TOP;
            default:
                return 0;
        }
    }

    private static final int H1_DEFAULT_MARGIN_BOTTOM = 60;
    private static final int H2_DEFAULT_MARGIN_BOTTOM = 50;
    private static final int H3_DEFAULT_MARGIN_BOTTOM = 40;
    private static final int H4_DEFAULT_MARGIN_BOTTOM = 30;
    private static final int H5_DEFAULT_MARGIN_BOTTOM = 20;
    private static final int H6_DEFAULT_MARGIN_BOTTOM = 10;
    private static int getHeadDefaultMarginBottom(int level) {
        switch (level) {
            case 0:
                return H1_DEFAULT_MARGIN_BOTTOM;
            case 1:
                return H2_DEFAULT_MARGIN_BOTTOM;
            case 2:
                return H3_DEFAULT_MARGIN_BOTTOM;
            case 3:
                return H4_DEFAULT_MARGIN_BOTTOM;
            case 4:
                return H5_DEFAULT_MARGIN_BOTTOM;
            case 5:
                return H6_DEFAULT_MARGIN_BOTTOM;
            default:
                return 0;
        }
    }

    @Expose
    private int mLevel;
    public int getLevel() {
        return mLevel;
    }

    public HeadSpan(int level) {
        this(level, getHeadDefaultMarginTop(level), getHeadDefaultMarginBottom(level));
    }

    public HeadSpan(int level, int marginTop, int marginBottom) {
        super(getHeadSize(level));
        mLevel = level;
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;
    }
    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        applyStyle(ds);
    }

    @Override
    public void updateMeasureState(TextPaint ds) {
        super.updateMeasureState(ds);
        applyStyle(ds);
    }

    ///StyleSpan#apply(Paint paint, int style)
    private static void applyStyle(Paint paint) {
        int style = Typeface.BOLD;
        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int want = oldStyle | style;

        Typeface tf;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }

        int fake = want & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }


    /* ----------- LineHeightSpan ------------ */
    @Expose
    private final int mMarginTop;
    public int getMarginTop() {
        return mMarginTop;
    }
    @Expose
    private final int mMarginBottom;
    public int getMarginBottom() {
        return mMarginBottom;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        if (lineHeight == 0) {  ///避免多行文本时重复叠加计算
            fm.top -= mMarginTop;
            fm.ascent -= mMarginTop;

            fm.bottom += mMarginBottom;
            fm.descent += mMarginBottom;
        }
    }


    public static final Creator<HeadSpan> CREATOR = new Creator<HeadSpan>() {
        @Override
        public HeadSpan createFromParcel(Parcel in) {
            ///注意：必须按照成员变量声明的顺序！
            int level = in.readInt();
            final int marginTop = in.readInt();
            final int marginBottom = in.readInt();
            return new HeadSpan(level, marginTop, marginBottom);
        }

        @Override
        public HeadSpan[] newArray(int size) {
            return new HeadSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mLevel);
        dest.writeInt(mMarginTop);
        dest.writeInt(mMarginBottom);
    }

}
