package widget.cf.com.widgetlibrary.face;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.executor.PoolThread;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.BitmapUtil;

public class FaceHelper {

    public static void mosaicFace(Bitmap sourceBitmap, int maxFace, @NonNull BaseCallBack.CallBack1<Bitmap> faceBlurListener) {
        if (sourceBitmap == null || sourceBitmap.isRecycled() || faceBlurListener == null) {
            faceBlurListener.onCallBack(sourceBitmap);
            return;
        }
        detectFace(sourceBitmap, maxFace, faceList -> {
            if (faceList == null || faceList.size() == 0) {
                faceBlurListener.onCallBack(sourceBitmap);
                return;
            }
            new PoolThread<Void, Bitmap>(null) {

                @Override
                public Bitmap run(Void data) {
                    return BitmapUtil.getMosaicBitmap(sourceBitmap, faceList, 10);
                }

                @Override
                public void onException(Exception e) {
                    faceBlurListener.onCallBack(sourceBitmap);
                }

                @Override
                public void onResult(Bitmap result) {
                    faceBlurListener.onCallBack(result);
                }
            }.setMainResult(ApplicationUtil.isMainThread()).start();
        });
    }

    public static void blurFace(Bitmap sourceBitmap, int maxFace, @NonNull BaseCallBack.CallBack1<Bitmap> faceBlurListener) {
        if (sourceBitmap == null || sourceBitmap.isRecycled() || faceBlurListener == null) {
            faceBlurListener.onCallBack(sourceBitmap);
            return;
        }
        detectFace(sourceBitmap, maxFace, faceList -> {
            if (faceList == null || faceList.size() == 0) {
                faceBlurListener.onCallBack(sourceBitmap);
                return;
            }
            new PoolThread<Void, Bitmap>(null) {
                @Override
                public Bitmap run(Void data) {
                    return BitmapUtil.getBlurBitmap(sourceBitmap, faceList, 25);
                }

                @Override
                public void onException(Exception e) {
                    faceBlurListener.onCallBack(sourceBitmap);
                }

                @Override
                public void onResult(Bitmap result) {
                    faceBlurListener.onCallBack(result);
                }
            }.setMainResult(ApplicationUtil.isMainThread()).start();
        });
    }

    private static void detectFace(@NonNull Bitmap targetBitmap, int maxFace, @NonNull BaseCallBack.CallBack1<List<Rect>> listener) {
        if (targetBitmap == null || targetBitmap.isRecycled() || listener == null) {
            if (listener != null) {
                listener.onCallBack(new ArrayList<>());
            }
            return;
        }

        new PoolThread<Void, List<Rect>>(null) {

            @Override
            public List<Rect> run(Void data) {
                List<Rect> faceRectList = new ArrayList<>();
                Bitmap copyBmp = targetBitmap.copy(Bitmap.Config.RGB_565, true);
                FaceDetector.Face[] myFace = new FaceDetector.Face[maxFace];       //分配人脸数组空间
                FaceDetector myFaceDetect = new FaceDetector(copyBmp.getWidth(), copyBmp.getHeight(), maxFace);
                int totalFaceNum = myFaceDetect.findFaces(copyBmp, myFace);
                if (totalFaceNum > 0) {
                    for (int i = 0; i < totalFaceNum; i++) {
                        FaceDetector.Face face = myFace[i];
                        PointF myMidPoint = new PointF();
                        face.getMidPoint(myMidPoint);
                        float myEyesDistance = face.eyesDistance();
                        int left = (int) (myMidPoint.x - myEyesDistance);
                        int top = (int) (myMidPoint.y - myEyesDistance);
                        int right = (int) (myMidPoint.x + myEyesDistance);
                        int bottom = (int) (myMidPoint.y + myEyesDistance);
                        faceRectList.add(new Rect(left < 0 ? 0 : left, top < 0 ? 0 : top,
                                right > copyBmp.getWidth() ? copyBmp.getWidth() : right,
                                bottom > copyBmp.getHeight() ? copyBmp.getHeight() : bottom));
                        copyBmp.recycle();
                    }
                }
                return faceRectList;
            }

            @Override
            public void onException(Exception e) {
                listener.onCallBack(new ArrayList<>());
            }

            @Override
            public void onResult(List<Rect> result) {
                listener.onCallBack(result);
            }
        }.setMainResult(ApplicationUtil.isMainThread()).start();
    }
}
