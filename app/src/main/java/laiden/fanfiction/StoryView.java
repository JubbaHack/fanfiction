package laiden.fanfiction;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;

import laiden.fanfiction.project.Scene;
import laiden.fanfiction.project.Story;
import laiden.fanfiction.project.Thing;

public class StoryView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private static final int DOUBLECLICK = 300;

    public static Story story; // todo make fields private
    public static Scene s;
    public static Thing thing;
    public static int scene;
    private static int resize_corner;
    private static PointF click;
    private static boolean doubleclick = false;
    private static long doubleclick_time = 0;
    private static ArrayList<String> editor_history = new ArrayList<>();
    private static int editor_history_ptr = 0;

    private static Paint p;
    public static int width;
    public static int height;

    public static StoryView instance;

    public StoryView(Context context) {
        super(context);
        instance = this;
        getHolder().addCallback(this);
        p = new Paint();
        p.setAntiAlias(true);
        this.story = null;
        this.thing = null;
        this.scene = 0;
        this.resize_corner = -1;
        this.click = new PointF();
        this.s = null;

        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                StoryView.this.onTouch(event.getX(), event.getY(), event.getAction());
                return true;
            }
        });
    }
    private void onTouch(float x, float y, int event){
        if(event == MotionEvent.ACTION_DOWN) {

            boolean was_selected = false;
            if(thing != null) resize_corner = thing.resizeFrom(x, y);
            if(resize_corner == -1) {
                for (Thing t : s.things) {
                    if (t.box().contains(x, y)) {
                        //t.onTouch(x, y);

                        //Log.d("History", "SETCOORDS(" + t.getPosition().x + "," + t.getPosition().y + ")");
                        EditorHistory.add(t, "SETCOORDS", t.getPosition().x, t.getPosition().y);

                        if(thing != null && doubleclick && (System.currentTimeMillis() - doubleclick_time) <= DOUBLECLICK) {

                            DialogWindow alert = new DialogWindow();
                            alert.showDialog(MainActivity.instance, "Text");

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
                RectF r = new RectF(500, 500, 600, 600);
                if(r.contains(x, y)){
                    EditorHistory.undo();
                }
            }

            if(!was_selected && resize_corner == -1) thing = null;

        }
        else if(event == MotionEvent.ACTION_MOVE){
            if(thing != null){
                if(resize_corner != -1){ // resize
                    if(resize_corner == 0){
                        RectF box = thing.box();
                        box.left = x;
                        box.top = y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 1){
                        RectF box = thing.box();
                        box.top = y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 2){
                        RectF box = thing.box();
                        box.right = x;
                        box.top = y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 3){
                        RectF box = thing.box();
                        box.left = x;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 4){
                        RectF box = thing.box();
                        box.right = x;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 5){
                        RectF box = thing.box();
                        box.left = x;
                        box.bottom = y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 6){
                        RectF box = thing.box();
                        box.bottom = y;
                        thing.setBox(box);
                    }
                    else if(resize_corner == 7){
                        RectF box = thing.box();
                        box.right = x;
                        box.bottom = y;
                        thing.setBox(box);
                    }
                }
                else thing.setPosition(x-click.x, y-click.y); // move
            }
        }
        else if(event == MotionEvent.ACTION_UP){
            resize_corner = -1;
            if(thing != null) EditorHistory.add(thing, "SETCOORDS", thing.getPosition().x, thing.getPosition().y);

        }
    }
    static LinkedList<Long> times = new LinkedList<Long>(){{
        add(System.nanoTime());
    }};

    /*@Override
    protected void onDraw(Canvas canvas) {
        double fps = fps();
        // ...
        super.onDraw(canvas);
    }*/

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
        this.s = this.story.scenes.get(scene);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MainActivity.instance.getWindow().setNavigationBarColor(Color.parseColor(this.s.background));
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        this.height = height;
        this.width = width;
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
            } catch (InterruptedException e) {
                // если не получилось, то будем пытаться еще и еще
            }
        }
    }
    static final class EditorHistory {

        static void add(Thing t, String method, Object... params){
            int index = s.things.indexOf(t);
            String p = "";
            for(Object _p: params) p += _p.toString() + " ";
            p = p.trim();
            String entry = index + " " + method + " " + p;

            Log.d("History", entry);

            /* Add a new EditorHistory entry if it's not the same one */
            if(editor_history.size() == 0 || !editor_history.get(editor_history.size() - 1).equals(entry)) editor_history.add(entry);
            editor_history_ptr = editor_history.size() - 1;
        }
        static void undo(){
            if(editor_history.size() == 0) return; /* Nothing to undo */
            String[] entry = editor_history.get(editor_history_ptr).split(" ");
            Thing t = s.things.get(Integer.parseInt(entry[0]));
            String method = entry[1];
            // args entry[2 ..]
            if(method.equals("SETCOORDS")){
                int x = Integer.parseInt(entry[2]);
                int y = Integer.parseInt(entry[3]);
                t.setPosition(x, y);
                editor_history_ptr--;
                Log.d("History", "Undone");
            }
        }
    }
}
