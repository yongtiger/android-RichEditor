package cc.brainbook.android.richeditor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import cc.brainbook.android.richeditortoolbar.ClickableMovementMethod;
import cc.brainbook.android.richeditortoolbar.ImageSpanOnClickListener;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;

import static cc.brainbook.android.richeditor.EditorActivity.FILE_PROVIDER_AUTHORITIES_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;

public class PreviewEditorActivity extends AppCompatActivity {

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mTextView = findViewById(R.id.tv_preview);

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

        final Intent intent = getIntent();
        final String jsonString = intent.getStringExtra(KEY_TEXT);
        if (jsonString != null) {
            mTextView.setText(ToolbarHelper.fromJson(jsonString));
            mTextView.post(new Runnable() {
                @Override
                public void run() {
                    postSetText(PreviewEditorActivity.this, mTextView);
                }
            });
        }
    }


    ///[postSetText#执行postLoadSpans及后处理，否则ImageSpan/VideoSpan/AudioSpan不会显示！]
    private void postSetText(Context context, @NonNull final TextView textView) {
        ///[postSetText#显示ImageSpan/VideoSpan/AudioSpan]如果自定义，则使用ToolbarHelper.postLoadSpans()
        ToolbarHelper.postSetText(context, (Spannable) textView.getText(), new ImageSpanOnClickListener(FILE_PROVIDER_AUTHORITIES_SUFFIX),
                ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
                ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
                new ToolbarHelper.LegacyLoadImageCallback() {
                    @Override
                    public int getMaxWidth() {
                        return textView.getWidth() - textView.getTotalPaddingLeft() - textView.getTotalPaddingRight();
                    }
                });
    }

}