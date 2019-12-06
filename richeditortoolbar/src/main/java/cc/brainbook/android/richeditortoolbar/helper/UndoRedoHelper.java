package cc.brainbook.android.richeditortoolbar.helper;

import java.util.LinkedList;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;

import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;

///https://gist.github.com/zeleven/0cfa738c1e8b65b23ff7df1fc30c9f7e
public class UndoRedoHelper {

    private RichEditorToolbar mRichEditorToolbar;

    private History mHistory;

    // =================================================================== //

    public UndoRedoHelper(RichEditorToolbar richEditorToolbar) {
        mRichEditorToolbar = richEditorToolbar;
        mHistory = new History();
    }

    // =================================================================== //

    /**
     * Set the maximum history size. If size is negative, then history size is
     * only limited by the device memory.
     */
    public void setHistorySize(int historySize) {
        mHistory.setHistorySize(historySize);
    }

    /**
     * Clear history.
     */
    public void clearHistory() {
        mHistory.clear();
    }

    public void addHistory(int start, CharSequence beforeChange, CharSequence afterChange) {
        mHistory.add(new EditItem(start, beforeChange, afterChange));
    }

    /**
     * Can undo be performed?
     */
    public boolean getCanUndo() {
        return (mHistory.mmPosition > 0);
    }

    /**
     * Perform undo.
     */
    public void undo() {
        EditItem edit = mHistory.getPrevious();
        if (edit == null) {
            return;
        }

        Editable editable = mRichEditorToolbar.getRichEditText().getText();
        int start = edit.mmStart;
        int end = start + (edit.mmAfter != null ? edit.mmAfter.length() : 0);

        mRichEditorToolbar.mIsUndoOrRedo = true;
        editable.replace(start, end, edit.mmBefore);
        mRichEditorToolbar.mIsUndoOrRedo = false;

//        // This will get rid of underlines inserted when editor tries to come
//        // up with a suggestion.
//        for (Object o : editable.getSpans(0, editable.length(), UnderlineSpan.class)) {
//            editable.removeSpan(o);
//        }

        Selection.setSelection(editable, edit.mmBefore == null ? start
                : (start + edit.mmBefore.length()));
    }

    /**
     * Can redo be performed?
     */
    public boolean getCanRedo() {
        return (mHistory.mmPosition < mHistory.mmHistory.size());
    }

    /**
     * Perform redo.
     */
    public void redo() {
        EditItem edit = mHistory.getNext();
        if (edit == null) {
            return;
        }

        Editable editable = mRichEditorToolbar.getRichEditText().getText();
        int start = edit.mmStart;
        int end = start + (edit.mmBefore != null ? edit.mmBefore.length() : 0);

        mRichEditorToolbar.mIsUndoOrRedo = true;
        editable.replace(start, end, edit.mmAfter);
        mRichEditorToolbar.mIsUndoOrRedo = false;

//        // This will get rid of underlines inserted when editor tries to come
//        // up with a suggestion.
//        for (Object o : editable.getSpans(0, editable.length(), UnderlineSpan.class)) {
//            editable.removeSpan(o);
//        }

        Selection.setSelection(editable, edit.mmAfter == null ? start
                : (start + edit.mmAfter.length()));
    }

    // =================================================================== //

    /**
     * Keeps track of all the edit history of a text.
     */
    private final class History {

        /**
         * The position from which an EditItem will be retrieved when getNext()
         * is called. If getPrevious() has not been called, this has the same
         * value as mmHistory.size().
         */
        private int mmPosition = 0;

        /**
         * Maximum undo history size.
         */
        private int mmMaxHistorySize = -1;

        /**
         * The list of edits in chronological order.
         */
        private final LinkedList<EditItem> mmHistory = new LinkedList<EditItem>();

        /**
         * Clear history.
         */
        private void clear() {
            mmPosition = 0;
            mmHistory.clear();
        }

        /**
         * Adds a new edit operation to the history at the current position. If
         * executed after a call to getPrevious() removes all the future history
         * (elements with positions >= current history position).
         */
        private void add(EditItem item) {
            while (mmHistory.size() > mmPosition) {
                mmHistory.removeLast();
            }
            mmHistory.add(item);
            mmPosition++;

            if (mmMaxHistorySize >= 0) {
                trimHistory();
            }
        }

        /**
         * Set the maximum history size. If size is negative, then history size
         * is only limited by the device memory.
         */
        private void setHistorySize(int historySize) {
            mmMaxHistorySize = historySize;
            if (mmMaxHistorySize >= 0) {
                trimHistory();
            }
        }

        /**
         * Trim history when it exceeds max history size.
         */
        private void trimHistory() {
            while (mmHistory.size() > mmMaxHistorySize) {
                mmHistory.removeFirst();
                mmPosition--;
            }

            if (mmPosition < 0) {
                mmPosition = 0;
            }
        }

        /**
         * Traverses the history backward by one position, returns and item at
         * that position.
         */
        private EditItem getPrevious() {
            if (mmPosition == 0) {
                return null;
            }
            mmPosition--;
            return mmHistory.get(mmPosition);
        }

        /**
         * Traverses the history forward by one position, returns and item at
         * that position.
         */
        private EditItem getNext() {
            if (mmPosition >= mmHistory.size()) {
                return null;
            }

            EditItem item = mmHistory.get(mmPosition);
            mmPosition++;
            return item;
        }
    }

    /**
     * Represents the changes performed by a single edit operation.
     */
    private final class EditItem {
        private final int mmStart;
        private final CharSequence mmBefore;
        private final CharSequence mmAfter;

        /**
         * Constructs EditItem of a modification that was applied at position
         * start and replaced CharSequence before with CharSequence after.
         */
        public EditItem(int start, CharSequence before, CharSequence after) {
            mmStart = start;
            mmBefore = before;
            mmAfter = after;
        }
    }

}
