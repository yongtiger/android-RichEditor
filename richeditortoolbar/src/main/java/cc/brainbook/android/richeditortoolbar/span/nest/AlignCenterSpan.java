package cc.brainbook.android.richeditortoolbar.span.nest;

import android.os.Parcel;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class AlignCenterSpan implements AlignmentSpan, INestParagraphStyle {
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
		@NonNull
		public AlignCenterSpan createFromParcel(@NonNull Parcel in) {
			final int nestingLevel = in.readInt();

			return new AlignCenterSpan(nestingLevel);
		}

		@Override
		@NonNull
		public AlignCenterSpan[] newArray(int size) {
			return new AlignCenterSpan[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeInt(getNestingLevel());
	}

}
