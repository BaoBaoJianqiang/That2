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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DownloadActivity extends Activity implements OnItemClickListener {

    public static final String FROM = "extra.from";
    public static final int FROM_INTERNAL = 0;
    public static final int FROM_EXTERNAL = 1;

    private ArrayList<PluginItem> mPluginItems = new ArrayList<PluginItem>();
    private PluginAdapter mPluginAdapter;

    private ListView mListView;

    private static final String downloadUrl = "https://files.cnblogs.com/files/Jax/plugin1.apk";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        Utils.extractAssets(newBase, "plugin1.apk");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请SDCard读写权限
        verifyStoragePermissions();

        initView();
    }

    private void initView() {
        Button btnDownload = (Button)findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String downloadFile = download();

                        Message message = new Message();
                        message.what = 100;
                        message.obj = downloadFile;
                        mHandler.sendMessage(message);
                    }
                }).start();

            }
        });


        mPluginAdapter = new PluginAdapter();
        mListView = (ListView) findViewById(R.id.plugin_list);
        mListView.setAdapter(mPluginAdapter);
        mListView.setOnItemClickListener(this);
    }

    Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case 100:
                    initData((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void initData(String downloadFilePath) {

        File file = null;
        if(downloadFilePath == null) {
            file = getFileStreamPath("plugin1.apk");
        } else {
            file = new File(downloadFilePath);
        }

        File[] plugins = {file};

        for (File plugin : plugins) {
            PluginItem item = new PluginItem();
            item.pluginPath = plugin.getAbsolutePath();
            item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);
            mPluginItems.add(item);
        }

        mPluginAdapter.notifyDataSetChanged();
    }

    private class PluginAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public PluginAdapter() {
            mInflater = DownloadActivity.this.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mPluginItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mPluginItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.plugin_item, parent, false);
                holder = new ViewHolder();
                holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.appName = (TextView) convertView.findViewById(R.id.app_name);
                holder.apkName = (TextView) convertView.findViewById(R.id.apk_name);
                holder.packageName = (TextView) convertView.findViewById(R.id.package_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PluginItem item = mPluginItems.get(position);
            PackageInfo packageInfo = item.packageInfo;
            holder.appIcon.setImageDrawable(DLUtils.getAppIcon(DownloadActivity.this, item.pluginPath));
            holder.appName.setText(DLUtils.getAppLabel(DownloadActivity.this, item.pluginPath));
            holder.apkName.setText(item.pluginPath.substring(item.pluginPath.lastIndexOf(File.separatorChar) + 1));
            holder.packageName.setText(packageInfo.applicationInfo.packageName);
            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView apkName;
        public TextView packageName;
    }

    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;

        public PluginItem() {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent("jianqiang.com.hostapp.VIEW");
        intent.putExtra(ProxyActivity.EXTRA_DEX_PATH, mPluginItems.get(position).pluginPath);
        intent.putExtra(ProxyActivity.EXTRA_CLASS, mPluginItems.get(position).packageInfo.packageName + ".MainActivity");
        startActivity(intent);
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


    //下载具体操作
    private String download() {
        try {
            URL url = new URL(downloadUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            int contentLength = conn.getContentLength();
            Log.e(TAG, "contentLength = " + contentLength);
            //创建文件夹 MyDownLoad，在存储卡下
            String dirName = Environment.getExternalStorageDirectory() + "/MyDownLoad/";
            File file = new File(dirName);
            //不存在创建
            if (!file.exists()) {
                file.mkdir();
            }
            //下载后的文件名
            String fileName = dirName + "plugin1.apk";
            File file1 = new File(fileName);
            if (file1.exists()) {
                file1.delete();
            }

            //创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fileName);
            //写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            //完成后关闭流
            Log.e(TAG, "download-finish");

            os.close();
            is.close();

            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
