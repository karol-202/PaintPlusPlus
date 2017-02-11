package pl.karol202.paintplus.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;
import pl.karol202.paintplus.tool.selection.Selection.OnSelectionChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class Image
{
	public interface OnImageChangeListener
	{
		void onImageChanged();
		
		void onLayersChanged();
	}
	
	private final String DEFAULT_LAYER_NAME;
	
	public static final int MAX_LAYERS = 8;
	public static final int FLIP_HORIZONTALLY = 0;
	public static final int FLIP_VERTICALLY = 1;
	
	private ArrayList<Layer> layers;
	private int selectedLayer;
	private int width;
	private int height;
	
	private OnImageChangeListener listener;
	private ColorsSet colorsSet;
	private Selection selection;
	private Clipboard clipboard;
	
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
		this.selection = new Selection();
		this.clipboard = new Clipboard(this, context.getString(R.string.pasted_layer));
		
		this.zoom = 1;
		this.imageMatrix = new Matrix();
		updateMatrix();
	}
	
	public void newImage(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		layers.clear();
		Layer layer = new Layer(0, 0, width, height, DEFAULT_LAYER_NAME, colorsSet.getSecondColor());
		layer.setImageChangeListener(listener);
		layers.add(layer);
		selectedLayer = 0;
		updateLayersPreview();
		
		selection.init(width, height);
	}
	
	public void openImage(Bitmap bitmap)
	{
		newImage(bitmap.getWidth(), bitmap.getHeight());
		getSelectedLayer().setBitmap(bitmap);
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
			layer.setX((int) Math.round(layer.getX() * scaleX));
			layer.setY((int) Math.round(layer.getY() * scaleY));
		}
		this.width = width;
		this.height = height;
	}
	
	public void flip(int direction)
	{
		for(Layer layer : layers)
		{
			layer.flip(direction);
			if(direction == FLIP_HORIZONTALLY) layer.setX(width - layer.getX() - layer.getWidth());
			else layer.setY(height - layer.getY() - layer.getHeight());
		}
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
	
	public void updateLayersPreview()
	{
		if(listener != null) listener.onLayersChanged();
	}
	
	public void centerView()
	{
		viewX = (int) (((width * zoom / 2) - (viewportWidth / 2)) / zoom);
		viewY = (int) (((height * zoom / 2) - (viewportHeight / 2)) / zoom);
		updateMatrix();
	}
	
	public Bitmap getFullImage()
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		ArrayList<Layer> reversed = new ArrayList<>(layers);
		Collections.reverse(reversed);
		for(Layer layer : reversed)
			if(layer.isVisible()) layer.drawLayer(bitmap);
		return bitmap;
	}
	
	
	public Layer newLayer(int width, int height, String name)
	{
		if(layers.size() >= MAX_LAYERS) return null;
		Layer layer = new Layer(0, 0, width, height, name, Color.TRANSPARENT);
		layer.setImageChangeListener(listener);
		layers.add(0, layer);
		selectLayer(0);
		updateLayersPreview();
		return layer;
	}
	
	public boolean addLayer(Layer layer, int index)
	{
		if(layers.size() >= MAX_LAYERS) return false;
		layer.setImageChangeListener(listener);
		layers.add(index, layer);
		selectLayer(index);
		updateLayersPreview();
		return true;
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
		updateLayersPreview();
	}
	
	public void selectLayer(Layer layer)
	{
		if(!layers.contains(layer))
			throw new NoSuchElementException("Layer cannot be selected because it does not exist in the list.");
		selectedLayer = layers.indexOf(layer);
		updateImage();
		updateLayersPreview();
	}
	
	public void deleteLayer(Layer layer)
	{
		if(!layers.contains(layer))
			throw new NoSuchElementException("Layer cannot be deleted because it does not exist in the list.");
		int index = layers.indexOf(layer);
		if(index <= selectedLayer && selectedLayer != 0) selectedLayer--;
		layers.remove(layer);
		if(layers.size() == 0) selection.selectNothing();
		updateLayersPreview();
	}
	
	public String getDefaultLayerName()
	{
		return DEFAULT_LAYER_NAME;
	}
	
	public ArrayList<Layer> getLayers()
	{
		return layers;
	}
	
	public int getLayersAmount()
	{
		return layers.size();
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
	
	
	public void cut()
	{
		if(selection.isEmpty()) return;
		clipboard.cut(getSelectedLayer());
	}
	
	public void copy()
	{
		if(selection.isEmpty()) return;
		clipboard.copy(getSelectedLayer());
	}
	
	public void paste()
	{
		if(clipboard.isEmpty()) return;
		clipboard.paste();
	}
	
	public void setOnImageChangeListener(OnImageChangeListener listener)
	{
		this.listener = listener;
		for(Layer layer : layers) layer.setImageChangeListener(listener);
	}
	
	public void addOnSelectionChangeListener(OnSelectionChangeListener listener)
	{
		selection.addListener(listener);
	}
	
	public ColorsSet getColorsSet()
	{
		return colorsSet;
	}
	
	public Selection getSelection()
	{
		return selection;
	}
	
	public void selectAll()
	{
		selection.selectAll();
	}
	
	public void selectNothing()
	{
		selection.selectNothing();
	}
	
	public void revertSelection()
	{
		selection.revert();
	}
	
	public Clipboard getClipboard()
	{
		return clipboard;
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