package cc.brainbook.android.richeditortoolbar.span;

import com.google.gson.annotations.Expose;

public abstract class NestSpan {
    ///[NestingLevel]
    @Expose
    private int mNestingLevel;


    public NestSpan(int nestingLevel) {
        mNestingLevel = nestingLevel;
    }


    public int getNestingLevel() {
        return mNestingLevel;
    }

    public void setNestingLevel(int nestingLevel) {
        mNestingLevel = nestingLevel;
    }

}
