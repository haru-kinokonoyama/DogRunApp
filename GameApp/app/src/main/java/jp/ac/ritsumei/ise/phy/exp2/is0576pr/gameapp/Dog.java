package jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp;

import static jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp.GameView.screenRatioX;
import static jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Dog {
    //(x,y)→dogの基本位置, width,height→画像サイズ, counter→走るアニメーションのコマ割カウント
    int x,y,width,height, counter = 0;
    //dog1,2,3→走っているときの画像, lose→ゲームオーバー時の画像, clear1,2→ゲームクリア時の画像
    Bitmap dog1, dog2, dog3, lose, clear, clear2;
    //ジャンプ中か判定用の真偽値
    boolean Jump = false;
    //GameViewのインスタンス
    private GameView gameView;

    Dog(GameView gameView, int screenY, Resources res){

        this.gameView = gameView;

        //画像の読み込み
        dog1 = BitmapFactory.decodeResource(res, R.drawable.dog1);
        dog2 = BitmapFactory.decodeResource(res, R.drawable.dog2);
        dog3 = BitmapFactory.decodeResource(res, R.drawable.dog3);
        lose = BitmapFactory.decodeResource(res, R.drawable.lose);
        clear = BitmapFactory.decodeResource(res, R.drawable.clear);
        clear2 = BitmapFactory.decodeResource(res, R.drawable.clear2);

        width = dog1.getWidth();
        height = dog1.getHeight();

        width /=  4;
        height /= 4;

        width = (int) (width*screenRatioX);
        height = (int) (height*screenRatioY);

        //画像のリサイズ
        dog1 = Bitmap.createScaledBitmap(dog1,width,height,false);
        dog2 = Bitmap.createScaledBitmap(dog2,width,height,false);
        dog3 = Bitmap.createScaledBitmap(dog3,width,height,false);
        lose = Bitmap.createScaledBitmap(lose,width,height,false);
        clear = Bitmap.createScaledBitmap(clear,width*4,height*4,false);
        clear2 = Bitmap.createScaledBitmap(clear2,width*3,height*3,false);

        y = (int) (screenY / 2 - (180*screenRatioY));
        x = (int) ((64 * screenRatioX)+(200*screenRatioX));
    }

    //走っている状態の画像を渡す
    Bitmap getDog(){

        //犬の描画コマ割
        if(counter == 1){
            counter++;
            return dog1;
        }
        if(counter == 2 || counter == 3){
            counter++;
            return dog2;
        }
        if(counter == 4 || counter == 5){
            counter++;
            return dog1;
        }
        if(counter == 5 || counter == 6){
            counter++;
            return dog3;
        }

        counter = 1;
        return dog1;
    }

    //ジャンプ中の画像を渡す
    Bitmap getJumpDog(){
        return dog2;
    }
    //ハードルに当たってゲームオーバーのときの画像を渡す
    Bitmap getLose(){
        return lose;
    }
    //ゲームクリア時の犬の画像を渡す
    Bitmap getClear(){ return clear;}
    //ゲームクリア時の文字画像を渡す
    Bitmap getClear2(){ return clear2;}

    //犬の当たり判定用の矩形
    Rect getCollisionShape(){
        return  new Rect((int) (x+70*screenRatioX),y,x+width,y+height);
    }
}
