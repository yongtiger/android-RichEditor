package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.brainbook.android.richeditortoolbar.R;

public class LongClickLineDividerDialogBuilder extends BaseDialogBuilder {

    private int mMarginTop;
    private int mMarginBottom;
    
    private TextView mTextViewMarginTop;
    private SeekBar mSeekBarMarginTop;
    private TextView mTextViewMarginBottom;
    private SeekBar mSeekBarMarginBottom;


    public interface OnClickListener {
        void onClick(DialogInterface d, int marginTop, int marginBottom);
    }

    public LongClickLineDividerDialogBuilder initial(int marginTop, int marginBottom) {
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;

        return this;
    }

    private void initView(View layout) {
        mTextViewMarginTop = (TextView) layout.findViewById(R.id.tv_margin_top);
        mSeekBarMarginTop = (SeekBar) layout.findViewById(R.id.sb_margin_top);
        mSeekBarMarginTop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMarginTop = progress;
                mTextViewMarginTop.setText(String.format(seekBar.getContext().getResources().getString(R.string.line_divider_span_margin_top), progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mTextViewMarginBottom = (TextView) layout.findViewById(R.id.tv_margin_bottom);
        mSeekBarMarginBottom = (SeekBar) layout.findViewById(R.id.sb_margin_bottom);
        mSeekBarMarginBottom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMarginBottom = progress;
                mTextViewMarginBottom.setText(String.format(seekBar.getContext().getResources().getString(R.string.line_divider_span_margin_bottom), progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupView(Context context) {
        mTextViewMarginTop.setText(String.format(context.getResources().getString(R.string.line_divider_span_margin_top), mMarginTop));
        mSeekBarMarginTop.setProgress(mMarginTop);
        mTextViewMarginBottom.setText(String.format(context.getResources().getString(R.string.line_divider_span_margin_bottom), mMarginBottom));
        mSeekBarMarginBottom.setProgress(mMarginBottom);
    }


    private LongClickLineDividerDialogBuilder(Context context) {
        this(context, 0);
    }

    private LongClickLineDividerDialogBuilder(Context context, int theme) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.layout_long_click_line_divider_span_dialog, null);

        initView(layout);

        builder = new AlertDialog.Builder(context, theme);
        builder.setView(layout);
    }

    public static LongClickLineDividerDialogBuilder with(Context context) {
        return new LongClickLineDividerDialogBuilder(context);
    }

    public static LongClickLineDividerDialogBuilder with(Context context, int theme) {
        return new LongClickLineDividerDialogBuilder(context, theme);
    }


    public AlertDialog build() {
        Context context = builder.getContext();

        setupView(context);

        return builder.create();
    }


    public LongClickLineDividerDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onClick(dialog, mMarginTop, mMarginBottom);
            }
        });
        return this;
    }

    public LongClickLineDividerDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
        builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onClick(dialog, mMarginTop, mMarginBottom);
            }
        });
        return this;
    }

}
