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

package pl.karol202.paintplus.file.explorer;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import pl.karol202.paintplus.ErrorHandler;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.util.ItemDivider;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public abstract class ActivityFileChoose extends AppCompatActivity implements FileAdapter.OnFileSelectListener
{
	private class FileFilter implements FilenameFilter
	{
		@Override
		public boolean accept(File dir, String name)
		{
			File file = new File(dir, name);
			if(file.isDirectory()) return true;
			
			String[] parts = name.split("\\.");
			String extension = parts[parts.length - 1];
			for(String filter : ImageLoader.OPEN_FORMATS)
				if(extension.equalsIgnoreCase(filter)) return true;
			return false;
		}
	}
	
	private class FileComparator implements Comparator<File>
	{
		@Override
		public int compare(File file1, File file2)
		{
			if(file1.isDirectory() && !file2.isDirectory()) return -1;
			else if(!file1.isDirectory() && file2.isDirectory()) return 1;
			else return file1.getAbsolutePath().compareToIgnoreCase(file2.getAbsolutePath());
		}
	}
	
	private FileAdapter adapter;
	private FileFilter fileFilter;
	private FileComparator fileComparator;
	private File currentDirectory;
	private Stack<File> previousDirectories;
	private ArrayList<File> files;
	
	private Toolbar toolbar;
	private RecyclerView recyclerFiles;
	
	@Override
	protected void onCreate(Bundle state)
	{
		super.onCreate(state);
		setContentView(getLayout());
		
		adapter = new FileAdapter(this, this);
		fileFilter = new FileFilter();
		fileComparator = new FileComparator();
		if(state == null) init();
		else initFromSavedState(state);
		
		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if(actionBar == null) throw new RuntimeException("Cannot set action bar of activity.");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		recyclerFiles = findViewById(R.id.recycler_files);
		recyclerFiles.setLayoutManager(new LinearLayoutManager(this));
		recyclerFiles.setAdapter(adapter);
		recyclerFiles.addItemDecoration(new ItemDivider(this));
	}
	
	private void init()
	{
		currentDirectory = Environment.getExternalStorageDirectory();
		previousDirectories = new Stack<>();
		files = new ArrayList<>();
		navigateTo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
	}
	
	private void initFromSavedState(Bundle state)
	{
		currentDirectory = new File(state.getString("currentDirectory"));
		
		previousDirectories = new Stack<>();
		ArrayList<String> previousList = state.getStringArrayList("previousDirectories");
		for(String filePath : previousList) previousDirectories.push(new File(filePath));
		
		files = new ArrayList<>();
		updateItems();
	}
	
	public abstract int getLayout();
	
	@Override
	public void onBackPressed()
	{
		if(canNavigateBack()) navigateBack();
		else super.onBackPressed();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString("currentDirectory", currentDirectory.getAbsolutePath());
		
		ArrayList<String> previousList = new ArrayList<>();
		for(File file : previousDirectories) previousList.add(file.getAbsolutePath());
		outState.putStringArrayList("previousDirectories", previousList);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home)
		{
			setResult(RESULT_CANCELED);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void navigateTo(File file)
	{
		if(!file.exists() || !file.isDirectory())
		{
			ErrorHandler.report(new RuntimeException("The specified path must be a directory."));
			return;
		}
		previousDirectories.push(currentDirectory);
		currentDirectory = file;
		updateItems();
	}
	
	private void navigateBack()
	{
		if(!canNavigateBack()) return;
		currentDirectory = previousDirectories.pop();
		updateItems();
	}
	
	private boolean canNavigateBack()
	{
		return previousDirectories.size() != 0;
	}
	
	private void updateItems()
	{
		File[] fileArray = currentDirectory.listFiles(fileFilter);
		if(fileArray == null) files.clear();
		else files = new ArrayList<>(Arrays.asList(fileArray));
		Collections.sort(files, fileComparator);
		
		ArrayList<File> visibleItems = new ArrayList<>(files);
		if(previousDirectories.size() != 0)
			visibleItems.add(0, null); //<- Powrót
		adapter.setFiles(visibleItems);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onFileSelected(File file)
	{
		if(file == null) navigateBack();
		else if(file.isDirectory()) navigateTo(file);
		else return true;
		return false;
	}
	
	File getCurrentDirectory()
	{
		return currentDirectory;
	}
}