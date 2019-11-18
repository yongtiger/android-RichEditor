package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;

import cc.brainbook.android.colorpicker.builder.ColorPickerClickListener;
import cc.brainbook.android.colorpicker.builder.ColorPickerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.BulletSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.ImageSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LeadingMarginSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LineDividerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.QuoteSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.URLSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.span.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.CodeSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomBulletSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;
import cc.brainbook.android.richeditortoolbar.util.StringUtil;

public class RichEditorToolbar extends FlexboxLayout implements View.OnClickListener, View.OnLongClickListener, RichEditText.OnSelectionChanged {
    private HashMap<View, Class> mClassMap = new HashMap<>();
    private RichEditText mRichEditText;

    private @ColorInt int mBulletColor = Color.parseColor("#DDDDDD");
    private int mBulletSpanRadius = 16;
    private int mBulletSpanGapWidth = 40;

    private int mLeadingMarginSpanIndent = 40;

    private int mLineDividerSpanMarginTop = 50;
    private int mLineDividerSpanMarginBottom = 50;

    private @ColorInt int mQuoteSpanColor = Color.parseColor("#DDDDDD");
    private int mQuoteSpanStripWidth = 16;
    private int mQuoteSpanGapWidth = 40;

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

    public void setEditText(RichEditText richEditText) {
        mRichEditText = richEditText;
        mRichEditText.addTextChangedListener(mRichEditorWatcher);
        mRichEditText.setOnSelectionChanged(this);
    }

    ///段落span（带参数）：Head
    private TextView mTextViewHead;

    ///段落span（带初始化参数）：Quote
    private ImageView mImageViewQuote;

    ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan
    private ImageView mImageViewAlignNormal;
    private ImageView mImageViewAlignCenter;
    private ImageView mImageViewAlignOpposite;

    ///段落span（带初始化参数）：Bullet
    private ImageView mImageViewBullet;

    ///段落span（带初始化参数）：LeadingMargin
    private ImageView mImageViewLeadingMargin;

    ///段落span：LineDivider
    private ImageView mImageViewLineDivider;

    ///字符span：Bold、Italic
    private ImageView mImageViewBold;
    private ImageView mImageViewItalic;

    ///字符span：Underline、StrikeThrough、Subscript、Superscript
    private ImageView mImageViewUnderline;
    private ImageView mImageViewStrikeThrough;
    private ImageView mImageViewSubscript;
    private ImageView mImageViewSuperscript;

    ///字符span（带参数）：Code
    private ImageView mImageViewCode;

    ///字符span（带参数）：ForegroundColor、BackgroundColor
    private ImageView mImageViewForegroundColor;
    private ImageView mImageViewBackgroundColor;

    ///字符span（带参数）：TypefaceFamily
    private TextView mTextViewTypefaceFamily;

    ///字符span（带参数）：AbsoluteSize
    private TextView mTextViewAbsoluteSize;

    ///字符span（带参数）：RelativeSize
    private TextView mTextViewRelativeSize;

    ///字符span（带参数）：ScaleX
    private TextView mTextViewScaleX;

    ///字符span（带参数）：URL
    private ImageView mImageViewURL;

    ///字符span（带参数）：Image
    private ImageView mImageViewImage;

    ///清除样式
    private ImageView mImageViewClearSpans;


    private void init(Context context) {
        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);

        LayoutInflater.from(context).inflate(R.layout.layout_tool_bar, this, true);


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
        mClassMap.put(mImageViewImage, ImageSpan.class);


        /* -------------- ///清除样式 --------------- */
        mImageViewClearSpans = (ImageView) findViewById(R.id.iv_clear_spans);
        mImageViewClearSpans.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //        final int selectionStart = mRichEditText.getSelectionStart();
                //        final int selectionEnd = mRichEditText.getSelectionEnd();
                final int selectionStart = Selection.getSelectionStart(mRichEditText.getText());
                final int selectionEnd = Selection.getSelectionEnd(mRichEditText.getText());
                if (selectionStart == -1 || selectionEnd == -1 || selectionStart == selectionEnd) {
                    return;
                }

                clearParagraphSpans(selectionStart, selectionEnd);
                clearCharacterSpans(selectionStart, selectionEnd);
            }
        });
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

    private <T> void updateParagraphView(View view, Class<T> clazz, Editable editable, int currentLineStart, int currentLineEnd) {
        final T[] spans = editable.getSpans(currentLineStart, currentLineEnd, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

            ///如果span与当前行首尾相同，则select
            if (spanStart == currentLineStart && spanEnd == currentLineEnd) {
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

                ///注意：找到第一个就退出，不必继续找了
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
            ((TextView) view).setText(view.getContext().getString(R.string.head));
        }
    }
    private <T> void updateCharacterStyleView(View view, Class<T> clazz, Editable editable, int selStart, int selEnd) {
        final T[] spans = editable.getSpans(selStart, selEnd, clazz);

        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

            ///如果不是单光标、或者span在光标区间外
            ///如果isBlockCharacterStyle为false，加上光标尾等于span尾
            if (selStart != selEnd || spanStart < selStart && (selEnd < spanEnd || !isBlockCharacterStyle(view) && selEnd == spanEnd)) {
                if (!view.isSelected()) {
                    view.setSelected(true);
                }

                ///字符span（带参数）：URL
                if (clazz == URLSpan.class) {
                    if (selStart == selEnd || spans.length == 1) {
                        final String text = String.valueOf(editable.toString().toCharArray(), spanStart, spanEnd - spanStart);
                        final String url = ((URLSpan) spans[0]).getURL();
                        view.setTag(R.id.url_text, text);
                        view.setTag(R.id.url_url, url);
                    } else {
                        view.setTag(R.id.url_text, null);
                        view.setTag(R.id.url_url, null);
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

                ///注意：找到第一个就退出，不必继续找了
                return;
            }
        }

        if (view.isSelected()) {
            view.setSelected(false);
        }

        ///字符span（带参数）：URL
        if (clazz == URLSpan.class) {
            ///初始化对话框：无、单选、多选，只有单选时才初始化对话框
            if (selStart < selEnd) {
                final String text = String.valueOf(mRichEditText.getText().toString().toCharArray(), selStart, selEnd - selStart);
                view.setTag(R.id.url_text, text);
                view.setTag(R.id.url_url, null);
            } else {
                view.setTag(R.id.url_text, null);
                view.setTag(R.id.url_url, null);
            }
        }
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == ForegroundColorSpan.class || clazz == BackgroundColorSpan.class) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        ///字符span（带参数）：TypefaceFamily
        else if (clazz == TypefaceSpan.class) {
            ((TextView) view).setText(view.getContext().getString(R.string.font_family));
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            ((TextView) view).setText(view.getContext().getString(R.string.absolute_size));
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            ((TextView) view).setText(view.getContext().getString(R.string.relative_size));
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            ((TextView) view).setText(view.getContext().getString(R.string.scale_x));
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

    private <T> T filterSpanByCompareSpanOrViewParameter(View view, Class<T> clazz, T span, T compareSpan) {
        ///字符span（带参数）：URL
        if (clazz == URLSpan.class) {
            final String spanUrl = ((URLSpan) span).getURL();
            if (compareSpan == null) {
                final URLSpan viewTagURLSpan = (URLSpan) view.getTag();
                final String viewTagUrl = viewTagURLSpan.getURL();
                return TextUtils.equals(spanUrl, viewTagUrl) ? span : null;
            } else {
                final String compareSpanUrl = ((URLSpan) compareSpan).getURL();
                return spanUrl.equals(compareSpanUrl) ? span : null;
            }
        }
        ///字符span（带参数）：Image///////////////?????????????????????????????????
        if (clazz == ImageSpan.class) {
            final String spanImageSrc = ((ImageSpan) span).getSource();
            if (compareSpan == null) {
                final ImageSpan viewTagImageSpan = (ImageSpan) view.getTag();
                final String viewTagImageSrc = viewTagImageSpan.getSource();
                return TextUtils.equals(spanImageSrc, viewTagImageSrc) ? span : null;
            } else {
                final String compareSpanImageSrc = ((ImageSpan) compareSpan).getSource();
                return TextUtils.equals(spanImageSrc, compareSpanImageSrc) ? span : null;
            }
        }
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == ForegroundColorSpan.class) {
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
        if (isParagraphStyle(view)) {

            ///段落span：LineDivider
            if (view == mImageViewLineDivider) {
                final int[] selectionLines = SpanUtil.getSelectionLines(mRichEditText);
                if (selectionLines[0] != -1 && selectionLines[1] != -1) {
                    for (int i = selectionLines[0]; i <= selectionLines[1]; i++) {
                        final int selectionLineStart = SpanUtil.getLineStart(mRichEditText, i);
                        final int selectionLineEnd = SpanUtil.getLineEnd(mRichEditText, i);

                        if (selectionLineStart + 1 != selectionLineEnd || mRichEditText.getText().charAt(selectionLineStart) != '\n') {
                            return;
                        }
                    }
                }
            }

            ///段落span（带参数）：Head
            if (view == mTextViewHead) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(view.getContext(), R.array.head_items, view.getTag());

                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.head_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final String head = (String) StringUtil.getItem(view.getContext(), R.array.head_items, which);
                                ///参数校验
                                if (head == null) {
                                    return;
                                }

                                ///如果view未选中则选中view
                                ///注意：如果view已经选中了则不再进行view选中操作！提高效率
                                if (!view.isSelected()) {
                                    view.setSelected(true);
                                }

                                ///保存参数到view tag
                                view.setTag(head);

                                ///改变selection的span
                                applyParagraphStyleSpansSelection(view, mRichEditText.getText());

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!head.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(head);
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
                                applyParagraphStyleSpansSelection(view, mRichEditText.getText());

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(view.getContext().getString(R.string.head));
                            }
                        }).show();

                return;
            }

            view.setSelected(!view.isSelected());

            applyParagraphStyleSpansSelection(view, mRichEditText.getText());

            ///同组选择互斥
            ///段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan
            if (view.isSelected()) {
                if (mClassMap.get(view) == AlignNormalSpan.class) {
                    if (mImageViewAlignCenter.isSelected()) {
                        mImageViewAlignCenter.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignCenter, mRichEditText.getText());
                    }
                    if (mImageViewAlignOpposite.isSelected()) {
                        mImageViewAlignOpposite.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignOpposite, mRichEditText.getText());
                    }
                } else if (mClassMap.get(view) == AlignCenterSpan.class) {
                    if (mImageViewAlignNormal.isSelected()) {
                        mImageViewAlignNormal.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignNormal, mRichEditText.getText());
                    }
                    if (mImageViewAlignOpposite.isSelected()) {
                        mImageViewAlignOpposite.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignOpposite, mRichEditText.getText());
                    }
                } else if (mClassMap.get(view) == AlignOppositeSpan.class) {
                    if (mImageViewAlignNormal.isSelected()) {
                        mImageViewAlignNormal.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignNormal, mRichEditText.getText());
                    }
                    if (mImageViewAlignCenter.isSelected()) {
                        mImageViewAlignCenter.setSelected(false);
                        applyParagraphStyleSpansSelection(mImageViewAlignCenter, mRichEditText.getText());
                    }
                }
            }

        } else if (isCharacterStyle(view)) {

            ///字符span（带参数）：URL
            if (view == mImageViewURL) {
                final URLSpanDialogBuilder urlSpanDialogBuilder = URLSpanDialogBuilder
                        .with(view.getContext())
                        .setPositiveButton(android.R.string.ok, new URLSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String text, String url) {
                                ///参数校验：两项都为空则代表维持不变、不做任何处理
                                ///注意：某项为空、或值相同即代表该项维持不变，不为空且值不同则代表该项改变
                                if (text.length() == 0 && url.length() == 0) {  //////??????url正则表达式
                                    return;
                                }
                                final URLSpan[] selectedSpans = SpanUtil.getSelectedSpans(mRichEditText, URLSpan.class);
                                if ((text.length() == 0 || url.length() == 0) && (selectedSpans == null || selectedSpans.length == 0)) {  //////??????url正则表达式
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

//                                ///清空view tag
                                view.setTag(R.id.url_text, null);
                                view.setTag(R.id.url_url, null);
                            }
                        });

                final String text = (String) view.getTag(R.id.url_text);
                final String url = (String) view.getTag(R.id.url_url);
                urlSpanDialogBuilder.initial(text, url);
                urlSpanDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：Image/////////////////?????????????
            if (view == mImageViewImage) {
                final ImageSpanDialogBuilder imageSpanDialogBuilder = ImageSpanDialogBuilder
                        .with(view.getContext())
                        .setPositiveButton(android.R.string.ok, new ImageSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String imageSrc) {
                                view.setSelected(true);
                                ///设置View的tag
                                view.setTag(imageSrc);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        ///清除样式
                        .setNeutralButton(R.string.clear, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view.setSelected(false);
                                ///清除View的tag
                                view.setTag(null);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                            }
                        });
                ///初始化
                if (view.isSelected()) {
                    final String imageSrc = (String) view.getTag();
                    imageSpanDialogBuilder.initial(imageSrc);
                }
                imageSpanDialogBuilder.build().show();

                return;
            }

            ///字符span（带参数）：ForegroundColor、BackgroundColor
            if (view == mImageViewForegroundColor || view == mImageViewBackgroundColor) {
                ///颜色选择器
                final ColorPickerDialogBuilder colorPickerDialogBuilder = ColorPickerDialogBuilder
                        .with(view.getContext())
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
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
                final int checkedItem = StringUtil.getIndex(view.getContext(), R.array.typeface_family_items, view.getTag());

                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.typeface_family_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence family = StringUtil.getItem(view.getContext(), R.array.typeface_family_items, which);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!family.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(family);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(view.getContext().getString(R.string.font_family));
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：AbsoluteSize
            if (view == mTextViewAbsoluteSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(view.getContext(), R.array.absolute_size_items, view.getTag());

                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.absolute_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence size = StringUtil.getItem(view.getContext(), R.array.absolute_size_items, which);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!size.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(size);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(view.getContext().getString(R.string.absolute_size));
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：RelativeSize
            if (view == mTextViewRelativeSize) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(view.getContext(), R.array.relative_size_items, view.getTag());

                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.relative_size_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence sizeChange = StringUtil.getItem(view.getContext(), R.array.relative_size_items, which);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!sizeChange.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(sizeChange);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(view.getContext().getString(R.string.relative_size));
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：ScaleX
            if (view == mTextViewScaleX) {
                ///checkedItem：由view tag决定checkedItem，如无tag，checkedItem则为-1
                final int checkedItem = StringUtil.getIndex(view.getContext(), R.array.scale_x_items, view.getTag());

                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.scale_x_items, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ///由用户选择项which获取对应的选择参数
                                final CharSequence scaleX = StringUtil.getItem(view.getContext(), R.array.scale_x_items, which);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///当view text不为用户选择参数时更新view text
                                ///注意：如果相同则不更新！提高效率
                                if (!scaleX.equals(((TextView) view).getText().toString())) {
                                    ((TextView) view).setText(scaleX);
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
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());

                                ///清空view tag
                                view.setTag(null);

                                ///更新view text
                                ((TextView) view).setText(view.getContext().getString(R.string.scale_x));
                            }
                        }).show();

                return;
            }

            view.setSelected(!view.isSelected());

            applyCharacterStyleSpansSelection(view, mRichEditText.getText());
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        ///段落span（带初始化参数）：LineDivider
        if (view == mImageViewLineDivider) {
            LineDividerDialogBuilder
                    .with(view.getContext())
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
                    .with(view.getContext())
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
                    .with(view.getContext())
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
                    .with(view.getContext())
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
    public void selectionChanged(int selStart, int selEnd) {
        Log.d("TAG", "============= selectionChanged ============" + mRichEditText.getText().length());

        int currentLineStart = -1;
        int currentLineEnd = -1;
        final int[] selectionLines = SpanUtil.getSelectionLines(mRichEditText);
        if (selectionLines[0] != -1 && selectionLines[1] != -1) {
            ///注意：只取第一行作为更新Toolbar的条件！
            currentLineStart = SpanUtil.getLineStart(mRichEditText, selectionLines[0]);
            currentLineEnd = SpanUtil.getLineEnd(mRichEditText, selectionLines[0]);
        }

        for (View view : mClassMap.keySet()) {
            if (isParagraphStyle(view)) {
                updateParagraphView(view, mClassMap.get(view), mRichEditText.getText(), currentLineStart, currentLineEnd);
            } else if (isCharacterStyle(view)) {
                updateCharacterStyleView(view, mClassMap.get(view), mRichEditText.getText(), selStart, selEnd);
            }

            ///test
            SpanUtil.testOutput(mRichEditText.getText(), mClassMap.get(view));
        }
    }

    private void applyParagraphStyleSpansSelection(View view, Editable editable) {
        final int[] selectionLines = SpanUtil.getSelectionLines(mRichEditText);
        if (selectionLines[0] != -1 && selectionLines[1] != -1) {
            for (int i = selectionLines[0]; i <= selectionLines[1]; i++) {
                final int selectionLineStart = SpanUtil.getLineStart(mRichEditText, i);
                final int selectionLineEnd = SpanUtil.getLineEnd(mRichEditText, i);

                ///调整同类span
                adjustParagraphStyleSpansSelection(view, mClassMap.get(view), editable, selectionLineStart, selectionLineEnd, view.isSelected());
            }
        }
    }
    private <T> void adjustParagraphStyleSpansSelection(View view, Class<T> clazz, Editable editable, int start, int end, boolean isSelected) {
        boolean isNewSpanNeeded = true;  ///changed内容是否需要新添加span

        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

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
    }


    /* ----------------- ///[TextWatcher] ------------------ */
    ///注意：当edittext有黑色下划线的单词提醒时（不是红色下划线的suggestion），start不是发生改变的位置！而是以空格或回车分割的单词的起始位置，而count/after/before可能会同时大于0
    private TextWatcher mRichEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0) {    ///[TextWatcher#删除]
                for (View view : mClassMap.keySet()) {
                    if (isCharacterStyle(view)) {
                        ///清除掉已经被删除的span，否则将会产生多余的无效span！
                        removeSpans(mClassMap.get(view), mRichEditText.getText(), start, start + count, false);
                    }
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final int firstLine = SpanUtil.getLineForOffset(mRichEditText, start);
            int firstLineStart = -1;
            int firstLineEnd = -1;
            if (firstLine != -1) {
                firstLineStart = SpanUtil.getLineStart(mRichEditText, firstLine);
                firstLineEnd = SpanUtil.getLineEnd(mRichEditText, firstLine);
            }

            final int lastLine = SpanUtil.getLineForOffset(mRichEditText, start + count);
            int lastLineStart = -1;
            int lastLineEnd = -1;
            if (lastLine != -1) {
                if (lastLine == firstLine) {
                    lastLineStart = firstLineStart;
                    lastLineEnd = firstLineEnd;
                } else {
                    lastLineStart = SpanUtil.getLineStart(mRichEditText, lastLine);
                    lastLineEnd = SpanUtil.getLineEnd(mRichEditText, lastLine);
                }
            }

            for (View view : mClassMap.keySet()) {
                if (isParagraphStyle(view)) {
                    adjustParagraphStyleSpans(view, mClassMap.get(view), mRichEditText.getText(),
                            start, start + count,
                            firstLineStart, firstLineEnd, lastLineStart, lastLineEnd, view.isSelected());
                } else if (isCharacterStyle(view)) {
                    if (count > 0) {   ///功能三：[TextWatcher#添加]
//                        //////??????平摊并合并交叉重叠的同类span
//                        flatSpans(mClassMap.get(view), mRichEditText.getText(), start, start + count);

                        if (isBlockCharacterStyle(view)) {
                            adjustBlockCharacterStyleSpans(view, mClassMap.get(view), mRichEditText.getText(), start, start + count, view.isSelected(), false);
                        } else {
                            ///adjustCharacterStyleSpans()与adjustCharacterStyleSpansSelection()完全相同！
                            adjustCharacterStyleSpans(view, mClassMap.get(view), mRichEditText.getText(), start, start + count, view.isSelected(), false);
                        }
                    } else if (before > 0) {    ///功能四：[TextWatcher]删除，删除后如果没添加内容（即count == 0且before > 0），则合并同类span
                        ///合并同类span
                        if (!isBlockCharacterStyle(view)) {
                            joinSpanByPosition(view, mClassMap.get(view), mRichEditText.getText(), start);
                        }
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            ///消除EditText输入时自动产生UnderlineSpan
            ///https://stackoverflow.com/questions/35323111/android-edittext-is-underlined-when-typing
            ///https://stackoverflow.com/questions/46822580/edittext-remove-black-underline-while-typing/47704299#47704299
            for (UnderlineSpan span : s.getSpans(0, s.length(), UnderlineSpan.class)) {
                ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
                if (span.getClass() != UnderlineSpan.class) {
                    continue;
                }

                s.removeSpan(span);
            }
        }
    };

    private <T> void adjustParagraphStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end,
                                               int firstLineStart, int firstLineEnd, int lastLineStart, int lastLineEnd, boolean isSelected) {
        boolean isSet = false;

        final T[] startSpans = editable.getSpans(firstLineStart, firstLineEnd, clazz);
        for (T span : startSpans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///段落span：LineDivider：移除无效的LineDivider
            if (spanStart == spanEnd || clazz == LineDividerSpan.class && spanStart == firstLineStart && firstLineStart + 1 != firstLineEnd) {
                editable.removeSpan(span);
                continue;
            }

            if (spanStart > firstLineStart) {
                editable.removeSpan(span);
            } else if (spanStart == firstLineStart) {
                if (isSet) {
                    editable.removeSpan(span);
                } else {
                    if (spanEnd != firstLineEnd) {
                        editable.setSpan(span, firstLineStart, firstLineEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    isSet = true;
                }
            } else {
                if (spanEnd != firstLineStart) {
                    editable.setSpan(span, spanStart, firstLineStart, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }

        if (firstLineStart != lastLineStart && lastLineStart != lastLineEnd) {
            final T[] endSpans = editable.getSpans(lastLineStart, lastLineEnd, clazz);
            for (T span : endSpans) {
                ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
                if (span.getClass() != clazz) {
                    continue;
                }

                final int spanStart = editable.getSpanStart(span);
                final int spanEnd = editable.getSpanEnd(span);
                ///删除多余的span
                if (spanStart == spanEnd) {
                    editable.removeSpan(span);
                    continue;
                }

                if (spanEnd != lastLineEnd) {
                    editable.setSpan(span, lastLineStart, lastLineEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }
    private <T> void adjustBlockCharacterStyleSpans(View view, Class<T> clazz, Editable editable, int start, int end, boolean isSelected, boolean isFromSelection) {
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

        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

            ///如果单光标、且位于span的首尾，则忽略
            if (start == end && (spanStart == start || end == spanEnd)) {
                continue;
            }

            if (isSelected) {

                ///字符span（带参数）：URL
                if (clazz == URLSpan.class) {
                    final String viewTagText = (String) view.getTag(R.id.url_text);
                    final String viewTagUrl = (String) view.getTag(R.id.url_url);
                    final String compareText = String.valueOf(mRichEditText.getText().toString().toCharArray(), spanStart, spanEnd - spanStart);
                    final String spanUrl = ((URLSpan) span).getURL();
                    if (isFromSelection && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                        mRichEditText.getText().replace(spanStart, spanEnd, viewTagText);

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
                final String compareText = String.valueOf(mRichEditText.getText().toString().toCharArray(), start, end - start);
                if (isFromSelection && !TextUtils.isEmpty(viewTagText) && !compareText.equals(viewTagText)) {
                    mRichEditText.getText().replace(start, end, viewTagText);
                } else {
                    if (!TextUtils.isEmpty(viewTagUrl)) {
                        editable.setSpan(new URLSpan(viewTagUrl), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

        }

        ///[isUpdateNeeded]
        if (isUpdateNeeded) {
            //        final int selectionStart = mRichEditText.getSelectionStart();
            //        final int selectionEnd = mRichEditText.getSelectionEnd();
            final int selectionStart = Selection.getSelectionStart(mRichEditText.getText());
            final int selectionEnd = Selection.getSelectionEnd(mRichEditText.getText());
            if (selectionStart != -1 && selectionEnd != -1) {
                updateCharacterStyleView(view, mClassMap.get(view), mRichEditText.getText(), selectionStart, selectionEnd);
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

        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            ///isSelected为true时（外延），当区间[start, end]中有多个span，首次处理span后，其余的span都应删除
            ///注意：区间[start, end]结尾处可能会有部分在区间[start, end]中的span，因为首次处理中包含了join，所以已经被删除了
            if (hasSpan && isSelected && isSameWithViewParameter) {
                editable.removeSpan(span);
                continue;
            }

            isSameWithViewParameter = isSameWithViewParameter(view, clazz, span);

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

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
        final T leftSpan = getLeftSpan(view, clazz, editable, start, null);
        if (leftSpan != null) {
            final int leftSpanStart = editable.getSpanStart(leftSpan);
            editable.setSpan(leftSpan, leftSpanStart, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            findAndJoinRightSpan(view, clazz, editable, leftSpan);
            return;
        }
        final T rightSpan = getRightSpan(view, clazz, editable, end, null);
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
        final T[] spans = editable.getSpans(position, position, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                return ;
            }

            findAndJoinLeftSpan(view, clazz, editable, span);
            findAndJoinRightSpan(view, clazz, editable, span);
        }
    }

    /**
     * 左联合并同类span
     */
    private <T> int findAndJoinLeftSpan(View view, Class<T> clazz, Editable editable, T span) {
        final int spanStart = editable.getSpanStart(span);
        final int spanEnd = editable.getSpanEnd(span);

        int resultStart = spanStart;
        final T leftSpan = getLeftSpan(view, clazz, editable, spanStart, span);
        if (leftSpan != null) {
            resultStart = editable.getSpanStart(leftSpan);
            editable.setSpan(span, resultStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            editable.removeSpan(leftSpan);
        }
        return resultStart;
    }

    /**
     * 右联合并同类span
     */
    private <T> int findAndJoinRightSpan(View view, Class<T> clazz, Editable editable, T span) {
        final int start = editable.getSpanStart(span);
        final int end = editable.getSpanEnd(span);

        int resultEnd = end;
        final T rightSpan = getRightSpan(view, clazz, editable, end, span);
        if (rightSpan != null) {
            resultEnd = editable.getSpanEnd(rightSpan);
            editable.setSpan(span, start, resultEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            editable.removeSpan(rightSpan);
        }
        return resultEnd;
    }

    /**
     * 获得左边的同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    private <T> T getLeftSpan(View view, Class<T> clazz, Editable editable, int start, T compareSpan) {
        final T[] leftSpans = editable.getSpans(start, start, clazz);
        for (T leftSpan : leftSpans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (leftSpan.getClass() != clazz || leftSpan == compareSpan) {
                continue;
            }
            return filterSpanByCompareSpanOrViewParameter(view, clazz, leftSpan, compareSpan);
        }
        return null;
    }

    /**
     * 获得右边的同类span
     *
     * 注意：要包含交叉的情况！而不仅仅是首尾相连
     */
    private <T> T getRightSpan(View view, Class<T> clazz, Editable editable, int end, T compareSpan) {
        final T[] rightSpans = editable.getSpans(end, end, clazz);
        for (T rightSpan : rightSpans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (rightSpan.getClass() != clazz || rightSpan == compareSpan) {
                continue;
            }
            return filterSpanByCompareSpanOrViewParameter(view, clazz, rightSpan, compareSpan);
        }
        return null;
    }

    /**
     * 清除区间内的span
     */
    private void clearParagraphSpans(int selectionStart, int selectionEnd) {
        int currentLineStart = -1;
        int currentLineEnd = -1;
        final int[] selectionLines = SpanUtil.getSelectionLines(mRichEditText);
        if (selectionLines[0] != -1 && selectionLines[1] != -1) {
            ///注意：只取第一行作为更新Toolbar的条件！
            currentLineStart = SpanUtil.getLineStart(mRichEditText, selectionLines[0]);
            currentLineEnd = SpanUtil.getLineEnd(mRichEditText, selectionLines[0]);
        }

        for (View view : mClassMap.keySet()) {
            if (isParagraphStyle(view)) {
                removeSpans(mClassMap.get(view), mRichEditText.getText(), selectionStart, selectionEnd, true);
            }
            updateParagraphView(view, mClassMap.get(view), mRichEditText.getText(), currentLineStart, currentLineEnd);
        }
    }
    private void clearCharacterSpans(int selectionStart, int selectionEnd) {
        for (View view : mClassMap.keySet()) {
            if (isCharacterStyle(view)) {
                ///调整同类span
                if (isBlockCharacterStyle(view)) {
                    adjustBlockCharacterStyleSpans(view, mClassMap.get(view), mRichEditText.getText(), selectionStart, selectionEnd, false, true);
                } else {
                    adjustCharacterStyleSpans(view, mClassMap.get(view), mRichEditText.getText(), selectionStart, selectionEnd, false, true);
                }
                updateCharacterStyleView(view, mClassMap.get(view), mRichEditText.getText(), selectionStart, selectionEnd);
            }
        }
    }

    /**
     * 清除掉已经被删除的span，否则将会产生多余的无效span！
     */
    private <T> void removeSpans(Class<T> clazz, Editable editable, int start, int end, boolean isRange) {
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart == spanEnd || isRange && start <= spanStart && spanEnd <= end) {
                editable.removeSpan(span);
            }
        }
    }

    /**
     * 平摊并合并交叉重叠的同类span
     *
     * 本编辑器内部添加逻辑不会产生交叉重叠，以防从编辑器外部或HTML转换后可能会产生的交叉重叠
     * 注意：暂时没有考虑ForegroundColor、BackgroundColor等带参数的span！即参数不同的同类span都视为相等而合并
     */
    private <T> void flatSpans(Class<T> clazz, Editable editable, int start, int end) {
        T currentSpan = null;
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身），比如CustomUnderlineSpan extends UnderlineSpan（注意：UnderlineSpan有可能被系统自动添加的suggestion！）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            ///删除多余的span
            if (spanStart == spanEnd) {
                editable.removeSpan(span);
                continue;
            }

            if (currentSpan == null) {
                currentSpan = span;
                continue;
            }
            if (currentSpan == span) {
                continue;
            }
            final int currentSpanStart = editable.getSpanStart(currentSpan);
            final int currentSpanEnd = editable.getSpanEnd(currentSpan);
            if (currentSpanEnd < spanStart) {
                currentSpan = span;
                continue;
            }
            if (currentSpanStart >= spanStart && currentSpanEnd <= spanEnd) {
                editable.removeSpan(currentSpan);
                currentSpan = span;
                continue;
            }
            if (currentSpanStart <= spanStart && currentSpanEnd <= spanEnd) {
                editable.setSpan(currentSpan, currentSpanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else if (currentSpanStart >= spanStart) {
                editable.setSpan(currentSpan, spanStart, currentSpanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            editable.removeSpan(span);
        }
    }

}
