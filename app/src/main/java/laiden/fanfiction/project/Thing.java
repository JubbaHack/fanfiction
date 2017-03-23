package laiden.fanfiction.project;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;

import laiden.fanfiction.ResourceManager;
import laiden.fanfiction.StoryView;
import laiden.fanfiction.Utils;

public class Thing {

    private static final int DEFAULT_W = (int)Utils.ndp(250);
    private static final int DEFAULT_H = (int)Utils.ndp(100);
    private static final int DEFAULT_X = 10;
    private static final int DEFAULT_Y = 10;

    private static transient Paint _p;
    private static transient RectF _r;
    private static transient TextPaint _tp;
    private static transient StaticLayout _sl;

    public transient boolean invisible = false;

    private static final String DEFAULT_BC = "#FFFFFF";
    private static final String DEFAULT_TC = "#000000";
    private static final String DEFAULT_T = "Default Text";

    public int index;

    private int w;
    private int h;
    private int x;
    private int y;

    private String background;
    private String text_color;
    private String text;

    public Thing(){
        _p = new Paint();
        _p.setAntiAlias(true);
        _r = new RectF();
        _tp = new TextPaint(_p);


        this.w                = DEFAULT_W;
        this.h                = DEFAULT_H;
        this.x                = DEFAULT_X;
        this.y                = DEFAULT_Y;
        this.background       = DEFAULT_BC;
        this.text_color       = DEFAULT_TC;
        this.text             = DEFAULT_T;
        this.index            = -1;
    }
    public boolean isImage(){
        return !ResourceManager.isColor(background);
    }
    public void setText(String t){
        this.text = t;
    }
    public void setPosition(float x, float y){
        this.x = (int)x;
        this.y = (int)y;
    }
    public void setBox(RectF b){
        this.x = (int)b.left;
        this.y = (int)b.top;
        this.w = (int)b.width();
        this.h = (int)b.height();
    }

    public Point getPosition(){
        return new Point(this.x, this.y);
    }

    public RectF box(){
        return new RectF(x, y, x+w, y+h);
    }
    public Rect box_int(){
        return new Rect(x, y, x+w, y+h);
    }
    public int resizeFrom(float x, float y){ /* try resizing */
        for(RectF r: getResizerRects()){
            if(Utils.inflatedRect(r, Utils.ndp(15)).contains(x, y)) return getResizerRects().indexOf(r);
        }
        return -1;
    }
    public void setBackground(String b){
        this.background = b;

    }
    public void render(Canvas canvas, boolean sel){
        _p.setPathEffect(null);
        if(this.invisible) {
            _r = box();
            _p.setColor(Color.WHITE);
            _p.setStyle(Paint.Style.STROKE);
            _p.setStrokeWidth(3);
            _p.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
            canvas.drawRect(_r, _p);
            return;
        }
        if(!ResourceManager.isResource(background)){
            _r = box();
            _p.setStyle(Paint.Style.FILL);
            _p.setColor(ResourceManager.color(background));
            canvas.drawRect(_r, _p);

            _p.setColor(Color.parseColor(text_color));
            _p.setTextSize(Utils.ndp(30.0f));

            _tp = new TextPaint(_p);
            _sl = new StaticLayout(text, _tp, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();
            canvas.clipRect(_r);
            canvas.translate(x, y);
            _sl.draw(canvas);
            canvas.restore();

            _sl = null;

            _p.setStyle(Paint.Style.STROKE);
            _p.setStrokeWidth(Utils.ndp(2));
            _p.setColor(Color.BLACK);
            canvas.drawRect(_r, _p);
        }
        else {
            Drawable d = StoryView.instance.drawables.get(background);
            d.setBounds(box_int());
            d.draw(canvas);
        }

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

    private void drawResizers(Canvas c){
        for(RectF r: getResizerRects()) drawResizer(c, r);
    }
    private RectF centerRect(float x, float y){
        final float size = Utils.ndp(14.0f);
        return new RectF(x - size/2, y - size/2, x+size/2, y+size/2);
    }
    private void drawResizer(Canvas canvas, RectF r){

        _p.setStyle(Paint.Style.FILL);
        _p.setColor(Color.WHITE);
        canvas.drawRect(r, _p);

        _p.setStyle(Paint.Style.STROKE);
        _p.setStrokeWidth(Utils.ndp(2));
        _p.setColor(Color.BLACK);
        canvas.drawRect(r, _p);

    }
}
