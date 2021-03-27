package cc.brainbook.android.richeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import cc.brainbook.android.richeditortoolbar.ClickableMovementMethod;
import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;

import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_RESULT;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.REQUEST_CODE_HTML_EDITOR;

public class EditorActivity extends AppCompatActivity {
    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;
    private TextView mTextViewPreview;
    private String mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mRichEditorToolbar = (RichEditorToolbar) findViewById(R.id.rich_editor_tool_bar);
        mRichEditText = (RichEditText) findViewById(R.id.et_rich_edit_text);
        ///（必选）RichEditorToolbar设置编辑器
        mRichEditorToolbar.setRichEditText(mRichEditText);

        ///（可选）设置初始文本
        final Intent intent = getIntent();
        final String text = intent.getStringExtra(KEY_TEXT);
        if (!TextUtils.isEmpty(text)) {
//            mRichEditText.setText(Html.fromHtml(text));
            mRichEditText.setText(ToolbarHelper.fromJson(text));
        }


        /* --------------///[ImageSpan]-------------- *//////////////////////////////
        ///（如enableVideo/enableAudio/enableImage为true，则必选）设置存放图片/音频、视频文件的目录（必须非null、且存在、且可写入）
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

        ///（可选）mPlaceholderDrawable和mPlaceholderResourceId可都不设置、采用缺省，如都设置则mPlaceholderDrawable优先
//        mRichEditorToolbar.setPlaceholderDrawable(new ColorDrawable(Color.LTGRAY));
//        mRichEditorToolbar.setPlaceholderResourceId(R.drawable.placeholder);


//        ///（可选）设置LineDividerSpan.DrawBackgroundCallback
//        mRichEditorToolbar.setDrawBackgroundCallback(new LineDividerSpan.DrawBackgroundCallback() {
//            @Override
//            public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
//                c.drawLine(left, (top + bottom) * 0.5F, right, (top + bottom) * 0.5F, p);    ///画直线
//            }
//        });

        ///（可选，缺省为TO_HTML_PARAGRAPH_LINES_CONSECUTIVE）
//        mRichEditorToolbar.setHtmlOption(Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);

        ///（可选）设置HtmlCallback
        mRichEditorToolbar.setHtmlCallback(new RichEditorToolbar.HtmlCallback() {
            @Override
            public void handleHtml(String htmlString) {
                final Intent intent = new Intent(EditorActivity.this, HtmlEditorActivity.class);
                intent.putExtra(KEY_TEXT, htmlString);

                startActivityForResult(intent, REQUEST_CODE_HTML_EDITOR);
            }
        });

        ///（可选，必须大于1！否则Undo和Redo永远disable。缺省为无限）RichEditorToolbar设置HistorySize
//        mRichEditorToolbar.setHistorySize(2);

        ///（可选）设置SaveCallback
        mRichEditorToolbar.setSaveCallback(new RichEditorToolbar.SaveCallback() {
            @Override
            public void save(String result) {
                mResult = result;
            }
        });

        ///（可选）设置Preview////////////////////////
        mTextViewPreview = (TextView) findViewById(R.id.tv_preview);
        ///实现TextView超链接五种方式：https://blog.csdn.net/lyankj/article/details/51882335
        ///设置TextView可点击，比如响应URLSpan点击事件。
//        mTextViewPreview.setMovementMethod(new ScrollingMovementMethod());  ///让TextView可以滚动显示完整内容
        ///注意：LinkMovementMethod继承了ScrollingMovementMethod，因此无需ScrollingMovementMethod
//        mTextViewPreview.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewPreview.setMovementMethod(ClickableMovementMethod.getInstance());   ///https://www.cnblogs.com/luction/p/3645210.html
        mRichEditorToolbar.setPreview(mTextViewPreview);

        ///（必选）初始化
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

//        mHtmlResult = Html.toHtml(mRichEditText.getText(), mRichEditorToolbar.getHtmlOption());
        mResult = ToolbarHelper.toJson(mRichEditText.getText(), 0, mRichEditText.getText().length(), true);
        intent.putExtra(KEY_RESULT, mResult);

        setResult(RESULT_OK, intent);

        finish();
    }

}