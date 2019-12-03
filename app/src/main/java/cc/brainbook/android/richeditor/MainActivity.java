package cc.brainbook.android.richeditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import java.io.File;

import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;

public class MainActivity extends AppCompatActivity {
    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;
    private TextView mTextViewPreviewText;

    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }

        mRichEditText = findViewById(R.id.et_rich_edit_text);
        mRichEditorToolbar = findViewById(R.id.rich_editor_tool_bar);
        mRichEditorToolbar.setEditText(mRichEditText);

        ///Preview
        mTextViewPreviewText = (TextView) findViewById(R.id.tv_preview_text);

        ///实现TextView超链接五种方式：https://blog.csdn.net/lyankj/article/details/51882335
        ///设置TextView可点击，比如响应URLSpan点击事件。LinkMovementMethod继承了ScrollingMovementMethod，因此无需ScrollingMovementMethod
        mTextViewPreviewText.setMovementMethod(LinkMovementMethod.getInstance());
//        mTextViewPreviewText.setMovementMethod(new ScrollingMovementMethod());  ///让TextView可以滚动显示完整内容

        mRichEditorToolbar.setPreviewText(mTextViewPreviewText);

        ///RichEditor中的ImageSpan存放图片文件的目录（缺省为getExternalCacheDir()）
        final File imageFilePath = getExternalCacheDir();
        mRichEditorToolbar.setImageFilePath(imageFilePath);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ///[ImageSpanDialogBuilder#onActivityResult()]
        if (mRichEditorToolbar != null) {
            mRichEditorToolbar.onActivityResult(requestCode, resultCode, data);
        }
    }

}
