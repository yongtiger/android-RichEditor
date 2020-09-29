package cc.brainbook.android.richeditor.json;

import android.os.Parcelable;
import android.text.Spannable;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;

import cc.brainbook.android.richeditor.EditorActivity;
import cc.brainbook.android.richeditor.R;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;

import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.fromJson;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FromJsonTest {
    private RichEditorToolbar mRichEditorToolbar;
    private LinkedHashMap<Class<? extends Parcelable>, View> mClassMap;

    @Rule
    public ActivityTestRule<EditorActivity> mActivityRule = new ActivityTestRule<>(
            EditorActivity.class);

    @Before
    public void beforeTest() {
        mRichEditorToolbar = mActivityRule.getActivity().findViewById(R.id.rich_editor_tool_bar);

        mClassMap = mRichEditorToolbar.getClassMap();

//        ///[UPGRADE#android.text.Html]px in CSS is the equivalence of dip in Android
//        ///注意：一般情况下，CustomAbsoluteSizeSpan的dip都为true，否则需要在使用Html之前设置本机的具体准确的屏幕密度！
//        Context context = mActivityRule.getActivity();
//        Html.sDisplayMetricsDensity = context.getResources().getDisplayMetrics().density;
    }

    private void check(String srcString) {
        Spannable spannable = fromJson(srcString);
        String jsonString = RichEditorToolbarHelper.toJson(mClassMap, spannable, 0, spannable.length(), true);
        assertEquals(srcString, jsonString);
    }


    @Test
    public void testEmpty() {
        check("{\"spans\":[],\"text\":\"\"}");
    }

    @Test
    public void testContent() {
        check("{\"spans\":[],\"text\":\"a\"}");
    }

    @Test
    public void testTagBr() {
        check("{\"spans\":[],\"text\":\"\\n\"}");
    }

    @Test
    public void testTagP() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{\"mColor\":-12303292},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}");
    }

    @Test
    public void testTagDiv() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");
        check("{\"spans\":[{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mNestingLevel\":2},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"DivSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");

        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignNormalSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignOppositeSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"mFirst\":20,\"mNestingLevel\":1,\"mRest\":0},\"spanClassName\":\"CustomLeadingMarginSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testListSpan() {
        check("{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":1,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\\n\"}");

        check("{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"spanClassName\":\"ListSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":0,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":1,\"mNestingLevel\":2,\"mStart\":0},\"mNestingLevel\":2,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mIndex\":1,\"mIndicatorColor\":-2236963,\"mIndicatorGapWidth\":40,\"mIndicatorWidth\":20,\"mListSpan\":{\"isReversed\":false,\"mIntent\":160,\"mListType\":-1,\"mNestingLevel\":1,\"mStart\":1},\"mNestingLevel\":1,\"mWantColor\":true},\"spanClassName\":\"ListItemSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagBlockquote() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\\na\"}");

        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1}],\"text\":\"\\n\\n\"}");

        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}");

//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":3,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\\n\"}");

        check("{\"spans\":[{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":3,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":2,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":1},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":2,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\\n\"}");

//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"AlignCenterSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-7829368,\"mGapWidth\":40,\"mNestingLevel\":1,\"mStripeWidth\":16},\"spanClassName\":\"CustomQuoteSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagH() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mLevel\":0,\"mMarginBottom\":60,\"mMarginTop\":60},\"spanClassName\":\"HeadSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"bab\"}");

        check("{\"spans\":[{\"span\":{\"mLevel\":1,\"mMarginBottom\":50,\"mMarginTop\":50},\"spanClassName\":\"HeadSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"baab\"}");
    }

    @Test
    public void testTagPre() {
        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":0,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"bab\"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":30,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"   int a \\u003d 0;\\n   int a \\u003d 0;   \"}");

        check("{\"spans\":[{\"span\":{\"mNestingLevel\":1},\"spanClassName\":\"PreSpan\",\"spanEnd\":30,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":7}],\"text\":\"   int a \\u003d 0;\\n   int a \\u003d 0;   \"}");
    }

    @Test
    public void testTagHr() {
        check("{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\n\"}");

        check("{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\n\"}");
        check("{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"\\na\"}");
        check("{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2}],\"text\":\"a\\n\\na\"}");

        check("{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":1,\"spanFlags\":17,\"spanStart\":0},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":4,\"spanFlags\":17,\"spanStart\":3}],\"text\":\"\\na\\n\\na\"}");
        check("{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":6,\"spanFlags\":17,\"spanStart\":5}],\"text\":\"a\\n\\na\\n\\n\"}");

        check("{\"spans\":[{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":3,\"spanFlags\":17,\"spanStart\":2},{\"span\":{\"mMarginBottom\":0,\"mMarginTop\":0},\"spanClassName\":\"LineDividerSpan\",\"spanEnd\":6,\"spanFlags\":17,\"spanStart\":5}],\"text\":\"a\\n\\na\\n\\na\"}");
    }


    // todo ... LineMarginSpan


    @Test
    public void testTagSpan() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    ///b/strong
    @Test
    public void testTagB() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    ///i/em/cite/dfn/
    @Test
    public void testTagI() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"ItalicSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagU() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomUnderlineSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    ///s/strike/del
    @Test
    public void testTagS() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomStrikethroughSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagSup() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSuperscriptSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagSub() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CustomSubscriptSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagCode() {
        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CodeSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"CodeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTt() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mFamily\":\"monospace\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagFont() {
        ///font#face
        check("{\"spans\":[{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");

        ///font#color
        ///注意：color转换为span标签
        check("{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");

        ///font#size（px）
        ///注意：size转换为span标签
        check("{\"spans\":[{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");

        ///font#size（%）
        ///注意：size转换为span标签
        check("{\"spans\":[{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");


        ///注意：多个style时可能顺序不同
        ///font#face+color
        check("{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");


        ///注意：多个style时可能顺序不同
        ///font#face+color+size（px）
        check("{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mDip\":true,\"mSize\":10},\"spanClassName\":\"CustomAbsoluteSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");

        ///注意：多个style时可能顺序不同
        ///font#face+color+size（%）
        check("{\"spans\":[{\"span\":{\"mColor\":-16776961},\"spanClassName\":\"CustomForegroundColorSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mFamily\":\"serif\"},\"spanClassName\":\"CustomFontFamilySpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{\"mProportion\":1.5},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagBig() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mProportion\":1.25},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagSmall() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mProportion\":0.8},\"spanClassName\":\"CustomRelativeSizeSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagA() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{\"mURL\":\"http://www.google.com\"},\"spanClassName\":\"CustomURLSpan\",\"spanEnd\":0,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{\"mURL\":\"http://www.google.com\"},\"spanClassName\":\"CustomURLSpan\",\"spanEnd\":1,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagImg() {
        check("{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\",\"mUri\":\"\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");

        check("{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\",\"mUri\":\"\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":17,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]a\"}");


        check("{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"http://www.google.com/a.jpg\",\"mUri\":\"\",\"mVerticalAlignment\":0},\"spanClassName\":\"CustomImageSpan\",\"spanEnd\":66,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[img src\\u003d\\\"http://www.google.com/a.jpg\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");
    }

    @Test
    public void testTagVideo() {
        check("{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.mp4\",\"mVerticalAlignment\":0},\"spanClassName\":\"VideoSpan\",\"spanEnd\":75,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.mp4\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");
    }

    @Test
    public void testTagAudio() {
        check("{\"spans\":[{\"span\":{\"mDrawableHeight\":68,\"mDrawableWidth\":74,\"mSource\":\"\",\"mUri\":\"http://www.google.com/a.wav\",\"mVerticalAlignment\":0},\"spanClassName\":\"AudioSpan\",\"spanEnd\":75,\"spanFlags\":33,\"spanStart\":0}],\"text\":\"[media uri\\u003d\\\"http://www.google.com/a.wav\\\" src\\u003d\\\"\\\" width\\u003d74 height\\u003d68 align\\u003d0]\"}");
    }


    /* -------------- 自定义tag ----------- */
    @Test
    public void testTagBlock() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BlockSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BlockSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }

    @Test
    public void testTagBorder() {
//        ///注意：spanStart == spanEnd的span会被SpanUtil.getFilteredSpans()删除！
//        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BorderSpan\",\"spanEnd\":0,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"\"}");

        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BorderSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}");
    }
}
