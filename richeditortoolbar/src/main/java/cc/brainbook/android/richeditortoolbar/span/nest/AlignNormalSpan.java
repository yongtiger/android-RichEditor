package cc.brainbook.android.richeditortoolbar.span.nest;

import android.os.Parcel;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

public class AlignNormalSpan implements AlignmentSpan, INestParagraphStyle, IReadableStyle {
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


	public AlignNormalSpan(int nestingLevel) {
		mNestingLevel = nestingLevel;
	}


	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_NORMAL;
	}


	public static final Creator<AlignNormalSpan> CREATOR = new Creator<AlignNormalSpan>() {
		@Override
		@NonNull
		public AlignNormalSpan createFromParcel(@NonNull Parcel in) {
			final int nestingLevel = in.readInt();

			return new AlignNormalSpan(nestingLevel);
		}

		@Override
		@NonNull
		public AlignNormalSpan[] newArray(int size) {
			return new AlignNormalSpan[size];
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
