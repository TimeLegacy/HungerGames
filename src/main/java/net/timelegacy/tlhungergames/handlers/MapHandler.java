package net.timelegacy.tlhungergames.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.timelegacy.tlhungergames.TLHungerGames;
import net.timelegacy.tlhungergames.datatype.Map;
import net.timelegacy.tlminigame.TLMinigame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class MapHandler {

  private static List<Map> maps = new ArrayList<>();
  private static Map currentMap;

  public static void addMap(Map map) {
    maps.add(map);
  }

  public static Map getCurrentMap() {
    return currentMap;
  }

  public static void setRandomMap() {
    Random r = new Random();
    Map m = (Map) maps.get(r.nextInt(maps.size()));
    if ((m == currentMap && (maps.size() != 1))) {
      setRandomMap();
    } else if (maps.size() == 1) {
      m = maps.get(0);
    }
    currentMap = m;

    TLMinigame.minigameServer.setMap(m.getMapName());

  }

  public static YamlConfiguration getMapConfig(String mapName) {
    File dataFile = new File(TLHungerGames.getPlugin().getDataFolder(), mapName + ".yml");
    YamlConfiguration config = null;

    if (!dataFile.exists()) {
      try {
        if (!dataFile.getParentFile().exists()) {
          dataFile.getParentFile().mkdirs();
        }
        dataFile.createNewFile();
        config = YamlConfiguration.loadConfiguration(dataFile);
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

  public static List<Location> getSpawns(String mapName) {
    List<Location> spawns = new ArrayList<>();

    for (String loc : getMapConfig(mapName).getStringList("spawns")) {
      double x = Double.parseDouble(loc.split(",")[0]);
      double y = Double.parseDouble(loc.split(",")[1]);
      double z = Double.parseDouble(loc.split(",")[2]);

      Location spawn = new Location(Bukkit.getWorld(mapName), x, y, z);

      Location center = getSingleLocation(mapName, "center");
      Vector dirBetweenLocations = center.toVector().subtract(spawn.toVector());
      spawn.setDirection(dirBetweenLocations);

      spawns.add(spawn);
    }

    return spawns;
  }

  public static Location getSingleLocation(String mapName, String path) {

    String center = getMapConfig(mapName).getString(path);
    double x = Double.parseDouble(center.split(",")[0]);
    double y = Double.parseDouble(center.split(",")[1]);
    double z = Double.parseDouble(center.split(",")[2]);

    return new Location(Bukkit.getWorld(mapName), x, y, z);
  }

}
