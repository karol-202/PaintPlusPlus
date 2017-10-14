package pl.karol202.paintplus.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.ItemDivider;

public class ActivityHistory extends AppCompatActivity
{
	private ActionBar actionBar;
	private HistoryAdapter adapter;
	
	private Toolbar toolbar;
	private RecyclerView recyclerHistory;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		initToolbar();
		initRecyclerHistory();
	}
	
	private void initToolbar()
	{
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		actionBar = getSupportActionBar();
		if(actionBar == null) throw new RuntimeException("Action bar not found.");
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void initRecyclerHistory()
	{
		adapter = new HistoryAdapter(this, ActivityHistoryHelper.getHistory());
		
		recyclerHistory = findViewById(R.id.recycler_history);
		recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
		recyclerHistory.setAdapter(adapter);
		recyclerHistory.addItemDecoration(new ItemDivider(this));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home) onBackPressed();
		return super.onOptionsItemSelected(item);
	}
}