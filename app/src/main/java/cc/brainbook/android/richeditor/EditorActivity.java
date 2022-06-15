package cc.brainbook.android.richeditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.Date;

import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.builder.ClickImageSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;
import cc.brainbook.android.richeditortoolbar.util.DirUtil;
import cc.brainbook.android.richeditortoolbar.util.FileProviderUtil;

import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_RESULT;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;
import static cc.brainbook.android.richeditortoolbar.config.Config.IMAGE_COMPRESS;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_HEIGHT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_WIDTH;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.AUDIO_FILE_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.IMAGE_FILE_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.VIDEO_FILE_SUFFIX;

public class EditorActivity extends AppCompatActivity {
    public static final String FILE_PROVIDER_AUTHORITIES_SUFFIX = ".file.path.share";

    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;

    private final boolean enableAddItemWithCamera = false;    ///test: if enableAddItemWithCamera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "onCreate: =========================");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mRichEditorToolbar = (RichEditorToolbar) findViewById(R.id.rich_editor_tool_bar);
        mRichEditText = (RichEditText) findViewById(R.id.et_rich_edit_text);

        ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
        ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
        mRichEditText.post(new Runnable() {
            @Override
            public void run() {
                ///[postSetText#执行postLoadSpans及后处理，否则ImageSpan/VideoSpan/AudioSpan不会显示！]
                mRichEditorToolbar.postSetText();
            }
        });

        ///（必选）RichEditorToolbar设置编辑器
        mRichEditorToolbar.setRichEditText(mRichEditText);

        ///（可选）设置初始文本
        if (getIntent().getStringExtra(KEY_TEXT) != null) {
            mRichEditText.setText(ToolbarHelper.fromJson(getIntent().getStringExtra(KEY_TEXT)));
        }


        /* --------------///[ImageSpan]-------------- */
        ///（如enableVideo/enableAudio/enableImage为true，则必选）设置存放图片/音频、视频文件的目录（必须非null、且存在、且可写入）
        final File imageFileDir = DirUtil.getPictureFilesDir(this);
        final File videoFileDir = DirUtil.getVideoFilesDir(this);
        final File audioFileDir = DirUtil.getAudioFilesDir(this);

        mRichEditorToolbar.setImageSpanCallback(new ClickImageSpanDialogBuilder.ImageSpanCallback() {
            @Override
            public Pair<Uri, File> generateActionSourceAndFile(Context context, String action, String src) {
                final Uri source = FileProviderUtil.parseToUri(context, src, context.getPackageName() + FILE_PROVIDER_AUTHORITIES_SUFFIX);
                final File file = new File(imageFileDir, action + "_" + Util.getDateFormat(new Date()) + IMAGE_FILE_SUFFIX);
                return new Pair<>(source, file);
            }

            @Override
            public Pair<Uri, File> generateMediaTypeSourceAndFile(Context context, int mediaType) {
                final File file = new File(mediaType == 0 ? imageFileDir : mediaType == 1 ? videoFileDir : audioFileDir,
				Util.getDateFormat(new Date()) + (mediaType == 0 ? IMAGE_FILE_SUFFIX : mediaType == 1 ? VIDEO_FILE_SUFFIX : AUDIO_FILE_SUFFIX));
		        final Uri source = FileProviderUtil.getFileProviderUriFromFile(context, file, context.getPackageName() + FILE_PROVIDER_AUTHORITIES_SUFFIX);
                return new Pair<>(source, file);
            }

            @Override
            public File generateVideoCoverFile(Context context, Uri uri) {
                final String videoCoverFileName = Util.getDateFormat(new Date()) + "_cover" + IMAGE_FILE_SUFFIX;
                final File videoCoverFile = new File(imageFileDir, videoCoverFileName);
                ///生成视频的第一帧图片
                Util.generateVideoCover(context, uri, videoCoverFile, Bitmap.CompressFormat.JPEG, IMAGE_COMPRESS);

                return videoCoverFile;
            }
        });

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

                startActivityForResult(intent, mRichEditorToolbar.getRequestCodeHtmlEditor());
            }
        });

        ///（可选，必须大于1！否则Undo和Redo永远disable。缺省为无限）RichEditorToolbar设置HistorySize
//        mRichEditorToolbar.setHistorySize(2);

        ///（可选）设置SaveCallback
        mRichEditorToolbar.setSaveCallback(new RichEditorToolbar.SaveCallback() {
            @Override
            public void save(String jsonString) {
                /// do save ...
            }
        });

        ///（如enablePreview为true，则必选）设置PreviewCallback
        mRichEditorToolbar.setPreviewCallback(new RichEditorToolbar.PreviewCallback() {
            @Override
            public void handlePreview(String jsonString) {
                final Intent intent = new Intent(EditorActivity.this, EditorPreviewActivity.class);
                intent.putExtra(KEY_TEXT, jsonString);

                startActivity(intent);
            }
        });

        ///[ClickImageSpanDialogBuilder#OnClickImageSpanDialogReadyListener]
        mRichEditorToolbar.setOnReadyListener(new RichEditorToolbar.OnReadyListener() {
            @Override
            public void onReady() {
                if (!enableAddItemWithCamera || savedInstanceState != null) {
                    return;
                }

                ///初始化mRichEditorToolbar时自动点击ImageViewImage
                if (mRichEditorToolbar.getImageViewImage() != null) {
                    mRichEditorToolbar.getImageViewImage().performClick();
                }

                mRichEditorToolbar.setOnClickImageSpanDialogReadyListener(new RichEditorToolbar.OnClickImageSpanDialogReadyListener() {
                    @Override
                    public void onReady() {
                        ///首次打开ClickImageSpanDialog后自动启动Camera
                        mRichEditorToolbar.setOnClickImageSpanDialogReadyListener(null);

                        if (mRichEditorToolbar.getClickImageSpanDialogBuilder() != null
                                && mRichEditorToolbar.getClickImageSpanDialogBuilder().getButtonPickFromCamera() != null) {
                            mRichEditorToolbar.getClickImageSpanDialogBuilder().getButtonPickFromCamera().performClick();
                        }
                    }
                });
            }
        });

        ///（可选）设置ImageMaxWidth/Height
        mRichEditorToolbar.setImageMaxWidth(DEFAULT_MAX_IMAGE_WIDTH);
        mRichEditorToolbar.setImageMaxHeight(DEFAULT_MAX_IMAGE_HEIGHT);

        ///（必选）初始化
        mRichEditorToolbar.init();
    }

    @Override
    protected void onStart() {
        Log.d("TAG", "onStart: =========================");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "onResume: =========================");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("TAG", "onPause: =========================");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("TAG", "onStop: =========================");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG", "onDestroy: =========================");
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ///[ClickImageSpanDialogBuilder#onActivityResult()]
        if (mRichEditorToolbar != null) {
            mRichEditorToolbar.onActivityResult(requestCode, resultCode, data);
        }
    }

    ///当设置了android:configChanges="smallestScreenSize|screenLayout|orientation|keyboardHidden|screenSize"不会重启Activity，将调用onConfigurationChanged()
    ///但必须在onConfigurationChanged(Configuration newConfig)中根据新的newConfig来配置新布局中各个控件！
    ///Note: To use android:configChanges attribute is also not recommended by Android!
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //////??????[含有RichEditor的Activity#重启时如有对话框则不再显示，造成无法接收拍照等，暂时禁止重启！以后考虑优化]
//        recreate();
    }


    public void btnClickSave(View view) {
        ///[startActivityForResult#setResult()返回数据]
        final Intent intent = new Intent();

        final String result = ToolbarHelper.toJson(mRichEditText.getText(), 0, mRichEditText.getText().length(), true);
        intent.putExtra(KEY_RESULT, result);

        setResult(RESULT_OK, intent);

        finish();
    }

}