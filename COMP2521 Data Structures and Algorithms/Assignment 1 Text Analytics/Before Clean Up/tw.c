// COMP2521 21T2 Assignment 1
// tw.c ... compute top N most frequent words in file F
// Usage: ./tw [Nwords] File

// Author: Max Xue
// zID: z5267325
// Date: 28/06/2021

// Header Comment: 
// This program takes in one or two command line arguments. 
// The first optional argument gives the number of words to be output.
// The second argument gives the name of a text file. If the first argument is
// not given, the default value of 10 is used. If the given Nwords argument is 
// less than 10, it is set to 10.
// It them reads text from the file, and computes word (stem) frequencies.
// Printing a list of the top Nwords most frequent words (word stems), 
// from most frequent to least frequent, where words with the same frequency 
// are in increasing lexicographic order.

#include <assert.h>
#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "Dict.h"
#include "stemmer.h"
#include "WFreq.h"

#define MAXLINE 1000
#define MAXWORD 100

#define isWordChar(c) (isalnum(c) || (c) == '\'' || (c) == '-')

// add function prototypes for your own functions here

int main(int argc, char *argv[]) {
	
	int   nWords;    // number of top frequency words to show
	char *fileName;  // name of file containing book text
    
    
	// process command-line args
	switch (argc) {
		case 2:
			nWords = 10;
			fileName = argv[1];
			break;
		case 3:
			nWords = atoi(argv[1]);
			if (nWords < 10) nWords = 10;
			fileName = argv[2];
			break;
		default:
			fprintf(stderr,"Usage: %s [Nwords] File\n", argv[0]);
			exit(EXIT_FAILURE);
	}
    
    
    FILE *stopwordsFile;
    stopwordsFile = fopen("stopwords", "r");
    
    // Check if the stopwords file is in the directory.
    if (stopwordsFile == NULL) {
        fprintf(stderr, "Can't open stopwords\n");
        exit(EXIT_FAILURE);
    }
    
    // Creating a Dict BSTree to hold all the stopwords so searching for a 
    // specific word is faster later on.
    char stopword[MAXWORD + 1];
    char stopwordWithoutNewLine[MAXWORD + 1];
    Dict stopwordsBST = DictNew();
    
    while (fgets(stopword, MAXWORD + 1, stopwordsFile) != NULL) {
        strcpy(stopwordWithoutNewLine, stopword);
        stopwordWithoutNewLine[strlen(stopwordWithoutNewLine) - 1] = '\0';
        DictInsert(stopwordsBST, stopwordWithoutNewLine);
    }
    fclose(stopwordsFile);
    
    
    FILE *fp;
    fp = fopen(fileName, "r"); 
    
    // Check if the given file argument is in the directory.
    if (fp == NULL) {
        fprintf(stderr, "Can't open %s\n", fileName);
        exit(EXIT_FAILURE);
    }
    
    char line[MAXLINE + 1]; // the + 1 is for the null terminator 
    
    // This string copies the original line but swaps all unaccpeted characters 
    // with ' ' so the token() function can remove those characters too.
    char alteredLine[MAXLINE + 1];
    
    
    // The beginningMarker checks if fputs has looped through the document 
    // to the point where it can actually start adding words to the BST.
    // This beginning line is started by "*** START OF". 
    // 0 if it hasn't been encountered, 1 if it has been encountered.
    int beginningMarker = 0;
    
    
    // The endingMarker checks if fputs has looped through the document
    // to the point where it has hit the end of the actual book.
    // This endling line is started by "*** END OF".
    // 0 if it hasn't been encountered, 1 if it has been encountered.
    int endingMarker = 0;
    
    
    Dict d = DictNew();
    
    
    while (fgets(line, MAXLINE + 1, fp) != NULL) {
        
        // This switches the endingMarker on, meaning that you have reached the 
        // end of the book and should stop running the while loop. It is put 
        // at the front because the moment it is swtiched on, you want to stop.
        if (strstr(line, "*** END OF")) {
            endingMarker = 1; 
        }
        
        // This if condition means that the beginningMarker must be switched on 
        // and the endingMarker must be switched off. This suggests the 
        // beginning of the book has begun but the end has not been met.
        if (beginningMarker == 1 && endingMarker == 0) {
            
            // This for loop goes through the line an replaces every 
            // non alphabetics (upper and lower case), numbers, single-quote 
            // and hyphen character with the space character " " so that 
            // strtok() will treat all unaccepted characters as the end 
            // or beginning of a word sequence.
            strcpy(alteredLine, line);
            
            
            // The - 1 in strlen(line) - 1 is to account for the null terminator
            // \0 should not be set to ' '.
            for (int lineIndexCounter = 0; lineIndexCounter < strlen(line) - 1;
            lineIndexCounter++) {
                if (isWordChar(line[lineIndexCounter]) != 1) {
                    alteredLine[lineIndexCounter] = ' ';
                }
            }
            
        	// strcpy the return form strtok() into currWord
	        char currWord[MAXWORD + 1];
            
            // TOKENISING //////////////////////////////////////////////////////
            
            // The following while loop breaks all the words in a line into 
            // tokens using strtok(). Each iteration of the loop processes 
            // one word at a time in the line.
            char *token = strtok(alteredLine, " ");
            
            while (token != NULL) {
                
                // NORMALISING /////////////////////////////////////////////////
                
                strcpy(currWord, token);
                
                // The for loop below uses tolower(currWord[i]) to normalise the
                // current word.
                for (int i = 0; i < strlen(currWord); i++) {
                    currWord[i] = tolower(currWord[i]);
                }

                // STOPWORD REMOVAL AND SINGLE CHARACTER REMOVAL ///////////////
                
                // Creating a int variable to use as a check to see if the given
                // word is in stopwords BSTree.
                int stopwordCheck = DictFind(stopwordsBST,currWord);
                
                // The strlen() check prevents any words that are only 
                // one character long from being added to the BST.
                // The if statement adds the current word, after it's been 
                // stemmed, if it didn't appear in the stopword BSTree.
                if (strlen(currWord) > 1 && currWord[1] != '\n' &&
                stopwordCheck == 0) {
                    
                    // Need to get rid of the '\n' character at the end of 
                    // words that end a line and made it through all processes 
                    // so far.
                    char processedWord[MAXWORD + 1];
                    
                    int x = 0;
                    while (currWord[x] != '\n' && x < strlen(currWord)) {
                        processedWord[x] = currWord[x];
                        x++;
                    }
                    processedWord[x] = '\0';
                    
                    // STEMMING ////////////////////////////////////////////////
                    
                    stem(processedWord, 0, strlen(processedWord) - 1);

                    DictInsert(d, processedWord);
                }
                
                // Iterating through the line to each word.
                token = strtok(NULL, " ");
            }
            
        }
        
        // This is put AFTER the beginning marker if condition because 
        // the actual line including "*** START OF" is not part of the text 
        // and should not be added to the BST. Because it has been switched on 
        // at the end, the NEXT iteration of the loop will begin processing 
        // lines into the BST.
        if (strstr(line, "*** START OF")) {
            beginningMarker = 1;
        }
        
	}
	
	
	// If the ending marker was never switched on, it means the string
	// "*** END OF" was never encountered. This means the file is not 
	// a Project Gutenberg book.
	if (endingMarker == 0) {
	    fprintf(stderr, "Not a Project Gutenberg book\n");
	    exit(EXIT_FAILURE);
	}
	
	// If the beginning marker was never switched on, it means the string
	// "*** START OF" was never encountered. This means the file is not 
	// a Project Gutenberg book.
	if (beginningMarker == 0) {
	    fprintf(stderr, "Not a Project Gutenberg book\n");
        exit(EXIT_FAILURE);
	}
	
	
	// Creates a WFreq array with enough memory space for nWords index elements.
	WFreq *wfs = malloc(nWords*sizeof(WFreq));
	
	// Need to save the return of DictFindTopN() to know if the number of words
	// in the book is less than the nWords argument or the given nWords
	// argument is less than the number of words in the given book. 
	int sizeOfFinalArray = DictFindTopN(d, wfs, nWords);
    
    // Using the sizeOfFinalArray variable, we stop iterating through the wfs
    // array if nWords is smaller or the number of words in the book is smaller.
    // If nWords is smaller, we stop printing that many words because thats how
    // big our wfs array is. If the number of words in the book is smaller, we 
    // stop printing at that number so we do not try printing uninitialised 
    // indexs in wfs.
	for (int i = 0; i < sizeOfFinalArray; i++) {
        printf("%d %s\n", wfs[i].freq, wfs[i].word);
    }
	
	

	free(wfs);
	
    fclose(fp);
    DictFree(d);
    DictFree(stopwordsBST);
    
    
}

// add your own functions here































































