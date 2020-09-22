package cc.brainbook.android.richeditortoolbar.span.nest;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.LineHeightSpan;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

//////??????[UPGRADE#LineMarginSpanTemp]
public class LineMarginSpanTemp implements LineHeightSpan, Parcelable {
    @Expose
    private final int mMarginTop, mMarginBottom;


    public LineMarginSpanTemp(int marginTop, int marginBottom) {
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;
    }


    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        if (lineHeight == 0) {  ///避免多行文本时重复叠加计算
            fm.top -= mMarginTop;
            fm.ascent -= mMarginTop;

            fm.bottom += mMarginBottom;
            fm.descent += mMarginBottom;
        }
    }


    public static final Creator<LineMarginSpanTemp> CREATOR = new Creator<LineMarginSpanTemp>() {
        @Override
        @NonNull
        public LineMarginSpanTemp createFromParcel(@NonNull Parcel in) {
            final int marginTop = in.readInt();
            final int marginBottom = in.readInt();

            return new LineMarginSpanTemp(marginTop, marginBottom);
        }

        @Override
        @NonNull
        public LineMarginSpanTemp[] newArray(int size) {
            return new LineMarginSpanTemp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(mMarginTop);
        dest.writeInt(mMarginBottom);
    }

}
