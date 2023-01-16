////////////////////////////////////////////////////////////////////////
// COMP1521 21t2 -- Assignment 2 -- shuck, A Simple Shell
// <https://www.cse.unsw.edu.au/~cs1521/21T2/assignments/ass2/index.html>
//
// Written by Max Xue (z5267325) on 01/08/2021.
//
// 2021-07-12    v1.0    Team COMP1521 <cs1521@cse.unsw.edu.au>
// 2021-07-21    v1.1    Team COMP1521 <cs1521@cse.unsw.edu.au>
//     * Adjust qualifiers and attributes in provided code,
//       to make `dcc -Werror' happy.
//

#include <sys/types.h>

#include <sys/stat.h>
#include <sys/wait.h>

#include <assert.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <glob.h>

// [[ TODO: put any extra `#include's here ]]

#include <limits.h>
#include <spawn.h>
#include <ctype.h>

// [[ TODO: put any `#define's here ]]


//
// Interactive prompt:
//     The default prompt displayed in `interactive' mode --- when both
//     standard input and standard output are connected to a TTY device.
//
static const char *const INTERACTIVE_PROMPT = "shuck& ";

//
// Default path:
//     If no `$PATH' variable is set in Shuck's environment, we fall
//     back to these directories as the `$PATH'.
//
static const char *const DEFAULT_PATH = "/bin:/usr/bin";

//
// Default history shown:
//     The number of history items shown by default; overridden by the
//     first argument to the `history' builtin command.
//     Remove the `unused' marker once you have implemented history.
//
static const int DEFAULT_HISTORY_SHOWN __attribute__((unused)) = 10;

//
// Input line length:
//     The length of the longest line of input we can read.
//
static const size_t MAX_LINE_CHARS = 1024;

//
// Special characters:
//     Characters that `tokenize' will return as words by themselves.
//
static const char *const SPECIAL_CHARS = "!><|";

//
// Word separators:
//     Characters that `tokenize' will use to delimit words.
//
static const char *const WORD_SEPARATORS = " \t\r\n";

// [[ TODO: put any extra constants here ]]


// [[ TODO: put any type definitions (i.e., `typedef', `struct', etc.) here ]]


static void execute_command(char **words, char **path, char **environment);
static void do_exit(char **words);
static int is_executable(char *pathname);
static char **tokenize(char *s, char *separators, char *special_chars);
static void free_tokens(char **tokens);

// [[ TODO: put any extra function prototypes here ]]
void appendCommand(char **words);

void executingProgram(char *program, char **words, char **path, 
char **environment, const posix_spawn_file_actions_t *posix_spawnFileActions,
int isPipeProcess);

void historyCommand(char *program, char **words, char **path, 
char **environment);

void cdCommand(char *program, char **words);
void pwdCommand(char *program, char **words);

void redirection(char *program, char **words, char **path, 
char **environment, int numTokens, 
posix_spawn_file_actions_t *posix_spawnFileActions);

int main (void)
{
    // Ensure `stdout' is line-buffered for autotesting.
    setlinebuf(stdout);

    // Environment variables are pointed to by `environ', an array of
    // strings terminated by a NULL value -- something like:
    //     { "VAR1=value", "VAR2=value", NULL }
    extern char **environ;

    // Grab the `PATH' environment variable for our path.
    // If it isn't set, use the default path defined above.
    char *pathp;
    if ((pathp = getenv("PATH")) == NULL) {
        pathp = (char *) DEFAULT_PATH;
    }
    char **path = tokenize(pathp, ":", "");

    // Should this shell be interactive?
    bool interactive = isatty(STDIN_FILENO) && isatty(STDOUT_FILENO);

    // Main loop: print prompt, read line, execute command
    while (1) {
        // If `stdout' is a terminal (i.e., we're an interactive shell),
        // print a prompt before reading a line of input.
        if (interactive) {
            fputs(INTERACTIVE_PROMPT, stdout);
            fflush(stdout);
        }

        char line[MAX_LINE_CHARS];
        if (fgets(line, MAX_LINE_CHARS, stdin) == NULL)
            break;

        // Tokenise and execute the input line.
        char **command_words =
            tokenize(line, (char *) WORD_SEPARATORS, (char *) SPECIAL_CHARS);
        execute_command(command_words, path, environ);
        free_tokens(command_words);
    }

    free_tokens(path);
    return 0;
}


//
// Execute a command, and wait until it finishes.
//
//  * `words': a NULL-terminated array of words from the input command line
//  * `path': a NULL-terminated array of directories to search in;
//  * `environment': a NULL-terminated array of environment variables.
//
static void execute_command(char **words, char **path, char **environment)
{
    assert(words != NULL);
    assert(path != NULL);
    assert(environment != NULL);

    char *program = words[0];

    if (program == NULL) {
        // nothing to do
        return;
    }
    
    
    
    
    // Checking for the number of tokens so we can get to the last elements of 
    // the words array instantly. This does not include the NULL token.
    int numTokens = 0;
    for (int i = 0; words[i] != NULL; i++) {
        numTokens++;
    }
    
    // SUBSET 4: ERROR CHECKS //////////////////////////////////////////////////
    
    // If a redirection error occurs, it should not go on history so we return 
    // before appending commands
    
    // Checks if '<' isnt any word except for the first
    for (int i = 0; words[i] != NULL; i++) {
        if (strrchr(words[i], '<') != NULL && i != 0) {
            fprintf(stderr, "invalid input redirection\n");
            return;
        }
    }
    
    // This error check is to check that if the '<' redirection is used, there 
    // is more than just '<' and a input file.
    if (numTokens <= 2) {
        if (strrchr(words[0], '<') != NULL) {
            fprintf(stderr, "invalid input redirection\n");
            return;
        }
    }
    
    // This error check is to see that '>' isnt the first word.
    if (strrchr(words[0], '>') != NULL) {
        fprintf(stderr, "invalid input redirection\n");
        return; 
    }
    
    
    // Since output redirection appending requires three 
    // words (>, >, outputFile), there needs to be more than 3 words.
    if (numTokens > 3) {
        // This for loop checks if any token that isnt the second last or third 
        // last word is the character '>'.
        for (int i = 0; words[i] != NULL; i++) {
            if (strrchr(words[i], '>') != NULL && i != numTokens - 2 && 
            i != numTokens - 3) {
                fprintf(stderr, "invalid input redirection\n");
                return;
            }
        }
        
        // This if statement checks that if the third last word is '>', 
        // the second last word must also be '>'.
        if (strrchr(words[numTokens - 3], '>') != NULL && 
        strrchr(words[numTokens - 2], '>') == NULL) {
            fprintf(stderr, "invalid input redirection\n");
            return; 
        }
    }
    
    // Error case: < file > file or < > file
    if (strrchr(words[0], '<') != NULL && 
    strrchr(words[numTokens - 1], '>') != NULL && numTokens < 5) {
        fprintf(stderr, "invalid input redirection\n");
        return; 
    }
    
    // Error case: < file >> file
    if (strrchr(words[0], '<') != NULL && 
    strrchr(words[numTokens - 1], '>') != NULL && 
    strrchr(words[numTokens - 2], '>') != NULL && numTokens < 6) {
        fprintf(stderr, "invalid input redirection\n");
        return;
    }
    
    
    // SUBSET 4: END OF ERROR CHECKS ///////////////////////////////////////////
    
    
    
    
    // APPENDING COMMANDS TO .shuck_history ////////////////////////////////////
    
    // This if statement is required so that we do not add the history command 
    // to the .shuck_history file BEFORE we print the past commands. This will
    // mess with the algorithm used to print the history n command. The function
    // will be called in the if statement cases dealing with history as the 
    // program.
    // The second part of the if statement is because the !n command should not 
    // be put in the .shuck_history file 
    if (strcmp(program, "history") != 0 && program[0] != '!') {
        // Appending the latest command onto the .shuck_history file
        appendCommand(words);
    }
    
    // END OF APPENDING COMMANDS TO .shuck_history /////////////////////////////
    
    
    if (strcmp(program, "exit") == 0) {
        do_exit(words);
        // `do_exit' will only return if there was an error.
        return;
    }
    
    
    
    ///// SUBSET 5:PIPES /////
    
    // Looks for if a pipe command exists 
    int numPipes = 0;
    for (int i = 0; words[i] != NULL; i++) {
        if (strcmp(words[i], "|") == 0) {
            numPipes++;
            
        }
    }
    
    // Error cases occur if redirection commands exist anywhere that isn't 
    // either the beginning process for '<' or at the end process '>>' or '>'.
    // However, this is caught in the regular error checks for subset 4.
    
    
    int currWord = 0; // Helps us know which word we are up to in the words[] 
                      // array
    
    if (numPipes > 0) {
        
        
        // CREATING PIPES //////////////////////////////////////////////////////
        // Creating two pairs of pipes (4 altogether). This allows us to send 
        // inputs to a child process and recieve outputs from the same process. 
        int pipeFileDescriptors1[2];
        pipe(pipeFileDescriptors1);
        
        int pipeFileDescriptors2[2];
        pipe(pipeFileDescriptors2);
        ////////////////////////////////////////////////////////////////////////
        
        
        
        posix_spawn_file_actions_t actions;
        
        // CREATING ACTIONS FOR FIRST COMMAND PROCESS //////////////////////////
        
        posix_spawn_file_actions_init(&actions);
        
        // We want to capture the output of the first process given in the 
        // command.
        // Hence, we close the read end of the process giving an output 
        posix_spawn_file_actions_addclose(&actions, pipeFileDescriptors1[0]);
        
        // Replacing stdout of the output process with the write end 
        // of the pipe
        posix_spawn_file_actions_adddup2(&actions, pipeFileDescriptors1[1], 1); 
        
        
        
        // CREATING A SUBSET OF THE WORDS[] ARRAY FOR THE COMMAND BETWEEN | ////
        
        // Counts how many words between each | command. Will be used to 
        // calculate how many array indexes we need to malloc.  
        int pipeSectionCounter = 0;
        while (strcmp(words[pipeSectionCounter], "|") != 0) {
            
            pipeSectionCounter++;
            currWord++;
            
        }
        pipeSectionCounter++; // Need to add one more for hold NULL at the end.
        // currWord holds the number of words we have passes, including index 0.
        
        // Creating a temp commands string array to pass to posix_spawn.
        char **currCommandSection = 
        malloc(pipeSectionCounter * sizeof(*currCommandSection));
        
        // Copying the first command in words to the temp command array.
        for (int i = 0; i < currWord; i++) {
            currCommandSection[i] = words[i];
        }
        // Ending the temp commands array of strings with NULL.
        currCommandSection[pipeSectionCounter - 1] = NULL;
        
        program = currCommandSection[0];
        
        
        
        // SPAWNING THE PROCESS ////////////////////////////////////////////////
        
        // This if statement will send the actions and words[] subarray to the 
        // redirection function if a redirection command has been given.
        if (strrchr(words[0], '<') != NULL) {
            redirection(program, currCommandSection, path, environment, 
            pipeSectionCounter - 1, &actions);
        } else {
            executingProgram(program, currCommandSection, path, environment, 
            &actions, 1);
        }
        
        // Clearing the actions list so we can use it for the next child process
        posix_spawn_file_actions_destroy(&actions);
        
        free(currCommandSection);
        
        ////////////////////////////////////////////////////////////////////////
        
        
        // At this point, the output of the first process is captured in 
        // pipe 1's buffer after we spawned the process. currWord is the array 
        // index of the first | command in words.
        
        
        
        // currPipes refers to how many pipe commands we have not processed yet.
        int currPipes = numPipes;
        
        // This while loop is for if more than one process has been piped.
        // I.e., more than one | command.
        while (currPipes > 0) {
            
            // Need to reinitialise the pipe because once a pipe has been 
            // closed, it cannot be reused.
            pipe(pipeFileDescriptors2);
            
            
        // PROCESS A ///////////////////////////////////////////////////////////
            
            posix_spawn_file_actions_init(&actions);
            
            // Process A wants to read pipe 1 so we close write end of pipe 1
            posix_spawn_file_actions_addclose(&actions, 
            pipeFileDescriptors1[1]);
            
            // Replace stdin of process A with read end of pipe 1
            posix_spawn_file_actions_adddup2(&actions, 
            pipeFileDescriptors1[0], 0);
            
            // This if statement is to redirect the output of process A to 
            // pipe 2 if it is not the last command (i.e., no more | after it).
            // This will add an action to its action list to let the stdout 
            // redirect to the write end of pipe 2.
            if (currPipes > 1) { 
                // Process A wants to write to pipe 2 so close read end of 
                // pipe 2
                posix_spawn_file_actions_addclose(&actions, 
                pipeFileDescriptors2[0]);
                
                // Replace stdout of process A with write end of pipe 2
                posix_spawn_file_actions_adddup2(&actions, 
                pipeFileDescriptors2[1], 1);
            }
            
            
            // CREATING A SUBSET OF THE WORDS[] ARRAY FOR CURRENT COMMAND //////
            
            // CurrWord previously held the array index for the previous | 
            // command. We are now shifting it up one word to go to the first 
            // word of the next command.
            currWord++; 
            
            // Index of the first word of the current command 
            int startingWord = currWord; 
            
            // Counts how many words between each | command 
            pipeSectionCounter = 0;
            while (words[currWord] != NULL && 
            strcmp(words[currWord], "|") != 0) { 
                
                pipeSectionCounter++;
                currWord++;
                
            }
            pipeSectionCounter++; 
            // Need to add one more for hold NULL at the end. currWord holds 
            // the number of words we have passed, counting from index 0.
            
            // Creating a temp commands string array to pass to posix_spawn.
            currCommandSection = 
            malloc(pipeSectionCounter * sizeof(*currCommandSection));
            
            int currTempCommandArrayWord = 0;
            // Copying the first command in words to the temp command array.
            for (int i = startingWord; i < currWord; i++) {
                currCommandSection[currTempCommandArrayWord] = words[i];
                currTempCommandArrayWord++;
            }
            // Ending the temp commands array of strings with NULL.
            currCommandSection[pipeSectionCounter - 1] = NULL;
            
            
            // SPAWNING PROCESS A //////////////////////////////////////////////
            
            // Need to close the write end of pipe 1 so the read end of pipe 1
            // recieves EOF.
            close(pipeFileDescriptors1[1]);
            
            program = currCommandSection[0];
            if (currPipes > 1) {
            
                executingProgram(program, currCommandSection, path, environment, 
                &actions, 1);
                
            } else {
                // This else case is for when it is the final process in the 
                // entire command.
                
                // This if statement is to check for any redirection commands
                if (strrchr(currCommandSection[pipeSectionCounter - 3], '>') != 
                NULL) {
                    redirection(program, currCommandSection, path, environment, 
                    pipeSectionCounter - 1, &actions);
                } else {
                    
                    executingProgram(program, currCommandSection, path, 
                    environment, &actions, 0);
                }
            }
            
            
            // Clearing the actions list so we can use it for the next child process
            posix_spawn_file_actions_destroy(&actions);
            free(currCommandSection);
            
            // At this point, the output of process 2 is captured in 
            // pipe 2's buffer after we spawned the process.
            
            currPipes--;
            
            // If we no longer have any pipes left, it means we no longer 
            // have any commands left to execute so we return to the main.
            if (currPipes == 0) {
                return;
            }
            
            
        // END OF PROCESS A ////////////////////////////////////////////////////
            
            // Need to reinitialise the pipe because once a pipe has been 
            // closed, it cannot be reused.
            pipe(pipeFileDescriptors1); 
            
            
            
            
        // PROCESS B ///////////////////////////////////////////////////////////
            
            posix_spawn_file_actions_init(&actions);
            
            // Process B wants to read pipe 2 so we close write end of pipe 2
            posix_spawn_file_actions_addclose(&actions, pipeFileDescriptors2[1]);
            
            // Replace stdin of process B with read end of pipe 2
            posix_spawn_file_actions_adddup2(&actions, 
            pipeFileDescriptors2[0], 0);
            
            if (currPipes > 1) {
                // Process B wants to write to pipe 1 so close read end of 
                // pipe 1
                posix_spawn_file_actions_addclose(&actions, 
                pipeFileDescriptors1[0]);
                
                // Replace stdout of process B with write end of pipe 1
                posix_spawn_file_actions_adddup2(&actions, 
                pipeFileDescriptors1[1], 1);
            }
            
            
            // CREATING A SUBSET OF THE WORDS[] ARRAY FOR CURRENT COMMAND //////
            
            // CurrWord previously held the array index for the previous | 
            // command. We are now shifting it up one word to go to the first 
            // word of the next command.
            currWord++; 
            
            // Index of the first word of the current command 
            startingWord = currWord; 
            
            // Counts how many words between each | command 
            pipeSectionCounter = 0;
            while (words[currWord] != NULL && 
            strcmp(words[currWord], "|") != 0) { 
                pipeSectionCounter++;
                currWord++;
            }
            pipeSectionCounter++; 
            // Need to add one more for hold NULL at the end. currWord holds 
            // the number of words we have passes, including index 0.
            
            // Creating a temp commands string array to pass to posix_spawn.
            currCommandSection = 
            malloc(pipeSectionCounter * sizeof(*currCommandSection));
            
            currTempCommandArrayWord = 0;
            // Copying the first command in words to the temp command array.
            for (int i = startingWord; i < currWord; i++) {
                currCommandSection[currTempCommandArrayWord] = words[i];
                currTempCommandArrayWord++;
            }
            // Ending the temp commands array of strings with NULL.
            currCommandSection[pipeSectionCounter - 1] = NULL;
            
            
            // SPAWNING PROCESS B //////////////////////////////////////////////
            
            // Need to close the write end of pipe 2 so the read end of pipe 2
            // recieves EOF.
            close(pipeFileDescriptors2[1]);
            
            program = currCommandSection[0];
            if (currPipes > 1) {
                
                executingProgram(program, currCommandSection, path, environment, 
                &actions, 1);
                
            } else {
                // This else case is for when it is the final process in the 
                // entire command.
                
                if (strrchr(currCommandSection[pipeSectionCounter - 3], '>') != 
                NULL) {
                    
                    redirection(program, currCommandSection, path, environment, 
                    pipeSectionCounter - 1, &actions);
                    
                } else {
                    executingProgram(program, currCommandSection, path, 
                    environment, &actions, 0);
                }
            }
            
            // reset actions for next process 
            posix_spawn_file_actions_destroy(&actions);
            free(currCommandSection);
            
            // At this point, the output of process 3 is captured in 
            // pipe 1's buffer after we spawned the process. 
            
            currPipes--;
            
            // If we no longer have any pipes left, it means we no longer 
            // have any commands left to execute so we return to the main.
            if (currPipes == 0) {
                return;
            }
            
        // END OF PROCESS B ////////////////////////////////////////////////////
        
        }
    }
    
    
    ///// SUBSET 4: REDIRECTION /////
    if (numTokens > 2 && (strrchr(words[0], '<') != NULL || 
    strrchr(words[numTokens - 2], '>') != NULL)) { 
        redirection(program, words, path, environment, numTokens, NULL);
        return;
    }
    
    ///// SUBSET 0: CD AND PWD /////
    if (strcmp(program, "cd") == 0) {
        cdCommand(program, words);
        return;
    }
    
    if (strcmp(program, "pwd") == 0) {
        pwdCommand(program, words);
        return;
    }
    
    ///// SUBSET 2: HISTORY /////
    if (strcmp(program, "history") == 0 || program[0] == '!') {
        historyCommand(program, words, path, environment);
        return;
    }
    
    ///// SUBSET 1: EXECUTING PROGRAMS /////
    executingProgram(program, words, path, environment, NULL, 0);
    return;
}



// This function finds an executable file corresponding to the command and 
// executes it. It takes three arguments. The command, an array of strings
// containing the arguments, a path, which is a list of directories to search 
// for the corresponding executable file, environmental variables, a list of
// actions to pass into spawned process, and a int dictating if the current 
// command is an intermediate pipe process.
void executingProgram(char *program, char **words, char **path, 
char **environment, const posix_spawn_file_actions_t *posix_spawnFileActions,
int isPipeProcess) {
    
    // SUBSET 1 ////////////////////////////////////////////////////////////////
    
    // Executing a program given the relative path 
    if (strrchr(program, '/') == NULL) {

        char pathOfProgram[PATH_MAX];
        
        // Loops through all paths' list of directories to search for the 
        // corresponding executable file to see if it exists
        int found = 0;
        for (int i = 0; path[i] != NULL; i++) {
            int numChar = strlen(path[i]) + strlen(words[0]) + 2;
            snprintf(pathOfProgram, numChar, "%s/%s", path[i], program);
            if (is_executable(pathOfProgram)) {
                found = 1;
                break;
            }
        }
        
        pid_t pid;
        int exitInt;
        if (found == 1) {
            // SUBSET 3: GLOBBING //////////////////////////////////////////////
            glob_t matches; // holds pattern expansion
            int globResult = glob(words[0], GLOB_NOCHECK|GLOB_TILDE
            , NULL, &matches);
            
            if (globResult == 0) {
                for (int i = 1; words[i] != NULL; i++) {
                
                    globResult = glob(words[i], 
                    GLOB_NOCHECK|GLOB_TILDE|GLOB_APPEND, NULL, &matches);
                    
                }
            }
            // END OF GLOBBING PROCESSING //////////////////////////////////////
            
            // If the spawned process is a pipe process, the spawned process 
            // should not print its exit status. 1 means it is a pipe process.
            // 0 means it is not a pipe process.
            
            posix_spawn(&pid, pathOfProgram, posix_spawnFileActions, NULL, 
            matches.gl_pathv, environment); 
            waitpid(pid, &exitInt, 0);
            if (isPipeProcess != 1) {
                printf("%s ", pathOfProgram);
                
                printf("exit status = %d\n", WEXITSTATUS(exitInt)); 
            }
            
            globfree(&matches);
            // Freeing the struct created from earlier glob()
            
            return;
            
        } else if (found == 0) {
            fprintf(stderr, "%s: command not found\n", program); 
            return;
        }
    }
    
    // Executing a program if given the absolute path 
    if (is_executable(program)) {
        
        pid_t pid;
        int exitInt;
        
        // SUBSET 3: GLOBBING ////////////////////////////////////////////////// 
        glob_t matches; // holds pattern expansion
        int globResult = glob(words[0], GLOB_NOCHECK|GLOB_TILDE
        , NULL, &matches);
        
        if (globResult == 0) {
            for (int i = 1; words[i] != NULL; i++) {
            
                globResult = glob(words[i], 
                GLOB_NOCHECK|GLOB_TILDE|GLOB_APPEND, NULL, &matches);
                
            }
        }
        // END OF GLOBBING PROCESSING //////////////////////////////////////////
        
        posix_spawn(&pid, program, posix_spawnFileActions, NULL, 
        matches.gl_pathv, environment); 
        waitpid(pid, &exitInt, 0); 
        
        // If the spawned process is a pipe process, the spawned process 
        // should not print its exit status. 1 means it is a pipe process.
        // 0 means it is not a pipe process.
        if (isPipeProcess != 1) {
            printf("%s ", program);
            printf("exit status = %d\n", WEXITSTATUS(exitInt)); 
        }
        
        globfree(&matches); // Freeing the struct created from earlier glob()
        
        return;
            
    } else {
        fprintf(stderr, "%s: command not found\n", program); 
        return;
    }
}



// This function implements the capability to take in input and output 
// redirections with the commands '<' and '>'.
void redirection(char *program, char **words, char **path, 
char **environment, int numTokens, 
posix_spawn_file_actions_t *posix_spawnFileActions) {
    
    // SUBSET 4: PART 1: < AND > OR >> REDIRECTION /////////////////////////////
    
    // This if statement handles if the redirection is both "< file.txt" and
    // "> file.txt". This causes the input for the given command to be taken 
    // from the file after '<' and put the output in the file after '>' or ">>".
    if (strrchr(words[0], '<') != NULL && 
    strrchr(words[numTokens - 2], '>') != NULL) {
        
        // Error check to see if the program was a builtin command.
        // The index value is 2 because the index value of 1 should be the file.
        if (strcmp(words[2], "history") == 0 || strcmp(words[2], "cd") == 0 ||
        strcmp(words[2], "pwd") == 0 || strcmp(words[2], "!") == 0) {
            fprintf(stderr,
            "%s: I/O redirection not permitted for builtin commands\n", 
            words[2]);
            return;
        }
        
        
        // Setting up the input redirection 
        FILE *inputFile = fopen(words[1], "r");
        
        // Error check to see if the file exists.
        if (inputFile == NULL) {
            fprintf(stderr, "%s: No such file or directory\n", words[1]);
            return;
        }
        
        
        // Redirecting input coming from inputFile rather than stdin
        int inputFileDescriptor = fileno(inputFile);
        
        posix_spawn_file_actions_t actions;
        posix_spawn_file_actions_init(&actions);
        
        posix_spawn_file_actions_adddup2(&actions, inputFileDescriptor, 0);
        
        
        // Setting up the output redirections
        FILE *outputFile;
        
        // This if statement differentiates if the redirection is appending (>>)
        // or truncating (>) the output capture file.
        if (strrchr(words[numTokens - 3], '>') != NULL) {
            outputFile = fopen(words[numTokens - 1], "a+");
            
            // When we free the words array, it stops freeding when it meets 
            // NULL. Hence, we need to free earlier if we are going to change
            // where NULL is.
            free(words[numTokens]);
            free(words[numTokens - 1]);
            free(words[numTokens - 2]);
            words[numTokens - 3] = NULL;
            
        } else {
            outputFile = fopen(words[numTokens - 1], "w+");
            
            // Same reasoning as above.
            free(words[numTokens]);
            free(words[numTokens - 1]);
            words[numTokens - 2] = NULL;
        }
        
        // Redirecting stdout into specified outputFile 
        int outputFileDescriptor = fileno(outputFile);
        posix_spawn_file_actions_adddup2(&actions, outputFileDescriptor, 1);
        
        // Need to free the first two strings {'<', "file name"} since we are 
        // going to remove them from the array of strings (words). 
        free(words[0]);
        free(words[1]);
        int i = 0;
        while (words[i + 2] != NULL) {
            words[i] = words[i + 2];
            i++;
        }
        words[i] = words[i + 2];
        
        
        program = words[0];
        executingProgram(program, words, path, environment, &actions, 0);
        
        fclose(outputFile);
        fclose(inputFile);
        posix_spawn_file_actions_destroy(&actions);
        return;
    }
    
    // END OF SUBSET 4: PART 1: < AND > OR >> REDIRECTION //////////////////////
    
    
    
    // SUBSET 4: PART 2: < REDIRECTION /////////////////////////////////////////
    
    // This if statement handles if the redirection is "< file.txt", which 
    // changes where the input to the given program is taken.
    if (strrchr(words[0], '<') != NULL) {
        
        // Error check to see if the program was a builtin command.
        // The index value is 2 because the index value of 1 should be the file.
        if (strcmp(words[2], "history") == 0 || strcmp(words[2], "cd") == 0 ||
        strcmp(words[2], "pwd") == 0 || strcmp(words[2], "!") == 0) {
            fprintf(stderr,
            "%s: I/O redirection not permitted for builtin commands\n", 
            words[2]);
            return;
        }
        
        FILE *inputFile = fopen(words[1], "r");
        
        // Error check to see if the file exists.
        if (inputFile == NULL) {
            fprintf(stderr, "%s: No such file or directory\n", words[1]);
            return;
        }
        
        // Redirecting input coming from inputFile rather than stdin
        int inputFileDescriptor = fileno(inputFile);
        
        posix_spawn_file_actions_t actions;
        posix_spawn_file_actions_init(&actions);
        
        // If posix_spawnFileActions == NULL, we use the newly created actions
        // as our input to the posix_spawn. If posix_spawnFileActions != NULL,
        // then it already exists and we just want to add onto the already
        // existing current actions.
        if (posix_spawnFileActions == NULL) {
            posix_spawn_file_actions_adddup2(&actions, inputFileDescriptor, 0);
        } else {
            posix_spawn_file_actions_adddup2(*&posix_spawnFileActions, 
            inputFileDescriptor, 0);
        }
        
        
        // We only need to free the words[] array elements if we aren't 
        // processing a pipe temp command array. 
        if (posix_spawnFileActions == NULL) {
            // Need to free the first two strings {'<', "file name"} since we are 
            // going to remove them from the array of strings (words). 
            free(words[0]);
            free(words[1]);
        }
        
        // This shifts the words down so the first two words { '<', file } 
        // are no longer in the words[] array
        int i = 0;
        while (words[i + 2] != NULL) {
            words[i] = words[i + 2];
            i++;
        }
        words[i] = words[i + 2];
        
        program = words[0];
        // Changes which actions variable to give to executeProgram depending 
        // on if a actions variable aready existed (from pipes)
        if (posix_spawnFileActions == NULL) {
            executingProgram(program, words, path, environment, &actions, 0);
        } else {
            executingProgram(program, words, path, environment, 
            *&posix_spawnFileActions, 1); 
        }
        
        fclose(inputFile);
        posix_spawn_file_actions_destroy(&actions);
        return;
    }
    
    // END OF SUBSET 4: PART 2: < REDIRECTION //////////////////////////////////
    
    
    
    
    
    
    // SUBSET 4: PART 3: > OR >> REDIRECTION ///////////////////////////////////
    
    // This if check is so that the if check below this one wont 
    // malloc buffer overflow
    if (numTokens > 2) {
        
        // This if statement handles if the redirection is "> file.txt"
        // Dont need to check for if the third last word is '>' because 
        // that will be handled in the if statement check later.
        if (strrchr(words[numTokens - 2], '>') != NULL) {
            
            // Error check to see if the program was a builtin command
            if (strcmp(program, "history") == 0 || strcmp(program, "cd") == 0 ||
            strcmp(program, "pwd") == 0 || strcmp(program, "!") == 0) {
                fprintf(stderr,
                "%s: I/O redirection not permitted for builtin commands\n", 
                program);
                return;
            }
            
            FILE *outputFile = NULL;
            
            posix_spawn_file_actions_t actions;
            posix_spawn_file_actions_init(&actions);
            
            
            // If posix_spawnFileActions == NULL, we use the newly created 
            // actions as our input to the posix_spawn. If 
            // posix_spawnFileActions != NULL, then it already exists and we 
            // just want to add onto the already existing current actions.
            if (posix_spawnFileActions == NULL) {
                
                // This if statement differentiates if the redirection is 
                // appending (>>) or truncating (>) the output capture file.
                if (strrchr(words[numTokens - 3], '>') != NULL) {
                    outputFile = fopen(words[numTokens - 1], "a+");
                    
                    // When we free the words array, it stops freeding when it
                    // meets NULL. 
                    // Hence, we need to free earlier if we are going to 
                    // change where NULL is.
                    free(words[numTokens]);
                    free(words[numTokens - 1]);
                    free(words[numTokens - 2]);
                    words[numTokens - 3] = NULL;
                    
                } else {
                    outputFile = fopen(words[numTokens - 1], "w+");
                    
                    // Same reasoning as above.
                    free(words[numTokens]);
                    free(words[numTokens - 1]);
                    words[numTokens - 2] = NULL;
                }
                
                // Redirecting stdout into specified outputFile 
                int outputFileDescriptor = fileno(outputFile);
                
                posix_spawn_file_actions_adddup2(&actions, 
                outputFileDescriptor, 1);
                
                executingProgram(program, words, path, environment, 
                &actions, 0);
                
            } else {
                
                
                if (strrchr(words[numTokens - 3], '>') != NULL) {
                    outputFile = fopen(words[numTokens - 1], "a+");
                    words[numTokens - 3] = NULL;
                    
                } else {
                    outputFile = fopen(words[numTokens - 1], "w+");
                    words[numTokens - 2] = NULL;
                    
                } 
                
                
                // Redirecting stdout into specified outputFile 
                int outputFileDescriptor = fileno(outputFile);
                
                posix_spawn_file_actions_adddup2(posix_spawnFileActions, 
                outputFileDescriptor, 1);

                executingProgram(program, words, path, environment, 
                posix_spawnFileActions, 0);
                
            }
            
            fclose(outputFile);
            posix_spawn_file_actions_destroy(&actions);
            return;
        }
    }
    
    // END OF SUBSET 4: PART 3: > OR >> REDIRECTION ////////////////////////////
}


// This function implements a built-in command history n which prints the last n 
// commands, or, if n is not specified, 10; and implements a built-in 
// command ! n which prints the nth command and then executes it, or, if n is 
// not specified, the last command.
void historyCommand(char *program, char **words, char **path, 
char **environment) {
    // SUBSET 2 ////////////////////////////////////////////////////////////////
    if (strcmp(program, "history") == 0) {
        
        int num; // Number of past commands to print 
        
        // Case if n is not specified, set it to be 10
        if (words[1] == NULL) {
            num = 10;
        } else {
            if (words[2] != NULL) {
                fprintf(stderr, "history: too many arguments\n");
                appendCommand(words); // Appending command to .shuck_history
                return;
            }
            
            char *end;
            strtol(words[1], &end, 10);
            if (end == words[1] || *end != '\0') {
                fprintf(stderr, 
                "history: %s: numeric argument required\n", words[1]);
                appendCommand(words); // Appending command to .shuck_history
                return;
            }
            
            // Negatives are not allowed.
            if (words[1][0] == '-') {
                fprintf(stderr, 
                "history: %s: numeric argument required\n", words[1]);
                appendCommand(words); // Appending command to .shuck_history
                return;
            }
            // 0 is a valid case and will just print nothing
            num = atoi(words[1]);
        }
        
        // Getting the path to $HOME/.shuck_history
        char pathToHistory[PATH_MAX]; 
        char *home = getenv("HOME");
        strcpy(pathToHistory, home);
        strcat(pathToHistory, "/.shuck_history");
        FILE *shuck_history = fopen(pathToHistory, "r+"); 
        
        char line[MAX_LINE_CHARS]; 
        int numCommands = 0;
        while (fgets(line, MAX_LINE_CHARS, shuck_history) != NULL) {
            numCommands++;
        }
        rewind(shuck_history);
        
        int currLine = 0;
        int printingLineStart = numCommands - num;
        
        while (fgets(line, MAX_LINE_CHARS, shuck_history) != NULL) {
            if (currLine >= printingLineStart) {
                printf("%d: %s",currLine, line);
            }
            currLine++;
        }

        fclose(shuck_history);
        appendCommand(words);
        return;
    }
    
    if (program[0] == '!') {
        
        // The number key of the command in history we are going to execute 
        int num;
        
        if (words[1] == NULL) {
            
            num = -1; 
            // If words[1] == NULL, ! should print the most recent command 
            // in .shuck_history. We can temporarily keep num as -1 to change it
            // to the last command later since it cannot be negative

        } else {
            
            if (words[2] != NULL) {
                fprintf(stderr, "!: too many arguments\n");
                return;
            }
            
            char *end;
            strtol(words[1], &end, 10);
            if (end == words[1] || *end != '\0') {
                fprintf(stderr, "!: %s: numeric argument required\n", words[1]);
                return;
            }
            
            // Negatives are not allowed.
            if (words[1][0] == '-') {
                fprintf(stderr, "!: %s: numeric argument required\n", words[1]);
                return;
            }
            
            num = atoi(words[1]);
        }
        
        // Getting the path to $HOME/.shuck_history
        char pathToHistory[PATH_MAX]; 
        char *home = getenv("HOME");
        strcpy(pathToHistory, home);
        strcat(pathToHistory, "/.shuck_history");
        FILE *shuck_history = fopen(pathToHistory, "r+"); 
        
        
        char line[MAX_LINE_CHARS]; 
        int numCommands = 0;
        while (fgets(line, MAX_LINE_CHARS, shuck_history) != NULL) {
            numCommands++;
        }
        rewind(shuck_history);
        
        // Checking if the number given after the ! is a valid history key 
        // number. The -1 after numCommands is because numCommands counts an 
        // extra line.
        if (num > numCommands - 1) {
            fprintf(stderr, "!: invalid history reference\n");
            return;
        }
        
        // We set up the input analysis to set num to -1 if the only input was 
        // '!'. In this case, we should print the last command. We do this by 
        // setting num (which represents the command inputted) to be the largest
        // one possible (which is the last command in .shuck_history
        if (num == -1) {
            num = numCommands - 1;
        }
        
        int i = 0;
        while (fgets(line, MAX_LINE_CHARS, shuck_history) != NULL && i < num) {
            i++;
        }
        rewind(shuck_history);
        
        int lengthOfLine = strlen(line);
        line[lengthOfLine - 1] = '\0'; 
        
        char **commandInput = tokenize(line, " ", (char *) SPECIAL_CHARS);
        
        printf("%s\n", line);
        execute_command(commandInput, path, environment);
        
        free_tokens(commandInput);
        
        return;
    }
}



// This function appends the given string to the .shuck_history file found in 
// the directory listed in the HOME env variable.
void appendCommand(char **words) { 
    
    
    // Getting the path to $HOME/.shuck_history
    char pathToHistory[PATH_MAX];
    char *home = getenv("HOME");
    
    
    strcpy(pathToHistory, home);
    
    strcat(pathToHistory, "/.shuck_history");
    
   
    FILE *shuck_history = fopen(pathToHistory, "a+"); 
    
    
    char command[MAX_LINE_CHARS];
    
    strcpy(command, words[0]);
    
    if (words[1] != NULL) {
        strcat(command, " ");
    }
    
    
    for (int i = 1; words[i] != NULL; i++) {
        strcat(command, words[i]);
        
        if (words[i + 1] != NULL) {
            strcat(command, " ");
        }
        
    }
    
    strcat(command, "\n");
    fputs(command, shuck_history);
    
    fclose(shuck_history);
}



// SUBSET 0 ////////////////////////////////////////////////////////////////
// This function changes the current directory to a given path. If no path 
// is given, it changes the directory to the path in the HOME env variable.
void cdCommand(char *program, char **words) {

    if (words[1] != NULL) {
        
        if (words[2] != NULL) {
            fprintf(stderr, "cd: too many arguments\n");
            return;
        }
        
        if (chdir(words[1]) != 0) {
            fprintf(stderr, "cd: %s: No such file or directory\n", words[1]);
            return;
        }
        return;
        
    } else if (words[1] == NULL) {
        char *home = getenv("HOME");
        chdir(home);
        return;
    }
}

// This function prints the current directory when called.
void pwdCommand(char *program, char **words) {
    if (words[1] != NULL) {
        fprintf(stderr, "pwd: too many arguments\n");
        return;
    }
    
    char pathname[PATH_MAX];
    getcwd(pathname, sizeof pathname);
    printf("current directory is '%s'\n", pathname);
    return;
}


// Given Functions /////////////////////////////////////////////////////////////

//
// Implement the `exit' shell built-in, which exits the shell.
//
// Synopsis: exit [exit-status]
// Examples:
//     % exit
//     % exit 1
//
static void do_exit(char **words)
{
    assert(words != NULL);
    assert(strcmp(words[0], "exit") == 0);

    int exit_status = 0;

    if (words[1] != NULL && words[2] != NULL) {
        // { "exit", "word", "word", ... }
        fprintf(stderr, "exit: too many arguments\n");

    } else if (words[1] != NULL) {
        // { "exit", something, NULL }
        char *endptr;
        exit_status = (int) strtol(words[1], &endptr, 10);
        if (*endptr != '\0') {
            fprintf(stderr, "exit: %s: numeric argument required\n", words[1]);
        }
    }

    exit(exit_status);
}


//
// Check whether this process can execute a file.  This function will be
// useful while searching through the list of directories in the path to
// find an executable file.
//
static int is_executable(char *pathname)
{
    struct stat s;
    return
        // does the file exist?
        stat(pathname, &s) == 0 &&
        // is the file a regular file?
        S_ISREG(s.st_mode) &&
        // can we execute it?
        faccessat(AT_FDCWD, pathname, X_OK, AT_EACCESS) == 0;
}


//
// Split a string 's' into pieces by any one of a set of separators.
//
// Returns an array of strings, with the last element being `NULL'.
// The array itself, and the strings, are allocated with `malloc(3)';
// the provided `free_token' function can deallocate this.
//
static char **tokenize(char *s, char *separators, char *special_chars)
{
    size_t n_tokens = 0;

    // Allocate space for tokens.  We don't know how many tokens there
    // are yet --- pessimistically assume that every single character
    // will turn into a token.  (We fix this later.)
    char **tokens = calloc((strlen(s) + 1), sizeof *tokens);
    assert(tokens != NULL);

    while (*s != '\0') {
        // We are pointing at zero or more of any of the separators.
        // Skip all leading instances of the separators.
        s += strspn(s, separators);

        // Trailing separators after the last token mean that, at this
        // point, we are looking at the end of the string, so:
        if (*s == '\0') {
            break;
        }

        // Now, `s' points at one or more characters we want to keep.
        // The number of non-separator characters is the token length.
        size_t length = strcspn(s, separators);
        size_t length_without_specials = strcspn(s, special_chars);
        if (length_without_specials == 0) {
            length_without_specials = 1;
        }
        if (length_without_specials < length) {
            length = length_without_specials;
        }

        // Allocate a copy of the token.
        char *token = strndup(s, length);
        assert(token != NULL);
        s += length;

        // Add this token.
        tokens[n_tokens] = token;
        n_tokens++;
    }

    // Add the final `NULL'.
    tokens[n_tokens] = NULL;

    // Finally, shrink our array back down to the correct size.
    tokens = realloc(tokens, (n_tokens + 1) * sizeof *tokens);
    assert(tokens != NULL);

    return tokens;
}


//
// Free an array of strings as returned by `tokenize'.
//
static void free_tokens(char **tokens)
{
    for (int i = 0; tokens[i] != NULL; i++) {
        free(tokens[i]);
    }
    free(tokens);
}
