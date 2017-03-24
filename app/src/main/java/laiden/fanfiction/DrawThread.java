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
import android.graphics.Typeface;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import laiden.fanfiction.project.Story;
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
                debug_paint.setTextSize(Utils.ndp(30));
                debug_paint.setColor(Color.WHITE);

                ArrayList<Rect> drawn_boxes = new ArrayList<>();
                ArrayList<Thing> drawables = new ArrayList<>();



                canvas = surfaceHolder.lockCanvas(null);


                if(canvas != null && StoryView.s != null && StoryView.s.things != null) synchronized (surfaceHolder) {
                    if(StoryView.s != null) {


                        canvas.drawColor(Color.parseColor(StoryView.s.background)); // todo: add a resource parser: background can be image or color

                        for (int i = StoryView.s.things.size() - 1; i >= 0; i--) {
                            Thing t = StoryView.s.things.get(i);
                            boolean draw = true;
                            for (Rect drawn_box : drawn_boxes)
                                if (drawn_box.contains(t.box())) draw = false;
                            if (draw) {
                                drawables.add(t);
                                drawn_boxes.add(t.box());
                            }
                        }

                        for (int i = drawables.size() - 1; i >= 0; i--) {
                            Thing d = drawables.get(i);
                            d.render(canvas, d == StoryView.thing);
                        }
                        //canvas.drawText("visible things: " + drawables.size() + ", " + (int)(StoryView.fps()) + "fps", 10, StoryView.height-10, debug_paint);


                        if (StoryView.s != null) {
                            if (StoryView.s.addable()) StoryView.ICON_COPY.setAlpha(255);
                            else StoryView.ICON_COPY.setAlpha(127);


                            if (!StoryView.moving) {
                                canvas.drawRect(StoryView.CONSOLE_RECT, debug_paint);
                                if (StoryView.thing != null) StoryView.ICON_COPY.draw(canvas);
                                else StoryView.ICON_ADD.draw(canvas);
                                StoryView.ICON_UNDO.draw(canvas);
                                StoryView.ICON_REDO.draw(canvas);
                            } else {
                                if (StoryView.CONSOLE_RECT.contains(StoryView.touch.x, StoryView.touch.y)) {
                                    debug_paint.setColor(Color.RED);
                                    debug_paint.setTypeface(Typeface.DEFAULT_BOLD);
                                    canvas.drawRect(StoryView.CONSOLE_RECT, debug_paint);

                                    debug_paint.setColor(Color.WHITE);
                                    debug_paint.setTextSize(Utils.ndp(40));
                                    StoryView.ICON_DELETE.draw(canvas);
                                    canvas.drawText("Remove", Utils.ndp(100), StoryView.height - Utils.ndp(37), debug_paint);
                                } else {
                                    canvas.drawRect(StoryView.CONSOLE_RECT, debug_paint);
                                    if (StoryView.thing != null) StoryView.ICON_COPY.draw(canvas);
                                    else StoryView.ICON_ADD.draw(canvas);
                                    StoryView.ICON_UNDO.draw(canvas);
                                    StoryView.ICON_REDO.draw(canvas);
                                }
                            }


                        }
                    }
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