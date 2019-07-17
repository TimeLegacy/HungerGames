package net.timelegacy.tlhungergames.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.timelegacy.tlminigame.enums.GameStatus;
import net.timelegacy.tlminigame.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestListener implements Listener {

  public static List<Location> chests = new ArrayList<Location>();

  private Inventory fillChest() {
    Inventory inv = Bukkit.createInventory(null, 27);
    for (int i = 0; i < inv.getSize() / 6; i++) {

      //CHANGE LOOTNAMES TO STATIC VARIABLE

      List<String> lootnames = new LinkedList<>();
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

  @EventHandler
  public void onChest(PlayerInteractEvent e) {
    Player p = e.getPlayer();

    //Bukkit.broadcastMessage("playing:" + (Game.players.isPlaying(p)) + " state:" + (Game.gameHandler.getState() == GameState.INGAME) + " action:" + (e.getAction() == Action.RIGHT_CLICK_BLOCK) + " block:" + (e.getClickedBlock().getType() == Material.CHEST));

    if ((GameManager.getGame("HungerGames").getGameStatus() == GameStatus.INGAME)
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

}
