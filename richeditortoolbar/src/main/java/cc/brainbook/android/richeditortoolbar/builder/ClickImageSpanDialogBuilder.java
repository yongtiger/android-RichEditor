package cc.brainbook.android.richeditortoolbar.builder;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Date;

import cc.brainbook.android.richeditortoolbar.R;
import cc.brainbook.android.richeditortoolbar.util.FileUtil;
import cc.brainbook.android.richeditortoolbar.util.StringUtil;
import cc.brainbook.android.richeditortoolbar.util.UriUtil;
import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleParams;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;
import static cc.brainbook.android.richeditortoolbar.RichEditorToolbar.PROVIDER_AUTHORITIES;
import static cc.brainbook.android.richeditortoolbar.util.FileUtil.generateVideoCover;

public class ClickImageSpanDialogBuilder extends BaseDialogBuilder {
	private static final String IMAGE_FILE_SUFFIX = ".jpg";
	private static final String VIDEO_FILE_SUFFIX = ".mp4";
	private static final String AUDIO_FILE_SUFFIX = ".3gp";

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
	private static final int REQUEST_CODE_DRAW = 10;


	private int mImageOverrideWidth = 200;
	private int mImageOverrideHeight = 200;

	private int mMediaType;	///0: image; 1: video; 2: audio
	private String mDefaultAudioVideoCoverImageFileName;	///缺省的音频/视频封面图片

	private File mImageFileDir;	///完整的图片绝对目录（绝对路径）
	public ClickImageSpanDialogBuilder setImageFileDir(File imageFileDir) {
		mImageFileDir = imageFileDir;

		return this;
	}
	private File mVideoFileDir;	///完整的视频绝对目录（绝对路径）
	public ClickImageSpanDialogBuilder setVideoFileDir(File videoFileDir) {
		mVideoFileDir = videoFileDir;

		return this;
	}
	private File mAudioFileDir;	///完整的音频绝对目录（绝对路径）
	public ClickImageSpanDialogBuilder setAudioFileDir(File audioFileDir) {
		mAudioFileDir = audioFileDir;

		return this;
	}

	private String mInitialUri;	///初始化时的uri
	private String mInitialSrc;	///初始化时的src

	private int mVerticalAlignment;

	private int mImageWidth;
	private int mImageHeight;

	private Button mButtonPickFromMedia;
	private Button mButtonPickFromRecorder;
	private EditText mEditTextUri;

	private Button mButtonPickFromGallery;
	private Button mButtonPickFromCamera;
	private EditText mEditTextSrc;

	private ImageView mImageViewPreview;

	private EditText mEditTextDisplayWidth;
	private EditText mEditTextDisplayHeight;
	private CheckBox mCheckBoxDisplayConstrain;
	private Button mButtonDisplayRestore;

    private Button mButtonCrop;
    private Button mButtonDraw;

	private RadioGroup mRadioGroup;
	private RadioButton mRadioButtonAlignBottom;
	private RadioButton mRadioButtonAlignBaseline;
	private RadioButton mRadioButtonAlignCenter;


	public interface OnClickListener {
		void onClick(DialogInterface d, String uri, String src, int width, int height, int align);
	}

	private boolean isInitializing;
	public ClickImageSpanDialogBuilder initial(String uriString, String src, int width, int height, int align, int imageOverrideWidth, int imageOverrideHeight) {
		isInitializing = true;
		mImageOverrideWidth = imageOverrideWidth;
		mImageOverrideHeight = imageOverrideHeight;

		mEditTextUri.setText(uriString);

		///保存初始uri、src
		mInitialUri = uriString;
		mInitialSrc = src;

		///设置缺省的音频/视频封面图片
		if (TextUtils.isEmpty(src) && mMediaType != 0) {
			mEditTextSrc.setText(mDefaultAudioVideoCoverImageFileName);
			mEditTextDisplayWidth.setText(String.valueOf(mImageOverrideWidth));
			mEditTextDisplayHeight.setText(String.valueOf(mImageOverrideHeight));
		} else {
			mEditTextSrc.setText(src);
			mEditTextDisplayWidth.setText(String.valueOf(width));
			mEditTextDisplayHeight.setText(String.valueOf(height));
		}

		mVerticalAlignment = align;
		if (align == ALIGN_BOTTOM) {
			mRadioButtonAlignBottom.setChecked(true);
		} else if (align == ALIGN_BASELINE) {
			mRadioButtonAlignBaseline.setChecked(true);
		} else if (align == ALIGN_CENTER) {
			mRadioButtonAlignCenter.setChecked(true);
		}

		return this;
	}

	private void initView(@NonNull View layout) {
		mButtonPickFromMedia = (Button) layout.findViewById(R.id.btn_pickup_from_media);
		mButtonPickFromMedia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickFromMedia();
			}
		});

		mButtonPickFromRecorder = (Button) layout.findViewById(R.id.btn_pickup_from_recorder);
		mButtonPickFromRecorder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickFromRecorder();
			}
		});

		mEditTextUri = (EditText) layout.findViewById(R.id.et_uri);
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

		mButtonPickFromMedia.setVisibility(mMediaType == 0 ? View.GONE : View.VISIBLE);
		mButtonPickFromRecorder.setVisibility(mMediaType == 0 ? View.GONE : View.VISIBLE);
		mEditTextUri.setVisibility(mMediaType == 0 ? View.GONE : View.VISIBLE);

		mButtonPickFromGallery = (Button) layout.findViewById(R.id.btn_pickup_from_gallery);
		mButtonPickFromGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickFromGallery();
			}
		});

		mButtonPickFromCamera = (Button) layout.findViewById(R.id.btn_pickup_from_camera);
		mButtonPickFromCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickFromCamera();
			}
		});

		mEditTextSrc = (EditText) layout.findViewById(R.id.et_src);
		mEditTextSrc.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				deleteOldSrcFile();
				enableDeleteOldSrcFile = false;
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				final String src = s.toString();

				///Glide下载图片（使用已经缓存的图片）给imageView
				///https://muyangmin.github.io/glide-docs-cn/doc/getting-started.html
				//////??????placeholder（占位符）、error（错误符）、fallback（后备回调符）
				final RequestOptions options = new RequestOptions()
						.placeholder(R.drawable.ic_image_black_24dp); ///   .placeholder(new ColorDrawable(Color.BLACK))   // 或者可以直接使用ColorDrawable

				///获取图片真正的宽高
				///https://www.jianshu.com/p/299b637afe7c
				Glide.with(mContext.getApplicationContext())
//						.asBitmap()//强制Glide返回一个Bitmap对象 //注意：在Glide 3中的语法是先load()再asBitmap()，而在Glide 4中是先asBitmap()再load()
						.load(src)
						.apply(options)

						.override(mImageOverrideWidth, mImageOverrideHeight) // resize the image to these dimensions (in pixel). does not respect aspect ratio
//							.centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
//						.fitCenter()    ///fitCenter()会缩放图片让两边都相等或小于ImageView的所需求的边框。图片会被完整显示，可能不能完全填充整个ImageView。

						///SimpleTarget deprecated. Use CustomViewTarget if loading the content into a view
						///http://bumptech.github.io/glide/javadocs/490/com/bumptech/glide/request/target/SimpleTarget.html
						///https://github.com/bumptech/glide/issues/3304
//							.into(new SimpleTarget<Bitmap>() { ... }
						.into(new CustomTarget<Drawable>() {
							@Override
							public void onLoadStarted(@Nullable Drawable placeholder) {	///placeholder
								mImageWidth = 0;
								mImageHeight = 0;

								mEditTextDisplayWidth.setText(String.valueOf(mImageWidth));
								mEditTextDisplayHeight.setText(String.valueOf(mImageHeight));

								mEditTextDisplayWidth.setEnabled(false);
								mEditTextDisplayHeight.setEnabled(false);
								mButtonDisplayRestore.setEnabled(false);

								///先设置Crop和Draw为false
								mButtonCrop.setEnabled(false);
								mButtonDraw.setEnabled(false);

								mImageViewPreview.setImageDrawable(placeholder);
							}

							@Override
							public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
								mImageWidth = resource.getIntrinsicWidth();
								mImageHeight = resource.getIntrinsicHeight();

								if (!isInitializing) {
									mEditTextDisplayWidth.setText(String.valueOf(mImageWidth));
									mEditTextDisplayHeight.setText(String.valueOf(mImageHeight));
								}

								mEditTextDisplayWidth.setEnabled(true);
								mEditTextDisplayHeight.setEnabled(true);
								mButtonDisplayRestore.setEnabled(true);

								mImageViewPreview.setImageDrawable(resource);

								isInitializing = false;

								///[ImageSpan#Glide#GifDrawable]
								///https://muyangmin.github.io/glide-docs-cn/doc/targets.html
								if (resource instanceof GifDrawable) {
									((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
									((GifDrawable) resource).start();
								} else {
									///除GifDrawable保持禁止之外，其它都允许Crop和Draw
									mButtonCrop.setEnabled(true);
									mButtonDraw.setEnabled(true);
								}
							}

							@Override
							public void onLoadFailed(@Nullable Drawable errorDrawable) {
								isInitializing = false;
							}

							@Override
							public void onLoadCleared(@Nullable Drawable placeholder) {}
						});
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		mImageViewPreview = (ImageView) layout.findViewById(R.id.iv_preview);

		mEditTextDisplayWidth = (EditText) layout.findViewById(R.id.et_display_width);
		mEditTextDisplayWidth.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mEditTextDisplayWidth.isFocused() && mCheckBoxDisplayConstrain.isChecked() && mImageWidth > 0) {
					final int width = Integer.parseInt(s.toString());
					final int height = width * mImageHeight / mImageWidth;
					if (height != Integer.parseInt(mEditTextDisplayHeight.getText().toString())) {
						mEditTextDisplayHeight.setText(String.valueOf(height));
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});
		mEditTextDisplayHeight = (EditText) layout.findViewById(R.id.et_display_height);
		mEditTextDisplayHeight.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mEditTextDisplayHeight.isFocused() && mCheckBoxDisplayConstrain.isChecked() && mImageHeight > 0) {
					final int height = Integer.parseInt(s.toString());
					final int width = height * mImageWidth / mImageHeight;
					if (width != Integer.parseInt(mEditTextDisplayWidth.getText().toString())) {
						mEditTextDisplayWidth.setText(String.valueOf(width));
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		mCheckBoxDisplayConstrain = (CheckBox) layout.findViewById(R.id.cb_display_constrain);
		mCheckBoxDisplayConstrain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked && mImageWidth > 0) {
					final int width = Integer.parseInt(mEditTextDisplayWidth.getText().toString());
					final int height = width * mImageHeight / mImageWidth;
					mEditTextDisplayHeight.setText(String.valueOf(height));
				}
			}
		});
		mButtonDisplayRestore = (Button) layout.findViewById(R.id.btn_display_restore);
		mButtonDisplayRestore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mEditTextDisplayWidth.setText(String.valueOf(mImageWidth));
				mEditTextDisplayHeight.setText(String.valueOf(mImageHeight));
			}
		});

		mButtonCrop = (Button) layout.findViewById(R.id.btn_crop);
		mButtonCrop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String src = mEditTextSrc.getText().toString();

				final Uri source = UriUtil.parseToUri(mContext, src, mContext.getPackageName() + PROVIDER_AUTHORITIES);
				if (source == null) {
					return;
				}

				final File destinationFile = new File(mImageFileDir, "crop_" + StringUtil.getDateFormat(new Date()) + IMAGE_FILE_SUFFIX);

				startCrop((Activity) mContext, source, destinationFile.getAbsolutePath());
			}
		});

		mButtonDraw = (Button) layout.findViewById(R.id.btn_draw);
		mButtonDraw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String src = mEditTextSrc.getText().toString();

				final Uri imageUri = UriUtil.parseToUri(mContext, src, mContext.getPackageName() + PROVIDER_AUTHORITIES);
				if (imageUri == null) {
					return;
				}

				final File destinationFile = new File(mImageFileDir, "draw_" + StringUtil.getDateFormat(new Date()) + IMAGE_FILE_SUFFIX);

				startDraw((Activity) mContext, imageUri, destinationFile.getAbsolutePath());
			}
		});

		mRadioGroup = (RadioGroup) layout.findViewById(R.id.rg_align);
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
		mRadioButtonAlignBottom = (RadioButton) layout.findViewById(R.id.rb_align_bottom);
		mRadioButtonAlignBaseline = (RadioButton) layout.findViewById(R.id.rb_align_baseline);
		mRadioButtonAlignCenter = (RadioButton) layout.findViewById(R.id.rb_align_center);
	}


	private ClickImageSpanDialogBuilder(Context context, int mediaType) {
		this(context, 0, mediaType);
	}

	private ClickImageSpanDialogBuilder(final Context context, int theme, int mediaType) {
        mContext = context;

		mMediaType = mediaType;

		if (mMediaType != 0) {
			mDefaultAudioVideoCoverImageFileName = "file:///android_asset/" + (mMediaType == 1 ? "video.png" : "audio.png");
		}

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_click_image_span_dialog, null);

		initView(layout);

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}

	@NonNull
	public static ClickImageSpanDialogBuilder with(Context context, int mediaType) {
		return new ClickImageSpanDialogBuilder(context, mediaType);
	}
	@NonNull
	public static ClickImageSpanDialogBuilder with(Context context, int theme, int mediaType) {
		return new ClickImageSpanDialogBuilder(context, theme, mediaType);
	}


	public AlertDialog build() {
		Context context = builder.getContext();

		return builder.create();
	}


	public ClickImageSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doPositiveAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ClickImageSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doPositiveAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ClickImageSpanDialogBuilder setNegativeButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNegativeAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ClickImageSpanDialogBuilder setNegativeButton(int textId, final OnClickListener onClickListener) {
		builder.setNegativeButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNegativeAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ClickImageSpanDialogBuilder setNeutralButton(int textId, final OnClickListener onClickListener) {
		builder.setNeutralButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNeutralAction(onClickListener, dialog);
			}
		});
		return this;
	}
	public ClickImageSpanDialogBuilder setNeutralButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setNeutralButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNeutralAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public void doPositiveAction(@NonNull OnClickListener onClickListener, DialogInterface dialog) {
		final String uri = mEditTextUri.getText().toString();
		final String src = mEditTextSrc.getText().toString();

		onClickListener.onClick(dialog,
				uri,
				src,
				Integer.parseInt(mEditTextDisplayWidth.getText().toString()),
				Integer.parseInt(mEditTextDisplayHeight.getText().toString()),
				mVerticalAlignment);
	}
	public void doNegativeAction(@NonNull OnClickListener onClickListener, DialogInterface dialog) {
		deleteOldUriFile();
		deleteOldSrcFile();
	}
	public void doNeutralAction(@NonNull OnClickListener onClickListener, DialogInterface dialog) {
		deleteOldUriFile();
		deleteOldSrcFile();

		onClickListener.onClick(dialog, null, null,0,0,0);
	}


	///[媒体选择器#Autio/Video媒体库]
	private void pickFromMedia() {
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.setType(mMediaType == 1 ? "video/*" : "audio/*")
				.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			((Activity) mContext).startActivityForResult(
					Intent.createChooser(intent, mContext.getString(mMediaType == 1 ? R.string.label_select_video : R.string.label_select_audio)),
					mMediaType == 1 ? REQUEST_CODE_PICK_FROM_VIDEO_MEDIA : REQUEST_CODE_PICK_FROM_AUDIO_MEDIA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext.getApplicationContext(), R.string.error_activity_not_found, Toast.LENGTH_SHORT).show();
		}
	}

	///[媒体选择器#Autio/Video媒体录制]
	private void pickFromRecorder() {
		final Intent intent = new Intent(mMediaType == 1 ? MediaStore.ACTION_VIDEO_CAPTURE : MediaStore.Audio.Media.RECORD_SOUND_ACTION);

		///MediaStore.EXTRA_OUTPUT：设置媒体文件的保存路径
		final File mediaFile = new File(mMediaType == 1 ? mVideoFileDir : mAudioFileDir,
				StringUtil.getDateFormat(new Date()) + (mMediaType == 1 ? VIDEO_FILE_SUFFIX : AUDIO_FILE_SUFFIX));
		final Uri mediaUri = UriUtil.getFileProviderUriFromFile(mContext, mediaFile, mContext.getPackageName() + PROVIDER_AUTHORITIES);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);

		//////??????如果是视频，还可以设置其它值：
		///MediaStore.EXTRA_VIDEO_QUALITY：设置视频录制的质量，0为低质量，1为高质量。
		///MediaStore.EXTRA_DURATION_LIMIT：设置视频最大允许录制的时长，单位为毫秒。
		///MediaStore.EXTRA_SIZE_LIMIT：指定视频最大允许的尺寸，单位为byte。

		///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}

		try {
			((Activity) mContext).startActivityForResult(intent,
					mMediaType == 1 ? REQUEST_CODE_PICK_FROM_VIDEO_RECORDER : REQUEST_CODE_PICK_FROM_AUDIO_RECORDER);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext.getApplicationContext(), R.string.error_activity_not_found, Toast.LENGTH_SHORT).show();
		}
	}

	///[图片选择器#相册图库]
	private void pickFromGallery() {
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*")
				.addCategory(Intent.CATEGORY_OPENABLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			///https://stackoverflow.com/questions/23385520/android-available-mime-types
			final String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png", "image/bmp", "image/gif"};//////??????相册不支持"image/bmp"
			intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
		}

		try {
			((Activity) mContext).startActivityForResult(Intent.createChooser(intent, mContext.getString(R.string.label_select_picture)),
					REQUEST_CODE_PICK_FROM_GALLERY);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext.getApplicationContext(), R.string.error_activity_not_found, Toast.LENGTH_SHORT).show();
		}
	}

	///[图片选择器#相机拍照]
	private File mCameraResultFile;
	private void pickFromCamera() {
		// create Intent to take a picture and return control to the calling application
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		///Android 11 (API 30)开始，只有预装的系统相机应用可以响应action.IMAGE_CAPTURE 等操作，
		///而且intent.resolveActivity(mContext.getPackageManager())会返回null
		///https://juejin.im/post/6860370635664261128
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
			// Ensure that there's a camera activity to handle the intent
			if (intent.resolveActivity(mContext.getPackageManager()) == null) {
				Toast.makeText(mContext.getApplicationContext(), R.string.error_system_camera_not_available, Toast.LENGTH_SHORT).show();
				return;
			}
		}

		///注意：系统相机拍摄的照片，如果不通过MediaStore.EXTRA_OUTPUT指定路径，data.getExtras().getParcelableExtra("data")只能得到Bitmap缩略图！
		///如果指定了保存路径，则照片保存到指定文件（此时，Intent返回null）
		///另外，不建议用uri！这种FileProvider内容提供者的uri很难获得File文件目录进行文件操作！
		///指定拍照路径时，先检查路径中的文件夹是否都存在，不存在时先创建文件夹再调用相机拍照；照片的命名中不要包含空格等特殊符号
		///https://www.jianshu.com/p/c1c2555e287c
		mCameraResultFile = new File(mImageFileDir, StringUtil.getDateFormat(new Date()) + IMAGE_FILE_SUFFIX);
		final Uri cameraResultUri = UriUtil.getFileProviderUriFromFile(mContext, mCameraResultFile, mContext.getPackageName() + PROVIDER_AUTHORITIES);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraResultUri);

		///因为没有使用cameraResultUri传递拍照结果图片目录，所以注释掉以下：
//		///如果Android N及以上，需要添加临时FileProvider的Uri读写权限
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//		}

		try {
			((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_PICK_FROM_CAMERA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext.getApplicationContext(), R.string.error_activity_not_found, Toast.LENGTH_SHORT).show();
		}
	}

    ///[裁剪/压缩#Yalantis/uCrop]https://github.com/Yalantis/uCrop
    private void startCrop(@NonNull Activity activity, @NonNull Uri source, @NonNull String destination) {
		///[FIX#NotFoundException: File res/drawable/ucrop_ic_cross.xml from drawable resource ID]
		///https://github.com/Yalantis/uCrop/issues/529
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

		final UCrop uCrop = UCrop.of(source, destination);

        uCrop.start(activity);
    }

    ///[手绘涂鸦#1993hzw/Doodle]https://github.com/1993hzw/Doodle
	private void startDraw(@NonNull Activity activity, @NonNull Uri imageUri, String savePath) {
        final DoodleParams params = new DoodleParams();
		params.mImageUri = imageUri;
		params.mSavePath = savePath;

		try {
			DoodleActivity.startActivityForResult(activity, params, REQUEST_CODE_DRAW);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext.getApplicationContext(), R.string.error_activity_not_found, Toast.LENGTH_SHORT).show();
		}
	}

	///[ClickImageSpanDialogBuilder#onActivityResult()]
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		///[媒体选择器#Video/Audio媒体库]
		if (requestCode == REQUEST_CODE_PICK_FROM_VIDEO_MEDIA || requestCode == REQUEST_CODE_PICK_FROM_AUDIO_MEDIA) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					final Uri selectedUri = data.getData();
					if (selectedUri != null) {
						mEditTextUri.setText(selectedUri.toString());

						if (requestCode == REQUEST_CODE_PICK_FROM_VIDEO_MEDIA) {
							///生成视频的第一帧图片
							final String videoCoverFileName = StringUtil.getDateFormat(new Date()) + "_cover" + IMAGE_FILE_SUFFIX;
							final File videoCoverFile = new File(mImageFileDir, videoCoverFileName);
							generateVideoCover(mContext, selectedUri, videoCoverFile, Bitmap.CompressFormat.JPEG, 90);

							mEditTextSrc.setText(videoCoverFile.getAbsolutePath());
							enableDeleteOldSrcFile = true;
						}
					} else {
						Toast.makeText(mContext.getApplicationContext(),
								mMediaType == 1 ? R.string.message_cannot_retrieve_video : R.string.message_cannot_retrieve_audio,
								Toast.LENGTH_SHORT).show();
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(mContext.getApplicationContext(),
						mMediaType == 1 ? R.string.message_video_select_cancelled : R.string.message_audio_select_cancelled,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext.getApplicationContext(),
						mMediaType == 1 ? R.string.message_video_select_failed : R.string.message_audio_select_failed, Toast.LENGTH_SHORT).show();
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
							///生成视频的第一帧图片
							final String videoCoverFileName = StringUtil.getDateFormat(new Date()) + "_cover" + IMAGE_FILE_SUFFIX;
							final File videoCoverFile = new File(mImageFileDir, videoCoverFileName);
							generateVideoCover(mContext, resultUri, videoCoverFile, Bitmap.CompressFormat.JPEG, 90);

							mEditTextSrc.setText(videoCoverFile.getAbsolutePath());
							enableDeleteOldSrcFile = true;
						}
					} else {
						Toast.makeText(mContext.getApplicationContext(),
								mMediaType == 1 ? R.string.message_cannot_retrieve_video : R.string.message_cannot_retrieve_audio,
								Toast.LENGTH_SHORT).show();
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(mContext.getApplicationContext(),
						mMediaType == 1 ? R.string.message_video_capture_cancelled : R.string.message_audio_capture_cancelled,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext.getApplicationContext(),
						mMediaType == 1 ? R.string.message_video_capture_failed : R.string.message_audio_capture_failed, Toast.LENGTH_SHORT).show();
			}
		}

		///[图片选择器#相册图库]
		else if (requestCode == REQUEST_CODE_PICK_FROM_GALLERY) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					final Uri selectedUri = data.getData();
					if (selectedUri != null && !TextUtils.equals(selectedUri.toString(), mEditTextSrc.getText())) {
						mEditTextSrc.setText(selectedUri.toString());
					} else {
						Toast.makeText(mContext.getApplicationContext(), R.string.message_cannot_retrieve_image, Toast.LENGTH_SHORT).show();
					}
				}
			} else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(mContext.getApplicationContext(), R.string.message_image_select_cancelled, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext.getApplicationContext(), R.string.message_image_select_failed, Toast.LENGTH_SHORT).show();
            }
        }

		///[图片选择器#相机拍照]
        else if (requestCode == REQUEST_CODE_PICK_FROM_CAMERA) {
			if (resultCode == RESULT_OK) {
				if (mCameraResultFile != null && !TextUtils.equals(mCameraResultFile.getAbsolutePath(), mEditTextSrc.getText())) {
					mEditTextSrc.setText(mCameraResultFile.getAbsolutePath());
					enableDeleteOldSrcFile = true;
				}
            } else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(mContext.getApplicationContext(), R.string.message_image_capture_cancelled, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext.getApplicationContext(), R.string.message_image_capture_failed, Toast.LENGTH_SHORT).show();
			}
		}

        ///[裁剪/压缩#Yalantis/uCrop]https://github.com/Yalantis/uCrop
        else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                final String resultString = UCrop.getOutput(data);
                if (!TextUtils.isEmpty(resultString) && !TextUtils.equals(resultString, mEditTextSrc.getText())) {
					mEditTextSrc.setText(resultString);
					enableDeleteOldSrcFile = true;
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                if (cropError != null) {
                    Toast.makeText(mContext.getApplicationContext(), cropError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        ///[手绘涂鸦#1993hzw/Doodle]https://github.com/1993hzw/Doodle
        else if (requestCode == REQUEST_CODE_DRAW) {
            if (data != null) {
				if (resultCode == DoodleActivity.RESULT_OK) {
					final String resultString = data.getStringExtra(DoodleActivity.KEY_IMAGE_PATH);
					if (!TextUtils.isEmpty(resultString) && !TextUtils.equals(resultString, mEditTextSrc.getText())) {
						mEditTextSrc.setText(resultString);
						enableDeleteOldSrcFile = true;
					}
				} else if (resultCode == DoodleActivity.RESULT_ERROR) {
					Toast.makeText(mContext.getApplicationContext(), R.string.message_image_draw_failed, Toast.LENGTH_SHORT).show();
				}
            }
        }
	}

	///删除与原始uri/src不同的文件
	private boolean enableDeleteOldUriFile = false;
	private boolean enableDeleteOldSrcFile = false;
	private void deleteOldUriFile() {
		if (enableDeleteOldUriFile && !TextUtils.isEmpty(mEditTextUri.getText()) && !TextUtils.equals(mInitialUri, mEditTextUri.getText())) {
			final Uri uri = Uri.parse(mEditTextUri.getText().toString());
			final String filePath = UriUtil.getFilePathFromUri(mContext, uri);
			deleteFile(filePath);
		}
	}
	private void deleteOldSrcFile() {
		if (enableDeleteOldSrcFile && !TextUtils.isEmpty(mEditTextSrc.getText()) && !TextUtils.equals(mInitialSrc, mEditTextSrc.getText())) {
			final Uri uri = Uri.parse(mEditTextSrc.getText().toString());
			final String filePath = UriUtil.getFilePathFromUri(mContext, uri);
			deleteFile(filePath);
		}
	}
	private void deleteFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return;
		}

		final File file = new File(filePath);
		if (file.isFile() && file.exists()) {
			if (!file.delete()) {
				if (DEBUG) Log.w("TAG", "Fail to delete file: " + file.getAbsolutePath());
			}
		}
	}

}