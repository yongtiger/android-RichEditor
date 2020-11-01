package cc.brainbook.android.richeditortoolbar.builder;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cc.brainbook.android.richeditortoolbar.R;

public class ClickURLSpanDialogBuilder extends BaseDialogBuilder {

	private EditText mEditTextText;
	private EditText mEditTextUrl;


	public interface OnClickListener {
		void onClick(DialogInterface d, String text, String url);
	}

	public ClickURLSpanDialogBuilder initial(String text, String url) {
		mEditTextText.setText(text);
		mEditTextUrl.setText(url);

		return this;
	}


	private ClickURLSpanDialogBuilder(@NonNull Context context) {
		this(context, 0);
	}

	private ClickURLSpanDialogBuilder(@NonNull Context context, int theme) {
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.click_url_span_dialog, null);
		mEditTextText = (EditText) layout.findViewById(R.id.et_text);
		mEditTextUrl = (EditText) layout.findViewById(R.id.et_url);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}


	@NonNull
	public static ClickURLSpanDialogBuilder with(@NonNull Context context) {
		return new ClickURLSpanDialogBuilder(context);
	}

	@NonNull
	public static ClickURLSpanDialogBuilder with(@NonNull Context context, int theme) {
		return new ClickURLSpanDialogBuilder(context, theme);
	}


	public AlertDialog build() {
		Context context = builder.getContext();

		return builder.create();
	}


	public ClickURLSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mEditTextText.getText().toString(), mEditTextUrl.getText().toString());
			}
		});
		return this;
	}

	public ClickURLSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, mEditTextText.getText().toString(), mEditTextUrl.getText().toString());
			}
		});
		return this;
	}

}