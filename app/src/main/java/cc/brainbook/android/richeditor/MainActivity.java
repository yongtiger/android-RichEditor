package cc.brainbook.android.richeditor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cc.brainbook.android.richeditortoolbar.EnhancedMovementMethod;
import cc.brainbook.android.richeditortoolbar.helper.Html;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_RICH_EDITOR = 101;
    private static final int RESULT_OK = -1;

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
                    mTextView.setText(TextUtils.isEmpty(mHtmlText)? null : Html.fromHtml(mHtmlText));
                    mTextView.setClickable(true);
                    mTextView.setMovementMethod(EnhancedMovementMethod.getInstance());
                }
            }
        }
    }

    public void btnClickEdit(View view) {
        ///[startActivityForResult#启动Activity来获取数据]
        final Intent intent = new Intent(MainActivity.this, TextActivity.class);

        if (!TextUtils.isEmpty(mHtmlText)) {
            intent.putExtra("html_text", mHtmlText);
        }

        startActivityForResult(intent, REQUEST_CODE_RICH_EDITOR);
    }
}
