import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class GG8723 extends JFrame {

    MouseListener LastMouseListener;
    JPanel panelexample;
    JRadioButton ck1,ck2,ck3,ck4,ck5;
    JButton Add,CC,Show,Help;
    JTextArea hint;
    JLabel h;
    ButtonGroup selectionLimit;
    double x, y;
    int count;
    
    private class DrawPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    LinkedList<Point2D> VerticeList = new LinkedList<Point2D>();
    LinkedList<Line2D> EdgeList = new LinkedList<Line2D>(); 
   
    //to store all StartPoint's index for Show cut Vertices method..
    LinkedList<Integer> StartList = new LinkedList<Integer>();
    //to store all EndPoint's index for Show cut Vertices method..
    LinkedList<Integer> EndList = new LinkedList<Integer>();
    //to store Found Cut Vertices ....
    LinkedList<Integer> CutVerticeList = new LinkedList<Integer>();
    
    
    //to store ConnectedComponents Group..
    LinkedList<Integer> CCList = new LinkedList<Integer>();
    int[] cc;
   
    int R = 5; //Set Radius for drawing..
 
    boolean VerticeFound = false;
	int VerticeIndex = 0;
	Point2D LastMouseLocation;

    public DrawPanel() {
        setBackground(Color.WHITE);
        VerticeList = new LinkedList<Point2D>();
    }
    public boolean exist(Point2D point) {
    	//See if Point exist in Current VerticeList
    	Iterator<Point2D> it = VerticeList.iterator();
		while(it.hasNext()) {
			if(point.distance(it.next())<15)
				return true;
		}
    	return false;
    } 
    public void drawPoint(Point2D point) {
     if(isEnabled()) {
    	if(!exist(point)) {  //Only Draw when the coordinate has no point yet
     	VerticeList.addLast(point);;
     	repaint();
     	}else
     		return;
     }
    } 
    public void removePoint(Point2D point) throws Exception{		
    	if( VerticeList.size() == 0) {
    		throw new IndexOutOfBoundsException(); 
    	}
    	Iterator<Point2D> it = VerticeList.iterator();
    	int index = 0;
    	while(it.hasNext()) {
    		if(point.distance(it.next())<15) {
      			VerticeList.remove(index);
      			
      			/*Check if current remove Vertex is a Cut Vertex ..
      			 * If yes then remove that from Cut Vertices List also..
      			 */
      			for(int i = 0; i < CutVerticeList.size(); i++) {
      				if (index == CutVerticeList.get(i)) {
      					CutVerticeList.remove(i);
      				}
      			}
    			break;
    		}
    		index++;
    	}
    	repaint();
    	return;
    }
	public void paintComponent(Graphics g) {		
    	
    	super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        BasicStroke S = new BasicStroke(3);
       
        //for Drawing Vertices..
        for (Point2D pt : VerticeList) {
        	Ellipse2D e = new Ellipse2D.Double(pt.getX() - R, pt.getY() - R, 2 * R, 2 * R);
        	/* if CutVerticeList is not Empty .. 
			 * it will determines the non-Cut Vertices are painted in red
			 * the Cut Vertices are painted in Green
			 */
        	 if(!CutVerticeList.isEmpty())
        		for(int i = 0; i < CutVerticeList.size(); i++) {
        			if(pt.equals(VerticeList.get(CutVerticeList.get(i))))
        			{
        				g2.setColor(Color.green);
        			}else
        				g2.setColor(Color.red);
        		}
        	//If CutVerticeList is empty, it just paint every vertices in red ..
        	else {
        		g2.setColor(Color.red);
        	}
			g2.fill(e);
        }

        //for Drawing Edges..
        for (Line2D ed : EdgeList) {
        	Line2D edge = new Line2D.Double(ed.getP1(),ed.getP2());
        	
        	if(!CCList.isEmpty()) {
        		for(int i = 0; i < CCList.size(); i++) {
        			
        			Point2D p = VerticeList.get(CCList.get(i));
        			
        			if(p.equals(ed.getP1())||p.equals(ed.getP2()))
        			{
        				g2.setColor(Color.gray);
        			}
        			else
        				g2.setColor(Color.blue);
        		}
        	}
        	else {
        		g2.setColor(Color.blue);
        	}
        	//g2.setColor(Color.blue);
        	g2.setStroke(S);
        	g2.draw(edge);
        }
    }
    public void FindPoint(Point2D point) throws Exception {
    	if(VerticeList.size() == 0) {
    		throw new IndexOutOfBoundsException();
    	}
    	//See if User's Vertex selection exist in  List , if yes, save the index and coordinate for later uses
    	Iterator<Point2D> it = VerticeList.iterator();
		int index = 0;				
		while (it.hasNext()) {
        if (point.distance(it.next())<15) {
        	VerticeFound = true;
        	VerticeIndex = index;
        	LastMouseLocation = point;
            break; 
        	}
        	index++;
		}
		return;
    }
    public void Movingpoint(Point2D point) {
		if (VerticeFound) {	
            Point2D CurrentPoint = VerticeList.get(VerticeIndex);
            //Fixing Coordinate...
            Point2D NewPoint = new Point2D.Double( CurrentPoint.getX() + point.getX()- LastMouseLocation.getX(),
            		CurrentPoint.getY()+point.getY()- LastMouseLocation.getY());
          
            //Replacing Selected Vertex with New Location
            VerticeList.set(VerticeIndex, NewPoint);
            LastMouseLocation = point;
            MovingVertexCheck(CurrentPoint,NewPoint);
            repaint();
		}
    }
    public void DrawLine(Point2D StartPoint, Point2D EndPoint) throws Exception {	
    	if(exist(StartPoint) && exist(EndPoint)) {
    		Point2D s = new Point2D.Double();
    		Point2D e = new Point2D.Double();
    		
    		//Convert User's Coordinate to actual exist vertex on current panel  
    		for(Point2D dot : VerticeList) {
    			if(StartPoint.distance(dot)<15)
    				s = dot;
    		}
       		for(Point2D dot : VerticeList) {
    			if(EndPoint.distance(dot)<15)
    				e = dot;
       		}
       		
    		//Saving the StartPoint Index and EndPoint Index into StartList , EndList for Show Cut Vertices Calculation ...
    		Iterator<Point2D> it = VerticeList.iterator();
    		int start = 0;
    		int end = 0;
    		
    		//Adding the Start Vertex's Index
    		while (it.hasNext()) {
    			if (s.equals(it.next())) {
    				StartList.add(start);
    				break; 
            	}
            	start++;
    		}
    		//Adding the End Vertex's Index 
    		Iterator<Point2D> it2 = VerticeList.iterator();
    		while (it2.hasNext()) {
    			if (e.equals(it2.next())) {
    				EndList.add(end);
    				break; 
            	}
            	end++;
    		}
    		
       		//Draw Edge ..
    		Line2D edge = new Line2D.Double(s, e);
    		EdgeList.add(edge);	
    		repaint();
    	}else {
    		throw new Exception();
    	}
    	return;
    }
    public void RemoveLine(double x, double y) {
    	//Turns Coordinate into a Box .. 
    	int Box_X = (int)x - 5;
    	int Box_Y = (int)y - 5;
    	double width = 10;
    	double height = 10;
    	//If edge intersects coordinates area ... remove Edge
    	for(Line2D line : EdgeList) {
    		if (line.intersects(Box_X, Box_Y, width, height)){
    			EdgeList.remove(line);
    			repaint();
    	        break;
    	    } 
    	}
    }    
    public void RemoveVertexCheck(Point2D Point){
    	//Remove if any Edges contains the given point
       	for(int i = 0; i < EdgeList.size();i++) {
    		Point2D p1 = EdgeList.get(i).getP1();
			Point2D p2 = EdgeList.get(i).getP2();
    		if((p1.distance(Point)<15)||(p2.distance(Point)<15))
			{
				EdgeList.remove(i);
				repaint();
			}
    	}
       	/* having some bug (if vertex has more than 2 lines,it will only delete 2 lines then stop ..)
       	 * so I created a 2nd for loop to go over list again
       	 * Just to make sure all edges that contains given Point will be deleted
       	 */
       	for(int i = 0; i<EdgeList.size();i++) {
       		Point2D p1 = EdgeList.get(i).getP1();
			Point2D p2 = EdgeList.get(i).getP2();
			if((p1.distance(Point)<15)||(p2.distance(Point)<15))
			{
				EdgeList.remove(i);
				repaint();
			}
    	}

    }
    public void MovingVertexCheck(Point2D CurrentPoint,Point2D NewPoint){
    	//repaint the Line with new Point coordinates
    	for(int i = 0; i<EdgeList.size();i++) {
    		Point2D p1 = EdgeList.get(i).getP1();
			Point2D p2 = EdgeList.get(i).getP2();
			if(p1.equals(CurrentPoint)){
				Line2D newLine = new Line2D.Double(NewPoint,p2);
				EdgeList.set(i, newLine);
				repaint();
			}
			else if(p2.equals(CurrentPoint)) {
				Line2D newLine = new Line2D.Double(p1,NewPoint);
				EdgeList.set(i, newLine);
				repaint();
			}
    	}
    }
    public void AddAllEdge() throws Exception {
    	for(Point2D pt : VerticeList) {
    		for(int i = 0; i < VerticeList.size(); i++ ) {
    			// add edges when the index element is not same as pt
    			if(!VerticeList.get(i).equals(pt)) {
    				Point2D st = pt;
    	    		Point2D end = VerticeList.get(i);
    	    		DrawLine(st,end);
    			}
    		}
    	}
    }
    //gets Methods for Show Cut Vertices Button..
    public int Size() {
    	return VerticeList.size();
    }
    public LinkedList<Integer> getStartPointList(){
    	return StartList;
    }
    public LinkedList<Integer> getEndPointList(){
    	return EndList;
    }
	public void ShowCutVertices(LinkedList<Integer> cutList) {
		CutVerticeList = cutList;
		repaint();
	}
	public void ShowCC(int[] arr) {
		cc = arr;
		int index = 1;
		for(int i = 0; i < cc.length; i++) {
			if(cc[i] == index) {
				CCList.add(i);
			}
		}
		repaint();
	}
}  
    // Class for Calculating Cut Vertices ..
    private class graph {
    	
    	int vertex;
    	private LinkedList<Integer> TempArr[];
    	int time = 0;
    	static final int NIL = -1;
    			
    	graph(int size){
    		vertex = size;
    		TempArr = new LinkedList[size];
    		for(int i = 0; i< size ; ++i) 
    		{
    			TempArr[i] = new LinkedList();
    		}
    	}
    	//AddEdge will Automatically Add all the edges that user made into TempArr..
    	public void addEdge(LinkedList<Integer> st, LinkedList<Integer> end)     	
    	{
    		int startIndex = 0;
    		int endIndex = 0;
    		
    		for(int i = 0; i < st.size();i++) {
    			startIndex = st.get(i);
    			endIndex = end.get(i);
    			TempArr[startIndex].add(endIndex);
        		TempArr[endIndex].add(startIndex);
    		}
    	}    	
    	public void CalCutPoint(int i, boolean visited[], int id[], int low[], int parent[], boolean CutPoint[]) 
    	{ 
    		int children = 0; 
    		visited[i] = true; 
    		id[i] = low[i] = ++time;
    		
    		Iterator<Integer> it = TempArr[i].iterator(); 
    		while (it.hasNext()) 
    		{ 
    			int v = it.next();
    			if (!visited[v]) { 
    				children++; 
                 	parent[v] = i; 
                 	CalCutPoint(v, visited, id, low, parent, CutPoint); 
                 	
                 	low[i]  = Math.min(low[i], low[v]); 
                 //if index i is root of DFS tree and has two or more children.
                 //then it is a cut vertex ..
                 if (parent[i] == NIL && children > 1) 
                     CutPoint[i] = true; 
                 //if index i is not a root, low value of its child > i .. 
                 //then it is a cut vertex ..
                 if (parent[i] != NIL && low[v] >= id[i]) 
                     CutPoint[i] = true; 
             } 
             else if (v != parent[i]) 
                 low[i]  = Math.min(low[i], id[v]); 
         	} 
    		
    		
    	}
    	public LinkedList<Integer> DFS() 
        { 
            boolean visited[] = new boolean[vertex]; 
            int id[] = new int[vertex]; 
            int low[] = new int[vertex]; 
            int parent[] = new int[vertex]; 
            boolean Cutpoint[] = new boolean[vertex]; 
            LinkedList<Integer> index = new LinkedList(); // To store Cut Vertices..
      
            // Setting parent, visited and CutPoints ...
            for (int i = 0; i < vertex; i++) 
            { 
                parent[i] = NIL; 
                visited[i] = false; 
                Cutpoint[i] = false; 
            }
            //Recursively finding Cut Vertices ..
            for (int i = 0; i < vertex; i++) { 
                if (visited[i] == false) 
                    CalCutPoint(i, visited, id, low, parent, Cutpoint); 
            }
            //Adding Found Cut Vertices into Index list for return type ..
            for (int i = 0; i < vertex; i++){
                if (Cutpoint[i] == true) 
                   index.add(i);
            }
            return index;
        }     	
    }
    //Class for Calculating Connected Components 
    /* Some Methods Looks Similar to Class graph,
     * But I tried to Put methods in same class, it doesn't works
     * so I just separated into 2 private classes ..
     */
    private class CC
    {
    	int vertex;
    	private LinkedList<Integer> CCArr[];
    	private int[] components;
    	private int componentCount;
    	
    	CC(int v){
    		vertex = v;
    		CCArr = new LinkedList[v];
    		for(int i = 0; i<v ; ++i) 
    		{
    			CCArr[i] = new LinkedList();
    		}
    	}
    	public void addEdgeCC(LinkedList<Integer> st,LinkedList<Integer> end) 
    	{
    		int startIndex = 0;
    		int endIndex = 0;
    		for(int i = 0; i < st.size();i++) {
    			startIndex = st.get(i);
    			endIndex = end.get(i);
    			CCArr[startIndex].add(endIndex);
        		CCArr[endIndex].add(startIndex);
    		}
    	}
    	//methods for Connected Component ..
    	private void CCdfs(int i,boolean visited[]) {
    	    visited[i] = true;
    	    components[i] = componentCount;
    	    for (int to : CCArr[i])
    	      if (!visited[to])
    	        CCdfs(to,visited);
    	}
    	public int[] getComponents() {
    		boolean visited[] = new boolean[vertex]; 
      	    components = new int[vertex];
      	   
      	    for (int i = 0; i < vertex; i++) {
      	      if (!visited[i]) {
      	        componentCount++;
      	        CCdfs(i,visited);
      	      }
      	    }
    	    return components;
    	}
    	
    }
    public GG8723 (){
    	
    	//Creating my Drawing Panel
        panelexample = new DrawPanel();
        panelexample.setBounds(250, 0, 800, 800);
        panelexample.setEnabled(false);

        //Adding Panel to JFrame
        Container cpane = this.getContentPane();
        cpane.setLayout(null);
        cpane.add(panelexample);
        
        h = new JLabel("INSTRUCTION :");
        h.setBounds(20,0,120,20);
        
        //Creating a Text area
        hint = new JTextArea();
        hint.setBounds(20, 20, 200, 100);
        hint.setForeground(Color.RED);
        hint.setLineWrap(true);
        
        //Creating CheckBox ..
        ck1 = new JRadioButton("Add Vertex");
        ck1.setBounds(20, 120, 120, 50);
        ck1.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		if(ck1.isSelected()) {
        			
        			hint.setText("Click on the White Area On the Right To Add Vertex");
        			panelexample.setEnabled(true);
        			
        			if (LastMouseListener != null) {
				    removeMouseListener(LastMouseListener);
				    }
				    
				    LastMouseListener = new MouseAdapter() {
        				public void mousePressed(MouseEvent e) {
        					x = e.getX() - 258;
        					y = e.getY() - 30;
        					Point2D point = new Point2D.Double(x,y);
        					((DrawPanel) panelexample).drawPoint(point);  
        				}
				    };
        			addMouseListener(LastMouseListener);					
        				
        			}	
        		}
        	});
        
        ck2 = new JRadioButton("Add Edge");
        ck2.setBounds(20, 170, 120, 50);
        ck2.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		if(ck2.isSelected()) 
        		{
        			count = 0;
        			hint.setText("Choose a Start Point and a EndPoint to Add Edge.The End Point will\nAutomatically become next Start\nPoint\nNOTE: NEED TO CHOOSE A NEW "
        					+ " START POINT IF ERROR OCCURS. ");
        			panelexample.setEnabled(true);

        			if (LastMouseListener != null) {
    				    removeMouseListener(LastMouseListener);
    				}
        			 
        			LastMouseListener = new MouseAdapter() {
         				public void mousePressed(MouseEvent e) {
         					if(count == 0) {
         						x = e.getX() - 258;
         						y = e.getY() - 30;
         						count ++;
         					}else {
         						double old_x = x;
         						double old_y = y;
         						x = e.getX()- 258;
         						y = e.getY() - 30;
         						
         						Point2D StartPoint = new Point2D.Double(old_x,old_y);
         						Point2D EndPoint = new Point2D.Double(x,y);
         						try {
         						((DrawPanel) panelexample).DrawLine(StartPoint,EndPoint);
         						}catch (Exception error) {
         							JOptionPane.showMessageDialog(null, "Unable to Find Selected Vertex : Please Select a Exist Vertex", "Coordinate Error", JOptionPane.ERROR_MESSAGE);
         							count = 0;
         						}
         					}
         				}
 				    };
 				    
         			addMouseListener(LastMouseListener);
        		}
        	}
        }); 
    
        ck3 = new JRadioButton("Remove Vertex");
        ck3.setBounds(20, 220, 120, 50);
        ck3.addActionListener(new ActionListener(){	
        	public void actionPerformed(ActionEvent e)
        	{	
        		if(ck3.isSelected()) {
        			
        			hint.setText("Click on the Vertex to Remove");
        			panelexample.setEnabled(true);	
        		    //if Last Mouse Listener Exist ..
        			
        			if (LastMouseListener != null) {
				    removeMouseListener(LastMouseListener);
				    }
				    
				    LastMouseListener = new MouseAdapter() {
        				public void mousePressed(MouseEvent e) {
        					x = e.getX() - 258;
        					y = e.getY() - 30;
        					Point2D point = new Point2D.Double(x,y);
        					try {
        						((DrawPanel) panelexample).removePoint(point);	
        						((DrawPanel) panelexample).RemoveVertexCheck(point);
        					}catch(Exception err) {
        						JOptionPane.showMessageDialog(null, "Current Panel Is Empty : Unable to Remove !", "ERROR", JOptionPane.ERROR_MESSAGE);
        					}
        				}
				    };
				    
        			addMouseListener(LastMouseListener);
        		}
        	}
        });

        ck4 = new JRadioButton("Remove Edge");
        ck4.setBounds(20, 270, 120, 50); 
        ck4.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e)
        	{
        		if(ck4.isSelected()) 
        		{
        			hint.setText("Double Click on the Edge to remove.\n\n( TRY CLICKING AS CLOSE AS\nPOSSIBLE )");
        			panelexample.setEnabled(true);
        			
        			if (LastMouseListener != null) 
        			{
				    removeMouseListener(LastMouseListener);
				    }
				    
        			LastMouseListener = new MouseAdapter() {
         				public void mousePressed(MouseEvent e) {
         					x = e.getX() - 258;
         					y = e.getY() - 30;
         					((DrawPanel) panelexample).RemoveLine(x,y);	
         				}
 				    };
        			addMouseListener(LastMouseListener);					
        		};
        	}
        }); 

        ck5 = new JRadioButton("Move Vertex");
        ck5.setBounds(20, 320, 120, 50);
        ck5.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent e)
        	{
        		if(ck5.isSelected()) 
        		{
        			hint.setText("PRESS on the Vertex and then\nDRAG to a new coordinate");
        			panelexample.setEnabled(true);
        			//if Last Mouse Listener Exist ..
        			if (LastMouseListener != null) {
				    removeMouseListener(LastMouseListener);
				    }
        			
        			LastMouseListener = new MouseAdapter() {
        	    		public void mousePressed(MouseEvent e) {
        	    			x = e.getX() - 258;
        					y = e.getY() - 30;
        					Point2D point = new Point2D.Double(x,y);
        					try {
        					((DrawPanel) panelexample).FindPoint(point);
        					}catch(Exception err) {
        						JOptionPane.showMessageDialog(null, "Current Panel Is Empty : Please create a vertex first !", "ERROR", JOptionPane.ERROR_MESSAGE);
        					}
        	    		}
        	    		public void mouseReleased(MouseEvent e) {
        	    			double newx = e.getX() - 258;
        					double newy = e.getY() - 30;
        					Point2D newpoint = new Point2D.Double(newx,newy);
        	    			((DrawPanel) panelexample).Movingpoint(newpoint);
        	    		}
        	    	};
        			
        			addMouseListener(LastMouseListener);
        		}
        	}
        }); 

        //Creating Buttons
        Add = new JButton("Add All Edges");
        Add.setBounds(20, 370, 200, 35);
        Add.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		try {
					((DrawPanel) panelexample).AddAllEdge();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Panel is Empty", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
          
        CC = new JButton("Connected Components");
        CC.setBounds(20, 420, 200, 35);
        CC.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		//Getting Panel's graph information..
        		int size = ((DrawPanel)panelexample).Size();
        		LinkedList<Integer> l1 = ((DrawPanel)panelexample).getStartPointList();
        		LinkedList<Integer> l2= ((DrawPanel)panelexample).getEndPointList();
        		        		
        		//Passing Panel's graph into graph Class ...
        		CC test2 = new CC(size);
        		test2.addEdgeCC(l1, l2);

        		int[] components = test2.getComponents();
        		((DrawPanel)panelexample).ShowCC(components);
        	
        	    for (int i = 0; i < size; i++)
        	      System.out.printf("Vertex at Index : %d is part of component %d\n", i, components[i]);
			}
		});
        
        Show = new JButton("Show Cut Vertices");
        Show.setBounds(20, 470, 200, 35);
        Show.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		
        		hint.setText("The Cut Vertices will be shown in\nGreen Color.");
        		
        		//Getting Panel's graph information..
        		int current_Vertex_size = ((DrawPanel)panelexample).Size();
        		LinkedList<Integer> list1 = ((DrawPanel)panelexample).getStartPointList();
        		LinkedList<Integer> list2 = ((DrawPanel)panelexample).getEndPointList();
        		
        		//Passing Panel's graph into graph Class ...
        		graph test = new graph(current_Vertex_size);
        		test.addEdge(list1, list2);
        		LinkedList<Integer> CutList = test.DFS(); //return list of cut vertices
        		
        		//Showing the Cut Vertices on graph ..
        		((DrawPanel)panelexample).ShowCutVertices(CutList);
			}
		});
        
        Help = new JButton("Help");
        Help.setBounds(20, 520, 200, 35);
        Help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					JFrame frame2 = new JFrame("App Instructions");
					frame2.setLocation(800, 300);
				 
					frame2.setVisible(true);
					frame2.setSize(600,400);
					
					JLabel label1 = new JLabel("Add Vertex : Adding new vertex by Clicking on the Frame");
					label1.setBounds(50, 50, 600, 30);
			    
					JLabel label2 = new JLabel("Add Edge : Add Edge to the Chosen vertexs");
					label2.setBounds(50,100, 600, 30);
			    
					JLabel label3 = new JLabel("Remove Vertex : Removing vertex by Clicking on the vertex");
					label3.setBounds(50, 150, 600, 30);
			    
					JLabel label4 = new JLabel("Remove Edge : Removing edge by Clicking on its Start and End Points");
					label4.setBounds(50, 200, 600, 30);
			    
					JLabel label5 = new JLabel("Move Vertex : Select a Vertex and move it to a new position");
					label5.setBounds(50, 250, 600, 30);
			    
					JLabel label6 = new JLabel(" ");
			    
					frame2.add(label1);
					frame2.add(label2);
					frame2.add(label3);
					frame2.add(label4);
					frame2.add(label5);
					frame2.add(label6);
				}
			});
        
        //Creating Selection Limit..
        selectionLimit = new ButtonGroup();
        selectionLimit.add(ck1);
        selectionLimit.add(ck2);
        selectionLimit.add(ck3);
        selectionLimit.add(ck4);
        selectionLimit.add(ck5);
        
        //Adding all Components into window ..
        cpane.add(hint);
        cpane.add(h);
        cpane.add(ck1);
        cpane.add(ck2);
        cpane.add(ck3);
        cpane.add(ck4);
        cpane.add(ck5);
        cpane.add(Add);
        cpane.add(CC);
        cpane.add(Show);
        cpane.add(Help);
    }
    public static void main(String[] args){   
        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {
            	 GG8723 f = new GG8723();
                 f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                 f.setSize(new Dimension(1000,700));
                 f.setLocation(500,200);
                 f.setVisible(true);
            }	
        });
    }
}


