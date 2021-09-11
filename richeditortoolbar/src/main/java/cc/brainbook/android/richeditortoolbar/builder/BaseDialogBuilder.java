package cc.brainbook.android.richeditortoolbar.builder;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;

public class BaseDialogBuilder {
    ///https://stackoverflow.com/questions/9891360/getting-activity-from-context-in-android
    @Nullable
    public static Activity getActivity(@Nullable Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    protected AlertDialog.Builder builder;

    protected RichEditorToolbar mRichEditorToolbar;
    public RichEditorToolbar getRichEditorToolbar() {
        return mRichEditorToolbar;
    }
    public BaseDialogBuilder setRichEditorToolbar(RichEditorToolbar richEditorToolbar) {
        mRichEditorToolbar = richEditorToolbar;
        return this;
    }

    public BaseDialogBuilder setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public BaseDialogBuilder setTitle(int titleId) {
        builder.setTitle(titleId);
        return this;
    }

    public BaseDialogBuilder setPositiveButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
        builder.setPositiveButton(text, onClickListener);
        return this;
    }

    public BaseDialogBuilder setPositiveButton(int textId, DialogInterface.OnClickListener onClickListener) {
        builder.setPositiveButton(textId, onClickListener);
        return this;
    }

    public BaseDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
        builder.setNegativeButton(text, onClickListener);
        return this;
    }

    public BaseDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener onClickListener) {
        builder.setNegativeButton(textId, onClickListener);
        return this;
    }

    public BaseDialogBuilder setNeutralButton(int textId, DialogInterface.OnClickListener onClickListener) {
        builder.setNeutralButton(textId, onClickListener);
        return this;
    }
    public BaseDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
        builder.setNeutralButton(text, onClickListener);
        return this;
    }

    ///避免内存泄漏
    public void clear() {
        builder = null;
    }

}
