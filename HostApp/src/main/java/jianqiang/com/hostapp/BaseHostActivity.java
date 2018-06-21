package jianqiang.com.hostapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class BaseHostActivity extends Activity {
    private AssetManager mAssetManager;
    private Resources mResources;
    private Theme mTheme;

    protected String mDexPath;
    protected ClassLoader dexClassLoader;

    protected void loadClassLoader() {
        File dexOutputDir = this.getDir("dex", Context.MODE_PRIVATE);
        final String dexOutputPath = dexOutputDir.getAbsolutePath();
        dexClassLoader = new DexClassLoader(mDexPath,
                dexOutputPath, null, getClassLoader());
    }
    protected void loadResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, mDexPath);
            mAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Resources superRes = super.getResources();
        mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.setTo(super.getTheme());
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }
}
