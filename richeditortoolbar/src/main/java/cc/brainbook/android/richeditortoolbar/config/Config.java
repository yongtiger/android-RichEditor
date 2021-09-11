package cc.brainbook.android.richeditortoolbar.config;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;

public abstract class Config {

    /* --------------///[ClickImageSpan]-------------- */
    public static final int IMAGE_MAX_DISPLAY_DIGITS = 4;

    /////??????Android设备最大尺寸（目前为3040）
    public static final int DEFAULT_IMAGE_MAX_WIDTH = 1024;
    public static final int DEFAULT_IMAGE_MAX_HEIGHT = 1024;

    ///ColorDrawable.getWidth()/getHeight()均为-1，此时设置为缺省数值
    public static final int DEFAULT_IMAGE_WIDTH = 200;
    public static final int DEFAULT_IMAGE_HEIGHT = 200;

    ///缩放因子（放大：1+0.5F；缩小：1-0.5F）
    public static final float IMAGE_ZOOM_FACTOR = 0.5F;

    @DrawableRes public static int PLACE_HOLDER_DRAWABLE = android.R.drawable.picture_frame;

    /* --------------///[PreSpan]-------------- */
    @ColorInt public static final int PRE_SPAN_BACKGROUND_COLOR = Color.GRAY;
    public static final int PRE_SPAN_PADDING = 0;

    /* --------------///[BlockSpan]-------------- */
    @ColorInt public static final int BLOCK_SPAN_COLOR = Color.GRAY;
    public static final float BLOCK_SPAN_RADIUS = 10;

    /* --------------///[BorderSpan]-------------- */
    @ColorInt public static final int BORDER_SPAN_COLOR = Color.GRAY;

    /* --------------///[CustomLeadingMarginSpan]-------------- */
    public static final int CUSTOM_LEADING_MARGIN_SPAN_DEFAULT_INDENT = 40;

    /* --------------///[CustomQuoteSpan]-------------- */
    @ColorInt public static final int CUSTOM_QUOTE_SPAN_STANDARD_COLOR = Color.GRAY;
    public static final int CUSTOM_QUOTE_SPAN_STANDARD_STRIPE_WIDTH_PX = 16;
    public static final int CUSTOM_QUOTE_SPAN_STANDARD_GAP_WIDTH_PX = 40;

    /* --------------///[ListItemSpan]-------------- */
    @IntRange(from = 0) public static final int LIST_ITEM_SPAN_DEFAULT_INDICATOR_WIDTH = 20;
    @IntRange(from = 0) public static final int LIST_ITEM_SPAN_DEFAULT_INDICATOR_GAP_WIDTH = 40;
    @ColorInt public static final int LIST_ITEM_SPAN_DEFAULT_INDICATOR_COLOR = Color.GRAY;

    /* --------------///[ListSpan]-------------- */
    @IntRange(from = 0) public static final int LIST_SPAN_DEFAULT_INDENT = 160;

    /* --------------///[LineDividerSpan]-------------- */
    public static final int LINE_DIVIDER_SPAN_DEFAULT_MARGIN_TOP = 0;
    public static final int LINE_DIVIDER_SPAN_DEFAULT_MARGIN_BOTTOM = 0;

    /* --------------///[HeadSpan]-------------- */
    ///参考Html.HEADING_SIZES
    public static final float[] HEAD_SPAN_HEADING_SIZES = {
            1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1f,
    };
    public static final String[] HEAD_SPAN_HEADING_LABELS = {"H1", "H2", "H3", "H4", "H5", "H6"};

    public static final int[] HEAD_SPAN_DEFAULT_MARGIN_TOP = {60, 50, 40, 30, 20, 10};
    public static final int[] HEAD_SPAN_DEFAULT_MARGIN_BOTTOM = {60, 50, 40, 30, 20, 10};

}
