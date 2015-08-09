package com.example.kato.helloworld;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private ImageView imageView;
    private Button button;

    //コメント

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.image_view);

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ギャラリー呼び出し
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);

                // KitKat以降は使えない
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);

            }
        });

    }

    // アプリからギャラリーにアクセスして、画像と画像情報を取得 からの戻り
    protected void onActivityResult( int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK && null != intent) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bmp = null;
            Uri selectedImageURI = intent.getData();

            getImageDataPath(selectedImageURI);

            InputStream input = null;
            try {
                input = getContentResolver().openInputStream(selectedImageURI);
                bmp = BitmapFactory.decodeStream(input, null, options);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(bmp != null){
                imageView.setImageBitmap(bmp);
            }

        }
    }

    private void getImageDataPath(Uri selectedImageURI) {
    }
}