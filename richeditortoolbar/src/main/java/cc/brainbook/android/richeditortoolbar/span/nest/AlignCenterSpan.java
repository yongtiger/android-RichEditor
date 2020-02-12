package cc.brainbook.android.richeditortoolbar.span.nest;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class AlignCenterSpan implements AlignmentSpan, Parcelable, INestParagraphStyle {
	///[NestingLevel]
	@Expose
	private int mNestingLevel;
	@Override
	public int getNestingLevel() {
		return mNestingLevel;
	}
	@Override
	public void setNestingLevel(int nestingLevel) {
		mNestingLevel = nestingLevel;
	}


	public AlignCenterSpan(int nestingLevel) {
		mNestingLevel = nestingLevel;
	}


	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_CENTER;
	}


	public static final Creator<AlignCenterSpan> CREATOR = new Creator<AlignCenterSpan>() {
		@Override
		public AlignCenterSpan createFromParcel(Parcel in) {
			final int nestingLevel = in.readInt();

			return new AlignCenterSpan(nestingLevel);
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
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(getNestingLevel());
	}

}
