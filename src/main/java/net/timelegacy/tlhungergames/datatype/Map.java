package net.timelegacy.tlhungergames.datatype;

import java.util.List;
import net.timelegacy.tlhungergames.handlers.MapHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;

public class Map {

  private List<Location> spawns;
  private String mapName;

  public Map(String mapName) {
    this.mapName = mapName;

    if (Bukkit.getWorld(mapName) == null) {
      Bukkit.createWorld(new WorldCreator(mapName));
    }

    this.spawns = MapHandler.getSpawns(mapName);
  }

  public String getMapName() {
    return mapName;
  }

  public List<Location> getSpawns() {
    return spawns;
  }
}
