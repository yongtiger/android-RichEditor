package cc.brainbook.android.richeditortoolbar.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cc.brainbook.android.richeditortoolbar.R;
import cc.brainbook.android.richeditortoolbar.bean.SpanBean;
import cc.brainbook.android.richeditortoolbar.bean.TextBean;
import cc.brainbook.android.richeditortoolbar.builder.ClickImageSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.span.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.CodeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomAbsoluteSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomBackgroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomFontFamilySpan;
import cc.brainbook.android.richeditortoolbar.span.CustomForegroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomLeadingMarginSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomRelativeSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomScaleXSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomStrikethroughSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomSubscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomSuperscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomURLSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.span.ListItemSpan;
import cc.brainbook.android.richeditortoolbar.span.ListSpan;
import cc.brainbook.android.richeditortoolbar.span.NestSpan;
import cc.brainbook.android.richeditortoolbar.span.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.ParcelUtil;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;
import cc.brainbook.android.richeditortoolbar.util.StringUtil;

public abstract class RichEditorToolbarHelper {
    public static Class getClassMapKey(LinkedHashMap<Class, View> classMap, View view) {
        for (Class clazz : classMap.keySet()) {
            if (classMap.get(clazz) == view) {
                return clazz;
            }
        }

        return null;
    }

    public static byte[] toByteArray(LinkedHashMap<Class, View> classHashMap, Editable editable, int selectionStart, int selectionEnd, boolean isSetText) {
        final TextBean textBean = saveSpans(classHashMap, editable, selectionStart, selectionEnd, isSetText);

        return ParcelUtil.marshall(textBean);
    }

    public static ArrayList<Object> fromByteArray(Editable editable, byte[] bytes) {
        final TextBean textBean = ParcelUtil.unmarshall(bytes, TextBean.CREATOR);

        return loadSpans(editable, textBean);
    }

    public static String toJson(LinkedHashMap<Class, View> classHashMap, Editable editable, int selectionStart, int selectionEnd, boolean isSetText) {
        final TextBean textBean = saveSpans(classHashMap, editable, selectionStart, selectionEnd, isSetText);

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

    public static ArrayList<Object> fromJson(Editable editable, String src) {
        final TextBean textBean = new Gson().fromJson(src, TextBean.class);

        return loadSpans(editable, textBean);
    }


    public static TextBean saveSpans(LinkedHashMap<Class, View> classHashMap, Editable editable, int selectionStart, int selectionEnd, boolean isSetText) {
        final TextBean textBean = new TextBean();
        if (isSetText) {
            final CharSequence subSequence = editable.subSequence(selectionStart, selectionEnd);
            textBean.setText(subSequence.toString());
        }

        final ArrayList<SpanBean> spanBeans = new ArrayList<>();
        for (Class clazz : classHashMap.keySet()) {
            saveSpansToSpanBeans(spanBeans, clazz, editable, selectionStart, selectionEnd);
        }
        textBean.setSpans(spanBeans);

        return textBean;
    }

    public static ArrayList<Object> loadSpans(Editable editable, TextBean textBean) {
        if (textBean != null) {
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

            return loadSpansFromSpanBeans(spanBeans, editable);
        }

        return null;
    }

    public static <T extends Parcelable> void saveSpansToSpanBeans(List<SpanBean> spanBeans, Class<T> clazz, Editable editable, int start, int end) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, false);
        for (T span : spans) {
            ///注意：必须过滤掉没有CREATOR变量的span！
            ///理论上，所有RichEditor用到的span都应该自定义、且直接实现Parcelable（即该span类直接包含CREATOR变量），否则予以忽略
            try {
                clazz.getField("CREATOR");
                final int spanStart = editable.getSpanStart(span);
                final int spanEnd = editable.getSpanEnd(span);
                final int spanFlags = editable.getSpanFlags(span);
                final int adjustSpanStart = spanStart < start ? 0 : spanStart - start;
                final int adjustSpanEnd = (spanEnd > end ? end : spanEnd) - start;
                final SpanBean<T> spanBean = new SpanBean<>(span, span.getClass().getSimpleName(), adjustSpanStart, adjustSpanEnd, spanFlags);
                spanBeans.add(spanBean);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Object> loadSpansFromSpanBeans(List<SpanBean> spanBeans, Editable editable) {
        final ArrayList<Object> resultSpanList = new ArrayList<>();
        if (spanBeans != null) {
            for (SpanBean spanBean : spanBeans) {
                final int spanStart = spanBean.getSpanStart();
                final int spanEnd = spanBean.getSpanEnd();
                final int spanFlags = spanBean.getSpanFlags();
                final Object span = spanBean.getSpan();
                editable.setSpan(span, spanStart, spanEnd, spanFlags);
                resultSpanList.add(span);

                ///[FIX#由于ListItemSpan类含有ListSpan成员，反序列化后生成的ListSpan成员必须更改为实际保存的ListSpan！]
                if (span instanceof ListItemSpan) {
                    final ListSpan parentListSpan =
                            getParentSpan(null, ListSpan.class, editable, spanStart, spanEnd, null, true, ((ListItemSpan) span).getNestingLevel());

                    if (parentListSpan == null) {
                        Log.e("TAG", "loadSpansFromSpanBeans()# parentListSpan cannot be null !!!");
                    }

                    ((ListItemSpan) span).setListSpan(parentListSpan);
                }
            }
        }

        return resultSpanList;
    }


    /* ------------------------------------------------------------------------------------------------------------ */
    public static boolean isParagraphStyle(Class clazz) {
        return clazz == CustomQuoteSpan.class
                || clazz == AlignNormalSpan.class
                || clazz == AlignCenterSpan.class
                || clazz == AlignOppositeSpan.class
                || clazz == ListSpan.class
                || clazz == ListItemSpan.class
                || clazz == HeadSpan.class
                || clazz == CustomLeadingMarginSpan.class
                || clazz == LineDividerSpan.class;
    }
    public static boolean isNestParagraphStyle(Class clazz) {
        return clazz == CustomQuoteSpan.class
                || clazz == AlignNormalSpan.class
                || clazz == AlignCenterSpan.class
                || clazz == AlignOppositeSpan.class
                || clazz == ListSpan.class
                || clazz == ListItemSpan.class;
    }
    public static boolean isCharacterStyle(Class clazz) {
        return clazz == BoldSpan.class
                || clazz == ItalicSpan.class
                || clazz == CustomUnderlineSpan.class
                || clazz == CustomStrikethroughSpan.class
                || clazz == CustomSuperscriptSpan.class
                || clazz == CustomSubscriptSpan.class
                || clazz == CodeSpan.class
                || clazz == CustomForegroundColorSpan.class
                || clazz == CustomBackgroundColorSpan.class
                || clazz == CustomFontFamilySpan.class
                || clazz == CustomAbsoluteSizeSpan.class
                || clazz == CustomRelativeSizeSpan.class
                || clazz == CustomScaleXSpan.class
                || clazz == CustomURLSpan.class
                || clazz == CustomImageSpan.class
                || clazz == VideoSpan.class
                || clazz == AudioSpan.class;
    }
    public static boolean isBlockCharacterStyle(Class clazz) {
        return clazz == CustomURLSpan.class
                || clazz == CustomImageSpan.class
                || clazz == VideoSpan.class
                || clazz == AudioSpan.class;
    }

    public static int getSpanFlag(Class clazz) {
        if (isBlockCharacterStyle(clazz)) {
            return Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        } else if (isCharacterStyle(clazz)) {
            return Spanned.SPAN_INCLUSIVE_INCLUSIVE;
        } else {
            ///注意：段落span只在开始位置才延申！结束位置不延申
            return Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
        }
    }


    /* ------------------------------------------------------------------------------------------------------------ */
    public static <T> void updateParagraphView(Context context, View view, Class<T> clazz, Editable editable, int start, int end) {
        ///注意：因为可能要用到spans.size()，所以不应使用getParentSpan()
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, true);    ///按照spanEnd升序
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果span包含了选中区间开始位置所在行的首尾[start, end]，则select
            ///单光标选择时spanStart <= start && end < spanEnd，当spanEnd前一字符为 '\n'时spanStart <= start && end <= spanEnd
            if (start < end || spanStart <= start && (end < spanEnd || editable.charAt(spanEnd - 1) != '\n' && end == spanEnd)) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                }

                ///段落span（带初始化参数）：List
                if (clazz == ListSpan.class) {
                    final int listStart = ((ListSpan) span).getStart();
                    final boolean isReversed = ((ListSpan) span).isReversed();
                    final int listType = ((ListSpan) span).getListType();
                    view.setTag(R.id.list_start, listStart);
                    view.setTag(R.id.list_is_reversed, isReversed);
                    view.setTag(R.id.list_list_type, listType);
                }

                ///段落span（带参数）：Head
                else if (clazz == HeadSpan.class) {
                    final int level = ((HeadSpan) span).getLevel();
                    view.setTag(level);
                    final String headText = HeadSpan.HEADING_LABELS[level];
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
            view.setTag(R.id.list_start, null);
            view.setTag(R.id.list_is_reversed, null);
            view.setTag(R.id.list_list_type, null);
        } else {
            view.setTag(null);
        }

        ///设置为缺省文字
        ///段落span（带参数）：Head
        if (clazz == HeadSpan.class) {
            ((TextView) view).setText(context.getString(R.string.head));
        }

    }

    public static <T> void updateCharacterStyleView(Context context, View view, Class<T> clazz, Editable editable, int start, int end) {
        ///注意：因为CustomURLSpan、CustomImageSpan等要用到spans.size()，所以不应使用getParentSpan()
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, true);    ///按照spanEnd升序
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果不是单光标、或者span在光标区间外
            ///如果isBlockCharacterStyle为false，并且上光标尾等于span尾
            if (start < end || spanStart < start && (end < spanEnd || !isBlockCharacterStyle(clazz) && end == spanEnd)) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                }

                ///字符span（带参数）：URL
                if (clazz == CustomURLSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
                        final String text = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                        final String url = ((CustomURLSpan) span).getURL();
                        view.setTag(R.id.url_text, text);
                        view.setTag(R.id.url_url, url);
                    } else {
                        view.setTag(R.id.url_text, null);
                        view.setTag(R.id.url_url, null);
                    }
                }

                ///字符span（带参数）：Image
                else if (clazz == CustomImageSpan.class || clazz == VideoSpan.class || clazz == AudioSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
                        final String text = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);

                        final String uri = clazz == CustomImageSpan.class ? null :
                                clazz == VideoSpan.class ? ((VideoSpan) span).getUri() : ((AudioSpan) span).getUri();
                        final String src = ((CustomImageSpan) span).getSource();

                        ///从text中解析出width\height\align
                        final String strWidth = StringUtil.getParameter(text, "width=", " ");
                        final String strHeight = StringUtil.getParameter(text, "height=", " ");
                        final String strAlign = StringUtil.getParameter(text, "align=", "]");
                        final int width = strWidth == null ? 0 : Integer.parseInt(strWidth);
                        final int height = strHeight == null ? 0 : Integer.parseInt(strHeight);
                        final int align = strAlign == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : Integer.parseInt(strAlign);

                        view.setTag(R.id.image_text, text);
                        view.setTag(R.id.image_src, uri);
                        view.setTag(R.id.image_src, src);
                        view.setTag(R.id.image_width, width);
                        view.setTag(R.id.image_height, height);
                        view.setTag(R.id.image_align, align);
                    } else {
                        view.setTag(R.id.image_text, null);
                        view.setTag(R.id.image_uri, null);
                        view.setTag(R.id.image_src, null);
                        view.setTag(R.id.image_width, null);
                        view.setTag(R.id.image_height, null);
                        view.setTag(R.id.image_align, null);
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
                final String text = String.valueOf(editable.toString().toCharArray(), start, end - start);
                view.setTag(R.id.url_text, text);///以选中的文本作为url_text
                view.setTag(R.id.url_url, null);
            } else {
                view.setTag(R.id.url_text, null);
                view.setTag(R.id.url_url, null);
            }
        }

        ///字符span（带参数）：Image
        else if (clazz == CustomImageSpan.class || clazz == VideoSpan.class || clazz == AudioSpan.class) {
            view.setTag(R.id.image_text, null);
            view.setTag(R.id.image_uri, null);
            view.setTag(R.id.image_src, null);
            view.setTag(R.id.image_width, null);
            view.setTag(R.id.image_height, null);
            view.setTag(R.id.image_align, null);
        }

        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == CustomForegroundColorSpan.class || clazz == CustomBackgroundColorSpan.class) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            ((TextView) view).setText(context.getString(R.string.font_family));
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            ((TextView) view).setText(context.getString(R.string.absolute_size));
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            ((TextView) view).setText(context.getString(R.string.relative_size));
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            ((TextView) view).setText(context.getString(R.string.scale_x));
        }

        else {
            view.setTag(null);
        }
    }


    /* ------------------------------------------------------------------------------------------------------------ */
    ///ViewParameter用于保存toolbar按钮的点选状态（如view.getBackground()，view.getTag()，view.getTag(R.id.url_text等)）
    public static <T> boolean isSameWithViewParameter(View view, Class<T> clazz, T span) {
        ///段落span（带参数）：Head
        if (clazz == HeadSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final int spanLevel = ((HeadSpan) span).getLevel();
            final int viewTagLevel = (int) view.getTag();
            return spanLevel == viewTagLevel;
        }

        ///段落span（带初始化参数）：List
        else if (clazz == ListSpan.class) {
            if (view.getTag(R.id.list_start) == null
                    || view.getTag(R.id.list_is_reversed) == null
                    || view.getTag(R.id.list_list_type) == null) {
                return false;
            }
            final int start = ((ListSpan) span).getStart();
            final boolean isReversed = ((ListSpan) span).isReversed();
            final int listType = ((ListSpan) span).getListType();
            return start == (int) view.getTag(R.id.list_start)
                    && isReversed == (boolean) view.getTag(R.id.list_is_reversed)
                    && listType == (int) view.getTag(R.id.list_list_type);
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
            final int viewTagAbsoluteSize = (int) view.getTag();
            return spanSize == viewTagAbsoluteSize;
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanSizeChange = ((CustomRelativeSizeSpan) span).getSizeChange();
            final float viewTagRelativeSize = (float) view.getTag();
            return spanSizeChange == viewTagRelativeSize;
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanScaleX = ((CustomScaleXSpan) span).getScaleX();
            final float viewTagScaleX = (float) view.getTag();
            return spanScaleX == viewTagScaleX;
        }

        else {
            return true;
        }
    }

    ///注意：不包括block span（如URLSpan、ImageSpan）
    public static <T> T filterSpanByCompareSpanOrViewParameter(View view, Class<T> clazz, T span, T compareSpan) {
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
                final int viewTagSize = (int) view.getTag();
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
                final float viewTagSizeChange = (float) view.getTag();
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
                final float viewTagScaleX = (float) view.getTag();
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
    public static <T> void joinSpanByPosition(View view, Class<T> clazz, Editable editable, int position) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, position, position, true);
        for (T span : spans) {
            final T leftSpan = getLeftSpan(view, clazz, editable, position, position, span);
            if (leftSpan != null) {
                findAndJoinRightSpan(view, clazz, editable, span);
            }
        }
    }

    /**
     * 左联合并同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    public static <T> int findAndJoinLeftSpan(View view, Class<T> clazz, Editable editable, T span) {
        final int spanStart = editable.getSpanStart(span);
        final int spanEnd = editable.getSpanEnd(span);

        int resultStart = spanStart;
        final T leftSpan = getLeftSpan(view, clazz, editable, spanStart, spanEnd, span);
        if (leftSpan != null) {
            resultStart = editable.getSpanStart(leftSpan);
            editable.setSpan(span, resultStart, spanEnd, getSpanFlag(clazz));
            editable.removeSpan(leftSpan);
        }
        return resultStart;
    }

    /**
     * 右联合并同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    public static <T> int findAndJoinRightSpan(View view, Class<T> clazz, Editable editable, T span) {
        final int spanStart = editable.getSpanStart(span);
        final int spanEnd = editable.getSpanEnd(span);

        int resultEnd = spanEnd;
        final T rightSpan = getRightSpan(view, clazz, editable, spanStart, spanEnd, span);
        if (rightSpan != null) {
            resultEnd = editable.getSpanEnd(rightSpan);
            editable.setSpan(span, spanStart, resultEnd, getSpanFlag(clazz));
            editable.removeSpan(rightSpan);
        }

        return resultEnd;
    }

    /**
     * 获得左边的span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    public static <T> T getLeftSpan(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        if (start == 0) {
            return null;
        }
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, start, true);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
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
    public static <T> T getRightSpan(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        if (end == editable.length()) {
            return null;
        }

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, end, end, true);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
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
    public static <T> T getParentSpan(View view, Class<T> clazz, Editable editable, int start, int end,
                                      T compareSpan, boolean isIncludeSameRange, int nestingLevel) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, true);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果span包含了选中区间开始位置所在行的首尾[start, end]，则select
            ///单光标选择时spanStart <= start && end < spanEnd，当spanEnd前一字符为 '\n'时spanStart <= start && end <= spanEnd
            ///如果不是单光标选择、或者span在光标区间外
            ///如果isBlockCharacterStyle为false，并且上光标尾等于span尾
            if (start < end && spanStart <= start && end <= spanEnd && (isIncludeSameRange || spanStart != start || end != spanEnd)
                    || start == end && spanStart <= start && (end < spanEnd || end == spanEnd
                        && (isParagraphStyle(clazz) && editable.charAt(spanEnd - 1) != '\n'
                        || isCharacterStyle(clazz) && !isBlockCharacterStyle(clazz)))) {
                if (nestingLevel == 0 || ((NestSpan) span).getNestingLevel() == nestingLevel) {
                    return filterSpanByCompareSpanOrViewParameter(view, clazz, span, compareSpan);
                }
            }
        }

        return null;
    }

    /**
     * 更新区间内所有NestSpan的nesting level，偏移量为offset
     */
    public static <T extends NestSpan> void updateDescendantNestingLevel(Class<T> clazz, Editable editable, int start, int end, int offset) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, false);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            if (start < spanStart && spanEnd < end
                    || start < spanStart && spanEnd == end
                    || start == spanStart && spanEnd < end) {
                span.setNestingLevel(span.getNestingLevel() + offset);
            }
        }
    }

}
