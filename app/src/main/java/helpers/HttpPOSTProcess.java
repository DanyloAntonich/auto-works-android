package helpers;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by volyminhnhan on 09/02/2015.
 */
public class HttpPOSTProcess implements HttpPOSTOnTaskCompleted{
    private String mURL = "";
    private String mAction = "";
    private JSONParser jsonParser;
    private HttpPOSTOnTaskCompleted mCallBackFunc ;
    private List<NameValuePair> mParams = new ArrayList<NameValuePair>();

    public HttpPOSTProcess(String URL, String action, List<NameValuePair> params, HttpPOSTOnTaskCompleted httpPOSTOnTaskCompleted){
        this.mURL = URL;
        this.mAction = action;
        params.add(new BasicNameValuePair("tag", action));
        this.mParams = params;
        this.mCallBackFunc = httpPOSTOnTaskCompleted;

        //execute request
        new mProcessHttpPOST(mCallBackFunc).execute();
    }

    @Override
    public void onHttpPOSTOnTaskCompleted(JSONObject json)
    {
        //nothing here
    }

    private class mProcessHttpPOST extends AsyncTask<String, Void, JSONObject> {
        HttpGet uri = null;
        StatusLine status = null;
        HttpResponse resp = null;
        DefaultHttpClient client = null;

        private HttpPOSTOnTaskCompleted listener;

        public mProcessHttpPOST(HttpPOSTOnTaskCompleted listener){
            this.listener=listener;
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            JSONObject json = null;
            try {
                // getting JSON Object
                jsonParser = new JSONParser();
                json = jsonParser.getJSONFromUrl(mURL, mParams);
            }
            catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return json;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            listener.onHttpPOSTOnTaskCompleted(result);
        }

    }
}
