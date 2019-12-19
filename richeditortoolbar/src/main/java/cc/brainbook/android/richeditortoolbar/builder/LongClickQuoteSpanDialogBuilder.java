package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.brainbook.android.colorpicker.ColorPickerView;
import cc.brainbook.android.richeditortoolbar.R;

public class LongClickQuoteSpanDialogBuilder extends BaseColorPickerDialogBuilder {

	private int mStripWidth;
	private int mGapWidth;

	private TextView mTextViewStripWidth;
	private SeekBar mSeekBarStripWidth;
	private TextView mTextViewGapWidth;
	private SeekBar mSeekBarGapWidth;


	public interface OnClickListener {
		void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors, int stripWidth, int gapWidth);
	}

	public LongClickQuoteSpanDialogBuilder initial(int initialColor, int stripWidth, int gapWidth) {
		this.initialColor[0] = initialColor;

		mStripWidth = stripWidth;
		mGapWidth = gapWidth;

		return this;
	}

	private void initView(View layout) {
		mTextViewStripWidth = (TextView) layout.findViewById(R.id.tv_strip_width);
		mSeekBarStripWidth = (SeekBar) layout.findViewById(R.id.sb_strip_width);
		mTextViewGapWidth = (TextView) layout.findViewById(R.id.tv_gap_width);
		mSeekBarGapWidth = (SeekBar) layout.findViewById(R.id.sb_gap_width);
		mSeekBarStripWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mStripWidth = progress;
				mTextViewStripWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.quote_span_strip_width), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		mSeekBarGapWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mGapWidth = progress;
				mTextViewGapWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.quote_span_gap_width), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
	}

	private void setupView(Context context) {
		mTextViewStripWidth.setText(String.format(context.getResources().getString(R.string.quote_span_strip_width), mStripWidth));
		mSeekBarStripWidth.setProgress(mStripWidth);
		mTextViewGapWidth.setText(String.format(context.getResources().getString(R.string.quote_span_gap_width), mGapWidth));
		mSeekBarGapWidth.setProgress(mGapWidth);
	}


	private LongClickQuoteSpanDialogBuilder(Context context) {
		this(context, 0);
	}

	private LongClickQuoteSpanDialogBuilder(Context context, int theme) {
		colorPickerView = new ColorPickerView(context);

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_long_click_quote_span_dialog, null);
		pickerContainer = (LinearLayout) layout.findViewById(R.id.picker_container);
		pickerContainer.addView(colorPickerView);

		initView(layout);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}


	public static LongClickQuoteSpanDialogBuilder with(Context context) {
		return new LongClickQuoteSpanDialogBuilder(context);
	}

	public static LongClickQuoteSpanDialogBuilder with(Context context, int theme) {
		return new LongClickQuoteSpanDialogBuilder(context, theme);
	}


	public AlertDialog build() {
		Context context = builder.getContext();

		setupColorPicker(context);
		setupView(context);

		return builder.create();
	}


	public LongClickQuoteSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				Integer[] allColors = colorPickerView.getAllColors();
				onClickListener.onClick(dialog, selectedColor, allColors, mStripWidth, mGapWidth);
			}
		});
		return this;
	}

	public LongClickQuoteSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				Integer[] allColors = colorPickerView.getAllColors();
				onClickListener.onClick(dialog, selectedColor, allColors, mStripWidth, mGapWidth);
			}
		});
		return this;
	}

}