package pl.karol202.paintplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import pl.karol202.paintplus.R;

public class ActivityMain extends AppCompatActivity
{
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
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
		switch(id)
		{
		case R.id.action_new:
			newImage();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void newImage()
	{
		Intent intent = new Intent(this, ActivityPaint.class);
		startActivity(intent);
	}
}
