package cc.brainbook.android.richeditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.brainbook.android.richeditortoolbar.ClickableMovementMethod;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;

import static cc.brainbook.android.richeditor.EditorActivity.FILE_PROVIDER_AUTHORITIES_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_RESULT;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_HEIGHT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_WIDTH;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.postSetText;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_RICH_EDITOR = 100;

    ///[权限申请]
    private final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    ///设置初始文本
    private String mJsonText = "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}";

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

        mTextView.setText(ToolbarHelper.fromJson(mJsonText));

        ///注意：postSetText()不必放在mTextView.post()中！
        postSetText(MainActivity.this, mTextView,
                getPackageName() + FILE_PROVIDER_AUTHORITIES_SUFFIX,
                DEFAULT_MAX_IMAGE_WIDTH, DEFAULT_MAX_IMAGE_HEIGHT,
                new ToolbarHelper.LoadImageCallback() {
                    @Override
                    public void onResourceReady(CustomImageSpan span) {
                        //////??????test
                        ///[FIX#OBJECT_REPLACEMENT_TEXT太短时图片可能覆盖文字！]
                        mTextView.postInvalidate(); ///在非UI线程中，可直接使用postInvalidate方法
                    }
                });
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                //////??????test
                ///[FIX#OBJECT_REPLACEMENT_TEXT太短时图片可能覆盖文字！]
                ///调用invalidate方法只会执行onDraw方法；调用requestLayout方法只会执行onMeasure方法和onLayout方法，并不会执行onDraw方法
                mTextView.invalidate();
//                mTextView.requestLayout();
            }
        });

        ///test
        LinearLayout ll = (LinearLayout)findViewById(R.id.ll_linear_layout);
        ll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("TAG", "LinearLayout click!");
            }
        });
    }

    ///[startActivityForResult#onActivityResult()获得返回数据]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE_RICH_EDITOR == requestCode) {
            if (RESULT_OK == resultCode) {
                if (data != null) {
                    mJsonText = data.getStringExtra(KEY_RESULT);
                    if (TextUtils.isEmpty(mJsonText)) {
                        mTextView.setText(null);
                    } else {
                        ///设置文本
                        mTextView.setText(ToolbarHelper.fromJson(mJsonText));
                        postSetText(MainActivity.this, mTextView,
                                getPackageName() + FILE_PROVIDER_AUTHORITIES_SUFFIX,
                                DEFAULT_MAX_IMAGE_WIDTH, DEFAULT_MAX_IMAGE_HEIGHT,
                                new ToolbarHelper.LoadImageCallback() {
                                    @Override
                                    public void onResourceReady(CustomImageSpan span) {
                                        ///[FIX#OBJECT_REPLACEMENT_TEXT太短时图片可能覆盖文字！]
                                        mTextView.invalidate();
                                    }
                                });
                    }
                }
            }
        }
    }

    public void btnClickEdit(View view) {
        ///[startActivityForResult#启动Activity来获取数据]
        final Intent intent = new Intent(MainActivity.this, EditorActivity.class);
//        intent.putExtra(KEY_TEXT, mHtmlText);
        intent.putExtra(KEY_TEXT, mJsonText);
        startActivityForResult(intent, REQUEST_CODE_RICH_EDITOR);
    }

}
