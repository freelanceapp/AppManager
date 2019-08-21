
package umairayub.appmanager;


import android.graphics.drawable.Drawable;

public class Item {


    private String name,pack,versionname;
    private long lastUpdateTime,firstInstallTime;
    private Drawable icon;




    public  Item (String name, String pack, String versionname, long firstInstallTime, long lastUpdateTime, Drawable icon){
        this.name = name;
        this.pack = pack;
        this.versionname = versionname;
        this.firstInstallTime = firstInstallTime;
        this.lastUpdateTime = lastUpdateTime;
        this.icon = icon;

    }
    public String getName() {
        return name;
    }
    public String getPack() {
        return pack;
    }
    public String getVersionname() {
        return versionname;
    }
    public long getFirstInstallTime() {
        return firstInstallTime;
    }
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    public Drawable getIcon() {
        return icon;
    }
}
