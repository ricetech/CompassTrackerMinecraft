package io.github.johnzhoudev.CompassTrackerMinecraft.commands;

import io.github.johnzhoudev.CompassTrackerMinecraft.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class TrackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Check if player
        if (!(sender instanceof Player)) {
            sender.sendMessage("/track [player or 'block'] can only be called by a player!");
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        // If player does not have a compass, give them one and continue
        if (!((inventory.contains(Material.COMPASS)) || (inventory.getItemInOffHand().getType() == Material.COMPASS))) {
            Bukkit.broadcastMessage("Given " + player.getName() + " a tracking compass.");

            ItemStack compass = new ItemStack(Material.COMPASS, 1);
            inventory.addItem(compass);

        }

        // If no arguments given, toggle track switch
        if (args.length <= 0) {
            String currentlyTracking = Main.playerCompassStatus.get(player.getName());
            if (currentlyTracking == null) {
                Main.switchTrack(player, null);
            } else {
                Main.switchTrack(player, Bukkit.getPlayer(currentlyTracking));
            }

            return true;
        }

        // if less
        else if (args.length > 1) {
            sender.sendMessage("/track [player or 'block'] only accepts 1 or 0 arguments!");
            return true;
        }

        // turn to block mode
        else if (args[0].equalsIgnoreCase("block")) {
            sender.sendMessage("Block track mode activated - right click with compass to track block");
            Main.playerCompassStatus.put(player.getName(), "block");

            return true;
        } else { //track player
            String trackPlayerName = args[0];

            // If player not found
            if (Bukkit.getPlayer(trackPlayerName) == null) {
                player.sendMessage(trackPlayerName + " not found");
            }

            // Track player
            Main.track(player, Bukkit.getPlayer(trackPlayerName));

            return true;
        }
    }
}
