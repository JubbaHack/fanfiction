package laiden.fanfiction;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import laiden.fanfiction.project.Story;

public final class StoryManager {

    private static final String STORIES_DIRECTORY   = "stories";
    private static final String STORY_FILE          = "story.json";
    private static final String RESOURCES_DIRECTORY = "resources";

    private static File files;
    private static File stories;
    private static Gson gson;

    static {
        files = MainActivity.instance.getFilesDir();
        gson = new Gson();
        stories = new File(files.getPath() + "/" + STORIES_DIRECTORY);
        if(!stories.exists()) stories.mkdir();
    }
    enum ErrorType{
        EXISTS, CREATED, ERROR
    }
    public static Story loadStory(String name){
        File storydir = new File(stories.getPath() + "/" + name);
        if(!storydir.exists()){
            Log.d("StoryManager", "Story directory '" + name + "' does not exist, cannot load this story.");
            return null;
        }
        else {
            try {
                File story_file = new File(storydir.getPath() + "/" + STORY_FILE);
                String json = Utils.readFile(story_file);
                Log.d("StoryManager", "Loading story '" + name + "'...");
                return gson.fromJson(json, Story.class);
            } catch (Exception ignored){}

        }
        return null;
    }
    public static boolean deleteStory(String name){
        File storydir = new File(stories.getPath() + "/" + name);
        if(!storydir.exists()){
            Log.d("StoryManager", "Story directory '" + name + "' does not exist, cannot delete this story.");
            return false;
        }
        else {
            Utils.delete(storydir);
            Log.d("StoryManager", "Deleting story '" + name + "'...");
            return true;
        }
    }
    public static ErrorType createStory(String name){
        File storydir = new File(stories.getPath() + "/" + name);
        if(storydir.exists()){
            Log.d("StoryManager", "Story directory '" + name + "' already exists.");
            return ErrorType.EXISTS;
        }
        else {
            if(storydir.mkdir()){
                File resources_dir = new File(storydir.getPath() + "/" + RESOURCES_DIRECTORY);
                if(!resources_dir.mkdir()){
                    Log.d("StoryManager", "Cannot create '" + name + "/" + RESOURCES_DIRECTORY + "' directory.");
                    return ErrorType.ERROR;
                }

                Uri horse_link = Uri.parse("android.resource://" + MainActivity.package_name + "/" + R.drawable.horse);
                File horse = new File(horse_link.getPath());
                File horse_c = new File(resources_dir.getPath() + "/" + "horse.png");

                if(!Utils.copyFile(horse, horse_c) && !horse_c.isFile()){
                    Log.d("StoryManager", "Cannot create a horse resource.");
                    return ErrorType.ERROR;
                }

                Log.d("StoryManager", "Story directory '" + name + "' has been created.");
                Story s = new Story();
                s.name = name;
                String story = gson.toJson(s);

                File story_file = new File(storydir.getPath() + "/" + STORY_FILE);

                try {
                    if(story_file.createNewFile()) {
                        FileWriter writer = new FileWriter(story_file);
                        writer.write(story);
                        writer.flush();
                        writer.close();
                        return ErrorType.CREATED;
                    }
                } catch (IOException ignored) {}
            }
            else {
                Log.e("StoryManager", "Could not create Story directory '" + name + "'");
                return ErrorType.ERROR;
            }
        }
        return ErrorType.ERROR;
    }


}
