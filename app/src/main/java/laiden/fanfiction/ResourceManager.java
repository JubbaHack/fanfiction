package laiden.fanfiction;

import android.graphics.Color;

/**
 * Created by student1 on 09.03.17.
 */

public final class ResourceManager {
    public static int color(String s){
        if(s.startsWith("#")) return Color.parseColor(s);
        else return Color.TRANSPARENT;
    }
}
