package widget.cf.com.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import widget.cf.com.widget.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.tintview.TintSelectTextView;
import widget.cf.com.widgetlibrary.touchmenu.ITouchPopMenu;
import widget.cf.com.widgetlibrary.touchmenu.TouchItemListener;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;

public class SpeedMenu extends LinearLayout implements ITouchPopMenu<Float> {

    private static final Float[] speedArray = {0.5f, 1f, 1.5f, 2f, 3f};
    private List<TextView> views = new ArrayList<>();
    private TouchItemListener<Float> mTouchListener;
    private float selectSpeed;
    private BaseCallBack.CallBack mCloseHelper;

    public SpeedMenu(Context context, float selectSpeed) {
        this(context, null);
        this.selectSpeed = selectSpeed;
    }

    public SpeedMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutParams(new LayoutParams(getMenuWith(), ViewGroup.LayoutParams.WRAP_CONTENT));
        setBackgroundResource(R.mipmap.speed_menu_bg);
        setOrientation(VERTICAL);
        initMenu();
    }

    private void initMenu() {
        for (Float speed : speedArray) {
            TintSelectTextView textView = new TintSelectTextView(getContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ApplicationUtil.getIntDimension(R.dimen.dp_40));
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ApplicationUtil.getIntDimension(R.dimen.dp_16));
            textView.setTextColor(ApplicationUtil.getColor(R.color.title_font_secondary));
            textView.setText(getSpeedStr(speed));
            textView.setTag(speed);
            textView.setOnClickListener(v -> {
                selectSpeed = (float) textView.getTag();
                mTouchListener.onSelect(selectSpeed);
                mCloseHelper.onCallBack();
            });
            if (speed == selectSpeed) {
                textView.setSelected(true);
            }
            addView(textView);
            views.add(textView);
        }
    }

    @Override
    public ViewGroup getRoot() {
        return this;
    }

    @Override
    public void onTouchChange(int index) {
        float data = (float) views.get(index).getTag();
        if (data == selectSpeed) {
            return;
        }
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            view.setSelected(i == index);
        }
        selectSpeed = data;
        mTouchListener.onTouchChange(data);
    }

    @Override
    public void onResult() {
        if (mTouchListener != null) {
            mTouchListener.onSelect(selectSpeed);
        }
    }


    @Override
    public void setCloseHelper(BaseCallBack.CallBack closeHelper) {
        this.mCloseHelper = closeHelper;
    }

    @Override
    public int getMenuWith() {
        return ApplicationUtil.getIntDimension(R.dimen.dp_80);
    }

    @Override
    public void setTouchListener(TouchItemListener<Float> touchListener) {
        mTouchListener = touchListener;
    }

    private String getSpeedStr(float speed) {
        String speedStr = "";
        if (speed == 0.5f) {
            speedStr = "0.5x";
        } else if (speed == 1f) {
            speedStr = "1.0x";
        } else if (speed == 1.5f) {
            speedStr = "1.5x";
        } else if (speed == 2f) {
            speedStr = "2.0x";
        } else if (speed == 3f) {
            speedStr = "3x";
        }
        return speedStr;
    }
}
