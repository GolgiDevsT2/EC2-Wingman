package io.golgi.wingman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by brian on 10/24/14.
 */
public class CPUView extends View {

    public int percent;

    protected void onDraw(Canvas canvas) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        if(percent >= 80) {
            p.setColor(0xffcc0000);
        }
        else if(percent >= 50){
            p.setColor(0xffdd7700);
        }
        else{
            p.setColor(0xff00aa00);
        }

        int w = canvas.getWidth();

        int w1 = (w * percent) / 100;
        Rect r = new Rect(0, 0, w1, canvas.getHeight());

        canvas.drawRect(r, p);

        float x, h;
        h = canvas.getHeight();

        p.setColor(0xee000000);


        canvas.drawLine(0.0f, 0.0f, (float)w, 0.0f, p);
        canvas.drawLine(0.0f, h, (float)w, h, p);
        canvas.drawLine(0.0f, 0.0f, (float)w, 0.0f, p);
        canvas.drawLine(0.0f, h, (float)w, h, p);

        for(int i = 0; i <= 10; i++){
            x = (float)(w * i) / 10;
            canvas.drawLine(x, 0.0f, x, h, p);
        }

        p.setColor(0xaa000000);

        for(int i = 1; i <= 20; i += 2){
            x = (float)(w * i) / 20;
            canvas.drawLine(x, h/2.0f, x, h, p);
        }

        DBG.write("onDraw(): " + canvas.getWidth() + "x" + canvas.getHeight() + "  CPU Usage: " + percent + "%");
    }

    public CPUView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CPUView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CPUView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

}
