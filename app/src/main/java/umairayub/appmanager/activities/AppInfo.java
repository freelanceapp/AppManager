package umairayub.appmanager.activities;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

//import spencerstudios.com.bungeelib.Bungee;
import spencerstudios.com.bungeelib.Bungee;
import umairayub.appmanager.R;

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
