package autoworks.app.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

import autoworks.app.view.MainActivity;
import autoworks.app.view.ProductCollection;

/**
 * Created by Tan on 9/22/2014.
 */
@Root
public class Customer {

    @Element
    private Integer customerID;
    @Element(required = false)
    private String email;
    @Element(required = false)
    private String profile_picture;
    @Element(required = false)
    private String firstname;
    @Element(required = false)
    private String lastname;
    @Element(required = false)
    private String address;
    @Element
    private Integer gender;
    @Element(required = false)
    private String city;
    @Element(required = false)
    private String countryCode;
    @Element(required = false)
    private String phone;

    @Element(required = false)
    private Integer languageId;


    @Element
    private int customerGroupId;
    @ElementList
    private ArrayList<Integer> favorite;

    public Integer getCustomerGroupID() {
        return customerGroupId;
    }

    public void setCustomerGroupID(int customer_group_id) {
        this.customerGroupId = customer_group_id;
    }



    public Customer(){
        customerID = 0;
        languageId = 1;
        gender = 0;
        email = "";
        countryCode ="";
        profile_picture = "https://fbcdn-sphotos-f-a.akamaihd.net/hphotos-ak-xfp1/t1.0-9/1511293_627529190645666_1890301354_n.jpg";
        firstname = "";
        lastname = "";
        //address = "";
        city = "";
        phone = "";
        customerGroupId = 1;
        favorite = new ArrayList<Integer>();
    }


    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getFullname() {
        return lastname + " " + firstname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /* public void setFullname(String firstname, String lastname) {
        this.firstname = firstname; this.lastname = lastname;
    }*/

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public ArrayList<Product> getProductList()
    {
        ArrayList<Product> productList = new ArrayList<Product>();
        if(MainActivity.productList != null)
        {
            for(int i=0; i<MainActivity.productList.size(); i++)
                if(MainActivity.productList.get(i).getProductOwnerID() == customerID)
                    productList.add(MainActivity.productList.get(i));
        }
        return productList;
    }

    public ArrayList<ProductCollection> getCollectionList()
    {
        ArrayList<ProductCollection> collectionList = new ArrayList<ProductCollection>();
        if(MainActivity.productList != null)
        {
            for(int i=0; i<MainActivity.collectionList.size(); i++)
                if(MainActivity.collectionList.get(i).getProductCollectionOwnerID() == customerID)
                    collectionList.add(MainActivity.collectionList.get(i));
        }
        return collectionList;
    }

    public ArrayList<Product> getFavoriteProductList() {
        ArrayList<Product> products = new ArrayList<Product>();
        if(MainActivity.productList != null) {
            for (int i = 0; i < favorite.size(); i++) {
                {
                    for (int j = 0; j < MainActivity.productList.size(); j++)
                        if (favorite.get(i) == MainActivity.productList.get(j).getProductID()) {
                            products.add(MainActivity.productList.get(j));
                            break;
                        }
                }
            }

        }
        return products;
    }

    public void setFavoriteProductList(ArrayList<Integer> favoriteProductCollectionList) {
        this.favorite = favoriteProductCollectionList;
    }

    public void removeFavorite(int productID) {
        for(int i=0; i<favorite.size(); i++)
            if(favorite.get(i) == productID)
            {
                favorite.remove(i);
                return;
            }
    }
}
