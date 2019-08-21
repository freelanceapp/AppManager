package umairayub.appmanager.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Locale;

import spencerstudios.com.bungeelib.Bungee;
import spencerstudios.com.fab_toast.FabToast;
import spencerstudios.com.jetdblib.JetDB;
import umairayub.appmanager.Item;
import umairayub.appmanager.R;
import umairayub.appmanager.adapters.ItemAdapter;


public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    private static final int UNINSTALL_REQ_CODE = 123;
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;

    public static boolean is_in_Action = false;
    ArrayList<Item> selection_list;
    TextView selec_counter, tv_title, tv_subtitle;
    RecyclerView recyclerView;
    List<Item> app;
    Dialog mDialog1;
    SwipeRefreshLayout mSwipeRL;
    ItemAdapter itemAdapter;
    private int STORAGE_PERMISSION_CODE = 1;
    Bundle bundle;
    String appname, packagename, filePath = null, newFilePath = null;
    private AdView mAdView;
    Toolbar toolbar;
    int counter;
    ArrayAdapter<String>listAdapter;
    BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeCAB);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSwipeRL = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.ListVIew);
        setSupportActionBar(toolbar);
        selec_counter = (TextView) findViewById(R.id.tv_counter);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_subtitle = (TextView) findViewById(R.id.tv_subtitile);
        selec_counter.setVisibility(View.GONE);
        toolbar.setTitle("AppManager");
        mSwipeRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRL.setRefreshing(false);
                refresh();
            }
        });

        MobileAds.initialize(this, "****************************");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.setVisibility(View.VISIBLE);
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
        mAdView.loadAd(adRequest);

        selection_list = new ArrayList<>();
        clearActionMode();
        //method to get Apps List
        getApps();

        itemAdapter = new ItemAdapter(app);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecorator = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.divider));
        recyclerView.addItemDecoration(itemDecorator);
        // Method  to Sort List Alphabethacally
        //update List
        recyclerView.setAdapter(itemAdapter);

        int count = recyclerView.getAdapter().getItemCount();

        tv_subtitle.setText("Total Apps :  " + count);
        PrepareOnCLick();
        Sorts();

    }


    public void PrepareOnCLick() {

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!is_in_Action) {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    long ftt = app.get(position).getFirstInstallTime();
                    long ltt = app.get(position).getLastUpdateTime();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(ftt);

                    Calendar mCalender = Calendar.getInstance();
                    mCalender.setTimeInMillis(ltt);

                    appname = app.get(position).getName();
                    packagename = app.get(position).getPack();
                    String version = app.get(position).getVersionname();
                    String lastupdate = formatter.format(mCalender.getTime());
                    String firstinstall = formatter.format(calendar.getTime());
                    String Mpermissins = getPermissionsByPackageName(app.get(position).getPack());

                    bundle = new Bundle();
                    bundle.putString("Name", appname);
                    bundle.putString("Package", packagename);
                    bundle.putString("Version", version);
                    bundle.putString("FirstTime", firstinstall);
                    bundle.putString("LastUpdate", lastupdate);
                    if (Mpermissins.equals("")) {
                        bundle.putString("Permissions", "No Permissions Requested or Granted");
                    } else {
                        bundle.putString("Permissions", Mpermissins);
                    }
                    // custom dialog
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom);
                    dialog.setTitle("Options...");

                    TextView textView = (TextView) dialog.findViewById(R.id.tv_appN);
                    ImageView ic = (ImageView) dialog.findViewById(R.id.imgVic);

                    textView.setText(appname);
                    ic.setImageDrawable(app.get(position).getIcon());
                    ListView listView = (ListView) dialog.findViewById(R.id.menulist);
                    ArrayList<String> list = new ArrayList<String>();
                    listAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.menu, R.id.tv_itm, list);

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
                            if (position == 0) {
                                Launch(packagename);
                                dialog.dismiss();

                            }
                            if (position == 1) {
                                Intent intent = new Intent(MainActivity.this, AppInfo.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                dialog.dismiss();

                            }
                            if (position == 2) {
                                requestStoragePermission();
                                FabToast.makeText(MainActivity.this, "Extracting Apk", FabToast.LENGTH_LONG, FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show();
                                filePath = getMyApk(packagename);
                                newFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AppManager/Apks";
                                newFilePath = copyFile(filePath, newFilePath);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        FabToast.makeText(MainActivity.this, " Extracted to " + newFilePath, FabToast.LENGTH_SHORT, FabToast.SUCCESS, FabToast.POSITION_DEFAULT).show();
                                    }
                                }, 1200); // will trigger your code after 1.1 seconds
                            }
                            if (position == 3) {
                                openGP(packagename);

                            }
                            if (position == 4) {
                                share(packagename, appname);
                            }
                        }
                    });
                    dialog.show();

                } else {

                }
            }

            @Override
            public void onDeleteClick(int position) {
                delete(app.get(position).getPack());
            }

            @Override
            public void onInfoClick(int position) {
                appinfo(app.get(position).getPack());
                Bungee.fade(MainActivity.this);

            }

        });
    }

    public void Launch(String pos) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pos);
        startActivity(launchIntent);
    }

    public void Sorts() {
        String sort = JetDB.getString(MainActivity.this, "sort", "sort by name ascending");
        if (sort.equals("sort by name ascending")) {
            Collections.sort(app, new Comparator<Item>() {
                @Override
                public int compare(Item item, Item t1) {
                    return item.getName().compareTo(t1.getName());
                }
            });
        }
        if (sort.equals("sort by name descending")) {
            Collections.sort(app, new Comparator<Item>() {
                @Override
                public int compare(Item item, Item t1) {
                    return t1.getName().compareTo(item.getName());
                }
            });
        }
        if (sort.equals("sort by date ascending")) {
            Collections.sort(app, new Comparator<Item>() {
                @Override
                public int compare(Item item, Item t1) {
                    return Long.valueOf(item.getFirstInstallTime()).compareTo(t1.getFirstInstallTime());
                }
            });
        }
        if (sort.equals("sort by date descending")) {
            Collections.sort(app, new Comparator<Item>() {
                @Override
                public int compare(Item item, Item t1) {
                    return Long.valueOf(t1.getFirstInstallTime()).compareTo(item.getFirstInstallTime());
                }
            });
        }
        if (sort.equals("sort by size ascending")) {
            Collections.sort(app, new Comparator<Item>() {
                @Override
                public int compare(Item item, Item t1) {
                    return Long.valueOf(item.getAppsize()).compareTo(Long.valueOf(t1.getAppsize()));
                }
            });
        }
        if (sort.equals("sort by size descending")) {
            Collections.sort(app, new Comparator<Item>() {
                @Override
                public int compare(Item item, Item t1) {
                    return Long.valueOf(t1.getAppsize()).compareTo(Long.valueOf(item.getAppsize()));
                }
            });
        }
    }

    //launch info Activity of a given Package
    public void appinfo(String packagename) {
        //redirecting user to app Settings
        Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + packagename));
        startActivity(i);
    }

    public static long GetApkSize(String apkPath) {
        File apk = new File(apkPath);
        return apk.length();
    }

    public static String bytesToMb(long bytes) {
        return String.format(Locale.getDefault(), "%.2f MB", ((double) bytes / 1048576));

    }

    //get All Installed Apps
    private void getApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageinfo : packages) {
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
                        pm.getApplicationIcon(packageinfo),
                        GetApkSize(info.applicationInfo.publicSourceDir)));

            }
        }
    }

    //Uninstall App
    private void delete(String packageName) {
        // Initialize a new Intent to uninstall an app/package
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Custom method to Share from package name
    private void share(String packageName, String appName) {
        String link = "App : " + appName + "\n" + "Link : " + "https://play.google.com/store/apps/details?id=" + packageName + "\n\nVia : http://bit.ly/2E9HMZw";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    // Custom method to open Google playstore from package name
    public void openGP(String packageName) {
        Context context = MainActivity.this;
        // you can also use BuildConfig.APPLICATION_ID
        String appId = packageName;
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
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
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            context.startActivity(webIntent);
        }
    }

    public void refresh() {
        app.clear();
        getApps();
        int count = recyclerView.getAdapter().getItemCount();
        tv_subtitle.setText("Total Apps :  " + count);
        clearActionMode();
        Sorts();
        itemAdapter.notifyDataSetChanged();

    }
//////////////////EXTRACT APK METHOD Starts HERE \\\\\\\\\\\\\\\\\\\

    public String getMyApk(String myPackage) {
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
        String outputFilePath = outputPath + "/" + packagename + ".apk";
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
                    permission = permission.substring(permission.lastIndexOf(".") + 1);
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

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                FabToast.makeText(MainActivity.this, "Permission DENIED", Toast.LENGTH_SHORT, FabToast.ERROR, FabToast.POSITION_DEFAULT).show();
            }

        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if (!is_in_Action) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    itemAdapter.filter(query);
                    Sorts();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    itemAdapter.filter(newText);
                    Sorts();
                    return true;
                }
            });
        }
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);

                break;
            case R.id.sort:
                Sort();
                break;
            case R.id.delete:
                FabToast.makeText(MainActivity.this, "Please Wait!", FabToast.LENGTH_SHORT, FabToast.INFORMATION, FabToast.POSITION_DEFAULT).show();
                for (int i = 0; i < selection_list.size(); i++) {
                    uninstallAllAppsFromList(selection_list.get(i).getPack());

                }

                break;
            case R.id.allselect:
                if (selection_list.size() < app.size()) {
                    counter = 0;
                    selection_list.clear();
                    selection_list.addAll(app);
                    for (int i = 0; i < selection_list.size(); i++) {
                        selection_list.get(i).setSelected(true);
                    }
                    itemAdapter.notifyDataSetChanged();
                    counter = counter + selection_list.size();
                    updateCounter(counter);
                } else if (app.size() == selection_list.size()) {
                    for (int ii = 0; ii < selection_list.size(); ii++) {
                        selection_list.get(ii).setSelected(false);
                    }
                    counter = 0;
                    selection_list.clear();
                    counter = counter + selection_list.size();
                    updateCounter(counter);
                    itemAdapter.notifyDataSetChanged();
                }


                break;
            case android.R.id.home:
                clearActionMode();
                itemAdapter.notifyDataSetChanged();
                refresh();
                break;
        }
        return (super.onOptionsItemSelected(item));
    }


    public void Sort() {
        // custom dialog
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(R.layout.sort_dialog);
        final RadioGroup radioGroup = (RadioGroup) bottomSheetDialog.findViewById(R.id.rgroup);

        final RadioButton sortByNameAscending = (RadioButton) bottomSheetDialog.findViewById(R.id.name_ascending);
        final RadioButton sortByNameDescending = (RadioButton) bottomSheetDialog.findViewById(R.id.name_descending);

        final RadioButton sortByDateAscending = (RadioButton) bottomSheetDialog.findViewById(R.id.date_ascending);
        final RadioButton sortByDateDescending = (RadioButton) bottomSheetDialog.findViewById(R.id.date_descending);

        final RadioButton sortBySizeAscending = (RadioButton) bottomSheetDialog.findViewById(R.id.size_ascending);
        final RadioButton sortBySizeDescending = (RadioButton) bottomSheetDialog.findViewById(R.id.size_descending);

        final Button btnapply = (Button) bottomSheetDialog.findViewById(R.id.btnApply);
        String sort = JetDB.getString(MainActivity.this, "sort", "sort by name ascending");
        if (sort.equals("sort by name ascending")) {
            sortByNameAscending.setChecked(true);
        }
        if (sort.equals("sort by size ascending")) {
            sortBySizeAscending.setChecked(true);
        }
        if (sort.equals("sort by size descending")) {
            sortBySizeDescending.setChecked(true);
        }
        if (sort.equals("sort by date ascending")) {
            sortByDateAscending.setChecked(true);
        }

        if (sort.equals("sort by date descending")) {
            sortByDateDescending.setChecked(true);
        }
        btnapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == sortByNameAscending.getId()) {
                    JetDB.putString(MainActivity.this, "sort by name ascending", "sort");
                    Sorts();
                    itemAdapter.notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                }
                if (selectedId == sortByNameDescending.getId()) {
                    JetDB.putString(MainActivity.this, "sort by name descending", "sort");
                    Sorts();
                    itemAdapter.notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                }
                if (selectedId == sortByDateAscending.getId()) {
                    JetDB.putString(MainActivity.this, "sort by date ascending", "sort");
                    Sorts();
                    itemAdapter.notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                }
                if (selectedId == sortByDateDescending.getId()) {
                    JetDB.putString(MainActivity.this, "sort by date descending", "sort");
                    Sorts();
                    itemAdapter.notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                }
                if (selectedId == sortBySizeAscending.getId()) {
                    JetDB.putString(MainActivity.this, "sort by size ascending", "sort");
                    Sorts();
                    itemAdapter.notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                }
                if (selectedId == sortBySizeDescending.getId()) {
                    JetDB.putString(MainActivity.this, "sort by size descending", "sort");
                    Sorts();
                    itemAdapter.notifyDataSetChanged();
                    bottomSheetDialog.dismiss();
                }
            }
        });

        bottomSheetDialog.show();
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

    private void uninstallAllAppsFromList(String packageName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, UNINSTALL_REQ_CODE);
    }

    @Override
    public boolean onLongClick(View view) {
        if (!is_in_Action) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_action_mode);
            tv_title.setVisibility(View.GONE);
            tv_subtitle.setVisibility(View.GONE);
            selec_counter.setVisibility(View.VISIBLE);
            is_in_Action = true;
            itemAdapter.notifyDataSetChanged();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } else {

        }
        return true;

    }

    public void prepareSelection(View view, int pos) {

        if (((CheckBox) view).isChecked()) {
            selection_list.add(app.get(pos));
            updateCounter(selection_list.size());
        } else {
            selection_list.remove(app.get(pos));
            updateCounter(selection_list.size());
        }
    }

    public void updateCounter(int counter) {
        if (counter == 0) {
            int size = selection_list.size();
            selec_counter.setText(size + " App(s) selected");
        } else {
            selec_counter.setText(counter + " App(s) selected");
        }
    }

    public void clearActionMode() {
        is_in_Action = false;
        toolbar.getMenu().clear();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        selec_counter.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);
        tv_subtitle.setVisibility(View.VISIBLE);
        selec_counter.setText("0 App(s) selected");
        counter = 0;
        selection_list.clear();
        onCreateOptionsMenu(toolbar.getMenu());

    }

    @Override
    public void onBackPressed() {
        if (is_in_Action) {
            clearActionMode();
            refresh();
            itemAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

}
