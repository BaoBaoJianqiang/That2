package jianqiang.com.hostapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ProxyActivity extends BaseHostActivity {

    private static final String TAG = "ProxyActivity";

    public static final String FROM = "extra.from";
    public static final int FROM_INTERNAL = 0;
    public static final int FROM_EXTERNAL = 1;

    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";

    private String mClass;

    private Activity mRemoteActivity;
    private HashMap<String, Method> mActivityLifecircleMethods = new HashMap<String, Method>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDexPath = getIntent().getStringExtra(EXTRA_DEX_PATH);
        mClass = getIntent().getStringExtra(EXTRA_CLASS);

        loadClassLoader();
        loadResources();

        launchTargetActivity(mClass);
    }

    void launchTargetActivity(final String className) {
        try {
            //反射出插件的Activity对象
            Class<?> localClass = dexClassLoader.loadClass(className);
            Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
            Object instance = localConstructor.newInstance(new Object[] {});
            mRemoteActivity = (Activity) instance;

            //执行插件Activity的setProxy方法，建立双向引用
            Method setProxy = localClass.getMethod("setProxy", new Class[] { Activity.class, String.class });
            setProxy.setAccessible(true);
            setProxy.invoke(instance, new Object[] { this, mDexPath });

            instantiateLifecircleMethods(localClass);

            //执行插件Activity的onCreate方法
            Method onCreate = mActivityLifecircleMethods.get("onCreate");
            Bundle bundle = new Bundle();
            bundle.putInt(FROM, FROM_EXTERNAL);
            onCreate.invoke(instance, new Object[] { bundle });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void instantiateLifecircleMethods(Class<?> localClass) {
        String[] methodNames = new String[] {
                "onRestart",
                "onStart",
                "onResume",
                "onPause",
                "onStop",
                "onDestory"
        };
        for (String methodName : methodNames) {
            Method method = null;
            try {
                method = localClass.getDeclaredMethod(methodName, new Class[] { });
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            mActivityLifecircleMethods.put(methodName, method);
        }

        Method onCreate = null;
        try {
            onCreate = localClass.getDeclaredMethod("onCreate", new Class[] { Bundle.class });
            onCreate.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        mActivityLifecircleMethods.put("onCreate", onCreate);

        Method onActivityResult = null;
        try {
            onActivityResult = localClass.getDeclaredMethod("onActivityResult",
                    new Class[] { int.class, int.class, Intent.class });
            onActivityResult.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        mActivityLifecircleMethods.put("onActivityResult", onActivityResult);
    }

    protected void setRemoteActivity(Object activity) {
        try {
            mRemoteActivity = (Activity) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult resultCode=" + resultCode);
        Method onActivityResult = mActivityLifecircleMethods.get("onActivityResult");
        if (onActivityResult != null) {
            try {
                onActivityResult.invoke(mRemoteActivity, new Object[] { requestCode, resultCode, data });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Method onStart = mActivityLifecircleMethods.get("onStart");
        if (onStart != null) {
            try {
                onStart.invoke(mRemoteActivity, new Object[] {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Method onRestart = mActivityLifecircleMethods.get("onRestart");
        if (onRestart != null) {
            try {
                onRestart.invoke(mRemoteActivity, new Object[] { });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Method onResume = mActivityLifecircleMethods.get("onResume");
        if (onResume != null) {
            try {
                onResume.invoke(mRemoteActivity, new Object[] { });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        Method onPause = mActivityLifecircleMethods.get("onPause");
        if (onPause != null) {
            try {
                onPause.invoke(mRemoteActivity, new Object[] { });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Method onStop = mActivityLifecircleMethods.get("onStop");
        if (onStop != null) {
            try {
                onStop.invoke(mRemoteActivity, new Object[] { });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Method onDestroy = mActivityLifecircleMethods.get("onDestroy");
        if (onDestroy != null) {
            try {
                onDestroy.invoke(mRemoteActivity, new Object[] { });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
