package com.kwmm0.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.kwmm0.R;

public class TermsActivity extends AppCompatActivity {
    ImageView check;
    TextView next;
    boolean checked = false;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_layout);
        getWindow().setStatusBarColor(Color.rgb(183,183,183));
        findFromXML();
        addListener();
    }

    private void addListener() {
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checked){
                    check.setImageResource(R.drawable.terms_checked);
                    next.setTextColor(Color.rgb(255, 117,0));
                    checked = true;
                }else{
                    check.setImageResource(R.drawable.terms_not_checked);
                    next.setTextColor(Color.rgb(170, 170,170));
                    checked = false;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked){
                    startActivity(new Intent(TermsActivity.this, SignUpActivity.class));
                }
            }
        });
    }

    private void findFromXML() {
        check = findViewById(R.id.terms_img_check);
        next = findViewById(R.id.terms_txt_next);
    }

}
