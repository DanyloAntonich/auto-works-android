package autoworks.app.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import autoworks.app.R;
import autoworks.app.model.Notification;


public class Fragment_Notification_Detail extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView titleTextView;
    TextView messageTextView;
    public static Notification notification;
    public Fragment_Notification_Detail()
    {

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


    private View myview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("AutoWorks", "oCreateView fragment_register");
        myview = inflater.inflate(R.layout.fragment_notification_detail, container,false);

        titleTextView = (TextView)myview.findViewById(R.id.item_tittle);
        messageTextView = (TextView)myview.findViewById(R.id.item_message);

        notification.setRead(true);
        titleTextView.setText(notification.getTitle());
        messageTextView.setText(notification.getMessage());
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
