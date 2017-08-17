package pl.karol202.paintplus.file;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

import java.io.File;
import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>
{
	public interface OnFileSelectListener
	{
		boolean onFileSelected(File file);
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private File file;
		private ImageView imageFileType;
		private TextView textFileName;
		
		ViewHolder(View view)
		{
			super(view);
			view.setOnClickListener(this);
			imageFileType = (ImageView) view.findViewById(R.id.image_file_type);
			textFileName = (TextView) view.findViewById(R.id.text_file_name);
		}
		
		void bind(File file)
		{
			this.file = file;
			imageFileType.setImageResource(getFileTypeIcon(file));
			textFileName.setText(getFileName(file));
		}
		
		private int getFileTypeIcon(File file)
		{
			if(file == null) return R.drawable.ic_arrow_file_back_black_24dp;
			if(file.isDirectory()) return R.drawable.ic_folder_black_24dp;
			else
			{
				String extension = getFileExtension(file);
				for(String filter : ImageLoader.OPEN_FORMATS)
					if(extension.equalsIgnoreCase(filter)) return R.drawable.ic_image_black_24dp;
				return R.drawable.ic_file_black_24dp;
			}
		}
		
		private String getFileExtension(File file)
		{
			String fileName = file.getName();
			String[] nameParts = fileName.split("\\.");
			return nameParts[nameParts.length - 1];
		}
		
		private String getFileName(File file)
		{
			if(file == null) return res.getString(R.string.file_back);
			else return file.getName();
		}
		
		@Override
		public void onClick(View v)
		{
			listener.onFileSelected(file);
		}
	}
	
	private Context context;
	private Resources res;
	private ArrayList<File> files;
	private OnFileSelectListener listener;
	
	FileAdapter(Context context, OnFileSelectListener listener)
	{
		this.context = context;
		this.res = context.getResources();
		this.listener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.item_file, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		holder.bind(files.get(position));
	}
	
	@Override
	public int getItemCount()
	{
		if(files == null) return 0;
		return files.size();
	}
	
	void setFiles(ArrayList<File> files)
	{
		this.files = files;
	}
}