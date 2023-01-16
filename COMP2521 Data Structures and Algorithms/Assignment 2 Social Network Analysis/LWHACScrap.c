


function testing


/*
                printf("\nNew Distances Array\n");
                // Printing the 2D array for testing
                for (int i = 0; i < numClusters; i++) {
                    for (int j = 0; j < numClusters; j++) {
                    
                        if (newDistances[i][j] == INFINITY) {
		                    printf("[INF] ");
	                    } else {
		                    printf("[%f] ", newDistances[i][j]);
	                    }
                    }
                    printf("\n");
                }
                printf("\n");
                */




    /*
    printf("\nNew Distances Array\n");
    // Printing the 2D array for testing
    for (int i = 0; i < numClusters; i++) {
        for (int j = 0; j < numClusters; j++) {
        
            if (newDistances[i][j] == INFINITY) {
		        printf("[INF] ");
	        } else {
		        printf("[%f] ", newDistances[i][j]);
	        }
        }
        printf("\n");
    }
    printf("\n");
    */
    


    
    printf("test3\n");
    printf("numClusters = [%d]\n", numClusters);
    printf("mergedCluster1 = [%d]\n", mergedCluster1);
    printf("mergedCluster2 = [%d]\n", mergedCluster2);
    // TODO Testing 
    
    
    // ISSUSE WITH THE DIAGONAL LINE OF -1s DFLKIUGHSLFGH TODO TODO TODO 
    // Initialising all array index elements in the new array 






    //root = dendA[1]; //TODO test 
    
    
    
    
    // create new dendA array and put all the vertices in unless is == currR || ==currC 
    
    // readjust the 2D array 
    // it doesnt matter what dendA array is what index. you just need to make sure the dendA and the 2D array are in the same order 
    
    // put a while loop and do while numCluster > 1 and then -- from numClusters at the end of the loop 
    // put dendA == newDendA at the start of the while loop and free dendA right after you finish processing the newDeandA 




    //dendA[0] = mergeCluster(dendA[0], dendA[1]);

///// TODO EVERYTHING UP TO HERE WORKS
        
        
        // printf("numClusters = [%d]\n", numClusters);
        
        //printf("j = [%d]\n", j);





//printf("numClusters = [%d]\n", numClusters);





















/*
        printf("numClusters = [%d]\n", numClusters);
        printf("currR = [%d]\n", currR);
        printf("currC = [%d]\n", currC);
        printf("[%f] \n", distances[currR][currC]);
        
        
        // Printing the 2D array for testing
    for (int i = 0; i < numClusters; i++) {
        for (int j = 0; j < numClusters; j++) {
        
            if (distances[i][j] == INFINITY) {
		        printf("[INF] ");
	        } else {
		        printf("[%f] ", distances[i][j]);
	        }
        }
        printf("\n");
    }
    */


















Dendrogram tests

    /*
    Dendrogram currRD = createLeafCluster(currR);
    
    Dendrogram currCD = createLeafCluster(currC);
    
    
    
    Dendrogram root = malloc(sizeof(Dendrogram));
    
    root->right = currRD;
    root->left = currCD;
    
    */


    //printf("currR = [%d]\n", currR);
    //printf("currC = [%d]\n", currC);
    //printf("[%f] \n", distances[currR][currC]);














Adding to 2d array testing


//printf("i = [%d]\n", i);
                //printf("j = [%d]\n", j);
                AdjList AdjITest = AdjI;
                while (AdjITest != NULL) {
	                //printf("\nTest AdjITest->v = %d\n", AdjITest->v);
	                //printf("Test AdjITest->weight = %d\n\n", AdjITest->weight);
	                AdjITest = AdjITest->next;
	            }


//printf("iToj = [%d]\n", iToj);
            //printf("jToi = [%d]\n", jToi);







2D array testing 


printing out the 2d array 
    
    /*
    for (int i = 0; i < GraphNumVertices(g); i++) {
        for (int j = 0; j < GraphNumVertices(g); j++) {
        
            if (distances[i][j] == INFINITY) {
		        printf("[INF] ");
	        } else {
		        printf("[%f] ", distances[i][j]);
	        }
	        
        }
        printf("\n");
    }
    */





    //printf("test\n");
    
    /*
    distances[0][0] = 1;
    
    printf("[%f] ", distances[0][0]);
    printf("\n");
    */
    
    // Note when creating the array, need to make usre i != j cuz those array elements should not be touched
    
    //printf("%f\n", INFINITY); 


    //double **distances;
    
    //printf("[%ld] \n", sizeof(double));
    
    //printf("[%ld] \n", sizeof(*distances));

text plan 

// how to measure distance. Get the weight between two nodes and find the largest distance (since two nodes can have up to two weights, one from a to b and another 
    // from b to a
    
    
    // delete clusters from the list as you merge them
    // you dont need to keep creating arrays. you can just use the original array but check the distance formula on each vertex in a cluster 
    
    
    
    
    // Distances between clusters
    // single link: you go through all the verticies in the clusters and find the two (take any vertex in each cluster) with the closest distance and link them 
    // complete: take a vertex in each cluster than are the furthest away from eacch other and link them. just single link but loop through both clusters 
    // so big o n^2 and find the largest distance 
    
    // ^^ these links are a measure of distance. You do not merge them just becasuse you found the distance. You simly update the distances and then look for 
    // two clusters that are closest together based on those distances and merge them. you then repeat the process with all remaining cluster, including 
    // the new one you just formed by merging
    // This will change the order of merging because clusters that were close using single link could be far using complete link, causing other clusters to merge first
    
    
    // LanceWilliams
    // Find the shortest existing distance between all existing clusters.
    // Merge the closest clusters 
    // UPDATE the distance between the new cluster and every single cluster that
    // exists (all the other clusters should already exist because they werent touched in the merge 
    
    
    // NOTE: TODO you dont do the part below exactly. ITs just explaining. best method is the part below below this  
    // Using single link as example for LW Algo check 2:30 of third youtube vid 
    // Single link: To update the distance between a cluster K and the new cluster i+j, take the single link distance between the cluster i and k
    // and the distance between the cluster j and k (the two distances should already by calculated due to previous distance calculations between clusters) 
    // and find the minimum one out of the two. That is the new distance between k and cluster i+j
    
    
    // To swap between the two methods, just change the values of the constants a,b and y (b is always 0 tho so just never use it)
    



