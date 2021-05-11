package cc.brainbook.android.richeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_RESULT;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;

public class HtmlEditorActivity extends AppCompatActivity {
    private EditText mEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_editor);

        mEditText = findViewById(R.id.et_edit_text);

        mEditText.setText(getIntent().getStringExtra(KEY_TEXT));
    }


    public void btnClickSave(View view) {
        ///[startActivityForResult#setResult()返回数据]
        final Intent intent = new Intent();

        intent.putExtra(KEY_RESULT, mEditText.getText().toString());

        setResult(RESULT_OK, intent);

        finish();
    }

}