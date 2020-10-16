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

package pl.karol202.paintplus.recent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.google.firebase.analytics.FirebaseAnalytics;
import pl.karol202.paintplus.BuildConfig;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.PermissionRequest;
import pl.karol202.paintplus.activity.PermissionRequest.PermissionGrantListener;
import pl.karol202.paintplus.activity.PermissionRequest.PermissionGrantingActivity;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.util.BlockableLinearLayoutManager;

import java.util.HashMap;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

public class ActivityRecent extends AppCompatActivity implements PermissionGrantingActivity
{
	private class SwipeCallback extends ItemTouchHelper.SimpleCallback
	{
		SwipeCallback()
		{
			super(0, LEFT | RIGHT);
		}
		
		@Override
		public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
		                      @NonNull RecyclerView.ViewHolder target)
		{
			return false;
		}
		
		@Override
		public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
		{
			RecentAdapter.ViewHolder holder = (RecentAdapter.ViewHolder) viewHolder;
			int imageId = holder.getAdapterPosition();
			loader.removeRecentImage(imageId);
			loader.save();
			adapter.notifyItemRemoved(imageId);
			animateNoImagesView();
		}
	}
	
	private RecentAdapter adapter;
	private RecentLoader loader;
	private int animationDuration;
	private HashMap<Integer, PermissionGrantListener> permissionListeners;
	
	private Toolbar toolbar;
	private View viewNoImages;
	private RecyclerView recyclerRecent;
	private FloatingActionButton buttonNewImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);
		initFirebaseIfNotDebug();
		ImageLoader.setTemporaryFileLocation(getExternalFilesDir(Environment.DIRECTORY_PICTURES));
		
		loader = new RecentLoader(this);
		adapter = new RecentAdapter(this, loader.getImages(), image -> editImage(image.getUri()));
		animationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
		permissionListeners = new HashMap<>();
		
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if(getSupportActionBar() == null) throw new RuntimeException("Cannot set action bar of activity.");
		
		viewNoImages = findViewById(R.id.view_no_images);

		recyclerRecent = findViewById(R.id.recycler_recent);
		recyclerRecent.setAdapter(adapter);
		recyclerRecent.setLayoutManager(new BlockableLinearLayoutManager(this));
		attachSwipingFeature();
		
		buttonNewImage = findViewById(R.id.button_new_image);
		buttonNewImage.setOnClickListener(v -> editImage(null));
	}
	
	private void initFirebaseIfNotDebug()
	{
		if(!BuildConfig.DEBUG) FirebaseAnalytics.getInstance(this);
	}
	
	private void attachSwipingFeature()
	{
		ItemTouchHelper helper = new ItemTouchHelper(new SwipeCallback());
		helper.attachToRecyclerView(recyclerRecent);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		loader.load();
		adapter.notifyDataSetChanged();
		updateNoImagesViewImmediately();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_recent, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_open_image:
			selectImageToOpen();
			break;
		default: super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private void editImage(Uri uri)
	{
		Intent intent = new Intent(this, ActivityPaint.class);
		if(uri != null) intent.putExtra(ActivityPaint.URI_KEY, uri);
		startActivity(intent);
	}
	
	private void selectImageToOpen()
	{
		new PermissionRequest<>(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, () -> {
			Intent intent = new Intent(ActivityRecent.this, ActivityPaint.class);
			intent.putExtra(ActivityPaint.OPEN_KEY, true);
			startActivity(intent);
		});
	}
	
	private void animateNoImagesView()
	{
		if(loader.getImagesAmount() != 0) return;
		
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(animationDuration);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation)
			{
				viewNoImages.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationEnd(Animation animation) { }
			
			@Override
			public void onAnimationRepeat(Animation animation) { }
		});
		
		viewNoImages.startAnimation(animation);
	}
	
	private void updateNoImagesViewImmediately()
	{
		boolean visible = loader.getImagesAmount() == 0;
		viewNoImages.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}
	
	@Override
	public void registerPermissionGrantListener(int requestCode, PermissionGrantListener listener)
	{
		if(permissionListeners.containsKey(requestCode))
			throw new RuntimeException("requestCode is already used: " + requestCode);
		permissionListeners.put(requestCode, listener);
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(!permissionListeners.containsKey(requestCode)) return;
		if(grantResults[0] == PackageManager.PERMISSION_GRANTED) permissionListeners.get(requestCode).onPermissionGrant();
		permissionListeners.remove(requestCode);
	}
}