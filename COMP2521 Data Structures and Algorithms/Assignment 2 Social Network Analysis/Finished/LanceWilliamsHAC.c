// Lance-Williams Algorithm for Hierarchical Agglomerative Clustering
// COMP2521 Assignment 2

#include <assert.h>
#include <float.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>

#include "Graph.h"
#include "LanceWilliamsHAC.h"

#define INFINITY DBL_MAX

/**
 * Generates  a Dendrogram using the Lance-Williams algorithm (discussed
 * in the spec) for the given graph  g  and  the  specified  method  for
 * agglomerative  clustering. The method can be either SINGLE_LINKAGE or
 * COMPLETE_LINKAGE (you only need to implement these two methods).
 * 
 * The function returns a 'Dendrogram' structure.
 */
 
 
Dendrogram createLeafCluster(Vertex v);
Dendrogram mergeCluster(Dendrogram d1, Dendrogram d2);
double **updateClusterDistanceArray(int numClusters, double **distancesArray, 
int mergedCluster1, int mergedCluster2, int method);
 
Dendrogram LanceWilliamsHAC(Graph g, int method) {
    
    
    int numClusters = GraphNumVertices(g);
    
    
    Dendrogram *dendA = malloc(numClusters * sizeof(Dendrogram));
    
    for (int i = 0; i < numClusters; i++) {
        dendA[i] = createLeafCluster(i);
    }
    
    
    // Create a 2D array to store distances between clusters
    double **distances = malloc(numClusters * sizeof(*distances));
    for (int i = 0; i < numClusters; i++) {
        distances[i] = malloc(numClusters * sizeof(**distances));
    }
    
    
    // Initialising all array index elements to infinity
    for (int i = 0; i < numClusters; i++) {
        for (int j = 0; j < numClusters; j++) {
            if (i == j) {
                // A cluster cannot have a distance to itself so we denote it 
                // as -1.
                distances[i][j] = -1;
            } else {
                distances[i][j] = DBL_MAX;
            }
        }
    }
    
    
    // Calculating and entering all the distances of the vertices from each 
    // other
    for (int i = 0; i < numClusters; i++) {
        for (int j = 0; j < numClusters; j++) {
            
            float iToj = 0;
            float jToi = 0;
            
            if (i != j && GraphIsAdjacent(g, i, j)) {
                // If theres a direct path from vertex i to j
                
                AdjList AdjI = GraphOutIncident(g, i);
                while (AdjI != NULL) {
                    if (AdjI->v == j) {
                        iToj = AdjI->weight;
                        break;
                        
                    }
                    AdjI = AdjI->next;
                }
            } 
            
            if (i != j && GraphIsAdjacent(g, j, i)) {
                // If theres a direct path from vertex j to i
                
                AdjList AdjJ = GraphOutIncident(g, j);
                while (AdjJ != NULL) {
                    if (AdjJ->v == j) {
                        jToi = AdjJ->weight;
                        break;
                    }
                    AdjJ = AdjJ->next;
                }
	        }
	        
	        if ((iToj != 0 || jToi != 0) && iToj > jToi) {
	            distances[i][j] = 1 / iToj;
	            distances[j][i] = 1 / iToj;
	        }
	        
	        if ((iToj != 0 || jToi != 0) && jToi > iToj) {
	            distances[i][j] = 1 / jToi;
	            distances[j][i] = 1 / jToi;
	        }
        }
    }
    

    while (numClusters > 1) {
        
        // Finds the index of the distances array with the smallest distance.
        int r = 0;
        int c = 0;
        float minDist = DBL_MAX;
        int currR = 0;
        int currC = 0;
        while (r < numClusters) {
            while (c < numClusters) {
                
                
                if (distances[r][c] < minDist && distances[r][c] != -1) {
                    minDist = distances[r][c];
                    currR = r;
                    currC = c;
                }
                
                c++;
            }
            c = 0;
            r++;
        }
        
        // Creates a new cluster out of the closest clusters by mergeing.
        Dendrogram newCluster = mergeCluster(dendA[currC], dendA[currR]);
        numClusters--;
        
        // Creates a new dendrogram array with the merged clusters removed.
        Dendrogram *newDendArray = malloc(numClusters * sizeof(Dendrogram));
        int j = 0;
        for (int i = 0; i < numClusters + 1; i++) {
            if (i != currC && i != currR) {
                
                newDendArray[j] = dendA[i];
                j++;
            }
        }
        
        
        
        free(dendA);
        // Adds the new cluster to the end of the new dendrogram array.
        newDendArray[j] = newCluster;
        
        dendA = newDendArray; 
        
        distances = updateClusterDistanceArray(numClusters, distances, 
        currR, currC, method);
        
    }
    
    Dendrogram root = dendA[0];
    
	return root;
}

/**
 * Frees all memory associated with the given Dendrogram structure.
 */
void freeDendrogram(Dendrogram d) {
    
    
    // TODO TODO TODO TODO TODO 
    
}

// TODO comments
Dendrogram createLeafCluster(Vertex v) {
    Dendrogram newDendrogram = malloc(sizeof(Dendrogram));
    newDendrogram->vertex = v;
    newDendrogram->left = NULL;
    newDendrogram->right = NULL;
    return newDendrogram;
}


Dendrogram mergeCluster(Dendrogram d1, Dendrogram d2) {
    Dendrogram newDendrogram = malloc(sizeof(Dendrogram));
    newDendrogram->vertex = -1; // Helps check if dendrogram is leaf
    newDendrogram->left = d1;
    newDendrogram->right = d2;
    return newDendrogram;
}




double **updateClusterDistanceArray(int numClusters, double **distancesArray, int mergedCluster1, int mergedCluster2, int method) {
    
    float a = 0.5;
    float y;
    switch (method) {
        case SINGLE_LINKAGE:
            y = -0.5;
            break;
        case COMPLETE_LINKAGE:
            y = 0.5;
            break;
        default:
            printf("Invalid method\n");
            return NULL;
    }
    
    double **newDistances = malloc(numClusters * sizeof(*distancesArray));
    for (int i = 0; i < numClusters; i++) {
        newDistances[i] = malloc(numClusters * sizeof(**distancesArray));
    }
    
    int r = 0;
    int c = 0;
    
    // Initialising all array index elements in the new array 
    for (int i = 0; i < numClusters + 1; i++) {
        for (int j = 0; j < numClusters + 1; j++) {
            
            if (j != mergedCluster1 && j != mergedCluster2 && i != mergedCluster1 && i != mergedCluster2) {
                
                newDistances[r][c] = distancesArray[i][j];
                c++;
            }
        }
        c = 0;
        
        if (i != mergedCluster1 && i != mergedCluster2) {
            r++;
        }
    }
    
    
    int j = 0;
    for (int i = 0; i < numClusters; i++) {
        
        if (i != mergedCluster1 && i != mergedCluster2) {
            
            
            float absDifference = distancesArray[i][mergedCluster1] - distancesArray[i][mergedCluster2];
            if (absDifference < 0) {
                absDifference = -absDifference;
            }
            
            float LanceWilliamsDistance = a * distancesArray[i][mergedCluster1] + 
            a * distancesArray[i][mergedCluster2] + y * absDifference;
            
            if (distancesArray[i][mergedCluster1] == DBL_MAX) {
            
                LanceWilliamsDistance = distancesArray[i][mergedCluster2];
                
            } else if (distancesArray[i][mergedCluster2] == DBL_MAX) {
            
                LanceWilliamsDistance = distancesArray[i][mergedCluster1];
                
            } else if (distancesArray[i][mergedCluster2] == DBL_MAX &&
            distancesArray[i][mergedCluster1] == DBL_MAX) {
            
                LanceWilliamsDistance = DBL_MAX;
                
            }
            
            newDistances[j][numClusters - 1] = LanceWilliamsDistance;
            newDistances[numClusters - 1][j] = LanceWilliamsDistance;
            
            j++;
        }
    }
    
    for (int i = 0; i < numClusters; i++) {
        for (int j = 0; j < numClusters; j++) {
            if (i == j) {
                newDistances[i][j] = -1;
            }
        }
    }
    
    
    for (int i = 0; i < numClusters; i++) {
        free(distancesArray[i]);
    }
    free(distancesArray);
    
    return newDistances;
}













