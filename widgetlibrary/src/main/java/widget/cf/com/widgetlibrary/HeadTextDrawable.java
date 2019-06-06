package widget.cf.com.widgetlibrary;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.emoji.EmojiHelper;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class HeadTextDrawable extends ShapeDrawable {

    private List<Integer> bgColors = new ArrayList<>();
    private CharSequence drawText = "";
    private Paint textPaint;

    public HeadTextDrawable() {
        this(new OvalShape());
    }

    public HeadTextDrawable(Shape s) {
        this("", 0);
    }

    public HeadTextDrawable(String text, int id) {
        super(new OvalShape());

        init(id);

        if (!TextUtils.isEmpty(text)) {
            if (text.length() > 1) {
                text = text.substring(0, 1).toUpperCase() + text.substring(1);
                drawText = EmojiHelper.getCutString(text, 2);
            } else {
                text = text.substring(0, 1).toUpperCase();
                drawText = EmojiHelper.getCutString(text, 1);
            }
        }
    }

    private void init(int id) {

        bgColors.add(0xffed4e4e);
        bgColors.add(0xff6c9dd9);
        bgColors.add(0xffeec74e);
        bgColors.add(0xff8576f2);
        bgColors.add(0xff7bcd59);
        bgColors.add(0xffbe6cd9);
        bgColors.add(0xff63c9bf);
        bgColors.add(0xffee6293);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);

        getPaint().setColor(bgColors.get(Math.abs(id) % 8));
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56f,
                ApplicationUtil.getResources().getDisplayMetrics());
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56f,
                ApplicationUtil.getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        super.onDraw(shape, canvas, paint);

        if (!TextUtils.isEmpty(drawText)) {

            Rect rect = getBounds();

            textPaint.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,
                    ApplicationUtil.getResources().getDisplayMetrics()));

            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
                    - (fontMetrics.bottom - fontMetrics.descent);
            float baseline = rect.centerY() + distance;

            int emojiDrawableW = (int) ((fontMetrics.bottom - fontMetrics.top) * 0.9f);

            int firstX = rect.centerX() - emojiDrawableW / 2 + emojiDrawableW / 8;
            int secondX = rect.centerX() - emojiDrawableW / 2 - emojiDrawableW / 2 + emojiDrawableW;

            EmojiHelper.EmojiSpan[] emojiSpans = ((SpannableString) drawText).getSpans(0, drawText.length(), EmojiHelper.EmojiSpan.class);

            if (emojiSpans == null || emojiSpans.length == 0 || emojiSpans.length > 2) {

                canvas.drawText(drawText, 0, drawText.length(), rect.centerX(), baseline, textPaint);

            } else if (emojiSpans.length == 1) {

                int spanStart = ((SpannableString) drawText).getSpanStart(emojiSpans[0]);
                int spanEnd = ((SpannableString) drawText).getSpanEnd(emojiSpans[0]);

                if (spanStart == 0) {

                    emojiSpans[0].getDrawable().setBounds(0, 0, (int) (emojiDrawableW * 0.95f), (int) (emojiDrawableW * 0.95f));
                    canvas.save();
                    if (spanEnd == drawText.length()) {
                        canvas.translate(firstX - emojiDrawableW / 8, rect.centerY() - emojiDrawableW / 2);
                    } else {
                        canvas.translate(firstX - emojiDrawableW / 2, rect.centerY() - emojiDrawableW / 2);
                    }
                    emojiSpans[0].getDrawable().draw(canvas);
                    canvas.restore();

                    if (spanEnd < drawText.length()) {
                        canvas.drawText(drawText, spanEnd, drawText.length(), secondX + emojiDrawableW / 2, baseline, textPaint);
                    }

                } else {

                    canvas.drawText(drawText, 0, 1, firstX, baseline, textPaint);

                    emojiSpans[0].getDrawable().setBounds(0, 0, (int) (emojiDrawableW * 0.95f), (int) (emojiDrawableW * 0.95f));
                    canvas.save();
                    canvas.translate(secondX, rect.centerY() - emojiDrawableW / 2);
                    emojiSpans[0].getDrawable().draw(canvas);
                    canvas.restore();
                }

            } else if (emojiSpans.length == 2) {

                emojiSpans[0].getDrawable().setBounds(0, 0, (int) (emojiDrawableW * 0.95f), (int) (emojiDrawableW * 0.95f));
                canvas.save();
                canvas.translate(firstX - emojiDrawableW / 2, rect.centerY() - emojiDrawableW / 2);
                emojiSpans[0].getDrawable().draw(canvas);
                canvas.restore();

                emojiSpans[1].getDrawable().setBounds(0, 0, (int) (emojiDrawableW * 0.95f), (int) (emojiDrawableW * 0.95f));
                canvas.save();
                canvas.translate(secondX, rect.centerY() - emojiDrawableW / 2);
                emojiSpans[1].getDrawable().draw(canvas);
                canvas.restore();
            }
        }
    }
}
