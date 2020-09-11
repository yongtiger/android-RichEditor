package cc.brainbook.android.richeditortoolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.CheckResult;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;

public class RichEditText extends AppCompatEditText {
    private OnSelectionChanged mOnSelectionChanged;
    public interface OnSelectionChanged {
        void selectionChanged(int selectionStart, int selectionEnd);
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

    @Override
    protected void onSelectionChanged(int selectionStart, int selectionEnd) {
        super.onSelectionChanged(selectionStart, selectionEnd);

        if (mOnSelectionChanged != null) {
            mOnSelectionChanged.selectionChanged(selectionStart, selectionEnd);
        }
    }

    public void setOnSelectionChanged(OnSelectionChanged selectionChanged) {
        mOnSelectionChanged = selectionChanged;
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
    ///注意：一个App可含有多个RichEditor，多个App的所有RichEditor共享剪切板的存储空间！所以可以实现跨进程复制粘贴
    @Override
    public boolean onTextContextMenuItem(int id) {
        if (getText() == null) {
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
                final ClipData cutData = ClipData.newPlainText(getContext().getPackageName(), getText().subSequence(min, max));
                if (setPrimaryClip(cutData)) {

                    if (mSaveSpansCallback != null) {
                        ///由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
                        ///而且应该保存到进程App共享空间！
                        mSaveSpansCallback.saveSpans(getText(), min, max);
                    }

                    getText().delete(min, max);
                } else {
                    Toast.makeText(getContext(),
                            R.string.failed_to_copy_to_clipboard,
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
                final ClipData copyData = ClipData.newPlainText(getContext().getPackageName(), getText().subSequence(min, max));
                if (setPrimaryClip(copyData)) {

                    if (mSaveSpansCallback != null) {
                        ///由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
                        ///而且应该保存到进程App共享空间！
                        mSaveSpansCallback.saveSpans(getText(), min, max);
                    }

                    //////??????如何关闭TextContextMenuItem

                } else {
                    Toast.makeText(getContext(),
                            R.string.failed_to_copy_to_clipboard,
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
                        R.string.please_try_undo_redo_in_toolbar,
                        Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.redo:
                ///屏蔽掉系统Undo/Redo
                Toast.makeText(getContext(),
                        R.string.please_try_undo_redo_in_toolbar,
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
        if (getText() == null) {
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            boolean didFirst = false;
            for (int i = 0; i < clip.getItemCount(); i++) {
                final SpannableStringBuilder paste;
                if (withFormatting && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //////??????[BUG]模拟器中：api 23以下无法获得剪切板内容
                    paste = new SpannableStringBuilder(clip.getItemAt(i).coerceToStyledText(getContext()));
                } else {
                    // Get an item as text and remove all spans by toString().
                    final CharSequence text = clip.getItemAt(i).coerceToText(getContext());
                    paste = new SpannableStringBuilder((text instanceof Spanned) ? text.toString() : text);
                }
                if (!TextUtils.isEmpty(paste)) {
                    if (!didFirst) {

                        ///如果为paint text则不执行loadSpans
                        if (withFormatting && mLoadSpansCallback != null) {
                            //////??????[BUG#ClipDescription的label总是为“host clipboard”]因此无法用label区分剪切板是否为RichEditor或其它App，只能用文本是否相同来“大约”区分
//                            final ClipDescription clipDescription = clipboard.getPrimaryClipDescription();
//                            if (clipDescription != null && getContext().getPackageName().equals(clipDescription.getLabel().toString())) {
                            mLoadSpansCallback.loadSpans(paste, min);
//                            }
                        }

                        ///注意：必须加入Selection.setSelection()，否则，在API 28会出现异常：java.lang.IllegalArgumentException: Invalid offset: XXX. Valid range is [0, X]
                        Selection.setSelection(getText(), max);

                        getText().replace(min, max, paste);
                        didFirst = true;
                    } else {
                        getText().insert(getSelectionEnd(), "\n");
                        getText().insert(getSelectionEnd(), paste);
                    }
                }
            }
        }
    }

    @CheckResult
    private boolean setPrimaryClip(ClipData clip) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        try {
            clipboard.setPrimaryClip(clip);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

}
