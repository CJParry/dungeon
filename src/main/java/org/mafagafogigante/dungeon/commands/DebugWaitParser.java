package org.mafagafogigante.dungeon.commands;

import org.mafagafogigante.dungeon.date.DungeonTimeParser;
import org.mafagafogigante.dungeon.date.Duration;
import org.mafagafogigante.dungeon.game.*;
import org.mafagafogigante.dungeon.io.Writer;
import org.mafagafogigante.dungeon.util.Matches;
import org.mafagafogigante.dungeon.util.Messenger;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.Arrays;

/**
 * The parser of the debugging Wait command.
 */
class DebugWaitParser {

  /**
   * Evaluates and returns a constant representing which syntax was used.
   */
  private Syntax evaluateSyntax(String[] arguments) {
    if (isForSyntax(arguments)) {
      return Syntax.FOR;
    } else if (isUntilNextSyntax(arguments)) {
      return Syntax.UNTIL;
    } else {
      return Syntax.INVALID;
    }
  }

  private boolean isForSyntax(String[] arguments) {
    return arguments.length > 1 && "for".equalsIgnoreCase(arguments[0]);
  }

  private boolean isUntilNextSyntax(String[] arguments) {
    return arguments.length > 2 && "until".equalsIgnoreCase(arguments[0]) && "next".equalsIgnoreCase(arguments[1]);
  }

  private void writeDebugWaitSyntax() {
    DungeonString string = new DungeonString();
    string.append("Usage: wait ");
    final Color highlightColor = Color.ORANGE;
    string.setColor(highlightColor);
    string.append("for");
    string.resetColor();
    string.append(" [amount of time] or wait ");
    string.setColor(highlightColor);
    string.append("until next");
    string.resetColor();
    string.append(" [part of the day].");
    Writer.write(string);
  }

  void parseDebugWait(@NotNull String[] arguments, GameState gameState) {
    Syntax syntax = evaluateSyntax(arguments);
    if (syntax == Syntax.INVALID) {
      writeDebugWaitSyntax();
    } else {
      if (syntax == Syntax.FOR) {
        String timeString = StringUtils.join(arguments, " ", 1, arguments.length);
        try {
          Duration duration = DungeonTimeParser.parseDuration(timeString);
          rollDate(duration.getSeconds());
        } catch (IllegalArgumentException badArgument) {
          Writer.write("Provide small positive multipliers and units such as: '2 minutes and 10 seconds'");
        }
      } else if (syntax == Syntax.UNTIL) {
        Matches<PartOfDay> matches = Matches.findBestCompleteMatches(Arrays.asList(PartOfDay.values()), arguments[2]);
        if (matches.size() == 0) {
          Writer.write("That did not match any part of the day.");
        } else if (matches.size() == 1) {
          rollDate(PartOfDay.getSecondsToNext(gameState.getWorld().getWorldDate(), matches.getMatch(0)));
        } else {
          Messenger.printAmbiguousSelectionMessage();
        }
      }
    }
  }

  private void rollDate(long seconds) {
    Engine.rollDateAndRefresh(seconds);
    Writer.write("Waited for " + seconds + " seconds.");
  }

  private enum Syntax {FOR, UNTIL, INVALID}

}
