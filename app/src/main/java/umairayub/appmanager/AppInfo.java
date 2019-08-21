package umairayub.appmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class AppInfo extends AppCompatActivity {

    TextView tvN;
    ListView listVIew;
    ImageView Img_Ic;
    ArrayAdapter<String> listAdapter;
    Bundle bundle;
    String PackName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        tvN = (TextView)findViewById(R.id.tvN);
        listVIew = (ListView)findViewById(R.id.listInfo);
        Img_Ic = (ImageView) findViewById(R.id.Img_Ic);



            ArrayList<String> list = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this,R.layout.appinfo_item,R.id.tv_item,list);


        bundle = getIntent().getExtras();
        PackName = bundle.getString("Package");

        try
        {
            Drawable icon = getPackageManager().getApplicationIcon(PackName);
            Img_Ic.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        listVIew.setAdapter(listAdapter);
        populate();

    }

    public void populate(){

        tvN.setText(bundle.getString("Name")+"\n");
        listAdapter.add("Version : " + bundle.getString("Version") );
        listAdapter.add("Package Name :" +"\n" + PackName);
        listAdapter.add("Installed On : " + bundle.getString("FirstTime"));
        listAdapter.add("Updated On : " + bundle.getString("LastUpdate"));
        String Mpermissins = bundle.getString("Permissions");
        listAdapter.add("Permissions:" + "\n" + Mpermissins);

        listAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        Bungee.fade(AppInfo.this);
        super.onPause();
    }
}
