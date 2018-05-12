package org.mafagafogigante.dungeon.commands;

import org.junit.Assert;
import org.junit.Test;
import org.mafagafogigante.dungeon.game.Game;

public class CommandSetsTest {

    @Test
    public void testInitialisation(){
        Game game = new Game();
        CommandSets commandSets = new CommandSets(game);
        Assert.assertTrue(commandSets.hasCommandSet("default"));
        Assert.assertTrue(commandSets.hasCommandSet("extra"));
        Assert.assertTrue(commandSets.hasCommandSet("debug"));

    }
}
