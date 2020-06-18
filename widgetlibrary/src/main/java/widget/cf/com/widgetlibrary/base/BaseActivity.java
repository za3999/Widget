package widget.cf.com.widgetlibrary.base;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import widget.cf.com.widgetlibrary.language.MultiLanguageUtil;
import widget.cf.com.widgetlibrary.util.StatusBarUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        StatusBarUtil.setTranslucentStatus(getWindow(), isTopOffset());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiLanguageUtil.attachBaseContext(this);
    }

    protected boolean isTopOffset() {
        return true;
    }
}
