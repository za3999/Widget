package widget.cf.com.widgetlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import widget.cf.com.widgetlibrary.util.SPUtil;
import widget.cf.com.widgetlibrary.util.ViewUtil;


public class AppearanceTextView extends AppCompatTextView {

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
            checkView();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkView();
    }

    private void checkView() {
        if (supportAppearance) {
            if (supportSelected) {
                setTextColor(ViewUtil.getColorStateList(SPUtil.get(getContext(), "color", 0, Integer.class), defaultColor));
            } else {
                setTextColor(SPUtil.get(getContext(), "color", 0, Integer.class));
            }
        }
    }

}
