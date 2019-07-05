package widget.cf.com.widgetlibrary.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChangeWrappingTransition extends ChangeBounds {

    protected static final String PROP_NAME_TEXT_SIZE = "ChangeWrappingTransition::textSize";
    protected static final String PROP_NAME_TEXT_COLOR = "ChangeWrappingTransition::textColor";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues values) {
        if (values.view instanceof TextView) {
            TextView textView = (TextView) values.view;
            values.values.put(PROP_NAME_TEXT_SIZE, textView.getTextSize());
            values.values.put(PROP_NAME_TEXT_COLOR, textView.getCurrentTextColor());
        }
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        if (endValues.view instanceof TextView) {
            final TextView view = (TextView) endValues.view;
            float startTextSize = (float) startValues.values.get(PROP_NAME_TEXT_SIZE);
            final float endTextSize = (float) endValues.values.get(PROP_NAME_TEXT_SIZE);
            ObjectAnimator textSizeAnimator = null;
            if (startTextSize != endTextSize) {
                textSizeAnimator = ObjectAnimator.ofFloat(view, new TextSizeProperty(), startTextSize, endTextSize);
            }

            int startTextColor = (int) startValues.values.get(PROP_NAME_TEXT_COLOR);
            int endTextColor = (int) endValues.values.get(PROP_NAME_TEXT_COLOR);
            ObjectAnimator textColorAnimator = null;
            if (startTextColor != endTextColor) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textColorAnimator = ObjectAnimator.ofArgb(view, new TextColorProperty(), startTextColor, endTextColor);
                }
            }
            Animator animator = super.createAnimator(sceneRoot, startValues, endValues);
            return mergeAnimators(animator, textColorAnimator, textSizeAnimator);
        } else {
            return super.createAnimator(sceneRoot, startValues, endValues);
        }
    }

    private Animator mergeAnimators(Animator... animators) {
        if (animators.length == 0) {
            return null;
        } else {
            AnimatorSet animatorSet = new AnimatorSet();
            for (Animator animator : animators) {
                if (animator != null) {
                    animatorSet.playTogether(animator);
                }
            }
            return animatorSet;
        }
    }

}
