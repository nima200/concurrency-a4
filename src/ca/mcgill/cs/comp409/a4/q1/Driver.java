package ca.mcgill.cs.comp409.a4.q1;

import ca.mcgill.cs.comp409.a4.q1.grid.Grid;
import ca.mcgill.cs.comp409.a4.q1.grid.items.CharacterItem;
import ca.mcgill.cs.comp409.a4.q1.runnable.CharacterPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Driver {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Invalid number of arguments. Expected: 4, Received: " + args.length + ". Exiting...");
            System.exit(1);
        }

        int n = 0, p = 0, r = 0, k = 0;
        try {
            n = Integer.parseInt(args[0]);
            p = Integer.parseInt(args[1]);
            r = Integer.parseInt(args[2]);
            k = Integer.parseInt(args[3]);
        } catch (NumberFormatException pE) {
            System.out.println("Invalid arguments to program. All 4 arguments must be valid numbers. Exiting...");
            System.exit(1);
        }

        int avgMaxCount = 0;
        /* Try to average 5 runs of the simulation since results are random due to obstacles */
        for (int j = 0; j < 5; j++) {
            Grid grid = new Grid(30, 30);
            grid.initialize(r, n, k);
            /* Create a concurrent queue of character items for threads to take characters from */
            ConcurrentLinkedQueue<CharacterItem> characterItems = new ConcurrentLinkedQueue<>(grid.getCharacters());
            /* Thread pool */
            ExecutorService poolExecutor = Executors.newFixedThreadPool(p);
            List<Future> results = new ArrayList<>();
            /* Submit p new jobs to the pool */
            for (int i = 0; i < p; i++) {
                results.add(poolExecutor.submit(new CharacterPlayer(characterItems, grid)));
            }

            /* Wait for all jobs to complete */
            for (Future result : results) {
                try {
                    result.get();
                } catch (InterruptedException pE) {
                    System.out.println("Main thread interrupted unexpectedly... Exiting.");
                    pE.printStackTrace();
                    System.exit(1);
                } catch (ExecutionException pE) {
                    pE.printStackTrace();
                    System.exit(1);
                }
            }

            int i = 0;
            /* Get all characters, print their move counts, and add to the total move count*/
            List<CharacterItem> characters = new ArrayList<>(characterItems);
            int totalCharMoves = 0;
            for (CharacterItem characterItem : characters) {
                System.out.println("Character " + i + " move count: " + characterItem.getMoveCount());
                totalCharMoves += characterItem.getMoveCount();
                i++;
            }
            /* Add this run's total moves to the average total moves */
            avgMaxCount += totalCharMoves;
            System.out.println("Total characters move count: (round " + j + ") " + totalCharMoves);
            poolExecutor.shutdown();
        }
        /* Report the average moves */
        avgMaxCount /= 5;
        System.out.println("Average total characters move count on 5 rounds: " + avgMaxCount);

    }


}
