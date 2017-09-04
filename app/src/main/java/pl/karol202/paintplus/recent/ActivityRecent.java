package pl.karol202.paintplus.recent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import com.google.firebase.analytics.FirebaseAnalytics;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.util.BlockableLinearLayoutManager;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

public class ActivityRecent extends AppCompatActivity implements OnImageSelectListener, View.OnClickListener
{
	private class SwipeCallback extends ItemTouchHelper.SimpleCallback
	{
		SwipeCallback()
		{
			super(0, LEFT | RIGHT);
		}
		
		@Override
		public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
		                      RecyclerView.ViewHolder target)
		{
			return false;
		}
		
		@Override
		public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
		{
			RecentAdapter.ViewHolder holder = (RecentAdapter.ViewHolder) viewHolder;
			int imageId = holder.getAdapterPosition();
			loader.removeRecentImage(imageId);
			loader.save();
			adapter.notifyItemRemoved(imageId);
		}
	}
	
	private RecentAdapter adapter;
	private RecentLoader loader;
	
	private Toolbar toolbar;
	private RecyclerView recyclerRecent;
	private FloatingActionButton buttonNewImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent);
		
		FirebaseAnalytics.getInstance(this);
		loader = new RecentLoader(this);
		adapter = new RecentAdapter(this, loader.getImages(), this);
		
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if(getSupportActionBar() == null) throw new RuntimeException("Cannot set action bar of activity.");
		
		recyclerRecent = findViewById(R.id.recycler_recent);
		recyclerRecent.setAdapter(adapter);
		recyclerRecent.setLayoutManager(new BlockableLinearLayoutManager(this));
		attachSwipingFeature();
		
		buttonNewImage = findViewById(R.id.button_new_image);
		buttonNewImage.setOnClickListener(this);
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
	}
	
	@Override
	public void onImageSelected(RecentImage image)
	{
		editImage(image.getPath());
	}
	
	@Override
	public void onClick(View v)
	{
		editImage(null);
	}
	
	private void editImage(String path)
	{
		Intent intent = new Intent(this, ActivityPaint.class);
		if(path != null) intent.putExtra("path", path);
		startActivity(intent);
	}
}