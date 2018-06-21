package jianqiang.com.hostapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author weishu
 * @date 16/3/29
 */
public class Utils {

    /**
     * 把Assets里面得文件复制到 /data/data/files 目录下
     *
     * @param context
     * @param sourceName
     */
    public static void extractAssets(Context context, String sourceName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = am.open(sourceName);
            File extractFile = context.getFileStreamPath(sourceName);
            fos = new FileOutputStream(extractFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(fos);
        }

    }

    // --------------------------------------------------------------------------
    private static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignore
        }
    }

    public static void Unzip(String zipFile, String targetDir) {
        int BUFFER = 4096; //这里缓冲区我们使用4KB，
        String strEntry; //保存每个zip的条目名称

        try {
            BufferedOutputStream dest = null; //缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; //每个zip条目的实例

            while ((entry = zis.getNextEntry()) != null) {

                try {
                    Log.i("Unzip: ","="+ entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }

                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();

        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }
}
