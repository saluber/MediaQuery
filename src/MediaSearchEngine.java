import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import sun.misc.FloatingDecimal;

public class MediaSearchEngine
{
	private int _width, _height;
	private ArrayList<BufferedImage> _searchImages;
	private ArrayList<Double[][][]> _hsvSearchImages;
	private ArrayList<Histogram2> _searchImageHistograms;
	private Double[][][] _hsvQueryImage;
	private Histogram2 _queryImageHistogram;
	private Integer[][] _backProjectedArray;
	private BufferedImage _backProjectedImage;

	
	public MediaSearchEngine(int width, int height, BufferedImage searchImage)
	{
		_width = width;
		_height = height;
		_searchImages = new ArrayList<BufferedImage>();
		_searchImages.add(searchImage);
		_hsvSearchImages = new ArrayList<Double[][][]>();
		_searchImageHistograms = new ArrayList<Histogram2>();
		_backProjectedArray = new Integer[width][height];
		_backProjectedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		_hsvQueryImage = new Double[_width][_height][3];
		_queryImageHistogram = new Histogram2();
	}
	
	// Create HSV histogram for Query Image
	public void createHSV(BufferedImage query, boolean[][] alphaImage) 
	{
		
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				
				if (alphaImage[x][y] == true) {
					int pixel = query.getRGB(x, y);
					int r = (pixel >> 16) & 0x000000FF;
					int g = (pixel >> 8) & 0x000000FF;
					int b = pixel & 0x000000FF;
					
					if (r > 255)
					{
						r = 255;
					}
					else if (r < 0)
					{
						r = 0;
					}
					if (g > 255)
					{
						g = 255;
					}
					else if (g < 0)
					{
						g = 0;
					}
					if (b > 255)
					{
						b = 255;
					}
					else if (b < 0)
					{
						b = 0;
					}
					
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					Float h_value = new Float(hsv[0]); // hue
					Float s_value = new Float(hsv[1]); // sat
					Float v_value = new Float(hsv[2]); // val
					_hsvQueryImage[x][y][0] = new FloatingDecimal(h_value.floatValue()).doubleValue();
					_hsvQueryImage[x][y][1] = new FloatingDecimal(s_value.floatValue()).doubleValue();
					_hsvQueryImage[x][y][2] = new FloatingDecimal(v_value.floatValue()).doubleValue();
					
					_queryImageHistogram.AddValue((hsv));					
				}
			}
		}
		
		// Normalize the histogram
		_queryImageHistogram.Normalize();
		
		// Print the histogram
		_queryImageHistogram.print();		
		
	}
	
	// Searches images for query image and returns rectangle of match region (if found)
	public Rectangle find() {
		System.out.println("Finding query in search image");
		
		Rectangle result = null;
		for (int i = 0; i < _searchImages.size(); i++) {
			BufferedImage image = _searchImages.get(i);
			Double[][][] hsvImage = new Double[_width][_height][3];
			Histogram2 h = new Histogram2();
			
			for (int y = 0; y < _height; y++) {
				for (int x = 0; x < _width; x++) {
					int pixel = image.getRGB(x, y);
					
					// Convert pixel to HSV	
					int r = ((pixel >> 16) & 0x000000FF);
					int g = ((pixel >> 8) & 0x000000FF);
					int b = (pixel & 0x000000FF);
													
					if (r > 255)
					{
						r = 255;
					}
					else if (r < 0)
					{
						r = 0;
					}
					if (g > 255)
					{
						g = 255;
					}
					else if (g < 0)
					{
						g = 0;
					}
					if (b > 255)
					{
						b = 255;
					}
					else if (b < 0)
					{
						b = 0;
					}
					
					// Method 3: Using Color.RGBtoHSB method
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					Float h_value = new Float(hsv[0]); // hue
					Float s_value = new Float(hsv[1]); // sat
					Float v_value = new Float(hsv[2]); // val
					hsvImage[x][y][0] = new FloatingDecimal(h_value.floatValue()).doubleValue();
					hsvImage[x][y][1] = new FloatingDecimal(s_value.floatValue()).doubleValue();
					hsvImage[x][y][2] = new FloatingDecimal(v_value.floatValue()).doubleValue();
					
					// Back-projection Algorithm
					// Get bin number of hue
					int bin = h.getHBin((hsv));
					
					// Get value of the corresponding bin from the Query Image
					int queryValue = _queryImageHistogram.getBinValue(bin);
					//int origQueryValue = queryValue;
					
					//System.out.println("Bin: " + bin + ", Value: " + queryValue + "   R: " + r + " G: " + g + " B: " + b + "  Hue: " + hsv[0] + " Sat: " + hsv[1] + " Val: " + hsv[2]);
					// queryValue = (int)Math.round(((double)queryValue/(double)_queryImageHistogram.gexMaxBinValue())*255.0);
					if (queryValue > 0)
					{
						//System.out.println("Bin: " + bin + " Query value: " + origQueryValue + " Rgb value: " + queryValue);
					}

					int grayPixel = 0;
					grayPixel = (grayPixel | ((queryValue & 0xff) << 16)
							| ((queryValue & 0xff) << 8) | (queryValue & 0xff))/3;
					
					// Set pixel of backProjectedImage to this value
					_backProjectedArray[x][y] = queryValue;
					_backProjectedImage.setRGB(x, y, grayPixel);
					
				}
			}
			
			
		//	MeanShift ms = new MeanShift();
		//	int[] starting_point = ms.applyMeanShift(_backProjectedArray);
		//	System.out.println("Starting point for block: " + starting_point[0] + ", " + starting_point[1]);
			
			
			JFrame frame1 = new JFrame();
			JLabel label2 = new JLabel(new ImageIcon(image));
			frame1.getContentPane().add(label2, BorderLayout.CENTER);
		    frame1.setLocation(372, 0);
		    frame1.pack();
		    frame1.setVisible(true);
		    frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    
		    JFrame frame2 = new JFrame();
			JLabel label3 = new JLabel(new ImageIcon(_backProjectedImage));
			frame2.getContentPane().add(label3, BorderLayout.CENTER);
		    frame2.setLocation(10, 0);
		    frame2.pack();
		    frame2.setVisible(true);
		    frame2.setLocation(734, 0);
		    frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		}
		
		for (int i = 0; i < _searchImageHistograms.size(); i++) {	
			//if (_searchImageHistograms.get(i).contains(_queryImageHistogram)) {
			//	System.out.println("Matched! Search Image Histogram:");
			//	_searchImageHistograms.get(i).print();
				
			//	result = new Rectangle(10, 10, 100, 100);
			//	break;
			//}
		}
		
		
		return result;
		
	}

}
