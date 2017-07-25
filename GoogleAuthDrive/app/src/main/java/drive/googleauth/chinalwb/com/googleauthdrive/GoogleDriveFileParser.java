
package drive.googleauth.chinalwb.com.googleauthdrive;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class GoogleDriveFileParser {
  private static Gson gson = new Gson();

  public static ArrayList<GoogleFile> parserGoogleFiles(String result) {
    if (TextUtils.isEmpty(result)) {
      return null;
    }

    ArrayList<GoogleFile> googleFiles = new ArrayList<GoogleFile>();

    try {
      JsonParser jsonparer = new JsonParser();

      JsonObject jsonObject = jsonparer.parse(result).getAsJsonObject();
      if (null == jsonObject) {
        return null;
      }

      if (!jsonObject.has("files") || jsonObject.get("files").isJsonNull()) {
        return null;
      }
      JsonArray filesJsonArray = jsonObject.get("files").getAsJsonArray();

      if (null == filesJsonArray) {
        return null;
      }

      GoogleFile googleFile;
      JsonObject object;
      for (int i = 0; i < filesJsonArray.size(); i++) {
        object = filesJsonArray.get(i).getAsJsonObject();
        googleFile = gson.fromJson(object.toString(), GoogleFile.class);
        if ("application/vnd.google-apps.folder".equals(googleFile.getMimeType())) {
          googleFile.setFolder(true);
        }
        googleFiles.add(googleFile);
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return googleFiles;
  }
  
  public static GoogleFile parserSingleGoogleFile(String result) {
    if (TextUtils.isEmpty(result)) {
      return null;
    }

    GoogleFile googleFile = null;

    try {
      JsonParser jsonparer = new JsonParser();

      JsonObject jsonObject = jsonparer.parse(result).getAsJsonObject();
      if (null == jsonObject) {
        return null;
      }

      googleFile = gson.fromJson(jsonObject.toString(), GoogleFile.class);
      if ("application/vnd.google-apps.folder".equals(googleFile.getMimeType())) {
        googleFile.setFolder(true);
      }

      return googleFile;
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
  
  public static ArrayList<String> parserChildrenID(String result) {
    if (TextUtils.isEmpty(result)) {
      return null;
    }
    
    ArrayList<String> childrenIDs = new ArrayList<String>();

    try {
      JsonParser jsonparer = new JsonParser();

      JsonObject jsonObject = jsonparer.parse(result).getAsJsonObject();
      if (null == jsonObject) {
        return null;
      }

      if (!jsonObject.has("items") || jsonObject.get("items").isJsonNull()) {
        return null;
      }
      JsonArray filesJsonArray = jsonObject.get("items").getAsJsonArray();

      if (null == filesJsonArray) {
        return null;
      }

      String childrenID;
      JsonObject object;
      for (int i = 0; i < filesJsonArray.size(); i++) {
        object = filesJsonArray.get(i).getAsJsonObject();
        childrenID = object.get("id").getAsString();
        childrenIDs.add(childrenID);
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    
    return childrenIDs;
  }

}
