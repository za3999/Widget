package widget.cf.com.widgetlibrary.face;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.executor.PoolThread;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;
import widget.cf.com.widgetlibrary.util.BitmapUtil;
import widget.cf.com.widgetlibrary.util.LogUtils;

public class FaceHelper {

    public static final int FACE_DETECT_MAX_SIDE = 360;
    private static final int FIREBASE_MIN_SIZE = 32;

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

    // FaceDetector
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


    // FirebaseVisionFaceDetector
    public static void detectFaceAsync(FirebaseVisionFaceDetector faceDetector, @NonNull Bitmap oriBitmap, @NonNull FaceDetectUtil.IFaceDetectListener listener) {
        long start = System.currentTimeMillis();

        if (oriBitmap == null || oriBitmap.isRecycled() || listener == null) {
            if (listener != null) {
                listener.onFail(new IllegalArgumentException());
            }
            return;
        }

        Bitmap detectBitmap = oriBitmap;
        // 1. convert bitmap to proper small size
        float scaleRate = 1f;
        int maxSide = oriBitmap.getWidth() >= oriBitmap.getHeight() ? oriBitmap.getWidth() : oriBitmap.getHeight();
        if (maxSide > FACE_DETECT_MAX_SIDE) {
            float bitmapScaleRate = FACE_DETECT_MAX_SIDE * 1f / maxSide;
            if (1f - bitmapScaleRate > 0.01f) {
                scaleRate = bitmapScaleRate;
                detectBitmap = BitmapUtil.scaleBitmap(oriBitmap, scaleRate, false);
            }
        }

        // 2. real face detect
        List<Rect> faceRectList = new ArrayList<>();
        try {
            float finalScaleRate = scaleRate;
            Bitmap finalDetectBitmap = detectBitmap;

            // Firebase request bitmap which being detected should be bigger than 32
            int minWidth = detectBitmap.getWidth() > detectBitmap.getHeight() ? detectBitmap.getHeight() : detectBitmap.getWidth();
            if (minWidth <= FIREBASE_MIN_SIZE) {
                if (listener != null) {
                    listener.onSuccess(faceRectList);
                }
                if (finalDetectBitmap != oriBitmap && !finalDetectBitmap.isRecycled()) {
                    finalDetectBitmap.recycle();
                }
                return;
            }

            FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromBitmap(detectBitmap);
            faceDetector.detectInImage(firebaseImage)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                            if (finalDetectBitmap != oriBitmap && !finalDetectBitmap.isRecycled()) {
                                finalDetectBitmap.recycle();
                            }

                            if (firebaseVisionFaces != null && firebaseVisionFaces.size() > 0) {
                                for (FirebaseVisionFace face : firebaseVisionFaces) {
                                    Rect faceRect = face.getBoundingBox();
                                    List<FirebaseVisionPoint> contourPointList = face.getContour(
                                            FirebaseVisionFaceContour.FACE).getPoints();
                                    if (contourPointList != null) {
                                        for (FirebaseVisionPoint contour : contourPointList) {
                                            // TODO - convert to path/ClipPath later
                                        }
                                    }

                                    // reset size to original bitmap size
                                    if (Math.abs(finalScaleRate - 1f) > 0.01f) {
                                        faceRect.left = (int) (faceRect.left * 1f / finalScaleRate);
                                        faceRect.top = (int) (faceRect.top * 1f / finalScaleRate);
                                        faceRect.right = (int) (faceRect.right * 1f / finalScaleRate);
                                        faceRect.bottom = (int) (faceRect.bottom * 1f / finalScaleRate);
                                    }

                                    if (faceRect.top < 0) {
                                        faceRect.top = 0;
                                    }
                                    // Extends bottom line to try to include thin area to rect
                                    // Adjust rect area, to make it more match with the shape of face
                                    faceRect.bottom += faceRect.height() * 0.1f;
                                    faceRect.left += faceRect.width() * 0.08f;
                                    faceRect.right -= faceRect.width() * 0.08f;
                                    if (faceRect.left < 0) {
                                        faceRect.left = 0;
                                    }
                                    if (faceRect.right > oriBitmap.getWidth()) {
                                        faceRect.right = oriBitmap.getWidth();
                                    }
                                    if (faceRect.bottom > oriBitmap.getHeight()) {
                                        faceRect.bottom = oriBitmap.getHeight();
                                    }
                                    faceRectList.add(faceRect);
                                }
                            }

                            LogUtils.d("faceDetect", "FaceDetect with Firebase cost: " + (System.currentTimeMillis() - start) + ", face size=" + faceRectList.size());
                            listener.onSuccess(faceRectList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (finalDetectBitmap != oriBitmap && !finalDetectBitmap.isRecycled()) {
                                finalDetectBitmap.recycle();
                            }
                            e.printStackTrace();
                            listener.onFail(e);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            if (detectBitmap != oriBitmap && !detectBitmap.isRecycled()) {
                detectBitmap.recycle();
            }
            listener.onFail(e);
        }
    }
}
