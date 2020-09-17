package cc.brainbook.android.richeditortoolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.os.Parcelable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cc.brainbook.android.colorpicker.builder.ColorPickerClickListener;
import cc.brainbook.android.colorpicker.builder.ColorPickerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.bean.SpanBean;
import cc.brainbook.android.richeditortoolbar.bean.TextBean;
import cc.brainbook.android.richeditortoolbar.builder.ClickImageSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickLeadingMarginSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickLineDividerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickListSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickQuoteSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.ClickURLSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;
import cc.brainbook.android.richeditortoolbar.helper.UndoRedoHelper;
import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.span.character.BorderSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.block.AudioSpan;
import cc.brainbook.android.richeditortoolbar.span.character.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.character.BlockSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CodeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomAbsoluteSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomBackgroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomForegroundColorSpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomImageSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.CustomLeadingMarginSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomRelativeSizeSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomScaleXSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomStrikethroughSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomSubscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomSuperscriptSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomFontFamilySpan;
import cc.brainbook.android.richeditortoolbar.span.block.CustomURLSpan;
import cc.brainbook.android.richeditortoolbar.span.character.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.DivSpan;
import cc.brainbook.android.richeditortoolbar.span.paragraph.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.character.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.paragraph.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListItemSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.ListSpan;
import cc.brainbook.android.richeditortoolbar.span.nest.PreSpan;
import cc.brainbook.android.richeditortoolbar.span.block.VideoSpan;
import cc.brainbook.android.richeditortoolbar.util.ArrayUtil;
import cc.brainbook.android.richeditortoolbar.util.FileUtil;
import cc.brainbook.android.richeditortoolbar.util.ParcelUtil;
import cc.brainbook.android.richeditortoolbar.util.PrefsUtil;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;
import cc.brainbook.android.richeditortoolbar.util.Util;

import static android.app.Activity.RESULT_OK;
import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.createChildrenListItemSpans;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.isListTypeOrdered;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.removeChildrenListItemSpans;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.updateListSpans;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.findAndJoinLeftSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.findAndJoinRightSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getLeftSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getParentSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getRightSpan;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isBlockCharacterStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isNestParagraphStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isCharacterStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isParagraphStyle;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.isSameWithViewParameter;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.joinSpanByPosition;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.updateCharacterStyleView;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.updateDescendantNestingLevel;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.updateParagraphView;
import static cc.brainbook.android.richeditortoolbar.util.SpanUtil.isInvalidParagraph;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class RichEditorToolbar extends FlexboxLayout implements
        Drawable.Callback, View.OnClickListener, View.OnLongClickListener,
        RichEditText.OnSelectionChanged,
        RichEditText.SaveSpansCallback, RichEditText.LoadSpansCallback,
        UndoRedoHelper.OnPositionChangedListener {
    public static final String SHARED_PREFERENCES_NAME_DRAFT = "rich_editor_shared_preferences_name_draft";
    public static final String SHARED_PREFERENCES_KEY_DRAFT_TEXT = "rich_editor_shared_preferences_key_draft_text";
    public static final String CLIPBOARD_FILE_NAME = "rich_editor_clipboard_file_name";
    public static final int REQUEST_CODE_HTML_EDITOR = 101;


    ///使用LinkedHashMap是为了保证顺序（ListItemSpan必须在ListSpan之后注册）
    private LinkedHashMap<Class<? extends Parcelable>, View> mClassMap = new LinkedHashMap<>();
    public LinkedHashMap<Class<? extends Parcelable>, View> getClassMap() {
        return mClassMap;
    }

    private RichEditText mRichEditText;
    public RichEditText getRichEditText() {
        return mRichEditText;
    }
    public void setEditText(RichEditText richEditText) {
        mRichEditText = richEditText;
    }

    private LineDividerSpan.DrawBackgroundCallback mDrawBackgroundCallback;
    public void setDrawBackgroundCallback(LineDividerSpan.DrawBackgroundCallback drawBackgroundCallback) {
        mDrawBackgroundCallback = drawBackgroundCallback;
    }

    /* ---------------- ///段落span（带初始化参数）：LeadingMargin ---------------- */
    private ImageView mImageViewLeadingMargin;
    private int mLeadingMarginSpanIndent = CustomLeadingMarginSpan.DEFAULT_INDENT;

    /* ---------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan ---------------- */
    private ImageView mImageViewAlignNormal;
    private ImageView mImageViewAlignCenter;
    private ImageView mImageViewAlignOpposite;

    /* ---------------- ///段落span（带初始化参数）：List ---------------- */
    private ImageView mImageViewList;
    private int mIndicatorMargin = ListSpan.DEFAULT_INDENT;
    private int mIndicatorWidth = ListItemSpan.DEFAULT_INDICATOR_WIDTH;
    private int mIndicatorGapWidth = ListItemSpan.DEFAULT_INDICATOR_GAP_WIDTH;
    private @ColorInt int mIndicatorColor = ListItemSpan.DEFAULT_INDICATOR_COLOR;

    /* ---------------- ///段落span（带初始化参数）：Quote ---------------- */
    private ImageView mImageViewQuote;
    private @ColorInt int mQuoteSpanColor = CustomQuoteSpan.STANDARD_COLOR;
    private int mQuoteSpanStripWidth = CustomQuoteSpan.STANDARD_STRIPE_WIDTH_PX;
    private int mQuoteSpanGapWidth = CustomQuoteSpan.STANDARD_GAP_WIDTH_PX;

    /* -------------- ///字符span（带参数）：Pre --------------- */
    private ImageView mImageViewPre;

    /* ---------------- ///段落span（带参数）：Head ---------------- */
    private TextView mTextViewHead;

    /* ---------------- ///段落span：LineDivider ---------------- */
    private ImageView mImageViewLineDivider;
    private int mLineDividerSpanMarginTop = LineDividerSpan.DEFAULT_MARGIN_TOP;
    private int mLineDividerSpanMarginBottom = LineDividerSpan.DEFAULT_MARGIN_BOTTOM;

    /* ---------------- ///字符span：Bold、Italic ---------------- */
    private ImageView mImageViewBold;
    private ImageView mImageViewItalic;

    /* ---------------- ///字符span：Underline、StrikeThrough、Subscript、Superscript ---------------- */
    private ImageView mImageViewUnderline;
    private ImageView mImageViewStrikeThrough;
    private ImageView mImageViewSubscript;
    private ImageView mImageViewSuperscript;

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

    /* ---------------- ///字符span：Code ---------------- */
    private ImageView mImageViewCode;

    /* ---------------- ///字符span：Block ---------------- */
    private ImageView mImageViewBlock;

    /* ---------------- ///字符span：Border ---------------- */
    private ImageView mImageViewBorder;

    /* ---------------- ///字符span（带参数）：URL ---------------- */
    private ImageView mImageViewURL;

    /* ---------------- ///字符span（带参数）：Image ---------------- */
    private ImageView mImageViewVideo;
    private ImageView mImageViewAudio;
    private ImageView mImageViewImage;
    private ClickImageSpanDialogBuilder mClickImageSpanDialogBuilder;

    private File mImageFilePath;  ///ImageSpan存放图片文件的目录，比如相机拍照、图片Crop剪切生成的图片文件
    public void setImageFilePath(File imageFilePath) {
        mImageFilePath = imageFilePath;
    }
    public File getImageFilePath() {
        return mImageFilePath;
    }
    private int mImageOverrideWidth = 200;
    private int mImageOverrideHeight = 200;
    public void setImageOverrideWidth(int imageOverrideWidth) {
        mImageOverrideWidth = imageOverrideWidth;
    }
    public void setImageOverrideHeight(int imageOverrideHeight) {
        mImageOverrideHeight = imageOverrideHeight;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_HTML_EDITOR == requestCode) {
            if (RESULT_OK == resultCode) {
                ///[HtmlEditor#onActivityResult]
                final Editable editable = mRichEditText.getText();
                if (editable != null) {
                    CharSequence htmlResult;

                    if (data != null) {
                        final String htmlText = data.getStringExtra("html_result");
                        if (htmlText != null) {
                            editable.replace(0, editable.length(), Html.fromHtml(htmlText));

                            ///[postSetText#执行postLoadSpans及后处理，否则LineDividerSpan、ImageSpan/VideoSpan/AudioSpan不会显示！]
                            postSetText();
                        }
                    }
                }
            }
        } else if (mClickImageSpanDialogBuilder != null) {
            ///[ImageSpan#ClickImageSpanDialogBuilder#onActivityResult()]
            mClickImageSpanDialogBuilder.onActivityResult(requestCode, resultCode, data);
        }
    }

    ///[ImageSpan#Glide#GifDrawable]
    ///注意：TextView在实际使用中可能不由EditText产生并赋值，所以需要单独另行处理Glide#GifDrawable的Callback
    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        final Editable editable = mRichEditText.getText();
        RichEditorToolbarHelper.setImageSpan(editable, drawable);

        ///使TextViewPreview无效，从而刷新TextViewPreview
        if (!TextUtils.isEmpty(mTextViewPreview.getText())) {
//                    mTextViewPreview.invalidateDrawable(drawable);//////??????无效！
            mTextViewPreview.invalidate();
        }
    }

    ///[ImageSpan#Glide#loadImage()#Placeholder]
    ///注意：mPlaceholderDrawable和mPlaceholderResourceId必须至少设置其中一个！如都设置则mPlaceholderDrawable优先
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

    /* ---------------- ///[清除样式] ---------------- */
    private ImageView mImageViewClearSpans;

    /* ---------------- ///[草稿Draft] ---------------- */
    private ImageView mImageViewSaveDraft;
    private ImageView mImageViewRestoreDraft;
    private ImageView mImageViewClearDraft;

    private boolean checkDraft() {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME_DRAFT, Context.MODE_PRIVATE);
        final String draftText = sharedPreferences.getString(SHARED_PREFERENCES_KEY_DRAFT_TEXT, null);
        final boolean hasDraft = !TextUtils.isEmpty(draftText);
        mImageViewRestoreDraft.setEnabled(hasDraft);
        mImageViewRestoreDraft.setSelected(hasDraft);
        mImageViewClearDraft.setEnabled(hasDraft);
        return hasDraft;
    }

    /* ---------------- ///[TextContextMenu#Clipboard] ---------------- */
    ///[clipboard]存放剪切板的文件目录
    ///由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
    private File mClipboardFile;

    @Override
    public void saveSpans(Editable editable, int selectionStart, int selectionEnd) {
        try {
            final byte[] bytes = RichEditorToolbarHelper.toByteArray(mClassMap, editable, selectionStart, selectionEnd, true);
            FileUtil.writeFile(mClipboardFile, bytes);
        } catch (IOException e) {
            if (DEBUG) Log.e("TAG", "Error: Cannot write Clipboard file");
            e.printStackTrace();
        }
    }

    ///[pasteEditable/pasteOffset]
    ///使用pasteOffset区分是否为paste操作，如offset为-1则不是，offset大于等于0则是
    ///如果pasteEditable为null，则忽略pasteOffset（即pasteOffset为-1）
    @Override
    public void loadSpans(Editable pasteEditable, int pasteOffset) {
        try {
            final byte[] bytes = FileUtil.readFile(mClipboardFile);

            ///执行postLoadSpans及后处理
            RichEditorToolbarHelper.postLoadSpans(mContext, pasteEditable, pasteOffset, RichEditorToolbarHelper.fromByteArray(pasteEditable, bytes),
                    mPlaceholderDrawable, mPlaceholderResourceId, this, mDrawBackgroundCallback);
        } catch (IOException e) {
            if (DEBUG) Log.e("TAG", "Error: Cannot read Clipboard file");
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
        if (editable == null) {
            return;
        }

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

            ///执行postLoadSpans及后处理
            RichEditorToolbarHelper.postLoadSpans(mContext, mRichEditText.getText(), -1, RichEditorToolbarHelper.fromByteArray(mRichEditText.getText(), action.getBytes()),
                    mPlaceholderDrawable, mPlaceholderResourceId, this, mDrawBackgroundCallback);
        }
    }

    /* ---------------- ///[Preview] ---------------- */
    private ImageView mImageViewPreview;

    private TextView mTextViewPreview;
    public void setPreview(TextView textViewPreview) {
        mTextViewPreview = textViewPreview;
    }

    private boolean enableSelectionChange = true;
    private void updatePreview() {
        if (mTextViewPreview.getVisibility() == VISIBLE) {
            ///[enableSelectionChange]禁止onSelectionChanged()
            ///注意：mTextViewPreview.setText()会引起mRichEditText#onSelectionChanged()，从而造成无selection单光标时切换toolbar按钮失效！
            enableSelectionChange = false;
            mTextViewPreview.setText(mRichEditText.getText());
            enableSelectionChange = true;
        }
    }

    /* ---------------- ///[Html] ---------------- */
    private ImageView mImageViewHtml;

    private int mHtmlOption = Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;
    public void setHtmlOption(int htmlOption) {
        mHtmlOption = htmlOption;
    }
    public int getHtmlOption() {
        return mHtmlOption;
    }

    ///[HtmlEditor#HtmlEditorCallback]
    public interface HtmlEditorCallback {
        void startHtmlEditorActivity(String htmlString);
    }
    private HtmlEditorCallback mHtmlEditorCallback;
    public void setHtmlEditorCallback(HtmlEditorCallback htmlEditorCallback) {
        mHtmlEditorCallback = htmlEditorCallback;
    }

    /* ------------------------------------------------ */
    ///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
    private Context mContext;

    public RichEditorToolbar(Context context) {
        this(context, null);
    }

    public RichEditorToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichEditorToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RichEditorToolbar, defStyleAttr, 0);

        init(context, a);

        a.recycle();
    }

    public void init(Context context, TypedArray a) {
        mContext = context;

        ///[clipboard]设置存放剪切板的文件目录
        ///由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
        ///注意：建议使用应用的cache目录，而getExternalCacheDir()在API 30中可能会返回null！
        mClipboardFile = new File(mContext.getCacheDir() + File.separator + CLIPBOARD_FILE_NAME);

        mUndoRedoHelper = new UndoRedoHelper(mContext, this);

        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);

        LayoutInflater.from(mContext).inflate(R.layout.layout_tool_bar, this, true);


        /* -------------- ///段落span：Div --------------- */
        mClassMap.put(DivSpan.class, null);

        /* -------------- ///段落span（带初始化参数）：LeadingMargin --------------- */
        mImageViewLeadingMargin = (ImageView) findViewById(R.id.iv_leading_margin);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_leading_margin, true)) {
            mImageViewLeadingMargin.setOnClickListener(this);
            mImageViewLeadingMargin.setOnLongClickListener(this);
            mClassMap.put(CustomLeadingMarginSpan.class, mImageViewLeadingMargin);
        } else {
            mImageViewLeadingMargin.setVisibility(GONE);
        }

        /* -------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan --------------- */
        mImageViewAlignNormal = (ImageView) findViewById(R.id.iv_align_normal);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_align_normal, true)) {
            mImageViewAlignNormal.setOnClickListener(this);
            mClassMap.put(AlignNormalSpan.class, mImageViewAlignNormal);
        } else {
            mImageViewAlignNormal.setVisibility(GONE);
        }

        mImageViewAlignCenter = (ImageView) findViewById(R.id.iv_align_center);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_align_center, true)) {
            mImageViewAlignCenter.setOnClickListener(this);
            mClassMap.put(AlignCenterSpan.class, mImageViewAlignCenter);
        } else {
            mImageViewAlignCenter.setVisibility(GONE);
        }

        mImageViewAlignOpposite = (ImageView) findViewById(R.id.iv_align_opposite);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_align_opposite, true)) {
            mImageViewAlignOpposite.setOnClickListener(this);
            mClassMap.put(AlignOppositeSpan.class, mImageViewAlignOpposite);
        } else {
            mImageViewAlignOpposite.setVisibility(GONE);
        }

        /* -------------- ///段落span（带初始化参数）：List --------------- */
        mImageViewList = (ImageView) findViewById(R.id.iv_list);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_list, true)) {
            mImageViewList.setOnClickListener(this);
            mImageViewList.setOnLongClickListener(this);
            mClassMap.put(ListSpan.class, mImageViewList);
            ///注意：ListItemSpan也要注册！否则不能保存到草稿等！
            ///而且必须在ListSpan之后！否则loadSpansFromSpanBeans()中的getParentNestSpan()将返回null
            mClassMap.put(ListItemSpan.class, null);
        } else {
            mImageViewList.setVisibility(GONE);
        }

        /* -------------- ///段落span（带初始化参数）：Quote --------------- */
        mImageViewQuote = (ImageView) findViewById(R.id.iv_quote);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_quote, true)) {
            mImageViewQuote.setOnClickListener(this);
            mImageViewQuote.setOnLongClickListener(this);
            mClassMap.put(CustomQuoteSpan.class, mImageViewQuote);
        } else {
            mImageViewQuote.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：Pre --------------- */
        mImageViewPre = (ImageView) findViewById(R.id.iv_pre);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_pre, true)) {
            mImageViewPre.setOnClickListener(this);
            mClassMap.put(PreSpan.class, mImageViewPre);
        } else {
            mImageViewPre.setVisibility(GONE);
        }

        /* -------------- ///段落span（带参数）：Head --------------- */
        mTextViewHead = (TextView) findViewById(R.id.tv_head);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_head, true)) {
            mTextViewHead.setOnClickListener(this);
            mClassMap.put(HeadSpan.class, mTextViewHead);
        } else {
            mTextViewHead.setVisibility(GONE);
        }

        /* -------------- ///段落span：LineDivider --------------- */
        mImageViewLineDivider = (ImageView) findViewById(R.id.iv_line_divider);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_line_divider, true)) {
            mImageViewLineDivider.setOnClickListener(this);
            mImageViewLineDivider.setOnLongClickListener(this);
            mClassMap.put(LineDividerSpan.class, mImageViewLineDivider);
        } else {
            mImageViewLineDivider.setVisibility(GONE);
        }

        /* -------------- ///字符span：Bold、Italic --------------- */
        mImageViewBold = (ImageView) findViewById(R.id.iv_bold);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_bold, true)) {
            mImageViewBold.setOnClickListener(this);
            mClassMap.put(BoldSpan.class, mImageViewBold);
        } else {
            mImageViewBold.setVisibility(GONE);
        }

        mImageViewItalic = (ImageView) findViewById(R.id.iv_italic);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_italic, true)) {
            mImageViewItalic.setOnClickListener(this);
            mClassMap.put(ItalicSpan.class, mImageViewItalic);
        } else {
            mImageViewItalic.setVisibility(GONE);
        }

        /* ------------ ///字符span：Underline、StrikeThrough、Subscript、Superscript ------------ */
        mImageViewUnderline = (ImageView) findViewById(R.id.iv_underline);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_underline, true)) {
            mImageViewUnderline.setOnClickListener(this);
            mClassMap.put(CustomUnderlineSpan.class, mImageViewUnderline);
        } else {
            mImageViewUnderline.setVisibility(GONE);
        }

        mImageViewStrikeThrough = (ImageView) findViewById(R.id.iv_strikethrough);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_strikethrough, true)) {
            mImageViewStrikeThrough.setOnClickListener(this);
            mClassMap.put(CustomStrikethroughSpan.class, mImageViewStrikeThrough);
        } else {
            mImageViewStrikeThrough.setVisibility(GONE);
        }

        mImageViewSuperscript = (ImageView) findViewById(R.id.iv_superscript);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_superscript, true)) {
            mImageViewSuperscript.setOnClickListener(this);
            mClassMap.put(CustomSuperscriptSpan.class, mImageViewSuperscript);
        } else {
            mImageViewSuperscript.setVisibility(GONE);
        }

        mImageViewSubscript = (ImageView) findViewById(R.id.iv_subscript);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_subscript, true)) {
            mImageViewSubscript.setOnClickListener(this);
            mClassMap.put(CustomSubscriptSpan.class, mImageViewSubscript);
        } else {
            mImageViewSubscript.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：ForegroundColor、BackgroundColor --------------- */
        mImageViewForegroundColor = (ImageView) findViewById(R.id.iv_foreground_color);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_foreground_color, true)) {
            mImageViewForegroundColor.setOnClickListener(this);
            mClassMap.put(CustomForegroundColorSpan.class, mImageViewForegroundColor);
        } else {
            mImageViewForegroundColor.setVisibility(GONE);
        }

        mImageViewBackgroundColor = (ImageView) findViewById(R.id.iv_background_color);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_background_color, true)) {
            mImageViewBackgroundColor.setOnClickListener(this);
            mClassMap.put(CustomBackgroundColorSpan.class, mImageViewBackgroundColor);
        } else {
            mImageViewBackgroundColor.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：FontFamily --------------- */
        mTextViewFontFamily = (TextView) findViewById(R.id.tv_font_family);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_font_family, true)) {
            mTextViewFontFamily.setOnClickListener(this);
            mClassMap.put(CustomFontFamilySpan.class, mTextViewFontFamily);
        } else {
            mTextViewFontFamily.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：AbsoluteSize --------------- */
        mTextViewAbsoluteSize = (TextView) findViewById(R.id.tv_absolute_size);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_absolute_size, true)) {
            mTextViewAbsoluteSize.setOnClickListener(this);
            mClassMap.put(CustomAbsoluteSizeSpan.class, mTextViewAbsoluteSize);
        } else {
            mTextViewAbsoluteSize.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：RelativeSize --------------- */
        mTextViewRelativeSize = (TextView) findViewById(R.id.tv_relative_size);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_relative_size, true)) {
            mTextViewRelativeSize.setOnClickListener(this);
            mClassMap.put(CustomRelativeSizeSpan.class, mTextViewRelativeSize);
        } else {
            mTextViewRelativeSize.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：ScaleX --------------- */
        mTextViewScaleX = (TextView) findViewById(R.id.tv_scale_x);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_scale_x, true)) {
            mTextViewScaleX.setOnClickListener(this);
            mClassMap.put(CustomScaleXSpan.class, mTextViewScaleX);
        } else {
            mTextViewScaleX.setVisibility(GONE);
        }

        /* -------------- ///字符span：Code --------------- */
        mImageViewCode = (ImageView) findViewById(R.id.iv_code);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_code, true)) {
            mImageViewCode.setOnClickListener(this);
            mClassMap.put(CodeSpan.class, mImageViewCode);
        } else {
            mImageViewCode.setVisibility(GONE);
        }

        /* -------------- ///字符span：Block --------------- */
        mImageViewBlock = (ImageView) findViewById(R.id.iv_block);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_block, true)) {
            mImageViewBlock.setOnClickListener(this);
            mClassMap.put(BlockSpan.class, mImageViewBlock);
        } else {
            mImageViewBlock.setVisibility(GONE);
        }

        /* -------------- ///字符span：Border --------------- */
        mImageViewBorder = (ImageView) findViewById(R.id.iv_border);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_border, true)) {
            mImageViewBorder.setOnClickListener(this);
            mClassMap.put(BorderSpan.class, mImageViewBorder);
        } else {
            mImageViewBorder.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：URL --------------- */
        mImageViewURL = (ImageView) findViewById(R.id.iv_url);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_url, true)) {
            mImageViewURL.setOnClickListener(this);
            mClassMap.put(CustomURLSpan.class, mImageViewURL);
        } else {
            mImageViewURL.setVisibility(GONE);
        }

        /* -------------- ///字符span（带参数）：Image --------------- */
        mImageViewVideo = (ImageView) findViewById(R.id.iv_video);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_video, true)) {
            mImageViewVideo.setOnClickListener(this);
            mClassMap.put(VideoSpan.class, mImageViewVideo);
        } else {
            mImageViewVideo.setVisibility(GONE);
        }

        mImageViewAudio = (ImageView) findViewById(R.id.iv_audio);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_audio, true)) {
            mImageViewAudio.setOnClickListener(this);
            mClassMap.put(AudioSpan.class, mImageViewAudio);
        } else {
            mImageViewAudio.setVisibility(GONE);
        }

        mImageViewImage = (ImageView) findViewById(R.id.iv_image);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_image, true)) {
            mImageViewImage.setOnClickListener(this);
            mClassMap.put(CustomImageSpan.class, mImageViewImage);
        } else {
            mImageViewImage.setVisibility(GONE);
        }


        /* -------------- ///[清除样式] --------------- */
        mImageViewClearSpans = (ImageView) findViewById(R.id.iv_clear_spans);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_clear_spans, true)) {
            mImageViewClearSpans.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Editable editable = mRichEditText.getText();
                    if (editable == null) {
                        return;
                    }

                    final int selectionStart = Selection.getSelectionStart(editable);
                    final int selectionEnd = Selection.getSelectionEnd(editable);
                    if (selectionStart == -1 || selectionEnd == -1) {
                        return;
                    }

                    ///[更新ListSpan]
                    ArrayList<ListSpan> updateListSpans = new ArrayList<>();

                    for (Class<? extends Parcelable> clazz : mClassMap.keySet()) {
                        if (mClassMap.get(clazz) == null) {
                            continue;
                        }
                        if (isParagraphStyle(clazz)) {
                            mClassMap.get(clazz).setSelected(false);
                            if (isNestParagraphStyle(clazz)) {
                                adjustNestParagraphStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, true, updateListSpans);
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

                    ///[更新ListSpan]
                    updateListSpans(editable, updateListSpans);

                    ///[Preview]
                    updatePreview();

                    ///[Undo/Redo]
                    mUndoRedoHelper.addHistory(UndoRedoHelper.CLEAR_SPANS_ACTION, selectionStart, null, null,
                            RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
                }
            });
        } else {
            mImageViewClearSpans.setVisibility(GONE);
        }

        /* -------------- ///[草稿Draft] --------------- */
        mImageViewSaveDraft = (ImageView) findViewById(R.id.iv_save_draft);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_save_draft, true)) {
            mImageViewSaveDraft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Editable editable = mRichEditText.getText();
                    if (editable == null) {
                        return;
                    }

                    final byte[] bytes = RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), true);
                    PrefsUtil.putString(mContext, SHARED_PREFERENCES_NAME_DRAFT, SHARED_PREFERENCES_KEY_DRAFT_TEXT, Base64.encodeToString(bytes, 0));

                    if (checkDraft()) {
                        Toast.makeText(mContext.getApplicationContext(), R.string.save_draft_successful, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), R.string.save_draft_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mImageViewSaveDraft.setVisibility(GONE);
        }

        mImageViewRestoreDraft = (ImageView) findViewById(R.id.iv_restore_draft);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_restore_draft, true)) {
            mImageViewRestoreDraft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String draftText = PrefsUtil.getString(mContext, SHARED_PREFERENCES_NAME_DRAFT, SHARED_PREFERENCES_KEY_DRAFT_TEXT, null);
                    if (TextUtils.isEmpty(draftText)) {
                        return;
                    }

                    final TextBean textBean = ParcelUtil.unmarshall(Base64.decode(draftText, Base64.DEFAULT), TextBean.CREATOR);
                    if (textBean != null) {
                        final Editable beforeEditable = mRichEditText.getText();
                        final int selectionStart = Selection.getSelectionStart(beforeEditable);
                        final int selectionEnd = Selection.getSelectionEnd(beforeEditable);
                        assert beforeEditable != null;
                        final String beforeChange = beforeEditable.toString();

                        ///忽略TextWatcher
                        isSkipTextWatcher = true;
                        mRichEditText.setText(textBean.getText());
                        isSkipTextWatcher = false;

                        final Editable editable = mRichEditText.getText();

                        ///[FIX#当光标位置未发生变化时不会调用selectionChanged()来更新view的select状态！]
                        ///解决：此时应手动调用selectionChanged()来更新view的select状态
                        if (selectionStart == selectionEnd && selectionEnd == 0) {
                            selectionChanged(0, 0);
                        } else {
                            Selection.setSelection(editable, 0);
                        }

                        final List<SpanBean<?>> spanBeans = textBean.getSpans();
                        ///执行postLoadSpans及后处理
                        RichEditorToolbarHelper.postLoadSpans(mContext, editable, -1, RichEditorToolbarHelper.fromSpanBeans(spanBeans, editable),
                                mPlaceholderDrawable, mPlaceholderResourceId, RichEditorToolbar.this, mDrawBackgroundCallback);

                        ///[Preview]
                        updatePreview();

                        ///[Undo/Redo]
                        assert editable != null;
                        mUndoRedoHelper.addHistory(UndoRedoHelper.RESTORE_DRAFT_ACTION, 0, beforeChange, editable.toString(),
                                RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));

                        Toast.makeText(mContext.getApplicationContext(), R.string.restore_draft_successful, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mImageViewRestoreDraft.setVisibility(GONE);
        }

        mImageViewClearDraft = (ImageView) findViewById(R.id.iv_clear_draft);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_clear_draft, true)) {
            mImageViewClearDraft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    PrefsUtil.clear(mContext, SHARED_PREFERENCES_NAME_DRAFT);

                    if (!checkDraft()) {
                        Toast.makeText(mContext.getApplicationContext(), R.string.clear_draft_successful, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), R.string.clear_draft_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            mImageViewClearDraft.setVisibility(GONE);
        }

        ///初始化时检查有无草稿Draft
        if (checkDraft()) {
            Toast.makeText(mContext.getApplicationContext(), R.string.has_draft, Toast.LENGTH_SHORT).show();
        }

        /* ------------------- ///[Undo/Redo/Save] ------------------- */
        mImageViewUndo = (ImageView) findViewById(R.id.iv_undo);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_undo, true)) {
            mImageViewUndo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUndoRedoHelper.undo();

                    ///[Preview]
                    updatePreview();
                }
            });
        } else {
            mImageViewUndo.setVisibility(GONE);
        }

        mImageViewRedo = (ImageView) findViewById(R.id.iv_redo);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_redo, true)) {
            mImageViewRedo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUndoRedoHelper.redo();

                    ///[Preview]
                    updatePreview();
                }
            });
        } else {
            mImageViewRedo.setVisibility(GONE);
        }

        mImageViewSave = (ImageView) findViewById(R.id.iv_save);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_save, true)) {
            mImageViewSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUndoRedoHelper.resetSavedPosition();
                    mImageViewSave.setSelected(false);
                    mImageViewSave.setEnabled(false);
                }
            });
        } else {
            mImageViewSave.setVisibility(GONE);
        }

        /* -------------- ///[Preview] --------------- */
        mImageViewPreview = (ImageView) findViewById(R.id.iv_preview);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_preview, true)) {
            mImageViewPreview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setSelected(!view.isSelected());

                    if (view.isSelected()) {
                        mRichEditText.setVisibility(GONE);
                        mTextViewPreview.setVisibility(VISIBLE);
                        updatePreview();
                    } else {
                        mRichEditText.setVisibility(VISIBLE);
                        mTextViewPreview.setVisibility(GONE);
                        mTextViewPreview.setText(null);
                    }
                }
            });
        } else {
            mImageViewPreview.setVisibility(GONE);
        }

        /* -------------- ///[Html] --------------- */
        mImageViewHtml = (ImageView) findViewById(R.id.iv_html);
        ///[RichEditorToolbar是否显示某按钮（app:enable_XXX）]
        if (a.getBoolean(R.styleable.RichEditorToolbar_enable_html, true)) {
            mImageViewHtml.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String htmlString = Html.toHtml(mRichEditText.getText(), mHtmlOption);

                    ///[HtmlEditor#启动HtmlEditorActivity]
                    if (mHtmlEditorCallback != null) {
                        mHtmlEditorCallback.startHtmlEditorActivity(htmlString);
                    }
                }
            });

            mImageViewHtml.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setSingleChoiceItems(R.array.html_option, mHtmlOption, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHtmlOption = which;

                                    dialog.dismiss();
                                }
                            })
                            .show();

                    return true;
                }
            });
        } else {
            mImageViewHtml.setVisibility(GONE);
        }
    }

    public void init() {
        mRichEditText.addTextChangedListener(new RichTextWatcher());
        mRichEditText.setOnSelectionChanged(this);
        mRichEditText.setSaveSpansCallback(this);
        mRichEditText.setLoadSpansCallback(this);

        ///[Undo/Redo]初始化时设置Undo/Redo各按钮的状态
        initUndoRedo();

        ///[postSetText#执行postLoadSpans及后处理，否则LineDividerSpan、ImageSpan/VideoSpan/AudioSpan不会显示！]
        postSetText();
    }

    ///[postSetText#执行postLoadSpans及后处理，否则LineDividerSpan、ImageSpan/VideoSpan/AudioSpan不会显示！]
    private void postSetText() {
        final Editable editable = mRichEditText.getText();
        if (editable == null) {
            return;
        }

        final TextBean textBean = RichEditorToolbarHelper.saveSpans(mClassMap, editable, 0, editable.length(), false);
        final List<SpanBean<?>> spanBeans = textBean.getSpans();
        RichEditorToolbarHelper.postLoadSpans(mContext, editable, -1, RichEditorToolbarHelper.fromSpanBeans(spanBeans, editable),
                mPlaceholderDrawable, mPlaceholderResourceId, this, mDrawBackgroundCallback);
    }

    private int getActionId(View view) {
        if (view == null) {
            return -1;
        } else if (view == mImageViewAlignNormal) {
            return UndoRedoHelper.CHANGE_ALIGN_NORMAL_SPAN_ACTION;
        } else if (view == mImageViewAlignCenter) {
            return UndoRedoHelper.CHANGE_ALIGN_CENTER_SPAN_ACTION;
        } else if (view == mImageViewAlignOpposite) {
            return UndoRedoHelper.CHANGE_ALIGN_OPPOSITE_SPAN_ACTION;
        } else if (view == mImageViewLeadingMargin) {
            return UndoRedoHelper.CHANGE_LEADING_MARGIN_SPAN_ACTION;
        } else if (view == mImageViewList) {
            return UndoRedoHelper.CHANGE_LIST_SPAN_ACTION;
        } else if (view == mImageViewQuote) {
            return UndoRedoHelper.CHANGE_QUOTE_SPAN_ACTION;
        } else if (view == mImageViewPre) {
            return UndoRedoHelper.CHANGE_PRE_SPAN_ACTION;
        } else if (view == mTextViewHead) {
            return UndoRedoHelper.CHANGE_HEAD_SPAN_ACTION;
        } else if (view == mImageViewLineDivider) {
            return UndoRedoHelper.CHANGE_LINE_DIVIDER_SPAN_ACTION;
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
        } else if (view == mImageViewCode) {
            return UndoRedoHelper.CHANGE_CODE_SPAN_ACTION;
        } else if (view == mImageViewBlock) {
            return UndoRedoHelper.CHANGE_BLOCK_SPAN_ACTION;
        } else if (view == mImageViewBorder) {
            return UndoRedoHelper.CHANGE_BORDER_SPAN_ACTION;
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
        if (editable == null) {
            return;
        }

        final Class<?> clazz = RichEditorToolbarHelper.getClassMapKey(mClassMap, view);
        if (isParagraphStyle(clazz)) {
            final int selectionStart = Selection.getSelectionStart(editable);
            final int selectionEnd = Selection.getSelectionEnd(editable);

            ///段落span（带初始化参数）：List
            if (view == mImageViewList) {
                final int listType = view.getTag(R.id.list_list_type) == null ? Integer.MIN_VALUE :  (int) view.getTag(R.id.list_list_type);
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = view.getTag(R.id.list_list_type) == null ? -1 :
                        ArrayUtil.getIntIndex(mContext, R.array.list_type_ids, (int) view.getTag(R.id.list_list_type));
                final AlertDialog listSpanAlertDialog = new AlertDialog.Builder(mContext)
                        ///[FIX#自定义单选或多选AlertDialog中含有其它控件时，ListView太长导致无法滚动显示完整]
                        ///不能用setView()！改为ListView.addFooterView()
//                        .setView(listSpanDialogView)
                        .setSingleChoiceItems(R.array.list_type_labels, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final int listType = ArrayUtil.getIntItem(mContext, R.array.list_type_ids, which);
                                final EditText editTextStart = ((AlertDialog) dialog).findViewById(R.id.et_start);
                                @SuppressLint("UseSwitchCompatOrMaterialCode")
                                final Switch switchIsReversed = ((AlertDialog) dialog).findViewById(R.id.switch_is_reversed);
                                assert editTextStart != null;
                                editTextStart.setEnabled(isListTypeOrdered(listType));
                                assert switchIsReversed != null;
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

                                ///更新View
                                ///注意：多层NestParagraphStyle span在清除样式后，仍然可能view被选中
                                selectionChanged(selectionStart, selectionEnd);

                                ///清空view tag
                                view.setTag(R.id.list_start, null);
                                view.setTag(R.id.list_is_reversed, null);
                                view.setTag(R.id.list_list_type, null);

                                ///[Preview]
                                updatePreview();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///获取listTypeIndex并由此得到对应的listType
                                final int listTypeIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                final int listType = ArrayUtil.getIntItem(mContext, R.array.list_type_ids, listTypeIndex);

                                final EditText editTextStart = ((AlertDialog) dialog).findViewById(R.id.et_start);
                                assert editTextStart != null;
                                final int start = Integer.parseInt(editTextStart.getText().toString());
                                @SuppressLint("UseSwitchCompatOrMaterialCode")
                                final Switch switchIsReversed = ((AlertDialog) dialog).findViewById(R.id.switch_is_reversed);
                                assert switchIsReversed != null;
                                final boolean isReversed = switchIsReversed.isChecked();

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
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                ///[FIX#自定义单选或多选AlertDialog中含有其它控件时，ListView太长导致无法滚动显示完整]
                ///不能用setView()！改为ListView.addFooterView()
                final View listSpanDialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_click_list_span_dialog, null);
                final ListView listView = listSpanAlertDialog.getListView();
                listView.addFooterView(listSpanDialogView);

                ///初始化AlertDialog
                final boolean isEnabled = view.getTag(R.id.list_list_type) != null && isListTypeOrdered(listType);
                final EditText editTextStart = (EditText) listSpanAlertDialog.findViewById(R.id.et_start);
                assert editTextStart != null;
                editTextStart.setEnabled(isEnabled);
                final int start = view.getTag(R.id.list_start) == null ? 1 :  (int) view.getTag(R.id.list_start);
                editTextStart.setText(String.valueOf(start));
                @SuppressLint("UseSwitchCompatOrMaterialCode")
                final Switch switchIsReversed = (Switch) listSpanAlertDialog.findViewById(R.id.switch_is_reversed);
                assert switchIsReversed != null;
                switchIsReversed.setEnabled(isEnabled);
                final boolean isReversed = view.getTag(R.id.list_is_reversed) != null && (boolean) view.getTag(R.id.list_is_reversed);
                switchIsReversed.setChecked(isReversed);

                return;
            }

            ///段落span（带初始化参数）：Quote
            else if (view == mImageViewQuote && selectionStart < selectionEnd && view.isSelected()) {
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

                                ///更新View
                                ///注意：多层NestParagraphStyle span在清除样式后，仍然可能view被选中
                                selectionChanged(selectionStart, selectionEnd);

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

            ///段落span（带参数）：Head
            else if (view == mTextViewHead) {
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
                if (selectionStart < selectionEnd || selectionStart == editable.length()
                        || editable.charAt(selectionStart) != '\n'
                        || selectionStart != 0 && editable.charAt(selectionStart - 1) != '\n') {
                    return;
                }
            }

            view.setSelected(!view.isSelected());

            ///改变selection的span
            applyParagraphStyleSpans(view, editable);

            ///更新View
            ///注意：多层NestParagraphStyle span在清除样式后，仍然可能view被选中
            if (view == mImageViewQuote && selectionStart == selectionEnd) {
                selectionChanged(selectionStart, selectionEnd);
            }

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
                                final ArrayList<CustomURLSpan> selectedSpans = SpanUtil.getSelectedSpans(CustomURLSpan.class, mRichEditText.getText());
                                if ((text.length() == 0 || url.length() == 0) && selectedSpans.size() == 0) {  //////??????url正则表达式
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
                mClickImageSpanDialogBuilder = ClickImageSpanDialogBuilder
                        .with(mContext, mediaType)
                        .setImageFilePath(mImageFilePath)
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
                        ///注意：加入null的强转，为了避免混淆ClickImageSpanDialogBuilder和BaseDialogBuilder的setNegativeButton()方法！
                        .setNegativeButton(android.R.string.cancel, (ClickImageSpanDialogBuilder.OnClickListener) null);

                final String uri = (String) view.getTag(R.id.image_uri);
                final String src = (String) view.getTag(R.id.image_src);
                final int width = view.getTag(R.id.image_width) == null ? 0 : (int) view.getTag(R.id.image_width);
                final int height = view.getTag(R.id.image_height) == null ? 0 : (int) view.getTag(R.id.image_height);
                final int align = view.getTag(R.id.image_align) == null ? ClickImageSpanDialogBuilder.DEFAULT_ALIGN : (int) view.getTag(R.id.image_align);
                mClickImageSpanDialogBuilder.initial(uri, src, width, height, align, mImageOverrideWidth, mImageOverrideHeight);
                mClickImageSpanDialogBuilder.build().show();

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

        ///[更新ListSpan]
        ArrayList<ListSpan> updateListSpans = new ArrayList<>();

        final Class<?> clazz = RichEditorToolbarHelper.getClassMapKey(mClassMap, view);
        if (isNestParagraphStyle(clazz)) {
            adjustNestParagraphStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true, updateListSpans);
        } else {
            adjustParagraphStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true);
        }

        ///[更新ListSpan]
        updateListSpans(editable, updateListSpans);

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            mUndoRedoHelper.addHistory(getActionId(view), selectionEnd, beforeChange, afterChange,
                    RichEditorToolbarHelper.toByteArray(mClassMap, editable, 0, editable.length(), false));
        }
    }

    private void applyCharacterStyleSpans(View view, Editable editable) {
        String beforeChange = null;
        int selectionStart = -1;
        int selectionEnd = -1;
        final Class<?> clazz = RichEditorToolbarHelper.getClassMapKey(mClassMap, view);

        ///[BlockCharacterStyle#beforeChange]调整BlockCharacterStyle的Selection为第一个span的起始位置和最后span的结尾位置
        if (isBlockCharacterStyle(clazz) && view.isSelected()) {
            final ArrayList<?> selectedSpans = SpanUtil.getSelectedSpans(clazz, editable);
            if (!selectedSpans.isEmpty()) {
                selectionStart = editable.getSpanStart(selectedSpans.get(0));
                selectionEnd = editable.getSpanEnd(selectedSpans.get(selectedSpans.size() - 1));
            }
        }
        if (selectionStart == -1 || selectionEnd == -1) {
            selectionStart = Selection.getSelectionStart(editable);
            selectionEnd = Selection.getSelectionEnd(editable);
        }

        ///当selectionStart != selectionEnd时改变selection的span
        if (selectionStart == -1 || selectionEnd == -1) {
            return;
        }

        if (isBlockCharacterStyle(clazz)) {
            beforeChange = editable.subSequence(selectionStart, selectionEnd).toString();
            adjustBlockCharacterStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true);
        } else {
            adjustCharacterStyleSpans(view, clazz, editable, selectionStart, selectionEnd, true);
        }

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            if (isBlockCharacterStyle(clazz) && view.isSelected()) {
                final int afterSelectionStart = Selection.getSelectionStart(editable);
                final int afterSelectionEnd = Selection.getSelectionEnd(editable);
                mUndoRedoHelper.addHistory(getActionId(view), selectionStart,
                        beforeChange,
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

        for (Class<?> clazz : mClassMap.keySet()) {
            if (mClassMap.get(clazz) != null) {
                if (isParagraphStyle(clazz)) {
                    updateParagraphView(mContext, mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd);
                } else if (isCharacterStyle(clazz)) {
                    updateCharacterStyleView(mContext, mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd);
                }
            }

            ///test
            if (DEBUG && editable != null) Util.testOutput(editable, clazz);
        }
    }


    /* ----------------- ///[TextWatcher] ------------------ */
    ///注意：语言环境为english时，存在before/count/after都大于0的情况！此时start为单词开始处（以空格或换行分割）
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
                for (Class<? extends Parcelable> clazz : mClassMap.keySet()) {
                    ///清除掉已经被删除的span，否则将会产生多余的无效span！
                    SpanUtil.removeSpans(clazz, mRichEditText.getText(), start, start + count);

                    ///[FIX#当两行span不同时（如h1和h6），选择第二行行首后回退删除'\n'，此时View仍然在第二行，应该更新为第一行！]
                    final Editable editable = mRichEditText.getText();
                    assert editable != null;
                    if (count == 1 && isParagraphStyle(clazz) && mClassMap.get(clazz) != null
                            && editable.charAt(start) == '\n') { ///如果含换行
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
            if (editable == null) {
                return;
            }
            final int selectionStart = start;
            final int selectionEnd = start + count;

            ///[更新ListSpan]
            ArrayList<ListSpan> updateListSpans = new ArrayList<>();

            for (Class<? extends Parcelable> clazz : mClassMap.keySet()) {
                if (isParagraphStyle(clazz)) {
                    final int currentParagraphStart = SpanUtil.getParagraphStart(editable, selectionStart);
                    ///[FIX#在行首换行时，上面产生的空行应该不被选中！]
                    if (count > 0 && mClassMap.get(clazz) != null
                            && start == currentParagraphStart && editable.charAt(start) != '\n'
                            && editable.subSequence(selectionStart, selectionEnd).toString().contains("\n")) {  ///并且含换行
                        mClassMap.get(clazz).setSelected(false);
                    }

                    ///注意：因为可能'\n'被删除了，所以删除时也要adjust！
                    if (isNestParagraphStyle(clazz)) {
                        adjustNestParagraphStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false, updateListSpans);
                    } else {
                        adjustParagraphStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false);
                    }
                } else if (isCharacterStyle(clazz)) {
                    if (isBlockCharacterStyle(clazz)) {
                        adjustBlockCharacterStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false);
                    } else {
                        adjustCharacterStyleSpans(mClassMap.get(clazz), clazz, editable, selectionStart, selectionEnd, false);
                    }
                }
            }

            ///[更新ListSpan]
            updateListSpans(editable, updateListSpans);
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
    private <T> void adjustNestParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply, ArrayList<ListSpan> updateListSpans) {
        ///[FIX#选中行尾字符时应该把随后的'\n'也选中进来！]
        if (isApply && start < end && end < editable.length()) {
            end++;
        }

        final int firstParagraphStart = SpanUtil.getParagraphStart(editable, start);
        final int lastParagraphEnd = start == end ? SpanUtil.getParagraphEnd(editable, end) : SpanUtil.getParagraphEnd(editable, end - 1);

        ///注意：当选中文尾的空行时会出现firstParagraphStart == lastParagraphEnd的情况，此时只切换view的selected状态、不调整spans！
        if (firstParagraphStart == lastParagraphEnd) {
            return;
        }

        innerAdjustNestParagraphStyleSpans(view, clazz, editable, start, end, firstParagraphStart, lastParagraphEnd, isApply, updateListSpans);
    }

    private <T> void innerAdjustNestParagraphStyleSpans(
            View view, Class<T> clazz, Editable editable,
            int start, int end, int firstParagraphStart, int lastParagraphEnd, boolean isApply, ArrayList<ListSpan> updateListSpans) {
        if (isApply) {
            if (start == end) {
                final T parentSpan = getParentSpan(view, clazz, editable, start, end, null, false, 0);
                if (parentSpan == null) {
                    if (view != null && view.isSelected()) {
                        createNewSpan(view, clazz, editable, firstParagraphStart, lastParagraphEnd, null, null);
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
                            if (!updateListSpans.contains((ListSpan) parentSpan)) {
                                updateListSpans.add((ListSpan) parentSpan);
                            }
                        }

                    } else {
                        ///更新区间内所有NestSpan的nesting level，偏移量为
                        if (clazz != ListItemSpan.class) {
                            final int parentStart = editable.getSpanStart(parentSpan);
                            final int parentEnd = editable.getSpanEnd(parentSpan);
                            updateDescendantNestingLevel(clazz, editable, parentStart, parentEnd, -1);

                            ///段落span（带初始化参数）：List
                            if (clazz == ListSpan.class) {
                                removeChildrenListItemSpans(editable, (ListSpan) parentSpan, parentStart, parentEnd);
                            }
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
                            if (clazz != ListItemSpan.class) {
                                ((INestParagraphStyle) span).setNestingLevel(((INestParagraphStyle) span).getNestingLevel() + 1);
                            }
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
                                    if (!updateListSpans.contains((ListSpan) span)) {
                                        updateListSpans.add((ListSpan) span);
                                    }
                                }

                            } else {
                                if (firstParagraphStart < spanStart) {    ///交叉上山
                                    newSpanEnd = spanEnd;
                                } else {    ///交叉下山
                                    newSpanStart = spanStart;
                                }

                                ///更新span的NestingLevel（增加一级）
                                if (clazz != ListItemSpan.class) {
                                    ((INestParagraphStyle) span).setNestingLevel(((INestParagraphStyle) span).getNestingLevel() + 1);
                                }
                            }
                        } else {    ///span包含选中区间 spanStart <= firstParagraphStart && end <= lastParagraphEnd
                            ///注意：这里使用otherSpan传递NestingLevel！
                            parentSpan = span;

                            break;
                        }
                    }

                    if (newSpanStart < newSpanEnd) {

                        ///段落span（带初始化参数）：List
                        if (clazz == ListSpan.class) {
                            ///[FIX#在已有ListSpan上多选时，需要按照ListItemSpan等NestSpan分割来创建new span]
                            int next;
                            for (int i = newSpanStart; i < newSpanEnd; i = next) {
                                next = editable.nextSpanTransition(i, newSpanEnd, INestParagraphStyle.class);
                                createNewSpan(view, clazz, editable, i, next, null, parentSpan);
                            }
                        } else {
                            createNewSpan(view, clazz, editable, newSpanStart, newSpanEnd, null, parentSpan);
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
            boolean isSplitListItemSpan = true;

            boolean hasDoneGetParentSpan = false;
            T parentSpan = null;

            final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, firstParagraphStart, lastParagraphEnd, true);
            for (T span : spans) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);

                ///段落span（带初始化参数）：List
                if (isSplitListItemSpan && clazz == ListItemSpan.class && (spanStart < start || end < spanEnd)
                        && start + 1 == end && editable.charAt(start) == '\n') {    ///输入一个换行时，只切割最上面一层ListItemSpan
                    final ListSpan listSpan = ((ListItemSpan) span).getListSpan();

                    editable.setSpan(span, spanStart, end, editable.getSpanFlags(span));

                    if (end < spanEnd) {
                        final ListItemSpan newListItemSpan = new ListItemSpan(listSpan, ((ListItemSpan) span).getIndex() + 1,
                                mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
                        editable.setSpan(newListItemSpan, end, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }

                    ///更新ListSpan包含的儿子一级ListItemSpans（注意：只children！）
                    if (!updateListSpans.contains(listSpan)) {
                        updateListSpans.add(listSpan);
                    }
                }

                isSplitListItemSpan = false;

                ///更新粘贴文本中的span的nesting level
                if (clazz != ListItemSpan.class && start <= spanStart && spanEnd <= end) {
                    ///当粘贴文本包含多级NestSpan时，保存首次获取的ParentSpan
                    if (!hasDoneGetParentSpan) {
                        hasDoneGetParentSpan = true;
                        parentSpan = getParentSpan(view, clazz, editable, start, end, null, false, 0);
                    }
                    ///如果parentSpan不为null，则更新span把nesting level增加parentSpan.getNestingLevel()
                    if (parentSpan != null) {
                        ((INestParagraphStyle) span).setNestingLevel(((INestParagraphStyle) span).getNestingLevel() + ((INestParagraphStyle) parentSpan).getNestingLevel());
                    }
                }

                ///调整span的起止位置
                ///删除（即start == end）、插入或替换文本（即start < end）时，如含有'\n'的文本时会造成一行中存在多个不完整的段落span，需要调整
                int st = spanStart, en = spanEnd;
                if (isInvalidParagraph(editable, spanEnd)) {
                    en = start == end ? SpanUtil.getParagraphEnd(editable, spanEnd) : SpanUtil.getParagraphEnd(editable, spanEnd - 1);
                }
                if (isInvalidParagraph(editable, spanStart)) {
                    ///[FIX#替换含有'\n'的文本（即start < end）时，删除选择区间尾部切断的span（其spanEnd > end），否则造成重复！]
                    if (start < end && end < spanEnd) {
                        st = en;
                    } else {
                        ///删除文本时（即start == end），如果span的起始位置不正确，则左缩（即设置spanStart为其所在行的行尾）
                        st = start == end ? SpanUtil.getParagraphEnd(editable, spanStart) : SpanUtil.getParagraphStart(editable, spanStart);
                    }
                }
                if (st == en) {

                    ///段落span（带初始化参数）：List
                    if (clazz == ListSpan.class && span instanceof ListSpan) {
                        removeChildrenListItemSpans(editable, (ListSpan) span, spanStart, spanEnd);
                    }

                    editable.removeSpan(span);
                } else if (st != spanStart || en != spanEnd) {
                    editable.setSpan(span, st, en, editable.getSpanFlags(span));
                }

                ///段落span（带初始化参数）：List
                if (clazz == ListSpan.class && span != null) {
                    ///更新ListSpan包含的儿子一级ListItemSpans（注意：只children！）
                    if (!updateListSpans.contains((ListSpan) span)) {
                        updateListSpans.add((ListSpan) span);
                    }
                }

            }
        }
    }

    private <T> void adjustParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply) {
        ///注意：必须增加end，要包括下一行到选择区间来，比如选中区间尾部在下一行的开始位置
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
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, false);
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

            if (clazz == LineDividerSpan.class) {
                ///[FIX#当LineDivider起止位置不正确时，应删除！]
                if (spanStart + 1 != spanEnd || editable.charAt(spanStart) != '\n') {
                    editable.removeSpan(span);
                }
            } else if ((spanStart != start || spanEnd != end) && start < end) {
                ///调整span的起止位置
                editable.setSpan(span, start, end, editable.getSpanFlags(span));
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
        if (view == null) {
            return;
        }

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

        ///[BlockCharacterStyle#newEnd]选中区间内如果存在多个span，则取其中最大的spanEnd为选中区间的结尾
        int newEnd = -1;

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, false);
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
                        ///忽略TextWatcher的UndoRedo
                        isSkipUndoRedo = true;
                        editable.replace(spanStart, spanEnd, viewTagText);
                        newEnd = max(newEnd, spanStart + viewTagText.length());    ///[BlockCharacterStyle#newEnd]
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
                            editable.setSpan(span, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
                        ///忽略TextWatcher的UndoRedo
                        isSkipUndoRedo = true;
                        editable.replace(spanStart, spanEnd, viewTagText);
                        newEnd = max(newEnd, spanStart + viewTagText.length());    ///[BlockCharacterStyle#newEnd]
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
                            RichEditorToolbarHelper.loadImage(mContext, clazz, editable, -1, start, end,
                                    viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight, mPlaceholderDrawable, mPlaceholderResourceId, this);
                        }
                    }
                }
            } else if (isApply) {
                editable.removeSpan(span);
            } else if (spanStart < end && end < spanEnd) { ///[FIX#因为SpanFlags全部为Spanned.SPAN_INCLUSIVE_EXCLUSIVE，而BlockCharacterStyle左侧不应添加文本的！因此要左缩]
                editable.setSpan(span, end, spanEnd, editable.getSpanFlags(span));
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

        ///[BlockCharacterStyle#newEnd]
        if (newEnd != -1) {
            ///忽略TextWatcher的UndoRedo
            isSkipUndoRedo = true;
            Selection.setSelection(editable, start, newEnd);
            isSkipUndoRedo = false;
        }

        if (view.isSelected() && isNewSpanNeeded) {

            ///字符span（带参数）：URL
            if (view == mImageViewURL) {
                final String viewTagText = (String) view.getTag(R.id.url_text);
                final String viewTagUrl = (String) view.getTag(R.id.url_url);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    ///忽略TextWatcher的UndoRedo
                    isSkipUndoRedo = true;
                    editable.replace(start, end, viewTagText);
                    Selection.setSelection(editable, start, start + viewTagText.length());
                    isSkipUndoRedo = false;
                } else {
                    if (!TextUtils.isEmpty(viewTagUrl) && start < end) {
                        editable.setSpan(new CustomURLSpan(viewTagUrl), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
                    ///忽略TextWatcher的UndoRedo
                    isSkipUndoRedo = true;
                    editable.replace(start, end, viewTagText);
                    Selection.setSelection(editable, start, start + viewTagText.length());
                    isSkipUndoRedo = false;
                } else {
                    if (!TextUtils.isEmpty(viewTagSrc) && start < end) {
                        ///[ImageSpan#Glide#GifDrawable]
                        RichEditorToolbarHelper.loadImage(mContext, clazz, editable, -1, start, end,
                                viewTagUri, viewTagSrc, viewTagAlign, viewTagWidth, viewTagHeight, mPlaceholderDrawable, mPlaceholderResourceId, this);
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
        if (view == null) {
            return;
        }

        ///[FIX#选中行尾字符时应该把随后的'\n'也选中进来！]
        if (start < end && end < editable.length() && editable.charAt(end) == '\n') {
            end++;
        }

        boolean hasSpan = false;    ///为true时，后续的span将被删除
        boolean isSameWithViewParameter = true;

        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, false);
        for (T span : spans) {
            ///isSelected为true时（外延），当区间[start, end]中有多个span，首次处理span后，其余的span都应删除
            ///注意：区间[start, end]结尾处可能会有部分在区间[start, end]中的span，因为首次处理中包含了join，所以已经被删除了
            if (hasSpan && view.isSelected()) {
                editable.removeSpan(span);

                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

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
                    final int st = min(start, spanStart);
                    final int en = max(end, spanEnd);
                    if (st != spanStart || en != spanEnd) {
                        editable.setSpan(span, st, en, editable.getSpanFlags(span));

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
                        editable.setSpan(span, st, en, editable.getSpanFlags(span));
                        if (spanStart < start && end < spanEnd) { ///右缩+new
                            ///按照'\n'来分割span
//                            createNewSpan(view, clazz, editable, end, spanEnd, span, null); ///new(end, spanEnd)
                            splitCharacterStyleSpan(view, clazz, editable, end, spanEnd, null);
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

                ///按照'\n'来分割span
                final int spanSt = editable.getSpanStart(span);
                final int spanEn = editable.getSpanEnd(span);
                splitCharacterStyleSpan(view, clazz, editable, spanSt, spanEn, span);
            }
        }

        ///extendOrNew+join
        if (view.isSelected() && (!hasSpan || !isSameWithViewParameter)) {
            int spanSt = start;
            int spanEn = end;
            T span = null;

            ///如果左右有span且参数相等，则延长，否则创建新Span
            final T leftSpan = getLeftSpan(view, clazz, editable, start, end, null);
            if (leftSpan != null) {
                final int leftSpanStart = editable.getSpanStart(leftSpan);
                editable.setSpan(leftSpan, leftSpanStart, end, editable.getSpanFlags(span));
                final int rightSpanEnd = findAndJoinRightSpan(view, clazz, editable, leftSpan);

                spanSt = leftSpanStart;
                spanEn = rightSpanEnd;
                span = leftSpan;
            } else {
                final T rightSpan = getRightSpan(view, clazz, editable, start, end, null);
                if (rightSpan != null) {
                    final int rightSpanEnd = editable.getSpanEnd(rightSpan);
                    editable.setSpan(rightSpan, start, rightSpanEnd, editable.getSpanFlags(span));

                    spanSt = start;
                    spanEn = rightSpanEnd;
                    span = rightSpan;
                }
            }

            ///按照'\n'来分割span
//            createNewSpan(view, clazz, editable, start, end, null, null);
            splitCharacterStyleSpan(view, clazz, editable, spanSt, spanEn, span);
        }
    }

    ///注意：当右缩+new时需要compareSpan
    private <T> Object createNewSpan(View view, Class<T> clazz, Editable editable, int start, int end, T compareSpan, T parentSpan) {
        ///添加新span
        Object newSpan = null;

        ///段落span（带初始化参数）：LeadingMargin
        if (clazz == CustomLeadingMarginSpan.class) {
            int nestingLevel = 1;
            if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                nestingLevel = ((INestParagraphStyle) parentSpan).getNestingLevel() + 1;
            }
            newSpan = new CustomLeadingMarginSpan(nestingLevel, mLeadingMarginSpanIndent);
        }

        ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan
        else if (clazz == AlignNormalSpan.class || clazz == AlignCenterSpan.class || clazz == AlignOppositeSpan.class) {
            int nestingLevel = 1;
            if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                nestingLevel = ((INestParagraphStyle) parentSpan).getNestingLevel() + 1;
            }
            if (clazz == AlignNormalSpan.class) {
                newSpan = new AlignNormalSpan(nestingLevel);
            } else if (clazz == AlignCenterSpan.class) {
                newSpan = new AlignCenterSpan(nestingLevel);
            } else {
                newSpan = new AlignOppositeSpan(nestingLevel);
            }
        }

        ///段落span（带初始化参数）：List
        else if (clazz == ListSpan.class) {
            if (compareSpan != null) {
                final int listStart = ((ListSpan) compareSpan).getStart();
                final boolean isReversed = ((ListSpan) compareSpan).isReversed();
                final int nestingLevel = ((ListSpan) compareSpan).getNestingLevel();
                final int listType = ((ListSpan) compareSpan).getListType();
                newSpan = new ListSpan(nestingLevel, listType, listStart, isReversed, mIndicatorMargin);
            } else if (view != null && view.getTag(R.id.list_start) != null
                    && view.getTag(R.id.list_is_reversed) != null
                    && view.getTag(R.id.list_list_type) != null) {
                final int listStart = (int) view.getTag(R.id.list_start);
                final boolean isReversed = (boolean) view.getTag(R.id.list_is_reversed);
                int nestingLevel = 1;
                if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                    nestingLevel = ((INestParagraphStyle) parentSpan).getNestingLevel() + 1;
                }
                final int listType = (int) view.getTag(R.id.list_list_type);
                newSpan = new ListSpan(nestingLevel, listType, listStart, isReversed, mIndicatorMargin);
            }
        }

        ///段落span（带初始化参数）：Quote
        else if (clazz == CustomQuoteSpan.class) {
            int nestingLevel = 1;
            if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                nestingLevel = ((INestParagraphStyle) parentSpan).getNestingLevel() + 1;
            }
//            newSpan = new QuoteSpan(Color.GREEN);
//            newSpan = new QuoteSpan(Color.GREEN, 20, 40); ///Call requires API level 28 (current min is 15)
            newSpan = new CustomQuoteSpan(nestingLevel, mQuoteSpanColor, mQuoteSpanStripWidth, mQuoteSpanGapWidth);
        }

        ///段落span（带初始化参数）：Pre
        else if (clazz == PreSpan.class) {
            int nestingLevel = 1;
            if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                nestingLevel = ((INestParagraphStyle) parentSpan).getNestingLevel() + 1;
            }
            newSpan = new PreSpan(nestingLevel);
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

        ///段落span：LineDivider
        else if (clazz == LineDividerSpan.class) {
            newSpan = new LineDividerSpan(mLineDividerSpanMarginTop, mLineDividerSpanMarginBottom, mDrawBackgroundCallback);
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
            editable.setSpan(newSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            ///段落span（带初始化参数）：List
            if (clazz == ListSpan.class) {
                createChildrenListItemSpans(editable, (ListSpan) newSpan, start, end,
                        mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
            }

        }

        return newSpan;
    }

    /**
     * 按照'\n'来分割span
     */
    public <T> void splitCharacterStyleSpan(View view, Class<T> clazz, Editable editable, int start, int end, T span) {
        boolean flag = false;
        int next;
        for (int i = start; i < end; i = next + 1) {
            next = TextUtils.indexOf(editable, '\n', i, end);
            if (next < 0) {
                next = end;
            }

            if (next > i) {
                if (flag || span == null) {
                    createNewSpan(view, clazz, editable, i, next, span, null);
                } else {
                    flag = true;
                    final int spanStart = editable.getSpanStart(span);
                    final int spanEnd = editable.getSpanEnd(span);

                    if (spanStart != i || spanEnd != next) {
                        editable.setSpan(span, i, next, editable.getSpanFlags(span));
                    }
                }
            }
        }
    }

}
