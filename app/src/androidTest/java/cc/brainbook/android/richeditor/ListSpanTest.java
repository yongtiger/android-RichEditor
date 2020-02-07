package cc.brainbook.android.richeditor;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.SpannableStringBuilder;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;

import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ListSpanTest {
    private RichEditorToolbar mRichEditorToolbar;
    private RichEditText mRichEditText;
    private LinkedHashMap<Class, View> mClassMap = new LinkedHashMap<>();

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void testAll() {
        mRichEditorToolbar = mActivityRule.getActivity().findViewById(R.id.rich_editor_tool_bar);
        mRichEditText = mActivityRule.getActivity().findViewById(R.id.et_rich_edit_text);

        mClassMap = mRichEditorToolbar.getClassMap();

//        onView(withId(R.id.et_rich_edit_text)).perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard()); //line 1
//        onView(withText("Say hello!")).perform(click()); //line 2
//        String expectedText = "Hello, " + STRING_TO_BE_TYPED + "!";
//        onView(withId(R.id.textView)).check(matches(withText(expectedText))); //line 3


        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testCase1();
            }
        });
    }

    ///[单选、粘贴（全span）]
    private void testCase1() {

        /* --------------- 初始化 --------------- */
        SpannableStringBuilder initText = new SpannableStringBuilder("a");
//        initText.setSpan(new ListSpan(), 0, 1, getSpanFlags(ListSpan.class));
        // todo ...

        mRichEditText.setText(initText);


        /* --------------- 操作 --------------- */
        SpannableStringBuilder actionText = new SpannableStringBuilder("b");
//        actionText.setSpan(new ListSpan(), 0, 1, getSpanFlags(ListSpan.class));
        // todo ...

        mRichEditText.getText().replace(1, 1, actionText);


        /* --------------- 检查结果 --------------- */
        String result = RichEditorToolbarHelper.toJson(mClassMap, mRichEditText.getText(), 0, mRichEditText.getText().length(), true);
        assertEquals("{\"spans\":[{\"span\":{\"mIndentWidth\":80,\"mIndicatorColor\":14540253,\"mIndicatorGapWidth\":40,\"mIndicatorText\":\"●\",\"mIndicatorWidth\":20,\"mListType\":1,\"mNestingLevel\":0,\"mOrderIndex\":1,\"mWantColor\":false},\"spanClassName\":\"ListSpan\",\"spanEnd\":2,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"ab\"}", result);

    }
}
