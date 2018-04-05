package ca.mcgill.cs.comp409.a4.q1.grid.items.exception;

public class InvalidMoveException extends Exception {
    public InvalidMoveException() {
        super("The target tile is not free!");
    }
}
