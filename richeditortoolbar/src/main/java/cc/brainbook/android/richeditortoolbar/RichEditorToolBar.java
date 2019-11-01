package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;

import cc.brainbook.android.colorpicker.builder.ColorPickerClickListener;
import cc.brainbook.android.colorpicker.builder.ColorPickerDialogBuilder;

public class RichEditorToolBar extends FlexboxLayout implements View.OnClickListener, RichEditText.OnSelectionChanged {
    private HashMap<View, Class> mTypeMap = new HashMap<>();
    private RichEditText mRichEditText;

    public RichEditorToolBar(Context context) {
        super(context);
        init(context);
    }

    public RichEditorToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RichEditorToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setEditText(RichEditText richEditText) {
        mRichEditText = richEditText;
        mRichEditText.addTextChangedListener(mRichEditorWatcher);
        mRichEditText.setOnSelectionChanged(this);
    }

    ///原生span：Underline、StrikeThrough、Subscript、Superscript
    private ImageView mImageViewUnderline;
    private ImageView mImageViewStrikeThrough;
    private ImageView mImageViewSubscript;
    private ImageView mImageViewSuperscript;

    ///原生span：ForegroundColor、BackgroundColor
    private ImageView mImageViewForegroundColor;
    private ImageView mImageViewBackgroundColor;

    ///////////////////////////////////////////////////////////
    private ImageView mImageViewBold;
    private ImageView mImageViewItalic;
    private ImageView mImageViewCode;
    private ImageView mImageViewQuote;


    private void init(Context context) {
        setFlexDirection(FlexDirection.ROW);
        setFlexWrap(FlexWrap.WRAP);

        LayoutInflater.from(context).inflate(R.layout.layout_tool_bar, this, true);


        /* ------------ 原生span：Underline、StrikeThrough、Subscript、Superscript ------------ */
        mImageViewUnderline = (ImageView) findViewById(R.id.iv_underline);
        mImageViewUnderline.setOnClickListener(this);
        mTypeMap.put(mImageViewUnderline, UnderlineSpan.class);

        mImageViewStrikeThrough = (ImageView) findViewById(R.id.iv_strikethrough);
        mImageViewStrikeThrough.setOnClickListener(this);
        mTypeMap.put(mImageViewStrikeThrough, StrikethroughSpan.class);

        mImageViewSuperscript = (ImageView) findViewById(R.id.iv_superscript);
        mImageViewSuperscript.setOnClickListener(this);
        mTypeMap.put(mImageViewSuperscript, SuperscriptSpan.class);

        mImageViewSubscript = (ImageView) findViewById(R.id.iv_subscript);
        mImageViewSubscript.setOnClickListener(this);
        mTypeMap.put(mImageViewSubscript, SubscriptSpan.class);


        /* -------------- 原生span：ForegroundColor、BackgroundColor --------------- */
        mImageViewForegroundColor = (ImageView) findViewById(R.id.iv_foreground_color);
        mImageViewForegroundColor.setOnClickListener(this);
        mTypeMap.put(mImageViewForegroundColor, ForegroundColorSpan.class);

        mImageViewBackgroundColor = (ImageView) findViewById(R.id.iv_background_color);
        mImageViewBackgroundColor.setOnClickListener(this);
        mTypeMap.put(mImageViewBackgroundColor, BackgroundColorSpan.class);


//        //////////////////////////////////////////////////////////////////////////
//        mImageViewBold = (ImageView) findViewById(R.id.iv_bold);
//        mImageViewBold.setOnClickListener(this);
//        mTypeMap.put(mImageViewBold, BoldSpan.class);
//
//        mImageViewItalic = (ImageView) findViewById(R.id.iv_italic);
//        mImageViewItalic.setOnClickListener(this);
//        mTypeMap.put(mImageViewItalic, ItalicSpan.class);
//
//        mImageViewCode = (ImageView) findViewById(R.id.iv_code);
//        mImageViewCode.setOnClickListener(this);
////        mTypeMap.put(mImageViewCode, CodeSpan.class);
//
//        mImageViewQuote = (ImageView) findViewById(R.id.iv_quote);
//        mImageViewQuote.setOnClickListener(this);
////        mTypeMap.put(mImageViewQuote, QuoteSpan.class);

    }


    /* ----------------- ///[selectionChanged]根据selection更新ImageView ------------------ */
    @Override
    public void selectionChanged(int selStart, int selEnd) {
        Log.d("TAG", "============= selectionChanged ============");
        for (View view : mTypeMap.keySet()) {
            updateView(mTypeMap.get(view), mRichEditText.getText(), selStart, selEnd, view);

            ///test
            testOutput(mTypeMap.get(view), mRichEditText.getText());
        }
    }

    ///更新ImageView
    private <T> void updateView(Class<T> clazz, Editable editable, int selStart, int selEnd, View view) {
        final T[] spans = editable.getSpans(selStart, selEnd, clazz);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
//            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart < selStart || spanStart == selStart && spanStart < selEnd) {
                view.setSelected(true);

                ///原生span：ForegroundColor、BackgroundColor
                if (clazz == ForegroundColorSpan.class) {
                    @ColorInt int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
                    view.setBackgroundColor(foregroundColor);
                } else if (clazz == BackgroundColorSpan.class) {
                    @ColorInt int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
                    view.setBackgroundColor(backgroundColor);
                }

                return;
            }
        }

        view.setSelected(false);

        ///原生span：ForegroundColor、BackgroundColor
        if (clazz == ForegroundColorSpan.class || clazz == BackgroundColorSpan.class) {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    /* ----------------- ///[onClick]点击更新ImageView，并且当selectionStart != selectionEnd时改变selection的span ------------------ */
    @Override
    public void onClick(final View view) {
        ///原生span：ForegroundColor、BackgroundColor
        if (view.getId() == R.id.iv_foreground_color || view.getId() == R.id.iv_background_color) {
            final ColorPickerDialogBuilder colorPickerDialogBuilder = ColorPickerDialogBuilder
                    .with(view.getContext())
                    .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            view.setBackgroundColor(selectedColor);
                            view.setSelected(true);
                            applySpansSelection(mTypeMap.get(view), mRichEditText.getText(), view.isSelected());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.setBackgroundColor(Color.TRANSPARENT);
                            view.setSelected(false);
                            applySpansSelection(mTypeMap.get(view), mRichEditText.getText(), view.isSelected());
                        }
                    });
            if (view.isSelected()) {
                final ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();
                colorPickerDialogBuilder.initialColor(colorDrawable.getColor());
            }
            colorPickerDialogBuilder.build().show();

            return;
        }

        ///原生span：Underline、StrikeThrough、Subscript、Superscript
        view.setSelected(!view.isSelected());
        applySpansSelection(mTypeMap.get(view), mRichEditText.getText(), view.isSelected());
    }

    ///当selectionStart != selectionEnd时改变selection的span
    private <T> void applySpansSelection(Class<T> clazz, Editable editable, boolean isSelected) {
        final int selectionStart = mRichEditText.getSelectionStart();
        final int selectionEnd = mRichEditText.getSelectionEnd();
        if (selectionStart == selectionEnd) {
            return;
        }

        adjustSpans(clazz, editable, selectionStart, selectionEnd, isSelected, true);
    }


    /* ----------------- ///[TextWatcher] ------------------ */
    private TextWatcher mRichEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (count > 0) {    ///[TextWatcher#删除]
                for (Class clazz : mTypeMap.values()) {
                    removeSpanBeforeTextChanged(clazz, mRichEditText.getText(), start, start + count);
                }
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {   ///[TextWatcher#添加]
                for (View view : mTypeMap.keySet()) {

                    Log.d("TAG", "onTextChanged: ------------------start");
                    flatSpans(mTypeMap.get(view), mRichEditText.getText(), start, start + count);
                    Log.d("TAG", "onTextChanged: ------------------end");

                    adjustSpans(mTypeMap.get(view), mRichEditText.getText(), start, start + count, view.isSelected(), false);
                }
            } else if (before > 0) {
                for (View view : mTypeMap.keySet()) {
                    joinSpans(mTypeMap.get(view), mRichEditText.getText(),  start);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private <T> void flatSpans(Class<T> clazz, Editable editable, int start, int end) {
        T currentSpan = null;
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
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
            if (currentSpanStart <= spanStart && currentSpanEnd >= spanEnd) {
                editable.removeSpan(span);
            } else if (currentSpanStart >= spanStart && currentSpanEnd <= spanEnd) {
                editable.removeSpan(currentSpan);
                currentSpan = span;
            } else if (currentSpanStart <= spanStart) {
                editable.setSpan(currentSpan, currentSpanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                editable.removeSpan(span);
            } else {    ///if (currentSpanStart >= spanStart)
                editable.setSpan(currentSpan, spanStart, currentSpanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                editable.removeSpan(span);
            }
        }
    }

    ///注意：nextSpanTransition与getSpans效果差不多！
    private <T> void flatSpans2(Class<T> clazz, Editable editable, int start, int end) {
        T currentSpan = null;
        int next;
        for (int i = start; i < end; i = next) {
            next = editable.nextSpanTransition(i, end, clazz);
            if (next >= end) {
                continue;
            }
            final T[] spans = editable.getSpans(i, next, clazz);
            for (T span : spans) {
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
                if (currentSpanStart <= spanStart && currentSpanEnd >= spanEnd) {
                    editable.removeSpan(span);
                } else if (currentSpanStart >= spanStart && currentSpanEnd <= spanEnd) {
                    editable.removeSpan(currentSpan);
                    currentSpan = span;
                } else if (currentSpanStart <= spanStart) {
                    editable.setSpan(currentSpan, currentSpanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    editable.removeSpan(span);
                } else {    ///if (currentSpanStart >= spanStart)
                    editable.setSpan(currentSpan, spanStart, currentSpanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    editable.removeSpan(span);
                }
            }
        }
    }

    ///[TextWatcher#删除]
    private <T> void removeSpanBeforeTextChanged(Class<T> clazz, Editable editable, int start, int end) {
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart >= start && spanEnd <= end) {
                editable.removeSpan(span);
            }
        }
    }

    private <T> void joinSpans(Class<T> clazz, Editable editable, int position) {
        final T[] spans = editable.getSpans(position, position, clazz);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanEnd == position) {
                rightJoinSpan(clazz, editable, spanStart, spanEnd, span);
            }
        }
    }

    ///注意：如果Spanned.SPAN_INCLUSIVE_INCLUSIVE，则自动添加span
    ///注意：新添加的内容除了可为纯文字（无span），也可以为富文本（有span，但必须flatSpans处理过！）
    private <T> void adjustSpans(Class<T> clazz, Editable editable, int start, int end, boolean isSelected, boolean isRemoved) {
        boolean isNewSpanNeeded = true;  ///changed内容是否需要新加span
        final T[] spans = editable.getSpans(start, end, clazz);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            if (spanStart < start || spanEnd > end) {
                if (isSelected) {
                    if (clazz == ForegroundColorSpan.class) {
                        @ColorInt int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
                        final ColorDrawable colorDrawable = (ColorDrawable) mImageViewForegroundColor.getBackground();
                        if (foregroundColor == colorDrawable.getColor()) {
                            isNewSpanNeeded = false;
                            innerAdjustSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (innerAdjustSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    } else if (clazz == BackgroundColorSpan.class) {
                        @ColorInt int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
                        final ColorDrawable colorDrawable = (ColorDrawable) mImageViewBackgroundColor.getBackground();
                        if (backgroundColor == colorDrawable.getColor()) {
                            isNewSpanNeeded = false;
                            innerAdjustSpan(editable, start, end, spanStart, spanEnd, span, true);
                        } else {
                            isNewSpanNeeded = true;
                            if (innerAdjustSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                                editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                newSpanByCompare(clazz, editable, end, spanEnd, span);
                            }
                        }
                    } else {
                        isNewSpanNeeded = false;
                        innerAdjustSpan(editable, start, end, spanStart, spanEnd, span, true);
                    }
                } else {
                    isNewSpanNeeded = false;
                    if (innerAdjustSpan(editable, start, end, spanStart, spanEnd, span, false)) {
                        editable.setSpan(span, spanStart, start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        newSpanByCompare(clazz, editable, end, spanEnd, span);
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

    private <T> boolean innerAdjustSpan(Editable editable, int start, int end, int spanStart, int spanEnd, T span, boolean isExtended) {
        if (spanStart >= start) {
            editable.setSpan(span, isExtended ? start : end, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else if (spanEnd <= end) {
            editable.setSpan(span, spanStart, isExtended ? end : start, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            return true;
        }
        return false;
    }

    private <T> void newSpanByCompare(Class<T> clazz, Editable editable, int start, int end, T compareSpan) {
        ///添加新span
        Object newSpan = null;
        if (clazz == ForegroundColorSpan.class) {
            @ColorInt int foregroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewForegroundColor.getBackground();
                foregroundColor = colorDrawable.getColor();
            } else {
                foregroundColor = ((ForegroundColorSpan) compareSpan).getForegroundColor();
            }
            newSpan = new ForegroundColorSpan(foregroundColor);
        } else if (clazz == BackgroundColorSpan.class) {
            @ColorInt int backgroundColor;
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewBackgroundColor.getBackground();
                backgroundColor = colorDrawable.getColor();
            } else {
                backgroundColor = ((BackgroundColorSpan) compareSpan).getBackgroundColor();
            }
            newSpan = new BackgroundColorSpan(backgroundColor);
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

    ///如果左右有span且color相等，则延长，否则添加
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

        newSpanByCompare(clazz, editable, start, end, null);
    }

    private <T> T getLeftSpan(Class<T> clazz, Editable editable, int start, T compareSpan) {
        final T[] leftSpans = editable.getSpans(start, start, clazz);
        for (T leftSpan : leftSpans) {
            final int leftSpanEnd = editable.getSpanEnd(leftSpan);
            if (leftSpanEnd != start) {
                continue;
            }
            return getSpanByCompare(clazz, leftSpan, compareSpan);
        }
        return null;
    }

    private <T> T getRightSpan(Class<T> clazz, Editable editable, int end, T compareSpan) {
        final T[] rightSpans = editable.getSpans(end, end, clazz);
        for (T rightSpan : rightSpans) {
            final int rightSpanStart = editable.getSpanStart(rightSpan);
            if (rightSpanStart != end) {
                continue;
            }
            return getSpanByCompare(clazz, rightSpan, compareSpan);
        }
        return null;
    }

    private <T> T getSpanByCompare(Class<T> clazz, T span, T compareSpan) {
        if (clazz == ForegroundColorSpan.class) {
            @ColorInt int foregroundColor = ((ForegroundColorSpan) span).getForegroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewForegroundColor.getBackground();
                return foregroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt int compareSpanForegroundColor = ((ForegroundColorSpan) compareSpan).getForegroundColor();
                return foregroundColor == compareSpanForegroundColor ? span : null;
            }
        } else if (clazz == BackgroundColorSpan.class) {
            @ColorInt int backgroundColor = ((BackgroundColorSpan) span).getBackgroundColor();
            if (compareSpan == null) {
                final ColorDrawable colorDrawable = (ColorDrawable) mImageViewBackgroundColor.getBackground();
                return backgroundColor == colorDrawable.getColor() ? span : null;
            } else {
                @ColorInt int compareSpanBackgroundColor = ((BackgroundColorSpan) compareSpan).getBackgroundColor();
                return backgroundColor == compareSpanBackgroundColor ? span : null;
            }
        }
        return span;
    }

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

    ///test
    private <T> void testOutput(Class<T> clazz, Editable editable) {
        final T[] spans = editable.getSpans(0, editable.length(), clazz);
        for (T span : spans) {
            final int spanStart = editable.getSpanStart(span);
            final int spanEnd = editable.getSpanEnd(span);
            Log.d("TAG", span.getClass().getSimpleName() + ": " + spanStart + ", " + spanEnd);
        }
    }

}
