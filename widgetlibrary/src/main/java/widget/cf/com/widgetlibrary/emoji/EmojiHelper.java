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
import android.os.Build;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.Container3;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class EmojiHelper {

    private static boolean useSysEmoji = false;
    private static int maxEmojiLength = 15;
    private static int drawImgSize;
    private static int bigImgSize;
    private static Paint placeholderPaint;
    private static int[] emojiCounts = new int[]{1620, 184, 115, 328, 125, 206, 288, 258};
    private static Bitmap[][] emojiBmp = new Bitmap[8][];
    private static boolean[][] loadingEmoji = new boolean[8][];
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap<>();
    private static int imageResize = 1;
    private static volatile Set<IEmojiObserve> emojiObserves = new HashSet<>();
    private static boolean loadPage = true;

    static {
        drawImgSize = ApplicationUtil.getIntDimension(R.dimen.dp_20);
        bigImgSize = ApplicationUtil.getIntDimension(R.dimen.dp_32);
        for (int a = 0; a < emojiBmp.length; a++) {
            emojiBmp[a] = new Bitmap[emojiCounts[a]];
            loadingEmoji[a] = new boolean[emojiCounts[a]];
        }

        for (int j = 0; j < EmojiData.data.length; j++) {
            for (int i = 0; i < EmojiData.data[j].length; i++) {
                rects.put(EmojiData.data[j][i], new DrawableInfo((byte) j, (short) i, i));
            }
        }
        placeholderPaint = new Paint();
        placeholderPaint.setColor(0x00000000);
    }

    public static void register(IEmojiObserve emojiObserve) {
        ApplicationUtil.getMainHandler().post(() -> emojiObserves.add(emojiObserve));
    }

    public static void unRegister(IEmojiObserve emojiObserve) {
        ApplicationUtil.getMainHandler().post(() -> emojiObserves.remove(emojiObserve));
    }

    private static void notifyEmojiLoaded() {
        ApplicationUtil.getMainHandler().post(() -> {
            Iterator<IEmojiObserve> it = emojiObserves.iterator();
            while (it.hasNext()) {
                it.next().onEmojiLoaded();
            }
        });
    }

    public static boolean isUseSysEmoji() {
        return useSysEmoji;
    }

    public static int getMaxEmojiLength() {
        return maxEmojiLength;
    }

    public static void setUseSysEmoji(boolean useSysEmoji) {
        EmojiHelper.useSysEmoji = useSysEmoji;
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint paint) {
        return replaceEmoji(cs, paint, true);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint paint, boolean createNew) {
        return replaceEmoji(cs, paint.getFontMetricsInt(), useSysEmoji, createNew, null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint paint, boolean isUserSystemEmoji, boolean isCreateNew) {
        return replaceEmoji(cs, (paint != null ? paint.getFontMetricsInt() : null), isUserSystemEmoji, isCreateNew, null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, boolean isUserSystemEmoji, boolean createNew, int[] emojiOnly) {
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
                if ((Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 29) && emojiCount >= 50) {
                    break;
                }
            }
        } catch (Exception e) {
            return cs;
        }
        return s;
    }

    public static CharSequence getCutString(CharSequence text, int start, int end) {
        if (start < 0 || end < 0 || start < end) {
            return text;
        }
        ArrayList<Container3<String, Integer, Integer>> emojiList = getEmojiLocationList(text, 0, end);
        for (Container3<String, Integer, Integer> emojiStr : emojiList) {
            int spanStart = emojiStr.second;
            int spanEnd = emojiStr.third;
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
        return text.subSequence(0, Math.min(end, text.length()));
    }

    public static CharSequence getCutEmojiString(CharSequence text, int end) {
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
                    checkEmojiSync(span);
                    break;
                } else if (spanStart > end - 1) {
                    break;
                } else {
                    checkEmojiSync(span);
                    end += spanLength - 1;
                }
            }
            return charSequence.subSequence(0, Math.min(end, text.length()));
        } else {
            return Spannable.Factory.getInstance().newSpannable(text.subSequence(0, end));
        }
    }

    public static int getEmojiSpanEnd(CharSequence inputStr, int index) {
        int spanEnd = -1;
        if (inputStr instanceof Spannable) {
            Spannable textSpannable = (Spannable) inputStr;
            int start = index > 20 ? index - 20 : 0;
            EmojiSpan[] spans = textSpannable.getSpans(start, index, EmojiSpan.class);
            if (spans != null && spans.length > 0) {
                EmojiSpan emojiSpan = spans[spans.length - 1];
                spanEnd = textSpannable.getSpanEnd(emojiSpan);
            }
//            int spanStart = textSpannable.getSpanStart(span);
//            int spanEnd = textSpannable.getSpanEnd(span);
//            if (spanStart<0||spanEnd<0||spanEnd>=spanStart){
//                span=null;
//            }
        }
        return spanEnd;
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

    public static CharSequence substring(CharSequence text, int start, int end) {
        start = start < 0 ? 0 : start;
        if (TextUtils.isEmpty(text) || end < start) {
            return "";
        }
        int checkStart = end - maxEmojiLength < start ? start : end - maxEmojiLength;
        int checkEnd = text.length() > end + maxEmojiLength ? end + maxEmojiLength : text.length();
        ArrayList<Container3<String, Integer, Integer>> emojiLocationList = EmojiHelper.getEmojiLocationList(text, checkStart, checkEnd);
        for (Container3<String, Integer, Integer> location : emojiLocationList) {
            if (end > location.second && (end < location.third)) {
                end = location.second;
            }
        }
        return text.subSequence(start, end);
    }

    public static int getLength(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return 0;
        }
        int emojiLength = 0;
        ArrayList<Container3<String, Integer, Integer>> emojiList = EmojiHelper.getEmojiLocationList(charSequence, 0, charSequence.length());
        for (Container3<String, Integer, Integer> location : emojiList) {
            emojiLength += location.third - location.second;
        }
        return charSequence.length() - emojiLength + emojiList.size();
    }

    public static ArrayList<Container3<String, Integer, Integer>> getEmojiLocationList(CharSequence cs, int start, int end) {
        ArrayList<Container3<String, Integer, Integer>> emojiLocationList = new ArrayList();
        if (cs == null || cs.length() == 0) {
            return emojiLocationList;
        }
        long buf = 0;
        int emojiCount = 0;
        char c;
        int startIndex = -1;
        int startLength = 0;
        int previousGoodIndex = 0;
        StringBuilder emojiCode = new StringBuilder(16);
        int length = cs.length();
        boolean doneEmoji = false;
        try {
            for (int i = start; i < end; i++) {
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
                    CharSequence code = emojiCode.subSequence(0, emojiCode.length());
                    emojiLocationList.add(new Container3(code, startIndex, startIndex + startLength));
                    emojiCount++;
                    startLength = 0;
                    startIndex = -1;
                    emojiCode.setLength(0);
                    doneEmoji = false;
                }
                if ((Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 29) && emojiCount >= 50) {
                    break;
                }
            }
        } catch (Exception e) {
            return emojiLocationList;
        }
        return emojiLocationList;
    }

    public static Drawable getEmojiBigDrawable(String code) {
        EmojiDrawable ed = getEmojiDrawable(code);
        if (ed == null) {
            CharSequence newCode = EmojiData.emojiAliasMap.get(code);
            if (newCode != null) {
                ed = getEmojiDrawable(newCode);
            }
        }
        if (ed == null) {
            return null;
        }
        ed.setBounds(0, 0, bigImgSize, bigImgSize);
        ed.fullSize = true;
        return ed;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence code) {
        DrawableInfo info = rects.get(code);
        if (info == null) {
            CharSequence newCode = EmojiData.emojiAliasMap.get(code);
            if (newCode != null) {
                info = rects.get(newCode);
            }
        }
        if (info == null) {
            return null;
        }
        EmojiDrawable ed = new EmojiDrawable(info);
        ed.setBounds(0, 0, drawImgSize, drawImgSize);
        return ed;
    }

    public static boolean isExistEmojiSpan(Spanned text, int start, int end) {
        start = Math.max(0, start);
        end = Math.min(text.length(), end);
        return text.getSpans(start, end, EmojiHelper.EmojiSpan.class).length > 0;
    }

    private static void checkEmojiSync(EmojiSpan span) {
        EmojiDrawable drawable = (EmojiDrawable) span.getDrawable();
        if (emojiBmp[drawable.info.page][drawable.info.page2] == null) {
            loadEmoji(drawable.info.page, drawable.info.page2);
        }
    }

    private static void loadEmoji(final int page) {
        for (int i = 0; i < emojiCounts[page]; i++) {
            loadEmoji(page, i);
        }
    }

    private static void loadEmoji(final int page, final int page2) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = ApplicationUtil.getApplication().getAssets().open("emoji/" + String.format(Locale.US, "%d_%d.png", page, page2));
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = imageResize;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
        } catch (Throwable e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        final Bitmap finalBitmap = bitmap;
        emojiBmp[page][page2] = finalBitmap;
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

    private static class EmojiInputFilter implements android.text.InputFilter {
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
        private int size = ApplicationUtil.getIntDimension(R.dimen.dp_20);

        public EmojiSpan(EmojiDrawable d, int verticalAlignment, Paint.FontMetricsInt original) {
            super(d, verticalAlignment);
            fontMetrics = original;
            if (original != null) {
                size = Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.ascent);
                if (size == 0) {
                    size = ApplicationUtil.getIntDimension(R.dimen.dp_20);
                }
            }
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = new Paint.FontMetricsInt();
            }

            if (fontMetrics == null) {
                int sz = super.getSize(paint, text, start, end, fm);

                int offset = ApplicationUtil.getIntDimension(R.dimen.dp_8);
                int w = ApplicationUtil.getIntDimension(R.dimen.dp_10);
                fm.top = -w - offset;
                fm.bottom = w - offset;
                fm.ascent = -w - offset;
                fm.leading = 0;
                fm.descent = w - offset;

                return sz;
            } else {
                if (fm != null) {
                    fm.ascent = fontMetrics.ascent;
                    fm.descent = fontMetrics.descent;

                    fm.top = fontMetrics.top;
                    fm.bottom = fontMetrics.bottom;
                }
                if (getDrawable() != null) {
                    getDrawable().setBounds(0, 0, size, size);
                }
                return size;
            }
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

    private static void loadEmojiAsync(int page, int page2) {
        ApplicationUtil.getBgHandler().post(() -> {
            if (loadPage) {
                loadEmoji(page);
            } else {
                loadEmoji(page, page2);
            }
            loadingEmoji[page][loadPage ? 0 : page2] = false;
            notifyEmojiLoaded();
        });
    }

    public static class EmojiDrawable extends Drawable {
        private DrawableInfo info;
        private boolean fullSize = false;
        private static Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        private static Rect rect = new Rect();

        public EmojiDrawable(DrawableInfo i) {
            info = i;
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
                if (loadingEmoji[info.page][loadPage ? 0 : info.page2]) {
                    return;
                }
                loadingEmoji[info.page][loadPage ? 0 : info.page2] = true;
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
            canvas.drawBitmap(emojiBmp[info.page][info.page2], null, b, paint);
            //}
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
        public byte page;
        public short page2;
        public int emojiIndex;

        public DrawableInfo(byte p, short p2, int index) {
            page = p;
            page2 = p2;
            emojiIndex = index;
        }
    }
}