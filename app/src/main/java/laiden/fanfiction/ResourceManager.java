package laiden.fanfiction;

import android.graphics.Color;

import java.io.File;

public final class ResourceManager {
    public static File resources;

    static {
        resources = new File(StoryManager.stories.getPath() + "/" + StoryView.story.name + "/" + StoryManager.RESOURCES_DIRECTORY);
    }

    public static String getPath(String s){
        File resource = new File(resources.getPath() + "/" + s);
        if(resource.isFile() && resource.exists()) return resource.getPath();
        else return null;
    }
    public static boolean isColor(String s){
        return s.startsWith("#");
    }
    public static boolean isResource(String s){
        return getPath(s) != null;
    }
    public static int color(String s){
        if(s.startsWith("#")) return Color.parseColor(s);
        else return Color.TRANSPARENT;
    }
}
