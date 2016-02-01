package autoworks.app.model;

import autoworks.app.view.MainActivity;

/**
 * Created by Tan on 9/30/2014.
 */
public class Order {
    public Order(){
        orderID=0;
        productCount=0;
        status = 0;
    }

    public Order(int orderID, int productID, int productCount, int status, int price){
        this.orderID=orderID;
        this.productCount=productCount;
        this.status = status;
        this.productID=productID;
    }

    private int orderID;

    private int productID;
    private int buyerID;
    private int status;
    private int productCount;

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Product getProduct() {
        for(int i=0; i< MainActivity.productList.size(); i++)
            if(MainActivity.productList.get(i).getProductID()==productID)
                return MainActivity.productList.get(i);
        return null;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public Customer getBuyer() {
        for(int i=0; i<MainActivity.userList.size(); i++)
            if(MainActivity.userList.get(i).getCustomerID()==productID)
                return MainActivity.userList.get(i);
        return null;
    }

    public int getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(int buyerID) {
        this.buyerID = buyerID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return productCount;
    }

    public void setCount(int count) {
        this.productCount = count;
    }

}
