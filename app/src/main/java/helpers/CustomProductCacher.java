package helpers;

import java.util.HashMap;
import java.util.Map;

import autoworks.app.model.CustomProduct;

/**
 * Created by INNOVATION on 3/15/2015.
 */
public class CustomProductCacher {

    static Map<Integer, CustomProduct> mapCustomProducts = new HashMap<Integer, CustomProduct>();


    public static void addCustomProductCache(CustomProduct customProduct)
    {
        mapCustomProducts.put(customProduct.getId(), customProduct);
    }

    public static void updateCustomProductBitmapCache(CustomProduct product)
    {
        if (mapCustomProducts.containsKey(product.getId()))
            mapCustomProducts.get(product.getId()).setLoadedBitmap2(product.getLoadedBitmap());
    }

    public static CustomProduct getCustomProduct(int id)
    {
        if (mapCustomProducts.containsKey(id))
            return mapCustomProducts.get(id).clone();
        return null;
    }

    public static CustomProduct getRawCustomProduct(int id)//without clone
    {
        if (mapCustomProducts.containsKey(id))
            return mapCustomProducts.get(id);
        return null;
    }
}
