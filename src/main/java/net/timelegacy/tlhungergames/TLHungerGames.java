package net.timelegacy.tlhungergames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.timelegacy.tlhungergames.events.ChestListener;
import net.timelegacy.tlhungergames.events.GameListener;
import net.timelegacy.tlminigame.enums.GameStatus;
import net.timelegacy.tlminigame.game.Arena;
import net.timelegacy.tlminigame.game.ArenaSettings;
import net.timelegacy.tlminigame.game.Game;
import net.timelegacy.tlminigame.game.GameSettings;
import net.timelegacy.tlminigame.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TLHungerGames extends JavaPlugin implements Listener {

  private static TLHungerGames plugin;

  public static YamlConfiguration getMapConfig(String configName) {
    File dataFile = new File(plugin.getDataFolder(), configName + ".yml");
    YamlConfiguration config = null;

    if (!dataFile.exists()) {
      try {
        if (!dataFile.getParentFile().exists()) {
          dataFile.getParentFile().mkdirs();
        }
        dataFile.createNewFile();
        config = YamlConfiguration.loadConfiguration(dataFile);
        config.set("spawnCount", 0);
        config.set("worldName", configName);
        config.save(dataFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (config == null) {
      config = YamlConfiguration.loadConfiguration(dataFile);
    }
    return config;
  }

  public static Location spectatorSpawn(String configName) {
    YamlConfiguration config = getMapConfig(configName);
    String worldName = config.getString("worldName");

    World world = Bukkit.getWorld(worldName);
    return new Location(world, config.getDouble("center.x"),
        config.getDouble("center.y"), config.getDouble("center.z"));
  }

  public static List<Location> spawns(String configName) {
    YamlConfiguration config = getMapConfig(configName);
    List<Location> temp = new ArrayList<>();
    int amount = config.getInt("spawnCount");
    String worldName = config.getString("worldName");

    World world = Bukkit.getWorld(worldName);
    if (config.getKeys(false).contains("spawns")) {
      for (int i = 0; i < amount; i++) {
        double x = config.getDouble("spawns." + i + ".x");

        double y = config.getDouble("spawns." + i + ".y");

        double z = config.getDouble("spawns." + i + ".z");

        Location spawn = new Location(world, x, y, z);
        Location lookloc = new Location(world, config.getDouble("center.x"),
            config.getDouble("center.y"), config.getDouble("center.z"));
        Vector dirBetweenLocations = lookloc.toVector().subtract(spawn.toVector());
        spawn.setDirection(dirBetweenLocations);

        temp.add(spawn);
      }
    }
    return temp;
  }

  @Override
  public void onEnable() {
    Bukkit.getServer().getPluginManager().registerEvents(new ChestListener(), this);
    Bukkit.getServer().getPluginManager().registerEvents(new GameListener(), this);

    plugin = this;

    Arena arena = new Arena("Highway");
    Game hungerGames = new Game("HungerGames", arena, GameStatus.WAITING, this);

    GameSettings gSettings = hungerGames.getGameSettings(); //Set a variable for accessibility.
    gSettings.shouldUseTeams(
        false); //Since Spleef is a solo minigame, we're disabling automatic features related to teams.gSettings.setMaximumPlayers(8);
    gSettings.setMinimumPlayers(2);
    gSettings.setAutomaticCountdown(
        true); //When minimum player requirements are filled, it will start counting down before starting the game.
    gSettings.setCountdownTime(20); //Set the countdown time in seconds.
    gSettings.setUsesBungee(true);
    gSettings.shouldLeavePlayerOnDisconnect(
        true); //Kick the player from the game when they disconnect from the server. If set to false, you can allow players to resume playing their game, but we don't need this feature here.
    gSettings.setDisableVanillaDeathMessages(true);
    gSettings.setResetWorlds(true);

    ArenaSettings aSettings = arena.getArenaSettings(); //Get a variable for convenience.
    aSettings.setCanBuild(false);
    aSettings.setCanDestroy(
        false); //While this is a Spleef minigame, we don't need players to be able to destroy blocks until the game starts. Properties can be changed at any time.
    aSettings.setCanPvP(true);
    aSettings.setAllowPlayerInvincibility(false);
    aSettings.setAllowDurabilityChange(true);
    aSettings.setAllowFoodLevelChange(true);
    aSettings.setAllowMobSpawn(false);
    aSettings.setAllowBlockDrop(true);
    aSettings.setAllowItemDrop(true);
    aSettings.setAllowInventoryChange(true);
    aSettings.setAllowTimeChange(false);
    aSettings.setAllowWeatherChange(false);

    for (Location location : spawns("highway")) {
      hungerGames.addSpawn(location);
    }

    Bukkit.getWorld("world").setPVP(false);

    gSettings.setMaximumPlayers(spawns("highway").size());

    arena.setLobbySpawn(new Location(Bukkit.getWorld("world"), 1402.5, 15, 21.5)); //Used before the game has started.

    arena.setSpectatorSpawn(spectatorSpawn("highway")); //Used for spectators

    GameManager.registerGame(hungerGames);
  }
}