package jp.ac.ritsumei.ise.phy.exp2.is0576pr.gameapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{

    //各クラスのインスタンスを準備
    private Thread thread;
    private Random random;
    private Paint paint;
    private BackGround backGround1,backGround2;
    private Dog dog;
    private Hurdle hurdle;
    private Heart[] hearts;  //ハートを格納する配列
    private SharedPreferences prefs;
    private GameActivity activity;
    private SoundPool soundPool;

    //ゲームプレイ中,ゲームオーバー,ゲームクリアを表す変数
    private boolean isPlaying, isGameOver = false, isGameClear = false;
    //オーディオファイルを取得したときのid
    private int gameClearSound, getSound, gameOverSound;
    //スクリーンサイズとスコア
    private int screenX, screenY, score = 0;
    //スクリーンの縦横比率
    public static float screenRatioX, screenRatioY;
    //ジャンプ中のカウント
    private double jumpCount = 0;
    //ジャンプ中のy,ジャンプ前のy
    private int jump_y,jumpBeforey;

    public GameView(GameActivity activity, int screenX, int screenY) {

        super(activity);

        this.activity = activity;

        //SharedPreferences→デバイス内にデータを保存する
        //"game"という名前のデータを自アプリのみ書き込み可能なモードで保存
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        }else{
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        }

        //オーディオファイルの読み込み
        gameOverSound = soundPool.load(activity, R.raw.gameover,1);
        gameClearSound = soundPool.load(activity, R.raw.clear,2);
        getSound = soundPool.load(activity,R.raw.get,3);

        //スクリーンサイズと比率
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920 / screenX;
        screenRatioY = 1920 / screenY;

        //背景の作成とスクロール背景の繋ぎ
        backGround1 = new BackGround(screenX, screenY, getResources());
        backGround2 = new BackGround(screenX, screenY, getResources());
        backGround2.x = screenX;

        //犬の作成
        dog = new Dog(this,screenY,getResources());
        jumpBeforey = dog.y; //ジャンプ用に犬のy方向の初期値を取得しておく

        //Paintクラスのインスタンスpaint作成
        paint = new Paint();

        //画面中央上に表示される得点の文字サイズと色
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        //ハードル作成
        hurdle = new Hurdle(getResources());

        //ハートの作成
        hearts = new Heart[3]; //スクリーンのハートの数
        for(int i = 0; i < 3; i++){
            Heart heart = new Heart(getResources());
            hearts[i] = heart;
        }

        random = new Random();
    }

    //スレッドの処理の中で一定の時間間隔で画面を描画する処理を実行
    @Override
    public void run() {
        while(isPlaying){
            update();
            draw();
            sleep();
        }
    }

    //スレッドの再開
    public void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }
    //スレッドの一時停止
    public void pause(){
        try {
            isPlaying = false;
            thread.join();
        }catch(InterruptedException e){
            e.printStackTrace(); //エラー処理
        }
    }

    //描画毎の各値のアップデート
    private void update(){

        //背景スクロール
        backGround1.x -= 30 * screenRatioX;
        backGround2.x -= 30 * screenRatioX;

        if(backGround1.x + backGround1.background.getWidth() < 0){
            backGround1.x = screenX;
        }
        if(backGround2.x + backGround2.background.getWidth() < 0){
            backGround2.x = screenX;
        }

        //犬のジャンプ中はdogjump()を呼び出す
        if(dog.Jump){
            dogJump();
        }else{
            dog.y = (int) (screenY/2 - 180*screenRatioY); //走っているとき
        }

        //ハートのx方向の値更新
        for (Heart heart : hearts){
            heart.x -= heart.speed;
            //ハードルが画面端にいったとき
            if (heart.x + heart.width < 0){
                //ハートの速さをランダムに変える
                int bound = (int) (70 * screenRatioX);
                heart.speed = random.nextInt(bound);
                //最低速度
                if (heart.speed < 30 * screenRatioX){
                    heart.speed = (int) (30 * screenRatioX);
                }
                heart.x = screenX;
                heart.y = (int) (screenY/2 - 300*screenRatioY);

            }
        }

        //ハードルのx方向の値更新
        hurdle.x -= hurdle.speed;
        //ハードルが画面端にいったとき
        if (hurdle.x + hurdle.width < 0){
            hurdle.x = screenX;
            hurdle.y = (int) (screenY/2 - 240*screenRatioY);
        }


        //犬がハートに当たったか
        for (Heart heart : hearts){
            //犬とハートの衝突判定
            if (Rect.intersects(heart.getCollisionShape(),dog.getCollisionShape())){

                //ミュートでないとき効果音が出る
                if (!prefs.getBoolean("isMute",false)){
                    soundPool.play(getSound, 1,1,0,0,1);
                }
                //スコア+1
                score++;
                heart.x = -500;
            }
        }

        //犬とハードルの衝突判定
        if (Rect.intersects(hurdle.getCollisionShape(), dog.getCollisionShape())) {
            isGameOver = true; //犬がハードルに当たったときゲームオーバー
            return;
        }

        //ハートを20個以上とればゲームクリア
        if (score >= 20){
            isGameClear = true;
        }

    }

    //犬がジャンプするときのy方向の値を決める関数
    private void dogJump() {
        dog.y -= 45 - 9.8*(jumpCount); //v=v_o+at
        jumpCount += 0.3; //tのカウント
        jump_y = dog.y;//その瞬間のdogのy位置を取得

        if ((jump_y-jumpBeforey)>1){ //地面についたとき
            jumpCount = 0; //tカウントリセット
            dog.y = jumpBeforey; //dogを元の位置に戻す
            jump_y = jumpBeforey;
            dog.Jump = false;
        }

    }

    //描画
    private void draw(){

        if(getHolder().getSurface().isValid()){
            //他のスレッドからCanvasを操作されないようにロック
            Canvas canvas = getHolder().lockCanvas();

            //背景の描画
            canvas.drawBitmap(backGround1.background,backGround1.x,backGround1.y,paint);
            canvas.drawBitmap(backGround2.background,backGround2.x,backGround2.y,paint);
            //ハードルの描画
            canvas.drawBitmap(hurdle.getHurdle(),hurdle.x,hurdle.y,paint);
            //ハートの描画
            for (Heart heart : hearts){
                canvas.drawBitmap(heart.getHeart(),heart.x,heart.y,paint);
            }
            //スコアの描画
            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if (isGameOver){ //ゲームオーバー時
                isPlaying = false;
                //ミュートでないとき効果音を鳴らす
                if (!prefs.getBoolean("isMute",false)){
                    soundPool.play(gameOverSound, 1,1,0,0,1);
                }
                //ゲームオーバー用の犬を描画
                canvas.drawBitmap(dog.getLose(), dog.x,dog.y,paint);
                //canvasのロック解除
                getHolder().unlockCanvasAndPost(canvas);
                //タイトル画面に戻る
                waitBeforeExiting();
                return;
            }

            if (isGameClear){ //ゲームクリア時
                isPlaying = false;

                //少し待つ
                try {
                    int sleepTime = 300;
                    Thread.sleep(sleepTime); // 300ms
                } catch (InterruptedException e) { //エラー処理
                    e.printStackTrace();
                }

                //ミュートでないとき効果音を鳴らす
                if (!prefs.getBoolean("isMute",false)){
                    soundPool.play(gameClearSound, 1,1,0,0,1);
                }
                //ゲームクリア用の犬と｢CLEAR｣を描画
                canvas.drawBitmap(dog.getClear(), screenX/2,(int) (50*screenRatioY),paint);
                canvas.drawBitmap(dog.getClear2(), (int) (screenX/3-100*screenRatioX),screenY/3,paint);
                ////canvasのロック解除
                getHolder().unlockCanvasAndPost(canvas);
                //タイトル画面へ戻る
                waitBeforeExiting();
                return;
            }

            if (dog.Jump){
                //ジャンプ中の犬の描画
                canvas.drawBitmap(dog.getJumpDog(), dog.x, dog.y, paint);
            }else {
                //ジャンプ中でない犬の描画
                canvas.drawBitmap(dog.getDog(), dog.x, dog.y, paint);
            }

            //canvasのロック解除
            getHolder().unlockCanvasAndPost(canvas);
        }

    }

    //ゲームオーバー又はゲームクリア時にタイトル画面に戻る関数
    private void waitBeforeExiting() {

        try {
            Thread.sleep(3000); //3秒
            //画面遷移
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            //現在のアクティビティを終了し元のアクティビティに戻る
            activity.finish();
        } catch (InterruptedException e) { //エラー処理
            e.printStackTrace();
        }

    }

    //待ちの関数
    private void sleep(){
        try {
            thread.sleep(17); //17ミリ秒
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    //指で画面を押したときの処理
    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: //指が押している間
                dog.Jump = true;
                break;
            case MotionEvent.ACTION_UP: //指が離れたとき
                break;
        }
        return true;
    }

}
