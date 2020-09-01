package widget.cf.com.widgetlibrary.dialog;

import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import widget.cf.com.widgetlibrary.R;
import widget.cf.com.widgetlibrary.base.BaseCallBack;
import widget.cf.com.widgetlibrary.util.ApplicationUtil;


public class IndicatorMenuDialog extends BaseBlurDialog {

    public static final int EDIT_FOLDER = 0;
    public static final int ADD_CHAT = 1;
    public static final int REMOVE = 2;
    public static final int REORDER_TABS = 3;

    private BaseCallBack.CallBack1<Integer> mListener;

    public IndicatorMenuDialog(View targetView, BaseCallBack.CallBack1<Integer> listener) {
        super(targetView);
        setCancelable(true);
        this.mListener = listener;
    }

    @Override
    public Pair<Integer, Integer> getAnimPoint() {
        return new Pair<>(getMenuWidth() / 2, 0);
    }

    @Override
    public RelativeLayout.LayoutParams getTargetLayoutParams() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mTargetRect.width(), mTargetRect.height());
        layoutParams.leftMargin = getTargetLeftMargin();
        layoutParams.topMargin = mTargetRect.top - ApplicationUtil.getStatusBarHeight();
        return layoutParams;
    }

    public RelativeLayout.LayoutParams getLayoutParams() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getMenuWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = getMenuLeftMargin();
        layoutParams.topMargin = ApplicationUtil.getIntDimension(R.dimen.dp_8);
        layoutParams.addRule(RelativeLayout.BELOW, mMirrorView.getId());
        return layoutParams;
    }

    @Override
    public View getContentView() {
        LinearLayout rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.addView(new BlurMenuItem(getContext()).bindData(R.string.edit_folder, R.drawable.edit_folder, v -> {
            mListener.onCallBack(EDIT_FOLDER);
            dismiss();
        }));
        rootView.addView(new BlurMenuItem(getContext()).bindData(R.string.edit_folder, R.drawable.edit_folder, v -> {
            mListener.onCallBack(ADD_CHAT);
            dismiss();
        }));
        rootView.addView(new BlurMenuItem(getContext()).bindData(R.string.edit_folder, R.drawable.edit_folder, ApplicationUtil.getColor(R.color.negative), v -> {
            mListener.onCallBack(REMOVE);
            dismiss();
        }));
        rootView.addView(new BlurMenuItem(getContext()).bindData(R.string.edit_folder, R.drawable.edit_folder, v -> {
            mListener.onCallBack(REORDER_TABS);
            dismiss();
        }));
        rootView.setBackgroundResource(R.drawable.white_radius_bg);
        return rootView;
    }

}
