package cc.brainbook.android.richeditortoolbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ClickImageSpanDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static ClickImageSpanDialogFragment newInstance(String param1, String param2) {
        final ClickImageSpanDialogFragment fragment = new ClickImageSpanDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(ARG_PARAM1, param1);
        arguments.putString(ARG_PARAM2, param2);
        fragment.setArguments(arguments);    ///注意：fragment.setArguments(args)在翻屏时会自动保留参数！所以不使用构造来传递参数
        return fragment;
    }

    public ClickImageSpanDialogFragment() {
        super();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        Log.d("TAG", "onAttach()# ");
        super.onAttach(context);

        Log.d("TAG", "onAttach()# " + getTag());

        final Bundle arguments = getArguments();
        if (arguments != null) {
            Log.d("TAG", "onAttach()# " + arguments.getString(ARG_PARAM1));
            Log.d("TAG", "onAttach()# " + arguments.getString(ARG_PARAM2));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "onCreate()# ");
        super.onCreate(savedInstanceState);

        ///setRetainInstance已经废弃
//        ///只有调用了fragment的setRetainInstance(true)方法，并且因设备配置改变，托管Activity正在被销毁的条件下，fragment才会短暂的处于保留状态。
//        ///如果activity是因操作系统需要回收内存而被销毁，则所有的fragment也会随之销毁。
//        ///https://blog.csdn.net/gaugamela/article/details/56280384
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView()# ");

        ///[Dialog全屏]
        ///requestFeature() must be called before adding content
        if (getDialog() != null)
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreateView(inflater, container, savedInstanceState);

        ///[Dialog全屏]
        final Window window;
        if (getDialog() != null && (window = getDialog().getWindow()) != null) {
            ///注意：如果不执行setBackgroundDrawable()，全屏不生效！
            ///可设置任意颜色，如果仍然背景透明，则在R.layout.click_image_span_dialog中设置背景为android:background="?android:colorBackground"
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ///修改Dialog默认的padding为0
            window.getDecorView().setPadding(0, 0, 0, 0);
            ///修改LayoutParams为MATCH_PARENT
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        return inflater.inflate(R.layout.click_image_span_dialog, null);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("TAG", "onViewCreated()# ");
        super.onViewCreated(view, savedInstanceState);

//        initView(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.d("TAG", "onStart()# ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("TAG", "onResume()# ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("TAG", "onPause()# ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("TAG", "onStop()# ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d("TAG", "onDestroyView()# ");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.d("TAG", "onDetach()# ");
        super.onDetach();
    }

}
