import java.util.ArrayList;
import java.util.Stack;


public class Cluster {
	
	private ArrayList<Point> _clusterPoints;
	private Stack<Point> _cluster;
	private Integer[] _rectangle = {0, 0, 0, 0};
	
	
	// A Cluster is a Stack of Points
	public Cluster() {
		_cluster = new Stack<Point>();
		_clusterPoints = new ArrayList<Point>();
	}
	
	
	
	
	// Returns the list of cluster points
	public ArrayList<Point> getClusterPoints(){
		return _clusterPoints;
	}
	
	// Returns the rectangle Integer array of the cluster
	public Integer[] getRectangle(){
		return _rectangle;
	}
	
	
	

	// Adds the given point to the cluster
	public void addPoint(Point p){
		_cluster.push(p);
		_clusterPoints.add(p);
		
		// Updates the rectangle coordinates given new point p
		updateRectangle(p);
	}
	
	
	// Removes the Point on the top of the cluster stack
	public Point removePoint(){
		return _cluster.pop();
	}	

	// Returns the size of the cluster
	public int size(){
		return _cluster.size();
	}
	
	// Checks whether cluster is empty
	public boolean isEmpty(){
		return _cluster.isEmpty();
	}
	
	// Checks whether cluster contains given point
	public boolean contains(Point p){
		return (_cluster.contains(p));
	}
	
	
	
	// Initializes the rectangle for this cluster given the first point
	public void initRectangle(int x, int y){
		_rectangle[0] = x;
		_rectangle[1] = y;
		_rectangle[2] = 1;
		_rectangle[3] = 1;
	}
	
	
	// Updates the coordinates of the rectangle array given new point p
	public void updateRectangle(Point p){
		int rectangle_x = _rectangle[0];
		int rectangle_y = _rectangle[1];
		int rectangle_width = _rectangle[2];
		int rectangle_height = _rectangle[3];
		
		int point_x = p.x;
		int point_y = p.y;
		
		// Update x and width
		if (point_x < rectangle_x){
			_rectangle[0] = point_x;
		} else {
			int width = point_x - rectangle_x + 1;
			if (width > rectangle_width){
				_rectangle[2] = width;
			}
		}
				
		// Update y and height
		if (point_y < rectangle_y){
			_rectangle[1] = point_y;
		} else {
			int height = point_y - rectangle_y + 1;
			if (height > rectangle_height){
				_rectangle[3] = height;
			}
		}
		
	}
	
	
	
	/*  Print functions  */
	
	// Prints the cluster
		public void print(){
			System.out.println("Cluster");
			for (int i=0; i<_cluster.size(); i++){
				Point point = _cluster.get(i);
				System.out.println("x: " + point.x + ", y: " + point.y);
			}
		}
		
}

