package pl.karol202.paintplus.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import pl.karol202.paintplus.file.AdapterFile;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.util.ItemDivider;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public abstract class ActivityFileChoose extends AppCompatActivity implements AdapterFile.OnFileSelectListener
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
			for(String filter : ImageLoader.FORMATS)
				if(extension.equalsIgnoreCase(filter)) return true;
			return false;
		}
	}
	
	private class FileComparator implements Comparator<File>
	{
		@Override
		public int compare(File file1, File file2)
		{
			return file1.getAbsolutePath().compareToIgnoreCase(file2.getAbsolutePath());
		}
	}
	
	private AdapterFile adapter;
	private FileFilter fileFilter;
	private FileComparator fileComparator;
	private File currentDirectory;
	private Stack<File> previousDirectories;
	private ArrayList<File> files;
	
	protected Toolbar toolbar;
	protected RecyclerView recyclerFiles;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		
		adapter = new AdapterFile(this, this);
		fileFilter = new FileFilter();
		fileComparator = new FileComparator();
		currentDirectory = Environment.getExternalStorageDirectory();
		previousDirectories = new Stack<>();
		files = new ArrayList<>();
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		recyclerFiles = (RecyclerView) findViewById(R.id.recycler_files);
		recyclerFiles.setLayoutManager(new LinearLayoutManager(this));
		recyclerFiles.setAdapter(adapter);
		recyclerFiles.addItemDecoration(new ItemDivider(this));
		
		navigateTo(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
	}
	
	public abstract int getLayout();
	
	@Override
	public void onBackPressed()
	{
		if(canNavigateBack()) navigateBack();
		else super.onBackPressed();
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
		if(!file.exists() || !file.isDirectory()) throw new RuntimeException("The specified path must be a directory.");
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
	
	public File getCurrentDirectory()
	{
		return currentDirectory;
	}
}