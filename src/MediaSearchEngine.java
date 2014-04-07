import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MediaSearchEngine
{
	private int _width, _height;
	private ArrayList<BufferedImage> _searchImages;
	
	public MediaSearchEngine(int width, int height, BufferedImage searchImage)
	{
		_width = width;
		_height = height;
		_searchImages = new ArrayList<BufferedImage>();
		_searchImages.add(searchImage);
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
		// TODO
		
		return result;
	}
}
