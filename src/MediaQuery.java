import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;

public class MediaQuery
{
	// Project Constants
	private static final String ProjectTitle = "CSCI 576: Final Project";
	private static final String QueryImageTitle = "Query Image:";
	private static final String SearchImageTitle = "Search Image:";
	private static final String[] InputArgumentNamesList = {
		"Query image file path: ",
		"Search image file path: "
	};
	
	// Image Constants
	private static final Integer ImageWidth = 352;
	private static final Integer ImageHeight = 288;
	
	// Member variables
	private static DoubleImageDisplay _display;
	private static BufferedImage _searchImage;
	private static BufferedImage _queryImage;
	private static MediaSearchEngine _searchEngine;
	private static Rectangle _searchResult;
	private static String _alphaImageFilePath;
	private static boolean[][] _alphaImage;
	
	// Input arguments
	private static String _queryImageFilePath;
	private static String _searchImageFilePath;
	private static String[] _inputArgumentsList;
	
	/* Private Helper Methods */
	private static boolean readRGBFileImage(String filePath, BufferedImage image, int width, int height)
	{
		boolean isValidImage = false;
		File file = null;
		InputStream is = null;
		try 
		{
			// Read file as byte array
			file = new File(filePath);
			is = new FileInputStream(file);
			long length = file.length();
			byte[] bytes = new byte[(int)length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) 
			{
				offset += numRead;
			}
	
			// Parse byte array to BufferImage and image pixel array
			int ind = 0;
			for (int y = 0; y < height; y++) 
			{
				for (int x = 0; x < width; x++)
				{
					// byte a = 0;
					Byte r = bytes[ind];
					Byte g = bytes[ind + height * width];
					Byte b = bytes[ind + height * width * 2];
					int pix = 0xff000000 | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);
					// int pix = ((a << 24) + (r << 16) + (g << 8) + b);
					image.setRGB(x, y, pix);
					
					ind++;
				}
			}
			
			is.close();
			isValidImage = true;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return isValidImage;
	}

	private static void readAlphaFileImage(String filePath, int width, int height)
	{
		boolean isAlphaFile = true;
		
		if (filePath == null)
		{
			isAlphaFile = false;
		}
		
		File file = null;
		InputStream is = null;
		try
		{
			// Read file as byte array
			file = new File(filePath);
			is = new FileInputStream(file);
			long length = file.length();
			byte[] bytes = new byte[(int)length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) 
			{
				offset += numRead;
			}
	
			// Parse byte array to BufferImage and image pixel array
			int ind = 0;
			for (int y = 0; y < height; y++) 
			{
				for (int x = 0; x < width; x++)
				{
					Byte a = bytes[ind];
					int alphaValue = 0x00000000 | a;
					// System.out.println("Alpha value: " + alphaValue);
					_alphaImage[x][y] = (alphaValue == 1);
					
					ind++;
				}
			}
			
			is.close();
		}
		catch (FileNotFoundException e) 
		{
			isAlphaFile = false;
		} 
		catch (IOException e) 
		{
			isAlphaFile = false;
		}
		
		if (!isAlphaFile)
		{
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					_alphaImage[x][y] = true;
				}
			}
		}
	}
	
	private static String parseAlphaFilePath(String imageFilePath)
	{
		String alphaFilePath = null;
		int endPos = imageFilePath.indexOf("_source");
		if ((endPos >= 0) && (endPos < imageFilePath.length()))
		{
			alphaFilePath = imageFilePath.substring(0, endPos);
			alphaFilePath += ".alpha";
		}
		
		System.out.println("Alpha file path: " + alphaFilePath);
		
		return alphaFilePath;
	}
	
	/* Public methods */
	public static void main(String[] args)
	{
		// Check for expected number of input arguments
		if (args.length != 2)
		{
			System.out.println("Error: Invalid number of input arguments.");
			return;
		}
		
		// Parse input
		_queryImageFilePath = args[0];
		_searchImageFilePath = args[1];
		_inputArgumentsList = new String[]{InputArgumentNamesList[0] + _queryImageFilePath, InputArgumentNamesList[1] + _searchImageFilePath};
		_alphaImageFilePath = parseAlphaFilePath(_queryImageFilePath);
		
		// Parse/validate query and search images
		_queryImage = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB);
		if (!readRGBFileImage(_queryImageFilePath, _queryImage, ImageWidth, ImageHeight))
		{
			System.out.println("Exiting program due to invalid query image.");
			return;
		}
		_searchImage = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB);
		if (!readRGBFileImage(_searchImageFilePath, _searchImage, ImageWidth, ImageHeight))
		{
			System.out.println("Exiting program due to invalid search image.");
			return;
		}
		
		_alphaImage = new boolean[ImageWidth][ImageHeight];
		readAlphaFileImage(_alphaImageFilePath, ImageWidth, ImageHeight);
		
		// Initialize program output display and show query and search images
		_display = new DoubleImageDisplay(ProjectTitle, QueryImageTitle, SearchImageTitle, _inputArgumentsList);
		_display.setFirstImage(_queryImage);
		_display.setSecondImage(_searchImage);
		
		// Initialize image search engine
		_searchEngine = new MediaSearchEngine(ImageWidth, ImageHeight, _searchImage);
		
		// Search for image and output result
		_searchResult = _searchEngine.search(_queryImage, _alphaImage);
		_display.displaySearchResult(_searchResult);
		if (_searchResult == null)
		{
			System.out.println("No");
		}
		else
		{
			System.out.println("Yes");
		}
	}
}