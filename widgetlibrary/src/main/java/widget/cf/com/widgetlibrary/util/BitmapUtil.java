package widget.cf.com.widgetlibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.HandlerThread;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import java.lang.ref.SoftReference;
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

    public static Bitmap getBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }
}
