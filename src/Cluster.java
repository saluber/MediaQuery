import java.util.ArrayList;
import java.util.Stack;


public class Cluster {
	
	ArrayList<Point> clusterPoints;
	Stack<Point> cluster;
	Integer[] rectangle = {0, 0, 0, 0};
	
	public Cluster() {
		cluster = new Stack<Point>();
		clusterPoints = new ArrayList<Point>();
	}
	
	

	
	
	public void addPoint(Point p){
		cluster.push(p);
		clusterPoints.add(p);
		
		updateRectangle(p);
	}
	
	public Point removePoint(){
		return cluster.pop();
	}
	

	public int size(){
		return cluster.size();
	}
	
	public void print(){
		System.out.println("Cluster");
		for (int i=0; i<cluster.size(); i++){
			Point point = cluster.get(i);
			System.out.println("x: " + point.x + ", y: " + point.y);
		}
		//System.out.println("");
	}
	
	public boolean isEmpty(){
		return cluster.isEmpty();
	}
	
	public boolean contains(Point p){
		return (cluster.contains(p));
	}
	
	public ArrayList<Point> getClusterPoints(){
		return clusterPoints;
	}
	
	public void initRectangle(int x, int y){
		rectangle[0] = x;
		rectangle[1] = y;
		rectangle[2] = 1;
		rectangle[3] = 1;
	}
	
	public void updateRectangle(Point p){
		int rectangle_x = rectangle[0];
		int rectangle_y = rectangle[1];
		int rectangle_width = rectangle[2];
		int rectangle_height = rectangle[3];
		
		int point_x = p.x;
		int point_y = p.y;
		
		// Update x and width
		if (point_x < rectangle_x){
			rectangle[0] = point_x;
		} else {
			int width = point_x - rectangle_x + 1;
			if (width > rectangle_width){
				rectangle[2] = width;
			}
		}
				
		// Update y and height
		if (point_y < rectangle_y){
			rectangle[1] = point_y;
		} else {
			int height = point_y - rectangle_y + 1;
			if (height > rectangle_height){
				rectangle[3] = height;
			}
		}
		
	}
	
	public Integer[] getRectangle(){
		return rectangle;
	}
	
}

