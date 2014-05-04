import java.util.ArrayList;


public class ClusterGroup {
	
	Integer[][] backProjectedArray;
	int HEIGHT;
	int WIDTH;
	Boolean[][] visitedArray;
	int RADIUS;
	int MIN_CLUSTER_SIZE;
	ArrayList<ArrayList<Point>> listOfClusters;
	Point[][] pointsArray;
	ArrayList<Integer[]> listOfRectangles;
	
	public ClusterGroup(Integer[][] BPArray, int imageWidth, int imageHeight){
		
		backProjectedArray = BPArray;
		WIDTH = imageWidth;
		HEIGHT = imageHeight;
		RADIUS = 1;
		MIN_CLUSTER_SIZE = 50;
		listOfClusters = new ArrayList<ArrayList<Point>>();
		listOfRectangles = new ArrayList<Integer[]>();
		visitedArray = new Boolean[WIDTH][HEIGHT];
		pointsArray = new Point[WIDTH][HEIGHT];
		initializeVisitedArray();
		initializePointsArray();
		
		for (int y=0; y<HEIGHT; y++){
			for (int x=0; x<WIDTH; x++){
				
				int value = backProjectedArray[x][y];
				
				// If point has not been visited
				if (!visitedArray[x][y]){
				
					// If value is not 0
					if (value != 0){
						// Start a new cluster
						Cluster cluster = new Cluster();
						cluster.initRectangle(x, y);
						cluster.addPoint(pointsArray[x][y]);
						
						while(!cluster.isEmpty()){
							Point p = cluster.removePoint();
							cluster = DFS(p, cluster);
						}
						
						ArrayList<Point> clusterPoints = cluster.getClusterPoints();
						
						
						// Add cluster to listOfClusters if cluster size is >= MIN_CLUSTER_SIZE;
						if (clusterPoints.size() >= MIN_CLUSTER_SIZE){
							listOfClusters.add(clusterPoints);
							listOfRectangles.add(cluster.getRectangle());
						}
					}
				}
			}
		}
		
		for (int i=0; i<listOfClusters.size(); i++){
			System.out.println("Rectangle " + i);
			printRectangle(listOfRectangles.get(i));
			System.out.println("");
		} 
		System.out.println("Number of clusters: " + listOfClusters.size());
		System.out.println("Number of rectangles: " + listOfRectangles.size());
		
	}
	
	
	
	public Cluster DFS(Point p, Cluster cluster) {
		int x = p.x;
		int y = p.y;
		// Add point to cluster and mark as visited
		if (!visitedArray[x][y]){
			visitedArray[x][y] = true;
		}
				
		// Get neighbors of point
		// Neighbors returned are only those that contain a value in the back projected image
		// and have not been visited yet
		ArrayList<Point> neighbors = getNeighbors(x, y);
		
		for (int i=0; i<neighbors.size(); i++){
			Point neighbor = neighbors.get(i);
			if (!cluster.contains(neighbor)){
				cluster.addPoint(neighbor);
			}
		}
		return cluster;
	}
	
	
	public ArrayList<Point> getNeighbors(int x, int y){
		int x_0 = x - RADIUS;
		int y_0 = y - RADIUS;
		int span = (int)Math.round(1 + Math.pow(2, RADIUS));
		ArrayList<Point> neighbors = new ArrayList<Point>();
		
		for (int j=0; j<span; j++){
			for (int i=0; i<span; i++){
				int x_point = x_0 + i;
				int y_point = y_0 + j;
				
				// If not the given pixel
				if (x_point != x || y_point != y){
					// Check if in bounds
					if (inBounds(x_point, y_point)){
						// Check if has value in backProjected Image and has not been visited before
						int value = backProjectedArray[x_point][y_point];
						if (value != 0 && !visitedArray[x_point][y_point]){
							neighbors.add(pointsArray[x_point][y_point]);
						}
					}
				}
				
			}
		}
		return neighbors;
	}
	
	// Determines if a pixel is in bounds
	public boolean inBounds(int x, int y){
		return (x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT);
	}
	
	public void initializeVisitedArray(){
		for (int y=0; y<HEIGHT; y++){
			for (int x=0; x<WIDTH; x++){
				visitedArray[x][y] = false;
			}
		}
	}
	
	public void initializePointsArray(){
		for (int y=0; y<HEIGHT; y++){
			for (int x=0; x<WIDTH; x++){
				pointsArray[x][y] = new Point(x, y);
			}
		}
	}
	
	
	public void printClusterPoints(ArrayList<Point> clusterPoints){
		for (int i=0; i<clusterPoints.size(); i++){
			Point p = clusterPoints.get(i);
			System.out.println("x: " + p.x + ", y: " + p.y);
		}
	}
	
	
	public void printRectangle(Integer[] rectangle){
		int rectangle_x = rectangle[0];
		int rectangle_y = rectangle[1];
		int rectangle_width = rectangle[2];
		int rectangle_height = rectangle[3];
		System.out.println("x: " + rectangle_x + ", y: " + rectangle_y + 
				", width: " + rectangle_width + ", height: " + rectangle_height);
	}
	
	
	public ArrayList<Integer[]> getListOfRectangles() {
		return listOfRectangles;
	}
	
	

}