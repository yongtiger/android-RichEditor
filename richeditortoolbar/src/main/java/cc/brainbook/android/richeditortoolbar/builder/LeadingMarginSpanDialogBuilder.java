package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.brainbook.android.richeditortoolbar.R;

public class LeadingMarginSpanDialogBuilder {
	private int mIndent;
	private TextView mTextViewIndent;
	private SeekBar mSeekBarIndent;

	private AlertDialog.Builder builder;

	private LeadingMarginSpanDialogBuilder(Context context) {
		this(context, 0);
	}

	private LeadingMarginSpanDialogBuilder(Context context, int theme) {
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_leading_margin_span_dialog, null);

		mTextViewIndent = (TextView) layout.findViewById(R.id.tv_indent);
		mSeekBarIndent = (SeekBar) layout.findViewById(R.id.sb_indent);
		mSeekBarIndent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mIndent = progress;
				mTextViewIndent.setText(String.format(seekBar.getContext().getResources().getString(R.string.leading_margin_span_indent), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}

	public static LeadingMarginSpanDialogBuilder with(Context context) {
		return new LeadingMarginSpanDialogBuilder(context);
	}

	public static LeadingMarginSpanDialogBuilder with(Context context, int theme) {
		return new LeadingMarginSpanDialogBuilder(context, theme);
	}

	public LeadingMarginSpanDialogBuilder setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public LeadingMarginSpanDialogBuilder setTitle(int titleId) {
		builder.setTitle(titleId);
		return this;
	}

	public LeadingMarginSpanDialogBuilder initial(int indent) {
		mIndent = indent;

		return this;
	}

	public LeadingMarginSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mIndent);
			}
		});
		return this;
	}

	public LeadingMarginSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mIndent);
			}
		});
		return this;
	}

	public LeadingMarginSpanDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(text, onClickListener);
		return this;
	}

	public LeadingMarginSpanDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(textId, onClickListener);
		return this;
	}

	public LeadingMarginSpanDialogBuilder setNeutralButton(int textId, DialogInterface.OnClickListener onClickListener) {
		builder.setNeutralButton(textId, onClickListener);
		return this;
	}
	public LeadingMarginSpanDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
		builder.setNeutralButton(text, onClickListener);
		return this;
	}

	public AlertDialog build() {
		Context context = builder.getContext();

		mTextViewIndent.setText(String.format(context.getResources().getString(R.string.leading_margin_span_indent), mIndent));
		mSeekBarIndent.setProgress(mIndent);

		return builder.create();
	}

	public interface OnClickListener {
		void onClick(DialogInterface d, int indent);
	}
}