package widget.cf.com.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import widget.cf.com.widgetlibrary.HeadTextDrawable;
import widget.cf.com.widgetlibrary.emoji.EmojiData;


@SuppressLint("NewApi")
public class MainActivity extends Activity {

    LinearLayout llLayout;
    TextView mEmojiTv;
    ImageView mDrawableIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llLayout = findViewById(R.id.ll_layout);
        mEmojiTv = findViewById(R.id.emoji_test_tv);
        mDrawableIv = findViewById(R.id.emoji_drawable_iv);
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

    }

    public void onEmojiClick(View view) {
        String[][] emojis = EmojiData.data;
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            int one = (int) (Math.random() * emojis.length);
            int two = (int) (Math.random() * emojis[one].length);
            content.append(emojis[one][two]);
        }
        mEmojiTv.setText(content.toString());
        int id = (int) ((Math.random() * 100) % 100);
        mDrawableIv.setImageDrawable(new HeadTextDrawable(content.toString(), id));
    }

    public void onSwipeLayoutClick(View view) {
        startActivity(new Intent(this, SwipeLayoutActivity.class));

    }

    public void onColorPickerClick(View view) {
        startActivity(new Intent(this, ColorPickerActivity.class));
    }

}
