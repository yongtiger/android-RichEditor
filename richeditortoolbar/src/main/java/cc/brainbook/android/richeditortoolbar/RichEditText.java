package cc.brainbook.android.richeditortoolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.CheckResult;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

public class RichEditText extends AppCompatEditText {
    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
    private boolean enableSelectionChange = true;
    private boolean isPreserveSelection;
    private int mSelectionStart = -1;
    private int mSelectionEnd = -1;
    public void disableSelectionChange(boolean isPreserveSelection) {
        enableSelectionChange = false;
        this.isPreserveSelection = isPreserveSelection;
        if (isPreserveSelection) {
            mSelectionStart = getSelectionStart();
            mSelectionEnd = getSelectionEnd();
        }
    }
    public void enableSelectionChange() {
        if (!enableSelectionChange) {
            if (isPreserveSelection) {
                if (getSelectionStart() != mSelectionStart || getSelectionEnd() != mSelectionEnd) {
                    setSelection(mSelectionStart, mSelectionEnd);
                }

                isPreserveSelection = false;
                mSelectionStart = -1;
                mSelectionEnd = -1;
            }

            enableSelectionChange = true;
        }
    }

    @Override
    protected void onSelectionChanged(int selectionStart, int selectionEnd) {
        super.onSelectionChanged(selectionStart, selectionEnd);

        if (selectionStart >= 0 && selectionEnd >= 0) {
            if (enableSelectionChange) {
                if (mOnSelectionChanged != null) {
                    mOnSelectionChanged.selectionChanged(selectionStart, selectionEnd);
                }
            } else if (isPreserveSelection) {
                if (getSelectionStart() != mSelectionStart || getSelectionEnd() != mSelectionEnd) {
                    setSelection(mSelectionStart, mSelectionEnd);
                }
            }
        }
    }


    private OnSelectionChanged mOnSelectionChanged;
    public interface OnSelectionChanged {
        void selectionChanged(int selectionStart, int selectionEnd);
    }
    public void setOnSelectionChanged(OnSelectionChanged onSelectionChanged) {
        mOnSelectionChanged = onSelectionChanged;
    }


    public RichEditText(Context context) {
        super(context);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
    }


    /* --------------- ///[TextContextMenu#Clipboard] --------------- */
    public interface SaveSpansCallback {
        void saveSpans(Editable editable, int selectionStart, int selectionEnd);
    }
    interface LoadSpansCallback {
        void loadSpans(Editable editable, int offset);
    }
    private SaveSpansCallback mSaveSpansCallback;
    private LoadSpansCallback mLoadSpansCallback;
    public void setSaveSpansCallback(SaveSpansCallback saveSpansCallback) {
        mSaveSpansCallback = saveSpansCallback;
    }
    public void setLoadSpansCallback(LoadSpansCallback loadSpansCallback) {
        mLoadSpansCallback = loadSpansCallback;
    }

    ///参考TextView#onTextContextMenuItem(int id)
    @Override
    public boolean onTextContextMenuItem(int id) {
        final Editable editable = getText();
        if (editable == null) {
            return super.onTextContextMenuItem(id);
        }

        int min = 0;
        int max = length();

        if (isFocused()) {
            final int selStart = getSelectionStart();
            final int selEnd = getSelectionEnd();

            min = Math.max(0, Math.min(selStart, selEnd));
            max = Math.max(0, Math.max(selStart, selEnd));
        }

        switch (id) {
            case android.R.id.cut:
                ///注意：必须editable.subSequence(min, max).toString()！否则editable.subSequence(min, max)在api 23以下无法获得剪切板内容
                final ClipData cutData = ClipData.newPlainText(getContext().getPackageName(), editable.subSequence(min, max).toString());
                if (setPrimaryClip(cutData)) {

                    if (mSaveSpansCallback != null) {
                        ///[clipboard]由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
                        mSaveSpansCallback.saveSpans(editable, min, max);
                    }

                    editable.delete(min, max);
                } else {
                    Log.e("TAG-RichEditText", getContext().getString(R.string.click_image_span_dialog_builder_msg_failed_to_cut_to_clipboard));
                    Toast.makeText(getContext(),
                            R.string.click_image_span_dialog_builder_msg_failed_to_cut_to_clipboard,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.copy:
                // For link action mode in a non-selectable/non-focusable TextView,
                // make sure that we set the appropriate min/max.
                final int selStart = getSelectionStart();
                final int selEnd = getSelectionEnd();
                min = Math.max(0, Math.min(selStart, selEnd));
                max = Math.max(0, Math.max(selStart, selEnd));
                ///注意：editable.subSequence(min, max)必须后跟toString()！否则editable.subSequence(min, max)在api 23以下无法获得剪切板内容
                final ClipData copyData = ClipData.newPlainText(getContext().getPackageName(), editable.subSequence(min, max).toString());
                if (setPrimaryClip(copyData)) {

                    if (mSaveSpansCallback != null) {
                        ///[clipboard]由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
                        mSaveSpansCallback.saveSpans(editable, min, max);
                    }

                    //////??????如何关闭TextContextMenuItem

                } else {
                    Log.e("TAG-RichEditText", getContext().getString(R.string.click_image_span_dialog_builder_msg_failed_to_copy_to_clipboard));
                    Toast.makeText(getContext(),
                            R.string.click_image_span_dialog_builder_msg_failed_to_copy_to_clipboard,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.paste:
                paste(min, max, true /* withFormatting */);
                return true;
            case android.R.id.pasteAsPlainText:
                paste(min, max, false /* withFormatting */);
                return true;

            case android.R.id.undo:
                ///屏蔽掉系统Undo/Redo
                Toast.makeText(getContext(),
                        R.string.click_image_span_dialog_builder_msg_please_try_undo_redo_in_toolbar,
                        Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.redo:
                ///屏蔽掉系统Undo/Redo
                Toast.makeText(getContext(),
                        R.string.click_image_span_dialog_builder_msg_please_try_undo_redo_in_toolbar,
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }

        return super.onTextContextMenuItem(id);
    }

    /**
     * Paste clipboard content between min and max positions.
     */
    private void paste(int min, int max, boolean withFormatting) {
        final Editable editable = getText();
        if (editable == null) {
            return;
        }

        final ClipboardManager clipboard =
                (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null) {
            boolean didFirst = false;
            for (int i = 0; i < clipData.getItemCount(); i++) {
                final SpannableStringBuilder paste;
                if (withFormatting && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    paste = new SpannableStringBuilder(clipData.getItemAt(i).coerceToStyledText(getContext()));
                } else {
                    // Get an item as text and remove all spans by toString().
                    final CharSequence text = clipData.getItemAt(i).coerceToText(getContext());
                    paste = new SpannableStringBuilder((text instanceof Spanned) ? text.toString() : text);
                }
                if (!TextUtils.isEmpty(paste)) {
                    if (!didFirst) {

                        ///[FIX#必须加入Selection.setSelection()，否则，在API 26及以上会出现异常：
                        ///java.lang.IllegalArgumentException: Invalid offset: XXX. Valid range is [0, X]
                        ///IndexOutOfBoundsException
                        ///比如：aa[a{a]aa[aa]aa} replace with [a]aa[a]
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Selection.setSelection(editable, max);
                        }

                        ///如果为paint text则不执行loadSpans
                        if (withFormatting && mLoadSpansCallback != null) {
                            //////??????[BUG#ClipDescription的label总是为“host clipboard”]因此无法用label区分剪切板是否为RichEditor或其它App，只能用文本是否相同来“大约”区分
//                            final ClipDescription clipDescription = clipboard.getPrimaryClipDescription();
//                            if (clipDescription != null && getContext().getPackageName().equals(clipDescription.getLabel().toString())) {
                                mLoadSpansCallback.loadSpans(paste, min);
//                            }
                        }

                        replace(editable, min, max, paste);

                        didFirst = true;
                    } else {
                        editable.insert(getSelectionEnd(), "\n");
                        editable.insert(getSelectionEnd(), paste);
                    }
                }
            }
        }
    }

    @CheckResult
    private boolean setPrimaryClip(ClipData clip) {
        final ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            clipboard.setPrimaryClip(clip);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }


    public void replace(Editable editable, int min, int max, Spannable paste) {
        editable.replace(min, max, paste);

        ///调整Selection起止位置
        Selection.removeSelection(editable);  ///[FIX#避免replace前后Selection相同导致不更新View]
        Selection.setSelection(editable, min, min + paste.length());
    }

}
