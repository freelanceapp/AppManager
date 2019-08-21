
package umairayub.appmanager;


import android.graphics.drawable.Drawable;

public class Item {


    private String name,pack,versionname;
    private long lastUpdateTime,firstInstallTime;
    private Drawable icon;
    private long appsize;
    private boolean isSelected;

    public boolean isSelected(){
        return  isSelected;
    }
    public void setSelected(boolean selected){
        isSelected = selected;
    }


    public  Item (String name, String pack, String versionname, long firstInstallTime, long lastUpdateTime, Drawable icon, long appsize){
        this.name = name;
        this.pack = pack;
        this.versionname = versionname;
        this.firstInstallTime = firstInstallTime;
        this.lastUpdateTime = lastUpdateTime;
        this.icon = icon;
        this.appsize = appsize;
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

    public long getAppsize() {
        return appsize;
    }
}
