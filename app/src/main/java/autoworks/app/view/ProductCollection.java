package autoworks.app.view;

import java.util.ArrayList;

import autoworks.app.model.Customer;
import autoworks.app.model.Product;

/**
 * Created by Tan on 9/30/2014.
 */
public class ProductCollection {
    public ProductCollection(){
        productCollectionName = "";
        productCollectionDescription = "";
        productCollectionView = 0;
        
    }

    private int productCollectionID;

    private String productCollectionName;
    private String productCollectionDescription;

    private int productCollectionOwnerID;

    private int productCollectionView;

    private ArrayList<Integer> productCollectionList;

    public int getProductCollectionID() {
        return productCollectionID;
    }

    public void setProductCollectionID(int productCollectionID) {
        this.productCollectionID = productCollectionID;
    }

    public String getProductCollectionName() {
        return productCollectionName;
    }

    public void setProductCollectionName(String productCollectionName) {
        this.productCollectionName = productCollectionName;
    }

    public String getProductCollectionDescription() {
        return productCollectionDescription;
    }

    public void setProductCollectionDescription(String productCollectionDescription) {
        this.productCollectionDescription = productCollectionDescription;
    }

    public Customer getProductCollectionOwner() {
        if(MainActivity.userList!=null)
        {
            for(int i=0; i<MainActivity.userList.size(); i++)
            {
                if(MainActivity.userList.get(i).getCustomerID() == this.productCollectionOwnerID)
                    return MainActivity.userList.get(i);
            }
        }
        return null;
    }

    public int getProductCollectionOwnerID() {
        return productCollectionOwnerID;
    }

    public void setProductCollectionOwnerID(int productCollectionOwnerID) {
        this.productCollectionOwnerID = productCollectionOwnerID;
    }

    public int getProductCollectionView() {
        return productCollectionView;
    }

    public void setProductCollectionView(int productCollectionView) {
        this.productCollectionView = productCollectionView;
    }

    public ArrayList<Product> getProductList() {
        ArrayList<Product> products = new ArrayList<Product>();
        if(MainActivity.productList != null) {
            for (int i = 0; i < productCollectionList.size(); i++) {
                {
                    for (int j = 0; j < MainActivity.productList.size(); j++)
                        if (productCollectionList.get(i) == MainActivity.productList.get(j).getProductID()) {
                            products.add(MainActivity.productList.get(j));
                            break;
                        }
                }
            }

        }
        return products;
    }

    public void setProductList(ArrayList<Integer> productCollectionList) {
        this.productCollectionList = productCollectionList;
    }

}
