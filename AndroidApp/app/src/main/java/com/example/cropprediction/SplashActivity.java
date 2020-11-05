package com.example.cropprediction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        startSplashAnimation();
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    // start main activity
//                    finish();
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                    return;
                }
            }
        };
        timerThread.start();
        startSplashAnimation();



    }

    private void startSplashAnimation() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.start_anim);
        anim.reset();
        ImageView animate = findViewById(R.id.animate_image);
        animate.clearAnimation();
        animate.startAnimation(anim);
    }
}