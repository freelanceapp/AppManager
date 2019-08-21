package umairayub.appmanager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import spencerstudios.com.bungeelib.Bungee;

public class splash extends Activity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(splash.this,MainActivity.class);
                startActivity(intent);
                Bungee.fade(splash.this);
                finish();
            }
        },100);

    }
}





