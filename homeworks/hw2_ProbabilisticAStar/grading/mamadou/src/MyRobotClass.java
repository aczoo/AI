import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import world.Robot;

public class MyRobotClass extends Robot {
	NodeList open = new NodeList();
	NodeList closed = new NodeList();
	HashMap<Point, String> pingMap = new HashMap<Point, String>();
	Point dest;
	int rows;
	int cols;
	boolean uncertainty;

	public void passInfo(Point end, int row, int col, boolean uncertain) {
		this.dest = end;
		this.rows = row;
		this.cols = col;
		this.uncertainty = uncertain;
	}

	@Override
	public void travelToDestination() {
		System.out.println("Uncertainty is set to "+uncertainty+".");
		if (uncertainty == true) {
			Scanner input = new Scanner(System.in);
			System.out.println("Please input an an integer value to set pingLimit. If unsure, please enter 40.");
			int pingLimit = input.nextInt();
			int pingcount = 0;
			pingMap.put(super.getPosition(), "S");
			Node start = new Node(super.getPosition(), "S");
			start.f = 0;
			start.g = 0;
			// String ping = "null";
			open.list.add(start);

			while (true) {
				// printList(closed, "Closed List");
				// System.out.println("Ping Count: "+pingcount);
				// System.out.println("Ping Count (method): "+this.getNumPings());
				if (pingcount > pingLimit || open.list.size() == 0) {
					move();
					open.list.clear();
					closed.list.clear();
					pingcount = 0;
					Node currentPos = new Node(super.getPosition(), "S");
					currentPos.f = 0;
					currentPos.g = 0;
					open.list.add(currentPos);
					pingMap.clear();
				}
				int least = open.getLeast();
				Node currNode = open.list.get(least);
				open.list.remove(least);
				if (currNode.pos.equals(dest)) {
					// System.out.println("DONE! If code doesn't work fix this line");
					finished();
					break;
				} else {

					// ping = "null";
					closed.list.add(currNode);
					ArrayList<Point> neighbors = new ArrayList<Point>();
					NodeList newNodes = new NodeList();
					neighbors
							.add(new Point(currNode.pos.x + 1, currNode.pos.y));
					neighbors
							.add(new Point(currNode.pos.x - 1, currNode.pos.y));
					neighbors
							.add(new Point(currNode.pos.x, currNode.pos.y + 1));
					neighbors
							.add(new Point(currNode.pos.x, currNode.pos.y - 1));
					neighbors.add(new Point(currNode.pos.x - 1,
							currNode.pos.y - 1));
					neighbors.add(new Point(currNode.pos.x - 1,
							currNode.pos.y + 1));
					neighbors.add(new Point(currNode.pos.x + 1,
							currNode.pos.y + 1));
					neighbors.add(new Point(currNode.pos.x + 1,
							currNode.pos.y - 1));

					for (int i = 0; i < neighbors.size(); i++) {
						// couldn't used java's null
						String ping = "null";
						if (pingMap.containsKey(neighbors.get(i))) {
							ping = pingMap.get(neighbors.get(i));
						} else {
							// constrain ping to avoid null pings
							if (neighbors.get(i).x >= 0
									&& neighbors.get(i).x < this.rows
									&& neighbors.get(i).y >= 0
									&& neighbors.get(i).y < this.cols) {
								String ping1 = super.pingMap(neighbors.get(i));
								String ping2 = super.pingMap(neighbors.get(i));
								String ping3 = super.pingMap(neighbors.get(i));
								String ping4 = super.pingMap(neighbors.get(i));
								String ping5 = super.pingMap(neighbors.get(i));
								// If the below line is false, it means that 2
								// are the same
								if (ping1.equals(ping2) && ping1.equals(ping3)
										&& ping1.equals(ping4) && ping1.equals(ping5))
									ping = ping1;
								else {
									ArrayList<String> pings = new ArrayList<String>();
									pings.add(ping1);
									pings.add(ping2);
									pings.add(ping3);
									pings.add(ping4);
									pings.add(ping5);
									int O = 0;
									int X = 0;
									int F = 0;
									int S = 0;
									for(int j = 0; j<pings.size();j++){
										if(pings.get(j).equals("O")) O++;
										if(pings.get(j).equals("X")) X++;
										if(pings.get(j).equals("F")) F++;
										if(pings.get(j).equals("S")) S++;
									}
									if (X>=O&&X>=F&&X>=S) ping = "X";
									else if (O>=X&&O>=F&&O>=S) ping = "O";
									else if (F>=X&&F>=O&&F>=S) ping = "F";
									else if (S>=X&&S>=F&&S>=O) ping = "S";
									else ping = "null";
								}
								// System.out.println("Pinged " + ping + " at "
								// + neighbors.get(i).toString());
								if (ping != null && !ping.equals("null"))
									pingcount++;
								pingMap.put(neighbors.get(i), ping);
							}
						}
						if (ping != null && !ping.equals("null")) {
							// if (ping.equals("F"))
							// finished();
							Node newNode = new Node(
									new Point(neighbors.get(i)), ping);
							newNodes.list.add(newNode);
						}
					}
					for (int i = 0; i < newNodes.list.size(); i++) {
						if (!newNodes.list.get(i).ping.equals("X")) {
							if (closed.contains(newNodes.list.get(i).pos) != null) {
								Node temp = closed.contains(newNodes.list
										.get(i).pos);
								if (currNode.g < temp.g) {
									temp.g = currNode.g;
									temp.f = temp.g + temp.h;
									temp.parent = currNode;
									closed.replace(newNodes.list.get(i).pos,
											temp);
								}
							} else if (open.contains(newNodes.list.get(i).pos) != null) {
								Node temp = open
										.contains(newNodes.list.get(i).pos);
								if (currNode.g < temp.g) {
									temp.g = currNode.g;
									temp.f = temp.g + temp.h;
									temp.parent = currNode;
									open.replace(newNodes.list.get(i).pos, temp);
								}
							} else {
								// GET and then change just changes the pulled
								// temp
								// value?
								newNodes.list.get(i).parent = currNode;
								newNodes.list.get(i).g = currNode.g
										+ currNode.pos.distance(newNodes.list
												.get(i).pos);
								newNodes.list.get(i).h = newNodes.list.get(i).pos
										.distance(this.dest);
								newNodes.list.get(i).f = newNodes.list.get(i).g
										+ newNodes.list.get(i).h;
								open.list.add(newNodes.list.get(i));
							}
						}
					}

				}

			}
		} else {
			// System.out.println(super.pingMap(new Point(3,3)));
			// super.move(new Point(1,0));
			// System.out.println("Destination: "+this.dest);
			// System.out.println("Point (Destination): (" + (dest.y+1) + ","
			// + (dest.x+1) + ")");
			pingMap.put(super.getPosition(), "S");
			Node start = new Node(super.getPosition(), "S");
			start.f = 0;
			start.g = 0;
			// String ping = "null";
			int pingcount = 0;
			open.list.add(start);

			while (open.list.size() != 0) {
				// printList(closed, "Closed List");
				// System.out.println("Ping Count: "+pingcount);
				// System.out.println("Ping Count (method): "+this.getNumPings());
				int least = open.getLeast();
				Node currNode = open.list.get(least);
				open.list.remove(least);
				if (currNode.pos.equals(dest)) {
					// System.out.println("DONE! If code doesn't work fix this line");
					finished();
					break;
				} else {

					// ping = "null";
					closed.list.add(currNode);
					ArrayList<Point> neighbors = new ArrayList<Point>();
					NodeList newNodes = new NodeList();
					neighbors
							.add(new Point(currNode.pos.x + 1, currNode.pos.y));
					neighbors
							.add(new Point(currNode.pos.x - 1, currNode.pos.y));
					neighbors
							.add(new Point(currNode.pos.x, currNode.pos.y + 1));
					neighbors
							.add(new Point(currNode.pos.x, currNode.pos.y - 1));
					neighbors.add(new Point(currNode.pos.x - 1,
							currNode.pos.y - 1));
					neighbors.add(new Point(currNode.pos.x - 1,
							currNode.pos.y + 1));
					neighbors.add(new Point(currNode.pos.x + 1,
							currNode.pos.y + 1));
					neighbors.add(new Point(currNode.pos.x + 1,
							currNode.pos.y - 1));

					for (int i = 0; i < neighbors.size(); i++) {
						// couldn't used java's null
						String ping = "null";
						if (pingMap.containsKey(neighbors.get(i))) {
							ping = pingMap.get(neighbors.get(i));
						} else {
							// constrain ping to avoid null pings
							if (neighbors.get(i).x >= 0
									&& neighbors.get(i).x < this.rows
									&& neighbors.get(i).y >= 0
									&& neighbors.get(i).y < this.cols) {
								ping = super.pingMap(neighbors.get(i));
								// System.out.println("Pinged " + ping + " at "
								// + neighbors.get(i).toString());
								if (ping != null && !ping.equals("null"))
									pingcount++;
								pingMap.put(neighbors.get(i), ping);
							}
						}
						if (ping != null && !ping.equals("null")) {
							// if (ping.equals("F"))
							// finished();
							Node newNode = new Node(
									new Point(neighbors.get(i)), ping);
							newNodes.list.add(newNode);
						}
					}
					for (int i = 0; i < newNodes.list.size(); i++) {
						if (!newNodes.list.get(i).ping.equals("X")) {
							if (closed.contains(newNodes.list.get(i).pos) != null) {
								Node temp = closed.contains(newNodes.list
										.get(i).pos);
								if (currNode.g < temp.g) {
									temp.g = currNode.g;
									temp.f = temp.g + temp.h;
									temp.parent = currNode;
									closed.replace(newNodes.list.get(i).pos,
											temp);
								}
							} else if (open.contains(newNodes.list.get(i).pos) != null) {
								Node temp = open
										.contains(newNodes.list.get(i).pos);
								if (currNode.g < temp.g) {
									temp.g = currNode.g;
									temp.f = temp.g + temp.h;
									temp.parent = currNode;
									open.replace(newNodes.list.get(i).pos, temp);
								}
							} else {
								// GET and then change just changes the pulled
								// temp
								// value?
								newNodes.list.get(i).parent = currNode;
								newNodes.list.get(i).g = currNode.g
										+ currNode.pos.distance(newNodes.list
												.get(i).pos);
								newNodes.list.get(i).h = newNodes.list.get(i).pos
										.distance(this.dest);
								newNodes.list.get(i).f = newNodes.list.get(i).g
										+ newNodes.list.get(i).h;
								open.list.add(newNodes.list.get(i));
							}
						}
					}

				}

			}
		}
	}

	public void move() {
		//System.out.println("EEPoint (Destination): ("
		//		+ (this.getPosition().y + 1) + "," + (this.getPosition().x + 1)
		//		+ ")");
		Node node = closed.list.get(closed.list.size() - 1);
		Stack<Point> route = new Stack<Point>();
		route.push(this.getPosition());
		route.push(node.pos);
		//System.out.println("DDPoint: (" + (node.pos.y + 1) + ","
		//		+ (node.pos.x + 1) + ")");
		int infLoop = 0;
		while (node.parent != null) {
			if(infLoop >10000){
				System.out.println("Program was terminated to avoid infinite loop. Please restart program.");
				System.exit(0);
			}
			route.push(node.parent.pos);
		//	System.out.println("CCPoint: (" + (node.parent.pos.y + 1) + ","
		//			+ (node.parent.pos.x + 1) + ")");
			node = node.parent;
			infLoop++;
		}
	//	System.out.println("Break:");
		route.pop();
	//	 System.out.println("Route:");
		while (!route.isEmpty()) {
			Point p = route.pop();
			System.out.println("Moving to: " + p);
			this.move(p);
		}
	//	System.out.println("finished move");
	}

	public void finished() {
		//System.out.println("BBPoint (Destination): (" + (dest.y + 1) + ","
		//		+ (dest.x + 1) + ")");
		Node node = closed.list.get(closed.list.size() - 1);
		Stack<Point> route = new Stack<Point>();
		route.push(dest);
		route.push(node.pos);
		//System.out.println("AAPoint: (" + (node.pos.y + 1) + ","
		//		+ (node.pos.x + 1) + ")");
		int infLoop = 0;
		while (node.parent != null) {
			if(infLoop >10000){
				System.out.println("Program was terminated to avoid infinite loop. Please restart program.");
				System.exit(0);
			}
			route.push(node.parent.pos);
			//System.out.println("ZZPoint: (" + (node.parent.pos.y + 1) + ","
			//		+ (node.parent.pos.x + 1) + ")");
			node = node.parent;
			infLoop++;
		}
		route.pop();
		// System.out.println("Route:");
		while (!route.isEmpty()) {
			Point p = route.pop();
			System.out.println("Moving to: " + p);
			this.move(p);
		}
	}

	public void printList(NodeList list, String name) {
		System.out.println();
		System.out.println(name);
		if (list.list.size() == 0) {
			System.out.println("Empty list");
		} else {
			for (int i = 0; i < list.list.size(); i++) {
				if (pingMap.containsKey(list.list.get(i).pos)) {
					System.out.print((pingMap.get(list.list.get(i).pos)
							+ " @ Point: (" + (list.list.get(i).pos.y + 1)
							+ "," + (list.list.get(i).pos.x + 1) + ")"));
				} else
					System.out.print(list.list.get(i).pos.toString()
							+ "(unknown/unpingged) ");
				System.out.println();
			}
		}
	}
}
