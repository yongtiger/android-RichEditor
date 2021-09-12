package cc.brainbook.android.richeditor.html;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import android.os.Parcelable;
import android.text.Editable;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;

import cc.brainbook.android.richeditor.EditorActivity;
import cc.brainbook.android.richeditor.R;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.Html;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class HtmlTest {
    private RichEditorToolbar mRichEditorToolbar;
    private Editable mEditable;


    @Rule
    public ActivityTestRule<EditorActivity> mActivityRule = new ActivityTestRule<>(
            EditorActivity.class);

    @Before
    public void beforeTest() {
        mRichEditorToolbar = mActivityRule.getActivity().findViewById(R.id.rich_editor_tool_bar);
    }

    private void check(String srcString, String expectJsonString, String expectHtmlConsecutiveString, String expectHtmlIndividualString) {
        mEditable = (Editable) Html.fromHtml(srcString);
        String jsonString = ToolbarHelper.toJson(mEditable, 0, mEditable.length(), true);
        assertEquals(expectJsonString, jsonString);

        String htmlConsecutiveString = Html.toHtml(mEditable);
        assertEquals(expectHtmlConsecutiveString, htmlConsecutiveString);

        String htmlIndividualString = Html.toHtml(mEditable, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        assertEquals(expectHtmlIndividualString, htmlIndividualString);
    }


    @Test
    public void testEmpty() {
        check("",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        ///忽略空格和'\n'
        check(" ",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("  ",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("\n",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("\n\n",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("\n \n",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("\n  \n",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("\n\n  \n\n",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
    }

    @Test
    public void testContent() {
        check("a",
                "{\"spans\":[],\"text\":\"a\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a</p>\n");

        ///文字间的多个连续空格转换为一个空格
        check("a a",
                "{\"spans\":[],\"text\":\"a a\"}",
                "<p dir=\"ltr\">a a</p>\n",
                "<p dir=\"ltr\">a a</p>\n");
        check("a  a",
                "{\"spans\":[],\"text\":\"a a\"}",
                "<p dir=\"ltr\">a a</p>\n",
                "<p dir=\"ltr\">a a</p>\n");

        ///忽略'\n'
        check("a\na",
                "{\"spans\":[],\"text\":\"aa\"}",
                "<p dir=\"ltr\">aa</p>\n",
                "<p dir=\"ltr\">aa</p>\n");
        check("a\n\na",
                "{\"spans\":[],\"text\":\"aa\"}",
                "<p dir=\"ltr\">aa</p>\n",
                "<p dir=\"ltr\">aa</p>\n");
        check("a\n \na",
                "{\"spans\":[],\"text\":\"a a\"}",
                "<p dir=\"ltr\">a a</p>\n",
                "<p dir=\"ltr\">a a</p>\n");
        check("a\n  \na",
                "{\"spans\":[],\"text\":\"a a\"}",
                "<p dir=\"ltr\">a a</p>\n",
                "<p dir=\"ltr\">a a</p>\n");
        check("a\n\n  \n\na",
                "{\"spans\":[],\"text\":\"a a\"}",
                "<p dir=\"ltr\">a a</p>\n",
                "<p dir=\"ltr\">a a</p>\n");
    }

    @Test
    public void testTagBr() {
        check("<br>",
                "{\"spans\":[],\"text\":\"\\n\"}",
                "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "</p>\n");
        check("<br><br>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "</p>\n");
        check("<br><br><br>",
                "{\"spans\":[],\"text\":\"\\n\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "<br>\n" +
                        "</p>\n");

        check("a<br>",
                "{\"spans\":[],\"text\":\"a\\n\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n");
        check("a<br><br>",
                "{\"spans\":[],\"text\":\"a\\n\\n\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "<br>\n" +
                        "</p>\n");

        check("<br>a",
                "{\"spans\":[],\"text\":\"\\na\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "a</p>\n");
        check("<br><br>a",
                "{\"spans\":[],\"text\":\"\\n\\na\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "a</p>\n");

        check("a <br> <br> a",
                "{\"spans\":[],\"text\":\"a\\n\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "<br>\n" +
                        "a</p>\n");
        check("a  <br>  <br>  a",
                "{\"spans\":[],\"text\":\"a\\n\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "<br>\n" +
                        "a</p>\n");
    }

    @Test
    public void testTagP() {
        check("<p> </p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("<p>  </p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("<p>\n</p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("<p>\n\n</p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("<p> \n \n </p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("<p>  \n  \n  </p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check("<p>  \n\n  \n\n  </p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\"}",
                "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "</p>\n");
        check("<p><br><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "</p>\n");
        check("<p> <br> <br> </p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "</p>\n");
        check("<p>  <br><br>  <br><br>  </p>",
                "{\"spans\":[],\"text\":\"\\n\\n\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "<br>\n" +
                        "<br>\n" +
                        "</p>\n");

        check("<p>a</p>",
                "{\"spans\":[],\"text\":\"a\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a</p>\n");
        check("<p> a </p>",
                "{\"spans\":[],\"text\":\"a\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a</p>\n");
        check("<p>  a  </p>",
                "{\"spans\":[],\"text\":\"a\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a</p>\n");
        check("<p><br>a<br></p>",
                "{\"spans\":[],\"text\":\"\\na\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "a<br>\n" +
                        "</p>\n");
        check("<p><br></p><p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "</p>\n");
        check("<p>a</p><p>a</p>",
                "{\"spans\":[],\"text\":\"a\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "a</p>\n");

        check(" <p></p> ",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check(" <p> </p> ",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");
        check(" <p>a</p> ",
                "{\"spans\":[],\"text\":\"a\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a</p>\n");

        check("a<p></p>",
                "{\"spans\":[],\"text\":\"a\\n\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n");
        check("<p></p>a",
                "{\"spans\":[],\"text\":\"\\na\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "a</p>\n");
        check("a<p></p>a",
                "{\"spans\":[],\"text\":\"a\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "a</p>\n");
        check("<p>a</p>a",
                "{\"spans\":[],\"text\":\"a\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "a</p>\n");
        check("a<p>a</p>a",
                "{\"spans\":[],\"text\":\"a\\na\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "a<br>\n" +
                        "a</p>\n");

        check("  <p>  a  a  </p>  ",
                "{\"spans\":[],\"text\":\"a a\"}",
                "<p dir=\"ltr\">a a</p>\n",
                "<p dir=\"ltr\">a a</p>\n");
        check("  \n\n<p>  a  \n\n  a\n\n  </p> ",
                "{\"spans\":[],\"text\":\"a a\"}",
                "<p dir=\"ltr\">a a</p>\n",
                "<p dir=\"ltr\">a a</p>\n");

        check("<p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\"}",
                "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "</p>\n");
        check("<p><br></p><p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "</p>\n");
        check("<p><br></p><p><br></p><p><br></p>",
                "{\"spans\":[],\"text\":\"\\n\\n\\n\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\"></p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "<br>\n" +
                        "<br>\n" +
                        "</p>\n");

        check("<p>a<br></p>",
                "{\"spans\":[],\"text\":\"a\\n\"}",
                "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n");
        check("<p><br>a</p>",
                "{\"spans\":[],\"text\":\"\\na\"}",
                "<p dir=\"ltr\"></p>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\"><br>\n" +
                        "a</p>\n");

        check("<p style=\"color:darkgray;text-decoration:line-through;\"></p>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<p style=\"color:darkgray;text-decoration:line-through;\">a</p>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><span style=\"color:#444444;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n",
                "<p dir=\"ltr\"><span style=\"color:#444444;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n");

        check("<p>a</p><p style=\"color:darkgray;text-decoration:line-through;\">a</p>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#444444;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "<span style=\"color:#444444;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n");
    }

    @Test
    public void testTagDiv() {
        check("<div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");
        check("<div></div><div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");
        check("<div><div></div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "</div>\n");
        check("<div></div><div><div></div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "<div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "</div>\n");

        check("<div> </div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");
        check(" <div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");
        check("<div></div> ",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");
        check(" <div></div> ",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");

        check("a<div></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\n\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");
        check("<div></div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\na\"}",
                "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("a<div></div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("a<div>a</div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n");
        check("<div>a</div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}",
                "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<div>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("a<div>a</div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\na\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("a<div>a</div><div>a</div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":6,\"spanFlags\":17,\"spanStart\":4}],\"text\":\"a\\na\\na\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("a<div>a</div>a<div>a</div>a",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":8,\"spanFlags\":17,\"spanStart\":6}],\"text\":\"a\\na\\na\\na\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<div>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n");

        check("<div style=\"text-align:center;\"></div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");

        check("<div style=\"text-indent:20px;\"></div>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-indent:20px;\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n",
                "<div style=\"text-indent:20px;\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n");

        check("<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "</div>\n" +
                        "</div>\n",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "</div>\n");

        check("<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">b</p>\n" +
                        "</div>\n",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\nb\"}",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">b</p>\n" +
                        "</div>\n",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">b</p>\n" +
                        "</div>\n");

        check("<div style=\"text-align:center;\">" +
                        "<blockquote></blockquote>"+
                        "</div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n");

        check("<div style=\"text-align:center;text-indent:20px;\"></div>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "</div>\n");

        check("<div style=\"text-align:center;\">" +
                        "<blockquote>a</blockquote>a"+
                        "</div>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</div>\n");

        check("<div style=\"text-align:center;text-indent:20px;\"><blockquote style=\"text-indent:20px;text-align:center;\"></blockquote></div>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":2,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<div style=\"text-indent:20px;\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</div>\n" +
                        "</div>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<div style=\"text-indent:20px;\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</div>\n" +
                        "</div>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n");
    }

    @Test
    public void testListSpan() {
        check("<ul></ul>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ul style=\"list-style-type:disc\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</ul>\n",
                "<ul style=\"list-style-type:disc\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</ul>\n");

        check("<ol></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</ol>\n",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</ol>\n");

        check("<ul><li></li></ul>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-7829368,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ul style=\"list-style-type:disc\">\n" +
                        "<li>\n" +
                        "</li>\n" +
                        "</ul>\n",
                "<ul style=\"list-style-type:disc\">\n" +
                        "<li>\n" +
                        "<br>\n" +
                        "</li>\n" +
                        "</ul>\n");

        check("<ol><li></li></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-7829368,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<li>\n" +
                        "</li>\n" +
                        "</ol>\n",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<li>\n" +
                        "<br>\n" +
                        "</li>\n" +
                        "</ol>\n");

        ///注意：<ol>或<ul>不能直接含<ol>或<ul>！只能含<li>
        check("<ol><ul></ul></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</ol>\n" +
                        "<ul style=\"list-style-type:disc\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</ul>\n",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</ol>\n" +
                        "<ul style=\"list-style-type:disc\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</ul>\n");

        check("<ol><li><ul></ul></li></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-7829368,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<li>\n" +
                        "<ul style=\"list-style-type:disc\">\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</ul>\n" +
                        "</li>\n" +
                        "</ol>\n",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<li>\n" +
                        "<ul style=\"list-style-type:disc\">\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</ul>\n" +
                        "</li>\n" +
                        "</ol>\n");

        check("<ol><li><ul><li></li></ul></li></ol>",
                "{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-7829368,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"mNestingLevel\":2,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-7829368,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<li>\n" +
                        "<ul style=\"list-style-type:disc\">\n" +
                        "<li>\n" +
                        "</li>\n" +
                        "</ul>\n" +
                        "</li>\n" +
                        "</ol>\n",
                "<ol start=\"1\" style=\"list-style-type:decimal\">\n" +
                        "<li>\n" +
                        "<ul style=\"list-style-type:disc\">\n" +
                        "<li>\n" +
                        "<br>\n" +
                        "</li>\n" +
                        "</ul>\n" +
                        "</li>\n" +
                        "</ol>\n");
    }

    @Test
    public void testTagBlockquote() {
        check("<blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");
        check("<blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");
        check("<blockquote>  </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");

        check("<blockquote><br></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");

        check("<blockquote>a</blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n");

        check("<blockquote>a<br>a</blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "a</p>\n" +
                        "</blockquote>\n");

        check("<blockquote></blockquote><blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");

        check("<blockquote> </blockquote><blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");

        check("<blockquote><blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n");

        check("<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\"> </p>\n" +
                        "</blockquote>\n",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n");

        check("<blockquote><blockquote></blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n");

        check("<blockquote> <blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n");

        check("<blockquote> <blockquote></blockquote> </blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n");

        check("<blockquote><blockquote><blockquote></blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":3,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n");

        check("<blockquote><blockquote></blockquote></blockquote><blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");

        check("<blockquote><blockquote><blockquote></blockquote></blockquote><blockquote></blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":3,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\\n\"}",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</blockquote>\n");

        check("<blockquote></blockquote>a",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\na\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n");

        check("a<blockquote></blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\n\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n");

        check("a<blockquote></blockquote>a",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n");

        check("<blockquote>a</blockquote>a",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n");

        check("a<blockquote>a</blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n");

        check("a<blockquote>a</blockquote>a",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\na\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n");

        check("<blockquote style=\"text-align:center;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n");

        check("<blockquote style=\"text-align:center;\">a</blockquote>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n");

        check("<blockquote style=\"text-align:center;text-indent:20px;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div style=\"text-indent:20px;\">\n" +
                        "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n");

        check("<blockquote style=\"text-indent:20px;text-align:center;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<div style=\"text-indent:20px;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<div style=\"text-indent:20px;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n");

        check("<blockquote style=\"text-indent:20px;text-align:center;\">a</blockquote>",
                "{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<div style=\"text-indent:20px;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<div style=\"text-indent:20px;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "</div>\n");

        ///段落/块元素含多个不同的style样式（即段落/块样式、内联样式）
        ///注意：如果内联样式where == len，则忽略，避免产生零长度的span！
        check("<blockquote style=\"color:#0000FF\"></blockquote>a",
                "{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\na\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("<blockquote style=\"color:#0000FF\">a</blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n" +
                        "</blockquote>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n" +
                        "</blockquote>\n");
        check("<blockquote style=\"color:#0000FF\">a</blockquote>a",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("<blockquote style=\"color:#0000FF;text-align:center;\"></blockquote>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n");
        check("<blockquote style=\"color:#0000FF;text-align:center;\">a</blockquote>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n");
        check("<blockquote style=\"color:#0000FF;text-align:center;\">a</blockquote>a",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<div style=\"text-align:center;\">\n" +
                        "<blockquote>\n" +
                        "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span><br>\n" +
                        "</p>\n" +
                        "</blockquote>\n" +
                        "</div>\n" +
                        "<p dir=\"ltr\">a</p>\n");
    }

    @Test
    public void testTagH() {
        check("<h1></h1>",
                "{\"spans\":[{\"span\":{\"mLevel\":0,\"mMarginBottom\":60,\"mMarginTop\":60},\"spanClassName\":\"HeadSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<h1></h1>\n",
                "<h1><br>\n" +
                        "</h1>\n");

        check("<h2>a</h2>",
                "{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}",
                "<h2>a</h2>\n",
                "<h2>a</h2>\n");

        check("<h2>b<font color=\"blue\">a</font>b</h2>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"bab\"}",
                "<h2>b<span style=\"color:#0000FF;\">a</span>b</h2>\n",
                "<h2>b<span style=\"color:#0000FF;\">a</span>b</h2>\n");

        check("<h2>b<font color=\"blue\">a</font><font color=\"blue\">a</font>b</h2>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"baab\"}",
                "<h2>b<span style=\"color:#0000FF;\">a</span><span style=\"color:#0000FF;\">a</span>b</h2>\n",
                "<h2>b<span style=\"color:#0000FF;\">a</span><span style=\"color:#0000FF;\">a</span>b</h2>\n");
    }

    @Test
    public void testTagPre() {
        check("<pre></pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<pre>\n" +
                        "</pre>\n",
                "<pre>\n" +
                        "</pre>\n");

        check("<pre>a</pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}",
                "<pre>a</pre>\n",
                "<pre>a</pre>\n");

        check("<pre>b<font color=\"blue\">a</font>b</pre>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"bab\"}",
                "<pre>b<span style=\"color:#0000FF;\">a</span>b</pre>\n",
                "<pre>b<span style=\"color:#0000FF;\">a</span>b</pre>\n");

        check("<pre>   int a = 0;\n" +
                        "   int a = 0;   </pre>",
                "{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":30,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"   int a \\u003d 0;\\n   int a \\u003d 0;   \"}",
                "<pre>   int a = 0;\n" +
                        "   int a = 0;   </pre>\n",
                "<pre>   int a = 0;\n" +
                        "   int a = 0;   </pre>\n");

        check("<pre>   int <font color=\"blue\">a</font> = 0;\n" +
                        "   int a = 0;   </pre>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":7},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":30,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"   int a \\u003d 0;\\n   int a \\u003d 0;   \"}",
                "<pre>   int <span style=\"color:#0000FF;\">a</span> = 0;\n" +
                        "   int a = 0;   </pre>\n",
                "<pre>   int <span style=\"color:#0000FF;\">a</span> = 0;\n" +
                        "   int a = 0;   </pre>\n");
    }

    @Test
    public void testTagHr() {
        check("<hr>",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}",
                "<hr>\n",
                "<hr>\n" +
                        "<br>\n");

        check("a<hr />",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\n\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<hr>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<hr>\n" +
                        "<br>\n");
        check("<hr />a",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\na\"}",
                "<hr>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<hr>\n" +
                        "<br>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("a<hr />a",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<hr>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<hr>\n" +
                        "<br>\n" +
                        "<p dir=\"ltr\">a</p>\n");

        check("<hr />a<hr />a",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":3}],\"text\":\"\\na\\n\\na\"}",
                "<hr>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "<hr>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<hr>\n" +
                        "<br>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<hr>\n" +
                        "<br>\n" +
                        "<p dir=\"ltr\">a</p>\n");
        check("a<hr />a<hr />",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":6,\"spanFlags\":17,\"spanStart\":5}],\"text\":\"a\\n\\na\\n\\n\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<hr>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "<hr>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<hr>\n" +
                        "<br>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<hr>\n" +
                        "<br>\n");

        check("a<hr /><p>a</p><hr />a",
                "{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":6,\"spanFlags\":17,\"spanStart\":5}],\"text\":\"a\\n\\na\\n\\na\"}",
                "<p dir=\"ltr\">a</p>\n" +
                        "<hr>\n" +
                        "<p dir=\"ltr\">a</p>\n" +
                        "<hr>\n" +
                        "<p dir=\"ltr\">a</p>\n",
                "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<hr>\n" +
                        "<br>\n" +
                        "<p dir=\"ltr\">a<br>\n" +
                        "</p>\n" +
                        "<hr>\n" +
                        "<br>\n" +
                        "<p dir=\"ltr\">a</p>\n");
    }


    // todo ... LineMarginSpan


    @Test
    public void testTagSpan() {
        check("<span style=\"color:blue\"></span>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<span style=\"color:blue\">a</span>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n",
                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n");

        check("<span style=\"color:blue;text-decoration:line-through;\">a</span>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><span style=\"color:#0000FF;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n",
                "<p dir=\"ltr\"><span style=\"color:#0000FF;\"><span style=\"text-decoration:line-through;\">a</span></span></p>\n");
    }

    ///b/strong
    @Test
    public void testTagB() {
        check("<b></b>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<b>a</b>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><b>a</b></p>\n",
                "<p dir=\"ltr\"><b>a</b></p>\n");
    }

    ///i/em/cite/dfn/
    @Test
    public void testTagI() {
        check("<i></i>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<i>a</i>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><i>a</i></p>\n",
                "<p dir=\"ltr\"><i>a</i></p>\n");
    }

    @Test
    public void testTagU() {
        check("<u></u>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<u>a</u>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><u>a</u></p>\n",
                "<p dir=\"ltr\"><u>a</u></p>\n");
    }

    ///s/strike/del
    @Test
    public void testTagS() {
        check("<s></s>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<s>a</s>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><span style=\"text-decoration:line-through;\">a</span></p>\n",
                "<p dir=\"ltr\"><span style=\"text-decoration:line-through;\">a</span></p>\n");
    }

    @Test
    public void testTagSup() {
        check("<sup></sup>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<sup>a</sup>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><sup>a</sup></p>\n",
                "<p dir=\"ltr\"><sup>a</sup></p>\n");
    }

    @Test
    public void testTagSub() {
        check("<sub></sub>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<sub>a</sub>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><sub>a</sub></p>\n",
                "<p dir=\"ltr\"><sub>a</sub></p>\n");
    }

    @Test
    public void testTagCode() {
        check("<code></code>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<code>a</code>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"CodeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><code>a</code></p>\n",
                "<p dir=\"ltr\"><code>a</code></p>\n");
    }

    @Test
    public void testTt() {
        check("<tt></tt>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<tt>a</tt>",
                "{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><tt>a</tt></p>\n",
                "<p dir=\"ltr\"><tt>a</tt></p>\n");
    }

    @Test
    public void testTagFont() {
        ///font#face
        check("<font face=\"serif\">a</font>",
                "{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><font face=\"serif\">a</font></p>\n",
                "<p dir=\"ltr\"><font face=\"serif\">a</font></p>\n");

        ///font#color
        ///注意：color转换为span标签
        check("<font color=\"blue\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n",
                "<p dir=\"ltr\"><span style=\"color:#0000FF;\">a</span></p>\n");

        ///font#size（px）
        ///注意：size转换为span标签
        check("<font size=\"10px\">a</font>",
                "{\"spans\":[{\"span\":{\"mDip\":false,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><span style=\"font-size:10px\";>a</span></p>\n",
                "<p dir=\"ltr\"><span style=\"font-size:10px\";>a</span></p>\n");

        ///font#size（%）
        ///注意：size转换为span标签
        check("<font size=\"150%\">a</font>",
                "{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><span style=\"font-size:1.50em;\">a</span></p>\n",
                "<p dir=\"ltr\"><span style=\"font-size:1.50em;\">a</span></p>\n");


        ///注意：多个style时可能顺序不同
        ///font#face+color
        check("<font color=\"blue\" face=\"serif\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\">a</span></font></p>\n",
                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\">a</span></font></p>\n");


        ///注意：多个style时可能顺序不同
        ///font#face+color+size（px）
        check("<font face=\"serif\" color=\"blue\" size=\"10px\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mDip\":false,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:10px\";>a</span></span></font></p>\n",
                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:10px\";>a</span></span></font></p>\n");

        ///注意：多个style时可能顺序不同
        ///font#face+color+size（%）
        check("<font face=\"serif\" color=\"blue\" size=\"150%\">a</font>",
                "{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:1.50em;\">a</span></span></font></p>\n",
                "<p dir=\"ltr\"><font face=\"serif\"><span style=\"color:#0000FF;\"><span style=\"font-size:1.50em;\">a</span></span></font></p>\n");
    }

    @Test
    public void testTagBig() {
        check("<big></big>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<big>a</big>",
                "{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><big>a</big></p>\n",
                "<p dir=\"ltr\"><big>a</big></p>\n");
    }

    @Test
    public void testTagSmall() {
        check("<small></small>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<small>a</small>",
                "{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><small>a</small></p>\n",
                "<p dir=\"ltr\"><small>a</small></p>\n");
    }

    @Test
    public void testTagA() {
        check("<a href=\"http://www.google.com\"></a>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<a href=\"http://www.google.com\">a</a>",
                "{\"spans\":[{\"span\":{\"mURL\":\"http://www.google.com\"},\"spanClassName\":\"CustomURLSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><a href=\"http://www.google.com\">a</a></p>\n",
                "<p dir=\"ltr\"><a href=\"http://www.google.com\">a</a></p>\n");
    }

    @Test
    public void testTagImg() {
        check("<img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\"></img>",  ///注意：等同于"<img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" />"
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":0,\"mDrawableWidth\":0,\"mSource\":\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":112,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\\\" width\\u003d height\\u003d align\\u003d0]\"}",
                "<p dir=\"ltr\"><img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" width=\"0\" height=\"0\" align=\"0\" /></p>\n",
                "<p dir=\"ltr\"><img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" width=\"0\" height=\"0\" align=\"0\" /></p>\n");

        check("<img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\">a</img>", ///注意：等同于"<img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" />a"
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":0,\"mDrawableWidth\":0,\"mSource\":\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":112,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\\\" width\\u003d height\\u003d align\\u003d0]a\"}",
                "<p dir=\"ltr\"><img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" width=\"0\" height=\"0\" align=\"0\" />a</p>\n",
                "<p dir=\"ltr\"><img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" width=\"0\" height=\"0\" align=\"0\" />a</p>\n");


        check("<img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" />",
                "{\"spans\":[{\"span\":{\"mDrawableHeight\":0,\"mDrawableWidth\":0,\"mSource\":\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":112,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\\\" width\\u003d height\\u003d align\\u003d0]\"}",
                "<p dir=\"ltr\"><img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" width=\"0\" height=\"0\" align=\"0\" /></p>\n",
                "<p dir=\"ltr\"><img src=\"https://ibrainbook.com/brainassistant/_image/_resource/en/carouse_guide_0.jpg\" width=\"0\" height=\"0\" align=\"0\" /></p>\n");

        //////??????
    }

    //////??????
//    @Test
//    public void testTagVideo() {
//        check("<video src=\"http://www.google.com/a.mp4\" />",
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.mp4\",\"mVerticalAlignment\":0},\"spanClassName\":\"VideoSpan\",\"spanEnd\":75,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.mp4\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}",
//                "<p dir=\"ltr\"><video src=\"http://www.google.com/a.mp4\" img=\"\" width=\"74\" height=\"68\" align=\"0\" /></p>\n",
//                "<p dir=\"ltr\"><video src=\"http://www.google.com/a.mp4\" img=\"\" width=\"74\" height=\"68\" align=\"0\" /></p>\n");
//    }
//
//    @Test
//    public void testTagAudio() {
//        check("<audio src=\"http://www.google.com/a.wav\" />",
//                "{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.wav\",\"mVerticalAlignment\":0},\"spanClassName\":\"AudioSpan\",\"spanEnd\":75,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.wav\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}",
//                "<p dir=\"ltr\"><audio src=\"http://www.google.com/a.wav\" img=\"\" width=\"74\" height=\"68\" align=\"0\" /></p>\n",
//                "<p dir=\"ltr\"><audio src=\"http://www.google.com/a.wav\" img=\"\" width=\"74\" height=\"68\" align=\"0\" /></p>\n");
//    }


    /* -------------- 自定义tag ----------- */
    @Test
    public void testTagBlock() {
        check("<block></block>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<block>a</block>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BlockSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><block>a</block></p>\n",
                "<p dir=\"ltr\"><block>a</block></p>\n");
    }

    @Test
    public void testTagBorder() {
        check("<border></border>",
                "{\"spans\":[],\"text\":\"\"}",
                "",
                "");

        check("<border>a</border>",
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BorderSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                "<p dir=\"ltr\"><border>a</border></p>\n",
                "<p dir=\"ltr\"><border>a</border></p>\n");
    }

}
