package autoworks.app.view;

/**
 * Created by Tan on 8/29/2014.
 */
public interface Communicator{

    public void checkLogin();
    public void GetProductDetail(int product_id);
    public void GetCollectionDetail(int product_id);
    public void GetCollectionList(int user_id);
    public void OrderProduct(int product_id);
    public void AddProduct();
    public void SaveProduct();
    public void OrderProductConfirm(int product_id);
    public void CommentProduct(int product_id);
    public void ViewCartDetail(int order_id);
    public void ViewOrderDetail(int order_id);

    public void GetMyCollectionDetail(int productCollectionID);

    public void LogOut();

    public void EditProduct(int productID);

    public void MyCartConfirm();

    public void SkipMyCartConfirm();

    public void OpenConfirmMyCart();
}
