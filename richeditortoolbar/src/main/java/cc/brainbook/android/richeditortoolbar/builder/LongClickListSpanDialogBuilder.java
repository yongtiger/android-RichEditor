package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.brainbook.android.colorpicker.ColorPickerView;
import cc.brainbook.android.richeditortoolbar.R;

public class LongClickListSpanDialogBuilder extends BaseColorPickerDialogBuilder {
	///[IndentWidth]
	@IntRange(from = 0) private int mIndentWidth;

	///[Indicator]
	@IntRange(from = 0)  private int mIndicatorWidth;
	@IntRange(from = 0)  private int mIndicatorGapWidth;

	private TextView mTextViewIndentWidth;
	private SeekBar mSeekBarIndentWidth;
	private TextView mTextViewIndicatorWidth;
	private SeekBar mSeekBarIndicatorWidth;
	private TextView mTextViewIndicatorGapWidth;
	private SeekBar mSeekIndicatorGapWidth;


	public interface OnClickListener {
		void onClick(DialogInterface d, @IntRange(from = 0) int indentWidth,
					 @IntRange(from = 0) int indicatorWidth,
					 @IntRange(from = 0) int indicatorGapWidth,
					 @ColorInt int indicatorColor, Integer[] allColors);
	}

	public LongClickListSpanDialogBuilder initial(@IntRange(from = 0) int indentWidth,
												  @IntRange(from = 0) int indicatorWidth,
												  @IntRange(from = 0) int indicatorGapWidth,
												  @ColorInt int indicatorColor) {
		mIndentWidth = indentWidth;
		mIndicatorWidth = indicatorWidth;
		mIndicatorGapWidth = indicatorGapWidth;
		this.initialColor[0] = indicatorColor;

		return this;
	}

	private void initView(View layout) {
		mTextViewIndentWidth = (TextView) layout.findViewById(R.id.tv_indent_width);
		mSeekBarIndentWidth = (SeekBar) layout.findViewById(R.id.sb_indent_width);
		mTextViewIndicatorWidth = (TextView) layout.findViewById(R.id.tv_indicator_width);
		mSeekBarIndicatorWidth = (SeekBar) layout.findViewById(R.id.sb_indicator_width);
		mTextViewIndicatorGapWidth = (TextView) layout.findViewById(R.id.tv_indicator_gap_width);
		mSeekIndicatorGapWidth = (SeekBar) layout.findViewById(R.id.sb_indicator_gap_width);

		mSeekBarIndentWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mIndentWidth = progress;
				mTextViewIndentWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.list_span_indent_width), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		mSeekBarIndicatorWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mIndicatorWidth = progress;
				mTextViewIndicatorWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.list_span_indicator_width), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		mSeekIndicatorGapWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mIndicatorGapWidth = progress;
				mTextViewIndicatorGapWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.list_span_indicator_gap_width), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
	}

	private void setupView(Context context) {
		mTextViewIndentWidth.setText(String.format(context.getResources().getString(R.string.bullet_span_bullet_radius), mIndentWidth));
		mSeekBarIndentWidth.setProgress(mIndentWidth);
		mTextViewIndicatorWidth.setText(String.format(context.getResources().getString(R.string.bullet_span_bullet_radius), mIndicatorWidth));
		mSeekBarIndicatorWidth.setProgress(mIndicatorWidth);
		mTextViewIndicatorGapWidth.setText(String.format(context.getResources().getString(R.string.bullet_span_gap_width), mIndicatorGapWidth));
		mSeekIndicatorGapWidth.setProgress(mIndicatorGapWidth);
	}


	private LongClickListSpanDialogBuilder(Context context) {
		this(context, 0);
	}

	private LongClickListSpanDialogBuilder(Context context, int theme) {
		colorPickerView = new ColorPickerView(context);

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_long_click_list_span_dialog, null);
		pickerContainer = (LinearLayout) layout.findViewById(R.id.picker_container);
		pickerContainer.addView(colorPickerView);

		initView(layout);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}


	public static LongClickListSpanDialogBuilder with(Context context) {
		return new LongClickListSpanDialogBuilder(context);
	}

	public static LongClickListSpanDialogBuilder with(Context context, int theme) {
		return new LongClickListSpanDialogBuilder(context, theme);
	}


	public AlertDialog build() {
		Context context = builder.getContext();

		setupColorPicker(context);
		setupView(context);

		return builder.create();
	}


	public LongClickListSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				Integer[] allColors = colorPickerView.getAllColors();
				onClickListener.onClick(dialog, mIndentWidth, mIndicatorWidth, mIndicatorGapWidth, selectedColor, allColors);
			}
		});
		return this;
	}

	public LongClickListSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				Integer[] allColors = colorPickerView.getAllColors();
				onClickListener.onClick(dialog, mIndentWidth, mIndicatorWidth, mIndicatorGapWidth, selectedColor, allColors);
			}
		});
		return this;
	}

}