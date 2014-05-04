import java.util.ArrayList;


public class Clusters {
	
	private int _imageWidth;
	private int _imageHeight;
	private Integer[][] _backProjectedArray;
	private int[][] clusterArray;
	private ArrayList<ArrayList<Integer[]>> _listOfClusters;
	private int minClusterSize = 40;
	private int radius = 1;
	private ArrayList<Integer[]> allRectangles;
	private ArrayList<Integer[]> listOfRectangles;
	
	
	public Clusters(Integer[][] backProjectedArray, int imageWidth, int imageHeight ){
		
		_backProjectedArray = backProjectedArray;
		_imageWidth = imageWidth;
		_imageHeight = imageHeight;
		clusterArray = new int[_imageWidth][_imageHeight];
		ArrayList<ArrayList<Integer[]>> _listOfClusters = new ArrayList<ArrayList<Integer[]>>();
		for (int y=0; y<_imageHeight; y++){
			for (int x=0; x<_imageWidth; x++){
				clusterArray[x][y] = -1;
			}
		}
		
	//	Integer[][] testArray = {{1, 1, 0, 0}, {1, 0, 0, 1}, {0, 0, 1, 1}};
	//	_backProjectedArray = testArray;
		
		allRectangles = new ArrayList<Integer[]>();
		listOfRectangles = new ArrayList<Integer[]>();
		int cluster_number = 0;

		// Returns a list of the clusters of white points in the back projected image		
		for (int y=0; y<_imageHeight; y++){
			for (int x=0; x<_imageWidth; x++){
								
				// for each pixel in the back-projected image, check for clustering
				int value = _backProjectedArray[x][y];	
				System.out.println("Value at " + x + ", " + y + " : " + value);
				if (value != 0 && clusterArray[x][y] == -1){
					//System.out.println("Pixel with value");
					ArrayList<Integer[]> cluster = createCluster();
					Integer[] point = {x, y};
					Integer[] rectangle = createRectangle(point);
					allRectangles.add(cluster_number, rectangle);
					clusterArray[x][y] = cluster_number;
					addToCluster(point, cluster, cluster_number);
					clusterPoints(point, cluster, cluster_number);
					
					if (cluster.size() > minClusterSize){
						_listOfClusters.add(cluster);
						listOfRectangles.add(allRectangles.get(cluster_number));
						cluster_number++;
					}
					//System.out.println("num rectangles: " + allRectangles.size());

				}
			}
		}
	//	System.out.println("Cluster array");
		//printClusterArray();
		System.out.println("Number of clusters total: " + _listOfClusters.size());
	//	printListOfRectangles();
	}
	
	
	// Creates a cluster
	public ArrayList<Integer[]> createCluster(){		
		ArrayList<Integer[]> cluster = new ArrayList<Integer[]>();	
		return cluster;
	}
	
	
	// Adding points to the cluster
	public void clusterPoints(Integer[] point, ArrayList<Integer[]> cluster, int cluster_number){
		// Get nearest neighbors of point that have a value in them
		ArrayList<Integer[]> neighbors = nearestNeighbors(point);
		for (int i=0; i<neighbors.size(); i++){
			Integer[] neighbor = neighbors.get(i);
			int x = neighbor[0];
			int y = neighbor[1];
			if (clusterArray[x][y] != cluster_number){
				addToCluster(neighbor, cluster, cluster_number);
				clusterPoints(neighbor, cluster, cluster_number);
			}			
		}
		
	}
	
	
	
	
	public void addToCluster(Integer[] point, ArrayList<Integer[]> cluster, int cluster_number){
		int x = point[0];
		int y = point[1];
		Integer[] cluster_point = {x, y};
		
		// Add point to cluster
		cluster.add(cluster_point);
		
		// Mark as visited in cluster Array
		clusterArray[x][y] = cluster_number;	
		
		// Update rectangle coordinates
		updateRectangleCoordinates(cluster_point, cluster_number);
	}
	
	// Gets all in-bounds nearest neighbors of a pixel given the spanning radius
	public ArrayList<Integer[]> nearestNeighbors(Integer[] point){
		ArrayList<Integer[]> neighbors = new ArrayList<Integer[]>();
		int x = point[0];
		int y = point[1];
		int x_corner = x - radius;
		int y_corner = y - radius;
		int span = (int)Math.round(1 + Math.pow(2, radius));
		
		for (int i=0; i<span; i++){
			for (int j=0; j<span; j++){
				int x_point = x_corner + i;
				int y_point = y_corner + j;
				
				// If not the given pixel
				if (x_point != x || y_point != y){
					
					// Check if in bounds
					if (inBounds(x_point, y_point)){
						
						// Check if has value in backProjected Image
						int value = _backProjectedArray[x_point][y_point];
						if (value != 0 && clusterArray[x_point][y_point] == -1){
							Integer[] neighbor = {x_point, y_point};
							neighbors.add(neighbor);
							
						}
					}
				}
			}
		}
		System.out.println("Neighbors for point " + x + ", " + y);
		printCluster(neighbors);
		return neighbors;
	}
	
	
	// Determines if a pixel is in bounds
	public boolean inBounds(int x, int y){
		return (x >= 0 && y >= 0 && x < _imageWidth && y < _imageHeight);
	}
	
	// Creates a rectangle
	public Integer[] createRectangle(Integer[] point){	
		Integer[] rectangle = {point[0], point[1], 0, 0};
		return rectangle;
	}
	
	// Updates 
	public void updateRectangleCoordinates(Integer[] point, int cluster_number){
		Integer[] rectangle = allRectangles.get(cluster_number);
		int rectangle_x = rectangle[0];
		int rectangle_y = rectangle[1];
		int rectangle_width = rectangle[2];
		int rectangle_height = rectangle[3];
		
		int point_x = point[0];
		int point_y = point[1];
		
		// Update x and width
		if (point_x < rectangle_x){
			rectangle[0] = point_x;
		} else {
			if (point_x - rectangle_x > rectangle_width){
				rectangle[2] = point_x - rectangle_x;
			}
		}
		
		// Update y and height
		if (point_y < rectangle_y){
			rectangle[1] = point_y;
		} else {
			if (point_y - rectangle_y > rectangle_height){
				rectangle[3] = point_y - rectangle_y;
			}
		}
		//return rectangle;
		
		allRectangles.set(cluster_number, rectangle);
	}
	


	// Prints a cluster
		public void printCluster(ArrayList<Integer[]> cluster){
			for (int i=0; i<cluster.size(); i++){
				Integer[] points = cluster.get(i);
				int x = points[0];
				int y = points[1];
				System.out.println("x: " + x + ", y: " + y);
			}
			System.out.println("");
		}
	
	
	// Prints the cluster array
	public void printClusterArray() {
		for (int y=0; y<_imageHeight; y++){
			for (int x=0; x<_imageWidth; x++){
				int value = clusterArray[x][y];
				if (value == -1){
					System.out.print(clusterArray[x][y] + "  ");
				} else {
					System.out.print(" " + clusterArray[x][y] + "  ");
				}
				
			}
			System.out.println("");
		}
	}
	
	// Prints the listofClusters
	public void printListOfClusters(){
		for (int i=0; i<_listOfClusters.size(); i++){
			ArrayList<Integer[]> cluster = _listOfClusters.get(i);
			printCluster(cluster);
		}
	}
	
	
	// Print rectangle
	public void printRectangle(int rectangle_number){
		Integer[] rectangle = listOfRectangles.get(rectangle_number);
		int rectangle_x = rectangle[0];
		int rectangle_y = rectangle[1];
		int rectangle_width = rectangle[2];
		int rectangle_height = rectangle[3];
		System.out.println("Rectangle x: " + rectangle_x + ", y: " + rectangle_y + 
				", width: " + rectangle_width + ", height: " + rectangle_height);
	}
	
	// Prints the listofClusters
	public void printListOfRectangles(){
		for (int i=0; i<listOfRectangles.size(); i++){
			printRectangle(i);
		}
	}
	
	
	public ArrayList<Integer[]> getRectangles(){
		//listOfRectangles.remove(0);
		return listOfRectangles;
	}
	
	
}
