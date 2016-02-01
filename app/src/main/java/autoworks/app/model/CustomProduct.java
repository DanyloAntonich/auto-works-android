package autoworks.app.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

import helpers.CustomProductCacher;
import helpers.ImageDownloader2;

/**
 * This is just a simple class for holding data that is used to render our custom view
 */
public class CustomProduct extends ImageDownloader2.ItemBitmap {
    private int id;
    private int quantity;

    private double totalPrice;
  //  private String productImage;
    private ArrayList<String> productImages = new ArrayList<>();
    private String productName;
    private String productPrice ="";
    private String productSpecialPrice="";
    private String salePercentage;
    private String productShortDescription;
    private String productDescription;
    private ArrayList<CustomProduct> relatedProducts = new ArrayList<>();
    private boolean isLoaded = false;
    private boolean isLoading = false;
    private boolean isEdittextFocusing;

    private int MAX_LENGTH_TITLE = 40;

    private ArrayList<IProductLoadingHandler> listIProductLoadingHandler = new ArrayList<>();

    public CustomProduct() {

    }

    public CustomProduct(int id, String imageURL, String productName, String productPrice, String productSpecialPrice, String salePercentage) {
        this.id = id;
        this.imageUrl = imageURL;
        //this.productImage = imageURL;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productSpecialPrice = productSpecialPrice;
        this.salePercentage = salePercentage;
    }

    public void setLoadedBitmap(Bitmap loadedBitmap)
    {
        super.setLoadedBitmap(loadedBitmap);
        CustomProductCacher.updateCustomProductBitmapCache(this);
    }

    public void setLoadedBitmap2(Bitmap loadedBitmap)
    {
        super.setLoadedBitmap(loadedBitmap);
    }

    public void setProductImage(String url)
    {
        this.setImageUrl(url);
    }
    public String getProductImage()
    {
        return this.getImageUrl();
    }

    public void addIProductLoadingHandler(IProductLoadingHandler iProductLoadingHandler) {
        this.listIProductLoadingHandler.add(iProductLoadingHandler);
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }

    public boolean isEdittextFocusing() {
        return isEdittextFocusing;
    }

    public void setEdittextFocusing(boolean isEdittextFocusing) {
        this.isEdittextFocusing = isEdittextFocusing;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        this.isLoaded = loaded;
        for (IProductLoadingHandler handler : listIProductLoadingHandler) {
            handler.onLoadProductCompleted();
        }

        listIProductLoadingHandler.clear();
    }

    public void setProductImages(ArrayList<String> productImages) {
        this.productImages = productImages;
    }

    public String getProductShortDescription() {
        return productShortDescription;
    }

    public void setProductShortDescription(String productShortDescription) {
        this.productShortDescription = productShortDescription;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public ArrayList<CustomProduct> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(ArrayList<CustomProduct> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        productName = ((productName.length() > MAX_LENGTH_TITLE) ? productName.substring(0,MAX_LENGTH_TITLE) + "..." : productName);
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    private String getNumericValue(String input) {
        StringBuilder myNumbers = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.') {
                myNumbers.append(input.charAt(i));
                //System.out.println(input.charAt(i) + " is a digit.");
            } else {
                //System.out.println(input.charAt(i) + " not a digit.");
            }
        }

        if(myNumbers.toString().isEmpty()) {
            return "0";
        }

        return myNumbers.toString();
    }

    public void setProductPrice(String productPrice) {
        //System.out.println(productPrice);

        Double price = (double)0;
        if(!productPrice.isEmpty() && productPrice != "null") {
            price =  (double)Math.round(Double.valueOf(getNumericValue(productPrice)));
        }

        this.productPrice = price.toString();

        //System.out.println(this.productName + ": " + this.productPrice);
    }

    public String getProductSpecialPrice() {
        return productSpecialPrice;
    }

    public void setProductSpecialPrice(String productSpecialPrice) {
        //System.out.println(productSpecialPrice);

        Double price = (double)0;
        if(!productSpecialPrice.isEmpty() && productSpecialPrice != "null") {
            price =  (double)Math.round(Double.valueOf(getNumericValue(productSpecialPrice)));
        }

        this.productSpecialPrice = price.toString();

        //System.out.println(this.productName + ": " + this.productSpecialPrice);
    }

    public String getSalePercentage() {
        return salePercentage;
    }

    public void setSalePercentage(String salePercentage) {
        this.salePercentage = salePercentage;
    }

    /**
     * @return the backgroundColor
     */
  /*  public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String imgURL) {
        this.productImage = imgURL;
    }*/

    /**
     * @return the text
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public static interface IProductLoadingHandler {
        void onLoadProductCompleted();
    }


    public void copy(CustomProduct product)
    {
        this.setId(product.getId());
       //this.setQuantity(product.getQuantity());
      //  this.setLoaded(product.isLoaded());
       this.isLoaded = product.isLoaded;
        this.setProductDescription(product.getProductDescription());
        this.setProductShortDescription(product.getProductShortDescription());
        this.setImageUrl(product.getImageUrl());
        this.setLoadedBitmap(product.getLoadedBitmap());
        this.setProductPrice(product.getProductPrice());
        this.setProductName(product.getProductName());
        this.setProductSpecialPrice(product.getProductSpecialPrice());
        this.setRelatedProducts(product.getRelatedProducts());
        this.setSalePercentage(product.getSalePercentage());
        this.setTotalPrice(product.getTotalPrice());
        this.setProductImages(product.getProductImages());
    }
    public CustomProduct clone() {
        CustomProduct newCustomProduct = new CustomProduct();

        newCustomProduct.copy(this);
       /* newCustomProduct.setId(this.getId());
        newCustomProduct.setQuantity(this.getQuantity());
        newCustomProduct.setLoaded(this.isLoaded());
        newCustomProduct.setProductDescription(this.getProductDescription());
        newCustomProduct.setProductShortDescription(this.getProductShortDescription());
        newCustomProduct.setImageUrl(this.getImageUrl());
        newCustomProduct.setLoadedBitmap(this.getLoadedBitmap());
        newCustomProduct.setProductPrice(this.getProductPrice());
        newCustomProduct.setProductName(this.getProductName());
        newCustomProduct.setProductSpecialPrice(this.getProductSpecialPrice());
        newCustomProduct.setRelatedProducts(this.getRelatedProducts());
        newCustomProduct.setSalePercentage(this.getSalePercentage());
        newCustomProduct.setTotalPrice(this.getTotalPrice());
        newCustomProduct.setProductImages(this.getProductImages());*/
        return newCustomProduct;
    }
}
