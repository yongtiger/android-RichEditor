package cc.brainbook.android.richeditortoolbar.span;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

import cc.brainbook.android.richeditortoolbar.interfaces.IBlockParagraphStyle;

public class AlignCenterSpan implements AlignmentSpan, Parcelable, IBlockParagraphStyle {

	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_CENTER;
	}

	public static final Creator<AlignCenterSpan> CREATOR = new Creator<AlignCenterSpan>() {
		@Override
		public AlignCenterSpan createFromParcel(Parcel in) {
			return new AlignCenterSpan();
		}

		@Override
		public AlignCenterSpan[] newArray(int size) {
			return new AlignCenterSpan[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {}

}
