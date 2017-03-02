package pl.karol202.paintplus.recent;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import pl.karol202.paintplus.R;

import java.util.ArrayList;

class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>
{
	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private RecentImage image;
		
		private ImageView imageThumbnail;
		private TextView textName;
		private Button buttonOpen;
		
		public ViewHolder(View view)
		{
			super(view);
			imageThumbnail = (ImageView) view.findViewById(R.id.image_recent_thumbnail);
			textName = (TextView) view.findViewById(R.id.text_recent_name);
			buttonOpen = (Button) view.findViewById(R.id.button_recent_open);
			buttonOpen.setOnClickListener(this);
		}
		
		void bind(RecentImage image)
		{
			this.image = image;
			Bitmap thumbnail = image.getThumbnail();
			
			if(thumbnail != null) imageThumbnail.setImageBitmap(thumbnail);
			textName.setText(image.getName());
		}
		
		@Override
		public void onClick(View v)
		{
			listener.onImageSelected(image);
		}
	}
	
	private Context context;
	private ArrayList<RecentImage> images;
	private OnImageSelectListener listener;
	
	public RecentAdapter(Context context, ArrayList<RecentImage> images, OnImageSelectListener listener)
	{
		this.context = context;
		this.images = images;
		this.listener = listener;
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.card_recent, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		holder.bind(images.get(position));
	}
	
	@Override
	public int getItemCount()
	{
		return images.size();
	}
}