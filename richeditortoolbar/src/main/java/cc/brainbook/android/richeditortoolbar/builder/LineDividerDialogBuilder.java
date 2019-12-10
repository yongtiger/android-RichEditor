package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.brainbook.android.richeditortoolbar.R;

public class LineDividerDialogBuilder {
    private int mMarginTop;
    private int mMarginBottom;
    
    private TextView mTextViewMarginTop;
    private SeekBar mSeekBarMarginTop;
    private TextView mTextViewMarginBottom;
    private SeekBar mSeekBarMarginBottom;
    
    private AlertDialog.Builder builder;

    private LineDividerDialogBuilder(Context context) {
        this(context, 0);
    }

    private LineDividerDialogBuilder(Context context, int theme) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.layout_line_divider_span_dialog, null);

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

        builder = new AlertDialog.Builder(context, theme);
        builder.setView(layout);
    }

    public static LineDividerDialogBuilder with(Context context) {
        return new LineDividerDialogBuilder(context);
    }

    public static LineDividerDialogBuilder with(Context context, int theme) {
        return new LineDividerDialogBuilder(context, theme);
    }

    public LineDividerDialogBuilder setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public LineDividerDialogBuilder setTitle(int titleId) {
        builder.setTitle(titleId);
        return this;
    }

    public LineDividerDialogBuilder initial(int marginTop, int marginBottom) {
        mMarginTop = marginTop;
        mMarginBottom = marginBottom;

        return this;
    }

    public LineDividerDialogBuilder setPositiveButton(CharSequence text, final LineDividerDialogBuilder.OnClickListener onClickListener) {
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onClick(dialog, mMarginTop, mMarginBottom);
            }
        });
        return this;
    }

    public LineDividerDialogBuilder setPositiveButton(int textId, final LineDividerDialogBuilder.OnClickListener onClickListener) {
        builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickListener.onClick(dialog, mMarginTop, mMarginBottom);
            }
        });
        return this;
    }

    public LineDividerDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
        builder.setNegativeButton(text, onClickListener);
        return this;
    }

    public LineDividerDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener onClickListener) {
        builder.setNegativeButton(textId, onClickListener);
        return this;
    }

    public LineDividerDialogBuilder setNeutralButton(int textId, DialogInterface.OnClickListener onClickListener) {
        builder.setNeutralButton(textId, onClickListener);
        return this;
    }
    public LineDividerDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
        builder.setNeutralButton(text, onClickListener);
        return this;
    }

    public AlertDialog build() {
        Context context = builder.getContext();

        mTextViewMarginTop.setText(String.format(context.getResources().getString(R.string.line_divider_span_margin_top), mMarginTop));
        mSeekBarMarginTop.setProgress(mMarginTop);
        mTextViewMarginBottom.setText(String.format(context.getResources().getString(R.string.line_divider_span_margin_bottom), mMarginBottom));
        mSeekBarMarginBottom.setProgress(mMarginBottom);

        return builder.create();
    }

    public interface OnClickListener {
        void onClick(DialogInterface d, int marginTop, int marginBottom);
    }
}
