package cc.brainbook.android.richeditor;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.Editable;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

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
    private HashMap<View, Class> mClassMap;

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

    private void check(String srcString, String expectJsonString, String expectConsecutiveString, String expectIndividualString) {
        editable = (Editable) Html.fromHtml(srcString);
        String jsonString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        assertEquals(expectJsonString, jsonString);

        String consecutiveString = Html.toHtml(editable, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        assertEquals(expectConsecutiveString, consecutiveString);

        String individualString = Html.toHtml(editable, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        assertEquals(expectIndividualString, individualString);

        ///double check
        //////??????注意：原生Html有可能产生尾部'\n'，应该避免toHtml/fromHtml反复多次转换后越来越多！
        editable = (Editable) Html.fromHtml(consecutiveString);
        String jsonConsecutiveString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        assertEquals(jsonString, jsonConsecutiveString);

        editable = (Editable) Html.fromHtml(individualString);
        String jsonIndividualString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        assertEquals(jsonString, jsonIndividualString);
    }


//    @Test
//    public void testEmpty() {    /////////////////////////////////////OK
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//
//        check(" ",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//
//        check("\n",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }

//    @Test
//    public void testContent() {    /////////////////////////////////////OK
//        check("a",
//                "{\"spans\":[],\"text\":\"a\"}",
//                "<p dir=\"ltr\">a</p>\n",   ///注意：原生Html会产生多余的'\n'
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\">a</p>\n"); ///注意：原生Html会产生多余的'\n'
//
//    }


    /* ------------- 原生android.text.Html的tag ------------- */
//    @Test
//    public void testTagBr() {    /////////////////////////////////////OK
//        check("<br>",
//                "{\"spans\":[],\"text\":\"\\n\"}",
//                "<p dir=\"ltr\"><br>\n</p>\n",
//                "<br>\n<br>\n");
//    }
//
//    @Test
//    public void testTagP() {    /////////////////////////////////////OK
//        check("<p></p>",
//                "{\"spans\":[],\"text\":\"\"}", ///???应为'\n'
//                "",
//                "");
//
//        check("<p>\n</p>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//
//        check("<p><br></p>",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//
//        check("<p>a</p>",
//                "{\"spans\":[],\"text\":\"a\\n\\n\"}",
//                "<p dir=\"ltr\">a</p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\">a</p>\n<br>\n<br>\n");
//    }
//
//    @Test
//    public void testTagDiv() {    /////////////////////////////////////OK
//        check("<div>a</div>",
//                "{\"spans\":[],\"text\":\"a\\n\\n\"}",
//                "<p dir=\"ltr\">a</p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\">a</p>\n" +
//                        "<br>\n" +
//                        "<br>\n");
//    }
//
//    @Test
//    public void testTagH() {    /////////////////////////////////////OK
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
//    @Test
//    public void testTagBlockquote() {    /////////////////////////////////////OK
//        check("<blockquote>a</blockquote>",
//                "{\"spans\":[{\"span\":{\"mColor\":-2236963,\"mGapWidth\":40,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":51,\"spanStart\":0}],\"text\":\"a\\n\\n\"}",
//                "<blockquote><p dir=\"ltr\">a<br>\n" +
//                        "</p>\n" +
//                        "</blockquote>\n" +
//                        "<p dir=\"ltr\"><br>\n" +
//                        "</p>\n",
//                "<blockquote><p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\">a</p>\n" +
//                        "<br>\n" +
//                        "</blockquote>\n" +
//                        "<br>\n" +
//                        "<br>\n");
//    }
//
//
//    ///b/strong
//    @Test
//    public void testTagB() {    /////////////////////////////////////OK
//        check("<b>a</b>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><b>a</b></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><b>a</b></p>\n");
//    }
//
//    ///i/em/cite/dfn/
//    @Test
//    public void testTagI() {    /////////////////////////////////////OK
//        check("<i>a</i>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><i>a</i></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><i>a</i></p>\n");
//    }
//
//    @Test
//    public void testTagU() {    /////////////////////////////////////OK
//        check("<u>a</u>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><u>a</u></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><u>a</u></p>\n");
//    }
//
//    ///s/strike/del
//    @Test
//    public void testTagS() {    /////////////////////////////////////OK
//        check("<s>a</s>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><strike>a</strike></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><strike>a</strike></p>\n");
//    }
//
//    @Test
//    public void testTagSup() {    /////////////////////////////////////OK
//        check("<sup>a</sup>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><sup>a</sup></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><sup>a</sup></p>\n");
//    }
//
//    @Test
//    public void testTagSub() {    /////////////////////////////////////OK
//        check("<sub>a</sub>",
//                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><sub>a</sub></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><sub>a</sub></p>\n");
//    }
//
    @Test
    public void testTagFont() {    /////////////////////////////////////OK
//        ///font#face
//        check("<font face=\"serif\">a</font>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\">a</font></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\">a</font></p>\n");

//        ///font#face
//        ///注意：当face="monospace"时转换为tt标签
//        check("<font face=\"monospace\">a</font>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><tt>a</tt></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><tt>a</tt></p>\n");

//        ///font#color
//        ///注意：color转换为span标签
//        check("<font color=\"blue\">a</font>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"color:#0000FF;\">a</span></p>\n");

//        ///font#size（px）
//        ///注意：size转换为span标签
//        check("<font size=\"10px\">a</font>",
//                "{\"spans\":[{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"font-size:10px\";>a</span></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"font-size:10px\";>a</span></p>\n");

//        ///font#size（%）
//        ///注意：size转换为span标签
//        check("<font size=\"150%\">a</font>",
//                "{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"font-size:1.50em;\">a</span></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"font-size:1.50em;\">a</span></p>\n");


//        ///font#face+color
//        check("<font face=\"serif\" color=\"blue\">a</font>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\">a</span></font></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\"><span style=\"color:#0000FF;\">a</span></font></p>\n");

//        ///font#face+color
//        ///注意：当face="monospace"时转换为tt标签
//        check("<font face=\"monospace\" color=\"blue\">a</font>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><tt><span style=\"color:#0000FF;\">a</span></tt></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><tt><span style=\"color:#0000FF;\">a</span></tt></p>\n");


//        ///font#face+color+size（px）
//        check("<font face=\"serif\" color=\"blue\" size=\"10px\">a</font>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:10px\";>a</span></span></font></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:10px\";>a</span></span></font></p>\n");

//        ///font#face+color+size（%）
//        check("<font face=\"serif\" color=\"blue\" size=\"150%\">a</font>",
//                "{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:1.50em;\">a</span></span></font></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:1.50em;\">a</span></span></font></p>\n");

    }
//
//    @Test
//    public void testTagTt() {    /////////////////////////////////////OK
//        check("<tt>a</tt>",
//                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><font face=\"monospace\">a</font></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><font face=\"monospace\">a</font></p>\n");
//    }
//
//    @Test
//    public void testTagBig() {    /////////////////////////////////////OK
//        check("<big>a</big>",
//                "{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><big>a</big></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><big>a</big></p>\n");
//    }
//
//    @Test
//    public void testTagSmall() {    /////////////////////////////////////OK
//        check("<small>a</small>",
//                "{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><small>a</small></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><small>a</small></p>\n");
//    }
//
//    @Test
//    public void testTagA() {    /////////////////////////////////////OK
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
//    @Test
//    public void testTagSpan() {    /////////////////////////////////////OK
//        check("<span style=\"color:blue\">a</span>",
//                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}",
//                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n",
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\"><span style=\"color:#0000FF;\">a</span></p>\n");
//    }
//
//
//    /* ------------- RichEditorToolbar特有的tag和span ------------- */
//    ///ul/ol/li
//    @Test
//    public void testListSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testLeadingMarginSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
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
