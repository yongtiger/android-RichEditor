package cc.brainbook.android.richeditortoolbar.helper;

import java.util.LinkedList;

import android.text.Editable;
import android.text.Selection;

import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;

///https://gist.github.com/zeleven/0cfa738c1e8b65b23ff7df1fc30c9f7e
public class UndoRedoHelper {
    public static final int TEXT_CHANGED_ACTION = 0;

    private RichEditorToolbar mRichEditorToolbar;
    private History mHistory;

    ///[SavedPosition]
    public interface OnPositionChangedListener {
        void onPositionChangedListener(int position, boolean isCanUndo, boolean isCanRedo, boolean isSavedPosition);
    }
    private OnPositionChangedListener mOnPositionChangedListener;
    private void onPositionChanged() {
        if (mOnPositionChangedListener != null) {
            mOnPositionChangedListener.onPositionChangedListener(mHistory.mPosition, isCanUndo(), isCanRedo(), isSavedPosition());
        }
    }

    ///设置保存的位置
    public void setSavedPosition() {
        mHistory.mSavedPosition = mHistory.mPosition;
    }

    public boolean isSavedPosition() {
        return mHistory.mSavedPosition == mHistory.mPosition;
    }

    ///Label
    public String getLabel(int id) {
        switch (id) {
            case TEXT_CHANGED_ACTION:
                return "Text changed";

            /// others

            default:
                return null;
        }
    }


    public UndoRedoHelper(RichEditorToolbar richEditorToolbar) {
        mRichEditorToolbar = richEditorToolbar;
        mOnPositionChangedListener = richEditorToolbar;
        mHistory = new History();
    }

    /**
     * Set the history size. If size is negative, then history size is
     * only limited by the device memory.
     */
    public void setHistorySize(int historySize) {
        mHistory.setSize(historySize);
    }

    /**
     * Clear history.
     */
    public void clearHistory() {
        mHistory.clear();

        ///[SavedPosition]
        onPositionChanged();
    }

    public void addHistory(int id, int start, CharSequence beforeChange, CharSequence afterChange) {
        mHistory.add(new Action(id, start, beforeChange, afterChange));

        ///[SavedPosition]
        onPositionChanged();
    }

    /**
     * Can undo be performed?
     */
    public boolean isCanUndo() {
        return (mHistory.mPosition > 0);
    }

    /**
     * Can redo be performed?
     */
    public boolean isCanRedo() {
        return (mHistory.mPosition < mHistory.mHistory.size());
    }

    /**
     * Perform undo.
     */
    public void undo() {
        final Action action = mHistory.previous();
        if (action == null) {
            return;
        }

        replace(action.mStart, action.mAfter, action.mBefore);
    }

    /**
     * Perform redo.
     */
    public void redo() {
        final Action action = mHistory.next();
        if (action == null) {
            return;
        }

        replace(action.mStart, action.mBefore, action.mAfter);
    }

    private void replace(int start, CharSequence originalText, CharSequence newText) {
        final Editable editable = mRichEditorToolbar.getRichEditText().getText();
        final int end = start + (originalText != null ? originalText.length() : 0);

        mRichEditorToolbar.isUndoOrRedo = true;
        editable.replace(start, end, newText);
        mRichEditorToolbar.isUndoOrRedo = false;

        Selection.setSelection(editable, newText == null ? start : (start + newText.length()));

        ///[SavedPosition]
        onPositionChanged();
    }

    /**
     * Keeps track of all the edit history of a text.
     */
    private final class History {
        /**
         * The position from which an Action will be retrieved when next()
         * is called. If previous() has not been called, this has the same
         * value as mHistory.size().
         */
        private int mPosition = 0;

        ///[SavedPosition]保存的位置（可用于提示disable编辑器的save按钮）
        private int mSavedPosition = 0;

        /**
         * history size.
         */
        private int mSize = -1;

        /**
         * The list of edits in chronological order.
         */
        private final LinkedList<Action> mHistory = new LinkedList<Action>();

        /**
         * Traverses the history backward by one position, returns and item at that position.
         */
        private Action previous() {
            if (mPosition == 0) {
                return null;
            }
            mPosition--;
            return mHistory.get(mPosition);
        }

        /**
         * Traverses the history forward by one position, returns and item at that position.
         */
        private Action next() {
            if (mPosition >= mHistory.size()) {
                return null;
            }

            final Action item = mHistory.get(mPosition);
            mPosition++;
            return item;
        }

        /**
         * Clear history.
         */
        private void clear() {
            mSavedPosition = mPosition == mSavedPosition ? 0 : -1;   ///[SavedPosition]
            mPosition = 0;
            mHistory.clear();
        }

        /**
         * Adds a new edit operation to the history at the current position.
         * If executed after a call to previous() removes all the future history
         * (elements with positions >= current history position).
         */
        private void add(Action action) {
            while (mHistory.size() > mPosition) {
                mHistory.removeLast();
            }
            mHistory.add(action);
            mPosition++;

            if (mSize >= 0) {
                trim();
            }
        }

        /**
         * Set the history size. If size is negative, then history size
         * is only limited by the device memory.
         */
        private void setSize(int historySize) {
            mSize = historySize;
            if (mSize >= 0) {
                trim();
            }
        }

        /**
         * Trim history when it exceeds history size.
         */
        private void trim() {
            while (mHistory.size() > mSize) {
                mHistory.removeFirst();
                mPosition--;
                mSavedPosition--;   ///[SavedPosition]
            }

            if (mPosition < 0) {
                mPosition = 0;
            }
        }
    }

    /**
     * Represents the changes performed by a single edit operation.
     */
    private final class Action {
        private final int mId;
        private final int mStart;
        private final CharSequence mBefore;
        private final CharSequence mAfter;

        /**
         * Constructs Action of a modification that was applied at position
         * start and replaced CharSequence before with CharSequence after.
         */
        public Action(int id, int start, CharSequence before, CharSequence after) {
            mId = id;
            mStart = start;
            mBefore = before;
            mAfter = after;
        }
    }

}
