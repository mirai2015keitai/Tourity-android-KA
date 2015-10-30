package com.example.kato.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements LocationListener {
//
    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private ImageView imageView;
    private Button button, button2;

    double latitude, longitude;

    String messa,img;

    //テストコメントです!


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ギャラリー呼び出し
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);

            }
        });


        EditText editText = (EditText) findViewById(R.id.editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(s.toString());
                messa = s.toString();

            }
        });



        // LocationManagerを取得
        LocationManager mLocationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Criteriaオブジェクトを生成
        Criteria criteria = new Criteria();

        // Accuracyを指定(低精度)
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // PowerRequirementを指定(低消費電力)
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // ロケーションプロバイダの取得
        String provider = mLocationManager.getBestProvider(criteria, true);

        // 取得したロケーションプロバイダを表示
        //TextView tv_provider = (TextView) findViewById(R.id.Provider);
        //tv_provider.setText("Provider: "+provider);

        // LocationListenerを登録
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);}

        //投稿ボタン
        public void alertDialogShow(View v){

            //AlertDialog.Builderクラスのインスタンスを生成

            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);

            //タイトルを設定
            alertDialogBuilder.setTitle("エラー")

            //メッセージを設定
            .setMessage("テキストが未入力です")

            //アイコンを設定
            //.setIcon(R.drawable.ic_launcher)

            //Positiveボタン、リスナーを設定
            .setPositiveButton("OK",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                    //OKボタンが押されたときの処理
                    //varTextView.setText("OKボタンがクリックされました");
                    }
            });

            //投稿文が空の場合ダイアログ表示
            if(messa == null || messa.length() == 0) {
                //ダイアログを表示
                alertDialogBuilder.show();
            }

            //サーバへ送信
            else{
                Post();
            }
        }


    //緯度経度取得
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

/*        TextView tv_lat = (TextView) findViewById(R.id.Latitude);
        tv_lat.setText("緯度:" + latitude);

        TextView tv_lng = (TextView) findViewById(R.id.Longitude);
        tv_lng.setText("経度:" + longitude);
*/    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

        // アプリからギャラリーにアクセスして、画像と画像情報を取得 からの戻り

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK && null != intent) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bmp = null;
            Uri selectedImageURI = intent.getData();

            getImageDataPath(selectedImageURI);

            img = String.valueOf(selectedImageURI);

            InputStream input = null;
            try {
                input = getContentResolver().openInputStream(selectedImageURI);
                bmp = BitmapFactory.decodeStream(input, null, options);

            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (bmp != null) {
                imageView.setImageBitmap(bmp);
            }

        }
    }


    private void getImageDataPath(Uri selectedImageURI) {
    }

    //Volleyによるサーバへの送信
    private void Post(){
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());


        String url = "ここにURL";
//        String url = "http://tourityplus-android.ddns.net/postMessage";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("レスポンス", response);//ここのString型のresponseにサーバからのレスポンスが入る
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("エラーレスポンス", "error");
                    }
                }) {
            protected Map<String, String> getParams() {
                //HashMapをこの関数内で書くことでサーバにPOSTする（送る）ことができる

                Map<String, String> params = new HashMap<String, String>();
                Map<String, File> FileMap = new HashMap<String, File>();

                params.put("user_id", "00001");     //仮ユーザID
                params.put("message", messa);       //メッセージ
                FileMap.put("image_path", new File(img));    //画像パス
                params.put("latitude", String.valueOf(latitude));      //緯度
                params.put("longitude", String.valueOf(longitude));     //経度

                return params;
            }
            Context sContext;

        };
        mQueue.add(request);


    }

}


