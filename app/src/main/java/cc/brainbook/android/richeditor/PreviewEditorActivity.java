package cc.brainbook.android.richeditor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cc.brainbook.android.richeditortoolbar.ClickableMovementMethod;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;

import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;

public class PreviewEditorActivity extends AppCompatActivity {

    private TextView mTextViewPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_editor);

        mTextViewPreview = findViewById(R.id.tv_preview);

        ///实现TextView超链接五种方式：https://blog.csdn.net/lyankj/article/details/51882335
        ///设置TextView可点击，比如响应URLSpan点击事件。
        //        mTextViewPreview.setMovementMethod(new ScrollingMovementMethod());  ///让TextView可以滚动显示完整内容
        ///注意：LinkMovementMethod继承了ScrollingMovementMethod，因此无需ScrollingMovementMethod
        //        mTextViewPreview.setMovementMethod(LinkMovementMethod.getInstance());
        mTextViewPreview.setMovementMethod(ClickableMovementMethod.getInstance());   ///https://www.cnblogs.com/luction/p/3645210.html

        ///[FIX#点击 ClickableSpan 的文本之外的文本时，TextView 会消费该事件，而不会传递给父View]
        ///https://blog.csdn.net/zhuhai__yizhi/article/details/53760663
        mTextViewPreview.setFocusable(false);
        mTextViewPreview.setClickable(false);
        mTextViewPreview.setLongClickable(false);

        final Intent intent = getIntent();
        final String jsonString = intent.getStringExtra(KEY_TEXT);
        if (jsonString != null) {
            mTextViewPreview.setText(ToolbarHelper.fromJson(jsonString));

            ///[postSetText#显示ImageSpan/VideoSpan/AudioSpan]
            ToolbarHelper.postSetText(this, (Spannable) mTextViewPreview.getText());
        }
    }

}