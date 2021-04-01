package cc.brainbook.android.richeditortoolbar.config;

public abstract class Config {

    /* --------------///[ClickImageSpan]-------------- */
    public static final int IMAGE_MAX_DISPLAY_DIGITS = 4;
    /////??????Android设备最大尺寸（目前为3040）
    public static final int IMAGE_MAX_WIDTH = 5000;
    public static final int IMAGE_MAX_HEIGHT = 5000;
    ///ColorDrawable.getWidth()/getHeight()均为-1，此时设置为缺省数值
    public static final int IMAGE_DEFAULT_WIDTH = 200;
    public static final int IMAGE_DEFAULT_HEIGHT = 200;
    ///缩放因子（放大：1+0.5F；缩小：1-0.5F）
    public static final float IMAGE_ZOOM_FACTOR = 0.5F;

}
