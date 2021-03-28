package cc.brainbook.android.richeditortoolbar.util;

import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.Application;

public abstract class AppUtil {
    ///[AppUtil#另外一种更优雅兼容Android P获取Application的方法]
    ///在本地定义的两个ActivityThread和AppGlobals要以android.app包名来命名，
    ///这样就可以欺骗编辑器，然后根据类加载器的委托机制，他会直接加载系统的ActivityThread和AppGlobals因此就可以直接获取application了
    ///https://www.jianshu.com/p/3628cdc19154
    public static Application getApplication() {
        Application application = null;

        try {
            //兼容android P，直接调用@hide注解的方法来获取application对象
            application = ActivityThread.currentApplication();
        } catch(Exception e) {
            e.printStackTrace();
        }

        if (application == null) {
            try {
                //兼容android P，直接调用@hide注解的方法来获取application对象
                application = AppGlobals.getInitialApplication();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return application;
    }

}
