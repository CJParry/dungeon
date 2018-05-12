package org.mafagafogigante.dungeon.game;

import org.mafagafogigante.dungeon.achievements.AchievementStoreFactory;
import org.mafagafogigante.dungeon.date.Date;
import org.mafagafogigante.dungeon.entity.creatures.Creature;
import org.mafagafogigante.dungeon.entity.creatures.Hero;
import org.mafagafogigante.dungeon.io.Writer;
import org.mafagafogigante.dungeon.util.Utils;

import java.awt.Color;

/**
 * Engine class that contains most static methods that need to be called to alter the loaded GameState.
 */
public final class Engine {

  private static final int BATTLE_TURN_DURATION = 30;
  private  GameState gameState;

  public GameState getGameState() {
    return gameState;
  }

  public Engine(GameState gameState) { this.gameState = gameState;
  }

  /**`
   * Refreshes the game. This method should be called whenever the state of the game is changed and the engine should be
   * updated. If time passed, use {@link Engine#rollDateAndRefresh(long)}.
   */
  public void refresh() {
    effectivelyUpdate(0);
  }

  /**
   * Rolls the world date forward and refreshes the game. This method should be called whenever the state of the game is
   * changed and the engine should be updated. If no time passed, use {@link Engine#refresh()}.
   *
   * @param seconds how many seconds to roll the date forward, a positive integer
   */
  public void rollDateAndRefresh(long seconds) {
    if (seconds <= 0) {
      throw new IllegalArgumentException("seconds should be positive.");
    }
    effectivelyUpdate(seconds);
  }

  /**
   * Effectively updates the game. Rolls time forward before silently refreshing the game.
   *
   * @param seconds how many seconds to roll the date forward, nonnegative
   */
  private void effectivelyUpdate(long seconds) {
    if (seconds < 0) {
      throw new IllegalArgumentException("seconds should be nonnegative.");
    }
    if (seconds > 0) {
      gameState.getWorld().rollDate(seconds);
    }
    silentRefresh();
    notifyGameStateModification();
  }

  /**
   * Sets the status of the GameState to unsaved.
   */
  void notifyGameStateModification() {
    gameState.setSaved(false);
  }

  /**
   * Silently refreshes the game. Does not produce any visible textual output.
   */
  private void silentRefresh() {
    refreshSpawners();
    refreshItems();
  }

  /**
   * Ends the turn, refreshing the game state and checking if any achievements were unlocked.
   */
  public void endTurn() {
    silentRefresh();
    refreshAchievements();
  }

  /**
   * Refreshes all relevant Spawners in the world, currently, that is the spawners of the location the Hero is at.
   */
  private void refreshSpawners() {
    gameState.getHero().getLocation().refreshSpawners(gameState);
  }

  /**
   * Refreshes all the items in the location the Hero is at.
   */
  private void refreshItems() {
    gameState.getHero().getLocation().refreshItems(gameState);
  }

  /**
   * Iterates over all achievements, trying to unlock yet to be unlocked achievements.
   */
  private void refreshAchievements() {
    Date worldDate = gameState.getWorld().getWorldDate();
    gameState.getHero().getAchievementTracker().update(AchievementStoreFactory.getDefaultStore(), worldDate);
  }

  /**
   * Simulates a battle between the hero and a creature.
   *
   * @param hero the attacker
   * @param foe the defender
   */
  public void battle(Hero hero, Creature foe) {
    if (hero == foe) {
      Writer.write(new DungeonString("You cannot attempt suicide."));
      return;
    }
    while (hero.getHealth().isAlive() && foe.getHealth().isAlive()) {
      hero.hit(foe, gameState);
      rollDateAndRefresh(BATTLE_TURN_DURATION);
      // No contract specifies that calling hit on the Hero will not kill it, so check both creatures again.
      // Additionally, rolling the date forward may kill the hero in the future.
      if (hero.getHealth().isAlive() && foe.getHealth().isAlive()) {
        foe.hit(hero, gameState);
        rollDateAndRefresh(BATTLE_TURN_DURATION);
      }
    }
    Creature survivor = hero.getHealth().isAlive() ? hero : foe;
    Creature defeated = (survivor == hero) ? foe : hero;
    // Imagine if a third factor (such as hunger) could kill one of the creatures.
    // I think it still makes sense to say that the survivor managed to kill the defeated, but that's just me.
    DungeonString dungeonString = new DungeonString();
    dungeonString.setColor(Color.CYAN);
    dungeonString.append(survivor.getName().getSingular());
    dungeonString.append(" managed to kill ");
    dungeonString.append(defeated.getName().getSingular());
    dungeonString.append(".\n");
    Writer.write(dungeonString);
    writeDrops(defeated);
    if (hero == survivor) {
      PartOfDay partOfDay = PartOfDay.getCorrespondingConstant(gameState.getWorld().getWorldDate());
      gameState.getStatistics().getBattleStatistics().addBattle(foe, defeated.getCauseOfDeath(), partOfDay);
      gameState.getStatistics().getExplorationStatistics().addKill(hero.getLocation().getPoint());
      long heroTakenDamage = hero.getBattleLog().getAndResetTaken();
      long heroInflictedDamage = hero.getBattleLog().getAndResetInflicted();
      gameState.getStatistics().getHeroStatistics().incrementDamageTaken(heroTakenDamage);
      gameState.getStatistics().getHeroStatistics().incrementDamageInflicted(heroInflictedDamage);
    }
  }

  /**
   * Writes a message with what the creature dropped if it dropped something.
   *
   * @param source a dead Creature
   */
  private void writeDrops(Creature source) {
    if (!source.getDroppedItemsList().isEmpty()) {
      DungeonString string = new DungeonString();
      string.append(source.getName().getSingular() + " dropped ");
      string.append(Utils.enumerateEntities(source.getDroppedItemsList()));
      string.append(".\n");
      Writer.write(string);
    }
  }

}
