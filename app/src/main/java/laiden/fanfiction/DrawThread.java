package laiden.fanfiction;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.SurfaceHolder;

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
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    canvas.drawColor(Color.parseColor(StoryView.s.background)); // todo: add a resource parser: background can be image or color

                    for(int j = StoryView.s.things.size() - 1; j >= 0; j--){
                        boolean sel = StoryView.s.things.get(j) == StoryView.thing;
                        StoryView.s.things.get(j).render(canvas, sel);
                    }
                }
            }
            finally {
                if (canvas != null) {
                    // отрисовка выполнена. выводим результат на экран
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}