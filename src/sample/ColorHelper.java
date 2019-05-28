package sample;

import java.awt.*;

public class ColorHelper{
    private Color[] colorArray;

    public ColorHelper(){
        colorArray = new Color[10];
        colorArray[0] = new Color(200,0,0);
        colorArray[1] = new Color(0,200,0);
        colorArray[2] = new Color(0,0,200);
        colorArray[3] = new Color(200,200,0);
        colorArray[4] = new Color(200,0,200);
        colorArray[5] = new Color(0,200,200);
        colorArray[6] = new Color(200,100,100);
        colorArray[7] = new Color(150,200,100);
        colorArray[8] = new Color(100,100,200);
        colorArray[9] = new Color(200,80,50);
    }

    public Color[] getArray(){
        return this.colorArray;
    }
}
