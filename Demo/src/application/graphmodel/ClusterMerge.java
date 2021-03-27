package application.graphmodel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ClusterMerge {
	ArrayList<LinienSegment> FinalTsp = new ArrayList<LinienSegment>();

	public void execute(List<List<Punkt>> clusters, ArrayList<LinienSegment> mst, List<LinkedList<Punkt>> tsp) {
		mst.get(0).SortMstLaenge(mst);		
		
		for (int i=0; i< clusters.size() +1 ;i++) {
			System.out.println("%%%%%@@@: "+i);
			System.out.println("Anzahl Cluster: "+clusters.size());
			System.out.println("Anzahl touren: "+tsp.size());
		LinienSegment mstEdge =  findClosestCluster(clusters, mst, tsp);
		//verbindet Cluster, behaltet Tsp Eigenschaften bei.
		System.out.println("%%%%%@@@: "+i);
		System.out.println(clusters.size());
		System.out.println(tsp.size());
		}
		
		FinalTsp(tsp);
	}



	public LinienSegment findClosestCluster(List<List<Punkt>> clusters, ArrayList<LinienSegment> mst, List<LinkedList<Punkt>> tsp) {
		LinienSegment ConnectingMstEdge = null;
		int a[]= {0,0,0};
		//findet immer connect Kante, solange mind 2 cluster
		for (int i=0 ; i < mst.size(); i++) {
			a = ConnectsTwoClusters(clusters, mst.get(i));
			if( a[0] == 1 ) {
				ConnectingMstEdge = mst.get(i);
				break;
			}
			if( a[0] == 1 && i == mst.size()-1) System.out.println("ups");
		}
				
		ConnectTsp(ConnectingMstEdge,a[1] , a[2], tsp);
		ClusterCombine(a[1] , a[2] , clusters);
		return ConnectingMstEdge;
		
	}

	
	private int[] ConnectsTwoClusters(List<List<Punkt>> clusters, LinienSegment ls) {
		//Test ob die beiden Endpkte NICHT dem selben Cluster gehören
		//1.Cluster finden
		int a[]= {0,0,0}; 
		for( int i =0 ; i < clusters.size(); i++) {
			
			if ((ls.endpkt1.inCluster(clusters.get(i))) == true) {
							
				for( int j =0 ; j < clusters.size(); j++) {
					if(i != j) {
						
						if ((ls.endpkt2.inCluster(clusters.get(j))) == true) {	
							a[0] =1; a[1] = i; a[2] =j;
							return a;
						}
						else if ((ls.endpkt2.inCluster(clusters.get(i))) == true) {	
							return a;
						}
						
						//2. Cluster finden

						}
					}
				}
				}
			
		return a;
	}
	
	private LinienSegment ConnectTsp(LinienSegment mstEdge, int i, int j, List<LinkedList<Punkt>> tsp) {
		LinienSegment bestEdge;
		int provisorischeLoesung =0;
		double g0,g1;
		
		
		LinkedList<Punkt> temp= new LinkedList<Punkt>();

		
		
		
		int a = tsp.get(i).indexOf(mstEdge.endpkt1);
		int b = tsp.get(j).indexOf(mstEdge.endpkt2);
		
		
		if ( a == -1) {
			
			int temp2 =i;
			i=j;
			j=temp2;
			System.out.println("i und j zuweisung falsch!");
//			System.out.println("a: "+a);
//			System.out.println("b: "+b);
	//		a = tsp.get(j).indexOf(mstEdge.endpkt1);
	//		b = tsp.get(i).indexOf(mstEdge.endpkt2);
			provisorischeLoesung =1;
			
			a = tsp.get(i).indexOf(mstEdge.endpkt1);
			b = tsp.get(j).indexOf(mstEdge.endpkt2);
		}
		
		int w= a+1; int x= a-1; int y= b+1 ; int z= b-1;
		
		
		try {
			tsp.get(i).get(w);
		}
		catch(IndexOutOfBoundsException e) {
			w = 0;
		}
		
		try {
			tsp.get(i).get(x);
		}
		catch(IndexOutOfBoundsException e) {
			x = tsp.get(i).size()-1;
		}
		
		
		try {
			tsp.get(j).get(y);
		}
		catch(IndexOutOfBoundsException e) {
			y = 0;
		}
		
		try {
			tsp.get(j).get(z);
		}
		catch(IndexOutOfBoundsException e) {
			z = tsp.get(j).size()-1;
		}
		

		
		LinienSegment c[] = candidate(i,j,w,x,y,z,tsp, mstEdge); 
		LinienSegment c11 = c[0];
		LinienSegment c22 = c[1];
		
		if(provisorischeLoesung ==0 ) {
			System.out.println("currently");
			LinienSegment m11 = new LinienSegment(mstEdge.endpkt1, tsp.get(i).get(w));
			LinienSegment m12 = new LinienSegment(mstEdge.endpkt1, tsp.get(i).get(x));
			LinienSegment m21 = new LinienSegment(mstEdge.endpkt2, tsp.get(j).get(y));
			LinienSegment m22 = new LinienSegment(mstEdge.endpkt2, tsp.get(j).get(z));
	
			g0= c11.gewicht - m12.gewicht - m21.gewicht;
			g1= c22.gewicht - m11.gewicht - m22.gewicht;
		}
		else {
			LinienSegment m11 = new LinienSegment(mstEdge.endpkt1, tsp.get(j).get(y));
			LinienSegment m12 = new LinienSegment(mstEdge.endpkt1, tsp.get(j).get(z));
			LinienSegment m21 = new LinienSegment(mstEdge.endpkt2, tsp.get(i).get(w));
			LinienSegment m22 = new LinienSegment(mstEdge.endpkt2, tsp.get(i).get(x));
			
			g0= c11.gewicht - m11.gewicht - m22.gewicht;
			g1= c22.gewicht - m12.gewicht - m21.gewicht;
		}

		if( g0 < g1) {
			bestEdge = c11;
			int count=0;
			for (int p=0; p < tsp.get(j).size() ; p++) {
				try {
					temp.add(tsp.get(j).get(b + p));
				}
				catch(IndexOutOfBoundsException e) {	//NoSuchElementException
					temp.add(tsp.get(j).get(count));
					count++;
				}
			}
			count =0;
			for (int p=0; p < tsp.get(i).size() ; p++) {
				try {
					temp.add(tsp.get(i).get(b + p + 1));
				}
				catch(IndexOutOfBoundsException e) {	//NoSuchElementException wenn mit indexIterator
					
					temp.add(tsp.get(i).get(count));
					count++;
				}
			}
			
			
		}
		else {
			bestEdge = c22;
			int count=0;
			
			for (int p=0; p < tsp.get(j).size() ; p++) {
				try {
					temp.add(tsp.get(j).get(b + p));
				}
				catch(IndexOutOfBoundsException e) {	//NoSuchElementException
					temp.add(tsp.get(j).get(count));
					count++;
				}
			}
			count =0;
			for (int p=0; p < tsp.get(i).size() ; p++) {
				try {
					
					//hier switch case: wann b+p+1 und wann b+p?
					temp.add(tsp.get(i).get(b + p + 1));
				}
				catch(IndexOutOfBoundsException e) {	//NoSuchElementException wenn mit indexIterator
					
					temp.add(tsp.get(i).get(count));
					count++;
				}
			}
		}
		
		
		tsp.remove(i);
		
		if( i < j ) {
			tsp.remove(j-1);
		}
		else tsp.remove(j);
		
		tsp.add(temp);

		System.out.println("connecting through:");
		bestEdge.printLs();
		
		
		return bestEdge;
	}
	
	public void ClusterCombine(int i, int j, List<List<Punkt>> clusters) {
		List<Punkt> temp= new ArrayList<Punkt>();
		
		clusters.get(i).addAll(clusters.get(j));
		temp = clusters.get(i);
		
		clusters.remove(i);
		if( i < j ) {
			clusters.remove(j-1);
		}
		else clusters.remove(j);
		
		clusters.add(temp);
	}

	private LinienSegment[] candidate(int i, int j,int w, int x, int y, int z, List<LinkedList<Punkt>> tsp, LinienSegment mst) {
		LinienSegment c[] = {null, null};

		
		LinienSegment c11 = new LinienSegment(tsp.get(i).get(w), tsp.get(j).get(y));
		LinienSegment c12 = new LinienSegment(tsp.get(i).get(x), tsp.get(j).get(y));
		
		LinienSegment c21 = new LinienSegment(tsp.get(i).get(w), tsp.get(j).get(z));
		LinienSegment c22 = new LinienSegment(tsp.get(i).get(x), tsp.get(j).get(z));
		
		c[0] = kreuzungsfrei(c11,c12, mst);
	
//		System.out.println("kreuzungstest @@@@@");	
//		c11.printLs();
//		c12.printLs();
//		c21.printLs();
//		c22.printLs();
//		c[0].printLs();
//		System.out.println("kreuzungstest @@@@@");
		
		c[1] = kreuzungsfrei(c21,c22, mst);

		return c;
	}
	
	
	private LinienSegment kreuzungsfrei(LinienSegment c1, LinienSegment c2, LinienSegment mst) {
		//check if c1 und mst sich kreuzen, wenn ja kreuzen sich c2 und mst nicht
		double t,u;
		//check ob richtiges ls wiedergegeben
		double t1= (c1.endpkt1.getX() - mst.endpkt1.getX())*(mst.endpkt1.getY()- mst.endpkt2.getY());
		double t2= (c1.endpkt1.getY() - mst.endpkt1.getY())*(mst.endpkt1.getX()- mst.endpkt2.getX());
		
		double t3 = (c1.endpkt1.getX() - c1.endpkt2.getX())*(mst.endpkt1.getY()- mst.endpkt2.getY());
		double t4 = (c1.endpkt1.getY() - c1.endpkt2.getY())*(mst.endpkt1.getX()- mst.endpkt2.getX());
		
		t = (t1 - t2)/(t3 - t4);

		if (t <= 1 && t >= 0) return c2;
		
		double u1= (c1.endpkt2.getX() - c1.endpkt1.getX())*(c1.endpkt1.getY()- mst.endpkt1.getY());
		double u2= (c1.endpkt2.getY() - c1.endpkt1.getY())*(c1.endpkt1.getX()- mst.endpkt1.getX());
		
		u = (u1-u2)/(t3-t4);
		
		if (u <= 1 && u >= 0) return c2;
		
		return c1;
		
		
		//... den Fall, dass beide Ls die mst Kante kreuzen...
	}

	

	private void FinalTsp(List<LinkedList<Punkt>> tsp) {
		for (int i=0; i< tsp.get(0).size() -1; i++) {
			FinalTsp.add(new LinienSegment(tsp.get(0).get(i), tsp.get(0).get(i+1)));
		}
		
		FinalTsp.add(new LinienSegment(tsp.get(0).get(tsp.get(0).size()-1),tsp.get(0).get(0)));		
		
		System.out.println("final tsp printing @@@@@@");
		
		System.out.println(tsp.size());
		System.out.println(FinalTsp.size());
		
		for (int i=0; i< FinalTsp.size(); i++) {
			FinalTsp.get(i).printLs();
		}
	}



	public ArrayList<LinienSegment> getTsp(){
		
		return FinalTsp;
	}

}
