package cc.brainbook.android.richeditor;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.Editable;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;

import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FromHtmlTest {
    private Editable editable;

    private RichEditorToolbar mRichEditorToolbar;
    private LinkedHashMap<Class, View> mClassMap;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Before
    public void beforeTest() {
        mRichEditorToolbar = mActivityRule.getActivity().findViewById(R.id.rich_editor_tool_bar);

        mClassMap = mRichEditorToolbar.getClassMap();

//        ///[UPGRADE#android.text.Html]px in CSS is the equivalance of dip in Android
//        ///注意：一般情况下，CustomAbsoluteSizeSpan的dip都为true，否则需要在使用Html之前设置本机的具体准确的屏幕密度！
//        Context context = mActivityRule.getActivity();
//        Html.sDisplayMetricsDensity = context.getResources().getDisplayMetrics().density;
    }

    private void check(String srcString, String expectJsonString) {
        editable = (Editable) Html.fromHtml(srcString);
        String jsonString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        assertEquals(expectJsonString, jsonString);
    }


    @Test
    public void testEmpty() {
        check("",
                "{\"spans\":[],\"text\":\"\"}");

        ///忽略空格和'\n'
        check(" ",
                "{\"spans\":[],\"text\":\"\"}");
        check("  ",
                "{\"spans\":[],\"text\":\"\"}");
        check("\n",
                "{\"spans\":[],\"text\":\"\"}");
        check("\n\n",
                "{\"spans\":[],\"text\":\"\"}");
        check("\n \n",
                "{\"spans\":[],\"text\":\"\"}");
        check("\n  \n",
                "{\"spans\":[],\"text\":\"\"}");
        check("\n\n  \n\n",
                "{\"spans\":[],\"text\":\"\"}");
    }

    @Test
    public void testContent() {
        check("a",
                "{\"spans\":[],\"text\":\"a\"}");

        ///文字间的多个连续空格转换为一个空格
        check("a ",
                "{\"spans\":[],\"text\":\"a\"}");
        check("a  ",
                "{\"spans\":[],\"text\":\"a\"}");
        check(" a",
                "{\"spans\":[],\"text\":\"a\"}");
        check("  a",
                "{\"spans\":[],\"text\":\"a\"}");
        check("a a",
                "{\"spans\":[],\"text\":\"a a\"}");
        check("a  a",
                "{\"spans\":[],\"text\":\"a a\"}");

        ///忽略'\n'
        check("a\n",
                "{\"spans\":[],\"text\":\"a\"}");
        check("a\n\n",
                "{\"spans\":[],\"text\":\"a\"}");
        check("a\n \n",
                "{\"spans\":[],\"text\":\"a\"}");
        check("a\n  \n",
                "{\"spans\":[],\"text\":\"a\"}");
        check("a\n\n  \n\n",
                "{\"spans\":[],\"text\":\"a\"}");
        check("\na",
                "{\"spans\":[],\"text\":\"a\"}");
        check("\n\na",
                "{\"spans\":[],\"text\":\"a\"}");
        check("\n \na",
                "{\"spans\":[],\"text\":\"a\"}");
        check("\n  \na",
                "{\"spans\":[],\"text\":\"a\"}");
        check("\n\n  \n\na",
                "{\"spans\":[],\"text\":\"a\"}");
        check("a\na",
                "{\"spans\":[],\"text\":\"aa\"}");
        check("a\n\na",
                "{\"spans\":[],\"text\":\"aa\"}");
        check("a\n \na",
                "{\"spans\":[],\"text\":\"a a\"}");
        check("a\n  \na",
                "{\"spans\":[],\"text\":\"a a\"}");
        check("a\n\n  \n\na",
                "{\"spans\":[],\"text\":\"a a\"}");
    }

    @Test
    public void testTagBr() {
        check("<br>",
                "{\"spans\":[],\"text\":\"\\n\"}");
        check("<br><br>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}");
        check("<br><br><br>",
                "{\"spans\":[],\"text\":\"\\n\\n\\n\"}");

        check("a<br>",
                "{\"spans\":[],\"text\":\"a\\n\"}");
        check("a<br><br>",
                "{\"spans\":[],\"text\":\"a\\n\\n\"}");

        check("<br>a",
                "{\"spans\":[],\"text\":\"\\na\"}");
        check("<br><br>a",
                "{\"spans\":[],\"text\":\"\\n\\na\"}");

        check("a <br> <br> a",
                "{\"spans\":[],\"text\":\"a\\n\\na\"}");
        check("a  <br>  <br>  a",
                "{\"spans\":[],\"text\":\"a\\n\\na\"}");
    }

    @Test
    public void testTagP() {
        check("<p></p>",
                "{\"spans\":[],\"text\":\"\"}");
        check("<p> </p>",
                "{\"spans\":[],\"text\":\"\"}");
        check("<p>  </p>",
                "{\"spans\":[],\"text\":\"\"}");
        check("<p>\n</p>",
                "{\"spans\":[],\"text\":\"\"}");
        check("<p>\n\n</p>",
                "{\"spans\":[],\"text\":\"\"}");
        check("<p> \n \n </p>",
                "{\"spans\":[],\"text\":\"\"}");
        check("<p>  \n  \n  </p>",
                "{\"spans\":[],\"text\":\"\"}");
        check("<p>  \n\n  \n\n  </p>",
                "{\"spans\":[],\"text\":\"\"}");

        check("<p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\"}");
        check("<p><br><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}");
        check("<p> <br> <br> </p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}");
        check("<p>  <br><br>  <br><br>  </p>",
                "{\"spans\":[],\"text\":\"\\n\\n\\n\\n\"}");

        check("<p>a</p>",
                "{\"spans\":[],\"text\":\"a\"}");
        check("<p> a </p>",
                "{\"spans\":[],\"text\":\"a\"}");
        check("<p>  a  </p>",
                "{\"spans\":[],\"text\":\"a\"}");
        check("<p><br>a<br></p>",
                "{\"spans\":[],\"text\":\"\\na\\n\"}");
        check("<p><br></p><p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}");
        check("<p>a</p><p>a</p>",
                "{\"spans\":[],\"text\":\"a\\na\"}");

        check(" <p></p> ",
                "{\"spans\":[],\"text\":\"\"}");
        check(" <p> </p> ",
                "{\"spans\":[],\"text\":\"\"}");
        check(" <p>a</p> ",
                "{\"spans\":[],\"text\":\"a\"}");

        check("a<p></p>",
                "{\"spans\":[],\"text\":\"a\\n\"}");
        check("<p></p>a",
                "{\"spans\":[],\"text\":\"\\na\"}");
        check("a<p></p>a",
                "{\"spans\":[],\"text\":\"a\\na\"}");
        check("<p>a</p>a",
                "{\"spans\":[],\"text\":\"a\\na\"}");
        check("a<p>a</p>a",
                "{\"spans\":[],\"text\":\"a\\na\\na\"}");

        check("  <p>  a  a  </p>  ",
                "{\"spans\":[],\"text\":\"a a\"}");
        check("  \n\n<p>  a  \n\n  a\n\n  </p> ",
                "{\"spans\":[],\"text\":\"a a\"}");

        check("<p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\"}");
        check("<p><br></p><p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}");
        check("<p><br></p><p><br></p><p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\\n\"}");

        check("<p>a<br></p>",
                "{\"spans\":[],\"text\":\"a\\n\"}");
        check("<p><br>a</p>",
                "{\"spans\":[],\"text\":\"\\na\"}");

        check("<p style=\"color:darkgray;text-decoration:line-through;\"></p>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<p style=\"color:darkgray;text-decoration:line-through;\">a</p>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("<p>a</p><p style=\"color:darkgray;text-decoration:line-through;\">a</p>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\na\"}");
    }

    @Test
    public void testTagDiv() {
        check("<div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");
        check("<div></div><div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");
        check("<div><div></div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");
        check("<div></div><div><div></div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");

        check("<div> </div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");
        check(" <div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");
        check("<div></div> ",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");
        check(" <div></div> ",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("a<div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\n\"}");
        check("<div></div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\na\"}");
        check("a<div></div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\na\"}");
        check("a<div>a</div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\na\\na\"}");
        check("a<div>a</div><div>a</div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":5,\"spanFlags\":17,\"spanStart\":4}],\"text\":\"a\\na\\na\\na\"}");
        check("a<div>a</div>a<div>a</div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":7,\"spanFlags\":17,\"spanStart\":6}],\"text\":\"a\\na\\na\\na\\na\"}");

        check("<div style=\"text-align:center;\"></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<div style=\"text-indent:20px;\"></div>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "</div>\n" +
                        "</div>\n",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">b</p>\n" +
                        "</div>\n",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\nb\"}");

        check("<div style=\"text-align:center;\">" +
                        "<blockquote></blockquote>"+
                        "</div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<div style=\"text-align:center;text-indent:20px;\"></div>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<div style=\"text-align:center;\">" +
                        "<blockquote>a</blockquote>a"+
                        "</div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}");

        check("<div style=\"text-align:center;text-indent:20px;\"><blockquote style=\"text-indent:20px;text-align:center;\"></blockquote></div>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":2,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");
    }

    @Test
    public void testListSpan() {
        check("<ul></ul>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<ol></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<ul><li></li></ul>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<ol><li></li></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<ol><ul></ul></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");

        check("<ol><li><ul></ul></li></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<ol><li><ul><li></li></ul></li></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"mNestingLevel\":2,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");
    }

    @Test
    public void testTagBlockquote() {
        check("<blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote>  </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote>a</blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("<blockquote>a<br>a</blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}");

        check("<blockquote></blockquote><blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");

        check("<blockquote> </blockquote><blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");

        check("<blockquote><blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\"> </p>\n" +
                        "</blockquote>\n",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}");

        check("<blockquote><blockquote></blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote> <blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote> <blockquote></blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote><blockquote><blockquote></blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":3,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote><blockquote></blockquote></blockquote><blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");

        check("<blockquote><blockquote><blockquote></blockquote></blockquote><blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":3,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\\n\"}");

        check("<blockquote style=\"text-align:center;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote style=\"text-align:center;\">a</blockquote>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("<blockquote style=\"text-align:center;text-indent:20px;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote style=\"text-indent:20px;text-align:center;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote style=\"text-indent:20px;text-align:center;\">a</blockquote>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        ///段落/块元素含多个不同的style样式（即段落/块样式、内联样式）
        check("<blockquote style=\"color:#0000FF;text-align:center;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<blockquote style=\"color:#0000FF;text-align:center;\">a</blockquote>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagH() {
        check("<h1></h1>",
                "{\"spans\":[{\"span\":{\"mLevel\":0,\"mMarginBottom\":60,\"mMarginTop\":60},\"spanClassName\":\"HeadSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<h2>a</h2>",
                "{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("<h2>b<font color=\"blue\">a</font>b</h2>",
                "{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"bab\"}");

        check("<h2>b<font color=\"blue\">a</font><font color=\"blue\">a</font>b</h2>",
                "{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"baab\"}");
    }

    @Test
    public void testTagPre() {
        check("<pre></pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<pre>a</pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("<pre>b<font color=\"blue\">a</font>b</pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"bab\"}");

        check("<pre>   int a = 0;\n" +
                        "   int a = 0;   </pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":30,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"   int a \\u003d 0;\\n   int a \\u003d 0;   \"}");

        check("<pre>   int <font color=\"blue\">a</font> = 0;\n" +
                        "   int a = 0;   </pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":30,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":8,\"spanFlags\":17,\"spanStart\":7}],\"text\":\"   int a \\u003d 0;\\n   int a \\u003d 0;   \"}");
    }

    @Test
    public void testTagHr() {
        check("<hr>",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}");

        check("<hr /><p>a</p><hr />a",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"\\na\\na\"}");
    }


    // todo ... LineMarginSpan


    @Test
    public void testTagSpan() {
        check("<span style=\"color:blue\"></span>", ///空ParagraphStyle标签
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<span style=\"color:blue\">a</span>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("<span style=\"color:blue;text-decoration:line-through;\">a</span>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    ///b/strong
    @Test
    public void testTagB() {
        check("<b></b>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<b>a</b>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    ///i/em/cite/dfn/
    @Test
    public void testTagI() {
        check("<i></i>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<i>a</i>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagU() {
        check("<u></u>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<u>a</u>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    ///s/strike/del
    @Test
    public void testTagS() {
        check("<s></s>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<s>a</s>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagSup() {
        check("<sup></sup>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<sup>a</sup>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagSub() {
        check("<sub></sub>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<sub>a</sub>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagCode() {
        check("<code></code>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CodeSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<code>a</code>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CodeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTt() {
        check("<tt></tt>",
                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<tt>a</tt>",
                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagFont() {
        ///font#face
        check("<font face=\"serif\">a</font>",
                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        ///font#color
        ///注意：color转换为span标签
        check("<font color=\"blue\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        ///font#size（px）
        ///注意：size转换为span标签
        check("<font size=\"10px\">a</font>",
                "{\"spans\":[{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        ///font#size（%）
        ///注意：size转换为span标签
        check("<font size=\"150%\">a</font>",
                "{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");


        ///注意：多个style时可能顺序不同
        ///font#face+color
        check("<font color=\"blue\" face=\"serif\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");


        ///注意：多个style时可能顺序不同
        ///font#face+color+size（px）
        check("<font face=\"serif\" color=\"blue\" size=\"10px\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        ///注意：多个style时可能顺序不同
        ///font#face+color+size（%）
        check("<font face=\"serif\" color=\"blue\" size=\"150%\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagBig() {
        check("<big></big>",
                "{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<big>a</big>",
                "{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagSmall() {
        check("<small></small>",
                "{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<small>a</small>",
                "{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagA() {
        check("<a href=\"http://www.google.com\"></a>",
                "{\"spans\":[{\"span\":{\"mURL\":\"http://www.google.com\"},\"spanClassName\":\"CustomURLSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<a href=\"http://www.google.com\">a</a>",
                "{\"spans\":[{\"span\":{\"mURL\":\"http://www.google.com\"},\"spanClassName\":\"CustomURLSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagImg() {
        check("<img src=\"http://www.google.com/a.jpg\"></img>",  ///注意：等同于"<img src=\"http://www.google.com/a.jpg\" />"
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");

        check("<img src=\"http://www.google.com/a.jpg\">a</img>", ///注意：等同于"<img src=\"http://www.google.com/a.jpg\" />a"
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]a\"}");


        check("<img src=\"http://www.google.com/a.jpg\" />",
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");
    }

    @Test
    public void testTagVideo() {
        check("<video src=\"http://www.google.com/a.mp4\" />",
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.mp4\",\"mVerticalAlignment\":0},\"spanClassName\":\"VideoSpan\",\"spanEnd\":75,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.mp4\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");
    }

    @Test
    public void testTagAudio() {
        check("<audio src=\"http://www.google.com/a.wav\" />",
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.wav\",\"mVerticalAlignment\":0},\"spanClassName\":\"AudioSpan\",\"spanEnd\":75,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.wav\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");
    }


    /* -------------- 自定义tag ----------- */
    @Test
    public void testBlock() {
        check("<block></block>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BlockSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<block>a</block>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BlockSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testBorder() {
        check("<border></border>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BorderSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("<border>a</border>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BorderSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

}
