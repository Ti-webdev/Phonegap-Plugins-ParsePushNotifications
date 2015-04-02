package com.stratogos.cordova.parsePushNotifications;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import com.parse.ParseAnalytics;

public class PushHandlerActivity extends Activity
{
	private static String TAG = "PushHandlerActivity";

	/*
	 * this activity will be started if the user touches a notification that we own.
	 * We send it's data off to the push plugin for processing.
	 * If needed, we boot up the main activity to kickstart the application.
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		ParseAnalytics.trackAppOpenedInBackground(getIntent());
		boolean isPluginActive = ParsePushNotificationPlugin.isActive();
		processPushBundle(isPluginActive);

		finish();

		if (!isPluginActive) {
			forceMainActivityReload();
		}
	}

	/**
	 * Takes the pushBundle extras from the intent,
	 * and sends it through to the Plugin for processing.
	 */
	private void processPushBundle(boolean isPluginActive)
	{
		Log.v(TAG, "processPushBundle, isActive: " + isPluginActive);
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			ParsePushNotificationPlugin.NotificationReceived(extras.getString("com.parse.Data"), ParsePushNotificationPlugin.isInForeground(), !isPluginActive);
		}
	}

	/**
	 * Forces the main activity to re-launch if it's unloaded.
	 */
	private void forceMainActivityReload()
	{
		PackageManager pm = getPackageManager();
		Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
		startActivity(launchIntent);
	}

}