package autoworks.app.model;

import autoworks.app.view.MainActivity;

/**
 * Created by Tan on 9/30/2014.
 */
public class Product {
    public Product(){
        productName = "";
        productDescription = "";
        productView = 0;
        productComment = 0;
        productFavorite = 0;
        productPicture = "";
        productExtraPicture = new String[]{""};
        productPrice = 0;
        productOwnerID = -1;
        productCount = 0;
    }

    private int productID;

    private String productName;
    private String productDescription;
    private String productPicture;
    private String[] productExtraPicture;
    private double productPrice;
    private int productCount;

    private int productOwnerID;

    private int productView;
    private int productComment;
    private int productFavorite;

    //define max title length
    private int MAX_LENGTH_TITLE = 40;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        productName = ((productName.length() > MAX_LENGTH_TITLE) ? productName.substring(0,MAX_LENGTH_TITLE) + "..." : productName);
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPicture() {
        return productPicture;
    }

    public void setProductPicture(String productPicture) {
        this.productPicture = productPicture;
    }

    public String[] getProductExtraPicture() {
        return productExtraPicture;
    }

    public void setProductExtraPicture(String[] productExtraPicture) {
        this.productExtraPicture = productExtraPicture;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getProductCount() {
        return productCount;
    }

    public void setProductCount(int productPrice) {
        this.productCount = productCount;
    }

    public Customer getProductOwner() {
        if(MainActivity.userList!=null)
        {
            for(int i=0; i<MainActivity.userList.size(); i++)
            {
                if(MainActivity.userList.get(i).getCustomerID() == this.productOwnerID)
                    return MainActivity.userList.get(i);
            }
        }
        return null;
    }

    public int getProductOwnerID() {
        return productOwnerID;
    }

    public void setProductOwnerID(int productOwnerID) {
        this.productOwnerID = productOwnerID;
    }

    public int getProductView() { return productView;    }

    public void setProductView(int productView) {
        this.productView = productView;
    }

    public int getProductComment() {
        return productComment;
    }

    public void setProductComment(int productComment) {
        this.productComment = productComment;
    }

    public int getProductFavorite() {
        return productFavorite;
    }

    public void setProductFavorite(int productFavorite) {
        this.productFavorite = productFavorite;
    }
}
