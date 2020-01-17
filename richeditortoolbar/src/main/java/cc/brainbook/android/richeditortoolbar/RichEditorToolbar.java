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
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import cc.brainbook.android.richeditortoolbar.builder.ClickImageSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickBulletSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickLeadingMarginSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickLineDividerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickListSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickQuoteSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.ClickURLSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;
import cc.brainbook.android.richeditortoolbar.helper.UndoRedoHelper;
import cc.brainbook.android.richeditortoolbar.span.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.CodeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomAbsoluteSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomBackgroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomBulletSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomForegroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomLeadingMarginSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomRelativeSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomScaleXSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomStrikethroughSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomSubscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomSuperscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomFontFamilySpan;
import cc.brainbook.android.richeditortoolbar.span.CustomURLSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.span.ListItemSpan;
import cc.brainbook.android.richeditortoolbar.span.ListSpan;
import cc.brainbook.android.richeditortoolbar.span.NestSpan;
import cc.brainbook.android.richeditortoolbar.span.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.ArrayUtil;
import cc.brainbook.android.richeditortoolbar.util.FileUtil;
import cc.brainbook.android.richeditortoolbar.util.ParcelUtil;
import cc.brainbook.android.richeditortoolbar.util.PrefsUtil;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;
import cc.brainbook.android.richeditortoolbar.util.Util;

import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.createChildrenListItemSpans;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.isListTypeOrdered;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.removeChildrenListItemSpans;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.updateChildrenListItemSpans;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.findAndJoinLeftSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.findAndJoinRightSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getLeftSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getParentSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getRightSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getSpanFlag;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isBlockCharacterStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isNestParagraphStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isCharacterStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isParagraphStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isSameWithViewParameter;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.joinSpanByPosition;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.updateCharacterStyleView;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.updateDescendantsNestingLevel;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.updateParagraphView;

public class RichEditorToolbar extends FlexboxLayout implements
        LineDividerSpan.DrawBackgroundCallback,
        Drawable.Callback, View.OnClickListener, View.OnLongClickListener,
        RichEditText.OnSelectionChanged,
        RichEditText.SaveSpansCallback, RichEditText.LoadSpansCallback,
        UndoRedoHelper.OnPositionChangedListener {
    public static final String SHARED_PREFERENCES_NAME = "draft_preferences";
    public static final String SHARED_PREFERENCES_KEY_DRAFT_TEXT = "draft_text";
    public static final String CLIPBOARD_FILE_NAME = "rich_editor_clipboard_file";

    private HashMap<Class, View> mClassMap = new HashMap<>();
    public HashMap<Class, View> getClassMap() {
        return mClassMap;
    }

    private RichEditText mRichEditText;
    public RichEditText getRichEditText() {
        return mRichEditText;
    }


    /* ---------------- ///段落span（带参数）：Head ---------------- */
    private TextView mTextViewHead;

    /* ---------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan ---------------- */
    private ImageView mImageViewAlignNormal;
    private ImageView mImageViewAlignCenter;
    private ImageView mImageViewAlignOpposite;

    /* ---------------- ///段落span（带初始化参数）：LeadingMargin ---------------- */
    private ImageView mImageViewLeadingMargin;
    private int mLeadingMarginSpanIndent = CustomLeadingMarginSpan.DEFAULT_INTENT;

    /* ---------------- ///段落span（带初始化参数）：Bullet ---------------- */
    private ImageView mImageViewBullet;
    private @ColorInt int mBulletColor = CustomBulletSpan.STANDARD_COLOR;
    private int mBulletSpanRadius = CustomBulletSpan.STANDARD_BULLET_RADIUS;
    private int mBulletSpanGapWidth = CustomBulletSpan.STANDARD_GAP_WIDTH;

    /* ---------------- ///段落span：LineDivider ---------------- */
    private ImageView mImageViewLineDivider;
    private int mLineDividerSpanMarginTop = LineDividerSpan.DEFAULT_MARGIN_TOP;
    private int mLineDividerSpanMarginBottom = LineDividerSpan.DEFAULT_MARGIN_BOTTOM;
    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        c.drawLine(left, (top + bottom) / 2, right, (top + bottom) / 2, p);    ///画直线
    }

    /* ---------------- ///段落span（带初始化参数）：Quote ---------------- */
    private ImageView mImageViewQuote;
    private @ColorInt int mQuoteSpanColor = CustomQuoteSpan.STANDARD_COLOR;
    private int mQuoteSpanStripWidth = CustomQuoteSpan.STANDARD_STRIPE_WIDTH_PX;
    private int mQuoteSpanGapWidth = CustomQuoteSpan.STANDARD_GAP_WIDTH_PX;

    /* ---------------- ///段落span（带初始化参数）：List ---------------- */
    private ImageView mImageViewList;
    private int mIndicatorMargin = ListSpan.DEFAULT_INDICATOR_MARGIN;
    private int mIndicatorWidth = ListItemSpan.DEFAULT_INDICATOR_WIDTH;
    private int mIndicatorGapWidth = ListItemSpan.DEFAULT_INDICATOR_GAP_WIDTH;
    private @ColorInt int mIndicatorColor = ListItemSpan.DEFAULT_INDICATOR_COLOR;

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

    /* ---------------- ///字符span（带参数）：FontFamily ---------------- */
    private TextView mTextViewFontFamily;

    /* ---------------- ///字符span（带参数）：AbsoluteSize ---------------- */
    private TextView mTextViewAbsoluteSize;

    /* ---------------- ///字符span（带参数）：RelativeSize ---------------- */
    private TextView mTextViewRelativeSize;

    /* ---------------- ///字符span（带参数）：ScaleX ---------------- */
    private TextView mTextViewScaleX;

    /* ---------------- ///字符span（带参数）：URL ---------------- */
    private ImageView mImageViewURL;

    /* ---------------- ///字符span（带参数）：Image ---------------- */
    private ImageView mImageViewVideo;
    private ImageView mImageViewAudio;
    private ImageView mImageViewImage;
    private CustomImageSpan imagePlaceholderSpan = null;///避免产生重复span！
    private ClickImageSpanDialogBuilder clickImageSpanDialogBuilder;

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

    ///[ImageSpan#ClickImageSpanDialogBuilder#onActivityResult()]
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (clickImageSpanDialogBuilder != null) {
            clickImageSpanDialogBuilder.onActivityResult(requestCode, resultCode, data);
        }
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

    ///[ImageSpan#Glide#loadImage()#Placeholder]
    @Nullable
    private Drawable mPlaceholderDrawable;
    public void setPlaceholderDrawable(@Nullable Drawable placeholderDrawable) {
        mPlaceholderDrawable = placeholderDrawable;
    }
    @DrawableRes
    private int mPlaceholderResourceId;
    public void setPlaceholderResourceId(@DrawableRes int placeholderResourceId) {
        mPlaceholderResourceId = placeholderResourceId;
    }

    private void loadImage(final Class clazz, final String viewTagUri, final String viewTagSrc, final int viewTagAlign, final int viewTagWidth, final int viewTagHeight,
                     final Editable pasteEditable, final int pasteOffset, final int start, final int end) {
        final GlideImageLoader glideImageLoader = new GlideImageLoader(mContext);

        ///注意：mPlaceholderDrawable和mPlaceholderResourceId必须至少设置其中一个！如都设置则mPlaceholderDrawable优先
        if (mPlaceholderDrawable != null) {
            glideImageLoader.setPlaceholderDrawable(mPlaceholderDrawable);
        } else {
            glideImageLoader.setPlaceholderResourceId(mPlaceholderResourceId);
        }

        glideImageLoader.setDrawableCallback(this);
        glideImageLoader.setCallback(new GlideImageLoader.Callback() {
            ///[GlideImageLoader#isAsync]GlideImageLoader是否为异步加载图片
            ///说明：当paste含有ImageSpan的文本时，有可能造成已经replace完了paste文本才完成Glide异步加载图片，
            ///此时loadImage仍然执行paste的setSpan，而此时应该执行mRichEditText的setSpan！
            private boolean isAsync = false;

            @Override
            public void onLoadStarted(@Nullable Drawable drawable) {
                isAsync = true;

                if (drawable != null) {
                    drawable.setBounds(0, 0, viewTagWidth, viewTagHeight);   ///注意：Drawable必须设置Bounds才能显示

                    imagePlaceholderSpan = clazz == VideoSpan.class ? new VideoSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : clazz == AudioSpan.class ? new AudioSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : new CustomImageSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign);

                    if (pasteEditable == null) {
                        mRichEditText.getText().setSpan(imagePlaceholderSpan, start, end, getSpanFlag(clazz));
                    } else {
                        pasteEditable.setSpan(imagePlaceholderSpan, start, end, getSpanFlag(clazz));
                    }
                }
            }

            @Override
            public void onResourceReady(@NonNull Drawable drawable) {
                drawable.setBounds(0, 0, viewTagWidth, viewTagHeight);  ///注意：Drawable必须设置Bounds才能显示

                if (isAsync || pasteEditable == null) {
                    mRichEditText.getText().removeSpan(imagePlaceholderSpan);
                    mRichEditText.getText().setSpan(
                            clazz == VideoSpan.class ? new VideoSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : clazz == AudioSpan.class ? new AudioSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : new CustomImageSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign),
                            pasteEditable == null ? start : start + pasteOffset, pasteEditable == null ? end : end + pasteOffset, getSpanFlag(clazz));
                } else {
                    pasteEditable.removeSpan(imagePlaceholderSpan);
                    pasteEditable.setSpan(
                            clazz == VideoSpan.class ? new VideoSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : clazz == AudioSpan.class ? new AudioSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : new CustomImageSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign),
                                    start, end, getSpanFlag(clazz));
                }
            }
        });

        ///[ImageSpan#Glide#loadImage()]
        glideImageLoader.loadImage(viewTagSrc);
    }

    ///执行setSpanFromSpanBeans后处理
    ///比如：设置LineDividerSpan的DrawBackgroundCallback、ImageSpan的Glide异步加载图片等
    private void postSetSpanFromSpanBeans(Editable pasteEditable, int pasteOffset, ArrayList<Object> spanList) {
        if (spanList == null) {
            return;
        }

        for (Object span : spanList) {
            if (span instanceof LineDividerSpan) {
                ((LineDividerSpan) span).setDrawBackgroundCallback(this);
            } else if (span instanceof CustomImageSpan) {
                final String uri = ((CustomImageSpan) span).getUri();
                final String source = ((CustomImageSpan) span).getSource();
                final int verticalAlignment = ((CustomImageSpan) span).getVerticalAlignment();
                final int drawableWidth = ((CustomImageSpan) span).getDrawableWidth();
                final int drawableHeight = ((CustomImageSpan) span).getDrawableHeight();

                if (pasteEditable == null) {
                    final int spanStart = mRichEditText.getText().getSpanStart(span);
                    final int spanEnd = mRichEditText.getText().getSpanEnd(span);
                    mRichEditText.getText().removeSpan(span);

                    ///[ImageSpan#Glide#GifDrawable]
                    loadImage(span.getClass(), uri, source, verticalAlignment, drawableWidth, drawableHeight, null, -1, spanStart, spanEnd);
                } else {
                    final int spanStart = pasteEditable.getSpanStart(span);
                    final int spanEnd = pasteEditable.getSpanEnd(span);
                    pasteEditable.removeSpan(span);

                    ///[ImageSpan#Glide#GifDrawable]
                    loadImage(span.getClass(), uri, source, verticalAlignment, drawableWidth, drawableHeight, pasteEditable, pasteOffset, spanStart, spanEnd);
                }
            }
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
            final byte[] bytes = RichEditorToolbarHelper.toByteArray(mClassMap, editable, selectionStart, selectionEnd, true);
            FileUtil.writeFile(mClipboardFile, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///[pasteEditable/pasteOffset]
    ///使用pasteOffset区分是否为paste操作，如offset为-1则不是，offset大于等于0则是
    ///如果pasteEditable为null，则忽略pasteOffset（即pasteOffset为-1）
    @Override
    public void loadSpans(Editable pasteEditable, int pasteOffset) {
        ///从进程App共享空间恢复spans
        try {
            final byte[] bytes = FileUtil.readFile(mClipboardFile);
            if (bytes == null) {
                return;
            }

            ///执行setSpanFromSpanBeans及后处理
            postSetSpanFromSpanBeans(pasteEditable, pasteOffset, RichEditorToolbarHelper.fromByteArray(pasteEditable, bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------------- ///[Undo/Redo] ---------------- */
    private ImageView mImageViewUndo;
    private ImageView mImageViewRedo;
    private ImageView mImageViewSave;

    private UndoRedoHelper mUndoRedoHelper;

    public void initUndoRedo() {
        mUndoRedoHelper.clearHistory();

        final Editable editable = mRichEditText.getText();
        mUndoRedoHelper.addHistory(UndoRedoHelper.INIT_ACTION, 0, null, null,
                RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
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
//            mRichEditText.getIndicatorText().clearSpans(); ///[FIX#误删除了其它有用的spans！]
            SpanUtil.clearAllSpans(mClassMap, mRichEditText.getText());

            ///执行setSpanFromSpanBeans及后处理
            postSetSpanFromSpanBeans(null, -1, RichEditorToolbarHelper.fromByteArray(mRichEditText.getText(), action.getBytes()));
        }
    }


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
        mUndoRedoHelper = new UndoRedoHelper(mContext, this);

        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);

        LayoutInflater.from(mContext).inflate(R.layout.layout_tool_bar, this, true);


        /* -------------- ///段落span（带参数）：Head --------------- */
        mTextViewHead = (TextView) findViewById(R.id.tv_head);
        mTextViewHead.setOnClickListener(this);
        mClassMap.put(HeadSpan.class, mTextViewHead);

        /* -------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan --------------- */
        mImageViewAlignNormal = (ImageView) findViewById(R.id.iv_align_normal);
        mImageViewAlignNormal.setOnClickListener(this);
        mClassMap.put(AlignNormalSpan.class, mImageViewAlignNormal);

        mImageViewAlignCenter = (ImageView) findViewById(R.id.iv_align_center);
        mImageViewAlignCenter.setOnClickListener(this);
        mClassMap.put(AlignCenterSpan.class, mImageViewAlignCenter);

        mImageViewAlignOpposite = (ImageView) findViewById(R.id.iv_align_opposite);
        mImageViewAlignOpposite.setOnClickListener(this);
        mClassMap.put(AlignOppositeSpan.class, mImageViewAlignOpposite);

        /* -------------- ///段落span（带初始化参数）：LeadingMargin --------------- */
        mImageViewLeadingMargin = (ImageView) findViewById(R.id.iv_leading_margin);
        mImageViewLeadingMargin.setOnClickListener(this);
        mImageViewLeadingMargin.setOnLongClickListener(this);
        mClassMap.put(CustomLeadingMarginSpan.class, mImageViewLeadingMargin);

        /* -------------- ///段落span（带初始化参数）：Bullet --------------- */
        mImageViewBullet = (ImageView) findViewById(R.id.iv_bullet);
        mImageViewBullet.setOnClickListener(this);
        mImageViewBullet.setOnLongClickListener(this);
        mClassMap.put(CustomBulletSpan.class, mImageViewBullet);

        /* -------------- ///段落span：LineDivider --------------- */
        mImageViewLineDivider = (ImageView) findViewById(R.id.iv_line_divider);
        mImageViewLineDivider.setOnClickListener(this);
        mImageViewLineDivider.setOnLongClickListener(this);
        mClassMap.put(LineDividerSpan.class, mImageViewLineDivider);

        /* -------------- ///段落span（带初始化参数）：Quote --------------- */
        mImageViewQuote = (ImageView) findViewById(R.id.iv_quote);
        mImageViewQuote.setOnClickListener(this);
        mImageViewQuote.setOnLongClickListener(this);
        mClassMap.put(CustomQuoteSpan.class, mImageViewQuote);

        /* -------------- ///段落span（带初始化参数）：List --------------- */
        mImageViewList = (ImageView) findViewById(R.id.iv_list);
        mImageViewList.setOnClickListener(this);
        mImageViewList.setOnLongClickListener(this);
        mClassMap.put(ListSpan.class, mImageViewList);
        ///注意：ListItemSpan也要注册！否则不能保存到草稿等！
        ///而且必须在ListSpan之后！否则loadSpansFromSpanBeans()中的getParentNestSpan()将返回null
        mClassMap.put(ListItemSpan.class, null);

        /* -------------- ///字符span：Bold、Italic --------------- */
        mImageViewBold = (ImageView) findViewById(R.id.iv_bold);
        mImageViewBold.setOnClickListener(this);
        mClassMap.put(BoldSpan.class, mImageViewBold);

        mImageViewItalic = (ImageView) findViewById(R.id.iv_italic);
        mImageViewItalic.setOnClickListener(this);
        mClassMap.put(ItalicSpan.class, mImageViewItalic);

        /* ------------ ///字符span：Underline、StrikeThrough、Subscript、Superscript ------------ */
        mImageViewUnderline = (ImageView) findViewById(R.id.iv_underline);
        mImageViewUnderline.setOnClickListener(this);
        mClassMap.put(CustomUnderlineSpan.class, mImageViewUnderline);

        mImageViewStrikeThrough = (ImageView) findViewById(R.id.iv_strikethrough);
        mImageViewStrikeThrough.setOnClickListener(this);
        mClassMap.put(CustomStrikethroughSpan.class, mImageViewStrikeThrough);

        mImageViewSuperscript = (ImageView) findViewById(R.id.iv_superscript);
        mImageViewSuperscript.setOnClickListener(this);
        mClassMap.put(CustomSuperscriptSpan.class, mImageViewSuperscript);

        mImageViewSubscript = (ImageView) findViewById(R.id.iv_subscript);
        mImageViewSubscript.setOnClickListener(this);
        mClassMap.put(CustomSubscriptSpan.class, mImageViewSubscript);

        /* -------------- ///字符span（带参数）：Code --------------- */
        mImageViewCode = (ImageView) findViewById(R.id.iv_code);
        mImageViewCode.setOnClickListener(this);
        mClassMap.put(CodeSpan.class, mImageViewCode);

        /* -------------- ///字符span（带参数）：ForegroundColor、BackgroundColor --------------- */
        mImageViewForegroundColor = (ImageView) findViewById(R.id.iv_foreground_color);
        mImageViewForegroundColor.setOnClickListener(this);
        mClassMap.put(CustomForegroundColorSpan.class, mImageViewForegroundColor);

        mImageViewBackgroundColor = (ImageView) findViewById(R.id.iv_background_color);
        mImageViewBackgroundColor.setOnClickListener(this);
        mClassMap.put(CustomBackgroundColorSpan.class, mImageViewBackgroundColor);

        /* -------------- ///字符span（带参数）：FontFamily --------------- */
        mTextViewFontFamily = (TextView) findViewById(R.id.tv_font_family);
        mTextViewFontFamily.setOnClickListener(this);
        mClassMap.put(CustomFontFamilySpan.class, mTextViewFontFamily);

        /* -------------- ///字符span（带参数）：AbsoluteSize --------------- */
        mTextViewAbsoluteSize = (TextView) findViewById(R.id.tv_absolute_size);
        mTextViewAbsoluteSize.setOnClickListener(this);
        mClassMap.put(CustomAbsoluteSizeSpan.class, mTextViewAbsoluteSize);

        /* -------------- ///字符span（带参数）：RelativeSize --------------- */
        mTextViewRelativeSize = (TextView) findViewById(R.id.tv_relative_size);
        mTextViewRelativeSize.setOnClickListener(this);
        mClassMap.put(CustomRelativeSizeSpan.class, mTextViewRelativeSize);

        /* -------------- ///字符span（带参数）：ScaleX --------------- */
        mTextViewScaleX = (TextView) findViewById(R.id.tv_scale_x);
        mTextViewScaleX.setOnClickListener(this);
        mClassMap.put(CustomScaleXSpan.class, mTextViewScaleX);

        /* -------------- ///字符span（带参数）：URL --------------- */
        mImageViewURL = (ImageView) findViewById(R.id.iv_url);
        mImageViewURL.setOnClickListener(this);
        mClassMap.put(CustomURLSpan.class, mImageViewURL);

        /* -------------- ///字符span（带参数）：Image --------------- */
        mImageViewVideo = (ImageView) findViewById(R.id.iv_video);
        mImageViewAudio = (ImageView) findViewById(R.id.iv_audio);
        mImageViewImage = (ImageView) findViewById(R.id.iv_image);
        mImageViewVideo.setOnClickListener(this);
        mImageViewAudio.setOnClickListener(this);
        mImageViewImage.setOnClickListener(this);
        mClassMap.put(VideoSpan.class, mImageViewVideo);
        mClassMap.put(AudioSpan.class, mImageViewAudio);
        mClassMap.put(CustomImageSpan.class, mImageViewImage);

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

                final int selectionStart = Selection.getSelectionStart(editable);
                final int selectionEnd = Selection.getSelectionEnd(editable);
                if (selectionStart == -1 || selectionEnd == -1) {
                    return;
                }

                for (Class clazz : mClassMap.keySet()) {
                    if (mClassMap.get(clazz) == null) {
                        continue;
                    }
                    if (isParagraphStyle(clazz)) {
                        mClassMap.get(clazz).setSelected(false);
                        if (isNestParagraphStyle(clazz)) {
                            adjustNestParagraphStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, true);
                        } else {
                            adjustParagraphStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, true);
                        }
                        updateParagraphView(mContext, mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd);
                    } else if (isCharacterStyle(clazz)) {
                        mClassMap.get(clazz).setSelected(false);
                        if (isBlockCharacterStyle(clazz)) {
                            adjustBlockCharacterStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, true);
                        } else {
                            adjustCharacterStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, true);
                        }
                        updateCharacterStyleView(mContext, mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd);
                    }
                }

                ///[Preview]
                updatePreview();

                ///[Undo/Redo]
                mUndoRedoHelper.addHistory(UndoRedoHelper.CLEAR_SPANS_ACTION, selectionStart, null, null,
                        RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
            }
        });

        /* -------------- ///[草稿Draft] --------------- */
        mImageViewSaveDraft = (ImageView) findViewById(R.id.iv_save_draft);
        mImageViewSaveDraft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Editable editable = mRichEditText.getText();

                final byte[] bytes = RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), true);
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

                final TextBean textBean = ParcelUtil.unmarshall(Base64.decode(draftText, Base64.DEFAULT), TextBean.CREATOR);
                if (textBean != null) {
                    final Editable beforeEditable = mRichEditText.getText();
                    final int selectionStart = Selection.getSelectionStart(beforeEditable);
                    final int selectionEnd = Selection.getSelectionEnd(beforeEditable);
                    final String beforeChange = beforeEditable.toString();

                    ///忽略TextWatcher
                    isSkipTextWatcher = true;
                    mRichEditText.setText(textBean.getText());
                    isSkipTextWatcher = false;

                    final Editable editable = mRichEditText.getText();
                    final List<SpanBean> spanBeans = textBean.getSpans();
                    ///执行setSpanFromSpanBeans及后处理
                    postSetSpanFromSpanBeans(null, -1, RichEditorToolbarHelper.loadSpansFromSpanBeans(spanBeans, editable));

                    ///[FIX#当光标位置未发生变化时不会调用selectionChanged()来更新view的select状态！]
                    ///解决：此时应手动调用selectionChanged()来更新view的select状态
                    if (selectionStart == selectionEnd && selectionEnd == 0) {
                        selectionChanged(0, 0);
                    } else {
                        Selection.setSelection(editable, 0);
                    }

                    ///[Preview]
                    updatePreview();

                    ///[Undo/Redo]
                    mUndoRedoHelper.addHistory(UndoRedoHelper.RESTORE_DRAFT_ACTION, 0, beforeChange, editable.toString(),
                            RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));

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
        mImageViewUndo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndoRedoHelper.undo();

                ///[Preview]
                updatePreview();
            }
        });
        mImageViewRedo = (ImageView) findViewById(R.id.iv_redo);
        mImageViewRedo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndoRedoHelper.redo();

                ///[Preview]
                updatePreview();
            }
        });
        mImageViewSave = (ImageView) findViewById(R.id.iv_save);
        mImageViewSave.setOnClickListener(new OnClickListener() {
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

    private int getActionId(View view) {
        if (view == mTextViewHead) {
            return UndoRedoHelper.CHANGE_HEAD_SPAN_ACTION;
        } else if (view == mImageViewAlignNormal) {
            return UndoRedoHelper.CHANGE_ALIGN_NORMAL_SPAN_ACTION;
        } else if (view == mImageViewAlignCenter) {
            return UndoRedoHelper.CHANGE_ALIGN_CENTER_SPAN_ACTION;
        } else if (view == mImageViewAlignOpposite) {
            return UndoRedoHelper.CHANGE_ALIGN_OPPOSITE_SPAN_ACTION;
        } else if (view == mImageViewLeadingMargin) {
            return UndoRedoHelper.CHANGE_LEADING_MARGIN_SPAN_ACTION;
        } else if (view == mImageViewBullet) {
            return UndoRedoHelper.CHANGE_BULLET_SPAN_ACTION;
        } else if (view == mImageViewLineDivider) {
            return UndoRedoHelper.CHANGE_LINE_DIVIDER_SPAN_ACTION;
        } else if (view == mImageViewQuote) {
            return UndoRedoHelper.CHANGE_QUOTE_SPAN_ACTION;
        } else if (view == mImageViewList) {
            return UndoRedoHelper.CHANGE_LIST_SPAN_ACTION;
        }

        else if (view == mImageViewBold) {
            return UndoRedoHelper.CHANGE_BOLD_SPAN_ACTION;
        } else if (view == mImageViewItalic) {
            return UndoRedoHelper.CHANGE_ITALIC_SPAN_ACTION;
        } else if (view == mImageViewUnderline) {
            return UndoRedoHelper.CHANGE_UNDERLINE_SPAN_ACTION;
        } else if (view == mImageViewStrikeThrough) {
            return UndoRedoHelper.CHANGE_STRIKE_THROUGH_SPAN_ACTION;
        } else if (view == mImageViewSubscript) {
            return UndoRedoHelper.CHANGE_SUBSCRIPT_SPAN_ACTION;
        } else if (view == mImageViewSuperscript) {
            return UndoRedoHelper.CHANGE_SUPERSCRIPT_SPAN_ACTION;
        } else if (view == mImageViewCode) {
            return UndoRedoHelper.CHANGE_CODE_SPAN_ACTION;
        } else if (view == mImageViewForegroundColor) {
            return UndoRedoHelper.CHANGE_FOREGROUND_COLOR_SPAN_ACTION;
        } else if (view == mImageViewBackgroundColor) {
            return UndoRedoHelper.CHANGE_BACKGROUND_COLOR_SPAN_ACTION;
        } else if (view == mTextViewFontFamily) {
            return UndoRedoHelper.CHANGE_FONT_FAMILY_SPAN_ACTION;
        } else if (view == mTextViewAbsoluteSize) {
            return UndoRedoHelper.CHANGE_ABSOLUTE_SIZE_SPAN_ACTION;
        } else if (view == mTextViewRelativeSize) {
            return UndoRedoHelper.CHANGE_RELATIVE_SIZE_SPAN_ACTION;
        } else if (view == mTextViewScaleX) {
            return UndoRedoHelper.CHANGE_SCALE_X_SPAN_ACTION;
        } else if (view == mImageViewURL) {
            return UndoRedoHelper.CHANGE_URL_SPAN_ACTION;
        } else if (view == mImageViewImage) {
            return UndoRedoHelper.CHANGE_IMAGE_SPAN_ACTION;
        } else if (view == mImageViewVideo) {
            return UndoRedoHelper.CHANGE_VIDEO_SPAN_ACTION;
        } else if (view == mImageViewAudio) {
            return UndoRedoHelper.CHANGE_AUDIO_SPAN_ACTION;
        } else {
            return -1;
        }
    }


    /* ----------------- ///[onClick]点击更新ImageView，并且当selectionStart != selectionEnd时改变selection的span ------------------ */
    @Override
    public void onClick(final View view) {
        final Editable editable = mRichEditText.getText();

        final Class clazz = RichEditorToolbarHelper.getClassMapKey(mClassMap, view);
        if (isParagraphStyle(clazz)) {
            final int selectionStart = Selection.getSelectionStart(editable);
            final int selectionEnd = Selection.getSelectionEnd(editable);

            ///段落span（带参数）：Head
            if (view == mTextViewHead) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = view.getTag() == null ? -1 : (int) view.getTag();

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.head_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (view.getTag() == null || which != (int) view.getTag()) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(which);

                                    ///改变selection的span
                                    applyParagraphStyleSpans(view, editable);

                                    ///[Preview]
                                    updatePreview();

                                    ((TextView) view).setText(HeadSpan.HEADING_LABELS[which]);
                                }

                                dialog.dismiss();
                            }
                        })
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
                                applyParagraphStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.head));

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return;
            }

            ///段落span：LineDivider
            else if (view == mImageViewLineDivider) {
                ///当下列情况，不能点击LineDivider：
                ///不是单光标；或光标处于文本尾部；或光标处字符不为'\n'；或光标前一个字符不为'\n'
                if (selectionStart != selectionEnd || selectionStart == editable.length()
                        || editable.charAt(selectionStart) != '\n'
                        || selectionStart != 0 && editable.charAt(selectionStart - 1) != '\n') {
                    return;
                }
            }

            ///段落span（带初始化参数）：Quote
            else if (view == mImageViewQuote) {
                new AlertDialog.Builder(mContext)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///改变selection的span
                                applyParagraphStyleSpans(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        })
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
                                applyParagraphStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        ///插入新建span
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return;
            }

            ///段落span（带初始化参数）：List
            else if (view == mImageViewList) {
                final boolean isInsertNewSpan = view.isSelected() && selectionStart < selectionEnd;
                final int listType = view.getTag(R.id.list_list_type) == null ? Integer.MIN_VALUE :  (int) view.getTag(R.id.list_list_type);
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = view.getTag(R.id.list_list_type) == null ? -1 :
                        ArrayUtil.getIntIndex(mContext, R.array.list_type_ids, (int) view.getTag(R.id.list_list_type));
                final View listSpanDialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_click_list_span_dialog, null);
                final AlertDialog listSpanAlertDialog = new AlertDialog.Builder(mContext)
                        .setView(listSpanDialogView)
                        .setSingleChoiceItems(R.array.list_type_labels, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final int listType = ArrayUtil.getIntItem(mContext, R.array.list_type_ids, which);
                                final EditText editTextStart = ((AlertDialog) dialog).findViewById(R.id.et_start);
                                final Switch switchIsReversed = ((AlertDialog) dialog).findViewById(R.id.switch_is_reversed);
                                editTextStart.setEnabled(isListTypeOrdered(listType));
                                switchIsReversed.setEnabled(isListTypeOrdered(listType));
                            }
                        })
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
                                applyParagraphStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(R.id.list_start, null);
                                view.setTag(R.id.list_is_reversed, null);
                                view.setTag(R.id.list_list_type, null);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        ///插入新建span
                        .setNegativeButton(isInsertNewSpan ? R.string.insert : android.R.string.cancel, isInsertNewSpan ? new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///获取listTypeIndex并由此得到对应的listType
                                final int listTypeIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                final int listType = ArrayUtil.getIntItem(mContext, R.array.list_type_ids, listTypeIndex);

                                final EditText editTextStart = ((AlertDialog) dialog).findViewById(R.id.et_start);
                                final int start = Integer.parseInt(editTextStart.getText().toString());
                                final Switch switchIsReversed = ((AlertDialog) dialog).findViewById(R.id.switch_is_reversed);
                                final boolean isReversed = switchIsReversed.isChecked();

                                ///保存参数到view tag
                                view.setTag(R.id.list_start, start);
                                view.setTag(R.id.list_is_reversed, isReversed);
                                view.setTag(R.id.list_list_type, listType);

                                ///改变selection的span
                                applyParagraphStyleSpans(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        } : null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///获取listTypeIndex并由此得到对应的listType
                                final int listTypeIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                final int listType = ArrayUtil.getIntItem(mContext, R.array.list_type_ids, listTypeIndex);

                                final EditText editTextStart = ((AlertDialog) dialog).findViewById(R.id.et_start);
                                final int start = Integer.parseInt(editTextStart.getText().toString());
                                final Switch switchIsReversed = ((AlertDialog) dialog).findViewById(R.id.switch_is_reversed);
                                final boolean isReversed = switchIsReversed.isChecked();

                                if (view.getTag(R.id.list_start) == null || start != (int) view.getTag(R.id.list_start)
                                        || view.getTag(R.id.list_is_reversed) == null || isReversed != (boolean) view.getTag(R.id.list_is_reversed)
                                        || view.getTag(R.id.list_list_type) == null || listType != (int) view.getTag(R.id.list_list_type)) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(R.id.list_start, start);
                                    view.setTag(R.id.list_is_reversed, isReversed);
                                    view.setTag(R.id.list_list_type, listType);

                                    ///改变selection的span
                                    applyParagraphStyleSpans(view, editable);

                                    ///[Preview]
                                    updatePreview();
                                }
                            }
                        })
                        .show();

                ///初始化AlertDialog
                final boolean isEnabled = view.getTag(R.id.list_list_type) != null && isListTypeOrdered(listType);
                final EditText editTextStart = (EditText) listSpanAlertDialog.findViewById(R.id.et_start);
                editTextStart.setEnabled(isEnabled);
                final int start = view.getTag(R.id.list_start) == null ? 1 :  (int) view.getTag(R.id.list_start);
                editTextStart.setText(String.valueOf(start));
                final Switch switchIsReversed = (Switch) listSpanAlertDialog.findViewById(R.id.switch_is_reversed);
                switchIsReversed.setEnabled(isEnabled);
                final boolean isReversed = view.getTag(R.id.list_is_reversed) != null && (boolean) view.getTag(R.id.list_is_reversed);
                switchIsReversed.setChecked(isReversed);

                return;
            }

            view.setSelected(!view.isSelected());

            applyParagraphStyleSpans(view, editable);

            ///同组选择互斥
            ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan
            if (view.isSelected()) {
                if (view == mImageViewAlignNormal) {
                    if (mImageViewAlignCenter.isSelected()) {
                        mImageViewAlignCenter.setSelected(false);
                        applyParagraphStyleSpans(mImageViewAlignCenter, editable);
                    }
                    if (mImageViewAlignOpposite.isSelected()) {
                        mImageViewAlignOpposite.setSelected(false);
                        applyParagraphStyleSpans(mImageViewAlignOpposite, editable);
                    }
                } else if (view == mImageViewAlignCenter) {
                    if (mImageViewAlignNormal.isSelected()) {
                        mImageViewAlignNormal.setSelected(false);
                        applyParagraphStyleSpans(mImageViewAlignNormal, editable);
                    }
                    if (mImageViewAlignOpposite.isSelected()) {
                        mImageViewAlignOpposite.setSelected(false);
                        applyParagraphStyleSpans(mImageViewAlignOpposite, editable);
                    }
                } else if (view == mImageViewAlignOpposite) {
                    if (mImageViewAlignNormal.isSelected()) {
                        mImageViewAlignNormal.setSelected(false);
                        applyParagraphStyleSpans(mImageViewAlignNormal, editable);
                    }
                    if (mImageViewAlignCenter.isSelected()) {
                        mImageViewAlignCenter.setSelected(false);
                        applyParagraphStyleSpans(mImageViewAlignCenter, editable);
                    }
                }
            }

        } else if (isCharacterStyle(clazz)) {

            ///字符span（带参数）：ForegroundColor、BackgroundColor
            if (view == mImageViewForegroundColor || view == mImageViewBackgroundColor) {
                ///颜色选择器
                final ColorPickerDialogBuilder colorPickerDialogBuilder = ColorPickerDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                                if (colorDrawable == null || colorDrawable.getColor() != selectedColor) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///设置View的背景颜色
                                    view.setBackgroundColor(selectedColor);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ///[Preview]
                                    updatePreview();
                                }
                            }
                        })
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
                                applyCharacterStyleSpans(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                ///初始化颜色为View的背景颜色
                if (view.isSelected()) {
                    final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                    colorPickerDialogBuilder.initialColor(colorDrawable.getColor());
                }
                colorPickerDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：FontFamily
            else if (view == mTextViewFontFamily) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.font_family_items, (String) view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.font_family_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final String family = ArrayUtil.getStringItem(mContext, R.array.font_family_items, which);

                                if (!TextUtils.equals(family, (String) view.getTag())) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(family);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ///[Preview]
                                    updatePreview();

                                    ((TextView) view).setText(family);
                                }

                                dialog.dismiss();
                            }
                        })
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
                                applyCharacterStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///[Preview]
                                updatePreview();

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.font_family));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return;
            }

            ///字符span（带参数）：AbsoluteSize
            else if (view == mTextViewAbsoluteSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.absolute_size_items, (String) view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.absolute_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final String size = ArrayUtil.getStringItem(mContext, R.array.absolute_size_items, which);

                                if (!TextUtils.equals(size, (String) view.getTag())) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(size);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ///[Preview]
                                    updatePreview();

                                    ((TextView) view).setText(size);
                                }

                                dialog.dismiss();
                            }
                        })
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
                                applyCharacterStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.absolute_size));

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return;
            }

            ///字符span（带参数）：RelativeSize
            else if (view == mTextViewRelativeSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.relative_size_items, (String) view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.relative_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final String sizeChange = ArrayUtil.getStringItem(mContext, R.array.relative_size_items, which);

                                if (!TextUtils.equals(sizeChange, (String) view.getTag())) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(sizeChange);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ///[Preview]
                                    updatePreview();

                                    ((TextView) view).setText(sizeChange);
                                }

                                dialog.dismiss();
                            }
                        })
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
                                applyCharacterStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.relative_size));

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return;
            }

            ///字符span（带参数）：ScaleX
            else if (view == mTextViewScaleX) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.scale_x_items, (String) view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.scale_x_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final String scaleX = ArrayUtil.getStringItem(mContext, R.array.scale_x_items, which);

                                if (!TextUtils.equals(scaleX, (String) view.getTag())) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(scaleX);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ///[Preview]
                                    updatePreview();

                                    ((TextView) view).setText(scaleX);
                                }

                                dialog.dismiss();
                            }
                        })
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
                                applyCharacterStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(mContext.getString(R.string.scale_x));

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                return;
            }

            ///字符span（带参数）：URL
            else if (view == mImageViewURL) {
                final ClickURLSpanDialogBuilder clickUrlSpanDialogBuilder = (ClickURLSpanDialogBuilder) ClickURLSpanDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new ClickURLSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String text, String url) {
                                ///参数校验：两项都为空则代表维持不变、不做任何处理
                                ///注意：某项为空、或值相同即代表该项维持不变，不为空且值不同则代表该项改变
                                if (text.length() == 0 && url.length() == 0) {  //////??????url正则表达式
                                    return;
                                }
                                final ArrayList<CustomURLSpan> selectedSpans = SpanUtil.getSelectedSpans(CustomURLSpan.class, mRichEditText);
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
                                applyCharacterStyleSpans(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        })
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
                                applyCharacterStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(R.id.url_text, null);
                                view.setTag(R.id.url_url, null);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);

                final String text = (String) view.getTag(R.id.url_text);
                final String url = (String) view.getTag(R.id.url_url);
                clickUrlSpanDialogBuilder.initial(text, url);
                clickUrlSpanDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：Image
            else if (view == mImageViewImage || view == mImageViewVideo || view == mImageViewAudio) {
                final int mediaType = view == mImageViewVideo ? 1 : view == mImageViewAudio ? 2 : 0;
                clickImageSpanDialogBuilder = ClickImageSpanDialogBuilder
                        .with(mContext, mediaType)
                        .setPositiveButton(android.R.string.ok, new ClickImageSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String uri, String src, int width, int height, int align) {
                                ///参数校验：两项都为空则代表维持不变、不做任何处理n
                                ///注意：某项为空、或值相同即代表该项维持不变，不为空且值不同则代表该项改变
                                if (view != mImageViewImage && uri.length() == 0 || src.length() == 0 || width == 0 || height == 0) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///把width\height\align保存到text中
                                final String text = view == mImageViewImage ?
                                        String.format(getContext().getResources().getString(R.string.image_span_text), src, width, height, align)
                                        : String.format(getContext().getResources().getString(R.string.image_span_media_text), uri, src, width, height, align);

                                ///保存参数到view tag
                                view.setTag(R.id.image_text, text);
                                view.setTag(R.id.image_uri, uri);
                                view.setTag(R.id.image_src, src);
                                view.setTag(R.id.image_width, width);
                                view.setTag(R.id.image_height, height);
                                view.setTag(R.id.image_align, align);

                                ///改变selection的span
                                applyCharacterStyleSpans(view, editable);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.clear, new ClickImageSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String uri, String src, int width, int height, int align) {
                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(R.id.image_text, null);
                                view.setTag(R.id.image_uri, null);
                                view.setTag(R.id.image_src, null);
                                view.setTag(R.id.image_width, null);
                                view.setTag(R.id.image_height, null);
                                view.setTag(R.id.image_align, null);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setImageFilePath(mImageFilePath)
                        ///注意：加入null的强转，为了避免混淆ClickImageSpanDialogBuilder和BaseDialogBuilder的setNegativeButton()方法！
                        .setNegativeButton(android.R.string.cancel, (ClickImageSpanDialogBuilder.OnClickListener) null);

                final String uri = (String) view.getTag(R.id.image_uri);
                final String src = (String) view.getTag(R.id.image_src);
                final int width = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                final int height = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                final int align = view.getTag(R.id.image_align) == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                clickImageSpanDialogBuilder.initial(uri, src, width, height, align, mImageOverrideWidth, mImageOverrideHeight);
                clickImageSpanDialogBuilder.build().show();

                return;
            }

            view.setSelected(!view.isSelected());

            applyCharacterStyleSpans(view, editable);
        }

        ///[Preview]
        updatePreview();
    }

    @Override
    public boolean onLongClick(final View view) {
        ///段落span（带初始化参数）：LeadingMargin
        if (view == mImageViewLeadingMargin) {
            ((LongClickLeadingMarginSpanDialogBuilder) LongClickLeadingMarginSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LongClickLeadingMarginSpanDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indent) {
                            mLeadingMarginSpanIndent = indent;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null))
                    .initial(mLeadingMarginSpanIndent)
                    .build().show();

            return true;
        }

        ///段落span（带初始化参数）：Bullet
        else if (view == mImageViewBullet) {
            ((LongClickBulletSpanDialogBuilder) LongClickBulletSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LongClickBulletSpanDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors, int stripWidth, int gapWidth) {
                            mBulletColor = selectedColor;
                            mBulletSpanRadius = stripWidth;
                            mBulletSpanGapWidth = gapWidth;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null))
                    .initial(mBulletColor, mBulletSpanRadius, mBulletSpanGapWidth)
                    .build().show();

            return true;
        }

        ///段落span（带初始化参数）：LineDivider
        else if (view == mImageViewLineDivider) {
            ((LongClickLineDividerDialogBuilder) LongClickLineDividerDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LongClickLineDividerDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int marginTop, int marginBottom) {
                            mLineDividerSpanMarginTop = marginTop;
                            mLineDividerSpanMarginBottom = marginBottom;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null))
                    .initial(mLineDividerSpanMarginTop, mLineDividerSpanMarginBottom)
                    .build().show();

            return true;
        }

        ///段落span（带初始化参数）：Quote
        else if (view == mImageViewQuote) {
            ((LongClickQuoteSpanDialogBuilder) LongClickQuoteSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LongClickQuoteSpanDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors, int stripWidth, int gapWidth) {
                            mQuoteSpanColor = selectedColor;
                            mQuoteSpanStripWidth = stripWidth;
                            mQuoteSpanGapWidth = gapWidth;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null))
                    .initial(mQuoteSpanColor, mQuoteSpanStripWidth, mQuoteSpanGapWidth)
                    .build().show();

            return true;
        }

        ///段落span（带初始化参数）：List
        else if (view == mImageViewList) {
            ((LongClickListSpanDialogBuilder) LongClickListSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LongClickListSpanDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indicatorMargin, int indicatorWidth, int indicatorGapWidth, int indicatorColor, Integer[] allColors) {
                            mIndicatorMargin = indicatorMargin;
                            mIndicatorWidth = indicatorWidth;
                            mIndicatorGapWidth = indicatorGapWidth;
                            mIndicatorColor = indicatorColor;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null))
                    .initial(mIndicatorMargin, mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor)
                    .build().show();

            return true;
        }

        return false;
    }

    private void applyParagraphStyleSpans(View view, Editable editable) {
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);

        String beforeChange = null;
        String afterChange = null;
        ///[FIX#当光标选择区间的尾部位于文本尾部空行时，点击select段落view后出现首尾相同的新span！且上一行显示view被selected]
        ///解决：此时补插入一个'\n'，并修改历史记录
        if (view.isSelected() && selectionEnd == editable.length()
                && (editable.length() == 0 || editable.charAt(editable.length() - 1) == '\n')) {
            ///注意：不能用isSkipUndoRedo！会造成死循环
            isSkipTextWatcher = true;
            editable.append('\n');

            ///调整光标位置到append之前的位置
            Selection.setSelection(editable, selectionEnd);
            isSkipTextWatcher = false;

            ///调整将要保存的历史记录的beforeChange/afterChange
            beforeChange = "";
            afterChange = "\n";
        }

        final Class clazz = RichEditorToolbarHelper.getClassMapKey(mClassMap, view);
        if (isNestParagraphStyle(clazz)) {
            adjustNestParagraphStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true);
        } else {
            adjustParagraphStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true);
        }

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            mUndoRedoHelper.addHistory(getActionId(view), selectionEnd, beforeChange, afterChange,
                    RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
        }
    }

    private void applyCharacterStyleSpans(View view, Editable editable) {
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);
        ///当selectionStart != selectionEnd时改变selection的span
        final Class clazz = RichEditorToolbarHelper.getClassMapKey(mClassMap, view);
        if (selectionStart == -1 || selectionEnd == -1) {
            return;
        }

        if (isBlockCharacterStyle(clazz)) {
            adjustBlockCharacterStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true);
        } else {
            adjustCharacterStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true);
        }

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            if (isBlockCharacterStyle(clazz)) {
                final int afterSelectionStart = Selection.getSelectionStart(editable);
                final int afterSelectionEnd = Selection.getSelectionEnd(editable);
                mUndoRedoHelper.addHistory(getActionId(view), selectionStart,
                        editable.subSequence(selectionStart, selectionEnd).toString(),
                        editable.subSequence(afterSelectionStart, afterSelectionEnd).toString(),
                        RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
            } else{
                mUndoRedoHelper.addHistory(getActionId(view), selectionStart, null, null,
                        RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
            }
        }
    }


    /* ----------------- ///[selectionChanged]根据selection更新工具条按钮 ------------------ */
    @Override
    public void selectionChanged(int selectionStart, int selectionEnd) {
        if (DEBUG) Log.d("TAG", "============= selectionChanged ============" + selectionStart + ", " + selectionEnd);

        ///[enableSelectionChange]禁止onSelectionChanged()
        if (!enableSelectionChange || isSkipTextWatcher || isSkipUndoRedo) {
            return;
        }

        final Editable editable = mRichEditText.getText();

        for (Class clazz : mClassMap.keySet()) {
            if (mClassMap.get(clazz) != null) {
                if (isParagraphStyle(clazz)) {
                    updateParagraphView(mContext, mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd);
                } else if (isCharacterStyle(clazz)) {
                    updateCharacterStyleView(mContext, mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd);
                }
            }

            ///test
            if (DEBUG) Util.testOutput(editable, clazz);
        }
    }


    /* ----------------- ///[TextWatcher] ------------------ */
    ///注意：语言环境为english时，存在before/count/after都大于0的情况！此时start为单词开始处（以空格或回车分割）
    ///解决方法：android:inputType="textVisiblePassword"

    public boolean isSkipTextWatcher = false;
    public boolean isSkipUndoRedo = false;
    private final class RichTextWatcher implements TextWatcher {
        ///[Undo/Redo]
        private int mStart;
        private String mBeforeChange;
        private String mAfterChange;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            ///忽略TextWatcher
            if (isSkipTextWatcher) {
                return;
            }

            ///[Undo/Redo]
            if (!isSkipUndoRedo) {
                mBeforeChange = s.subSequence(start, start + count).toString();
            }

            if (count > 0) {
                for (Class clazz : mClassMap.keySet()) {
                    ///清除掉已经被删除的span，否则将会产生多余的无效span！
                    SpanUtil.removeSpans(clazz, mRichEditText.getText(), start, start + count);

                    ///[FIX#当两行span不同时（如h1和h6），选择第二行行首后回退删除'\n'，此时View仍然在第二行，应该更新为第一行！]
                    final Editable editable = mRichEditText.getText();
                    if (count == 1 && isParagraphStyle(clazz) && mClassMap.get(clazz)!= null
                            && editable.charAt(start) == '\n') { ///如果含回车
                        updateParagraphView(mContext, mClassMap.get(clazz), clazz, editable, start, start);
                    }
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ///忽略TextWatcher
            if (isSkipTextWatcher) {
                return;
            }

            ///[Undo/Redo]
            if (!isSkipUndoRedo) {
                mAfterChange = s.subSequence(start, start + count).toString();
                mStart = start;
            }

            final Editable editable = mRichEditText.getText();
            final int selectionStart = start;
            int selectionEnd = start + count;

//            if (count > 0) {
//                final int next = editable.nextSpanTransition(selectionStart - 1, selectionEnd, ListSpan.class);
//                if (next < selectionEnd) {
//                    final ListItemSpan[] selectionStartListItemSpans = editable.getSpans(selectionStart, selectionStart, ListItemSpan.class);
//                    for (ListItemSpan listItemSpan : selectionStartListItemSpans) {
//                        final int listItemSpanStart = editable.getSpanStart(listItemSpan);
//                        final int listItemSpanEnd = editable.getSpanEnd(listItemSpan);
//                        if (listItemSpanStart < selectionStart && editable.charAt(selectionStart - 1) != '\n') {
//                            ///注意：不能用isSkipUndoRedo！会造成死循环
//                            isSkipTextWatcher = true;
//                            editable.insert(selectionStart, "\n");
//
////                            ///调整光标位置到append之前的位置
////                            Selection.setSelection(editable, selectionEnd);
//                            isSkipTextWatcher = false;
//
//                            ///调整使其不包含"\n"
//                            editable.setSpan(listItemSpan, listItemSpanStart, listItemSpanEnd, getSpanFlag(ListSpan.class));
//
//                            ///调整将要保存的历史记录的beforeChange/afterChange
//                            ///[Undo/Redo]
//                            if (!isSkipUndoRedo) {
//                                mAfterChange = editable.subSequence(start, ++selectionEnd).toString();
//                            }
//
//                            break;
//                        }
//                    }
//                    final ListItemSpan[] selectionEndListItemSpans = editable.getSpans(selectionEnd, selectionEnd, ListItemSpan.class);
//                    for (ListItemSpan listItemSpan : selectionEndListItemSpans) {
////                        final int listItemSpanStart = editable.getSpanStart(listItemSpan);
//                        final int listItemSpanEnd = editable.getSpanEnd(listItemSpan);
//                        if (selectionEnd == listItemSpanEnd && editable.charAt(selectionEnd - 1) != '\n') {
//                            ///注意：不能用isSkipUndoRedo！会造成死循环
//                            isSkipTextWatcher = true;
//                            editable.insert(selectionEnd, "\n");
//
////                            ///调整光标位置到append之前的位置
////                            Selection.setSelection(editable, selectionEnd);
//                            isSkipTextWatcher = false;
//
//                            ///调整将要保存的历史记录的beforeChange/afterChange
//                            ///[Undo/Redo]
//                            if (!isSkipUndoRedo) {
//                                mAfterChange = editable.subSequence(start, ++selectionEnd).toString();
//                            }
//
//                            break;
//                        }
//                    }
//                }
//            }

            for (Class clazz : mClassMap.keySet()) {
                if (isParagraphStyle(clazz) && mClassMap.get(clazz)!= null) {
                    ///[FIX#当光标选择区间的尾部位于文本尾部空行时，段落view被select时，出现首尾相同的新span！且上一行显示view被selected]
                    ///解决：此时补插入一个'\n'
                    if (mClassMap.get(clazz).isSelected() && selectionEnd == editable.length()
                            && (editable.length() == 0 || editable.charAt(editable.length() - 1) == '\n')) {
                        ///注意：不能用isSkipUndoRedo！会造成死循环
                        isSkipTextWatcher = true;
                        editable.append('\n');

                        ///调整光标位置到append之前的位置
                        Selection.setSelection(editable, selectionEnd);
                        isSkipTextWatcher = false;

                        ///调整将要保存的历史记录的beforeChange/afterChange
                        ///[Undo/Redo]
                        if (!isSkipUndoRedo) {
                            mAfterChange = editable.subSequence(start, selectionEnd + 1).toString();
                        }

                        ///补插后文本长度不再等于selectionEnd，所以跳出循环结束
                        break;
                    }
                }
            }

            for (Class clazz : mClassMap.keySet()) {
                if (isParagraphStyle(clazz) && mClassMap.get(clazz)!= null) {
                    final int currentParagraphStart = SpanUtil.getParagraphStart(editable, selectionStart);
                    ///[FIX#在行首回车时，上面产生的空行应该不被选中！]
                    if (count > 0 && start == currentParagraphStart && editable.charAt(start) != '\n'
                            && editable.subSequence(selectionStart, selectionEnd).toString().contains("\n")) {  ///并且含回车
                        mClassMap.get(clazz).setSelected(false);
                    }

                    ///注意：因为可能'\n'被删除了，所以删除时也要adjust！
                    if (isNestParagraphStyle(clazz)) {
                        adjustNestParagraphStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false);
                    } else {
                        adjustParagraphStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false);
                    }
                } else if (isCharacterStyle(clazz) && mClassMap.get(clazz)!= null) {
                    if (isBlockCharacterStyle(clazz)) {
                        adjustBlockCharacterStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false);
                    } else {
                        adjustCharacterStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            ///忽略TextWatcher
            if (isSkipTextWatcher) {
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
            if (!isSkipUndoRedo) {
                mUndoRedoHelper.addHistory(UndoRedoHelper.CHANGE_TEXT_ACTION, mStart, mBeforeChange, mAfterChange,
                        RichEditorToolbarHelper.toByteArray(mClassMap, s, 0, s.length(), false));
            }
        }
    }


    /* ------------------------------------------------------------------------------------------ */
    private <T extends NestSpan> void adjustNestParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply) {
        final int firstParagraphStart = SpanUtil.getParagraphStart(editable, start);
        final int lastParagraphEnd = SpanUtil.getParagraphEnd(editable, end);

        ///注意：当选中文尾的空行时会出现firstParagraphStart == lastParagraphEnd的情况，此时只切换view的selected状态、不调整spans！
        if (firstParagraphStart == lastParagraphEnd) {
            return;
        }

        innerAdjustNestParagraphStyleSpans(view, clazz, editable, start, end, firstParagraphStart, lastParagraphEnd, isApply);
    }

    private <T extends NestSpan> void innerAdjustNestParagraphStyleSpans(
            View view, Class<T> clazz, Editable editable,
            int start, int end, int firstParagraphStart, int lastParagraphEnd, boolean isApply) {
        if (isApply) {
            if (start == end) {
                final T parentSpan = getParentSpan(view, clazz, editable, start, end, null, false);
                if (parentSpan == null) {
                    if (view != null && view.isSelected()) {
                        final Object newSpan = createNewSpan(view, clazz, editable, firstParagraphStart, lastParagraphEnd, null, null);

                        ///段落span（带初始化参数）：List
                        if (clazz == ListSpan.class) {
                            createChildrenListItemSpans(editable, (ListSpan) newSpan, firstParagraphStart, lastParagraphEnd,
                                    mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
                        }

                    }
                } else {
                    if (view != null && view.isSelected()) {

                        ///段落span（带初始化参数）：List
                        if (clazz == ListSpan.class) {
                            if (view.getTag(R.id.list_start) != null
                                    && view.getTag(R.id.list_is_reversed) != null
                                    && view.getTag(R.id.list_list_type) != null) {
                                final int listStart = (int) view.getTag(R.id.list_start);
                                final boolean isReversed = (boolean) view.getTag(R.id.list_is_reversed);
                                final int listType = (int) view.getTag(R.id.list_list_type);
                                ((ListSpan) parentSpan).setStart(listStart);
                                ((ListSpan) parentSpan).isReversed(isReversed);
                                ((ListSpan) parentSpan).setListType(listType);
                            }

                            ///更新ListSpan包含的儿子一级ListItemSpans（注意：只children！）
                            final int parentStart = editable.getSpanStart(parentSpan);
                            final int parentEnd = editable.getSpanEnd(parentSpan);
                            updateChildrenListItemSpans(editable, (ListSpan) parentSpan, parentStart, parentEnd);
                        }

                    } else {
                        ///更新区间内所有NestSpan的nesting level，偏移量为
                        final int parentStart = editable.getSpanStart(parentSpan);
                        final int parentEnd = editable.getSpanEnd(parentSpan);
                        updateDescendantsNestingLevel(clazz, editable, parentStart, parentEnd, -1);

                        ///段落span（带初始化参数）：List
                        if (clazz == ListSpan.class) {
                            removeChildrenListItemSpans(editable, (ListSpan) parentSpan, parentStart, parentEnd);
                        }

                        editable.removeSpan(parentSpan);
                    }
                }
            } else {
                final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, firstParagraphStart, lastParagraphEnd, true);
                if (view != null && view.isSelected()) {
                    int newSpanStart = firstParagraphStart;
                    int newSpanEnd = lastParagraphEnd;
                    T parentSpan = null;

                    ///山顶开始位置
                    int topSpanStart = -1;

                    for (T span : spans) {
                        int spanStart = editable.getSpanStart(span);
                        int spanEnd = editable.getSpanEnd(span);

                        if (firstParagraphStart < spanStart && spanEnd < lastParagraphEnd) {    ///选中区间包含span
                            if (topSpanStart < spanStart) {
                                topSpanStart = spanStart;
                            }

                            ///更新span的NestingLevel（增加一级）
                            span.setNestingLevel(span.getNestingLevel() + 1);
                        } else if (firstParagraphStart < spanStart || spanEnd < lastParagraphEnd) { ///交叉
                            if (spanStart > topSpanStart) {
                                if (firstParagraphStart < spanStart) {    ///交叉上山
                                    newSpanEnd = spanStart;
                                } else {    ///交叉下山
                                    newSpanStart = spanEnd;
                                }

                                ///段落span（带初始化参数）：List
                                if (clazz == ListSpan.class) {
                                    if (view.getTag(R.id.list_start) != null
                                            && view.getTag(R.id.list_is_reversed) != null
                                            && view.getTag(R.id.list_list_type) != null) {
                                        final int listStart = (int) view.getTag(R.id.list_start);
                                        final boolean isReversed = (boolean) view.getTag(R.id.list_is_reversed);
                                        final int listType = (int) view.getTag(R.id.list_list_type);
                                        ((ListSpan) span).setStart(listStart);
                                        ((ListSpan) span).isReversed(isReversed);
                                        ((ListSpan) span).setListType(listType);
                                    }

                                    ///更新ListSpan包含的儿子一级ListItemSpans（注意：只children！）
                                    updateChildrenListItemSpans(editable, (ListSpan) span, spanStart, spanEnd);
                                }

                            } else {
                                if (firstParagraphStart < spanStart) {    ///交叉上山
                                    newSpanEnd = spanEnd;
                                } else {    ///交叉下山
                                    newSpanStart = spanStart;
                                }

                                ///更新span的NestingLevel（增加一级）
                                span.setNestingLevel(span.getNestingLevel() + 1);
                            }
                        } else {    ///span包含选中区间 spanStart <= firstParagraphStart && end <= lastParagraphEnd
                            ///注意：这里使用otherSpan传递NestingLevel！
                            parentSpan = span;

                            break;
                        }
                    }

                    if (newSpanStart < newSpanEnd) {
                        final Object newSpan = createNewSpan(view, clazz, editable, newSpanStart, newSpanEnd, null, parentSpan);

                        ///段落span（带初始化参数）：List
                        if (clazz == ListSpan.class) {
                            createChildrenListItemSpans(editable, (ListSpan) newSpan, newSpanStart, newSpanEnd,
                                    mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
                        }

                    }
                } else {
                    for (T span : spans) {
                        int spanStart = editable.getSpanStart(span);
                        int spanEnd = editable.getSpanEnd(span);

                        //////??????目前只处理包含关系，将来增加处理交叉关系（需要遍历更新上面所有span的nestLevel！）
                        if (firstParagraphStart <= spanStart && spanEnd <= lastParagraphEnd) {

                            ///段落span（带初始化参数）：List
                            if (clazz == ListSpan.class) {
                                removeChildrenListItemSpans(editable, (ListSpan) span, spanStart, spanEnd);
                            }

                            editable.removeSpan(span);
                        }
                    }
                }
            }
        } else {
            final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, true);
            if (spans.size() == 0) {
                if (view != null && view.isSelected()) {
                    final Object newSpan = createNewSpan(view, clazz, editable, firstParagraphStart, lastParagraphEnd, null, null);

                    ///段落span（带初始化参数）：List
                    if (clazz == ListSpan.class) {
                        createChildrenListItemSpans(editable, (ListSpan) newSpan, firstParagraphStart, lastParagraphEnd,
                                mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
                    }

                }

                return;
            }

            final T parentSpan = getParentSpan(view, clazz, editable, start, end, null, false);
            for (T span : spans) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);

                if (start <= spanStart && spanEnd <= end) {
                    ///如果parentSpan不为null，则更新span把nesting level增加parentSpan.getNestingLevel()
                    if (parentSpan != null) {
                        final int parentNestingLevel = parentSpan.getNestingLevel();
                        span.setNestingLevel(span.getNestingLevel() + parentNestingLevel);
                    }

                    ///调整span的起止位置
                    final int currentParagraphStart = SpanUtil.getParagraphStart(editable, spanStart);
                    final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, spanEnd);
                    if (spanStart != currentParagraphStart || spanEnd != currentParagraphEnd) {
                        spanStart = currentParagraphStart;
                        spanEnd = currentParagraphEnd;
                        editable.setSpan(span, spanStart, spanEnd, getSpanFlag(clazz));
                    }
                }
            }
        }
    }

    private <T> void adjustParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply) {
        ///注意：必须多循环一次！比如选中区间尾部在下一行的开始位置，也要包括下一行到选择区间来
        if (end < editable.length()) {
            end++;
        }

        int next;
        for (int i = start; i <= end; i = next) { ///单选（start == end)时也能loop
            final int currentParagraphStart = SpanUtil.getParagraphStart(editable, i);
            final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, i);
            next = currentParagraphEnd;

            innerAdjustParagraphStyleSpans(view, clazz, editable, currentParagraphStart, currentParagraphEnd, isApply);

            if (next >= end) {
                break;
            }
        }
    }

    private <T> void innerAdjustParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, true);
        if (spans.size() == 0) {
            if (view != null && view.isSelected()) {
                createNewSpan(view, clazz, editable, start, end, null, null);
            }

            return;
        }

        boolean hasSpan = false;    ///为true时，后续的span将被删除
        for (T span : spans) {
            ///isSelected为true时（外延），当区间[start, end]中有多个span，首次处理span后，其余的span都应删除
            if (hasSpan) {
                editable.removeSpan(span);

                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///先调整span的起止位置
            if (spanStart != start || spanEnd != end) {
                editable.setSpan(span, start, end, getSpanFlag(clazz));
            }

            if (view != null && view.isSelected()) {
                if (!isSameWithViewParameter(view, clazz, span)) {
                    editable.removeSpan(span);
                    createNewSpan(view, clazz, editable, start, end, null, null);
                }
            } else {
                if (isApply) {
                    editable.removeSpan(span);
                }
            }

            hasSpan = true;
        }
    }

    private <T> void adjustBlockCharacterStyleSpans(View view, Class<T> clazz, final Editable editable, final int start, final int end, boolean isApply) {
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

        boolean isNewSpanNeeded = true;

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, true);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///如果单光标、且位于span的首尾，则忽略
            if (start == end && (spanStart == start || end == spanEnd)) {
                continue;
            }

            if (view.isSelected()) {

                ///字符span（带参数）：URL
                if (view == mImageViewURL) {
                    final String viewTagText = (String) view.getTag(R.id.url_text);
                    final String viewTagUrl = (String) view.getTag(R.id.url_url);
                    final String compareText = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanUrl = ((CustomURLSpan) span).getURL();
                    if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        ///忽略TextWatcher中的UndoRedo
                        isSkipUndoRedo = true;
                        editable.replace(spanStart, spanEnd, viewTagText);
                        Selection.setSelection(editable, start, start + viewTagText.length());
                        isSkipUndoRedo = false;

                        ///[isUpdateNeeded]
                        view.setSelected(view.isSelected());
                        view.setTag(R.id.url_text, viewTagText);
                        view.setTag(R.id.url_url, viewTagUrl);
                        isUpdateNeeded = true;
                    } else {
                        if (!TextUtils.isEmpty(viewTagUrl) && !viewTagUrl.equals(spanUrl)) {
                            editable.removeSpan(span);
                            span = (T) new CustomURLSpan(viewTagUrl);
                            editable.setSpan(span, spanStart, spanEnd, getSpanFlag(clazz));
                        }
                    }
                }

                ///字符span（带参数）：Image
                else if (view == mImageViewVideo || view == mImageViewAudio || view == mImageViewImage) {
                    final String viewTagText = (String) view.getTag(R.id.image_text);
                    final String viewTagUri = (String) view.getTag(R.id.image_uri);
                    final String viewTagSrc = (String) view.getTag(R.id.image_src);
                    final int viewTagWidth = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                    final int viewTagHeight = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                    final int viewTagAlign = view.getTag(R.id.image_align) == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                    final String compareText = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanSrc = ((CustomImageSpan) span).getSource();
                    if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        ///忽略TextWatcher中的UndoRedo
                        isSkipUndoRedo = true;
                        editable.replace(spanStart, spanEnd, viewTagText);
                        Selection.setSelection(editable, start, start + viewTagText.length());
                        isSkipUndoRedo = false;

                        ///[isUpdateNeeded]
                        view.setTag(R.id.image_text, viewTagText);
                        view.setTag(R.id.image_uri, viewTagUri);
                        view.setTag(R.id.image_src, viewTagSrc);
                        view.setTag(R.id.image_width, viewTagWidth);
                        view.setTag(R.id.image_height, viewTagHeight);
                        view.setTag(R.id.image_align, viewTagAlign);
                        isUpdateNeeded = true;
                    } else {
                        if (!TextUtils.isEmpty(viewTagSrc) && !viewTagSrc.equals(spanSrc)) {
                            editable.removeSpan(span);

                            ///[ImageSpan#Glide#GifDrawable]
                            loadImage(clazz, viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight, null, -1, start, end);
                        }
                    }
                }
            } else if (isApply) {
                editable.removeSpan(span);
            }

            ///[isRemoveNeeded]
            if (!isApply) {
                ///如果span包含了区间[start, end]则说明要删除所有在区间[start, end]的span
                if (spanStart < start && end < spanEnd) {
                    isRemoveNeeded = true;
                } else if (start <= spanStart && spanEnd <= end) {
                    removedSpans.add(span);
                }
            }

            isNewSpanNeeded = false;
        }

        if (view.isSelected() && isNewSpanNeeded) {

            ///字符span（带参数）：URL
            if (view == mImageViewURL) {
                final String viewTagText = (String) view.getTag(R.id.url_text);
                final String viewTagUrl = (String) view.getTag(R.id.url_url);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    ///忽略TextWatcher
                    isSkipUndoRedo = true;
                    editable.replace(start, end, viewTagText);
                    Selection.setSelection(editable, start, start + viewTagText.length());
                    isSkipUndoRedo = false;
                } else {
                    if (!TextUtils.isEmpty(viewTagUrl)) {
                        editable.setSpan(new CustomURLSpan(viewTagUrl), start, end, getSpanFlag(clazz));
                    }
                }
            }

            ///字符span（带参数）：Image
            else if (view == mImageViewVideo || view == mImageViewAudio || view == mImageViewImage) {
                final String viewTagText = (String) view.getTag(R.id.image_text);
                final String viewTagUri = (String) view.getTag(R.id.image_uri);
                final String viewTagSrc = (String) view.getTag(R.id.image_src);
                final int viewTagWidth = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                final int viewTagHeight = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                final int viewTagAlign = view.getTag(R.id.image_align) == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    ///忽略TextWatcher
                    isSkipUndoRedo = true;
                    editable.replace(start, end, viewTagText);
                    Selection.setSelection(editable, start, start + viewTagText.length());
                    isSkipUndoRedo = false;
                } else {
                    if (!TextUtils.isEmpty(viewTagSrc)) {
                        ///[ImageSpan#Glide#GifDrawable]
                        loadImage(clazz, viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight, null, -1, start, end);
                    }
                }
            }
        }

        ///[isUpdateNeeded]
        if (isUpdateNeeded) {
            final int selectionStart = Selection.getSelectionStart(editable);
            final int selectionEnd = Selection.getSelectionEnd(editable);
            if (selectionStart != -1 && selectionEnd != -1) {
                updateCharacterStyleView(mContext, mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd);
            }
        }

        ///[isRemoveNeeded]
        if (isRemoveNeeded) {
            for (T span : removedSpans) {
                editable.removeSpan(span);
            }
        }
    }

    private <T> void adjustCharacterStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply) {
        ///[FIX#选中行尾字符时应该把随后的'\n'也选中进来！]
        if (start < end && end < editable.length() && editable.charAt(end) == '\n') {
            end++;
        }

        boolean hasSpan = false;    ///为true时，后续的span将被删除
        boolean isSameWithViewParameter = true;

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, true);
        for (T span : spans) {
            ///isSelected为true时（外延），当区间[start, end]中有多个span，首次处理span后，其余的span都应删除
            ///注意：区间[start, end]结尾处可能会有部分在区间[start, end]中的span，因为首次处理中包含了join，所以已经被删除了
            if (hasSpan && view.isSelected()) {
                editable.removeSpan(span);

                continue;
            }

            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);

            isSameWithViewParameter = isSameWithViewParameter(view, clazz, span);

            ///删除文本或单选点击时start == end
            if (start == end) {
                if (isApply) { ///单选点击
                    if (start != spanStart && end != spanEnd) { ///不在span首尾处
                        if (view.isSelected()) {
                            if (!isSameWithViewParameter) {
                                editable.removeSpan(span);

                                ///重新设置[start, end]，以便后面的createNewSpan()
                                start = spanStart;
                                end = spanEnd;
                            }
                        } else {
                            editable.removeSpan(span);
                        }
                    }
                } else {    ///删除文本
                    if (editable.length() > 0) {//////?????[BUG]模拟器中：当文本长度为0，调用joinSpanByPosition()死机！要保证editable.length() > 0
                        joinSpanByPosition(mClassMap.get(clazz), clazz, editable, start);
                    }
                }
            } else {
                if (view.isSelected() && isSameWithViewParameter) {
                    final int st = Math.min(start, spanStart);
                    final int en = Math.max(spanEnd, end);
                    if (st != spanStart || en != spanEnd) {
                        editable.setSpan(span, st, en, getSpanFlag(clazz));
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
                        editable.setSpan(span, st, en, getSpanFlag(clazz));
                        if (spanStart < start && end < spanEnd) { ///右缩+new
                            ///new(end, spanEnd)
                            createNewSpan(view, clazz, editable, end, spanEnd, span, null);
                        }
                    } else if (isApply) {
                        editable.removeSpan(span);
                    } else {
                        if (start == spanStart) {  ///左join
                            findAndJoinLeftSpan(view, clazz, editable, span);
                        }
                        if (end == spanEnd) {  ///右join
                            findAndJoinRightSpan(view, clazz, editable, span);
                        }
                    }
                }

                hasSpan = true;
            }
        }

        ///extendOrNew+join
        if (view.isSelected() && (!hasSpan || !isSameWithViewParameter)) {
            ///如果左右有span且参数相等，则延长，否则创建新Span
            final T leftSpan = getLeftSpan(view, clazz, editable, start, end, null);
            if (leftSpan != null) {
                final int leftSpanStart = editable.getSpanStart(leftSpan);
                editable.setSpan(leftSpan, leftSpanStart, end, getSpanFlag(clazz));
                findAndJoinRightSpan(view, clazz, editable, leftSpan);
                return;
            }
            final T rightSpan = getRightSpan(view, clazz, editable, start, end, null);
            if (rightSpan != null) {
                final int rightSpanEnd = editable.getSpanEnd(rightSpan);
                editable.setSpan(rightSpan, start, rightSpanEnd, getSpanFlag(clazz));
//            findAndJoinLeftSpan(view, clazz, editable, start, rightSpanEnd, rightSpan);   ///无用！
                return;
            }

            ///创建新Span
            createNewSpan(view, clazz, editable, start, end, null, null);
        }
    }

    ///注意：当右缩+new时需要compareSpan
    private <T> Object createNewSpan(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan, T parentSpan) {
        ///添加新span
        Object newSpan = null;

        ///段落span（带初始化参数）：Quote
        if (clazz == CustomQuoteSpan.class) {
            int nestingLevel = 1;
            if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                nestingLevel = ((NestSpan) parentSpan).getNestingLevel() + 1;
            }
//            newSpan = new QuoteSpan(Color.GREEN);
//            newSpan = new QuoteSpan(Color.GREEN, 20, 40); ///Call requires API level 28 (current min is 15)
            newSpan = new CustomQuoteSpan(nestingLevel, mQuoteSpanColor, mQuoteSpanStripWidth, mQuoteSpanGapWidth);
        }

        ///段落span（带初始化参数）：List
        else if (clazz == ListSpan.class) {
            if (compareSpan != null) {
                final int listStart = ((ListSpan) compareSpan).getStart();
                final boolean isReversed = ((ListSpan) compareSpan).isReversed();
                final int nestingLevel = ((ListSpan) compareSpan).getNestingLevel();
                final int listType = ((ListSpan) compareSpan).getListType();
                newSpan = new ListSpan(nestingLevel, listStart, isReversed, listType, mIndicatorMargin);
            } else if (view != null && view.getTag(R.id.list_start) != null
                    && view.getTag(R.id.list_is_reversed) != null
                    && view.getTag(R.id.list_list_type) != null) {
                final int listStart = (int) view.getTag(R.id.list_start);
                final boolean isReversed = (boolean) view.getTag(R.id.list_is_reversed);
                int nestingLevel = 1;
                if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                    nestingLevel = ((NestSpan) parentSpan).getNestingLevel() + 1;
                }
                final int listType = (int) view.getTag(R.id.list_list_type);
                newSpan = new ListSpan(nestingLevel, listStart, isReversed, listType, mIndicatorMargin);
            }
        }

        ///段落span（带参数）：Head
        else if (clazz == HeadSpan.class) {
            if (compareSpan != null) {
                final int level = ((HeadSpan) compareSpan).getLevel();
                newSpan = new HeadSpan(level);
            } else if (view != null && view.getTag() != null) {
                final int level = (int) view.getTag();
                newSpan = new HeadSpan(level);
            }
        }

        ///段落span（带初始化参数）：LeadingMargin
        else if (clazz == CustomLeadingMarginSpan.class) {
            newSpan = new CustomLeadingMarginSpan(mLeadingMarginSpanIndent);
//            newSpan = new LeadingMarginSpan.LeadingMarginSpan2.Standard(first, rest); //////??????
        }

        ///段落span（带初始化参数）：Bullet
        else if (clazz == CustomBulletSpan.class) {
//            newSpan = new BulletSpan(Color.GREEN);
//            newSpan = new BulletSpan(40, Color.GREEN, 20); ///Call requires API level 28 (current min is 15)
            newSpan = new CustomBulletSpan(mBulletSpanGapWidth, mBulletColor, mBulletSpanRadius);
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

        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == CustomForegroundColorSpan.class) {
            if (compareSpan != null) {
                @ColorInt final int foregroundColor = ((CustomForegroundColorSpan) compareSpan).getForegroundColor();
                newSpan = new CustomForegroundColorSpan(foregroundColor);
            } else if (view != null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                @ColorInt final int foregroundColor = colorDrawable.getColor();
                newSpan = new CustomForegroundColorSpan(foregroundColor);
            }
        } else if (clazz == CustomBackgroundColorSpan.class) {
            if (compareSpan != null) {
                @ColorInt final int backgroundColor = ((CustomBackgroundColorSpan) compareSpan).getBackgroundColor();
                newSpan = new CustomBackgroundColorSpan(backgroundColor);
            } else if (view != null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                @ColorInt final int backgroundColor = colorDrawable.getColor();
                newSpan = new CustomBackgroundColorSpan(backgroundColor);
            }
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            if (compareSpan != null) {
                final String family = ((CustomFontFamilySpan) compareSpan).getFamily();
                newSpan = new CustomFontFamilySpan(family);
            } else if (view != null && view.getTag() != null) {
                final String family = (String) view.getTag();
                newSpan = new CustomFontFamilySpan(family);
            }
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            if (compareSpan != null) {
                final int size = ((CustomAbsoluteSizeSpan) compareSpan).getSize();
                newSpan = new CustomAbsoluteSizeSpan(size);
            } else if (view != null && view.getTag() != null) {
                final int size = (int) view.getTag();
                newSpan = new CustomAbsoluteSizeSpan(size);
            }
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            if (compareSpan != null) {
                final float sizeChange = ((CustomRelativeSizeSpan) compareSpan).getSizeChange();
                newSpan = new CustomRelativeSizeSpan(sizeChange);
            } else if (view != null && view.getTag() != null) {
                final float sizeChange = (float) view.getTag();
                newSpan = new CustomRelativeSizeSpan(sizeChange);
            }
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            if (compareSpan != null) {
                final float scaleX = ((CustomScaleXSpan) compareSpan).getScaleX();
                newSpan = new CustomScaleXSpan(scaleX);
            } else if (view != null && view.getTag() != null) {
                final float scaleX = (float) view.getTag();
                newSpan = new CustomScaleXSpan(scaleX);
            }

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
            editable.setSpan(newSpan, start, end, getSpanFlag(clazz));
        }

        return newSpan;
    }

}
