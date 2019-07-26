package net.timelegacy.tlhungergames;

import net.timelegacy.tlhungergames.datatype.Map;
import net.timelegacy.tlhungergames.events.ChestListener;
import net.timelegacy.tlhungergames.events.GameListener;
import net.timelegacy.tlhungergames.handlers.MapHandler;
import net.timelegacy.tlminigame.datatype.Game;
import net.timelegacy.tlminigame.handler.GameHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class TLHungerGames extends JavaPlugin implements Listener {

  private static TLHungerGames plugin;

  public static Game hungerGames;

  public static TLHungerGames getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {
    Bukkit.getServer().getPluginManager().registerEvents(new ChestListener(), this);
    Bukkit.getServer().getPluginManager().registerEvents(new GameListener(), this);

    plugin = this;

    hungerGames = new Game("HUNGERGAMES");

    MapHandler.addMap(new Map("Highway"));
    MapHandler.addMap(new Map("Breeze2"));

    MapHandler.setRandomMap();

    GameHandler.setGame(hungerGames);
  }
}