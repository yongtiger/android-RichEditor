package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.brainbook.android.richeditortoolbar.R;

public class LongClickLeadingMarginSpanDialogBuilder extends BaseDialogBuilder {

	private int mIndent;
	private TextView mTextViewIndent;
	private SeekBar mSeekBarIndent;


	public interface OnClickListener {
		void onClick(DialogInterface d, int indent);
	}

	public LongClickLeadingMarginSpanDialogBuilder initial(int indent) {
		mIndent = indent;

		return this;
	}

	private void initView(@NonNull View layout) {
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
	}

	private void setupView(@NonNull Context context) {
		mTextViewIndent.setText(String.format(context.getResources().getString(R.string.leading_margin_span_indent), mIndent));
		mSeekBarIndent.setProgress(mIndent);
	}


	private LongClickLeadingMarginSpanDialogBuilder(@NonNull Context context) {
		this(context, 0);
	}

	private LongClickLeadingMarginSpanDialogBuilder(@NonNull Context context, int theme) {
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.long_click_leading_margin_span_dialog, null);

		initView(layout);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}


	@NonNull
	public static LongClickLeadingMarginSpanDialogBuilder with(@NonNull Context context) {
		return new LongClickLeadingMarginSpanDialogBuilder(context);
	}

	@NonNull
	public static LongClickLeadingMarginSpanDialogBuilder with(@NonNull Context context, int theme) {
		return new LongClickLeadingMarginSpanDialogBuilder(context, theme);
	}


	public AlertDialog build() {
		Context context = builder.getContext();

		setupView(context);

		return builder.create();
	}


	public LongClickLeadingMarginSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mIndent);
			}
		});
		return this;
	}

	public LongClickLeadingMarginSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mIndent);
			}
		});
		return this;
	}

}