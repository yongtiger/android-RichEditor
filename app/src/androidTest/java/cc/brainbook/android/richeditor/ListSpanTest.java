package cc.brainbook.android.richeditor;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;
import cc.brainbook.android.richeditortoolbar.span.BoldSpan;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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
    private HashMap<View, Class> mClassMap = new HashMap<>();

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
//        onView(withId(cc.brainbook.android.richeditortoolbar.R.id.textView)).check(matches(withText(expectedText))); //line 3


        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testCase1();
            }
        });
    }

    public void testCase1() {
        ///创建文本
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("a");

        ///添加span
        BoldSpan boldSpan = new BoldSpan();
        spannableStringBuilder.setSpan(boldSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        // todo ...

        ///设置EditText
        mRichEditText.setText(spannableStringBuilder);


        Editable editable = mRichEditText.getText();
        String result = RichEditorToolbarHelper.toJson(mClassMap, editable, 0, editable.length(), true);

        ///断言文本
        assertEquals("{\"spans\":[{\"span\":{\"mStyle\":1},\"spanClassName\":\"BoldSpan\"," +
                "\"spanEnd\":1,\"spanFlags\":18,\"spanStart\":0}],\"text\":\"a\"}", result);

    }
}
