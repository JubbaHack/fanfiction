package laiden.fanfiction;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import laiden.fanfiction.project.Scene;
import laiden.fanfiction.project.Story;
import laiden.fanfiction.project.Thing;

public class StoryView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private static final int DOUBLECLICK = 300;

    public static PointF touch = new PointF();
    public static final float d = 1.75f;
    public static Story story; // todo make fields private
    public static Scene s;
    public static Thing thing;
    public static int scene;
    private static int resize_corner;
    private static PointF click;
    private static boolean doubleclick = false;
    private static long doubleclick_time = 0;
    private static ArrayList<String> editor_history = new ArrayList<>();
    private static int editor_history_ptr = -1;

    public static Map<String, Drawable> drawables;

    public static boolean moving = false;

    Paint p;
    public static int width;
    public static int height;

    public static StoryView instance;

    public static final Drawable ICON_ADD = App.instance.getResources().getDrawable(R.drawable.add);
    public static final Drawable ICON_UNDO = App.instance.getResources().getDrawable(R.drawable.undo);
    public static final Drawable ICON_REDO = App.instance.getResources().getDrawable(R.drawable.redo);
    //public static final Drawable ICON_SETTINGS = App.instance.getResources().getDrawable(R.drawable.settings);
    //public static final Drawable ICON_MENU = App.instance.getResources().getDrawable(R.drawable.menu);
    //public static final Drawable ICON_DASHBOARD = App.instance.getResources().getDrawable(R.drawable.dash);
    //public static final Drawable ICON_PLAY = App.instance.getResources().getDrawable(R.drawable.play);
    public static final Drawable ICON_DELETE = App.instance.getResources().getDrawable(R.drawable.delete);
    public static final Drawable ICON_COPY = App.instance.getResources().getDrawable(R.drawable.copy);

    public static RectF CONSOLE_RECT = new RectF();

    public StoryView(Context context) {
        super(context);
        Log.d("Density", App.density + "");
        instance = this;
        getHolder().addCallback(this);
        p = new Paint();
        p.setAntiAlias(true);

        drawables = new HashMap<>();
        story = null;
        thing = null;
        scene = 0;
        resize_corner = -1;
        click = new PointF();
        s = null;

        EditorHistory.updateui();

        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                StoryView.this.onTouch(event.getX(), event.getY(), event.getAction());
                return true;
            }
        });
    }
    private void updateDrawables(){
        drawables.clear();
        for(File resource: ResourceManager.resources.listFiles()){
            Drawable d = Drawable.createFromPath(resource.getPath());
            String   s = resource.getName();
            drawables.put(s, d);
        }
    }
    private void onTouch(float x, float y, int event){
        touch.x = x;
        touch.y = y;
        if(event == MotionEvent.ACTION_DOWN) {
            if(ICON_ADD.getBounds().contains((int)x, (int)y) && thing == null){
                if(s.addable()){
                    Thing _t = new Thing();
                    _t.setPosition((int)x, (int)(y - 180));
                    _t.invisible = true;
                    s.add(_t);
                    App.vibrator.vibrate(50);

                    thing = _t;
                    click.x = x - _t.getPosition().x;
                    click.y = y - _t.getPosition().y;
                    moving = true;
                    return;
                }
            }
            boolean was_selected = false;
            if(thing != null) resize_corner = thing.resizeFrom(x, y);
            if(resize_corner == -1) {
                for(int i = s.things.size()-1; i >= 0; i--){
                    Thing t = s.things.get(i);
                    if (t.box().contains((int)x, (int)y)) {
                        //t.onTouch(x, y);

                        EditorHistory.add(t, "SETCOORDS", t.getPosition().x, t.getPosition().y);

                        if(!moving && thing != null && doubleclick && (System.currentTimeMillis() - doubleclick_time) <= DOUBLECLICK) {

                            Intent settingsIntent = new Intent(App.instance, PreferenceActivity.class);
                            App.instance.startActivity(settingsIntent);

                            //ProjectSettingsWindow alert = new ProjectSettingsWindow();
                            //alert.showDialog(App.instance, "Text");

                            doubleclick = false;

                        } else {
                            doubleclick = true;
                            doubleclick_time = System.currentTimeMillis();
                        }

                        was_selected = true;
                        thing = t;
                        click.x = x - t.getPosition().x;
                        click.y = y - t.getPosition().y;



                        break;
                    }

                }

                if(!moving){
                    if (Utils.scaleRect(ICON_UNDO.getBounds(), 1.75f).contains((int)x, (int)y)) EditorHistory.undo();
                    else if(Utils.scaleRect(ICON_REDO.getBounds(), 1.75f).contains((int)x, (int)y)) EditorHistory.redo();
                }

                if(thing != null && ICON_COPY.getBounds().contains((int)x, (int)y)){
                    if(s.addable()){
                        Thing _t = new Thing();
                        _t.setBox(thing.box());
                        _t.setPosition(thing.getPosition().x+10, thing.getPosition().y+10);
                        s.add(_t);

                        thing = _t;
                        was_selected = true;
                    }
                }
            }

            if(!was_selected && resize_corner == -1) thing = null;

        }
        else if(event == MotionEvent.ACTION_MOVE){
            if(thing != null){
                if(resize_corner != -1 && !thing.invisible){ /* Resizes the element from a corner, cannot resize translucent elements. */
                    if(resize_corner == 0){
                        Rect box = thing.box();
                        box.left = (int)x;
                        box.top = (int)y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 1){
                        Rect box = thing.box();
                        box.top = (int)y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 2){
                        Rect box = thing.box();
                        box.right = (int)x;
                        box.top = (int)y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 3){
                        Rect box = thing.box();
                        box.left = (int)x;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 4){
                        Rect box = thing.box();
                        box.right = (int)x;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 5){
                        Rect box = thing.box();
                        box.left = (int)x;
                        box.bottom = (int)y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 6){
                        Rect box = thing.box();
                        box.bottom = (int)y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 7){
                        Rect box = thing.box();
                        box.right = (int)x;
                        box.bottom = (int)y;
                        thing.setBox(box);
                    }
                }
                else if(!ICON_COPY.getBounds().contains((int)x, (int)y)){
                    moving = true;
                    thing.setPosition((int)(x-click.x), (int)(y-click.y)); // move
                }
            }
        }
        else if(event == MotionEvent.ACTION_UP){
            if(thing != null && CONSOLE_RECT.contains(x, y) && resize_corner == -1 && moving){
                s.things.remove(thing);
                thing = null;
                Log.d("StoryView", "Thing removed.");
            }
            resize_corner = -1;
            moving = false;

            if(thing != null) EditorHistory.add(thing, "SETCOORDS", thing.getPosition().x, thing.getPosition().y);
            if(thing != null && thing.invisible){ thing.invisible = false; thing = null; }

        }
    }
    static LinkedList<Long> times = new LinkedList<Long>(){{
        add(System.nanoTime());
    }};

    private static final int MAX_SIZE = 100;
    private static final double NANOS = 1000000000.0;

    /** Calculates and returns frames per second */
    public static double fps() {
        long lastTime = System.nanoTime();
        double difference = (lastTime - times.getFirst()) / NANOS;
        times.addLast(lastTime);
        int size = times.size();
        if (size > MAX_SIZE) {
            times.removeFirst();
        }
        return difference > 0 ? (times.size() / difference) : 0;
    }
    public void play(Story s){

        this.story = s;
        updateDrawables();
        this.s = this.story.scenes.get(scene);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            App.instance.getWindow().setNavigationBarColor(Color.parseColor(this.s.background));
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        this.height = height;
        this.width = width;

        CONSOLE_RECT.top = height - Utils.ndp(100);
        CONSOLE_RECT.left = 0;
        CONSOLE_RECT.bottom = height;
        CONSOLE_RECT.right = width;

        final int d = (int)Utils.ndp(15);
        final int s = (int)Utils.ndp(75);




        ICON_ADD.setBounds(Utils.inflatedRect(s*0 + d, height - s - d, s*1 + d, height - d, 0.75f));
        //ICON_ADD.
        ICON_COPY.setBounds(Utils.inflatedRect(s*0 + d, height - s - d, s*1 + d, height - d, 0.75f));
        //ICON_DELETE_BLACK.setBounds((width/2)-40, height - s - d, (width/2)+40, height - d);
        ICON_UNDO.setBounds(Utils.inflatedRect(s*1 + d + 10, height - s - d, s*2 + d + 10, height - d, 0.75f));
        ICON_REDO.setBounds(Utils.inflatedRect(s*2 + d + 20, height - s - d, s*3 + d + 20, height - d, 0.75f));

        ICON_DELETE.setBounds(Utils.inflatedRect(s*0 + d, height - s - d, s*1 + d, height - d, 0.75f));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder());
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        // завершаем работу потока
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException ignored) {}
        }
    }
    static final class EditorHistory {

        static void add(Thing t, String method, Object... params){
            int index = s.things.indexOf(t);
            String p = "";
            for(Object _p: params) p += _p.toString() + " ";
            p = p.trim();
            String entry = index + " " + method + " " + p;
            /* Add a new EditorHistory entry if it's not the same one */
            if(editor_history.size() == 0 || !editor_history.get(editor_history.size() - 1).equals(entry)){
                if(editor_history_ptr == editor_history.size()-1){
                    editor_history_ptr++;
                    editor_history.add(entry);
                }
                else{
                    for(int i = editor_history.size()-1; i >= editor_history_ptr; i--) editor_history.remove(i);

                    editor_history.add(entry);
                    editor_history_ptr = editor_history.size() - 1;
                }
            }
            else editor_history_ptr++;
            updateui();
        }
        static void redo(){
            if (editor_history_ptr == editor_history.size() - 1){
                Log.d("History", "Nothing to redo");
                return;
            }

            editor_history_ptr++;
            perform(editor_history.get(editor_history_ptr));


            updateui();
        }
        static void undo(){
            if(editor_history_ptr <= 0){
                editor_history_ptr = 0;
                Log.d("History", "Nothing to undo");
                return;
            }


            editor_history_ptr--;
            perform(editor_history.get(editor_history_ptr));

            updateui();

        }
        static void perform(String entry) {
            String[] _entry = entry.split(" ");
            Thing t = s.things.get(Integer.parseInt(_entry[0]));
            String method = _entry[1];
            // args entry[2 ..]
            if (method.equals("SETCOORDS")) {
                int x = Integer.parseInt(_entry[2]);
                int y = Integer.parseInt(_entry[3]);
                t.setPosition(x, y);
            }


        }
        static void updateui() {
            ICON_UNDO.setAlpha(255);
            ICON_REDO.setAlpha(255);
            if (editor_history.size() == 0) {
                ICON_UNDO.setAlpha(120);
                ICON_REDO.setAlpha(120);
            }
            else if (editor_history_ptr <= 0) {
                ICON_UNDO.setAlpha(120);
            } else if (editor_history_ptr == editor_history.size() - 1) {
                ICON_REDO.setAlpha(120);
            }
        }
    }
}
