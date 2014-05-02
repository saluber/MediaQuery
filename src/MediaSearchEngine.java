import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MediaSearchEngine
{
	private int _width, _height;
	private ArrayList<BufferedImage> _searchImages;
	private ArrayList<Double[][][]> _hsvSearchImages;
	private ArrayList<Histogram2> _searchImageHistograms;
	Double[][][] _hsvQueryImage;
	Histogram2 _queryImageHistogram;
	Integer[][] _backProjectedArray;
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
		_backProjectedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	
	public MediaSearchEngine(int width, int height, ArrayList<BufferedImage> searchImages)
	{
		_width = width;
		_height = height;
		_searchImages = searchImages;
	}
		
	
	// Create HSV histogram for Query Image
	public void createHSV(BufferedImage query, boolean[][] alphaImage) {
		
		_hsvQueryImage = new Double[_width][_height][3];
		_queryImageHistogram = new Histogram2();
		
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				
				if (alphaImage[x][y] == true) {
					int pixel = query.getRGB(x, y);
					int r = (pixel >> 16) & 0x000000FF;
					int g = (pixel >> 8) & 0x000000FF;
					int b = pixel & 0x000000FF;
					
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					_hsvQueryImage[x][y][0] = (double) hsv[0]; // hue
					_hsvQueryImage[x][y][1] = (double) hsv[1];
					_hsvQueryImage[x][y][2] = (double) hsv[2];
					
					_queryImageHistogram.AddValue((hsv));					
				}
			}
		}
		
		// Normalize the histogram
		_queryImageHistogram.Normalize();
		_queryImageHistogram.print();		
		
	}
	
	// Searches images for query image and returns rectangle of match region (if found)
	public Rectangle find(BufferedImage searchImage) {
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
														
					// Method 3: Using Color.RGBtoHSB method
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					hsvImage[x][y][0] = (double) hsv[0]; // hue
					hsvImage[x][y][1] = (double) hsv[1];
					hsvImage[x][y][2] = (double) hsv[2];
					
					
					// Back-projection Algorithm
					// Get bin number of hue
					int bin = h.getHBin((hsv));
					
					// Get value of the corresponding bin from the Query Image
					int queryValue = _queryImageHistogram.getBinValue(bin);
											
					
					//System.out.println("Bin: " + bin + ", Value: " + queryValue + "   R: " + r + " G: " + g + " B: " + b + "  Hue: " + hsv[0] + " Sat: " + hsv[1] + " Val: " + hsv[2]);

					
					queryValue = (int) Math.floor((double)queryValue/_queryImageHistogram.gexMaxBinValue()*255);
					
					int pix = 0xff000000 | ((queryValue & 0xff) << 16)
							| ((queryValue & 0xff) << 8) | (queryValue & 0xff);
					
					// Set pixel of backProjectedImage to this value
					_backProjectedArray[x][y] = queryValue;
					_backProjectedImage.setRGB(x, y, pix);
					
					
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
