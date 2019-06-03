package widget.cf.com.widgetlibrary.appearance;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.SPUtil;
import widget.cf.com.widgetlibrary.util.ViewUtil;


public class AppearanceTextView extends AppCompatTextView implements IAppearanceChange {

    boolean supportAppearance;
    boolean supportSelected;
    int defaultColor;

    public AppearanceTextView(Context context) {
        super(context);
    }

    public AppearanceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AppearanceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.AppearanceTextView);
            supportAppearance = array.getBoolean(R.styleable.AppearanceTextView_support_text_appearance, false);
            supportSelected = array.getBoolean(R.styleable.AppearanceTextView_support_text_selected, false);
            defaultColor = array.getColor(R.styleable.AppearanceTextView_support_text_default_color, Color.TRANSPARENT);
            onChange();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (supportAppearance) {
            AppearanceManager.getInstance().unRegister(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (supportAppearance) {
            AppearanceManager.getInstance().register(this);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (supportSelected) {
            onChange();
        }
    }

    @Override
    public void onChange() {
        if (supportAppearance) {
            if (supportSelected) {
                setTextColor(ViewUtil.getColorStateList(SPUtil.get(getContext(), "color", 0, Integer.class), defaultColor));
            } else {
                setTextColor(SPUtil.get(getContext(), "color", 0, Integer.class));
            }
        }
    }
}
