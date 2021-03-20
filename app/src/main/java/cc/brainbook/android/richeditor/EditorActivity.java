package cc.brainbook.android.richeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import cc.brainbook.android.richeditortoolbar.ClickableMovementMethod;
import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.span.paragraph.LineDividerSpan;

import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.REQUEST_CODE_HTML_EDITOR;

public class EditorActivity extends AppCompatActivity {
    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;
    private TextView mTextViewPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mRichEditorToolbar = (RichEditorToolbar) findViewById(R.id.rich_editor_tool_bar);
        mRichEditText = (RichEditText) findViewById(R.id.et_rich_edit_text);

        ///[Preview]
        mTextViewPreview = (TextView) findViewById(R.id.tv_preview);
        ///实现TextView超链接五种方式：https://blog.csdn.net/lyankj/article/details/51882335
        ///设置TextView可点击，比如响应URLSpan点击事件。
//        mTextViewPreview.setMovementMethod(new ScrollingMovementMethod());  ///让TextView可以滚动显示完整内容
        ///注意：LinkMovementMethod继承了ScrollingMovementMethod，因此无需ScrollingMovementMethod
//        mTextViewPreview.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewPreview.setMovementMethod(ClickableMovementMethod.getInstance());   ///https://www.cnblogs.com/luction/p/3645210.html


        /* -------------- ///[startActivityForResult#Activity获取数据] -------------- */
        final Intent intent = getIntent();
        final String htmlTextString = intent.getStringExtra("html_text");//////////////////
        if (!TextUtils.isEmpty(htmlTextString)) {
            final Spanned htmlTextSpanned = Html.fromHtml(htmlTextString);
            mRichEditText.setText(htmlTextSpanned);
        }


        /* -------------- ///设置 -------------- */
//        ///（可选）设置编辑器初始文本
//        mRichEditText.setText("\n\naaabbbcccdddeeefffggghhhiiijjjkkklllmmmnnnooopppqqqrrrssstttuuuvvvwwwxxxyyyzzz\n1234567890"); ///test
//        mRichEditText.getText().setSpan(new BoldSpan(), 2, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);    ///test
//        mRichEditText.getText().setSpan(new BoldSpan(), 5, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);    ///test

        ///（必选）RichEditorToolbar设置编辑器
        mRichEditorToolbar.setRichEditText(mRichEditText);

        ///（必选）RichEditorToolbar设置Preview
        mRichEditorToolbar.setPreview(mTextViewPreview);

        ///（必选）RichEditorToolbar设置LineDividerSpan.DrawBackgroundCallback
        mRichEditorToolbar.setDrawBackgroundCallback(new LineDividerSpan.DrawBackgroundCallback() {
            @Override
            public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
                c.drawLine(left, (top + bottom) / 2, right, (top + bottom) / 2, p);    ///画直线//////////////////
            }
        });

        ///（必选）mPlaceholderDrawable和mPlaceholderResourceId必须至少设置其中一个！如都设置则mPlaceholderDrawable优先
        mRichEditorToolbar.setPlaceholderDrawable(new ColorDrawable(Color.LTGRAY));
//        mRichEditorToolbar.setPlaceholderResourceId(R.drawable.ic_image_black_24dp);

        ///设置存放ImageSpan图片的文件目录//////////////////
        ///（必选）RichEditorToolbar设置RichEditor中的ImageSpan存放图片文件的目录（必须非null、且存在、且可写入）
        File imageFileDir, videoFileDir, audioFileDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            ///外部存储可用
            ///注意：Android 10 Q (API 29) 访问公共外部存储必须MediaStore API！而11 R (API 30)则恢复29之前，即照常访问
//            imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            imageFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            videoFileDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            audioFileDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        } else {
            ///外部存储不可用
            imageFileDir = new File(getFilesDir(), Environment.DIRECTORY_PICTURES);
            videoFileDir = new File(getFilesDir(), Environment.DIRECTORY_MOVIES);
            audioFileDir = new File(getFilesDir(), Environment.DIRECTORY_MUSIC);
        }
        mRichEditorToolbar.setImageFileDir(imageFileDir);
        mRichEditorToolbar.setVideoFileDir(videoFileDir);
        mRichEditorToolbar.setAudioFileDir(audioFileDir);

        ///（可选，缺省为richeditortoolbar\src\main\res\layout\layout_toolbar.xml）
        mRichEditorToolbar.setLayoutRes(R.layout.editor_toolbar);

        ///（可选，缺省为TO_HTML_PARAGRAPH_LINES_CONSECUTIVE）
//        mRichEditorToolbar.setHtmlOption(Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);

        ///（可选，必须大于1！否则Undo和Redo永远disable。缺省为无限）RichEditorToolbar设置HistorySize
//        mRichEditorToolbar.setHistorySize(2); ///test

        ///（可选）设置HtmlEditorCallback
        mRichEditorToolbar.setHtmlEditorCallback(new RichEditorToolbar.HtmlEditorCallback() {
            @Override
            public void startHtmlEditorActivity(String htmlString) {
                ///[HtmlEditor#启动HtmlEditorActivity]
                final Intent intent = new Intent(EditorActivity.this, HtmlEditorActivity.class);
                intent.putExtra("html_text", htmlString);//////////////////

                startActivityForResult(intent, REQUEST_CODE_HTML_EDITOR);
            }
        });

        ///（必选）初始化RichEditorToolbar
        mRichEditorToolbar.init();
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

        final String htmlResult = Html.toHtml(mRichEditText.getText(), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        intent.putExtra("html_result", htmlResult);//////////////////

        setResult(RESULT_OK, intent);

        finish();
    }

}