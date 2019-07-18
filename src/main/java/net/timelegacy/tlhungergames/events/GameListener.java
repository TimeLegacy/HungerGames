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
import net.timelegacy.tlminigame.manager.PlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {

  @EventHandler
  public void onGameEnd(GameEndEvent e) {
    ChestListener.chests.clear();
  }

  @EventHandler
  public void onGameStart(GameStartEvent e) {
    e.getGame()
        .sendMessage("&7&l|&b&m&l                                                             &7&l|", false, true);
    e.getGame().sendMessage("");
    e.getGame().sendMessage("&6&lHUNGER GAMES", false, true);
    e.getGame().sendMessage(
        "&7&lMAP&7: &e" + TLHungerGames.getMapConfig("highway").get("name") + " &7&oby&e " + TLHungerGames
            .getMapConfig("highway").get("author") + " ", false, true);
    e.getGame().sendMessage("");
    e.getGame()
        .sendMessage("&7&l|&b&m&l                                                             &7&l|", false, true);

    for (GamePlayer gamePlayer : e.getGame().getPlayers()) {
      //TODO make this update on a runnable. Yay!

      CustomScoreboard scoreboard = ScoreboardUtils.getCustomScoreboard(gamePlayer.getOnlinePlayer().getUniqueId());
      scoreboard.setLine(1, MessageUtils.colorize("&fMap: &b" +
          e.getGame().getArena().getName()));
      scoreboard.setLine(2, MessageUtils.colorize("&fSpectators: &d" +
          e.getGame().getGamePlayerByMode(GamePlayerType.SPECTATOR).size()));
      scoreboard.setLine(3, MessageUtils.colorize("&fChests Refill In: &c" + "TODO dis ting"));
      scoreboard.setLine(4, MessageUtils.colorize("&1"));
      scoreboard.setLine(5, MessageUtils.colorize("&eplay.timelegacy.net"));
    }

    e.getGame().getArena().getArenaSettings().setCanDestroy(true);
    e.getGame().getArena().getArenaSettings().setAllowPlayerInvincibility(false);
  }

  @EventHandler
  public void blockBreak(BlockBreakEvent e) {
    if (PlayerManager.getGamePlayer(e.getPlayer()).getGame().getGameStatus() == GameStatus.INGAME) {
      if (e.getBlock().getType() == Material.ACACIA_LEAVES
          || e.getBlock().getType() == Material.BIRCH_LEAVES
          || e.getBlock().getType() == Material.DARK_OAK_LEAVES
          || e.getBlock().getType() == Material.JUNGLE_LEAVES
          || e.getBlock().getType() == Material.OAK_LEAVES
          || e.getBlock().getType() == Material.SPRUCE_LEAVES) {

        e.setCancelled(false);
      } else {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerJoinGame(PlayerJoinGameEvent e) {
    if (e.getGame().getGameStatus().equals(GameStatus.WAITING)) { //We only want to send messages before the game starts
      e.getGame()
          .sendMessage(
              MessageUtils.MAIN_COLOR +
                  e.getPlayer().getPlayer().getName()
                  + "&7 joined the game! &f(&7"
                  + e.getGame().getPlayers().size()
                  + "&8/&7"
                  + e.getGame().getGameSettings().getMaximumPlayers()
                  + "&f)");
      e.getPlayer().getOnlinePlayer().getInventory().clear(); //The player shouldn't have anything in their inventory

      CustomScoreboard scoreboard = new CustomScoreboard(e.getPlayer().getOnlinePlayer(),
          MessageUtils.colorize("&c&lHUNGER GAMES"));
      scoreboard.create();
      scoreboard.setLine(0, MessageUtils.colorize("&0"));
      scoreboard.setLine(1,
          MessageUtils.colorize("&fNeeded Players: &e" + e.getGame().getGameSettings().getMinimumPlayers()));
      scoreboard.setLine(2, MessageUtils.colorize("&fMap: &b" + e.getGame().getArena().getName()));
      scoreboard.setLine(3, MessageUtils.colorize("&1"));
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

    MessageUtils.sendMessage(e.getKiller().getOnlinePlayer(), "&eYou have been awarded 10 coins.", false);
    CoinHandler.addCoins(e.getKiller().getOnlinePlayer().getUniqueId(), 10);

    Location loc = e.getKilled().getOnlinePlayer().getLocation();
    Inventory inv = e.getKilled().getOnlinePlayer().getInventory();
    for (ItemStack item : inv.getContents()) {
      if (item != null) {
        loc.getWorld().dropItemNaturally(loc,
            item.clone());
      }
    }

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
