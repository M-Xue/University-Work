/*******************************************************************************
> CSpotify - 20T3 COMP1511 Assignment 2
| cspotify.c
|
| zID: z5267325
| Name: Max Xue
| Date: 5 November 2020
| Program Description:
| The program allows you to create playlists in a given library and add tracks
| to each playlist. You are also able to delete and rename these playlists.
| You can print out the whole library. You can also print out every track 
| with the same Soundex Coding as a given artist and copy paste all these 
| tracks into a new playlist. You can also delete specific tracks or the entire 
| library but you need to end the program if you delete the library. You can 
| calculate the cumulative length of the tracks in a selected playlist. You can 
| cut and paste specific tracks into chosen playlists. You can also reorder a
| playlist's tracks. 
|
| Version 1.0.0: Assignment released.
|
*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h> 

#include "cspotify.h"

/******************************************************************************/
// TODO: Add any other #defines you need.


/******************************************************************************/
// 'struct library' represents a library, which represents the state of the
// entire program. It is mainly used to point to a linked list of 
// playlists, though you may want to add other fields to it.
//
// You may choose to add or change fields in this struct.
struct library {
    struct playlist *head;
    struct playlist *selectedPlaylist;
};

// 'struct playlist' represents a playlist. 
// You may choose to add or change fields in this struct.
struct playlist {
    char name[MAX_LEN];
    struct track *tracks;
    struct playlist *next;
    struct playlist *previous;
};

// 'struct trackLength' represents the length of a track. 
// You may choose to add or change fields in this struct.
struct trackLength {
    int minutes;
    int seconds;
};

// 'struct track' represents a track. 
// You may choose to add or change fields in this struct.
struct track {
    char title[MAX_LEN];
    char artist[MAX_LEN];
    struct trackLength length;
    struct track *next;
    struct track *previous;
    int position; 
};

/******************************************************************************/
// TODO: Add any other structs you define here.


/******************************************************************************/
// TODO: Add prototypes for any extra functions you create here.

int alphanumericChecker(char string[MAX_LEN]);
struct track *removeTrackFromList(Library library, char track[MAX_LEN]);
void soundexSearch(char artist [MAX_LEN], char soundexCodeHolder[MAX_LEN]);
int alphaCheck(char string[MAX_LEN]);
void add_playlist_void(Library library, char playlistName[MAX_LEN]);
struct track *removeTrackFromListGivenPlaylist(
    Library library, char track[MAX_LEN], struct playlist *givenPlaylist
);
int cut_and_paste_track_in_given_playlist(
    Library library, char trackTitle[MAX_LEN], char destPlaylist[MAX_LEN], 
    struct playlist *givenPlaylist
);
void removingArrayElement(char string[MAX_LEN], int arrayIndexNum);

// Function prototypes for helper functions. 
static void print_playlist(int number, char playlistName[MAX_LEN]);
static void print_selected_playlist(int number, char playlistName[MAX_LEN]);
static void print_track(
    char title[MAX_LEN], char artist[MAX_LEN], int minutes, int seconds
);

/******************************************************************************/
// You need to implement the following functions.
// In other words, write code to make the function work as described 
// in cspotify.h

/*********
> STAGE 1
*********/

// Create a new Library and return a pointer to it.
Library create_library(void) {
    Library newLibrary = malloc(sizeof(struct library));
    newLibrary->head = NULL;
    return newLibrary;
}

// Add a new Playlist to the Library.
int add_playlist(Library library, char playlistName[MAX_LEN]) {
        
    if (alphanumericChecker(playlistName) == 1) {
        struct playlist *newPlaylist = malloc(sizeof(struct playlist));
        strcpy(newPlaylist->name, playlistName);
        newPlaylist->tracks = NULL;
        newPlaylist->next = NULL;
        newPlaylist->previous = NULL;

        if (library->head == NULL) {
            newPlaylist->next = library->head;
            library->head = newPlaylist;
            
            library->selectedPlaylist = library->head;
            
        } else {
            
            // Creates a playlist pointer that goes through the linked list 
            // and stops when it points at the struct playlist node right before 
            // NULL i.e. the final node of the linked list
            struct playlist *curr = library->head;
            while (curr->next != NULL) {
                curr = curr->next; 
            }
                        
            // Makes the new playlist point at what the curr pointer node's 
            // pointer is pointing at and points at the same thing 
            // (this should be NULL).
            // It then makes the curr pointer node's pointer point at 
            // newPlaylist, effectively inserting the new playlist at the end 
            // of the linked list.
            newPlaylist->next = curr->next;
            curr->next = newPlaylist; 
            newPlaylist->previous = curr;
            
        }
        return SUCCESS;
    } else {
        return ERROR_INVALID_INPUTS;
    }
}

// Print out the Library.
void print_library(Library library) {
    
    struct playlist *curr = library->head;
    int i = 0;
    
    while (curr != NULL) {
        
        if (curr == library->selectedPlaylist) {
        
            // First if statment allows the function to differentiate 
            // between the selected playlist and all other playlists so
            // that it will print the [*] next to the selected playlist title
            
            print_selected_playlist(i, library->selectedPlaylist->name);
            
            struct track *currTrack = library->selectedPlaylist->tracks;
            
            while (currTrack != NULL) {
                print_track(
                    currTrack->title, 
                    currTrack->artist, 
                    currTrack->length.minutes, 
                    currTrack->length.seconds
                );
                
                
                currTrack = currTrack->next;
            }
            
            curr = curr->next;  
            i++; 
        } else {
            print_playlist(i, curr->name);
            
            struct track *currTrack = curr->tracks;
            
            while (currTrack != NULL) {
                print_track(
                    currTrack->title, 
                    currTrack->artist, 
                    currTrack->length.minutes, 
                    currTrack->length.seconds
                );
                currTrack = currTrack->next;
            }
            
            curr = curr->next;  
            i++; 
        }
    }
}

// Rename the name of an existing Playlist.
int rename_playlist(Library library, char playlistName[MAX_LEN],
    char newPlaylistName[MAX_LEN]) {
    
    if (alphanumericChecker(newPlaylistName) == 0) {
        return ERROR_INVALID_INPUTS;
    }
    
    struct playlist *curr = library->head;
        
    // The total number of characters in both strings should be equal because 
    // of the first if statment condition. If the strings are the same length 
    // and the number of similar characters is equal to the full string, then 
    // they are the same 
    
    while (curr != NULL) {
        if (strlen(playlistName) == strlen(curr->name)) {
            int i = 0;
            int sameCharacterCounter = 0;
            
            while (curr->name[i] != '\0' && playlistName[i] != '\0') {
                if (curr->name[i] == playlistName[i]) {
                    sameCharacterCounter++;
                }
                i++;
            }
            if (sameCharacterCounter == strlen(playlistName)) {
                strcpy(curr->name, newPlaylistName);
                return SUCCESS;
            } else {
                curr = curr->next;
            }
        } else {
            curr = curr->next;
        }
    }
    
    if (curr == NULL) {
        return ERROR_NOT_FOUND;
    }  
    
    return ERROR_INVALID_INPUTS;
}

/*********
> STAGE 2
*********/

// Selects the next Playlist in the Library.
void select_next_playlist(Library library) {
    
    if (library->head != NULL) {
        if (library->selectedPlaylist->next == NULL) {
            library->selectedPlaylist = library->head;
        } else {
            library->selectedPlaylist = library->selectedPlaylist->next;
        }
    }
}

// Selects the previous Playlist in the Library.
void select_previous_playlist(Library library) {
    
    if (library->head != NULL) {
        if (library->selectedPlaylist == library->head) {
        
            struct playlist *curr = library->head;
            while (curr->next != NULL) {
                curr = curr->next;
            }
            
            library->selectedPlaylist = curr;    
        } else {
            library->selectedPlaylist = library->selectedPlaylist->previous;
        }
    }
}

// Add a new Track to the selected Playlist.
int add_track(Library library, char title[MAX_LEN], char artist[MAX_LEN], 
    int trackLengthInSec, int position) {
    
    if (library->head == NULL) {
        return ERROR_NOT_FOUND;
    }
    
    if (position < 0 || trackLengthInSec < 0) {
        return ERROR_INVALID_INPUTS;
    }
    
    if (alphanumericChecker(title) == 0 || alphanumericChecker(artist) == 0) {
        return ERROR_INVALID_INPUTS;
    }
    
    struct track *newTrack = malloc(sizeof(struct track));
    strcpy(newTrack->title, title);
    strcpy(newTrack->artist, artist);

    int numMinutes = 0;
    int secondsInTrack = trackLengthInSec;
    while (secondsInTrack >= 60) {
        secondsInTrack = secondsInTrack - 60;
        numMinutes++;
    }
    
    newTrack->length.minutes = numMinutes;
    newTrack->length.seconds = secondsInTrack;
    
    newTrack->next = NULL;
    newTrack->previous = NULL;
    

    if (library->selectedPlaylist->tracks == NULL) {
        // First if statment is for scenerios where the playlist is empty
        if (position != 0) {
            return ERROR_INVALID_INPUTS;
        } else if (position == 0) {
            newTrack->next = library->selectedPlaylist->tracks;
            library->selectedPlaylist->tracks = newTrack;
            
            return SUCCESS;
        }
    } else if (position == 0) {
        
        // Second if statement is for if the playlist is not empty but you 
        // are placing the new track in position 0.
        
        newTrack->next = library->selectedPlaylist->tracks;
        library->selectedPlaylist->tracks->previous = newTrack;
        library->selectedPlaylist->tracks = newTrack;
        
        
        return SUCCESS;
    } else {
        struct track *curr = library->selectedPlaylist->tracks;
        int currentTrackPositonCounter = 0;
        
        while (currentTrackPositonCounter != position - 1) {
            if (curr == NULL) {
                // If the playlist is empty, the input is invalid
                return ERROR_INVALID_INPUTS;
            }
            curr = curr->next;
            currentTrackPositonCounter++;
        }
        
        if (curr == NULL) {
            return ERROR_INVALID_INPUTS;
        }
        
        newTrack->next = curr->next;
        newTrack->previous = curr;
        curr->next = newTrack;
        
        
        return SUCCESS;
    }
    
    return ERROR_INVALID_INPUTS;
}

// Calculate the total length of the selected Playlist in minutes and seconds.
void playlist_length(
    Library library, int *playlistMinutes, int *playlistSeconds
) {
    
    if (library->head == NULL) {
        *playlistMinutes = -1;
        *playlistSeconds = -1;
    } else if (library->head->tracks == NULL) {
        *playlistMinutes = 0;
        *playlistSeconds = 0;
    } else {
        int playlistTotalLengthSec = 0;
        struct track *curr = library->selectedPlaylist->tracks;

        while (curr != NULL) {
        
            playlistTotalLengthSec = 
            playlistTotalLengthSec + (curr->length.minutes * 60) 
            + curr->length.seconds;
            
            curr = curr->next;
        }
        
        int totalMinutes = 0;
        
        while (playlistTotalLengthSec >= 60) {
            playlistTotalLengthSec = playlistTotalLengthSec - 60;
            totalMinutes++;
        }
        
        int remainingSeconds = playlistTotalLengthSec;
        
        *playlistMinutes = totalMinutes;
        *playlistSeconds = remainingSeconds;
    }
}


/*********
> STAGE 3
*********/

// Delete the first instance of the given track in the selected Playlist
// of the Library.
void delete_track(Library library, char track[MAX_LEN]) {

    if (library->head != NULL && library->selectedPlaylist->tracks != NULL) {
        struct track *removedTrack = removeTrackFromList(library, track);
        if (removedTrack != NULL) {
            free(removedTrack);
        }
    } 
}

// Delete the selected Playlist and select the next Playlist in the Library.
void delete_playlist(Library library) {

    if (library->head != NULL) {
        struct playlist *deletedPlaylist = library->selectedPlaylist;

        if (
            library->selectedPlaylist->previous == NULL && 
            library->selectedPlaylist->next == NULL
        ) {
            library->head = NULL;
            library->selectedPlaylist = NULL;
        } else if (library->selectedPlaylist->previous == NULL) {
            library->selectedPlaylist->next->previous = NULL;
            library->head = library->selectedPlaylist->next;
            library->selectedPlaylist = library->head;
        
        } else if (library->selectedPlaylist->next == NULL) {
            library->selectedPlaylist->previous->next = NULL;
            library->selectedPlaylist = library->head;
        } else {
            library->selectedPlaylist->next->previous = 
            library->selectedPlaylist->previous;
            
            library->selectedPlaylist->previous->next =
            library->selectedPlaylist->next;
            
            library->selectedPlaylist = library->selectedPlaylist->next;
        }
        
        if (deletedPlaylist->tracks != NULL) {
            struct track *currTrack = deletedPlaylist->tracks;
            struct track *previousTrack;
            
            while (currTrack != NULL) {
                previousTrack = currTrack;
                currTrack = currTrack->next;
                free(previousTrack);
            }
        }
        free(deletedPlaylist);
    }
}

// Delete an entire Library and its associated Playlists and Tracks.
void delete_library(Library library) {
    
    struct playlist *currPlaylist = library->head;
    
    while (currPlaylist != NULL) {
        
        struct track *currTrack = currPlaylist->tracks;

        while (currTrack != NULL) {
            struct track *previousTrack = currTrack;
            currTrack = currTrack->next;
            free(previousTrack);
        }
        
        struct playlist *previousPlaylist = currPlaylist;
        currPlaylist = currPlaylist->next;
        free(previousPlaylist);
        
    }
    
    free(library);
}


/*********
> STAGE 4
*********/

// Cut the given track in selected Playlist and paste it into the given 
// destination Playlist.
int cut_and_paste_track(Library library, char trackTitle[MAX_LEN], 
    char destPlaylist[MAX_LEN]) {
    
    // The following if statment checks if the library is empty or not 
    if (library->head == NULL) {
        return ERROR_NOT_FOUND;
    }
    
    // The following paragraph of code is to check if the destination 
    // playlist exists or not 
    struct playlist *playlistCheck = library->head;
    while (
        playlistCheck != NULL && 
        strcmp(destPlaylist, playlistCheck->name) != 0
    ) {
        playlistCheck = playlistCheck->next;
    }
    if (playlistCheck == NULL) {
        return ERROR_NOT_FOUND;
    }
    
    
    struct track *selectedTrack = NULL;
    
    if (library->head != NULL && library->selectedPlaylist->tracks != NULL) {
        selectedTrack = removeTrackFromList(library, trackTitle);
    }
    
    if (selectedTrack == NULL) {
        
        if (library->selectedPlaylist->tracks == NULL) {
            // Do nothing.
        } else {
            return ERROR_NOT_FOUND;
        }
        // The function "removeTrack" returns NULL if the playlist is empty, 
        // if the library is empty or if the given title does not match
        // any of the tracks within the playlist. The above if statement
        // differentiates between if the playlist is empty or if the track 
        // was not found in the playlist.
        
    } else if (selectedTrack != NULL) {
        
        struct playlist *currPlaylist = library->head;
        
        while (
            strcmp(destPlaylist, currPlaylist->name) != 0 && 
            currPlaylist != NULL
        ) {
            currPlaylist = currPlaylist->next;
        }
        // Stops when currPlaylist has the same name as the destination playlist
        // or if currPlaylist == NULL, meaning the destination playlist could
        // not be found
        
        if (currPlaylist == NULL) {
            return ERROR_NOT_FOUND;
            // If the playlist is not found in the library, return error
        }
        
        if (currPlaylist->tracks == NULL) {
            // Adds selected track to the head of the playlist if the playlist 
            // is empty
            
            selectedTrack->next = NULL;
            selectedTrack->previous = NULL;
            currPlaylist->tracks = selectedTrack;
            
            return SUCCESS;
        }
        
        struct track *currTrack = currPlaylist->tracks;
        
        while (currTrack->next != NULL) {
            currTrack = currTrack->next;
        }
        // Stops when currTrack is pointing at the last node on the list before
        // NULL
        
        selectedTrack->next = NULL;
        currTrack->next = selectedTrack;
        selectedTrack->previous = currTrack;
        
        return SUCCESS;
    }
    return ERROR_NOT_FOUND;
}

// Print out all Tracks with artists that have the same Soundex Encoding 
// as the given artist.
void soundex_search(Library library, char artist[MAX_LEN]) {   
    
    if (alphaCheck(artist) == 0) {
        // To make sure its alphabetical 
        
        char searchedArtistSoundexCode[MAX_LEN] = {0};
        
        soundexSearch(artist, searchedArtistSoundexCode);
        
        struct playlist *currPlaylist = library->head;
        
        while (currPlaylist != NULL) {

                struct track *currTrack = currPlaylist->tracks;
                
                while (currTrack != NULL) {
                
                char currTrackArtistSoundexCode[MAX_LEN] = {0};
                soundexSearch(currTrack->artist, currTrackArtistSoundexCode);
                
                    if (
                        strcmp(
                            searchedArtistSoundexCode, 
                            currTrackArtistSoundexCode
                        ) == 0
                    ) {
                        print_track(
                            currTrack->title, 
                            currTrack->artist, 
                            currTrack->length.minutes, 
                            currTrack->length.seconds
                        );
                    }
                    currTrack = currTrack->next;
                }
                
                currPlaylist = currPlaylist->next;  
        }
    }
}


/*********
> STAGE 5
*********/

// Move all Tracks matching the Soundex encoding of the given artist 
// to a new Playlist.
int add_filtered_playlist(Library library, char artist[MAX_LEN]) {
    
    // To check if the given artist name is alphabetical only
    if (alphaCheck (artist) == 1) {
        return ERROR_INVALID_INPUTS;
    }
    
    // To check if a playlist of the same name already exists
    struct playlist *playlistNameCheck = library->head;
    while (playlistNameCheck != NULL) {
        if (strcmp (playlistNameCheck->name, artist) == 0) {
            return ERROR_INVALID_INPUTS;
        }
        playlistNameCheck = playlistNameCheck->next;
    }

    // Get soundex code of artist   
    char searchedArtistSoundexCode[MAX_LEN] = {0};
    soundexSearch(artist, searchedArtistSoundexCode);
 
    // Create new playlist called given artist name     
    add_playlist_void(library, artist);

    // Loop through every playlist and copy paste out of playlist and into 
    // new playlist 
    struct playlist *currPlaylist = library->head;
    while (currPlaylist != NULL) {
        
        struct track *currTrack = currPlaylist->tracks;
                
        while (currTrack != NULL) {
            
            char currTrackArtistSoundexCode[MAX_LEN] = {0};
            soundexSearch(currTrack->artist, currTrackArtistSoundexCode);
            
            
            if (strcmp(currPlaylist->name, artist) == 0) {
                // This if statement checks if youve moved through the library 
                // and made it to the newly created playlist. If so, do nothing.
                // If this check point did not exist, the function would reach 
                // the newly created playlist and keep copy pasting the track 
                // into the new playlist.

                currTrack = currTrack->next;
            
            } else if (
                strcmp(
                    currTrackArtistSoundexCode, searchedArtistSoundexCode
                ) == 0
            ) {
                
                struct track *foundTrack = currTrack;
                currTrack = currTrack->next;
                cut_and_paste_track_in_given_playlist(library, foundTrack->title, artist, currPlaylist);

            } else {
                currTrack = currTrack->next;
            }
        }
        currPlaylist = currPlaylist->next;
    }    
    return SUCCESS;
}

// Reorder the selected Playlist in the given order specified by the order array.
void reorder_playlist(Library library, int order[MAX_LEN], int length) {
    
    int positionAllocator = 0;
    struct track *currTrack = library->selectedPlaylist->tracks;
    
    while (currTrack != NULL) {
        
        currTrack->position = positionAllocator;
        currTrack = currTrack->next;
        positionAllocator++;
    }

    int i = 0;
    
    while (i < length) {
    
        struct track *reorderingCurrTrack = library->selectedPlaylist->tracks;
        
        while (reorderingCurrTrack != NULL && i < length) {
            
            if (reorderingCurrTrack->position == order[i]) {
                cut_and_paste_track(
                    library, reorderingCurrTrack->title, 
                    library->selectedPlaylist->name
                ); 
            }
            
            reorderingCurrTrack = reorderingCurrTrack->next;
        }
        i++;
    } 
}


/*****************
> Helper Functions
*****************/

static void print_playlist(int number, char playlistName[MAX_LEN]) {
    printf("[ ] %d. %s\n", number, playlistName);
}

static void print_selected_playlist(int number, char playlistName[MAX_LEN]) {
    printf("[*] %d. %s\n", number, playlistName);
}

static void print_track(
    char title[MAX_LEN], char artist[MAX_LEN], int minutes, int seconds
) {
    printf("       - %-32s    %-24s    %02d:%02d\n", title, artist, 
        minutes, seconds);
}

/*****************************
> Personally Created Functions
*****************************/

// To check if all the characters in the given string are alphanumeric. 
// It returns 0 if any character is not alphanumeric
int alphanumericChecker(char string[MAX_LEN]) {

    int i = 0;
    int alphanumericChecker = 1;
    while (string[i] != '\0' && alphanumericChecker != 0) {
        if (string[i] >= '0' && string[i] <= '9') {
            alphanumericChecker = 1;
        } else if (string[i] >= 'A' && string[i] <= 'Z') {
            alphanumericChecker = 1;
        } else if (string[i] >= 'a' && string[i] <= 'z') {
            alphanumericChecker = 1;
        } else {
            // The character in the string index is not alphanumeric
            alphanumericChecker = 0;
        }
        i++;
    }
    
    // If statement below means no name was entered. Hence, playlist name 
    // is invalid 
    if (string[0] == '\0') {
        return 0;
    }
    
    if (alphanumericChecker == 0) {
        return 0;
    } else if (alphanumericChecker == 1){
        return 1;
    }
    
    return 1;
}

// This function takes a string containing the name of a track and removes
// the track with the same name from the selected playlist. It does not 
// free the malloc and instead returns a pointer to the malloc. This
// effectively removes the track from the linked list but keeps the node within
// the malloc.
struct track *removeTrackFromList(Library library, char track[MAX_LEN]) {

    if (library->head != NULL && library->selectedPlaylist->tracks != NULL) {
        
        // This if statment above ensures the library is not empty and that
        // the playlist is not empty
    
        struct track *curr = library->selectedPlaylist->tracks;
        struct track *prevNode = NULL;
        struct track *nextNode = NULL;
        
        while (curr != NULL && strcmp(track, curr->title) != 0) {
            curr = curr->next;
        }
        
        // This while statement loops through the playlist to find a track 
        // with a matching title to the string given to the function 
        
        if (curr != NULL) {
            if (
                strcmp(track, curr->title) == 0 && curr->previous == NULL &&
                curr->next == NULL
            ) {
                library->selectedPlaylist->tracks = curr->next;
                
                curr->next = NULL;
                curr->previous = NULL;

                return curr;
            
            } else if (
                strcmp(track, curr->title) == 0 && curr->previous == NULL
            ) {
                
                library->selectedPlaylist->tracks = curr->next;
                curr->next->previous = NULL;
                
                curr->next = NULL;
                curr->previous = NULL;
                
                return curr;
                
            } else if (strcmp(track, curr->title) == 0 && curr->next == NULL) {
                
                prevNode = curr->previous;
                prevNode->next = NULL;
                
                curr->next = NULL;
                curr->previous = NULL;

                return curr;
                
            } else if (strcmp(track, curr->title) == 0 && curr != NULL) {
                
                prevNode = curr->previous;
                nextNode = curr->next;

                prevNode->next = nextNode;
                nextNode->previous = prevNode;
          
                curr->next = NULL;
                curr->previous = NULL;
                
                return curr;
                
            }
            
            // The first if statement accounts for the case where you are
            // at the start of the playlist. This can be checked by seeing 
            // if the "previous" pointer is pointing at NULL AND the "next" 
            // pointer is pointing at NULL, meaning there is only one track in 
            // the playlist.
            
            // It then makes the head pointer of the playlist point at 
            // the current tracks "next", which should be NULL, so the curr 
            // node is removed from the list.
            
            // The second if statment accounts for the case where you are
            // at the start of the playlist but there is more than one track 
            // in the selected playlist. 
            
            // It then makes the head of the list point at the next track on 
            // the list and makes the "previous" pointer of the next track 
            // point at NULL because it is the new head of the list.
            
            
            // The third if statement accounts for the case where you are at 
            // the end of the playlist. This can be checked by seeing if 
            // the "next" pointer of the selected track is pointing at NULL.
            
            // It then makes the previous node's "next" pointer point at NULL.
            
            // The fourth if statement accounts for the case where you are 
            // at the middle of the list (i.e. not at the start or the end). 
            
            // It then makes the previous node's "next" pointer point at the 
            // selected node's "next" pointer and the next node's "previous" 
            // pointer point at the selected node's "previous" pointer.

        }
    }
    
    return NULL; 
    
    // If the function reaches the end of the function, it means that either 
    // the entire library is empty (no playlists) or the selected playlist 
    // is empty (no tracks). It might also mean the track title was not found.
    
    // In these cases, the function will then return a pointer to NULL.
    
    // NOTE: if the library is empty, the returned 
    // struct track * variable pointer to NULL should be unsuable since the 
    // library has no playlists to put the pointer into. 
    // Hence, you should alway write the if statement:
    // if (library->head != NULL && library->selectedPlaylist->tracks != NULL)
    // before you call the function and double check if your logic is right.
}


// This function takes two arguments. The first one is the name of an artist 
// being searched for. The second argument is an empty string for the function 
// to alter so that after the function is called, the place holder string
// now contains the Soundex Code for the given artist name. 
// Hence, you must always declare and give an empty string before you call 
// the function to hold the Soundex Code.
void soundexSearch (char artist [MAX_LEN], char soundexCodeHolder[MAX_LEN]) {

    char soundexCodeConverter[MAX_LEN] = {0};
    
    /**************
    > RULE 1, 2 ,3
    **************/
    
    int i = 0;
    
    while (artist[i] != '\0') {
        
        if (i == 0) {
            // Make the first character of the soundex code the same as 
            // the first letter of the given artist name (case insensitive)
            
            soundexCodeConverter[i] = toupper(artist[i]);
            
        } else if (
            artist[i] == 'a' || artist[i] == 'A' || artist[i] == 'e' ||
            artist[i] == 'E' || artist[i] == 'i' || artist[i] == 'I' ||
            artist[i] == 'o' || artist[i] == 'O' || artist[i] == 'u' ||
            artist[i] == 'U' || artist[i] == 'y' || artist[i] == 'Y' ||
            artist[i] == 'h' || artist[i] == 'H' || artist[i] == 'w' ||
            artist[i] == 'W'
        ) {
            // Map all occurrences of a, e, i, o, u, y, h, w to 0 
            
            soundexCodeConverter[i] = '0';
        
        } else if (
            artist[i] == 'b' || artist[i] == 'B' || artist[i] == 'f' ||
            artist[i] == 'F' || artist[i] == 'p' || artist[i] == 'P' ||
            artist[i] == 'v' || artist[i] == 'V' 
        ) {
            // Map all occurrences of b, f, p, v to 1
        
            soundexCodeConverter[i] = '1';
        
        
        } else if (
            artist[i] == 'c' || artist[i] == 'C' || artist[i] == 'g' ||
            artist[i] == 'G' || artist[i] == 'j' || artist[i] == 'J' ||
            artist[i] == 'k' || artist[i] == 'K' || artist[i] == 'q' ||
            artist[i] == 'Q' || artist[i] == 's' || artist[i] == 'S' ||
            artist[i] == 'x' || artist[i] == 'X' || artist[i] == 'z' ||
            artist[i] == 'Z' 
        ) {
            // Map all occurrences of c, g, j, k, q, s, x, z to 2
        
            soundexCodeConverter[i] = '2';
        
        } else if (
            artist[i] == 'd' || artist[i] == 'D' || artist[i] == 't' ||
            artist[i] == 'T'
        ) {
            // Map all occurrences of d, t to 3
        
            soundexCodeConverter[i] = '3';
        
        } else if (
            artist[i] == 'l' || artist[i] == 'L'
        ) {
            // Map all occurrences of l to 4
        
            soundexCodeConverter[i] = '4';
        
        } else if (
            artist[i] == 'm' || artist[i] == 'M' || artist[i] == 'n' ||
            artist[i] == 'N'
        ) {
            // Map all occurrences of m, n to 5
        
            soundexCodeConverter[i] = '5';
        
        } else if (
            artist[i] == 'r' || artist[i] == 'R'
        ) {
            // Map all occurrences of r to 6
        
            soundexCodeConverter[i] = '6';
        
        }
        i++;
    }
    
    /********
    > RULE 4 
    ********/
    
    int j = 0;
    int k = 0;
    
    // The following while loop is to remove any duplicate digits 
    while (soundexCodeConverter[j] != '\0') {
        if (soundexCodeConverter[j] == soundexCodeConverter[j + 1]) {
            k = j;
            while (soundexCodeConverter[k] != '\0') {
                soundexCodeConverter[k] = soundexCodeConverter[k + 1];
                k++;
            }
            j = 0;
        }
        j++;
    }

    j = 0;
    k = 0;

    // The following while loop is to remove any 0s in the Soundex Code 
    while (soundexCodeConverter[j] != '\0') {
        if (soundexCodeConverter[j] == '0') {
            k = j;
            while (soundexCodeConverter[k] != '\0') {
                soundexCodeConverter[k] = soundexCodeConverter[k + 1];
                k++;
            }
        }
        j++;
    }    

    /********
    > RULE 5
    ********/    
    // Remove the first digit if it matches the numerical encoding of the 
    // leading letter 
    
    if (
        soundexCodeConverter[0] == 'a' || soundexCodeConverter[0] == 'A' || 
        soundexCodeConverter[0] == 'e' || soundexCodeConverter[0] == 'E' || 
        soundexCodeConverter[0] == 'i' || soundexCodeConverter[0] == 'I' ||
        soundexCodeConverter[0] == 'o' || soundexCodeConverter[0] == 'O' || 
        soundexCodeConverter[0] == 'u' || soundexCodeConverter[0] == 'U' || 
        soundexCodeConverter[0] == 'y' || soundexCodeConverter[0] == 'Y' ||
        soundexCodeConverter[0] == 'h' || soundexCodeConverter[0] == 'H' || 
        soundexCodeConverter[0] == 'w' || soundexCodeConverter[0] == 'W' 
    ) {
    
        if (soundexCodeConverter[1] == '0') {
            removingArrayElement(soundexCodeConverter, 1);        
        }
    
    } else if (
        soundexCodeConverter[0] == 'b' || soundexCodeConverter[0] == 'B' || 
        soundexCodeConverter[0] == 'f' || soundexCodeConverter[0] == 'F' || 
        soundexCodeConverter[0] == 'p' || soundexCodeConverter[0] == 'P' ||
        soundexCodeConverter[0] == 'v' || soundexCodeConverter[0] == 'V' 
    ) {
    
        if (soundexCodeConverter[1] == '1') {
            removingArrayElement(soundexCodeConverter, 1); 
        }
    
    } else if (
        soundexCodeConverter[0] == 'c' || soundexCodeConverter[0] == 'C' || 
        soundexCodeConverter[0] == 'g' || soundexCodeConverter[0] == 'G' || 
        soundexCodeConverter[0] == 'j' || soundexCodeConverter[0] == 'J' ||
        soundexCodeConverter[0] == 'k' || soundexCodeConverter[0] == 'K' || 
        soundexCodeConverter[0] == 'q' || soundexCodeConverter[0] == 'Q' || 
        soundexCodeConverter[0] == 's' || soundexCodeConverter[0] == 'S' ||
        soundexCodeConverter[0] == 'x' || soundexCodeConverter[0] == 'X' || 
        soundexCodeConverter[0] == 'z' || soundexCodeConverter[0] == 'Z' 
    ) {

        if (soundexCodeConverter[1] == '2') {
            removingArrayElement(soundexCodeConverter, 1);
        }
    
    } else if (
        soundexCodeConverter[0] == 'd' || soundexCodeConverter[0] == 'D' || 
        soundexCodeConverter[0] == 't' || soundexCodeConverter[0] == 'T' 
    ) {

        if (soundexCodeConverter[1] == '3') {
            removingArrayElement(soundexCodeConverter, 1);
        }
      
    
    } else if (
        soundexCodeConverter[0] == 'l' || soundexCodeConverter[0] == 'L' 
    ) {

        if (soundexCodeConverter[1] == '4') {
            removingArrayElement(soundexCodeConverter, 1);
        }

    } else if (
        soundexCodeConverter[0] == 'm' || soundexCodeConverter[0] == 'M' || 
        soundexCodeConverter[0] == 'n' || soundexCodeConverter[0] == 'N' 
    ) {

        if (soundexCodeConverter[1] == '5') {
            removingArrayElement(soundexCodeConverter, 1); 
        }
        
    } else if (
        soundexCodeConverter[0] == 'r' || soundexCodeConverter[0] == 'R'
    ) {

        if (soundexCodeConverter[1] == '6') {
            removingArrayElement(soundexCodeConverter, 1); 
        }
        
    }
    
    /********
    > RULE 6
    ********/
    
    // The below if statment ensures that the Soundex Code is only 4 characters 
    if (soundexCodeConverter[4] != '\0') {
        int a = 0;
        while (soundexCodeConverter[a] != '\0') {
            a++;
        }
        // This while loop goes to the end of the string and stops at the 
        // index right after the last digit 
        
        while (a > 3 && a >= 0) {
            soundexCodeConverter[a] = '\0';
            a--;
        }
    } 
    
    // The below 3 if statements ensure the Soundex Code is at least 4 
    // characters long i.e. append 0s onto the end if less than 3 digits
    if (soundexCodeConverter[3] == '\0') {
        soundexCodeConverter[3] = '0';
    }
    
    if (soundexCodeConverter[2] == '\0') {
        soundexCodeConverter[2] = '0';
    }

    if (soundexCodeConverter[1] == '\0') {
        soundexCodeConverter[1] = '0';
    }
  
    strcpy(soundexCodeHolder, soundexCodeConverter);
}

// The fucntion below checks if the entire string consists of only 
// alphabetical characters and returns 0 if everything index element 
// is an alphabet character or returns 1 is any element is not alphabetical 
int alphaCheck (char string[MAX_LEN]) {
    
    int alphaCheck = 0;
    int i = 0;
    
    while (string[i] != '\0') {
        
        if (string[i] >= 'A' && string[i] <= 'Z') {
            // Leave alphaCheck at 0
        } else if (string[i] >= 'a' && string[i] <= 'z') {
            // Leave alphaCheck at 0
        } else {
            // If any index of the string "artist" has an element that 
            // is not an alphatetical character 
            
            alphaCheck = 1;
        }
        i++;
    }
    return alphaCheck;
}


// Add a new Playlist to the Library without returning anything. The 
// function does nothing if the given playlist name is not alphanumeric.
void add_playlist_void(Library library, char playlistName[MAX_LEN]) {
        
    if (alphanumericChecker(playlistName) == 1) {
        struct playlist *newPlaylist = malloc(sizeof(struct playlist));
        strcpy(newPlaylist->name, playlistName);
        newPlaylist->tracks = NULL;
        newPlaylist->next = NULL;
        newPlaylist->previous = NULL;

        if (library->head == NULL) {
            newPlaylist->next = library->head;
            library->head = newPlaylist;
            
            library->selectedPlaylist = library->head;
                
        } else {
            
            // Creates a playlist pointer that goes through the linked list 
            // and stops when it points at the struct playlist node right before 
            // NULL i.e. the final node of the linked list
            struct playlist *curr = library->head;
            while (curr->next != NULL) {
                curr = curr->next; 
            }
                        
            // Makes the new playlist point at what the curr pointer node's 
            // pointer is pointing at and points at the same thing 
            // (this should be NULL).
            // It then makes the curr pointer node's pointer point at 
            // newPlaylist, effectively inserting the new playlist at the end 
            // of the linked list.
            newPlaylist->next = curr->next;
            curr->next = newPlaylist; 
            newPlaylist->previous = curr;
        }
    }
}


// Cut the given track in given Playlist and paste it into the given 
// destination Playlist. This differs from the cut_and_paste_track function 
// because that function can only cut and paste from the selectedPlaylist
// whereas this function can take from any playlist given
int cut_and_paste_track_in_given_playlist(
    Library library, char trackTitle[MAX_LEN], char destPlaylist[MAX_LEN], 
    struct playlist *givenPlaylist
) {
    
    // The following if statment checks if the library is empty or not 
    if (library->head == NULL) {
        return ERROR_NOT_FOUND;
    }
    
    // The following paragraph of code is to check if the destination 
    // playlist exists or not 
    struct playlist *playlistCheck = library->head;
    while (
        playlistCheck != NULL && 
        strcmp(destPlaylist, playlistCheck->name) != 0
    ) {
        playlistCheck = playlistCheck->next;
    }
    if (playlistCheck == NULL) {
        return ERROR_NOT_FOUND;
    }

    struct track *selectedTrack = NULL;
    
    if (library->head != NULL && givenPlaylist->tracks != NULL) {
        selectedTrack = removeTrackFromListGivenPlaylist(library, trackTitle, givenPlaylist);
    }
    
    if (selectedTrack == NULL) {
        
        if (givenPlaylist->tracks == NULL) {
            // Do nothing.
        } else {
            return ERROR_NOT_FOUND;
        }
        // The function "removeTrack" returns NULL if the playlist is empty, 
        // if the library is empty or if the given title does not match
        // any of the tracks within the playlist. The above if statement
        // differentiates between if the playlist is empty or if the track 
        // was not found in the playlist.
        
    } else if (selectedTrack != NULL) {
        
        struct playlist *currPlaylist = library->head;
        
        while (
            strcmp(destPlaylist, currPlaylist->name) != 0 && 
            currPlaylist != NULL
        ) {
            currPlaylist = currPlaylist->next;
        }
        // Stops when currPlaylist has the same name as the destination playlist
        // or if currPlaylist == NULL, meaning the destination playlist could
        // not be found
        
        if (currPlaylist == NULL) {
            return ERROR_NOT_FOUND;
            // If the playlist is not found in the library, return error
        }
        
        if (currPlaylist->tracks == NULL) {
            // Adds selected track to the head of the playlist if the playlist 
            // is empty
            
            selectedTrack->next = NULL;
            selectedTrack->previous = NULL;
            currPlaylist->tracks = selectedTrack;
            
            return SUCCESS;
        }
        
        struct track *currTrack = currPlaylist->tracks;
        
        while (currTrack->next != NULL) {
            currTrack = currTrack->next;
        }
        // Stops when currTrack is pointing at the last node on the list before
        // NULL
        
        selectedTrack->next = NULL;
        currTrack->next = selectedTrack;
        selectedTrack->previous = currTrack;
        
        return SUCCESS;
    }

    return ERROR_NOT_FOUND;
}


// This function takes a string containing the name of a track and removes
// the track with the same name from the given playlist. It does not 
// free the malloc and instead returns a pointer to the malloc. This
// effectively removes the track from the linked list but keeps the node within
// the malloc. Difference between this and removeTrackFromList is 
// that it takes from given playlist rather than the selectedPlaylist
struct track *removeTrackFromListGivenPlaylist(
    Library library, char track[MAX_LEN], struct playlist *givenPlaylist
) {

    if (library->head != NULL && givenPlaylist->tracks != NULL) {
        
        // This if statment above ensures the library is not empty and that
        // the playlist is not empty
    
        struct track *curr = givenPlaylist->tracks;
        struct track *prevNode = NULL;
        struct track *nextNode = NULL;
        
        while (curr != NULL && strcmp(track, curr->title) != 0) {
            curr = curr->next;
        }
        
        // This while statement loops through the playlist to find a track 
        // with a matching title to the string given to the function 
        
        if (curr != NULL) {
            if (
                strcmp(track, curr->title) == 0 && curr->previous == NULL &&
                curr->next == NULL
            ) {
                givenPlaylist->tracks = curr->next;
                
                curr->next = NULL;
                curr->previous = NULL;

                return curr;
            
            } else if (
                strcmp(track, curr->title) == 0 && curr->previous == NULL
            ) {
                
                givenPlaylist->tracks = curr->next;
                curr->next->previous = NULL;
                
                curr->next = NULL;
                curr->previous = NULL;
                
                return curr;
                
            } else if (strcmp(track, curr->title) == 0 && curr->next == NULL) {
                
                prevNode = curr->previous;
                prevNode->next = NULL;
                
                curr->next = NULL;
                curr->previous = NULL;

                return curr;
                
            } else if (strcmp(track, curr->title) == 0 && curr != NULL) {
                
                prevNode = curr->previous;
                nextNode = curr->next;
                
                prevNode->next = nextNode;
                nextNode->previous = prevNode;
          
                curr->next = NULL;
                curr->previous = NULL;
                
                return curr;
            }
            
            // The first if statement accounts for the case where you are
            // at the start of the playlist. This can be checked by seeing 
            // if the "previous" pointer is pointing at NULL AND the "next" 
            // pointer is pointing at NULL, meaning there is only one track in 
            // the playlist.
            
            // It then makes the head pointer of the playlist point at 
            // the current tracks "next", which should be NULL, so the curr 
            // node is removed from the list.
            
            // The second if statment accounts for the case where you are
            // at the start of the playlist but there is more than one track 
            // in the selected playlist. 
            
            // It then makes the head of the list point at the next track on 
            // the list and makes the "previous" pointer of the next track 
            // point at NULL because it is the new head of the list.
            
            
            // The third if statement accounts for the case where you are at 
            // the end of the playlist. This can be checked by seeing if 
            // the "next" pointer of the selected track is pointing at NULL.
            
            // It then makes the previous node's "next" pointer point at NULL.
            
            // The fourth if statement accounts for the case where you are 
            // at the middle of the list (i.e. not at the start or the end). 
            
            // It then makes the previous node's "next" pointer point at the 
            // selected node's "next" pointer and the next node's "previous" 
            // pointer point at the selected node's "previous" pointer.

        }
    }
    
    return NULL; 
    
    // If the function reaches the end of the function, it means that either 
    // the entire library is empty (no playlists) or the selected playlist 
    // is empty (no tracks). It might also mean the track title was not found.
    
    // In these cases, the function will then return a pointer to NULL.
    
    // NOTE: if the library is empty, the returned 
    // struct track * variable pointer to NULL should be unsuable since the 
    // library has no playlists to put the pointer into. 
    // Hence, you should alway write the if statement:
    // if (library->head != NULL && givenPlaylist->tracks != NULL)
    // before you call the function and double check if your logic is right.
}


// The function below removes an array element, who's position is given by the 
// parameter "arrayIndexNum" and shifts all subsequent array elements down by 
// one index
void removingArrayElement(char string[MAX_LEN], int arrayIndexNum) {
    int l = arrayIndexNum;
    
    while (string[l] != '\0') {
        string[l] = string[l + 1];
        l++;
    }        
}








