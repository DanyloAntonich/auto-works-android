package autoworks.app.model;

import java.util.ArrayList;

/**
 * Created by volyminhnhan on 3/5/15.
 */
public class OrderItem {
    public ArrayList<CustomProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(ArrayList<CustomProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }


    private Integer orderID;
    private String date;
    private String status;
    private Double total;
    private ArrayList<CustomProduct> orderProducts;
}
