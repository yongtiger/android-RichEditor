package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cc.brainbook.android.richeditortoolbar.R;

public class ImageSpanDialogBuilder {
	private EditText mEditTextImageSrc;

	private AlertDialog.Builder builder;

	private ImageSpanDialogBuilder(Context context) {
		this(context, 0);
	}

	private ImageSpanDialogBuilder(Context context, int theme) {
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_image_span_dialog, null);
		mEditTextImageSrc = layout.findViewById(R.id.et_image_src);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}

	public static ImageSpanDialogBuilder with(Context context) {
		return new ImageSpanDialogBuilder(context);
	}

	public static ImageSpanDialogBuilder with(Context context, int theme) {
		return new ImageSpanDialogBuilder(context, theme);
	}

	public ImageSpanDialogBuilder setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public ImageSpanDialogBuilder setTitle(int titleId) {
		builder.setTitle(titleId);
		return this;
	}

	public ImageSpanDialogBuilder initial(String imageSrc) {
		mEditTextImageSrc.setText(imageSrc);

		return this;
	}

	public ImageSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mEditTextImageSrc.getText().toString());
			}
		});
		return this;
	}

	public ImageSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mEditTextImageSrc.getText().toString());
			}
		});
		return this;
	}

	public ImageSpanDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(text, onClickListener);
		return this;
	}

	public ImageSpanDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(textId, onClickListener);
		return this;
	}

	public ImageSpanDialogBuilder setNeutralButton(int textId, DialogInterface.OnClickListener onClickListener) {
		builder.setNeutralButton(textId, onClickListener);
		return this;
	}
	public ImageSpanDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
		builder.setNeutralButton(text, onClickListener);
		return this;
	}

	public AlertDialog build() {
		Context context = builder.getContext();

		return builder.create();
	}

	public interface OnClickListener {
		void onClick(DialogInterface d, String imageSrc);
	}
}