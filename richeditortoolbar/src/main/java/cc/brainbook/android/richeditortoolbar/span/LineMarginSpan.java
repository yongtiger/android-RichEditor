package cc.brainbook.android.richeditortoolbar.span;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.LineHeightSpan;

import com.google.gson.annotations.Expose;

//////??????[UPGRADE#LineMarginSpan]
public class LineMarginSpan implements LineHeightSpan, Parcelable {
    @Expose
    private final int mMarginTop, mMarginBottom;


    public LineMarginSpan(int marginTop, int marginBottom) {
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


    public static final Creator<LineMarginSpan> CREATOR = new Creator<LineMarginSpan>() {
        @Override
        public LineMarginSpan createFromParcel(Parcel in) {
            final int marginTop = in.readInt();
            final int marginBottom = in.readInt();

            return new LineMarginSpan(marginTop, marginBottom);
        }

        @Override
        public LineMarginSpan[] newArray(int size) {
            return new LineMarginSpan[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mMarginTop);
        dest.writeInt(mMarginBottom);
    }

}
