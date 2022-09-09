package cc.brainbook.android.richeditortoolbar.span.nest;

import android.os.Parcel;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

public class AlignOppositeSpan implements AlignmentSpan, INestParagraphStyle, IReadableStyle {
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


	public AlignOppositeSpan(int nestingLevel) {
		mNestingLevel = nestingLevel;
	}


	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_OPPOSITE;
	}


	public static final Creator<AlignOppositeSpan> CREATOR = new Creator<AlignOppositeSpan>() {
		@Override
		@NonNull
		public AlignOppositeSpan createFromParcel(@NonNull Parcel in) {
			final int nestingLevel = in.readInt();

			return new AlignOppositeSpan(nestingLevel);
		}

		@Override
		@NonNull
		public AlignOppositeSpan[] newArray(int size) {
			return new AlignOppositeSpan[size];
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
