package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;

import cc.brainbook.android.richeditortoolbar.span.CustomImageSpan;

public class RichEditText extends AppCompatEditText implements Drawable.Callback {
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

    ///[ImageSpan#Glide#GifDrawable]
    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        ///注意：实测此方法不闪烁！
        ///https://www.cnblogs.com/mfrbuaa/p/5045666.html
        final CustomImageSpan imageSpan = getImageSpan(drawable);
        if (imageSpan != null) {
            final Editable editable = getText();
            if (!TextUtils.isEmpty(editable)) {
                final int spanStart = editable.getSpanStart(imageSpan);
                final int spanEnd = editable.getSpanEnd(imageSpan);
                final int spanFlags = editable.getSpanFlags(imageSpan);

                ///注意：不必先removeSpan()！只setSpan()就能实现局部刷新EditText，以便让Gif动起来
//                    editable.removeSpan(imageSpan);
                editable.setSpan(imageSpan, spanStart, spanEnd, spanFlags);
            }
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    private CustomImageSpan getImageSpan(Drawable drawable) {
        CustomImageSpan imageSpan = null;
        final Editable editable = getText();
        if (!TextUtils.isEmpty(editable)) {
            final CustomImageSpan[] spans = editable.getSpans(0, editable.length(), CustomImageSpan.class);
            if (spans != null && spans.length > 0) {
                for (CustomImageSpan span : spans) {
                    if (drawable == span.getDrawable()) {
                        imageSpan = span;
                    }
                }
            }
        }

        return imageSpan;
    }
}
