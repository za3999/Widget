package widget.cf.com.widgetlibrary.face;

import android.graphics.Rect;

import java.util.List;

public class FaceDetectUtil {
    public interface IFaceDetectListener {
        void onSuccess(List<Rect> faceRectList);

        void onFail(Exception e);
    }
}
