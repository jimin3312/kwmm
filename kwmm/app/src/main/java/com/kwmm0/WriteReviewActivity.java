package com.kwmm0;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;


import com.kwmm0.CallBackInterface.HttpCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jade3 on 2018-11-07.
 */

public class WriteReviewActivity extends AppCompatActivity {
    private final int PIXEL_SIZE = 1024;
    private ImageView imageView;
    private Button btnReviewComplete;
    private RatingBar reviewRatingBar;
    private EditText etxtReview;
    private final int PICTURE_FROM_GALLERY = 1;
    private String imageString = null;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_review_layout);

        fimdViewFromXML();
    }

    private void fimdViewFromXML() {
        imageView = (ImageView) findViewById(R.id.img_food_review);
        btnReviewComplete = (Button) findViewById(R.id.btn_review_complete);
        reviewRatingBar = (RatingBar) findViewById(R.id.review_rating_bar);
        etxtReview = (EditText) findViewById(R.id.etxt_review_text);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode)
        {
            case PICTURE_FROM_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICTURE_FROM_GALLERY);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;

            Bitmap photo = BitmapFactory.decodeFile(picturePath, options);
            int width = photo.getWidth(), height = photo.getHeight();
            float ratio;
            if(width > height){
                ratio = (float)height / width;
                if(width > PIXEL_SIZE)
                    width = PIXEL_SIZE;
                height = (int)(width * ratio);
            }else{
                ratio = (float)width / height;
                if(height > PIXEL_SIZE)
                    height = PIXEL_SIZE;
                width = (int)(height * ratio);
            }

            Bitmap resized = Bitmap.createScaledBitmap(photo, width, height, true);

            ExifInterface exif;
            try {
                exif = new ExifInterface(picturePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }

                resized = Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageString = ImageConverter.BitmapToString(resized);
            imageView.setImageBitmap(resized);
        }
    }

    public void onClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

        switch(view.getId())
        {
            case R.id.img_food_review:
                try{

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }else {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, 1);
                        }
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 1);
                    }

                } catch(Exception e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_review_complete:
                float rating = reviewRatingBar.getRating();
                String text = etxtReview.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("func", "리뷰작성");
                    jsonObject.put("rate", rating);
                    jsonObject.put("contents", text);
                    jsonObject.put("id", sharedPreferences.getString("id", "A"));
                    jsonObject.put("menuId", getIntent().getIntExtra("menuId",-1));

                    if (imageString != null){
                        jsonObject.put("image", imageString);
                    }
                    else{
                        jsonObject.put("image","null");
                    }

                    new HttpConnection(WriteReviewActivity.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject resultJSON) {
                            try {
                                String result = resultJSON.getString("data");
                                if(result.equals("작성성공")){
                                    Toast.makeText(WriteReviewActivity.this, "작성 완료", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else if(result.equals("이미작성")){
                                    new ShowDialog(WriteReviewActivity.this).show("알림", "이미 작성한 리뷰가 있습니다.");
                                }else{
                                    new ShowDialog(WriteReviewActivity.this).show("알림", "인터넷 에러");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}