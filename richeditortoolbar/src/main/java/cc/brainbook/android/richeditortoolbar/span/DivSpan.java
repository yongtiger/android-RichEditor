package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.ParagraphStyle;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class DivSpan extends NestSpan implements ParagraphStyle, Parcelable, INestParagraphStyle {
	public DivSpan(int nestingLevel) {
		super(nestingLevel);
	}


	public static final Creator<DivSpan> CREATOR = new Creator<DivSpan>() {
		@Override
		public DivSpan createFromParcel(Parcel in) {
			final int nestingLevel = in.readInt();

			return new DivSpan(nestingLevel);
		}

		@Override
		public DivSpan[] newArray(int size) {
			return new DivSpan[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(getNestingLevel());
	}

}
