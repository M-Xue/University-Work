// Centrality Measures API implementation
// COMP2521 Assignment 2

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

#include "CentralityMeasures.h"
#include "Dijkstra.h"
#include "PQ.h"


static int numPaths(ShortestPaths paths, Vertex src, Vertex end);

NodeValues closenessCentrality(Graph g) {
	NodeValues nvs = {0};
	
	nvs.numNodes = GraphNumVertices(g);
	nvs.values = malloc(nvs.numNodes * sizeof(double));
	
	// Looping through each vertex to get their centrality values
	for (int src = 0; src < GraphNumVertices(g); src++) {
		ShortestPaths paths = dijkstra(g, src);
		
		double sumOfShortestPaths = 0;
		double reachableNodes = 0;
		
		// Looping through each vertex to get the sum of shortest paths
		for (int v = 0; v < paths.numNodes; v++) {
		    if (paths.dist[v] != INT_MAX) {
			    sumOfShortestPaths += paths.dist[v];
			    reachableNodes++;
		    }
	    }
	    
	    if (reachableNodes == 1) {
	        nvs.values[src] = 0;
	    } else {
	        nvs.values[src] = ((reachableNodes - 1)/(nvs.numNodes - 1)) *
	        ((reachableNodes - 1)/sumOfShortestPaths);
		}
		
		freeShortestPaths(paths);
	}
	
	return nvs;
}

NodeValues betweennessCentrality(Graph g) {
	NodeValues nvs = {0};
	
	nvs.numNodes = GraphNumVertices(g);
	nvs.values = malloc(nvs.numNodes * sizeof(double));
	
	// Looping through each vertex to count its appearances 
	// in all shortest paths (i.e., calculating Betweenness Centrality)
	for (int centralityVertex = 0; centralityVertex < nvs.numNodes;
	centralityVertex++) {
	    
	    // Looping through each vertex as the source vertex to check their paths
	    // with every other vertex 
	    for (int src = 0; src < GraphNumVertices(g); src++) {
	    
		    ShortestPaths paths = dijkstra(g, src);
		    
		    // Looping through each vertex to treat them as the end vertex
		    // of a path from the source vertex src
		    for (int end = 0; end < GraphNumVertices(g); end++) {
		        
		        // Can't count the appearances of the vertex being counted for 
		        // centrality if it is the source node or the end node 
		        // since we ignore the first and last node of a path 
		        if (centralityVertex != src && centralityVertex != end) {
		            float appearances = 0;
	                float totalShortestPaths = 0;
		            
		            totalShortestPaths += numPaths(paths, src, end);
	                
	                // Since we want the number of paths where the vertex being
	                // tested for centrality is passed through, we can find 
	                // the number of paths from the source vertex to the 
	                // centrality vertex and the number of paths from the 
	                // centrality vertex to an end vertex and multiply the two 
	                // numbers to find the total paths 
	                appearances += numPaths(paths, centralityVertex, end) *
	                numPaths(paths, src, centralityVertex);
	                
	                if (totalShortestPaths == 0) {
	                    // If the number of shortest paths is 0, then 6(v)/6 is 
	                    // treated as 0
	                    nvs.values[centralityVertex] += 0;
	                } else {
	                    nvs.values[centralityVertex] += 
	                    appearances / totalShortestPaths;
	                }
		        }
		    }
		    freeShortestPaths(paths);
	    }
        
	}
	
	return nvs;
}


NodeValues betweennessCentralityNormalised(Graph g) {
	NodeValues nvs = {0};
	
	float numVertices = GraphNumVertices(g);
	
	// Since all we are doing by normlising is multiplying each 
	// Betweenness Centrality for a particular graph by a constant, we can 
	// get all the Betweenness Centrality values with betweennessCentrality() 
	// and multiply every value by 1 / ((numVertices - 1) * (numVertices - 2))
	nvs = betweennessCentrality(g);
	for (int v = 0; v < GraphNumVertices(g); v++) {
	    nvs.values[v] = nvs.values[v] * 
	    1 / ((numVertices - 1) * (numVertices - 2));
	}
	
	return nvs;
}

void showNodeValues(NodeValues nvs) {

}

void freeNodeValues(NodeValues nvs) {
    free(nvs.values);
}

// Takes a graph, a source (starting) vertex and ending vertex. It then 
// recursively goes backwards from the end vertex to look through of the 
// predecessors of the end node and then their predecessors and so on. 
// Returns the number of paths from the starting vertex to the ending vertex.
static int numPaths(ShortestPaths paths, Vertex src, Vertex end) {
    
    // Base case is when the vertex being searched for predecessors is the 
    // source vertex. Once you reached the source, that is considered one path
    if (end == src) {
        return 1;
    }
    
    int currV = 0;
    PredNode *curr = paths.pred[end];
    int count = 0;
    while (curr != NULL) {
        
        currV = curr->v;
        count += numPaths(paths, src, currV);
        
        curr = curr->next;
    }
    
    return count;
}

