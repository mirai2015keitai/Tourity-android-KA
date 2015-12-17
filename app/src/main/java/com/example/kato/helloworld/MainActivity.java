package com.example.kato.helloworld;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements LocationListener {

    //ギャラリーのみのプログラム
    private ImageView imageView;
    private Button button2;
    InputStream is = null;

    //カメラ、ギャラリー
    private Uri m_uri;
    private static final int REQUEST_CHOOSER = 1000;

    double latitude, longitude;

    private String message;
    private Uri resultUri;

    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();

//緯度経度取得の準備
//--------------------------------------------------------------------------------------------------

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


        // LocationListenerを登録
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);

//プレビューのための入力文章取得
//--------------------------------------------------------------------------------------------------

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
                message = s.toString();

            }
        });
    }

//画像取得
//--------------------------------------------------------------------------------------------------

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
//--------------------------------------------------------------------------------------------------

    public void alertDialogShow(View v) {

        //AlertDialog.Builderクラスのインスタンスを生成

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        //タイトルを設定
        alertDialogBuilder.setTitle("エラー")

                //メッセージを設定
                .setMessage("テキストまたは画像を入力してください")

                        //アイコンを設定
                        //.setIcon(R.drawable.ic_launcher)

                        //Positiveボタン、リスナーを設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //OKボタンが押されたときの処理
                        //varTextView.setText("OKボタンがクリックされました");
                    }
                });

        //投稿文が空 -> ダイアログ表示
        if (message == null || message.length() == 0) {
            //ダイアログを表示
            alertDialogBuilder.show();
        }

        //投稿文あり -> サーバへ送信
        else {
            Post();
            Toast.makeText(getApplicationContext(), "POST完了", Toast.LENGTH_LONG).show();
        }
    }

//緯度経度取得
//--------------------------------------------------------------------------------------------------

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

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

//画像取得
//--------------------------------------------------------------------------------------------------

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHOOSER) {

            if (resultCode != RESULT_OK) {
                // キャンセル時
                return;
            }

            resultUri = (data != null ? data.getData() : m_uri);

            if (resultUri == null) {
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
            imageView = (ImageView) findViewById(R.id.image_view);
            imageView.setImageURI(resultUri);

//送信のためのファイルに画像をセット
//--------------------------------------------------------------------------------------------------

            String id = DocumentsContract.getDocumentId(data.getData());
            String selection = "_id=?";
            String[] selectionArgs = new String[]{id.split(":")[1]};
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns.DATA}, selection, selectionArgs, null);

            if (cursor.moveToFirst()) {
                file = new File(cursor.getString(0));
            }
            cursor.close();
        }
    }

//サーバとの通信
//--------------------------------------------------------------------------------------------------

    private void Post(){

        String url = "http://192.168.33.10:1337/postMessage";
//        String url = "http://tourityplus-android.ddns.net:1337/postMessage";

        Map<String,String> stringMap = new HashMap<String, String>();
        Map<String,File> fileMap = new HashMap<String, File>();

        //送るデータを設定
        stringMap.put("user_id", "1");     //仮ユーザID
        stringMap.put("message", message);       //メッセージ
        fileMap.put("image_path", file);    //画像パス
        stringMap.put("latitude", String.valueOf(latitude));      //緯度
        stringMap.put("longitude", String.valueOf(longitude));     //経度

        MultipartRequest multipartRequest = new MultipartRequest(
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Upload成功
                        Log.d("レスポンス", response);//ここのString型のresponseにサーバからのレスポンスが入る
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Upload失敗
                        Log.d("エラーレスポンス", error.getMessage());
                    }
                },
                stringMap,
                fileMap);

        RequestQueue mQueue = Volley.newRequestQueue(this.getApplicationContext());
        mQueue.add(multipartRequest);
    }

}