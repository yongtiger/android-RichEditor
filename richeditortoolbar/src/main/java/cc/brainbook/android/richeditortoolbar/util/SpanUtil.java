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


//    /**
//     * Returns the line start position of the current line (which cursor is focusing now).
//     *
//     * @param editText
//     * @return
//     */
//    public static int getThisLineStart(EditText editText, int currentLine) {
//        Layout layout = editText.getLayout();
//        int start = 0;
//        if (currentLine > 0) {
//            start = layout.getLineStart(currentLine);
//            if (start > 0) {
//                String text = editText.getText().toString();
//                char lastChar = text.charAt(start - 1);
//                while (lastChar != '\n') {
//                    if (currentLine > 0) {
//                        currentLine--;
//                        start = layout.getLineStart(currentLine);
//                        if (start > 1) {
//                            start--;
//                            lastChar = text.charAt(start);
//                        } else {
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        return start;
//    }
//
//    /**
//     * Returns the line end position of the current line (which cursor is focusing now).
//     *
//     * @param editText
//     * @return
//     */
//    public static int getThisLineEnd(EditText editText, int currentLine) {
//        Layout layout = editText.getLayout();
//        if (-1 != currentLine) {
//            return layout.getLineEnd(currentLine);
//        }
//        return -1;
//    }

//    public static Selection getParagraphs(EditText editor) {
//        Layout layout = new Layout( editor.getEditableText() );
//
//        int selStart = editor.getSelectionStart();
//        int selEnd = editor.getSelectionEnd();
//
//        int firstLine = layout.getLineForOffset(selStart);
//        int end = selStart == selEnd ? selEnd : selEnd - 1;
//        int lastLine = layout.getLineForOffset(end);
//
//        return new Selection(layout.getLineStart(firstLine), layout.getLineEnd(lastLine));
//    }

    ///test
    public static <T> void testOutput(Class<T> clazz, Editable editable) {
        final T[] spans = editable.getSpans(0, editable.length(), clazz);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            Log.d("TAG", span.getClass().getSimpleName() + ": " + spanStart + ", " + spanEnd);
        }
    }

    private SpanUtil() {}
}
