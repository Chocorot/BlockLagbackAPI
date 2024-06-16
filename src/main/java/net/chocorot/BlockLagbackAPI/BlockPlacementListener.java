package net.chocorot.BlockLagbackAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class BlockPlacementListener implements Listener {


    private final Map<Player, Queue<Long>> blockPlaced = new HashMap<>();
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (canPlace(player)) {
            event.setCancelled(true);
        }
        addPlayer(player);
    }

    public boolean canPlace(Player player) {
        long currentTime = System.currentTimeMillis();
        int TIME_WINDOW = Integer.parseInt(Settings.getString("time"));
        int REQUIRED_COUNT = Integer.parseInt(Settings.getString("count"));

        if (!blockPlaced.containsKey(player)) {
            blockPlaced.put(player, new LinkedList<>());
        }

        Queue<Long> timestamps = blockPlaced.get(player);

        // Remove timestamps that are outside the time window
        while (!timestamps.isEmpty() && currentTime - timestamps.peek() > TIME_WINDOW) {
            timestamps.poll();
        }

        // Check if there are at least REQUIRED_COUNT timestamps within the time window
        return timestamps.size() >= REQUIRED_COUNT;
    }

    public void addPlayer(Player player) {
        long currentTime = System.currentTimeMillis();

        if (!blockPlaced.containsKey(player)) {
            blockPlaced.put(player, new LinkedList<>());
        }

        Queue<Long> timestamps = blockPlaced.get(player);
        timestamps.add(currentTime);
    }

}
