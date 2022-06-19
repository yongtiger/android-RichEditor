package cc.brainbook.android.richeditortoolbar.fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static cc.brainbook.android.richeditortoolbar.config.Config.IMAGE_MAX_DISPLAY_DIGITS;
import static cc.brainbook.android.richeditortoolbar.config.Config.IMAGE_ZOOM_FACTOR;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.AUDIO_TYPE;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.IMAGE_TYPE;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.VIDEO_TYPE;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.adjustHeight;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.adjustWidth;
import static cc.brainbook.android.richeditortoolbar.util.StringUtil.parseInt;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.util.Pair;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.slider.Slider;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import cc.brainbook.android.richeditortoolbar.R;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;
import cc.brainbook.android.richeditortoolbar.util.UriUtil;
import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleParams;

public class ClickImageSpanDialogFragment extends DialogFragment {

    public interface ImageSpanCallback {
        Pair<Uri, File> generateActionSourceAndFile(Context context, String action, String src);
        Pair<Uri, File> generateMediaTypeSourceAndFile(Context context, int mediaType);
        File generateVideoCoverFile(Context context, Uri uri);
    }
    private ImageSpanCallback mImageSpanCallback;
    public void setImageSpanCallback(ImageSpanCallback imageSpanCallback) {
        mImageSpanCallback = imageSpanCallback;
    }

    ///Button
    public interface OnFinishListener {
        void onClick(DialogInterface dialog, String uri, String src, int width, int height, int align);
    }
    private OnFinishListener mOnFinishListener;
    public void setOnFinishListener(OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    private DialogInterface.OnClickListener mOnClearListener;
    public void setOnClearListener(DialogInterface.OnClickListener onClearListener) {
        mOnClearListener = onClearListener;
    }

    private DialogInterface.OnCancelListener mOnCancelListener;
    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
    }

    ///Dialog
    private DialogInterface.OnDismissListener mOnDismissListener;
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    private DialogInterface.OnShowListener mOnShowListener;
    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
    }

    public void doFinishAction() {
        if (mOnFinishListener == null) {
            return;
        }

        final String uri = mEditTextUri.getText() == null ? "" : mEditTextUri.getText().toString();	///[FIX]
        final String src = mEditTextSrc.getText() == null ? "" : mEditTextSrc.getText().toString();	///[FIX]

        int width = parseInt(mEditTextDisplayWidth.getText().toString());
        if (width < 0) {
            width = 0;
        }

        int height = parseInt(mEditTextDisplayHeight.getText().toString());
        if (height < 0) {
            height = 0;
        }

        mOnFinishListener.onClick(getDialog(),
                uri,
                src,
                width,
                height,
                mVerticalAlignment);
    }
    public void doClearAction() {
        deleteOldUriFile();
        deleteOldSrcFile();

        if (mOnClearListener != null) {
            mOnClearListener.onClick(getDialog(), -1);
        }
    }
    public void doCancelAction() {
        deleteOldUriFile();
        deleteOldSrcFile();

        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(getDialog());
        }
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "key_media_type";
    private static final String ARG_PARAM2 = "key_uri";
    private static final String ARG_PARAM3 = "key_src";
    private static final String ARG_PARAM4 = "key_width";
    private static final String ARG_PARAM5 = "key_height";
    private static final String ARG_PARAM6 = "key_align";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mediaType Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static ClickImageSpanDialogFragment newInstance(int mediaType, String uri, String src, int width, int height, int align) {
        final ClickImageSpanDialogFragment fragment = new ClickImageSpanDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(ARG_PARAM1, mediaType);
        arguments.putString(ARG_PARAM2, uri);
        arguments.putString(ARG_PARAM3, src);
        arguments.putInt(ARG_PARAM4, width);
        arguments.putInt(ARG_PARAM5, height);
        arguments.putInt(ARG_PARAM6, align);
        fragment.setArguments(arguments);    ///注意：fragment.setArguments(args)在翻屏时会自动保留参数！所以不使用构造来传递参数
        return fragment;
    }

    public ClickImageSpanDialogFragment() {
        super();
    }


    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2;
    public static final int DEFAULT_ALIGN = ALIGN_BOTTOM;

    private static final int REQUEST_CODE_PICK_FROM_VIDEO_MEDIA = 1;
    private static final int REQUEST_CODE_PICK_FROM_AUDIO_MEDIA = 2;
    private static final int REQUEST_CODE_PICK_FROM_VIDEO_RECORDER = 3;
    private static final int REQUEST_CODE_PICK_FROM_AUDIO_RECORDER = 4;
    private static final int REQUEST_CODE_PICK_FROM_GALLERY = 5;
    private static final int REQUEST_CODE_PICK_FROM_CAMERA = 6;
    private static final int REQUEST_CODE_DOODLE = 10;

    private static final String ACTION_CROP = "crop";
    private static final String ACTION_DOODLE = "doodle";


    private RichEditorToolbar mRichEditorToolbar;
    public RichEditorToolbar getRichEditorToolbar() {
        return mRichEditorToolbar;
    }
    public void setRichEditorToolbar(RichEditorToolbar richEditorToolbar) {
        mRichEditorToolbar = richEditorToolbar;
    }

    private int mMediaType;	///0: image; 1: video; 2: audio
    private String mUri;
    private String mSrc;
    private int mWidth;
    private int mHeight;
    private int mAlign;

    private String mInitialUri;	///初始化时的uri
    private String mInitialSrc;	///初始化时的src

    private int mVerticalAlignment;

    private int mImageWidth;
    private int mImageHeight;

    ///[FIX#保存原始图片，避免反复缩放过程中使用模糊图片]
    private Drawable mOriginalDrawable;

    private Button mButtonPickFromMedia;
    private Button mButtonPickFromRecorder;
    private EditText mEditTextUri;

    private Button mButtonPickFromGallery;
    private Button mButtonPickFromCamera;
    public Button getButtonPickFromCamera() {
        return mButtonPickFromCamera;
    }

    private EditText mEditTextSrc;

    private ImageView mImageViewPreview;

    private EditText mEditTextDisplayWidth;
    private EditText mEditTextDisplayHeight;
    private boolean enableEditTextDisplayChangedListener;
    private CheckBox mCheckBoxDisplayConstrainWidth;
    private CheckBox mCheckBoxDisplayConstrainHeight;
    private ImageButton mImageButtonDisplayRestore;
    private ImageButton mImageButtonZoomOut;
    private ImageButton mImageButtonZoomIn;
    private Slider mSliderDisplayWidth;
    private Slider mSliderDisplayHeight;

    private Button mButtonCrop;
    private Button mButtonDoodle;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButtonAlignBottom;
    private RadioButton mRadioButtonAlignBaseline;
    private RadioButton mRadioButtonAlignCenter;

    private Group mGroupMedia;
    private Group mGroupGallery;
    private NestedScrollView mNestedScrollView;
    private ImageButton mImageButtonPrev;
    private ImageButton mImageButtonNext;

    private int mCurrentPageNumber;
    private void showPage() {
        hideAll();
        if (mCurrentPageNumber == 1) {
            mNestedScrollView.setVisibility(View.VISIBLE);
            mImageButtonPrev.setVisibility(View.VISIBLE);
            mImageButtonNext.setVisibility(View.VISIBLE);
        } else if (mCurrentPageNumber == 2) {
            mRadioGroup.setVisibility(View.VISIBLE);
            mImageButtonPrev.setVisibility(View.VISIBLE);
        } else { ///0
            if (mMediaType == 1 || mMediaType == 2) {
                mGroupMedia.setVisibility(View.VISIBLE);
            }
            mGroupGallery.setVisibility(View.VISIBLE);
            mImageButtonNext.setVisibility(View.VISIBLE);
        }
    }
    private void hideAll() {
        mGroupMedia.setVisibility(View.GONE);
        mGroupGallery.setVisibility(View.GONE);
        mNestedScrollView.setVisibility(View.GONE);
        mRadioGroup.setVisibility(View.GONE);
        mImageButtonPrev.setVisibility(View.GONE);
        mImageButtonNext.setVisibility(View.GONE);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d("TAG", "onAttach()# ");
        super.onAttach(context);

        final Bundle arguments = getArguments();
        if (arguments != null) {
            mMediaType = arguments.getInt(ARG_PARAM1);
            mUri = arguments.getString(ARG_PARAM2);
            mSrc = arguments.getString(ARG_PARAM3);
            mWidth = arguments.getInt(ARG_PARAM4);
            mHeight = arguments.getInt(ARG_PARAM5);
            mAlign = arguments.getInt(ARG_PARAM6);
        }
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView()# ");

        ///[Dialog全屏]
        ///requestFeature() must be called before adding content
        if (getDialog() != null)
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreateView(inflater, container, savedInstanceState);

        ///[Dialog全屏]
        final Window window;
        if (getDialog() != null && (window = getDialog().getWindow()) != null) {
            ///注意：如果不执行setBackgroundDrawable()，全屏不生效！
            ///可设置任意颜色，如果仍然背景透明，则在R.layout.click_image_span_dialog中设置背景为android:background="?android:colorBackground"
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ///修改Dialog默认的padding为0
            window.getDecorView().setPadding(0, 0, 0, 0);
            ///修改LayoutParams为MATCH_PARENT
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        return inflater.inflate(R.layout.click_image_span_dialog, null);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onViewCreated()# ");
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initData();
        resetViews();

        if (getDialog() != null) {
            getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if (mOnShowListener != null) {
                        mOnShowListener.onShow(dialog);
                    }
                }
            });
            getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {  ///选返回键触发
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ClickImageSpanDialogFragment.super.onDismiss(dialog);
                    if (mOnDismissListener != null) {
                        mOnDismissListener.onDismiss(dialog);
                    }
                }
            });
        }
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
    public void onDismiss(@NonNull DialogInterface dialog) {
        Log.d("TAG", "onDismiss()# ");
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d("TAG", "onDismiss()# ");
        super.onCancel(dialog);

        if (mOnCancelListener != null) {
            mOnCancelListener.onCancel(dialog);
        }
    }


    ///[ClickImageSpanDialogBuilder#onActivityResult()]
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mEditTextUri == null || mEditTextSrc == null || getActivity() == null) {
            return;
        }

        ///[媒体选择器#Video/Audio媒体库]
        if (requestCode == REQUEST_CODE_PICK_FROM_VIDEO_MEDIA || requestCode == REQUEST_CODE_PICK_FROM_AUDIO_MEDIA) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri resultUri = data.getData();
                    if (resultUri != null) {
                        mEditTextUri.setText(resultUri.toString());

                        UriUtil.grantReadPermissionToUri(getActivity(), resultUri, data.getFlags());

                        if (requestCode == REQUEST_CODE_PICK_FROM_VIDEO_MEDIA) {
                            if (mImageSpanCallback != null) {
                                ///生成视频的第一帧图片
                                final File videoCoverFile = mImageSpanCallback.generateVideoCoverFile(getActivity(), resultUri);
                                if (videoCoverFile != null) {
                                    mImageWidth = 0;
                                    mImageHeight = 0;
                                    mEditTextSrc.setText(videoCoverFile.getAbsolutePath());
                                    enableDeleteOldSrcFile = true;
                                }
                            }
                        }
                    } else {
                        Log.e("TAG-ClickImageSpan", getActivity().getString(
                                mMediaType == 1 ? R.string.click_image_span_dialog_builder_msg_cannot_retrieve_video : R.string.click_image_span_dialog_builder_msg_cannot_retrieve_audio));
                        Toast.makeText(getActivity(),
                                mMediaType == 1 ? getActivity().getString(R.string.click_image_span_dialog_builder_msg_cannot_retrieve_video) : getActivity().getString(R.string.click_image_span_dialog_builder_msg_cannot_retrieve_audio),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(),
                        mMediaType == 1 ? getActivity().getString(R.string.click_image_span_dialog_builder_msg_video_select_cancelled) : getActivity().getString(R.string.click_image_span_dialog_builder_msg_audio_select_cancelled),
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TAG-ClickImageSpan", getActivity().getString(
                        mMediaType == 1 ? R.string.click_image_span_dialog_builder_msg_video_select_failed : R.string.click_image_span_dialog_builder_msg_audio_select_failed));
                Toast.makeText(getActivity(),
                        mMediaType == 1 ? getActivity().getString(R.string.click_image_span_dialog_builder_msg_video_select_failed) : getActivity().getString(R.string.click_image_span_dialog_builder_msg_audio_select_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }

        ///[媒体选择器#Video/Audio媒体录制]
        else if (requestCode == REQUEST_CODE_PICK_FROM_VIDEO_RECORDER || requestCode == REQUEST_CODE_PICK_FROM_AUDIO_RECORDER) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri resultUri = data.getData();
                    if (resultUri != null) {
                        mEditTextUri.setText(resultUri.toString());
                        enableDeleteOldUriFile = true;

                        if (requestCode == REQUEST_CODE_PICK_FROM_VIDEO_RECORDER) {
                            if (mImageSpanCallback != null) {
                                ///生成视频的第一帧图片
                                final File videoCoverFile = mImageSpanCallback.generateVideoCoverFile(getActivity(), resultUri);
                                if (videoCoverFile != null) {
                                    mImageWidth = 0;
                                    mImageHeight = 0;
                                    mEditTextSrc.setText(videoCoverFile.getAbsolutePath());
                                    enableDeleteOldSrcFile = true;
                                }
                            }
                        }
                    } else {
                        Log.e("TAG-ClickImageSpan", getActivity().getString(
                                mMediaType == 1 ? R.string.click_image_span_dialog_builder_msg_cannot_retrieve_video : R.string.click_image_span_dialog_builder_msg_cannot_retrieve_audio));
                        Toast.makeText(getActivity(),
                                mMediaType == 1 ? getActivity().getString(R.string.click_image_span_dialog_builder_msg_cannot_retrieve_video) : getActivity().getString(R.string.click_image_span_dialog_builder_msg_cannot_retrieve_audio),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(),
                        mMediaType == 1 ? getActivity().getString(R.string.click_image_span_dialog_builder_msg_video_capture_cancelled) : getActivity().getString(R.string.click_image_span_dialog_builder_msg_audio_capture_cancelled),
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TAG-ClickImageSpan", getActivity().getString(
                        mMediaType == 1 ? R.string.click_image_span_dialog_builder_msg_video_capture_failed : R.string.click_image_span_dialog_builder_msg_audio_capture_failed));
                Toast.makeText(getActivity(),
                        mMediaType == 1 ? getActivity().getString(R.string.click_image_span_dialog_builder_msg_video_capture_failed) : getActivity().getString(R.string.click_image_span_dialog_builder_msg_audio_capture_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }

        ///[图片选择器#相册图库]
        else if (requestCode == REQUEST_CODE_PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri selectedUri = data.getData();	///content://com.android.providers.media.documents/document/image%3A46
//					if (selectedUri != null && !TextUtils.equals(selectedUri.toString(), mEditTextSrc.getText())) {
                    if (selectedUri != null) {
                        mImageWidth = 0;
                        mImageHeight = 0;
                        mEditTextSrc.setText(selectedUri.toString());

                        UriUtil.grantReadPermissionToUri(getActivity(), selectedUri, data.getFlags());
                    } else {
                        Log.e("TAG-ClickImageSpan", getActivity().getString(R.string.click_image_span_dialog_builder_msg_cannot_retrieve_image));
                        Toast.makeText(getActivity(), getActivity().getString(R.string.click_image_span_dialog_builder_msg_cannot_retrieve_image),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_select_cancelled),
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TAG-ClickImageSpan", getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_select_failed));
                Toast.makeText(getActivity(), getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_select_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }

        ///[图片选择器#相机拍照]
        else if (requestCode == REQUEST_CODE_PICK_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
//				if (mCameraResultFile != null && !TextUtils.equals(mCameraResultFile.getAbsolutePath(), mEditTextSrc.getText())) {
                if (mCameraResultFile != null) {
                    mImageWidth = 0;
                    mImageHeight = 0;
                    mEditTextSrc.setText(mCameraResultFile.getAbsolutePath());
                    enableDeleteOldSrcFile = true;
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), R.string.click_image_span_dialog_builder_msg_image_capture_cancelled, Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TAG-ClickImageSpan", getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_capture_failed));
                Toast.makeText(getActivity(), getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_capture_failed), Toast.LENGTH_SHORT).show();
            }
        }

        ///[裁剪/压缩#Yalantis/uCrop]https://github.com/Yalantis/uCrop
        else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                final String resultString = UCrop.getOutput(data);
//				if (!TextUtils.isEmpty(resultString) && !TextUtils.equals(resultString, mEditTextSrc.getText())) {
                if (!TextUtils.isEmpty(resultString)) {
                    mImageWidth = 0;
                    mImageHeight = 0;
                    mEditTextSrc.setText(resultString);
                    enableDeleteOldSrcFile = true;
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                if (cropError != null) {
                    Log.e("TAG-ClickImageSpan", cropError.getMessage());
                    Toast.makeText(getActivity(), cropError.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (mTempCropFile != null && mTempCropFile.isFile() && mTempCropFile.exists()) {
                    mTempCropFile.delete();
                    mTempCropFile = null;
                }
            }
        }

        ///[手绘涂鸦#1993hzw/Doodle]https://github.com/1993hzw/Doodle
        else if (requestCode == REQUEST_CODE_DOODLE) {
            if (data == null || resultCode == RESULT_CANCELED) {
                if (mTempDoodleFile != null && mTempDoodleFile.isFile() && mTempDoodleFile.exists()) {
                    mTempDoodleFile.delete();
                    mTempDoodleFile = null;
                }
            } else if (resultCode == DoodleActivity.RESULT_OK) {
                final String resultString = data.getStringExtra(DoodleActivity.KEY_IMAGE_PATH);
//					if (!TextUtils.isEmpty(resultString) && !TextUtils.equals(resultString, mEditTextSrc.getText())) {
                if (!TextUtils.isEmpty(resultString)) {
                    mImageWidth = 0;
                    mImageHeight = 0;
                    mEditTextSrc.setText(resultString);
                    enableDeleteOldSrcFile = true;
                }
            } else if (resultCode == DoodleActivity.RESULT_ERROR) {
                Log.e("TAG-ClickImageSpan", getActivity().getString(R.string.click_image_span_dialog_builder_msg_doodle_failed));
                Toast.makeText(getActivity(), getActivity().getString(R.string.click_image_span_dialog_builder_msg_doodle_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void initData() {
        mInitialUri = mUri;
        mInitialSrc = mSrc;

        mImageWidth = mWidth;
        mImageHeight = mHeight;
        Log.d("TAG-ClickImageSpan", "initial()# mImageWidth: " + mImageWidth + ", mImageHeight: " + mImageHeight);

        if (!TextUtils.isEmpty(mUri)) {
            mEditTextUri.setText(mUri);
        }
        if (!TextUtils.isEmpty(mSrc)) {
            mEditTextSrc.setText(mSrc);
        }

        mVerticalAlignment = mAlign;
        if (mAlign == ALIGN_BOTTOM) {
            mRadioButtonAlignBottom.setChecked(true);
        } else if (mAlign == ALIGN_BASELINE) {
            mRadioButtonAlignBaseline.setChecked(true);
        } else if (mAlign == ALIGN_CENTER) {
            mRadioButtonAlignCenter.setChecked(true);
        }
    }

    ///避免内存泄漏
    public void clear() {
        mButtonCrop = null;
        mButtonDoodle = null;
        mImageButtonDisplayRestore = null;
        mImageButtonZoomOut = null;
        mImageButtonZoomIn = null;
        mButtonPickFromCamera = null;
        mButtonPickFromGallery = null;
        mButtonPickFromRecorder = null;
        mButtonPickFromMedia = null;
        mCheckBoxDisplayConstrainWidth = null;
        mCheckBoxDisplayConstrainHeight = null;
        mEditTextDisplayHeight = null;
        mEditTextDisplayWidth = null;
        mSliderDisplayWidth = null;
        mSliderDisplayHeight = null;
        mEditTextSrc = null;
        mEditTextUri = null;
        mImageViewPreview = null;
        mRadioGroup = null;
        mRadioButtonAlignBottom = null;
        mRadioButtonAlignBaseline = null;
        mRadioButtonAlignCenter = null;
    }


    private void initView(@NonNull View view) {
        view.findViewById(R.id.btn_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFinishAction();

                if (getDialog() != null) {
                    getDialog().dismiss();
                }

                dismiss();
            }
        });
        view.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClearAction();

                if (getDialog() != null) {
                    getDialog().dismiss();
                }

                dismiss();
            }
        });
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancelAction();

                if (getDialog() != null) {
                    getDialog().dismiss();
                }

                dismiss();
            }
        });

        mButtonPickFromMedia = (Button) view.findViewById(R.id.btn_pickup_from_media);
        mButtonPickFromMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromMedia();
            }
        });

        mButtonPickFromRecorder = (Button) view.findViewById(R.id.btn_pickup_from_recorder);
        mButtonPickFromRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromRecorder();
            }
        });

        mEditTextUri = (EditText) view.findViewById(R.id.et_uri);
        mEditTextUri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                deleteOldUriFile();
                enableDeleteOldUriFile = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mButtonPickFromGallery = (Button) view.findViewById(R.id.btn_pickup_from_gallery);
        mButtonPickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });

        mButtonPickFromCamera = (Button) view.findViewById(R.id.btn_pickup_from_camera);
        mButtonPickFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromCamera();
            }
        });

        mEditTextSrc = (EditText) view.findViewById(R.id.et_src);
        mEditTextSrc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                deleteOldSrcFile();
                enableDeleteOldSrcFile = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String src = s.toString();
                Log.d("TAG-ClickImageSpan", "mEditTextSrc# TextWatcher.onTextChanged()# src: " + src);

                loadImage(src);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mImageViewPreview = (ImageView) view.findViewById(R.id.iv_preview);

        mSliderDisplayWidth = view.findViewById(R.id.slider_display_width);
        mSliderDisplayWidth.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                adjustEditTextDisplay(true,
                        mCheckBoxDisplayConstrainWidth.isChecked() || mCheckBoxDisplayConstrainHeight.isChecked(),
                        slider.getValue());
            }
        });
        mSliderDisplayHeight = view.findViewById(R.id.slider_display_height);
        mSliderDisplayHeight.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                adjustEditTextDisplay(false,
                        mCheckBoxDisplayConstrainWidth.isChecked() || mCheckBoxDisplayConstrainHeight.isChecked(),
                        slider.getValue());
            }
        });

        mEditTextDisplayWidth = (EditText) view.findViewById(R.id.et_display_width);
        mEditTextDisplayWidth.setFilters(inputFilters);
        mEditTextDisplayWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!enableEditTextDisplayChangedListener) {
                    return;
                }

                adjustEditTextDisplay(true,
                        mCheckBoxDisplayConstrainWidth.isChecked() || mCheckBoxDisplayConstrainHeight.isChecked(),
                        0.0F);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mEditTextDisplayHeight = (EditText) view.findViewById(R.id.et_display_height);
        mEditTextDisplayHeight.setFilters(inputFilters);
        mEditTextDisplayHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!enableEditTextDisplayChangedListener) {
                    return;
                }

                adjustEditTextDisplay(false,
                        mCheckBoxDisplayConstrainWidth.isChecked() || mCheckBoxDisplayConstrainHeight.isChecked(),
                        0.0F);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mCheckBoxDisplayConstrainWidth = (CheckBox) view.findViewById(R.id.cb_display_constrain_by_width);
        mCheckBoxDisplayConstrainWidth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && mImageWidth > 0) {
                    adjustEditTextDisplay(true, true, 0.0F);
                }
            }
        });

        mCheckBoxDisplayConstrainHeight = (CheckBox) view.findViewById(R.id.cb_display_constrain_by_height);
        mCheckBoxDisplayConstrainHeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && mImageHeight > 0) {
                    adjustEditTextDisplay(false, true, 0.0F);
                }
            }
        });

        mImageButtonDisplayRestore = (ImageButton) view.findViewById(R.id.ib_display_restore);
        mImageButtonDisplayRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditTextDisplayChangedListener = false;
                mEditTextDisplayWidth.setText(String.valueOf(mImageWidth));
                mEditTextDisplayHeight.setText(String.valueOf(mImageHeight));
                enableEditTextDisplayChangedListener = true;

                ///[FIX#保存原始图片，避免反复缩放过程中使用模糊图片]
//				setDrawable(mImageViewPreview.getDrawable(), mImageWidth, mImageHeight);
                setDrawable(mOriginalDrawable, mImageWidth, mImageHeight);
            }
        });

        mImageButtonZoomOut = (ImageButton) view.findViewById(R.id.ib_zoom_out);
        mImageButtonZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustEditTextDisplay(true, true, -IMAGE_ZOOM_FACTOR);
            }
        });

        mImageButtonZoomIn = (ImageButton) view.findViewById(R.id.ib_zoom_in);
        mImageButtonZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustEditTextDisplay(true, true, IMAGE_ZOOM_FACTOR);
            }
        });

        mButtonCrop = (Button) view.findViewById(R.id.btn_crop);
        mButtonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageSpanCallback == null || getActivity() == null) {
                    return;
                }

                final String src = mEditTextSrc.getText().toString();

                final Pair<Uri, File> pair = mImageSpanCallback.generateActionSourceAndFile(getActivity(), ACTION_CROP, src);

                if (pair.first != null && pair.second != null) {
                    mTempCropFile = pair.second;
                    startCrop(getActivity(), pair.first, pair.second.getAbsolutePath());
                } else {
                    Log.e("TAG-ClickImageSpan", "Image does not exist, or the read and write permissions are not authorized. " + src);
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_does_not_exist, src), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonDoodle = (Button) view.findViewById(R.id.btn_doodle);
        mButtonDoodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageSpanCallback == null || getActivity() == null) {
                    return;
                }

                final String src = mEditTextSrc.getText().toString();

                final Pair<Uri, File> pair = mImageSpanCallback.generateActionSourceAndFile(getActivity(), ACTION_DOODLE, src);

                if (pair.first != null && pair.second != null) {
                    mTempDoodleFile = pair.second;
                    startDoodle(getActivity(), pair.first, pair.second.getAbsolutePath());
                } else {
                    Log.e("TAG-ClickImageSpan", "Image does not exist, or the read and write permissions are not authorized. " + src);
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_does_not_exist, src), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRadioGroup = (RadioGroup) view.findViewById(R.id.rg_align);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_align_bottom) {
                    mVerticalAlignment = ALIGN_BOTTOM;
                } else if (checkedId == R.id.rb_align_baseline) {
                    mVerticalAlignment = ALIGN_BASELINE;
                } else if (checkedId == R.id.rb_align_center) {
                    mVerticalAlignment = ALIGN_CENTER;
                }
            }
        });
        mRadioButtonAlignBottom = (RadioButton) view.findViewById(R.id.rb_align_bottom);
        mRadioButtonAlignBaseline = (RadioButton) view.findViewById(R.id.rb_align_baseline);
        mRadioButtonAlignCenter = (RadioButton) view.findViewById(R.id.rb_align_center);

        mGroupMedia = (Group) view.findViewById(R.id.group_media);
        mGroupGallery = (Group) view.findViewById(R.id.group_gallery);
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.nsc_scroll_view);

        mImageButtonPrev = (ImageButton) view.findViewById(R.id.btn_prev);
        mImageButtonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPageNumber > 0) {
                    mCurrentPageNumber--;
                }
                showPage();
            }
        });
        mImageButtonNext = (ImageButton) view.findViewById(R.id.btn_next);
        mImageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPageNumber < 2) {
                    mCurrentPageNumber++;
                }
                showPage();
            }
        });

        ///初始化mCurrentPageNumber
        mCurrentPageNumber = mUri == null && mSrc == null ?  0 : 1;
        showPage();
    }

    private void loadImage(String src) {
        if (getActivity() == null) {
            return;
        }

        ///Glide下载图片（使用已经缓存的图片）给imageView
        ///https://muyangmin.github.io/glide-docs-cn/doc/getting-started.html
        //////??????placeholder（占位符）、error（错误符）、fallback（后备回调符）
        final RequestOptions options = new RequestOptions();

        ///[FIX#Android KITKAT 4.4 (API 19及以下)使用Vector Drawable出现异常：android.content.res.Resources$NotFoundException:  See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info]
        ///https://stackoverflow.com/questions/34417843/how-to-use-vectordrawables-in-android-api-lower-than-21
        ///https://stackoverflow.com/questions/39419596/resourcesnotfoundexception-file-res-drawable-abc-ic-ab-back-material-xml/41965285
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            final Drawable placeholderDrawable = VectorDrawableCompat.create(getActivity().getResources(),
                    ///[FIX#Android KITKAT 4.4 (API 19及以下)使用layer-list Drawable出现异常：org.xmlpull.v1.XmlPullParserException: Binary XML file line #2<vector> tag requires viewportWidth > 0
//                    		R.drawable.layer_list_placeholder,
                    R.drawable.placeholder,
                    getActivity().getTheme());

            options.placeholder(placeholderDrawable);
        } else {
            options.placeholder(R.drawable.layer_list_placeholder);	///options.placeholder(new ColorDrawable(Color.BLACK));	// 或者可以直接使用ColorDrawable
        }

        ///获取图片真正的宽高
        ///https://www.jianshu.com/p/299b637afe7c
        Glide.with(getActivity().getApplicationContext())
//						.asBitmap()//强制Glide返回一个Bitmap对象 //注意：在Glide 3中的语法是先load()再asBitmap()，而在Glide 4中是先asBitmap()再load()
                .load(src)
                .apply(options)

                .override(getRichEditorToolbar().getImageMaxWidth(), getRichEditorToolbar().getImageMaxHeight()) // resize the image to these dimensions (in pixel). does not respect aspect ratio
//				.centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
//				.fitCenter()    ///fitCenter()会缩放图片让两边都相等或小于ImageView的所需求的边框。图片会被完整显示，可能不能完全填充整个ImageView。

                ///SimpleTarget deprecated. Use CustomViewTarget if loading the content into a view
                ///http://bumptech.github.io/glide/javadocs/490/com/bumptech/glide/request/target/SimpleTarget.html
                ///https://github.com/bumptech/glide/issues/3304
//						.into(new SimpleTarget<Bitmap>() { ... }
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        mImageViewPreview.setImageDrawable(placeholder);

                        resetViews();
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        ///[ImageSpan#调整宽高：考虑到宽高为0或负数的情况]
                        Log.d("TAG-ClickImageSpan", "mEditTextSrc# TextWatcher.onTextChanged()# Glide.onResourceReady()# Before adjustDrawableSize()# mImageWidth: " + mImageWidth + ", mImageHeight: " + mImageHeight);
                        final Pair<Integer, Integer> pair = ToolbarHelper.adjustDrawableSize(drawable,
                                mImageWidth, mImageHeight,
                                getRichEditorToolbar().getImageMaxWidth(), getRichEditorToolbar().getImageMaxHeight());
                        mImageWidth = pair.first; mImageHeight = pair.second;
                        Log.d("TAG-ClickImageSpan", "mEditTextSrc# TextWatcher.onTextChanged()# Glide.onResourceReady()# After adjustDrawableSize()# mImageWidth: " + mImageWidth + ", mImageHeight: " + mImageHeight);

                        if (mImageWidth > getRichEditorToolbar().getImageMaxWidth()
//								|| mImageHeight > getRichEditorToolbar().getImageMaxHeight()	///只检测宽度！
                        ) {
                            Toast.makeText(getRichEditorToolbar().getContext(),
                                    getRichEditorToolbar().getContext().getString(R.string.click_image_span_dialog_builder_msg_image_size_exceeds,
                                            getRichEditorToolbar().getImageMaxWidth(), getRichEditorToolbar().getImageMaxHeight()),
                                    Toast.LENGTH_LONG).show();
                        }

                        ///[FIX#保存原始图片，避免反复缩放过程中使用模糊图片]
                        mOriginalDrawable = drawable;
                        setDrawable(drawable, mImageWidth, mImageHeight);

                        enableEditTextDisplayChangedListener = false;
                        mEditTextDisplayWidth.setText(String.valueOf(mImageWidth));
                        mEditTextDisplayHeight.setText(String.valueOf(mImageHeight));
                        enableEditTextDisplayChangedListener = true;

                        if (!(drawable instanceof GifDrawable)) {
                            ///除GifDrawable保持禁止之外，其它都允许Crop和Draw
                            mButtonCrop.setEnabled(true);
                            mButtonDoodle.setEnabled(true);
                        }

                        mSliderDisplayWidth.setEnabled(true);
                        mSliderDisplayHeight.setEnabled(true);
                        mEditTextDisplayWidth.setEnabled(true);
                        mEditTextDisplayHeight.setEnabled(true);
                        mCheckBoxDisplayConstrainWidth.setEnabled(true);
                        mCheckBoxDisplayConstrainHeight.setEnabled(true);
                        mImageButtonDisplayRestore.setEnabled(true);
                        mImageButtonZoomOut.setEnabled(true);
                        mImageButtonZoomIn.setEnabled(true);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (TextUtils.isEmpty(src)) {
                            Toast.makeText(getRichEditorToolbar().getContext(),
                                    getRichEditorToolbar().getContext().getString(R.string.click_image_span_dialog_builder_msg_please_input_image_resource),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getRichEditorToolbar().getContext(),
                                    getRichEditorToolbar().getContext().getString(R.string.click_image_span_dialog_builder_msg_image_load_fails, src),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
//						Toast.makeText(getRichEditorToolbar().getContext(),
//								String.format(getRichEditorToolbar().getContext().getString(R.string.message_image_load_is_cancelled), src),
//								Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void resetViews() {
        enableEditTextDisplayChangedListener = false;
        mEditTextDisplayWidth.setText(null);
        mEditTextDisplayHeight.setText(null);
        enableEditTextDisplayChangedListener = true;

        mSliderDisplayWidth.setEnabled(false);
        mSliderDisplayHeight.setEnabled(false);
        mEditTextDisplayWidth.setEnabled(false);
        mEditTextDisplayHeight.setEnabled(false);
        mCheckBoxDisplayConstrainWidth.setEnabled(false);
        mCheckBoxDisplayConstrainHeight.setEnabled(false);
        mImageButtonDisplayRestore.setEnabled(false);
        mImageButtonZoomOut.setEnabled(false);
        mImageButtonZoomIn.setEnabled(false);

        ///先设置Crop和Doodle为false
        mButtonCrop.setEnabled(false);
        mButtonDoodle.setEnabled(false);
    }

    ///https://www.jianshu.com/p/5b5cef2ffff2
    private final InputFilter[] inputFilters = new InputFilter[] {
            /**
             * @param source 输入的文字
             * @param start 输入-0，删除-0
             * @param end 输入-文字的长度，删除-0
             * @param dest 原先显示的内容
             * @param dstart 输入-原光标位置，删除-光标删除结束位置
             * @param dend  输入-原光标位置，删除-光标删除开始位置
             * @return
            */
            /**限制文本长度*/
            new InputFilter.LengthFilter(IMAGE_MAX_DISPLAY_DIGITS),

            new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                    ///不做任何Filter
//                    return null;
//                    return source; ///等同于source.subSequence(start, end);

//                    ///删除（所选内容替换为空）
//                    //////??????[BUG#当所选包括最后一个字符时，全部内容被删除了！]
//                    return "";

                    ///[Filter过滤#android:inputType="number"]注意：不受EditText#android:inputType限制！可以任意内容，所以需要Filter过滤来处理
                    ///注意：允许删除，甚至为空
                    return source.toString().replaceAll("[^\\d]+", ""); ///注意：负数符号也被过滤掉了
                }
            }
    };

    private void setDrawable(Drawable drawable, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }

        drawable.setBounds(0, 0, width, height);
        mImageViewPreview.setImageDrawable(drawable);

        ///[ImageSpan#Glide#GifDrawable]
        ///https://muyangmin.github.io/glide-docs-cn/doc/targets.html
        if (drawable instanceof GifDrawable) {
            ((GifDrawable) drawable).setLoopCount(GifDrawable.LOOP_FOREVER);
            ((GifDrawable) drawable).start();
        }
    }

    private void adjustEditTextDisplay(boolean isWidth, boolean isConstrain, float zoomFactor) {
        if (mEditTextDisplayWidth.getText() == null || mEditTextDisplayHeight.getText() == null) {
            return;
        }

        int width = parseInt(mEditTextDisplayWidth.getText().toString());
        int height = parseInt(mEditTextDisplayHeight.getText().toString());

        int newWidth = zoomFactor == 0.0F || !isWidth && !isConstrain ? width : (int) (width * (zoomFactor + 1.0F));
        int newHeight = zoomFactor == 0.0F || isWidth && !isConstrain ? height : (int) (height * (zoomFactor + 1.0F));

        final Pair<Integer, Integer> pair = isWidth ?
                adjustWidth(newWidth, newHeight,
                        getRichEditorToolbar().getImageMaxWidth(), getRichEditorToolbar().getImageMaxHeight(),
                        mImageWidth, mImageHeight,
                        isConstrain) :
                adjustHeight(newWidth, newHeight,
                        getRichEditorToolbar().getImageMaxWidth(), getRichEditorToolbar().getImageMaxHeight(),
                        mImageWidth, mImageHeight,
                        isConstrain);

        updateEditTextDisplay(width, height, pair.first, pair.second);

        ///[FIX#保存原始图片，避免反复缩放过程中使用模糊图片]
//		setDrawable(mImageViewPreview.getDrawable(), pair.first, pair.second);
        setDrawable(mOriginalDrawable, pair.first, pair.second);
    }

    private void updateEditTextDisplay(int width, int height, int newWidth, int newHeight) {
        if (width > 0 && width != newWidth) {
            enableEditTextDisplayChangedListener = false;
            mEditTextDisplayWidth.setText(String.valueOf(newWidth));
            enableEditTextDisplayChangedListener = true;
        }
        if (height > 0 && height != newHeight) {
            enableEditTextDisplayChangedListener = false;
            mEditTextDisplayHeight.setText(String.valueOf(newHeight));
            enableEditTextDisplayChangedListener = true;
        }
    }


    ///[媒体选择器#Autio/Video媒体库]
    private void pickFromMedia() {
        if (getActivity() == null) {
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType(mMediaType == 1 ? VIDEO_TYPE : AUDIO_TYPE)
                .addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, getActivity().getString(
                            mMediaType == 1 ? R.string.click_image_span_dialog_builder_title_select_video : R.string.click_image_span_dialog_builder_title_select_audio)),
                    mMediaType == 1 ? REQUEST_CODE_PICK_FROM_VIDEO_MEDIA : REQUEST_CODE_PICK_FROM_AUDIO_MEDIA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {	///[FIX#Amazon Kindle#App requires rear camera]
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    ///[媒体选择器#Autio/Video媒体录制]
    private void pickFromRecorder() {
        if (mImageSpanCallback == null || getActivity() == null) {
            return;
        }

        final Intent intent = new Intent(mMediaType == 1 ? MediaStore.ACTION_VIDEO_CAPTURE : MediaStore.Audio.Media.RECORD_SOUND_ACTION);

        final Pair<Uri, File> pair = mImageSpanCallback.generateMediaTypeSourceAndFile(getActivity(), mMediaType);
        Log.d("TAG-ClickImageSpan", "pickFromRecorder()# Uri: " + pair.first);

        if (pair.first != null && pair.second != null) {
            ///MediaStore.EXTRA_OUTPUT：设置媒体文件的保存路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pair.first);

            //////??????如果是视频，还可以设置其它值：
            ///MediaStore.EXTRA_VIDEO_QUALITY：设置视频录制的质量，0为低质量，1为高质量。
            ///MediaStore.EXTRA_DURATION_LIMIT：设置视频最大允许录制的时长，单位为毫秒。
            ///MediaStore.EXTRA_SIZE_LIMIT：指定视频最大允许的尺寸，单位为byte。

            ///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            try {
                startActivityForResult(intent,
                        mMediaType == 1 ? REQUEST_CODE_PICK_FROM_VIDEO_RECORDER : REQUEST_CODE_PICK_FROM_AUDIO_RECORDER);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {	///[FIX#Amazon Kindle#App requires rear camera]
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("TAG-ClickImageSpan", "Image does not exist, or the read and write permissions are not authorized. " + pair.first + ", " + pair.second);
            Toast.makeText(getActivity(),
                    getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_does_not_exist,
                            pair.first + ", " + pair.second), Toast.LENGTH_SHORT).show();
        }
    }

    ///[图片选择器#相册图库]
    private void pickFromGallery() {
        if (getActivity() == null) {
            return;
        }

        ///[FIX#java.lang.SecurityException: Permission Denial: opening provider com.android.providers.media.MediaDocumentsProvider requires that you obtain access using ACTION_OPEN_DOCUMENT or related APIs]
        ///https://stackoverflow.com/questions/22178041/getting-permission-denial-exception
        final Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType(IMAGE_TYPE);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(IMAGE_TYPE);

            ///https://stackoverflow.com/questions/23385520/android-available-mime-types
            final String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png", "image/bmp", "image/gif"};//////??????相册不支持"image/bmp"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.click_image_span_dialog_builder_title_select_picture)),
                    REQUEST_CODE_PICK_FROM_GALLERY);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {	///[FIX#Amazon Kindle#App requires rear camera]
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    ///[图片选择器#相机拍照]
    private File mCameraResultFile;
    private void pickFromCamera() {
        if (mImageSpanCallback == null || getActivity() == null) {
            return;
        }

        // create Intent to take a picture and return control to the calling application
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        ///https://stackoverflow.com/questions/64945660/consider-adding-a-queries-declaration-to-your-manifest-when-calling-this-method
//		///Android R 11 (API 30)开始，只有预装的系统相机应用可以响应action.IMAGE_CAPTURE 等操作，
//		///而且intent.resolveActivity(getActivity().getPackageManager())会返回null
//		///https://juejin.im/post/6860370635664261128
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//			// Ensure that there's a camera activity to handle the intent
//			if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
//				Toast.makeText(getActivity(), R.string.message_system_camera_not_available, Toast.LENGTH_SHORT).show();
//				return;
//			}
//		}

        final Pair<Uri, File> pair = mImageSpanCallback.generateMediaTypeSourceAndFile(getActivity(), mMediaType);
        mCameraResultFile = pair.second;
        Log.d("TAG-ClickImageSpan", "pickFromCamera()# mCameraResultFile: " + pair.second);

        if (pair.first != null && pair.second != null) {
            ///注意：系统相机拍摄的照片，如果不通过MediaStore.EXTRA_OUTPUT指定路径，data.getExtras().getParcelableExtra("data")只能得到Bitmap缩略图！
            ///如果指定了保存路径，则照片保存到指定文件（此时，Intent返回null）
            ///另外，不建议用uri！这种FileProvider内容提供者的uri很难获得File文件目录进行文件操作！
            ///指定拍照路径时，先检查路径中的文件夹是否都存在，不存在时先创建文件夹再调用相机拍照；照片的命名中不要包含空格等特殊符号
            ///https://www.jianshu.com/p/c1c2555e287c
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pair.first);

            ///因为没有使用cameraResultUri传递拍照结果图片目录，所以注释掉以下：
//			///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//			}

            try {
                startActivityForResult(intent, REQUEST_CODE_PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {	///[FIX#Amazon Kindle#App requires rear camera]
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("TAG-ClickImageSpan", "Image does not exist, or the read and write permissions are not authorized. " + pair.first + ", " + pair.second);
            Toast.makeText(getActivity(),
                    getActivity().getString(R.string.click_image_span_dialog_builder_msg_image_does_not_exist,
                            pair.first + ", " + pair.second), Toast.LENGTH_SHORT).show();
        }
    }

    ///[裁剪/压缩#Yalantis/uCrop]https://github.com/Yalantis/uCrop
    private File mTempCropFile;
    private void startCrop(@NonNull Activity activity, @NonNull Uri source, @NonNull String destination) {
        ///建议在MyApplication中添加
//		///[FIX#Android KITKAT 4.4 (API 19及以下)使用Vector Drawable出现异常：android.content.res.Resources$NotFoundException:  See AppCompatDelegate.setCompatVectorFromResourcesEnabled() for more info]
//		///[NotFoundException: File res/drawable/ucrop_ic_cross.xml from drawable resource ID]https://github.com/Yalantis/uCrop/issues/529
//		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        final UCrop uCrop = UCrop.of(source, destination);

        uCrop.start(activity, this);
    }

    ///[手绘涂鸦#1993hzw/Doodle]https://github.com/1993hzw/Doodle
    private File mTempDoodleFile;
    private void startDoodle(@NonNull Activity activity, @NonNull Uri imageUri, String savePath) {
        final DoodleParams params = new DoodleParams();
        params.mImageUri = imageUri;
        params.mSavePath = savePath;

        try {
//            DoodleActivity.startActivityForResult(activity, params, REQUEST_CODE_DOODLE);
            final Intent intent = new Intent(activity, DoodleActivity.class);
            intent.putExtra(DoodleActivity.KEY_PARAMS, params);

            startActivityForResult(intent, REQUEST_CODE_DOODLE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {	///[FIX#Amazon Kindle#App requires rear camera]
            e.printStackTrace();
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    ///删除与原始uri/src不同的文件
    private boolean enableDeleteOldUriFile = false;
    private boolean enableDeleteOldSrcFile = false;
    private void deleteOldUriFile() {
        if (enableDeleteOldUriFile && !TextUtils.isEmpty(mEditTextUri.getText()) && !TextUtils.equals(mInitialUri, mEditTextUri.getText())) {
            deleteFile(mEditTextUri.getText().toString());
        }
    }
    private void deleteOldSrcFile() {
        if (enableDeleteOldSrcFile && !TextUtils.isEmpty(mEditTextSrc.getText()) && !TextUtils.equals(mInitialSrc, mEditTextSrc.getText())) {
            deleteFile(mEditTextSrc.getText().toString());
        }
    }
    private void deleteFile(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        final Uri uri = Uri.parse(text);
        if (uri == null) {
            return;
        }

        ///比如“/storage/emulated/0/Android/data/cc.brainbook.android.richeditor/files/Pictures/20201003050111.jpg”，或无效文本
        if (uri.getScheme() == null) {
            innerDeleteFile(text);
        } else

            ///比如“file:///storage/emulated/0/Android/data/cc.brainbook.android.richeditor/files/Pictures/20201003050111.jpg”
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                innerDeleteFile(uri.getPath());
            }
    }

    private void innerDeleteFile(String filePath) {
        final File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            if (!file.delete()) {
                Log.w("TAG-ClickImageSpan", "Fail to delete file: " + file.getAbsolutePath());
            }
        }
    }

}
