package com.example.kato.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends Activity {
//implements LocationListener
    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private ImageView imageView;
    private Button button;

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

            }
        });

        //String s = editText.toString();


    }

/*
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // LocationManagerを取得
        LocationManager mLocationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Criteriaオブジェクトを生成
        Criteria criteria = new Criteria();

        // Accuracyを指定(低精度)
        //criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // PowerRequirementを指定(低消費電力)
        //criteria.setPowerRequirement(Criteria.POWER_LOW);

        // ロケーションプロバイダの取得
        String provider = mLocationManager.getBestProvider(criteria, true);

        // 取得したロケーションプロバイダを表示
        //TextView tv_provider = (TextView) findViewById(R.id.Provider);
        //tv_provider.setText("Provider: "+provider);

        // LocationListenerを登録
        mLocationManager.requestLocationUpdates(provider, 0, 0, this);

    }

        //@Override
        public void onLocationChanged(Location location) {
            // 緯度の表示
            TextView tv_lat = (TextView) findViewById(R.id.Latitude);
            tv_lat.setText("緯度:" + location.getLatitude());

            // 経度の表示
            TextView tv_lng = (TextView) findViewById(R.id.Longitude);
            tv_lng.setText("経度:" + location.getLongitude());

        }

        //@Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        //@Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        //@Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

*/


        // アプリからギャラリーにアクセスして、画像と画像情報を取得 からの戻り

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK && null != intent) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bmp = null;
            Uri selectedImageURI = intent.getData();

            getImageDataPath(selectedImageURI);

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
}


