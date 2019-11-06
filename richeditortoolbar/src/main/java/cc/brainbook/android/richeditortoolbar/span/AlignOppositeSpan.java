package cc.brainbook.android.richeditortoolbar.span;

import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

public class AlignOppositeSpan implements AlignmentSpan {

	@Override
	public Alignment getAlignment() {
		return Alignment.ALIGN_OPPOSITE;
	}
}
