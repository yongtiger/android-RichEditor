package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.brainbook.android.colorpicker.ColorPickerView;
import cc.brainbook.android.richeditortoolbar.R;

public class LongClickListSpanDialogBuilder extends BaseColorPickerDialogBuilder {
	///[IndicatorMargin]
	@IntRange(from = 0) private int mIndicatorMargin;

	///[Indicator]
	@IntRange(from = 0)  private int mIndicatorWidth;
	@IntRange(from = 0)  private int mIndicatorGapWidth;

	private TextView mTextViewIndicatorMargin;
	private SeekBar mSeekBarIndicatorMargin;
	private TextView mTextViewIndicatorWidth;
	private SeekBar mSeekBarIndicatorWidth;
	private TextView mTextViewIndicatorGapWidth;
	private SeekBar mSeekIndicatorGapWidth;


	public interface OnClickListener {
		void onClick(DialogInterface d, @IntRange(from = 0) int indicatorMargin,
					 @IntRange(from = 0) int indicatorWidth,
					 @IntRange(from = 0) int indicatorGapWidth,
					 @ColorInt int indicatorColor, Integer[] allColors);
	}

	public LongClickListSpanDialogBuilder initial(@IntRange(from = 0) int indicatorMargin,
												  @IntRange(from = 0) int indicatorWidth,
												  @IntRange(from = 0) int indicatorGapWidth,
												  @ColorInt int indicatorColor) {
		mIndicatorMargin = indicatorMargin;
		mIndicatorWidth = indicatorWidth;
		mIndicatorGapWidth = indicatorGapWidth;
		this.initialColor[0] = indicatorColor;

		return this;
	}

	private void initView(@NonNull View layout) {
		mTextViewIndicatorMargin = (TextView) layout.findViewById(R.id.tv_indicator_margin);
		mSeekBarIndicatorMargin = (SeekBar) layout.findViewById(R.id.sb_indicator_margin);
		mTextViewIndicatorWidth = (TextView) layout.findViewById(R.id.tv_indicator_width);
		mSeekBarIndicatorWidth = (SeekBar) layout.findViewById(R.id.sb_indicator_width);
		mTextViewIndicatorGapWidth = (TextView) layout.findViewById(R.id.tv_indicator_gap_width);
		mSeekIndicatorGapWidth = (SeekBar) layout.findViewById(R.id.sb_indicator_gap_width);

		mSeekBarIndicatorMargin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mIndicatorMargin = progress;
				mTextViewIndicatorMargin.setText(String.format(seekBar.getContext().getResources().getString(R.string.long_click_list_span_dialog_builder_text_list_span_indicator_margin), progress));
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
				mTextViewIndicatorWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.long_click_list_span_dialog_builder_text_list_span_indicator_width), progress));
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
				mTextViewIndicatorGapWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.long_click_list_span_dialog_builder_text_list_span_indicator_gap_width), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
	}

	private void setupView(@NonNull Context context) {
		mTextViewIndicatorMargin.setText(String.format(context.getResources().getString(R.string.long_click_list_span_dialog_builder_text_list_span_indicator_margin), mIndicatorMargin));
		mSeekBarIndicatorMargin.setProgress(mIndicatorMargin);
		mTextViewIndicatorWidth.setText(String.format(context.getResources().getString(R.string.long_click_list_span_dialog_builder_text_list_span_indicator_width), mIndicatorWidth));
		mSeekBarIndicatorWidth.setProgress(mIndicatorWidth);
		mTextViewIndicatorGapWidth.setText(String.format(context.getResources().getString(R.string.long_click_list_span_dialog_builder_text_list_span_indicator_gap_width), mIndicatorGapWidth));
		mSeekIndicatorGapWidth.setProgress(mIndicatorGapWidth);
	}


	private LongClickListSpanDialogBuilder(@NonNull Context context) {
		this(context, 0);
	}

	private LongClickListSpanDialogBuilder(@NonNull Context context, int theme) {
		colorPickerView = new ColorPickerView(context);

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.long_click_list_span_dialog, null);
		pickerContainer = (LinearLayout) layout.findViewById(R.id.picker_container);
		pickerContainer.addView(colorPickerView);

		initView(layout);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}


	@NonNull
	public static LongClickListSpanDialogBuilder with(@NonNull Context context) {
		return new LongClickListSpanDialogBuilder(context);
	}

	@NonNull
	public static LongClickListSpanDialogBuilder with(@NonNull Context context, int theme) {
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
				onClickListener.onClick(dialog, mIndicatorMargin, mIndicatorWidth, mIndicatorGapWidth, selectedColor, allColors);
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
				onClickListener.onClick(dialog, mIndicatorMargin, mIndicatorWidth, mIndicatorGapWidth, selectedColor, allColors);
			}
		});
		return this;
	}

}