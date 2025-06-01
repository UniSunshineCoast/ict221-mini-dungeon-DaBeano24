import dungeon.engine.GameEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGameEngine {
    @Test
    void testGetSize() {
        GameEngine gameEngine = new GameEngine(10, 3);

        assertEquals(10, gameEngine.getSize());

    }
}
