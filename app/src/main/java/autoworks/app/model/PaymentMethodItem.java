package autoworks.app.model;

/**
 * Created by volyminhnhan on 24/03/2015.
 */
public class PaymentMethodItem {
    private String mTitle ="";
    private String mCode ="";

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public PaymentMethodItem(String title, String code) {
        mTitle = title;
        mCode = code;
    }

}
