package cc.brainbook.android.richeditortoolbar.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.brainbook.android.richeditortoolbar.bean.SpanBean;
import cc.brainbook.android.richeditortoolbar.bean.TextBean;
import cc.brainbook.android.richeditortoolbar.span.BoldSpan;
import cc.brainbook.android.richeditortoolbar.util.ParcelUtil;
import cc.brainbook.android.richeditortoolbar.util.SpanUtil;

public abstract class RichEditorToolbarHelper {
    public static byte[] saveSpans(HashMap<View, Class> classHashMap, Editable editable, int selectionStart, int selectionEnd, boolean isSetText) {
        final TextBean textBean = new TextBean();
        if (isSetText) {
            final CharSequence subSequence = editable.subSequence(selectionStart, selectionEnd);
            textBean.setText(subSequence.toString());
        }

        final ArrayList<SpanBean> spanBeans = new ArrayList<>();
        for (Class clazz : classHashMap.values()) {
            getSpanToSpanBeans(spanBeans, clazz, editable, selectionStart, selectionEnd);
        }
        textBean.setSpans(spanBeans);

        return ParcelUtil.marshall(textBean);
    }

    public static boolean loadSpans(Editable editable, byte[] bytes) {
        final TextBean textBean = createTextBean(bytes);
        if (textBean != null) {
            if (textBean.getText() != null) {
                //////??????[BUG#ClipDescription的label总是为“host clipboard”]因此无法用label区分剪切板是否为RichEditor或其它App，只能用文本是否相同来“大约”区分
                if (!TextUtils.equals(textBean.getText(), editable)) {
                    return false;
                }

                ///注意：清除原有的span，比如BoldSpan的父类StyleSpan
                ///注意：必须保证selectionChanged()不被执行！否则死循环！
                editable.clearSpans();
            }

            final List<SpanBean> spanBeans = textBean.getSpans();
            setSpanFromSpanBeans(spanBeans, editable);

            return true;
        }

        return false;
    }

    public static void setSpanFromSpanBeans(List<SpanBean> spanBeans, Editable editable) {
        if (spanBeans != null) {
            for (SpanBean spanBean : spanBeans) {
                final int spanStart = spanBean.getSpanStart();
                final int spanEnd = spanBean.getSpanEnd();
                final int spanFlags = spanBean.getSpanFlags();
                final Object span = spanBean.getSpan();
                editable.setSpan(span, spanStart, spanEnd, spanFlags);
            }
        }
    }

    public static <T extends Parcelable> void getSpanToSpanBeans(List<SpanBean> spanBeans, Class<T> clazz, Editable editable, int start, int end) {
        final ArrayList<T> spans = SpanUtil.getFilteredSpans(editable, clazz, start, end);
        for (T span : spans) {
            ///注意：必须过滤掉没有CREATOR变量的span！
            ///理论上，所有RichEditor用到的span都应该自定义、且直接实现Parcelable（即该span类直接包含CREATOR变量），否则予以忽略
            try {
                clazz.getField("CREATOR");
                final int spanStart = editable.getSpanStart(span);
                final int spanEnd = editable.getSpanEnd(span);
                final int spanFlags = editable.getSpanFlags(span);
                final int adjustSpanStart = spanStart < start ? 0 : spanStart - start;
                final int adjustSpanEnd = (spanEnd > end ? end : spanEnd) - start;
                final SpanBean spanBean = new SpanBean((BoldSpan) span, adjustSpanStart, adjustSpanEnd, spanFlags);
                spanBeans.add(spanBean);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static TextBean createTextBean(byte[] bytes) {
        ///https://stackoverflow.com/questions/18000093/how-to-marshall-and-unmarshall-a-parcelable-to-a-byte-array-with-help-of-parcel
        final TextBean textBean = ParcelUtil.unmarshall(bytes, TextBean.CREATOR);
//        final Parcel parcel = ParcelUtil.unmarshall(bytes);
//        if (parcel == null) {
//            return null;
//        }
//
//        final TextBean textBean = TextBean.CREATOR.createFromParcel(parcel);
//
//        parcel.recycle();   ///使用完parcel后应及时回收

        return textBean;
    }

}
