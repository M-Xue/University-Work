package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestSwampTile {
    @Test
    @DisplayName("Test swamp tile using one mercenary")
    public void testSwampTileMercenary() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_assassin", "c_swampTileTest_assassin");

        res = dmc.tick(Direction.RIGHT);
        Position posAssassin = getEntities(res, "assassin").get(0).getPosition();
        Position posSwamp = getEntities(res, "swamp_tile").get(0).getPosition();
        assertEquals(posAssassin, posSwamp);

        // movement factor = 3
        for (int i = 0; i < 3; i++) {
            res = dmc.tick(Direction.RIGHT);
            posAssassin = getEntities(res, "assassin").get(0).getPosition();
            posSwamp = getEntities(res, "swamp_tile").get(0).getPosition();
            assertEquals(posAssassin, posSwamp);
        }

        res = dmc.tick(Direction.RIGHT);
        posAssassin = getEntities(res, "assassin").get(0).getPosition();
        posSwamp = getEntities(res, "swamp_tile").get(0).getPosition();
        assertNotEquals(posAssassin, posSwamp);
    } 

    @Test
    @DisplayName("Test swamp tile using 2 assassins")
    public void testSwampTileMercAndAssassin() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_2assassins", "c_swampTileTest_assassin");

        res = dmc.tick(Direction.RIGHT);
        Position posAssassin1 = getEntities(res, "assassin").get(0).getPosition();
        Position posAssassin2 = getEntities(res, "assassin").get(1).getPosition();
        Position posSwamp = getEntities(res, "swamp_tile").get(0).getPosition();
        assertEquals(posAssassin1, posSwamp);
        assertEquals(posAssassin2, posSwamp);

        // movement factor = 3
        for (int i = 0; i < 3; i++) {
            res = dmc.tick(Direction.RIGHT);
            posAssassin1 = getEntities(res, "assassin").get(0).getPosition();
            posAssassin2 = getEntities(res, "assassin").get(1).getPosition();
            posSwamp = getEntities(res, "swamp_tile").get(0).getPosition();
            assertEquals(posAssassin1, posSwamp);
            assertEquals(posAssassin2, posSwamp);
        }

        res = dmc.tick(Direction.RIGHT);
        posAssassin1 = getEntities(res, "assassin").get(0).getPosition();
        posAssassin2 = getEntities(res, "assassin").get(1).getPosition();
        posSwamp = getEntities(res, "swamp_tile").get(0).getPosition();
        assertNotEquals(posAssassin1, posSwamp);
        assertNotEquals(posAssassin2, posSwamp);
    } 
}
