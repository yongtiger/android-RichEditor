package cc.brainbook.android.richeditortoolbar.helper;

import android.text.Editable;

import java.util.ArrayList;

import cc.brainbook.android.richeditortoolbar.span.ListItemSpan;
import cc.brainbook.android.richeditortoolbar.span.ListSpan;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;

import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.getSpanFlags;

///Unicode表：http://www.tamasoft.co.jp/en/general-info/unicode.html
///Unicode在线转换工具：http://tool.chinaz.com/tools/unicode.aspx, http://www.jsons.cn/unicode/
///https://html.spec.whatwg.org/multipage/grouping-content.html#attr-ol-type
public class ListSpanHelper {
    public static final int LIST_TYPE_UNORDERED_NONE = 0;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_NONE = "";
    public static final int LIST_TYPE_UNORDERED_DISC = 1;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_DISC = "\u25cf";
    public static final int LIST_TYPE_UNORDERED_CIRCLE = 2;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE = "\u25cb";
    public static final int LIST_TYPE_UNORDERED_SQUARE = 3;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_SQUARE = "\u25a0";
    /// others ...
    public static final int LIST_TYPE_ORDERED_DECIMAL = -1;  ///无限
    public static final int LIST_TYPE_ORDERED_LOWER_LATIN = -2;  ///最大26
    public static final int LIST_TYPE_ORDERED_UPPER_LATIN = -3;  ///最大26
    public static final int LIST_TYPE_ORDERED_LOWER_ROMAN = -4;    ///最大3999
    public static final int LIST_TYPE_ORDERED_UPPER_ROMAN = -5;    ///最大3999
    /// others ...


    public static boolean isListTypeOrdered(int listType) {
        return listType < 0 && listType != Integer.MIN_VALUE;
    }

    public static String getIndicatorText(int listType, int orderIndex) {
        switch (listType) {
            case LIST_TYPE_UNORDERED_DISC:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_DISC;
            case LIST_TYPE_UNORDERED_CIRCLE:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE;
            case LIST_TYPE_UNORDERED_SQUARE:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_SQUARE;

            case LIST_TYPE_ORDERED_DECIMAL:
                return orderIndex + ".";
            case LIST_TYPE_ORDERED_LOWER_LATIN:
                return getLetterIndicatorTextByIndex(orderIndex) + ".";
            case LIST_TYPE_ORDERED_UPPER_LATIN:
                return getLetterIndicatorTextByIndex(orderIndex).toUpperCase() + ".";
            case LIST_TYPE_ORDERED_LOWER_ROMAN:
                return getRomanLetterIndicatorTextByIndex(orderIndex).toLowerCase() + ".";
            case LIST_TYPE_ORDERED_UPPER_ROMAN:
                return getRomanLetterIndicatorTextByIndex(orderIndex) + ".";

            default:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_NONE;
        }
    }


    /* ----------------------- ///[OrderIndex] ----------------------- */
    ///https://blog.csdn.net/u013072976/article/details/51698196
    public static final String[] letterArray = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

    public static String getLetterIndicatorTextByIndex(int index) {

        String result;
        result = switchNumToLetter(index);
        return result;
    }

    private static String switchNumToLetter(int index) {
        if (index <= 0) {
            return "";
        }
        String out;
        int count = 26;
        if (index > 0 && index <= count) {
            out = letterArray[index - 1];
            return out;
        } else {
            if (index % count == 0) {
                out = switchNumToLetter(index / count - 1) + letterArray[count - 1];
            } else {
                out = switchNumToLetter(index / count) + switchNumToLetter(index - (index / count) * count);
            }
        }
        return out;
    }

    public static String getRomanLetterIndicatorTextByIndex(int index) {
        String result;
        result = intToRoman(index);
        return result;
    }

    /**
     * @param s
     *            - String Roman
     * @return int number
     */
    private static int romanToInt(String s) {
        if (s.length() < 1)
            return 0;
        int result = 0;
        int current = 0;
        int pre = singleRomanToInt(s.charAt(0));
        int temp = pre;
        for (int i = 1; i < s.length(); i++) {
            current = singleRomanToInt(s.charAt(i));
            if (current == pre)
                temp += current;
            else if (current > pre) {
                temp = current - temp;
            } else if (current < pre) {
                result += temp;
                temp = current;
            }
            pre = current;
        }
        result += temp;
        return result;
    }

    /**
     * @param c
     *            single Roman
     * @return single number
     */
    private static int singleRomanToInt(char c) {
        switch (c) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                return 0;
        }
    }

    /**
     * @param n
     *            - input single int
     * @param nth
     *            must start from 1; 1 <= nth <= 4
     * @return String single Roman
     */
    private static String singleDigitToRoman(int n, int nth) {
        if (n == 0) {
            return "";
        }
        nth = 2 * nth - 1; // nth must start from 1
        char singleRoman[] = { 'I', 'V', 'X', 'L', 'C', 'D', 'M', 'Z', 'E' }; // never
        // use
        // 'Z'
        // &
        // 'E'
        StringBuilder rsb = new StringBuilder("");
        if (n <= 3) {
            for (int i = 0; i < n; i++) {
                rsb.append(singleRoman[nth - 1]);
            }
            return rsb.toString();
        }
        if (n == 4) {
            rsb.append(singleRoman[nth - 1]);
            rsb.append(singleRoman[nth]);
            return rsb.toString();
        }
        if (n == 5) {
            return singleRoman[nth] + "";
        }
        if (n >= 6 && n <= 8) {
            rsb.append(singleRoman[nth]);
            for (int i = 0; i < (n - 5); i++) {
                rsb.append(singleRoman[nth - 1]);
            }
            return rsb.toString();
        }
        if (n == 9) {
            rsb.append(singleRoman[nth - 1]);
            rsb.append(singleRoman[nth + 1]);
            return rsb.toString();
        }
        return "ERROR!!!";
    }

    /**
     * @param num
     *            - input number within range 1 ~ 3999
     * @return String Roman number
     */
    private static String intToRoman(int num) {
        if (num < 1 || num > 3999) {
            return "";
        }
        int temp = num;
        String singleRoman[] = { "", "", "", "" };
        StringBuilder result = new StringBuilder();
        int digits = 0; // 1 ~ 4
        while (temp != 0) {
            temp = temp / 10;
            digits++;
        }
        temp = num;
        int[] singleInt = new int[digits];
        for (int i = 0; i < digits; i++) {
            singleInt[i] = temp % 10;
            singleRoman[i] = singleDigitToRoman(temp % 10, i + 1);
            temp /= 10;
        }
        for (int i = digits - 1; i >= 0; i--) {
            result.append(singleRoman[i]);
        }
        return result.toString();
    }


    /**
     * 创建ListSpan包含的儿子一级ListItemSpans
     *
     * 注意：只children！
     */
    public static void createChildrenListItemSpans(Editable editable, ListSpan listSpan, int start, int end,
                                                   int indicatorWidth,
                                                   int indicatorGapWidth,
                                                   int indicatorColor,
                                                   boolean wantColor) {
        int index = listSpan.getStart();

        int next;
        for (int i = start; i < end; i = next) {
            final int currentParagraphStart = SpanUtil.getParagraphStart(editable, i);
            final int currentParagraphEnd = SpanUtil.getParagraphEnd(editable, i);
            next = currentParagraphEnd;

            final ListItemSpan newListItemSpan = new ListItemSpan(listSpan, index,
                    indicatorWidth, indicatorGapWidth, indicatorColor, wantColor);

            editable.setSpan(newListItemSpan, currentParagraphStart, currentParagraphEnd, getSpanFlags(ListItemSpan.class));

            if (listSpan.isReversed()) {
                index--;
            } else {
                index++;
            }
        }
    }

    /**
     * 更新ListSpan
     */
    public static void updateListSpans(Editable editable, ArrayList<ListSpan> listSpans) {
        for (ListSpan listSpan : listSpans) {
            if (listSpan != null) {
                ///更新ListSpan包含的儿子一级ListItemSpans（注意：只children！）
                final int listSpanStart = editable.getSpanStart(listSpan);
                final int listSpanEnd = editable.getSpanEnd(listSpan);

                updateChildrenListItemSpans(editable, listSpan, listSpanStart, listSpanEnd);
            }
        }
    }

    /**
     * 更新ListSpan包含的儿子一级ListItemSpans
     *
     * 注意：只children！
     */
    public static void updateChildrenListItemSpans(Editable editable, ListSpan listSpan, int start, int end) {
        int index = listSpan.getStart();

        final ArrayList<ListItemSpan> spans = SpanUtil.getFilteredSpans(ListItemSpan.class, editable, start, end, true);
        for (ListItemSpan span : spans) {
            if (span.getListSpan() == listSpan) {
                final int spanStart = editable.getSpanStart(span);
                final int spanEnd = editable.getSpanEnd(span);

                ///调整span的起止位置（删除含有'\n'的文本时会造成一行中存在多个不完整的段落span！需要调整）
                int st = spanStart, en = spanEnd;
                if (spanStart < 0 && editable.charAt(spanStart - 1) != '\n') {
                    ///如果span的起始位置不正确，则左缩（即设置spanStart为其所在行的行尾）
                    st = SpanUtil.getParagraphEnd(editable, spanStart);
                }
                if (spanEnd < editable.length() && editable.charAt(spanEnd - 1) != '\n') {
                    en = start == end ? SpanUtil.getParagraphEnd(editable, spanEnd) : SpanUtil.getParagraphEnd(editable, spanEnd - 1);
                }
                if (st == en) {
                    editable.removeSpan(span);

                    continue;
                }

                ///设置index
                span.setIndex(index);

                ///注意：必须重新setSpan，否则不会自动更新绘制！
                editable.setSpan(span, st, en, getSpanFlags(ListItemSpan.class));

                if (listSpan.isReversed()) {
                    index--;
                } else {
                    index++;
                }
            } else if(span.getNestingLevel() == listSpan.getNestingLevel()) {
                span.setListSpan(listSpan);

                ///设置index
                span.setIndex(index);

                if (listSpan.isReversed()) {
                    index--;
                } else {
                    index++;
                }
            }
        }
    }

    /**
     * 移除ListSpan包含的儿子一级ListItemSpans
     *
     * 注意：只children！
     */
    public static void removeChildrenListItemSpans(Editable editable, ListSpan listSpan, int start, int end) {
        final ListItemSpan[] listItemSpans = editable.getSpans(start, end, ListItemSpan.class);
        for (ListItemSpan listItemSpan : listItemSpans) {
            if (listItemSpan.getListSpan() == listSpan) {
                editable.removeSpan(listItemSpan);
            }
        }
    }

}
