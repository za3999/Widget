package widget.cf.com.widgetlibrary.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import widget.cf.com.widgetlibrary.language.MultiLanguageUtil;
import widget.cf.com.widgetlibrary.util.StatusBarUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucentStatus(this, isTopOffset());
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
