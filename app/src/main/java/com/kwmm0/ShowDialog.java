package com.kwmm0;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;

import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.kwmm0.CallBackInterface.ConfirmCallBack;
import com.kwmm0.CallBackInterface.HttpCallBack;
import com.kwmm0.Login.Hashing;
import com.kwmm0.Main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by jade3 on 2018-11-08.
 */

public class ShowDialog {
    Context context;
    ConfirmCallBack confirmCallBack;
    SharedPreferences sharedPreferences;

    public ShowDialog(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public ShowDialog(Context context, ConfirmCallBack confirmCallBack) {
        this.context = context;
        this.confirmCallBack = confirmCallBack;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public void show(String... strings) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder
                .setTitle(strings[0])
                .setMessage(strings[1])
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
        Button postiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        postiveButton.setTextColor(Color.parseColor("#000000"));
    }

    public void report(final String reviewId, final String id) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("신고하기");
        final EditText editText = new EditText(context);
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        linearLayout.setLayoutParams(linearParams);

        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        editTextParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin));
        editText.setLayoutParams(editTextParams);
        editText.setPadding(context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding));
        editText.setBackground(context.getResources().getDrawable(R.drawable.edit_text_boundary, null));
        editText.setHeight(context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_height));
        editText.setGravity(Gravity.TOP);
        editText.setHint("불건전한 내용을 신고해주세요.");

        linearLayout.addView(editText);

        alertBuilder.setView(linearLayout);
        alertBuilder.setPositiveButton("신고", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("func", "report");
                    jsonObject.put("reviewId", reviewId);
                    jsonObject.put("id", id);
                    if (editText.getText().toString().length() >= 8)
                        jsonObject.put("contents", editText.getText().toString());
                    else {
                        show("알림", "8자 이상 작성해주세요");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new HttpConnection(context, new HttpCallBack() {
                    @Override
                    public void callBack(JSONObject jsonObject) {
                        try {
                            String result = jsonObject.getString("data");
                            if (result.equals("신고 접수")) {
                                show("알림", "신고가 접수되었습니다.");
                            } else {
                                //이미 신고당한 사람
                                show("알림", "이미 신고한 리뷰입니다.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).execute(jsonObject);
                dialog.dismiss();
            }
        });
        alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
        setButton(dialog);
    }

    public void startFinish() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder
                .setTitle("warning")
                .setMessage("application is terminated")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCallBack.callBack(true);
                    }
                });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();

        Button postiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        postiveButton.setTextColor(Color.parseColor("#000000"));
        negativeButton.setTextColor(Color.parseColor("#000000"));

    }

    public void logout() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder
                .setTitle("로그아웃")
                .setMessage("정말로 로그아웃 하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmCallBack.callBack(false);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = alertBuilder.create();
        dialog.show();
        setButton(dialog);
    }

    public void withdrawal() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("func", "withdrawal");
            jsonObject.put("id", sharedPreferences.getString("id", "null"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder
                .setTitle("회원탈퇴")
                .setMessage("정말로 계정을 탈퇴 하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new HttpConnection(context, new HttpCallBack() {
                            @Override
                            public void callBack(JSONObject jsonObject) {
                                try {
                                    String result = jsonObject.getString("data");
                                    if (result.equals("success")) {

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear();
                                        editor.commit();

                                        show("알람", "탈퇴되었습니다.");
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);

                                    } else {
                                        show("알람", "인터넷 확인");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(jsonObject);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = alertBuilder.create();
        dialog.show();
        setButton(dialog);
    }

    public void confirmDelete(final JSONObject jsonObject) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("알람");
        alertBuilder.setMessage("정말 지우실 거에요?");
        alertBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                new HttpConnection(context, new HttpCallBack() {
                    @Override
                    public void callBack(JSONObject resultJSON) {
                        try {
                            String result = resultJSON.getString("data");
                            if (result.equals("delete")) {
                                dialog.dismiss();
                                confirmCallBack.callBack(true);
                            } else {
                                dialog.dismiss();
                                new ShowDialog(context).show("알람", "인터넷 확인");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(jsonObject);
            }
        });
        alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
        setButton(dialog);
    }

    public void confirmPassword(final String id){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("현재 비밀번호");
        final EditText editText = new EditText(context);


        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        linearLayout.setLayoutParams(linearParams);

        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        editTextParams.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_margin));
        editText.setLayoutParams(editTextParams);
        editText.setPadding(context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding),
                context.getResources().getDimensionPixelOffset(R.dimen.report_edit_text_padding));
        editText.setBackground(context.getResources().getDrawable(R.drawable.edit_text_boundary, null));
        editText.setHeight(context.getResources().getDimensionPixelOffset(R.dimen.confirmPass_edit_text_height));
        editText.setTextColor(Color.BLACK);
        editText.setGravity(Gravity.TOP);

        linearLayout.addView(editText);

        alertBuilder.setView(linearLayout);
        alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("func", "login");
                    jsonObject.put("id", id);
                    jsonObject.put("pwd", new Hashing().getSHA256(editText.getText().toString()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new HttpConnection(context, new HttpCallBack() {
                    @Override
                    public void callBack(JSONObject jsonObject) {
                        try {
                            String result = jsonObject.getString("data");
                            if (result.equals("로그인")) {
                                context.startActivity(new Intent(context, MyEditProfileActivity.class));
                            } else {
                                show("알림", "비밀번호를 확인해 주세요");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).execute(jsonObject);
                dialog.dismiss();
            }
        });
        alertBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
        setButton(dialog);
    }

    private void setButton(AlertDialog dialog) {
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(Color.parseColor("#000000"));
        negativeButton.setTextColor(Color.parseColor("#000000"));
    }
}