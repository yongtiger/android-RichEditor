package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;

public class AlignNormalSpan implements AlignmentSpan, Parcelable, IParagraphStyle {

	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_NORMAL;
	}

	public static final Creator<AlignNormalSpan> CREATOR = new Creator<AlignNormalSpan>() {
		@Override
		public AlignNormalSpan createFromParcel(Parcel in) {
			return new AlignNormalSpan();
		}

		@Override
		public AlignNormalSpan[] newArray(int size) {
			return new AlignNormalSpan[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {}

}
