package widget.cf.com.widgetlibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import androidx.palette.graphics.Palette;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BitmapUtil {

    private static Map<String, BitmapUseage> softReferenceMap = new ConcurrentHashMap<>();

    private static class BitmapUseage {
        public SoftReference<Bitmap> bitmap;
        public long lastAttacheTime;
        public int usedTimes;
        public String key;

        public BitmapUseage(String key, SoftReference<Bitmap> bitmap) {
            this.bitmap = bitmap;
            this.key = key;
        }

        @Override
        public String toString() {
            return "BitmapUseage{" +
                    "lastAttacheTime=" + lastAttacheTime +
                    ", usedTimes=" + usedTimes +
                    ", key='" + key + '\'' +
                    '}';
        }
    }

    public static Bitmap rsBlur(Context context, Bitmap source, int radius) {
        final Bitmap inputBmp = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * 0.5f), (int) (source.getHeight() * 0.5f), false);
        RenderScript renderScript = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);
        scriptIntrinsicBlur.setRadius(radius);
        scriptIntrinsicBlur.forEach(output);
        output.copyTo(inputBmp);
        renderScript.destroy();
        inputBmp.setHasAlpha(true);
        return inputBmp;
    }


    public static Drawable rsBlur(Context context, Bitmap source, int radius, int alpha) {
        if (source == null) {
            return null;
        }
        String key = source.hashCode() + "";
        SoftReference<Bitmap> soft;
        Bitmap cacheBitmap;
        BitmapUseage bitmapUseage;

        if ((bitmapUseage = softReferenceMap.get(key)) == null
                || (soft = bitmapUseage.bitmap) == null
                || (cacheBitmap = soft.get()) == null
                || cacheBitmap.isRecycled()) {
            final Bitmap inputBmp = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * 0.5f), (int) (source.getHeight() * 0.5f), false);
            RenderScript renderScript = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
            final Allocation output = Allocation.createTyped(renderScript, input.getType());

            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            scriptIntrinsicBlur.setInput(input);
            scriptIntrinsicBlur.setRadius(radius);
            scriptIntrinsicBlur.forEach(output);
            output.copyTo(inputBmp);
            renderScript.destroy();
            if (alpha != 255) {
                inputBmp.setHasAlpha(true);
                cacheBitmap = Bitmap.createScaledBitmap(source, (int) (source.getWidth() * 0.5f), (int) (source.getHeight() * 0.5f), false);
                Canvas canvas = new Canvas(cacheBitmap);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setAlpha(alpha);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(inputBmp, 0, 0, paint);
                inputBmp.recycle();
            } else {
                cacheBitmap = inputBmp;
            }
            if (bitmapUseage == null) {
                bitmapUseage = new BitmapUseage(key, new SoftReference<>(cacheBitmap));
            } else {
                bitmapUseage.bitmap = new SoftReference<>(cacheBitmap);
                bitmapUseage.usedTimes = 0;
            }
            softReferenceMap.put(key, bitmapUseage);
        }
        bitmapUseage.lastAttacheTime = System.currentTimeMillis();
        BitmapDrawable drawable = new BitmapDrawable(cacheBitmap);
        return drawable;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static Bitmap changeColor(Bitmap originalBitmap, int color) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap result = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);
        return result;
    }

    public static Bitmap blurWallpaper(Bitmap src, int degree) {
        if (src == null) {
            return null;
        }
        Bitmap b;
        if (src.getHeight() > src.getWidth()) {
            b = Bitmap.createBitmap(Math.round(450f * src.getWidth() / src.getHeight()), 450, Bitmap.Config.ARGB_8888);
        } else {
            b = Bitmap.createBitmap(450, Math.round(450f * src.getHeight() / src.getWidth()), Bitmap.Config.ARGB_8888);
        }
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());
        new Canvas(b).drawBitmap(src, null, rect, paint);
//        ImageNativeUtil.stackBlurBitmap(b, degree);
        return b;
    }

    public static Bitmap getBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.destroyDrawingCache();
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }

    public static Bitmap getCoverBitmap(Bitmap bitmap, int coverColor) {
        if (bitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        paint.setColor(coverColor);
        canvas.drawRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), paint);
        return outBitmap;
    }

    public static Bitmap getBlurBitmap(Bitmap bitmapOriginal, List<Rect> rects, int radius) {
        if (bitmapOriginal == null || bitmapOriginal.isRecycled()) {
            return null;
        }
        try {
            // 1. copy origin bitmap
            Bitmap newBitmap = bitmapOriginal.copy(bitmapOriginal.getConfig(), true);
            Canvas canvas = new Canvas(newBitmap);
            // 2. blur rects
            Paint photoPaint = new Paint();
            photoPaint.setDither(true);
            photoPaint.setFilterBitmap(true);
            Paint rectPaint = new Paint();
            rectPaint.setAntiAlias(true);
            rectPaint.setStyle(Paint.Style.FILL);
            for (Rect factRect : rects) {
                Bitmap faceBitmap = Bitmap.createBitmap(bitmapOriginal, factRect.left, factRect.top, factRect.width(), factRect.height());
                faceBitmap = getBlurBitmap(faceBitmap, radius);
                canvas.drawBitmap(faceBitmap, factRect.left, factRect.top, photoPaint);
                if (!faceBitmap.isRecycled()) {
                    faceBitmap.recycle();
                }
            }
            return newBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBlurBitmap(Bitmap image, float radius) {
        RenderScript rs = RenderScript.create(ApplicationUtil.getApplication());
        Bitmap outputBitmap = Bitmap.createBitmap(image.getHeight(), image.getWidth(), image.getConfig());
        Allocation in = Allocation.createFromBitmap(rs, image);
        Allocation out = Allocation.createFromBitmap(rs, outputBitmap);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        intrinsicBlur.setRadius(radius);
        intrinsicBlur.setInput(in);
        intrinsicBlur.forEach(out);
        out.copyTo(outputBitmap);
        image.recycle();
        rs.destroy();
        return outputBitmap;
    }

    public static Bitmap getMosaicBitmap(Bitmap sourceBitmap, List<Rect> faceList, int radius) {
        final int width = sourceBitmap.getWidth();
        final int height = sourceBitmap.getHeight();
        final Bitmap outBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), sourceBitmap.getConfig());
        try {
            final int[] pixels = new int[width * height];
            sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    boolean isInFaceRect = false;
                    for (Rect faceRect : faceList) {
                        if (faceRect.contains(x, y)) {
                            isInFaceRect = true;
                            break;
                        }
                    }
                    if (isInFaceRect) {
                        int newX = x % radius;
                        int newY = y % radius;
                        pixels[y * width + x] = pixels[(y - newY) * width + x - newX];
                    }
                }
            }
            outBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outBitmap;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, float rate, boolean recyclerOrigbitmap) {
        if (bitmap == null || rate <= 0 || rate >= 1) {
            return bitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(rate, rate, bitmap.getWidth()/2, bitmap.getHeight()/2);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (recyclerOrigbitmap && bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        return scaledBitmap;
    }


    /**
     * 获取主题色
     * @param bitmap
     * @return
     */
    public static int getVibrantColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        return palette.getVibrantColor(Color.WHITE);
    }

    /**
     * 获取以 bitmap 为背景时的推荐标题颜色
     * @param bitmap
     * @return
     */
    public static int getVibrantSwitch(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        return palette.getVibrantSwatch().getRgb();
    }

}
