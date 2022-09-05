package cc.brainbook.android.richeditor.button.characterstyle;

import android.text.Selection;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cc.brainbook.android.richeditor.EditorActivity;
import cc.brainbook.android.richeditor.R;
import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static cc.brainbook.android.richeditortoolbar.constant.Constant.EMPTY_TEXT_JSON;
import static cc.brainbook.android.richeditortoolbar.helper.ToolbarHelper.fromJson;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BoldSpanTest {
    ///[Lock to synchronize access]
    private static final Object LOCK = new Object();


    private RichEditorToolbar mRichEditorToolbar;
    private RichEditText mRichEditText;

    private String mInitJsonString;
    private int mInitSelectionStart, mInitSelectionEnd;
    private boolean mExpectInitSelected;


    @Rule
    public ActivityTestRule<EditorActivity> mActivityRule = new ActivityTestRule<>(
            EditorActivity.class);

    @Before
    public void beforeTest() {
        mRichEditorToolbar = mActivityRule.getActivity().findViewById(R.id.rich_editor_tool_bar);
        mRichEditText = mRichEditorToolbar.getRichEditText();
    }


    private void check(final String expectJsonString, boolean expectSelected) {
        ///注意：必须在UI线程操作View
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                ///[init]
                mRichEditorToolbar.isSkipTextWatcher = true;
                mRichEditText.setText(fromJson(mInitJsonString));
                mRichEditorToolbar.isSkipTextWatcher = false;
                ///[设置光标]
                Selection.setSelection(mRichEditText.getText(), mInitSelectionStart, mInitSelectionEnd);

                ///[Lock to synchronize access]
                synchronized (LOCK) {
                    LOCK.notify();
                }
            }
        });

        ///[Lock to synchronize access]
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ///检查是否Selected
        onView(withId(R.id.toolbar_bold)).check(matches(mExpectInitSelected ? isSelected() : not(isSelected())));

        ///[动作：点击button]
        onView(withId(R.id.toolbar_bold)).perform(click());

        ///[assert]
        final String result = ToolbarHelper.toJson(mRichEditText.getText(), 0, mRichEditText.getText().length(), true);
        assertEquals(result, expectJsonString);

        ///检查是否Selected
        onView(withId(R.id.toolbar_bold)).check(matches(expectSelected ? isSelected() : not(isSelected())));
    }


    /* ----------------- ///[单选#未selected] ----------------- */

    /**
     * {}
     */
    @Test
    public void testCase000() {
        ///[init]
        mInitJsonString = EMPTY_TEXT_JSON;
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;
        check(EMPTY_TEXT_JSON, true);
    }

    /**
     * {}a
     */
    @Test
    public void testCase001() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;
        check("{\"spans\":[],\"text\":\"a\"}", true);
    }

    /**
     * a{}
     */
    @Test
    public void testCase002() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = false;
        check("{\"spans\":[],\"text\":\"a\"}", true);
    }

    /**
     * a{}a
     */
    @Test
    public void testCase003() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = false;
        check("{\"spans\":[],\"text\":\"aa\"}", true);
    }

    /**
     * {}[a]
     */
    @Test
    public void testCase004() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}", true);
    }

    /**
     * [a]{}
     */
    @Test
    public void testCase005() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}", false);
    }

    /**
     * [a{}a]
     */
    @Test
    public void testCase006() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aa\"}", false);
    }


    /* ----------------- ///[单选#selected] ----------------- */

    /**
     * {}
     */
    @Test
    public void testCase010() {
        ///[init]
        mInitJsonString = EMPTY_TEXT_JSON;
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;
        check(EMPTY_TEXT_JSON, true);

        onView(withId(R.id.toolbar_bold)).perform(click());
        ///检查是否Selected
        onView(withId(R.id.toolbar_bold)).check(matches(not(isSelected())));
    }

    /**
     * {}a
     */
    @Test
    public void testCase011() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;
        check("{\"spans\":[],\"text\":\"a\"}", true);

        onView(withId(R.id.toolbar_bold)).perform(click());
        ///检查是否Selected
        onView(withId(R.id.toolbar_bold)).check(matches(not(isSelected())));
    }

    /**
     * a{}
     */
    @Test
    public void testCase012() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = false;
        check("{\"spans\":[],\"text\":\"a\"}", true);

        onView(withId(R.id.toolbar_bold)).perform(click());
        ///检查是否Selected
        onView(withId(R.id.toolbar_bold)).check(matches(not(isSelected())));
    }

    /**
     * a{}a
     */
    @Test
    public void testCase013() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = false;
        check("{\"spans\":[],\"text\":\"aa\"}",true);

        onView(withId(R.id.toolbar_bold)).perform(click());
        ///检查是否Selected
        onView(withId(R.id.toolbar_bold)).check(matches(not(isSelected())));
    }

    /**
     * {}[a]
     */
    @Test
    public void testCase014() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}", true);

        onView(withId(R.id.toolbar_bold)).perform(click());
        ///检查是否Selected
        onView(withId(R.id.toolbar_bold)).check(matches(not(isSelected())));
    }

    /**
     * [a]{}
     */
    @Test
    public void testCase015() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}", false);
    }

    /**
     * [a{}a]
     */
    @Test
    public void testCase016() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aa\"}", false);
    }


    /* ----------------- ///[区间选] ----------------- */

    /**
     * {a}
     */
    @Test
    public void testCase100() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}", true);
    }

    /**
     * aa[aaa]aa
     */
    @Test
    public void testCase101() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}";


        /* ----------------------- {aa}[aaa]aa ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 2; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}", true);

        /* ----------------------- {aa[a}aa]aa ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- {aa[aa}a]aa ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 4; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- {aa[aaa]}aa ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 5; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- {aa[aaa]a}a ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 6; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- {aa[aaa]aa} ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa{[a}aa]aa ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 3; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa{[aa}a]aa ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 4; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa{[aaa}]aa ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 5; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa{[aaa]a}a ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 6; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa{[aaa]aa} ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[a{a}a]aa ------------------------ */
        mInitSelectionStart = 3; mInitSelectionEnd = 4; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[a{aa}]aa ------------------------ */
        mInitSelectionStart = 3; mInitSelectionEnd = 5; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[a{aa]a}a ------------------------ */
        mInitSelectionStart = 3; mInitSelectionEnd = 6; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[a{aa]aa} ------------------------ */
        mInitSelectionStart = 3; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[aa{a}]aa ------------------------ */
        mInitSelectionStart = 4; mInitSelectionEnd = 5; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[aa{a]a}a ------------------------ */
        mInitSelectionStart = 4; mInitSelectionEnd = 6; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[aa{a]aa} ------------------------ */
        mInitSelectionStart = 4; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", false);

        /* ----------------------- aa[aaa]{a}a ------------------------ */
        mInitSelectionStart = 5; mInitSelectionEnd = 6; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", true);

        /* ----------------------- aa[aaa]{aa} ------------------------ */
        mInitSelectionStart = 5; mInitSelectionEnd = 7; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaa\"}", true);
    }

    /**
     * aa[aaa]aa[aaa]aa
     */
    @Test
    public void testCase102() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":6}],\"text\":\"aaaaaaaaaa\"}";


        /* ----------------------- {aa[aa]aa}[aa]aa ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 6; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":6}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- {aa[aa]aa[a}a]aa ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":7}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- {aa[aa]aa[aa]}aa ------------------------ */
        mInitSelectionStart = 0; mInitSelectionEnd = 8; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa{[aa]aa}[aa]aa ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 6; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":6}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa{[aa]aa[a}a]aa ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":7}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa{[aa]aa[aa]}aa ------------------------ */
        mInitSelectionStart = 2; mInitSelectionEnd = 8; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa[a{a]aa}[aa]aa ------------------------ */
        mInitSelectionStart = 3; mInitSelectionEnd = 6; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":6}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa[a{a]aa[a}a]aa ------------------------ */
        mInitSelectionStart = 3; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":7}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa[a{a]aa[aa]}aa ------------------------ */
        mInitSelectionStart = 3; mInitSelectionEnd = 8; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa[aa]{aa}[aa]aa ------------------------ */
        mInitSelectionStart = 4; mInitSelectionEnd = 6; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaaaaa\"}", true);

        /* ----------------------- aa[aa]{aa[a}a]aa ------------------------ */
        mInitSelectionStart = 4; mInitSelectionEnd = 7; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":7}],\"text\":\"aaaaaaaaaa\"}", false);

        /* ----------------------- aa[aa]{aa[aa}]aa ------------------------ */
        mInitSelectionStart = 4; mInitSelectionEnd = 8; mExpectInitSelected = true;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaaaaaaa\"}", false);
    }


    /**
     * {\n}
     */
    @Test
    public void testCase110() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"\\n\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = false;
        check("{\"spans\":[],\"text\":\"\\n\"}", true);
    }

    /**
     * {a\n}
     */
    @Test
    public void testCase111() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\\n\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 2; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\\n\"}", true);
    }

    /**
     * {\na}
     */
    @Test
    public void testCase112() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 2; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"\\na\"}", true);
    }

    /**
     * {a\na}
     */
    @Test
    public void testCase113() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = false;
        check("{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}", true);
    }

    /**
     * {[a]\n}
     */
    @Test
    public void testCase114() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\\n\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 2; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"a\\n\"}", false);
    }

    /**
     * {\n[a]}
     */
    @Test
    public void testCase115() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 2; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"\\na\"}", false);
    }

    /**
     * {[a]\n[a]}
     */
    @Test
    public void testCase116() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"a\\na\"}", false);
    }

    /**
     * {a\n[a]}
     */
    @Test
    public void testCase117() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"a\\na\"}", false);
    }

    /**
     * {[a]\na}
     */
    @Test
    public void testCase118() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = true;
        check("{\"spans\":[],\"text\":\"a\\na\"}", false);
    }

}
