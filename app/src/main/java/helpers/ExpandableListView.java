package helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import autoworks.app.view.MainActivity;
import autoworks.app.view.adapters.NotificationListAdapter;
import autoworks.app.dialogs.TransparentProgressDialog;
import autoworks.app.model.Notification;

/**
 * Created by INNOVATION on 3/4/2015.
 */
public class ExpandableListView extends ListView {


    Dialog progressDialog;
    int numberOfItemEachPage = 10;
    boolean loading = false;
    private ArrayList<Notification> listOfPostData;
    boolean scrollBot = false;
    private Fragment currentFragment;

    public ExpandableListView(Context context) {
        super(context);
    }

    public ExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setCurrentFragment(Fragment currentFragment) {
        this.currentFragment = currentFragment;
    }


    public void setListOfPostData(ArrayList<Notification> listOfPostData) {

        this.listOfPostData = listOfPostData;
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    scrollBot = true;
                } else {
                    scrollBot = false;
                }
            }
        });
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

        if (clampedY) {
            if (!loading) {
                if (scrollBot) {
                    loadOldItem(listOfPostData.size());
                } else {
                      loadMostRecentItem(true);
                }
            }

        }


    }

    private void showTopProgressDialog() {
        if (currentFragment!=null) {
            if (progressDialog == null)
                progressDialog = TransparentProgressDialog.createProgressDialog(currentFragment.getActivity());
            else
                progressDialog.show();
        }

    }
    public void destroyFragment()
    {
        currentFragment = null;
    }

   /* private void showBotProgressDialog() {
        if (progressDialog == null)
            progressDialog = ProgressDialog.show(getContext(), "loading", "");
        else
            progressDialog.show();
    }*/


    //Load all recent item until reach old item (saved item)
    //is like loadOldItem(..,..,true), except it loads until reach a saved item
    //
    public void loadMostRecentItem(boolean showDialog) {
        if (loading)
            return;
        if (showDialog)
            showTopProgressDialog();
        loading = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                final ArrayList<Notification> newGetListOfPostData = new ArrayList<Notification>();
                try {
                    boolean isReachOldItem = false;
                    int page = 0;
                    while (!isReachOldItem) {
                        final ArrayList<Notification> getListOfPostData = new ArrayList<Notification>();


                        final List<NameValuePair> params = new ArrayList<NameValuePair>();
                        if (MainActivity.currentUser.getCustomerID() <= 0)
                            throw new Exception("user not logged in");


                        params.add(new BasicNameValuePair("customer_id", MainActivity.currentUser.getCustomerID()+ ""));
                        params.add(new BasicNameValuePair("notification_index", (page * numberOfItemEachPage) + ""));
                        params.add(new BasicNameValuePair("notification_size", numberOfItemEachPage + ""));
                        params.add(new BasicNameValuePair("tag", "getMostRecentNotifications"));


                        JSONParser jsonParser = new JSONParser();
                        JSONObject json = jsonParser.getJSONFromUrl(MainActivity.getDataURL2, params);


                        //final Object result = client.call(method, numberOfItemEachPage, page * numberOfItemEachPage);


                        if (json != null && !json.isNull(MainActivity.KEY_RESULT)) {

                            JSONArray notifications = json.getJSONArray(MainActivity.KEY_NOTIFICATION);


                            for (int i=0;i<notifications.length();i++){
                                Notification notification = new Notification();
                                JSONObject jsonObject = notifications.getJSONObject(i);
                                String id = jsonObject.getString("customer_notification_id");
                                String title = jsonObject.getString("title");
                                String message = jsonObject.getString("content");
                                boolean isRead = jsonObject.getInt("is_read") == 1 ? true : false;

                                notification.setRead(isRead);
                                notification.setId(id);
                                //postData.setLink(map.get("link").toString());
                                // postData.setDateCreated(map.get("post_date").toString());
                                notification.setTitle(title);
                                notification.setMessage(message);
                                //postData.setAuthor(map.get("post_author").toString());
                                //  postData.setCategories(map.get("post_categories").toString());
                                getListOfPostData.add(notification);
                            }
                           // System.out.println(result.toString());

                            if (listOfPostData.size() == 0) {
                                //empty listView, end after load the first time
                                newGetListOfPostData.addAll(getListOfPostData);
                                break;
                            } else {
                                //non-empty listView, performance looping load until reach old item
                                for (int i = 0; i < getListOfPostData.size(); i++) {
                                    String postId = getListOfPostData.get(i).getId();
                                    int k = listOfPostData.size() < numberOfItemEachPage ? listOfPostData.size() : numberOfItemEachPage;
                                    boolean isOldPost = false;
                                    for (int j = 0; j < k; j++) {
                                        if (listOfPostData.get(j).getId().equals(postId)) {
                                            //this post is old, not new
                                            isOldPost = true;
                                            break;
                                        }
                                    }
                                    if (isOldPost) {
                                        //old post -> duplicate post-> reached the saved item here
                                        //end the loop
                                        isReachOldItem = true;
                                        break;


                                    } else {
                                        //new post
                                        newGetListOfPostData.add(getListOfPostData.get(i));
                                    }
                                }
                            }
                            page++;
                        }
                    }
                    // progressDialog.dismiss();
                    //successfully login
                } catch (Exception e) {
                    //error happened
                    e.printStackTrace();
                }


                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean update = false;
                        while (newGetListOfPostData.size() > 0) {
                            listOfPostData.add(0, newGetListOfPostData.remove(newGetListOfPostData.size() - 1));
                            update = true;
                        }

                        if (update)
                            ((NotificationListAdapter) getAdapter()).notifyDataSetChanged();
                        if (progressDialog!=null)
                            progressDialog.dismiss();
                        loading = false;
                    }
                });


            }

        }).start();

    }


    //load old data(when scroll to bottom)
   public void loadOldItem(final int offset) {
       if (loading)
           return;
        showTopProgressDialog();
        loading = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
               // String method = "frm.getPosts";
                final ArrayList<Notification> getListOfPostData = new ArrayList<Notification>();
                final ArrayList<Notification> newGetListOfPostData = new ArrayList<Notification>();
                try {
                   // final Object result = client.call(method, numberOfItemEachPage, offset);
                   // Object[] objects = (Object[]) result;

                    final List<NameValuePair> params = new ArrayList<NameValuePair>();
                    if (MainActivity.currentUser.getCustomerID() <= 0)
                        throw new Exception("user not logged in");




                        params.add(new BasicNameValuePair("customer_id", MainActivity.currentUser.getCustomerID() + ""));
                        params.add(new BasicNameValuePair("notification_index", offset + ""));
                        params.add(new BasicNameValuePair("notification_size", numberOfItemEachPage + ""));
                        params.add(new BasicNameValuePair("tag", "getMostRecentNotifications"));


                        JSONParser jsonParser = new JSONParser();
                        JSONObject json = jsonParser.getJSONFromUrl(MainActivity.getDataURL2, params);
                    if (json != null && !json.isNull(MainActivity.KEY_RESULT)) {
                        JSONArray notifications = json.getJSONArray(MainActivity.KEY_NOTIFICATION);
                        for (int i = 0; i <notifications.length();i++) {
                            Notification notification = new Notification();
                            JSONObject jsonObject = notifications.getJSONObject(i);
                            String id = jsonObject.getString("customer_notification_id");
                            String title = jsonObject.getString("title");
                            String message = jsonObject.getString("content");
                            boolean isRead = jsonObject.getInt("is_read") == 1 ? true : false;

                            notification.setRead(isRead);
                            notification.setId(id);
                            notification.setTitle(title);
                            notification.setMessage(message);

                            getListOfPostData.add(notification);
                        }
                      //  System.out.println(result.toString());

                        for (int i = 0; i < getListOfPostData.size(); i++) {
                            String postId = getListOfPostData.get(i).getId();
                            int k = listOfPostData.size() < numberOfItemEachPage ? listOfPostData.size() : numberOfItemEachPage;
                            boolean isOldPost = false;
                            int listOfPostDataSize = listOfPostData.size();
                            for (int j = 0; j < k; j++) {
                                if (listOfPostData.get(listOfPostDataSize - j - 1).getId().equals(postId)) {
                                    //this post is old, not new
                                    isOldPost = true;
                                    break;
                                }
                            }
                            if (!isOldPost) {
                                //new post
                                newGetListOfPostData.add(getListOfPostData.get(i));
                            }
                        }
                    }

                } catch (Exception e) {
                    //error happened
                    e.printStackTrace();
                }

                // newGetListOfPostData.addAll(getListOfPostData);


                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (newGetListOfPostData.size() > 0) {
                            listOfPostData.addAll(newGetListOfPostData);
                            ((NotificationListAdapter) getAdapter()).notifyDataSetChanged();
                        }
                        progressDialog.dismiss();
                        loading = false;
                        loadMostRecentItem(false);
                    }
                });


            }

        }).start();
    }


}
