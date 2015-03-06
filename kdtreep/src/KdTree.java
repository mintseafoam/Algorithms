/******************************************************************************
 *  Compilation:  javac KdTree.java
 *  Execution:    java KdTree
 *  Dependencies: StdDraw.java Point2D.java RectHV.java
 *
 *  Creates a kd-tree which supports: contains (determines if a specific point
 *  is in the tree), draw, range (returns a queue of points on tree within a
 *  given rectangle), nearest (finds the point on tree closest to given point).
 *  
 *  Draw gives a visual representation of the kd-tree, with vertical splits drawn
 *  in red, horizontal drawn in blue, and points drawn in black.
 ******************************************************************************/
public class KdTree {
  private static final double SCALE_MIN = 0.0;
  private static final double SCALE_MAX = 1.0;

  private KdTreeNode root;
  private int treeSize;

  //Initializes an empty KdTree
  public KdTree() {
    root = null;
    treeSize = 0;
  }

  private class KdTreeNode {
    private Point2D data;
    private KdTreeNode left;
    private KdTreeNode right;

    //Initializes a KdTreeNode with given Point2D data
    public KdTreeNode(Point2D points) {
      data = points;
      left = null;
      right = null;
    }

    Point2D data() {
      return data;
    }

    KdTreeNode left() {
      return left;
    }

    KdTreeNode right() {
      return right;
    }
  }

  //Whether the tree is empty
  public boolean isEmpty() {
    return (size() == 0);
  }

  //The size of the tree
  public int size() {
    return treeSize;
  }

  //Inserts a given point into the tree
  public void insert(Point2D p) {
    if (p == null) {
      throw new java.lang.NullPointerException();
    }
    Point2D add = new Point2D(p.x(), p.y());
    //Inserts given point as root of the tree if the tree is empty
    if (isEmpty()) {
      root = new KdTreeNode(add);
      treeSize++;
    }
    //Calls recursive method to insert if tree is not empty
    else {
      insertR(add, root, true);
    }
  }

  //Recursive method to insert a given point into the tree
  private void insertR(Point2D add, KdTreeNode parent, boolean isEven) {
    if (!(add.equals(parent.data()))) {
      if (isEven) {
        if (add.x() < parent.data().x()) {
          //Inserts node to left child if x coordinate is smaller than the parent, which was split vertically
          if (parent.left() == null) {
            parent.left = new KdTreeNode(add);
            treeSize++;
          }
          else {
            insertR(add, parent.left(), !isEven);
          }
        }
        else {
          //Inserts node to right child if x coordinate is greater than or equal to the parent, which was split vertically
          if (parent.right() == null) {
            parent.right = new KdTreeNode(add);
            treeSize++;
          }
				else {
					insertR(add, parent.right(), !isEven);
				}
			  }
		  }
		  else {
			if (add.y() < parent.data().y()) {
				//Inserts node to left child if y coordinate is smaller than the parent, which was split horizontally
				if(parent.left() == null) {
					parent.left = new KdTreeNode(add);
					treeSize++;
				}
				else {
					insertR(add, parent.left(), !isEven);
				}
			}
			else {
				//Inserts node to right child if y coordinate is greater than or equal to the parent, which was split horizontally
				if (parent.right() == null) {
					parent.right = new KdTreeNode(add);
					treeSize++;
				}
				else {
					insertR(add, parent.right(), !isEven);
				}
			}
		  }
	  }
	}
	
	//Whether a given point is in the tree
	public boolean contains(Point2D p) {
		boolean answer = false;
		if (p == null) {
			throw new java.lang.NullPointerException();
		}
		if (isEmpty()) {
			return false;
		}
		if (p.equals(root.data())) {
			return true;
		}
		//If the x coordinate (vertical split on odd level) is smaller than the root recurse left
		if (p.x() < root.data().x()) {
			answer = containsR(p, root.left(), true);
		}
		//If the x coordinate (vertical split on odd level) is greater than or equal to the root recurse right
		else {
			answer = containsR(p, root.right(), true);
		}
		return answer;
	}
	
	//Recursive method that finds if a given point is in the tree
	private boolean containsR(Point2D p, KdTreeNode cur, boolean isEven) {
		boolean answer = false;
		//If a null node is reached, the point is not in the tree
		if (cur == null) {
			return false;
		}
		else if (p.equals(cur.data())) {
			return true;
		}
		else if (isEven) {
			//If the given y coordinate (even level split horizontally) is less than the current node's, recurse left
			if(p.y() < cur.data().y()) {
				answer = containsR(p, cur.left(), !isEven);
			}
			//If the given y coordinate (even level split horizontally) is greater than or equal to the current node's, recurse right
			else {
				answer = containsR(p, cur.right(), !isEven);
			}
		}
		else {
			//If the given x coordinate (odd level split vertically) is less than the current node's, recurse left
			if (p.x() < cur.data().x()) {
				answer = containsR(p, cur.left(), !isEven);
			}
			//If the given x coordinate (even level split vertically) is greater than or equal to the current node's, recurse right
			else {
				answer = containsR(p, cur.right(), !isEven);
			}
		}
		return answer;
	}
	
	//Visual representation of the kd-tree is drawn with vertical splits in red, horizontal splits in blue, and points in black
	public void draw() {
		if (!(isEmpty())) {
		  Point2D subtreeMin = new Point2D(SCALE_MIN, SCALE_MIN);
		  Point2D subtreeMax = new Point2D(SCALE_MAX, SCALE_MAX);
		  Point2D drawA = new Point2D(root.data().x(), subtreeMin.y());
		  Point2D drawB = new Point2D(root.data().x(), subtreeMax.y());
		  StdDraw.setPenRadius();
		  StdDraw.setPenColor(StdDraw.RED);
		  //Draws a vertical line at the root's x coordinate from the minimum to maximum allowed range
		  drawA.drawTo(drawB);
		  StdDraw.setPenColor(StdDraw.BLACK);
		  StdDraw.setPenRadius(.01);
		  root.data.draw();
		  drawR(root.left(), true, root.data(), subtreeMin, subtreeMax);
		  drawR(root.right(), true, root.data(), subtreeMin, subtreeMax);
		}
	}
	
	//Recursive method that traverses and draws the tree in a pre-order depth first search
	private void drawR(KdTreeNode cur, boolean isEven, Point2D prev, Point2D subtreeMin, Point2D subtreeMax) {
		if (cur != null) {
			Point2D drawA, drawB;
			if (isEven) {
				StdDraw.setPenRadius();
				StdDraw.setPenColor(StdDraw.BLUE);
				//Even steps are split horizontally and drawn in blue
				if (cur.data().x() < prev.x()) {
					//If the current x is less than the parent x coordinate (the parent was split vertically) it is to the left of the parent
					drawA = new Point2D(subtreeMin.x(), cur.data().y());
					drawB = new Point2D(prev.x(), cur.data().y());
					//The line is drawn from the minimum x coordinate in the subtree thus far to the parent x coordinate
					subtreeMax = new Point2D(prev.x(), subtreeMax.y());
					//The maximum x coordinate for this subtree is set to be the x coordinate of the parent (where the vertical split occurred)
				}
				else {
					//If the current x is greater than or equal to the parent x coordinate (the parent was split vertically) it is to the right of the parent
					drawA = new Point2D(subtreeMax.x(), cur.data().y());
					drawB = new Point2D(prev.x(), cur.data().y());
					//The line is drawn from the parent x coordinate to the maximum x coordinate in the subtree thus far
					subtreeMin = new Point2D(prev.x(), subtreeMin.y());
					//The minimum x coordinate for this subtree is set to be the x coordinate of the parent (where the vertical split occurred)
				}
				drawA.drawTo(drawB);
			}
			else {
				//Odd steps are split vertically and drawn in red
				StdDraw.setPenRadius();
				StdDraw.setPenColor(StdDraw.RED);
				//If the current y is less than the parent y coordinate (the parent was split horizontally) it is below the parent
				if (cur.data().y() < prev.y()) {
					//The line is drawn from the minimum y coordinate in the subtree thus far to the parent y coordinate
					drawA = new Point2D(cur.data().x(), subtreeMin.y());
					drawB = new Point2D(cur.data().x(), prev.y());
					//The maximum y coordinate for this subtree is set to be the y coordinate of the parent (where the horizontal split occurred)
					subtreeMax = new Point2D(subtreeMax.x(), prev.y());
				}
				//If the current y is greater than or equal to the parent y coordinate (the parent was split horizontally) it is above the parent
				else {
					//The line is drawn from the parent y coordinate to the maximum y coordinate in the subtree thus far
					drawA = new Point2D(cur.data().x(), subtreeMax.y());
					drawB = new Point2D(cur.data().x(), prev.y());
					//The minimum y coordinate for this subtree is set to be the y coordinate of the parent (where the horizontal split occurred)
					subtreeMin = new Point2D(subtreeMin.x(), prev.y());
				}
				drawA.drawTo(drawB);
			}
			//The point is drawn in black
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(.01);
			cur.data().draw();
			//The pre-order DFS is continued
			drawR(cur.left(), !isEven, cur.data(), subtreeMin, subtreeMax);
			drawR(cur.right(), !isEven, cur.data(), subtreeMin, subtreeMax);
		}
	}
	
	//Returns a queue of the Point2D points on the tree within a given rectangle
	public Iterable<Point2D> range(RectHV rect) {
		if (rect == null) {
			throw new java.lang.NullPointerException();
		}
		Queue<Point2D> inRange = new Queue<Point2D>();
		if (!isEmpty()) {
		  if (rect.contains(root.data())) {
			inRange.enqueue(root.data());
		  }
		  //Creates the left half of the rectangle encompassing all points split by the root's x coordinate
		  RectHV divide = new RectHV(SCALE_MIN, SCALE_MIN, root.data().x(), SCALE_MAX);
		  //If the given rectangle intersects the split rectangle, the split rectangle may contain points in the given one and should be searched
		  if (rect.intersects(divide)) {
			rangeR(rect, true, root.left(), divide, inRange);
		  }
		  //Creates the right half of the rectangle encompassing all points split by the root's x coordinate
		  divide = new RectHV(root.data().x(), SCALE_MIN, SCALE_MAX, SCALE_MAX);
		  //If the given rectangle intersects the split rectangle, the split rectangle may contain points in the given one and should be searched
		  if (rect.intersects(divide)) {
			rangeR(rect, true, root.right(), divide, inRange);
		  }
		}
		return inRange;
	}
	
	//Recursive method returns a queue of the Point2D points on the tree within a given rectangle
  private void rangeR(RectHV rect, boolean isEven, KdTreeNode cur, RectHV divide, Queue<Point2D> inRange) {
		if (cur != null) {
			RectHV div;
			if (rect.contains(cur.data())) {
				inRange.enqueue(cur.data());
			}
		  if (isEven) {
			  //Divides current node's rectangle into its top half based on the current node's y coordinate (even steps are split horizontally)
			  div = new RectHV(divide.xmin(), divide.ymin(), divide.xmax(), cur.data().y());
			  //If the given rectangle intersects the split rectangle, the split rectangle may contain points in the given one and should be searched
			  if (rect.intersects(div)) {
				  rangeR(rect, !isEven, cur.left(), div, inRange);
			  }
			  //Divides current node's rectangle into its bottom half based on the current node's y coordinate (even steps are split horizontally)
			  div = new RectHV(divide.xmin(), cur.data().y(), divide.xmax(), divide.ymax());
			  //If the given rectangle intersects the split rectangle, the split rectangle may contain points in the given one and should be searched
			  if (rect.intersects(div)) {
				  rangeR(rect, !isEven, cur.right(), div, inRange);
			  }
		  }
		  else {
			  //Divides current node's rectangle into its left side based on the current node's x coordinate (odd steps are split vertically)
			  div = new RectHV(divide.xmin(), divide.ymin(), cur.data().x(), divide.ymax());
			  //If the given rectangle intersects the split rectangle, the split rectangle may contain points in the given one and should be searched
			  if (rect.intersects(div)) {
				  rangeR(rect, !isEven, cur.left(), div, inRange);
			  }
			  //Divides current node's rectangle into its right side based on the current node's x coordinate (odd steps are split vertically)
			  div = new RectHV(cur.data().x(), divide.ymin(), divide.xmax(), divide.ymax());
			  //If the given rectangle intersects the split rectangle, the split rectangle may contain points in the given one and should be searched
			  if (rect.intersects(div)) {
				  rangeR(rect, !isEven, cur.right, div, inRange);
			  }
		  }
		}
	}
	
	//Returns the Point2D point in the tree that is closest to a given Point2D point
	public Point2D nearest(Point2D p) {
		if (p == null) {
			throw new java.lang.NullPointerException();
		}
		Point2D closestToP = null;
		if (!isEmpty()) {
			closestToP = root.data();
			//The shortest distance from the given point to the vertical split
			Point2D shortestToSplit = new Point2D(closestToP.x(), p.y());
			//If the given point x coordinate is smaller than the closest point so far it is to the left of the vertical split
		    if (p.x() < closestToP.x()) {
		    	//Recursively search the left subtree
		    	closestToP = nearestR(p, root.left(), true, closestToP);
		    	//If the shortest distance from the given point to the vertical split is greater than the distance from the given point to the closest point
		    	//There is no need to search the right subtree
		    	if (p.distanceTo(closestToP) > p.distanceTo(shortestToSplit)) {
		    		closestToP = nearestR(p, root.right(), true, closestToP);
		    	}
		    }
		    //If the given point x coordinate is greater than or equal to the closest point so far it is to the right of the vertical split
		    else {
		    	//Recursively search the right subtree
		    	closestToP = nearestR(p, root.right(), true, closestToP);
		    	//If the shortest distance from the given point to the vertical split is greater than the distance from the given point to the closest point
		    	//There is no need to search the left subtree
		    	if (p.distanceTo(closestToP) > p.distanceTo(shortestToSplit)) {
		    		closestToP = nearestR(p, root.left(), true, closestToP);
		    	}
		    }
		}
		return closestToP;
	}
	
	//Recursively finds the Point2D point in the tree that is closest to the given Point2D point
	private Point2D nearestR(Point2D p, KdTreeNode cur, boolean isEven, Point2D closestToP) {
		if (cur != null) {
			if (p.distanceTo(cur.data()) < p.distanceTo(closestToP)) {
				closestToP = cur.data();
			}
			if (isEven) {
				//The shortest distance from the given point to the horizontal split (even level)
				Point2D shortestToSplit = new Point2D(p.x(), cur.data().y());
				//If the given point y coordinate is smaller than the closest point so far it is below the horizontal split
				if (p.y() < cur.data().y()) {
					//Recursively search the left subtree
					closestToP = nearestR(p, cur.left(), !isEven, closestToP);
					//If the shortest distance from the given point to the horizontal split is greater than
					//the distance from the given point to the closest found point
			    	//There is no need to search the right subtree
					if (p.distanceTo(closestToP) > p.distanceTo(shortestToSplit)) {
						closestToP = nearestR(p, cur.right(), !isEven, closestToP);
					}
				}
				//If the given point y coordinate is greater than or equal to the closest point so far it is above the horizontal split
				else {
					//Recursively search the right subtree
					closestToP = nearestR(p, cur.right(), !isEven, closestToP);
					//If the shortest distance from the given point to the horizontal split is greater than
					//the distance from the given point to the closest found point
			    	//There is no need to search the left subtree
					if (p.distanceTo(closestToP) > p.distanceTo(shortestToSplit)) {
						 closestToP = nearestR(p, cur.left(), !isEven, closestToP);
					}
				}
			}
			else {
				//The shortest distance from the given point to the vertical split (odd level)
				Point2D shortestToSplit = new Point2D(cur.data().x(), p.y());
				//If the given point x coordinate is smaller than the closest point so far it is to the left of the vertical split
				if (p.x() < cur.data().x()) {
					//Recursively search the left subtree
					closestToP = nearestR(p, cur.left(), !isEven, closestToP);
					//If the shortest distance from the given point to the vertical split is greater than
					//the distance from the given point to the closest found point
			    	//There is no need to search the right subtree
					if (p.distanceTo(closestToP) > p.distanceTo(shortestToSplit)) {
						closestToP = nearestR(p, cur.right(), !isEven, closestToP);
					}
				}
				//If the given point x coordinate is greater than or equal to the closest point so far it is to the right of the vertical split
				else {
					//Recursively search the right subtree
					closestToP = nearestR(p, cur.right(), !isEven, closestToP);
					//If the shortest distance from the given point to the vertical split is greater than
					//the distance from the given point to the closest found point
			    	//There is no need to search the left subtree
					if (p.distanceTo(closestToP) > p.distanceTo(shortestToSplit)) {
						closestToP = nearestR(p, cur.left(), !isEven, closestToP);
					}
				}
			}
		}
		return closestToP;
	}

  public static void main(String[] args) {
    //Testing as needed
  }
}
