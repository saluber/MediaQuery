import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MediaSearchEngine
{
	private int _width, _height;
	private ArrayList<BufferedImage> _searchImages;
	private ArrayList<Double[][][]> _hsvSearchImages;
	private ArrayList<Histogram> _searchImageHistograms;
	Double[][][] _hsvQueryImage;
	Histogram _queryImageHistogram;
	
	public MediaSearchEngine(int width, int height, BufferedImage searchImage)
	{
		_width = width;
		_height = height;
		_searchImages = new ArrayList<BufferedImage>();
		_searchImages.add(searchImage);
		_hsvSearchImages = new ArrayList<Double[][][]>();
		_searchImageHistograms = new ArrayList<Histogram>();
		
		// Convert search images to HSV
		System.out.println("Converting search images to HSV!");
		for (int i = 0; i < _searchImages.size(); i++)
		{
			BufferedImage image = _searchImages.get(i);
			Double[][][] hsvImage = new Double[_width][_height][3];
			Histogram h = new Histogram();

			for (int y = 0; y < _height; y++)
			{
				for (int x = 0; x < _width; x++)
				{
					int pixel = image.getRGB(x, y);
					int r = ((pixel >> 16) & 0x000000FF);
					int g = ((pixel >> 8) & 0x000000FF);
					int b = (pixel & 0x000000FF);
					
					// Method 1: Using rgbToHSV method
					/*
					Double[] hsv = new Double[3];
					rgbToHSV((double)r, (double)g, (double)b, hsv);
					hsvImage[x][y][0] = hsv[0];
					hsvImage[x][y][1] = hsv[1];
					hsvImage[x][y][2] = hsv[2];
					*/
					
					// Method 2: Using rgbToHsv2 method
					Double[] hsv = new Double[3];
					rgbToHsv2(r, g, b, hsv);
					hsvImage[x][y][0] = hsv[0];
					hsvImage[x][y][1] = hsv[1];
					hsvImage[x][y][2] = hsv[2];
					
					// Add hue to image histogram
					h.AddValue((int)Math.round(hsv[0]));
					
					// Method 3: Using Java Color.RGBtoHSB
					/*
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					
					Float hsv0 = hsv[0];
					Float hsv1 = hsv[1];
					Float hsv2 = hsv[2];
					
					hsvImage[x][y][0] = hsv0.doubleValue();
					hsvImage[x][y][1] = hsv1.doubleValue();
					hsvImage[x][y][2] = hsv2.doubleValue();
					h.AddValue(hsvImage[x][y][0].intValue());
					*/
					
					// System.out.println("Adding pixel: (" + x + ", " + y + ") with hsv value: [" +  hsvImage[x][y][0] + "," + hsvImage[x][y][1] + "," + hsvImage[x][y][2] + "]");
				}
			}
			
			System.out.println("Search image (prenormalization):");
			h.print();
			
			_hsvSearchImages.add(hsvImage);
			h.Normalize();
			_searchImageHistograms.add(h);
		}
	}
	
	public MediaSearchEngine(int width, int height, ArrayList<BufferedImage> searchImages)
	{
		_width = width;
		_height = height;
		_searchImages = searchImages;
	}
	
	// Searches images for query image and returns rectangle of match region (if found)
	public Rectangle search(BufferedImage query, boolean[][] alphaImage)
	{
		Rectangle result = null;
		
		// Convert query image to HSV
		System.out.println("Converting query image to HSV");
		
		_hsvQueryImage = new Double[_width][_height][3];
		_queryImageHistogram = new Histogram();
		for (int y = 0; y < _height; y++)
		{
			for (int x = 0; x < _width; x++)
			{
				int pixel = query.getRGB(x, y);
				int r = (pixel >> 16) & 0x000000FF;
				int g = (pixel >> 8) & 0x000000FF;
				int b = pixel & 0x000000FF;
				
				// Method 1: Using rgbToHSV method
				/*
				Double[] hsv = new Double[3];
				rgbToHSV((double)r, (double)g, (double)b, hsv);
				_hsvQueryImage[x][y][0] = hsv[0];
				_hsvQueryImage[x][y][1] = hsv[1];
				_hsvQueryImage[x][y][2] = hsv[2];
				*/
				
				// Method 2: Using rgbToHsv2 method
				Double[] hsv = new Double[3];
				rgbToHsv2(r, g, b, hsv);
				_hsvQueryImage[x][y][0] = hsv[0];
				_hsvQueryImage[x][y][1] = hsv[1];
				_hsvQueryImage[x][y][2] = hsv[2];
				
				// Add hue to query image histogram
				if (alphaImage[x][y] == true)
				{
					_queryImageHistogram.AddValue((int)Math.round(hsv[0]));
				}
				
				// Method 3: Using Java Color.RGBtoHSB
				/*
				float[] hsv = new float[3];
				Color.RGBtoHSB(r, g, b, hsv);
				
				Float hsv0 = hsv[0];
				Float hsv1 = hsv[1];
				Float hsv2 = hsv[2];
				
				_hsvQueryImage[x][y][0] = hsv0.doubleValue();
				_hsvQueryImage[x][y][1] = hsv1.doubleValue();
				_hsvQueryImage[x][y][2] = hsv2.doubleValue();
				if (alphaImage[x][y])
				{
					_queryImageHistogram.AddValue(_hsvQueryImage[x][y][0].intValue());
				}
				*/
				
				// System.out.println("Adding pixel: (" + x + ", " + y + ") with hsv value: [" +  _hsvQueryImage[x][y][0] + "," + _hsvQueryImage[x][y][1] + "," + _hsvQueryImage[x][y][2] + "]");
			}
		}
		
		System.out.println("Query image (prenormalization):");
		_queryImageHistogram.print();
		
		_queryImageHistogram.Normalize();
		
		System.out.println("Normalized Query Image Histogram:");
		_queryImageHistogram.print();
		
		// Search image histograms for one that contains query image histogram
		for (int i = 0; i < _searchImageHistograms.size(); i++)
		{	
			if (_searchImageHistograms.get(i).contains(_queryImageHistogram))
			{
				System.out.println("Matched! Search Image Histogram:");
				_searchImageHistograms.get(i).print();
				
				result = new Rectangle(10, 10, 100, 100);
				break;
			}
		}
		
		return result;
	}
	
	// RGB to HSV Conversion
	// Hue degree between 0.0 and 360.0
	// Saturation between 0.0 (gray) and 1.0
	// Value between 0.0 (black) and 1.0
	private void rgbToHsv2(double r, double g, double b, Double[] hsv)
	{
		double hue, sat, val;
		
		if (r > 255)
		{
			r = 255.0;
		}
		if (r < 0)
		{
			r = 0.0;
		}
		if (g > 255)
		{
			g = 255.0;
		}
		if (g < 0)
		{
			g = 0.0;
		}
		if (b > 255)
		{
			b = 255.0;
		}
		if (b < 0)
		{
			b = 0.0;
		}
		
		// Find min and max RGB values
		double rgbMin, rgbMax;
		if (r < g)
		{
			rgbMin = r;
			rgbMax = g;
		}
		else
		{
			rgbMin = g;
			rgbMax = r;
		}
		if (b < rgbMin)
		{
			rgbMin = b;
		}
		if (b > rgbMax)
		{
			rgbMax = b;
		}
		
		// Calculate value
		val = rgbMax;
		if (val == 0)
		{
			hue = 0.0;
			sat = 0.0;
		}
		else
		{
			// Calculate saturation
			sat = 1.0 - (rgbMin/rgbMax);
			if (sat == 0)
			{
				hue = 0.0;
			}
			else
			{
				// Calculate hue
				if (rgbMax == r)
				{
					hue = ((60.0 *(g - b)/(rgbMax - rgbMin)) % 360);
				}
				else if (rgbMax == g)
				{
					hue = 120.0 + 60.0*(b - r)/(rgbMax - rgbMin);
				}
				else
				{
					hue = 240.0 + 60.0*(r - g)/(rgbMax - rgbMin);
				}
			}
		}
		
		if (hue < 0)
		{
			hue += 360.0;
		}
		
		hsv[0] = hue;
		hsv[1] = sat;
		hsv[2] = val;
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
			g = 255.0;
		}
		else if (g < 0)
		{
			g = 0.0;
		}
		
		if (b > 255)
		{
			b = 255.0;
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
			
			// System.out.println("HSV: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
		}
	}
}
