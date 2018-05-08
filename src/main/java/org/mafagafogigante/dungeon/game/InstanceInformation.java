package org.mafagafogigante.dungeon.game;

import org.mafagafogigante.dungeon.util.Utils;

import java.io.Serializable;

/**
 * Information about an instance of the game.
 */
class InstanceInformation implements Serializable {

  private final long startingTimeMillis;
  private int acceptedCommandCount;

  public InstanceInformation() {
    this.startingTimeMillis = System.currentTimeMillis();
  }

  /**
   * Returns a string representing the duration of this instance.
   */
  public String getDurationString() {
    return Utils.makePeriodString(System.currentTimeMillis() - startingTimeMillis);
  }

  public int getAcceptedCommandCount() {
    return acceptedCommandCount;
  }

  public void incrementAcceptedCommandCount() {
    this.acceptedCommandCount++;
  }

}
