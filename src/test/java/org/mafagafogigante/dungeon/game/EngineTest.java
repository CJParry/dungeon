package org.mafagafogigante.dungeon.game;

import org.junit.Assert;
import org.junit.Test;

public class EngineTest {

    @Test
    public void testInitializeEngineGameState(){
        GameState gameState = new GameState();
        Engine engine = new Engine(gameState);
        Assert.assertTrue(gameState == engine.getGameState());
    }

    @Test
    public void testNotifyGameStateModification(){
        GameState gameState = new GameState();
        Engine engine = new Engine(gameState);
        engine.notifyGameStateModification();
        Assert.assertTrue(!gameState.isSaved());
    }

    @Test
    public void testRefresh(){
        GameState gameState = new GameState();
        Engine engine = new Engine(gameState);
        gameState.setSaved(true);
        engine.refresh();
        Assert.assertTrue(!engine.getGameState().isSaved());
    }
}
