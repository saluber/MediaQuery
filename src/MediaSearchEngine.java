import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MediaSearchEngine
{
	private int _width, _height;
	private ArrayList<BufferedImage> _searchImages;
	private ArrayList<Double[][][]> _hsvSearchImages;
	Double[][][] _hsvQueryImage;
	
	public MediaSearchEngine(int width, int height, BufferedImage searchImage)
	{
		_width = width;
		_height = height;
		_searchImages = new ArrayList<BufferedImage>();
		_searchImages.add(searchImage);
		_hsvSearchImages = new ArrayList<Double[][][]>();
		
		// Convert search images to HSV
		System.out.println("Converting search images to HSV!");
		for (int i = 0; i < _searchImages.size(); i++)
		{
			BufferedImage image = _searchImages.get(i);
			Double[][][] hsvImage = new Double[image.getWidth()][image.getHeight()][3];

			for (int y = 0; y < image.getHeight(); y++)
			{
				for (int x = 0; x < image.getWidth(); x++)
				{
					int pixel = image.getRGB(x, y);
					int r = (pixel >> 16) & 0xFF;
					int g = (pixel >> 8) & 0xFF;
					int b = (pixel & 0xFF);
					
					Double[] hsv = new Double[3];
					rgbToHSV((double)r, (double)g, (double)b, hsv);
					
					hsvImage[x][y][0] = hsv[0];
					hsvImage[x][y][1] = hsv[1];
					hsvImage[x][y][2] = hsv[2];
					
					System.out.println("Adding pixel: (" + x + ", " + y + ") with hsv value: [" +  hsvImage[x][y][0] + "," + hsvImage[x][y][1] + "," + hsvImage[x][y][2] + "]");
				}
			}
			
			_hsvSearchImages.add(hsvImage);
		}
	}
	
	public MediaSearchEngine(int width, int height, ArrayList<BufferedImage> searchImages)
	{
		_width = width;
		_height = height;
		_searchImages = searchImages;
	}
	
	// Searches images for query image and returns rectangle of match region (if found)
	public Rectangle search(BufferedImage query)
	{
		Rectangle result = new Rectangle(10, 10, 100, 100);
		
		// Convert query image to HSV
		System.out.println("Converting query image to HSV");
		
		_hsvQueryImage = new Double[query.getWidth()][query.getHeight()][3];
		for (int y = 0; y < query.getHeight(); y++)
		{
			for (int x = 0; x < query.getWidth(); x++)
			{
				int pixel = query.getRGB(x, y);
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = pixel & 0xFF;
				
				Double[] hsv = new Double[3];
				rgbToHSV((double)r, (double)g, (double)b, hsv);
				
				_hsvQueryImage[x][y][0] = hsv[0];
				_hsvQueryImage[x][y][1] = hsv[1];
				_hsvQueryImage[x][y][2] = hsv[2];
				
				System.out.println("Adding pixel: (" + x + ", " + y + ") with hsv value: [" +  _hsvQueryImage[x][y][0] + "," + _hsvQueryImage[x][y][1] + "," + _hsvQueryImage[x][y][2] + "]");
			}
		}
		
		return result;
	}
	
	// RGB to HSV Conversion
	private void rgbToHSV(double r, double g, double b, Double[] hsv)
	{
		if (r > 255)
		{
			r = 255.0;
		}
		else if (r < 0)
		{
			r = 0.0;
		}
		
		if (g > 255)
		{
			g = 255;
		}
		else if (g < 0)
		{
			g = 0.0;
		}
		
		if (b > 255)
		{
			b = 255;
		}
		else if (b < 0)
		{
			b = 0.0;
		}
		
		r = r/255.0;
		g = g/255.0;
		b = b/255.0;
		
		double minRGB = Math.min(r, Math.min(g, b));
		double maxRGB = Math.max(r,  Math.max(g, b));
		
		// Black-gray-white
		if (minRGB == maxRGB)
		{
			hsv[0] = 0.0;
			hsv[1] = 0.0;
			hsv[2] = minRGB;
		}
		else
		{
			// Colors
			double d, h;
			if (r == minRGB)
			{
				d = g - b;
				h = 3;
			}
			else if (b == minRGB)
			{
				d = r - g;
				h = 1;
			}
			else
			{
				d = b - r;
				h = 5;
			}
			
			hsv[0] = 60.0*(h - d/(maxRGB-minRGB));
			hsv[1] = (maxRGB - minRGB)/maxRGB;
			hsv[2] = maxRGB;
			
			System.out.println("HSV: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
		}
	}
}
