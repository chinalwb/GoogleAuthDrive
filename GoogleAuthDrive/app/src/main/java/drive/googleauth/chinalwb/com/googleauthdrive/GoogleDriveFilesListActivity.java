package drive.googleauth.chinalwb.com.googleauthdrive;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GoogleDriveFilesListActivity extends AppCompatActivity {

    public static String CLIENT_ID = "927421464752-1uf4qs6q43e8aaod3a6q8g7akiimpt8f.apps.googleusercontent.com";
    private static String API_KEY = "AIzaSyDqqGJo3XMhMiR3KCFXLE2fH7CUzrV7xkg";

    // private static String TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
    public static String OAUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    public static String OAUTH_SCOPE = "https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/userinfo.email";
    private static String AUTH_GRANT_TYPE = "authorization_code";

    // Redirect uri is reverse of CLIENT_ID
    public static String REDIRECT_URI = "com.googleusercontent.apps.927421464752-1uf4qs6q43e8aaod3a6q8g7akiimpt8f:/oauth";
    private static final String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
    private static String REFRESH_GRANT_TYPE = "refresh_token";
    public static final String BUNDLE_GOOGLE_AUTH_CODE = "BUNDLE_GOOGLE_AUTH_CODE";
    public static final String BUNDLE_GOOGLE_ACCESS_TOKEN = "BUNDLE_GOOGLE_ACCESS_TOKEN";
    public static final String BUNDLE_GOOGLE_TOKEN_TYPE = "BUNDLE_GOOGLE_TOKEN_TYPE";

    /** https://www.googleapis.com/plus/v1/people/{userId}, userId = me for current user   */
    private static final String GOOGLE_URL_USER_EMAIL = "https://www.googleapis.com/plus/v1/people/me?key=" + API_KEY;
    private static final String CMD_GET_TOKEN = "CMD_GET_TOKEN";
    private static final String CMD_GET_USER_EMAIL = "CMD_GET_USER_EMAIL";
    private static final String CMD_SAVE_TOKEN = "CMD_SAVE_TOKEN";

    private String googleAccessToken;
    private String googleTokenType;
    private JSONObject googleAuthJson;

    /** Root folder ID */
    private static final String GOOGLE_DRIVE_ROOT_FOLDER_ID = "root";

    private String GOOGLE_DRIVE_DEFAULT_ERROR_MESSAGE;

    /**
     * Header Layout: Back button | input box | "Cancel" button
     */
    private View searchLayout;
    private ImageView headerBackView; // "<" button
    private TextView headerCancelView; // "Cancel" button

    /** Search List View  */
    private ListView mListView;

    /** */
    private ProgressDialog pDialog;

    /** Handler: handle auto search for search input box(#searchView)  */
    private Handler handler = new Handler();

    /** */
    private Runnable runnable = null;

    private List<GoogleFile> googleFilesList = new ArrayList<GoogleFile>();

    private List<String> childrenIDs = new ArrayList<String>();
    private boolean isLoadingChild = false;

    private Stack<String> parentIDStack = new Stack<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_drive_files_layout);

        //
        // Gets auth code and then call drive API to list files
        Intent intent = this.getIntent();
        if (null == intent) {
            return;
        }
        Uri uri = intent.getData();
        if (null != uri) {
            googleAccessToken = uri.getQueryParameter("code");
        }

        if (TextUtils.isEmpty(this.googleAccessToken)) {
            if (!TextUtils.isEmpty(this.googleAccessToken) && !TextUtils.isEmpty(this.googleTokenType)) {
                getGoogleFilesByFolderID(GOOGLE_DRIVE_ROOT_FOLDER_ID);
            }
        } else {
            new TokenGet().execute(CMD_GET_TOKEN, this.googleAccessToken);
        }

    }

    /**
     * @param folderID
     */
    private void getGoogleFilesByFolderID(String folderID) {
        GoogleDriveAPIExecutor driveAPIExecutor = new GoogleDriveAPIExecutor(this, API_KEY, googleAccessToken, googleTokenType);
        driveAPIExecutor.setFolderID(folderID);
        parentIDStack.push(folderID);
        driveAPIExecutor.listChilds(new GoogleDriveAPIExecutor.APIExecutorListener() {
            @Override
            public void onResult(String result) {
                showChildrenListView(result);
            }
        });
    }

    /**
     * @param result
     */
    private void showChildrenListView(String result) {
        TextView textView = (TextView) this.findViewById(R.id.tmpText);
        textView.setText("Loading...");
        childrenIDs = GoogleDriveFileParser.parserChildrenID(result);
        loadingChildren();
    }

    private void loadingChildren() {
        if (isLoadingChild) {
            return;
        }

        if (0 < childrenIDs.size()) {
            isLoadingChild = true;
            String childID = childrenIDs.get(0);
            GoogleDriveAPIExecutor driveAPIExecutor = new GoogleDriveAPIExecutor(this, API_KEY, googleAccessToken, googleTokenType);
            driveAPIExecutor.setChildID(childID);
            driveAPIExecutor.getSingleChild(new GoogleDriveAPIExecutor.APIExecutorListener() {
                @Override
                public void onResult(String result) {
                    GoogleFile googleFile = GoogleDriveFileParser.parserSingleGoogleFile(result);
                    googleFilesList.add(googleFile);
                    isLoadingChild = false;
                    loadingChildren();
                }
            });
            childrenIDs.remove(childID);
        }
        else {
            StringBuffer fileBuffer = new StringBuffer();
            for (GoogleFile file : googleFilesList) {
                String type = file.isFolder() ? "Folder" : "File";
                String name = file.getName();
                fileBuffer.append(type + ":" + name);
                fileBuffer.append("\n");
            }

            TextView textView = (TextView) this.findViewById(R.id.tmpText);
            textView.setText(fileBuffer);
        }
    }

    /**
     *
     */
    private class TokenGet extends AsyncTask<String, String, JSONObject> {
        private String command;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            this.command = args[0];
            GoogleDriveGetAccessToken jParser = new GoogleDriveGetAccessToken();
            JSONObject json = null;

            if (CMD_GET_TOKEN.equals(command)) {
                String code = "";
                if (args.length > 1) {
                    code = args[1];
                }

                json = jParser.getToken(TOKEN_URL, code, CLIENT_ID, REDIRECT_URI, AUTH_GRANT_TYPE);
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                try {
                    if (CMD_GET_TOKEN.equals(command)) {
                        if (json.has("access_token")) {
                            googleAccessToken = json.getString("access_token");
                            googleTokenType = json.getString("token_type");

                            googleAuthJson = json;
                            getGoogleFilesByFolderID(GOOGLE_DRIVE_ROOT_FOLDER_ID);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                pDialog.dismiss();
            }
        }
    } // #End of AsyncTask
}
