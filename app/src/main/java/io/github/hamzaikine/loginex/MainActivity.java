package io.github.hamzaikine.loginex;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private ConstraintLayout container;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


       imageView = findViewById(R.id.imageView);
       showLogo();


    }

    private void showLogo(){

        CountDownTimer countDownTimer = new CountDownTimer(2000,1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                imageView.setImageResource(R.drawable.logo);
                imageView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
                startAnimation();
            }
        }.start();

    }

    private void startAnimation(){
        int xValue = imageView.getMaxWidth();
        int yValue = imageView.getMaxHeight();
        imageView.animate().x(xValue).y(yValue).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }



}
