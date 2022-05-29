package jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isMute; //ミュート設定

    //立ち上げた際に最初に呼ばれるメソッド
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activity_main.xml(レイアウトファイル)を表示
        setContentView(R.layout.activity_main);

        //フルスクリーンに対応させる
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //playが押されたときの処理
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            //画面遷移
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        //SharedPreferences→デバイス内にデータを保存する
        //今回は"game"という名前のデータを自アプリのみ書き込み可能なモードで保存
        SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);

        isMute = prefs.getBoolean("isMute", false); //isMuteのデフォルト値false

        //音声アイコンのid
        ImageView volumeCtrl = findViewById(R.id.volumeCtrl);

        if (isMute){
            //ミュート時のアイコン画像の読み込み
            volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_off_24);
        }else {
            //ミュートでない時のアイコン画像の読み込み
            volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_up_24);
        }

        //音声アイコンが押されたときの処理
        volumeCtrl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                isMute = !isMute; //ミュートのon/off切り替え
                if (isMute){
                    //ミュート時のアイコン画像の読み込み
                    volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_off_24);
                }else {
                    //ミュートでない時のアイコン画像の読み込み
                    volumeCtrl.setImageResource(R.drawable.ic_baseline_volume_up_24);
                }
                //prefsに書き込み
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMute", isMute); //現在のisMuteの値を設定
                editor.apply();
            }
        });

    }
}

