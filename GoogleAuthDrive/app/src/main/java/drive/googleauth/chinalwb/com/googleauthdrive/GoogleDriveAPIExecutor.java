
package drive.googleauth.chinalwb.com.googleauthdrive;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class GoogleDriveAPIExecutor {

  public interface APIExecutorListener {
    public void onResult(String result);
  }

  private static final String CMD_SEARCH_FILES = "CMD_SEARCH_FILES";
  private static final String CMD_LIST_FOLDER_CHILDREN = "CMD_LIST_FOLDER_CHILDREN";
  private static final String CMD_GET_SINGLE_CHILD = "CMD_GET_SINGLE_CHILD";
  private static final String CMD_SHARE_FILE_TO_VMOSO = "CMD_SHARE_FILE_TO_VMOSO";

  private Context context;
  private String apiKey;
  private String accessToken;
  private String tokenType;
  private String folderID;

  public void setFolderID(String folderID) {
    this.folderID = folderID;
  }
  
  private String childID;
  public void setChildID(String childID) {
    this.childID = childID;
  }
  
  private GoogleFile googleFile;
  public void setGoogleFile(GoogleFile googleFile) {
    this.googleFile = googleFile;
  }
  
  private String searchKeywords;
  public String getSearchKeywords() {
    return searchKeywords;
  }

  public void setSearchKeywords(String searchKeywords) {
    this.searchKeywords = searchKeywords;
  }

  public GoogleDriveAPIExecutor(
      Context context,
      String apiKey, 
      String accessToken,
      String tokenType) {
    this.context = context;
    this.apiKey = apiKey;
    this.accessToken = accessToken;
    this.tokenType = tokenType;
  }
  
  /**
   * List Drive files.
   */
  public void searchFiles(APIExecutorListener listener) {
    callAPI(CMD_SEARCH_FILES, listener);
  }
  
  public void listChilds(APIExecutorListener listener) {
    callAPI(CMD_LIST_FOLDER_CHILDREN, listener);
  }
  
  public void getSingleChild(APIExecutorListener listener) {
    callAPI(CMD_GET_SINGLE_CHILD, listener);
  }
  
  public void shareFileToVmoso(APIExecutorListener listener) {
    callAPI(CMD_SHARE_FILE_TO_VMOSO, listener);
  }

  private void callAPI(String cmd, APIExecutorListener listener) {
    APIAsyncTask task = new APIAsyncTask(cmd, listener);
    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }

  /**
   * 
   * @author wenbin
   * 
   */
  private class APIAsyncTask extends AsyncTask<String, Integer, String> {

    private String cmd;
    private APIExecutorListener listener;

    public APIAsyncTask(String cmd, APIExecutorListener listener) {
      this.cmd = cmd;
      this.listener = listener;
    }

    @Override
    protected String doInBackground(String... parms) {
      
      if (CMD_LIST_FOLDER_CHILDREN.equals(cmd)) {
        return getFolderChildren(folderID);
      }
      else if (CMD_GET_SINGLE_CHILD.equals(cmd)) {
        return getSingleChild(childID);
      }
      else if (CMD_SEARCH_FILES.equals(cmd)) {
        return searchFiles();
      }
      
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      listener.onResult(result);
    }
  }
  
  private String getFolderChildren(String folderKey) {
    try {
      // https://www.googleapis.com/drive/v2/files/0B1npQPTTEuRWbVlVWjA0WmEydkU/children?key=AIzaSyBztHtwRi6Os3-UEcyKC-EfHNoYYovZNok
      String requestUrl = 
          "https://www.googleapis.com/drive/v2/files/" 
          + folderKey 
          + "/children?fields=items/id&key="
          + GoogleDriveAPIExecutor.this.apiKey;
      URL url = new URL(requestUrl);
      
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.addRequestProperty("authorization", tokenType + " " + accessToken);
      connection.connect();
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

      String line;
      StringBuffer response = new StringBuffer();
      while ((line = rd.readLine()) != null) {
        response.append(line);
      }
      rd.close();
      is.close();
      return response.toString();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      GoogleDriveHelper googleDriveHelper = new GoogleDriveHelper(context);
      GoogleDriveHelper.openBrowserForGoogleOAuth(context);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return null;    
  }
  
  private String getSingleChild(String childID) {
    try {
      // https://www.googleapis.com/drive/v3/files/1fT7lEzKnBURoP98S1uiYc5a5dANaqRB3nxTshJ1oSlw?fields=iconLink%2Cid%2Ckind%2CmimeType%2CmodifiedTime%2Cname&key={YOUR_API_KEY}
      String requestUrl = 
          "https://www.googleapis.com/drive/v3/files/"
          + childID 
          + "?fields=iconLink,id,kind,mimeType,modifiedTime,name,owners,webViewLink,size&key="
          + GoogleDriveAPIExecutor.this.apiKey;
      URL url = new URL(requestUrl);
      
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.addRequestProperty("Authorization", tokenType + " " + accessToken);
      connection.connect();
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

      String line;
      StringBuffer response = new StringBuffer();
      while ((line = rd.readLine()) != null) {
        response.append(line);
      }
      rd.close();
      is.close();
      return response.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return null;    
  }
  

  private JSONObject prepareInputParams() {
    JSONObject params = new JSONObject();
    JSONObject contentRecord = new JSONObject();
    
    try {
      contentRecord.put("name", googleFile.getName());
      contentRecord.put("description", "");
      contentRecord.put("filename", "1");
      contentRecord.put("originalfilename", googleFile.getName());
      contentRecord.put("mimetype", googleFile.getMimeType());
      contentRecord.put("downloadable", "1");
      contentRecord.put("site_selected", "1");
      contentRecord.put("bvrev_version", "1");
      contentRecord.put("bvrev_last_updater", "1");
      contentRecord.put("dvault_item_status", "1");
      contentRecord.put("dvault_item_flag", "1");
      contentRecord.put("simpletype", "1");
      contentRecord.put("filestore_dir_root", "1");
      contentRecord.put("filestore_filestore", "");
      contentRecord.put("viewHistoryFlag", "1");
      contentRecord.put("filesize", googleFile.getSize());
      contentRecord.put("fileOrigin", "google");
      contentRecord.put("externalFileID", googleFile.getId());
      contentRecord.put("externalFileURL", googleFile.getWebViewLink());
      contentRecord.put("externalFileCreatorEmail", googleFile.getOwners().get(0).emailAddress);
      contentRecord.put("type", "file");
      
      params.put("viewHistoryFlag", "1");
      params.put("contentType", "file");
      params.put("inputfile", contentRecord);
      params.put("dominantActivity", "1");
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
    
    return params;
  }

  private String searchFiles() {
    try {
      // https://www.googleapis.com/drive/v3/files?pageSize=100&q=name+contains+'test'&fields=files(mimeType%2Cname)&key={YOUR_API_KEY}
      String requestUrl = 
          "https://www.googleapis.com/drive/v3/files?" 
          +"q=fullText+contains+" 
          + "'" 
          + URLEncoder.encode(searchKeywords, "utf-8")
          + "'" 
          + "&fields=files,kind&key=" 
          + GoogleDriveAPIExecutor.this.apiKey;
      URL url = new URL(requestUrl);
      
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.addRequestProperty("Authorization", tokenType + " " + accessToken);
      connection.connect();
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));

      String line;
      StringBuffer response = new StringBuffer();
      while ((line = rd.readLine()) != null) {
        response.append(line);
      }
      rd.close();
      is.close();
      return response.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
