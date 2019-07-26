package net.timelegacy.tlhungergames.events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.timelegacy.tlminigame.enums.GameState;
import net.timelegacy.tlminigame.handler.GameHandler;
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

      List<ItemStack> loot = new LinkedList<>();

      //WEAPONS
      loot.add(new ItemStack(Material.BOW));
      loot.add(new ItemStack(Material.STONE_SWORD));
      loot.add(new ItemStack(Material.WOODEN_SWORD));
      loot.add(new ItemStack(Material.STONE_AXE));

      loot.add(new ItemStack(Material.ARROW));

      //ARMOR
      loot.add(new ItemStack(Material.LEATHER_HELMET));
      loot.add(new ItemStack(Material.LEATHER_CHESTPLATE));
      loot.add(new ItemStack(Material.LEATHER_LEGGINGS));
      loot.add(new ItemStack(Material.LEATHER_BOOTS));

      loot.add(new ItemStack(Material.IRON_HELMET));
      loot.add(new ItemStack(Material.IRON_CHESTPLATE));
      loot.add(new ItemStack(Material.IRON_LEGGINGS));
      loot.add(new ItemStack(Material.IRON_BOOTS));

      loot.add(new ItemStack(Material.CHAINMAIL_HELMET));
      loot.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
      loot.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
      loot.add(new ItemStack(Material.CHAINMAIL_BOOTS));

      loot.add(new ItemStack(Material.GOLDEN_HELMET));
      loot.add(new ItemStack(Material.GOLDEN_CHESTPLATE));
      loot.add(new ItemStack(Material.GOLDEN_LEGGINGS));
      loot.add(new ItemStack(Material.GOLDEN_BOOTS));

      //FOOD
      loot.add(new ItemStack(Material.APPLE));
      loot.add(new ItemStack(Material.BREAD));
      loot.add(new ItemStack(Material.GOLDEN_APPLE));
      loot.add(new ItemStack(Material.COOKED_BEEF));
      loot.add(new ItemStack(Material.COOKED_CHICKEN));
      loot.add(new ItemStack(Material.COOKED_PORKCHOP));
      loot.add(new ItemStack(Material.COOKIE));
      loot.add(new ItemStack(Material.MELON_SLICE));

      loot.add(new ItemStack(Material.DIAMOND));
      loot.add(new ItemStack(Material.IRON_INGOT));
      loot.add(new ItemStack(Material.GOLD_INGOT));
      loot.add(new ItemStack(Material.STICK));

      loot.add(new ItemStack(Material.DIRT));

      Random r = new Random();
      int item = r.nextInt(loot.size());

      ItemStack is = loot.get(item);

      int amount = r.nextInt(4);
      String iname = is.getType().toString().toUpperCase();
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

    if ((GameHandler.getGame().getState() == GameState.INGAME)
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
