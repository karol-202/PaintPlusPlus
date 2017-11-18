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

package pl.karol202.paintplus.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.util.ItemDivider;

public class ActivityHistory extends AppCompatActivity
{
	private Image image;
	private ActionBar actionBar;
	private HistoryAdapter adapter;
	
	private Toolbar toolbar;
	private RecyclerView recyclerHistory;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		image = ActivityHistoryHelper.getImage();
		
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
		adapter = new HistoryAdapter(this, image.getHistory());
		
		recyclerHistory = findViewById(R.id.recycler_history);
		recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
		recyclerHistory.setAdapter(adapter);
		recyclerHistory.addItemDecoration(new ItemDivider(this));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_history, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.findItem(R.id.action_undo).setEnabled(image.canUndo());
		menu.findItem(R.id.action_redo).setEnabled(image.canRedo());
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.action_undo:
			image.undo();
			adapter.notifyDataSetChanged();
			invalidateOptionsMenu();
			break;
		case R.id.action_redo:
			image.redo();
			adapter.notifyDataSetChanged();
			invalidateOptionsMenu();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}