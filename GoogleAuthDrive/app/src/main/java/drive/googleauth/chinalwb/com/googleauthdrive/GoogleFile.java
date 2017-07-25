
package drive.googleauth.chinalwb.com.googleauthdrive;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class GoogleFile {

  private String kind;

  private String id;

  private String name;

  private String mimeType;
  
  private String webViewLink;
  
  private String iconLink;

  private String modifiedTime;
  
  private List<User> owners;
  
  private long size;
  
  private boolean isCategory = false;

  private boolean isEmpty = false;

  private boolean isFolder = false;
  
  private boolean isBackFolder = false;

  public String getKind() {
    if (TextUtils.isEmpty(kind)) {
      return "";
    }

    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getId() {
    if (TextUtils.isEmpty(id)) {
      return "";
    }

    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    if (TextUtils.isEmpty(name)) {
      return "";
    }

    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMimeType() {
    if (TextUtils.isEmpty(mimeType)) {
      return "";
    }

    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
  
  public String getWebViewLink() {
    if (TextUtils.isEmpty(webViewLink)) {
      return "";
    }
    
    return webViewLink;
  }

  public void setWebViewLink(String webViewLink) {
    this.webViewLink = webViewLink;
  }
  
  public String getIconLink() {
    if (TextUtils.isEmpty(iconLink)) {
      return "";
    }
    
    return iconLink;
  }

  public void setIconLink(String iconLink) {
    this.iconLink = iconLink;
  }

  public String getModifiedTime() {
    if (TextUtils.isEmpty(modifiedTime)) {
      return "";
    }
    
    return modifiedTime;
  }

  public void setModifiedTime(String modifiedTime) {
    this.modifiedTime = modifiedTime;
  }
  
  public List<User> getOwners() {
    if (null == owners) {
      return new ArrayList<User>();
    }
    
    return owners;
  }

  public void setOwners(List<User> owners) {
    this.owners = owners;
  }
  
  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }
  
  public boolean isCategory() {
    return isCategory;
  }

  public void setCategory(boolean isCategory) {
    this.isCategory = isCategory;
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  public boolean isFolder() {
    return isFolder;
  }

  public void setFolder(boolean isFolder) {
    this.isFolder = isFolder;
  }
  
  public boolean isBackFolder() {
    return isBackFolder;
  }

  public void setBackFolder(boolean isBackFolder) {
    this.isBackFolder = isBackFolder;
  }
  
  public class User {
    public String kind;
    public String displayName;
    public boolean me;
    public String permissionId;
    public String emailAddress;
  }
}
