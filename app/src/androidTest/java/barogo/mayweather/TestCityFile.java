package barogo.mayweather;

import android.content.res.AssetManager;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by user on 2015-08-04.
 */
public class TestCityFile extends AndroidTestCase {

    public void testReadCity() throws Throwable {
        String filePath = "/data/data/" + "barogo.mayweather" + "/databases/" + "mayweather.db";
        File file = new File(filePath);
        long aa = file.length();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream =
                    new FileInputStream(new File("/data/data/" + "barogo.mayweather" + "/databases/" + "mayweather.db"));
            File root = Environment.getExternalStorageDirectory();
            String a = root.getPath();

            File aaa = new File(root, "mayweather.db");
            outputStream =
                    new FileOutputStream(aaa);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            Log.d("", "");
        } catch (Exception e) {
            Log.d("", "");
        }
    }

//    public void testWriteCity() throws Throwable {
//        isDeleteExistDB();
//        moveFile();
//        Log.d("1234","1234");
//    }

    public void isDeleteExistDB() {
        String filePath = "/data/data/" + "barogo.mayweather" + "/databases/" + "mayweather.db";
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }

    public void moveFile() {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            AssetManager am = mContext.getAssets();//u have get assets path from this ocde
            inputStream = am.open("mayweather.db");

            // write the inputStream to a FileOutputStream
            outputStream =
                    new FileOutputStream(new File("/data/data/" + "barogo.mayweather" + "/databases/" + "mayweather.db"));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            System.out.println("Done!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}