package autoworks.app.model;

/**
 * Created by volyminhnhan on 2/9/15.
 */
public class Zone {
    private String zid = "";
    private String zname = "";

    public Zone() {

    }

    public Zone(String code, String name) {
        this.zid = code;
        this.zname = name;
    }
    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getZname() {
        return zname;
    }

    public void setZname(String zname) {
        this.zname = zname;
    }

    @Override
    public String toString() {
       // return zname + ((zid != "") ? " (" + zid + ")" : "");
        return zname ;
    }

}
