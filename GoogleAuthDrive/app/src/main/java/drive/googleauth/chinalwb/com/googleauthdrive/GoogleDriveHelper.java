package drive.googleauth.chinalwb.com.googleauthdrive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Helper class to check and get access token for calling Drive APIs.
 * 
 * @author Wenbin Liu
 * 
 */
public class GoogleDriveHelper {

	/** */
	private static  final String CMD_GET_ACCESS_TOKEN_FROM_VMOSO = "CMD_GET_ACCESS_TOKEN_FROM_VMOSO";

	/** */
	private static  final String CMD_GET_GOOGLE_EMAIL_FROM_VMOSO = "CMD_GET_GOOGLE_EMAIL_FROM_VMOSO";

	/** */
	private Context mContext;

	private String mGoogleEmailValue;

	/**
	 * Constructor.
	 *
	 * If you use the <code>Activity</code> to start Google Drive Files list Activity, then
	 * you probably do not have an anchor fragment, so use this constructor then you don't
	 * need to set the other parameters.
	 *
	 * @param context Android context.
	 */
	public GoogleDriveHelper(Context context) {
		this.mContext = context;
	}

	/**
	 * Opens the Google Drive files list page if there is already a google access token exists.
	 * Or else opens browser for google auth.
	 */
	public void openDriveFilesListPage() {
		String googleAccessToken = ""; // get google access token
		if (TextUtils.isEmpty(googleAccessToken)) {
			GoogleDriveHelper.openBrowserForGoogleOAuth(mContext);
		}
		else {
			Intent intent = new Intent(mContext, GoogleDriveFilesListActivity.class);
			intent.putExtra(GoogleDriveFilesListActivity.BUNDLE_GOOGLE_ACCESS_TOKEN, googleAccessToken);
			// intent.putExtra(GoogleDriveFilesListActivity.BUNDLE_GOOGLE_TOKEN_TYPE, googleTokenType);
			if (null != mContext && mContext instanceof Activity) {
				((Activity) mContext).startActivity(intent);
			}
		}
	}

	/**
	 * Opens browser on phone for getting through of Google OAuth.
	 *
	 * @param context Android context
	 */
	public static void openBrowserForGoogleOAuth(Context context) {
        String authUrl = GoogleDriveFilesListActivity.OAUTH_URL
        		+ "?redirect_uri="
        		+ GoogleDriveFilesListActivity.REDIRECT_URI
        		+ "&response_type=code&client_id="
        		+ GoogleDriveFilesListActivity.CLIENT_ID
        		+ "&scope="
        		+ GoogleDriveFilesListActivity.OAUTH_SCOPE;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
		context.startActivity(browserIntent);
	}

}
