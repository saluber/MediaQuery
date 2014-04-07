import java.awt.image.*;
import javax.swing.*;

public class DoubleImageDisplay
{
	public static final int HORIZONTAL_PADDING = 75;
	public static final int VERTICAL_PADDING = 50;
	public static final int TEXT_LABEL_HEIGHT = 25;
	public static final int IMAGE_WIDTH = 288;
	public static final int IMAGE_HEIGHT = 352;
	public static final int WINDOW_WIDTH = 950;
	public static final int WINDOW_HEIGHT = 550;
	
	private String _containerTitle = "Double Image Display";
	private String _firstImageTitle = "First Image:";
	private String _secondImageTitle = "Second Image:";
	private JFrame _frame;
	private JPanel _basePanel;
	private JLabel _firstImageLabel;
	private JLabel _secondImageLabel;
	
	public DoubleImageDisplay(String containerTitle, String firstImageTitle,
			String secondImageTitle, String[] labels)
	{
		if (containerTitle != null)
		{
			_containerTitle = containerTitle;
		}
		if (firstImageTitle != null)
		{
			_firstImageTitle = firstImageTitle;
		}
		if (secondImageTitle != null)
		{
			_secondImageTitle = secondImageTitle;
		}
		
		initDisplay(labels);
	}
	
	public void setFirstImage(BufferedImage image)
	{
		// Create image label
		JLabel nextImageLabel = new JLabel(new ImageIcon(image));
		nextImageLabel.setLocation(DoubleImageDisplay.HORIZONTAL_PADDING, DoubleImageDisplay.VERTICAL_PADDING);
		nextImageLabel.setSize(image.getWidth(), image.getHeight());
		nextImageLabel.setHorizontalAlignment(JLabel.LEFT);
		
		// Swap new image label with existing image label
		_basePanel.add(nextImageLabel);
		if (_firstImageLabel != null)
		{
			_basePanel.remove(_firstImageLabel);
		}
		
		// Store reference to new image label
		_firstImageLabel = nextImageLabel;
		
		// "Refresh" view
		_frame.setContentPane(_basePanel);
		_frame.setVisible(true);
	}
	
	public void setSecondImage(BufferedImage image)
	{
		// Create image label
		JLabel nextImageLabel = new JLabel(new ImageIcon(image));
		nextImageLabel.setLocation(
				(DoubleImageDisplay.HORIZONTAL_PADDING * 3) + DoubleImageDisplay.IMAGE_WIDTH, 
				DoubleImageDisplay.VERTICAL_PADDING);
		nextImageLabel.setSize(image.getWidth(), image.getHeight());
		nextImageLabel.setHorizontalAlignment(JLabel.LEFT);
		
		// Swap new image label with existing image label
		_basePanel.add(nextImageLabel);
		if (_secondImageLabel != null)
		{
			_basePanel.remove(_secondImageLabel);
		}
		
		// Store reference to new image label
		_secondImageLabel = nextImageLabel;
		
		// "Refresh" view
		_frame.setContentPane(_basePanel);
		_frame.setVisible(true);
	}
	
	private void initDisplay(String[] labels)
	{
		// Create JFrame
		_frame = new JFrame(_containerTitle);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setSize(DoubleImageDisplay.WINDOW_WIDTH, DoubleImageDisplay.WINDOW_HEIGHT);
		
		// Create JPanel
		_basePanel = new JPanel();
		_basePanel.setLayout(null);
		_basePanel.setOpaque(true);
		// Draw first image title
		JLabel imageTitleLabel = createTextLabel(
				_firstImageTitle,
				DoubleImageDisplay.HORIZONTAL_PADDING,
				DoubleImageDisplay.VERTICAL_PADDING/2);
		_basePanel.add(imageTitleLabel);
		// Draw second image title
		JLabel secondTitleLabel = createTextLabel(
				_secondImageTitle,
				(DoubleImageDisplay.HORIZONTAL_PADDING * 3) + DoubleImageDisplay.IMAGE_WIDTH,
				DoubleImageDisplay.VERTICAL_PADDING/2);
		_basePanel.add(secondTitleLabel);
		// Draw additional text labels (if any)
		if (labels != null)
		{
			int y_position = DoubleImageDisplay.VERTICAL_PADDING + DoubleImageDisplay.IMAGE_HEIGHT;
			for (int i = 0; i < labels.length; i++)
			{
				JLabel textLabel = createTextLabel(
						labels[i],
						DoubleImageDisplay.HORIZONTAL_PADDING,
						y_position);
				_basePanel.add(textLabel);
				y_position += (DoubleImageDisplay.VERTICAL_PADDING/2);
			}
		}
		
		// Attach JPanel to JFrame
		_frame.setContentPane(_basePanel);
		_frame.setVisible(true);
	}
	
	private JLabel createTextLabel(String text, int x_position, int y_position)
	{
		JLabel textLabel = new JLabel(text);
		textLabel.setLayout(null);
		textLabel.setLocation(
				x_position,
				y_position);
		textLabel.setSize(
				DoubleImageDisplay.IMAGE_WIDTH*2,
				DoubleImageDisplay.TEXT_LABEL_HEIGHT);
		textLabel.setHorizontalTextPosition(JLabel.CENTER);
		
		return textLabel;
	}
}
