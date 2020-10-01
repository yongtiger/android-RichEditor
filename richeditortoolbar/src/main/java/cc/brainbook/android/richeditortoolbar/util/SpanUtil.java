package cc.brainbook.android.richeditortoolbar.util;

import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ParagraphStyle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;

import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListItemSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListSpan;

public abstract class SpanUtil {
    /**
     * 获得排序和过滤后的spans
     */
    @NonNull
    public static <T> ArrayList<T> getFilteredSpans(final Class<T> clazz, @NonNull final Spannable spannable, int start, int end, boolean isSort) {
        final ArrayList<T> filteredSpans = new ArrayList<>();
        final T[] spans = spannable.getSpans(start, end, clazz);

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
                    int result = spannable.getSpanEnd(o1) - spannable.getSpanEnd(o2);

                    if (result == 0) {
                        result = spannable.getSpanStart(o2) - spannable.getSpanStart(o1);
                    }
                    if (result == 0) {
                        if (o1 instanceof INestParagraphStyle && o2 instanceof INestParagraphStyle) {
                            result = ((INestParagraphStyle) o2).getNestingLevel() - ((INestParagraphStyle) o1).getNestingLevel();

                            if (result == 0) {
                                if (o1 instanceof ListSpan && o2 instanceof ListItemSpan) {
                                    result = 1;
                                } else if (o1 instanceof ListItemSpan && o2 instanceof ListSpan) {
                                    result = -1;
                                }
                            }
                        } else if (clazz == ParagraphStyle.class) {
                            if (o1 instanceof INestParagraphStyle) {
                                result = 1;
                            } else if (o2 instanceof INestParagraphStyle) {
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
//                editable.removeSpan(span);    ///注意：千万不要remove！因为有可能是子类！
                continue;
            }

            ///[UPGRADE#android.text.Html#ParagraphStyle span的结束位置是否在'\n'处]
            if (!Html.isSpanEndAtNewLine) {
                final int spanStart = spannable.getSpanStart(span);
                final int spanEnd = spannable.getSpanEnd(span);
                ///删除多余的span
                if (spanStart == spanEnd) {
                    spannable.removeSpan(span);
                    continue;
                }
            }

            filteredSpans.add(span);
        }

        return filteredSpans;
    }

    /**
     * 获取光标选择区间的spans
     */
    @NonNull
    public static <T> ArrayList<T> getSelectedSpans(Class<T> clazz, Editable editable) {
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);
        if (selectionStart == -1 || selectionEnd == -1 || selectionStart == selectionEnd) { ///-1 if there is no selection or cursor
            return new ArrayList<>();
        }

        final ArrayList<T> resultSpans = new ArrayList<>();
        final ArrayList<T> filteredSpans = getFilteredSpans(clazz, editable, selectionStart, selectionEnd, true);
        for (T filteredSpan : filteredSpans) {
            final int spanStart = editable.getSpanStart(filteredSpan);
            final int spanEnd = editable.getSpanEnd(filteredSpan);
            if (spanStart <= selectionEnd && selectionStart <= spanEnd) {
                resultSpans.add(filteredSpan);
            }
        }

        return resultSpans;
//        return (T[]) Array.newInstance(clazz);  ///https://bbs.csdn.net/topics/370137571, https://blog.csdn.net/qing0706/article/details/51067981
    }

    /**
     * 获得段落首尾
     *
     * 参考：DynamicLayout#reflow(CharSequence s, int where, int before, int after)
     */
    public static int getParagraphStart(CharSequence charSequence, int where) {
        // seek back to the start of the paragraph
        int find = TextUtils.lastIndexOf(charSequence, '\n', where - 1);
        if (find < 0)
            find = 0;
        else
            find++;

        return find;
    }
    public static int getParagraphEnd(@NonNull CharSequence charSequence, int where) {
        // seek forward to the end of the paragraph
        int len = charSequence.length();
        int look = TextUtils.indexOf(charSequence, '\n', where);
        if (look < 0)
            look = len;
        else
            look++; // we want the index after the \n

        return look;
    }

    public static boolean isInvalidParagraph(CharSequence charSequence, int index) {
        return index != 0 && index != charSequence.length() && charSequence.charAt(index - 1) != '\n';
    }

    /**
     * 清除区间[start, end]内的spans
     */
    public static <T> void removeSpans(Class<T> clazz, Spannable spannable, int start, int end) {
        final ArrayList<T> spans = getFilteredSpans(clazz, spannable, start, end, false);
        for (T span : spans) {
            final int spanStart = spannable.getSpanStart(span);
            final int spanEnd = spannable.getSpanEnd(span);
            if (start <= spanStart && spanEnd <= end) {
                spannable.removeSpan(span);
            }
        }
    }

    /**
     * 清除所有spans
     */
    public static void clearAllSpans(@NonNull LinkedHashMap<Class<? extends Parcelable>, View> classHashMap, Spannable spannable) {
        for (Class<?> clazz : classHashMap.keySet()) {
            removeSpans(clazz, spannable, 0, spannable.length());
        }
    }

    /**
     * 通过Drawable获取ImageSpan
     */
    @Nullable
    public static CustomImageSpan getImageSpanByDrawable(Spanned spanned, Drawable drawable) {
        if (!TextUtils.isEmpty(spanned)) {
            final CustomImageSpan[] spans = spanned.getSpans(0, spanned.length(), CustomImageSpan.class);
            if (spans != null && spans.length > 0) {
                for (CustomImageSpan span : spans) {
                    if (drawable == span.getDrawable()) {
                        return span;
                    }
                }
            }
        }

        return null;
    }

}
