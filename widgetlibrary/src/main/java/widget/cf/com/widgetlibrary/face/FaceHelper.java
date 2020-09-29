package widget.cf.com.widgetlibrary.face;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.executor.TaskRunner;
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
            new TaskRunner.RunnerOnlyOutput<Bitmap>() {
                @Override
                public Bitmap runWrapper() {
                    return BitmapUtil.getMosaicBitmap(sourceBitmap, faceList, 10);
                }

                @Override
                public void onResultWrapper(Bitmap bitmap) {
                    faceBlurListener.onCallBack(bitmap);
                }
            }.setMainResult(ApplicationUtil.isMainThread()).start();

            faceBlurListener.onCallBack(sourceBitmap);
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
            new TaskRunner.RunnerOnlyOutput<Bitmap>() {
                @Override
                public Bitmap runWrapper() {
                    return BitmapUtil.getBlurBitmap(sourceBitmap, faceList, 25);
                }

                @Override
                public void onResultWrapper(Bitmap bitmap) {
                    faceBlurListener.onCallBack(bitmap);
                }
            }.setMainResult(ApplicationUtil.isMainThread()).start();

            faceBlurListener.onCallBack(sourceBitmap);
        });
    }

    private static void detectFace(@NonNull Bitmap targetBitmap, int maxFace, @NonNull BaseCallBack.CallBack1<List<Rect>> listener) {
        List<Rect> faceRectList = new ArrayList<>();
        if (targetBitmap == null || targetBitmap.isRecycled() || listener == null) {
            if (listener != null) {
                listener.onCallBack(faceRectList);
            }
            return;
        }
        new TaskRunner.RunnerOnlyOutput<List<Rect>>() {
            @Override
            public List<Rect> runWrapper() {
                Bitmap copyBmp = null;
                try {
                    copyBmp = targetBitmap.copy(Bitmap.Config.RGB_565, true);
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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (copyBmp != null && !copyBmp.isRecycled()) {
                        copyBmp.recycle();
                    }
                }
                return faceRectList;
            }

            @Override
            public void onResultWrapper(List<Rect> faceRectList) {
                listener.onCallBack(faceRectList);
            }
        }.setMainResult(ApplicationUtil.isMainThread()).start();
    }
}
