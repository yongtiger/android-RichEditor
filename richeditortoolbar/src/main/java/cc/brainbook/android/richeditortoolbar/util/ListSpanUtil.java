package cc.brainbook.android.richeditortoolbar.util;

///Unicode表：http://www.tamasoft.co.jp/en/general-info/unicode.html
///Unicode在线转换工具：http://tool.chinaz.com/tools/unicode.aspx, http://www.jsons.cn/unicode/
public class ListSpanUtil {
    public static final int LIST_TYPE_UNORDERED_EMPTY = 0;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_EMPTY = "";
    public static final int LIST_TYPE_UNORDERED_CIRCLE = 1;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE = "\u25cf";
    public static final int LIST_TYPE_UNORDERED_SQUARE = 2;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_SQUARE = "\u25a0";
    public static final int LIST_TYPE_UNORDERED_CIRCLE_HOLLOW = 11;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE_HOLLOW = "\u25cb";
    public static final int LIST_TYPE_UNORDERED_SQUARE_HOLLOW = 12;
    public static final String INDICATOR_TEXT_LIST_TYPE_UNORDERED_SQUARE_HOLLOW = "\u25a1";
    /// others ...
    public static final int LIST_TYPE_ORDERED_LETTER = -1;  ///最大26
    public static final int LIST_TYPE_ORDERED_NUMBER = -2;  ///无限
    public static final int LIST_TYPE_ORDERED_ROMAN_NUMBER = -3;    ///最大3999
    public static final int LIST_TYPE_ORDERED_LETTER_QUOTE = -11;
    public static final int LIST_TYPE_ORDERED_NUMBER_QUOTE = -12;
    public static final int LIST_TYPE_ORDERED_ROMAN_NUMBER_QUOTE = -13;
    /// others ...


    public static boolean isListTypeOrdered(int listType) {
        return listType < 0;
    }

    public static String getIndicatorText(int listType, int orderIndex) {
        switch (listType) {
            case LIST_TYPE_UNORDERED_CIRCLE:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE;
            case LIST_TYPE_UNORDERED_SQUARE:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_SQUARE;
            case LIST_TYPE_UNORDERED_CIRCLE_HOLLOW:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_CIRCLE_HOLLOW;
            case LIST_TYPE_UNORDERED_SQUARE_HOLLOW:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_SQUARE_HOLLOW;

            case LIST_TYPE_ORDERED_LETTER:
                return getLetterIndicatorTextByIndex(orderIndex) + ".";
            case LIST_TYPE_ORDERED_NUMBER:
                return orderIndex + ".";
            case LIST_TYPE_ORDERED_ROMAN_NUMBER:
                return getRomanLetterIndicatorTextByIndex(orderIndex) + ".";
            case LIST_TYPE_ORDERED_LETTER_QUOTE:
                return "(" + getLetterIndicatorTextByIndex(orderIndex) + ")";
            case LIST_TYPE_ORDERED_NUMBER_QUOTE:
                return "(" + orderIndex + ")";
            case LIST_TYPE_ORDERED_ROMAN_NUMBER_QUOTE:
                return "(" + getRomanLetterIndicatorTextByIndex(orderIndex) + ")";
            default:
                return INDICATOR_TEXT_LIST_TYPE_UNORDERED_EMPTY;
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

}
