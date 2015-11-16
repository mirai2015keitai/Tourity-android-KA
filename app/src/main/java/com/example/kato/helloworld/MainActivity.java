package com.example.kato.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    //ギャラリーのみのプログラム
    //private static final int RESULT_PICK_IMAGEFILE = 1001;

    //ギャラリーのみのプログラム
    //private ImageView imageView;
    private Button button2;

    //private Button button;

    //カメラ、ギャラリー
    private Uri m_uri;
    private static final int REQUEST_CHOOSER = 1000;

    double latitude, longitude;

    String messa, img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();

        /*imageView = (ImageView) findViewById(R.id.image_view);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ギャラリー呼び出し
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);

            
        });*/


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
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);


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


    }

    private void setViews() {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(button_onClick);

    }

    private View.OnClickListener button_onClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showGallery();
        }

    };

    private void showGallery() {

        //カメラの起動Intentの用意
        String photoName = System.currentTimeMillis() + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, photoName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        m_uri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri);

        // ギャラリー用のIntent作成
        Intent intentGallery;
        if (Build.VERSION.SDK_INT < 19) {
            intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
            intentGallery.setType("image/*");
        } else {
            intentGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
            intentGallery.setType("image/jpeg");
        }
        Intent intent = Intent.createChooser(intentCamera, "画像の選択");
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentGallery});
        startActivityForResult(intent, REQUEST_CHOOSER);

    }










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

            //Postでサーバへ送信
            else{
                Post();
            }
        }


    //緯度経度取得
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //緯度経度を画面上に表示
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CHOOSER) {

            if(resultCode != RESULT_OK) {
                // キャンセル時
                return ;
            }

            Uri resultUri = (data != null ? data.getData() : m_uri);

            if(resultUri == null) {
                // 取得失敗
                return;
            }

            // ギャラリーへスキャンを促す
            MediaScannerConnection.scanFile(
                    this,
                    new String[]{resultUri.getPath()},
                    new String[]{"image/jpeg"},
                    null
            );

            // 画像を設定
            ImageView imageView = (ImageView)findViewById(R.id.image_view);
            imageView.setImageURI(resultUri);
        }

        /* ギャラリーのみのプログラム
        // アプリからギャラリーにアクセスして、画像と画像情報を取得 からの戻り
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

        }*/

    }


    private void getImageDataPath(Uri selectedImageURI) {
    }

    //Volleyによるサーバへの送信
    private void Post(){

        //確認のためのトースト
        Toast.makeText(getApplicationContext(),"POST完了",Toast.LENGTH_LONG).show();

        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());


//        String url = "ここにURL";
        String url = "http://tourityplus-android.ddns.net/postMessage";

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


