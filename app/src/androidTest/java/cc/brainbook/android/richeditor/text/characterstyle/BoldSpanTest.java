package cc.brainbook.android.richeditor.text.characterstyle;

import android.os.Parcelable;
import android.text.Selection;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;

import cc.brainbook.android.richeditor.EditorActivity;
import cc.brainbook.android.richeditor.R;
import cc.brainbook.android.richeditortoolbar.RichEditText;
import cc.brainbook.android.richeditortoolbar.RichEditorToolbar;
import cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static cc.brainbook.android.richeditortoolbar.helper.RichEditorToolbarHelper.fromJson;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

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
    private LinkedHashMap<Class<? extends Parcelable>, View> mClassMap;
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
        mClassMap = mRichEditorToolbar.getClassMap();
        mRichEditText = mRichEditorToolbar.getRichEditText();
    }


    private void check(final String replaceJsonString, String expectJsonString,
                       int expectSelectionStart, int expectSelectionEnd, boolean expectSelected) {
        ///注意：必须在UI线程操作View
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                ///[init]
                mRichEditorToolbar.isSkipTextWatcher = true;
                mRichEditText.setText(fromJson(mInitJsonString));
                mRichEditorToolbar.isSkipTextWatcher = false;
                ///重置光标
                ///否则同一test中多次调用check()时，如果SelectionStart, SelectionEnd无变化则Selection.setSelection()不会触发selectionChanged()
                Selection.removeSelection(mRichEditText.getText());
                ///设置光标
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
        onView(withId(R.id.iv_bold)).check(matches(mExpectInitSelected ? isSelected() : not(isSelected())));

        ///注意：必须在UI线程操作View
        mActivityRule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                ///[动作：替换text文本]参考RichEditText

                final int min = Selection.getSelectionStart(mRichEditText.getText());
                final int max = Selection.getSelectionEnd(mRichEditText.getText());
                mRichEditText.replace(mRichEditText.getText(), min, max, fromJson(replaceJsonString));

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

        ///[assert]
        final String result = RichEditorToolbarHelper.toJson(mClassMap, mRichEditText.getText(), 0, mRichEditText.getText().length(), true);
        assertEquals(result, expectJsonString);

        ///检查Selection
        assertEquals(Selection.getSelectionStart(mRichEditText.getText()), expectSelectionStart);
        assertEquals(Selection.getSelectionEnd(mRichEditText.getText()), expectSelectionEnd);

        ///检查是否Selected
        onView(withId(R.id.iv_bold)).check(matches(expectSelected ? isSelected() : not(isSelected())));
    }


    private String mReplaceJsonString0 = "{\"spans\":[],\"text\":\"\"}";///
    private String mReplaceJsonString1 = "{\"spans\":[],\"text\":\"a\"}";///a
    private String mReplaceJsonString2 = "{\"spans\":[],\"text\":\"\\n\"}";///\n
    private String mReplaceJsonString3 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";///[a]
    private String mReplaceJsonString4 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}";///a[a]
    private String mReplaceJsonString5 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";///[a]a
    private String mReplaceJsonString6 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}";///a[a]a
    private String mReplaceJsonString7 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}";///a[a]a[a]
    private String mReplaceJsonString8 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}";///[a]a[a]a
    private String mReplaceJsonString9 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}";///a[a]a[a]a
    private String mReplaceJsonString10 = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}";///[a]a[a]

    
    /**
     * {}
     */
    @Test
    public void testCase000() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * {}a
     */
    @Test
    public void testCase001() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 3, true);

    }

    /**
     * a{}
     */
    @Test
    public void testCase002() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}",
                1, 4, true);

    }

    /**
     * a{}a
     */
    @Test
    public void testCase003() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }

    /**
     * {}[a]
     */
    @Test
    public void testCase004() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 0; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                0, 1, false);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"\\na\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":5},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 3, true);

    }

    /**
     * [a]{}
     */
    @Test
    public void testCase005() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\\n\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 4, true);

    }

    /**
     * [a{}a]
     */
    @Test
    public void testCase006() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 1; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }


    /**
     * {a}
     */
    @Test
    public void testCase010() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * {a}a
     */
    @Test
    public void testCase011() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"aa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 3, true);

    }

    /**
     * a{a}
     */
    @Test
    public void testCase012() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}",
                1, 4, true);

    }

    /**
     * a{a}a
     */
    @Test
    public void testCase013() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"aaa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */

        /* ----------------------- replace with:{\n} ------------------------ */

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }

    /**
     * {a}[a]
     */
    @Test
    public void testCase014() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                0, 1, false);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"\\na\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":5},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 3, true);

    }

    /**
     * [a]{a}
     */
    @Test
    public void testCase015() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 2, false);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\\n\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}",
                1, 4, true);

    }

    /**
     * [a{a}a]
     */
    @Test
    public void testCase016() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }


    /**
     * {[a]}
     */
    @Test
    public void testCase020() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\n\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * {[a]}a
     */
    @Test
    public void testCase021() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"a\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\na\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 3, true);

    }

    /**
     * a{[a]}
     */
    @Test
    public void testCase022() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"a\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"a\\n\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 4, true);

    }

    /**
     * a{[a]}a
     */
    @Test
    public void testCase023() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"aa\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"a\\na\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }

    /**
     * {[a}a]
     */
    @Test
    public void testCase024() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"\\na\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 3, true);

    }

    /**
     * [a{a]}
     */
    @Test
    public void testCase025() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\\n\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 4, true);

    }


    /**
     * {a[aa]aa}[aa]a
     */
    @Test
    public void testCase030() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 5; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"\\naaa\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                0, 3, true);

    }

    /**
     * {a[aa]aa[a}a]a
     */
    @Test
    public void testCase031() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 6; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"\\naa\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 3, true);

    }

    /**
     * {a[aa]aa[aa]}a
     */
    @Test
    public void testCase032() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 7; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"a\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\na\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 3, true);

    }

    /**
     * {a[aa]aa[aa]a}
     */
    @Test
    public void testCase033() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 8; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\n\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * a{[aa]aa}[aa]a
     */
    @Test
    public void testCase034() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 5; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\naaa\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                1, 4, true);

    }

    /**
     * a{[aa]aa[a}a]a
     */
    @Test
    public void testCase035() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 6; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\naa\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 4, true);

    }

    /**
     * a{[aa]aa[aa]a}
     */
    @Test
    public void testCase036() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 8; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"a\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"a\\n\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                1, 4, true);

    }

    /**
     * a[a{a]aa}[aa]a
     */
    @Test
    public void testCase037() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 2; mInitSelectionEnd = 5; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                2, 2, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 3, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aa\\naaa\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 3, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                2, 5, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":9,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaaa\"}",
                2, 7, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                2, 5, true);

    }

    /**
     * a[a{a]aa[a}a]a
     */
    @Test
    public void testCase038() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 2; mInitSelectionEnd = 6; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                2, 2, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                2, 3, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aa\\naa\"}",
                2, 3, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                2, 3, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                2, 5, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaa\"}",
                2, 7, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                2, 5, true);

    }

    /**
     * a[a{a]aa[aa]}a
     */
    @Test
    public void testCase039() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 2; mInitSelectionEnd = 7; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                2, 2, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                2, 3, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\\na\"}",
                2, 3, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                2, 3, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 5, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                2, 7, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 5, true);

    }

    /**
     * a[a{a]aa[aa]a}
     */
    @Test
    public void testCase040() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 2; mInitSelectionEnd = 8; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                2, 2, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                2, 3, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\\n\"}",
                2, 3, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                2, 3, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                2, 4, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                2, 5, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                2, 6, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                2, 7, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                2, 5, true);

    }

    /**
     * a[aa]{aa}[aa]a
     */
    @Test
    public void testCase041() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 3; mInitSelectionEnd = 5; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                3, 3, false);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaa\"}",
                3, 4, false);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaa\\naaa\"}",
                3, 4, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                3, 4, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":6},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaaaa\"}",
                3, 6, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":9,\"spanFlags\":34,\"spanStart\":6}],\"text\":\"aaaaaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":9,\"spanFlags\":34,\"spanStart\":7},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":10,\"spanFlags\":34,\"spanStart\":8},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":6}],\"text\":\"aaaaaaaaaaa\"}",
                3, 8, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaaa\"}",
                3, 6, true);

    }

    /**
     * a[aa]{aa[a}a]a
     */
    @Test
    public void testCase042() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 3; mInitSelectionEnd = 6; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                3, 3, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                3, 4, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaa\\naa\"}",
                3, 4, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                3, 4, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                3, 6, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":9,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaaa\"}",
                3, 8, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                3, 6, true);

    }

    /**
     * a[aa]{aa[aa]}a
     */
    @Test
    public void testCase043() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 3; mInitSelectionEnd = 7; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                3, 3, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                3, 4, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\\na\"}",
                3, 4, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                3, 4, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                3, 6, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaaa\"}",
                3, 8, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                3, 6, true);

    }

    /**
     * a[aa]{aa[aa]a}
     */
    @Test
    public void testCase044() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":5}],\"text\":\"aaaaaaaa\"}";
        mInitSelectionStart = 3; mInitSelectionEnd = 8; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                3, 3, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                3, 4, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\\n\"}",
                3, 4, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaa\"}",
                3, 4, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaa\"}",
                3, 5, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                3, 6, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaa\"}",
                3, 7, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":8,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaaaa\"}",
                3, 8, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaaaaa\"}",
                3, 6, true);

    }


    /**
     * {\n}
     */
    @Test
    public void testCase100() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"\\n\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 1; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"\"}",
                0, 0, false);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[],\"text\":\"a\"}",
                0, 1, false);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\n\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * {a\na}
     */
    @Test
    public void testCase101() {
        ///[init]
        mInitJsonString = "{\"spans\":[],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"\"}",
                0, 0, false);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[],\"text\":\"a\"}",
                0, 1, false);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\n\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":1},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * {a\n[a]}
     */
    @Test
    public void testCase102() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\n\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * {[a]\na}
     */
    @Test
        public void testCase103() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\n\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * {[a]\n[a]}
     */
    @Test
    public void testCase104() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}";
        mInitSelectionStart = 0; mInitSelectionEnd = 3; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[],\"text\":\"\"}",
                0, 0, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[],\"text\":\"\\n\"}",
                0, 1, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"a\"}",
                0, 1,true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                0, 2, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                0, 4, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                0, 5, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                0, 3, true);

    }

    /**
     * [a{a]\n}[a]
     */
    @Test
    public void testCase105() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aa\\na\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 3; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }

    /**
     * [a]{\n[a}a]
     */
    @Test
    public void testCase106() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\naa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 3; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }

    /**
     * [a{a]\n[a}a]
     */
    @Test
    public void testCase107() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aa\\naa\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 4; mExpectInitSelected = true;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 1, true);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }

    /**
     * [a]{\n}[a]
     */
    @Test
    public void testCase108() {
        ///[init]
        mInitJsonString = "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}";
        mInitSelectionStart = 1; mInitSelectionEnd = 2; mExpectInitSelected = false;

        /* ----------------------- replace with:{} ------------------------ */
        check(mReplaceJsonString0,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aa\"}",
                1, 1, false);

        /* ----------------------- replace with:{a} ------------------------ */
        check(mReplaceJsonString1,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaa\"}",
                1, 2, false);

        /* ----------------------- replace with:{\n} ------------------------ */
        check(mReplaceJsonString2,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"a\\na\"}",
                1, 2, false);

        /* ----------------------- replace with:[a] ------------------------ */
        check(mReplaceJsonString3,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaa\"}",
                1, 2, true);

        /* ----------------------- replace with:a[a] ------------------------ */
        check(mReplaceJsonString4,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:[a]a ------------------------ */
        check(mReplaceJsonString5,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0}],\"text\":\"aaaa\"}",
                1, 3, true);

        /* ----------------------- replace with:a[a]a ------------------------ */
        check(mReplaceJsonString6,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2}],\"text\":\"aaaaa\"}",
                1, 4, true);

        /* ----------------------- replace with:a[a]a[a] ------------------------ */
        check(mReplaceJsonString7,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:[a]a[a]a ------------------------ */
        check(mReplaceJsonString8,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":6,\"spanFlags\":34,\"spanStart\":5},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":4,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaaa\"}",
                1, 5, true);

        /* ----------------------- replace with:a[a]a[a]a ------------------------ */
        check(mReplaceJsonString9,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":1,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":7,\"spanFlags\":34,\"spanStart\":6},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":3,\"spanFlags\":34,\"spanStart\":2},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":4}],\"text\":\"aaaaaaa\"}",
                1, 6, true);

        /* ----------------------- replace with:[a]a[a] ------------------------ */
        check(mReplaceJsonString10,
                "{\"spans\":[{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":2,\"spanFlags\":34,\"spanStart\":0},{\"span\":{},\"spanClassName\":\"BoldSpan\",\"spanEnd\":5,\"spanFlags\":34,\"spanStart\":3}],\"text\":\"aaaaa\"}",
                1, 4, true);

    }

}
