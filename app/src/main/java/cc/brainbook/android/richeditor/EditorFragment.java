package cc.brainbook.android.richeditor;

import static android.app.Activity.RESULT_OK;
import static cc.brainbook.android.richeditor.EditorActivity.FILE_PROVIDER_AUTHORITIES_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_RESULT;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.KEY_TEXT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_HEIGHT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_WIDTH;
import static cc.brainbook.android.richeditortoolbar.config.Config.IMAGE_COMPRESS;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.AUDIO_FILE_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.IMAGE_FILE_SUFFIX;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.VIDEO_FILE_SUFFIX;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Date;

import cc.brainbook.android.richeditortoolbar.ClickImageSpanDialogFragment;
import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;
import cc.brainbook.android.richeditortoolbar.util.DirUtil;
import cc.brainbook.android.richeditortoolbar.util.FileProviderUtil;

public class EditorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static EditorFragment newInstance(String param1, String param2) {
        final EditorFragment fragment = new EditorFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(ARG_PARAM1, param1);
        arguments.putString(ARG_PARAM2, param2);
        fragment.setArguments(arguments);    ///注意：fragment.setArguments(args)在翻屏时会自动保留参数！所以不使用构造来传递参数
        return fragment;
    }

    public EditorFragment() {
        super();
    }


    private RichEditText mRichEditText;
    private RichEditorToolbar mRichEditorToolbar;

    private final boolean enableAddItemWithCamera = false;    ///test: if enableAddItemWithCamera


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d("TAG", "onAttach()# ");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "onCreate()# ");
        super.onCreate(savedInstanceState);

        ///setRetainInstance已经废弃
//        ///只有调用了fragment的setRetainInstance(true)方法，并且因设备配置改变，托管Activity正在被销毁的条件下，fragment才会短暂的处于保留状态。
//        ///如果activity是因操作系统需要回收内存而被销毁，则所有的fragment也会随之销毁。
//        ///https://blog.csdn.net/gaugamela/article/details/56280384
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView()# ");
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_editor_flexbox_layout, container, false);
//        return inflater.inflate(R.layout.fragment_editor_linear_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onViewCreated()# ");
        super.onViewCreated(view, savedInstanceState);

        initView(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d("TAG", "onStart()# ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("TAG", "onResume()# ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("TAG", "onPause()# ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("TAG", "onStop()# ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("TAG", "onDestroyView()# ");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.d("TAG", "onDetach()# ");
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ///[ClickImageSpanDialogBuilder#onActivityResult()]
        if (mRichEditorToolbar != null) {
            mRichEditorToolbar.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void initView(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) {
                    return;
                }

                ///[startActivityForResult#setResult()返回数据]
                final Intent intent = new Intent();

                final String result = ToolbarHelper.toJson(mRichEditText.getText(), 0, mRichEditText.getText().length(), true);
                intent.putExtra(KEY_RESULT, result);

                getActivity().setResult(RESULT_OK, intent);

                getActivity().finish();
            }
        });

        initRichEditorToolbar(view, savedInstanceState);
    }

    private void initRichEditorToolbar(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getActivity() == null) {
            return;
        }

        mRichEditorToolbar = (RichEditorToolbar) view.findViewById(R.id.rich_editor_tool_bar);
        mRichEditText = (RichEditText) view.findViewById(R.id.et_rich_edit_text);

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
        if (getActivity().getIntent().getStringExtra(KEY_TEXT) != null) {
            mRichEditText.setText(ToolbarHelper.fromJson(getActivity().getIntent().getStringExtra(KEY_TEXT)));
        }


        /* --------------///[ImageSpan]-------------- */
        ///（如enableVideo/enableAudio/enableImage为true，则必选）设置存放图片/音频、视频文件的目录（必须非null、且存在、且可写入）
        final File imageFileDir = DirUtil.getPictureFilesDir(getActivity());
        final File videoFileDir = DirUtil.getVideoFilesDir(getActivity());
        final File audioFileDir = DirUtil.getAudioFilesDir(getActivity());

        mRichEditorToolbar.setImageSpanCallback(new ClickImageSpanDialogFragment.ImageSpanCallback() {
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
                final Intent intent = new Intent(getContext(), HtmlEditorActivity.class);
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
                final Intent intent = new Intent(getContext(), EditorPreviewActivity.class);
                intent.putExtra(KEY_TEXT, jsonString);

                startActivity(intent);
            }
        });

        ///[ClickImageSpanDialogBuilder#OnClickImageSpanDialogFragmentReadyListener]
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

                mRichEditorToolbar.setOnClickImageSpanDialogReadyListener(new RichEditorToolbar.OnClickImageSpanDialogFragmentReadyListener() {
                    @Override
                    public void onReady() {
                        ///首次打开ClickImageSpanDialog后自动启动Camera
                        mRichEditorToolbar.setOnClickImageSpanDialogReadyListener(null);

                        if (mRichEditorToolbar.getClickImageSpanDialogFragment() != null
                                && mRichEditorToolbar.getClickImageSpanDialogFragment().getButtonPickFromCamera() != null) {
                            mRichEditorToolbar.getClickImageSpanDialogFragment().getButtonPickFromCamera().performClick();
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

}
