import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MediaQuery
{
	
	// Image Constants
	private static final Integer ImageWidth = 352;
	private static final Integer ImageHeight = 288;
	
	// Member variables
	private static BufferedImage _searchImage;
	private static BufferedImage _queryImage;
	private static MediaSearchEngine _searchEngine;
	private static String _alphaImageFilePath;
	private static boolean[][] _alphaImage;
	
	// Input arguments
	private static String _queryImageFilePath;
	private static String _searchImageFilePath;
	
	/* Private Helper Methods */
	// Read in the values of the given file and store as pixels in given Buffered Image
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
					byte a = 0;
					Byte r = bytes[ind];
					Byte g = bytes[ind + height * width];
					Byte b = bytes[ind + height * width * 2];
					int pix = ((a & 0xff) << 24) | ((r & 0xff) << 16)
							| ((g & 0xff) << 8) | (b & 0xff);
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

	// Read in the values of the alpha file and store them in _alphaImage array
	private static void readAlphaFileImage(String filePath, int width, int height)
	{
		boolean isAlphaFile = true;
		
		if (filePath == null)
		{
			isAlphaFile = false;
		}
		else
		{
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
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						Byte a = bytes[ind];
						int alphaValue = 0;
						alphaValue = alphaValue | (a & 0xff);
						if (alphaValue == 1) {
							_alphaImage[x][y] = true;
						}
						else {
							_alphaImage[x][y] = false;
						}	
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
	
	// Return the query image's corresponding alpha file
	private static String parseAlphaFilePath(String imageFilePath) {
		String alphaFilePath = null;
		int endPos = imageFilePath.indexOf("_source");
		if ((endPos >= 0) && (endPos < imageFilePath.length())) {
			alphaFilePath = imageFilePath.substring(0, endPos);
			alphaFilePath += ".alpha";
		}
				
		return alphaFilePath;
	}
	

	
	/* Public methods */
	public static void main(String[] args) {
		// Check for expected number of input arguments
		if (args.length != 2) {
			System.out.println("Error: Invalid number of input arguments.");
			return;
		}
		
		// Parse input
		_queryImageFilePath = args[0];
		_searchImageFilePath = args[1];
		_alphaImageFilePath = parseAlphaFilePath(_queryImageFilePath);
		
		// Parse/validate query and search images
		_queryImage = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB);
		if (!readRGBFileImage(_queryImageFilePath, _queryImage, ImageWidth, ImageHeight)) {
			System.out.println("Exiting program due to invalid query image.");
			return;
		}
		_searchImage = new BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB);
		if (!readRGBFileImage(_searchImageFilePath, _searchImage, ImageWidth, ImageHeight)) {
			System.out.println("Exiting program due to invalid search image.");
			return;
		}
		
		_alphaImage = new boolean[ImageWidth][ImageHeight];
		readAlphaFileImage(_alphaImageFilePath, ImageWidth, ImageHeight);

		
		// Display query image
		JFrame queryImageFrame = new JFrame();
		JLabel queryImageLabel = new JLabel(new ImageIcon(_queryImage));
		queryImageFrame.getContentPane().add(queryImageLabel, BorderLayout.CENTER);
		queryImageFrame.setTitle("Query Image");
		queryImageFrame.setLocation(10, 0);
		queryImageFrame.pack();
		queryImageFrame.setVisible(true);
		queryImageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	    
		// Initialize image search engine		
		_searchEngine = new MediaSearchEngine(ImageWidth, ImageHeight, _searchImage);
		
		
		// Get HSV histogram for Query Image
		_searchEngine.createHSV(_queryImage, _alphaImage);
		
		
		// Search for image and output result
		_searchEngine.find();

		return;
	}
}