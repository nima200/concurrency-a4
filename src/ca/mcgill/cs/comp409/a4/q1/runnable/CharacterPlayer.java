package ca.mcgill.cs.comp409.a4.q1.runnable;

import ca.mcgill.cs.comp409.a4.q1.grid.items.CharacterItem;

public class CharacterPlayer implements Runnable {

    private CharacterItem myCharacter;

    public CharacterPlayer(CharacterItem pCharacter) {
        assert pCharacter != null;
        myCharacter = pCharacter;
    }

    @Override
    public void run() {

    }
}
