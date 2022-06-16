package cc.brainbook.android.richeditortoolbar.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.brainbook.android.richeditortoolbar.GlideImageLoader;
import cc.brainbook.android.richeditortoolbar.ImageSpanOnClickListener;
import cc.brainbook.android.richeditortoolbar.R;
import cc.brainbook.android.richeditortoolbar.bean.SpanBean;
import cc.brainbook.android.richeditortoolbar.bean.TextBean;
import cc.brainbook.android.richeditortoolbar.interfaces.IBlockCharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IStyle;
import cc.brainbook.android.richeditortoolbar.span.character.BorderSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.block.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.character.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.character.BlockSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CodeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomAbsoluteSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomBackgroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomFontFamilySpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomForegroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.CustomLeadingMarginSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomRelativeSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomScaleXSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomStrikethroughSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomSubscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomSuperscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomURLSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.DivSpan;
import cc.brainbook.android.richeditortoolbar.span.paragraph.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.character.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.paragraph.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListItemSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.PreSpan;
import cc.brainbook.android.richeditortoolbar.span.block.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.ParcelUtil;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;

import static cc.brainbook.android.richeditortoolbar.config.Config.AUDIO_DRAWABLE;
import static cc.brainbook.android.richeditortoolbar.config.Config.HEAD_SPAN_HEADING_LABELS;
import static cc.brainbook.android.richeditortoolbar.config.Config.OBJECT_REPLACEMENT_TEXT;
import static cc.brainbook.android.richeditortoolbar.config.Config.PLACE_HOLDER_DRAWABLE;
import static cc.brainbook.android.richeditortoolbar.config.Config.VIDEO_DRAWABLE;

public abstract class ToolbarHelper {

    public static final ArrayList<Class<? extends INestParagraphStyle>> sNestParagraphStyleSpanClassList = new ArrayList<Class<? extends INestParagraphStyle>>(){{
        add(DivSpan.class);
        add(CustomLeadingMarginSpan.class);
        add(AlignNormalSpan.class);
        add(AlignCenterSpan.class);
        add(AlignOppositeSpan.class);
        add(ListSpan.class);
        add(ListItemSpan.class);    ///注意：必须在ListSpan之后！否则loadSpansFromSpanBeans()中的getParentNestSpan()将返回null
        add(CustomQuoteSpan.class);
        add(PreSpan.class);
    }};

    public static final ArrayList<Class<? extends IParagraphStyle>> sParagraphStyleSpanClassList = new ArrayList<Class<? extends IParagraphStyle>>(){{
        add(HeadSpan.class);
        add(LineDividerSpan.class);
        addAll(sNestParagraphStyleSpanClassList);
    }};

    public static final ArrayList<Class<? extends IBlockCharacterStyle>> sBlockCharacterStyleSpanClassList = new ArrayList<Class<? extends IBlockCharacterStyle>>(){{
        add(CustomURLSpan.class);
        add(VideoSpan.class);
        add(AudioSpan.class);
        add(CustomImageSpan.class);
    }};

    public static final ArrayList<Class<? extends ICharacterStyle>> sCharacterStyleSpanClassList = new ArrayList<Class<? extends ICharacterStyle>>(){{
        add(BoldSpan.class);
        add(ItalicSpan.class);
        add(CustomUnderlineSpan.class);
        add(CustomStrikethroughSpan.class);
        add(CustomSuperscriptSpan.class);
        add(CustomSubscriptSpan.class);
        add(CustomForegroundColorSpan.class);
        add(CustomBackgroundColorSpan.class);
        add(CustomFontFamilySpan.class);
        add(CustomAbsoluteSizeSpan.class);
        add(CustomRelativeSizeSpan.class);
        add(CustomScaleXSpan.class);
        add(CodeSpan.class);
        add(BlockSpan.class);
        add(BorderSpan.class);
        addAll(sBlockCharacterStyleSpanClassList);
    }};

    public static final ArrayList<Class<? extends IStyle>> sAllClassList = new ArrayList<Class<? extends IStyle>>(){{
        addAll(sCharacterStyleSpanClassList);
        addAll(sParagraphStyleSpanClassList);
    }};

    public static int getSpanFlags(Object object) {
        if (object instanceof IParagraphStyle) {
            return Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
        } else if (object instanceof IBlockCharacterStyle) {
            return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        } else {
            return Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
        }
    }


    /* ------------------------------------------------------------------------------------------------------------ */
    public static <T extends IParagraphStyle> void updateParagraphView(Context context, @NonNull View view, Class<T> clazz, Spannable spannable, int start, int end) {
        ///注意：因为可能要用到spans.size()，所以不应使用getParentSpan()
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, start, end, true);    ///按照spanEnd升序
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);

            ///如果span包含了选中区间开始位置所在行的首尾[start, end]，则select
            ///单光标选择时spanStart <= start && end < spanEnd，当spanEnd前一字符为 '\n'时spanStart <= start && end <= spanEnd
            if (start < end || spanStart <= start && (end < spanEnd || spannable.charAt(spanEnd - 1) != '\n' && end == spanEnd)) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                }

                ///段落span（带初始化参数）：List
                if (clazz == ListSpan.class) {
                    final int listStart = ((ListSpan) span).getStart();
                    final boolean isReversed = ((ListSpan) span).isReversed();
                    final int listType = ((ListSpan) span).getListType();
                    view.setTag(R.id.view_tag_list_start, listStart);
                    view.setTag(R.id.view_tag_list_is_reversed, isReversed);
                    view.setTag(R.id.view_tag_list_list_type, listType);
                }

                ///段落span（带参数）：Head
                else if (clazz == HeadSpan.class) {
                    final int level = ((HeadSpan) span).getLevel();
                    view.setTag(level);
                    final String headText = HEAD_SPAN_HEADING_LABELS[level];
                    if (!headText.equals(((TextView) view).getText().toString())) {
                        ((TextView) view).setText(headText);
                    }
                }

                ///注意：找到第一个就退出，不必继续找了。因为getFilteredSpans()返回的是按照spanEnd升序排序后的spans
                return;
            }
        }

        if (view.isSelected()) {
            view.setSelected(false);
        }

        ///段落span（带初始化参数）：List
        if (clazz == ListSpan.class) {
            view.setTag(R.id.view_tag_list_start, null);
            view.setTag(R.id.view_tag_list_is_reversed, null);
            view.setTag(R.id.view_tag_list_list_type, null);
        } else {
            view.setTag(null);
        }

        ///设置为缺省文字
        ///段落span（带参数）：Head
        if (clazz == HeadSpan.class) {
            ((TextView) view).setText(context.getString(R.string.layout_toolbar_text_head));
        }

    }

    public static <T extends ICharacterStyle> void updateCharacterStyleView(Context context, @NonNull View view, Class<T> clazz, Spannable spannable, int start, int end) {
        ///注意：因为CustomURLSpan、CustomImageSpan等要用到spans.size()，所以不应使用getParentSpan()
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, start, end, true);    ///按照spanEnd升序
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);

            ///如果不是单光标、或者span在光标区间外
            ///如果isBlockCharacterStyle为false，并且上光标尾等于span尾
            if (start < end || spanStart < start && (end < spanEnd || !(IBlockCharacterStyle.class.isAssignableFrom(clazz)) && end == spanEnd)) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                }

                ///字符span（带参数）：URL
                if (clazz == CustomURLSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
                        final String text = String.valueOf(spannable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                        final String url = ((CustomURLSpan) span).getURL();
                        view.setTag(R.id.view_tag_url_text, text);
                        view.setTag(R.id.view_tag_url_url, url);
                    } else {
                        view.setTag(R.id.view_tag_url_text, null);
                        view.setTag(R.id.view_tag_url_url, null);
                    }
                }

                ///字符span（带参数）：Image
                else if (clazz == CustomImageSpan.class || clazz == VideoSpan.class || clazz == AudioSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
//                        final String text = String.valueOf(spannable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                        final String text = OBJECT_REPLACEMENT_TEXT;

                        final String uri = clazz == CustomImageSpan.class ? null :
                                clazz == VideoSpan.class ? ((VideoSpan) span).getUri() : ((AudioSpan) span).getUri();
                        final String src = ((CustomImageSpan) span).getSource();

//                        ///从text中解析出width\height\align
//                        final String strWidth = StringUtil.getParameter(text, "width=", " ");
//                        final String strHeight = StringUtil.getParameter(text, "height=", " ");
//                        final String strAlign = StringUtil.getParameter(text, "align=", "]");
//                        final int width = parseInt(strWidth);
//                        final int height = parseInt(strHeight);
//                        final int align = strAlign == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : Integer.parseInt(strAlign);
                        final int width = ((CustomImageSpan) span).getImageWidth();
                        final int height = ((CustomImageSpan) span).getImageHeight();
                        final int align = ((CustomImageSpan) span).getVerticalAlignment();

                        view.setTag(R.id.view_tag_image_text, text);
                        view.setTag(R.id.view_tag_image_uri, uri);
                        view.setTag(R.id.view_tag_image_src, src);
                        view.setTag(R.id.view_tag_image_width, width);
                        view.setTag(R.id.view_tag_image_height, height);
                        view.setTag(R.id.view_tag_image_align, align);
                    } else {
                        view.setTag(R.id.view_tag_image_text, null);
                        view.setTag(R.id.view_tag_image_uri, null);
                        view.setTag(R.id.view_tag_image_src, null);
                        view.setTag(R.id.view_tag_image_width, null);
                        view.setTag(R.id.view_tag_image_height, null);
                        view.setTag(R.id.view_tag_image_align, null);
                    }
                }

                ///字符span（带参数）：ForegroundColor、BackgroundColor
                else if (clazz == CustomForegroundColorSpan.class) {
                    @ColorInt final int foregroundColor = ((CustomForegroundColorSpan) span).getForegroundColor();
                    view.setBackgroundColor(foregroundColor);
                } else if (clazz == CustomBackgroundColorSpan.class) {
                    @ColorInt final int backgroundColor = ((CustomBackgroundColorSpan) span).getBackgroundColor();
                    view.setBackgroundColor(backgroundColor);
                }

                ///字符span（带参数）：FontFamily
                else if (clazz == CustomFontFamilySpan.class) {
                    final String family = ((CustomFontFamilySpan) span).getFamily();
                    view.setTag(family);
                    ((TextView) view).setText(family);
                }

                ///字符span（带参数）：AbsoluteSize
                else if (clazz == CustomAbsoluteSizeSpan.class) {
                    final int size = ((CustomAbsoluteSizeSpan) span).getSize();
                    view.setTag(size);
                    ((TextView) view).setText(String.valueOf(size));
                }

                ///字符span（带参数）：RelativeSize
                else if (clazz == CustomRelativeSizeSpan.class) {
                    final float sizeChange = ((CustomRelativeSizeSpan) span).getSizeChange();
                    view.setTag(sizeChange);
                    ((TextView) view).setText(String.valueOf(sizeChange));
                }

                ///字符span（带参数）：ScaleX
                else if (clazz == CustomScaleXSpan.class) {
                    final float scaleX = ((CustomScaleXSpan) span).getScaleX();
                    view.setTag(scaleX);
                    ((TextView) view).setText(String.valueOf(scaleX));
                }

                ///注意：找到第一个就退出，不必继续找了。因为getFilteredSpans()返回的是按照spanEnd升序排序后的spans
                return;
            }
        }

        if (view.isSelected()) {
            view.setSelected(false);
        }

        ///字符span（带参数）：URL
        if (clazz == CustomURLSpan.class) {
            ///初始化对话框：无、单选、多选，只有单选时才初始化对话框
            if (start < end) {
                final String text = String.valueOf(spannable.toString().toCharArray(), start, end - start);
                view.setTag(R.id.view_tag_url_text, text);///以选中的文本作为url_text
                view.setTag(R.id.view_tag_url_url, null);
            } else {
                view.setTag(R.id.view_tag_url_text, null);
                view.setTag(R.id.view_tag_url_url, null);
            }
        }

        ///字符span（带参数）：Image
        else if (clazz == CustomImageSpan.class || clazz == VideoSpan.class || clazz == AudioSpan.class) {
            view.setTag(R.id.view_tag_image_text, null);
            view.setTag(R.id.view_tag_image_uri, null);
            view.setTag(R.id.view_tag_image_src, null);
            view.setTag(R.id.view_tag_image_width, null);
            view.setTag(R.id.view_tag_image_height, null);
            view.setTag(R.id.view_tag_image_align, null);
        }

        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == CustomForegroundColorSpan.class || clazz == CustomBackgroundColorSpan.class) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            ((TextView) view).setText(context.getString(R.string.layout_toolbar_text_font_family));
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            ((TextView) view).setText(context.getString(R.string.layout_toolbar_text_absolute_size));
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            ((TextView) view).setText(context.getString(R.string.layout_toolbar_text_relative_size));
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            ((TextView) view).setText(context.getString(R.string.layout_toolbar_text_scale_x));
        }

        else {
            view.setTag(null);
        }
    }


    /* ------------------------------------------------------------------------------------------------------------ */
    ///ViewParameter用于保存toolbar按钮的点选状态（如view.getBackground()，view.getTag()，view.getTag(R.id.url_text等)）
    public static <T extends IStyle> boolean isSameWithViewParameter(View view, Class<T> clazz, T span) {
        ///段落span（带参数）：Head
        if (clazz == HeadSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final int spanLevel = ((HeadSpan) span).getLevel();
            final int viewTagLevel = (Integer) view.getTag();
            return spanLevel == viewTagLevel;
        }

        ///段落span（带初始化参数）：List
        else if (clazz == ListSpan.class) {
            if (view.getTag(R.id.view_tag_list_start) == null
                    || view.getTag(R.id.view_tag_list_is_reversed) == null
                    || view.getTag(R.id.view_tag_list_list_type) == null) {
                return false;
            }
            final int start = ((ListSpan) span).getStart();
            final boolean isReversed = ((ListSpan) span).isReversed();
            final int listType = ((ListSpan) span).getListType();
            return start == (int) view.getTag(R.id.view_tag_list_start)
                    && isReversed == (boolean) view.getTag(R.id.view_tag_list_is_reversed)
                    && listType == (int) view.getTag(R.id.view_tag_list_list_type);
        }

        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == CustomForegroundColorSpan.class) {
            @ColorInt final int foregroundColor = ((CustomForegroundColorSpan) span).getForegroundColor();
            final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
            return foregroundColor == colorDrawable.getColor();
        } else if (clazz == CustomBackgroundColorSpan.class) {
            @ColorInt final int backgroundColor = ((CustomBackgroundColorSpan) span).getBackgroundColor();
            final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
            return backgroundColor == colorDrawable.getColor();
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final String spanFamily = ((CustomFontFamilySpan) span).getFamily();
            final String viewTagFontFamily = (String) view.getTag();
            return TextUtils.equals(spanFamily, viewTagFontFamily);
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final int spanSize = ((CustomAbsoluteSizeSpan) span).getSize();
            final int viewTagAbsoluteSize = Integer.parseInt(String.valueOf(view.getTag()));
            return spanSize == viewTagAbsoluteSize;
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanSizeChange = ((CustomRelativeSizeSpan) span).getSizeChange();
            final float viewTagRelativeSize = Float.parseFloat(String.valueOf(view.getTag()));
            return spanSizeChange == viewTagRelativeSize;
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanScaleX = ((CustomScaleXSpan) span).getScaleX();
            final float viewTagScaleX = Float.parseFloat(String.valueOf(view.getTag()));
            return spanScaleX == viewTagScaleX;
        }

        else {
            return true;
        }
    }

    ///注意：不包括block span（如URLSpan、ImageSpan）
    @Nullable
    public static <T extends IStyle> T filterSpanByCompareSpanOrViewParameter(View view, Class<T> clazz, T span, T compareSpan) {
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        if (clazz == CustomForegroundColorSpan.class) {
            @ColorInt final int foregroundColor = ((CustomForegroundColorSpan) span).getForegroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                return foregroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt final int compareSpanForegroundColor = ((CustomForegroundColorSpan) compareSpan).getForegroundColor();
                return foregroundColor == compareSpanForegroundColor ? span : null;
            }
        } else if (clazz == CustomBackgroundColorSpan.class) {
            @ColorInt final int backgroundColor = ((CustomBackgroundColorSpan) span).getBackgroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                return backgroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt final int compareSpanBackgroundColor = ((CustomBackgroundColorSpan) compareSpan).getBackgroundColor();
                return backgroundColor == compareSpanBackgroundColor ? span : null;
            }
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            final String spanFamily = ((CustomFontFamilySpan) span).getFamily();
            if (compareSpan == null) {
                final String viewTagFamily = (String) view.getTag();
                return TextUtils.equals(spanFamily, viewTagFamily) ? span : null;
            } else {
                final String compareSpanFamily = ((CustomFontFamilySpan) compareSpan).getFamily();
                return TextUtils.equals(spanFamily, compareSpanFamily) ? span : null;
            }
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            final int spanSize = ((CustomAbsoluteSizeSpan) span).getSize();
            if (compareSpan == null) {
                final int viewTagSize = Integer.parseInt(String.valueOf(view.getTag()));
                return spanSize == viewTagSize ? span : null;
            } else {
                final int compareSpanSize = ((CustomAbsoluteSizeSpan) compareSpan).getSize();
                return spanSize == compareSpanSize ? span : null;
            }
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            final float spanSizeChange = ((CustomRelativeSizeSpan) span).getSizeChange();
            if (compareSpan == null) {
                final float viewTagSizeChange = Float.parseFloat(String.valueOf(view.getTag()));
                return spanSizeChange == viewTagSizeChange ? span : null;
            } else {
                final float compareSpanSizeChange = ((CustomRelativeSizeSpan) compareSpan).getSizeChange();
                return spanSizeChange == compareSpanSizeChange ? span : null;
            }
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            final float spanScaleX = ((CustomScaleXSpan) span).getScaleX();
            if (compareSpan == null) {
                final float viewTagScaleX = Float.parseFloat(String.valueOf(view.getTag()));
                return spanScaleX == viewTagScaleX ? span : null;
            } else {
                final float compareSpanScaleX = ((CustomScaleXSpan) compareSpan).getScaleX();
                return spanScaleX == compareSpanScaleX ? span : null;
            }
        }

        return span;
    }

    /**
     * 合并指定位置的同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    public static <T extends IStyle> void joinSpanByPosition(View view, Class<T> clazz, Spannable spannable, int position) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, position, position, true);
        for (T span : spans) {
            final T leftSpan = getLeftSpan(view, clazz, spannable, position, position, span);
            if (leftSpan != null) {
                findAndJoinRightSpan(view, clazz, spannable, span);
            }
        }
    }

    /**
     * 左联合并同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    public static <T extends IStyle> int findAndJoinLeftSpan(View view, Class<T> clazz, @NonNull Spannable spannable, T span) {
        final int spanStart = spannable.getSpanStart(span);
        final int spanEnd = spannable.getSpanEnd(span);

        int resultStart = spanStart;
        final T leftSpan = getLeftSpan(view, clazz, spannable, spanStart, spanEnd, span);
        if (leftSpan != null) {
            resultStart = spannable.getSpanStart(leftSpan);
            spannable.setSpan(span, resultStart, spanEnd, getSpanFlags(span));
            spannable.removeSpan(leftSpan);
        }
        return resultStart;
    }

    /**
     * 右联合并同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    public static <T extends IStyle> int findAndJoinRightSpan(View view, Class<T> clazz, @NonNull Spannable spannable, T span) {
        final int spanStart = spannable.getSpanStart(span);
        final int spanEnd = spannable.getSpanEnd(span);

        int resultEnd = spanEnd;
        final T rightSpan = getRightSpan(view, clazz, spannable, spanStart, spanEnd, span);
        if (rightSpan != null) {
            resultEnd = spannable.getSpanEnd(rightSpan);
            spannable.setSpan(span, spanStart, resultEnd, getSpanFlags(span));
            spannable.removeSpan(rightSpan);
        }

        return resultEnd;
    }

    /**
     * 获得左边的span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    @Nullable
    public static <T extends IStyle> T getLeftSpan(View view, Class<T> clazz, Spannable spannable, int start, int end, T compareSpan) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, start, start, true);
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);
            if (spanStart < start && spanEnd <= end) {
                return filterSpanByCompareSpanOrViewParameter(view, clazz, span, compareSpan);
            }
        }
        return null;
    }

    /**
     * 获得右边的span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    @Nullable
    public static <T extends IStyle> T getRightSpan(View view, Class<T> clazz, Spannable spannable, int start, int end, T compareSpan) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, end, end, true);
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);
            if (start <= spanStart && end < spanEnd) {
                return filterSpanByCompareSpanOrViewParameter(view, clazz, span, compareSpan);
            }
        }

        return null;
    }

    /**
     * 获得span的上一级父span
     *
     * 比如：ListItemSpan和ListSpan的父span是ListSpan
     */
    @Nullable
    public static <T extends IStyle> T getParentSpan(View view, Class<T> clazz, Spannable spannable, int start, int end,
                                       T compareSpan, boolean isIncludeSameRange, int nestingLevel) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, start, end, true);
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);

            ///如果isParagraphStyle，且span包含了选中区间开始位置所在行的首尾[start, end]，则select
            ///单光标选择时spanStart <= start && end < spanEnd，当spanEnd前一字符为 '\n'时spanStart <= start && end <= spanEnd
            ///如果isCharacterStyle，且不是单光标选择、或者span在光标区间外
            ///如果isBlockCharacterStyle为false，并且上光标尾等于span尾
            if (start < end && spanStart <= start && end <= spanEnd && (isIncludeSameRange || spanStart != start || end != spanEnd)
                    || start == end && spanStart <= start && (end < spanEnd || end == spanEnd
                        && (IParagraphStyle.class.isAssignableFrom(clazz) && spannable.charAt(spanEnd - 1) != '\n'
                        || ICharacterStyle.class.isAssignableFrom(clazz) && !IBlockCharacterStyle.class.isAssignableFrom(clazz)))) {
                if (nestingLevel == 0 || ((INestParagraphStyle) span).getNestingLevel() == nestingLevel) {
                    return filterSpanByCompareSpanOrViewParameter(view, clazz, span, compareSpan);
                }
            }
        }

        return null;
    }

    /**
     * 更新区间内所有NestSpan的nesting level，偏移量为offset
     */
    public static <T extends INestParagraphStyle> void updateDescendantNestingLevel(@NonNull Class<T> clazz, Spannable spannable, int start, int end, int offset) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, start, end, false);
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);

            if (start < spanStart && spanEnd < end
                    || start < spanStart && spanEnd == end
                    || start == spanStart && spanEnd < end) {
                span.setNestingLevel(span.getNestingLevel() + offset);
            }
        }
    }


    /* --------------------------------------------------------------------------------------- */
    @NonNull
    public static TextBean saveSpans(Spannable spannable, int selectionStart, int selectionEnd, boolean isSetText) {
        final TextBean textBean = new TextBean();

        if (spannable != null) {
            if (isSetText) {
                final CharSequence subSequence = spannable.subSequence(selectionStart, selectionEnd);
                textBean.setText(subSequence.toString());
            }

            final ArrayList<SpanBean> spanBeans = new ArrayList<>();
            for (Class<? extends IStyle> clazz : sAllClassList) {
                toSpanBeans(spanBeans, clazz, spannable, selectionStart, selectionEnd);
            }
            textBean.setSpans(spanBeans);
        }

        return textBean;
    }

    public static ArrayList<IStyle> loadSpans(Editable editable, TextBean textBean) {
        if (editable != null && textBean != null) {
            if (textBean.getText() != null) {
                //////??????[BUG#ClipDescription的label总是为“host clipboard”]因此无法用label区分剪切板是否为RichEditor或其它App，只能用文本是否相同来“大约”区分
                if (!TextUtils.equals(textBean.getText(), editable)) {
                    return null;
                }

                ///注意：清除原有的span，比如BoldSpan的父类StyleSpan
                ///注意：必须保证selectionChanged()不被执行！否则死循环！
                editable.clearSpans();
            }

            final List<SpanBean> spanBeans = textBean.getSpans();

            return fromSpanBeans(spanBeans, editable);
        }

        return null;
    }

    public static <T extends IStyle> void toSpanBeans(List<SpanBean> spanBeans, @NonNull Class<T> clazz, Spannable spannable, int start, int end) {
        if (spanBeans == null || spannable == null) {
            return;
        }

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, spannable, start, end, false);
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);
            final int spanFlags = getSpanFlags(span);
            final int adjustSpanStart = spanStart < start ? 0 : spanStart - start;
            final int adjustSpanEnd = (Math.min(spanEnd, end)) - start;
            final SpanBean spanBean = new SpanBean(span, span.getClass().getSimpleName(), adjustSpanStart, adjustSpanEnd, spanFlags);
            spanBeans.add(spanBean);
        }
    }

    @NonNull
    public static ArrayList<IStyle> fromSpanBeans(List<SpanBean> spanBeans, Spannable spannable) {
        final ArrayList<IStyle> resultSpanList = new ArrayList<>();
        if (spannable != null && spanBeans != null) {
            for (SpanBean spanBean : spanBeans) {
                final int spanStart = spanBean.getSpanStart();
                final int spanEnd = spanBean.getSpanEnd();
                final int spanFlags = spanBean.getSpanFlags();
                final IStyle span = (IStyle) spanBean.getSpan();
                spannable.setSpan(span, spanStart, spanEnd, spanFlags);
                resultSpanList.add(span);

                ///[FIX#由于ListItemSpan类含有ListSpan成员，反序列化后生成的ListSpan成员必须更改为实际保存的ListSpan！]
                if (span instanceof ListItemSpan) {
                    final ListSpan parentListSpan =
                            (ListSpan) getParentSpan(null, ListSpan.class, spannable, spanStart, spanEnd, null, true, ((ListItemSpan) span).getNestingLevel());

                    assert parentListSpan != null;
                    ((ListItemSpan) span).setListSpan(parentListSpan);
                }
            }
        }

        return resultSpanList;
    }

    public static byte[] toByteArray(Spannable spannable, int selectionStart, int selectionEnd, boolean isSetText) {
        final TextBean textBean = spannable == null ? null :saveSpans(spannable, selectionStart, selectionEnd, isSetText);

        return ParcelUtil.marshall(textBean);
    }

    public static ArrayList<IStyle> fromByteArray(Editable editable, byte[] bytes) {
        final TextBean textBean = ParcelUtil.unmarshall(bytes, TextBean.CREATOR);

        return loadSpans(editable, textBean);
    }

    public static String toJson(Spannable spannable, int selectionStart, int selectionEnd, boolean isSetText) {
        final TextBean textBean = spannable == null ? null : saveSpans(spannable, selectionStart, selectionEnd, isSetText);

        ///[Gson#Exclude父类成员变量的序列化和反序列化]
        ///一是因为只测试显示使用而不进行真正的反序列化，并不需要父类成员变量序列化
        ///二是父类可能包含图片等，造成测试时获取的jsonString字符串长度超长！
        ///https://howtodoinjava.com/gson/gson-exclude-or-ignore-fields/
        ///https://www.baeldung.com/gson-exclude-fields-serialization
        final Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        return gson.toJson(textBean);
    }

    public static ArrayList<IStyle> fromJson(Editable editable, String src) {
        final TextBean textBean = getTextBeanFromJson(src);

        return loadSpans(editable, textBean);
    }

    @NonNull
    public static Spannable fromJson(String src) {
        final TextBean textBean = getTextBeanFromJson(src);

        final List<SpanBean> spanBeans = textBean.getSpans();
        final Spannable spannable = new SpannableStringBuilder(textBean.getText());
        fromSpanBeans(spanBeans, spannable);

        return spannable;
    }

    @Nullable
    public static String getTextFromJson(String src) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(String.class, new JsonDeserializer<String>() {
            @Override
            public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json.isJsonObject()) {
                    final JsonObject jsonObject = json.getAsJsonObject();

                    JsonElement jsonElement = jsonObject.get("text");
                    if (jsonElement != null) {
                        return jsonElement.getAsString();
                    }
                }

                return null;
            }
        }).create();

        return gson.fromJson(src, String.class);
    }

    ///[FIX#java.lang.IllegalArgumentException: field has type android.os.Parcelable, got com.google.gson.internal.LinkedTreeMap]
    ///https://www.jianshu.com/p/3108f1e44155
    ///https://www.jianshu.com/p/d62c2be60617
    public static TextBean getTextBeanFromJson(String src) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(TextBean.class, new JsonDeserializer<TextBean>() {
            @Override
            public TextBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                final TextBean textBean = new TextBean();
                if (json.isJsonObject()) {
                    final JsonObject jsonObject = json.getAsJsonObject();

                    JsonElement jsonElement = jsonObject.get("text");
                    final String text = jsonElement == null ? null : jsonElement.getAsString();
                    textBean.setText(text);

                    jsonElement = jsonObject.get("spans");
                    final JsonArray spansJsonArray = jsonElement == null ? null : jsonElement.getAsJsonArray();
                    final ArrayList<SpanBean> spans = new ArrayList<>();
                    if (spansJsonArray != null) {
                        for (JsonElement spanJsonElement : spansJsonArray) {
                            final JsonObject spanBeanJsonObject = spanJsonElement.getAsJsonObject();
                            if (spanBeanJsonObject == null) {
                                continue;
                            }

                            JsonElement spanBeanJsonElement = spanBeanJsonObject.get("spanClassName");
                            if (spanBeanJsonElement == null) {
                                continue;
                            }
                            final String spanClassName = spanBeanJsonElement.getAsString();

                            spanBeanJsonElement = spanBeanJsonObject.get("span");
                            if (spanBeanJsonElement == null) {
                                continue;
                            }
                            final JsonObject spanJsonObject = spanBeanJsonElement.getAsJsonObject();

                            final Parcelable span = newSpanFromJsonObject(spanClassName, spanJsonObject);

                            if (span != null) {
                                JsonElement jsonElemen = spanBeanJsonObject.get("spanStart");
                                final int spanStart = jsonElemen == null ? -1 : jsonElemen.getAsInt();
                                jsonElemen = spanBeanJsonObject.get("spanEnd");
                                final int spanEnd = jsonElemen == null ? -1 : jsonElemen.getAsInt();
                                jsonElemen = spanBeanJsonObject.get("spanFlags");
                                final int spanFlags = jsonElemen == null ?
                                        getSpanFlags(span)
                                        : jsonElemen.getAsInt();

                                spans.add(new SpanBean(span, spanClassName, spanStart, spanEnd, spanFlags));
                            }
                        }
                    }

                    textBean.setSpans(spans);
                }

                return textBean;
            }
        }).create();

        return gson.fromJson(src, TextBean.class);
    }

    @Nullable
    private static IStyle newSpanFromJsonObject(String spanClassName, @NonNull JsonObject spanJsonObject) {
        JsonElement jsonElement;

        jsonElement = spanJsonObject.get("mNestingLevel");
        final int nestingLevel = jsonElement == null ? 1 : jsonElement.getAsInt();

        if ("DivSpan".equals(spanClassName)) {
            return new DivSpan(nestingLevel);
        } else if ("CustomLeadingMarginSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mFirst");
            final int first = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mRest");
            final int rest = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new CustomLeadingMarginSpan(nestingLevel, first, rest);
        } else if ("AlignNormalSpan".equals(spanClassName)) {
            return new AlignNormalSpan(nestingLevel);
        } else if ("AlignCenterSpan".equals(spanClassName)) {
            return new AlignCenterSpan(nestingLevel);
        } else if ("AlignOppositeSpan".equals(spanClassName)) {
            return new AlignOppositeSpan(nestingLevel);
        } else if ("ListSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mStart");
            final int start = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("isReversed");
            final boolean isReversed = jsonElement != null && jsonElement.getAsBoolean();
            jsonElement = spanJsonObject.get("mListType");
            final int listType = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mIntent");
            final int intent = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new ListSpan(nestingLevel, listType, start, isReversed, intent);
        } else if ("ListItemSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mIndex");
            final int index = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mIndicatorWidth");
            final int indicatorWidth = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mIndicatorGapWidth");
            final int indicatorGapWidth = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mIndicatorColor");
            final int indicatorColor = jsonElement == null ? 0 : jsonElement.getAsInt();

            final ListItemSpan listItemSpan = new ListItemSpan(null, index,
                    indicatorWidth, indicatorGapWidth, indicatorColor, true);   ///注意：需要最后更新ListSpan！
            listItemSpan.setNestingLevel(nestingLevel);

            return listItemSpan;
        } else if ("CustomQuoteSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mColor");
            final int color = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mStripeWidth");
            final int stripWidth = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mGapWidth");
            final int gapWidth = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new CustomQuoteSpan(nestingLevel, color, stripWidth, gapWidth);
        } else if ("PreSpan".equals(spanClassName)) {
            return new PreSpan(nestingLevel);
        } else if ("HeadSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mLevel");
            final int level = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new HeadSpan(level);
        } else if ("LineDividerSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mMarginTop");
            final int marginTop = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mMarginBottom");
            final int marginBottom = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new LineDividerSpan(marginTop, marginBottom);   ///注意：需要最后更新mDrawBackgroundCallback！
        } else if ("CustomForegroundColorSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mColor");
            final int foregroundColor = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new CustomForegroundColorSpan(foregroundColor);
        } else if ("CustomBackgroundColorSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mColor");
            final int backgroundColor = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new CustomBackgroundColorSpan(backgroundColor);
        } else if ("CustomFontFamilySpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mFamily");
            final String family = jsonElement == null ? null : jsonElement.getAsString();

            return new CustomFontFamilySpan(family);
        } else if ("CustomAbsoluteSizeSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mSize");
            final int size = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mDip");
            final boolean isDip = jsonElement != null && jsonElement.getAsBoolean();

            return new CustomAbsoluteSizeSpan(size, isDip);
        } else if ("CustomRelativeSizeSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mProportion");
            final float sizeChange = jsonElement == null ? 0f : jsonElement.getAsFloat();

            return new CustomRelativeSizeSpan(sizeChange);
        } else if ("CustomScaleXSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mProportion");
            final float scaleX = jsonElement == null ? 0f : jsonElement.getAsFloat();

            return new CustomScaleXSpan(scaleX);
        } else if ("BoldSpan".equals(spanClassName)) {
            return new BoldSpan();
        } else if ("ItalicSpan".equals(spanClassName)) {
            return new ItalicSpan();
        } else if ("CustomUnderlineSpan".equals(spanClassName)) {
            return new CustomUnderlineSpan();
        } else if ("CustomStrikethroughSpan".equals(spanClassName)) {
            return new CustomStrikethroughSpan();
        } else if ("CustomSubscriptSpan".equals(spanClassName)) {
            return new CustomSubscriptSpan();
        } else if ("CustomSuperscriptSpan".equals(spanClassName)) {
            return new CustomSuperscriptSpan();
        } else if ("CodeSpan".equals(spanClassName)) {
            return new CodeSpan();
        } else if ("BorderSpan".equals(spanClassName)) {
            return new BorderSpan();
        } else if ("BlockSpan".equals(spanClassName)) {
            return new BlockSpan();
        } else if ("CustomURLSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mURL");
            final String url = jsonElement == null ? "" : jsonElement.getAsString();

            return new CustomURLSpan(url);
        } else if ("CustomImageSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mUri");
            final String uri = jsonElement == null ? "" : jsonElement.getAsString();
            jsonElement = spanJsonObject.get("mSource");
            final String source = jsonElement == null ? "" : jsonElement.getAsString();
            jsonElement = spanJsonObject.get("mVerticalAlignment");
            final int verticalAlignment = jsonElement == null ? 0 : jsonElement.getAsInt();

            jsonElement = spanJsonObject.get("mDrawableWidth");
            final int drawableWidth = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mDrawableHeight");
            final int drawableHeight = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new CustomImageSpan(getPlaceHolderDrawable(drawableWidth, drawableHeight), uri, source, verticalAlignment);
        } else if ("VideoSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mUri");
            final String uri = jsonElement == null ? "" : jsonElement.getAsString();
            jsonElement = spanJsonObject.get("mSource");
            final String source = jsonElement == null ? "" : jsonElement.getAsString();
            jsonElement = spanJsonObject.get("mVerticalAlignment");
            final int verticalAlignment = jsonElement == null ? 0 : jsonElement.getAsInt();

            jsonElement = spanJsonObject.get("mDrawableWidth");
            final int drawableWidth = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mDrawableHeight");
            final int drawableHeight = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new VideoSpan(getPlaceHolderDrawable(drawableWidth, drawableHeight), uri, source, verticalAlignment);
        } else if ("AudioSpan".equals(spanClassName)) {
            jsonElement = spanJsonObject.get("mUri");
            final String uri = jsonElement == null ? "" : jsonElement.getAsString();
            jsonElement = spanJsonObject.get("mSource");
            final String source = jsonElement == null ? "" : jsonElement.getAsString();
            jsonElement = spanJsonObject.get("mVerticalAlignment");
            final int verticalAlignment = jsonElement == null ? 0 : jsonElement.getAsInt();

            jsonElement = spanJsonObject.get("mDrawableWidth");
            final int drawableWidth = jsonElement == null ? 0 : jsonElement.getAsInt();
            jsonElement = spanJsonObject.get("mDrawableHeight");
            final int drawableHeight = jsonElement == null ? 0 : jsonElement.getAsInt();

            return new AudioSpan(getPlaceHolderDrawable(drawableWidth, drawableHeight), uri, source, verticalAlignment);
        }

        return null;
    }

    @NonNull
    private static Drawable getPlaceHolderDrawable(int drawableWidth, int drawableHeight) {
        final Drawable d = ResourcesCompat.getDrawable(Resources.getSystem(), PLACE_HOLDER_DRAWABLE, null);
        assert d != null;
        d.setBounds(0, 0, drawableWidth, drawableHeight);

        return d;
    }

    ///[ImageSpan#调整宽高：考虑到宽高为0或负数的情况]
    @NonNull
    public static Pair<Integer, Integer> adjustDrawableSize(Drawable drawable,
                                                            int width, int height,
                                                            int imageMaxWidth, int imageMaxHeight) {
        if ((width <= 0 || height <= 0)
                && (drawable.getIntrinsicWidth() == -1 || drawable.getIntrinsicHeight() == -1)) {  ///注意：ColorDrawable.getIntrinsicWidth/Height()返回-1
            width = imageMaxWidth;
            height = imageMaxHeight;
        } else {
            if (width <= 0 && height <= 0) {
                width = drawable.getIntrinsicWidth();
                height = drawable.getIntrinsicHeight();
            } else if (width <= 0) {
                width = drawable.getIntrinsicWidth() * height / drawable.getIntrinsicHeight();
            } else if (height <= 0) {
                height = drawable.getIntrinsicHeight() * width / drawable.getIntrinsicWidth();
            }
        }

        if (width > imageMaxWidth || height > imageMaxHeight) {
            final double ratio = (double) width / height;
            if (ratio > 1.0D) {
                width = imageMaxWidth;
                height = (int) (width / ratio);
                if (height > imageMaxHeight) {
                    height = imageMaxHeight;
                    width = (int) (height * ratio);
                }
            } else {
                height = imageMaxHeight;
                width = (int) (height * ratio);
                if (width > imageMaxWidth) {
                    width = imageMaxWidth;
                    height = (int) (width / ratio);
                }
            }
        }

        return new Pair<>(width, height);
    }


    @NonNull
    public static Pair<Integer, Integer> adjustWidth(int width, int height,
                                                     int imageMaxWidth, int imageMaxHeight,
                                                     int compareWidth, int compareHeight,
                                                     boolean isConstrain) {
        if (width <= 0) {
            width = compareWidth <= 0 || compareHeight <= 0 ? 0 : height * compareWidth / compareHeight;
            if (width > 0) {
                final Pair<Integer, Integer> pair = adjustWidth(width, height,
                        imageMaxWidth, imageMaxHeight, compareWidth, compareHeight, isConstrain);
                width = pair.first;
                height = pair.second;
            }

            return new Pair<>(width, height);
        }

        if (width > imageMaxWidth) {
            width = imageMaxWidth;
        }

        if (isConstrain && (Math.abs(width * compareHeight - height * compareWidth) > compareWidth + compareHeight)) {
            height = 0;
            final Pair<Integer, Integer> pair = adjustHeight(width, height,
                    imageMaxWidth, imageMaxHeight, compareWidth, compareHeight, true);
            width = pair.first;
            height = pair.second;
        }

        return new Pair<>(width, height);
    }

    @NonNull
    public static Pair<Integer, Integer> adjustHeight(int width, int height,
                                                      int imageMaxWidth, int imageMaxHeight,
                                                      int compareWidth, int compareHeight,
                                                      boolean isConstrain) {
        if (height <= 0) {
            height = compareWidth <= 0 || compareHeight <= 0 ? 0 : width * compareHeight / compareWidth;
            if (height > 0) {
                final Pair<Integer, Integer> pair = adjustHeight(width, height,
                        imageMaxWidth, imageMaxHeight, compareWidth, compareHeight, isConstrain);
                width = pair.first;
                height = pair.second;
            }

            return new Pair<>(width, height);
        }

        if (height > imageMaxHeight) {
            height = imageMaxHeight;
        }

        if (isConstrain && (Math.abs(width * compareHeight - height * compareWidth) > compareWidth + compareHeight)) {
            width = 0;
            final Pair<Integer, Integer> pair = adjustWidth(width, height,
                    imageMaxWidth, imageMaxHeight, compareWidth, compareHeight, true);
            width = pair.first;
            height = pair.second;
        }

        return new Pair<>(width, height);
    }


    /* --------------------------------------------------------------------------------------- */
    ///[postSetText#执行postLoadSpans及后处理，否则ImageSpan/VideoSpan/AudioSpan不会显示！]
    public static void postSetText(@NonNull Context context, final TextView textView, String authority,
                                                    final int imageMaxWidth, final int imageMaxHeight) {
        if (textView == null) {
            return;
        }

        ///[postSetText#显示ImageSpan/VideoSpan/AudioSpan]如果自定义，则使用ToolbarHelper.postLoadSpans()
        ToolbarHelper.postSetText(context, (Spannable) textView.getText(), new ImageSpanOnClickListener(authority),
                imageMaxWidth, imageMaxHeight);
    }

    ///[postSetText#执行postLoadSpans及后处理，否则ImageSpan/VideoSpan/AudioSpan不会显示！]
    public static void postSetText(@NonNull Context context, final Spannable textSpannable,
                                   CustomImageSpan.OnClickListener onClickListener,
                                   final int imageMaxWidth, final int imageMaxHeight) {
        if (textSpannable == null) {
            return;
        }

        final IStyle[] spans = textSpannable.getSpans(0, textSpannable.length(), IStyle.class);
        final List<IStyle> spanList = Arrays.asList(spans);
        ///执行postLoadSpans及后处理
        ToolbarHelper.postLoadSpans(context, textSpannable, spanList, null, -1,
                imageMaxWidth, imageMaxHeight,
                null, R.drawable.layer_list_placeholder,
                new Drawable.Callback() {
                    ///[Drawable.Callback#ImageSpan#Glide#GifDrawable]
                    ///注意：TextView在实际使用中可能不由EditText产生并赋值，所以需要单独另行处理Glide#GifDrawable的Callback
                    @Override
                    public void invalidateDrawable(@NonNull Drawable drawable) {
                        ToolbarHelper.setImageSpan(textSpannable, drawable);
                    }

                    @Override
                    public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long l) {}

                    @Override
                    public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {}
                },  onClickListener);
    }

    ///执行postLoadSpans后处理（比如：ImageSpan的Glide异步加载图片等）
    public static void postLoadSpans(@NonNull Context context, Spannable spannable, List<IStyle> spans,
                                     Spannable pasteSpannable, int pasteOffset,
                                     final int imageMaxWidth, final int imageMaxHeight,
                                     Drawable placeholderDrawable,
                                     @DrawableRes int placeholderResourceId,
                                     Drawable.Callback drawableCallback,
                                     CustomImageSpan.OnClickListener onClickListener
    ) {
        if (spannable == null && pasteSpannable == null || spans == null || spans.isEmpty()) {
            return;
        }

        for (IStyle span : spans) {
            if (span instanceof CustomImageSpan) {
                final String uri = ((CustomImageSpan) span).getUri();
                final String source = ((CustomImageSpan) span).getSource();
                final int verticalAlignment = ((CustomImageSpan) span).getVerticalAlignment();
                final int imageWidth = ((CustomImageSpan) span).getImageWidth();
                final int imageHeight = ((CustomImageSpan) span).getImageHeight();
                final int drawableWidth = ((CustomImageSpan) span).getDrawableWidth();
                final int drawableHeight = ((CustomImageSpan) span).getDrawableHeight();

                final Spannable spannable0 = pasteSpannable == null ? spannable : pasteSpannable;
                final int spanStart = spannable0.getSpanStart(span);
                final int spanEnd = spannable0.getSpanEnd(span);
                spannable0.removeSpan(span);

                ///[ImageSpan#Glide#GifDrawable]
                loadImage(context, span.getClass(), spannable, spanStart, spanEnd
                        , pasteSpannable, pasteOffset,
                        uri, source, verticalAlignment,
                        imageWidth, imageHeight,
                        drawableWidth, drawableHeight,
                        imageMaxWidth, imageMaxHeight,
                        placeholderDrawable, placeholderResourceId,
                        drawableCallback, onClickListener);
            }
        }
    }

    public static <T extends IStyle> void loadImage(@NonNull final Context context, @NonNull final Class<T> clazz, final Spannable spannable, final int start, final int end,
                                                    final Spannable pasteSpannable, final int pasteOffset,
                                                    final String viewTagUri, final String viewTagSrc,
                                                    final int viewTagAlign,
                                                    final int viewTagWidth, final int viewTagHeight,
                                                    final int drawableWidth, final int drawableHeight,
                                                    final int imageMaxWidth, final int imageMaxHeight,
                                                    Drawable placeholderDrawable,
                                                    @DrawableRes int placeholderResourceId,
                                                    Drawable.Callback drawableCallback,
                                                    final CustomImageSpan.OnClickListener onClickListener) {
        if (spannable == null && pasteSpannable == null) {
            return;
        }

        final GlideImageLoader glideImageLoader = new GlideImageLoader(context, imageMaxWidth, imageMaxHeight);

        ///注意：mPlaceholderDrawable和mPlaceholderResourceId如都设置则mPlaceholderDrawable优先
        if (placeholderDrawable == null && placeholderResourceId == 0) {
            glideImageLoader.setPlaceholderResourceId(R.drawable.layer_list_placeholder);
        } else if (placeholderDrawable == null) {
            glideImageLoader.setPlaceholderResourceId(placeholderResourceId);
        } else {
            glideImageLoader.setPlaceholderDrawable(placeholderDrawable);
        }

        glideImageLoader.setDrawableCallback(drawableCallback);
        glideImageLoader.setCallback(new GlideImageLoader.Callback() {
            ///[GlideImageLoader#isAsync]GlideImageLoader是否为异步加载图片
            ///说明：当paste含有ImageSpan的文本时，有可能造成已经replace完了paste文本才完成Glide异步加载图片，
            ///此时loadImage仍然执行paste的setSpan，而此时应该执行mRichEditText的setSpan！
            private boolean isAsync = false;

            private CustomImageSpan mImagePlaceholderSpan = null;///避免产生重复span！

            @Override
            public void onLoadStarted(@Nullable Drawable drawable) {
                isAsync = true;

//                drawable = new ColorDrawable(Color.BLACK);  ///test

                if (drawable != null) {
                    ///[ImageSpan#调整宽高：考虑到宽高为0或负数的情况]
                    final Pair<Integer, Integer> pair = adjustDrawableSize(drawable,
                            drawableWidth, drawableHeight,
                            imageMaxWidth, imageMaxHeight);

                    drawable.setBounds(0, 0, pair.first, pair.second);  ///注意：Drawable必须设置Bounds才能显示

                    mImagePlaceholderSpan = clazz == VideoSpan.class ? new VideoSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight)
                            : clazz == AudioSpan.class ? new AudioSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                            : new CustomImageSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight);

                    try {
                        (pasteSpannable == null ? spannable : pasteSpannable)
                                .setSpan(mImagePlaceholderSpan, start, end, getSpanFlags(mImagePlaceholderSpan));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onResourceReady(@NonNull Drawable drawable) {
                ///[ImageSpan#调整宽高：考虑到宽高为0或负数的情况]
                final Pair<Integer, Integer> pair = adjustDrawableSize(drawable,
                        drawableWidth, drawableHeight,
                        imageMaxWidth, imageMaxHeight);

                drawable.setBounds(0, 0, pair.first, pair.second);  ///注意：Drawable必须设置Bounds才能显示

                final CustomImageSpan span = clazz == VideoSpan.class ? new VideoSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight)
                        : clazz == AudioSpan.class ? new AudioSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                        : new CustomImageSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight);

                if (isAsync || pasteSpannable == null) {
                    if (spannable != null) {
                        if (mImagePlaceholderSpan != null) {
                            spannable.removeSpan(mImagePlaceholderSpan);
                        }
                        spannable.setSpan(span,
                                pasteSpannable == null ? start : start + pasteOffset,
                                pasteSpannable == null ? end : end + pasteOffset,
                                getSpanFlags(span));
                    }
                } else {
                    if (mImagePlaceholderSpan != null) {
                        pasteSpannable.removeSpan(mImagePlaceholderSpan);
                    }
                    pasteSpannable.setSpan(span, start, end, getSpanFlags(span));
                }

                ///[CustomImageSpan.OnClickListener]
                span.setOnClickListener(onClickListener);
            }

        });

        ///[ImageSpan#Glide#loadImage()]
        if (viewTagSrc.isEmpty()) {
            if (clazz == AudioSpan.class) {
                glideImageLoader.loadImageByDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), AUDIO_DRAWABLE, null));
            } else if (clazz == VideoSpan.class) {
                glideImageLoader.loadImageByDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), VIDEO_DRAWABLE, null));
            }
        } else {
            glideImageLoader.loadImage(viewTagSrc);
        }
    }

    public static void setImageSpan(Spannable spannable, @NonNull Drawable drawable) {
        ///注意：实测此方法不闪烁！
        ///https://www.cnblogs.com/mfrbuaa/p/5045666.html
        final CustomImageSpan imageSpan = SpanUtil.getImageSpanByDrawable(spannable, drawable);
        if (imageSpan != null && !TextUtils.isEmpty(spannable)) {
            final int spanStart = spannable.getSpanStart(imageSpan);
            final int spanEnd = spannable.getSpanEnd(imageSpan);

            ///注意：不必先removeSpan()！只setSpan()就能实现局部刷新EditText，以便让Gif动起来
//                spannable.removeSpan(imageSpan);
            spannable.setSpan(imageSpan, spanStart, spanEnd, getSpanFlags(imageSpan));
        }
    }

}
