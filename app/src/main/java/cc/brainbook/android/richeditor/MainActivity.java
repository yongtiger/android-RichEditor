package cc.brainbook.android.richeditor;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import cc.brainbook.android.richeditortoolbar.EnhancedMovementMethod;
import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;
import cc.brainbook.android.richeditortoolbar.span.paragraph.LineDividerSpan;

public class MainActivity extends AppCompatActivity implements
        LineDividerSpan.DrawBackgroundCallback,
        Drawable.Callback {
    private static final int REQUEST_CODE_RICH_EDITOR = 101;

    private String mHtmlText;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.tv_text);
        mTextView.setClickable(true);
        mTextView.setMovementMethod(EnhancedMovementMethod.getInstance());   ///http://stackoverflow.com/a/23566268/569430
    }

    ///[startActivityForResult#onActivityResult()获得返回数据]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_RICH_EDITOR == requestCode) {
            if (RESULT_OK == resultCode) {
                if (data != null) {
                    mHtmlText = data.getStringExtra("html_result");
                    if (TextUtils.isEmpty(mHtmlText)) {
                        mTextView.setText(null);
                    } else {
                        final Spanned spanned = Html.fromHtml(mHtmlText);
                        mTextView.setText(spanned);


                        /* ----------------------------------------------------------------------- */
                        ///[postSetText#显示LineDividerSpan、ImageSpan/VideoSpan/AudioSpan]
                        final Spannable textSpanned = ((Spannable) mTextView.getText());
                        final Object[] spans = textSpanned.getSpans(0, textSpanned.length(), Object.class);
                        final List<Object> spanList = Arrays.asList(spans);
                        ///执行postLoadSpans及后处理
                        RichEditorToolbarHelper.postLoadSpans(this, textSpanned, -1, spanList,
                                new ColorDrawable(Color.LTGRAY), -1, this, this);
                    }
                }
            }
        }
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        c.drawLine(left, (top + bottom) / 2, right, (top + bottom) / 2, p);    ///画直线
    }

    ///[ImageSpan#Glide#GifDrawable]
    ///注意：TextView在实际使用中可能不由EditText产生并赋值，所以需要单独另行处理Glide#GifDrawable的Callback
    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        final Spannable spannable = ((Spannable) mTextView.getText());
        RichEditorToolbarHelper.setImageSpan(spannable, drawable);
    }


    @Override
    public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long l) {}

    @Override
    public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {}


    public void btnClickEdit(View view) {
        ///[startActivityForResult#启动Activity来获取数据]
        final Intent intent = new Intent(MainActivity.this, TextActivity.class);

        if (!TextUtils.isEmpty(mHtmlText)) {
            intent.putExtra("html_text", mHtmlText);
        }

        startActivityForResult(intent, REQUEST_CODE_RICH_EDITOR);
    }

}
