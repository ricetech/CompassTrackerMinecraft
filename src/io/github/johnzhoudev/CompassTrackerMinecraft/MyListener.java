package io.github.johnzhoudev.CompassTrackerMinecraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MyListener implements Listener {

    public static void giveCompass(Player player) {
        PlayerInventory inventory = player.getInventory();

        if (inventory.contains(Material.COMPASS)) {
            return;
        }

        // Make compass with compassmeta
        ItemStack itemstack = new ItemStack(Material.COMPASS, 1);
        CompassMeta compassmeta = (CompassMeta) itemstack.getItemMeta();
        compassmeta.setLodestoneTracked(false);


        inventory.addItem(itemstack); // Adds compass to player inventory
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.broadcastMessage("Welcome to the server, " + player.getName() + "!");

        // Give player compass, if not already in inventory
        giveCompass(player);

        // Add player to list of players and compass status hashmap
        Main.listOfPlayers.add(player);
        Main.playerCompassStatus.put(player.getName(), null);
        Main.portalLocations.put(player.getName(), null);
        Main.portalLocationsNether.put(player.getName(), null);

        Bukkit.getLogger().info(player.getName() + " has been added to list of players and compass status");
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Inventory inventory = player.getInventory();

        giveCompass(player);

        //Must respawn in overworld, so change portal locations for both nether and overworld to null
        Main.portalLocations.put(player.getName(), null);
        Main.portalLocationsNether.put(player.getName(), null);

        return;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Called when a player leaves a server, removes player from list of players and hashmap
        Player player = event.getPlayer();
        Main.listOfPlayers.remove(player);
        Main.playerCompassStatus.remove(player.getName());
        Main.portalLocations.remove(player.getName());
        Main.portalLocationsNether.remove(player.getName());
        Bukkit.getLogger().info(player.getName() + " has been removed from list of players and compass status");
        return;
    }

    // Handles block mode right click, requires player to exist and the event to be a block click
    public void handleBlockModeRightClick(PlayerInteractEvent event, Player player) {
        Block block = event.getClickedBlock();
        Location blockLocation = block.getLocation();

        // Set compass to point to block
        // player.setCompassTarget(blockLocation);

        Main.trackLocation(player, blockLocation);

        player.sendMessage("Tracking: " + block.getType() + " at (X,Y,Z): " + block.getX() + " " + block.getY() + " " + block.getZ());

        return;
    }

    public void handleTrackRightClick(String compassStatus, Player player) {
        // if not currently tracking anyone, tell player to use /track
        if (compassStatus == null) {
            player.sendMessage("Use /track [player or 'block'] to begin tracking");
            return;
        } else {
            //Else re-track the currently tracked player
            Main.track(player, Bukkit.getPlayer(Main.playerCompassStatus.get(player.getName())));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        String compassStatus = Main.playerCompassStatus.get(player.getName());


        if (e.getHand() == EquipmentSlot.OFF_HAND) {

            // If compass is in offhand and block mode is on
            if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                    (player.getInventory().getItemInOffHand().getType() == Material.COMPASS) &&
                    (compassStatus == "block")) {
                handleBlockModeRightClick(e, player);
            }

            // Else if right click with compass in offhand
            else if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
                    (player.getInventory().getItemInOffHand().getType() == Material.COMPASS) &&
                    (compassStatus != "block")) {

                handleTrackRightClick(compassStatus, player);

            }

            return;
        }

        //If the action is a right click on a block and the player is holding a compass in the main hand and block mode is enabled
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                (player.getInventory().getItemInMainHand().getType() == Material.COMPASS) &&
                (compassStatus == "block")) {

            handleBlockModeRightClick(e, player);
        }

        // If right clicked with compass, update current tracked player location if block mode is disabled
        // Or track player if not in block mode
        else if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) &&
                (player.getInventory().getItemInMainHand().getType() == Material.COMPASS) &&
                (compassStatus != "block")) {
            handleTrackRightClick(compassStatus, player);

        }


        return;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();

        // If player touched in nether, return
        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            Main.portalLocationsNether.put(player.getName(), location);
            return;
        }

        // Else log location if in overworld
        Main.portalLocations.put(player.getName(), location);
        return;


        // So if the player is in a different world, the locations will be used from these arrays.

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Location destination = e.getTo();
        Player player = e.getPlayer();

        // if you teleported to the nether, set your portal location to your teleport location
        // Also give you fire resistance for 30 seconds
        if (destination.getWorld().getEnvironment() == World.Environment.NETHER) {
            Main.portalLocationsNether.put(player.getName(), destination);
            PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300, 1);
            player.addPotionEffect(fireResistance);
        }

    }
}


