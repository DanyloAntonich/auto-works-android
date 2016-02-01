package autoworks.app.model;

/**
 * Created by volyminhnhan on 2/9/15.
 */
public class Country {
    private String ccode = "";
    private String cname = "";

    public Country() {

    }

    public Country(String code, String name) {
        this.ccode = code;
        this.cname = name;
    }
    public String getCcode() {
        return ccode;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public String toString() {
        return cname + ((ccode != "") ? " (" + ccode + ")" : "");
    }

}
