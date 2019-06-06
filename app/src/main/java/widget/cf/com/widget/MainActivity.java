package widget.cf.com.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;


@SuppressLint("NewApi")
public class MainActivity extends Activity {

    LinearLayout llLayout;
    TextView mEmojiTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llLayout = findViewById(R.id.ll_layout);
        mEmojiTv = findViewById(R.id.emoji_test_tv);
    }

    public void onSearchClick(View view) {
        TextView textView = new TextView(this);
        textView.setText("哈哈");
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.leftMargin = 5;
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView.setBackgroundResource(R.color.color_01dfa6);
        llLayout.addView(textView);
        mEmojiTv.setText("\uD83D\uDC12\uD83D\uDC37\uD83D\uDC14\uD83D\uDC14\uD83D\uDC38\uD83D\uDC38\uD83D\uDC3A\uD83D\uDC0C\uD83E\uDD8B\uD83D\uDD78\uD83E\uDD91\uD83E\uDD9E\uD83D\uDC22");
    }

    public void onSwipeLayoutClick(View view) {
        startActivity(new Intent(this, SwipeLayoutActivity.class));

}

    public void onColorPickerClick(View view) {
        startActivity(new Intent(this, ColorPickerActivity.class));
    }

}
