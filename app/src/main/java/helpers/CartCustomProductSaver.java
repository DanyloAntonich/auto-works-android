package helpers;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import autoworks.app.view.Fragment_Product_Detail;
import autoworks.app.view.MainActivity;
import autoworks.app.model.CustomProduct;

/**
 * Created by INNOVATION on 3/10/2015.
 */
@Root
public class CartCustomProductSaver {


    private static CartCustomProductSaver customProductSaver;
    @ElementList
    protected ArrayList<ProductIdWithQuantity> listOfCartProductIds = new ArrayList<>();

    protected ArrayList<CustomProduct> listOfCartProducts = new ArrayList<CustomProduct>();

    protected IEventHandler eventHandler;

    protected Context context;
    protected Persister persister;
    protected String fileName = "";


    protected CartCustomProductSaver(Context context) {
        this.context = context;
        persister = new Persister();
        fileName = "CartListSave3.xml";
    }

    protected CartCustomProductSaver() {
        this(null);
    }


    public void setEventHandler(IEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public IEventHandler getEventHandler() {
        return eventHandler;
    }

    public void removeCustomProduct(int id) {
        for (ProductIdWithQuantity product : new ArrayList<>(listOfCartProductIds)) {
            if (product.getId() == id) {
                listOfCartProductIds.remove(product);
                for (CustomProduct customProduct1 : new ArrayList<>(listOfCartProducts)) {
                    if (customProduct1.getId() == id) {
                        listOfCartProducts.remove(customProduct1);

                        setCartIndicator();

                        break;
                    }
                }
                break;
            }
        }
        if (eventHandler != null)
            eventHandler.onListChanged();
    }

    public void changeQuantity(int productId, int newQuantity) {
        for (ProductIdWithQuantity product : new ArrayList<>(listOfCartProductIds)) {
            if (product.getId() == productId) {
                product.setQuantity(newQuantity);
                break;
            }

        }
    }

    //add a new custom product, return quantity
    public int addCustomProduct(boolean isCart, CustomProduct customProduct) {
        customProduct = customProduct.clone();
        boolean duplicate = false;
        int quantity = 1;
        for (ProductIdWithQuantity product : new ArrayList<>(listOfCartProductIds)) {
            if (product.getId() == customProduct.getId()) {
                //duplicate item, increase quantity
                product.setQuantity(product.getQuantity() + 1);
                quantity = product.getQuantity();
                for (CustomProduct customProduct1 : new ArrayList<>(listOfCartProducts)) {
                    if (customProduct1.getId() == product.getId()) {
                        customProduct1.setQuantity(quantity);
                        customProduct.setQuantity(quantity);
                        break;
                    }
                }
                duplicate = true;
                break;
            }
        }

        if (!duplicate) {
            //new item
            listOfCartProductIds.add(new ProductIdWithQuantity(customProduct.getId(), 1));
            customProduct.setQuantity(1);
            listOfCartProducts.add(customProduct);
        }

        if(isCart) {
            setCartIndicator();
        }

        if (eventHandler != null)
            eventHandler.onListChanged();

        return quantity;
    }

    //reload the real list of products in cart
    public void loadListOfCartProducts(final RelativeLayout progressDialog) {
        ArrayList<CustomProduct> list = new ArrayList<>();

        progressDialog.setVisibility(View.VISIBLE);


        //do http post to get product list base on listOfCartProductIds
        for (ProductIdWithQuantity productIdWithQuantity : listOfCartProductIds) {
           // System.out.println(productIdWithQuantity.getId());

            CustomProduct customProduct = new CustomProduct();
            customProduct.setId(productIdWithQuantity.getId());
            customProduct.setQuantity(productIdWithQuantity.getQuantity());
            Fragment_Product_Detail.loadProduct( productIdWithQuantity.getId(), true, customProduct, this.context);
            customProduct.addIProductLoadingHandler(
                    new CustomProduct.IProductLoadingHandler() {
                        @Override
                        public void onLoadProductCompleted() {
                            if (eventHandler != null)
                                eventHandler.onListChanged();
                        }
                    });
            list.add(customProduct);
        }

        progressDialog.setVisibility(View.GONE);

        this.listOfCartProducts.clear();
      /* for (CustomProduct customProduct : list)
       {
           listOfCartProductIds.add(new ProductIdWithQuantity(customProduct.getId(), customProduct.getQuantity()));
       }*/
        this.listOfCartProducts.addAll(list);

        if (eventHandler != null)
            eventHandler.onListChanged();
    }

    public ArrayList<CustomProduct> getListOfCartProducts() {
        return listOfCartProducts;
    }


    public ArrayList<ProductIdWithQuantity> getListOfCartProductIds() {
        return listOfCartProductIds;
    }


    public void dataToXml() {
        // Log.d(PostsActivity.TAG, "saving postDataSource");

        // OutputStream outputStream = new FileOutputStream(fileName)
        try {
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            persister.write(this, outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

   public void clearCart() {
       this.listOfCartProductIds.clear();
       this.listOfCartProducts.clear();
   }

   public void setCartIndicator() {
        if(MainActivity.notification != null) {
            if(listOfCartProductIds.size() > 0) {
                //set cart number indicator
                MainActivity.notification.setTitle(String.valueOf(listOfCartProductIds.size()));
                MainActivity.notification.setVisible(true);
            }
            else {
                MainActivity.notification.setVisible(false);
            }
        }
    }

    public void xmlToData(Context context) {
        //  Log.d(PostsActivity.TAG, "loading postDataSource");
        try {
            InputStream inputStream = context.openFileInput(fileName);
            customProductSaver = persister.read(CartCustomProductSaver.class, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (customProductSaver == null)
            customProductSaver = new CartCustomProductSaver(context);

        customProductSaver.context = context;
    }


    public static CartCustomProductSaver getInstance(Context context) {
        if (customProductSaver == null) {
            customProductSaver = new CartCustomProductSaver(context);
            customProductSaver.xmlToData(context);
        }
        return customProductSaver;
    }

    public static interface IEventHandler {
        void onListChanged();

        void onTotalPriceChanged();
    }

    public static class ProductIdWithQuantity {
        private int id;
        private int quantity;

        public ProductIdWithQuantity() {
        }

        ;

        public ProductIdWithQuantity(int id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
