package com.example.phanhuuchi.huydaoduc.test.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phanhuuchi.huydaoduc.test.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity implements Animation.AnimationListener {

    @BindView(R.id.custombackground)
    ConstraintLayout layout;
    @BindView(R.id.circleImageView)
    ImageView circleImageView;
    @BindView(R.id.circleImageView2)
    ImageView circleImageView2;
    @BindView(R.id.textViewHuy)
    TextView textViewHuy;
    @BindView(R.id.textViewChi)
    TextView textViewChi;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.textView2)
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ButterKnife.bind(this);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.anim_backgroud);
        layout.setAnimation(animation);
        Animation transIcon = AnimationUtils.loadAnimation(this,R.anim.transition_icon);
        circleImageView.setAnimation(transIcon);
        circleImageView2.setAnimation(transIcon);
        textViewHuy.setAnimation(transIcon);
        textViewChi.setAnimation(transIcon);
        textView3.setAnimation(transIcon);
        textView.setAnimation(transIcon);
        textView2.setAnimation(transIcon);
        animation.setAnimationListener(this);



    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
                finish();
            }
        },600);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
