package cc.brainbook.android.richeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

import cc.brainbook.android.richeditortoolbar.EnhancedMovementMethod;
import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.Html;

public class TextActivity extends AppCompatActivity {
    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;
    private TextView mTextViewPreview;
    private EditText mEditTextHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        mRichEditorToolbar = (RichEditorToolbar) findViewById(R.id.rich_editor_tool_bar);
        mRichEditText = (RichEditText) findViewById(R.id.et_rich_edit_text);

        ///[Preview]
        mTextViewPreview = (TextView) findViewById(R.id.tv_preview);
        ///实现TextView超链接五种方式：https://blog.csdn.net/lyankj/article/details/51882335
        ///设置TextView可点击，比如响应URLSpan点击事件。LinkMovementMethod继承了ScrollingMovementMethod，因此无需ScrollingMovementMethod
//        mTextViewPreview.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewPreview.setMovementMethod(EnhancedMovementMethod.getInstance());   ///http://stackoverflow.com/a/23566268/569430
//        mTextViewPreview.setMovementMethod(new ScrollingMovementMethod());  ///让TextView可以滚动显示完整内容

        ///[Html]
        mEditTextHtml = (EditText) findViewById(R.id.et_html);


        /* -------------- ///设置 -------------- */
//        ///（可选）设置编辑器初始文本
//        mRichEditText.setText("\n\naaabbbcccdddeeefffggghhhiiijjjkkklllmmmnnnooopppqqqrrrssstttuuuvvvwwwxxxyyyzzz\n1234567890"); ///test
//        mRichEditText.getText().setSpan(new BoldSpan(), 2, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);    ///test
//        mRichEditText.getText().setSpan(new BoldSpan(), 5, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);    ///test

        ///（必选）RichEditorToolbar设置编辑器
        mRichEditorToolbar.setupEditText(mRichEditText);

        ///（必选）RichEditorToolbar设置Preview
        mRichEditorToolbar.setPreview(mTextViewPreview);

        ///（必选）RichEditorToolbar设置Html
        mRichEditorToolbar.setHtml(mEditTextHtml);

        ///（必选）mPlaceholderDrawable和mPlaceholderResourceId必须至少设置其中一个！如都设置则mPlaceholderDrawable优先
        mRichEditorToolbar.setPlaceholderDrawable(new ColorDrawable(Color.LTGRAY));
//        mRichEditorToolbar.setPlaceholderResourceId(R.drawable.ic_image_black_24dp);

        ///（可选，缺省为getExternalCacheDir()）RichEditorToolbar设置RichEditor中的ImageSpan存放图片文件的目录（必须非null、且存在、且可写入）
        ///注意：getExternalCacheDir()在API 30中可能会返回null！
        final File imageFilePath = getExternalCacheDir();   ///test
        mRichEditorToolbar.setImageFilePath(imageFilePath);

        ///（可选，必须大于1！否则Undo和Redo永远disable。缺省为无限）RichEditorToolbar设置HistorySize
//        mRichEditorToolbar.setHistorySize(2); ///test


        /* -------------- ///[startActivityForResult#Activity获取数据] -------------- */
        final Intent intent = getIntent();
        final String htmlTextString = intent.getStringExtra("html_text");
        if (!TextUtils.isEmpty(htmlTextString)) {
            final Spanned htmlTextSpanned = Html.fromHtml(htmlTextString);
            mRichEditText.setText(htmlTextSpanned);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ///[ClickImageSpanDialogBuilder#onActivityResult()]
        if (mRichEditorToolbar != null) {
            mRichEditorToolbar.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void btnClickSave(View view) {
        ///[startActivityForResult#setResult()返回数据]
        final Intent intent = new Intent();

        if (!TextUtils.isEmpty(mRichEditText.getText())) {
            final String htmlResult = Html.toHtml(mRichEditText.getText(), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            intent.putExtra("html_result", htmlResult);
        }

        setResult(RESULT_OK, intent);

        finish();
    }
}