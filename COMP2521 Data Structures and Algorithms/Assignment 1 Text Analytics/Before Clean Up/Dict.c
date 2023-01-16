// COMP2521 21T2 Assignment 1
// Dict.c ... implementation of the Dictionary ADT

// Author: Max Xue
// zID: z5267325
// Date: 28/06/2021

// Header Comment:
// This program is the implementation file of the Dict ADT.

#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "Dict.h"
#include "WFreq.h"

#define MAXWORD 100

// you may define your own structs here

typedef struct DictNode *NodeLink;

// The DictRep struct contains a pointer the root of a BSTree.
struct DictRep {
    NodeLink root;
};

// DictNode struct is every actual node in a Dict BSTree.
typedef struct DictNode {
    char word[MAXWORD + 1];
    NodeLink left;
    NodeLink right;
    int height;
    WFreq data;
} DictNode;

typedef struct WFreqDataNode *WFreqLink;

// WFreqDataNode struct is the nodes for a WFreq BST.
typedef struct WFreqDataNode {
    
    WFreq WFreqData;
    WFreqLink left;
    WFreqLink right;
    
} WFreqDataNode;

typedef struct WFreqRoot *WFreqTree;

// The WFreqRoot struct contains a pointer the root of a WFreq BST.
struct WFreqRoot {
    WFreqLink root;
};


// add function prototypes for your helper functions here

void DictShowR(NodeLink currNode, int depth);

NodeLink insertLink(NodeLink currNode, char *word);

void freeNode(NodeLink node);

NodeLink rotateLeft(NodeLink rotatedNode);

NodeLink rotateRight(NodeLink rotatedNode);

int heightOfNode(NodeLink node);

int max(int a, int b);

NodeLink search(NodeLink currNode, char *word);

WFreqLink insertWFreq(WFreqLink currWFreqNode, NodeLink currNode);

void DictTraversal(NodeLink currNode, WFreqTree WFT);

void WFTShow(WFreqTree WFT);

void WFTShowR(WFreqLink currNode, int depth);

int sizeOfWFTree(WFreqLink currNode);

int copyWFTreeToArray(WFreqLink currWFreqNode, WFreq *EntireWFTArray, int i);

void freeWFTree(WFreqLink node);

// Creates a new Dictionary 
Dict DictNew(void) {
    Dict d = malloc(sizeof(struct DictRep));
    d->root = NULL;
    
    return d;
}

// Frees the given Dictionary 
void DictFree(Dict d) {
    
    if (d->root != NULL) {
        freeNode(d->root);
    }
    
    free(d);
}

// Inserts an occurrence of the given word into the Dictionary
void DictInsert(Dict d, char *word) {
    
    d->root = insertLink(d->root, word);
    
}

// Returns the occurrence count of the given word. Returns 0 if the word
// is not in the Dictionary.
int DictFind(Dict d, char *word) {
    NodeLink foundWord = search(d->root, word);
    
    if (foundWord != NULL) {
        return foundWord->data.freq;
    }
    
	return 0;
}

// Finds  the top `n` frequently occurring words in the given Dictionary
// and stores them in the given  `wfs`  array  in  decreasing  order  of
// frequency,  and then in increasing lexicographic order for words with
// the same frequency. Returns the number of WFreq's stored in the given
// array (this will be min(`n`, #words in the Dictionary)) in  case  the
// Dictionary  does  not  contain enough words to fill the entire array.
// Assumes that the `wfs` array has size `n`.
int DictFindTopN(Dict d, WFreq *wfs, int n) {
    
    
    // Creating a non-AVL Tree with the nodes containing WFreq pointers
    // containing every word in the Dict AVL BSTree and their respective 
    // frequencies. The WFTree is ordered by frequency of the word and 
    // duplicates (words with the same frequency) can exist. Words with the 
    // same frequency are ordered by their descending lexicographic order.
    WFreqTree WFT = malloc(sizeof(struct WFreqRoot));
    WFT->root = NULL;
    DictTraversal(d->root, WFT);

    
    // Creating WFreq array to copy every WFTree node in order so it is easier 
    // to copy into wfs array argument. This way, we can decide when to stop 
    // copying into the wfs array based on the nWord argument given in the main 
    // or the number of words in the book, which ever one is smaller.
    WFreq *EntireWFTArray = malloc(sizeOfWFTree(WFT->root)*sizeof(WFreq));
    copyWFTreeToArray(WFT->root, EntireWFTArray, 0);
    
    
    // Copying every element in the WFT array until the nWord argument or the 
    // number of words in the book has been reached, which ever one comes first.
    for (int i = 0; i < n && i < sizeOfWFTree(WFT->root); i++) {
        wfs[i].word = EntireWFTArray[i].word;
        wfs[i].freq = EntireWFTArray[i].freq;
    }
    
    // If the size of the tree has less nodes than the given input n, when 
    // printing the index elements in the main function, the index counter
    // needs to stop before the size of the tree rather than stopping before n.
    
    int sizeOfWFArray = sizeOfWFTree(WFT->root);
    
    freeWFTree(WFT->root);
    free(WFT);
    free(EntireWFTArray);
    
    // Returns the number of nodes in the WFTree (which is the number of 
    // processed words) if this number is smaller than the nWords argument in
    // the main.
    if (sizeOfWFArray < n) {
        return sizeOfWFArray;
    }

	return n;
}

// Displays the given Dictionary. This is purely for debugging purposes,
// so you may display the Dictionary in any format you want.  You may
// choose not to implement this.
void DictShow(Dict d) {
    DictShowR(d->root, 0);
}


// HELPER FUNCTIONS ************************************************************

// Goes through the Dict BST and recursively frees every node.
void freeWFTree(WFreqLink node) {
    if (node != NULL) {
        freeWFTree(node->left);
        freeWFTree(node->right);
        
        free(node);
    }
}

// Checks the size of a given WFreq Tree
int sizeOfWFTree(WFreqLink currNode) {
    if (currNode == NULL) {
        return 0;
    }
    return 1 + sizeOfWFTree(currNode->left) +  sizeOfWFTree(currNode->right);
}

// Copies the data in a WFreq Tree's nodes into a WFreq array in order 
int copyWFTreeToArray(WFreqLink currWFreqNode, WFreq *EntireWFTArray, int i) {
    if(currWFreqNode == NULL) {
        return i;
    }
    
    if(currWFreqNode->right != NULL) {
        i = copyWFTreeToArray(currWFreqNode->right, EntireWFTArray, i);
    }
    
    EntireWFTArray[i].word = currWFreqNode->WFreqData.word;
    EntireWFTArray[i].freq = currWFreqNode->WFreqData.freq;
    i++;
    
    if(currWFreqNode->left != NULL) {
        i = copyWFTreeToArray(currWFreqNode->left, EntireWFTArray, i);
    }
    
    return i;
}

// Traverses an entire Dict BSTree, calling insertWFreq() at every node to 
// insert the node's data into a WFreq Tree.
void DictTraversal(NodeLink currNode, WFreqTree WFT) {
    if (currNode != NULL) {

        WFT->root = insertWFreq(WFT->root, currNode);
        
        DictTraversal(currNode->left, WFT);
        DictTraversal(currNode->right, WFT);
    }
}

// Takes the WFreq data in a given Dict BSTree node and inserts it into 
// a WFreq Tree.
WFreqLink insertWFreq(WFreqLink currWFreqNode, NodeLink currNode) {
    
    if (currWFreqNode == NULL) {
        
        WFreqLink newNode = malloc(sizeof(struct WFreqDataNode));
        newNode->WFreqData.word = currNode->data.word;
        newNode->WFreqData.freq = currNode->data.freq;
        newNode->left = NULL;
        newNode->right = NULL;
        
        return newNode;
        
    } else if (currWFreqNode->WFreqData.freq == currNode->data.freq) {
        
        if (strcmp(currNode->word, currWFreqNode->WFreqData.word) > 0) {
            currWFreqNode->left = insertWFreq(currWFreqNode->left, currNode);
        } else if (strcmp(currNode->word, currWFreqNode->WFreqData.word) < 0) {
            currWFreqNode->right = insertWFreq(currWFreqNode->right, currNode);
        }
        
    } else if (currNode->data.freq < currWFreqNode->WFreqData.freq) {
        currWFreqNode->left = insertWFreq(currWFreqNode->left, currNode);
    } else if (currNode->data.freq > currWFreqNode->WFreqData.freq) {
        currWFreqNode->right = insertWFreq(currWFreqNode->right, currNode);
    }
    
    return currWFreqNode;
}

// Prints a sideways representation of a WFreq Tree.
void WFTShow(WFreqTree WFT) {
    WFTShowR(WFT->root, 0);
}

// Helper function for WFTShow().
void WFTShowR(WFreqLink currNode, int depth) {
    if (currNode != NULL) {
        WFTShowR(currNode->right, depth + 1);
        
        for (int i = 0; i < depth; i++) {
            printf("\t");
        }
        printf("%s|F:%d\n", currNode->WFreqData.word, currNode->WFreqData.freq);
        
        WFTShowR(currNode->left, depth + 1);     
    }
}

// Inserts a word into a Dict BSTree based on lexicographic order. If the word 
// is already in the tree, increase the frequency counter for the corresponding 
// node holding that word.
NodeLink insertLink(NodeLink currNode, char *word) {
    // Inserts the node if you have reached the end of the tree
    if (currNode == NULL) {
        NodeLink newNode = malloc(sizeof(struct DictNode));
        strcpy(newNode->word, word);

        newNode->left = NULL;
        newNode->right = NULL;
        newNode->height = 0;

        newNode->data.word = newNode->word;
        newNode->data.freq = 1;

        return newNode;
    
    // Checks if the inserted word already exsists in the tree. 
    } else if (strcmp(currNode->word, word) == 0) {

        currNode->data.freq++;

    // strcmp() returns negative value if lhs appears before rhs in 
    // lexicographical order (uppercase letters preceed all lowercase letters).
    } else if (strcmp(word, currNode->word) < 0) {
        
        currNode->left = insertLink(currNode->left, word);

    // strcmp() returns positive value if lhs appears after rhs in 
    // lexicographical order (uppercase letters preceed all lowercase letters).
    } else if (strcmp(word, currNode->word) > 0) {
    
        currNode->right = insertLink(currNode->right, word);

    }
    
    
    // AVL maintance
    int leftHeight = heightOfNode(currNode->left); 
    int rightHeight = heightOfNode(currNode->right); 
    
    currNode->height = 1 + max(leftHeight, rightHeight);
    
    if ((leftHeight - rightHeight) > 1) {
        if (strcmp(word,currNode->left->word) > 0) {
            currNode->left = rotateLeft(currNode->left);
        } 
        
        currNode = rotateRight(currNode);
        
    } else if ((rightHeight - leftHeight) > 1) {
        if (strcmp(word,currNode->right->word) < 0) {
            currNode->right = rotateRight(currNode->right);
        }

        currNode = rotateLeft(currNode);
    }
    
    return currNode;
}

// Helper funcion for DictFind(). Goes through a Dict BSTree looking for a word.
// If found, it returns that node. If not, it returns NULL.
NodeLink search(NodeLink currNode, char *word) {
    
    
    if (currNode == NULL) {
        return NULL;
    }
    
    int comparison = strcmp(word, currNode->word);
    
    if (comparison == 0) {
        return currNode;
    } else if (comparison > 0) {
        return search(currNode->right, word);
    } else if (comparison < 0) {
        return search(currNode->left, word);
    }
    
    return currNode;
}

// Helper function for DictFree(). Recursively frees every node in a Dict BSTree
void freeNode(NodeLink node) {
    if (node != NULL) {
        freeNode(node->left);
        freeNode(node->right);
        
        free(node);
    }
}

// Takes two integers and returns the larger one.
int max(int a, int b) {
    if (a >= b) {
        return a;
    } else if (b > a) {
        return b;
    }
    return 0;
}

// Returns the height of a node. Helps account for NULL case of child nodes.
int heightOfNode(NodeLink node) {
    if (node == NULL) {
        return -1;
    }
    return node->height;
}

// Rotates a node right.
NodeLink rotateRight(NodeLink rotatedNode) {
    if (rotatedNode == NULL || rotatedNode->left == NULL) {
        return rotatedNode;
    }
    
    NodeLink leftChild = rotatedNode->left;
    rotatedNode->left = leftChild->right;
    leftChild->right = rotatedNode;
    
    // Adjusting the height .
    rotatedNode->height = 
    max(heightOfNode(rotatedNode->left), heightOfNode(rotatedNode->right)) + 1;
    leftChild->height = 
    max(heightOfNode(leftChild->left), heightOfNode(leftChild->right)) + 1;
    
    return leftChild;
}

// Rotates a node left.
NodeLink rotateLeft(NodeLink rotatedNode) {
    if (rotatedNode == NULL || rotatedNode->right == NULL) {
        return rotatedNode;
    }
    
    NodeLink rightChild = rotatedNode->right;
    rotatedNode->right = rightChild->left;
    rightChild->left = rotatedNode;
    
    // Adjusting the height .
    rotatedNode->height = 
    max(heightOfNode(rotatedNode->left), heightOfNode(rotatedNode->right)) + 1;
    rightChild->height = 
    max(heightOfNode(rightChild->left), heightOfNode(rightChild->right)) + 1;
    
    return rightChild;
}

// Helper function for DictShow().
void DictShowR(NodeLink currNode, int depth) {
    if (currNode != NULL) {
        
        DictShowR(currNode->right, depth + 1);
        
        for (int i = 0; i < depth; i++) {
            printf("\t");
        }
        printf("%s | F:%d | H:%d\n", currNode->word, currNode->data.freq, heightOfNode(currNode));
        //printf(" | H:%d\n",heightOfNode(currNode));
        DictShowR(currNode->left, depth + 1);     
    }
}

