package net.timelegacy.tlhungergames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.timelegacy.tlcore.handler.CoinHandler;
import net.timelegacy.tlcore.utils.MessageUtils;
import net.timelegacy.tlminigame.events.GameEndEvent;
import net.timelegacy.tlminigame.events.GameStartEvent;
import net.timelegacy.tlminigame.map.Map;
import net.timelegacy.tlminigame.match.GameHandler;
import net.timelegacy.tlminigame.match.GameState;
import net.timelegacy.tlminigame.playerstate.PlayersTeam;
import net.timelegacy.tlminigame.playerstate.SpectatorsTeam;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TLHungerGames extends JavaPlugin implements Listener {

  @Override
  public void onEnable() {
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    GameHandler.setGameName("HUNGERGAMES");

    // Maps

    GameHandler.maps.add(new Map("Highway", Map.spawns("highway")));
    GameHandler.maps.add(new Map("Highway", Map.spawns("highway")));

    GameHandler.getRandomMap();
    GameHandler.loadMap(GameHandler.currentMap.getWorldName());
  }

  @EventHandler
  public void onGameStart(GameStartEvent e) {
    Map currentMap = GameHandler.currentMap;

    int count = 0;
    for (Player p : PlayersTeam.getPlayers()) {
      Location spawn = new Location(Bukkit.getWorld(currentMap.getWorldName()),
          currentMap.getSpawns().get(count).getX(), currentMap.getSpawns().get(count).getY(),
          currentMap.getSpawns().get(count).getZ());

      Location center = new Location(Bukkit.getWorld(currentMap.getWorldName()), currentMap.getCenter().getX(),
          currentMap.getCenter().getY(), currentMap.getCenter().getZ());
      Vector dirBetweenLocations = center.toVector().subtract(spawn.toVector());
      spawn.setDirection(dirBetweenLocations);
      p.teleport(spawn);

      p.getInventory().clear();
      p.updateInventory();

      p.setGameMode(GameMode.SURVIVAL);
      count++;
    }
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e){

    //TODO allow blocks being broken and roll back when game ends

    e.setCancelled(true);
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e){
    e.setCancelled(true);
  }

  // CHANGE DEPENDING ON THE GAME
  @EventHandler
  public void onDamage(EntityDamageEvent e) {

    if (e.getEntity() instanceof Player) {

      Player p = (Player) e.getEntity();

      if(GameHandler.getState() == GameState.WAITING || GameHandler.getState() == GameState.STARTING) {
        e.setCancelled(true);
      }

      if (GameHandler.getState() == GameState.INGAME) {

        if (e.getDamage() >= p.getHealth()) {

          e.setCancelled(true);

          //drop items
          Location loc = p.getLocation();
          Inventory inv = p.getInventory();
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

              String damager = ((EntityDamageByEntityEvent) e)
                  .getDamager().getName();

              Bukkit.broadcastMessage(
                  "§b"
                      + e.getEntity().getName()
                      + " §ehas been eliminated by §b"
                      + damager + "§e!");

              Player d = Bukkit.getPlayer(damager);
 	                                /*if (sgc.getMinigame().killCoins > 1) {
 	                                    TitleManager.sendActionTitle(d, "§6+"
 	                                            + sgc.getMinigame().killCoins + " Coins");
 	                                } else {
 	                                    TitleManager.sendActionTitle(d, "§6+"
 	                                            + sgc.getMinigame().killCoins + " Coin");

 	                                }*/
              CoinHandler.addCoins(d.getUniqueId(), 5);

              //Game.getInstance().core.statsHandler.addKill(d);
            }else {
              Bukkit.broadcastMessage(
                  "§b"
                      + e.getEntity().getName()
                      + " §ehas been eliminated!");
            }
          }

          // debug spectator
          PlayersTeam
              .removePlayer(p);
          SpectatorsTeam.addSpectator(p);

 	                        /*if (sgc.getMinigame().loseCoins > 1) {
 	                            TitleManager.sendTitle(p, 20, 20, 30, "§cYou died!",
 	                                    "§eYou received §6" + sgc.getMinigame().loseCoins
 	                                            + " Coins!");
 	                            TitleManager.sendActionTitle(p,
 	                                    "§6+" + sgc.getMinigame().loseCoins + " Coins");
 	                        } else {
 	                            TitleManager.sendTitle(p, 20, 20, 30, "§cYou died!",
 	                                    "§eYou received §6" + sgc.getMinigame().loseCoins
 	                                            + " Coin!");
 	                            TitleManager.sendActionTitle(p,
 	                                    "§6+" + sgc.getMinigame().loseCoins + " Coin");

 	                        }*/

          CoinHandler.addCoins(p.getUniqueId(), 5);

          //Game.getInstance().core.statsHandler.addDeath(p);
          //Game.getInstance().core.statsHandler.addLoss(p);

 	                        if (PlayersTeam.getPlayers().size() == 1) {
 	                            GameHandler.finish(PlayersTeam.getPlayers().get(0).getName());

 	                        } else {
 	                            Bukkit.broadcastMessage(MessageUtils.colorize(MessageUtils.MAIN_COLOR + PlayersTeam.getPlayers()
                                  .size() + MessageUtils.SECOND_COLOR + " players remain!"));
 	                        }
        }
      }
    }

  }

  @EventHandler
  public void onGameEnd(GameEndEvent e) {
    for (Player p : Bukkit.getOnlinePlayers()) {

      p.sendMessage("§b-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
      p.sendMessage("§7");
      MessageUtils.sendCenteredMessage(p, "§e§lGame Over!");
      p.sendMessage("§7");
      MessageUtils.sendCenteredMessage(p, "§7§lWinner: §b§o" + e.getWinner());
      p.sendMessage("§d");
      p.sendMessage("§7");
      p.sendMessage("§b-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
    }

    Player winner = Bukkit.getPlayer(e.getWinner());

    //Game.getInstance().core.statsHandler.addWin(winner);
    //Game.getInstance().core.coinHandler.addCoins(winner.getName(), 20);

    PlayersTeam.removePlayer(winner);
    SpectatorsTeam.addSpectator(winner);

    winner.getInventory().clear();
    winner.getActivePotionEffects().clear();
    winner.setExp(0);
  }

  public static List<Location> chests = new ArrayList<Location>();

  @EventHandler
  public void onChest(PlayerInteractEvent e) {
    Player p = e.getPlayer();

    //Bukkit.broadcastMessage("playing:" + (Game.players.isPlaying(p)) + " state:" + (Game.gameHandler.getState() == GameState.INGAME) + " action:" + (e.getAction() == Action.RIGHT_CLICK_BLOCK) + " block:" + (e.getClickedBlock().getType() == Material.CHEST));

    if ((PlayersTeam.isPlaying(p)) && (GameHandler.getState() == GameState.INGAME)
        && (e.getAction() == Action.RIGHT_CLICK_BLOCK) && (e.getClickedBlock().getType() == Material.CHEST)) {
      Block chest = e.getClickedBlock().getLocation().getBlock();
      e.setCancelled(false);
      if ((chest.getState() instanceof Chest)) {
        Chest c = (Chest) chest.getState();
        if (!chests.contains(chest.getLocation())) {
          chests.add(c.getLocation());
          Inventory i = fillChest();

          c.getInventory().setContents(i.getContents());
        }
      }
    }
  }

  private Inventory fillChest() {
    Inventory inv = Bukkit.createInventory(null, 27);
    for (int i = 0; i < inv.getSize() / 6; i++) {

      //CHANGE LOOTNAMES TO STATIC VARIABLE

      List<String> lootnames = new LinkedList<String>();
      lootnames.add("BOW");
      lootnames.add("STONE_SWORD");
      lootnames.add("WOOD_SWORD");
      lootnames.add("STONE_AXE");

      lootnames.add("ARROW");

      lootnames.add("LEATHER_HELMET");
      lootnames.add("LEATHER_CHESTPLATE");
      lootnames.add("LEATHER_LEGGINGS");
      lootnames.add("LEATHER_BOOTS");
      lootnames.add("IRON_HELMET");
      lootnames.add("IRON_CHESTPLATE");
      lootnames.add("IRON_LEGGINGS");
      lootnames.add("IRON_BOOTS");
      lootnames.add("GOLD_HELMET");
      lootnames.add("GOLD_CHESTPLATE");
      lootnames.add("GOLD_LEGGINGS");
      lootnames.add("GOLD_BOOTS");
      lootnames.add("CHAINMAIL_HELMET");
      lootnames.add("CHAINMAIL_CHESTPLATE");
      lootnames.add("CHAINMAIL_LEGGINGS");
      lootnames.add("CHAINMAIL_BOOTS");

      lootnames.add("APPLE");
      lootnames.add("BREAD");
      lootnames.add("GOLDEN_APPLE");
      lootnames.add("COOKED_FISH");
      lootnames.add("COOKED_CHICKEN");
      lootnames.add("COOKIE");
      lootnames.add("MELON");
      lootnames.add("COOKED_BEEF");
      lootnames.add("COOKED_CHICKEN");
      lootnames.add("ROTTEN_FLESH");
      lootnames.add("CARROT_ITEM");
      lootnames.add("BAKED_POTATO");

      lootnames.add("DIAMOND");
      lootnames.add("IRON_INGOT");
      lootnames.add("GOLD_INGOT");
      lootnames.add("STICK");

      Random r = new Random();
      int item = r.nextInt(lootnames.size());

      ItemStack is = new ItemStack(Material.getMaterial(((String) lootnames.get(item)).toUpperCase()));

      int amount = r.nextInt(4);
      String iname = ((String) lootnames.get(item)).toUpperCase();
      if ((iname.contains("HELMET")) || (iname.contains("CHESTPLATE")) || (iname.contains("LEGGINGS"))
          || (iname.contains("BOOTS")) || (iname.contains("SWORD")) || (iname.contains("AXE"))
          || (iname.contains("BOW"))) {
        is.setAmount(1);
      } else if (amount == 0) {
        is.setAmount(amount + 1);
      } else {
        is.setAmount(amount);
      }
      int slot = r.nextInt(inv.getSize());
      //Bukkit.broadcastMessage(is.getType().toString());
      inv.setItem(slot, is);
    }
    return inv;
  }

}