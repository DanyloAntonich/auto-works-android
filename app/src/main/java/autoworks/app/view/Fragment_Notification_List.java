package autoworks.app.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import autoworks.app.R;
import autoworks.app.view.adapters.NotificationListAdapter;
import autoworks.app.model.Notification;
import autoworks.app.model.NotificationDataSource;
import gcm.GCMNotificationIntentService;
import helpers.ExpandableListView;


public class Fragment_Notification_List extends Fragment implements View.OnClickListener{

    private static ArrayList<Notification> mNotifications = new ArrayList<Notification>();
    private NotificationListAdapter adapter;
    private ExpandableListView notificationListView;

    public Fragment_Notification_List() {


    }
    public void addNotification(Notification notification)
    {

        //edit this-> let Expandable list handler
        mNotifications.add(notification);
        if (adapter!=null)
            adapter.notifyDataSetChanged();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
    @Override
    public void onStop()
    {
        super.onStop();
        NotificationDataSource.getInstance(this.getActivity()).dataToXml();
        notificationListView.destroyFragment();
        //this.notificationListView
    }

    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment__notification__list, container,false);
        notificationListView = (ExpandableListView) myview.findViewById(R.id.notification_list);
        notificationListView.setCurrentFragment(this);
        mNotifications = NotificationDataSource.getInstance(this.getActivity()).getListOfPostData();
        adapter = new NotificationListAdapter(this.getActivity(), R.layout.notification_item, mNotifications);
        notificationListView.setListOfPostData(mNotifications);
        notificationListView.setAdapter(adapter);
        notificationListView.loadMostRecentItem(true);

        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment_Notification_Detail.notification = mNotifications.get(position);
                Fragment_Notification_Detail fragment_notification_detail = new Fragment_Notification_Detail();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment_notification_detail)
                        .addToBackStack("fragment_notification_detail")
                        .commit();
            }
        });

        //This is how IntentService communicate with Activity
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String id = intent.getStringExtra(GCMNotificationIntentService.KEY_ID);
                String title = intent.getStringExtra(GCMNotificationIntentService.KEY_TITLE);
                String message = intent.getStringExtra(GCMNotificationIntentService.KEY_MESSAGE);
                // ... do something ...
                //don't care about the content of message
                //just update post list
                notificationListView.loadMostRecentItem(true);

            }
        };
        LocalBroadcastManager.getInstance(this.getActivity())
                .registerReceiver(receiver, new IntentFilter("myBroadcastIntent"));
        return myview;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

    }


}
