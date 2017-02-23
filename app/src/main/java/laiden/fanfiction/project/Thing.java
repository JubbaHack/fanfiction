package laiden.fanfiction.project;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import laiden.fanfiction.MainActivity;
import laiden.fanfiction.TextRect;

public class Thing {

    private static final float DEFAULT_W = 250;
    private static final float DEFAULT_H = 100;
    private static final float DEFAULT_X = 10;
    private static final float DEFAULT_Y = 10;

    private static transient Paint _p;
    private static transient RectF _r;
    private static transient TextRect _t;

    private static final String DEFAULT_BC = "#FFFFFF";
    private static final String DEFAULT_TC = "#000000";
    private static final String DEFAULT_T = "Default Text";

    public int index;

    private float w;
    private float h;
    private float x;
    private float y;

    private String background_color;
    private String text_color;
    private String text;

    Thing(){
        this._p = new Paint();
        _p.setAntiAlias(true);
        this._r = new RectF();
        this._t = null;

        this.w                = DEFAULT_W;
        this.h                = DEFAULT_H;
        this.x                = DEFAULT_X;
        this.y                = DEFAULT_Y;
        this.background_color = DEFAULT_BC;
        this.text_color       = DEFAULT_TC;
        this.text             = DEFAULT_T;
        this.index            = -1;
    }
    public void setText(String t){
        this.text = t;
    }
    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }
    public void setPosition(PointF p){
        this.x = p.x;
        this.y = p.y;
    }
    public void setBox(RectF b){
        this.x = b.left;
        this.y = b.top;
        this.w = b.width();
        this.h = b.height();
    }

    public PointF getPosition(){
        return new PointF(this.x, this.y);
    }
    public void onTouch(double x, double y){
        //Toast.makeText(MainActivity.instance, "Clicked on Thing '" + this.text + "'", Toast.LENGTH_SHORT).show();
    }
    public RectF box(){
        return new RectF(x, y, x+w, y+h);
    }
    public int resizeFrom(float x, float y){ /* try resizing */
        for(RectF r: getResizerRects()){
            if(inflatedRect(r, 15).contains(x, y)) return getResizerRects().indexOf(r);
        }
        return -1;
    }
    public void render(Canvas canvas, boolean sel){

        _r = box();

        _p.setStyle(Paint.Style.FILL);
        _p.setColor(Color.parseColor(background_color)); //todo res parser
        canvas.drawRect(_r, _p);

        _p.setColor(Color.parseColor(text_color));
        _p.setTextSize(30.0f);

        _t = new TextRect(_p);
        _t.prepare(text, (int)w, (int)h);
        _t.draw(canvas, (int)x, (int)y);



        _p.setStyle(Paint.Style.STROKE);
        _p.setStrokeWidth(2);
        _p.setColor(Color.BLACK);
        canvas.drawRect(_r, _p);

        if(sel) drawResizers(canvas);
    }
    private ArrayList<RectF> getResizerRects(){
        ArrayList<RectF> a = new ArrayList<>();
            a.add(centerRect(x, y));
            a.add(centerRect(x+w/2, y));
            a.add(centerRect(x+w, y));
            a.add(centerRect(x, y+h/2));
            a.add(centerRect(x+w, y+h/2));
            a.add(centerRect(x, y+h));
            a.add(centerRect(x+w/2, y+h));
            a.add(centerRect(x+w, y+h));
        return a;
    }
    private RectF inflatedRect(RectF r, float d){
        return new RectF(r.left - d, r.top - d, r.right + d*2, r.bottom + d*2);
    }
    private void drawResizers(Canvas c){
        for(RectF r: getResizerRects()) drawResizer(c, r);
    }
    private RectF centerRect(float x, float y){
        final float size = 14.0f;
        return new RectF(x - size/2, y - size/2, x+size/2, y+size/2);
    }
    private void drawResizer(Canvas canvas, RectF r){

        _p.setStyle(Paint.Style.FILL);
        _p.setColor(Color.WHITE);
        canvas.drawRect(r, _p);

        _p.setStyle(Paint.Style.STROKE);
        _p.setStrokeWidth(2);
        _p.setColor(Color.BLACK);
        canvas.drawRect(r, _p);

    }
}
