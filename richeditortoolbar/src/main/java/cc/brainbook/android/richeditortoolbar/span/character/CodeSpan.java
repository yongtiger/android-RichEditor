package cc.brainbook.android.richeditortoolbar.span.character;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;

//public class CodeSpan extends CharacterStyle implements UpdateAppearance, Parcelable, ICharacterStyle {
public class CodeSpan extends MetricAffectingSpan implements Parcelable, ICharacterStyle {
    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(Typeface.MONOSPACE);
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint textPaint) {

    }

//    ///StyleSpan#apply(Paint paint, int style)
//    private static void applyStyle(Paint paint) {
//        int style = Typeface.BOLD;
//        int oldStyle;
//
//        Typeface old = paint.getTypeface();
//        if (old == null) {
//            oldStyle = 0;
//        } else {
//            oldStyle = old.getStyle();
//        }
//
//        int want = oldStyle | style;
//
//        Typeface tf;
//        if (old == null) {
//            tf = Typeface.defaultFromStyle(want);
//        } else {
//            tf = Typeface.create(old, want);
//        }
//
//        int fake = want & ~tf.getStyle();
//
//        if ((fake & Typeface.BOLD) != 0) {
//            paint.setFakeBoldText(true);
//        }
//
//        if ((fake & Typeface.ITALIC) != 0) {
//            paint.setTextSkewX(-0.25f);
//        }
//
//        paint.setTypeface(tf);
//    }


    public static final Creator<CodeSpan> CREATOR = new Creator<CodeSpan>() {
        @Override
        public CodeSpan createFromParcel(Parcel in) {
            return new CodeSpan();
        }

        @Override
        public CodeSpan[] newArray(int size) {
            return new CodeSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

}
