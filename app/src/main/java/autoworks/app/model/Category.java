package autoworks.app.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by volyminhnhan on 12/02/2015.
 */
public class Category {
    public Category() {
        childCategories = new ArrayList<Category>();
        mProductFinalImage = null;
    }

    public Integer getmCategoryID() {
        return mCategoryID;
    }

    public void setmCategoryID(Integer mCategoryID) {
        this.mCategoryID = mCategoryID;
    }

    public String getmCategoryName() {
        return mCategoryName;
    }

    public void setmCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
    }

    public int getmCategoryLevel() {
        return mCategoryLevel;
    }

    public void setmCategoryLevel(int mCategoryLevel) {
        this.mCategoryLevel = mCategoryLevel;
    }

    public int getmCategoryParentID() {
        return mCategoryParentID;
    }

    public void setmCategoryParentID(int mCategoryParentID) {
        this.mCategoryParentID = mCategoryParentID;
    }

    public ArrayList<Category> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(ArrayList<Category> childCategories) {
        this.childCategories = childCategories;
    }

    public String getmCategoryDescription() {
        return mCategoryDescription;
    }

    public void setmCategoryDescription(String mCategoryDescription) {
        this.mCategoryDescription = mCategoryDescription;
    }
    public String getmCategoryImageURL() {
        return mCategoryImageURL;
    }

    public void setmCategoryImageURL(String mCategoryImage) {
        this.mCategoryImageURL = mCategoryImage;
    }

    public Bitmap getmProductFinalImage() {
        return mProductFinalImage;
    }

    public void setmProductFinalImage(Bitmap mProductFinalImage) {
        this.mProductFinalImage = mProductFinalImage;
    }

    private Bitmap mProductFinalImage;
    private Integer mCategoryID;
    private String mCategoryName;
    private String mCategoryDescription;
    private String mCategoryImageURL = "";
    private int mCategoryLevel;
    private int mCategoryParentID;
    private ArrayList<Category> childCategories;
}
