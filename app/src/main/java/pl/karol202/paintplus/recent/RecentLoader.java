package pl.karol202.paintplus.recent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;
import com.google.firebase.crash.FirebaseCrash;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

class RecentLoader
{
	private final String FILENAME = "recent.dat";
	
	private Context context;
	private ArrayList<RecentImage> images;
	private XmlPullParser parser;
	private XmlSerializer serializer;
	
	RecentLoader(Context context)
	{
		this.context = context;
		this.images = new ArrayList<>();
	}
	
	ArrayList<RecentImage> load()
	{
		images.clear();
		
		try
		{
			InputStream inputStream = createInputStream();
			if(inputStream == null) return images;
			
			/*int r = inputStream.read();
			while(r != -1)
			{
				System.out.println((char) r);
				r = inputStream.read();
			}*/
			
			createParser(inputStream);
			readImages();
			
			inputStream.close();
		}
		catch(XmlPullParserException | IOException | ParseException e)
		{
			e.printStackTrace();
			FirebaseCrash.report(e);
		}
		return images;
	}
	
	private InputStream createInputStream() throws IOException
	{
		try
		{
			return new FileInputStream(getFile());
		}
		catch(FileNotFoundException e)
		{
			return null;
		}
	}
	
	private File getFile() throws IOException
	{
		File file = new File(context.getFilesDir(), FILENAME);
		file.createNewFile();
		return file;
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
		
		String path = readPath();
		Bitmap thumbnail = loadThumbnail(path);
		String name = readName();
		long date = readDate();
		if(thumbnail == null) return;
		
		images.add(new RecentImage(path, thumbnail, name, date));
	}
	
	private void checkTag() throws IOException, XmlPullParserException
	{
		parser.require(XmlPullParser.START_TAG, null, "image");
	}
	
	private String readPath() throws ParseException, IOException, XmlPullParserException
	{
		checkTag();
		
		String path = parser.getAttributeValue(null, "path");
		if(path == null || path.isEmpty()) throw new ParseException("Attribute not found: path", parser.getLineNumber());
		return path;
	}
	
	private Bitmap loadThumbnail(String path)
	{
		return BitmapFactory.decodeFile(path);
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
		System.out.println("u");
	}
	
	private OutputStream createOutputStream() throws IOException
	{
		return new FileOutputStream(getFile());
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
		serializer.attribute(null, "path", image.getPath());
		serializer.attribute(null, "name", image.getName());
		serializer.attribute(null, "date", String.valueOf(image.getDate()));
		serializer.endTag(null, "image");
	}
	
	void addOrUpdateRecentImage(RecentImage image)
	{
		if(!images.contains(image)) images.add(image);
		else
		{
			int indexOfExisting = images.indexOf(image);
			RecentImage existing = images.get(indexOfExisting);
			existing.setDate(image.getDate());
		}
		Collections.sort(images);
		Collections.reverse(images);
	}
	
	void removeRecentImage(int imageId)
	{
		images.remove(imageId);
	}
}