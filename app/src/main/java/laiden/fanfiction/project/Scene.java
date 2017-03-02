package laiden.fanfiction.project;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Scene {

    private static final String DEFAULT_B = "#000000";
    private static final int THING_LIMIT = 50;

    public ArrayList<Thing> things;
    public String background;

    Scene(){
        things = new ArrayList<Thing>();
        this.background = DEFAULT_B;

        for(int i = 1; i <= THING_LIMIT/2; i++){
            Thing t = new Thing();
            t.setText("Lorem ipsum dolor sit amet, per temporibus reprehendunt te, choro graecis vivendum ad per. Privet Nastya. No quo probo primis. Mauris convallis ante sed felis malesuada dapibus. Vivamus ut arcu tellus. #" + i);
            add(t);
        }

    }
    public boolean addable(){
        return things.size()+1 < THING_LIMIT;
    }
    public boolean add(Thing t){
        if(t.index == -1) t.index = things.size() + 1;
        if(things.size() < THING_LIMIT){ things.add(t); return true;}
        else return false;
        /*Collections.sort(things, new Comparator<Thing>() {
            @Override public int compare(Thing t1, Thing t2) {
                return t2.index - t1.index;
            }
        });*/
    }


}
