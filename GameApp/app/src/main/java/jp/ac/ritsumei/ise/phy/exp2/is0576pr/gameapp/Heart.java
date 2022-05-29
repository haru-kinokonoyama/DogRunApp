package jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp;

import static jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp.GameView.screenRatioX;
import static jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Heart {
    Bitmap heart;
    public int speed = 20;
    int x = 0, y, width, height;

    Heart(Resources res){
        //画像の読み込み
        heart = BitmapFactory.decodeResource(res, R.drawable.heart);

        width = heart.getWidth();
        height = heart.getHeight();

        width /= 8;
        height /= 8;

        width = (int) (width*screenRatioX);
        height = (int) (height*screenRatioY);

        //画像のリサイズ
        heart = Bitmap.createScaledBitmap(heart, width,height,false);

        y = -height;

    }

    //ハートの画像を渡す
    Bitmap getHeart(){
        return heart;
    }

    //ハートの当たり判定用の矩形
    Rect getCollisionShape(){
        return new Rect((int) (x+(width/2)-5*screenRatioX),(int) (y+(height/2)-5*screenRatioY)
                ,(int) (x+(width/2)+5*screenRatioX),(int) (y+(height/2)+5*screenRatioY));
    }
}
