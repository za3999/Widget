package widget.cf.com.widgetlibrary.appearance;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.util.SPUtil;
import widget.cf.com.widgetlibrary.util.ViewUtil;

public class AppearanceImageView extends AppCompatImageView implements IAppearanceChange {
    boolean supportAppearance;
    boolean supportSelected;
    int defaultColor;

    public AppearanceImageView(Context context) {
        super(context);
    }

    public AppearanceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AppearanceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.AppearanceImageView);
            supportAppearance = array.getBoolean(R.styleable.AppearanceImageView_support_image_appearance, false);
            supportSelected = array.getBoolean(R.styleable.AppearanceImageView_support_image_selected, false);
            defaultColor = array.getColor(R.styleable.AppearanceImageView_support_image_default_color, Color.TRANSPARENT);
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
    public void onChange() {
        if (supportAppearance) {
            if (supportSelected) {
                ViewUtil.setImageTint(this, SPUtil.get(getContext(), "color", 0, Integer.class), defaultColor);
            } else {
                ViewUtil.setImageTint(this, SPUtil.get(getContext(), "color", 0, Integer.class));
            }
        }
    }
}
