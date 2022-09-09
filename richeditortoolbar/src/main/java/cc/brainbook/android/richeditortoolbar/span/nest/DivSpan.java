package cc.brainbook.android.richeditortoolbar.span.nest;

import android.os.Parcel;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IReadableStyle;

public class DivSpan implements INestParagraphStyle, IReadableStyle {
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


	public DivSpan(int nestingLevel) {
		mNestingLevel = nestingLevel;
	}


	public static final Creator<DivSpan> CREATOR = new Creator<DivSpan>() {
		@Override
		@NonNull
		public DivSpan createFromParcel(@NonNull Parcel in) {
			final int nestingLevel = in.readInt();

			return new DivSpan(nestingLevel);
		}

		@Override
		@NonNull
		public DivSpan[] newArray(int size) {
			return new DivSpan[size];
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
