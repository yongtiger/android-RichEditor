package cc.brainbook.android.richeditortoolbar;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;

///处理CustomImageSpan及其子类VideoSpan/AudioSpan的点击事件
///http://stackoverflow.com/a/23566268/569430
public class EnhancedMovementMethod extends ArrowKeyMovementMethod {
    private static EnhancedMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new EnhancedMovementMethod ();
        }
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, @NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            if (x < 0) return true;

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ///处理CustomImageSpan及其子类VideoSpan/AudioSpan的点击事件
            CustomImageSpan[] link = buffer.getSpans(off, off, CustomImageSpan.class);
            if (link.length != 0) {
                link[0].onClick(widget);

                return true;
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

}
