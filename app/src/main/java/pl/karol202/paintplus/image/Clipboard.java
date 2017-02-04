package pl.karol202.paintplus.image;

import android.graphics.*;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;

public class Clipboard
{
	private Image image;
	private Selection selection;
	private String defaultLayerName;
	
	private Bitmap bitmap;
	private int left;
	private int top;
	
	public Clipboard(Image image, String defaultLayerName)
	{
		this.image = image;
		this.selection = image.getSelection();
		this.defaultLayerName = defaultLayerName;
	}
	
	public void cut(Layer selectedLayer)
	{
		copy(selectedLayer);
		
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		Canvas canvas = selectedLayer.getEditCanvas();
		canvas.clipPath(selection.getPath(), Region.Op.REPLACE);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
	}
	
	public void copy(Layer selectedLayer)
	{
		Rect bounds = selection.getBounds();
		left = bounds.left;
		top = bounds.top;
		Path path = new Path(selection.getPath());
		path.offset(-left, -top);
		
		bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.clipPath(path);
		canvas.drawBitmap(selectedLayer.getBitmap(), -left, -top, null);
	}
	
	public void paste()
	{
		Layer layer = image.newLayer(bitmap.getWidth(), bitmap.getHeight(), defaultLayerName);
		if(layer == null) return;
		layer.setX(left);
		layer.setY(top);
		layer.setBitmap(bitmap);
	}
	
	public boolean isEmpty()
	{
		return bitmap == null;
	}
}