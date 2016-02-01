package autoworks.app.model;

import android.content.Context;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by INNOVATION on 3/9/2015.
 */
@Root
public class NotificationDataSource {

    @ElementList
    private ArrayList<Notification> listOfPostData = new ArrayList<Notification>();

    private Context context;
    private Persister persister;
    private final String fileName = "NotificationDataSource4.xml";
    private final int MAX_ITEMS = 50;//if listOfPostData contains more than this, perform a cut-off operation
    private static NotificationDataSource notificationDataSource;

    private NotificationDataSource(Context context) {
        this.context = context;
        persister = new Persister();
    }

    private NotificationDataSource() {
        persister = new Persister();
    }

    public ArrayList<Notification> getListOfPostData() {
        return listOfPostData;
    }

    /**
     * Convert this instance to xml
     */

    public void clear()
    {
        this.listOfPostData.clear();
    }
    public void dataToXml() {
        //Log.d(TAG, "saving notificationDataSource");



        //cut-off before saving
        while (listOfPostData.size() > MAX_ITEMS) {
            listOfPostData.remove(listOfPostData.size() - 1);
        }

        // OutputStream outputStream = new FileOutputStream(fileName)
        try {
            OutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            persister.write(this, outputStream);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void xmlToData(Context context) {
       // Log.d(PostsActivity.TAG, "loading notificationDataSource");
        try {
            InputStream inputStream = context.openFileInput(fileName);
            notificationDataSource = persister.read(NotificationDataSource.class, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (notificationDataSource == null)
            notificationDataSource = new NotificationDataSource(context);//some error happen, create a new notificationDataSource

        notificationDataSource.context = context;
    }

    public static NotificationDataSource getInstance(Context context) {
        if (notificationDataSource == null) {
            notificationDataSource = new NotificationDataSource(context);
            notificationDataSource.xmlToData(context);
        }
        return notificationDataSource;
    }
}
