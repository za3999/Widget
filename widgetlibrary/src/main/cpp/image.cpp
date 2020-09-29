#include <jni.h>
#include <stdio.h>
#include <setjmp.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <android/bitmap.h>
#include "c_utils.h"


#define SQUARE(i) ((i)*(i))

inline static void zeroClearInt(int *p, size_t count) { memset(p, 0, sizeof(int) * count); }

extern "C" JNIEXPORT void
Java_widget_cf_com_widgetlibrary_jni_ImageNativeUtil_stackBlurBitmap(JNIEnv *env, jclass clazz,
                                                                     jobject bitmap, jint radius) {
//    if (radius < 1) return;
//
//    AndroidBitmapInfo info;
//    if (AndroidBitmap_getInfo(env, bitmap, &info) != ANDROID_BITMAP_RESULT_SUCCESS)
//        return;
//    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
//        return;
//
//    int w = info.width;
//    int h = info.height;
//    int stride = info.stride;
//
//    unsigned char *pixels = 0;
//    AndroidBitmap_lockPixels(env, bitmap, (void **) &pixels);
//    if (!pixels) {
//        return;
//    }
//    // Constants
//    //const int radius = (int)inradius; // Transform unsigned into signed for further operations
//    const int wm = w - 1;
//    const int hm = h - 1;
//    const int wh = w * h;
//    const int div = radius + radius + 1;
//    const int r1 = radius + 1;
//    const int divsum = SQUARE((div + 1) >> 1);
//
//    // Small buffers
//    int stack[div * 3];
//    zeroClearInt(stack, div * 3);
//
//    int vmin[MAX(w, h)];
//    zeroClearInt(vmin, MAX(w, h));
//
//    // Large buffers
//    int *r = static_cast<int *>(malloc(wh * sizeof(int)));
//    int *g = static_cast<int *>(malloc(wh * sizeof(int)));
//    int *b = static_cast<int *>(malloc(wh * sizeof(int)));
//    zeroClearInt(r, wh);
//    zeroClearInt(g, wh);
//    zeroClearInt(b, wh);
//
//    const size_t dvcount = 256 * divsum;
//    int *dv = static_cast<int *>(malloc(sizeof(int) * dvcount));
//    int i;
//    for (i = 0; (size_t) i < dvcount; i++) {
//        dv[i] = (i / divsum);
//    }
//
//    // Variables
//    int x, y;
//    int *sir;
//    int routsum, goutsum, boutsum;
//    int rinsum, ginsum, binsum;
//    int rsum, gsum, bsum, p, yp;
//    int stackpointer;
//    int stackstart;
//    int rbs;
//
//    int yw = 0, yi = 0;
//    for (y = 0; y < h; y++) {
//        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
//
//        for (i = -radius; i <= radius; i++) {
//            sir = &stack[(i + radius) * 3];
//            int offset = (y * stride + (MIN(wm, MAX(i, 0))) * 4);
//            sir[0] = pixels[offset];
//            sir[1] = pixels[offset + 1];
//            sir[2] = pixels[offset + 2];
//
//            rbs = r1 - abs(i);
//            rsum += sir[0] * rbs;
//            gsum += sir[1] * rbs;
//            bsum += sir[2] * rbs;
//            if (i > 0) {
//                rinsum += sir[0];
//                ginsum += sir[1];
//                binsum += sir[2];
//            } else {
//                routsum += sir[0];
//                goutsum += sir[1];
//                boutsum += sir[2];
//            }
//        }
//        stackpointer = radius;
//
//        for (x = 0; x < w; x++) {
//            r[yi] = dv[rsum];
//            g[yi] = dv[gsum];
//            b[yi] = dv[bsum];
//
//            rsum -= routsum;
//            gsum -= goutsum;
//            bsum -= boutsum;
//
//            stackstart = stackpointer - radius + div;
//            sir = &stack[(stackstart % div) * 3];
//
//            routsum -= sir[0];
//            goutsum -= sir[1];
//            boutsum -= sir[2];
//
//            if (y == 0) {
//                vmin[x] = MIN(x + radius + 1, wm);
//            }
//
//            int offset = (y * stride + vmin[x] * 4);
//            sir[0] = pixels[offset];
//            sir[1] = pixels[offset + 1];
//            sir[2] = pixels[offset + 2];
//            rinsum += sir[0];
//            ginsum += sir[1];
//            binsum += sir[2];
//
//            rsum += rinsum;
//            gsum += ginsum;
//            bsum += binsum;
//
//            stackpointer = (stackpointer + 1) % div;
//            sir = &stack[(stackpointer % div) * 3];
//
//            routsum += sir[0];
//            goutsum += sir[1];
//            boutsum += sir[2];
//
//            rinsum -= sir[0];
//            ginsum -= sir[1];
//            binsum -= sir[2];
//
//            yi++;
//        }
//        yw += w;
//    }
//
//    for (x = 0; x < w; x++) {
//        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
//        yp = -radius * w;
//        for (i = -radius; i <= radius; i++) {
//            yi = MAX(0, yp) + x;
//
//            sir = &stack[(i + radius) * 3];
//
//            sir[0] = r[yi];
//            sir[1] = g[yi];
//            sir[2] = b[yi];
//
//            rbs = r1 - abs(i);
//
//            rsum += r[yi] * rbs;
//            gsum += g[yi] * rbs;
//            bsum += b[yi] * rbs;
//
//            if (i > 0) {
//                rinsum += sir[0];
//                ginsum += sir[1];
//                binsum += sir[2];
//            } else {
//                routsum += sir[0];
//                goutsum += sir[1];
//                boutsum += sir[2];
//            }
//
//            if (i < hm) {
//                yp += w;
//            }
//        }
//        stackpointer = radius;
//        for (y = 0; y < h; y++) {
//            int offset = stride * y + x * 4;
//            pixels[offset] = dv[rsum];
//            pixels[offset + 1] = dv[gsum];
//            pixels[offset + 2] = dv[bsum];
//            rsum -= routsum;
//            gsum -= goutsum;
//            bsum -= boutsum;
//
//            stackstart = stackpointer - radius + div;
//            sir = &stack[(stackstart % div) * 3];
//
//            routsum -= sir[0];
//            goutsum -= sir[1];
//            boutsum -= sir[2];
//
//            if (x == 0) {
//                vmin[y] = (MIN(y + r1, hm)) * w;
//            }
//            p = x + vmin[y];
//
//            sir[0] = r[p];
//            sir[1] = g[p];
//            sir[2] = b[p];
//
//            rinsum += sir[0];
//            ginsum += sir[1];
//            binsum += sir[2];
//
//            rsum += rinsum;
//            gsum += ginsum;
//            bsum += binsum;
//
//            stackpointer = (stackpointer + 1) % div;
//            sir = &stack[stackpointer * 3];
//
//            routsum += sir[0];
//            goutsum += sir[1];
//            boutsum += sir[2];
//
//            rinsum -= sir[0];
//            ginsum -= sir[1];
//            binsum -= sir[2];
//
//            yi += w;
//        }
//    }
//
//    free(r);
//    free(g);
//    free(b);
//    free(dv);
//    AndroidBitmap_unlockPixels(env, bitmap);
}