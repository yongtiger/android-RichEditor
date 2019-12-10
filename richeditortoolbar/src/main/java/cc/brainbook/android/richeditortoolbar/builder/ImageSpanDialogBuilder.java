package cc.brainbook.android.richeditortoolbar.builder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;

import cc.brainbook.android.richeditortoolbar.R;
import cc.brainbook.android.richeditortoolbar.util.FileUtil;
import cc.brainbook.android.richeditortoolbar.util.StringUtil;
import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleParams;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ImageSpanDialogBuilder {
	public static final int ALIGN_BOTTOM = 0;
	public static final int ALIGN_BASELINE = 1;
	public static final int ALIGN_CENTER = 2;
	public static final int DEFAULT_ALIGN = ALIGN_BOTTOM;

	private static final int REQUEST_CODE_PICK_FROM_GALLERY = 1;
	private static final int REQUEST_CODE_PICK_FROM_CAMERA = 2;
	private static final int REQUEST_CODE_DRAW = 3;

	private int mImageOverrideWidth = 1000;
	private int mImageOverrideHeight = 1000;
	private File mCachedOriginalImageFile;
	private File mCachedOldImageFile;
	private File mCachedImageFile;	///相机拍照、图片Crop剪切生成的图片文件
	private File mDestinationFile;	///图片Crop、Draw生成的目标文件
    private File mImageFilePath;	///ImageSpan存放图片文件的目录
    public ImageSpanDialogBuilder setImageFilePath(File imageFilePath) {
        mImageFilePath = imageFilePath;
		mImageFilePath.mkdirs();
        return this;
    }

	private int mVerticalAlignment;

	private int mImageWidth;
	private int mImageHeight;

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

	private AlertDialog.Builder builder;

	///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
	private Context mContext;

	private ImageSpanDialogBuilder(Context context) {
		this(context, 0);
	}

	private ImageSpanDialogBuilder(final Context context, int theme) {
        mContext = context;
		mImageFilePath = context.getExternalCacheDir();///设置ImageSpan存放图片文件的缺省目录

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.layout_image_span_dialog, null);

		mButtonPickFromGallery = (Button) layout.findViewById(R.id.btn_pickup_from_gallery);
		mButtonPickFromGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
				pickFromGallery(context);
			}
		});

		mButtonPickFromCamera = (Button) layout.findViewById(R.id.btn_pickup_from_camera);
		mButtonPickFromCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
				pickFromCamera(context);
			}
		});

		mEditTextSrc = (EditText) layout.findViewById(R.id.et_src);
		mEditTextSrc.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				final String src = s.toString();

				if (mCachedOldImageFile != null && !mCachedOldImageFile.equals(mCachedOriginalImageFile)) {
					mCachedOldImageFile.delete();
				}
				if (!TextUtils.isEmpty(src) && !StringUtil.isUrl(src)) {
					final File srcFile = new File(src);
					if (mImageFilePath.equals(srcFile.getParentFile())) {
						mCachedOldImageFile = srcFile;
					} else {
						mCachedOldImageFile = null;
					}
				} else {
					mCachedOldImageFile = null;
				}

				///Glide下载图片（使用已经缓存的图片）给imageView
				///https://muyangmin.github.io/glide-docs-cn/doc/getting-started.html
				//////??????placeholer（占位符）、error（错误符）、fallback（后备回调符）
				final RequestOptions options = new RequestOptions()
						.placeholder(R.drawable.ic_image_black_24dp); ///   .placeholder(new ColorDrawable(Color.BLACK))   // 或者可以直接使用ColorDrawable
//				Glide.with(context.getApplicationContext())
//						.load(src)
//						.apply(options)
//						.into(mImageViewPreview);

				///获取图片真正的宽高
				///https://www.jianshu.com/p/299b637afe7c
				Glide.with(context.getApplicationContext())
//						.asBitmap()//强制Glide返回一个Bitmap对象 //注意：在Glide 3中的语法是先load()再asBitmap()，而在Glide 4中是先asBitmap()再load()
						.load(src)
						.apply(options)

						.override(mImageOverrideWidth, mImageOverrideHeight) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
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
									///GifDrawable禁止Crop和Draw
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
				final Uri source;
                final String src = mEditTextSrc.getText().toString();
				final String imageFileName = FileUtil.generateImageFileName("jpg");
                if (StringUtil.isUrl(src)) {
					mCachedImageFile = new File(mImageFilePath, imageFileName);
                    FileUtil.saveDrawableToFile(mImageViewPreview.getDrawable(), mCachedImageFile, Bitmap.CompressFormat.JPEG, 100);
					source = FileUtil.getUriFromFile(mContext, mCachedImageFile);
                } else {
					source = Uri.parse("file://" + src);///[FIX#startCrop()#src]加前缀"file://"
				}

				mDestinationFile = new File(mImageFilePath, "crop_" + imageFileName);
                startCrop((Activity) mContext, source, Uri.fromFile(mDestinationFile));
            }
        });

        mButtonDraw = (Button) layout.findViewById(R.id.btn_draw);
        mButtonDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String imagePath;
                final String src = mEditTextSrc.getText().toString();
				final String imageFileName = FileUtil.generateImageFileName("jpg");
                if (StringUtil.isUrl(src)) {
					mCachedImageFile = new File(mImageFilePath, imageFileName);
					FileUtil.saveDrawableToFile(mImageViewPreview.getDrawable(), mCachedImageFile, Bitmap.CompressFormat.JPEG, 100);
                    imagePath = mCachedImageFile.getAbsolutePath();
                } else {
                    imagePath = src;
                }

				mDestinationFile = new File(mImageFilePath, "draw_" + imageFileName);
                startDraw((Activity) mContext, imagePath, mDestinationFile.getAbsolutePath());
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

		builder = new AlertDialog.Builder(context, theme);
		builder.setView(layout);
	}

	public static ImageSpanDialogBuilder with(Context context) {
		return new ImageSpanDialogBuilder(context);
	}

	public static ImageSpanDialogBuilder with(Context context, int theme) {
		return new ImageSpanDialogBuilder(context, theme);
	}

	public ImageSpanDialogBuilder setTitle(String title) {
		builder.setTitle(title);
		return this;
	}

	public ImageSpanDialogBuilder setTitle(int titleId) {
		builder.setTitle(titleId);
		return this;
	}

	private boolean isInitializing;
	public ImageSpanDialogBuilder initial(String src, int width, int height, int align, int imageOverrideWidth, int imageOverrideHeight) {
		isInitializing = true;
		mImageOverrideWidth = imageOverrideWidth;
		mImageOverrideHeight = imageOverrideHeight;

		if (!TextUtils.isEmpty(src) && !StringUtil.isUrl(src)) {
			final File srcFile = new File(src);
			if (mImageFilePath.equals(srcFile.getParentFile())) {
				mCachedOriginalImageFile = srcFile;
			}
		}

		mEditTextSrc.setText(src);

		mEditTextDisplayWidth.setText(String.valueOf(width));
		mEditTextDisplayHeight.setText(String.valueOf(height));

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

	public ImageSpanDialogBuilder setPositiveButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doPositiveAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ImageSpanDialogBuilder setPositiveButton(int textId, final OnClickListener onClickListener) {
		builder.setPositiveButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doPositiveAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ImageSpanDialogBuilder setNegativeButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNegativeAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ImageSpanDialogBuilder setNegativeButton(int textId, final OnClickListener onClickListener) {
		builder.setNegativeButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNegativeAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public ImageSpanDialogBuilder setNeutralButton(int textId, final OnClickListener onClickListener) {
		builder.setNeutralButton(textId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNeutralAction(onClickListener, dialog);
			}
		});
		return this;
	}
	public ImageSpanDialogBuilder setNeutralButton(CharSequence text, final OnClickListener onClickListener) {
		builder.setNeutralButton(text, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doNeutralAction(onClickListener, dialog);
			}
		});
		return this;
	}

	public AlertDialog build() {
		Context context = builder.getContext();

		return builder.create();
	}

	public interface OnClickListener {
		void onClick(DialogInterface d, String src, int width, int height, int align);
	}

	public void doPositiveAction(OnClickListener onClickListener, DialogInterface dialog) {
		final String src = mEditTextSrc.getText().toString();
		if (mCachedOriginalImageFile != null
				&& !TextUtils.isEmpty(src) && !StringUtil.isUrl(src)
				&& !mCachedOriginalImageFile.equals(new File(src))) {
			mCachedOriginalImageFile.delete();
		}

		onClickListener.onClick(dialog,
				src,
				Integer.parseInt(mEditTextDisplayWidth.getText().toString()),
				Integer.parseInt(mEditTextDisplayHeight.getText().toString()),
				mVerticalAlignment);
	}
	public void doNegativeAction(OnClickListener onClickListener, DialogInterface dialog) {
		if (mCachedOldImageFile != null) {
			mCachedOldImageFile.delete();
		}
	}
	public void doNeutralAction(OnClickListener onClickListener, DialogInterface dialog) {
		if (mCachedOldImageFile != null) {
			mCachedOldImageFile.delete();
		}
		if (mCachedOriginalImageFile != null) {
			mCachedOriginalImageFile.delete();
		}

		onClickListener.onClick(dialog, null,0,0,0);
	}

	///[图片选择器#相册图库]
	private void pickFromGallery(Context context) {
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
				.setType("image/*")
				.addCategory(Intent.CATEGORY_OPENABLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			///https://stackoverflow.com/questions/23385520/android-available-mime-types
			final String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png", "image/bmp", "image/gif"};//////??????相册不支持"image/bmp"
			intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
		}

		///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
//        final Activity activity = Util.getActivityFromContext(context);
//        if (activity != null) {
//            activity.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.label_select_picture)),
//                    REQUEST_CODE_PICK_FROM_GALLERY);
//        }
		((Activity) mContext).startActivityForResult(Intent.createChooser(intent, context.getString(R.string.label_select_picture)),
				REQUEST_CODE_PICK_FROM_GALLERY);
	}

	///[图片选择器#相机拍照]
	private void pickFromCamera(Context context) {
		// create Intent to take a picture and return control to the calling application
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Ensure that there's a camera activity to handle the intent
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			mCachedImageFile = new File(mImageFilePath, FileUtil.generateImageFileName("jpg"));
            final Uri imageUri = FileUtil.getUriFromFile(context, mCachedImageFile);
            // MediaStore.EXTRA_OUTPUT参数不设置时,系统会自动生成一个uri,但是只会返回一个缩略图
            // 返回图片在onActivityResult中通过以下代码获取
            // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

			///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
//			final Activity activity = Util.getActivityFromContext(context);
//            if (activity != null) {
//                activity.startActivityForResult(intent, REQUEST_CODE_PICK_FROM_CAMERA);
//            }
			((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_PICK_FROM_CAMERA);
		}
	}

    ///[裁剪/压缩#Yalantis/uCrop]https://github.com/Yalantis/uCrop
    private void startCrop(Activity activity, @NonNull Uri source, @NonNull Uri destination) {
		///[FIX#NotFoundException: File res/drawable/ucrop_ic_cross.xml from drawable resource ID]
		///https://github.com/Yalantis/uCrop/issues/529
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

		final UCrop uCrop = UCrop.of(source, destination);

        uCrop.start(activity);
    }

    ///[手绘涂鸦#1993hzw/Doodle]https://github.com/1993hzw/Doodle
	private void startDraw(Activity activity, String imagePath, String savePath) {
        final DoodleParams params = new DoodleParams(); // 涂鸦参数
		params.mImagePath = imagePath; // the file path of image
		params.mSavePath = savePath;
		DoodleActivity.startActivityForResult(activity, params, REQUEST_CODE_DRAW);
	}

	///[ImageSpanDialogBuilder#onActivityResult()]
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		///[图片选择器#相册图库]
		if (requestCode == REQUEST_CODE_PICK_FROM_GALLERY) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					final Uri selectedUri = data.getData();
					if (selectedUri != null) {
						mEditTextSrc.setText(FileUtils.getPath(mContext, selectedUri));
						return;
					} else {
						Toast.makeText(mContext.getApplicationContext(), R.string.message_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
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
				if (mCachedImageFile != null) {
					mEditTextSrc.setText(mCachedImageFile.getAbsolutePath());
					return;
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
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    mEditTextSrc.setText(FileUtils.getPath(mContext, resultUri));
					if (mCachedImageFile != null) {
						mCachedImageFile.delete();
						mCachedImageFile = null;
					}
                    return;
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
					mEditTextSrc.setText(data.getStringExtra(DoodleActivity.KEY_IMAGE_PATH));
					if (mCachedImageFile != null) {
						mCachedImageFile.delete();
						mCachedImageFile = null;
					}
					return;
				} else if (resultCode == DoodleActivity.RESULT_ERROR) {
					Toast.makeText(mContext.getApplicationContext(), R.string.message_image_draw_failed, Toast.LENGTH_SHORT).show();
				}
            }
        }

		if (mDestinationFile != null) {
			mDestinationFile.delete();
			mDestinationFile = null;
		}
		if (mCachedImageFile != null) {
			mCachedImageFile.delete();
			mCachedImageFile = null;
		}
	}

}