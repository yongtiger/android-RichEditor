package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.StringDef;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.LineHeightSpan;

///D:\AndroidStudioProjects\_demo_module\_rich_editor\zzhoujay-RichEditor\richeditor\src\main\java\com\zzhoujay\richeditor\span\HeadSpan.java
public class HeadSpan extends AbsoluteSizeSpan implements LineHeightSpan {
    @StringDef({Head.H1, Head.H2, Head.H3, Head.H4, Head.H5, Head.H6})
    public @interface Head {
        String H1 = "H1";
        String H2 = "H2";
        String H3 = "H3";
        String H4 = "H4";
        String H5 = "H5";
        String H6 = "H6";
    }

    private static final int H1_SIZE = 280;
    private static final int H2_SIZE = 240;
    private static final int H3_SIZE = 200;
    private static final int H4_SIZE = 160;
    private static final int H5_SIZE = 120;
    private static final int H6_SIZE = 80;
    private static int getHeadSize(@Head String head) {
        switch (head) {
            case Head.H1:
                return H1_SIZE;
            case Head.H2:
                return H2_SIZE;
            case Head.H3:
                return H3_SIZE;
            case Head.H4:
                return H4_SIZE;
            case Head.H5:
                return H5_SIZE;
            case Head.H6:
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
    private static int getHeadDefaultMarginTop(@Head String head) {
        switch (head) {
            case Head.H1:
                return H1_DEFAULT_MARGIN_TOP;
            case Head.H2:
                return H2_DEFAULT_MARGIN_TOP;
            case Head.H3:
                return H3_DEFAULT_MARGIN_TOP;
            case Head.H4:
                return H4_DEFAULT_MARGIN_TOP;
            case Head.H5:
                return H5_DEFAULT_MARGIN_TOP;
            case Head.H6:
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
    private static int getHeadDefaultMarginBottom(@Head String head) {
        switch (head) {
            case Head.H1:
                return H1_DEFAULT_MARGIN_BOTTOM;
            case Head.H2:
                return H2_DEFAULT_MARGIN_BOTTOM;
            case Head.H3:
                return H3_DEFAULT_MARGIN_BOTTOM;
            case Head.H4:
                return H4_DEFAULT_MARGIN_BOTTOM;
            case Head.H5:
                return H5_DEFAULT_MARGIN_BOTTOM;
            case Head.H6:
                return H6_DEFAULT_MARGIN_BOTTOM;
            default:
                return 0;
        }
    }

    @Head
    private final String mHead;
    @Head
    public String getHead() {
        return mHead;
    }

    public HeadSpan(@Head String head) {
        this(head, getHeadDefaultMarginTop(head), getHeadDefaultMarginBottom(head));
    }

    public HeadSpan(@Head String head, int marginTop, int marginBottom) {
        super(getHeadSize(head));
        mHead = head;
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
    private final int mMarginTop;
    public int getMarginTop() {
        return mMarginTop;
    }
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

}
