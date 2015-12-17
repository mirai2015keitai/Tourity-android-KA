package com.example.kato.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by s1321067 on 2015/12/17.
 */
public class CameraActivity extends Activity {

    private SurfaceView mySurfaceView;
    private Camera myCamera; //hardware

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);

        //投稿画面への画面遷移
        Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }
        });

        //SurfaceView
        mySurfaceView = (SurfaceView)findViewById(R.id.mySurfaceVIew);

        //SurfaceHolder(SVの制御に使うInterface）
        SurfaceHolder holder = mySurfaceView.getHolder();
        //コールバックを設定
        holder.addCallback(callback);

    }

    //コールバック
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            //CameraOpen
            myCamera = Camera.open();

            //出力をSurfaceViewに設定
            try{
                myCamera.setPreviewDisplay(surfaceHolder);
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

            //プレビュースタート（Changedは最初にも1度は呼ばれる）
            myCamera.startPreview();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            //片付け
            myCamera.release();
            myCamera = null;
        }
    };
}
