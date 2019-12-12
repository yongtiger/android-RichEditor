package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

public class AlignOppositeSpan implements AlignmentSpan, Parcelable {

	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_OPPOSITE;
	}

	public static final Creator<AlignOppositeSpan> CREATOR = new Creator<AlignOppositeSpan>() {
		@Override
		public AlignOppositeSpan createFromParcel(Parcel in) {
			return new AlignOppositeSpan();
		}

		@Override
		public AlignOppositeSpan[] newArray(int size) {
			return new AlignOppositeSpan[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {}

}
