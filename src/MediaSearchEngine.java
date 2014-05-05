import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MediaSearchEngine {
	private int _width, _height;
	private ArrayList<BufferedImage> _searchImages;
	private ArrayList<Histogram> _searchImageHistograms;
	private Double[][][] _hsvQueryImage;
	private Histogram _queryImageHistogram;
	private Integer[][] _backProjectedArray;
	private BufferedImage _backProjectedImage;
	private int _range = 2;

	
	public MediaSearchEngine(int width, int height, BufferedImage searchImage) {
		_width = width;
		_height = height;
		_searchImages = new ArrayList<BufferedImage>();
		_searchImages.add(searchImage);
		_searchImageHistograms = new ArrayList<Histogram>();
		_backProjectedArray = new Integer[width][height];
		_backProjectedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		_hsvQueryImage = new Double[_width][_height][3];
		_queryImageHistogram = new Histogram();
	}
	
	// Create HSV histogram for Query Image
	public void createHSV(BufferedImage query, boolean[][] alphaImage) {
		
		for (int y = 0; y < _height; y++) {
			for (int x = 0; x < _width; x++) {
				
				if (alphaImage[x][y] == true) {
					int pixel = query.getRGB(x, y);
					int r = (pixel >> 16) & 0x000000FF;
					int g = (pixel >> 8) & 0x000000FF;
					int b = pixel & 0x000000FF;
					
					if (r > 255) {
						r = 255;
					}
					else if (r < 0) {
						r = 0;
					}
					if (g > 255){
						g = 255;
					}
					else if (g < 0) {
						g = 0;
					}
					if (b > 255) {
						b = 255;
					}
					else if (b < 0) {
						b = 0;
					}
					
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					Float h_value = new Float(hsv[0]); // hue
					Float s_value = new Float(hsv[1]); // sat
					Float v_value = new Float(hsv[2]); // val
					_hsvQueryImage[x][y][0] = new Double(h_value.doubleValue());
					_hsvQueryImage[x][y][1] = new Double(s_value.doubleValue());
					_hsvQueryImage[x][y][2] = new Double(v_value.doubleValue());
					
					_queryImageHistogram.AddValue((hsv));					
				}
			}
		}
		
		// Normalize the histogram
		_queryImageHistogram.Normalize();	
		
	}
	
	// Searches images for query image
	public boolean find() {	
		boolean result = false;
		int resultNum = -1;
		double resultDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < _searchImages.size(); i++) {
			BufferedImage image = _searchImages.get(i);
			Double[][][] hsvImage = new Double[_width][_height][3];
			Histogram h = new Histogram();
			
			for (int y = 0; y < _height; y++) {
				for (int x = 0; x < _width; x++) {
					int pixel = image.getRGB(x, y);
					
					// Convert pixel to HSV	
					int r = ((pixel >> 16) & 0x000000FF);
					int g = ((pixel >> 8) & 0x000000FF);
					int b = (pixel & 0x000000FF);
													
					if (r > 255) {
						r = 255;
					}
					else if (r < 0) {
						r = 0;
					}
					if (g > 255) {
						g = 255;
					}
					else if (g < 0) {
						g = 0;
					}
					if (b > 255) {
						b = 255;
					}
					else if (b < 0) {
						b = 0;
					}
					
					// Method 3: Using Color.RGBtoHSB method
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					Float h_value = new Float(hsv[0]); // hue
					Float s_value = new Float(hsv[1]); // sat
					Float v_value = new Float(hsv[2]); // val
					hsvImage[x][y][0] = new Double(h_value.doubleValue());
					hsvImage[x][y][1] = new Double(s_value.doubleValue());
					hsvImage[x][y][2] = new Double(v_value.doubleValue());
					h.AddValue(hsv);
					
					// Back-projection Algorithm
					// Get bin number of hue
					int bin = h.getHBin((hsv));
					
					// Get value of the corresponding bin from the Query Image
					int queryValue = _queryImageHistogram.getBinValue(bin);
					
					// If value is 0, add up the values surrounding bins
					if (queryValue == 0){
						Integer[] binRange = h.getBinRange(bin, _range);
						for (int j=0; j<binRange.length; j++){
							queryValue += binRange[i];
						}
					}
					
					queryValue = (int)Math.round(((double)queryValue/(double)Histogram._histogramHeight)*255.0);
					
					int grayPixel = 0;
					grayPixel = (grayPixel | ((queryValue & 0xff) << 16)
							| ((queryValue & 0xff) << 8) | (queryValue & 0xff))/3;
					
					// Set pixel of backProjectedImage to this value
					_backProjectedArray[x][y] = queryValue;
					_backProjectedImage.setRGB(x, y, grayPixel);
					
				}
			}
			
			

			// Returns an ArrayList of Integer[] where each Integer[] corresponds to a rectangle
			ClusterGroup clusters = new ClusterGroup(_backProjectedArray, _width, _height, _queryImageHistogram.getMaxBin(), hsvImage);
			ArrayList<Integer[]> listOfRectangles = clusters.getListOfRectangles();
			
			// Sort clusters by size
			for (int s = 0; s < listOfRectangles.size(); s++)
			{
				for (int r = 1; r < listOfRectangles.size(); r++)
				{
					int area1 = listOfRectangles.get(r-1)[2] * listOfRectangles.get(r-1)[3];
					int area2 = listOfRectangles.get(r)[2] * listOfRectangles.get(r)[3];
					if (area1 < area2)
					{
						// Swap
						Integer[] rect = listOfRectangles.get(r);
						listOfRectangles.set(r, listOfRectangles.get(r-1));
						listOfRectangles.set(r-1, rect);
					}
				}
			}
			
			// Create a histogram for each rectangle and add to rectangleHistograms
			for (int j = 0; j< listOfRectangles.size(); j++){
				Integer[] rectangle = listOfRectangles.get(j);
				int rectangle_x = rectangle[0];
				int rectangle_y = rectangle[1];
				int rectangle_width = rectangle[2];
				int rectangle_height = rectangle[3];
				
				// Create histogram for cluster
				Histogram histogram = new Histogram();
				for (int y=rectangle_y; y<rectangle_y + rectangle_height; y++){
					for (int x=rectangle_x; x<rectangle_x + rectangle_width; x++){
						
						float[] hsv = new float[3];
						Double h_value = new Double(hsvImage[x][y][0]);
						Double s_value = new Double(hsvImage[x][y][1]);
						Double v_value = new Double(hsvImage[x][y][2]);
						hsv[0] = h_value.floatValue();
						hsv[1] = s_value.floatValue();
						hsv[2] = v_value.floatValue();
						
						histogram.AddValue(hsv);
					}
				}
				histogram.Normalize();				
				_searchImageHistograms.add(histogram);
				
				// Compare the search histogram for the logo histogram to see if there is a match
				double distance = _queryImageHistogram.Compare(histogram);
				if ((distance > 0) && (distance < resultDistance))
				{
					result = true;
					resultDistance = distance;
					resultNum = j;
				}
			}
		    
			
			
			// Display back projected image
			JFrame backProjectedFrame = new JFrame();
			JLabel backProjectedLabel = new JLabel(new ImageIcon(_backProjectedImage));
			backProjectedFrame.getContentPane().add(backProjectedLabel, BorderLayout.CENTER);
			backProjectedFrame.setTitle("Back Projected Image");
			backProjectedFrame.setLocation(10, 0);
			backProjectedFrame.pack();
			backProjectedFrame.setVisible(true);
			backProjectedFrame.setLocation(372, 0);
			backProjectedFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			// Display search image with rectangle clusters
			JLabel searchImageLabel = new JLabel(new ImageIcon(image));
			DrawRectangle searchImageRectangles = new DrawRectangle(listOfRectangles, resultNum);
			searchImageRectangles.getContentPane().add(searchImageLabel, BorderLayout.CENTER);
			searchImageRectangles.pack();
			searchImageRectangles.setLocation(734, 0);
		}
		
		return result;
	}
	
	

	class DrawRectangle extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ArrayList<Integer[]> _rectangles;
		int _resultNum;
		int _topInset;
		
		public DrawRectangle(ArrayList<Integer[]> rectangles, int resultNum) {
			_rectangles = rectangles;
			_resultNum = resultNum;
			
			if (resultNum != -1) {
				setTitle("Search Image: Match Found!");
			} else {
				setTitle("Search Image: Match Not Found");
			}
			
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;		
			setVisible(true);
			Insets c = getInsets();
			_topInset = c.top;
		}
	
		public void paint(Graphics g) {
			super.paint(g);
			for (int i=0; i<_rectangles.size(); i++){
				Integer[] rectangle = _rectangles.get(i);
		  		int rectangle_x = rectangle[0];
		  		int rectangle_y = rectangle[1];
		  		int rectangle_width = rectangle[2];
		  		int rectangle_height = rectangle[3];
		  		if (i == _resultNum){
		  			g.setColor(Color.yellow);
		  		} else {
		  			g.setColor(Color.blue);
		  		}
		  		g.drawRect(rectangle_x, rectangle_y + _topInset, rectangle_width, rectangle_height);
			}
		}

	} 


}
