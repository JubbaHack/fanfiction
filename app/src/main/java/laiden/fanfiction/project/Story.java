package laiden.fanfiction.project;


import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Story {

    public ArrayList<Scene> scenes;

    public String author;
    public String name;

    public Story(){
        scenes = new ArrayList<>();
        Scene s = new Scene(); /* Initializing a new story adds a first scene to it. */
        this.add(s);
    }
    public void add(Scene s){
        scenes.add(s);

    }

}
