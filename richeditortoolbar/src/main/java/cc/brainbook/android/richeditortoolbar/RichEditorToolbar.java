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
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import cc.brainbook.android.richeditortoolbar.span.ListSpan;
import cc.brainbook.android.richeditortoolbar.span.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.ArrayUtil;
import cc.brainbook.android.richeditortoolbar.util.FileUtil;
import cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper;
import cc.brainbook.android.richeditortoolbar.util.ParcelUtil;
import cc.brainbook.android.richeditortoolbar.util.PrefsUtil;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;
import cc.brainbook.android.richeditortoolbar.util.StringUtil;
import cc.brainbook.android.richeditortoolbar.util.Util;

import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;

public class RichEditorToolbar extends FlexboxLayout implements
        LineDividerSpan.DrawBackgroundCallback,
        Drawable.Callback, View.OnClickListener, View.OnLongClickListener,
        RichEditText.OnSelectionChanged,
        RichEditText.SaveSpansCallback, RichEditText.LoadSpansCallback,
        UndoRedoHelper.OnPositionChangedListener {
    public static final String SHARED_PREFERENCES_NAME = "draft_preferences";
    public static final String SHARED_PREFERENCES_KEY_DRAFT_TEXT = "draft_text";
    public static final String CLIPBOARD_FILE_NAME = "rich_editor_clipboard_file";

    private HashMap<View, Class> mClassMap = new HashMap<>();
    public HashMap<View, Class> getClassMap() {
        return mClassMap;
    }

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

    /* ---------------- ///段落span（带初始化参数）：LeadingMargin ---------------- */
    private ImageView mImageViewLeadingMargin;
    private int mLeadingMarginSpanIndent = 40;

    /* ---------------- ///段落span（带初始化参数）：Bullet ---------------- */
    private ImageView mImageViewBullet;
    private @ColorInt int mBulletColor = Color.parseColor("#DDDDDD");
    private int mBulletSpanRadius = 16;
    private int mBulletSpanGapWidth = 40;

    /* ---------------- ///段落span（带初始化参数）：List ---------------- */
    private ImageView mImageViewList;
    private int mIndentWidth = ListSpan.DEFAULT_INDENT_WIDTH;
    private int mIndicatorWidth = ListSpan.DEFAULT_INDICATOR_WIDTH;
    private int mIndicatorGapWidth = ListSpan.DEFAULT_INDICATOR_GAP_WIDTH;
    private @ColorInt int mIndicatorColor = Color.parseColor("#DDDDDD");

    private int[] getNestingLevelAndOrderIndex(int listType,  Editable editable, int start, int end) {
        int[] result = {0, 1};
        boolean isInit = true;
        int pointer = Integer.MAX_VALUE;

        int currentStart = start;
        int currentEnd = end;
        ListSpan leftSpan;

        while (true) {
            leftSpan = getLeftSpan(null, ListSpan.class, editable, currentStart, currentEnd, null);
            if (leftSpan == null) {
                break;
            }

            if (isInit) {
                result[0] = leftSpan.getNestingLevel() + 1;
                isInit = false;
            }

            if (leftSpan.getNestingLevel() < pointer) {
                pointer = leftSpan.getNestingLevel();
            }

            if (pointer == leftSpan.getNestingLevel()) {
                if (leftSpan.getListType() == listType) {
                    result[0] = pointer;
                    result[1] = leftSpan.getOrderIndex() + 1;
                    break;
                }
            }

            currentStart = editable.getSpanStart(leftSpan);
            currentEnd = editable.getSpanEnd(leftSpan);
        }

        return result;
    }
    private void updateAllRightListSpans(Editable editable, int start, int end) {
        int currentStart = start;
        int currentEnd = end;
        ListSpan rightSpan;

        while (true) {
            rightSpan = getRightSpan(null, ListSpan.class, editable, currentStart, currentEnd, null);
            if (rightSpan == null) {
                break;
            }

            final int listType = rightSpan.getListType();
            currentStart = editable.getSpanStart(rightSpan);
            currentEnd = editable.getSpanEnd(rightSpan);
            final int[] nestingLevelAndOrderIndex = getNestingLevelAndOrderIndex(listType,  editable, currentStart, currentEnd);
            if (nestingLevelAndOrderIndex[0] != rightSpan.getNestingLevel() || nestingLevelAndOrderIndex[1] != rightSpan.getOrderIndex()) {
                editable.removeSpan(rightSpan);

                final String indicatorText = ListSpanHelper.getIndicatorText(listType, nestingLevelAndOrderIndex[1]);
                final ListSpan listSpan = new ListSpan(listType, indicatorText, nestingLevelAndOrderIndex[0], nestingLevelAndOrderIndex[1],
                        mIndentWidth, mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
                editable.setSpan(listSpan, currentStart, currentEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
    }

    /* ---------------- ///段落span：LineDivider ---------------- */
    private ImageView mImageViewLineDivider;
    private int mLineDividerSpanMarginTop = 50;
    private int mLineDividerSpanMarginBottom = 50;
    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
        c.drawLine(left, (top + bottom) / 2, right, (top + bottom) / 2, p);    ///画直线
    }

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
                        mRichEditText.getText().setSpan(imagePlaceholderSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else {
                        pasteEditable.setSpan(imagePlaceholderSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                            pasteEditable == null ? start : start + pasteOffset, pasteEditable == null ? end : end + pasteOffset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    pasteEditable.removeSpan(imagePlaceholderSpan);
                    pasteEditable.setSpan(
                            clazz == VideoSpan.class ? new VideoSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : clazz == AudioSpan.class ? new AudioSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign)
                                    : new CustomImageSpan(drawable, viewTagUri, viewTagSrc, viewTagAlign),
                                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

        /* -------------- ///段落span（带初始化参数）：LeadingMargin --------------- */
        mImageViewLeadingMargin = (ImageView) findViewById(R.id.iv_leading_margin);
        mImageViewLeadingMargin.setOnClickListener(this);
        mImageViewLeadingMargin.setOnLongClickListener(this);
        mClassMap.put(mImageViewLeadingMargin, CustomLeadingMarginSpan.class);

        /* -------------- ///段落span（带初始化参数）：Bullet --------------- */
        mImageViewBullet = (ImageView) findViewById(R.id.iv_bullet);
        mImageViewBullet.setOnClickListener(this);
        mImageViewBullet.setOnLongClickListener(this);
        mClassMap.put(mImageViewBullet, CustomBulletSpan.class);

        /* -------------- ///段落span（带初始化参数）：List --------------- */
        mImageViewList = (ImageView) findViewById(R.id.iv_list);
        mImageViewList.setOnClickListener(this);
        mImageViewList.setOnLongClickListener(this);
        mClassMap.put(mImageViewList, ListSpan.class);

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
        mClassMap.put(mImageViewStrikeThrough, CustomStrikethroughSpan.class);

        mImageViewSuperscript = (ImageView) findViewById(R.id.iv_superscript);
        mImageViewSuperscript.setOnClickListener(this);
        mClassMap.put(mImageViewSuperscript, CustomSuperscriptSpan.class);

        mImageViewSubscript = (ImageView) findViewById(R.id.iv_subscript);
        mImageViewSubscript.setOnClickListener(this);
        mClassMap.put(mImageViewSubscript, CustomSubscriptSpan.class);

        /* -------------- ///字符span（带参数）：Code --------------- */
        mImageViewCode = (ImageView) findViewById(R.id.iv_code);
        mImageViewCode.setOnClickListener(this);
        mClassMap.put(mImageViewCode, CodeSpan.class);

        /* -------------- ///字符span（带参数）：ForegroundColor、BackgroundColor --------------- */
        mImageViewForegroundColor = (ImageView) findViewById(R.id.iv_foreground_color);
        mImageViewForegroundColor.setOnClickListener(this);
        mClassMap.put(mImageViewForegroundColor, CustomForegroundColorSpan.class);

        mImageViewBackgroundColor = (ImageView) findViewById(R.id.iv_background_color);
        mImageViewBackgroundColor.setOnClickListener(this);
        mClassMap.put(mImageViewBackgroundColor, CustomBackgroundColorSpan.class);

        /* -------------- ///字符span（带参数）：FontFamily --------------- */
        mTextViewFontFamily = (TextView) findViewById(R.id.tv_font_family);
        mTextViewFontFamily.setOnClickListener(this);
        mClassMap.put(mTextViewFontFamily, CustomFontFamilySpan.class);

        /* -------------- ///字符span（带参数）：AbsoluteSize --------------- */
        mTextViewAbsoluteSize = (TextView) findViewById(R.id.tv_absolute_size);
        mTextViewAbsoluteSize.setOnClickListener(this);
        mClassMap.put(mTextViewAbsoluteSize, CustomAbsoluteSizeSpan.class);

        /* -------------- ///字符span（带参数）：RelativeSize --------------- */
        mTextViewRelativeSize = (TextView) findViewById(R.id.tv_relative_size);
        mTextViewRelativeSize.setOnClickListener(this);
        mClassMap.put(mTextViewRelativeSize, CustomRelativeSizeSpan.class);

        /* -------------- ///字符span（带参数）：ScaleX --------------- */
        mTextViewScaleX = (TextView) findViewById(R.id.tv_scale_x);
        mTextViewScaleX.setOnClickListener(this);
        mClassMap.put(mTextViewScaleX, CustomScaleXSpan.class);

        /* -------------- ///字符span（带参数）：URL --------------- */
        mImageViewURL = (ImageView) findViewById(R.id.iv_url);
        mImageViewURL.setOnClickListener(this);
        mClassMap.put(mImageViewURL, CustomURLSpan.class);

        /* -------------- ///字符span（带参数）：Image --------------- */
        mImageViewVideo = (ImageView) findViewById(R.id.iv_video);
        mImageViewAudio = (ImageView) findViewById(R.id.iv_audio);
        mImageViewImage = (ImageView) findViewById(R.id.iv_image);
        mImageViewVideo.setOnClickListener(this);
        mImageViewAudio.setOnClickListener(this);
        mImageViewImage.setOnClickListener(this);
        mClassMap.put(mImageViewVideo, VideoSpan.class);
        mClassMap.put(mImageViewAudio, AudioSpan.class);
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

                final int selectionStart = Selection.getSelectionStart(editable);
                final int selectionEnd = Selection.getSelectionEnd(editable);
                if (selectionStart == -1 || selectionEnd == -1) {
                    return;
                }

                clearParagraphSpans(selectionStart, selectionEnd);
                clearCharacterSpans(selectionStart, selectionEnd);

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

    private boolean isParagraphStyle(View view) {
        return view == mTextViewHead
                || view == mImageViewQuote
                || view == mImageViewAlignNormal
                || view == mImageViewAlignCenter
                || view == mImageViewAlignOpposite
                || view == mImageViewLeadingMargin
                || view == mImageViewBullet
                || view == mImageViewList
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
                || view == mImageViewVideo
                || view == mImageViewAudio
                || view == mImageViewImage
                || view == mImageViewCode
                || view == mImageViewForegroundColor
                || view == mImageViewBackgroundColor
                || view == mTextViewFontFamily
                || view == mTextViewAbsoluteSize
                || view == mTextViewRelativeSize
                || view == mTextViewScaleX;
    }
    private boolean isBlockCharacterStyle(View view) {
        return view == mImageViewURL
                || view == mImageViewVideo
                || view == mImageViewAudio
                || view == mImageViewImage;
    }
    private int getActionId(View view) {
        if (view == mTextViewHead) {
            return UndoRedoHelper.CHANGE_HEAD_SPAN_ACTION;
        } else if (view == mImageViewQuote) {
            return UndoRedoHelper.CHANGE_QUOTE_SPAN_ACTION;
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
        } else if (view == mImageViewList) {
            return UndoRedoHelper.CHANGE_LIST_SPAN_ACTION;
        } else if (view == mImageViewLineDivider) {
            return UndoRedoHelper.CHANGE_LINE_DIVIDER_SPAN_ACTION;

        } else if (view == mImageViewBold) {
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
        } else if (view == mImageViewVideo) {
            return UndoRedoHelper.CHANGE_VIDEO_SPAN_ACTION;
        } else if (view == mImageViewAudio) {
            return UndoRedoHelper.CHANGE_AUDIO_SPAN_ACTION;
        } else if (view == mImageViewImage) {
            return UndoRedoHelper.CHANGE_IMAGE_SPAN_ACTION;
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

                ///段落span（带参数）：List
                else if (clazz == ListSpan.class) {
                    final int listType = ((ListSpan) span).getListType();
                    view.setTag(listType);
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
                if (clazz == CustomURLSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
                        final String text = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                        final String url = ((CustomURLSpan) span).getURL();
                        view.setTag(R.id.url_text, text);
                        view.setTag(R.id.url_url, url);
                    } else {
                        view.setTag(R.id.url_text, null);
                        view.setTag(R.id.url_url, null);
                    }
                }

                ///字符span（带参数）：Image
                else if (clazz == VideoSpan.class || clazz == AudioSpan.class || clazz == CustomImageSpan.class) {
                    if (start == end || spans.size() == 1) {    ///注意：不是filter之前的spans的length为1！要考虑忽略getSpans()获取的子类（不是clazz本身）
                        final String text = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);

                        final String uri = clazz == CustomImageSpan.class ? null :
                                clazz == VideoSpan.class ? ((VideoSpan) span).getUri() : ((AudioSpan) span).getUri();
                        final String src = ((CustomImageSpan) span).getSource();

                        ///从text中解析出width\height\align
                        final String strWidth = StringUtil.getParameter(text, "width=", " ");
                        final String strHeight = StringUtil.getParameter(text, "height=", " ");
                        final String strAlign = StringUtil.getParameter(text, "align=", "]");
                        final int width = strWidth == null ? 0 : Integer.parseInt(strWidth);
                        final int height = strHeight == null ? 0 : Integer.parseInt(strHeight);
                        final int align = strAlign == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : Integer.parseInt(strAlign);

                        view.setTag(R.id.image_text, text);
                        view.setTag(R.id.image_src, uri);
                        view.setTag(R.id.image_src, src);
                        view.setTag(R.id.image_width, width);
                        view.setTag(R.id.image_height, height);
                        view.setTag(R.id.image_align, align);
                    } else {
                        view.setTag(R.id.image_text, null);
                        view.setTag(R.id.image_uri, null);
                        view.setTag(R.id.image_src, null);
                        view.setTag(R.id.image_width, null);
                        view.setTag(R.id.image_height, null);
                        view.setTag(R.id.image_align, null);
                    }
                }

                ///字符span（带参数）：ForegroundColor、BackgroundColor
                else if (clazz == CustomForegroundColorSpan.class) {
                    @ColorInt final int foregroundColor = ((CustomForegroundColorSpan) span).getForegroundColor();
                    view.setBackgroundColor(foregroundColor);
                } else if (clazz == CustomBackgroundColorSpan.class) {
                    @ColorInt final int backgroundColor = ((CustomBackgroundColorSpan) span).getBackgroundColor();
                    view.setBackgroundColor(backgroundColor);
                }

                ///字符span（带参数）：FontFamily
                else if (clazz == CustomFontFamilySpan.class) {
                    final String family = ((CustomFontFamilySpan) span).getFamily();
                    view.setTag(family);
                    ((TextView) view).setText(family);
                }

                ///字符span（带参数）：AbsoluteSize
                else if (clazz == CustomAbsoluteSizeSpan.class) {
                    final int size = ((CustomAbsoluteSizeSpan) span).getSize();
                    view.setTag(size);
                    ((TextView) view).setText(String.valueOf(size));
                }

                ///字符span（带参数）：RelativeSize
                else if (clazz == CustomRelativeSizeSpan.class) {
                    final float sizeChange = ((CustomRelativeSizeSpan) span).getSizeChange();
                    view.setTag(sizeChange);
                    ((TextView) view).setText(String.valueOf(sizeChange));
                }

                ///字符span（带参数）：ScaleX
                else if (clazz == CustomScaleXSpan.class) {
                    final float scaleX = ((CustomScaleXSpan) span).getScaleX();
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
        if (clazz == CustomURLSpan.class) {
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
        else if (clazz == VideoSpan.class || clazz == AudioSpan.class || clazz == CustomImageSpan.class) {
            view.setTag(R.id.image_text, null);
            view.setTag(R.id.image_uri, null);
            view.setTag(R.id.image_src, null);
            view.setTag(R.id.image_width, null);
            view.setTag(R.id.image_height, null);
            view.setTag(R.id.image_align, null);
        }

        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == CustomForegroundColorSpan.class || clazz == CustomBackgroundColorSpan.class) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.font_family));
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.absolute_size));
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.relative_size));
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            ((TextView) view).setText(mContext.getString(R.string.scale_x));
        }

        else {
            view.setTag(null);
        }
    }

    private <T> void newParagraphStyleSpan(View view, Class<T> clazz, Editable editable, int start, int end) {
        ///添加新span
        Object newSpan = null;

        ///段落span（带参数）：Head
        if (clazz == HeadSpan.class) {
            if (view.getTag() != null) {
                final String viewTagHead = (String) view.getTag();
                newSpan = new HeadSpan(viewTagHead);
            }
        }

        ///段落span（带初始化参数）：Quote
        else if (clazz == CustomQuoteSpan.class) {
//            newSpan = new QuoteSpan(Color.GREEN);
//            newSpan = new QuoteSpan(Color.GREEN, 20, 40); ///Call requires API level 28 (current min is 15)
            newSpan = new CustomQuoteSpan(mQuoteSpanColor, mQuoteSpanStripWidth, mQuoteSpanGapWidth);
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

        ///段落span（带初始化参数）：List
        else if (clazz == ListSpan.class) {
            if (view.getTag() != null) {
                final int listType = (int) view.getTag();
                final int[] nestingLevelAndOrderIndex = getNestingLevelAndOrderIndex(listType, editable, start, end);
                final int nestingLevel = nestingLevelAndOrderIndex[0];
                final int orderIndex = nestingLevelAndOrderIndex[1];
                final String indicatorText = ListSpanHelper.getIndicatorText(listType, orderIndex);
                newSpan = new ListSpan(listType, indicatorText, nestingLevel, orderIndex,
                        mIndentWidth, mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
            }
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

            ///段落span（带初始化参数）：List
            ///新建ListSpan后，更新所有后续的ListSpan
            if (clazz == ListSpan.class) {
                updateAllRightListSpans(editable, start, end);
            }

        }
    }
    private <T> void newCharacterStyleSpanByCompareSpanOrViewParameter(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        ///添加新span
        Object newSpan = null;

        ///字符span（带参数）：ForegroundColor、BackgroundColor
        if (clazz == CustomForegroundColorSpan.class) {
            @ColorInt final int foregroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                foregroundColor = colorDrawable.getColor();
            } else {
                foregroundColor = ((CustomForegroundColorSpan) compareSpan).getForegroundColor();
            }
            newSpan = new CustomForegroundColorSpan(foregroundColor);
        } else if (clazz == CustomBackgroundColorSpan.class) {
            @ColorInt final int backgroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                backgroundColor = colorDrawable.getColor();
            } else {
                backgroundColor = ((CustomBackgroundColorSpan) compareSpan).getBackgroundColor();
            }
            newSpan = new CustomBackgroundColorSpan(backgroundColor);
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            String family;
            if (compareSpan == null) {
                family = (String) view.getTag();
            } else {
                family = ((CustomFontFamilySpan) compareSpan).getFamily();
            }
            newSpan = new CustomFontFamilySpan(family);
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            int size;
            if (compareSpan == null) {
                size = (int) view.getTag();
            } else {
                size = ((CustomAbsoluteSizeSpan) compareSpan).getSize();
            }
            newSpan = new CustomAbsoluteSizeSpan(size);
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            float sizeChange;
            if (compareSpan == null) {
                sizeChange = (float) view.getTag();
            } else {
                sizeChange = ((CustomRelativeSizeSpan) compareSpan).getSizeChange();
            }
            newSpan = new CustomRelativeSizeSpan(sizeChange);
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            float scaleX;
            if (compareSpan == null) {
                scaleX = (float) view.getTag();
            } else {
                scaleX = ((CustomScaleXSpan) compareSpan).getScaleX();
            }
            newSpan = new CustomScaleXSpan(scaleX);

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
        if (clazz == CustomForegroundColorSpan.class) {
            @ColorInt final int foregroundColor = ((CustomForegroundColorSpan) span).getForegroundColor();
            final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
            return foregroundColor == colorDrawable.getColor();
        } else if (clazz == CustomBackgroundColorSpan.class) {
            @ColorInt final int backgroundColor = ((CustomBackgroundColorSpan) span).getBackgroundColor();
            final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
            return backgroundColor == colorDrawable.getColor();
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final String spanFamily = ((CustomFontFamilySpan) span).getFamily();
            final String viewTagFontFamily = (String) view.getTag();
            return TextUtils.equals(spanFamily, viewTagFontFamily);
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final int spanSize = ((CustomAbsoluteSizeSpan) span).getSize();
            final int viewTagAbsoluteSize = (int) view.getTag();
            return spanSize == viewTagAbsoluteSize;
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanSizeChange = ((CustomRelativeSizeSpan) span).getSizeChange();
            final float viewTagRelativeSize = (float) view.getTag();
            return spanSizeChange == viewTagRelativeSize;
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            if (view.getTag() == null) {
                return false;
            }
            final float spanScaleX = ((CustomScaleXSpan) span).getScaleX();
            final float viewTagScaleX = (float) view.getTag();
            return spanScaleX == viewTagScaleX;
        }

        else {
            return true;
        }
    }

    ///注意：不包括block span（如URLSpan、ImageSpan）
    private <T> T filterSpanByCompareSpanOrViewParameter(View view, Class<T> clazz, T span, T compareSpan) {
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        if (clazz == CustomForegroundColorSpan.class) {
            @ColorInt final int foregroundColor = ((CustomForegroundColorSpan) span).getForegroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                return foregroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt final int compareSpanForegroundColor = ((CustomForegroundColorSpan) compareSpan).getForegroundColor();
                return foregroundColor == compareSpanForegroundColor ? span : null;
            }
        } else if (clazz == CustomBackgroundColorSpan.class) {
            @ColorInt final int backgroundColor = ((CustomBackgroundColorSpan) span).getBackgroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                return backgroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt final int compareSpanBackgroundColor = ((CustomBackgroundColorSpan) compareSpan).getBackgroundColor();
                return backgroundColor == compareSpanBackgroundColor ? span : null;
            }
        }

        ///字符span（带参数）：FontFamily
        else if (clazz == CustomFontFamilySpan.class) {
            final String spanFamily = ((CustomFontFamilySpan) span).getFamily();
            if (compareSpan == null) {
                final String viewTagFamily = (String) view.getTag();
                return TextUtils.equals(spanFamily, viewTagFamily) ? span : null;
            } else {
                final String compareSpanFamily = ((CustomFontFamilySpan) compareSpan).getFamily();
                return TextUtils.equals(spanFamily, compareSpanFamily) ? span : null;
            }
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            final int spanSize = ((CustomAbsoluteSizeSpan) span).getSize();
            if (compareSpan == null) {
                final int viewTagSize = (int) view.getTag();
                return spanSize == viewTagSize ? span : null;
            } else {
                final int compareSpanSize = ((CustomAbsoluteSizeSpan) compareSpan).getSize();
                return spanSize == compareSpanSize ? span : null;
            }
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            final float spanSizeChange = ((CustomRelativeSizeSpan) span).getSizeChange();
            if (compareSpan == null) {
                final float viewTagSizeChange = (float) view.getTag();
                return spanSizeChange == viewTagSizeChange ? span : null;
            } else {
                final float compareSpanSizeChange = ((CustomRelativeSizeSpan) compareSpan).getSizeChange();
                return spanSizeChange == compareSpanSizeChange ? span : null;
            }
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            final float spanScaleX = ((CustomScaleXSpan) span).getScaleX();
            if (compareSpan == null) {
                final float viewTagScaleX = (float) view.getTag();
                return spanScaleX == viewTagScaleX ? span : null;
            } else {
                final float compareSpanScaleX = ((CustomScaleXSpan) compareSpan).getScaleX();
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
            else if (view == mTextViewHead) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.head_items, (String) view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.head_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final String head = (String) ArrayUtil.getStringItem(mContext, R.array.head_items, which);

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!TextUtils.equals(head, (String) view.getTag())) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(head);

                                    ///改变selection的span
                                    applyParagraphStyleSpansSelection(view, editable);

                                    ///[Preview]
                                    updatePreview();

                                    ((TextView) view).setText(head);
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
                                applyParagraphStyleSpansSelection(view, editable);

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

            ///段落span（带参数）：List
            else if (view == mImageViewList) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = view.getTag() == null ? -1 :
                        ArrayUtil.getIntIndex(mContext, R.array.list_type_ids, (int) view.getTag());

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.list_type_labels, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final int listType = ArrayUtil.getIntItem(mContext, R.array.list_type_ids, which);

                                if (view.getTag() == null || listType != (int) view.getTag()) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(listType);

                                    ///改变selection的span
                                    applyParagraphStyleSpansSelection(view, editable);

                                    ///[Preview]
                                    updatePreview();
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
                                applyParagraphStyleSpansSelection(view, editable);

                                ///清空view tag
                                view.setTag(null);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

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
                                final ArrayList<CustomURLSpan> selectedSpans = SpanUtil.getSelectedSpans(mRichEditText, CustomURLSpan.class);
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
                        })
                        .setNegativeButton(android.R.string.cancel, null);

                final String text = (String) view.getTag(R.id.url_text);
                final String url = (String) view.getTag(R.id.url_url);
                clickUrlSpanDialogBuilder.initial(text, url);
                clickUrlSpanDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：Image
            else if (view == mImageViewVideo || view == mImageViewAudio || view == mImageViewImage) {
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
                                applyCharacterStyleSpansSelection(view, editable);

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
                                applyCharacterStyleSpansSelection(view, editable);

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

            ///字符span（带参数）：ForegroundColor、BackgroundColor
            else if (view == mImageViewForegroundColor || view == mImageViewBackgroundColor) {
                ///颜色选择器
                final ColorPickerDialogBuilder colorPickerDialogBuilder = ColorPickerDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                                if (colorDrawable.getColor() != selectedColor) {
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
                                applyCharacterStyleSpansSelection(view, editable);

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
                                    applyCharacterStyleSpansSelection(view, editable);

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
                                applyCharacterStyleSpansSelection(view, editable);

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
                                    applyCharacterStyleSpansSelection(view, editable);

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
                                applyCharacterStyleSpansSelection(view, editable);

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
                                    applyCharacterStyleSpansSelection(view, editable);

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
                                applyCharacterStyleSpansSelection(view, editable);

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
                                final CharSequence scaleX = ArrayUtil.getStringItem(mContext, R.array.scale_x_items, which);

                                if (!TextUtils.equals(scaleX, (CharSequence) view.getTag())) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(scaleX);

                                    ///改变selection的span
                                    applyCharacterStyleSpansSelection(view, editable);

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
                                applyCharacterStyleSpansSelection(view, editable);

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

        ///段落span（带初始化参数）：LeadingMargin
        else if (view == mImageViewLeadingMargin) {
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

        ///段落span（带初始化参数）：List
        else if (view == mImageViewList) {
            ((LongClickListSpanDialogBuilder) LongClickListSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LongClickListSpanDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indentWidth, int indicatorWidth, int indicatorGapWidth, int indicatorColor, Integer[] allColors) {
                            mIndentWidth = indentWidth;
                            mIndicatorWidth = indicatorWidth;
                            mIndicatorGapWidth = indicatorGapWidth;
                            mIndicatorColor = indicatorColor;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null))
                    .initial(mIndentWidth, mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor)
                    .build().show();

            return true;
        }

        return false;
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

        final int currentParagraphStart = SpanUtil.getParagraphStart(editable, selectionStart);
        final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, selectionStart);

        for (View view : mClassMap.keySet()) {
            if (isParagraphStyle(view)) {
                updateParagraphView(view, mClassMap.get(view), editable, currentParagraphStart, currentParagraphEnd);
            } else if (isCharacterStyle(view)) {
                updateCharacterStyleView(view, mClassMap.get(view), editable, selectionStart, selectionEnd);
            }

            ///test
            if (DEBUG) Util.testOutput(editable, mClassMap.get(view));
        }
    }

    private void applyParagraphStyleSpansSelection(View view, Editable editable) {
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

            ///调整其它段落span
            for (View v : mClassMap.keySet()) {
                if (isParagraphStyle(v) && v!= view) {
                    adjustParagraphStyleSpans(v, mClassMap.get(v), editable, selectionStart, selectionEnd, false);
                }
            }
        }

        adjustParagraphStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, true);

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            mUndoRedoHelper.addHistory(getActionId(view), selectionEnd, beforeChange, afterChange,
                    RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
        }
    }
    private <T> void adjustParagraphStyleSpansSelection(View view, Class<T> clazz, Editable editable, int start, int end, boolean isSelected, boolean isRemove) {
        boolean isNewSpanNeeded = true;  ///changed内容是否需要新添加span
        boolean isSet = false;  ///为true时，后续的span将被删除

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            if (isSet) {
                editable.removeSpan(span);
                continue;
            }

            ///如果span与当前行首尾相同
            if (spanStart == start && spanEnd == end) {
                isNewSpanNeeded = false;
                isSet = true;

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

                    ///段落span（带参数）：List
                    else if (clazz == ListSpan.class) {
                        final int spanListType = ((ListSpan) span).getListType();
                        final int viewTagListType = (int) view.getTag();

                        final int[] nestingLevelAndOrderIndex = getNestingLevelAndOrderIndex(spanListType,  editable, spanStart, spanEnd);
                        if (spanListType != viewTagListType || nestingLevelAndOrderIndex[0] != ((ListSpan) span).getNestingLevel()
                                || nestingLevelAndOrderIndex[1] != ((ListSpan) span).getOrderIndex()) {
                            isNewSpanNeeded = true;
                            editable.removeSpan(span);
                        }
                    }

                } else {
                    if (isRemove) {
                        editable.removeSpan(span);

                        ///段落span（带初始化参数）：List
                        ///清空ListSpan样式后，更新所有后续的ListSpan
                        if (clazz == ListSpan.class) {
                            updateAllRightListSpans(editable, start, end);
                        }
                    }
                }

            } else if (spanStart < start) {
                ///补插'\n'后造成span自动延展，所以需要调整右缩span，不覆盖[start, end]区间的span
                ///[单选、回车]
                ///[单选（前有selected空行）、回车]
                ///[单选（文本尾部、前有空行）、删除]
                ///[单选（前有selected空行）、粘贴（左缺右全span）]
                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else if (spanStart > start) { ///spanStart > start
                ///[单选（unselected非空行）、粘贴（全span）]
                ///[多选（unselected非空行）、粘贴（左缺全span左缺）]
                editable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                isNewSpanNeeded = false;
                isSet = true;

                if (end < spanEnd) {   ///spanStart == start && end < spanEnd
                    ///[单选、回车]
                    ///[多选（view未selected、包含文本尾部空行）、点击]
                    editable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                } else {   ///spanStart == start && end > spanEnd
                    if (isSelected) {
                        ///[单选、回车]
                        ///[单选（空行）、删除]
                        ///[多选（含'\n'的内容到selected段落中间内）、粘贴]
                        editable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    } else {
                        ///[单选（非文尾unselected空行）、粘贴（全span）]
                        editable.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
            }
        }

        ///如果changed内容需要新加span，且工具条选中，则添加新span
        if (isNewSpanNeeded && isSelected) {
            newParagraphStyleSpan(view, clazz, editable, start, end);
        }
    }
    private void applyCharacterStyleSpansSelection(View view, Editable editable) {
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);
        ///当selectionStart != selectionEnd时改变selection的span
        if (selectionStart == -1 || selectionEnd == -1 || selectionStart == selectionEnd && !isBlockCharacterStyle(view)) {
            return;
        }

        if (isBlockCharacterStyle(view)) {
            adjustBlockCharacterStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, view.isSelected(), true);
        } else {
            adjustCharacterStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, view.isSelected(), true);
        }

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            if (isBlockCharacterStyle(view)) {
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
                for (View view : mClassMap.keySet()) {
                    if (isCharacterStyle(view)) {
                        ///清除掉已经被删除的span，否则将会产生多余的无效span！
                        SpanUtil.removeSpans(mClassMap.get(view), mRichEditText.getText(), start, start + count);
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
            final int selectionEnd = start + count;

            for (View view : mClassMap.keySet()) {
                if (isParagraphStyle(view)) {
                    ///[FIX#当光标选择区间的尾部位于文本尾部空行时，段落view被select时，出现首尾相同的新span！且上一行显示view被selected]
                    ///解决：此时补插入一个'\n'
                    if (count > 0 && view.isSelected() && selectionEnd == editable.length()
                            && (editable.length() == 0 || editable.charAt(editable.length() - 1) == '\n')) {
                        ///注意：不能用isSkipUndoRedo！会造成死循环
                        isSkipTextWatcher = true;
                        editable.append('\n');

                        ///调整光标位置到append之前的位置
                        Selection.setSelection(editable, selectionEnd);
                        isSkipTextWatcher = false;

                        ///调整将要保存的历史记录的beforeChange/afterChange
                        mAfterChange = editable.subSequence(start, selectionEnd + 1).toString();

                        ///补插后文本长度不再等于selectionEnd，所以跳出循环结束
                        break;
                    }
                }
            }
            for (View view : mClassMap.keySet()) {
                if (isParagraphStyle(view)) {
                    adjustParagraphStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, false);
                } else if (isCharacterStyle(view)) {
                    if (count > 0) {
                        if (isBlockCharacterStyle(view)) {
                            adjustBlockCharacterStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, view.isSelected(), false);
                        } else {
                            adjustCharacterStyleSpans(view, mClassMap.get(view), editable, selectionStart, selectionEnd, view.isSelected(), false);
                        }
                    } else if (before > 0) {
                        if (!isBlockCharacterStyle(view) && editable.length() > 0) {///[BUG]模拟器中：当文本长度为0，调用joinSpanByPosition()死机！要保证editable.length() > 0
                            joinSpanByPosition(view, mClassMap.get(view), editable, selectionStart);
                        }
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

    private <T> void adjustParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isRemove) {
        ///段落span（带初始化参数）：List
        ///粘贴含有ListSpan的文本需要先获得选择区间前面ListSpan的位置，以便以后更新ListSpan
        int previousWhere = 0;
        if (clazz == ListSpan.class) {
            final int currentParagraphStart = SpanUtil.getParagraphStart(editable, start);
//            final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, start);
            previousWhere = currentParagraphStart -1 > 0
                    ? SpanUtil.getParagraphStart(editable, currentParagraphStart -1) : currentParagraphStart;
        }

        ///注意：必须多循环一次！比如单光标选中或区间选择的尾部含有一个空行，也要包括到选择区间来
        if (end < editable.length()) {
            end++;
        }

        int next;
        for (int i = start; i <= end; i = next) { ///单选（start == end)时也能loop
            final int currentParagraphStart = SpanUtil.getParagraphStart(editable, i);
            final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, i);
            next = currentParagraphEnd;

            adjustParagraphStyleSpansSelection(view, clazz, editable, currentParagraphStart, currentParagraphEnd, view.isSelected(), isRemove);

            if (next >= end) {
                ///段落span（带初始化参数）：List
                ///粘贴含有ListSpan的文本后，更新选择区间内和后续的ListSpan
                if (clazz == ListSpan.class) {
                    final int previousParagraphStart = SpanUtil.getParagraphStart(editable, previousWhere);
                    final int previousParagraphEnd = SpanUtil.getParagraphEnd(editable, previousWhere);
                    updateAllRightListSpans(editable, previousParagraphStart, previousParagraphEnd);    ///更新选择区间内ListSpan
                    updateAllRightListSpans(editable, currentParagraphStart, currentParagraphEnd);  ///更新后续的ListSpan
                }

                break;
            }
        }
    }
    private <T> void adjustBlockCharacterStyleSpans(View view, Class<T> clazz, final Editable editable, final int start, final int end, boolean isSelected, boolean isRemove) {
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
                if (clazz == CustomURLSpan.class) {
                    final String viewTagText = (String) view.getTag(R.id.url_text);
                    final String viewTagUrl = (String) view.getTag(R.id.url_url);
                    final String compareText = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanUrl = ((CustomURLSpan) span).getURL();
                    if (isRemove && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        ///忽略TextWatcher中的UndoRedo
                        isSkipUndoRedo = true;
                        editable.replace(spanStart, spanEnd, viewTagText);
                        Selection.setSelection(editable, start, start + viewTagText.length());
                        isSkipUndoRedo = false;

                        ///[isUpdateNeeded]
                        view.setSelected(isSelected);
                        view.setTag(R.id.url_text, viewTagText);
                        view.setTag(R.id.url_url, viewTagUrl);
                        isUpdateNeeded = true;
                    } else {
                        if (!TextUtils.isEmpty(viewTagUrl) && !viewTagUrl.equals(spanUrl)) {
                            editable.removeSpan(span);
                            span = (T) new CustomURLSpan(viewTagUrl);
                            editable.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }

                ///字符span（带参数）：Image
                else if (clazz == VideoSpan.class || clazz == AudioSpan.class || clazz == CustomImageSpan.class) {
                    final String viewTagText = (String) view.getTag(R.id.image_text);
                    final String viewTagUri = (String) view.getTag(R.id.image_uri);
                    final String viewTagSrc = (String) view.getTag(R.id.image_src);
                    final int viewTagWidth = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                    final int viewTagHeight = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                    final int viewTagAlign = view.getTag(R.id.image_align) == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                    final String compareText = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanSrc = ((CustomImageSpan) span).getSource();
                    if (isRemove && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        ///忽略TextWatcher中的UndoRedo
                        isSkipUndoRedo = true;
                        editable.replace(spanStart, spanEnd, viewTagText);
                        Selection.setSelection(editable, start, start + viewTagText.length());
                        isSkipUndoRedo = false;

                        ///[isUpdateNeeded]
                        view.setSelected(isSelected);
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
            } else if (isRemove) {
                editable.removeSpan(span);
            }

            ///[isRemoveNeeded]
            if (!isRemove) {
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
            if (clazz == CustomURLSpan.class) {
                final String viewTagText = (String) view.getTag(R.id.url_text);
                final String viewTagUrl = (String) view.getTag(R.id.url_url);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isRemove && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    ///忽略TextWatcher
                    isSkipUndoRedo = true;
                    editable.replace(start, end, viewTagText);
                    Selection.setSelection(editable, start, start + viewTagText.length());
                    isSkipUndoRedo = false;
                } else {
                    if (!TextUtils.isEmpty(viewTagUrl)) {
                        editable.setSpan(new CustomURLSpan(viewTagUrl), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            ///字符span（带参数）：Image
            else if (clazz == VideoSpan.class || clazz == AudioSpan.class || clazz == CustomImageSpan.class) {
                final String viewTagText = (String) view.getTag(R.id.image_text);
                final String viewTagUri = (String) view.getTag(R.id.image_uri);
                final String viewTagSrc = (String) view.getTag(R.id.image_src);
                final int viewTagWidth = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                final int viewTagHeight = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                final int viewTagAlign = view.getTag(R.id.image_align) == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isRemove && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
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
    private <T> void adjustCharacterStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isSelected, boolean isRemove) {
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
                } else if (isRemove) {
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
//            findAndJoinLeftSpan(view, clazz, editable, start, rightSpanEnd, rightSpan);   ///无用！
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

        final int firstParagraphStart = SpanUtil.getParagraphStart(editable, start);
        final int firstParagraphEnd = SpanUtil.getParagraphEnd(editable, start);
        final int lastParagraphStart = SpanUtil.getParagraphStart(editable, end);
        final int lastParagraphEnd = SpanUtil.getParagraphEnd(editable, end);

        for (View view : mClassMap.keySet()) {
            if (isParagraphStyle(view)) {
                SpanUtil.removeParagraphSpans(mClassMap.get(view), editable, start, end);
                updateParagraphView(view, mClassMap.get(view), editable, firstParagraphStart, firstParagraphEnd);

                ///段落span（带初始化参数）：List
                ///新建ListSpan后，更新所有后续的ListSpan
                if (mClassMap.get(view) == ListSpan.class) {
                    updateAllRightListSpans(editable, lastParagraphStart, lastParagraphEnd);
                }
            }
        }
    }
    private void clearCharacterSpans(int start, int end) {
        final Editable editable = mRichEditText.getText();

        for (View view : mClassMap.keySet()) {
            if (isCharacterStyle(view)) {
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
