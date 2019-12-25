package widget.cf.com.widgetlibrary.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import widget.cf.com.widgetlibrary.language.MultiLanguageUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiLanguageUtil.attachBaseContext(this);
    }

}
