package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class BaseDialogBuilder {
    protected AlertDialog.Builder builder;

    ///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
    protected Context mContext;


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

}
