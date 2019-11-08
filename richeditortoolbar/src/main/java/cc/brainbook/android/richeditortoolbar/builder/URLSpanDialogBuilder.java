package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cc.brainbook.android.richeditortoolbar.R;

public class URLSpanDialogBuilder {
	private EditText mEditTextLink;

	private AlertDialog.Builder builder;

	private URLSpanDialogBuilder(Context context) {
		this(context, 0);
	}

	private URLSpanDialogBuilder(Context context, int theme) {
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_url_span_dialog, null);
		mEditTextLink = layout.findViewById(R.id.et_link);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}

	public static URLSpanDialogBuilder with(Context context) {
		return new URLSpanDialogBuilder(context);
	}

	public static URLSpanDialogBuilder with(Context context, int theme) {
		return new URLSpanDialogBuilder(context, theme);
	}

	public URLSpanDialogBuilder setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public URLSpanDialogBuilder setTitle(int titleId) {
		builder.setTitle(titleId);
		return this;
	}

	public URLSpanDialogBuilder initial(String link) {
		mEditTextLink.setText(link);

		return this;
	}

	public URLSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mEditTextLink.getText().toString());
			}
		});
		return this;
	}

	public URLSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mEditTextLink.getText().toString());
			}
		});
		return this;
	}

	public URLSpanDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(text, onClickListener);
		return this;
	}

	public URLSpanDialogBuilder setNegativeButton(int textId, DialogInterface.OnClickListener onClickListener) {
		builder.setNegativeButton(textId, onClickListener);
		return this;
	}

	public URLSpanDialogBuilder setNeutralButton(int textId, DialogInterface.OnClickListener onClickListener) {
		builder.setNeutralButton(textId, onClickListener);
		return this;
	}
	public URLSpanDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
		builder.setNeutralButton(text, onClickListener);
		return this;
	}

	public AlertDialog build() {
		Context context = builder.getContext();

		return builder.create();
	}

	public interface OnClickListener {
		void onClick(DialogInterface d, String link);
	}
}