package jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BackGround {

    int x=0,y=0;
    Bitmap background;

    BackGround(int screenX, int screenY, Resources res){
        //画像の読み込みとリサイズ
        background = BitmapFactory.decodeResource(res, R.drawable.background);
        background = Bitmap.createScaledBitmap(background,screenX,screenY,false);
    }
}
