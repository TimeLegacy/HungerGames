package net.timelegacy.tlhungergames.events;

import net.timelegacy.tlcore.handler.CoinHandler;
import net.timelegacy.tlcore.utils.MessageUtils;
import net.timelegacy.tlminigame.enums.GamePlayerType;
import net.timelegacy.tlminigame.enums.GameStatus;
import net.timelegacy.tlminigame.event.PlayerJoinGameEvent;
import net.timelegacy.tlminigame.event.PlayerKillPlayerEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameListener implements Listener {

  @EventHandler
  public void onPlayerJoinGame(PlayerJoinGameEvent e) {
    if (e.getGame().getGameStatus().equals(GameStatus.WAITING)) { //We only want to send messages before the game starts
      // "ScarabCoder joined the game (1/8)
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
    }
  }

  @EventHandler
  public void onDamage(PlayerKillPlayerEvent e) {
    e.getGame().sendMessage(
        "&b"
            + e.getKilled().getOnlinePlayer().getName()
            + " &ehas been eliminated by §b"
            + e.getKiller().getOnlinePlayer().getName() + "&e!");

    CoinHandler.addCoins(e.getKiller().getOnlinePlayer().getUniqueId(), 5);

    CoinHandler.addCoins(e.getKiller().getOnlinePlayer().getUniqueId(), 5);

    e.getGame().setPlayerMode(GamePlayerType.SPECTATOR, e.getKilled());

    if (e.getGame().getGamePlayerByMode(GamePlayerType.PLAYER).size() == 1) {
      e.getGame().endGame();

    } else {
      e.getGame().sendMessage("&2"
          + e.getGame().getGamePlayerByMode(GamePlayerType.PLAYER).size()
          + " &aplayers remain!");
    }
  }
}
