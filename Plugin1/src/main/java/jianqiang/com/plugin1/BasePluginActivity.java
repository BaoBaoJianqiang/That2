package jianqiang.com.plugin1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class BasePluginActivity extends Activity {

    private static final String TAG = "Client-BaseActivity";

    public static final String FROM = "extra.from";
    public static final int FROM_INTERNAL = 0;
    public static final int FROM_EXTERNAL = 1;
    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";

    public static final String PROXY_VIEW_ACTION = "jianqiang.com.hostapp.VIEW";

    /**
     * 等同于mProxyActivity，可以当作Context来使用，会根据需要来决定是否指向this<br/>
     * 可以当作this来使用
     */
    protected Activity that;
    protected int mFrom = FROM_INTERNAL;
    protected String mDexPath;

    public void setProxy(Activity proxyActivity, String dexPath) {
        Log.d(TAG, "setProxy: proxyActivity= " + proxyActivity + ", dexPath= " + dexPath);
        that = proxyActivity;
        mDexPath = dexPath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("baobao", "hello");
        if (savedInstanceState != null) {
            mFrom = savedInstanceState.getInt(FROM, FROM_INTERNAL);
        }
        if (mFrom == FROM_INTERNAL) {
            super.onCreate(savedInstanceState);
            that = this;
        }

        Log.d(TAG, "onCreate: from= " + (mFrom == FROM_INTERNAL ? "FROM_INTERNAL" : "FROM_EXTERNAL"));
    }

    protected void startActivityByProxy(String className) {
        if (that == this) {
            Intent intent = new Intent();
            intent.setClassName(this, className);
            that.startActivity(intent);
        } else {
            Intent intent = new Intent(PROXY_VIEW_ACTION);
            intent.putExtra(EXTRA_DEX_PATH, mDexPath);
            intent.putExtra(EXTRA_CLASS, className);
            that.startActivity(intent);
        }
    }

    public void startActivityForResultByProxy(String className, int requestCode) {
        if (that == this) {
            Log.e("baobao", "101");
            Intent intent = new Intent();
            intent.setClassName(this, className);
            that.startActivityForResult(intent, requestCode);
        } else {
            Log.e("baobao", "102");
            Intent intent = new Intent(PROXY_VIEW_ACTION);
            intent.putExtra(EXTRA_DEX_PATH, mDexPath);
            intent.putExtra(EXTRA_CLASS, className);
            that.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void setContentView(View view) {
        if (that == this) {
            super.setContentView(view);
        } else {
            that.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        if (that == this) {
            super.setContentView(view, params);
        } else {
            that.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (that == this) {
            super.setContentView(layoutResID);
        } else {
            that.setContentView(layoutResID);
        }
    }

    @Override
    public void addContentView(View view, LayoutParams params) {
        if (that == this) {
            super.addContentView(view, params);
        } else {
            that.addContentView(view, params);
        }
    }

    @Override
    public View findViewById(int id) {
        if (that == this) {
            return super.findViewById(id);
        } else {
            return that.findViewById(id);
        }
    }
}
