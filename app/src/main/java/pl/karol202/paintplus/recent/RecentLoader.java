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

package pl.karol202.paintplus.recent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import pl.karol202.paintplus.ErrorHandler;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.file.UriUtils;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RecentLoader
{
	private final String FILENAME = "recent.dat";
	
	private Context context;
	private List<RecentImage> images;
	private XmlPullParser parser;
	private XmlSerializer serializer;
	
	RecentLoader(Context context)
	{
		this.context = context;
		this.images = new ArrayList<>();
	}
	
	void load()
	{
		images.clear();
		
		try
		{
			InputStream inputStream = createInputStream();
			if(inputStream == null) return;
			
			createParser(inputStream);
			readImages();
			
			inputStream.close();
		}
		catch(XmlPullParserException | IOException | ParseException e)
		{
			ErrorHandler.report(e);
		}
	}
	
	private InputStream createInputStream() throws IOException
	{
		try
		{
			File file = getFile(false);
			if(file == null) return null;
			return new FileInputStream(file);
		}
		catch(FileNotFoundException e)
		{
			return null;
		}
	}
	
	private File getFile(boolean createIfNotExisting) throws IOException
	{
		File file = new File(context.getFilesDir(), FILENAME);
		
		if(createIfNotExisting)
		{
			file.createNewFile();
			return file;
		}
		else return file.exists() ? file : null;
	}
	
	private void createParser(InputStream inputStream) throws XmlPullParserException, IOException, ParseException
	{
		parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(inputStream, null);
		parser.nextTag();
	}
	
	private void readImages() throws IOException, XmlPullParserException, ParseException
	{
		parser.require(XmlPullParser.START_TAG, null, "images");
		while(parser.next() != XmlPullParser.END_DOCUMENT)
		{
			if(parser.getEventType() != XmlPullParser.START_TAG) continue;
			String name = parser.getName();
			if(!name.equals("image")) throw new ParseException("Unexpected tag in images: " + name, parser.getLineNumber());
			readImage();
		}
	}
	
	private void readImage() throws IOException, XmlPullParserException, ParseException
	{
		checkTag();
		
		Uri uri = readUri();
		Uri thumbnailUri = readThumbnailUri();
		Bitmap thumbnail = loadThumbnail(thumbnailUri);
		String name = readName();
		long date = readDate();
		if(!checkIfImageExists(uri) || thumbnail == null) return;
		
		images.add(new RecentImage(uri, thumbnailUri, thumbnail, name, date));
	}
	
	private void checkTag() throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "image");
	}
	
	private Uri readUri() throws ParseException, IOException, XmlPullParserException
	{
		checkTag();
		
		String uriString = parser.getAttributeValue(null, "path");
		if(uriString == null || uriString.isEmpty()) throw new ParseException("Attribute not found: path", parser.getLineNumber());
		return Uri.parse(uriString);
	}
	
	private Uri readThumbnailUri() throws ParseException, IOException, XmlPullParserException
	{
		checkTag();
		
		String uriString = parser.getAttributeValue(null, "thumbnailPath");
		if(uriString == null || uriString.isEmpty()) throw new ParseException("Attribute not found: thumbnailPath", parser.getLineNumber());
		return Uri.parse(uriString);
	}
	
	private Bitmap loadThumbnail(Uri uri)
	{
		ParcelFileDescriptor fileDescriptor = UriUtils.createFileOpenDescriptor(context, uri);
		if(fileDescriptor == null) return null;
		Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor());
		UriUtils.closeFileDescriptor(fileDescriptor);
		return bitmap;
	}
	
	private String readName() throws IOException, XmlPullParserException, ParseException
	{
		checkTag();
		
		String name = parser.getAttributeValue(null, "name");
		if(name == null || name.isEmpty()) throw new ParseException("Attribute not found: name", parser.getLineNumber());
		return name;
	}
	
	private long readDate() throws IOException, XmlPullParserException, ParseException
	{
		checkTag();
		
		String dateString = parser.getAttributeValue(null, "date");
		if(dateString == null || dateString.isEmpty())
			throw new ParseException("Attribute not found: date", parser.getLineNumber());
		return Long.parseLong(dateString);
	}
	
	private boolean checkIfImageExists(Uri uri)
	{
		ParcelFileDescriptor fileDescriptor = UriUtils.createFileOpenDescriptor(context, uri);
		return fileDescriptor != null;
	}
	
	void save()
	{
		try
		{
			OutputStream outputStream = createOutputStream();
			createSerializer(outputStream);
			saveImages();
			
			outputStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		load();
	}
	
	private OutputStream createOutputStream() throws IOException
	{
		return new FileOutputStream(getFile(true));
	}
	
	private void createSerializer(OutputStream outputStream) throws IOException
	{
		serializer = Xml.newSerializer();
		serializer.setOutput(outputStream, "UTF-8");
	}
	
	private void saveImages() throws IOException
	{
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "images");
		for(RecentImage image : images) saveImage(image);
		serializer.endTag(null, "images");
		serializer.endDocument();
	}
	
	private void saveImage(RecentImage image) throws IOException
	{
		serializer.startTag(null, "image");
		serializer.attribute(null, "path", image.getUri().toString());
		serializer.attribute(null, "thumbnailPath", saveThumbnailAndGetPath(image).toString());
		serializer.attribute(null, "name", image.getName());
		serializer.attribute(null, "date", String.valueOf(image.getDate()));
		serializer.endTag(null, "image");
	}
	
	private Uri saveThumbnailAndGetPath(RecentImage image) throws IOException
	{
		if(image.getThumbnailUri() != null) return image.getThumbnailUri();
		
		String fileName = String.format("_%s", image.getName());
		File file = new File(context.getFilesDir(), fileName);
		Bitmap bitmap = image.getThumbnail();
		
		Uri uri = Uri.fromFile(file);
		ParcelFileDescriptor fileDescriptor = UriUtils.createFileSaveDescriptor(context, uri);
		ImageLoader.saveBitmap(bitmap, fileDescriptor.getFileDescriptor(), fileName, 70);
		fileDescriptor.close();
		return uri;
	}
	
	void addOrUpdateRecentImage(RecentImage image)
	{
		if(!images.contains(image)) images.add(image);
		else
		{
			//Find old entry about the same file and update it.
			int indexOfExisting = images.indexOf(image);
			RecentImage existing = images.get(indexOfExisting);
			existing.setThumbnailUri(null);
			existing.setThumbnail(image.getThumbnail());
			existing.setDate(image.getDate());
		}
		Collections.sort(images);
	}
	
	void removeRecentImage(int imageId)
	{
		images.remove(imageId);
	}
	
	List<RecentImage> getImages()
	{
		return images;
	}
	
	int getImagesAmount()
	{
		return images.size();
	}
}