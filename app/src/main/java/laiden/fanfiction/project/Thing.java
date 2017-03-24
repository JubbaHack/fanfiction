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
import android.util.Log;

import java.util.ArrayList;

import laiden.fanfiction.ResourceManager;
import laiden.fanfiction.StoryView;
import laiden.fanfiction.Utils;

import static laiden.fanfiction.Utils.str;

public class Thing {

    private static final int DEFAULT_W = (int)Utils.ndp(250);
    private static final int DEFAULT_H = (int)Utils.ndp(100);
    private static final int DEFAULT_X = 10;
    private static final int DEFAULT_Y = 10;

    private static transient Paint _p;
    private static transient TextPaint _tp;
    private static transient StaticLayout _sl;

    public transient boolean invisible = false;

    private static final String DEFAULT_BC = "#FFFFFFFF";
    private static final String DEFAULT_TC = "#FF000000";
    private static final String DEFAULT_T = str("default_text");

    public int index;

    private Rect box;

    private String background;
    private String text_color;
    private String text;

    private int border_size;
    private String border_color;
    private int border_dx;
    private int border_dy;

    public Thing(){
        _p = new Paint();
        _p.setAntiAlias(true);
        _tp = new TextPaint(_p);


        this.box              = new Rect(DEFAULT_X, DEFAULT_Y, DEFAULT_X + DEFAULT_W, DEFAULT_Y + DEFAULT_H);
        this.background       = DEFAULT_BC;
        this.text_color       = DEFAULT_TC;
        this.border_size      = 2;
        this.border_color     = DEFAULT_TC;
        this.border_dx        = 0;
        this.border_dy        = 0;
        this.text             = DEFAULT_T;
        this.index            = -1;
    }
    public boolean isImage(){
        return !ResourceManager.isColor(background);
    }
    public void setText(String t){
        this.text = t;
    }
    public void setPosition(int x, int y){
        this.box.offsetTo(x, y);
    }
    public void setTextColor(String color){
        this.text_color = color;
    }
    public Rect box(){
        return new Rect(this.box);
    }
    public String getTextColor(){
        return this.text_color;
    }
    public void setBox(Rect b){
        this.box = b;
    }

    public Point getPosition(){
        return new Point(this.box.left, this.box.top);
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
    public String getText(){
        return this.text;
    }
    public String getBackground(){
        return this.background;
    }
    public int getBorderSize(){
        return border_size;
    }
    public int getBorderDx(){
        return border_dx;
    }
    public int getBorderDy(){
        return border_dy;
    }
    public boolean hasBorderOffset(){
        return !(border_dx == 0 && border_dy == 0);
    }
    public String getBorderColor(){
        return border_color;
    }
    public boolean hasBorder(){
        return border_size > 0;
    }
    public void render(Canvas canvas, boolean sel){
        _p.setPathEffect(null);
        _p.setColor(0x00000000);
        _p.setStyle(Paint.Style.FILL);

        if(this.invisible) {
            _p.setColor(Color.WHITE);
            _p.setStyle(Paint.Style.STROKE);
            _p.setStrokeWidth(3);
            _p.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
            canvas.drawRect(this.box, _p);
            return;
        }
        if(!ResourceManager.isResource(background)){

            if(hasBorder()){
                _p.setColor(Color.parseColor(border_color));
                canvas.drawRect(Utils.getBorder(this.box, border_size, border_dx, border_dy), _p);
            }

            _p.setColor(ResourceManager.color(background));
            canvas.drawRect(this.box, _p);

            _p.setColor(Color.parseColor(text_color));
            _p.setTextSize(Utils.ndp(30.0f));

            _tp = new TextPaint(_p);
            _sl = new StaticLayout(text, _tp, this.box.width(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            canvas.save();
            canvas.clipRect(this.box);
            canvas.translate(this.box.left, this.box.top);
            _sl.draw(canvas);
            canvas.restore();

            _sl = null;


        }
        else {
            Drawable d = StoryView.instance.drawables.get(background);
            d.setBounds(box);
            d.draw(canvas);
        }

        if(sel) drawResizers(canvas);
    }
    private ArrayList<RectF> getResizerRects(){
        ArrayList<RectF> a = new ArrayList<>();
            a.add(centerRect(this.box.left, this.box.top));
            a.add(centerRect(this.box.left+this.box.width()/2, this.box.top));
            a.add(centerRect(this.box.left+this.box.width(), this.box.top));
            a.add(centerRect(this.box.left, this.box.top+this.box.height()/2));
            a.add(centerRect(this.box.left+this.box.width(), this.box.top+this.box.height()/2));
            a.add(centerRect(this.box.left, this.box.top+this.box.height()));
            a.add(centerRect(this.box.left+this.box.width()/2, this.box.top+this.box.height()));
            a.add(centerRect(this.box.left+this.box.width(), this.box.top+this.box.height()));
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
