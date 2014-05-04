import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

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
	private ArrayList<Histogram2> _rectangleHistograms;

	
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
		_rectangleHistograms = new ArrayList<Histogram2>();
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
		boolean matched = false;
		
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
			
			// Returns an ArrayList of Integer[] where each Integer[] corresponds to a rectangle
			ClusterGroup clusters = new ClusterGroup(_backProjectedArray, _width, _height);
			ArrayList<Integer[]> listOfRectangles = clusters.getListOfRectangles();
			
			// Create a histogram for each rectangle and add to rectangleHistograms
			for (int j = 0; j< listOfRectangles.size(); j++){
				Integer[] rectangle = listOfRectangles.get(j);
				int rectangle_x = rectangle[0];
				int rectangle_y = rectangle[1];
				int rectangle_width = rectangle[2];
				int rectangle_height = rectangle[3];
				
				// Create histogram for cluster
				Histogram2 histogram = new Histogram2();
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
				System.out.println("Printing histogram for cluster #" + j);
				histogram.print();
				_searchImageHistograms.add(histogram);
				
				if (_queryImageHistogram.Equals(histogram))
				{
					System.out.println("Query image matched cluster: " + j);
					matched = true;
					result = new Rectangle(rectangle_x, rectangle_y, rectangle_width, rectangle_height);
					break;
				}
			}
			
			JFrame frame1 = new JFrame();
		    frame1.setLocation(372, 0);
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setOpaque(true);
			// Draw search image
			JLabel label2 = new JLabel(new ImageIcon(image));
			label2.setSize(image.getWidth(), image.getHeight());
			label2.setLocation(0, 0);
			panel.add(label2);
			
			// Draw rectangle (if result found)
			if (result != null)
			{
				JLabel rectangleLabel = new JLabel();
				rectangleLabel.setLocation(
						label2.getLocation().x + result.x,
						label2.getLocation().y + result.y);
				rectangleLabel.setSize(result.width, result.height);
				rectangleLabel.setBorder(LineBorder.createBlackLineBorder());
				panel.add(rectangleLabel);
				panel.setComponentZOrder(rectangleLabel, 0);
				panel.setComponentZOrder(label2, 1);
			}
			
			frame1.setContentPane(panel);
		    //frame1.pack();
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

			
			/*DrawRectangle rect=new DrawRectangle();
			rect.getContentPane().add(label2, BorderLayout.CENTER);
			rect.pack();
			rect.setLocation(372, 400); */
		    
		   /* GameFrame g = new GameFrame(rectangles, new JLabel(new ImageIcon(image)));
		    g.setLocation(372, 400);
		    //g.getContentPane().add(label2, BorderLayout.CENTER);
		    g.pack();
		    g.setVisible(true); */
			if (matched)
			{
				// Matched symbol
				break;
			}
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
	
	
/*
	
	public class GameFrame extends JFrame {
		public GameFrame(ArrayList<Integer[]> rectangles, JLabel label) {
			super("Game Frame");
		    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		   // getContentPane().add(label, BorderLayout.CENTER);
		    Squares squares = new Squares();
		    getContentPane().add(squares);
		    for (int i = 0; i < rectangles.size(); i++) {
		    	Integer[] rectangle = rectangles.get(i);
		  		int rectangle_x = rectangle[0];
		  		int rectangle_y = rectangle[1];
		  		int rectangle_width = rectangle[2];
		  		int rectangle_height = rectangle[3];
		        squares.addSquare(rectangle_x, rectangle_y, rectangle_width, rectangle_height);
		    }
		    
		  //  pack();
		   // setLocationRelativeTo(null);
		   // setVisible(true);

		   }

		}
	
	class Squares extends JPanel {
		   private List<Rectangle> squares = new ArrayList<Rectangle>();

		   public void addSquare(int x, int y, int width, int height) {
		      Rectangle rect = new Rectangle(x, y, width, height);
		      squares.add(rect);
		   }

		   @Override
		   protected void paintComponent(Graphics g) {
		      super.paintComponent(g);
		      Graphics2D g2 = (Graphics2D) g;
		      for (Rectangle rect : squares) {
		    	 g2.setColor(Color.black);
		         g2.draw(rect);
		      }
		   }

		}
	
*/
/*

class DrawRectangle extends JFrame {
	public DrawRectangle() {
		//to  Set JFrame title
		super("Draw A Rectangle In JFrame");

		//Set default close operation for JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Set JFrame size
		//setSize(500,500);

		//Make JFrame visible
		setVisible(true);
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.yellow);
		g.drawRect(0,0,100,100);//this will draw your border. 

	}
} */


}
