package autoworks.app.model;

/** This is just a simple class for holding data that is used to render our custom view */
public class CustomData {
    private int id;
    private String mURL;

    public CustomData() {

    }

    public CustomData(int id, String imgURL) {
        setId(id);
        setImgURL(imgURL);
    }

    /**
     * @return the backgroundColor
     */
    public String getImgURL() {
        return mURL;
    }

    public void setImgURL(String imgURL) {
        this.mURL = imgURL;
    }

    /**
     * @return the text
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
