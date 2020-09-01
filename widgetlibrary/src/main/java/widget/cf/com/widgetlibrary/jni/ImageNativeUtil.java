package widget.cf.com.widgetlibrary.jni;

import android.graphics.Bitmap;

public class ImageNativeUtil {

    public static native void stackBlurBitmap(Bitmap bitmap, int radius);

    static {
        System.loadLibrary("jni_image");
    }

}
