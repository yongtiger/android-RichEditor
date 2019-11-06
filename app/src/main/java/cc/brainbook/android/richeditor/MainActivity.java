package cc.brainbook.android.richeditor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.EditText;

import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;

public class MainActivity extends AppCompatActivity {
    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRichEditText = findViewById(R.id.edit_text);
        mRichEditorToolbar = findViewById(R.id.rich_editor_tool_bar);
        mRichEditorToolbar.setEditText(mRichEditText);

//        mRichEditText.setText("aaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaa");

        ///test
//        mRichEditText.setText("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
//        for (int i = 0; i < 20; i++) {
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 6, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 2, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 10, 15, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 11, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 26, 28, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 22, 25, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 20, 23, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 20, 26, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 30, 35, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 31, 33, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 42, 45, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 40, 43, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 40, 46, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 40, 45, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 51, 53, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 56, 58, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 52, 55, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 50, 53, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 60, 66, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 60, 65, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 61, 63, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        ///https://stackoverflow.com/questions/16558948/how-to-use-textview-getlayout-it-returns-null
//        SpanUtil.getThisLineStart(mRichEditText, 1);
    }
}
