package laiden.fanfiction;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import laiden.fanfiction.project.Story;

public final class StoryManager {

    private static final String STORIES_DIRECTORY = "stories";
    private static final String STORY_FILE        = "story.json";

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
