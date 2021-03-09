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

package pl.karol202.paintplus.image;

import android.content.Context;
import android.graphics.*;
import android.net.Uri;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.file.BitmapSaveFormat;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.history.History;
import pl.karol202.paintplus.history.OnHistoryUpdateListener;
import pl.karol202.paintplus.history.action.Action;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;
import pl.karol202.paintplus.tool.selection.Selection.OnSelectionChangeListener;
import pl.karol202.paintplus.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class Image
{
	public interface OnImageChangeListener
	{
		void onImageChanged();

		void onLayersChanged();

		void onImageMatrixChanged();
	}

	public enum FlipDirection
	{
		HORIZONTALLY, VERTICALLY
	}

	public enum RotationAmount
	{
		ANGLE_90(90), ANGLE_180(180), ANGLE_270(270);

		private float angle;

		RotationAmount(float angle)
		{
			this.angle = angle;
		}

		float getAngle()
		{
			return angle;
		}
	}

	public static final int MAX_LAYERS = 8;
	public static final float MIN_ZOOM = 0.009f;
	public static final float MAX_ZOOM = 16f;

	private final String DEFAULT_LAYER_NAME;
	private final String FLATTENED_LAYER_NAME;
	public final float SCREEN_DENSITY;

	private int width;
	private int height;
	private ArrayList<Layer> layers;
	private int selectedLayer;

	private OnImageChangeListener listener;
	private ColorsSet colorsSet;
	private Selection selection;
	private Clipboard clipboard;
	private HelpersManager helpersManager;
	private History history;

	private float viewX;
	private float viewY;
	private float zoom;
	private Matrix imageMatrix;
	private int viewportWidth;
	private int viewportHeight;
	private boolean layersLocked;
	private Uri lastUri;
	private BitmapSaveFormat lastFormat;
	private boolean changedSinceSave;

	public Image(Context context)
	{
		this.DEFAULT_LAYER_NAME = context.getString(R.string.new_layer_name);
		this.FLATTENED_LAYER_NAME = context.getString(R.string.flattened);
		this.SCREEN_DENSITY = context.getResources().getDisplayMetrics().density;

		this.layers = new ArrayList<>();
		this.colorsSet = ColorsSet.getDefault();
		this.selection = new Selection(this);
		this.clipboard = new Clipboard(this, context.getString(R.string.pasted_layer));
		this.helpersManager = new HelpersManager(this, context.getResources());
		this.history = new History();

		this.zoom = 1;
		this.imageMatrix = new Matrix();
		this.layersLocked = false;
		updateMatrix();
	}

	public void newImage(int width, int height)
	{
		this.width = width;
		this.height = height;
		lastUri = null;
		lastFormat = null;
		changedSinceSave = false;

		layers.clear();
		Layer layer = new Layer(0, 0, DEFAULT_LAYER_NAME, width, height, colorsSet.getSecondColor());
		layer.setImageChangeListener(listener);
		layers.add(layer);
		selectedLayer = 0;
		updateLayersPreview();

		selection.init(width, height);
		history.clear();
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
		this.viewX -= x;
		this.viewY -= y;

		for(Layer layer : layers) layer.offset(-x, -y);
		selection.offsetSelection(-x, -y);
		updateMatrix();
	}

	public void scale(int width, int height, boolean bilinear)
	{
		double scaleX = ((double) width / this.width);
		double scaleY = ((double) height / this.height);
		for(Layer layer : layers)
		{
			layer.scale(scaleX, scaleY, bilinear);
			layer.setPosition((int) Math.round(layer.getX() * scaleX), (int) Math.round(layer.getY() * scaleY));
		}
		this.width = width;
		this.height = height;
		updateMatrix();
	}

	public void flip(FlipDirection direction)
	{
		for(Layer layer : layers)
		{
			layer.flip(direction);
			if(direction == FlipDirection.HORIZONTALLY)
				layer.setPosition(width - layer.getX() - layer.getWidth(), layer.getY());
			else
				layer.setPosition(layer.getX(), height - layer.getY() - layer.getHeight());
		}
	}

	public void rotate(RotationAmount angle)
	{
		for(Layer layer : layers)
		{
			if(angle == RotationAmount.ANGLE_90)
				layer.setPosition(height - layer.getY() - layer.getHeight(), layer.getX());
			else if(angle == RotationAmount.ANGLE_180)
				layer.setPosition(width - layer.getX() - layer.getWidth(), height - layer.getY() - layer.getHeight());
			else if(angle == RotationAmount.ANGLE_270)
				layer.setPosition(layer.getY(), width - layer.getX() - layer.getWidth());
			layer.rotate(angle.getAngle(), false);
		}

		if(angle == RotationAmount.ANGLE_90 || angle == RotationAmount.ANGLE_270)
		{
			int temp = width;
			width = height;
			height = temp;
		}

		updateMatrix();
	}

	private void updateMatrix()
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(-viewX, -viewY);
		matrix.postScale(zoom, zoom);

		imageMatrix.set(matrix);
		if(listener != null) listener.onImageMatrixChanged();
	}

	public void updateImage()
	{
		if(listener != null) listener.onImageChanged();
	}

	private void updateLayersPreview()
	{
		if(listener != null) listener.onLayersChanged();
	}

	public void centerView()
	{
		viewX = ((width * zoom / 2) - (viewportWidth / 2f)) / zoom;
		viewY = ((height * zoom / 2) - (viewportHeight / 2f)) / zoom;
		updateMatrix();
	}

	public Bitmap getFullImage()
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Matrix matrix = new Matrix();

		ArrayList<Layer> reversed = new ArrayList<>(layers);
		Collections.reverse(reversed);
		for(Layer layer : reversed)
			if(layer.isVisible()) bitmap = layer.drawLayerAndReturnBitmap(bitmap, canvas, null, matrix);
		return bitmap;
	}

	public void flattenImage()
	{
		Rect resultBounds = new Rect();
		for(Layer layer : layers) resultBounds.union(layer.getBounds());

		Matrix matrix = new Matrix();
		matrix.preTranslate(-resultBounds.left, -resultBounds.top);

		Bitmap bitmap = Bitmap.createBitmap(resultBounds.width(), resultBounds.height(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		for(int i = layers.size() - 1; i >= 0; i--)
		{
			Layer layer = layers.get(i);
			bitmap = layer.drawLayerAndReturnBitmap(bitmap, canvas, null, matrix);
		}

		Layer resultLayer = new Layer(resultBounds.left, resultBounds.top, FLATTENED_LAYER_NAME,
		                              resultBounds.width(), resultBounds.height(), Color.TRANSPARENT);
		resultLayer.setBitmap(bitmap);

		layers.clear();
		layers.add(resultLayer);
		selectedLayer = 0;

		updateImage();
		updateLayersPreview();
	}


	public Layer newLayer(int width, int height, String name)
	{
		if(layers.size() >= MAX_LAYERS) return null;
		Layer layer = new Layer(0, 0, name, width, height, Color.TRANSPARENT);
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

	public int getLayerIndex(Layer layer)
	{
		return layers.indexOf(layer);
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
		updateLayersPreview();
	}

	public void deleteAllLayers()
	{
		layers.clear();
		updateLayersPreview();
	}

	public String getDefaultLayerName()
	{
		return DEFAULT_LAYER_NAME;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public ArrayList<Layer> getLayers()
	{
		return layers;
	}

	public int getLayersAmount()
	{
		return layers.size();
	}

	public Layer getLayerAtIndex(int layerIndex)
	{
		return layers.get(layerIndex);
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

	public boolean canUndo()
	{
		return history.canUndo();
	}

	public boolean canRedo()
	{
		return history.canRedo();
	}

	public void undo()
	{
		if(!history.canUndo()) return;
		history.undo(this);
	}

	public void redo()
	{
		if(!history.canRedo()) return;
		history.redo(this);
	}

	public void addHistoryAction(Action action)
	{
		history.addAction(action);
		changedSinceSave = true;
	}

	public void setOnImageChangeListener(OnImageChangeListener listener)
	{
		this.listener = listener;
		for(Layer layer : layers) layer.setImageChangeListener(listener);
	}

	// Can be changed to setOnSelectionChangeListener
	public void addOnSelectionChangeListener(OnSelectionChangeListener listener)
	{
		selection.addListener(listener);
	}

	public void setOnHistoryUpdateListener(OnHistoryUpdateListener listener)
	{
		history.setHistoryUpdateListener(listener);
	}

	public ColorsSet getColorsSet()
	{
		return colorsSet;
	}

	public Selection getSelection()
	{
		return selection;
	}

	public Clipboard getClipboard()
	{
		return clipboard;
	}

	public HelpersManager getHelpersManager()
	{
		return helpersManager;
	}

	public History getHistory()
	{
		return history;
	}

	public float getViewX()
	{
		return viewX;
	}

	public float getViewY()
	{
		return viewY;
	}

	public void setViewX(float viewX)
	{
		this.viewX = viewX;
		updateMatrix();
	}

	public void setViewY(float viewY)
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
		setZoom(zoom, viewportWidth / 2, viewportHeight / 2);
	}

	public void setZoom(float zoom, float focusX, float focusY)
	{
		float focusXInImage = Utils.map(focusX, -viewX * this.zoom, (-viewX + width) * this.zoom, 0, 1);
		float focusYInImage = Utils.map(focusY, -viewY * this.zoom, (-viewY + height) * this.zoom, 0, 1);
		float offsetXLeft = (viewX * (this.zoom / zoom)) - viewX;
		float offsetYTop = (viewY * (this.zoom / zoom)) - viewY;
		float offsetXRight = (((viewX * this.zoom) + ((width * zoom) - (width * this.zoom))) / zoom) - viewX;
		float offsetYBottom = (((viewY * this.zoom) + ((height * zoom) - (height * this.zoom))) / zoom) - viewY;
		viewX += Utils.lerp(focusXInImage, offsetXLeft, offsetXRight);
		viewY += Utils.lerp(focusYInImage, offsetYTop, offsetYBottom);

		this.zoom = zoom;
		updateMatrix();
	}

	public Matrix getImageMatrix()
	{
		return imageMatrix;
	}

	public RectF getImageRect()
	{
		float viewX = -getViewX() * zoom;
		float viewY = -getViewY() * zoom;
		float width = this.width * zoom;
		float height = this.height * zoom;
		return new RectF(viewX, viewY, viewX + width, viewY + height);
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

	public boolean areLayersLocked()
	{
		return layersLocked;
	}

	public void lockLayers()
	{
		this.layersLocked = true;
	}

	public void unlockLayers()
	{
		this.layersLocked = false;
	}

	public Uri getLastUri()
	{
		return lastUri;
	}

	public void setLastUri(Uri lastUri)
	{
		this.lastUri = lastUri;
	}

	public BitmapSaveFormat getLastFormat()
	{
		return lastFormat;
	}

	public void setLastFormat(BitmapSaveFormat lastFormat)
	{
		this.lastFormat = lastFormat;
	}

	public boolean wasModifiedSinceLastSave()
	{
		return changedSinceSave;
	}

	public void notifySave()
	{
		changedSinceSave = false;
	}
}
