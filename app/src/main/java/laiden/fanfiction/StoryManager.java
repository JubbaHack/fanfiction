package laiden.fanfiction;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import laiden.fanfiction.project.Story;

public final class StoryManager {

    private static final String STORIES_DIRECTORY   = "stories";
    private static final String STORY_FILE          = "story.json";
    public static final String RESOURCES_DIRECTORY = "resources";

    public static File files;
    public static File stories;
    public static Gson gson;

    static {
        files = App.instance.getFilesDir();
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
    public static File addResource(InputStream in, String story, String name){
        File story_dir = new File(stories.getPath() + "/" + story);
        File resources_dir = new File(story_dir.getPath() + "/" + RESOURCES_DIRECTORY);
        name += ".png";
        File resource = new File(resources_dir.getPath() + "/" + name);
        try {
            OutputStream out = new FileOutputStream(resource);
            Utils.copyInputStreamToFile(in, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return resource.isFile() ? resource : null;
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

                File horse = new File(resources_dir.getPath() + "/" + "horse.png");

                if(!Utils.copyResource(R.drawable.horse, horse) || !horse.isFile()){
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
