package cc.brainbook.android.richeditor;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cc.brainbook.android.richeditortoolbar.ClickableMovementMethod;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;

import static cc.brainbook.android.richeditor.EditorActivity.FILE_PROVIDER_AUTHORITIES_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_IMAGE_MAX_HEIGHT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_IMAGE_MAX_WIDTH;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.postSetText;

public class EditorPreviewActivity extends AppCompatActivity {
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_preview);

        mTextView = findViewById(R.id.tv_editor_preview);

        ///实现TextView超链接五种方式：https://blog.csdn.net/lyankj/article/details/51882335
        ///设置TextView可点击，比如响应URLSpan点击事件。
        //        mTextViewPreview.setMovementMethod(new ScrollingMovementMethod());  ///让TextView可以滚动显示完整内容
        ///注意：LinkMovementMethod继承了ScrollingMovementMethod，因此无需ScrollingMovementMethod
        //        mTextViewPreview.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setMovementMethod(ClickableMovementMethod.getInstance());   ///https://www.cnblogs.com/luction/p/3645210.html

        ///[FIX#点击 ClickableSpan 的文本之外的文本时，TextView 会消费该事件，而不会传递给父View]
        ///https://blog.csdn.net/zhuhai__yizhi/article/details/53760663
        mTextView.setFocusable(false);
        mTextView.setClickable(false);
        mTextView.setLongClickable(false);

        if (getIntent().getStringExtra(KEY_TEXT) != null) {
            mTextView.setText(ToolbarHelper.fromJson(getIntent().getStringExtra(KEY_TEXT)));
        }

        mTextView.post(new Runnable() {
            @Override
            public void run() {
                postSetText(EditorPreviewActivity.this, mTextView,
                        getPackageName() + FILE_PROVIDER_AUTHORITIES_SUFFIX,
                        DEFAULT_IMAGE_MAX_WIDTH, DEFAULT_IMAGE_MAX_HEIGHT);
            }
        });
    }

}