package umairayub.appmanager;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;
import spencerstudios.com.fab_toast.FabToast;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Item> app;
    Dialog mDialog1;
    SwipeRefreshLayout mSwipeRL;
    ItemAdapter itemAdapter;
    String filePath = null;
    String newFilePath = null;
    private int STORAGE_PERMISSION_CODE = 1;
    Bundle bundle;
    String appname;
    String packagename;
    ArrayAdapter<String>listAdapter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRL = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.ListVIew);



        mSwipeRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
        boolean ifShowDialog = sharedPreferences.getBoolean("showDialog",true);
        if(ifShowDialog){
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();

            }
        }

         MobileAds.initialize(this, "*************************");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        app = new ArrayList<>();
      //method to get Apps List
        getApps();
      //setting Adapter to recyclerView
        itemAdapter = new ItemAdapter(app);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        DividerItemDecoration itemDecorator = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.divider));
        recyclerView.addItemDecoration(itemDecorator);
        // Method  to Sort List Alphabethacally
        sortArray();
        //update List
        recyclerView.setAdapter(itemAdapter);
        int count  = recyclerView.getAdapter().getItemCount();

        getSupportActionBar().setSubtitle("Total Apps :  " + count);
        PrepareOnCLick();

    }


    public void PrepareOnCLick(){

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                long ftt = app.get(position).getFirstInstallTime();
                long ltt = app.get(position).getLastUpdateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(ftt);

                Calendar mCalender = Calendar.getInstance();
                mCalender.setTimeInMillis(ltt);

                appname = app.get(position).getName();
                packagename = app.get(position).getPack();
                String version  = app.get(position).getVersionname();
                String lastupdate = formatter.format(mCalender.getTime());
                String firstinstall = formatter.format(calendar.getTime());
                String Mpermissins = getPermissionsByPackageName(app.get(position).getPack());

                bundle = new Bundle();
                bundle.putString("Name",appname);
                bundle.putString("Package", packagename);
                bundle.putString("Version",version);
                bundle.putString("FirstTime", firstinstall);
                bundle.putString("LastUpdate", lastupdate);
                if(Mpermissins.equals("")){
                    bundle.putString("Permissions", "No Permissions Requested or Granted");
                }else {
                    bundle.putString("Permissions", Mpermissins);
                }
                // custom dialog
                 final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Options...");

                TextView textView = (TextView) dialog.findViewById(R.id.tv_appN);

                textView.setText(appname);
                ListView listView = (ListView) dialog.findViewById(R.id.menulist);
                ArrayList<String>list = new ArrayList<String>();
                listAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.menu,R.id.tv_itm,list);

                list.add("Launch App");
                list.add("App Info");
                list.add("Extract/Backup Apk");
                list.add("Open in Play Store");
                list.add("Share App(Only Link)");
                list.add("");
                listView.setAdapter(listAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0){
                            Launch(packagename);
                            dialog.dismiss();

                        }
                        if(position == 1){
                            Intent intent = new Intent(MainActivity.this,AppInfo.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            dialog.dismiss();

                        }
                        if(position == 2){
                            FabToast.makeText(MainActivity.this, "Extracting Apk",FabToast.LENGTH_LONG,FabToast.INFORMATION,FabToast.POSITION_DEFAULT).show();
                            filePath = getMyApk(packagename);
                            newFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AppManager/Apks";
                            newFilePath = copyFile(filePath, newFilePath);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                            FabToast.makeText(MainActivity.this, " Extracted to " + newFilePath, FabToast.LENGTH_SHORT, FabToast.SUCCESS,  FabToast.POSITION_DEFAULT).show();
                                }
                            }, 1200); // will trigger your code after 1.1 seconds
                        }
                        if(position == 3){
                            openGP(packagename);

                        }
                        if(position == 4){
                            share(packagename,appname);
                        }
                    }
                });
                dialog.show();

            }

            @Override
            public void onDeleteClick(int position) {
                delete(app.get(position).getPack());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 1550); // will trigger your code after 1.1 seconds
            }
            @Override
            public void onInfoClick(int position) {
                appinfo(app.get(position).getPack());
                Bungee.fade(MainActivity.this);

            }

        });
    }

    public void Launch(String pos){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pos);
        startActivity( launchIntent );
    }
    //Sort List Alphabethacally
    public void sortArray() {
        Collections.sort(app, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item t1) {
                return item.getName().compareTo(t1.getName());
            }
        });
    }
    //launch info Activity of a given Package
    public void appinfo(String packagename){
        //redirecting user to app Settings
        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + packagename));
        startActivity(i);
    }
    //get All Installed Apps
    private void getApps(){
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo packageinfo : packages){
            // Filtering out System Apps
            if ((packageinfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                PackageInfo info = null;
                try {
                    info = pm.getPackageInfo(packageinfo.packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                app.add(new Item(pm.getApplicationLabel(packageinfo).toString(),
                    packageinfo.packageName,
                    info.versionName,
                    info.firstInstallTime,
                    info.lastUpdateTime,
                    pm.getApplicationIcon(packageinfo)));
        }
      }
    }
    //Uninstall App
    private void delete(String packageName){
        // Initialize a new Intent to uninstall an app/package
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:"+packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
     }
    // Custom method to Share from package name
    private void share(String packageName,String appName){
        String link  = "App : " +appName +"\n" + "Link : " + "https://play.google.com/store/apps/details?id="+packageName + "\n\nVia : http://bit.ly/2E9HMZw";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,link );
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
     }

    // Custom method to open Google playstore from package name
    public  void openGP(String packageName) {
        Context context = MainActivity.this;
        // you can also use BuildConfig.APPLICATION_ID
        String appId = packageName;
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }

    public void refresh(){
       mSwipeRL.setRefreshing(true);
        app = null;
        app = new ArrayList<>();
        //method to get Apps List
        getApps();
        itemAdapter = new ItemAdapter(app);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);
        PrepareOnCLick();
        sortArray();
        int count  = recyclerView.getAdapter().getItemCount();
        getSupportActionBar().setSubtitle("Total Apps :  " + count);
        itemAdapter.notifyDataSetChanged();
        mSwipeRL.setRefreshing(false);

    }

//////////////////EXTRACT APK METHOD Starts HERE \\\\\\\\\\\\\\\\\\\

    public String getMyApk(String myPackage){
        String path = null;

        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo;

        try {
            applicationInfo = packageManager.getApplicationInfo(myPackage, 0);
            File apkFile = new File(applicationInfo.publicSourceDir);
            if (apkFile.exists()) {
                //installedApkFilePaths.put(packageName, apkFile.getAbsolutePath());
                path = apkFile.getAbsolutePath();
                Log.e("MYTASK", myPackage + " = " + apkFile.getName());
                Log.e("MYTASK", path);
            }

        } catch (PackageManager.NameNotFoundException error) {
            error.printStackTrace();
        }

        return path;
    }


    private String copyFile(String inputPath, String outputPath) {
        String outputFilePath = outputPath +"/" +packagename+".apk";
        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
                Log.e("MYTASK", "created");
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputFilePath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        return outputFilePath;
    }
//////////////////EXTRACT APK METHOD ENDS HERE \\\\\\\\\\\\\\\\\\\

    // Custom method to get app requested and granted permissions from package name
    protected String getPermissionsByPackageName(String packageName) {
        // Initialize a new string builder instance
        StringBuilder builder = new StringBuilder();

        try {
            // Get the package info
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            // Permissions counter
            int counter = 1;

            // Loop through the package info requested permissions
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    String permission = packageInfo.requestedPermissions[i];
                    // To make permission name shorter
                    permission = permission.substring(permission.lastIndexOf(".")+1);
                    builder.append(counter + ". " + permission + "\n");
                    counter++;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
            return builder.toString();

        }
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            mDialog1 = new Dialog(MainActivity.this);
            mDialog1.setContentView(R.layout.cus_dialogp);
            mDialog1.setTitle("Permission Needed");

            TextView msg = mDialog1.findViewById(R.id.msg);
            Button yes = mDialog1.findViewById(R.id.btnY);
            Button no = mDialog1.findViewById(R.id.btnN);


            msg.setText("This permission is needed to Extract Apk in your Phone's storage");


            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    mDialog1.dismiss();

                }

            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("showDialog",false);
                    editor.apply();
                    mDialog1.dismiss();
                    FabToast.makeText(MainActivity.this,"You Can't Extract Apk",FabToast.LENGTH_LONG,FabToast.WARNING,FabToast.POSITION_DEFAULT).show();



                }

            });
            mDialog1.show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                FabToast.makeText(MainActivity.this, "Permission DENIED", Toast.LENGTH_SHORT,FabToast.ERROR,FabToast.POSITION_DEFAULT).show();
            }
     
        }
    }

    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                itemAdapter.filter(query);
                sortArray();
                return true;

            }
            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.filter(newText);
                sortArray();
                return true;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.about:
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
   
   
    @Override
    protected void onStart() {
        refresh();
        super.onStart();
    }

    @Override
    protected void onPause() {
        Bungee.fade(this);
        super.onPause();
    }
}


