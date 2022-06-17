package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cc.brainbook.android.colorpicker.builder.ColorPickerClickListener;
import cc.brainbook.android.colorpicker.builder.ColorPickerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.bean.SpanBean;
import cc.brainbook.android.richeditortoolbar.bean.TextBean;
import cc.brainbook.android.richeditortoolbar.builder.ClickURLSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickLeadingMarginSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickLineDividerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickListSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LongClickQuoteSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;
import cc.brainbook.android.richeditortoolbar.helper.UndoRedoHelper;
import cc.brainbook.android.richeditortoolbar.interfaces.IBlockCharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.ICharacterStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.INestParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IParagraphStyle;
import cc.brainbook.android.richeditortoolbar.interfaces.IStyle;
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
import cc.brainbook.android.richeditortoolbar.util.StringUtil;

import static android.app.Activity.RESULT_OK;
import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;
import static cc.brainbook.android.richeditortoolbar.config.Config.CUSTOM_LEADING_MARGIN_SPAN_DEFAULT_INDENT;
import static cc.brainbook.android.richeditortoolbar.config.Config.CUSTOM_QUOTE_SPAN_STANDARD_COLOR;
import static cc.brainbook.android.richeditortoolbar.config.Config.CUSTOM_QUOTE_SPAN_STANDARD_GAP_WIDTH_PX;
import static cc.brainbook.android.richeditortoolbar.config.Config.CUSTOM_QUOTE_SPAN_STANDARD_STRIPE_WIDTH_PX;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_HEIGHT;
import static cc.brainbook.android.richeditortoolbar.config.Config.HEAD_SPAN_HEADING_LABELS;
import static cc.brainbook.android.richeditortoolbar.config.Config.LINE_DIVIDER_SPAN_DEFAULT_MARGIN_BOTTOM;
import static cc.brainbook.android.richeditortoolbar.config.Config.LINE_DIVIDER_SPAN_DEFAULT_MARGIN_TOP;
import static cc.brainbook.android.richeditortoolbar.config.Config.LIST_ITEM_SPAN_DEFAULT_INDICATOR_COLOR;
import static cc.brainbook.android.richeditortoolbar.config.Config.LIST_ITEM_SPAN_DEFAULT_INDICATOR_GAP_WIDTH;
import static cc.brainbook.android.richeditortoolbar.config.Config.LIST_ITEM_SPAN_DEFAULT_INDICATOR_WIDTH;
import static cc.brainbook.android.richeditortoolbar.config.Config.LIST_SPAN_DEFAULT_INDENT;
import static cc.brainbook.android.richeditortoolbar.config.Config.DEFAULT_MAX_IMAGE_WIDTH;
import static cc.brainbook.android.richeditortoolbar.config.Config.OBJECT_REPLACEMENT_TEXT;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.createChildrenListItemSpans;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.isListTypeOrdered;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.removeChildrenListItemSpans;
import static cc.brainbook.android.richeditortoolbar.helper.ListSpanHelper.updateListSpans;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.findAndJoinLeftSpan;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.findAndJoinRightSpan;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.getLeftSpan;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.getParentSpan;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.getRightSpan;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.getSpanFlags;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.isSameWithViewParameter;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.joinSpanByPosition;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.sAllClassList;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.sCharacterStyleSpanClassList;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.sParagraphStyleSpanClassList;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.updateCharacterStyleView;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.updateDescendantNestingLevel;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.updateParagraphView;
import static cc.brainbook.android.richeditortoolbar.util.SpanUtil.isInvalidParagraph;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.google.android.flexbox.FlexboxLayout;

////??????public class RichEditorToolbar extends FlexboxLayout implements
public class RichEditorToolbar extends LinearLayout implements
        Drawable.Callback, View.OnClickListener,
        View.OnLongClickListener,   ///注意：若开启LongClick，则android:tooltipText会不显示
        RichEditText.OnSelectionChanged,
        RichEditText.SaveSpansCallback, RichEditText.LoadSpansCallback,
        UndoRedoHelper.OnPositionChangedListener {

    public static final String KEY_TEXT = "key_text";
    public static final String KEY_RESULT = "key_result";

    public static final String SHARED_PREFERENCES_KEY_DRAFT_TEXT = "rich_editor_shared_preferences_key_draft_text";
    public static final String CLIPBOARD_FILE_NAME = "rich_editor_clipboard_file";

    ///RichEditorToolbar名字，用于保存草稿等
    ///注意：多RichEditorToolbar中必须添加且唯一！同样，mRequestCodeHtmlEditor也要唯一
    public static final String DEFAULT_TOOLBAR_NAME = "rich_editor";


    //////??????只注册View不为null的Class！
    ///注意：ListItemSpan必须在ListSpan之后注册
    private final LinkedHashMap<Class<? extends IStyle>, View> mClassMap = new LinkedHashMap<>();
    @Nullable
    public Class<? extends IStyle> getClassMapKey(View view) {
        for (Class<? extends IStyle> clazz : mClassMap.keySet()) {
            if (mClassMap.get(clazz) == view) {
                return clazz;
            }
        }

        return null;
    }

    private RichEditText mRichEditText;
    public RichEditText getRichEditText() {
        return mRichEditText;
    }
    public void setRichEditText(RichEditText richEditText) {
        mRichEditText = richEditText;
    }

    private LineDividerSpan.DrawBackgroundCallback mDrawBackgroundCallback;
    public void setDrawBackgroundCallback(LineDividerSpan.DrawBackgroundCallback drawBackgroundCallback) {
        mDrawBackgroundCallback = drawBackgroundCallback;
    }

    ///[OnReadyListener]
    public interface OnReadyListener {
        void onReady();
    }
    private OnReadyListener mOnReadyListener;
    public void setOnReadyListener(OnReadyListener onReadyListener) {
        mOnReadyListener = onReadyListener;
    }


    /* ---------------- ///段落span（带初始化参数）：LeadingMargin ---------------- */
    private ImageView mImageViewLeadingMargin;
    public ImageView getImageViewLeadingMargin() {
        return mImageViewLeadingMargin;
    }
    private int mLeadingMarginSpanIndent = CUSTOM_LEADING_MARGIN_SPAN_DEFAULT_INDENT;

    /* ---------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan ---------------- */
    private ImageView mImageViewAlignNormal;
    public ImageView getImageViewAlignNormal() {
        return mImageViewAlignNormal;
    }
    private ImageView mImageViewAlignCenter;
    public ImageView getImageViewAlignCenter() {
        return mImageViewAlignCenter;
    }
    private ImageView mImageViewAlignOpposite;
    public ImageView getImageViewAlignOpposite() {
        return mImageViewAlignOpposite;
    }

    /* ---------------- ///段落span（带初始化参数）：List ---------------- */
    private ImageView mImageViewList;
    public ImageView getImageViewList() {
        return mImageViewList;
    }
    private int mIndicatorMargin = LIST_SPAN_DEFAULT_INDENT;
    private int mIndicatorWidth = LIST_ITEM_SPAN_DEFAULT_INDICATOR_WIDTH;
    private int mIndicatorGapWidth = LIST_ITEM_SPAN_DEFAULT_INDICATOR_GAP_WIDTH;
    private @ColorInt int mIndicatorColor = LIST_ITEM_SPAN_DEFAULT_INDICATOR_COLOR;

    /* ---------------- ///段落span（带初始化参数）：Quote ---------------- */
    private ImageView mImageViewQuote;
    public ImageView getImageViewQuote() {
        return mImageViewQuote;
    }
    private @ColorInt int mQuoteSpanColor = CUSTOM_QUOTE_SPAN_STANDARD_COLOR;
    private int mQuoteSpanStripWidth = CUSTOM_QUOTE_SPAN_STANDARD_STRIPE_WIDTH_PX;
    private int mQuoteSpanGapWidth = CUSTOM_QUOTE_SPAN_STANDARD_GAP_WIDTH_PX;

    /* -------------- ///字符span（带参数）：Pre --------------- */
    private ImageView mImageViewPre;
    public ImageView getImageViewPre() {
        return mImageViewPre;
    }

    /* ---------------- ///段落span（带参数）：Head ---------------- */
    private TextView mTextViewHead;
    public TextView getTextViewHead() {
        return mTextViewHead;
    }

    /* ---------------- ///段落span：LineDivider ---------------- */
    private ImageView mImageViewLineDivider;
    public ImageView getImageViewLineDivider() {
        return mImageViewLineDivider;
    }
    private int mLineDividerSpanMarginTop = LINE_DIVIDER_SPAN_DEFAULT_MARGIN_TOP;
    private int mLineDividerSpanMarginBottom = LINE_DIVIDER_SPAN_DEFAULT_MARGIN_BOTTOM;

    /* ---------------- ///字符span：Bold、Italic ---------------- */
    private ImageView mImageViewBold;
    public ImageView getImageViewBold() {
        return mImageViewBold;
    }
    private ImageView mImageViewItalic;
    public ImageView getImageViewItalic() {
        return mImageViewItalic;
    }

    /* ---------------- ///字符span：Underline、StrikeThrough、Subscript、Superscript ---------------- */
    private ImageView mImageViewUnderline;
    public ImageView getImageViewUnderline() {
        return mImageViewUnderline;
    }
    private ImageView mImageViewStrikethrough;
    public ImageView getImageViewStrikethrough() {
        return mImageViewStrikethrough;
    }
    private ImageView mImageViewSubscript;
    public ImageView getImageViewSubscript() {
        return mImageViewSubscript;
    }
    private ImageView mImageViewSuperscript;
    public ImageView getImageViewSuperscript() {
        return mImageViewSuperscript;
    }

    /* ---------------- ///字符span（带参数）：ForegroundColor、BackgroundColor ---------------- */
    private ImageView mImageViewForegroundColor;
    public ImageView getImageViewForegroundColor() {
        return mImageViewForegroundColor;
    }
    private ImageView mImageViewBackgroundColor;
    public ImageView getImageViewBackgroundColor() {
        return mImageViewBackgroundColor;
    }

    /* ---------------- ///字符span（带参数）：FontFamily ---------------- */
    private TextView mTextViewFontFamily;
    public TextView getTextViewFontFamily() {
        return mTextViewFontFamily;
    }

    /* ---------------- ///字符span（带参数）：AbsoluteSize ---------------- */
    private TextView mTextViewAbsoluteSize;
    public TextView getImageViewAbsoluteSize() {
        return mTextViewAbsoluteSize;
    }

    /* ---------------- ///字符span（带参数）：RelativeSize ---------------- */
    private TextView mTextViewRelativeSize;
    public TextView getTextViewRelativeSize() {
        return mTextViewRelativeSize;
    }

    /* ---------------- ///字符span（带参数）：ScaleX ---------------- */
    private TextView mTextViewScaleX;
    public TextView getTextViewScaleX() {
        return mTextViewScaleX;
    }

    /* ---------------- ///字符span：Code ---------------- */
    private ImageView mImageViewCode;
    public ImageView getImageViewCode() {
        return mImageViewCode;
    }

    /* ---------------- ///字符span：Block ---------------- */
    private ImageView mImageViewBlock;
    public ImageView getImageViewBlock() {
        return mImageViewBlock;
    }

    /* ---------------- ///字符span：Border ---------------- */
    private ImageView mImageViewBorder;
    public ImageView getImageViewBorder() {
        return mImageViewBorder;
    }

    /* ---------------- ///字符span（带参数）：URL ---------------- */
    private ImageView mImageViewUrl;
    public ImageView getImageViewUrl() {
        return mImageViewUrl;
    }

    /* ---------------- ///字符span（带参数）：Image ---------------- */
    private int mImageMaxWidth = DEFAULT_MAX_IMAGE_WIDTH;
    public int getImageMaxWidth() {
        return mImageMaxWidth;
    }
    public void setImageMaxWidth(int imageMaxWidth) {
        mImageMaxWidth = imageMaxWidth;
    }
    private int mImageMaxHeight = DEFAULT_MAX_IMAGE_HEIGHT;
    public int getImageMaxHeight() {
        return mImageMaxHeight;
    }
    public void setImageMaxHeight(int imageMaxHeight) {
        mImageMaxHeight = imageMaxHeight;
    }

    private ImageView mImageViewVideo;
    public ImageView getImageViewVideo() {
        return mImageViewVideo;
    }
    private ImageView mImageViewAudio;
    public ImageView getImageViewAudio() {
        return mImageViewAudio;
    }
    private ImageView mImageViewImage;
    public ImageView getImageViewImage() {
        return mImageViewImage;
    }
    private ClickImageSpanDialogFragment mClickImageSpanDialogFragment;
    public ClickImageSpanDialogFragment getClickImageSpanDialogFragment() {
        return mClickImageSpanDialogFragment;
    }

    private ClickImageSpanDialogFragment.ImageSpanCallback mImageSpanCallback;
    public void setImageSpanCallback(ClickImageSpanDialogFragment.ImageSpanCallback imageSpanCallback) {
        mImageSpanCallback = imageSpanCallback;
    }

    ///[ClickImageSpanDialogFragment#isStartCamera]打开ClickImageSpanDialog时自动开启Camera
    public boolean isStartCamera;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mRequestCodeHtmlEditor == requestCode) {
            if (RESULT_OK == resultCode) {
                final Editable editable = mRichEditText.getText();
                if (editable != null) {
                    if (data != null) {
                        final String htmlText = data.getStringExtra(KEY_RESULT);
                        if (htmlText != null) {
                            editable.replace(0, editable.length(), Html.fromHtml(htmlText));

                            ///[postSetText#执行postLoadSpans及后处理，否则ImageSpan/VideoSpan/AudioSpan不会显示！]
                            postSetText();
                        }
                    }
                }
            }
        }
    }

    ///[ImageSpan#Glide#GifDrawable]
    ///注意：TextView在实际使用中可能不由EditText产生并赋值，所以需要单独另行处理Glide#GifDrawable的Callback
    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        final Editable editable = mRichEditText.getText();
        ToolbarHelper.setImageSpan(editable, drawable);
    }

    ///[ImageSpan#Glide#loadImage()#Placeholder]
    ///注意：mPlaceholderDrawable和mPlaceholderResourceId如都设置则mPlaceholderDrawable优先
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
    private ImageView mImageViewClearStyles;

    /* ---------------- ///[草稿Draft] ---------------- */
    private ImageView mImageViewSaveDraft;
    private ImageView mImageViewRestoreDraft;
    private ImageView mImageViewClearDraft;

    private boolean checkDraft() {
        final String draftText = PrefsUtil.getString(mContext, mToolbarName, SHARED_PREFERENCES_KEY_DRAFT_TEXT, null);
        final boolean hasDraft = !TextUtils.isEmpty(draftText);
        mImageViewRestoreDraft.setEnabled(hasDraft);
        mImageViewRestoreDraft.setSelected(hasDraft);
        mImageViewClearDraft.setEnabled(hasDraft);
        return hasDraft;
    }

    /* ---------------- ///[Undo/Redo] ---------------- */
    private ImageView mImageViewUndo;
    private ImageView mImageViewRedo;
    private ImageView mImageViewSave;

    public interface SaveCallback {
        void save(String jsonString);
    }
    private SaveCallback mSaveCallback;
    public void setSaveCallback(SaveCallback saveCallback) {
        mSaveCallback = saveCallback;
    }

    private UndoRedoHelper mUndoRedoHelper;

    public void initUndoRedo() {
        mUndoRedoHelper.clearHistory();

        final Editable editable = mRichEditText.getText();
        if (editable == null) {
            return;
        }

        mUndoRedoHelper.addHistory(UndoRedoHelper.INIT_ACTION, 0, null, null,
                ToolbarHelper.toByteArray(editable, 0, editable.length(), false));
    }

    public void setHistorySize(int historySize) {
        mUndoRedoHelper.setHistorySize(historySize);
    }

    @Override
    public void onPositionChangedListener(int position, UndoRedoHelper.Action action, boolean isSetSpans, boolean isCanUndo, boolean isCanRedo, boolean isSavedPosition) {
        if (mImageViewUndo != null) {
            mImageViewUndo.setSelected(isCanUndo);
            mImageViewUndo.setEnabled(isCanUndo);
        }
        if (mImageViewRedo != null) {
            mImageViewRedo.setSelected(isCanRedo);
            mImageViewRedo.setEnabled(isCanRedo);
        }
        if (mImageViewSave != null) {
            mImageViewSave.setSelected(!isSavedPosition);
            mImageViewSave.setEnabled(!isSavedPosition);
        }

        if (isSetSpans && action != null) {
            ///注意：清除原有的span，比如BoldSpan的父类StyleSpan
            ///注意：必须保证selectionChanged()不被执行！否则死循环！
//            mRichEditText.getIndicatorText().clearStyles(); ///[FIX#误删除了其它有用的spans！]
            SpanUtil.clearAllSpans(mRichEditText.getText());

            ///执行postLoadSpans及后处理
            ToolbarHelper.postLoadSpans(mContext, mRichEditText.getText(),
                    ToolbarHelper.fromByteArray(mRichEditText.getText(), action.getBytes()),
                    null, -1,

                    ///FIX#宽度不能超过屏幕宽度！
                    ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
                    ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
                    Math.min(getImageMaxWidth(), mRichEditText.getWidth() - mRichEditText.getTotalPaddingLeft() - mRichEditText.getTotalPaddingRight()),

                    getImageMaxHeight(),
                    mPlaceholderDrawable, mPlaceholderResourceId,
                    this, null);
        }
    }

    /* ---------------- ///[Preview] ---------------- */
    private ImageView mImageViewPreview;
    private boolean enablePreview;

    public interface PreviewCallback {
        void handlePreview(String result);
    }
    private PreviewCallback mPreviewCallback;
    public void setPreviewCallback(PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }

    /* ---------------- ///[Html] ---------------- */
    private ImageView mImageViewHtml;
    private boolean enableHtml;

    private int mHtmlOption = Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;
    public void setHtmlOption(int htmlOption) {
        mHtmlOption = htmlOption;
    }
    public int getHtmlOption() {
        return mHtmlOption;
    }

    public interface HtmlCallback {
        void handleHtml(String htmlString);
    }
    private HtmlCallback mHtmlCallback;
    public void setHtmlCallback(HtmlCallback htmlCallback) {
        mHtmlCallback = htmlCallback;
    }

    /* ---------------- ///[TextContextMenu#Clipboard] ---------------- */
    ///[clipboard]存放剪切板的文件目录
    ///由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
    private File mClipboardFile;

    @Override
    public void saveSpans(Editable editable, int selectionStart, int selectionEnd) {
        try {
            final byte[] bytes = ToolbarHelper.toByteArray(editable, selectionStart, selectionEnd, true);
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
        try {
            final byte[] bytes = FileUtil.readFile(mClipboardFile);

            ///执行postLoadSpans及后处理
            ToolbarHelper.postLoadSpans(mContext, mRichEditText.getText(),
                    ToolbarHelper.fromByteArray(pasteEditable, bytes),
                    pasteEditable, pasteOffset,

                    ///FIX#宽度不能超过屏幕宽度！
                    ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
                    ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
                    Math.min(getImageMaxWidth(), mRichEditText.getWidth() - mRichEditText.getTotalPaddingLeft() - mRichEditText.getTotalPaddingRight()),

                    getImageMaxHeight(),
                    mPlaceholderDrawable, mPlaceholderResourceId,
                    this, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* ------------------------------------------------ */
    ///尽量直接使用mContext，避免用view.getContext()！否则可能获取不到Activity而导致异常
    private Context mContext;

    private String mToolbarName;
    public String getToolbarName() {
        return mToolbarName;
    }

    private int mRequestCodeHtmlEditor;
    public int getRequestCodeHtmlEditor() {
        return mRequestCodeHtmlEditor;
    }

    private @LayoutRes int mToolbarLayout;
    private boolean enableLongClick;


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

    public void init(@NonNull Context context, @NonNull TypedArray a) {
        mContext = context;

        ///[clipboard]设置存放剪切板的文件目录
        ///由于无法把spans一起Cut/Copy到剪切板，所以需要另外存储spans
        ///注意：建议使用应用的cache目录
        mClipboardFile = new File(mContext.getCacheDir() + File.separator + CLIPBOARD_FILE_NAME);

        mUndoRedoHelper = new UndoRedoHelper(mContext, this);

        ///RichEditorToolbar名字，用于保存草稿等
        ///注意：多RichEditorToolbar中必须添加且唯一！同样，mRequestCodeHtmlEditor也要唯一
        mToolbarName = a.getString(R.styleable.RichEditorToolbar_toolbarName);
        if (TextUtils.isEmpty(mToolbarName)) {
            mToolbarName = DEFAULT_TOOLBAR_NAME;
        }
        mRequestCodeHtmlEditor = mToolbarName.hashCode() & 0xffff;    ///Can only use lower 16 bits for requestCode


        mToolbarLayout = a.getResourceId(R.styleable.RichEditorToolbar_toolbarLayout, R.layout.toolbar);
        LayoutInflater.from(mContext).inflate(mToolbarLayout, this, true);

        enableLongClick = a.getBoolean(R.styleable.RichEditorToolbar_enableLongClick, false);


        /* -------------- ///段落span（带初始化参数）：LeadingMargin --------------- */
        mImageViewLeadingMargin = (ImageView) findViewById(R.id.toolbar_leading_margin);
        if (mImageViewLeadingMargin != null) {
            mImageViewLeadingMargin.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableLeadingMargin, true));
            mImageViewLeadingMargin.setOnClickListener(this);
            mImageViewLeadingMargin.setOnLongClickListener(enableLongClick ? this : null);
            mClassMap.put(CustomLeadingMarginSpan.class, mImageViewLeadingMargin);
        }

        /* -------------- ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan --------------- */
        mImageViewAlignNormal = (ImageView) findViewById(R.id.toolbar_align_normal);
        if (mImageViewAlignNormal != null) {
            mImageViewAlignNormal.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableAlignNormal, true));
            mImageViewAlignNormal.setOnClickListener(this);
            mClassMap.put(AlignNormalSpan.class, mImageViewAlignNormal);
        }

        mImageViewAlignCenter = (ImageView) findViewById(R.id.toolbar_align_center);
        if (mImageViewAlignCenter != null) {
            mImageViewAlignCenter.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableAlignCenter, true));
            mImageViewAlignCenter.setOnClickListener(this);
            mClassMap.put(AlignCenterSpan.class, mImageViewAlignCenter);
        }

        mImageViewAlignOpposite = (ImageView) findViewById(R.id.toolbar_align_opposite);
        if (mImageViewAlignOpposite != null) {
            mImageViewAlignOpposite.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableAlignOpposite, true));
            mImageViewAlignOpposite.setOnClickListener(this);
            mClassMap.put(AlignOppositeSpan.class, mImageViewAlignOpposite);
        }

        /* -------------- ///段落span（带初始化参数）：List --------------- */
        mImageViewList = (ImageView) findViewById(R.id.toolbar_list);
        if (mImageViewList != null) {
            mImageViewList.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableList, true));
            mImageViewList.setOnClickListener(this);
            mImageViewList.setOnLongClickListener(enableLongClick ? this : null);
            mClassMap.put(ListSpan.class, mImageViewList);
        }

        /* -------------- ///段落span（带初始化参数）：Quote --------------- */
        mImageViewQuote = (ImageView) findViewById(R.id.toolbar_quote);
        if (mImageViewQuote != null) {
            mImageViewQuote.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableQuote, true));
            mImageViewQuote.setOnClickListener(this);
            mImageViewQuote.setOnLongClickListener(enableLongClick ? this : null);
            mClassMap.put(CustomQuoteSpan.class, mImageViewQuote);
        }

        /* -------------- ///字符span（带参数）：Pre --------------- */
        mImageViewPre = (ImageView) findViewById(R.id.toolbar_pre);
        if (mImageViewPre != null) {
            mImageViewPre.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enablePre, true));
            mImageViewPre.setOnClickListener(this);
            mClassMap.put(PreSpan.class, mImageViewPre);
        }

        /* -------------- ///段落span（带参数）：Head --------------- */
        mTextViewHead = (TextView) findViewById(R.id.toolbar_head);
        if (mTextViewHead != null) {
            mTextViewHead.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableHead, true));
            mTextViewHead.setOnClickListener(this);
            mClassMap.put(HeadSpan.class, mTextViewHead);
        }

        /* -------------- ///段落span：LineDivider --------------- */
        mImageViewLineDivider = (ImageView) findViewById(R.id.toolbar_line_divider);
        if (mImageViewLineDivider != null) {
            mImageViewLineDivider.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableLineDivider, true));
            mImageViewLineDivider.setOnClickListener(this);
            mImageViewLineDivider.setOnLongClickListener(enableLongClick ? this : null);
            mClassMap.put(LineDividerSpan.class, mImageViewLineDivider);
        }

        /* -------------- ///字符span：Bold、Italic --------------- */
        mImageViewBold = (ImageView) findViewById(R.id.toolbar_bold);
        if (mImageViewBold != null) {
            mImageViewBold.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableBold, true));
            mImageViewBold.setOnClickListener(this);
            mClassMap.put(BoldSpan.class, mImageViewBold);
        }

        mImageViewItalic = (ImageView) findViewById(R.id.toolbar_italic);
        if (mImageViewItalic != null) {
            mImageViewItalic.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableItalic, true));
            mImageViewItalic.setOnClickListener(this);
            mClassMap.put(ItalicSpan.class, mImageViewItalic);
        }

        /* ------------ ///字符span：Underline、StrikeThrough、Subscript、Superscript ------------ */
        mImageViewUnderline = (ImageView) findViewById(R.id.toolbar_underline);
        if (mImageViewUnderline != null) {
            mImageViewUnderline.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableUnderline, true));
            mImageViewUnderline.setOnClickListener(this);
            mClassMap.put(CustomUnderlineSpan.class, mImageViewUnderline);
        }

        mImageViewStrikethrough = (ImageView) findViewById(R.id.toolbar_strikethrough);
        if (mImageViewStrikethrough != null) {
            mImageViewStrikethrough.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableStrikethrough, true));
            mImageViewStrikethrough.setOnClickListener(this);
            mClassMap.put(CustomStrikethroughSpan.class, mImageViewStrikethrough);
        }

        mImageViewSuperscript = (ImageView) findViewById(R.id.toolbar_superscript);
        if (mImageViewSuperscript != null) {
            mImageViewSuperscript.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableSuperscript, true));
            mImageViewSuperscript.setOnClickListener(this);
            mClassMap.put(CustomSuperscriptSpan.class, mImageViewSuperscript);
        }

        mImageViewSubscript = (ImageView) findViewById(R.id.toolbar_subscript);
        if (mImageViewSubscript != null) {
            mImageViewSubscript.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableSubscript, true));
            mImageViewSubscript.setOnClickListener(this);
            mClassMap.put(CustomSubscriptSpan.class, mImageViewSubscript);
        }

        /* -------------- ///字符span（带参数）：ForegroundColor、BackgroundColor --------------- */
        mImageViewForegroundColor = (ImageView) findViewById(R.id.toolbar_foreground_color);
        if (mImageViewForegroundColor != null) {
            mImageViewForegroundColor.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableForegroundColor, true));
            mImageViewForegroundColor.setOnClickListener(this);
            mClassMap.put(CustomForegroundColorSpan.class, mImageViewForegroundColor);
        }

        mImageViewBackgroundColor = (ImageView) findViewById(R.id.toolbar_background_color);
        if (mImageViewBackgroundColor != null) {
            mImageViewBackgroundColor.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableBackgroundColor, true));
            mImageViewBackgroundColor.setOnClickListener(this);
            mClassMap.put(CustomBackgroundColorSpan.class, mImageViewBackgroundColor);
        }

        /* -------------- ///字符span（带参数）：FontFamily --------------- */
        mTextViewFontFamily = (TextView) findViewById(R.id.toolbar_font_family);
        if (mTextViewFontFamily != null) {
            mTextViewFontFamily.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableFontFamily, true));
            mTextViewFontFamily.setOnClickListener(this);
            mClassMap.put(CustomFontFamilySpan.class, mTextViewFontFamily);
        }

        /* -------------- ///字符span（带参数）：AbsoluteSize --------------- */
        mTextViewAbsoluteSize = (TextView) findViewById(R.id.toolbar_absolute_size);
        if (mTextViewAbsoluteSize != null) {
            mTextViewAbsoluteSize.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableAbsoluteSize, true));
            mTextViewAbsoluteSize.setOnClickListener(this);
            mClassMap.put(CustomAbsoluteSizeSpan.class, mTextViewAbsoluteSize);
        }

        /* -------------- ///字符span（带参数）：RelativeSize --------------- */
        mTextViewRelativeSize = (TextView) findViewById(R.id.toolbar_relative_size);
        if (mTextViewRelativeSize != null) {
            mTextViewRelativeSize.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableRelativeSize, true));
            mTextViewRelativeSize.setOnClickListener(this);
            mClassMap.put(CustomRelativeSizeSpan.class, mTextViewRelativeSize);
        }

        /* -------------- ///字符span（带参数）：ScaleX --------------- */
        mTextViewScaleX = (TextView) findViewById(R.id.toolbar_scale_x);
        if (mTextViewScaleX != null) {
            mTextViewScaleX.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableScaleX, true));
            mTextViewScaleX.setOnClickListener(this);
            mClassMap.put(CustomScaleXSpan.class, mTextViewScaleX);
        }

        /* -------------- ///字符span：Code --------------- */
        mImageViewCode = (ImageView) findViewById(R.id.toolbar_code);
        if (mImageViewCode != null) {
            mImageViewCode.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableCode, true));
            mImageViewCode.setOnClickListener(this);
            mClassMap.put(CodeSpan.class, mImageViewCode);
        }

        /* -------------- ///字符span：Block --------------- */
        mImageViewBlock = (ImageView) findViewById(R.id.toolbar_block);
        if (mImageViewBlock != null) {
            mImageViewBlock.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableBlock, true));
            mImageViewBlock.setOnClickListener(this);
            mClassMap.put(BlockSpan.class, mImageViewBlock);
        }

        /* -------------- ///字符span：Border --------------- */
        mImageViewBorder = (ImageView) findViewById(R.id.toolbar_border);
        if (mImageViewBorder != null) {
            mImageViewBorder.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableBorder, true));
            mImageViewBorder.setOnClickListener(this);
            mClassMap.put(BorderSpan.class, mImageViewBorder);
        }

        /* -------------- ///字符span（带参数）：URL --------------- */
        mImageViewUrl = (ImageView) findViewById(R.id.toolbar_url);
        if (mImageViewUrl != null) {
            mImageViewUrl.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableUrl, true));
            mImageViewUrl.setOnClickListener(this);
            mClassMap.put(CustomURLSpan.class, mImageViewUrl);
        }

        /* -------------- ///字符span（带参数）：Image --------------- */
        mImageViewVideo = (ImageView) findViewById(R.id.toolbar_video);
        if (mImageViewVideo != null) {
            mImageViewVideo.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableVideo, true));
            mImageViewVideo.setOnClickListener(this);
            mClassMap.put(VideoSpan.class, mImageViewVideo);
        }

        mImageViewAudio = (ImageView) findViewById(R.id.toolbar_audio);
        if (mImageViewAudio != null) {
            mImageViewAudio.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableAudio, true));
            mImageViewAudio.setOnClickListener(this);
            mClassMap.put(AudioSpan.class, mImageViewAudio);
        }

        mImageViewImage = (ImageView) findViewById(R.id.toolbar_image);
        if (mImageViewImage != null) {
            mImageViewImage.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableImage, true));
            mImageViewImage.setOnClickListener(this);
            mClassMap.put(CustomImageSpan.class, mImageViewImage);
        }


        /* -------------- ///[清除样式] --------------- */
        mImageViewClearStyles = (ImageView) findViewById(R.id.toolbar_clear_styles);
        if (mImageViewClearStyles != null) {
            mImageViewClearStyles.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableClearStyles, true));
            mImageViewClearStyles.setOnClickListener(this);
        }

        /* -------------- ///[草稿Draft] --------------- */
        mImageViewSaveDraft = (ImageView) findViewById(R.id.toolbar_save_draft);
        if (mImageViewSaveDraft != null) {
            mImageViewSaveDraft.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableSaveDraft, true));
            mImageViewSaveDraft.setOnClickListener(this);
        }

        mImageViewRestoreDraft = (ImageView) findViewById(R.id.toolbar_restore_draft);
        if (mImageViewRestoreDraft != null) {
            mImageViewRestoreDraft.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableRestoreDraft, true));
            mImageViewRestoreDraft.setOnClickListener(this);
        }

        mImageViewClearDraft = (ImageView) findViewById(R.id.toolbar_clear_draft);
        if (mImageViewClearDraft != null) {
            mImageViewClearDraft.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableClearDraft, true));
            mImageViewClearDraft.setOnClickListener(this);
        }

        ///初始化时检查有无草稿Draft
        if (mImageViewSaveDraft.isEnabled() && mImageViewRestoreDraft.isEnabled() && mImageViewClearDraft.isEnabled() && checkDraft()) {
            Toast.makeText(mContext, mContext.getString(R.string.rich_editor_toolbar_msg_has_draft), Toast.LENGTH_SHORT).show();
        }

        /* ------------------- ///[Undo/Redo/Save] ------------------- */
        mImageViewUndo = (ImageView) findViewById(R.id.toolbar_undo);
        if (mImageViewUndo != null) {
            mImageViewUndo.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableUndo, true));
            mImageViewUndo.setOnClickListener(this);
        }

        mImageViewRedo = (ImageView) findViewById(R.id.toolbar_redo);
        if (mImageViewRedo != null) {
            mImageViewRedo.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableRedo, true));
            mImageViewRedo.setOnClickListener(this);
        }

        mImageViewSave = (ImageView) findViewById(R.id.toolbar_save);
        if (mImageViewSave != null) {
            mImageViewSave.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableSave, true));
            mImageViewSave.setOnClickListener(this);
        }

        /* -------------- ///[Preview] --------------- */
        mImageViewPreview = (ImageView) findViewById(R.id.iv_preview);
        if (mImageViewPreview != null) {
            mImageViewPreview.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enablePreview, true));
            mImageViewPreview.setOnClickListener(this);
        }

        /* -------------- ///[Html] --------------- */
        mImageViewHtml = (ImageView) findViewById(R.id.toolbar_html);
        if (mImageViewHtml != null) {
            mImageViewHtml.setEnabled(a.getBoolean(R.styleable.RichEditorToolbar_enableHtml, true));
            mImageViewHtml.setOnClickListener(this);
            mImageViewHtml.setOnLongClickListener(enableLongClick ? this : null);
        }
    }

    public void init() {
        mRichEditText.addTextChangedListener(new RichTextWatcher());
        mRichEditText.setOnSelectionChanged(this);
        mRichEditText.setSaveSpansCallback(this);
        mRichEditText.setLoadSpansCallback(this);

        ///[Undo/Redo]初始化时设置Undo/Redo各按钮的状态
        initUndoRedo();

        ///[OnReadyListener]
        if (mOnReadyListener != null) {
            mOnReadyListener.onReady();
        }
    }

    ///[postSetText#执行postLoadSpans及后处理，否则ImageSpan/VideoSpan/AudioSpan不会显示！]
    public void postSetText() {
        final Editable editable = mRichEditText.getText();
        if (editable == null) {
            return;
        }

        final TextBean textBean = ToolbarHelper.saveSpans(editable, 0, editable.length(), false);
        final List<SpanBean> spanBeans = textBean.getSpans();
        ToolbarHelper.postLoadSpans(mContext, editable,
                ToolbarHelper.fromSpanBeans(spanBeans, editable),
                null, -1,

                ///FIX#宽度不能超过屏幕宽度！
                ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
                ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
                Math.min(getImageMaxWidth(), mRichEditText.getWidth() - mRichEditText.getTotalPaddingLeft() - mRichEditText.getTotalPaddingRight()),

                getImageMaxHeight(),
                mPlaceholderDrawable, mPlaceholderResourceId,
                this, null);
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
        } else if (view == mImageViewStrikethrough) {
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
        } else if (view == mImageViewUrl) {
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


    /* ----------------- ///[onClick] ------------------ */
    @Override
    public void onClick(final View view) {
        ///[Html]
        if (view == mImageViewClearStyles) {
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

            for (Class<? extends IStyle> clazz : sParagraphStyleSpanClassList) {
                if (mClassMap.get(clazz) != null) {
                    mClassMap.get(clazz).setSelected(false);
                }

                if (INestParagraphStyle.class.isAssignableFrom(clazz)) {
                    adjustNestParagraphStyleSpans(mClassMap.get(clazz), (Class<INestParagraphStyle>) clazz, editable, selectionStart, selectionEnd, true, updateListSpans);
                } else {
                    adjustParagraphStyleSpans(mClassMap.get(clazz), (Class<IParagraphStyle>) clazz, editable, selectionStart, selectionEnd, true);
                }

                if (mClassMap.get(clazz) != null) {
                    updateParagraphView(mContext, mClassMap.get(clazz), (Class<IParagraphStyle>) clazz, editable, selectionStart, selectionEnd);
                }
            }
            for (Class<? extends IStyle> clazz : sCharacterStyleSpanClassList) {
                if (mClassMap.get(clazz) != null) {
                    mClassMap.get(clazz).setSelected(false);
                }

                if (IBlockCharacterStyle.class.isAssignableFrom(clazz)) {
                    adjustBlockCharacterStyleSpans(mClassMap.get(clazz), (Class<IBlockCharacterStyle>) clazz, editable, selectionStart, selectionEnd, true);
                } else {
                    adjustCharacterStyleSpans(mClassMap.get(clazz), (Class<ICharacterStyle>) clazz, editable, selectionStart, selectionEnd, true);
                }

                if (mClassMap.get(clazz) != null) {
                    updateCharacterStyleView(mContext, mClassMap.get(clazz), (Class<ICharacterStyle>) clazz, editable, selectionStart, selectionEnd);
                }
            }

            ///[更新ListSpan]
            updateListSpans(editable, updateListSpans);

            ///[Undo/Redo]
            mUndoRedoHelper.addHistory(UndoRedoHelper.CLEAR_STYLES_ACTION, selectionStart, null, null,
                    ToolbarHelper.toByteArray(editable, 0, editable.length(), false));

            return;
        } else if (view == mImageViewSaveDraft) {
            final Editable editable = mRichEditText.getText();
            if (editable == null) {
                return;
            }

            final byte[] bytes = ToolbarHelper.toByteArray(editable, 0, editable.length(), true);
            PrefsUtil.putString(mContext, mToolbarName, SHARED_PREFERENCES_KEY_DRAFT_TEXT, Base64.encodeToString(bytes, 0));

            if (checkDraft()) {
                Toast.makeText(mContext, mContext.getString(R.string.rich_editor_toolbar_msg_save_draft_successful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.rich_editor_toolbar_msg_save_draft_failed), Toast.LENGTH_SHORT).show();
            }

            return;
        } else if (view == mImageViewRestoreDraft) {
            final String draftText = PrefsUtil.getString(mContext, mToolbarName, SHARED_PREFERENCES_KEY_DRAFT_TEXT, null);
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

                final List<SpanBean> spanBeans = textBean.getSpans();
                ///执行postLoadSpans及后处理
                ToolbarHelper.postLoadSpans(mContext, editable,
                        ToolbarHelper.fromSpanBeans(spanBeans, editable),
                        null, -1,

                        ///FIX#宽度不能超过屏幕宽度！
                        ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
                        ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
                        Math.min(getImageMaxWidth(), mRichEditText.getWidth() - mRichEditText.getTotalPaddingLeft() - mRichEditText.getTotalPaddingRight()),

                        getImageMaxHeight(),
                        mPlaceholderDrawable, mPlaceholderResourceId,
                        this, null);

                ///[Undo/Redo]
                assert editable != null;
                mUndoRedoHelper.addHistory(UndoRedoHelper.RESTORE_DRAFT_ACTION, 0, beforeChange, editable.toString(),
                        ToolbarHelper.toByteArray(editable, 0, editable.length(), false));

                Toast.makeText(mContext, mContext.getString(R.string.rich_editor_toolbar_msg_restore_draft_successful), Toast.LENGTH_SHORT).show();
            }

            return;
        } else if (view == mImageViewClearDraft) {
            PrefsUtil.clear(mContext, mToolbarName);

            if (!checkDraft()) {
                Toast.makeText(mContext, mContext.getString(R.string.rich_editor_toolbar_msg_clear_draft_successful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.rich_editor_toolbar_msg_clear_draft_failed), Toast.LENGTH_SHORT).show();
            }

            return;
        } else if (view == mImageViewUndo) {
            mUndoRedoHelper.undo();

            return;
        } else if (view == mImageViewRedo) {
            mUndoRedoHelper.redo();

            return;
        } else if (view == mImageViewSave) {
            if (mSaveCallback != null) {
                mSaveCallback.save(ToolbarHelper.toJson(mRichEditText.getText(),
                        0, mRichEditText.getText().length(),
                        true));
            }

            mUndoRedoHelper.resetSavedPosition();
            mImageViewSave.setSelected(false);
            mImageViewSave.setEnabled(false);

            return;
        } else if (view == mImageViewPreview) {
            if (mPreviewCallback != null) {
                mPreviewCallback.handlePreview(ToolbarHelper.toJson(mRichEditText.getText(),
                        0, mRichEditText.getText().length(),
                        true));
            }

            return;
        } else if (view == mImageViewHtml) {
            final String htmlString = Html.toHtml(mRichEditText.getText(), mHtmlOption);

            if (mHtmlCallback != null) {
                mHtmlCallback.handleHtml(htmlString);
            }

            return;
        }


        /* --------- ///[onClick]点击更新ImageView，并且当selectionStart != selectionEnd时改变selection的span --------- */
        final Editable editable = mRichEditText.getText();
        if (editable == null) {
            return;
        }

        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);
        if (selectionStart == -1 || selectionEnd == -1) {
            return;
        }

        final Class<? extends IStyle> clazz = getClassMapKey(view);
        if (IParagraphStyle.class.isAssignableFrom(clazz)) {

            ///段落span（带初始化参数）：List
            if (view == mImageViewList) {
                final int listType = view.getTag(R.id.view_tag_list_list_type) == null ? Integer.MIN_VALUE :  (int) view.getTag(R.id.view_tag_list_list_type);
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = view.getTag(R.id.view_tag_list_list_type) == null ? -1 :
                        ArrayUtil.getIntIndex(mContext, R.array.list_type_ids, (int) view.getTag(R.id.view_tag_list_list_type));
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
                                final SwitchCompat switchIsReversed = ((AlertDialog) dialog).findViewById(R.id.switch_is_reversed);
                                assert editTextStart != null;
                                editTextStart.setEnabled(isListTypeOrdered(listType));
                                assert switchIsReversed != null;
                                switchIsReversed.setEnabled(isListTypeOrdered(listType));

                                ((AlertDialog) dialog).getListView().findViewById(R.id.ib_decrease).setEnabled(isListTypeOrdered(listType));
                                ((AlertDialog) dialog).getListView().findViewById(R.id.ib_increase).setEnabled(isListTypeOrdered(listType));
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///获取listTypeIndex并由此得到对应的listType
                                final int listTypeIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                final int listType = ArrayUtil.getIntItem(mContext, R.array.list_type_ids, listTypeIndex);

                                final EditText editTextStart = ((AlertDialog) dialog).findViewById(R.id.et_start);
                                assert editTextStart != null;
                                final int start = Integer.parseInt(editTextStart.getText().toString());
                                final SwitchCompat switchIsReversed = ((AlertDialog) dialog).findViewById(R.id.switch_is_reversed);
                                assert switchIsReversed != null;
                                final boolean isReversed = switchIsReversed.isChecked();

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(R.id.view_tag_list_start, start);
                                view.setTag(R.id.view_tag_list_is_reversed, isReversed);
                                view.setTag(R.id.view_tag_list_list_type, listType);

                                ///改变selection的span
                                applyParagraphStyleSpans(view, editable);
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                                view.setTag(R.id.view_tag_list_start, null);
                                view.setTag(R.id.view_tag_list_is_reversed, null);
                                view.setTag(R.id.view_tag_list_list_type, null);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();

                listSpanAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();
                            }
                        });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                ///[FIX#自定义单选或多选AlertDialog中含有其它控件时，ListView太长导致无法滚动显示完整]
                ///不能用setView()！改为ListView.addFooterView()
                final View listSpanDialogView = LayoutInflater.from(mContext).inflate(R.layout.click_list_span_dialog, null);
                final ListView listView = listSpanAlertDialog.getListView();
                listView.addFooterView(listSpanDialogView);

                ///[FIX#Android api 16#listView.addFooterView不显示]解决：addFootView()放在setAdapter()之前
                ///https://blog.csdn.net/tengzhinei1/article/details/84211698
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    listView.setAdapter(listView.getAdapter());
                }

                listView.findViewById(R.id.ib_decrease).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editTextStart = listSpanAlertDialog.findViewById(R.id.et_start);
                        assert editTextStart != null;
                        final int start = Integer.parseInt(editTextStart.getText().toString());
                        if (start > 1) {
                            editTextStart.setText(String.valueOf(start - 1));
                        }
                    }
                });
                listView.findViewById(R.id.ib_increase).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editTextStart = listSpanAlertDialog.findViewById(R.id.et_start);
                        assert editTextStart != null;
                        final int start = Integer.parseInt(editTextStart.getText().toString());
                        editTextStart.setText(String.valueOf(start + 1));
                    }
                });

                ///初始化AlertDialog
                final boolean isEnabled = view.getTag(R.id.view_tag_list_list_type) != null && isListTypeOrdered(listType);
                final EditText editTextStart = (EditText) listSpanAlertDialog.findViewById(R.id.et_start);
                assert editTextStart != null;
                editTextStart.setEnabled(isEnabled);
                final int start = view.getTag(R.id.view_tag_list_start) == null ? 1 :  (int) view.getTag(R.id.view_tag_list_start);
                editTextStart.setText(String.valueOf(start));

                ///[FIX#AlertDialog中的EditText无法弹出软键盘]
                ///https://itimetraveler.github.io/2017/01/20/%E3%80%90Android%E3%80%91AlertDialog%E4%B8%AD%E7%9A%84EditText%E4%B8%8D%E8%83%BD%E5%BC%B9%E5%87%BA%E8%BD%AF%E9%94%AE%E7%9B%98%E7%9A%84%E9%97%AE%E9%A2%98/
                ///https://developer.android.com/reference/android/app/Dialog.html
//                editTextStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View view, boolean focused) {
//                        if (focused) {
                            //dialog弹出软键盘
                            listSpanAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//                        }
//                    }
//                });

                final SwitchCompat switchIsReversed = listSpanAlertDialog.findViewById(R.id.switch_is_reversed);
                assert switchIsReversed != null;
                switchIsReversed.setEnabled(isEnabled);
                final boolean isReversed = view.getTag(R.id.view_tag_list_is_reversed) != null && (boolean) view.getTag(R.id.view_tag_list_is_reversed);
                switchIsReversed.setChecked(isReversed);

                listView.findViewById(R.id.ib_decrease).setEnabled(isEnabled);
                listView.findViewById(R.id.ib_increase).setEnabled(isEnabled);

                return;
            }

            ///段落span（带初始化参数）：Quote
            else if (view == mImageViewQuote && selectionStart < selectionEnd && view.isSelected()) {
                new AlertDialog.Builder(mContext)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///改变selection的span
                                applyParagraphStyleSpans(view, editable);
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                    mRichEditText.enableSelectionChange();
                                }
                        });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            ///段落span（带参数）：Head
            else if (view == mTextViewHead) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = view.getTag() == null ? -1 : (Integer) view.getTag();

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.head_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (view.getTag() == null || which != (Integer) view.getTag()) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(which);

                                    ///改变selection的span
                                    applyParagraphStyleSpans(view, editable);

                                    ((TextView) view).setText(HEAD_SPAN_HEADING_LABELS[which]);
                                }

                                dialog.dismiss();
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                                ((TextView) view).setText(mContext.getString(R.string.layout_toolbar_text_head));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                    mRichEditText.enableSelectionChange();
                                }
                        });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

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

        } else if (ICharacterStyle.class.isAssignableFrom(clazz)) {

            ///字符span（带参数）：ForegroundColor、BackgroundColor
            if (view == mImageViewForegroundColor || view == mImageViewBackgroundColor) {
                ///颜色选择器
                final ColorPickerDialogBuilder colorPickerDialogBuilder = ColorPickerDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                                }
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }
                                ///清除View的背景颜色
                                view.setBackgroundColor(Color.TRANSPARENT);
                                ///改变selection的span
                                applyCharacterStyleSpans(view, editable);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                ///初始化颜色为View的背景颜色
                if (view.isSelected()) {
                    final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                    colorPickerDialogBuilder.initialColor(colorDrawable.getColor());
                }

                AlertDialog alertDialog = colorPickerDialogBuilder.build();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                        mRichEditText.enableSelectionChange();
                    }
                });
                alertDialog.show();

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            ///字符span（带参数）：FontFamily
            else if (view == mTextViewFontFamily) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.font_family_items, (String.valueOf(view.getTag())));

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.font_family_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///由用户选择项which获取对应的选择参数
                                final String family = ArrayUtil.getStringItem(mContext, R.array.font_family_items, which);

                                if (!TextUtils.equals(family, String.valueOf(view.getTag()))) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(family);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ((TextView) view).setText(family);
                                }

                                dialog.dismiss();
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                                ((TextView) view).setText(mContext.getString(R.string.layout_toolbar_text_font_family));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                    mRichEditText.enableSelectionChange();
                                }
                        });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            ///字符span（带参数）：AbsoluteSize
            else if (view == mTextViewAbsoluteSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.absolute_size_items, String.valueOf(view.getTag()));

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.absolute_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///由用户选择项which获取对应的选择参数
                                final String size = ArrayUtil.getStringItem(mContext, R.array.absolute_size_items, which);

                                if (!TextUtils.equals(size, String.valueOf(view.getTag()))) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(size);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ((TextView) view).setText(size);
                                }

                                dialog.dismiss();
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                                ((TextView) view).setText(mContext.getString(R.string.layout_toolbar_text_absolute_size));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                    mRichEditText.enableSelectionChange();
                                }
                        });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            ///字符span（带参数）：RelativeSize
            else if (view == mTextViewRelativeSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.relative_size_items, String.valueOf(view.getTag()));

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.relative_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///由用户选择项which获取对应的选择参数
                                final String sizeChange = ArrayUtil.getStringItem(mContext, R.array.relative_size_items, which);

                                if (!TextUtils.equals(sizeChange, String.valueOf(view.getTag()))) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(sizeChange);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ((TextView) view).setText(sizeChange);
                                }

                                dialog.dismiss();
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                                ((TextView) view).setText(mContext.getString(R.string.layout_toolbar_text_relative_size));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                    mRichEditText.enableSelectionChange();
                                }
                        });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            ///字符span（带参数）：ScaleX
            else if (view == mTextViewScaleX) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = ArrayUtil.getStringIndex(mContext, R.array.scale_x_items, String.valueOf(view.getTag()));

                new AlertDialog.Builder(mContext)
                        .setSingleChoiceItems(R.array.scale_x_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///由用户选择项which获取对应的选择参数
                                final String scaleX = ArrayUtil.getStringItem(mContext, R.array.scale_x_items, which);

                                if (!TextUtils.equals(scaleX, String.valueOf(view.getTag()))) {
                                    ///如果view未选中则选中view
                                    ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                    if (!view.isSelected()) {
                                        view.setSelected(true);
                                    }

                                    ///保存参数到view tag
                                    view.setTag(scaleX);

                                    ///改变selection的span
                                    applyCharacterStyleSpans(view, editable);

                                    ((TextView) view).setText(scaleX);
                                }

                                dialog.dismiss();
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

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
                                ((TextView) view).setText(mContext.getString(R.string.layout_toolbar_text_scale_x));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                    mRichEditText.enableSelectionChange();
                                }
                        });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            ///字符span（带参数）：URL
            else if (view == mImageViewUrl) {
                final ClickURLSpanDialogBuilder clickUrlSpanDialogBuilder = (ClickURLSpanDialogBuilder) ClickURLSpanDialogBuilder
                        .with(mContext)
                        .setPositiveButton(android.R.string.ok, new ClickURLSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String text, String url) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///参数校验：两项都为空则代表维持不变、不做任何处理
                                ///注意：某项为空、或值相同即代表该项维持不变，不为空且值不同则代表该项改变
                                if (text.length() == 0 && url.length() == 0) {
                                    return;
                                }
                                final ArrayList<CustomURLSpan> selectedSpans = SpanUtil.getSelectedSpans(CustomURLSpan.class, mRichEditText.getText());
                                if ((text.length() == 0 || url.length() == 0) && selectedSpans.size() == 0) {
                                    return;
                                }

                                if (!StringUtil.isUrl(url)) {
                                    url = "http://" + url;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(R.id.view_tag_url_text, text);
                                view.setTag(R.id.view_tag_url_url, url);

                                ///改变selection的span
                                applyCharacterStyleSpans(view, editable);
                            }
                        })
                        ///清除样式
                        .setNeutralButton(R.string.rich_editor_toolbar_text_clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///如果view选中则未选中view
                                ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                }

                                ///改变selection的span
                                applyCharacterStyleSpans(view, editable);

                                ///清空view tag
                                view.setTag(R.id.view_tag_url_text, null);
                                view.setTag(R.id.view_tag_url_url, null);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);

                final String text = (String) view.getTag(R.id.view_tag_url_text);
                final String url = (String) view.getTag(R.id.view_tag_url_url);

                final AlertDialog alertDialog = clickUrlSpanDialogBuilder.initial(text, url).build();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                        mRichEditText.enableSelectionChange();
                    }
                });
                alertDialog.show();

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            ///字符span（带参数）：Image
            else if (view == mImageViewImage || view == mImageViewVideo || view == mImageViewAudio) {
                final int mediaType = view == mImageViewVideo ? 1 : view == mImageViewAudio ? 2 : 0;
                final String uri = (String) view.getTag(R.id.view_tag_image_uri);
                final String src = (String) view.getTag(R.id.view_tag_image_src);
                final int width = view.getTag(R.id.view_tag_image_width) == null ? 0 : (int) view.getTag(R.id.view_tag_image_width);
                final int height = view.getTag(R.id.view_tag_image_height) == null ? 0 : (int) view.getTag(R.id.view_tag_image_height);
                final int align = view.getTag(R.id.view_tag_image_align) == null ? ClickImageSpanDialogFragment.DEFAULT_ALIGN : (int) view.getTag(R.id.view_tag_image_align);

                final ClickImageSpanDialogFragment clickImageSpanDialogFragment = ClickImageSpanDialogFragment.newInstance(mediaType,
                        uri, src, width, height, align);
                clickImageSpanDialogFragment.setRichEditorToolbar(this);
                clickImageSpanDialogFragment.setImageSpanCallback(mImageSpanCallback);

                ///OK
                clickImageSpanDialogFragment.setOnFinishListener(new ClickImageSpanDialogFragment.OnFinishListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String uri, String src, int width, int height, int align) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();

                                ///避免内存泄漏
                                clickImageSpanDialogFragment.clear();

                                ///参数校验：两项都为空则代表维持不变、不做任何处理n
                                ///注意：某项为空、或值相同即代表该项维持不变，不为空且值不同则代表该项改变
                                if (view != mImageViewImage && uri.length() == 0 || view == mImageViewImage && src.length() == 0) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

//                                ///把width\height\align保存到text中
//                                final String w = width == 0 ? "" : String.valueOf(width);
//                                final String h = height == 0 ? "" : String.valueOf(height);
//                                final String text = view == mImageViewImage ?
//                                        String.format(getContext().getResources().getString(R.string.rich_editor_toolbar_image_span_text), src,
//                                                w, h, align)
//                                        : String.format(getContext().getResources().getString(R.string.rich_editor_toolbar_image_span_media_text), uri, src,
//                                        w, h, align);
                                final String text = OBJECT_REPLACEMENT_TEXT;

                                ///保存参数到view tag
                                view.setTag(R.id.view_tag_image_text, text);
                                view.setTag(R.id.view_tag_image_uri, uri);
                                view.setTag(R.id.view_tag_image_src, src);
                                view.setTag(R.id.view_tag_image_width, width);
                                view.setTag(R.id.view_tag_image_height, height);
                                view.setTag(R.id.view_tag_image_align, align);

                                ///改变selection的span
                                applyCharacterStyleSpans(view, editable);
                            }
                        });

                ///清除样式
                clickImageSpanDialogFragment.setOnClearListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                        mRichEditText.enableSelectionChange();

                        ///避免内存泄漏
                        clickImageSpanDialogFragment.clear();

                        ///如果view选中则未选中view
                        ///注意：如果view未选中了则不再进行view未选中操作！提高效率
                        if (view.isSelected()) {
                            view.setSelected(false);
                        }

                        ///改变selection的span
                        applyCharacterStyleSpans(view, editable);

                        ///清空view tag
                        view.setTag(R.id.view_tag_image_text, null);
                        view.setTag(R.id.view_tag_image_uri, null);
                        view.setTag(R.id.view_tag_image_src, null);
                        view.setTag(R.id.view_tag_image_width, null);
                        view.setTag(R.id.view_tag_image_height, null);
                        view.setTag(R.id.view_tag_image_align, null);
                    }
                });

                ///Cancel
                clickImageSpanDialogFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ///避免内存泄漏
                        clickImageSpanDialogFragment.clear();
                    }
                });

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                clickImageSpanDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                        mRichEditText.enableSelectionChange();
                    }
                });
                clickImageSpanDialogFragment.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        if (mOnClickImageSpanDialogFragmentReadyListener != null) {
                            mOnClickImageSpanDialogFragmentReadyListener.onReady();
                        }
                    }
                });

                clickImageSpanDialogFragment.show(
                        ((FragmentActivity) mContext).getSupportFragmentManager(), ///注意：如果在Fragment中，使用getChildFragmentManager()
                        "ImageSpanDialog");

                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                mRichEditText.disableSelectionChange(true);

                return;
            }

            view.setSelected(!view.isSelected());

            applyCharacterStyleSpans(view, editable);
        }
    }

    ///[ClickImageSpanDialogFragment#OnClickImageSpanDialogFragmentReadyListener]
    public interface OnClickImageSpanDialogFragmentReadyListener {
        void onReady();
    }
    private OnClickImageSpanDialogFragmentReadyListener mOnClickImageSpanDialogFragmentReadyListener;
    public void setOnClickImageSpanDialogReadyListener(OnClickImageSpanDialogFragmentReadyListener onReadyListener) {
        mOnClickImageSpanDialogFragmentReadyListener = onReadyListener;
    }


    ///注意：若开启LongClick，则android:tooltipText会不显示
    @Override
    public boolean onLongClick(final View view) {
        ///[Html]
        if (view == mImageViewHtml) {
            new AlertDialog.Builder(mContext)
                    .setSingleChoiceItems(R.array.html_option, mHtmlOption, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mHtmlOption = which;

                            dialog.dismiss();
                        }
                    })
                    .show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                                mRichEditText.enableSelectionChange();
                            }
                    });

            ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
            mRichEditText.disableSelectionChange(true);

            return true;
        }

        ///段落span（带初始化参数）：LeadingMargin
        else if (view == mImageViewLeadingMargin) {
            AlertDialog alertDialog = ((LongClickLeadingMarginSpanDialogBuilder) LongClickLeadingMarginSpanDialogBuilder
                    .with(mContext)
                    .setPositiveButton(android.R.string.ok, new LongClickLeadingMarginSpanDialogBuilder.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indent) {
                            mLeadingMarginSpanIndent = indent;
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null))
                    .initial(mLeadingMarginSpanIndent)
                    .build();
            alertDialog.show();
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                    mRichEditText.enableSelectionChange();
                }
            });

            ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
            mRichEditText.disableSelectionChange(true);

            return true;
        }

        ///段落span（带初始化参数）：List
        else if (view == mImageViewList) {
            AlertDialog alertDialog = ((LongClickListSpanDialogBuilder) LongClickListSpanDialogBuilder
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
                    .build();
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                    mRichEditText.enableSelectionChange();
                }
            });
            alertDialog.show();

            ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
            mRichEditText.disableSelectionChange(true);

            return true;
        }

        ///段落span（带初始化参数）：Quote
        else if (view == mImageViewQuote) {
            AlertDialog alertDialog = ((LongClickQuoteSpanDialogBuilder) LongClickQuoteSpanDialogBuilder
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
                    .build();
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                    mRichEditText.enableSelectionChange();
                }
            });
            alertDialog.show();

            ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
            mRichEditText.disableSelectionChange(true);

            return true;
        }

        ///段落span（带初始化参数）：LineDivider
        else if (view == mImageViewLineDivider) {
            AlertDialog alertDialog = ((LongClickLineDividerDialogBuilder) LongClickLineDividerDialogBuilder
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
                    .build();
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
                    mRichEditText.enableSelectionChange();
                }
            });
            alertDialog.show();

            ///[FIX#平板SAMSUNG SM-T377A弹出对话框后自动设置Selection为选中所选区间的末尾！应该保持原来所选区间]
            mRichEditText.disableSelectionChange(true);

            return true;
        }

        return false;
    }

    private void applyParagraphStyleSpans(@NonNull View view, Editable editable) {
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

        final Class<? extends IStyle> clazz = getClassMapKey(view);
        if (clazz == null || !IParagraphStyle.class.isAssignableFrom(clazz)) {
            return;
        }

        if (INestParagraphStyle.class.isAssignableFrom(clazz)) {
            adjustNestParagraphStyleSpans(view, (Class<INestParagraphStyle>) clazz, editable, selectionStart, selectionEnd, true, updateListSpans);
        } else {
            adjustParagraphStyleSpans(view, (Class<IParagraphStyle>) clazz, editable, selectionStart, selectionEnd, true);
        }

        ///[更新ListSpan]
        updateListSpans(editable, updateListSpans);

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            mUndoRedoHelper.addHistory(getActionId(view), selectionEnd, beforeChange, afterChange,
                    ToolbarHelper.toByteArray(editable, 0, editable.length(), false));
        }
    }

    private void applyCharacterStyleSpans(@NonNull View view, Editable editable) {
        String beforeChange = null;
        int selectionStart = -1;
        int selectionEnd = -1;
        final Class<? extends IStyle> clazz = getClassMapKey(view);
        if (clazz == null || !ICharacterStyle.class.isAssignableFrom(clazz)) {
            return;
        }

        ///[BlockCharacterStyle#beforeChange]调整BlockCharacterStyle的Selection为第一个span的起始位置和最后span的结尾位置
        if (IBlockCharacterStyle.class.isAssignableFrom(clazz) && view.isSelected()) {
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

        if (IBlockCharacterStyle.class.isAssignableFrom(clazz)) {
            beforeChange = editable.subSequence(selectionStart, selectionEnd).toString();
            adjustBlockCharacterStyleSpans(view, (Class<IBlockCharacterStyle>) clazz, editable, selectionStart, selectionEnd, true);
        } else {
            adjustCharacterStyleSpans(view, (Class<ICharacterStyle>) clazz, editable, selectionStart, selectionEnd, true);
        }

        ///[Undo/Redo]
        if (getActionId(view) >= 0) {
            if (IBlockCharacterStyle.class.isAssignableFrom(clazz) && view.isSelected()) {
                final int afterSelectionStart = Selection.getSelectionStart(editable);
                final int afterSelectionEnd = Selection.getSelectionEnd(editable);
                mUndoRedoHelper.addHistory(getActionId(view), selectionStart,
                        beforeChange,
                        editable.subSequence(afterSelectionStart, afterSelectionEnd).toString(),
                        ToolbarHelper.toByteArray(editable, 0, editable.length(), false));
            } else{
                mUndoRedoHelper.addHistory(getActionId(view), selectionStart, null, null,
                        ToolbarHelper.toByteArray(editable, 0, editable.length(), false));
            }
        }
    }


    /* ----------------- ///[selectionChanged]根据selection更新工具条按钮 ------------------ */
    @Override
    public void selectionChanged(int selectionStart, int selectionEnd) {
        final Editable editable = mRichEditText.getText();
        if (editable == null) {
            return;
        }

        ///[BlockCharacterStyle#调整选择区间起止位置]注意：CustomURLSpan除外！
        int st = selectionStart, ed = selectionEnd;
        final IBlockCharacterStyle[] spans = editable.getSpans(st, ed, IBlockCharacterStyle.class);
        for (IBlockCharacterStyle span : spans) {
            if (span instanceof CustomURLSpan) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanEnd <= st || ed <= spanStart) {
                continue;
            }

            st = min(spanStart, st);
            ed = max(spanEnd, ed);
        }
        if (st != selectionStart || ed != selectionEnd) {
            Selection.setSelection(editable, st, ed);
            return;
        }

        if (isSkipTextWatcher || isSkipUndoRedo) {
            return;
        }

        if (DEBUG) Log.d("TAG-RichEditorToolbar", "============= selectionChanged ============" + selectionStart + ", " + selectionEnd);

        for (Class<? extends IStyle> clazz : mClassMap.keySet()) {
            if (IParagraphStyle.class.isAssignableFrom(clazz)) {
                updateParagraphView(mContext, mClassMap.get(clazz), (Class<IParagraphStyle>) clazz, editable, selectionStart, selectionEnd);
            } else if (ICharacterStyle.class.isAssignableFrom(clazz)) {
                updateCharacterStyleView(mContext, mClassMap.get(clazz), (Class<ICharacterStyle>) clazz, editable, selectionStart, selectionEnd);
            }

            ///test
            if (DEBUG) testOutput(editable, clazz);
        }

        if (DEBUG) Log.d("TAG-RichEditorToolbar", ToolbarHelper.toJson(editable, 0, editable.length(), true));
    }

    private static <T> void testOutput(@NonNull Spanned spanned, Class<T> clazz) {
        final T[] spans = spanned.getSpans(0, spanned.length(), clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = spanned.getSpanStart(span);
            final int spanEnd = spanned.getSpanEnd(span);

            if (span instanceof INestParagraphStyle) {
                if (DEBUG) Log.d("TAG-RichEditorToolbar", span.getClass().getSimpleName() + ": " + spanStart + ", " + spanEnd
                        + "  nest = " + ((INestParagraphStyle) span).getNestingLevel());
            } else  {
                if (DEBUG) Log.d("TAG-RichEditorToolbar", span.getClass().getSimpleName() + ": " + spanStart + ", " + spanEnd);
            }
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

        ///[BlockCharacterStyle#删除选择区间内的BlockCharacterStyle，如果选择其局部，则整体删除]注意：CustomURLSpan除外！
        private ArrayList<IBlockCharacterStyle> mBlockCharacterStyleSpans;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            final Editable editable = (Editable) s;
            if (editable == null) {
                return;
            }

            ///忽略TextWatcher
            if (isSkipTextWatcher) {
                return;
            }

            ///[Undo/Redo]
            if (!isSkipUndoRedo) {
                mBeforeChange = s.subSequence(start, start + count).toString();
            }

            mBlockCharacterStyleSpans = new ArrayList<>();
            if (count > 0) {
                for (Class<? extends IStyle> clazz : sAllClassList) {
                    ///清除掉已经被删除的span，否则将会产生多余的无效span！
                    SpanUtil.removeSpans(clazz, editable, start, start + count);

                    ///[FIX#当两行span不同时（如h1和h6），选择第二行行首后回退删除'\n'，此时View仍然在第二行，应该更新为第一行！]
                    if (count == 1 && IParagraphStyle.class.isAssignableFrom(clazz) && mClassMap.get(clazz) != null
                            && editable.charAt(start) == '\n') { ///如果含换行
                        updateParagraphView(mContext, mClassMap.get(clazz), (Class<IParagraphStyle>) clazz, editable, start, start);
                    }
                }

                ///[BlockCharacterStyle#删除选择区间内的BlockCharacterStyle，如果选择其局部，则整体删除]注意：CustomURLSpan除外！
                final IBlockCharacterStyle[] spans = editable.getSpans(start, start + count, IBlockCharacterStyle.class);
                for (IBlockCharacterStyle span : spans) {
                    if (span instanceof CustomURLSpan) {
                        continue;
                    }

                    final int spanStart = editable.getSpanStart(span);
                    final int spanEnd = editable.getSpanEnd(span);
                    if (spanStart < start + count && start < spanEnd) {
                        mBlockCharacterStyleSpans.add(span);
                    }
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final Editable editable = (Editable) s;
            if (editable == null) {
                return;
            }

            ///忽略TextWatcher
            if (isSkipTextWatcher) {
                return;
            }

            ///[Undo/Redo]
            if (!isSkipUndoRedo) {
                mAfterChange = s.subSequence(start, start + count).toString();
                mStart = start;
            }

            final int selectionStart = start;
            final int selectionEnd = start + count;

            ///[更新ListSpan]
            ArrayList<ListSpan> updateListSpans = new ArrayList<>();

            for (Class<? extends IStyle> clazz : mClassMap.keySet()) {
                if (IParagraphStyle.class.isAssignableFrom(clazz)) {
                    final int currentParagraphStart = SpanUtil.getParagraphStart(s, selectionStart);
                    ///[FIX#在行首换行时，上面产生的空行应该不被选中！]
                    if (count > 0 && mClassMap.get(clazz) != null
                            && start == currentParagraphStart && editable.charAt(start) != '\n'
                            && editable.subSequence(selectionStart, selectionEnd).toString().contains("\n")) {  ///并且含换行
                        mClassMap.get(clazz).setSelected(false);
                    }

                    ///注意：因为可能'\n'被删除了，所以删除时也要adjust！
                    if (INestParagraphStyle.class.isAssignableFrom(clazz)) {
                        adjustNestParagraphStyleSpans(mClassMap.get(clazz), (Class<INestParagraphStyle>) clazz, editable, selectionStart, selectionEnd, false, updateListSpans);
                    } else {
                        adjustParagraphStyleSpans(mClassMap.get(clazz), (Class<IParagraphStyle>) clazz, editable, selectionStart, selectionEnd, false);
                    }
                } else if (ICharacterStyle.class.isAssignableFrom(clazz)) {
                    if (IBlockCharacterStyle.class.isAssignableFrom(clazz)) {
                        adjustBlockCharacterStyleSpans(mClassMap.get(clazz), (Class<IBlockCharacterStyle>) clazz, editable, selectionStart, selectionEnd, false);
                    } else {
                        adjustCharacterStyleSpans(mClassMap.get(clazz), (Class<ICharacterStyle>) clazz, editable, selectionStart, selectionEnd, false);
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

            ///[BlockCharacterStyle#删除选择区间内的BlockCharacterStyle，如果选择其局部，则整体删除]注意：CustomURLSpan除外！
            if (mBlockCharacterStyleSpans != null) {
                for (IBlockCharacterStyle span : mBlockCharacterStyleSpans) {
                    if (span == null || span instanceof CustomURLSpan) {
                        continue;
                    }

                    final int spanStart = s.getSpanStart(span);
                    final int spanEnd = s.getSpanEnd(span);

                    ///[Undo/Redo]
                    if (!isSkipUndoRedo) {
                        if (mStart + mAfterChange.length() < spanEnd) {
                            final String diff = s.subSequence(mStart + mAfterChange.length(), spanEnd).toString();
                            mBeforeChange = mBeforeChange.concat(diff);
                        }
                        if (spanStart < mStart) {
                            final String diff = s.subSequence(spanStart, mStart).toString();
                            mBeforeChange = diff.concat(mBeforeChange);
                            mStart = spanStart;
                        }
                    }

                    ///忽略TextWatcher
                    isSkipTextWatcher = true;
                    s.delete(spanStart, spanEnd);
                    Selection.setSelection(s, mStart, mStart + mAfterChange.length());
                    isSkipTextWatcher = false;
                }
            }

            ///[Undo/Redo]
            if (!isSkipUndoRedo) {
                mUndoRedoHelper.addHistory(UndoRedoHelper.CHANGE_TEXT_ACTION, mStart, mBeforeChange, mAfterChange,
                        ToolbarHelper.toByteArray(s, 0, s.length(), false));
            }
        }
    }


    /* ------------------------------------------------------------------------------------------ */
    private <T extends INestParagraphStyle> void adjustNestParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply, ArrayList<ListSpan> updateListSpans) {
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

    private <T extends INestParagraphStyle> void innerAdjustNestParagraphStyleSpans(View view, Class<T> clazz, Editable editable,
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
                            if (view.getTag(R.id.view_tag_list_start) != null
                                    && view.getTag(R.id.view_tag_list_is_reversed) != null
                                    && view.getTag(R.id.view_tag_list_list_type) != null) {
                                final int listStart = (int) view.getTag(R.id.view_tag_list_start);
                                final boolean isReversed = (boolean) view.getTag(R.id.view_tag_list_is_reversed);
                                final int listType = (int) view.getTag(R.id.view_tag_list_list_type);
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
                                span.setNestingLevel(span.getNestingLevel() + 1);
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
                                    if (view.getTag(R.id.view_tag_list_start) != null
                                            && view.getTag(R.id.view_tag_list_is_reversed) != null
                                            && view.getTag(R.id.view_tag_list_list_type) != null) {
                                        final int listStart = (int) view.getTag(R.id.view_tag_list_start);
                                        final boolean isReversed = (boolean) view.getTag(R.id.view_tag_list_is_reversed);
                                        final int listType = (int) view.getTag(R.id.view_tag_list_list_type);
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
                                    span.setNestingLevel(span.getNestingLevel() + 1);
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
            INestParagraphStyle parentSpan = null;

            final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, firstParagraphStart, lastParagraphEnd, true);
            for (T span : spans) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);

                ///段落span（带初始化参数）：List
                if (isSplitListItemSpan && clazz == ListItemSpan.class && (spanStart < start || end < spanEnd)
                        && start + 1 == end && editable.charAt(start) == '\n') {    ///输入一个换行时，只切割最上面一层ListItemSpan
                    final ListSpan listSpan = ((ListItemSpan) span).getListSpan();

                    editable.setSpan(span, spanStart, end, getSpanFlags(span));

                    if (end < spanEnd) {
                        final ListItemSpan newListItemSpan = new ListItemSpan(listSpan, ((ListItemSpan) span).getIndex() + 1,
                                mIndicatorWidth, mIndicatorGapWidth, mIndicatorColor, true);
                        editable.setSpan(newListItemSpan, end, spanEnd, getSpanFlags(newListItemSpan));
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
                        span.setNestingLevel(span.getNestingLevel() + parentSpan.getNestingLevel());
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
                    editable.setSpan(span, st, en, getSpanFlags(span));
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

    private <T extends IParagraphStyle> void adjustParagraphStyleSpans(View view, Class<T> clazz, @NonNull Editable editable, int start, int end, boolean isApply) {
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

    private <T extends IParagraphStyle> void innerAdjustParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(clazz, editable, start, end, false);
        if (spans.size() == 0) {
            if (isApply && view != null && view.isSelected()) {
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

            ///忽略已经editable.removeSpan()删除的span
            if (spanStart == -1 || spanEnd == -1) {
                continue;
            }

            if (clazz == LineDividerSpan.class) {
                ///[FIX#当LineDivider起止位置不正确时，应删除！]
                if (spanStart + 1 != spanEnd || editable.charAt(spanStart) != '\n') {
                    editable.removeSpan(span);
                }
            } else if ((spanStart != start || spanEnd != end) && start < end) {
                ///调整span的起止位置
                editable.setSpan(span, start, end, getSpanFlags(span));
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

    private <T extends IBlockCharacterStyle> void adjustBlockCharacterStyleSpans(View view, Class<T> clazz, final Editable editable, final int start, final int end, boolean isApply) {
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

            ///忽略已经editable.removeSpan()删除的span
            if (spanStart == -1 || spanEnd == -1) {
                continue;
            }

            ///如果单光标、且位于span的首尾，则忽略
            if (start == end && (spanStart == start || end == spanEnd)) {
                continue;
            }

            if (view.isSelected()) {

                ///字符span（带参数）：URL
                if (view == mImageViewUrl) {
                    final String viewTagText = (String) view.getTag(R.id.view_tag_url_text);
                    final String viewTagUrl = (String) view.getTag(R.id.view_tag_url_url);
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
                        view.setTag(R.id.view_tag_url_text, viewTagText);
                        view.setTag(R.id.view_tag_url_url, viewTagUrl);
                        isUpdateNeeded = true;
                    } else {
                        if (!TextUtils.isEmpty(viewTagUrl) && !viewTagUrl.equals(spanUrl)) {
                            editable.removeSpan(span);
                            span = (T) new CustomURLSpan(viewTagUrl);
                            editable.setSpan(span, spanStart, spanEnd, getSpanFlags(span));
                        }
                    }
                }

                ///字符span（带参数）：Image
                else if (view == mImageViewVideo || view == mImageViewAudio || view == mImageViewImage) {
                    final String viewTagText = (String) view.getTag(R.id.view_tag_image_text);
                    final String viewTagUri = (String) view.getTag(R.id.view_tag_image_uri);
                    final String viewTagSrc = (String) view.getTag(R.id.view_tag_image_src);
                    final int viewTagWidth = view.getTag(R.id.view_tag_image_width) == null ? 0 : (int) view.getTag(R.id.view_tag_image_width);
                    final int viewTagHeight = view.getTag(R.id.view_tag_image_height) == null ? 0 : (int) view.getTag(R.id.view_tag_image_height);
                    final int viewTagAlign = view.getTag(R.id.view_tag_image_align) == null ? ClickImageSpanDialogFragment.DEFAULT_ALIGN : (int) view.getTag(R.id.view_tag_image_align);
                    final String compareText = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanSrc = ((CustomImageSpan) span).getSource();
                    if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        ///忽略TextWatcher的UndoRedo
                        isSkipUndoRedo = true;
                        editable.replace(spanStart, spanEnd, viewTagText);
                        newEnd = max(newEnd, spanStart + viewTagText.length());    ///[BlockCharacterStyle#newEnd]
                        isSkipUndoRedo = false;

                        ///[isUpdateNeeded]
                        view.setTag(R.id.view_tag_image_text, viewTagText);
                        view.setTag(R.id.view_tag_image_uri, viewTagUri);
                        view.setTag(R.id.view_tag_image_src, viewTagSrc);
                        view.setTag(R.id.view_tag_image_width, viewTagWidth);
                        view.setTag(R.id.view_tag_image_height, viewTagHeight);
                        view.setTag(R.id.view_tag_image_align, viewTagAlign);
                        isUpdateNeeded = true;
                    } else {
                        if (!TextUtils.isEmpty(viewTagSrc) && !viewTagSrc.equals(spanSrc)) {
                            editable.removeSpan(span);

                            ///[ImageSpan#Glide#GifDrawable]
                            ToolbarHelper.loadImage(mContext, clazz, editable, start, end, null, -1,
                                    viewTagUri, viewTagSrc, viewTagAlign,
                                    viewTagWidth, viewTagHeight,
                                    viewTagWidth, viewTagHeight,

                                    ///FIX#宽度不能超过屏幕宽度！
                                    ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
                                    ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
                                    Math.min(getImageMaxWidth(), mRichEditText.getWidth() - mRichEditText.getTotalPaddingLeft() - mRichEditText.getTotalPaddingRight()),

                                    getImageMaxHeight(),
                                    mPlaceholderDrawable, mPlaceholderResourceId,
                                    this, null);
                        }
                    }
                }
            } else if (isApply) {
                editable.removeSpan(span);
            } else if (spanStart < end && end < spanEnd) { ///BlockCharacterStyle左侧不应添加文本的！因此要左缩
                editable.setSpan(span, end, spanEnd, getSpanFlags(span));
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
            if (view == mImageViewUrl) {
                final String viewTagText = (String) view.getTag(R.id.view_tag_url_text);
                final String viewTagUrl = (String) view.getTag(R.id.view_tag_url_url);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    ///忽略TextWatcher的UndoRedo
                    isSkipUndoRedo = true;
                    editable.replace(start, end, viewTagText);
                    Selection.setSelection(editable, start, start + viewTagText.length());
                    isSkipUndoRedo = false;
                } else {
                    if (!TextUtils.isEmpty(viewTagUrl) && start < end) {
                        final CustomURLSpan span = new CustomURLSpan(viewTagUrl);
                        editable.setSpan(span, start, end, getSpanFlags(span));
                    }
                }
            }

            ///字符span（带参数）：Image
            else if (view == mImageViewVideo || view == mImageViewAudio || view == mImageViewImage) {
                final String viewTagText = (String) view.getTag(R.id.view_tag_image_text);
                final String viewTagUri = (String) view.getTag(R.id.view_tag_image_uri);
                final String viewTagSrc = (String) view.getTag(R.id.view_tag_image_src);
                final int viewTagWidth = view.getTag(R.id.view_tag_image_width) == null ? 0 : (int) view.getTag(R.id.view_tag_image_width);
                final int viewTagHeight = view.getTag(R.id.view_tag_image_height) == null ? 0 : (int) view.getTag(R.id.view_tag_image_height);
                final int viewTagAlign = view.getTag(R.id.view_tag_image_align) == null ? ClickImageSpanDialogFragment.DEFAULT_ALIGN : (int) view.getTag(R.id.view_tag_image_align);
                final String compareText = String.valueOf(editable.toString().toCharArray(), start, end - start);
                if (isApply && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    ///忽略TextWatcher的UndoRedo
                    isSkipUndoRedo = true;
                    editable.replace(start, end, viewTagText);
                    Selection.setSelection(editable, start, start + viewTagText.length());
                    isSkipUndoRedo = false;
                } else {
                    if (start < end) {
                        ///[ImageSpan#Glide#GifDrawable]
                        ToolbarHelper.loadImage(mContext, clazz, editable, start, end, null, -1,
                                viewTagUri, viewTagSrc, viewTagAlign,
                                viewTagWidth, viewTagHeight,
                                viewTagWidth, viewTagHeight,

                                ///FIX#宽度不能超过屏幕宽度！
                                ///[ImageSpan#调整宽高#FIX#Android KITKAT 4.4 (API 19及以下)图片大于容器宽度时导致出现两个图片！]解决：如果图片大于容器宽度则应先缩小后再drawable.setBounds()
                                ///https://stackoverflow.com/questions/31421141/duplicate-images-appear-in-edittext-after-insert-one-imagespan-in-android-4-x
                                Math.min(getImageMaxWidth(), mRichEditText.getWidth() - mRichEditText.getTotalPaddingLeft() - mRichEditText.getTotalPaddingRight()),

                                getImageMaxHeight(),
                                mPlaceholderDrawable, mPlaceholderResourceId,
                                this, null);
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
            for (IStyle span : removedSpans) {
                editable.removeSpan(span);
            }
        }
    }

    private <T extends ICharacterStyle> void adjustCharacterStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isApply) {
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

            ///忽略已经editable.removeSpan()删除的span
            if (spanStart == -1 || spanEnd == -1) {
                continue;
            }

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
                        editable.setSpan(span, st, en, getSpanFlags(span));
                    }

                    findAndJoinLeftSpan(view, clazz, editable, span);
                    findAndJoinRightSpan(view, clazz, editable, span);
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
                        editable.setSpan(span, st, en, getSpanFlags(span));
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
                final int leftSpanEnd = editable.getSpanEnd(leftSpan);
                if (leftSpanEnd != end) {
                    editable.setSpan(leftSpan, leftSpanStart, end, getSpanFlags(leftSpan));
                }

                final int rightSpanEnd = findAndJoinRightSpan(view, clazz, editable, leftSpan);

                spanSt = leftSpanStart;
                spanEn = rightSpanEnd;
                span = leftSpan;
            } else {
                final T rightSpan = getRightSpan(view, clazz, editable, start, end, null);
                if (rightSpan != null) {
                    final int rightSpanStart = editable.getSpanStart(rightSpan);
                    final int rightSpanEnd = editable.getSpanEnd(rightSpan);
                    if (rightSpanStart != start) {
                        editable.setSpan(rightSpan, start, rightSpanEnd, getSpanFlags(rightSpan));
                    }

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
    private IStyle createNewSpan(View view, Class<?  extends IStyle> clazz, Editable editable, int start, int end, IStyle compareSpan, IStyle parentSpan) {
        ///添加新span
        IStyle newSpan = null;

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
            } else if (view != null && view.getTag(R.id.view_tag_list_start) != null
                    && view.getTag(R.id.view_tag_list_is_reversed) != null
                    && view.getTag(R.id.view_tag_list_list_type) != null) {
                final int listStart = (int) view.getTag(R.id.view_tag_list_start);
                final boolean isReversed = (boolean) view.getTag(R.id.view_tag_list_is_reversed);
                int nestingLevel = 1;
                if (parentSpan != null) {   ///注意：这里使用parentSpan传递NestingLevel！
                    nestingLevel = ((INestParagraphStyle) parentSpan).getNestingLevel() + 1;
                }
                final int listType = (int) view.getTag(R.id.view_tag_list_list_type);
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
                final int level = (Integer) view.getTag();
                newSpan = new HeadSpan(level);
            }
        }

        ///段落span：LineDivider
        else if (clazz == LineDividerSpan.class) {
            newSpan = new LineDividerSpan(mLineDividerSpanMarginTop, mLineDividerSpanMarginBottom);
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
                final String family = String.valueOf(view.getTag());
                newSpan = new CustomFontFamilySpan(family);
            }
        }

        ///字符span（带参数）：AbsoluteSize
        else if (clazz == CustomAbsoluteSizeSpan.class) {
            if (compareSpan != null) {
                final int size = ((CustomAbsoluteSizeSpan) compareSpan).getSize();
                newSpan = new CustomAbsoluteSizeSpan(size);
            } else if (view != null && view.getTag() != null) {
                final int size = Integer.parseInt(String.valueOf(view.getTag()));
                newSpan = new CustomAbsoluteSizeSpan(size);
            }
        }

        ///字符span（带参数）：RelativeSize
        else if (clazz == CustomRelativeSizeSpan.class) {
            if (compareSpan != null) {
                final float sizeChange = ((CustomRelativeSizeSpan) compareSpan).getSizeChange();
                newSpan = new CustomRelativeSizeSpan(sizeChange);
            } else if (view != null && view.getTag() != null) {
                final float sizeChange = Float.parseFloat(String.valueOf(view.getTag()));
                newSpan = new CustomRelativeSizeSpan(sizeChange);
            }
        }

        ///字符span（带参数）：ScaleX
        else if (clazz == CustomScaleXSpan.class) {
            if (compareSpan != null) {
                final float scaleX = ((CustomScaleXSpan) compareSpan).getScaleX();
                newSpan = new CustomScaleXSpan(scaleX);
            } else if (view != null && view.getTag() != null) {
                final float scaleX = Float.parseFloat(String.valueOf(view.getTag()));
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
            editable.setSpan(newSpan, start, end, getSpanFlags(newSpan));

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
    private <T extends ICharacterStyle> void splitCharacterStyleSpan(View view, Class<T> clazz, Editable editable, int start, int end, T span) {
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
                        editable.setSpan(span, i, next, getSpanFlags(span));
                    }
                }
            }
        }
    }

}
