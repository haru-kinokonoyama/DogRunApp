package jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp;

import static jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp.GameView.screenRatioX;
import static jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Hurdle {
    Bitmap hurdle;
    public int speed = 30;
    int x = 0, y, width, height;

    Hurdle(Resources res){

        //画像の読み込み
        hurdle = BitmapFactory.decodeResource(res, R.drawable.hurdle);

        width = hurdle.getWidth();
        height = hurdle.getHeight();

        width /= 3;
        height /= 3;

        width = (int) (width*screenRatioX);
        height = (int) (height*screenRatioY);

        //画像のリサイズ
        hurdle = Bitmap.createScaledBitmap(hurdle, width,height,false);

        y = -height;

    }


    //ハードルの画像を渡す
    Bitmap getHurdle(){
        return hurdle;
    }

    //ハードルの当たり判定用の矩形
    Rect getCollisionShape(){
        return new Rect(x+(width/4),(int) (y+(height/3)+(30*screenRatioY))
                ,(int) (x+(2*width/3)-20*screenRatioY),y+height);
    }
}
