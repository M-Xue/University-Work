For loop tests
	    
	    //printf("totalShortestPaths = %f\n", totalShortestPaths);
	    
	    
	    
	    //printf("\n\n\n"); // TODO
	                //printf("totalShortestPaths = %f\n", totalShortestPaths);
	                //printf("appearances = %f\n", appearances);
	                //printf("\n");

		        //printf("centralityVertex = %d\n", centralityVertex);
		        //printf("src = %d\n", src);
		        //printf("end = %d\n", end);
		        // Check for isolated nodes
	
	////////////////////////
	
	
	//////////////////////// TEST TODO
	
	/*
	float appearances1 = numPaths(paths, 1, 1) *
	numPaths(paths, 1, 2);
	printf("appearances1 = %f\n", appearances1);
	printf("numPaths(paths, 1, 1) = %d\n", numPaths(paths, 1, 1));
	printf("numPaths(paths, 0, 1) = %d\n", numPaths(paths, 0, 1));
	
	
	
	
	
	    //////////////////////// TEST TODO
	ShortestPaths paths = dijkstra(g, 0);
	
	int numberOfPaths = numPaths(paths, 0, 1);
	printf("numPaths = %d\n", numberOfPaths);
	
	
	
	
	// Check for isolated nodes
	*/
	
	
	
	
	
	////////////////////////

	//printf("numPaths(paths, 1, 1) = %d\n", numPaths(paths, 1, 1));
	
		            
	
	//numPaths(ShortestPaths paths, Vertex src, Vertex end);
	
	
	
	















Testing recursion funciton

//////////////////////// TEST TODO
	ShortestPaths paths = dijkstra(g, 4);
	
	int numberOfPaths = numPaths(paths, 4, 0);
	printf("numPaths = %d\n", numberOfPaths);
	
	
	// Check for isolated nodes
	
	////////////////////////


    Second recursion funct 
	//////////////////////// TEST TODO
	ShortestPaths paths = dijkstra(g, 0);
	
	int numberOfPaths = appearancePaths(paths, 0, 1, 2);
	printf("numPaths = %d\n", numberOfPaths);
	
	
	// Check for isolated nodes
	
	////////////////////////
	
	// TODO TODO 
static int appearancePaths(ShortestPaths paths, Vertex src, Vertex end, 
Vertex v) {
    
    
    if (end == src) {
        return 0;
    }
    
    int currV = 0;
    PredNode *curr = paths.pred[end];
    int count = 0;
    
    // Loops through the whole graph
    while (curr != NULL) {
    
        currV = curr->v;
        count += appearancePaths(paths, src, currV, v);
        
        curr = curr->next;
    }
    
    // Moment it finds target vertex in graph, it returns num of paths from 
    // target vertex to source vertex
    if (end == v) {
        currV = 0;
        PredNode *curr = paths.pred[v];
        while (curr != NULL) {
        
            currV = curr->v;
            count += numPaths(paths, src, currV);
            
            curr = curr->next;
        }
        return count;
    }
    
    
    return count;
}
// Multiply this with numPaths() from end to v with v as the new src argument?
// USe if statment to check that appearancePaths() returned not 0?
// Wait actualy you might not even need to do that 


// OR yse numPaths twice, usingv

	

Original centrality calculations
/*
	
	// Looping through each vertex to count its appearances 
	// in all shortest paths
	for (int centralityVertex = 0; centralityVertex < nvs.numNodes;
	centralityVertex++) {
	    float appearances = 0;
	    float totalShortestPaths = 0; // TODO TODO need to use this to count how many shortest paths there are
	
	    // Looping through each vertex as the source vertex to check their paths
	    // with every other vertex 
	    for (int src = 0; src < GraphNumVertices(g); src++) {
		    
		    
		    // TODO can probably put the dijkstra call inside the if statmenet for effiecny sake?
		    ShortestPaths paths = dijkstra(g, src);
		    
		    // Can't count the appearances of the vertex being counted for 
		    // centrality if it is the source node since we ignore the first
		    // node or a path 
		    if (centralityVertex != src) {
		        for (int v = 0; v < GraphNumVertices(g); v++) {
		            PredNode *curr = paths.pred[v];
		            
		            while (curr != NULL) {
			            if (curr->v == centralityVertex) {
			                appearances++;
			            }
			            curr = curr->next;
		            }
		        }
		        
		    }
		    
		    freeShortestPaths(paths);
	    }
	    
	    // TODO 
	    // Need to find the number of shortest paths between src and v and 
	    // divide appearances with that value 
	    //nvs.values[centralityVertex] = appearances;
	}
	*/
