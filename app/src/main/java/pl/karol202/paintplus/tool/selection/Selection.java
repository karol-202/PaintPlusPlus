package pl.karol202.paintplus.tool.selection;

import android.graphics.Path;
import android.graphics.Rect;

public class Selection
{
	private static class SelectionChunk
	{
		private enum PixelCode
		{
			FIRST  (0b10000000),
			SECOND (0b01000000),
			THIRD  (0b00100000),
			FOURTH (0b00010000),
			FIFTH  (0b00001000),
			SIXTH  (0b00000100),
			SEVENTH(0b00000010),
			EIGHTH (0b00000001);
			
			private int code;
			
			PixelCode(int code)
			{
				this.code = code;
			}
			
			public int getCode()
			{
				return code;
			}
		}
		
		private byte chunk;
		
		public SelectionChunk()
		{
			this.chunk = 0;
		}
		
		public boolean getPixel(short pixel)
		{
			return (chunk & pixel) != 0;
		}
		
		public void setPixel(short pixel, boolean value)
		{
			if(value) chunk |= pixel;
			else chunk &= (~pixel);
		}
		
		public short getPixelCode(int pixel)
		{
			if(pixel < 0 || pixel >= 8) throw new IllegalArgumentException("Invalid pixel of chunk: " + pixel);
			return (short) PixelCode.values()[pixel].getCode();
		}
	}
	
	private int width;
	private int height;
	private Rect imageRect;
	private SelectionChunk[] chunks;
	
	private Path path;
	
	public Selection(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.imageRect = new Rect(0, 0, width - 1, height - 1);
		this.chunks = new SelectionChunk[getChunksAmount()];
		clear();
		
		this.path = new Path();
		updatePath();
	}
	
	private void clear()
	{
		for(int i = 0; i < getChunksAmount(); i++) chunks[i] = new SelectionChunk();
	}
	
	private int getChunksAmount()
	{
		return width * height / 8;
	}
	
	public boolean getPixel(int x, int y)
	{
		if(x < 0 || x >= width || y < 0 || y >= height)
			throw new IllegalArgumentException("Invalid coordinates of pixel: " + x + ", " + y);
		int pixelIndex = y * width + x;
		int chunkIndex = (int) Math.floor(pixelIndex / 8d);
		int pixelOfChunk = pixelIndex - (chunkIndex * 8);
		SelectionChunk chunk = chunks[chunkIndex];
		short pixelCode = chunk.getPixelCode(pixelOfChunk);
		return chunk.getPixel(pixelCode);
	}
	
	public void setPixel(int x, int y, boolean value)
	{
		if(x < 0 || x >= width || y < 0 || y >= height)
			throw new IllegalArgumentException("Invalid coordinates of pixel: " + x + ", " + y);
		int pixelIndex = y * width + x;
		int chunkIndex = (int) Math.floor(pixelIndex / 8d);
		int pixelOfChunk = pixelIndex - (chunkIndex * 8);
		SelectionChunk chunk = chunks[chunkIndex];
		short pixelCode = chunk.getPixelCode(pixelOfChunk);
		chunk.setPixel(pixelCode, value);
	}
	
	public void replace(Rect rect)
	{
		clear();
		add(rect);
	}
	
	public void add(Rect rect)
	{
		rect.intersect(imageRect);
		for(int x = rect.left; x <= rect.right; x++)
			for(int y = rect.top; y <= rect.bottom; y++)
				setPixel(x, y, true);
		updatePath();
	}
	
	public void subtract(Rect rect)
	{
		rect.intersect(imageRect);
		for(int x = rect.left; x <= rect.right; x++)
			for(int y = rect.top; y <= rect.bottom; y++)
				setPixel(x, y, false);
		updatePath();
	}
	
	public void multiply(Rect rect)
	{
		rect.intersect(imageRect);
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				if(!rect.contains(x, y))
					setPixel(x, y, false);
		updatePath();
	}
	
	private void updatePath()
	{
		path.reset();
		
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				if(!getPixel(x, y)) continue;
				if(!getPixel(x - 1, y))
				{
					path.moveTo(x, y);
					path.lineTo(x, y + 1);
				}
				if(!getPixel(x, y - 1))
				{
					path.moveTo(x, y);
					path.lineTo(x + 1, y);
				}
				if(!getPixel(x + 1, y))
				{
					path.moveTo(x + 1, y);
					path.lineTo(x + 1, y + 1);
				}
				if(!getPixel(x, y + 1))
				{
					path.moveTo(x, y + 1);
					path.lineTo(x + 1, y + 1);
				}
			}
	}
	
	public Path getPath()
	{
		return path;
	}
}