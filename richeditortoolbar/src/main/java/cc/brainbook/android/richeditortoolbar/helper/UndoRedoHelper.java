package cc.brainbook.android.richeditortoolbar.helper;

import java.util.Arrays;
import java.util.LinkedList;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cc.brainbook.android.richeditortoolbar.R;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;

import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;

///https://gist.github.com/zeleven/0cfa738c1e8b65b23ff7df1fc30c9f7e
public class UndoRedoHelper {
    public static final int INIT_ACTION = 0;
    public static final int CHANGE_TEXT_ACTION = 1;
    public static final int RESTORE_DRAFT_ACTION = 2;
    public static final int CLEAR_STYLES_ACTION = 3;

    public static final int CHANGE_ALIGN_NORMAL_SPAN_ACTION = 11;
    public static final int CHANGE_ALIGN_CENTER_SPAN_ACTION = 12;
    public static final int CHANGE_ALIGN_OPPOSITE_SPAN_ACTION = 13;
    public static final int CHANGE_LEADING_MARGIN_SPAN_ACTION = 14;
    public static final int CHANGE_QUOTE_SPAN_ACTION = 15;
    public static final int CHANGE_LIST_SPAN_ACTION = 16;
    public static final int CHANGE_HEAD_SPAN_ACTION = 17;
    public static final int CHANGE_PRE_SPAN_ACTION = 18;
    public static final int CHANGE_LINE_DIVIDER_SPAN_ACTION = 19;

    public static final int CHANGE_BOLD_SPAN_ACTION = 20;
    public static final int CHANGE_ITALIC_SPAN_ACTION = 21;
    public static final int CHANGE_UNDERLINE_SPAN_ACTION = 22;
    public static final int CHANGE_STRIKE_THROUGH_SPAN_ACTION = 23;
    public static final int CHANGE_SUBSCRIPT_SPAN_ACTION = 24;
    public static final int CHANGE_SUPERSCRIPT_SPAN_ACTION = 25;
    public static final int CHANGE_FOREGROUND_COLOR_SPAN_ACTION = 26;
    public static final int CHANGE_BACKGROUND_COLOR_SPAN_ACTION = 27;
    public static final int CHANGE_FONT_FAMILY_SPAN_ACTION = 28;
    public static final int CHANGE_ABSOLUTE_SIZE_SPAN_ACTION = 29;
    public static final int CHANGE_RELATIVE_SIZE_SPAN_ACTION = 30;
    public static final int CHANGE_SCALE_X_SPAN_ACTION = 31;
    public static final int CHANGE_CODE_SPAN_ACTION = 32;
    public static final int CHANGE_BLOCK_SPAN_ACTION = 33;
    public static final int CHANGE_BORDER_SPAN_ACTION = 34;
    public static final int CHANGE_URL_SPAN_ACTION = 35;
    public static final int CHANGE_IMAGE_SPAN_ACTION = 36;
    public static final int CHANGE_VIDEO_SPAN_ACTION = 37;
    public static final int CHANGE_AUDIO_SPAN_ACTION = 38;


    private final Context mContext;
    private final RichEditorToolbar mRichEditorToolbar;
    private final History mHistory;

    ///[PositionChanged]
    public interface OnPositionChangedListener {
        void onPositionChangedListener(int position, Action action, boolean isSetSpans, boolean isCanUndo, boolean isCanRedo, boolean isSavedPosition);
    }
    private final OnPositionChangedListener mOnPositionChangedListener;
    private void onPositionChanged(boolean isSetSpans) {
        if (mOnPositionChangedListener != null) {
            mOnPositionChangedListener.onPositionChangedListener(mHistory.mPosition, mHistory.current(), isSetSpans, isCanUndo(), isCanRedo(), isSavedPosition());
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
                return mContext.getString(R.string.undo_redo_helper_label_init);
            case CHANGE_TEXT_ACTION:
                return mContext.getString(R.string.layout_click_url_span_dialog_hint_text);
            case RESTORE_DRAFT_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_restore_draft);
            case CLEAR_STYLES_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_clear_styles);

            case CHANGE_ALIGN_NORMAL_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_align_normal);
            case CHANGE_ALIGN_CENTER_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_align_center);
            case CHANGE_ALIGN_OPPOSITE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_align_opposite);
            case CHANGE_LEADING_MARGIN_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_leading_margin);
            case CHANGE_QUOTE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_quote);
            case CHANGE_LIST_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_list);
            case CHANGE_HEAD_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_head);
            case CHANGE_PRE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_pre);
            case CHANGE_LINE_DIVIDER_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_line_divider);

            case CHANGE_BOLD_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_bold);
            case CHANGE_ITALIC_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_italic);
            case CHANGE_UNDERLINE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_underline);
            case CHANGE_STRIKE_THROUGH_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_strikethrough);
            case CHANGE_SUBSCRIPT_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_subscript);
            case CHANGE_SUPERSCRIPT_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_superscript);
            case CHANGE_FOREGROUND_COLOR_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_foreground_color);
            case CHANGE_BACKGROUND_COLOR_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_background_color);
            case CHANGE_FONT_FAMILY_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_text_font_family);
            case CHANGE_ABSOLUTE_SIZE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_text_absolute_size);
            case CHANGE_RELATIVE_SIZE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_text_relative_size);
            case CHANGE_SCALE_X_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_text_scale_x);
            case CHANGE_CODE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_code);
            case CHANGE_BLOCK_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_block);
            case CHANGE_BORDER_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_border);
            case CHANGE_URL_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_url);
            case CHANGE_IMAGE_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_image);
            case CHANGE_VIDEO_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_video);
            case CHANGE_AUDIO_SPAN_ACTION:
                return mContext.getString(R.string.layout_toolbar_desc_audio);

            default:
                return null;
        }
    }


    public UndoRedoHelper(Context context, RichEditorToolbar richEditorToolbar) {
        mContext = context;
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

    public void addHistory(int id, int start, String beforeChange, String afterChange, byte[] bytes) {
        final Action action = new Action(id, start, beforeChange, afterChange, bytes);
        mHistory.add(action);

        if (DEBUG) Log.d("TAG", "addHistory: " + action.mStart + "," + action.mBefore + "," + action.mAfter + "," + new String (action.mBytes));

        ///[PositionChanged]
        onPositionChanged(false);
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
        final Action currentAction = mHistory.current();
        final Action previousAction = mHistory.previous();
        if (previousAction == null) {
            return;
        }

        replace(currentAction, currentAction.mAfter, currentAction.mBefore);

        Toast.makeText(mContext,
                mContext.getString(R.string.undo_redo_helper_msg_undo,
                        mContext.getString(R.string.layout_toolbar_desc_undo),
                        getLabel(currentAction.getId())),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Perform redo.
     */
    public void redo() {
//        final Action currentAction = mHistory.current();
        final Action nextAction = mHistory.next();
        if (nextAction == null) {
            return;
        }

        replace(nextAction, nextAction.mBefore, nextAction.mAfter);

        Toast.makeText(mContext,
                mContext.getString(R.string.undo_redo_helper_msg_redo,
                        mContext.getString(R.string.layout_toolbar_desc_redo),
                        getLabel(nextAction.getId())),
                Toast.LENGTH_SHORT).show();
    }

    private void replace(Action action, CharSequence originalText, CharSequence newText) {
        final Editable editable = mRichEditorToolbar.getRichEditText().getText();
        if (editable == null) {
           return;
        }

        final int start = action.mStart;
        final int end = start + (originalText == null ? 0 : originalText.length());

        ///忽略TextWatcher
        mRichEditorToolbar.isSkipTextWatcher = true;
        if (newText != null) {
            editable.replace(start, end, newText);
        }

        ///[PositionChanged]
        onPositionChanged( true);
        mRichEditorToolbar.isSkipTextWatcher = false;

        ///[FIX#当光标位置未发生变化时不会调用selectionChanged()来更新view的select状态！]
        ///解决：此时应手动调用selectionChanged()来更新view的select状态
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);
        final int newSelectionEnd = start + (newText == null ? 0 : newText.length());
        if (selectionStart == selectionEnd && newSelectionEnd == selectionEnd) {
            mRichEditorToolbar.selectionChanged(newSelectionEnd, newSelectionEnd);
        } else {
            Selection.setSelection(editable, start + (newText == null ? 0 : newText.length()));
        }
    }

    /**
     * Keeps track of all the edit history of a text.
     */
    private static final class History {
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
        private final LinkedList<Action> mHistory = new LinkedList<>();

        private Action current() {
            return mHistory.get(mPosition);
        }

        /**
         * Traverses the history backward by one position, returns and item at that position.
         */
        @Nullable
        private Action previous() {
            if (mPosition <= 0) {
                return null;
            }
            return mHistory.get(--mPosition);
        }

        /**
         * Traverses the history forward by one position, returns and item at that position.
         */
        @Nullable
        private Action next() {
            if (mPosition + 1 >= mHistory.size()) {
                return null;
            }
            return mHistory.get(++mPosition);
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
    public static final class Action {
        private final int mId;
        private final int mStart;
        private final CharSequence mBefore;
        private final CharSequence mAfter;
        private byte[] mBytes;

        /**
         * Constructs Action of a modification that was applied at position
         * start and replaced CharSequence before with CharSequence after.
         */
        public Action(int id, int start, CharSequence before, CharSequence after, byte[] bytes) {
            mId = id;
            mStart = start;
            mBefore = before;
            mAfter = after;
            mBytes = bytes;
        }

        public int getId() {
            return mId;
        }

        public int getStart() {
            return mStart;
        }

        public CharSequence getBefore() {
            return mBefore;
        }

        public CharSequence getAfter() {
            return mAfter;
        }

        public byte[] getBytes() {
            return mBytes;
        }

        public void setBytes(byte[] mBytes) {
            this.mBytes = mBytes;
        }

        @Override
        @NonNull
        public String toString() {
            return "Action{" +
                    "mId=" + mId +
                    ", mStart=" + mStart +
                    ", mBefore=" + mBefore +
                    ", mAfter=" + mAfter +
                    ", mBytes=" + Arrays.toString(mBytes) +
                    '}';
        }
    }

}
