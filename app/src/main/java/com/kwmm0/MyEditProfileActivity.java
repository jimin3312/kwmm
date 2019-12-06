package com.kwmm0;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.Login.Hashing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

public class MyEditProfileActivity extends AppCompatActivity {
    TextView withdrawal, nicknameConfirm, passwordConfirm;
    EditText nickname, password;
    ImageView profileImage;
    Bitmap resized;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    JSONObject jsonObject;
    ConstraintLayout constraintLayout;
    Button editPassword;
    boolean isNicknameChecked = false, isPwChecked = false;
    private final int PICTURE_FROM_GALLERY = 1;
    private static final int PIXEL_SIZE = 1024;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_edit_profile_layout);
        getWindow().setStatusBarColor(Color.rgb(183, 183, 183));

        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        findViewFromXML();
        setView();
        addListener();
        withdrawal.setAlpha(0.25f);
        if (sharedPreferences.getString("login", "3").equals("2")) {
            editPassword.setClickable(false);
            editPassword.setAlpha(0.3f);
            password.setHint("구글 계정 변경 불가");
            password.setEnabled(false);
        }
    }

    private void setView() {
        constraintLayout.setFocusableInTouchMode(true);
        constraintLayout.requestFocus();
        withdrawal.setPaintFlags(withdrawal.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (!(sharedPreferences.getString("profile", "null").equals(HttpConnection.profileURL))) {
            Glide.with(MyEditProfileActivity.this)
                    .load(sharedPreferences.getString("profile", "null"))
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }
    }

    private void addListener() {
        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!Pattern.matches("^[가-힣a-zA-Z0-9]{1,8}+$", nickname.getText().toString())) {
                    nicknameConfirm.setVisibility(View.VISIBLE);
                    nicknameConfirm.setText("특수문자 제외, 8글자 이하");
                } else {
                    isNicknameChecked = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                JSONObject jsonObject = new JSONObject();
                if (Pattern.matches("^[가-힣a-zA-Z0-9]{1,8}+$", nickname.getText().toString())) {
                    try {
                        jsonObject.put("func", "checkusable nickname");
                        jsonObject.put("nickname", nickname.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new HttpConnection(MyEditProfileActivity.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject resultJSON) {
                            try {
                                String result = resultJSON.getString("data");
                                if (result.equals("overlapped")) {
                                    nicknameConfirm.setVisibility(View.VISIBLE);
                                    nicknameConfirm.setText("중복된 닉네임입니다.");
                                    isNicknameChecked = false;
                                } else if (result.equals("ok")) {
                                    nicknameConfirm.setVisibility(View.GONE);
                                    isNicknameChecked = true;
                                } else {
                                    new ShowDialog(MyEditProfileActivity.this).show("알림", "인터넷에러");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).execute(jsonObject);
                } else {
                    nicknameConfirm.setVisibility(View.VISIBLE);
                    nicknameConfirm.setText("특수문자 사용 불가, 8자이하 ");
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (password.getText().toString().length() < 8) {
                    passwordConfirm.setVisibility(View.VISIBLE);
                    passwordConfirm.setText("8자 이상");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password.getText().toString().length() >= 8) {
                    passwordConfirm.setVisibility(View.GONE);
                    isPwChecked = true;
                } else {
                    isPwChecked = false;
                }
            }
        });
    }

    private void findViewFromXML() {
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_my_edit_profile);
        withdrawal = (TextView) findViewById(R.id.txt_withdrawal_myAccount);
        nickname = (EditText) findViewById(R.id.etxt_my_edit_nickname);
        password = (EditText) findViewById(R.id.etxt_my_edit_password);
        profileImage = (ImageView) findViewById(R.id.img_my_edit_profileImage);
        nicknameConfirm = (TextView) findViewById(R.id.my_edit_txt_nickname_check);
        passwordConfirm = (TextView) findViewById(R.id.my_edit_txt_pw_check);
        editPassword = (Button) findViewById(R.id.btn_my_edit_password);
    }

    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.img_my_edit_profileImage:
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

            case R.id.btn_my_edit_nickname:
                if (isNicknameChecked) {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("func", "edit nickname");
                        jsonObject.put("id", sharedPreferences.getString("id", "null"));
                        jsonObject.put("nickname", nickname.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new HttpConnection(MyEditProfileActivity.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject jsonObject) {
                            try {
                                String result = jsonObject.getString("data");
                                if (result.equals("success")) {
                                    editor.putString("nickname", nickname.getText().toString());
                                    nickname.setText("");
                                    nickname.setHint("닉네임 변경");
                                    nicknameConfirm.setVisibility(View.GONE);
                                    new ShowDialog(MyEditProfileActivity.this).show("알림", "닉네임이 변경되었습니다.");
                                    editor.commit();

                                } else if (result.equals("overlap")) {
                                    new ShowDialog(MyEditProfileActivity.this).show("알림", "닉네임이 중복되었습니다.");
                                } else {
                                    new ShowDialog(MyEditProfileActivity.this).show("알림", "인터넷 확인");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }).execute(jsonObject);
                } else {

                }
                break;
            case R.id.btn_my_edit_password:
                if (isPwChecked) {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put("func", "edit pw");
                        jsonObject.put("id", sharedPreferences.getString("id", "null"));
                        jsonObject.put("pw", new Hashing().getSHA256(password.getText().toString()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new HttpConnection(MyEditProfileActivity.this, new HttpCallBack() {
                        @Override
                        public void callBack(JSONObject jsonObject) {
                            try {
                                String result = jsonObject.getString("data");
                                if (result.equals("success")) {
                                    password.setText("");
                                    password.setHint("비밀번호 변경");
                                    passwordConfirm.setVisibility(View.GONE);
                                    new ShowDialog(MyEditProfileActivity.this).show("알람", "비밀번호가 변경되었습니다.");
                                } else if (result.equals("same")) {
                                    new ShowDialog(MyEditProfileActivity.this).show("알람", "이전 비밀번호와 같은 비밀번호입니다.");
                                } else {
                                    new ShowDialog(MyEditProfileActivity.this).show("알람", "인터넷 확인");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }).execute(jsonObject);
                }
                break;

            case R.id.txt_withdrawal_myAccount:
                new ShowDialog(MyEditProfileActivity.this).withdrawal();
                break;

        }

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
            options.inSampleSize = 4;

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

            String imageString = ImageConverter.BitmapToString(resized);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("func", "edit profile");
                jsonObject.put("image", imageString);
                jsonObject.put("id", sharedPreferences.getString("id", "null"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            new HttpConnection(MyEditProfileActivity.this, new HttpCallBack() {
                @Override
                public void callBack(JSONObject resultJSON) {

                    try {
                        String result = resultJSON.getString("data");
                        if (result.equals("success")) {

                            new ShowDialog(MyEditProfileActivity.this).show("알람", "이미지가 변경되었습니다.");

                            String url = resultJSON.getString("profilePic");
                            editor.putString("profile", url);
                            editor.commit();

                            Glide.with(MyEditProfileActivity.this)
                                    .load(sharedPreferences.getString("profile", "null"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImage);
                        } else {
                            new ShowDialog(MyEditProfileActivity.this).show("알람", "인터넷 확인");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).execute(jsonObject);
        }
    }


}
