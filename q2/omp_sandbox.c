#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define max(a,b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a > _b ? _a : _b; })

/* Size of the DFA */
#define MAXSTATES 5
/* Number of characters in the alphabet */
#define ALPHABETSIZE 4
/* Size of the string to match against.  You may need to adjust this. */
#define STRINGSIZE 100000000

/* State transition table (ie the DFA) */
int stateTable[MAXSTATES][ALPHABETSIZE];

/* Initialize the table */
void initTable() ;

int main(int argc, char* argv[]) {

    if (argc != 2) {
        printf("Invalid number of arguments. Expected: 2, received %d\n", argc);
        exit(1);
    }
    int threads = atoi(argv[1]) + 1;
    initTable();
    omp_set_num_threads(threads);

    char* string = "aabcaaaaaaabcdddddddddddddddddddddddddddddddddddddddaaaaaaaaaaaaaabbbcdd";
    char* reg = "^(a+b+(c|d)+)$";
    int length = (int) (strlen(string));
    int partitionSize = length / threads;
    int globalState = -1;
    printf("String to match: %s\n", string);
    int threadStates[threads][4];
#pragma omp master
    {
        /* INITIAL WORK BY MASTER HAPPENS HERE*/
        char subString_master[partitionSize + 1];
        strncpy(subString_master, string, (size_t) partitionSize);
        subString_master[partitionSize] = '\0';
        int currentState = subString_master[0] == 'a' ? 0 : 4;

        for (int i = 1; i < partitionSize; ++i) {
            char nextChar = subString_master[i];
            switch (nextChar) {
                case 'a': {
                    currentState = stateTable[currentState][0];
                    break;
                }
                case 'b': {
                    currentState = stateTable[currentState][1];
                    break;
                }
                case 'c': {
                    currentState = stateTable[currentState][2];
                    break;
                }
                case 'd': {
                    currentState = stateTable[currentState][3];
                    break;
                }
                default: {
                    currentState = 4;
                    break;
                }
            }
        }
        for (int i = 0; i < 4; ++i) {
            threadStates[0][i] = currentState;
        }
        globalState = currentState;
        printf("Master Substring: %s\n", subString_master);
        printf("Initial master state of string: %d\n", globalState);
    }

#pragma omp parallel for

    for (int i = 1; i < threads ; i++) {
        /* THE PARTITIONED WORK FROM THE WORKER THREADS HAPPENS HERE */
        char subString_worker[partitionSize + 1];
        int start = i * partitionSize;
        /* Make the last thread take any remainder extra characters that are left */
        int end = i != threads - 1 ? start + partitionSize : max(length, start + partitionSize);
        strncpy(subString_worker, &string[start], (size_t) end - start);
        subString_worker[end - start] = '\0';
        int outcome_states[4] = {-1, -1, -1, -1};
        printf("Thread %d substring: %s\n", omp_get_thread_num(), subString_worker);
        for (int j = 0; j < 4; ++j) {
            int currentState = j;
            for (int k = 0; k < strlen(subString_worker); k++) {
                char nextChar = subString_worker[k];
                switch (nextChar) {
                    case 'a': {
                        currentState = stateTable[currentState][0];
                        break;
                    }
                    case 'b': {
                        currentState = stateTable[currentState][1];
                        break;
                    }
                    case 'c': {
                        currentState = stateTable[currentState][2];
                        break;
                    }
                    case 'd': {
                        currentState = stateTable[currentState][3];
                        break;
                    }
                    default: {
                        currentState = 4;
                        break;
                    }
                }
            }
            outcome_states[j] = currentState;
            threadStates[i][j] = currentState;
        }
        printf("Thread %d states: { %d, %d, %d, %d }\n", omp_get_thread_num(), outcome_states[0], outcome_states[1], outcome_states[2], outcome_states[3]);
    }
    /* THE FINAL WORK BY THREAD 0 HAPPENS HERE */
#pragma omp master
    {
        for (int i = 0; i < threads; ++i) {
            if (globalState == 4) break;
            globalState = threadStates[i][globalState];
        }
        printf("Final global state is: %s\n", globalState == 3 ? "accept" : "reject");
    }
}

/* Initialize the table */
void initTable() {
    int start = 0;
    int accept = 3;
    int reject = 4;

    /* Note that characters values are assumed to be 0-based. */
    stateTable[0][0] = 1;
    stateTable[0][1] = reject;
    stateTable[0][2] = reject;
    stateTable[0][3] = reject;

    stateTable[1][0] = 1;
    stateTable[1][1] = 2;
    stateTable[1][2] = reject;
    stateTable[1][3] = reject;

    stateTable[2][0] = reject;
    stateTable[2][1] = 2;
    stateTable[2][2] = accept;
    stateTable[2][3] = accept;

    stateTable[3][0] = 1;
    stateTable[3][1] = reject;
    stateTable[3][2] = accept;
    stateTable[3][3] = accept;

    // reject state
    stateTable[4][0] = reject;
    stateTable[4][1] = reject;
    stateTable[4][2] = reject;
    stateTable[4][3] = reject;
}