#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>

#define max(a, b) \
   ({ __typeof__ (a) _a = (a); \
       __typeof__ (b) _b = (b); \
     _a > _b ? _a : _b; })

/* Size of the DFA */
#define MAXSTATES 5
/* Number of characters in the alphabet */
#define ALPHABETSIZE 4
/* Size of the string to match against.  You may need to adjust this. */
#define STRINGSIZE 999999999

/* State transition table (ie the DFA) */
int stateTable[MAXSTATES][ALPHABETSIZE];
int globalState = -1;

/* Initialize the table */
void initTable();

/* Construct a sample string to match against.  Note that this uses characters, encoded in ASCII,
   so to get 0-based characters you'd need to subtract 'a'. */
char *buildString();

void worker(int threads, char **partitionStrings, int **threadStates, long partitionSize);

void master(char *string, int **threadStates);

char **partitionString(char *string, int numPartitions);

int main(int argc, char *argv[]) {

    if (argc != 2) {
        printf("Invalid number of arguments. Expected: 2, received %d\n", argc);
        exit(1);
    }
    int threads = atoi(argv[1]) + 1;
    initTable();
    char *string = buildString();

    unsigned long length = (long) (strlen(string));
    unsigned long partitionSize = length / threads;
    int **threadStates = (int **) malloc((threads) * sizeof(int *));
    for (int i = 0; i < (threads); ++i) {
        threadStates[i] = (int *) malloc(sizeof(int));
        for (int j = 0; j < 4; ++j) {
            threadStates[i][j] = 0;
        }
    }


    char **partitionStrings = partitionString(string, threads);

    struct timeval begin, end;
    gettimeofday(&begin, NULL);
    omp_set_num_threads(2);
    omp_set_nested(1);
#pragma omp parallel
    {
#pragma omp sections
        {
#pragma omp section
            /* MASTER */
            {
                master(partitionStrings[0], threadStates);
            }
#pragma omp section
            /* WORKERS */
            {
                worker(threads, partitionStrings, threadStates, partitionSize);
            }
        }
    }


    for (int i = 0; i < threads; i++) {
        if (globalState == 4) break;
        globalState = threadStates[i][globalState];
    }
    gettimeofday(&end, NULL);
    printf("Final global state is: %d, %s\n", globalState, globalState == 3 ? "accept" : "reject");
    printf("Total time = %f seconds\n",
           (double) (end.tv_usec - begin.tv_usec) / 1000000 +
           (double) (end.tv_sec - begin.tv_sec));
    free(string);
    for (int i = 0; i < threads; i++) {
        free(threadStates[i]);
        free(partitionStrings[i]);
    }
    free(threadStates);
    free(partitionStrings);
}


void master(char *string, int **threadStates) {

    int masterState = string[0] == 'a' ? 0 : 4;
    if (masterState == 4) {
        globalState = masterState;
        return;
    }

    long partitionSize = strlen(string);
    for (long i = 1; i < partitionSize; ++i) {
        char nextChar = string[i];
        switch (nextChar) {
            case 'a': {
                masterState = stateTable[masterState][0];
                break;
            }
            case 'b': {
                masterState = stateTable[masterState][1];
                break;
            }
            case 'c': {
                masterState = stateTable[masterState][2];
                break;
            }
            case 'd': {
                masterState = stateTable[masterState][3];
                break;
            }
            default: {
                masterState = 4;
                globalState = masterState;
                return;
            }
        }
    }
    globalState = masterState;
    for (int i = 0; i < 4; ++i) {
        threadStates[0][i] = globalState;
    }
}

void worker(int threads, char **partitionStrings, int **threadStates, long partitionSize) {
    omp_set_num_threads(threads - 1);
#pragma omp parallel for
    for (int i = 1; i < threads; i++) {
        for (int j = 1; j < 4; j++) {
            int workerState = j;
            for (long k = 0; k < partitionSize; k++) {
                switch (partitionStrings[i][k]) {
                    case 'a': {
                        workerState = stateTable[workerState][0];
                        break;
                    }
                    case 'b': {
                        workerState = stateTable[workerState][1];
                        break;
                    }
                    case 'c': {
                        workerState = stateTable[workerState][2];
                        break;
                    }
                    case 'd': {
                        workerState = stateTable[workerState][3];
                        break;
                    }
                    default: {
                        workerState = 4;
                        break;
                    }
                }
            }
            threadStates[i][j] = workerState;
        }
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

/* Construct a sample string to match against.  Note that this uses characters, encoded in ASCII,
   so to get 0-based characters you'd need to subtract 'a'. */
char *buildString() {
    int i;
    char *s = (char *) malloc(sizeof(char) * (STRINGSIZE));
    if (s == NULL) {
        printf("\nOut of memory!\n");
        exit(1);
    }
    unsigned long long max = STRINGSIZE - 3;

    /* seed the rnd generator (use a fixed number rather than the time for testing) */
    srand((unsigned int) time(NULL));

    /* And build a long string that might actually match */
    int j = 0;
    while (j < max) {
        s[j++] = 'a';
        while (rand() % 1000 < 997 && j < max)
            s[j++] = 'a';
        if (j < max)
            s[j++] = 'b';
        while (rand() % 1000 < 997 && j < max)
            s[j++] = 'b';
        if (j < max)
            s[j++] = (rand() % 2 == 1) ? 'c' : 'd';
        while (rand() % 1000 < 997 && j < max)
            s[j++] = (rand() % 2 == 1) ? 'c' : 'd';
    }
    s[max] = 'a';
    s[max + 1] = 'b';
    s[max + 2] = (rand() % 2 == 1) ? 'c' : 'd';
    return s;
}

char **partitionString(char *string, int numPartitions) {
    long length = strlen(string);
    long partitionSize = length / numPartitions;
    char **partitionedStrings = (char **) malloc((numPartitions + 1) * sizeof(char *));
    for (int i = 0; i < numPartitions; i++) {
        long start = i * partitionSize;
        long end = start + partitionSize;
        char *partitionString = (char *) malloc((partitionSize + 1) * sizeof(char));
        memcpy(partitionString, &string[start], (size_t) end - start);
        partitionString[partitionSize] = '\0';
        partitionedStrings[i] = partitionString;
    }
    partitionedStrings[numPartitions] = 0;
    return partitionedStrings;
}