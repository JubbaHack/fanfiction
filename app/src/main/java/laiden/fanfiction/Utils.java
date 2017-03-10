package laiden.fanfiction;


import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public final class Utils {
    public static Rect inflatedRect(int a, int b, int c, int d, float f){
        Rect r = new Rect(a, b, c, d);
        return scaleRect(r, f);
    }
    public static Rect scaleRect(Rect rect, float factor){

        Rect r = new Rect(rect.left, rect.top, rect.right, rect.bottom);

        float diffHorizontal = (r.right-r.left) * (factor-1f);
        float diffVertical = (r.bottom-r.top) * (factor-1f);


        r.top -= diffVertical/2f;
        r.bottom += diffVertical/2f;

        r.left -= diffHorizontal/2f;
        r.right += diffHorizontal/2f;

        return r;
    }
    public static Rect deflatedRect(Rect r, float d){
        return new Rect((int)(r.left - d), (int)(r.top - d), (int)(r.right + d*2), (int)(r.bottom + d*2));
    }
    public static RectF inflatedRect(RectF r, float d){
        return new RectF((r.left - d), (r.top - d), (r.right + d*2), (r.bottom + d*2));
    }
    public static Rect inflatedRect(Rect r, float d){
        return new Rect((int)(r.left - d), (int)(r.top - d), (int)(r.right + d*2), (int)(r.bottom + d*2));
    }
    public static boolean copyResource(int id, File destination) {
        try {
            int len;
            InputStream  is = MainActivity.instance.getResources().openRawResource(id);
            OutputStream os = new FileOutputStream(destination);
            byte buffer[]   = new byte[1024];
            while((len = is.read(buffer)) > 0) os.write(buffer, 0, len);
            os.close();
            is.close();
            return true;
        }
        catch (IOException ignored){}
        return false;
    }
    public static float ndp(float v){
        return (v*MainActivity.density)/StoryView.d;
    }
    public static String readFile(File file) throws Exception {
        FileInputStream stream = new FileInputStream(file);
        String string = convertStreamToString(stream);
        stream.close();
        return string;
    }
    public static int darker (int color, float factor) {
        factor = 1 - factor;
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }
    public static void delete(File file) {
        if(file.isDirectory()) {
            if (file.list().length == 0) file.delete();
            else {
                String files[] = file.list();
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    delete(fileDelete);
                }
                if (file.list().length == 0) file.delete();

            }
        }
        else file.delete();
    }
    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
