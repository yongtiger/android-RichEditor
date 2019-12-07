package cc.brainbook.android.richeditortoolbar.helper;

import java.util.LinkedList;

import android.os.Parcel;
import android.text.Editable;
import android.text.Selection;

import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;

///https://gist.github.com/zeleven/0cfa738c1e8b65b23ff7df1fc30c9f7e
public class UndoRedoHelper {
    public static final int INIT_ACTION = 0;
    public static final int TEXT_CHANGED_ACTION = 1;

    private RichEditorToolbar mRichEditorToolbar;
    private History mHistory;

    ///[PositionChanged]
    public interface OnPositionChangedListener {
        void onPositionChangedListener(int position, Action action, boolean isSetSpans, boolean isCanUndo, boolean isCanRedo, boolean isSavedPosition);
    }
    private OnPositionChangedListener mOnPositionChangedListener;
    private void onPositionChanged(Action action, boolean isSetSpans) {
        if (mOnPositionChangedListener != null) {
            mOnPositionChangedListener.onPositionChangedListener(mHistory.mPosition, action, isSetSpans, isCanUndo(), isCanRedo(), isSavedPosition());
        }
    }

    ///[SavedPosition]设置保存的位置
    public void resetSavedPosition() {
        mHistory.mSavedPosition = mHistory.mPosition;
    }
    public boolean isSavedPosition() {
        return mHistory.mSavedPosition == mHistory.mPosition;
    }

    ///Label
    public String getLabel(int id) {
        switch (id) {
            case INIT_ACTION:
                return "Init";
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
    }

    public void addHistory(int id, int start, CharSequence beforeChange, CharSequence afterChange, Parcel parcel) {
        final Action action = new Action(id, start, beforeChange, afterChange, parcel);
        mHistory.add(action);

        ///[PositionChanged]
        onPositionChanged(action, false);
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
        return (mHistory.mPosition + 1 < mHistory.mHistory.size());
    }

    /**
     * Perform undo.
     */
    public void undo() {
        final Action action = mHistory.previous();
        if (action == null) {
            return;
        }

        replace(action, action.mAfter, action.mBefore);
    }

    /**
     * Perform redo.
     */
    public void redo() {
        final Action action = mHistory.next();
        if (action == null) {
            return;
        }

        replace(action, action.mBefore, action.mAfter);
    }

    private void replace(Action action, CharSequence originalText, CharSequence newText) {
        final Editable editable = mRichEditorToolbar.getRichEditText().getText();
        final int start = action.mStart;
        final int end = start + (originalText != null ? originalText.length() : 0);

        mRichEditorToolbar.isUndoOrRedo = true;
        editable.replace(start, end, newText);

        ///[PositionChanged]
        onPositionChanged(action, true);
        mRichEditorToolbar.isUndoOrRedo = false;

        Selection.setSelection(editable, newText == null ? start : (start + newText.length()));
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
        private int mPosition = -1;

        ///[SavedPosition]保存的位置（可用于提示disable编辑器的save按钮）
        private int mSavedPosition = -1;

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

            final Action action = mHistory.get(mPosition);
            mPosition--;
            return action;
        }

        /**
         * Traverses the history forward by one position, returns and item at that position.
         */
        private Action next() {
            mPosition++;
            if (mPosition >= mHistory.size()) {
                return null;
            }

            return mHistory.get(mPosition);
        }

        /**
         * Clear history.
         */
        private void clear() {
            mSavedPosition = mPosition == mSavedPosition ? 0 : -1;   ///[SavedPosition]
            mPosition = -1;
            mHistory.clear();
        }

        /**
         * Adds a new edit operation to the history at the current position.
         * If executed after a call to previous() removes all the future history
         * (elements with positions >= current history position).
         */
        private void add(Action action) {
            mPosition++;
            while (mHistory.size() > mPosition) {
                mHistory.removeLast();
            }
            mHistory.add(action);

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
                mSavedPosition--;   ///[SavedPosition]注意：有可能小于-1！
            }

            if (mPosition < 0) {
                mPosition = 0;
            }
        }
    }

    /**
     * Represents the changes performed by a single edit operation.
     */
    public final class Action {
        private final int mId;
        private final int mStart;
        private final CharSequence mBefore;
        private final CharSequence mAfter;
        private Parcel mParcel;

        /**
         * Constructs Action of a modification that was applied at position
         * start and replaced CharSequence before with CharSequence after.
         */
        public Action(int id, int start, CharSequence before, CharSequence after, Parcel parcel) {
            mId = id;
            mStart = start;
            mBefore = before;
            mAfter = after;
            mParcel = parcel;
        }

        public int getmId() {
            return mId;
        }

        public int getmStart() {
            return mStart;
        }

        public CharSequence getmBefore() {
            return mBefore;
        }

        public CharSequence getmAfter() {
            return mAfter;
        }

        public Parcel getmParcel() {
            return mParcel;
        }

        public void setmParcel(Parcel mParcel) {
            this.mParcel = mParcel;
        }
    }

}
