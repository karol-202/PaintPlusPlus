package pl.karol202.paintplus.file;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UriUtils
{
	public static ParcelFileDescriptor createFileOpenDescriptor(Context context, Uri uri)
	{
		return createFileDescriptor(context, uri, "r");
	}
	
	public static ParcelFileDescriptor createFileSaveDescriptor(Context context, Uri uri)
	{
		return createFileDescriptor(context, uri, "w");
	}
	
	private static ParcelFileDescriptor createFileDescriptor(Context context, Uri uri, String mode)
	{
		try
		{
			return context.getContentResolver().openFileDescriptor(uri, mode);
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void closeFileDescriptor(ParcelFileDescriptor fileDescriptor)
	{
		if(fileDescriptor == null) return;
		try
		{
			fileDescriptor.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}