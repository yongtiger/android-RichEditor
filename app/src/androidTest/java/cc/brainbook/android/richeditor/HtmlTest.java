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
    private String htmlString;
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
        ///注意：原生Html有可能产生尾部'\n'，应该避免toHtml/fromHtml反复多次转换后越来越多！
        editable = (Editable) Html.fromHtml(consecutiveString);
        String jsonConsecutiveString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        assertEquals(jsonString, jsonConsecutiveString);

        editable = (Editable) Html.fromHtml(individualString);
        String jsonIndividualString = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);
        assertEquals(jsonString, jsonIndividualString);
    }


//    @Test
//    public void testEmpty() {
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
//    public void testContent() {
//        check("a",
//                "{\"spans\":[],\"text\":\"a\"}",
//                "<p dir=\"ltr\">a</p>\n",   ///注意：原生Html会产生多余的'\n'
//                "<p dir=\"ltr\" style=\"margin-top:0; margin-bottom:0;\">a</p>\n"); ///注意：原生Html会产生多余的'\n'
//
//    }


    /* ------------- 原生android.text.Html的tag ------------- */
//    @Test
//    public void testTagBr() {
//        check("<br>",
//                "{\"spans\":[],\"text\":\"\\n\"}",
//                "<p dir=\"ltr\"><br>\n</p>\n",
//                "<br>\n<br>\n");
//    }

//    @Test
//    public void testTagP() {
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

//    @Test
//    public void testTagH() {////////////////////
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagBlockquote() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//
    ///b/strong
    @Test
    public void testTagB() {
        check("<b>a</b>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
    }
//
//    ///i/em/cite/dfn/
//    @Test
//    public void testTagI() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagU() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    ///s/strike/del
//    @Test
//    public void testTagS() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagSup() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagSub() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagFont() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagBig() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagSmall() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagA() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagImg() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagDiv() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagTt() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
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
//    public void testTagAlignNormalSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testAlignCenterSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagAlignOppositeSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testTagHr() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testBackgroundColorSpan() {
//        check("",
//                "{\"spans\":[],\"text\":\"\"}",
//                "",
//                "");
//    }
//
//    @Test
//    public void testForegroundColorSpan() {
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
