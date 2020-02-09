package cc.brainbook.android.richeditor;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.Editable;
import android.util.Log;
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
public class HtmlTest {
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

    private void check(String srcString, String expectJsonString, String expectHtmlString) {
        editable = (Editable) Html.fromHtml(srcString);
        String jsonString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        assertEquals(expectJsonString, jsonString);

        String htmlString = Html.toHtml(editable);
        assertEquals(expectHtmlString, htmlString);

        ///double check
        editable = (Editable) Html.fromHtml(htmlString);
        String jsonHtmlString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        if (!jsonString.equals(jsonHtmlString)) {   ///注意：可能多余一个'\n'
            Log.e("TAG", "check: " + srcString + "\n" + jsonHtmlString);
            htmlString = Html.toHtml(editable);
            assertEquals(expectHtmlString, htmlString);
        } else {
            assertEquals(jsonString, jsonHtmlString);
        }
    }


//    @Test
//    public void testEmpty() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check(" ",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("  ",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("\n",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("\n\n",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("\n \n",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("\n  \n",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("\n\n  \n\n",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//    }
//
//    @Test
//    public void testContent() {
//        check("a",
//                "{\"spans\":[],\"text\":\"a\"}",    ///{"spans":[],"text":"a\n"}
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("a ",
//                "{\"spans\":[],\"text\":\"a \"}",   ///{"spans":[],"text":"a \n"}
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a  ",
//                "{\"spans\":[],\"text\":\"a \"}",   ///{"spans":[],"text":"a \n"}
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a\n",
//                "{\"spans\":[],\"text\":\"a\"}",    ///{"spans":[],"text":"a\n"}
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("a\n\n",
//                "{\"spans\":[],\"text\":\"a\"}",    ///{"spans":[],"text":"a\n"}
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("a\n \n",
//                "{\"spans\":[],\"text\":\"a \"}",   ///{"spans":[],"text":"a \n"}
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a\n  \n",
//                "{\"spans\":[],\"text\":\"a \"}",   ///{"spans":[],"text":"a \n"}
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a\n\n  \n\n",
//                "{\"spans\":[],\"text\":\"a \"}",   ///{"spans":[],"text":"a \n"}
//                "<p dir=\"ltr\">a </p>\n");
//
//        check(" a",
//                "{\"spans\":[],\"text\":\" a\"}",   ///{"spans":[],"text":" a\n"}
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("  a",
//                "{\"spans\":[],\"text\":\" a\"}",   ///{"spans":[],"text":" a\n"}
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("\na",
//                "{\"spans\":[],\"text\":\"a\"}",    ///{"spans":[],"text":"a\n"}
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("\n\na",
//                "{\"spans\":[],\"text\":\"a\"}",    ///{"spans":[],"text":"a\n"}
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("\n \na",
//                "{\"spans\":[],\"text\":\" a\"}",   ///{"spans":[],"text":" a\n"}
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("\n  \na",
//                "{\"spans\":[],\"text\":\" a\"}",   ///{"spans":[],"text":" a\n"}
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("\n\n  \n\na",
//                "{\"spans\":[],\"text\":\" a\"}",   ///{"spans":[],"text":" a\n"}
//                "<p dir=\"ltr\"> a</p>\n");
//    }
//
//
//    @Test
//    public void testTagBr() {
//        check("<br>",
//                "{\"spans\":[],\"text\":\"\\n\"}",
//                "<p dir=\"ltr\"></p>\n");
//
//        check("<br><br>",
//                "{\"spans\":[],\"text\":\"\\n\\n\"}",
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n");
//
//        check("<br><br><br>",
//                "{\"spans\":[],\"text\":\"\\n\\n\\n\"}",
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n");
//
//        check("a<br>",
//                "{\"spans\":[],\"text\":\"a\\n\"}",
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("a<br><br>",
//                "{\"spans\":[],\"text\":\"a\\n\\n\"}",
//                "<p dir=\"ltr\">a</p>\n" +
//                        "<p dir=\"ltr\"></p>\n");
//
//        check("<br>a",
//                "{\"spans\":[],\"text\":\"\\na\"}", ///{"spans":[],"text":"\na\n"}
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\">a</p>\n");
//
//        check("<br><br>a",
//                "{\"spans\":[],\"text\":\"\\n\\na\"}",  ///{"spans":[],"text":"\n\na\n"}
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\">a</p>\n");
//
//        check("a <br> <br> a",
//                "{\"spans\":[],\"text\":\"a \\n \\n a\"}",  ///{"spans":[],"text":"a \n \n a\n"}
//                "<p dir=\"ltr\">a </p>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "<p dir=\"ltr\"> a</p>\n");
//
//        check("a  <br>  <br>  a",
//                "{\"spans\":[],\"text\":\"a \\n \\n a\"}",  ///{"spans":[],"text":"a \n \n a\n"}
//                "<p dir=\"ltr\">a </p>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "<p dir=\"ltr\"> a</p>\n");
//    }


//    @Test
//    public void testTagP() {
//        check("<p></p>",
//                "{\"spans\":[],\"text\":\"\\n\"}",
//                "<p dir=\"ltr\"></p>\n");
//
//        check("<p> </p>",
//                "{\"spans\":[],\"text\":\" \\n\"}",
//                "<p dir=\"ltr\"> </p>\n");
//
//        check("<p>  </p>",
//                "{\"spans\":[],\"text\":\" \\n\"}",
//                "<p dir=\"ltr\"> </p>\n");
//
//        check("<p>\n</p>",
//                "{\"spans\":[],\"text\":\"\\n\"}",
//                "<p dir=\"ltr\"></p>\n");
//
//        check("<p>\n\n</p>",
//                "{\"spans\":[],\"text\":\"\\n\"}",
//                "<p dir=\"ltr\"></p>\n");
//
//        check("<p> \n \n </p>",
//                "{\"spans\":[],\"text\":\" \\n\"}",
//                "<p dir=\"ltr\"> </p>\n");
//
//        check("<p>  \n  \n  </p>",
//                "{\"spans\":[],\"text\":\" \\n\"}",
//                "<p dir=\"ltr\"> </p>\n");
//
//        check("<p>  \n\n  \n\n  </p>",
//                "{\"spans\":[],\"text\":\" \\n\"}",
//                "<p dir=\"ltr\"> </p>\n");
//
//
//        check("<p><br></p>",
//                "{\"spans\":[],\"text\":\"\\n\\n\"}",
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n");
//
//        check("<p><br><br></p>",
//                "{\"spans\":[],\"text\":\"\\n\\n\\n\"}",
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n");
//
//        check("<p> <br> <br> </p>",
//                "{\"spans\":[],\"text\":\" \\n \\n \\n\"}",
//                "<p dir=\"ltr\"> </p>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "<p dir=\"ltr\"> </p>\n");
//
//        check("<p>  <br><br>  <br><br>  </p>",
//                "{\"spans\":[],\"text\":\" \\n\\n \\n\\n \\n\"}",
//                "<p dir=\"ltr\"> </p>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"> </p>\n");
//
//
//        check("<p>a</p>",
//                "{\"spans\":[],\"text\":\"a\\n\"}",
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("<p> a </p>",
//                "{\"spans\":[],\"text\":\" a \\n\"}",
//                "<p dir=\"ltr\"> a </p>\n");
//
//        check("<p>  a  </p>",
//                "{\"spans\":[],\"text\":\" a \\n\"}",
//                "<p dir=\"ltr\"> a </p>\n");
//
//        check("<p><br>a<br></p>",
//                "{\"spans\":[],\"text\":\"\\na\\n\\n\"}",
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "<p dir=\"ltr\"></p>\n");
//
//
//        check("<p><br></p><p><br></p>",
//                "{\"spans\":[],\"text\":\"\\n\\n\\n\\n\"}",
//                "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "<p dir=\"ltr\"></p>\n");
//
//        check("<p>a</p><p>a</p>",
//                "{\"spans\":[],\"text\":\"a\\na\\n\"}",
//                "<p dir=\"ltr\">a</p>\n" +
//                        "<p dir=\"ltr\">a</p>\n");
//
//
//        check("<p style=\"color:darkgray;text-decoration:line-through;\"></p>",
//                "{\"spans\":[],\"text\":\"\\n\"}",
//                "<p dir=\"ltr\"></p>\n");
//
//        check("<p style=\"color:darkgray;text-decoration:line-through;\">a</p>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":2,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\\n\"}",
//                "<p dir=\"ltr\"><span style=\"color:#444444;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n");
//
//    }
//
//
//    @Test
//    public void testTagDiv() {
//        check("<div></div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n");
//
//        check("<div></div><div></div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
//                "<div>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n" +
//                        "<div>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n");
//
//        check("<div><div></div></div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div>\n" +
//                        "<div>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n" +
//                        "</div>\n");
//
//
//        check("<div> </div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\" \\n\"}",
//                "<div>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "</div>\n");
//
//
//        check("<div style=\"text-align:center;\"></div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n");
//
//
//        check("<div style=\"text-indent:20px;\"></div>",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-indent:20px;\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n");
//
//
//        check("<div style=\"text-indent:20px;\">\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "</div>\n" +
//                        "</div>\n",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-indent:20px;\">\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n" +
//                        "</div>\n");
//
//        check("<div style=\"text-indent:20px;\">\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</div>\n" +
//                        "<p dir=\"ltr\">b</p>\n" +
//                        "</div>\n",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\nb\\n\"}",
//                "<div style=\"text-indent:20px;\">\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</div>\n" +
//                        "<p dir=\"ltr\">b</p>\n" +
//                        "</div>\n");
//
//        check("<div style=\"text-align:center;\">" +
//                        "<blockquote></blockquote>"+
//                        "</div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n");
//
//        check("<div style=\"text-align:center;\">" +
//                        "<blockquote>a</blockquote>a"+
//                        "</div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</div>\n");
//
//        check("<div style=\"text-align:center;\">" +
//                        "<blockquote>a</blockquote>a"+
//                        "</div>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</div>\n");


//        check("<div style=\"text-align:center;text-indent:20px;\"></div>",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-indent:20px;\">\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n" +
//                        "</div>\n");
//
//
//        check("<div style=\"text-align:center;text-indent:20px;\"><blockquote style=\"text-indent:20px;text-align:center;\"></blockquote></div>",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":2},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-indent:20px;\">\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "<div style=\"text-indent:20px;\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</div>\n" +
//                        "</div>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n" +
//                        "</div>\n");
//
//    }


    @Test
    public void testListSpan() {
        check("<ul></ul>", //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ul style=\"list-style-type:disc\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</ul>\n");

        check("<ol></ol>", //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</ol>\n");


//        check("<ul><li></li></ul>",
//                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":1},\"mWantColor\":true,\"mNestingLevel\":1},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<ul style=\"list-style-type:disc\">\n" +
//                        "<li>\n" +
//                        "\n" +
//                        "</li>\n" +
//                        "</ul>\n");
//
//        check("<ol><li></li></ol>",
//                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"mWantColor\":true,\"mNestingLevel\":1},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
//                        "<li>\n" +
//                        "\n" +
//                        "</li>\n" +
//                        "</ol>\n");


//        check("<ol><ul></ul></ol>", //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
//                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
//                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</ol>\n" +
//                        "<ul style=\"list-style-type:disc\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</ul>\n");

//        check("<ol><li><ul></ul></li></ol>",    //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
//                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":2},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"mWantColor\":true,\"mNestingLevel\":1},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
//                        "<li>\n" +
//                        "<ul style=\"list-style-type:disc\">\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</ul>\n" +
//                        "</li>\n" +
//                        "</ol>\n");

//        check("<ol><li><ul><li></li></ul></li></ol>",
//                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":2},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":2},\"mWantColor\":true,\"mNestingLevel\":2},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"mWantColor\":true,\"mNestingLevel\":1},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
//                        "<li>\n" +
//                        "<ul style=\"list-style-type:disc\">\n" +
//                        "<li>\n" +
//                        "\n" +
//                        "</li>\n" +
//                        "</ul>\n" +
//                        "</li>\n" +
//                        "</ol>\n");

    }
//
//
//    @Test
//    public void testTagBlockquote() {
//        check("<blockquote></blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n");
//
//        check("<blockquote> </blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\" \\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "</blockquote>\n");
//
//        check("<blockquote>  </blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\" \\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "</blockquote>\n");
//
//
//        check("<blockquote>a</blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</blockquote>\n");
//
//        check("<blockquote>a<br>a</blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</blockquote>\n");
//
//
//        check("<blockquote></blockquote><blockquote></blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n");
//
//        check("<blockquote> </blockquote><blockquote> </blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":2}],\"text\":\" \\n \\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "</blockquote>\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "</blockquote>\n");
//
//
//        check("<blockquote><blockquote></blockquote></blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":2},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<blockquote>\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "</blockquote>\n");
//
//
//        check("<blockquote> <blockquote></blockquote> </blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":2},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0}],\"text\":\" \\n \\n\"}",
//                "<blockquote>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "<p dir=\"ltr\"> </p>\n" +
//                        "</blockquote>\n");
//
//
//        check("<blockquote style=\"text-align:center;\"></blockquote>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n");
//
//        check("<blockquote style=\"text-align:center;\">a</blockquote>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n");
//
//
//        check("<blockquote style=\"text-align:center;text-indent:20px;\"></blockquote>",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-indent:20px;\">\n" +
//                        "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n" +
//                        "</div>\n");
//
//        check("<blockquote style=\"text-indent:20px;text-align:center;\"></blockquote>",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<div style=\"text-indent:20px;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n" +
//                        "</div>\n");
//
//        check("<blockquote style=\"text-indent:20px;text-align:center;\">a</blockquote>",
//                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mRest\":0,\"mNestingLevel\":1},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<div style=\"text-indent:20px;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n" +
//                        "</div>\n");
//
//
//        ///段落/块元素含多个不同的style样式（即段落/块样式、内联样式）
//        check("<blockquote style=\"color:#0000FF;text-align:center;\"></blockquote>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"></p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n");
//
//        check("<blockquote style=\"color:#0000FF;text-align:center;\">a</blockquote>",
//                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16,\"mNestingLevel\":1},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\\n\"}",
//                "<div style=\"text-align:center;\">\n" +
//                        "<blockquote>\n" +
//                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n" +
//                        "</blockquote>\n" +
//                        "</div>\n");
//
//    }


//    @Test
//    public void testTagH() {
//        check("<h1>a</h1>",
//                "{\"spans\":[{\"span\":{\"mLevel\":0,\"mMarginBottom\":60,\"mMarginTop\":60},\"spanClassName\":\"HeadSpan\",\"spanEnd\":2,\"spanFlags\":51,\"spanStart\":0}],\"text\":\"a\\n\\n\"}",
//                "<p dir=\"ltr\"><h1>a</h1><br>\n" +
//                        "</p>\n" +
//                        "<p dir=\"ltr\"><br>\n" +
//                        "</p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><h1>a</h1></p>\n" +
//                        "<br>\n" +
//                        "<br>\n");
//    }
//
//
//
////////////////////////????????????????????????????CustomLeadingMarginSpan

//    @Test
//    public void testTagHr() {
//        check("<hr /><p>a</p><hr />a",
//                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":6,\"spanFlags\":17,\"spanStart\":5}],\"text\":\"\\n\\na\\n\\n\\na\"}",
//                "<p dir=\"ltr\">&lt;hr&gt;</p>\n" +
//                        "<p dir=\"ltr\"><br>\n" +
//                        "&lt;hr&gt;</p>\n" +
//                        "<p dir=\"ltr\"><br>\n" +
//                        "</p>\n" +
//                        "<p dir=\"ltr\">a</p>\n",
//                "");
//    }
//

//    ///b/strong
//    @Test
//    public void testTagB() {
//        check("<b>a</b>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><b>a</b></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><b>a</b></p>\n");
//    }
//
//    ///i/em/cite/dfn/
//    @Test
//    public void testTagI() {
//        check("<i>a</i>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><i>a</i></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><i>a</i></p>\n");
//    }
//
//    @Test
//    public void testTagU() {
//        check("<u>a</u>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><u>a</u></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><u>a</u></p>\n");
//    }
//
//    ///s/strike/del
//    @Test
//    public void testTagS() {
//        check("<s>a</s>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><strike>a</strike></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><strike>a</strike></p>\n");
//    }
//
//    @Test
//    public void testTagSup() {
//        check("<sup>a</sup>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><sup>a</sup></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><sup>a</sup></p>\n");
//    }
//
//    @Test
//    public void testTagSub() {
//        check("<sub>a</sub>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><sub>a</sub></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><sub>a</sub></p>\n");
//    }
//
    ////////////////code、
    //    @Test
//    public void testTagSpan() {????????????????????????????
//        check("<span style=\"color:blue\">a</span>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"color:#0000FF;\">a</span></p>\n");
//    }
//

//    @Test
//    public void testTagFont() {
////        ///font#face
////        check("<font face=\"serif\">a</font>",
////                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><font face=\"serif\">a</font></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\">a</font></p>\n");
//
////        ///font#face
////        ///注意：当face="monospace"时转换为tt标签
////        check("<font face=\"monospace\">a</font>",
////                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><tt>a</tt></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><tt>a</tt></p>\n");
//
////        ///font#color
////        ///注意：color转换为span标签
////        check("<font color=\"blue\">a</font>",
////                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"color:#0000FF;\">a</span></p>\n");
//
////        ///font#size（px）
////        ///注意：size转换为span标签
////        check("<font size=\"10px\">a</font>",
////                "{\"spans\":[{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><span style=\"font-size:10px\";>a</span></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"font-size:10px\";>a</span></p>\n");
//
////        ///font#size（%）
////        ///注意：size转换为span标签
////        check("<font size=\"150%\">a</font>",
////                "{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><span style=\"font-size:1.50em;\">a</span></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"font-size:1.50em;\">a</span></p>\n");
//
//
////        ///font#face+color
////        check("<font face=\"serif\" color=\"blue\">a</font>",
////                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\">a</span></font></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\"><span style=\"color:#0000FF;\">a</span></font></p>\n");
//
////        ///font#face+color
////        ///注意：当face="monospace"时转换为tt标签
////        check("<font face=\"monospace\" color=\"blue\">a</font>",
////                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><tt><span style=\"color:#0000FF;\">a</span></tt></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><tt><span style=\"color:#0000FF;\">a</span></tt></p>\n");
//
//
////        ///font#face+color+size（px）
////        check("<font face=\"serif\" color=\"blue\" size=\"10px\">a</font>",
////                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:10px\";>a</span></span></font></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:10px\";>a</span></span></font></p>\n");
//
////        ///font#face+color+size（%）
////        check("<font face=\"serif\" color=\"blue\" size=\"150%\">a</font>",
////                "{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
////                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:1.50em;\">a</span></span></font></p>\n",
////                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:1.50em;\">a</span></span></font></p>\n");
//
//    }
//
//    @Test
//    public void testTagTt() {
//        check("<tt>a</tt>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"monospace\">a</font></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"monospace\">a</font></p>\n");
//    }
//
//    @Test
//    public void testTagBig() {
//        check("<big>a</big>",
//                "{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><big>a</big></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><big>a</big></p>\n");
//    }
//
//    @Test
//    public void testTagSmall() {
//        check("<small>a</small>",
//                "{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><small>a</small></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><small>a</small></p>\n");
//    }
//

//    @Test
//    public void testTagA() {
//        check("<a href=\"http://www.google.com\">a</a>",
//                "{\"spans\":[{\"span\":{\"mURL\":\"http://www.google.com\"},\"spanClassName\":\"CustomURLSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><a href=\"http://www.google.com\">a</a></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><a href=\"http://www.google.com\">a</a></p>\n");
//    }
//
//    @Test
//    public void testTagImg() {    /////////////////////////////////////OK   /////////??????????要按照RichEditorToolbar的要求添加[img uri= src= ]
//        check("<img src=\"http://www.google.com/a.jpg\">a</img>",
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":91,\"mDrawableWidth\":98,\"mSource\":\"http://www.google.com/a.jpg\"},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"￼a\"}",
//                "<p dir=\"ltr\"><img src=\"http://www.google.com/a.jpg\">a</p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><img src=\"http://www.google.com/a.jpg\">a</p>\n");
//    }
//
    //////////////video、audio

//
//    /* ------------- RichEditorToolbar特有的tag和span ------------- */
//    @Test
//    public void testLeadingMarginSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testCodeSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testVideoSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testAudioSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//
//    /* ------------ 有待扩展：color、center、pre、style、table ------------ */
//    // todo ...
}
