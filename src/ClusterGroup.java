import java.util.ArrayList;


public class ClusterGroup {
	
	private static int _width;
	private static int _height;
	private Integer[][] _backProjectedArray;
	private Boolean[][] _visitedArray;
	private Point[][] _pointsArray;
	private ArrayList<ArrayList<Point>> _listOfClusters;
	private ArrayList<Integer[]> _listOfRectangles;
	private Histogram _searchHistogram;
	private static int _valueThreshold = 30;
	private static int _radius = 1;
	private static int _minClusterSize = 100;
	private static int _range = 1;
	private static int _maxBin;
		
	public ClusterGroup(Integer[][] BPArray, int imageWidth, int imageHeight, int maxBin, Double[][][] hsvImage){
		
		_width = imageWidth;
		_height = imageHeight;
		_backProjectedArray = BPArray;
		_visitedArray = new Boolean[_width][_height];
		_pointsArray = new Point[_width][_height];
		_listOfClusters = new ArrayList<ArrayList<Point>>();
		_listOfRectangles = new ArrayList<Integer[]>();
		_searchHistogram = new Histogram();
		_maxBin = maxBin;
		initializeVisitedArray();
		initializePointsArray();
		
		Integer[] binRange = _searchHistogram.getBinRange(_maxBin, _range);
				
		for (int y=0; y<_height; y++){
			for (int x=0; x<_width; x++){
				
				// Get value at point x, y from back projected image
				int value = _backProjectedArray[x][y];
				float[] hsv = new float[3];
				Double h_value = new Double(hsvImage[x][y][0]);
				Double s_value = new Double(hsvImage[x][y][1]);
				Double v_value = new Double(hsvImage[x][y][2]);
				hsv[0] = h_value.floatValue();
				hsv[1] = s_value.floatValue();
				hsv[2] = v_value.floatValue();
				
				int bin = _searchHistogram.getHBin(hsv);
				boolean containsBin = false;
				
				// Cluster must start with range surrounding the max color value from logo histogram
				for (int i=0; i<binRange.length; i++){
					if (bin == binRange[i]){
						containsBin = true;
						break;
					}
				}
	
				// If point has not been visited 
				if ((!_visitedArray[x][y]) && containsBin){
	
					// If value is above the threshold
					if (value > _valueThreshold){
						// Start a new cluster
						Cluster cluster = new Cluster();
						cluster.initRectangle(x, y);
						cluster.addPoint(_pointsArray[x][y]);
						
						// While the cluster is not empty, do a depth-first search
						while(!cluster.isEmpty()){
							Point p = cluster.removePoint();
							DFS(p, cluster);
						}
						
						// Add cluster to listOfClusters if cluster size is >= the minimum cluster size;
						ArrayList<Point> clusterPoints = cluster.getClusterPoints();
						if (clusterPoints.size() >= _minClusterSize){
							_listOfClusters.add(clusterPoints);
							_listOfRectangles.add(cluster.getRectangle());
						}
					}
				}
			}
		}		
	}
	
	
	
	public void DFS(Point p, Cluster cluster) {
		int x = p.x;
		int y = p.y;
		// Add point to cluster and mark as visited
		if (!_visitedArray[x][y]){
			_visitedArray[x][y] = true;
		}
				
		// Get neighbors of point
		// Neighbors returned are only those that contain a value over the threshold
		//   in the back projected image and have not been visited yet
		ArrayList<Point> neighbors = getNeighbors(x, y);
		for (int i=0; i<neighbors.size(); i++){
			Point neighbor = neighbors.get(i);
			
			if (!cluster.contains(neighbor)){
				cluster.addPoint(neighbor);
			}
		}
	}
	
	
	// Returns the neighbors of the given x, y point
	public ArrayList<Point> getNeighbors(int x, int y){
		int x_0 = x - _radius;
		int y_0 = y - _radius;
		int span = (int)Math.round(1 + Math.pow(2, _radius));
		ArrayList<Point> neighbors = new ArrayList<Point>();
		
		for (int j=0; j<span; j++){
			for (int i=0; i<span; i++){
				int x_point = x_0 + i;
				int y_point = y_0 + j;
				
				// If not the given pixel
				if (x_point != x || y_point != y){
					// Check if in bounds
					if (inBounds(x_point, y_point)){
						// Check if value is greater than threshold and point has not been visited before
						int value = _backProjectedArray[x_point][y_point];
						if ((value > _valueThreshold) && !_visitedArray[x_point][y_point]){
							neighbors.add(_pointsArray[x_point][y_point]);
						}
					}
				}
				
			}
		}
		return neighbors;
	}
	
	
	// Determines if a pixel is in bounds
	public boolean inBounds(int x, int y){
		return (x >= 0 && y >= 0 && x < _width && y < _height);
	}
	
	// Initializes every value in the _visitedArray to false
	public void initializeVisitedArray(){
		for (int y=0; y<_height; y++){
			for (int x=0; x<_width; x++){
				_visitedArray[x][y] = false;
			}
		}
	}
	
	// Initializes every value in the _pointsArray to a new Point of the
	//  corresponding x and y coordinates
	public void initializePointsArray(){
		for (int y=0; y<_height; y++){
			for (int x=0; x<_width; x++){
				_pointsArray[x][y] = new Point(x, y);
			}
		}
	}
	
	// Returns the list of rectangle integer arrays
	public ArrayList<Integer[]> getListOfRectangles() {
		return _listOfRectangles;
	}
	
	
	
	
	
	/*  Print functions  */
	// Prints the points contained in a cluster
	public void printClusterPoints(ArrayList<Point> clusterPoints){
		for (int i=0; i<clusterPoints.size(); i++){
			Point p = clusterPoints.get(i);
			System.out.println("x: " + p.x + ", y: " + p.y);
		}
	}
	
	
	// Prints the values corresponding to the given rectangle
	public void printRectangle(Integer[] rectangle){
		int rectangle_x = rectangle[0];
		int rectangle_y = rectangle[1];
		int rectangle_width = rectangle[2];
		int rectangle_height = rectangle[3];
		System.out.println("x: " + rectangle_x + ", y: " + rectangle_y + 
				", width: " + rectangle_width + ", height: " + rectangle_height);
	}
	

}
