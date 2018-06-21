package jianqiang.com.hostapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cundong.utils.PatchUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MergeActivity extends Activity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        Utils.extractAssets(newBase, "plug_old.apk");
        Utils.extractAssets(newBase, "diff.patch");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请SDCard读写权限
        verifyStoragePermissions();

        File oldApk = this.getFileStreamPath("plug_old.apk");
        String oldApkPath = oldApk.getPath();
        File patchFile = this.getFileStreamPath("diff.patch");
        String patchFilePath = patchFile.getPath();

        String newFilePath = Environment.getExternalStorageDirectory() + File.separator + "new3.apk";

        try {
            int patchResult = PatchUtils.patch(oldApkPath, newFilePath, patchFilePath);
            if(patchResult == 0) {
                Log.e("bao", patchResult + "");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };


    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to
     * grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    static {
        System.loadLibrary("ApkPatchLibrary");
    }
}
