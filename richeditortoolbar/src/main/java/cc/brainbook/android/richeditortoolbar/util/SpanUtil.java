package cc.brainbook.android.richeditortoolbar.util;

import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.widget.EditText;

public class SpanUtil {

    /**
     * Returns the selected area line numbers.
     *
     * @param editText
     * @return int[selectionStartLine, selectionEndLine], [-1, -1] if there is no selection or cursor
     */
    public static int[] getSelectionLines(EditText editText) {
        final int[] results = new int[] {-1, -1};
        final int selectionStart = editText.getSelectionStart();
        final int selectionEnd = editText.getSelectionEnd();
        if (selectionStart != -1) { ///-1 if there is no selection or cursor
            results[0] = getLineForOffset(editText, selectionStart);
        }
        if (selectionEnd != -1) { ///-1 if there is no selection or cursor
            results[1] = getLineForOffset(editText, selectionEnd);
        }
        return results;
    }
    public static int getLineForOffset(EditText editText, int offset) {
        final Layout layout = editText.getLayout();
        if (layout != null) {
            return layout.getLineForOffset(offset);
        }
        return -1;
    }

    /**
     * Returns the line start position of the current line (which cursor is focusing now).
     *
     * @param editText
     * @return
     */
    public static int getLineStart(EditText editText, int line) {
        final Layout layout = editText.getLayout();
        if (layout != null) {
            return layout.getLineStart(line);
        }
        return -1;
    }

    /**
     * Returns the line end position of the current line (which cursor is focusing now).
     *
     * @param editText
     * @return
     */
    public static int getLineEnd(EditText editText, int line) {
        final Layout layout = editText.getLayout();
        if (layout != null) {
            return layout.getLineEnd(line);
        }
        return -1;
    }

    ///test
    public static <T> void testOutput(Class<T> clazz, Editable editable) {
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
