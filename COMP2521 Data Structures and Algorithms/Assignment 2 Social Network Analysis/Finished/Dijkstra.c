// Dijkstra API implementation
// COMP2521 Assignment 2

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

#include "Dijkstra.h"
#include "Graph.h"
#include "PQ.h"

ShortestPaths dijkstra(Graph g, Vertex src) {
	ShortestPaths sps = {0};
	
	// The number of vertices in the graph
	sps.numNodes = GraphNumVertices(g);             
	
	// The source vertex. This element will give the node its label 
	// "E.g., Node 0"   
	sps.src = src;                                  
	
	// Creates an array of length V to store the cumulative distance 
	// from source vertex
	sps.dist = malloc(sps.numNodes * sizeof(int));  
	
	// Initialises all distances as INT_MAX to denote they haven't been visited 
	// yet
	for (int v = 0; v < sps.numNodes; v++) {        
	    sps.dist[v] = INT_MAX;                           
	}
	
	// Creates an array of length V of linked lists to store predecessor nodes
	// Initialises all linked lists to point to an empty list (NULL)
	sps.pred = malloc(sps.numNodes * sizeof(*sps.pred));  
	for (int v = 0; v < sps.numNodes; v++) {        
	    sps.pred[v] = NULL;
	}
	
	sps.dist[src] = 0;              // Setting the source vertex distance to 0
	// Can start checking for minimum dist[v] immediately. Just make sure to 
	// include sps.dist[vertex] > -1 in an if condition check because negative
	// numbers will mess up the <= inequality
	
	
	// Basically vSet
	// The block of code below represents how many vertecies have not been 
	// visited and which ones they are
	
	// Creates an array of length V to check if the vertex has been explored
	//  and initialises all verticies to "not been explored"
	bool vertexHasBeenVisited[sps.numNodes];        
	for (int v = 0; v < sps.numNodes; v++) {        
	    vertexHasBeenVisited[v] = false;
	}
	// Creates counter to see if all verticies have been explored
	int numVerticiesNotExplored = sps.numNodes;     
	
	
	// From this point forward, when the term TODO EXPLORE TODO  is used, it 
	// means the current vertex being checked (i.e., its children are being 
	// checked for edge relaxation) 
	while (numVerticiesNotExplored > 0) {
        
	    // find v in vSet with minimum dist[v]
	    // The block of code with the for loop below finds the vertex with the 
	    // smallest cumulative weight from the source vertex that has not been 
	    // explored yet and saves its index number as well as the cumulative 
	    // weight 
	    
	    // minDist is the smallest cumulative distance out of all 
        // the vertecies that have not been explored yet
	    int minDist = INT_MAX;
	    
        // minDistIndex is the vertex with the smallest cumulative distance out 
        // of the vertices that have not been explored yet                               
        int minDistIndex = INT_MAX;              
	                                                        
        // This for loop only works on vertices that have not been explored yet
	    for (int v = 0; v < sps.numNodes; v++) {
            if (sps.dist[v] != INT_MAX && sps.dist[v] <= minDist && 
            vertexHasBeenVisited[v] == false) {
                minDistIndex = v;
                minDist = sps.dist[v];
            }
	    }
	    // The rest of the while loop will explore this vertex
	    
	    // This ends the while loop because, while every vertex hasn't been 
	    // explored, all the ones with paths from the src have been visited
	    // This occurs when sps.dist[v] == INT_MAX but all the children 
	    // vertecies have been explored
	    if (minDistIndex == INT_MAX) {
            break;
        }
	    
	    
	    // EDGE RELAXATION /////////////////////////////////////////////////////
	    
	    // Gets a list of all the children of the vertex currently being 
	    // explored, with their corresponding edge weights
	    AdjList currExploredVertexChild = GraphOutIncident(g, minDistIndex);
	    
	    while (currExploredVertexChild != NULL) {
	        int edgeWeight = currExploredVertexChild->weight;
	        
	        
	        // If a new path with lower weight has been found 
	        if (sps.dist[currExploredVertexChild->v] == INT_MAX || 
	        sps.dist[minDistIndex] + edgeWeight < 
	        sps.dist[currExploredVertexChild->v]) {
                
                // Updating distance array of explored child
                sps.dist[currExploredVertexChild->v] = 
                sps.dist[minDistIndex] + edgeWeight;
                
                if (sps.pred[currExploredVertexChild->v] != NULL) {
                    
                    // Clearing old predecessor linked list since we found a 
                    // shorter path 
                    PredNode *head = sps.pred[currExploredVertexChild->v];
                    sps.pred[currExploredVertexChild->v] = NULL;
                    
                    while (head->next != NULL) {
                        
                        PredNode *next = head;
                        PredNode *curr = next;
                        
                        while (next->next != NULL) {
                            curr = next;
                            next = next->next;
                        }
                        // Should stop at the last node so 
                        // next->next points at NULL at this point 
                        // curr points at the second last node
                        
                        curr->next = NULL;
                        free(next);
                    }
                    
                    free(head);
                }
                
                // Adding new predecessor to START of 
                // sps.pred[currExploredVertexChild->v]
                PredNode *newNode = malloc(sizeof(PredNode));
                newNode->next = NULL;
                newNode->v = minDistIndex;
                sps.pred[currExploredVertexChild->v] = newNode;
                
            } else if (sps.dist[minDistIndex] + edgeWeight == 
            sps.dist[currExploredVertexChild->v]) {
            
                // Extra predecessors feature
                
                // Adding new predecessor to END of 
                // sps.pred[currExploredVertexChild->v]
                PredNode *newNode = malloc(sizeof(PredNode));
                newNode->next = NULL;
                newNode->v = minDistIndex;
                PredNode *curr = sps.pred[currExploredVertexChild->v];
                
                while (curr->next != NULL) {
                    curr = curr->next;
                }
                // Should stop at the last node so 
                // curr->next points at NULL at this point 
                curr->next = newNode;
            }
	        currExploredVertexChild = currExploredVertexChild->next;
	    }
        
	    // Removing the currently explored vertex from vSet
	    vertexHasBeenVisited[minDistIndex] = true;
	    numVerticiesNotExplored--;
	}
	
	return sps;
}

void showShortestPaths(ShortestPaths sps) {

}

void freeShortestPaths(ShortestPaths sps) {
    
    // Clearing predecessor linked list array linked lists
    for (int v = 0; v < sps.numNodes; v++) {
        // Clearing predecessor linked list if it exists
        if (sps.pred[v] != NULL) {
            PredNode *head = sps.pred[v];
            sps.pred[v] = NULL;
            
            while (head->next != NULL) {
                
                PredNode *next = head;
                PredNode *curr = next;
                
                while (next->next != NULL) {
                    curr = next;
                    next = next->next;
                }
                // Should stop at the last node so 
                // next->next points at NULL at this point 
                // curr points at the second last node
                
                curr->next = NULL;
                free(next);
            }
            
            free(head);
        }
    }

    free(sps.pred);
    free(sps.dist);
}

