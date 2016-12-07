package pl.karol202.paintplus.tool.fill;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.color.ColorsSet;

import java.util.Stack;

public class ToolFillAsyncTask extends AsyncTask<FillParams, Void, Bitmap>
{
	public interface OnFillCompleteListener
	{
		void onFillComplete(Bitmap bitmap);
	}
	
	private OnFillCompleteListener listener;
	private ColorsSet colors;
	private Bitmap bitmap;
	
	@Override
	protected Bitmap doInBackground(FillParams... paramsArray)
	{
		if(paramsArray.length != 1)
			throw new IllegalArgumentException("There must be only one params object passed to ToolFillAsyncTask.");
		FillParams params = paramsArray[0];
		Image image = params.getImage();
		
		listener = params.getListener();
		colors = image.getColorsSet();
		bitmap = image.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
		
		fill(params.getX(), params.getY(), colors.getFirstColor());
		return bitmap;
	}
	
	private void fill(int x, int y, int destColor)
	{
		int touchedColor = bitmap.getPixel(x, y);
		if(touchedColor == destColor) return;
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		Stack<Point> pointsToCheck = new Stack<>();
		pointsToCheck.push(new Point(x, y));
		while(!pointsToCheck.isEmpty())
		{
			Point point = pointsToCheck.pop();
			int pos = point.y * width + point.x;
			int color = pixels[pos];
			
			if(color != touchedColor) continue;
			pixels[pos] = destColor;
			
			if(point.x > 0)
				pointsToCheck.add(new Point(point.x - 1, point.y));
			if(point.y > 0)
				pointsToCheck.add(new Point(point.x, point.y - 1));
			if(point.x < width - 1)
				pointsToCheck.add(new Point(point.x + 1, point.y));
			if(point.y < height - 1)
				pointsToCheck.add(new Point(point.x, point.y + 1));
		}
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap)
	{
		super.onPostExecute(bitmap);
		if(listener != null) listener.onFillComplete(bitmap);
	}
}