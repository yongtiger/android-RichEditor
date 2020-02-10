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
            if (!expectHtmlString.equals(htmlString)) {   ///注意：多个style时可能顺序不同
                Log.e("TAG", "check: " + srcString + "\n" + htmlString);

                editable = (Editable) Html.fromHtml(htmlString);
                jsonHtmlString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
                if (!jsonString.equals(jsonHtmlString)) {   ///注意：可能多余一个'\n'
                    Log.e("TAG", "check: " + srcString + "\n" + jsonHtmlString);

                    htmlString = Html.toHtml(editable);
                    assertEquals(expectHtmlString, htmlString);
                } else {
                    assertEquals(jsonString, jsonHtmlString);
                }
            } else {
                assertEquals(expectHtmlString, htmlString);
            }
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
//                "{\"spans\":[],\"text\":\"a\"}",    ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("a ",
//                "{\"spans\":[],\"text\":\"a \"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a  ",
//                "{\"spans\":[],\"text\":\"a \"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a\n",
//                "{\"spans\":[],\"text\":\"a\"}",    ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("a\n\n",
//                "{\"spans\":[],\"text\":\"a\"}",    ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("a\n \n",
//                "{\"spans\":[],\"text\":\"a \"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a\n  \n",
//                "{\"spans\":[],\"text\":\"a \"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a </p>\n");
//
//        check("a\n\n  \n\n",
//                "{\"spans\":[],\"text\":\"a \"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a </p>\n");
//
//        check(" a",
//                "{\"spans\":[],\"text\":\" a\"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("  a",
//                "{\"spans\":[],\"text\":\" a\"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("\na",
//                "{\"spans\":[],\"text\":\"a\"}",    ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("\n\na",
//                "{\"spans\":[],\"text\":\"a\"}",    ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\">a</p>\n");
//
//        check("\n \na",
//                "{\"spans\":[],\"text\":\" a\"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("\n  \na",
//                "{\"spans\":[],\"text\":\" a\"}",   ///注意：可能多余一个'\n'
//                "<p dir=\"ltr\"> a</p>\n");
//
//        check("\n\n  \n\na",
//                "{\"spans\":[],\"text\":\" a\"}",   ///注意：可能多余一个'\n'
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
//
//
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
//
//
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
//
//
//    @Test
//    public void testListSpan() {
////        check("<ul></ul>", //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
////                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
////                "<ul style=\"list-style-type:disc\">\n" +
////                        "<p dir=\"ltr\"></p>\n" +
////                        "</ul>\n");
////
////        check("<ol></ol>", //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
////                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
////                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
////                        "<p dir=\"ltr\"></p>\n" +
////                        "</ol>\n");
//
//
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
//
//
////        check("<ol><ul></ul></ol>", //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
////                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
////                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
////                        "<p dir=\"ltr\"></p>\n" +
////                        "</ol>\n" +
////                        "<ul style=\"list-style-type:disc\">\n" +
////                        "<p dir=\"ltr\"></p>\n" +
////                        "</ul>\n");
////
////        check("<ol><li><ul></ul></li></ol>",    //////??????解析出问题！<ul>或<ol>必须插入<li>后再嵌套<ul>或<ol>
////                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mStart\":0,\"mNestingLevel\":2},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mStart\":1,\"mNestingLevel\":1},\"mWantColor\":true,\"mNestingLevel\":1},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
////                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
////                        "<li>\n" +
////                        "<ul style=\"list-style-type:disc\">\n" +
////                        "<p dir=\"ltr\"></p>\n" +
////                        "</ul>\n" +
////                        "</li>\n" +
////                        "</ol>\n");
//
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
//
//    }
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
//
//
//    @Test
//    public void testTagH() {
//        check("<h1></h1>",
//                "{\"spans\":[{\"span\":{\"mLevel\":0,\"mMarginBottom\":60,\"mMarginTop\":60},\"spanClassName\":\"HeadSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<h1>\n" +
//                        "</h1>\n");
//
//        check("<h2>a</h2>",
//                "{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\n\"}",
//                "<h2>a\n" +
//                        "</h2>\n");
//    }
//
//
//    @Test
//    public void testTagHr() {
//        check("<hr>",
//                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
//                "<hr>\n" +
//                        "\n");
//
//        check("<hr /><p>a</p><hr />a",
//                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":3}],\"text\":\"\\na\\n\\na\"}",
//                "<hr>\n" +
//                        "\n" +
//                        "<p dir=\"ltr\">a</p>\n" +
//                        "<hr>\n" +
//                        "\n" +
//                        "<p dir=\"ltr\">a</p>\n");
//    }
//
//
//    ///b/strong
//    @Test
//    public void testTagB() {
//        check("<b></b>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<b>a</b>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><b>a</b></p>\n");
//    }
//
//    ///i/em/cite/dfn/
//    @Test
//    public void testTagI() {
//        check("<i></i>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<i>a</i>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><i>a</i></p>\n");
//    }
//
//    @Test
//    public void testTagU() {
//        check("<u></u>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<u>a</u>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><u>a</u></p>\n");
//    }
//
//    ///s/strike/del
//    @Test
//    public void testTagS() {
//        check("<s></s>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<s>a</s>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"text-decoration:line-through;\">a</span></p>\n");
//    }
//
//    @Test
//    public void testTagSup() {
//        check("<sup></sup>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<sup>a</sup>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><sup>a</sup></p>\n");
//    }
//
//    @Test
//    public void testTagSub() {
//        check("<sub></sub>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<sub>a</sub>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><sub>a</sub></p>\n");
//    }
//
//
//    @Test
//    public void testTagSpan() {
//        check("<span style=\"color:blue\"></span>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<span style=\"color:blue\">a</span>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n");
//
//        check("<span style=\"color:blue;text-decoration:line-through;\">a</span>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"color:#0000FF;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n");
//    }
//
//
//    @Test
//    public void testTagFont() {
//        ///font#face
//        check("<font face=\"serif\">a</font>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\">a</font></p>\n");
//
//        ///font#color
//        ///注意：color转换为span标签
//        check("<font color=\"blue\">a</font>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n");
//
//        ///font#size（px）
//        ///注意：size转换为span标签
//        check("<font size=\"10px\">a</font>",
//                "{\"spans\":[{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"font-size:10px\";>a</span></p>\n");
//
//        ///font#size（%）
//        ///注意：size转换为span标签
//        check("<font size=\"150%\">a</font>",
//                "{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"font-size:1.50em;\">a</span></p>\n");
////
//
//        ///注意：多个style时可能顺序不同
//        ///font#face+color
//        check("<font color=\"blue\" face=\"serif\">a</font>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\">a</span></font></p>\n");
//
//
//        ///注意：多个style时可能顺序不同
//        ///font#face+color+size（px）
//        check("<font face=\"serif\" color=\"blue\" size=\"10px\">a</font>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:10px\";>a</span></span></font></p>\n");
//
//        ///注意：多个style时可能顺序不同
//        ///font#face+color+size（%）
//        check("<font face=\"serif\" color=\"blue\" size=\"150%\">a</font>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:1.50em;\">a</span></span></font></p>\n");
//
//    }
//
//    @Test
//    public void testTagTt() {
//        check("<tt></tt>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<tt>a</tt>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"monospace\">a</font></p>\n");
//    }
//
//    @Test
//    public void testTagBig() {
//        check("<big></big>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<big>a</big>",
//                "{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"font-size:1.25em;\">a</span></p>\n");
//    }
//
//    @Test
//    public void testTagSmall() {
//        check("<small></small>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<small>a</small>",
//                "{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"font-size:0.80em;\">a</span></p>\n");
//    }
//
//
//    @Test
//    public void testTagA() {
//        check("<a href=\"http://www.google.com\"></a>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "");
//
//        check("<a href=\"http://www.google.com\">a</a>",
//                "{\"spans\":[{\"span\":{\"mURL\":\"http://www.google.com\"},\"spanClassName\":\"CustomURLSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><a href=\"http://www.google.com\">a</a></p>\n");
//    }
//

    @Test
    public void testBlockSpan() {
        check("",
                "{\"spans\":[],\"text\":\"\"}",
                "");
    }

//    @Test
//    public void testTagImg() {
//        check("<img src=\"http://www.google.com/a.jpg\"></img>",  ///注意：等同于"<img src=\"http://www.google.com/a.jpg\" />"
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\"},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}",
//                "<p dir=\"ltr\"><img src=\"http://www.google.com/a.jpg\" width=\"74\" height=\"68\" align=\"0\" /></p>\n");
//
//        check("<img src=\"http://www.google.com/a.jpg\">a</img>", ///注意：等同于"<img src=\"http://www.google.com/a.jpg\" />a"
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\"},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]a\"}",
//                "<p dir=\"ltr\"><img src=\"http://www.google.com/a.jpg\" width=\"74\" height=\"68\" align=\"0\" />a</p>\n");
//
//
//        check("<img src=\"http://www.google.com/a.jpg\" />",
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\"},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}",
//                "<p dir=\"ltr\"><img src=\"http://www.google.com/a.jpg\" width=\"74\" height=\"68\" align=\"0\" /></p>\n");
//
//    }
//
//
//    @Test
//    public void testVideoSpan() {
//        check("<video src=\"http://www.google.com/a.mp4\" />",
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.mp4\"},\"spanClassName\":\"VideoSpan\",\"spanEnd\":75,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.mp4\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}",
//                "<p dir=\"ltr\"><video src=\"http://www.google.com/a.mp4\" img=\"\" width=\"74\" height=\"68\" align=\"0\" /></p>\n");
//    }
//
//    @Test
//    public void testAudioSpan() {
//        check("<audio src=\"http://www.google.com/a.wav\" />",
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.wav\"},\"spanClassName\":\"AudioSpan\",\"spanEnd\":75,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.wav\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}",
//                "<p dir=\"ltr\"><audio src=\"http://www.google.com/a.wav\" img=\"\" width=\"74\" height=\"68\" align=\"0\" /></p>\n");
//    }

}
