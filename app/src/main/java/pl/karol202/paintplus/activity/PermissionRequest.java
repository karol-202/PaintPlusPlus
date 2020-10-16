/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

public class PermissionRequest<A extends Activity & PermissionRequest.PermissionGrantingActivity>
{
	public interface PermissionGrantingActivity
	{
		void registerPermissionGrantListener(int requestCode, PermissionGrantListener permissionGrantListener);
	}

	public interface PermissionGrantListener
	{
		void onPermissionGrant();
	}

	private static final int REQUEST_CODE = 10;

	private A activity;
	private String permission;
	private PermissionGrantListener grantListener;

	public PermissionRequest(A activity, String permission, PermissionGrantListener grantListener)
	{
		this.activity = activity;
		this.permission = permission;
		this.grantListener = grantListener;
		checkForPermission();
	}

	private void checkForPermission()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			if(activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) permissionGranted();
			else requestForPermission();
		}
		else permissionGranted();
	}

	private void permissionGranted()
	{
		if(grantListener != null) grantListener.onPermissionGrant();
	}

	private void requestForPermission()
	{
		activity.registerPermissionGrantListener(REQUEST_CODE, grantListener);
		ActivityCompat.requestPermissions(activity, new String[] { permission }, REQUEST_CODE);
	}
}
