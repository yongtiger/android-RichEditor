package cc.brainbook.android.richeditortoolbar.util;

import android.text.Spanned;
import android.util.Log;

import androidx.annotation.NonNull;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public abstract class Util {

    /* ---------------------- ///test ---------------------- */
    public static <T> void testOutput(@NonNull Spanned spanned, Class<T> clazz) {
        final T[] spans = spanned.getSpans(0, spanned.length(), clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = spanned.getSpanStart(span);
            final int spanEnd = spanned.getSpanEnd(span);

            if (span instanceof INestParagraphStyle) {
                Log.d("TAG", span.getClass().getSimpleName() + ": " + spanStart + ", " + spanEnd
                        + "  nest = " + ((INestParagraphStyle) span).getNestingLevel());
            } else  {
                Log.d("TAG", span.getClass().getSimpleName() + ": " + spanStart + ", " + spanEnd);
            }
        }
    }

}
