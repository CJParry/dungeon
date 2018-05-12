package org.mafagafogigante.dungeon.gui;

import org.junit.Assert;
import org.junit.Test;
import org.mafagafogigante.dungeon.game.Game;

public class GameWindowTest {

    @Test
    public void testInitComponents(){
        Game game = new Game();
        GameWindow gameWindow = new GameWindow(game);
        Assert.assertEquals(gameWindow.getColumns(), 100);
        Assert.assertEquals(gameWindow.getRows(), 30);
    }
}
