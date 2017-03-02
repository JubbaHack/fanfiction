package laiden.fanfiction;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import laiden.fanfiction.project.Thing;

/**
 * Created by Undust4able on 23.02.2017.
 */

class DrawThread extends Thread{



    private boolean runFlag = false;
    private final SurfaceHolder surfaceHolder;


    public DrawThread(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    @Override
    public void run() {
        Canvas canvas;
        while (runFlag) {

            canvas = null;
            try {
                final Paint debug_paint = new Paint();
                debug_paint.setAntiAlias(true);
                debug_paint.setTextSize(30);
                debug_paint.setColor(Color.WHITE);

                ArrayList<RectF> drawn_boxes = new ArrayList<>();
                ArrayList<Thing> drawables = new ArrayList<>();

                canvas = surfaceHolder.lockCanvas(null);
                if(canvas != null && StoryView.s != null && StoryView.s.things != null) synchronized (surfaceHolder) {
                    canvas.drawColor(Color.parseColor(StoryView.s.background)); // todo: add a resource parser: background can be image or color

                    for(Thing t: StoryView.s.things){
                        //Thing t = StoryView.s.things.get(j);
                        RectF box = t.box();
                        boolean draw = true;
                        for(RectF drawn_box: drawn_boxes) if(drawn_box.contains(box)) draw = false;
                        if(draw){
                            drawables.add(t);
                            drawn_boxes.add(box);
                        }
                    }

                    for(int i = drawables.size()-1; i >= 0; i--){
                        Thing d = drawables.get(i);
                        d.render(canvas, d == StoryView.thing);
                    }

                    canvas.drawText("visible things: " + drawables.size() + ", " + (int)(StoryView.fps()) + "fps", 10, StoryView.height-10, debug_paint);
                }
            }
            finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}