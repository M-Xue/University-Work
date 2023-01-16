# Data Structures and Algorithms

The goal of this course is to deepen students' understanding of data structures and algorithms and how these can be employed effectively in the design of software systems. Explores the nuance and detail of how computers store data and process that data when solving problems and how different storage and processing methods have vastly different outcomes at scale.

**Topics**:
An introduction the structure, analysis and usage of a range of fundamental data types and the core algorithms that operate on them, including: algorithm analysis, sorting, searching, trees, graphs, files, algorithmic strategies, analysis and measurement of programs. Labs and programming assignments in C, using a range of Unix tools.

## Assignments

### [Assignment 1: Text Analytics](https://cgi.cse.unsw.edu.au/~cs2521/21T2/assignments/ass1)

In this assignment, we aim to write a program which can extract one important text analytics "measure": the frequency of occurrence of the most common words in the text.

Used Dictionary ADT and binary search.

### [Assignment 2: Social Network Analysis](https://cgi.cse.unsw.edu.au/~cs2521/21T2/assignments/ass2)

The main focus of this assignment is to implement graph-based data analysis functions that could be used to identify influencers, followers and communities in a given social network.

#### **Part 1**

Implemented a variant Dijkstra's algorithm to discover the shortest paths from a given source node to all other nodes in the graph. The algorithm offers one important additional feature that the regular Dijkstra's algorithm does not: if there are multiple shortest paths from a source node to another node, it keeps track of all of them by allowing each node to have multiple predecessors. In the code, this is achieved by each node having a linked list of predecessors

#### **Part 2**

Implemented two well-known centrality measures for a given directed weighted graph. Closeness Centrality and Betweenness Centrality.

#### **Part 3**

Implemented the Hierarchical Agglomerative Clustering (HAC) algorithm to discover communities in a given graph
