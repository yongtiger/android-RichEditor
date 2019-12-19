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

public class LongClickBulletSpanDialogBuilder extends BaseColorPickerDialogBuilder {

	private int mBulletRadius;
	private int mGapWidth;

	private TextView mTextViewBulletRadius;
	private SeekBar mSeekBarBulletRadius;
	private TextView mTextViewGapWidth;
	private SeekBar mSeekBarGapWidth;


	public interface OnClickListener {
		void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors, int bulletRadius, int gapWidth);
	}

	public LongClickBulletSpanDialogBuilder initial(int initialColor, int bulletRadius, int gapWidth) {
		this.initialColor[0] = initialColor;

		mBulletRadius = bulletRadius;
		mGapWidth = gapWidth;

		return this;
	}

	private void initView(View layout) {
		mTextViewBulletRadius = (TextView) layout.findViewById(R.id.tv_bullet_radius);
		mSeekBarBulletRadius = (SeekBar) layout.findViewById(R.id.sb_bullet_radius);
		mTextViewGapWidth = (TextView) layout.findViewById(R.id.tv_gap_width);
		mSeekBarGapWidth = (SeekBar) layout.findViewById(R.id.sb_gap_width);
		mSeekBarBulletRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mBulletRadius = progress;
				mTextViewBulletRadius.setText(String.format(seekBar.getContext().getResources().getString(R.string.bullet_span_bullet_radius), progress));
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
				mTextViewGapWidth.setText(String.format(seekBar.getContext().getResources().getString(R.string.bullet_span_gap_width), progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

	}

	private void setupView(Context context) {
		mTextViewBulletRadius.setText(String.format(context.getResources().getString(R.string.bullet_span_bullet_radius), mBulletRadius));
		mSeekBarBulletRadius.setProgress(mBulletRadius);
		mTextViewGapWidth.setText(String.format(context.getResources().getString(R.string.bullet_span_gap_width), mGapWidth));
		mSeekBarGapWidth.setProgress(mGapWidth);
	}


	private LongClickBulletSpanDialogBuilder(Context context) {
		this(context, 0);
	}

	private LongClickBulletSpanDialogBuilder(Context context, int theme) {
		colorPickerView = new ColorPickerView(context);

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_long_click_bullet_span_dialog, null);
		pickerContainer = (LinearLayout) layout.findViewById(R.id.picker_container);
		pickerContainer.addView(colorPickerView);

		initView(layout);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}


	public static LongClickBulletSpanDialogBuilder with(Context context) {
		return new LongClickBulletSpanDialogBuilder(context);
	}

	public static LongClickBulletSpanDialogBuilder with(Context context, int theme) {
		return new LongClickBulletSpanDialogBuilder(context, theme);
	}


	public AlertDialog build() {
		Context context = builder.getContext();

		setupColorPicker(context);
		setupView(context);

		return builder.create();
	}


	public LongClickBulletSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				Integer[] allColors = colorPickerView.getAllColors();
				onClickListener.onClick(dialog, selectedColor, allColors, mBulletRadius, mGapWidth);
			}
		});
		return this;
	}

	public LongClickBulletSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int selectedColor = colorPickerView.getSelectedColor();
				Integer[] allColors = colorPickerView.getAllColors();
				onClickListener.onClick(dialog, selectedColor, allColors, mBulletRadius, mGapWidth);
			}
		});
		return this;
	}

}