package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.brainbook.android.colorpicker.builder.ColorPickerClickListener;
import cc.brainbook.android.colorpicker.builder.ColorPickerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.bean.SpanBean;
import cc.brainbook.android.richeditortoolbar.bean.TextBean;
import cc.brainbook.android.richeditortoolbar.builder.BulletSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.ImageSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LeadingMarginSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LineDividerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.QuoteSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.URLSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;
import cc.brainbook.android.richeditortoolbar.helper.UndoRedoHelper;
import cc.brainbook.android.richeditortoolbar.span.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.CodeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomBulletSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.util.FileUtil;
import cc.brainbook.android.richeditortoolbar.util.PrefsUtil;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;
import cc.brainbook.android.richeditortoolbar.util.StringUtil;

import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;

public class RichEditorToolbar extends FlexboxLayout implements
        Drawable.Callback, View.OnClickListener, View.OnLongClickListener,
        RichEditText.OnSelectionChanged,
        RichEditText.SaveSpansCallback, RichEditText.LoadSpansCallback,
        UndoRedoHelper.OnPositionChangedListener {
    public static final String SHARED_PREFERENCES_NAME = "draft_preferences";
    public static final String SHARED_PREFERENCES_KEY_DRAFT_TEXT = "draft_text";
    public static final String CLIPBOARD_FILE_NAME = "rich_editor_clipboard_file";

    private HashMap<View, Class> mClassMap = new HashMap<>();

    private RichEditText mRichEditText;
    public RichEditText getRichEditText() {
        return mRichEditText;
    }


    /* ---------------- ///段落span（带参数）：Head ---------------- */
    private TextView mTextViewHead;

    /* ---------------- ///段落span（带初始化参数）：Quote ---------------- */
    private ImageView mImageViewQuote;
    private @ColorInt int mQuoteSpanColor = Color.parseColor("#DDDDDD");
    private int mQuoteSpanStripWidth = 16;
    private int mQuoteSpanGapWidth = 40;

    /* ---------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan ---------------- */
    private ImageView mImageViewAlignNormal;
    private ImageView mImageViewAlignCenter;
    private ImageView mImageViewAlignOpposite;

    /* ---------------- ///段落span（带初始化参数）：Bullet ---------------- */
    private ImageView mImageViewBullet;
    private @ColorInt int mBulletColor = Color.parseColor("#DDDDDD");
    private int mBulletSpanRadius = 16;
    private int mBulletSpanGapWidth = 40;

    /* ---------------- ///段落span（带初始化参数）：LeadingMargin ---------------- */
    private ImageView mImageViewLeadingMargin;
    private int mLeadingMarginSpanIndent = 40;

    /* ---------------- ///段落span：LineDivider ---------------- */
    private ImageView mImageViewLineDivider;
    private int mLineDividerSpanMarginTop = 50;
    private int mLineDividerSpanMarginBottom = 50;

    /* ---------------- ///字符span：Bold、Italic ---------------- */
    private ImageView mImageViewBold;
    private ImageView mImageViewItalic;

    /* ---------------- ///字符span：Underline、StrikeThrough、Subscript、Superscript ---------------- */
    private ImageView mImageViewUnderline;
    private ImageView mImageViewStrikeThrough;
    private ImageView mImageViewSubscript;
    private ImageView mImageViewSuperscript;

    /* ---------------- ///字符span（带参数）：Code ---------------- */
    private ImageView mImageViewCode;

    /* ---------------- ///字符span（带参数）：ForegroundColor、BackgroundColor ---------------- */
    private ImageView mImageViewForegroundColor;
    private ImageView mImageViewBackgroundColor;

    /* ---------------- ///字符span（带参数）：TypefaceFamily ---------------- */
    private TextView mTextViewTypefaceFamily;

    /* ---------------- ///字符span（带参数）：AbsoluteSize ---------------- */
    private TextView mTextViewAbsoluteSize;

    /* ---------------- ///字符span（带参数）：RelativeSize ---------------- */
    private TextView mTextViewRelativeSize;

    /* ---------------- ///字符span（带参数）：ScaleX ---------------- */
    private TextView mTextViewScaleX;

    /* ---------------- ///字符span（带参数）：URL ---------------- */
    private ImageView mImageViewURL;

    /* ---------------- ///字符span（带参数）：Image ---------------- */
    private ImageView mImageViewImage;
    private CustomImageSpan imagePlaceholderSpan = null;///避免产生重复span！
    private ImageSpanDialogBuilder imageSpanDialogBuilder;

    private File mImageFilePath;  ///ImageSpan存放图片文件的目录，比如相机拍照、图片Crop剪切生成的图片文件
    public void setImageFilePath(File imageFilePath) {
        mImageFilePath = imageFilePath;
    }
    private int mImageOverrideWidth = 1000;
    private int mImageOverrideHeight = 1000;
    public void setImageOverrideWidth(int imageOverrideWidth) {
        mImageOverrideWidth = imageOverrideWidth;
    }
    public void setImageOverrideHeight(int imageOverrideHeight) {
        mImageOverrideHeight = imageOverrideHeight;
    }

    ///[ImageSpan#ImageSpanDialogBuilder#onActivityResult()]
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (imageSpanDialogBuilder != null) {
            imageSpanDialogBuilder.onActivityResult(requestCode, resultCode, data);
        }
    }

    ///[ImageSpan#Glide#loadImage()]
    private void loadImage(final Editable editable,
                           final String viewTagSrc,
                           final int viewTagWidth,
                           final int viewTagHeight,
                           final int viewTagAlign,
                           final int start, final int end) {
        ///Glide下载图片（使用已经缓存的图片）给imageView
        ///https://muyangmin.github.io/glide-docs-cn/doc/getting-started.html
        //////??????placeholer（占位符）、error（错误符）、fallback（后备回调符）
        final RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_image_black_24dp); ///   .placeholder(new ColorDrawable(Color.BLACK))   // 或者可以直接使用ColorDrawable
        Glide.with(getContext())
                .load(viewTagSrc)
                .apply(options)
                .override(mImageOverrideWidth, mImageOverrideHeight) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
//                .centerCrop() // this cropping technique scales the image so that it fills the requested bounds and then crops the extra.
//                .fitCenter()    ///fitCenter()会缩放图片让两边都相等或小于ImageView的所需求的边框。图片会被完整显示，可能不能完全填充整个ImageView。
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {	///placeholder
                        if (placeholder != null) {
                            ///注意：Drawable必须设置Bounds才能显示
                            placeholder.setBounds(0, 0, viewTagWidth, viewTagHeight);
                            imagePlaceholderSpan = new CustomImageSpan(placeholder, viewTagSrc, viewTagAlign);///避免产生重复span！
                            editable.setSpan(imagePlaceholderSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ///避免产生重复span！
                        if (imagePlaceholderSpan != null) {
                            editable.removeSpan(imagePlaceholderSpan);
                        }

                        ///注意：Drawable必须设置Bounds才能显示
                        resource.setBounds(0, 0, viewTagWidth, viewTagHeight);
                        editable.setSpan(new CustomImageSpan(resource, viewTagSrc, viewTagAlign), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        ///[ImageSpan#Glide#GifDrawable]
                        ///https://muyangmin.github.io/glide-docs-cn/doc/targets.html
                        if (resource instanceof GifDrawable) {
                            ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                            ((GifDrawable) resource).start();

                            ///For animated GIFs inside span you need to assign bounds and callback (which is TextView holding that span) to GifDrawable
                            ///https://github.com/koral--/android-gif-drawable/issues/516
                            resource.setCallback(RichEditorToolbar.this);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }

    ///[ImageSpan#Glide#GifDrawable]
    ///注意：TextView在实际使用中可能不由EditText产生并赋值，所以需要单独另行处理Glide#GifDrawable的Callback
    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        final Editable editable = mRichEditText.getText();

        ///注意：实测此方法不闪烁！
        ///https://www.cnblogs.com/mfrbuaa/p/5045666.html
        final CustomImageSpan imageSpan = SpanUtil.getImageSpanByDrawable(editable, drawable);
        if (imageSpan != null) {
            if (!TextUtils.isEmpty(editable)) {
                final int spanStart = editable.getSpanStart(imageSpan);
                final int spanEnd = editable.getSpanEnd(imageSpan);
                final int spanFlags = editable.getSpanFlags(imageSpan);

                ///注意：不必先removeSpan()！只setSpan()就能实现局部刷新EditText，以便让Gif动起来
//                editable.removeSpan(imageSpan);
                editable.setSpan(imageSpan, spanStart, spanEnd, spanFlags);

                if (!TextUtils.isEmpty(mTextViewPreviewText.getText())) {
//                    mTextViewPreviewText.invalidateDrawable(drawable);//////??????无效！
                    mTextViewPreviewText.invalidate();
                }
            }
        } else {
            super.invalidateDrawable(drawable);
        }
    }

    /* ---------------- ///[Preview] ---------------- */
    private ImageView mImageViewPreview;

    private TextView mTextViewPreviewText;
    public void setPreviewText(TextView textView) {
        mTextViewPreviewText = textView;
    }

    private boolean enableSelectionChange = true;
    private void updatePreview() {
        if (mTextViewPreviewText.getVisibility() == VISIBLE) {
            ///[enableSelectionChange]禁止onSelectionChanged()
            ///注意：mTextViewPreviewText.setText()会引起mRichEditText#onSelectionChanged()，从而造成无selection单光标时切换toolbar按钮失效！
            enableSelectionChange = false;
            mTextViewPreviewText.setText(mRichEditText.getText());
            enableSelectionChange = true;
        }
    }

    /* ---------------- ///[清除样式] ---------------- */
    private ImageView mImageViewClearSpans;

    /* ---------------- ///[草稿Draft] ---------------- */
    private ImageView mImageViewSaveDraft;
    private ImageView mImageViewRestoreDraft;
    private ImageView mImageViewClearDraft;

    private boolean checkDraft() {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        final String draftText = sharedPreferences.getString(SHARED_PREFERENCES_KEY_DRAFT_TEXT, null);
        final boolean hasDraft = !TextUtils.isEmpty(draftText);
        mImageViewRestoreDraft.setEnabled(hasDraft);
        mImageViewRestoreDraft.setSelected(hasDraft);
        mImageViewClearDraft.setEnabled(hasDraft);
        return hasDraft;
    }

    /* ---------------- ///[TextContextMenu#Clipboard] ---------------- */
    ///保存spans到进程App共享空间，因此不建议用SharedPreferences
    ///Environment.getDataDirectory() Permission denied
    ///在/data文件夹进行操作是不被允许的!
    ///能操作文件夹只有两个地方：
    ///1.sdcard
    ///2./data/<package_name>/files/
    ///参考：docs/guide/topics/data/data-storage.html#filesExternal
//    private File mClipboardFile = new File(Environment.getDataDirectory() + File.separator + CLIPBOARD_FILE_NAME);
    private File mClipboardFile = new File(Environment.getExternalStorageDirectory() + File.separator + CLIPBOARD_FILE_NAME);

    @Override
    public void saveSpans(Editable editable, int selectionStart, int selectionEnd) {
        ///保存spans到进程App共享空间
        ///注意：由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans，而且应该保存到进程App共享空间！
        try {
            final byte[] bytes = RichEditorToolbarHelper.saveSpans(mClassMap, editable, selectionStart, selectionEnd, true);
            FileUtil.writeFile(mClipboardFile, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadSpans(Editable editable) {
        ///从进程App共享空间恢复spans
        try {
            final byte[] bytes = FileUtil.readFile(mClipboardFile);
            if (bytes == null) {
                return;
            }

            RichEditorToolbarHelper.loadSpans(editable, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------------- ///[Undo/Redo] ---------------- */
    private ImageView mImageViewUndo;
    private ImageView mImageViewRedo;
    private ImageView mImageViewSave;

    private UndoRedoHelper mUndoRedoHelper = new UndoRedoHelper(this);

    public void initUndoRedo() {
        mUndoRedoHelper.clearHistory();

        final Editable editable = mRichEditText.getText();
        mUndoRedoHelper.addHistory(UndoRedoHelper.INIT_ACTION, -1, null, null,
                RichEditorToolbarHelper.saveSpans(mClassMap, editable, 0, editable.length(), false));
    }

    public void setHistorySize(int historySize) {
        mUndoRedoHelper.setHistorySize(historySize);
    }

    @Override
    public void onPositionChangedListener(int position, UndoRedoHelper.Action action, boolean isSetSpans, boolean isCanUndo, boolean isCanRedo, boolean isSavedPosition) {
        mImageViewUndo.setSelected(isCanUndo);
        mImageViewUndo.setEnabled(isCanUndo);
        mImageViewRedo.setSelected(isCanRedo);
        mImageViewRedo.setEnabled(isCanRedo);
        mImageViewSave.setSelected(!isSavedPosition);
        mImageViewSave.setEnabled(!isSavedPosition);

        if (isSetSpans && action != null) {
            ///注意：清除原有的span，比如BoldSpan的父类StyleSpan
            ///注意：必须保证selectionChanged()不被执行！否则死循环！
//            mRichEditText.getText().clearSpans(); ///[FIX#误删除了其它有用的spans！]
            SpanUtil.clearAllSpans(mClassMap, mRichEditText.getText());

            RichEditorToolbarHelper.loadSpans(mRichEditText.getText(), action.getBytes());
        }
    }

    /**
     * Is undo/redo being performed? This member signals if an undo/redo
     * operation is currently being performed. Changes in the text during
     * undo/redo are not recorded because it would mess up the undo history.
     */
    public boolean isUndoOrRedo = false;


    /* ------------------------------------------------ */
    ///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
    private Context mContext;

    public RichEditorToolbar(Context context) {
        super(context);
        init(context);
    }

    public RichEditorToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RichEditorToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mContext = context;

        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);

        LayoutInflater.from(mContext).inflate(R.layout.layout_tool_bar, this, true);


        /* -------------- ///段落span（带参数）：Head --------------- */
        mTextViewHead = (TextView) findViewById(R.id.tv_head);
        mTextViewHead.setOnClickListener(this);
        mClassMap.put(mTextViewHead, HeadSpan.class);

        /* -------------- ///段落span（带初始化参数）：Quote --------------- */
        mImageViewQuote = (ImageView) findViewById(R.id.iv_quote);
        mImageViewQuote.setOnClickListener(this);
        mImageViewQuote.setOnLongClickListener(this);
        mClassMap.put(mImageViewQuote, CustomQuoteSpan.class);

        /* -------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan --------------- */
        mImageViewAlignNormal = (ImageView) findViewById(R.id.iv_align_normal);
        mImageViewAlignNormal.setOnClickListener(this);
        mClassMap.put(mImageViewAlignNormal, AlignNormalSpan.class);

        mImageViewAlignCenter = (ImageView) findViewById(R.id.iv_align_center);
        mImageViewAlignCenter.setOnClickListener(this);
        mClassMap.put(mImageViewAlignCenter, AlignCenterSpan.class);

        mImageViewAlignOpposite = (ImageView) findViewById(R.id.iv_align_opposite);
        mImageViewAlignOpposite.setOnClickListener(this);
        mClassMap.put(mImageViewAlignOpposite, AlignOppositeSpan.class);

        /* -------------- ///段落span（带初始化参数）：Bullet --------------- */
        mImageViewBullet = (ImageView) findViewById(R.id.iv_bullet);
        mImageViewBullet.setOnClickListener(this);
        mImageViewBullet.setOnLongClickListener(this);
        mClassMap.put(mImageViewBullet, CustomBulletSpan.class);

        /* -------------- ///段落span（带初始化参数）：LeadingMargin --------------- */
        mImageViewLeadingMargin = (ImageView) findViewById(R.id.iv_leading_margin);
        mImageViewLeadingMargin.setOnClickListener(this);
        mImageViewLeadingMargin.setOnLongClickListener(this);
        mClassMap.put(mImageViewLeadingMargin, LeadingMarginSpan.Standard.class);

        /* -------------- ///段落span：LineDivider --------------- */
        mImageViewLineDivider = (ImageView) findViewById(R.id.iv_line_divider);
        mImageViewLineDivider.setOnClickListener(this);
        mImageViewLineDivider.setOnLongClickListener(this);
        mClassMap.put(mImageViewLineDivider, LineDividerSpan.class);

        /* -------------- ///字符span：Bold、Italic --------------- */
        mImageViewBold = (ImageView) findViewById(R.id.iv_bold);
        mImageViewBold.setOnClickListener(this);
        mClassMap.put(mImageViewBold, BoldSpan.class);

        mImageViewItalic = (ImageView) findViewById(R.id.iv_italic);
        mImageViewItalic.setOnClickListener(this);
        mClassMap.put(mImageViewItalic, ItalicSpan.class);

        /* ------------ ///字符span：Underline、StrikeThrough、Subscript、Superscript ------------ */
        mImageViewUnderline = (ImageView) findViewById(R.id.iv_underline);
        mImageViewUnderline.setOnClickListener(this);
        mClassMap.put(mImageViewUnderline, CustomUnderlineSpan.class);

        mImageViewStrikeThrough = (ImageView) findViewById(R.id.iv_strikethrough);
        mImageViewStrikeThrough.setOnClickListener(this);
        mClassMap.put(mImageViewStrikeThrough, StrikethroughSpan.class);

        mImageViewSuperscript = (ImageView) findViewById(R.id.iv_superscript);
        mImageViewSuperscript.setOnClickListener(this);
        mClassMap.put(mImageViewSuperscript, SuperscriptSpan.class);

        mImageViewSubscript = (ImageView) findViewById(R.id.iv_subscript);
        mImageViewSubscript.setOnClickListener(this);
        mClassMap.put(mImageViewSubscript, SubscriptSpan.class);

        /* -------------- ///字符span（带参数）：Code --------------- */
        mImageViewCode = (ImageView) findViewById(R.id.iv_code);
        mImageViewCode.setOnClickListener(this);
        mClassMap.put(mImageViewCode, CodeSpan.class);

        /* -------------- ///字符span（带参数）：ForegroundColor、BackgroundColor --------------- */
        mImageViewForegroundColor = (ImageView) findViewById(R.id.iv_foreground_color);
        mImageViewForegroundColor.setOnClickListener(this);
        mClassMap.put(mImageViewForegroundColor, ForegroundColorSpan.class);

        mImageViewBackgroundColor = (ImageView) findViewById(R.id.iv_background_color);
        mImageViewBackgroundColor.setOnClickListener(this);
        mClassMap.put(mImageViewBackgroundColor, BackgroundColorSpan.class);

        /* -------------- ///字符span（带参数）：TypefaceFamily --------------- */
        mTextViewTypefaceFamily = (TextView) findViewById(R.id.tv_typeface_family);
        mTextViewTypefaceFamily.setOnClickListener(this);
        mClassMap.put(mTextViewTypefaceFamily, TypefaceSpan.class);

        /* -------------- ///字符span（带参数）：AbsoluteSize --------------- */
        mTextViewAbsoluteSize = (TextView) findViewById(R.id.tv_absolute_size);
        mTextViewAbsoluteSize.setOnClickListener(this);
        mClassMap.put(mTextViewAbsoluteSize, AbsoluteSizeSpan.class);

        /* -------------- ///字符span（带参数）：RelativeSize --------------- */
        mTextViewRelativeSize = (TextView) findViewById(R.id.tv_relative_size);
        mTextViewRelativeSize.setOnClickListener(this);
        mClassMap.put(mTextViewRelativeSize, RelativeSizeSpan.class);

        /* -------------- ///字符span（带参数）：ScaleX --------------- */
        mTextViewScaleX = (TextView) findViewById(R.id.tv_scale_x);
        mTextViewScaleX.setOnClickListener(this);
        mClassMap.put(mTextViewScaleX, ScaleXSpan.class);

        /* -------------- ///字符span（带参数）：URL --------------- */
        mImageViewURL = (ImageView) findViewById(R.id.iv_url);
        mImageViewURL.setOnClickListener(this);
        mClassMap.put(mImageViewURL, URLSpan.class);

        /* -------------- ///字符span（带参数）：Image --------------- */
        mImageViewImage = (ImageView) findViewById(R.id.iv_image);
        mImageViewImage.setOnClickListener(this);
        mClassMap.put(mImageViewImage, CustomImageSpan.class);

        /* -------------- ///[Preview] --------------- */
        mImageViewPreview = (ImageView) findViewById(R.id.iv_preview);
        mImageViewPreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());

                if (view.isSelected()) {
                    mRichEditText.setVisibility(GONE);
                    mTextViewPreviewText.setVisibility(VISIBLE);
                    updatePreview();
                } else {
                    mRichEditText.setVisibility(VISIBLE);
                    mTextViewPreviewText.setVisibility(GONE);
                    mTextViewPreviewText.setText(null);
                }
            }
        });

        /* -------------- ///[清除样式] --------------- */
        mImageViewClearSpans = (ImageView) findViewById(R.id.iv_clear_spans);
        mImageViewClearSpans.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Editable editable = mRichEditText.getText();

                //        final int selectionStart = mRichEditText.getSelectionStart();
                //        final int selectionEnd = mRichEditText.getSelectionEnd();
                final int selectionStart = Selection.getSelectionStart(editable);
                final int selectionEnd = Selection.getSelectionEnd(editable);
                if (selectionStart == -1 || selectionEnd == -1 || selectionStart == selectionEnd) {
                    return;
                }

                clearParagraphSpans(selectionStart, selectionEnd);
                clearCharacterSpans(selectionStart, selectionEnd);

                ///[Preview]
                updatePreview();

                ///[Undo/Redo]
                mUndoRedoHelper.addHistory(UndoRedoHelper.SPANS_CLEARED_ACTION, -1, null, null,
                        RichEditorToolbarHelper.saveSpans(mClassMap, editable, 0, editable.length(), false));
            }
        });

        /* -------------- ///[草稿Draft] --------------- */
        mImageViewSaveDraft = (ImageView) findViewById(R.id.iv_save_draft);
        mImageViewSaveDraft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable editable = mRichEditText.getText();

                final byte[] bytes = RichEditorToolbarHelper.saveSpans(mClassMap, editable, 0, editable.length(), true);
                PrefsUtil.putString(mContext, SHARED_PREFERENCES_NAME, SHARED_PREFERENCES_KEY_DRAFT_TEXT, Base64.encodeToString(bytes, 0));

                if (checkDraft()) {
                    Toast.makeText(mContext.getApplicationContext(), R.string.save_draft_successful, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), R.string.save_draft_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mImageViewRestoreDraft = (ImageView) findViewById(R.id.iv_restore_draft);
        mImageViewRestoreDraft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String draftText = PrefsUtil.getString(mContext, SHARED_PREFERENCES_NAME, SHARED_PREFERENCES_KEY_DRAFT_TEXT, null);
                if (TextUtils.isEmpty(draftText)) {
                    return;
                }

                final TextBean textBean = RichEditorToolbarHelper.createTextBean(Base64.decode(draftText, Base64.DEFAULT));
                if (textBean != null) {
                    final String beforeChange = mRichEditText.getText().toString();

                    isUndoOrRedo = true;
                    mRichEditText.setText(textBean.getText());
                    isUndoOrRedo = false;

                    final Editable editable = mRichEditText.getText();
                    final List<SpanBean> spanBeans = textBean.getSpans();
                    RichEditorToolbarHelper.setSpanFromSpanBeans(spanBeans, editable);

                    Selection.setSelection(editable, 0);

                    ///[Undo/Redo]
                    mUndoRedoHelper.addHistory(UndoRedoHelper.DRAFT_RESTORED_ACTION, 0, beforeChange, editable.toString(),
                            RichEditorToolbarHelper.saveSpans(mClassMap, editable, 0, editable.length(), false));

                    Toast.makeText(mContext.getApplicationContext(), R.string.restore_draft_successful, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mImageViewClearDraft = (ImageView) findViewById(R.id.iv_clear_draft);
        mImageViewClearDraft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefsUtil.clear(mContext, SHARED_PREFERENCES_NAME);

                if (!checkDraft()) {
                    Toast.makeText(mContext.getApplicationContext(), R.string.clear_draft_successful, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), R.string.clear_draft_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ///初始化时检查有无草稿Draft
        if (checkDraft()) {
            Toast.makeText(mContext.getApplicationContext(), R.string.has_draft, Toast.LENGTH_SHORT).show();
        }

        /* ------------------- ///[Undo/Redo] ------------------- */
        mImageViewUndo = (ImageView) findViewById(R.id.iv_undo);
        mImageViewUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndoRedoHelper.undo();
            }
        });
        mImageViewRedo = (ImageView) findViewById(R.id.iv_redo);
        mImageViewRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndoRedoHelper.redo();
            }
        });
        mImageViewSave = (ImageView) findViewById(R.id.iv_save);
        mImageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndoRedoHelper.resetSavedPosition();
                mImageViewSave.setSelected(false);
                mImageViewSave.setEnabled(false);
            }
        });
    }

    public void setupEditText(RichEditText richEditText) {
        mRichEditText = richEditText;
        mRichEditText.addTextChangedListener(new RichTextWatcher());
        mRichEditText.setOnSelectionChanged(this);
        mRichEditText.setSaveSpansCallback(this);
        mRichEditText.setLoadSpansCallback(this);

        ///[Undo/Redo]初始化时设置Undo/Redo各按钮的状态
        initUndoRedo();
    }

    private boolean isParagraphStyle(View view) {
        return view == mTextViewHead
                || view == mImageViewQuote
                || view == mImageViewAlignNormal
                || view == mImageViewAlignCenter
                || view == mImageViewAlignOpposite
                || view == mImageViewBullet
                || view == mImageViewLeadingMargin
                || view == mImageViewLineDivider;
    }
    private boolean isCharacterStyle(View view) {
        return view == mImageViewBold
                || view == mImageViewItalic
                || view == mImageViewUnderline
                || view == mImageViewStrikeThrough
                || view == mImageViewSuperscript
                || view == mImageViewSubscript
                || view == mImageViewURL
                || view == mImageViewImage
                || view == mImageViewCode
                || view == mImageViewForegroundColor
                || view == mImageViewBackgroundColor
                || view == mTextViewTypefaceFamily
                || view == mTextViewAbsoluteSize
                || view == mTextViewRelativeSize
                || view == mTextViewScaleX;
    }
    private boolean isBlockCharacterStyle(View view) {
        return view == mImageViewURL
                || view == mImageViewImage;
    }
    private int getActionId(View view) {
        if (view == mImageViewBold) {
            return UndoRedoHelper.BOLD_SPAN_CHANGED_ACTION;
//        } else if (view == mImageViewBold) {
//            /// todo ...
        } else {
            return -1;
        }
    }

    private <T> void updateParagraphView(View view, Class<T> clazz, Editable editable, int start, int end) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果span与当前行首尾相同，则select
            if (spanStart == start && spanEnd == end) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                }

                ///段落span（带参数）：Head
                if (clazz == HeadSpan.class) {
                    final String head = ((HeadSpan) span).getHead();
                    view.setTag(head);
                    if (!head.equals(((TextView) view).getText().toString())) {
                        ((TextView) view).setText(head);
                    }
                }

                ///注意：找到第一个就退出，不必继续找了。因为getFilteredSpans()返回的是按照span起始位置从小到大排序后的spans
                return;
            }
        }

        if (view.isSelected()) {
            view.setSelected(false);
        }

        view.setTag(null);

        ///设置为缺省文字
        ///段落span（带参数）：Head
        if (clazz == HeadSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.head));
        }
    }
    private <T> void updateCharacterStyleView(View view, Class<T> clazz, Editable editable, int start, int end) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果不是单光标、或者span在光标区间外
            ///如果isBlockCharacterStyle为false，加上光标尾等于span尾
            if (start != end || spanStart < start && (end < spanEnd || !isBlockCharacterStyle(view) && end == spanEnd)) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                }

                ///字符span（带参数）：URL
                if (clazz == URLSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
                        final String text = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                        final String url = ((URLSpan) span).getURL();
                        view.setTag(R.id.url_text, text);
                        view.setTag(R.id.url_url, url);
                    } else {
                        view.setTag(R.id.url_text, null);
                        view.setTag(R.id.url_url, null);
                    }
                }
                ///字符span（带参数）：Image
                else if (clazz == CustomImageSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
                        final String text = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                        final String src = ((CustomImageSpan) span).getSource();

                        ///从text中解析出width\height\align
                        final String strWidth = StringUtil.getParameter(text, "width=", " ");
                        final String strHeight = StringUtil.getParameter(text, "height=", " ");
                        final String strAlign = StringUtil.getParameter(text, "align=", "]");
                        final int width = strWidth == null ? 0 : Integer.parseInt(strWidth);
                        final int height = strHeight == null ? 0 : Integer.parseInt(strHeight);
                        final int align = strAlign == null ? ImageSpanDialogBuilder.DEFAULT_ALIGN : Integer.parseInt(strAlign);

                        view.setTag(R.id.image_text, text);
                        view.setTag(R.id.image_src, src);
                        view.setTag(R.id.image_width, width);
                        view.setTag(R.id.image_height, height);
                        view.setTag(R.id.image_align, align);
                    } else {
                        view.setTag(R.id.image_text, null);
                        view.setTag(R.id.image_src, null);
                        view.setTag(R.id.image_width, null);
                        view.setTag(R.id.image_height, null);
                        view.setTag(R.id.image_align, null);
                    }
                }
                ///字符span（带参数）：ForegroundColor、BackgroundColor
                else if (clazz == ForegroundColorSpan.class) {
                    @ColorInt final int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
                    view.setBackgroundColor(foregroundColor);
                } else if (clazz == BackgroundColorSpan.class) {
                    @ColorInt final int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
                    view.setBackgroundColor(backgroundColor);
                }
                ///字符span（带参数）：TypefaceFamily
                else if (clazz == TypefaceSpan.class) {
                    final String family = ((TypefaceSpan) span).getFamily();
                    view.setTag(family);
                    ((TextView) view).setText(family);
                }
                ///字符span（带参数）：AbsoluteSize
                else if (clazz == AbsoluteSizeSpan.class) {
                    final int size = ((AbsoluteSizeSpan) span).getSize();
                    view.setTag(size);
                    ((TextView) view).setText(String.valueOf(size));
                }
                ///字符span（带参数）：RelativeSize
                else if (clazz == RelativeSizeSpan.class) {
                    final float sizeChange = ((RelativeSizeSpan) span).getSizeChange();
                    view.setTag(sizeChange);
                    ((TextView) view).setText(String.valueOf(sizeChange));
                }
                ///字符span（带参数）：ScaleX
                else if (clazz == ScaleXSpan.class) {
                    final float scaleX = ((ScaleXSpan) span).getScaleX();
                    view.setTag(scaleX);
                    ((TextView) view).setText(String.valueOf(scaleX));
                }

                ///注意：找到第一个就退出，不必继续找了。因为getFilteredSpans()返回的是按照span起始位置从小到大排序后的spans
                return;
            }
        }

        if (view.isSelected()) {
            view.setSelected(false);
        }

        ///字符span（带参数）：URL
        if (clazz == URLSpan.class) {
            ///初始化对话框：无、单选、多选，只有单选时才初始化对话框
            if (start < end) {
                final String text = String.valueOf(editable.toString().toCharArray(), start, end - start);
                view.setTag(R.id.url_text, text);///以选中的文本作为url_text
                view.setTag(R.id.url_url, null);
            } else {
                view.setTag(R.id.url_text, null);
                view.setTag(R.id.url_url, null);
            }
        }
        ///字符span（带参数）：Image
        else if (clazz == CustomImageSpan.class) {
            view.setTag(R.id.image_text, null);
            view.setTag(R.id.image_src, null);
            view.setTag(R.id.image_width, null);
            view.setTag(R.id.image_height, null);
            view.setTag(R.id.image_align, null);
        }
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == ForegroundColorSpan.class || clazz == BackgroundColorSpan.class) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        ///字符span（带参数）：TypefaceFamily
        else if (clazz == TypefaceSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.font_family));
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.absolute_size));
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.relative_size));
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.scale_x));
        }

        else {
            view.setTag(null);
        }
    }

    private <T> void newParagraphStyleSpan(View view, Class<T> clazz, Editable editable, int start, int end) {
        ///添加新span
        Object newSpan = null;

        ///段落span（带初始化参数）：Quote
        if (clazz == CustomQuoteSpan.class) {
//            newSpan = new QuoteSpan(Color.GREEN);
//            newSpan = new QuoteSpan(Color.GREEN, 20, 40); ///Call requires API level 28 (current min is 15)
            newSpan = new CustomQuoteSpan(mQuoteSpanColor, mQuoteSpanStripWidth, mQuoteSpanGapWidth);
        }
        ///段落span（带初始化参数）：Bullet
        else if (clazz == CustomBulletSpan.class) {
//            newSpan = new BulletSpan(Color.GREEN);
//            newSpan = new BulletSpan(40, Color.GREEN, 20); ///Call requires API level 28 (current min is 15)
            newSpan = new CustomBulletSpan(mBulletSpanGapWidth, mBulletColor, mBulletSpanRadius);
        }
        ///段落span（带初始化参数）：LeadingMargin
        else if (clazz == LeadingMarginSpan.Standard.class) {
            newSpan = new LeadingMarginSpan.Standard(mLeadingMarginSpanIndent);
//            newSpan = new LeadingMarginSpan.LeadingMarginSpan2.Standard(first, rest); //////??????
        }
        ///段落span：LineDivider
        else if (clazz == LineDividerSpan.class) {
            newSpan = new LineDividerSpan(mLineDividerSpanMarginTop, mLineDividerSpanMarginBottom, new LineDividerSpan.DrawBackgroundCallback() {
                @Override
                public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
                    c.drawLine(left, (top + bottom) / 2, right, (top + bottom) / 2, p);    ///画直线
                }
            });
        }
        ///段落span（带参数）：Head
        else if (clazz == HeadSpan.class) {
            final String viewTagHead = (String) view.getTag();
            newSpan = new HeadSpan(viewTagHead);
        }

        else {
            try {
                newSpan = clazz.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        if (newSpan != null) {
            editable.setSpan(newSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }
    private <T> void newCharacterStyleSpanByCompareSpanOrViewParameter(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        ///添加新span
        Object newSpan = null;

        ///字符span（带参数）：ForegroundColor、BackgroundColor
        if (clazz == ForegroundColorSpan.class) {
            @ColorInt final int foregroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                foregroundColor = colorDrawable.getColor();
            } else {
                foregroundColor = ((ForegroundColorSpan) compareSpan).getForegroundColor();
            }
            newSpan = new ForegroundColorSpan(foregroundColor);
        } else if (clazz == BackgroundColorSpan.class) {
            @ColorInt final int backgroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                backgroundColor = colorDrawable.getColor();
            } else {
                backgroundColor = ((BackgroundColorSpan) compareSpan).getBackgroundColor();
            }
            newSpan = new BackgroundColorSpan(backgroundColor);
        }
        ///字符span（带参数）：TypefaceFamily
        else if (clazz == TypefaceSpan.class) {
            String family;
            if (compareSpan == null) {
                family = (String) view.getTag();
            } else {
                family = ((TypefaceSpan) compareSpan).getFamily();
            }
            newSpan = new TypefaceSpan(family);
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            int size;
            if (compareSpan == null) {
                size = (int) view.getTag();
            } else {
                size = ((AbsoluteSizeSpan) compareSpan).getSize();
            }
            newSpan = new AbsoluteSizeSpan(size);
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            float sizeChange;
            if (compareSpan == null) {
                sizeChange = (float) view.getTag();
            } else {
                sizeChange = ((RelativeSizeSpan) compareSpan).getSizeChange();
            }
            newSpan = new RelativeSizeSpan(sizeChange);
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            float scaleX;
            if (compareSpan == null) {
                scaleX = (float) view.getTag();
            } else {
                scaleX = ((ScaleXSpan) compareSpan).getScaleX();
            }
            newSpan = new ScaleXSpan(scaleX);

        } else {
            try {
                newSpan = clazz.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        if (newSpan != null) {
            editable.setSpan(newSpan, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    ///ViewParameter用于保存toolbar按钮的点选状态（如view.getBackground()，view.getTag()，view.getTag(R.id.url_text等)）
    private <T> boolean isSameWithViewParameter(View view, Class<T> clazz, T span) {
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        if (clazz == ForegroundColorSpan.class) {
            @ColorInt final int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
            final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
            return foregroundColor == colorDrawable.getColor();
        } else if (clazz == BackgroundColorSpan.class) {
            @ColorInt final int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
            final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
            return backgroundColor == colorDrawable.getColor();
        }
        ///字符span（带参数）：TypefaceFamily
        else if (clazz == TypefaceSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final String spanFamily = ((TypefaceSpan) span).getFamily();
            final String viewTagTypeface = (String) view.getTag();
            return TextUtils.equals(spanFamily, viewTagTypeface);
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final int spanSize = ((AbsoluteSizeSpan) span).getSize();
            final int viewTagAbsoluteSize = (int) view.getTag();
            return spanSize == viewTagAbsoluteSize;
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanSizeChange = ((RelativeSizeSpan) span).getSizeChange();
            final float viewTagRelativeSize = (float) view.getTag();
            return spanSizeChange == viewTagRelativeSize;
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanScaleX = ((ScaleXSpan) span).getScaleX();
            final float viewTagScaleX = (float) view.getTag();
            return spanScaleX == viewTagScaleX;
        } else {
            return true;
        }
    }

    ///注意：不包括block span（如URLSpan、ImageSpan）
    private <T> T filterSpanByCompareSpanOrViewParameter(View view, Class<T> clazz, T span, T compareSpan) {
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        if (clazz == ForegroundColorSpan.class) {
            @ColorInt final int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                return foregroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt final int compareSpanForegroundColor = ((ForegroundColorSpan) compareSpan).getForegroundColor();
                return foregroundColor == compareSpanForegroundColor ? span : null;
            }
        } else if (clazz == BackgroundColorSpan.class) {
            @ColorInt final int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                return backgroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt final int compareSpanBackgroundColor = ((BackgroundColorSpan) compareSpan).getBackgroundColor();
                return backgroundColor == compareSpanBackgroundColor ? span : null;
            }
        }
        ///字符span（带参数）：TypefaceFamily
        else if (clazz == TypefaceSpan.class) {
            final String spanFamily = ((TypefaceSpan) span).getFamily();
            if (compareSpan == null) {
                final String viewTagFamily = (String) view.getTag();
                return TextUtils.equals(spanFamily, viewTagFamily) ? span : null;
            } else {
                final String compareSpanFamily = ((TypefaceSpan) compareSpan).getFamily();
                return TextUtils.equals(spanFamily, compareSpanFamily) ? span : null;
            }
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            final int spanSize = ((AbsoluteSizeSpan) span).getSize();
            if (compareSpan == null) {
                final int viewTagSize = (int) view.getTag();
                return spanSize == viewTagSize ? span : null;
            } else {
                final int compareSpanSize = ((AbsoluteSizeSpan) compareSpan).getSize();
                return spanSize == compareSpanSize ? span : null;
            }
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            final float spanSizeChange = ((RelativeSizeSpan) span).getSizeChange();
            if (compareSpan == null) {
                final float viewTagSizeChange = (float) view.getTag();
                return spanSizeChange == viewTagSizeChange ? span : null;
            } else {
                final float compareSpanSizeChange = ((RelativeSizeSpan) compareSpan).getSizeChange();
                return spanSizeChange == compareSpanSizeChange ? span : null;
            }
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            final float spanScaleX = ((ScaleXSpan) span).getScaleX();
            if (compareSpan == null) {
                final float viewTagScaleX = (float) view.getTag();
                return spanScaleX == viewTagScaleX ? span : null;
            } else {
                final float compareSpanScaleX = ((ScaleXSpan) compareSpan).getScaleX();
                return spanScaleX == compareSpanScaleX ? span : null;
            }
        }

        return span;
    }


    /* ----------------- ///[onClick]点击更新ImageView，并且当selectionStart != selectionEnd时改变selection的span ------------------ */
    @Override
    public void onClick(final View view) {
        final Editable editable = mRichEditText.getText();

        if (isParagraphStyle(view)) {
            final int selectionStart = Selection.getSelectionStart(editable);
            final int selectionEnd = Selection.getSelectionEnd(editable);

            ///[FIX#当单光标位于文本尾部，click段落view后出现首尾相同的新span！且上一行显示view被selected]当单光标位于文本尾部，补插入一个'\n'
            if (selectionStart == selectionEnd && selectionStart == editable.length()) {
                editable.append('\n');
                ///调整光标位置到append之前的位置
//                mRichEditText.setSelection(selectionStart);
                Selection.setSelection(editable, selectionStart);
            }

            ///段落span：LineDivider
            if (view == mImageViewLineDivider) {
                ///当下列情况，不能点击LineDivider：
                ///不是单光标；或光标处于文本尾部；或光标处字符不为'\n'；或光标前一个字符不为'\n'
                if (selectionStart != selectionEnd || selectionStart == editable.length()
                        || editable.charAt(selectionStart) != '\n'
                        || selectionStart != 0 && editable.charAt(selectionStart - 1) != '\n') {
                    return;
                }
            }

            ///段落span（带参数）：Head
            if (view == mTextViewHead) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(mContext, R.array.head_items, view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.head_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final String head = (String) StringUtil.getItem(mContext, R.array.head_items, which);
                                ///参数校验
                                if (head == null) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!head.equals(((TextView) view).getText().toString())) {
                                    ///保存参数到view tag
                                    view.setTag(head);

                                    ///改变selection的span
                                    applyParagraphStyleSpansSelection(view, editable);

                                    ((TextView) view).setText(head);

                                    ///[Preview]
                                    updatePreview();
                                }

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyParagraphStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.head));

                                ///[Preview]
                                updatePreview();
                            }
                        }).show();

                return;
            }

            view.setSelected(!view.isSelected());

            applyParagraphStyleSpansSelection(view, editable);

            ///同组选择互斥
            ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan
            if (view.isSelected()) {
                if (mClassMap.get(view) == AlignNormalSpan.class) {
                    if (mImageViewAlignCenter.isSelected()) {
                        mImageViewAlignCenter.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignCenter, editable);
                    }
                    if (mImageViewAlignOpposite.isSelected()) {
                        mImageViewAlignOpposite.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignOpposite, editable);
                    }
                } else if (mClassMap.get(view) == AlignCenterSpan.class) {
                    if (mImageViewAlignNormal.isSelected()) {
                        mImageViewAlignNormal.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignNormal, editable);
                    }
                    if (mImageViewAlignOpposite.isSelected()) {
                        mImageViewAlignOpposite.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignOpposite, editable);
                    }
                } else if (mClassMap.get(view) == AlignOppositeSpan.class) {
                    if (mImageViewAlignNormal.isSelected()) {
                        mImageViewAlignNormal.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignNormal, editable);
                    }
                    if (mImageViewAlignCenter.isSelected()) {
                        mImageViewAlignCenter.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignCenter, editable);
                    }
                }
            }
        } else if (isCharacterStyle(view)) {

            ///字符span（带参数）：URL
            if (view == mImageViewURL) {
                final URLSpanDialogBuilder urlSpanDialogBuilder = URLSpanDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new URLSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String text, String url) {
                                ///参数校验：两项都为空则代表维持不变、不做任何处理
                                ///注意：某项为空、或值相同即代表该项维持不变，不为空且值不同则代表该项改变
                                if (text.length() == 0 && url.length() == 0) {  //////??????url正则表达式
                                    return;
                                }
                                final ArrayList<URLSpan> selectedSpans = SpanUtil.getSelectedSpans(mRichEditText, URLSpan.class);
                                if ((text.length() == 0 || url.length() == 0) && (selectedSpans == null || selectedSpans.size() == 0)) {  //////??????url正则表达式
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(R.id.url_text, text);
                                view.setTag(R.id.url_url, url);

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(R.id.url_text, null);
                                view.setTag(R.id.url_url, null);

                                ///[Preview]
                                updatePreview();
                            }
                        });

                final String text = (String) view.getTag(R.id.url_text);
                final String url = (String) view.getTag(R.id.url_url);
                urlSpanDialogBuilder.initial(text, url);
                urlSpanDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：Image
            if (view == mImageViewImage) {
                imageSpanDialogBuilder = ImageSpanDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new ImageSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String src, int width, int height, int align) {
                                ///参数校验：两项都为空则代表维持不变、不做任何处理n
                                ///注意：某项为空、或值相同即代表该项维持不变，不为空且值不同则代表该项改变
                                if (src.length() == 0 || width == 0 || height == 0) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///把width\height\align保存到text中
                                final String text = String.format(getContext().getResources().getString(R.string.image_span_text), src, width, height, align);

                                ///保存参数到view tag
                                view.setTag(R.id.image_text, text);
                                view.setTag(R.id.image_src, src);
                                view.setTag(R.id.image_width, width);
                                view.setTag(R.id.image_height, height);
                                view.setTag(R.id.image_align, align);

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new ImageSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String src, int width, int height, int align) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(R.id.image_text, null);
                                view.setTag(R.id.image_src, null);
                                view.setTag(R.id.image_width, null);
                                view.setTag(R.id.image_height, null);
                                view.setTag(R.id.image_align, null);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setImageFilePath(mImageFilePath);

                final String src = (String) view.getTag(R.id.image_src);
                final int width = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                final int height = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                final int align = view.getTag(R.id.image_align) == null ? ImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                imageSpanDialogBuilder.initial(src, width, height, align, mImageOverrideWidth, mImageOverrideHeight);
                imageSpanDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：ForegroundColor、BackgroundColor
            if (view == mImageViewForegroundColor || view == mImageViewBackgroundColor) {
                ///颜色选择器
                final ColorPickerDialogBuilder colorPickerDialogBuilder = ColorPickerDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }
                                ///设置View的背景颜色
                                view.setBackgroundColor(selectedColor);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }
                                ///清除View的背景颜色
                                view.setBackgroundColor(Color.TRANSPARENT);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        });
                ///初始化颜色为View的背景颜色
                if (view.isSelected()) {
                    final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                    colorPickerDialogBuilder.initialColor(colorDrawable.getColor());
                }
                colorPickerDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：TypefaceFamily
            if (view == mTextViewTypefaceFamily) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(mContext, R.array.typeface_family_items, view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.typeface_family_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence family = StringUtil.getItem(mContext, R.array.typeface_family_items, which);
                                ///参数校验
                                if (family == null) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(family);

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!family.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(family);
                                }

                                ///[Preview]
                                updatePreview();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.font_family));

                                ///[Preview]
                                updatePreview();
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：AbsoluteSize
            if (view == mTextViewAbsoluteSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(mContext, R.array.absolute_size_items, view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.absolute_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence size = StringUtil.getItem(mContext, R.array.absolute_size_items, which);
                                ///参数校验
                                if (size == null) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(Integer.parseInt(size.toString()));

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!size.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(size);
                                }

                                ///[Preview]
                                updatePreview();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.absolute_size));

                                ///[Preview]
                                updatePreview();
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：RelativeSize
            if (view == mTextViewRelativeSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(mContext, R.array.relative_size_items, view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.relative_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence sizeChange = StringUtil.getItem(mContext, R.array.relative_size_items, which);
                                ///参数校验
                                if (sizeChange == null) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(Float.parseFloat(sizeChange.toString()));

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!sizeChange.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(sizeChange);
                                }

                                ///[Preview]
                                updatePreview();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.relative_size));

                                ///[Preview]
                                updatePreview();
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：ScaleX
            if (view == mTextViewScaleX) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(mContext, R.array.scale_x_items, view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.scale_x_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence scaleX = StringUtil.getItem(mContext, R.array.scale_x_items, which);
                                ///参数校验
                                if (scaleX == null) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(Float.parseFloat(scaleX.toString()));

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!scaleX.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(scaleX);
                                }

                                ///[Preview]
                                updatePreview();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.scale_x));

                                ///[Preview]
                                updatePreview();
                            }
                        }).show();

                return;
            }

            view.setSelected(!view.isSelected());

            applyCharacterStyleSpansSelection(view, editable);
        }

        ///[Preview]
        updatePreview();
    }

    @Override
    public boolean onLongClick(final View view) {
        ///段落span（带初始化参数）：LineDivider
        if (view == mImageViewLineDivider) {
            LineDividerDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LineDividerDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int marginTop, int marginBottom) {
                            mLineDividerSpanMarginTop = marginTop;
                            mLineDividerSpanMarginBottom = marginBottom;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .initial(mLineDividerSpanMarginTop, mLineDividerSpanMarginBottom)
                    .build().show();

            return true;
        }
        ///段落span（带初始化参数）：Quote
        if (view == mImageViewQuote) {
            QuoteSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new QuoteSpanDialogBuilder.PickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors, int stripWidth, int gapWidth) {
                            mQuoteSpanColor = selectedColor;
                            mQuoteSpanStripWidth = stripWidth;
                            mQuoteSpanGapWidth = gapWidth;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .initial(mQuoteSpanColor, mQuoteSpanStripWidth, mQuoteSpanGapWidth)
                    .build().show();

            return true;
        }
        ///段落span（带初始化参数）：Bullet
        if (view == mImageViewBullet) {
            BulletSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new BulletSpanDialogBuilder.PickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors, int stripWidth, int gapWidth) {
                            mBulletColor = selectedColor;
                            mBulletSpanRadius = stripWidth;
                            mBulletSpanGapWidth = gapWidth;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .initial(mBulletColor, mBulletSpanRadius, mBulletSpanGapWidth)
                    .build().show();

            return true;
        }
        ///段落span（带初始化参数）：LeadingMargin
        if (view == mImageViewLeadingMargin) {
            LeadingMarginSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LeadingMarginSpanDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indent) {
                            mLeadingMarginSpanIndent = indent;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .initial(mLeadingMarginSpanIndent)
                    .build().show();

            return true;
        }

        return false;
    }


    /* ----------------- ///[selectionChanged]根据selection更新工具条按钮 ------------------ */
    @Override
    public void selectionChanged(int selectionStart, int selectionEnd) {
        Log.d("TAG", "============= selectionChanged ============" + selectionStart + ", " + selectionEnd);

        ///[enableSelectionChange]禁止onSelectionChanged()
        ///[Undo/Redo]
        if (!enableSelectionChange || isUndoOrRedo) {
            return;
        }

        final Editable editable = mRichEditText.getText();

        final int currentParagraphStart = SpanUtil.getParagraphStart(editable, selectionStart);
        final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, selectionStart);

        for (View view : mClassMap.keySet()) {
            if (isParagraphStyle(view)) {
                updateParagraphView(view, mClassMap.get(view), editable, currentParagraphStart, currentParagraphEnd);
            } else if (isCharacterStyle(view)) {
                updateCharacterStyleView(view, mClassMap.get(view), editable, selectionStart, selectionEnd);
            }

            ///test
            if (DEBUG) SpanUtil.testOutput(editable, mClassMap.get(view));
        }
    }

    private void applyParagraphStyleSpansSelection(View view, Editable editable) {
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);

        int next;
        for (int i = selectionStart; i <= selectionEnd; i = next) {
            final int currentParagraphStart = SpanUtil.getParagraphStart(editable, i);
            final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, i);
            next = currentParagraphEnd;

            ///调整同类span
            adjustParagraphStyleSpansSelection(view, mClassMap.get(view), editable, currentParagraphStart, currentParagraphEnd, view.isSelected());

            if (next >= selectionEnd) {
                break;
            }
        }

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            mUndoRedoHelper.addHistory(getActionId(view), -1, null, null,
                    RichEditorToolbarHelper.saveSpans(mClassMap, editable, 0, editable.length(), false));
        }
    }
    private <T> void adjustParagraphStyleSpansSelection(View view, Class<T> clazz, Editable editable, int start, int end, boolean isSelected) {
        boolean isNewSpanNeeded = true;  ///changed内容是否需要新添加span

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果单光标、且位于span的首尾，则忽略
            if (spanStart == start && spanEnd == end) {
                isNewSpanNeeded = false;
                if (isSelected) {

                    ///段落span（带参数）：Head
                    if (clazz == HeadSpan.class) {
                        final String spanHead = ((HeadSpan) span).getHead();
                        final String viewTagHead = (String) view.getTag();
                        if (!spanHead.equals(viewTagHead)) {
                            isNewSpanNeeded = true;
                            editable.removeSpan(span);
                        }
                    }

                } else {
                    editable.removeSpan(span);
                }
            }
        }

        ///如果changed内容需要新加span，且工具条选中，则添加新span
        if (isNewSpanNeeded && isSelected) {
            newParagraphStyleSpan(view, clazz, editable, start, end);
        }
    }
    private void applyCharacterStyleSpansSelection(View view, Editable editable) {
//        final int selectionStart = mRichEditText.getSelectionStart();
//        final int selectionEnd = mRichEditText.getSelectionEnd();
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);
        ///当selectionStart != selectionEnd时改变selection的span
        if (selectionStart == -1 || selectionEnd == -1 || selectionStart == selectionEnd && !isBlockCharacterStyle(view)) {
            return;
        }

        ///调整同类span
        if (isBlockCharacterStyle(view)) {
            adjustBlockCharacterStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, view.isSelected(), true);
        } else {
            adjustCharacterStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, view.isSelected(), true);
        }

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            mUndoRedoHelper.addHistory(getActionId(view), -1, null, null,
                    RichEditorToolbarHelper.saveSpans(mClassMap, editable, 0, editable.length(), false));
        }
    }


    /* ----------------- ///[TextWatcher] ------------------ */
    ///注意：语言环境为english时，存在before/count/after都大于0的情况！此时start为单词开始处（以空格或回车分割）
    ///解决方法：android:inputType="textVisiblePassword"
    private final class RichTextWatcher implements TextWatcher {
        ///[Undo/Redo]
        private int mStart;
        private String mBeforeChange;
        private String mAfterChange;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            ///[Undo/Redo]
            if (isUndoOrRedo) {
                return;
            }

            if (count > 0) {    ///[TextWatcher#删除]
                for (View view : mClassMap.keySet()) {
                    if (isCharacterStyle(view)) {
                        ///清除掉已经被删除的span，否则将会产生多余的无效span！
                        SpanUtil.removeSpans(mClassMap.get(view), mRichEditText.getText(), start, start + count);
                    }
                }
            }

            ///[Undo/Redo]
            mBeforeChange = s.subSequence(start, start + count).toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ///[Undo/Redo]
            if (isUndoOrRedo) {
                return;
            }

            final Editable editable = mRichEditText.getText();

            final int firstParagraphStart = SpanUtil.getParagraphStart(editable, start);
            final int firstParagraphEnd = SpanUtil.getParagraphEnd(editable, start);
            final int lastParagraphStart = SpanUtil.getParagraphStart(editable, start + count);
            final int lastParagraphEnd = SpanUtil.getParagraphEnd(editable, start + count);

            for (View view : mClassMap.keySet()) {
                if (isParagraphStyle(view)) {
                    adjustParagraphStyleSpans(view, mClassMap.get(view), editable,
                            start, start + count,
                            firstParagraphStart, firstParagraphEnd, lastParagraphStart, lastParagraphEnd, view.isSelected());
                } else if (isCharacterStyle(view)) {
                    if (count > 0) {   ///功能三：[TextWatcher#添加]
//                        //////??????平摊并合并交叉重叠的同类span
//                        flatSpans(mClassMap.get(view), editable, adjustStart, adjustStart + adjustCount);

                        if (isBlockCharacterStyle(view)) {
                            adjustBlockCharacterStyleSpans(view, mClassMap.get(view), editable, start, start + count, view.isSelected(), false);
                        } else {
                            ///adjustCharacterStyleSpans()与adjustCharacterStyleSpansSelection()完全相同！
                            adjustCharacterStyleSpans(view, mClassMap.get(view), editable, start, start + count, view.isSelected(), false);
                        }
                    } else if (before > 0) {    ///功能四：[TextWatcher]删除，删除后如果没添加内容（即count == 0且before > 0），则合并同类span
                        ///合并同类span
                        if (!isBlockCharacterStyle(view) && editable.length() > 0) {///[BUG]模拟器中：当文本长度为0，调用joinSpanByPosition()死机！要保证editable.length() > 0
                            joinSpanByPosition(view, mClassMap.get(view), editable, start);
                        }
                    }
                }
            }

            ///[Undo/Redo]
            mAfterChange = s.subSequence(start, start + count).toString();
            mStart = start;
        }

        @Override
        public void afterTextChanged(Editable s) {
            ///[Undo/Redo]
            if (isUndoOrRedo) {
                return;
            }

            ///消除EditText输入时自动产生UnderlineSpan
            ///https://stackoverflow.com/questions/35323111/android-edittext-is-underlined-when-typing
            ///https://stackoverflow.com/questions/46822580/edittext-remove-black-underline-while-typing/47704299#47704299
            for (UnderlineSpan span : s.getSpans(0, s.length(), UnderlineSpan.class)) {
                ///忽略不是clazz本身（比如为clazz的子类）的span
                ///getSpans()获取clazz类及其子类
                ///比如：HeadSpan extends AbsoluteSizeSpan：
                ///editable.getSpans(start, end, AbsoluteSizeSpan)也能获取到AbsoluteSizeSpan的子类HeadSpan
                ///这里CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
                if (span.getClass() != UnderlineSpan.class) {
                    continue;
                }

                s.removeSpan(span);
            }

            ///[Preview]
            updatePreview();

            ///[Undo/Redo]
            mUndoRedoHelper.addHistory(UndoRedoHelper.TEXT_CHANGED_ACTION, mStart, mBeforeChange, mAfterChange,
                    RichEditorToolbarHelper.saveSpans(mClassMap, s, 0, s.length(), false));
        }
    }

    private <T> void adjustParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end,
                                               int firstParagraphStart, int firstParagraphEnd,
                                               int lastParagraphStart, int lastParagraphEnd,
                                               boolean isSelected) {
        boolean isSet = false;

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///移除无效的段落span：LineDivider
            ///当光标位于LineDivider处，输入内容后LineDivider虽然不显示了，但span仍然存在！需要删除span
            if (clazz == LineDividerSpan.class && spanStart == firstParagraphStart
                    && firstParagraphStart + 1 != firstParagraphEnd && editable.charAt(firstParagraphStart) != '\n') {
                editable.removeSpan(span);
                continue;
            }

            if (spanStart > firstParagraphStart && spanStart < firstParagraphEnd) {
                editable.removeSpan(span);
            } else if (spanStart == firstParagraphStart) {
                if (isSet) {
                    editable.removeSpan(span);
                } else {
                    if (spanEnd != firstParagraphEnd) {
                        editable.setSpan(span, firstParagraphStart, firstParagraphEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    isSet = true;
                }
            } else if (spanStart < firstParagraphStart && spanEnd != firstParagraphStart) {
                editable.setSpan(span, spanStart, firstParagraphStart, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        ///当下列情况，需要调整一下最后一个段落span的结束位置
        ///两个以上的段落；且最后段落有内容（不只是'\n'）；段落span与最后段落的结束位置不同
        if (firstParagraphStart != lastParagraphStart && lastParagraphStart + 1 != lastParagraphEnd) {
            final ArrayList<T> endSpans = SpanUtil.getFilteredSpans(editable, clazz, lastParagraphStart, lastParagraphEnd);
            for (T span : endSpans) {
                final int spanStart = editable.getSpanStart(span);
                final int spanEnd = editable.getSpanEnd(span);

                ///段落span与最后段落的结束位置不同
                if (spanStart == lastParagraphStart && spanEnd != lastParagraphEnd) {
                    editable.setSpan(span, lastParagraphStart, lastParagraphEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }
    private <T> void adjustBlockCharacterStyleSpans(View view, Class<T> clazz, final Editable editable, final int start, final int end, boolean isSelected, boolean isFromSelection) {
        ///[isUpdateNeeded]
        ///注意：EditText的文本被replace后，selection区间变为不存在
        ///从而调用updateCharacterStyleView()，导致view的selected变为false、viewTag被清空
        ///所以除首次span外，都需要重置view的selected为true、设置viewTag
        ///并在全部处理了span后，再次调用updateCharacterStyleView()
        ///注意：不建议用setSelection()，效率低
        boolean isUpdateNeeded = false;

        ///[isRemoveNeeded]
        ///当复制粘贴包含block span到目标block span区间中时，为避免重复所以删除粘贴的block span，保留目标block span
        boolean isRemoveNeeded = false;
        final ArrayList<T> removedSpans = new ArrayList<>();

        boolean hasSpan = false;

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果单光标、且位于span的首尾，则忽略
            if (start == end && (spanStart == start || end == spanEnd)) {
                continue;
            }

            if (isSelected) {

                ///字符span（带参数）：URL
                if (clazz == URLSpan.class) {
                    final String viewTagText = (String) view.getTag(R.id.url_text);
                    final String viewTagUrl = (String) view.getTag(R.id.url_url);
                    final String compareText = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanUrl = ((URLSpan) span).getURL();
                    if (isFromSelection && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        editable.replace(spanStart, spanEnd, viewTagText);

                        ///[isUpdateNeeded]
                        view.setSelected(isSelected);
                        view.setTag(R.id.url_text, viewTagText);
                        view.setTag(R.id.url_url, viewTagUrl);
                        isUpdateNeeded = true;
                    } else {
                        if (!TextUtils.isEmpty(viewTagUrl) && !viewTagUrl.equals(spanUrl)) {
                            editable.removeSpan(span);
                            span = (T) new URLSpan(viewTagUrl);
                            editable.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
                ///字符span（带参数）：Image
                else if (clazz == CustomImageSpan.class) {
                    final String viewTagText = (String) view.getTag(R.id.image_text);
                    final String viewTagSrc = (String) view.getTag(R.id.image_src);
                    final int viewTagWidth = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                    final int viewTagHeight = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                    final int viewTagAlign = view.getTag(R.id.image_align) == null ? ImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                    final String compareText = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanSrc = ((CustomImageSpan) span).getSource();
                    if (isFromSelection && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        editable.replace(spanStart, spanEnd, viewTagText);

                        ///[isUpdateNeeded]
                        view.setSelected(isSelected);
                        view.setTag(R.id.image_text, viewTagText);
                        view.setTag(R.id.image_src, viewTagSrc);
                        view.setTag(R.id.image_width, viewTagWidth);
                        view.setTag(R.id.image_height, viewTagHeight);
                        view.setTag(R.id.image_align, viewTagAlign);
                        isUpdateNeeded = true;
                    } else {
                        if (!TextUtils.isEmpty(viewTagSrc) && !viewTagSrc.equals(spanSrc)) {
                            editable.removeSpan(span);

                            loadImage(editable, viewTagSrc, viewTagWidth, viewTagHeight, viewTagAlign, start, end);
                        }
                    }
                }
            } else {
                if (isFromSelection) {
                    editable.removeSpan(span);
                }
            }

            ///[isRemoveNeeded]
            if (!isFromSelection) {
                ///如果span包含了区间[start, end]则说明要删除所有在区间[start, end]的span
                if (spanStart < start && end < spanEnd) {
                    isRemoveNeeded = true;
                } else if (start <= spanStart && spanEnd <= end) {
                    removedSpans.add(span);
                }
            }

            hasSpan = true;
        }

        if (isSelected && !hasSpan) {

            ///字符span（带参数）：URL
            if (clazz == URLSpan.class) {
                final String viewTagText = (String) view.getTag(R.id.url_text);
                final String viewTagUrl = (String) view.getTag(R.id.url_url);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isFromSelection && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    editable.replace(start, end, viewTagText);
                } else {
                    if (!TextUtils.isEmpty(viewTagUrl)) {
                        editable.setSpan(new URLSpan(viewTagUrl), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            ///字符span（带参数）：Image
            else if (clazz == CustomImageSpan.class) {
                final String viewTagText = (String) view.getTag(R.id.image_text);
                final String viewTagSrc = (String) view.getTag(R.id.image_src);
                final int viewTagWidth = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                final int viewTagHeight = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                final int viewTagAlign = view.getTag(R.id.image_align) == null ? ImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isFromSelection && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    editable.replace(start, end, viewTagText);
                } else {
                    if (!TextUtils.isEmpty(viewTagSrc)) {
                        loadImage(editable, viewTagSrc, viewTagWidth, viewTagHeight, viewTagAlign, start, end);
                    }
                }
            }

        }

        ///[isUpdateNeeded]
        if (isUpdateNeeded) {
            //        final int selectionStart = mRichEditText.getSelectionStart();
            //        final int selectionEnd = mRichEditText.getSelectionEnd();
            final int selectionStart = Selection.getSelectionStart(editable);
            final int selectionEnd = Selection.getSelectionEnd(editable);
            if (selectionStart != -1 && selectionEnd != -1) {
                updateCharacterStyleView(view, mClassMap.get(view), editable, selectionStart, selectionEnd);
            }
        }

        ///[isRemoveNeeded]
        if (isRemoveNeeded) {
            for (T span : removedSpans) {
                editable.removeSpan(span);
            }
        }
    }
    private <T> void adjustCharacterStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isSelected, boolean isFromSelection) {
        boolean hasSpan = false;
        boolean isSameWithViewParameter = true;

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            ///isSelected为true时（外延），当区间[start, end]中有多个span，首次处理span后，其余的span都应删除
            ///注意：区间[start, end]结尾处可能会有部分在区间[start, end]中的span，因为首次处理中包含了join，所以已经被删除了
            if (hasSpan && isSelected && isSameWithViewParameter) {
                editable.removeSpan(span);
                continue;
            }

            isSameWithViewParameter = isSameWithViewParameter(view, clazz, span);

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            if (isSelected && isSameWithViewParameter) {
//                int st = spanStart, en = spanEnd;
//                if (start < spanStart && spanEnd < end) {///左右延+join
//                    st = start; en = end;
//                } else if (start < spanStart) {    ///左延+join
//                    st = start;
//                } else if (spanEnd < end) {    ///右延+join
//                    en = end;
//                }
                final int st = Math.min(start, spanStart);
                final int en = Math.max(spanEnd, end);
                if (st != spanStart || en != spanEnd) {
                    editable.setSpan(span, st, en, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    if (st != spanStart) {  ///左join
                        findAndJoinLeftSpan(view, clazz, editable, span);
                    }
                    if (en != spanEnd) {  ///右join
                        findAndJoinRightSpan(view, clazz, editable, span);
                    }
                }
            } else {
                int st = spanStart, en = spanEnd;
                if (spanStart < start && end < spanEnd) { ///右缩+new
                    en = start;
                } else if (spanStart < end && end < spanEnd) { ///左缩
                    st = end;
                } else if (spanStart < start && start < spanEnd) { ///右缩
                    en = start;
                }
                if (st != spanStart || en != spanEnd) {
                    editable.setSpan(span, st, en, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    if (spanStart < start && end < spanEnd) { ///右缩+new
                        ///new end, spanEnd
                        newCharacterStyleSpanByCompareSpanOrViewParameter(view, clazz, editable, end, spanEnd, span);
                    }
                } else {
                    if (!isFromSelection) {
                        if (start == spanStart) {  ///左join
                            findAndJoinLeftSpan(view, clazz, editable, span);
                        }
                        if (end == spanEnd) {  ///右join
                            findAndJoinRightSpan(view, clazz, editable, span);
                        }
                    } else {
                        editable.removeSpan(span);
                    }
                }
            }

            hasSpan = true;
        }

        ///extendOrNew+join
        if (isSelected && (!hasSpan || !isSameWithViewParameter)) {
            extendOrNewCharacterStyleSpan(view, clazz, editable, start, end);
        }
    }


    /* ------------------------------------------------------------------------------------------ */
    private <T> void extendOrNewCharacterStyleSpan(View view, Class<T> clazz, Editable editable, int start, int end) {
        ///如果左右有span且参数相等，则延长，否则添加
        final T leftSpan = getLeftSpan(view, clazz, editable, start, end, null);
        if (leftSpan != null) {
            final int leftSpanStart = editable.getSpanStart(leftSpan);
            editable.setSpan(leftSpan, leftSpanStart, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            findAndJoinRightSpan(view, clazz, editable, leftSpan);
            return;
        }
        final T rightSpan = getRightSpan(view, clazz, editable, start, end, null);
        if (rightSpan != null) {
            final int rightSpanEnd = editable.getSpanEnd(rightSpan);
            editable.setSpan(rightSpan, start, rightSpanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            findAndJoinLeftSpan(view, clazz, editable, start, rightSpanEnd, rightSpan);
            return;
        }

        newCharacterStyleSpanByCompareSpanOrViewParameter(view, clazz, editable, start, end, null);
    }

    /**
     * 合并指定位置的同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    private <T> void joinSpanByPosition(View view, Class<T> clazz, Editable editable, int position) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, position, position);
        for (T span : spans) {
            final T leftSpan = getLeftSpan(view, clazz, editable, position, position, span);
            if (leftSpan != null) {
                findAndJoinRightSpan(view, clazz, editable, span);
            }
        }
    }

    /**
     * 左联合并同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    private <T> int findAndJoinLeftSpan(View view, Class<T> clazz, Editable editable, T span) {
        final int spanStart = editable.getSpanStart(span);
        final int spanEnd = editable.getSpanEnd(span);

        int resultStart = spanStart;
        final T leftSpan = getLeftSpan(view, clazz, editable, spanStart, spanEnd, span);
        if (leftSpan != null) {
            resultStart = editable.getSpanStart(leftSpan);
            editable.setSpan(span, resultStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            editable.removeSpan(leftSpan);
        }
        return resultStart;
    }

    /**
     * 右联合并同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    private <T> int findAndJoinRightSpan(View view, Class<T> clazz, Editable editable, T span) {
        final int spanStart = editable.getSpanStart(span);
        final int spanEnd = editable.getSpanEnd(span);

        int resultEnd = spanEnd;
        final T rightSpan = getRightSpan(view, clazz, editable, spanStart, spanEnd, span);
        if (rightSpan != null) {
            resultEnd = editable.getSpanEnd(rightSpan);
            editable.setSpan(span, spanStart, resultEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            editable.removeSpan(rightSpan);
        }
        return resultEnd;
    }

    /**
     * 获得左边的同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    private <T> T getLeftSpan(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        if (start == 0) {
            return null;
        }
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, start);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart < start && spanEnd <= end) {
                return filterSpanByCompareSpanOrViewParameter(view, clazz, span, compareSpan);
            }
        }
        return null;
    }

    /**
     * 获得右边的同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    private <T> T getRightSpan(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        if (end == editable.length()) {
            return null;
        }
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, end, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (start <= spanStart && end < spanEnd) {
                return filterSpanByCompareSpanOrViewParameter(view, clazz, span, compareSpan);
            }
        }
        return null;
    }

    /**
     * 清除区间内的spans
     */
    private void clearParagraphSpans(int start, int end) {
        final Editable editable = mRichEditText.getText();

        final int currentParagraphStart = SpanUtil.getParagraphStart(editable, start);
        final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, start);

        for (View view : mClassMap.keySet()) {
            if (isParagraphStyle(view)) {
                SpanUtil.removeSpans(mClassMap.get(view), editable, start, end);
                updateParagraphView(view, mClassMap.get(view), editable, currentParagraphStart, currentParagraphEnd);
            }
        }
    }
    private void clearCharacterSpans(int start, int end) {
        final Editable editable = mRichEditText.getText();

        for (View view : mClassMap.keySet()) {
            if (isCharacterStyle(view)) {
                ///调整同类span
                if (isBlockCharacterStyle(view)) {
                    adjustBlockCharacterStyleSpans(view, mClassMap.get(view), editable, start, end, false, true);
                } else {
                    adjustCharacterStyleSpans(view, mClassMap.get(view), editable, start, end, false, true);
                }
                updateCharacterStyleView(view, mClassMap.get(view), editable, start, end);
            }
        }
    }

}
