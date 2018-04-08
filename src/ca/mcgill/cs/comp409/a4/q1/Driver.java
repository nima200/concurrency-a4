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
        for (int j = 0; j < 5; j++) {
            Grid grid = new Grid(30, 30);
            grid.initialize(r, n, k);

            ConcurrentLinkedQueue<CharacterItem> characterItems = new ConcurrentLinkedQueue<>(grid.getCharacters());
            ExecutorService poolExecutor = Executors.newFixedThreadPool(p);
            List<Future<Long>> results = new ArrayList<>();
            for (int i = 0; i < p; i++) {
                results.add(poolExecutor.submit(new CharacterPlayer(characterItems, grid)));
            }

            for (Future<Long> result : results) {
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
            List<CharacterItem> characters = new ArrayList<>(characterItems);
            while (!characterItems.isEmpty()) {
                CharacterItem characterItem = characterItems.poll();
                assert characterItem != null;
                System.out.println("Character " + i + " move count: " + characterItem.getMoveCount());
                i++;
            }
            int totalCharMoves = 0;
            for (CharacterItem characterItem : characters) {
                totalCharMoves += characterItem.getMoveCount();
            }
            avgMaxCount += totalCharMoves;
            System.out.println("Total characters move count: (round " + j + ") " + totalCharMoves);
            poolExecutor.shutdown();
        }
        avgMaxCount /= 5;
        System.out.println("Average total characters move count on 5 rounds: " + avgMaxCount);

    }


}
