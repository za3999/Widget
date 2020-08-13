package widget.cf.com.widgetlibrary.emoji;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.text.Layout;
import android.text.TextUtils;

import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class HeadTextDrawable extends ShapeDrawable {

    private static List<Integer> bgColors = new ArrayList<>();
    private CharSequence drawText = "";
    private Layout textLayout;

    static {
        bgColors.add(0xffed4e4e);
        bgColors.add(0xff6c9dd9);
        bgColors.add(0xffeec74e);
        bgColors.add(0xff8576f2);
        bgColors.add(0xff7bcd59);
        bgColors.add(0xffbe6cd9);
        bgColors.add(0xff63c9bf);
        bgColors.add(0xffee6293);
    }

    public HeadTextDrawable() {
        this(new OvalShape());
    }

    public HeadTextDrawable(Shape s) {
        this("", 0);
    }

    public HeadTextDrawable(String text, int id) {
        this(text, id, ApplicationUtil.getIntDimension(R.dimen.sp_19));
    }

    public HeadTextDrawable(String text, int id, int textSize) {

        super(new OvalShape());

        init(id);

        if (!TextUtils.isEmpty(text)) {

            Paint emojiPaint = new Paint();
            emojiPaint.setTextSize(textSize);

            if (text.length() > 1) {
                text = text.substring(0, 1).toUpperCase() + text.substring(1);
                drawText = EmojiHelper.getCutEmojiString(text, 2, emojiPaint);
            } else {
                text = text.substring(0, 1).toUpperCase();
                drawText = EmojiHelper.getCutEmojiString(text, 1, emojiPaint);
            }
        }
        if (TextUtils.isEmpty(drawText)) {
            return;
        }

        TextLayoutBuilder builder = new TextLayoutBuilder()
                .setTextSize(textSize)
                .setTextColor(Color.WHITE)
                .setText(drawText)
                .setTextStyle(Typeface.BOLD)
                .setIncludeFontPadding(false)
                .setSingleLine(true)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setWidth(ApplicationUtil.getIntDimension(R.dimen.dp_54), TextLayoutBuilder.MEASURE_MODE_EXACTLY);

        textLayout = builder.build();
    }

    public static int obtainBgColor(int id) {
        int absId = Math.abs(id);
        return bgColors.get((absId / 2 + absId % 2) % 8);
    }

    private void init(int id) {
        getPaint().setColor(obtainBgColor(id));
    }

    @Override
    public int getIntrinsicWidth() {
        return ApplicationUtil.getIntDimension(R.dimen.dp_56);
    }

    @Override
    public int getIntrinsicHeight() {
        return ApplicationUtil.getIntDimension(R.dimen.dp_56);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        super.onDraw(shape, canvas, paint);
        if (null == textLayout) {
            return;
        }

        final Rect rect = getBounds();
        final int startX = (rect.right - rect.left - textLayout.getWidth()) / 2;
        final int startY = rect.top + (rect.bottom - rect.top - textLayout.getHeight()) / 2;
        canvas.save();
        canvas.translate(startX, startY);
        textLayout.draw(canvas);
        canvas.restore();
    }

    public CharSequence getDrawText() {
        return drawText;
    }
}
