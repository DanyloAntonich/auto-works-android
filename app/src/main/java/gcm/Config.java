package gcm;

import autoworks.app.view.MainActivity;

public interface Config {

	// used to share GCM regId with application server - using php app server
	//static final String APP_SERVER_URL = "http://192.168.1.17/gcm/gcm.php?shareRegId=1";

	static final String SERVER_URL = MainActivity.ROOT_API_URL;
	static final String APP_SERVER_URL = SERVER_URL + "&regId=REG_ID";
	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "918450834075";
    static final String ID_KEY = "id";
	static final String MESSAGE_KEY = "message";
    static final String IS_READ_KEY = "is_read";
    static final String TITLE_KEY = "title";
	static final String XML_RPC_URL = SERVER_URL + "/xmlrpc.php";

}
