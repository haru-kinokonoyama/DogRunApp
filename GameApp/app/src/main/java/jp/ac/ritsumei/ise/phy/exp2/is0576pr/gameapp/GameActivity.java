package jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //フルスクリーンに対応させる
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //スクリーンサイズ取得
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        //gameView作成
        gameView = new GameView(this, point.x,point.y);

        //gameView(ビュー)を表示
        setContentView(gameView);
    }
    @Override
    protected  void onPause(){ //終了
        super.onPause();
        gameView.pause();
    }
    @Override
    protected  void onResume(){ //監視
        super.onResume();
        gameView.resume();
    }
}