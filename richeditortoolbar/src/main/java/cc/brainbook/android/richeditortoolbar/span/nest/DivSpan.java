package cc.brainbook.android.richeditortoolbar.span.nest;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;

public class DivSpan implements Parcelable, INestParagraphStyle {
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
