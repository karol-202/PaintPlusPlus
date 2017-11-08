package pl.karol202.paintplus.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

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