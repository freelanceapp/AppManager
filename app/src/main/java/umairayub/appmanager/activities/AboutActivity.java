package umairayub.appmanager.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.gainwise.exceptionbuddy.ExceptionBuddy;

import spencerstudios.com.bungeelib.Bungee;

import umairayub.appmanager.R;

public class AboutActivity extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv = (TextView)findViewById(R.id.tvVersion);
        String version = "1.0";

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tv.setText("Version : "+version);

    }

    @Override
    protected void onPause() {
        Bungee.fade(AboutActivity.this);
        super.onPause();
    }
}
