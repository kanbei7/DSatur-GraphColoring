import java.util.*;
import java.lang.Math;

class UndirectedGraphNode {
    int label;
    int sat;
    int color;
    int degree;
    HashSet<UndirectedGraphNode> neighbors;
    Set<Integer> satSet;
    UndirectedGraphNode(int x) { 
        label = x; 
        sat = 0;
        color = -2;
        degree = 0;
        neighbors = new HashSet<UndirectedGraphNode>(); 
        satSet = new HashSet<Integer>();
    }

    public void addNeighor(UndirectedGraphNode v){
        neighbors.add(v);
    }

    public boolean hasNeighbor(UndirectedGraphNode v){
        return neighbors.contains(v);
    }

    public boolean isColored(){
        return color>=0;
    }

    public void satIncrement(){
        sat++;
    }

    public void updateSat(){
        sat=satSet.size();
    }

    public int getSat(){
        return sat;
    }

    public int setColor(){
        if (satSet.size()==0){
            color=0;
            return 0;
        }
        else{
            Integer c=0;
            while (satSet.contains(c)){
                c+=1;
            }
            color=c;
            return c;    
        }
        
    }

    public int getColor(int c){
        return color;
    }

    public void addColorset(int c){
        satSet.add(c);
    }

    public Set<Integer> getColorSet(){
        Set<Integer> ans=new HashSet<Integer>();
        for(UndirectedGraphNode u:neighbors){
            if (u.color>-1){
                ans.add(u.color);
            }
        }
        return ans;
    }

    public void degreeIncrement(){
        degree++;
    }

    public int getDegree(){
        return degree;
    }
};

class NodeComparator implements Comparator<UndirectedGraphNode>{
    @Override
    public int compare(UndirectedGraphNode a, UndirectedGraphNode b) {
        return a.sat > b.sat ? 1: (a.sat == b.sat ? (a.degree==b.degree?0:(a.degree>b.degree?1:-1)) : -1 );
    }
};

class NodeDegreeComparator implements Comparator<UndirectedGraphNode>{
    @Override
    public int compare(UndirectedGraphNode a, UndirectedGraphNode b) {
        return a.degree==b.degree?0:(a.degree>b.degree?1:-1);
    }
};


class UndirectedGraph{

    int numberOfNodes;
    int numberOfEdges;
    int numberOfColors;
    HashMap< Integer, UndirectedGraphNode> vertices;
    List<UndirectedGraphNode> degreeRank;
    int dgptr;
    UndirectedGraph(int n){
        numberOfNodes=n;
        numberOfEdges=0;
        numberOfColors=0;
        vertices = new HashMap< Integer , UndirectedGraphNode>();
        degreeRank= new ArrayList<UndirectedGraphNode>();
        dgptr=0;
        for (int i = 0; i < n; i++){
            UndirectedGraphNode tmp=new UndirectedGraphNode(i);
            vertices.put(i , tmp);
            degreeRank.add(tmp);
        } 
    }

    public void display(){
        //adjacency matrix
        
        for (int i=0;i<numberOfNodes;i++){
            for (int j=0;j<numberOfNodes;j++){
                if (i==j){
                    System.out.print(0);
                    System.out.print('\t');
                    continue;
                }
                int p=hasEdge(i, j)?1:0;
                System.out.print(p);
                System.out.print('\t');               
            }
            System.out.print('\n');
        }
        
        //color
        System.out.println("Node , Color");
        Set<Integer> colorset=new HashSet<Integer>();
        for (int i=0;i<numberOfNodes;i++){
            UndirectedGraphNode u=getNode(i);
            System.out.println(Integer.toString(i)+" , "+Integer.toString(u.color));
            colorset.add(u.color);
        }
        this.numberOfColors=colorset.size();

        //numberOfcolor
        System.out.println("NumberOfcolors:"+Integer.toString(this.numberOfColors));
        //numberOfedges
        System.out.println("NumberOfedges:"+Integer.toString(this.numberOfEdges));
        //numberOfNodes
        System.out.println("NumberOfNodes:"+Integer.toString(this.numberOfNodes));
        //density
        System.out.println("Density:"+Double.toString(this.getDensity()));
    }

    public int getColors(){
        return numberOfColors;
    }

    public double getDensity(){
        return 1.0*numberOfEdges/(numberOfNodes*(numberOfNodes-1)/2.0);
    }

    public UndirectedGraphNode getNode(int u){
        return vertices.get(u);
    }

    public boolean hasNode(int label){
        return vertices.containsKey(label);
    }

    public boolean hasEdge(int u, int v){
        UndirectedGraphNode nodeX=getNode(u);
        UndirectedGraphNode nodeY=getNode(v);        
        return nodeX.hasNeighbor(nodeY);
    }

    public boolean addEdge(int u, int v){
        UndirectedGraphNode nodeX=getNode(u);
        UndirectedGraphNode nodeY=getNode(v);
        if (nodeX.hasNeighbor(nodeY)){return false;}

        nodeX.addNeighor(nodeY);
        nodeY.addNeighor(nodeX);
        nodeX.degreeIncrement();
        nodeY.degreeIncrement();
        numberOfEdges++;
        return true;
    }

    public void sortNodes(){
        Collections.sort(this.degreeRank, new NodeDegreeComparator());
    }

    public UndirectedGraphNode getFirstNode(){
        while (dgptr<degreeRank.size()){
            if (degreeRank.get(dgptr).color > -1){
                dgptr++;
            }
            else{
                return degreeRank.get(dgptr);
            }
        }
        return new UndirectedGraphNode(numberOfNodes+1);

    }
    public void incrementDgptr(){
        dgptr++;
    }


    public void DSaturUpdate(UndirectedGraphNode u, int c, MaxHeap heap){
        for (UndirectedGraphNode v:u.neighbors){
            if (v.isColored()){continue;}
            //add to colorset
            v.addColorset(c);
            int key=v.label;
            v.updateSat();
            if (heap.contains(key)){
                heap.siftUp(heap.getPos(key));
            }
            else{
                heap.insert(v);
            }
        }
        return;
    }

    public void DSatur(int n){
        MaxHeap heap= new MaxHeap(n);
        UndirectedGraphNode u=this.getFirstNode();
        int c=u.setColor();
        DSaturUpdate(u,c,heap);

        for (int k=0;k<n-1;k++){
            
            if (heap.isEmpty()){
                heap= new MaxHeap(n);
                //get node with max degree
                u=this.getFirstNode();
            }
            else{
                //pop heap
                u= heap.remove();
            }
            //set color
            c=u.setColor();
            //update
            DSaturUpdate(u,c,heap);
        }
    }


};

class GraphGenerator{

    double decay_r=0.8;

    public void connectT1(UndirectedGraph g, int n){
        g.addEdge(1,2);
        for (int i=3;i<n;i++){
            int p=(int)(Math.random() * (i-1));
            g.addEdge(i,p);
        }
    }

    private int binarySearch(double[] prb, int r,double key){
        //int ans=0;
        for (int i=r-2;i>0;i--){
            if (key>prb[i]){
                return i+1;
            }
        }
        return 0;
    }

    public int pick(double[] s,int r){
        double sv=0.0;
        double nv[]=new double[r];
        for (int i=0;i<r;i++){sv+=s[i];}
        for (int i=0;i<r;i++){nv[i]=(s[i]/sv);}
        for (int i=0;i<r-1;i++){nv[i+1]+=nv[i];}
        nv[r-1]=1.01;
        double key=Math.random();
        //binary search
        return binarySearch(nv,r,key);
    }

    public void connectT2(UndirectedGraph g, int n){
        g.addEdge(1,2);
        double[] s=new double[g.numberOfNodes];
        s[1]=s[0]=100.0;
        for (int i=2;i<n;i++){
            int p=pick(s,i);
            g.addEdge(i,p);
            s[i]=i*100.0;
            for (int j=0;j<i;j++){
                s[j]*=decay_r;
            }
            
            }
        }

    public UndirectedGraph generateOne(int n, double density){
        int total=(n*(n-1))/2;
        double mDensity= (total*density-n+1)/(total-n+1);
        UndirectedGraph g=new UndirectedGraph(n);
        //connectT1(g,n);
        connectT2(g,n);
        for (int i =0;i<n-1;i++){
            for (int j=i+1;j<n;j++){
                if (g.hasEdge(i,j)) {continue;}
                double tmp_r=Math.random();
                if (tmp_r<mDensity){
                    g.addEdge(i,j);
                }
            }
        }
        g.sortNodes();
        return g;
    }

    public UndirectedGraph[] generateGraphs(int num, int nodes,double density){
        UndirectedGraph[] res=new UndirectedGraph[num]; 
        for (int i=0;i<num;i++){
            res[i]=generateOne(nodes, density);
        }
        return res;
    }

}

class MaxHeap{
    UndirectedGraphNode[] heap;
    int size;
    int max_size;
    private static final int front =1;
    NodeComparator ncompare;
    HashMap<Integer, Integer> elements;
    MaxHeap(int n){
        this.size=0;
        this.max_size=n;
        this.heap=new UndirectedGraphNode[n+1];
        ncompare=new NodeComparator();
        elements= new HashMap<Integer, Integer>();
    }

    public boolean isEmpty(){
        return size<=0;
    }

    public boolean contains(int n){
        return elements.containsKey(n);
    }

    public int getPos(int n){
        return elements.get(n);
    }

    public boolean compareEle(int p1,int p2){
        return ncompare.compare(heap[p1], heap[p2])>0;
    }

    public void swap(int p1,int p2){
        UndirectedGraphNode tmp;
        int u=heap[p1].label;
        int v=heap[p2].label;
        elements.put(u,p2);
        elements.put(v,p1);
        tmp=heap[p1];
        heap[p1]=heap[p2];
        heap[p2]=tmp;
    }

    public int parent(int pos){
        return pos/2;
    }

    public int leftchild(int pos){
        return (2*pos)>size?-1:(2*pos);
    }

    public int rightchild(int pos){
        return (2*pos+1)>size?-1:(2*pos+1);
    }

    public boolean isRoot(int pos){
        return pos==1;
    }

    public boolean isLeaf(int pos){
        return ( pos>=(size/2) && pos<=size)?true:false;
    }

    public void siftUp(int pos){
        if (!isRoot(pos)){
            int pr=parent(pos);
            if (compareEle(pos, pr)){
                swap(pos,pr);
                siftUp(pr);
            }
        }
    }

    public void siftDown(int pos){
        if (!isLeaf(pos)){
            int lch=leftchild(pos);
            int rch=rightchild(pos);
            int mch=-1;
            if (lch>0 && rch>0){mch=(compareEle(lch,rch)?lch:rch);}
            else{mch=(lch>0?lch:rch);}

            if (!compareEle(pos,mch)){
                swap(pos,mch);
                siftDown(mch);
            }
        }

    }

    public UndirectedGraphNode remove(){
        UndirectedGraphNode ans=heap[front];
        heap[front]=heap[size--];
        if (isEmpty()){return ans;}
        elements.put(heap[front].label,front);
        siftDown(front);
        elements.remove(ans.label);
        return ans;
    }

    public void insert(UndirectedGraphNode key){
        heap[++size]=key;
        elements.put(key.label,size);
        siftUp(size);
    }

};

public class TCSS543{

	public static void main(String[] args) {
        int nn=0;
        double[] darray={0.3,0.5,0.65,0.75};
        int ng=100;
		//generate graphs
        for (double d:darray){ 
            nn=0;
        for (int i=0;i<10;i++){
            nn+=10;
            GraphGenerator a = new GraphGenerator();
            UndirectedGraph[] glst=a.generateGraphs(ng,nn,d);
        
            long startTime = System.currentTimeMillis();
            for (UndirectedGraph g:glst){
                //100 DSatur
                g.DSatur(nn);
            }
            long endTime   = System.currentTimeMillis();
            double totalTime = (endTime - startTime)/1000.0;

            //statistics
            double maxD=0.0;
            double minD=2.0;
            double avgD=0.0;
            for (UndirectedGraph g:glst){
                g.display();          
                double dtmp=g.getDensity();
                avgD+=dtmp;
                if(d>maxD){maxD=dtmp;}
                if(d<minD){minD=dtmp;}
            }

            for (UndirectedGraph g:glst){
                System.out.println(Integer.toString(g.numberOfColors));
            }
            System.out.println("NomuberOfNodes:"+Integer.toString(nn));
            System.out.println("RunngingTime(s):"+Double.toString(totalTime));
            System.out.println("Average Density:"+Double.toString(avgD/ng));
            System.out.println("Max Density:"+Double.toString(maxD));
            System.out.println("Min Density:"+Double.toString(minD));
            System.out.println("==============================");
        }
	}
    }
}