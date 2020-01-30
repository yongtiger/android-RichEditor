package cc.brainbook.android.richeditortoolbar.util;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;

import cc.brainbook.android.richeditortoolbar.span.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.NestSpan;

public abstract class SpanUtil {
    /**
     * 获得排序和过滤后的spans
     */
    public static <T> ArrayList<T> getFilteredSpans(final Class<T> clazz, final Editable editable, int start, int end, boolean isSort) {
        final ArrayList<T> filteredSpans = new ArrayList<>();
        final T[] spans = editable.getSpans(start, end, clazz);

        if (isSort) {
            ///在Android6.0 以下这个方法返回的数组是有顺序的，但是7.0以上系统这个方法返回的数组顺序有错乱，所以我们需要自己排序
            ///https://stackoverflow.com/questions/41052172/spannablestringbuilder-getspans-sort-order-is-wrong-on-nougat-7-0-7-1
            ///https://www.jianshu.com/p/57783747e530
            ///注意：按照spanEnd升序！考虑了span相互交叉（spanStart于spanEnd顺序相同）、嵌套（spanStart于spanEnd顺序相反）等各种关系
            ///嵌套时最里面的span顺序优先；如果spanEnd相同则按照spanStart倒序；如果仍然相同且为NestSpan，则按照其nesting level倒序
            ///[UPGRADE#android.text.Html#ParagraphStyle]
            Arrays.sort(spans, new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    int result = editable.getSpanEnd(o1) - editable.getSpanEnd(o2);

                    if (result == 0) {
                        result = editable.getSpanStart(o2) - editable.getSpanStart(o1);
                    }
                    if (result == 0) {
                        if (o1 instanceof NestSpan && o2 instanceof NestSpan) {
                            result = ((NestSpan) o2).getNestingLevel() - ((NestSpan) o1).getNestingLevel();
                        } else if (clazz == ParagraphStyle.class) {
                            if (o1 instanceof NestSpan) {
                                result = 1;
                            } else if (o2 instanceof NestSpan) {
                                result = -1;
                            }
                        }
                    }

                    return result;
                }
            });
        }

        for (T span : spans) {
            ///[UPGRADE#android.text.Html#ParagraphStyle]如果clazz不是ParagraphStyle，则忽略不是clazz本身（比如为clazz的子类）的span
            ///getSpans()获取clazz类及其子类
            ///比如：HeadSpan extends AbsoluteSizeSpan：
            ///editable.getSpans(start, end, AbsoluteSizeSpan)也能获取到AbsoluteSizeSpan的子类HeadSpan
            if (clazz != ParagraphStyle.class && span.getClass() != clazz) {
//                editable.removeSpan(span);///注意：千万不要remove！因为有可能是子类！
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除spanStart == spanEnd的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

            filteredSpans.add(span);
        }

        return filteredSpans;
    }

    /**
     * 获取光标选择区间的spans
     */
    public static <T> ArrayList<T> getSelectedSpans(Class<T> clazz, EditText editText) {
        ArrayList<T> filteredSpans = new ArrayList<>();
        final int selectionStart = editText.getSelectionStart();
        final int selectionEnd = editText.getSelectionEnd();
        if (selectionStart != -1 && selectionEnd != -1) { ///-1 if there is no selection or cursor
            filteredSpans = getFilteredSpans(clazz, editText.getText(), selectionStart, selectionEnd, false);
        }

        return filteredSpans;
//        return (T[]) Array.newInstance(clazz);  ///https://bbs.csdn.net/topics/370137571, https://blog.csdn.net/qing0706/article/details/51067981
    }

    /**
     * 获得段落首尾
     *
     * 参考：DynamicLayout#reflow(CharSequence s, int where, int before, int after)
     */
    public static int getParagraphStart(Editable editable, int where) {
        // seek back to the start of the paragraph
        int find = TextUtils.lastIndexOf(editable, '\n', where - 1);
        if (find < 0)
            find = 0;
        else
            find++;

        return find;
    }
    public static int getParagraphEnd(Editable editable, int where) {
        // seek forward to the end of the paragraph
        int len = editable.length();
        int look = TextUtils.indexOf(editable, '\n', where);
        if (look < 0)
            look = len;
        else
            look++; // we want the index after the \n

        return look;
    }

    /**
     * 清除区间[start, end]内的spans
     */
    public static <T> void removeSpans(Class<T> clazz, Editable editable, int start, int end) {
        final ArrayList<T> spans = getFilteredSpans(clazz, editable, start, end, false);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (start <= spanStart && spanEnd <= end) {
                editable.removeSpan(span);
            }
        }
    }

    /**
     * 清除所有spans
     */
    public static void clearAllSpans(LinkedHashMap<Class, View> classHashMap, Editable editable) {
        for (Class clazz : classHashMap.keySet()) {
            removeSpans(clazz, editable, 0, editable.length());
        }
    }

    /**
     * 通过Drawable获取ImageSpan
     */
    public static CustomImageSpan getImageSpanByDrawable(Editable editable, Drawable drawable) {
        CustomImageSpan imageSpan = null;
        if (!TextUtils.isEmpty(editable)) {
            final CustomImageSpan[] spans = editable.getSpans(0, editable.length(), CustomImageSpan.class);
            if (spans != null && spans.length > 0) {
                for (CustomImageSpan span : spans) {
                    if (drawable == span.getDrawable()) {
                        imageSpan = span;
                    }
                }
            }
        }

        return imageSpan;
    }

}
