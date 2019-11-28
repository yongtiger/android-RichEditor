package cc.brainbook.android.richeditor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;

public class MainActivity extends AppCompatActivity {
    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;

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
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }

        mRichEditText = findViewById(R.id.edit_text);
        mRichEditorToolbar = findViewById(R.id.rich_editor_tool_bar);
        mRichEditorToolbar.setEditText(mRichEditText);

        ///RichEditor中的ImageSpan存放图片文件的目录（缺省为getExternalCacheDir()）
        final File imageFilePath = getExternalCacheDir();
        mRichEditorToolbar.setImageFilePath(imageFilePath);

//        mRichEditText.setText("aaaaa\naaaaaaaa\naaaaaaaa\naaaaaaaa\naaa");
//
//        Drawable drawable = getResources().getDrawable(R.drawable.a);
//        drawable.setBounds(0, 0, 150, 150); //必须设置图片大小，否则不显示
//        ImageSpan newSpan = new ImageSpan(drawable);

//        ImageSpan newSpan = new ImageSpan(this, R.drawable.a);///ok

//        mRichEditText.getText().setSpan(newSpan, 1, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//
////        findViewById(R.id.iv_test).setBackground(imageDrawable);

        ///test
//        mRichEditText.setText("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
//        for (int i = 0; i < 20; i++) {
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 6, 8, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 2, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 10, 15, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 11, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 26, 28, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 22, 25, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 20, 23, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 20, 26, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 30, 35, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 31, 33, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 42, 45, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 40, 43, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 40, 46, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 40, 45, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 51, 53, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 56, 58, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 52, 55, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 50, 53, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 60, 66, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 60, 65, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            mRichEditText.getText().setSpan(new StrikethroughSpan(), 61, 63, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        ///https://stackoverflow.com/questions/16558948/how-to-use-textview-getlayout-it-returns-null
//        SpanUtil.getThisLineStart(mRichEditText, 1);
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
