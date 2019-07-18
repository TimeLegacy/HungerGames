package net.timelegacy.tlhungergames.events;

import net.timelegacy.tlcore.datatype.CustomScoreboard;
import net.timelegacy.tlcore.handler.CoinHandler;
import net.timelegacy.tlcore.utils.MessageUtils;
import net.timelegacy.tlcore.utils.ScoreboardUtils;
import net.timelegacy.tlhungergames.TLHungerGames;
import net.timelegacy.tlminigame.enums.GamePlayerType;
import net.timelegacy.tlminigame.enums.GameStatus;
import net.timelegacy.tlminigame.event.GameEndEvent;
import net.timelegacy.tlminigame.event.GameStartEvent;
import net.timelegacy.tlminigame.event.PlayerJoinGameEvent;
import net.timelegacy.tlminigame.event.PlayerKillPlayerEvent;
import net.timelegacy.tlminigame.event.PlayerLeaveGameEvent;
import net.timelegacy.tlminigame.game.GamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameListener implements Listener {

  @EventHandler
  public void onGameEnd(GameEndEvent e) {
    ChestListener.chests.clear();
  }

  @EventHandler
  public void onGameStart(GameStartEvent e) {
    e.getGame().sendMessage("&7&l|&b&m&l                                    &7&l|", false, true);
    e.getGame().sendMessage("");
    e.getGame().sendMessage("&6&lHUNGER GAMES", false, true);
    e.getGame().sendMessage(
        "&7&lMAP&7: &e" + TLHungerGames.getMapConfig("highway").get("name") + " &7&oby&e " + TLHungerGames
            .getMapConfig("highway").get("author") + " ", false, true);
    e.getGame().sendMessage("");
    e.getGame().sendMessage("&7&l|&b&m&l                                    &7&l|", false, true);

    for (GamePlayer gamePlayer : e.getGame().getPlayers()) {
      ScoreboardUtils.getCustomScoreboard(gamePlayer.getOnlinePlayer().getUniqueId()).destroy();
    }
  }

  @EventHandler
  public void onPlayerJoinGame(PlayerJoinGameEvent e) {
    if (e.getGame().getGameStatus().equals(GameStatus.WAITING)) { //We only want to send messages before the game starts
      e.getGame()
          .sendMessage(
              MessageUtils.MAIN_COLOR +
                  e.getPlayer().getPlayer().getName()
                  + "&7 joined the game! &f(&8"
                  + e.getGame().getPlayers().size()
                  + "&7/&8"
                  + e.getGame().getGameSettings().getMaximumPlayers()
                  + "&f)");
      e.getPlayer().getOnlinePlayer().getInventory().clear(); //The player shouldn't have anything in their inventory

      CustomScoreboard scoreboard = new CustomScoreboard(e.getPlayer().getOnlinePlayer(),
          MessageUtils.colorize("&c&lHUNGER GAMES"));
      scoreboard.create();
      scoreboard.setLine(0, MessageUtils.colorize("&9"));
      scoreboard.setLine(1,
          MessageUtils.colorize("&fNeeded Players: &e" + e.getGame().getGameSettings().getMinimumPlayers()));
      scoreboard.setLine(2, MessageUtils.colorize("&fMap: &b" + e.getGame().getArena().getName()));
      scoreboard.setLine(3, MessageUtils.colorize("&8"));
      scoreboard.setLine(4, MessageUtils.colorize("&eplay.timelegacy.net"));

      ScoreboardUtils.saveCustomScoreboard(e.getPlayer().getOnlinePlayer().getUniqueId(), scoreboard);
    }
  }

  @EventHandler
  public void onPlayerLeaveGame(PlayerLeaveGameEvent e) {
    if (e.getGame().getGameStatus() == GameStatus.INGAME
        && e.getGame().getGamePlayerByMode(GamePlayerType.PLAYER).size() <= 1) {
      e.getGame().endGame();
    }
  }

  @EventHandler
  public void onDamage(PlayerKillPlayerEvent e) {
    e.getGame().sendMessage(
        "&b"
            + e.getKilled().getOnlinePlayer().getName()
            + " &ehas been eliminated by §b"
            + e.getKiller().getOnlinePlayer().getName() + "&e!");

    CoinHandler.addCoins(e.getKiller().getOnlinePlayer().getUniqueId(), 10);
    CoinHandler.addCoins(e.getKilled().getOnlinePlayer().getUniqueId(), 5);

    e.getGame().setPlayerMode(GamePlayerType.SPECTATOR, e.getKilled());
    e.getKilled().getOnlinePlayer().teleport(TLHungerGames.spectatorSpawn("highway"));

    if (e.getGame().getGamePlayerByMode(GamePlayerType.PLAYER).size() == 1) {
      e.getGame().endGame();

    } else {
      e.getGame().sendMessage("&2"
          + e.getGame().getGamePlayerByMode(GamePlayerType.PLAYER).size()
          + " &aplayers remain!");
    }
  }
}
