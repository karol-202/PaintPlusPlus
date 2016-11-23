package pl.karol202.paintplus.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import pl.karol202.paintplus.R;

public class ActivityMain extends AppCompatActivity
{
	private Toolbar toolbar;
	//private RecyclerView recentlyEdited;
	//private LinearLayoutManager layoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		//recentlyEdited = (RecyclerView) findViewById(R.id.recyclerRecentlyEdited);

		//layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		
		//recentlyEdited.setLayoutManager(layoutManager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == R.id.action_new)
		{
			Intent intent = new Intent(this, ActivityPaint.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_main);
	}
}
