import java.awt.image.BufferedImage;


public class MeanShift {
	
	//private BufferedImage _meanShiftImage;
	private int _blockWidth;
	private int _blockHeight;
	private int _imageWidth;
	private int _imageHeight;	
	
	public MeanShift() {
		_blockWidth = 45;
		_blockHeight = 45;
		_imageWidth = 352;
		_imageHeight = 288;
		
	}
	
	// Applies the Mean-shift algorithm to the back projected array
	public int[] applyMeanShift(Integer[][] backProjectedArray){
		int[] starting_point = {0, 0};
		int[] center_point = getCenterPoint(starting_point);
		int[] centroid_point = findCentroid(starting_point, backProjectedArray);
		
		while (center_point[0] != centroid_point[0] && center_point[1] != centroid_point[1]){
			// Get new starting point use previous centroid as center
			starting_point = getStartingPoint(centroid_point);
			System.out.println("starting point: " + starting_point[0] + ", " + starting_point[1]);
			// Use previous centroid point as the new center point
			center_point = centroid_point;	
			System.out.println("center point: " + center_point[0] + ", " + center_point[1]);
			// Calculate a new centroid point
			centroid_point = findCentroid(starting_point, backProjectedArray);
			System.out.println("centroid point: " + centroid_point[0] + ", " + centroid_point[1]);
			System.out.println();
		}
		return starting_point;
	}
	
	
	// Finds the centroid of a block given the block's starting point
	public int[] findCentroid(int[] starting_point, Integer[][] backProjectedArray){
		int starting_x = starting_point[0];
		int starting_y = starting_point[1];
		int Mx = 0;
		int My = 0;
		double mass = 0;
		int[] centroid = {0, 0};
		for (int i=0; i<_blockWidth; i++){
			for (int j=0; j<_blockHeight;j++){
				int x = i + starting_x;
				int y = j + starting_y;
				if (x>0 && y >0 && x < _imageWidth && y < _imageHeight){
					
					//System.out.println("x: " + x + ", y:" + y);
					double value = backProjectedArray[x][y]/255;
					if (value != 0){
						Mx += y;
						My += x;
						mass += value;
					}
				}
				
			}
		}
		centroid[0] = (int)Math.floor(Mx/mass);
		centroid[1] = (int)Math.floor(My/mass);
		
		return centroid;
	}
	
	
	// Gets the center point of a block given it's starting point
	public int[] getCenterPoint(int[] starting_point){
		int starting_x = starting_point[0];
		int starting_y = starting_point[1];
		int[] center_point = new int[2];
		center_point[0] = starting_x + ((_blockWidth-1)/2);
		center_point[1] = starting_y + ((_blockHeight-1)/2);
		
		return center_point;
	}
	
	
	// Gets the starting point of a block given it's center point
	public int[] getStartingPoint(int[] center_point){
		int center_x = center_point[0];
		int center_y = center_point[1];
		int[] starting_point = new int[2];
		starting_point[0] = center_x - ((_blockWidth-1)/2);
		starting_point[1] = center_y - ((_blockHeight-1)/2);
		
		return starting_point;
	}

}
