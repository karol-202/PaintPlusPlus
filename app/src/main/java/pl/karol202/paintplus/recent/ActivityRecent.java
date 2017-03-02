package pl.karol202.paintplus.recent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.google.firebase.analytics.FirebaseAnalytics;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.image.layer.LayersLayoutManager;

public class ActivityRecent extends AppCompatActivity implements OnImageSelectListener, View.OnClickListener
{
	private RecentAdapter adapter;
	
	private Toolbar toolbar;
	private RecyclerView recyclerRecent;
	private FloatingActionButton buttonNewImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		FirebaseAnalytics.getInstance(this);
		RecentLoader loader = new RecentLoader(this);
		adapter = new RecentAdapter(this, loader.load(), this);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		recyclerRecent = (RecyclerView) findViewById(R.id.recycler_recent);
		recyclerRecent.setAdapter(adapter);
		recyclerRecent.setLayoutManager(new LayersLayoutManager(this));
		
		buttonNewImage = (FloatingActionButton) findViewById(R.id.button_new_image);
		buttonNewImage.setOnClickListener(this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
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