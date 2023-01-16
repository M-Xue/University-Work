

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
    
    // TODO numClusters should be 1 less than the previous iteration so put this update after the numClusters-- TODO 
    double **newDistances = malloc(numClusters * sizeof(*distancesArray));
    for (int i = 0; i < numClusters; i++) {
        newDistances[i] = malloc(numClusters * sizeof(**distancesArray));
    }
    
    int r = 0;
    int c = 0;
    // Initialising all array index elements in the new array 
    for (int i = 0; i < numClusters + 1; i++) {
        for (int j = 0; j < numClusters + 1; j++) {
            
            
            if (j != mergedCluster1 && j != mergedCluster2) {
                
                newDistances[r][c] = distancesArray[i][j];
                c++;
            }
        }
        
        
        if (i != mergedCluster1 && i != mergedCluster2) {
            r++;
        }
        
    }
    
    // Put the new cluster values here. It should be the last index of cols and rows
    // the Dk,i is just distance distancesArray[k][j];
    
    // the -1 here in the for statement is so that it doesnt overlap [numClusters - 1][numClusters - 1] later
    for (int i = 0; i < numClusters - 1; i++) {
        
        
        float absDifference = distancesArray[i][mergedCluster1] - distancesArray[i][mergedCluster2];
        if (absDifference < 0) {
            absDifference = -absDifference;
        }
        
        float LanceWilliamsDistance = a * distancesArray[i][mergedCluster1] + 
        a * distancesArray[i][mergedCluster2] + y * absDifference;
        
        
        newDistances[i][numClusters - 1] = LanceWilliamsDistance;
        newDistances[numClusters - 1][i] = LanceWilliamsDistance;
        
        
    }
    
    
    
    // TODO free double **distancesArray
    
    for (int i = 0; i < numClusters; i++) {
        free(newDistances[i]);
    }
    free(newDistances);
    
    
    return newDistances;
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
    
    // TODO numClusters should be 1 less than the previous iteration so put this update after the numClusters-- TODO 
    double **newDistances = malloc(numClusters * sizeof(*distances));
    for (int i = 0; i < numClusters; i++) {
        newDistances[i] = malloc(numClusters * sizeof(**distances));
    }
    
    int r = 0;
    int c = 0;
    // Initialising all array index elements in the new array 
    for (int i = 0; i < numClusters + 1; i++) {
        for (int j = 0; j < numClusters + 1; j++) {
            
            
            if (j != mergedCluster1 && j != mergedCluster2) {
                
                newDistances[r][c] = distancesArray[i][j];
                c++;
            }
        }
        
        
        if (i != mergedCluster1 && i != mergedCluster2) {
            r++;
        }
        
    }
    
    // Put the new cluster values here. It should be the last index of cols and rows
    // the Dk,i is just distance distancesArray[k][j];
    
    // the -1 here in the for statement is so that it doesnt overlap [numClusters - 1][numClusters - 1] later
    for (int i = 0; i < numClusters - 1; i++) {
        
        
        float absDifference = distancesArray[i][mergedCluster1] - distancesArray[i][mergedCluster2];
        if (absDifference < 0) {
            absDifference = -absDifference;
        }
        
        float LanceWilliamsDistance = a * distancesArray[i][mergedCluster1] + 
        a * distancesArray[i][mergedCluster2] + y * absDifference;
        
        
        newDendArray[i][numClusters - 1] = LanceWilliamsDistance;
        newDendArray[numClusters - 1][i] = LanceWilliamsDistance;
        
        
    }
    
    
    
    // TODO free double **distancesArray
    
    for (int i = 0; i < numClusters; i++) {
        free(newDistances[i]);
    }
    free(newDistances);
    
    
    return newDistances;
}











// Put this stuff in a function TODO so it creates a new array every tiem you update the clusters
    
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
    
    
    // TODO put this in a function too 
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






    return distances;
}















































