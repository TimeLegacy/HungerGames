package net.timelegacy.tlhungergames.events;

import java.util.ArrayList;
import java.util.List;
import net.timelegacy.tlcore.datatype.CustomScoreboard;
import net.timelegacy.tlcore.utils.MessageUtils;
import net.timelegacy.tlcore.utils.ScoreboardUtils;
import net.timelegacy.tlhungergames.handlers.MapHandler;
import net.timelegacy.tlminigame.datatype.Game;
import net.timelegacy.tlminigame.datatype.GamePlayer;
import net.timelegacy.tlminigame.datatype.GamePlayer.Mode;
import net.timelegacy.tlminigame.enums.GameState;
import net.timelegacy.tlminigame.events.GameEndEvent;
import net.timelegacy.tlminigame.events.GamePlayerRemoveEvent;
import net.timelegacy.tlminigame.events.GameStartEvent;
import net.timelegacy.tlminigame.handler.GameHandler;
import net.timelegacy.tlminigame.handler.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {

  private List<Block> brokenBlocks = new ArrayList<>();

  @EventHandler
  public void onGameEnd(GameEndEvent e) {
    ChestListener.chests.clear();

    for (Block block : brokenBlocks) {
      block.getLocation().getBlock().setType(block.getType());
    }

    MapHandler.setRandomMap();
  }

  @EventHandler
  public void onGameStart(GameStartEvent e) {
    int count = 0;

    for (GamePlayer gamePlayer : PlayerHandler.getGamePlayerByMode(Mode.PLAYER)) {
      Player player = gamePlayer.getPlayer();
      MessageUtils.sendCenteredMessage(player, "&7&l|&b&m&l                                                  &7&l|");
      MessageUtils.sendCenteredMessage(player, "");
      MessageUtils.sendCenteredMessage(player, "&c&lHUNGER GAMES");
      MessageUtils.sendCenteredMessage(player, "&7&lMAP: &f&o" + MapHandler.getCurrentMap().getMapName());
      MessageUtils.sendCenteredMessage(player, "");
      MessageUtils.sendCenteredMessage(player, "&7&l|&b&m&l                                                  &7&l|");

      //TODO make this update on a runnable. Yay!

      CustomScoreboard scoreboard = ScoreboardUtils.getCustomScoreboard(gamePlayer.getPlayer().getUniqueId());
      scoreboard.setLine(1, MessageUtils.colorize("&fMap: &b" + MapHandler.getCurrentMap().getMapName()));
      scoreboard.setLine(2, MessageUtils.colorize("&fSpectators: &d" +
          PlayerHandler.getGamePlayerByMode(Mode.SPECTATOR.SPECTATOR).size()));
      scoreboard.setLine(3, MessageUtils.colorize("&fChests Refill In: &c" + "TODO dis ting"));
      scoreboard.setLine(4, MessageUtils.colorize("&1"));
      scoreboard.setLine(5, MessageUtils.colorize("&eplay.timelegacy.net"));

      player.teleport(MapHandler.getCurrentMap().getSpawns().get(count));
      count++;

      player.setGameMode(GameMode.SURVIVAL);
    }


  }

  @EventHandler
  public void blockBreak(BlockBreakEvent e) {
    if (GameHandler.getGame().getState() == GameState.INGAME) {
      if (e.getBlock().getType() == Material.ACACIA_LEAVES
          || e.getBlock().getType() == Material.BIRCH_LEAVES
          || e.getBlock().getType() == Material.DARK_OAK_LEAVES
          || e.getBlock().getType() == Material.JUNGLE_LEAVES
          || e.getBlock().getType() == Material.OAK_LEAVES
          || e.getBlock().getType() == Material.SPRUCE_LEAVES) {

        brokenBlocks.add(e.getBlock());

        e.setCancelled(false);
      } else {
        e.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerJoinGame(PlayerJoinEvent e) {
    CustomScoreboard scoreboard = new CustomScoreboard(e.getPlayer(),
          MessageUtils.colorize("&c&lHUNGER GAMES"));
      scoreboard.create();
      scoreboard.setLine(0, MessageUtils.colorize("&0"));
      scoreboard.setLine(1,
          MessageUtils.colorize("&fNeeded Players: &e" + GameHandler.getGame().getMinPlayers()));
    scoreboard.setLine(2, MessageUtils.colorize("&fMap: &b" + MapHandler.getCurrentMap().getMapName()));
      scoreboard.setLine(3, MessageUtils.colorize("&1"));
      scoreboard.setLine(4, MessageUtils.colorize("&eplay.timelegacy.net"));

    ScoreboardUtils.saveCustomScoreboard(e.getPlayer().getPlayer().getUniqueId(), scoreboard);
  }

  @EventHandler
  public void onWorldLoad(WorldLoadEvent e) {
    World world = e.getWorld();

    world.setTime(0);

    for (Entity entity : world.getEntities()) {
      if (!(entity instanceof Player)) {
        entity.remove();
      }
    }
  }

  @EventHandler
  public void onGamePlayerLeave(GamePlayerRemoveEvent e) {
    if (PlayerHandler.getGamePlayerByMode(Mode.PLAYER).size() == 1) {
      showEndGameMessage(GameHandler.getGame());
    }
  }

  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (GameHandler.getGame().getState() == GameState.INGAME) {
      if (e.getEntity() instanceof Player) {
        Player killed = (Player) e.getEntity();
        if (e.getDamage() >= killed.getHealth()) {
          e.setCancelled(true);

          //drop items
          Location loc = killed.getLocation();
          Inventory inv = killed.getInventory();
          for (ItemStack item : inv.getContents()) {
            if (item != null) {
              loc.getWorld().dropItemNaturally(loc,
                  item.clone());
            }
          }

          //give coins to person who killed e.getPlayer()
          if (e instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) e)
                .getDamager() instanceof Player) {

              Player killer = Bukkit.getPlayer(((EntityDamageByEntityEvent) e)
                  .getDamager().getName());

              Bukkit.broadcastMessage(MessageUtils.colorize(
                  "&b"
                      + killed.getName()
                      + " &ehas been eliminated by &b"
                      + killer.getName() + "&e!"));

            } else {
              Bukkit.broadcastMessage(MessageUtils.colorize(
                  "&b"
                      + e.getEntity().getName()
                      + " &ehas been eliminated!"));
            }
          }
          PlayerHandler.getGamePlayer(killed.getUniqueId()).setMode(Mode.SPECTATOR);
          if (PlayerHandler.getGamePlayerByMode(Mode.PLAYER).size() == 1) {
            showEndGameMessage(GameHandler.getGame());
          } else {
            Bukkit.broadcastMessage(MessageUtils.colorize(
                "&2"
                    + PlayerHandler.getGamePlayerByMode(Mode.PLAYER).size()
                    + " &aplayers remain!"));
          }
        }
      }
    }
  }

  public void showEndGameMessage(Game game) {

    for (Player player : Bukkit.getOnlinePlayers()) {
      MessageUtils.sendCenteredMessage(
          player, "&7&l|&b&m&l                                                  &7&l|");
      MessageUtils.sendCenteredMessage(player, "");
      MessageUtils.sendCenteredMessage(player, "&c&lHUNGER GAMES");
      MessageUtils.sendCenteredMessage(player,
          "&7&lWINNER: &f&o" + PlayerHandler.getGamePlayerByMode(Mode.PLAYER).get(0).getPlayer().getName());
      MessageUtils.sendCenteredMessage(player, "");
      MessageUtils.sendCenteredMessage(
          player, "&7&l|&b&m&l                                                  &7&l|");
    }

    for (GamePlayer gamePlayer : PlayerHandler.getGamePlayerByMode(Mode.SPECTATOR)) {
      gamePlayer.getPlayer().teleport(PlayerHandler.getGamePlayerByMode(Mode.PLAYER).get(0).getPlayer().getLocation());
    }

    game.end();
  }
}
