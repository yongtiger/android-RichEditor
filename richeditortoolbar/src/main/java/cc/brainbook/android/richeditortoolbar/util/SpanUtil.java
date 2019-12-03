package cc.brainbook.android.richeditortoolbar.util;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SpanUtil {

    public static <T> ArrayList<T> getFilteredSpans(final Editable editable, Class<T> clazz, int start, int end) {
        final ArrayList<T> filteredSpans = new ArrayList<>();
        final T[] spans = editable.getSpans(start, end, clazz);

        ///在Android6.0 以下这个方法返回的数组是有顺序的，但是7.0以上系统这个方法返回的数组顺序有错乱，所以我们需要自己排序
        ///https://stackoverflow.com/questions/41052172/spannablestringbuilder-getspans-sort-order-is-wrong-on-nougat-7-0-7-1
        ///https://www.jianshu.com/p/57783747e530
        ///按照span起始位置从小到大排序
        Arrays.sort(spans, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return editable.getSpanStart(o1) - editable.getSpanStart(o2);
            }
        });

        for (T span : spans) {
            ///忽略不是clazz本身（比如为clazz的子类）的span
            ///getSpans()获取clazz类及其子类
            ///比如：HeadSpan extends AbsoluteSizeSpan：
            ///editable.getSpans(start, end, AbsoluteSizeSpan)也能获取到AbsoluteSizeSpan的子类HeadSpan
            if (span.getClass() != clazz) {
//                editable.removeSpan(span);///注意：千万不要remove！因为有可能是子类！
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

            filteredSpans.add(span);
        }

        return filteredSpans;
    }

    public static <T> ArrayList<T> getSelectedSpans(EditText editText, Class<T> clazz) {
        ArrayList<T> filteredSpans = new ArrayList<>();
        final int selectionStart = editText.getSelectionStart();
        final int selectionEnd = editText.getSelectionEnd();
        if (selectionStart != -1 && selectionEnd != -1) { ///-1 if there is no selection or cursor
            filteredSpans = SpanUtil.getFilteredSpans(editText.getText(), clazz, selectionStart, selectionEnd);
        }
        return filteredSpans;
//        return (T[]) Array.newInstance(clazz);  ///https://bbs.csdn.net/topics/370137571, https://blog.csdn.net/qing0706/article/details/51067981
    }

    public static int getParagraphStart(Editable editable, int where) {
        ///DynamicLayout#reflow(CharSequence s, int where, int before, int after)
        // seek back to the start of the paragraph
        int find = TextUtils.lastIndexOf(editable, '\n', where - 1);
        if (find < 0)
            find = 0;
        else
            find++;

        return find;
    }

    public static int getParagraphEnd(Editable editable, int where) {
        ///DynamicLayout#reflow(CharSequence s, int where, int before, int after)
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
     * 清除掉已经被删除的span，否则将会产生多余的无效span！
     */
    public static <T> void removeSpans(Class<T> clazz, Editable editable, int start, int end) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (start <= spanStart && spanEnd <= end) {
                editable.removeSpan(span);
            }
        }
    }

    /**
     * 平摊并合并交叉重叠的同类span
     *
     * 本编辑器内部添加逻辑不会产生交叉重叠，以防从编辑器外部或HTML转换后可能会产生的交叉重叠
     * 注意：暂时没有考虑ForegroundColor、BackgroundColor等带参数的span！即参数不同的同类span都视为相等而合并
     */
    public static <T> void flatSpans(Class<T> clazz, Editable editable, int start, int end) {
        T currentSpan = null;
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            if (currentSpan == null) {
                currentSpan = span;
                continue;
            }
            if (currentSpan == span) {
                continue;
            }
            final int currentSpanStart = editable.getSpanStart(currentSpan);
            final int currentSpanEnd = editable.getSpanEnd(currentSpan);
            if (currentSpanEnd < spanStart) {
                currentSpan = span;
                continue;
            }
            if (currentSpanStart >= spanStart && currentSpanEnd <= spanEnd) {
                editable.removeSpan(currentSpan);
                currentSpan = span;
                continue;
            }
            if (currentSpanStart <= spanStart && currentSpanEnd <= spanEnd) {
                editable.setSpan(currentSpan, currentSpanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else if (currentSpanStart >= spanStart) {
                editable.setSpan(currentSpan, spanStart, currentSpanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            editable.removeSpan(span);
        }
    }

    ///test
    public static <T> void testOutput(Editable editable, Class<T> clazz) {
        final T[] spans = editable.getSpans(0, editable.length(), clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            Log.d("TAG", span.getClass().getSimpleName() + ": " + spanStart + ", " + spanEnd);
        }
    }

    private SpanUtil() {}
}
