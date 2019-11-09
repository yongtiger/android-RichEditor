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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;

import cc.brainbook.android.colorpicker.builder.ColorPickerClickListener;
import cc.brainbook.android.colorpicker.builder.ColorPickerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.BulletSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LeadingMarginSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.LineDividerDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.QuoteSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.builder.URLSpanDialogBuilder;
import cc.brainbook.android.richeditortoolbar.span.AlignCenterSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignNormalSpan;
import cc.brainbook.android.richeditortoolbar.span.AlignOppositeSpan;
import cc.brainbook.android.richeditortoolbar.span.BoldSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomBulletSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomQuoteSpan;
import cc.brainbook.android.richeditortoolbar.span.CustomUnderlineSpan;
import cc.brainbook.android.richeditortoolbar.span.HeadSpan;
import cc.brainbook.android.richeditortoolbar.span.ItalicSpan;
import cc.brainbook.android.richeditortoolbar.span.LineDividerSpan;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;
import cc.brainbook.android.richeditortoolbar.util.StringUtil;

import static cc.brainbook.android.richeditortoolbar.BuildConfig.DEBUG;

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

    ///字符span（带参数）：URL
    private ImageView mImageViewURL;

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


        /* -------------- ///自定义段落span：LineDivider --------------- */
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


        /* -------------- ///字符span（带参数）：URL --------------- */
        mImageViewURL = (ImageView) findViewById(R.id.iv_url);
        mImageViewURL.setOnClickListener(this);
        mClassMap.put(mImageViewURL, URLSpan.class);


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

    }

    private boolean isCharacterStyle(View view) {
        return view == mImageViewUnderline
                || view == mImageViewStrikeThrough
                || view == mImageViewSuperscript
                || view == mImageViewSubscript
                || view == mImageViewBold
                || view == mImageViewItalic
                || view == mImageViewForegroundColor
                || view == mImageViewBackgroundColor
                || view == mImageViewURL
                || view == mTextViewTypefaceFamily
                || view == mTextViewAbsoluteSize
                || view == mTextViewRelativeSize
                || view == mTextViewScaleX;
    }
    private boolean isParagraphStyle(View view) {
        return view == mImageViewAlignNormal
                || view == mImageViewAlignCenter
                || view == mImageViewAlignOpposite
                || view == mImageViewLineDivider
                || view == mImageViewQuote
                || view == mImageViewBullet
                || view == mImageViewLeadingMargin
                || view == mTextViewHead;
    }


    /* ----------------- ///功能一：[selectionChanged]根据selection更新工具条按钮 ------------------ */
    @Override
    public void selectionChanged(int selStart, int selEnd) {
        if (DEBUG) Log.d("TAG", "============= selectionChanged ============" + mRichEditText.getText().length());

        int currentLineStart = -1;
        int currentLineEnd = -1;
        final int[] selectionLines = SpanUtil.getSelectionLines(mRichEditText);
        if (selectionLines[0] != -1 && selectionLines[1] != -1) {
            ///注意：只取第一行作为更新Toolbar的条件！
            currentLineStart = SpanUtil.getLineStart(mRichEditText, selectionLines[0]);
            currentLineEnd = SpanUtil.getLineEnd(mRichEditText, selectionLines[0]);
        }

        for (View view : mClassMap.keySet()) {
            if (isCharacterStyle(view)) {
                updateCharacterStyleViews(mClassMap.get(view), mRichEditText.getText(), selStart, selEnd, view);
            } else {
                updateParagraphViews(mClassMap.get(view), mRichEditText.getText(), currentLineStart, currentLineEnd, view);
            }

            ///test
            if (DEBUG) SpanUtil.testOutput(mClassMap.get(view), mRichEditText.getText());
        }
    }

    /**
     * 更新工具条按钮
     */
    private <T> void updateCharacterStyleViews(Class<T> clazz, Editable editable, int selStart, int selEnd, View view) {
        final T[] spans = editable.getSpans(selStart, selEnd, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
//            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart < selStart || spanStart == selStart && spanStart < selEnd) {  ///!!!!!!!!!!!!
                view.setSelected(true);

                ///字符span（带参数）：URL
                if (clazz == URLSpan.class) {
                    final String link = ((URLSpan) span).getURL();
                    view.setTag(link);
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
                    mTextViewTypefaceFamily.setText(family);
                }
                ///字符span（带参数）：AbsoluteSize
                else if (clazz == AbsoluteSizeSpan.class) {
                    final int size = ((AbsoluteSizeSpan) span).getSize();
                    view.setTag(size);
                    mTextViewAbsoluteSize.setText(String.valueOf(size));
                }
                ///字符span（带参数）：RelativeSize
                else if (clazz == RelativeSizeSpan.class) {
                    final float sizeChange = ((RelativeSizeSpan) span).getSizeChange();
                    view.setTag(sizeChange);
                    mTextViewRelativeSize.setText(String.valueOf(sizeChange));
                }
                ///字符span（带参数）：ScaleX
                else if (clazz == ScaleXSpan.class) {
                    final float scaleX = ((ScaleXSpan) span).getScaleX();
                    view.setTag(scaleX);
                    mTextViewScaleX.setText(String.valueOf(scaleX));
                }

                return;
            }
        }

        view.setSelected(false);

        ///字符span（带参数）：URL
        if (clazz == URLSpan.class) {
            view.setTag(null);
        }
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == ForegroundColorSpan.class || clazz == BackgroundColorSpan.class) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        ///字符span（带参数）：TypefaceFamily
        else if (clazz == TypefaceSpan.class) {
            view.setTag(null);
            mTextViewTypefaceFamily.setText(view.getContext().getString(R.string.font_family));
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            view.setTag(null);
            mTextViewAbsoluteSize.setText(view.getContext().getString(R.string.absolute_size));
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            view.setTag(null);
            mTextViewRelativeSize.setText(view.getContext().getString(R.string.relative_size));
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            view.setTag(null);
            mTextViewScaleX.setText(view.getContext().getString(R.string.scale_x));
        }
    }
    private <T> void updateParagraphViews(Class<T> clazz, Editable editable, int currentLineStart, int currentLineEnd, View view) {
        final T[] spans = editable.getSpans(currentLineStart, currentLineEnd, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart == currentLineStart && spanEnd == currentLineEnd) {
                view.setSelected(true);

                ///段落span（带参数）：Head
                if (clazz == HeadSpan.class) {
                    final String head = ((HeadSpan) span).getHead();
                    view.setTag(head);
                    mTextViewHead.setText(head);
                }

                return;
            }
        }
        view.setSelected(false);

        ///段落span（带参数）：Head
        if (clazz == HeadSpan.class) {
            view.setTag(null);
            mTextViewHead.setText(view.getContext().getString(R.string.head));
        }
    }


    /* ----------------- ///功能二：[onClick]点击更新ImageView，并且当selectionStart != selectionEnd时改变selection的span ------------------ */
    @Override
    public void onClick(final View view) {
        if (isCharacterStyle(view)) {

            ///字符span（带参数）：URL
            if (view == mImageViewURL) {
                final URLSpanDialogBuilder urlSpanDialogBuilder = URLSpanDialogBuilder
                        .with(view.getContext())
                        .setPositiveButton(android.R.string.ok, new URLSpanDialogBuilder.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, String link) {
                                view.setSelected(true);
                                ///设置View的tag
                                view.setTag(link);
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
                    final String link = (String) view.getTag();
                    urlSpanDialogBuilder.initial(link);
                }
                urlSpanDialogBuilder.build().show();

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
                                view.setSelected(true);
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
                                view.setSelected(false);
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
                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.typeface_family_items, StringUtil.getIndex(view.getContext(), R.array.typeface_family_items, view.getTag()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view.setSelected(true);
                                final CharSequence family = StringUtil.getItem(view.getContext(), R.array.typeface_family_items, which);
                                ///设置View的tag
                                view.setTag(family);
                                mTextViewTypefaceFamily.setText(family);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                                dialog.dismiss();
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
                                mTextViewTypefaceFamily.setText(view.getContext().getString(R.string.font_family));
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：AbsoluteSize
            if (view == mTextViewAbsoluteSize) {
                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.absolute_size_items, StringUtil.getIndex(view.getContext(), R.array.absolute_size_items, view.getTag()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view.setSelected(true);
                                final CharSequence absoluteSize = StringUtil.getItem(view.getContext(), R.array.absolute_size_items, which);
                                ///设置View的tag
                                view.setTag(absoluteSize);
                                mTextViewAbsoluteSize.setText(absoluteSize);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                                dialog.dismiss();
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
                                mTextViewAbsoluteSize.setText(view.getContext().getString(R.string.absolute_size));
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：RelativeSize
            if (view == mTextViewRelativeSize) {
                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.relative_size_items, StringUtil.getIndex(view.getContext(), R.array.relative_size_items, view.getTag()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view.setSelected(true);
                                final CharSequence relativeSize = StringUtil.getItem(view.getContext(), R.array.relative_size_items, which);
                                ///设置View的tag
                                view.setTag(relativeSize);
                                mTextViewRelativeSize.setText(relativeSize);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                                dialog.dismiss();
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
                                mTextViewRelativeSize.setText(view.getContext().getString(R.string.relative_size));
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                            }
                        }).show();

                return;
            }

            ///字符span（带参数）：ScaleX
            if (view == mTextViewScaleX) {
                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.scale_x_items, StringUtil.getIndex(view.getContext(), R.array.scale_x_items, view.getTag()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view.setSelected(true);
                                final CharSequence scaleX = StringUtil.getItem(view.getContext(), R.array.scale_x_items, which);
                                ///设置View的tag
                                view.setTag(scaleX);
                                mTextViewScaleX.setText(scaleX);
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                                dialog.dismiss();
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
                                mTextViewScaleX.setText(view.getContext().getString(R.string.scale_x));
                                ///改变selection的span
                                applyCharacterStyleSpansSelection(view, mRichEditText.getText());
                            }
                        }).show();

                return;
            }

            view.setSelected(!view.isSelected());
            applyCharacterStyleSpansSelection(view, mRichEditText.getText());

        } else if (isParagraphStyle(view)) {

            ///自定义段落span：LineDivider
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
                new AlertDialog.Builder(view.getContext())
                        .setSingleChoiceItems(R.array.head_items, StringUtil.getIndex(view.getContext(), R.array.head_items, view.getTag()), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view.setSelected(true);
                                final CharSequence head = StringUtil.getItem(view.getContext(), R.array.head_items, which);
                                ///设置View的tag
                                view.setTag(head);
                                mTextViewHead.setText(head);
                                ///改变selection的span
                                applyParagraphStyleSpansSelection(view, mRichEditText.getText());
                                dialog.dismiss();
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
                                mTextViewHead.setText(view.getContext().getString(R.string.head));
                                ///改变selection的span
                                applyParagraphStyleSpansSelection(view, mRichEditText.getText());
                            }
                        }).show();

                return;
            }

            view.setSelected(!view.isSelected());
            applyParagraphStyleSpansSelection(view, mRichEditText.getText());

            ///同组选择互斥
            ///自定义段落span：AlignNormalSpan、AlignCenterSpan、AlignOppositeSpan
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

    /**
     * 改变selection的span
     */
    private void applyCharacterStyleSpansSelection(View view, Editable editable) {
//        final int selectionStart = mRichEditText.getSelectionStart();
//        final int selectionEnd = mRichEditText.getSelectionEnd();
        final int selectionStart = Selection.getSelectionStart(editable);
        final int selectionEnd = Selection.getSelectionEnd(editable);
        ///当selectionStart != selectionEnd时改变selection的span
        if (selectionStart == selectionEnd) {
            return;
        }

        ///调整同类span
        adjustCharacterStyleSpans(mClassMap.get(view), editable, selectionStart, selectionEnd, view.isSelected(), true);
    }
    private void applyParagraphStyleSpansSelection(View view, Editable editable) {
        final int[] selectionLines = SpanUtil.getSelectionLines(mRichEditText);
        if (selectionLines[0] != -1 && selectionLines[1] != -1) {
            for (int i = selectionLines[0]; i <= selectionLines[1]; i++) {
                final int selectionLineStart = SpanUtil.getLineStart(mRichEditText, i);
                final int selectionLineEnd = SpanUtil.getLineEnd(mRichEditText, i);

                ///调整同类span
                adjustParagraphStyleSpansSelection(mClassMap.get(view), editable, selectionLineStart, selectionLineEnd, view.isSelected());
            }
        }
    }
    private <T> void adjustParagraphStyleSpansSelection(Class<T> clazz, Editable editable, int start, int end, boolean isSelected) {
        boolean isNewSpanNeeded = true;  ///changed内容是否需要新添加span
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart == start && spanEnd == end) {
                isNewSpanNeeded = false;
                if (isSelected) {

                    ///段落span（带参数）：Head
                    if (clazz == HeadSpan.class) {
                        final String spanHead = ((HeadSpan) span).getHead();
                        final String viewHead = (String) mTextViewHead.getTag();
                        if (!spanHead.equals(viewHead)) {
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
            newParagraphStyleSpan(clazz, editable, start, end);
        }
    }


    /* ----------------- ///[TextWatcher] ------------------ */
    private TextWatcher mRichEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0) {    ///功能四：[TextWatcher#删除]
                for (View view : mClassMap.keySet()) {
                    if (isCharacterStyle(view)) {
                        ///清除掉已经被删除的span，否则将会产生多余的无效span！
                        removeSpanBeforeTextChanged(mClassMap.get(view), mRichEditText.getText(), start, start + count);
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
                if (isCharacterStyle(view)) {
                    if (count > 0) {   ///功能三：[TextWatcher#添加]
//                        //////??????平摊并合并交叉重叠的同类span
//                        flatSpans(mClassMap.get(view), mRichEditText.getText(), start, start + count);

                        adjustCharacterStyleSpans(mClassMap.get(view), mRichEditText.getText(), start, start + count, view.isSelected(), false);
                    } else if (before > 0) {    ///功能四：[TextWatcher]删除，删除后如果没添加内容（即count == 0且before > 0），则合并同类span
                        ///合并同类span
                        joinSpans(mClassMap.get(view), mRichEditText.getText(), start);
                    }
                } else if (isParagraphStyle(view)) {
                    adjustParagraphStyleSpans(mClassMap.get(view), mRichEditText.getText(),
                            start, start + count,
                            firstLineStart, firstLineEnd, lastLineStart, lastLineEnd, view.isSelected());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            ///消除EditText输入时自动产生UnderlineSpan
            ///https://stackoverflow.com/questions/35323111/android-edittext-is-underlined-when-typing
            ///https://stackoverflow.com/questions/46822580/edittext-remove-black-underline-while-typing/47704299#47704299
            for (UnderlineSpan span : s.getSpans(0, s.length(), UnderlineSpan.class)) {
                ///忽略getSpans()获取的子类（不是clazz本身）
                if (span.getClass() != UnderlineSpan.class) {
                    continue;
                }

                s.removeSpan(span);
            }
        }
    };

    /**
     * 清除掉已经被删除的span，否则将会产生多余的无效span！
     */
    private <T> void removeSpanBeforeTextChanged(Class<T> clazz, Editable editable, int start, int end) {
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart >= start && spanEnd <= end) {
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
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
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

    /**
     * 合并同类span
     */
    private <T> void joinSpans(Class<T> clazz, Editable editable, int position) {
        final T[] spans = editable.getSpans(position, position, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanEnd == position) {
                rightJoinSpan(clazz, editable, spanStart, spanEnd, span);
            }
        }
    }

    /**
     * 调整同类span
     *
     * isRemoved：当工具条按钮isSelected为false时，新添加的富文本是否清除。applyCharacterStyleSpansSelection()中为true，onClick()中为false
     *
     * 注意：如果Spanned.SPAN_INCLUSIVE_INCLUSIVE，则自动添加span
     * 注意：新添加的内容除了可为纯文字（无span），也可以为富文本（有span，但必须flatSpans()处理过！）
     */
    private <T> void adjustCharacterStyleSpans(Class<T> clazz, Editable editable, int start, int end, boolean isSelected, boolean isRemoved) {
        boolean isNewSpanNeeded = true;  ///changed内容是否需要新添加span
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart < start || spanEnd > end) {
                if (isSelected) {

                    ///字符span（带参数）：URL
                    if (clazz == URLSpan.class) {
                        final String spanLink = ((URLSpan) span).getURL();
                        final String viewLink = (String) mImageViewURL.getTag();
                        if (spanLink.equals(viewLink)) {
                            isNewSpanNeeded = false;
                            expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    }
                    ///字符span（带参数）：ForegroundColor、BackgroundColor
                    else if (clazz == ForegroundColorSpan.class) {
                        @ColorInt final int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
                        final ColorDrawable colorDrawable = (ColorDrawable) mImageViewForegroundColor.getBackground();
                        if (foregroundColor == colorDrawable.getColor()) {
                            isNewSpanNeeded = false;
                            expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    } else if (clazz == BackgroundColorSpan.class) {
                        @ColorInt final int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
                        final ColorDrawable colorDrawable = (ColorDrawable) mImageViewBackgroundColor.getBackground();
                        if (backgroundColor == colorDrawable.getColor()) {
                            isNewSpanNeeded = false;
                            expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    }
                    ///字符span（带参数）：TypefaceFamily
                    else if (clazz == TypefaceSpan.class) {
                        final String spanFamily = ((TypefaceSpan) span).getFamily();
                        final String viewFamily = (String) mTextViewTypefaceFamily.getTag();
                        if (spanFamily.equals(viewFamily)) {
                            isNewSpanNeeded = false;
                            expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    }
                    ///字符span（带参数）：AbsoluteSize
                    else if (clazz == AbsoluteSizeSpan.class) {
                        final int spanSize = ((AbsoluteSizeSpan) span).getSize();
                        final int viewSize = Integer.parseInt(mTextViewAbsoluteSize.getTag().toString());
                        if (spanSize == viewSize) {
                            isNewSpanNeeded = false;
                            expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    }
                    ///字符span（带参数）：RelativeSize
                    else if (clazz == RelativeSizeSpan.class) {
                        final float spanSizeChange = ((RelativeSizeSpan) span).getSizeChange();
                        final float viewSizeChange = Float.parseFloat(mTextViewRelativeSize.getTag().toString());
                        if (spanSizeChange == viewSizeChange) {
                            isNewSpanNeeded = false;
                            expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    }
                    ///字符span（带参数）：ScaleX
                    else if (clazz == ScaleXSpan.class) {
                        final float spanScaleX = ((ScaleXSpan) span).getScaleX();
                        final float viewScaleX = Float.parseFloat(mTextViewScaleX.getTag().toString());
                        if (spanScaleX == viewScaleX) {
                            isNewSpanNeeded = false;
                            expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }

                    } else {
                        isNewSpanNeeded = false;
                        expendSpan(editable, start, end, spanStart, spanEnd, span, true);
                    }
                } else {
                    isNewSpanNeeded = false;
                    if (expendSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                        editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        newCharacterStyleSpanByCompare(clazz, editable, end, spanEnd, span);
                    }
                }
            } else {
                if (isSelected || isRemoved) {
                    editable.removeSpan(span);
                } else if (spanStart == start) {
                    leftJoinSpan(clazz, editable, spanStart, spanEnd, span);
                } else if (spanEnd == end) {
                    rightJoinSpan(clazz, editable, spanStart, spanEnd, span);
                }
            }
        }

        ///如果changed内容需要新加span，且工具条选中，则添加新span
        if (isNewSpanNeeded && isSelected) {
            insertNewSpan(clazz, editable, start, end);
        }
    }
    private <T> void adjustParagraphStyleSpans(Class<T> clazz, Editable editable, int start, int end,
                                               int firstLineStart, int firstLineEnd, int lastLineStart, int lastLineEnd, boolean isSelected) {
        boolean isSet = false;
        final T[] startSpans = editable.getSpans(firstLineStart, firstLineEnd, clazz);
        for (T span : startSpans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (span.getClass() != clazz) {
                continue;
            }

            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);

            ///自定义段落span：LineDivider：移除无效的LineDivider
            if (clazz == LineDividerSpan.class && spanStart == firstLineStart && firstLineStart + 1 != firstLineEnd) {
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
                ///忽略getSpans()获取的子类（不是clazz本身）
                if (span.getClass() != clazz) {
                    continue;
                }

//                final int spanStart = editable.getSpanStart(span);
                final int spanEnd = editable.getSpanEnd(span);
                if (spanEnd != lastLineEnd) {
                    editable.setSpan(span, lastLineStart, lastLineEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }

    private <T> void newCharacterStyleSpanByCompare(Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        ///添加新span
        Object newSpan = null;

        ///字符span（带参数）：URL
        if (clazz == URLSpan.class) {
            String link;
            if (compareSpan == null) {
                link = (String) mImageViewURL.getTag();
            } else {
                link = ((URLSpan) compareSpan).getURL();
            }
            newSpan = new URLSpan(link);
        }
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == ForegroundColorSpan.class) {
            @ColorInt final int foregroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewForegroundColor.getBackground();
                foregroundColor = colorDrawable.getColor();
            } else {
                foregroundColor = ((ForegroundColorSpan) compareSpan).getForegroundColor();
            }
            newSpan = new ForegroundColorSpan(foregroundColor);
        } else if (clazz == BackgroundColorSpan.class) {
            @ColorInt final int backgroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewBackgroundColor.getBackground();
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
                family = (String) mTextViewTypefaceFamily.getTag();
            } else {
                family = ((TypefaceSpan) compareSpan).getFamily();
            }
            newSpan = new TypefaceSpan(family);
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            int size;
            if (compareSpan == null) {
                size = Integer.parseInt(mTextViewAbsoluteSize.getTag().toString());
            } else {
                size = ((AbsoluteSizeSpan) compareSpan).getSize();
            }
            newSpan = new AbsoluteSizeSpan(size);
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            float sizeChange;
            if (compareSpan == null) {
                sizeChange = Float.parseFloat(mTextViewRelativeSize.getTag().toString());
            } else {
                sizeChange = ((RelativeSizeSpan) compareSpan).getSizeChange();
            }
            newSpan = new RelativeSizeSpan(sizeChange);
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            float scaleX;
            if (compareSpan == null) {
                scaleX = Float.parseFloat(mTextViewScaleX.getTag().toString());
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
    private <T> void newParagraphStyleSpan(Class<T> clazz, Editable editable, int start, int end) {
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
            final String head = (String) mTextViewHead.getTag();
            newSpan = new HeadSpan(head);
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

    /**
     * 插入新span
     *
     * 如果左右有span且color相等，则延长，否则添加
     */
    private <T> void insertNewSpan(Class<T> clazz, Editable editable, int start, int end) {
        final T leftSpan = getLeftSpan(clazz, editable, start, null);
        if (leftSpan != null) {
            final int leftSpanStart = editable.getSpanStart(leftSpan);
            editable.setSpan(leftSpan, leftSpanStart, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            rightJoinSpan(clazz, editable, leftSpanStart, end, leftSpan);
            return;
        }
        final T rightSpan = getRightSpan(clazz, editable, end, null);
        if (rightSpan != null) {
            final int rightSpanEnd = editable.getSpanEnd(rightSpan);
            editable.setSpan(rightSpan, start, rightSpanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            leftJoinSpan(clazz, editable, start, rightSpanEnd, rightSpan);
            return;
        }

        newCharacterStyleSpanByCompare(clazz, editable, start, end, null);
    }

    /**
     * 获得左边的同类span
     */
    private <T> T getLeftSpan(Class<T> clazz, Editable editable, int start, T compareSpan) {
        final T[] leftSpans = editable.getSpans(start, start, clazz);
        for (T leftSpan : leftSpans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (leftSpan.getClass() != clazz) {
                continue;
            }

            final int leftSpanEnd = editable.getSpanEnd(leftSpan);
            if (leftSpanEnd != start) {
                continue;
            }
            return getSpanByCompareSpanParameter(clazz, leftSpan, compareSpan);
        }
        return null;
    }

    /**
     * 获得右边的同类span
     */
    private <T> T getRightSpan(Class<T> clazz, Editable editable, int end, T compareSpan) {
        final T[] rightSpans = editable.getSpans(end, end, clazz);
        for (T rightSpan : rightSpans) {
            ///忽略getSpans()获取的子类（不是clazz本身）
            if (rightSpan.getClass() != clazz) {
                continue;
            }

            final int rightSpanStart = editable.getSpanStart(rightSpan);
            if (rightSpanStart != end) {
                continue;
            }
            return getSpanByCompareSpanParameter(clazz, rightSpan, compareSpan);
        }
        return null;
    }

    private <T> T getSpanByCompareSpanParameter(Class<T> clazz, T span, T compareSpan) {
        ///字符span（带参数）：URL
        if (clazz == URLSpan.class) {
            final String spanLink = ((URLSpan) span).getURL();
            if (compareSpan == null) {
                final String viewLink = (String) mImageViewURL.getTag();
                return spanLink.equals(viewLink) ? span : null;
            } else {
                final String compareSpanLink = ((URLSpan) compareSpan).getURL();
                return spanLink.equals(compareSpanLink) ? span : null;
            }
        }
        ///字符span（带参数）：ForegroundColor、BackgroundColor
        else if (clazz == ForegroundColorSpan.class) {
            @ColorInt final int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewForegroundColor.getBackground();
                return foregroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt final int compareSpanForegroundColor = ((ForegroundColorSpan) compareSpan).getForegroundColor();
                return foregroundColor == compareSpanForegroundColor ? span : null;
            }
        } else if (clazz == BackgroundColorSpan.class) {
            @ColorInt final int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewBackgroundColor.getBackground();
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
                final String viewFamily = (String) mTextViewTypefaceFamily.getTag();
                return spanFamily.equals(viewFamily) ? span : null;
            } else {
                final String compareSpanLink = ((TypefaceSpan) compareSpan).getFamily();
                return spanFamily.equals(compareSpanLink) ? span : null;
            }
        }
        ///字符span（带参数）：AbsoluteSize
        else if (clazz == AbsoluteSizeSpan.class) {
            final int  spanSize = ((AbsoluteSizeSpan) span).getSize();
            if (compareSpan == null) {
                final int viewSize = Integer.parseInt(mTextViewAbsoluteSize.getTag().toString());
                return spanSize == viewSize ? span : null;
            } else {
                final int compareSpanSize = ((AbsoluteSizeSpan) compareSpan).getSize();
                return spanSize == compareSpanSize ? span : null;
            }
        }
        ///字符span（带参数）：RelativeSize
        else if (clazz == RelativeSizeSpan.class) {
            final float  spanSizeChange = ((RelativeSizeSpan) span).getSizeChange();
            if (compareSpan == null) {
                final float viewSizeChange = Float.parseFloat(mTextViewRelativeSize.getTag().toString());
                return spanSizeChange == viewSizeChange ? span : null;
            } else {
                final float compareSpanSizeChange = ((RelativeSizeSpan) compareSpan).getSizeChange();
                return spanSizeChange == compareSpanSizeChange ? span : null;
            }
        }
        ///字符span（带参数）：ScaleX
        else if (clazz == ScaleXSpan.class) {
            final float  spanScaleX = ((ScaleXSpan) span).getScaleX();
            if (compareSpan == null) {
                final float viewScaleX = Float.parseFloat(mTextViewScaleX.getTag().toString());
                return spanScaleX == viewScaleX ? span : null;
            } else {
                final float compareSpanScaleX = ((ScaleXSpan) compareSpan).getScaleX();
                return spanScaleX == compareSpanScaleX ? span : null;
            }
        }

        return span;
    }

    /**
     * 左联合并同类span
     */
    private <T> int leftJoinSpan(Class<T> clazz, Editable editable, int start, int end, T span) {
        int resultStart = start;
        final T leftSpan = getLeftSpan(clazz, editable, start, span);
        if (leftSpan != null) {
            resultStart = editable.getSpanStart(leftSpan);
            editable.setSpan(span, resultStart, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            editable.removeSpan(leftSpan);
        }
        return resultStart;
    }

    /**
     * 右联合并同类span
     */
    private <T> int rightJoinSpan(Class<T> clazz, Editable editable, int start, int end, T span) {
        int resultEnd = end;
        final T rightSpan = getRightSpan(clazz, editable, end, span);
        if (rightSpan != null) {
            resultEnd = editable.getSpanEnd(rightSpan);
            editable.setSpan(span, start, resultEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            editable.removeSpan(rightSpan);
        }
        return resultEnd;
    }

    private <T> boolean expendSpan(Editable editable, int start, int end, int spanStart, int spanEnd, T span, boolean isExtended) {
        if (spanStart >= start) {
            editable.setSpan(span, isExtended ? start : end, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else if (spanEnd <= end) {
            editable.setSpan(span, spanStart, isExtended ? end : start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            return true;
        }
        return false;
    }

}
