package io.github.johnzhoudev.CompassTrackerMinecraft;

import io.github.johnzhoudev.CompassTrackerMinecraft.commands.ResetCommand;
import io.github.johnzhoudev.CompassTrackerMinecraft.commands.StartCommand;
import io.github.johnzhoudev.CompassTrackerMinecraft.commands.TrackCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedList;

public class Main extends JavaPlugin {

    public static HashMap<String, String> playerCompassStatus = new HashMap<String, String>();
    public static LinkedList<Player> listOfPlayers = new LinkedList<Player>();
    public static HashMap<String, Location> portalLocations = new HashMap<String, Location>();
    public static HashMap<String, Location> portalLocationsNether = new HashMap<String, Location>();


    //These functions are run when the plugin is enabled and disabled
    @Override
    public void onEnable() {
        getLogger().info("CompassTrackerMinecraft has been enabled.");

        //Setup right click listener
        getServer().getPluginManager().registerEvents(new MyListener(), this);

        //Register all commands
        this.getCommand("reset").setExecutor(new ResetCommand());
        this.getCommand("start").setExecutor(new StartCommand(this));
        this.getCommand("track").setExecutor(new TrackCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("CompassTrackerMinecraft has been disabled.");
    }


    // givePlayerDiamonds(Player player) gives a stack of diamonds to player
    public void givePlayerDiamonds(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack itemstack = new ItemStack(Material.DIAMOND, 64);

        inventory.addItem(itemstack); // Adds a stack of diamonds to the player's inventory
        player.sendMessage("Here are some diamonds");
    }

    //Switches target to next player in listOfPlayers linked list
    public static void switchTrack(Player player, Player currentTarget) {

        if (currentTarget == null) {
            track(player, listOfPlayers.element());
            return;
        }

        // Get index of current target
        int currentTargetIndex = listOfPlayers.indexOf(currentTarget);

        Player playerToTrack;

        if (listOfPlayers.size() == 0) {
            player.sendMessage("No players to track...?");
            return;
        }


        // Player not found or last entity
        if ((currentTargetIndex == -1) || (currentTargetIndex == (listOfPlayers.size() - 1))) {
            // track first element
            playerToTrack = listOfPlayers.element();
        } else {
            playerToTrack = listOfPlayers.get(currentTargetIndex + 1);
        }

        track(player, playerToTrack);

    }

    // Makes player track target player
    // Requires: player and target are both valid players online and are not null
    //			compass is in player inventory
    public static void track(Player player, Player target) {

        // If there is no target specified
        if (target == null) {
            return;
        } else {

            // Get world of current player and verify if it's the same world
            World playerWorld = player.getWorld();
            World targetWorld = target.getWorld();

            // Can't track in the end
            if (playerWorld.getEnvironment() == World.Environment.THE_END) {
                player.sendMessage("Tracking Error: User in THE END");
                return;
            } else if (targetWorld.getEnvironment() == World.Environment.THE_END) {
                player.sendMessage("Tracking Error: Target in THE END");
                return;
            }

            // Player and target are in nether / overworld, separate
            if (playerWorld != targetWorld) {

                // If player is in the overworld and target is in the nether
                if (playerWorld.getEnvironment() == World.Environment.NORMAL) {
                    Location portalLocation = portalLocations.get(target.getName());

                    if (portalLocation == null) {
                        player.sendMessage("Woah that's a glitch, notify");
                        return;
                    }

                    trackLocation(player, portalLocation);

                    player.sendMessage("Tracking: " + target.getName() + " | Y: " + portalLocation.getBlockY());

                    //else player is in nether, tracks nether portal of opponent or your own if they haven't been to nether yet
                } else {
                    Location portalLocation = portalLocationsNether.get(target.getName());

                    // Player hasn't been to nether yet, track your own nether portal
                    if (portalLocation == null) {

                        portalLocation = portalLocationsNether.get((player.getName()));

                        if (portalLocation == null) {
                            player.sendMessage("Error at 226 of Main.java. Pls notify operator.");
                            return;
                        }

                    }

                    trackLocation(player, portalLocation);

                    player.sendMessage("Tracking: " + target.getName() + " | Y: " + portalLocation.getBlockY());

                }

                playerCompassStatus.put(player.getName(), target.getName());
                return;
            }

            // Get locations if both in same world
            Location targetLocation = target.getLocation();
            Location playerLocation = player.getLocation();

            trackLocation(player, targetLocation);

            player.sendMessage("Tracking: " + target.getName() + " | Y: " + targetLocation.getBlockY());

            playerCompassStatus.put(player.getName(), target.getName());

        }

        return;
    }

    public static void trackLocation(Player player, Location targetLocation) {
        PlayerInventory inventory = player.getInventory();

        int compassPosition = inventory.first(Material.COMPASS);
        if (compassPosition == -1) {
            player.sendMessage(player.getName() + " used track method without compass in inventory - Glitch, notify admin");
            return;
        }

        //get compass metadata
        ItemStack compass = inventory.getItem(compassPosition);
        ItemMeta compassMeta = compass.getItemMeta();

        if (compassMeta instanceof CompassMeta) {
            CompassMeta trackerCompassMeta = (CompassMeta) compassMeta;

            if (trackerCompassMeta != null) {
                trackerCompassMeta.setLodestoneTracked(false);
                trackerCompassMeta.setLodestone(targetLocation);

                compass.setItemMeta(trackerCompassMeta);
            }

        }

        return;
    }
}
