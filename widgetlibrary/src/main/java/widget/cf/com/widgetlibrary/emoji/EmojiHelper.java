/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package widget.cf.com.widgetlibrary.emoji;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.TransformationMethod;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class EmojiHelper {

    private static boolean useSysEmoji = false;
    private static int maxEmojiLength = 15;
    private static int drawImgSize;
    private static int bigImgSize;
    private static boolean inited = false;
    private static Paint placeholderPaint;
    private static final int splitCount = 4;
    private static Bitmap emojiBmp[][] = new Bitmap[5][splitCount];
    private static boolean loadingEmoji[][] = new boolean[5][splitCount];
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap<>();
    private static int imageResize = 1;

    private static final int[][] cols = {
            {16, 16, 16, 16},
            {6, 6, 6, 6},
            {9, 9, 9, 9},
            {9, 9, 9, 9},
            {10, 10, 10, 10}
    };

    static {
        int add = 2 / imageResize;
        int emojiFullSize = 64 / imageResize;
        drawImgSize = ApplicationUtil.dp(20);
        bigImgSize = ApplicationUtil.dp(32);
        for (int j = 0; j < EmojiData.data.length; j++) {
            int count2 = (int) Math.ceil(EmojiData.data[j].length / (float) splitCount);
            int position;
            for (int i = 0; i < EmojiData.data[j].length; i++) {
                int page = i / count2;
                position = i - page * count2;
                int row = position % cols[j][page];
                int col = position / cols[j][page];
                Rect rect = new Rect(row * emojiFullSize + row * add, col * emojiFullSize + col * add, (row + 1) * emojiFullSize + row * add, (col + 1) * emojiFullSize + col * add);
                rects.put(EmojiData.data[j][i], new DrawableInfo(rect, (byte) j, (byte) page, i));
            }
        }
        placeholderPaint = new Paint();
        placeholderPaint.setColor(0x00000000);
    }

    public static boolean isUseSysEmoji() {
        return useSysEmoji;
    }

    public static void setUseSysEmoji(boolean useSysEmoji) {
        EmojiHelper.useSysEmoji = useSysEmoji;
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint paint) {
        return replaceEmoji(cs, paint, true);
    }

    public static CharSequence replaceEmoji(
            CharSequence cs,
            Paint paint,
            boolean isUserSystemEmoji,
            boolean isCreateNew) {
        return replaceEmoji(
                cs,
                (paint != null ? paint.getFontMetricsInt() : null),
                isUserSystemEmoji,
                isCreateNew,
                null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint paint, boolean createNew) {
        return replaceEmoji(cs, paint.getFontMetricsInt(), createNew, null);
    }

    private static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, boolean createNew, int[] emojiOnly) {
        if (useSysEmoji || cs == null || cs.length() == 0) {
            return cs;
        }
        Spannable s;
        if (!createNew && cs instanceof Spannable) {
            s = (Spannable) cs;
        } else {
            s = Spannable.Factory.getInstance().newSpannable(cs);
        }
        long buf = 0;
        int emojiCount = 0;
        char c;
        int startIndex = -1;
        int startLength = 0;
        int previousGoodIndex = 0;
        StringBuilder emojiCode = new StringBuilder(16);
        EmojiDrawable drawable;
        EmojiSpan span;
        int length = cs.length();
        boolean doneEmoji = false;
        try {
            for (int i = 0; i < length; i++) {
                c = cs.charAt(i);
                if (c >= 0xD83C && c <= 0xD83E || (buf != 0 && (buf & 0xFFFFFFFF00000000L) == 0 && (buf & 0xFFFF) == 0xD83C && (c >= 0xDDE6 && c <= 0xDDFF))) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    emojiCode.append(c);
                    startLength++;
                    buf <<= 16;
                    buf |= c;
                } else if (emojiCode.length() > 0 && (c == 0x2640 || c == 0x2642 || c == 0x2695)) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (buf > 0 && (c & 0xF000) == 0xD000) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (c == 0x20E3) {
                    if (i > 0) {
                        char c2 = cs.charAt(previousGoodIndex);
                        if ((c2 >= '0' && c2 <= '9') || c2 == '#' || c2 == '*') {
                            startIndex = previousGoodIndex;
                            startLength = i - previousGoodIndex + 1;
                            emojiCode.append(c2);
                            emojiCode.append(c);
                            doneEmoji = true;
                        }
                    }
                } else if ((c == 0x00A9 || c == 0x00AE || c >= 0x203C && c <= 0x3299) && EmojiData.dataCharsMap.containsKey(c)) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    startLength++;
                    emojiCode.append(c);
                    doneEmoji = true;
                } else if (startIndex != -1) {
                    emojiCode.setLength(0);
                    startIndex = -1;
                    startLength = 0;
                    doneEmoji = false;
                } else if (c != 0xfe0f) {
                    if (emojiOnly != null) {
                        emojiOnly[0] = 0;
                        emojiOnly = null;
                    }
                }
                if (doneEmoji && i + 2 < length) {
                    char next = cs.charAt(i + 1);
                    if (next == 0xD83C) {
                        next = cs.charAt(i + 2);
                        if (next >= 0xDFFB && next <= 0xDFFF) {
                            emojiCode.append(cs.subSequence(i + 1, i + 3));
                            startLength += 2;
                            i += 2;
                        }
                    } else if (emojiCode.length() >= 2 && emojiCode.charAt(0) == 0xD83C && emojiCode.charAt(1) == 0xDFF4 && next == 0xDB40) {
                        i++;
                        while (true) {
                            emojiCode.append(cs.subSequence(i, i + 2));
                            startLength += 2;
                            i += 2;
                            if (i >= cs.length() || cs.charAt(i) != 0xDB40) {
                                i--;
                                break;
                            }
                        }

                    }
                }
                previousGoodIndex = i;
                char prevCh = c;
                for (int a = 0; a < 3; a++) {
                    if (i + 1 < length) {
                        c = cs.charAt(i + 1);
                        if (a == 1) {
                            if (c == 0x200D && emojiCode.length() > 0) {
                                emojiCode.append(c);
                                i++;
                                startLength++;
                                doneEmoji = false;
                            }
                        } else if (startIndex != -1 || prevCh == '*' || prevCh >= '1' && prevCh <= '9') {
                            if (c >= 0xFE00 && c <= 0xFE0F) {
                                i++;
                                startLength++;
                            }
                        }
                    }
                }
                if (doneEmoji && i + 2 < length && cs.charAt(i + 1) == 0xD83C) {
                    char next = cs.charAt(i + 2);
                    if (next >= 0xDFFB && next <= 0xDFFF) {
                        emojiCode.append(cs.subSequence(i + 1, i + 3));
                        startLength += 2;
                        i += 2;
                    }
                }
                if (doneEmoji) {
                    if (emojiOnly != null) {
                        emojiOnly[0]++;
                    }
                    CharSequence code = emojiCode.subSequence(0, emojiCode.length());
                    drawable = EmojiHelper.getEmojiDrawable(code);
                    if (drawable != null) {
                        span = new EmojiSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM, fontMetrics);
                        s.setSpan(span, startIndex, startIndex + startLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        emojiCount++;
                    }
                    startLength = 0;
                    startIndex = -1;
                    emojiCode.setLength(0);
                    doneEmoji = false;
                }
            }
        } catch (Exception e) {
            return cs;
        }
        return s;
    }

    private static CharSequence replaceEmoji(
            CharSequence cs,
            Paint.FontMetricsInt fontMetrics,
            boolean isUserSystemEmoji,
            boolean createNew,
            int[] emojiOnly) {
        if (isUserSystemEmoji || cs == null || cs.length() == 0) {
            return cs;
        }
        Spannable s;
        if (!createNew && cs instanceof Spannable) {
            s = (Spannable) cs;
        } else {
            s = Spannable.Factory.getInstance().newSpannable(cs);
        }
        long buf = 0;
        int emojiCount = 0;
        char c;
        int startIndex = -1;
        int startLength = 0;
        int previousGoodIndex = 0;
        StringBuilder emojiCode = new StringBuilder(16);
        EmojiDrawable drawable;
        EmojiSpan span;
        int length = cs.length();
        boolean doneEmoji = false;
        try {
            for (int i = 0; i < length; i++) {
                c = cs.charAt(i);
                if (c >= 0xD83C && c <= 0xD83E || (buf != 0 && (buf & 0xFFFFFFFF00000000L) == 0 && (buf & 0xFFFF) == 0xD83C && (c >= 0xDDE6 && c <= 0xDDFF))) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    emojiCode.append(c);
                    startLength++;
                    buf <<= 16;
                    buf |= c;
                } else if (emojiCode.length() > 0 && (c == 0x2640 || c == 0x2642 || c == 0x2695)) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (buf > 0 && (c & 0xF000) == 0xD000) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (c == 0x20E3) {
                    if (i > 0) {
                        char c2 = cs.charAt(previousGoodIndex);
                        if ((c2 >= '0' && c2 <= '9') || c2 == '#' || c2 == '*') {
                            startIndex = previousGoodIndex;
                            startLength = i - previousGoodIndex + 1;
                            emojiCode.append(c2);
                            emojiCode.append(c);
                            doneEmoji = true;
                        }
                    }
                } else if ((c == 0x00A9 || c == 0x00AE || c >= 0x203C && c <= 0x3299) && EmojiData.dataCharsMap.containsKey(c)) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    startLength++;
                    emojiCode.append(c);
                    doneEmoji = true;
                } else if (startIndex != -1) {
                    emojiCode.setLength(0);
                    startIndex = -1;
                    startLength = 0;
                    doneEmoji = false;
                } else if (c != 0xfe0f) {
                    if (emojiOnly != null) {
                        emojiOnly[0] = 0;
                        emojiOnly = null;
                    }
                }
                if (doneEmoji && i + 2 < length) {
                    char next = cs.charAt(i + 1);
                    if (next == 0xD83C) {
                        next = cs.charAt(i + 2);
                        if (next >= 0xDFFB && next <= 0xDFFF) {
                            emojiCode.append(cs.subSequence(i + 1, i + 3));
                            startLength += 2;
                            i += 2;
                        }
                    } else if (emojiCode.length() >= 2 && emojiCode.charAt(0) == 0xD83C && emojiCode.charAt(1) == 0xDFF4 && next == 0xDB40) {
                        i++;
                        while (true) {
                            emojiCode.append(cs.subSequence(i, i + 2));
                            startLength += 2;
                            i += 2;
                            if (i >= cs.length() || cs.charAt(i) != 0xDB40) {
                                i--;
                                break;
                            }
                        }

                    }
                }
                previousGoodIndex = i;
                char prevCh = c;
                for (int a = 0; a < 3; a++) {
                    if (i + 1 < length) {
                        c = cs.charAt(i + 1);
                        if (a == 1) {
                            if (c == 0x200D && emojiCode.length() > 0) {
                                emojiCode.append(c);
                                i++;
                                startLength++;
                                doneEmoji = false;
                            }
                        } else if (startIndex != -1 || prevCh == '*' || prevCh >= '1' && prevCh <= '9') {
                            if (c >= 0xFE00 && c <= 0xFE0F) {
                                i++;
                                startLength++;
                            }
                        }
                    }
                }
                if (doneEmoji && i + 2 < length && cs.charAt(i + 1) == 0xD83C) {
                    char next = cs.charAt(i + 2);
                    if (next >= 0xDFFB && next <= 0xDFFF) {
                        emojiCode.append(cs.subSequence(i + 1, i + 3));
                        startLength += 2;
                        i += 2;
                    }
                }
                if (doneEmoji) {
                    if (emojiOnly != null) {
                        emojiOnly[0]++;
                    }
                    CharSequence code = emojiCode.subSequence(0, emojiCode.length());
                    drawable = EmojiHelper.getEmojiDrawable(code);
                    if (drawable != null) {
                        span = new EmojiSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM, fontMetrics);
                        s.setSpan(span, startIndex, startIndex + startLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        emojiCount++;
                    }
                    startLength = 0;
                    startIndex = -1;
                    emojiCode.setLength(0);
                    doneEmoji = false;
                }
            }
        } catch (Exception e) {
            return cs;
        }
        return s;
    }

    public static CharSequence getCutString(CharSequence text, int end) {
        if (end < 0) {
            return Spannable.Factory.getInstance().newSpannable(text.subSequence(0, end));
        }

        CharSequence charSequence = EmojiHelper.replaceEmoji(text + " ", new Paint());
        if (charSequence instanceof Spannable) {
            Spannable textSpannable = (Spannable) charSequence;
            EmojiSpan[] spans = textSpannable.getSpans(0, textSpannable.length(), EmojiSpan.class);
            for (EmojiSpan span : spans) {
                int spanStart = textSpannable.getSpanStart(span);
                int spanEnd = textSpannable.getSpanEnd(span);
                int spanLength = spanEnd - spanStart;
                if (spanStart == end - 1) {
                    end = spanEnd;
                    break;
                } else if (spanStart > end - 1) {
                    break;
                } else {
                    end += spanLength - 1;
                }
            }
            return charSequence.subSequence(0, Math.min(end, text.length()));
        } else {
            return Spannable.Factory.getInstance().newSpannable(text.subSequence(0, end));
        }
    }

    public static int getLengthWhenEmojiEnd(CharSequence inputStr, int index) {
        int length = -1;
        if (inputStr instanceof Spannable) {
            Spannable textSpannable = (Spannable) inputStr;
            int start = index > 20 ? index - 20 : 0;
            EmojiSpan[] spans = textSpannable.getSpans(start, index, EmojiSpan.class);
            if (spans != null && spans.length > 0) {
                EmojiSpan lastSpans = spans[spans.length - 1];
                int spanEnd = textSpannable.getSpanEnd(lastSpans);
                if (spanEnd == index) {
                    length = spanEnd - textSpannable.getSpanStart(lastSpans);
                }
            }
        }
        return length;
    }


    public static Drawable getEmojiBigDrawable(String code) {
        EmojiDrawable source = getEmojiDrawable(code);
        EmojiDrawable result = null;
        if (source != null) {
            result = new EmojiDrawable(code, source.info);
            result.setBounds(0, 0, bigImgSize, bigImgSize);
            result.fullSize = true;
        }
        return result;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence code) {
        DrawableInfo info = rects.get(code);
        if (info == null) {
            CharSequence newCode = EmojiData.emojiAliasMap.get(code);
            if (newCode != null) {
                info = rects.get(newCode);
            }
        }
        if (info != null) {
            return new EmojiDrawable(code, info);
        } else {
            return null;
        }
    }

    public static boolean isExistEmojiSpan(Spanned text, int start, int end) {
        start = Math.max(0, start);
        end = Math.min(text.length(), end);
        return text.getSpans(start, end, EmojiHelper.EmojiSpan.class).length > 0;
    }

    public static void loadEmoji() {
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 3; j++) {
                EmojiHelper.loadEmoji(i, j);
            }
        }
    }

    private static void loadEmoji(final int page, final int page2) {
        try {
            float scale = 2.0f;
            Bitmap bitmap = null;
            try {
                InputStream is = ApplicationUtil.getApplication().getAssets().open("emoji/" + String.format(Locale.US, "v13_emoji%.01fx_%d_%d.png", scale, page, page2));
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = false;
                opts.inSampleSize = imageResize;
                bitmap = BitmapFactory.decodeStream(is, null, opts);
                is.close();
            } catch (Throwable e) {
            }
            emojiBmp[page][page2] = bitmap;
        } catch (Throwable x) {
        }
    }


    public static InputFilter[] getFilters(@NonNull final InputFilter[] filters, TextView
            textView) {
        if (useSysEmoji) {
            return filters;
        }
        final int count = filters.length;
        for (int i = 0; i < count; i++) {
            if (filters[i] instanceof EmojiInputFilter) {
                return filters;
            }
        }
        final InputFilter[] newFilters = new InputFilter[filters.length + 1];
        System.arraycopy(filters, 0, newFilters, 1, count);
        newFilters[0] = new EmojiInputFilter(textView);
        return newFilters;
    }

    public static TransformationMethod wrapTransformationMethod(TransformationMethod tm) {
        if (useSysEmoji) {
            return tm;
        }
        if (tm instanceof EmojiTransformationMethod) {
            return tm;
        }
        return new EmojiTransformationMethod(tm);
    }

    private static class EmojiTransformationMethod implements TransformationMethod {
        private final TransformationMethod mTransformationMethod;

        EmojiTransformationMethod(TransformationMethod transformationMethod) {
            mTransformationMethod = transformationMethod;
        }

        @Override
        public CharSequence getTransformation(@Nullable CharSequence source, @NonNull final View view) {
            if (view.isInEditMode()) {
                return source;
            }
            if (source != null && view instanceof TextView) {
                TextView textView = (TextView) view;
                return EmojiHelper.replaceEmoji(source, textView.getPaint());
            }
            return source;
        }

        @Override
        public void onFocusChanged(final View view, final CharSequence sourceText,
                                   final boolean focused, final int direction, final Rect previouslyFocusedRect) {
            if (mTransformationMethod != null) {
                mTransformationMethod.onFocusChanged(view, sourceText, focused, direction, previouslyFocusedRect);
            }
        }
    }

    private static class EmojiInputFilter implements InputFilter {
        private final TextView mTextView;

        EmojiInputFilter(@NonNull final TextView textView) {
            mTextView = textView;
        }

        @Override
        public CharSequence filter(final CharSequence source, final int sourceStart,
                                   final int sourceEnd, final Spanned dest, final int destStart, final int destEnd) {
            if (mTextView.isInEditMode()) {
                return source;
            }
            return EmojiHelper.replaceEmoji(source, mTextView.getPaint());
        }
    }

    public static class EmojiSpan extends ImageSpan {
        private Paint.FontMetricsInt fontMetrics;
        private int size;
        CharSequence code;

        public EmojiSpan(EmojiDrawable d, int verticalAlignment, Paint.FontMetricsInt original) {
            super(d, verticalAlignment);
            code = d.code;
            setFontMetricsInt(original);
        }

        public void setFontMetricsInt(Paint.FontMetricsInt original) {
            fontMetrics = original;
            if (original != null) {
                size = Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.ascent);
                if (size == 0) {
                    size = ApplicationUtil.dp(20);
                }
            }
        }

        public CharSequence getCode() {
            return code;
        }

        public void replaceFontMetrics(Paint.FontMetricsInt newMetrics, int newSize) {
            fontMetrics = newMetrics;
            size = newSize;
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = new Paint.FontMetricsInt();
            }

            if (fontMetrics == null) {
                size = super.getSize(paint, text, start, end, fm);
                int offset = ApplicationUtil.dp(8);
                int w = ApplicationUtil.dp(10);
                fm.top = -w - offset;
                fm.bottom = w - offset;
                fm.ascent = -w - offset;
                fm.leading = 0;
                fm.descent = w - offset;
            } else {
                if (fm != null) {
                    fm.ascent = fontMetrics.ascent;
                    fm.descent = fontMetrics.descent;

                    fm.top = fontMetrics.top;
                    fm.bottom = fontMetrics.bottom;
                }
            }
            if (getDrawable() != null) {
                getDrawable().setBounds(0, 0, size, size);
            }
            return size;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int transY = (y + fontMetricsInt.descent + y + fontMetricsInt.ascent) / 2 - drawable.getBounds().bottom / 2;
            canvas.save();
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public static class EmojiDrawable extends Drawable {
        private DrawableInfo info;
        private boolean fullSize = false;
        private static Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        private static Rect rect = new Rect();
        private CharSequence code;

        public EmojiDrawable(CharSequence code, DrawableInfo i) {
            this.code = code;
            info = i;
        }

        public CharSequence getCode() {
            return code;
        }

        public Rect getDrawRect() {
            Rect original = getBounds();
            int cX = original.centerX(), cY = original.centerY();
            rect.left = cX - (fullSize ? bigImgSize : drawImgSize) / 2;
            rect.right = cX + (fullSize ? bigImgSize : drawImgSize) / 2;
            rect.top = cY - (fullSize ? bigImgSize : drawImgSize) / 2;
            rect.bottom = cY + (fullSize ? bigImgSize : drawImgSize) / 2;
            return rect;
        }

        @Override
        public void draw(Canvas canvas) {
            if (emojiBmp[info.page][info.page2] == null) {
                if (loadingEmoji[info.page][info.page2]) {
                    return;
                }
                loadingEmoji[info.page][info.page2] = true;
                loadEmojiAsync(info.page, info.page2);
                canvas.drawRect(getBounds(), placeholderPaint);
                return;
            }

            Rect b;
            if (fullSize) {
                b = getDrawRect();
            } else {
                b = getBounds();
            }
            //if (!canvas.quickReject(b.left, b.top, b.right, b.bottom, Canvas.EdgeType.AA)) {
            canvas.drawBitmap(emojiBmp[info.page][info.page2], info.rect, b, paint);
            //}
        }

        private void loadEmojiAsync(final int page, final int page2) {
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            loadEmoji(page, page2);
                            loadingEmoji[page][page2] = false;
                        }
                    }
            ).start();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }
    }

    private static class DrawableInfo {
        public Rect rect;
        public byte page;
        public byte page2;
        public int emojiIndex;

        public DrawableInfo(Rect r, byte p, byte p2, int index) {
            rect = r;
            page = p;
            page2 = p2;
            emojiIndex = index;
        }
    }
}