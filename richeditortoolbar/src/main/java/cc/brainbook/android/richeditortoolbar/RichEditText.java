package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class RichEditText extends AppCompatEditText {
    private OnSelectionChanged mOnSelectionChanged;

    public interface OnSelectionChanged {
        void selectionChanged(int selStart, int selEnd);
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
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if (mOnSelectionChanged != null) {
            mOnSelectionChanged.selectionChanged(selStart, selEnd);
        }
    }

    public void setOnSelectionChanged(OnSelectionChanged selectionChanged) {
        mOnSelectionChanged = selectionChanged;
    }

}
