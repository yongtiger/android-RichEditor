package cc.brainbook.android.richeditor;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import cc.brainbook.android.richeditortoolbar.ClickableMovementMethod;
import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;
import cc.brainbook.android.richeditortoolbar.interfaces.Clickable;
import cc.brainbook.android.richeditortoolbar.span.block.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.block.VideoSpan;
import cc.brainbook.android.richeditortoolbar.span.paragraph.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.util.UriUtil;

import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.PROVIDER_AUTHORITIES;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity implements
        Drawable.Callback,
        CustomImageSpan.OnClickListener,
        LineDividerSpan.DrawBackgroundCallback {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_RICH_EDITOR = 100;

    ///[权限申请]
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private String mHtmlText = "<hr>";  ///设置初始文本
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///[权限申请]当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            final int permission0 = ContextCompat.checkSelfPermission(this, permissions[0]);
            final int permission1 = ContextCompat.checkSelfPermission(this, permissions[1]);
            // 权限是否已经 授权 GRANTED---授权  DENIED---拒绝
            if (permission0 != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
            }
        }

        mTextView = findViewById(R.id.tv_text);

        ///实现TextView超链接五种方式：https://blog.csdn.net/lyankj/article/details/51882335
        ///设置TextView可点击，比如响应URLSpan点击事件。
//        mTextView.setMovementMethod(new ScrollingMovementMethod());  ///让TextView可以滚动显示完整内容
        ///注意：LinkMovementMethod继承了ScrollingMovementMethod，因此无需ScrollingMovementMethod
//        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setMovementMethod(ClickableMovementMethod.getInstance());   ///https://www.cnblogs.com/luction/p/3645210.html

        ///[FIX#点击 ClickableSpan 的文本之外的文本时，TextView 会消费该事件，而不会传递给父View]
        ///https://blog.csdn.net/zhuhai__yizhi/article/details/53760663
        mTextView.setFocusable(false);
        mTextView.setClickable(false);
        mTextView.setLongClickable(false);
        ///test
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll_linear_layout);
        ll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int i = 0;
            }
        });

        ///设置初始文本
        final Spanned spanned = Html.fromHtml(mHtmlText);
        mTextView.setText(spanned);
        ///[postSetText#显示LineDividerSpan、ImageSpan/VideoSpan/AudioSpan]
        final Spannable textSpannable = ((Spannable) mTextView.getText());
        postSetText(textSpannable);


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

                        ///[postSetText#显示LineDividerSpan、ImageSpan/VideoSpan/AudioSpan]
                        final Spannable textSpanned = ((Spannable) mTextView.getText());
                        postSetText(textSpanned);
                    }
                }
            }
        }
    }

    ///[Drawable.Callback#ImageSpan#Glide#GifDrawable]
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

    ///[CustomImageSpan.OnClickListener]TextView点击事件：URL点击；图片点击显示全图；video/audio点击开始播放
    @Override
    public void onClick(View widget, Clickable clickable, Drawable drawable, String uriString, String source) {
        final Context context = widget.getContext();

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        final String mediaType = clickable instanceof AudioSpan ? "audio/*" : clickable instanceof VideoSpan ? "video/*" : "image/*";
        final Uri mediaUri = UriUtil.parseToUri(this, clickable instanceof AudioSpan || clickable instanceof VideoSpan ? uriString : source,
                getPackageName() + PROVIDER_AUTHORITIES);

        ///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(mediaUri, mediaType);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Activity was not found for intent, " + intent.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    ///[LineDividerSpan.DrawBackgroundCallback]
    @Override
    public void drawBackground(@NonNull Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        c.drawLine(left, (top + bottom) / 2, right, (top + bottom) / 2, p);    ///画直线
    }


    ///[postSetText#显示LineDividerSpan、ImageSpan/VideoSpan/AudioSpan]
    public void postSetText(@NonNull Spannable textSpannable) {
        final Object[] spans = textSpannable.getSpans(0, textSpannable.length(), Object.class);
        final List<Object> spanList = Arrays.asList(spans);
        ///执行postLoadSpans及后处理
        RichEditorToolbarHelper.postLoadSpans(this, textSpannable, spanList, null, -1,
                new ColorDrawable(Color.LTGRAY), -1,this,  this, this);
    }

    public void btnClickEdit(View view) {
        ///[startActivityForResult#启动Activity来获取数据]
        final Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.putExtra("html_text", mHtmlText);
        startActivityForResult(intent, REQUEST_CODE_RICH_EDITOR);
    }

}
