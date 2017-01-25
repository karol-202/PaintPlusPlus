package pl.karol202.paintplus.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.selection.Selection;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Image
{
	public interface OnImageChangeListener
	{
		void onImageChanged();
	}
	
	private final String DEFAULT_LAYER_NAME;
	
	public final int MAX_LAYERS = 10;
	public static final int FLIP_HORIZONTALLY = 0;
	public static final int FLIP_VERTICALLY = 1;
	
	private ArrayList<Layer> layers;
	private int selectedLayer;
	
	private OnImageChangeListener listener;
	private ColorsSet colorsSet;
	private Selection selection;
	private int width;
	private int height;
	
	private int viewX;
	private int viewY;
	private float zoom;
	private Matrix imageMatrix;
	private int viewportWidth;
	private int viewportHeight;
	
	public Image(Context context)
	{
		this.DEFAULT_LAYER_NAME = context.getString(R.string.new_layer_name);
		
		this.layers = new ArrayList<>();
		this.colorsSet = ColorsSet.getDefault();
		
		this.zoom = 1;
		this.imageMatrix = new Matrix();
		updateMatrix();
	}
	
	public void newImage(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		layers.clear();
		layers.add(new Layer(0, 0, width, height, DEFAULT_LAYER_NAME, colorsSet.getSecondColor()));
		selectedLayer = 0;
		
		selection = new Selection(width, height);
	}
	
	public void resize(int x, int y, int width, int height)
	{
		this.width = width;
		this.height = height;
		for(Layer layer : layers) layer.offset(-x, -y);
	}
	
	public void scale(int width, int height, boolean bilinear)
	{
		double scaleX = ((double) width / this.width);
		double scaleY = ((double) height / this.height);
		for(Layer layer : layers)
		{
			layer.scale(scaleX, scaleY, bilinear);
			layer.setX((int) (layer.getX() * scaleX));
			layer.setY((int) (layer.getY() * scaleY));
		}
		this.width = width;
		this.height = height;
	}
	
	public void flip(int direction)
	{
		/*Matrix matrix = new Matrix();
		matrix.preScale(direction == FLIP_HORIZONTALLY ? -1 : 1, direction == FLIP_VERTICALLY ? -1 : 1);
		
		Bitmap source = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
		editCanvas.drawBitmap(source, 0, 0, null);*/
	}
	
	public void rotate(float angle)
	{
		/*Matrix matrix = new Matrix();
		matrix.preRotate(angle);
		
		Bitmap source = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		newImage(source.getWidth(), source.getHeight());
		editCanvas.drawBitmap(source, 0, 0, null);*/
	}
	
	private void updateMatrix()
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(-viewX, -viewY);
		matrix.postScale(zoom, zoom);
		
		imageMatrix.set(matrix);
		if(listener != null) listener.onImageChanged();
	}
	
	public void updateImage()
	{
		if(listener != null) listener.onImageChanged();
	}
	
	public void centerView()
	{
		viewX = (int) (((width * zoom / 2) - (viewportWidth / 2)) / zoom);
		viewY = (int) (((height * zoom / 2) - (viewportHeight / 2)) / zoom);
		updateMatrix();
	}
	
	
	public Layer newLayer(int width, int height, String name)
	{
		if(layers.size() >= MAX_LAYERS) return null;
		Layer layer = new Layer(0, 0, width, height, name, Color.TRANSPARENT);
		layer.setImageChangeListener(listener);
		layers.add(0, layer);
		selectedLayer++;
		return layer;
	}
	
	public int getSelectedLayerIndex()
	{
		return selectedLayer;
	}
	
	public Layer getSelectedLayer()
	{
		if(layers.size() == 0) return null;
		return layers.get(selectedLayer);
	}
	
	public boolean isLayerSelected(Layer layer)
	{
		return layer == getSelectedLayer();
	}
	
	public void selectLayer(int index)
	{
		if(index < 0 || index >= layers.size()) throw new IllegalArgumentException("Invalid layer index.");
		selectedLayer = index;
		updateImage();
	}
	
	public void selectLayer(Layer layer)
	{
		if(!layers.contains(layer))
			throw new NoSuchElementException("Layer cannot be selected because it does not exist in the list.");
		selectedLayer = layers.indexOf(layer);
		updateImage();
	}
	
	public void deleteLayer(Layer layer)
	{
		if(!layers.contains(layer))
			throw new NoSuchElementException("Layer cannot be deleted because it does not exist in the list.");
		int index = layers.indexOf(layer);
		if(index <= selectedLayer && selectedLayer != 0) selectedLayer--;
		layers.remove(layer);
		if(layers.size() == 0) selection.selectNothing();
	}
	
	public String getDefaultLayerName()
	{
		return DEFAULT_LAYER_NAME;
	}
	
	public void setOnImageChangeListener(OnImageChangeListener listener)
	{
		this.listener = listener;
		for(Layer layer : layers) layer.setImageChangeListener(listener);
	}
	
	public ColorsSet getColorsSet()
	{
		return colorsSet;
	}
	
	public Selection getSelection()
	{
		return selection;
	}
	
	public ArrayList<Layer> getLayers()
	{
		return layers;
	}
	
	public Bitmap getSelectedBitmap()
	{
		Layer layer = getSelectedLayer();
		if(layer == null) return null;
		return layer.getBitmap();
	}
	
	public Canvas getSelectedCanvas()
	{
		Layer layer = getSelectedLayer();
		if(layer == null) return null;
		return layer.getEditCanvas();
	}
	
	public int getSelectedLayerX()
	{
		Layer selected = getSelectedLayer();
		return selected != null ? selected.getX() : 0;
	}
	
	public int getSelectedLayerY()
	{
		Layer selected = getSelectedLayer();
		return selected != null ? selected.getY() : 0;
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getViewX()
	{
		return viewX;
	}
	
	public int getViewY()
	{
		return viewY;
	}
	
	public void setViewX(int viewX)
	{
		this.viewX = viewX;
		updateMatrix();
	}
	
	public void setViewY(int viewY)
	{
		this.viewY = viewY;
		updateMatrix();
	}
	
	public float getZoom()
	{
		return zoom;
	}
	
	public void setZoom(float zoom)
	{
		this.viewX += ((viewportWidth / this.zoom) - (viewportWidth / zoom)) / 2;
		this.viewY += ((viewportHeight / this.zoom) - (viewportHeight / zoom)) / 2;
		
		this.zoom = zoom;
		updateMatrix();
	}
	
	public Matrix getImageMatrix()
	{
		return imageMatrix;
	}
	
	public int getViewportWidth()
	{
		return viewportWidth;
	}
	
	public void setViewportWidth(int viewportWidth)
	{
		this.viewportWidth = viewportWidth;
	}
	
	public int getViewportHeight()
	{
		return viewportHeight;
	}
	
	public void setViewportHeight(int viewportHeight)
	{
		this.viewportHeight = viewportHeight;
	}
}