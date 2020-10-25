package io.github.johnzhoudev.CompassTrackerMinecraft.commands;

import io.github.johnzhoudev.CompassTrackerMinecraft.Main;
import io.github.johnzhoudev.CompassTrackerMinecraft.MyListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class ResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length > 0) {
            return false;
        }

        Iterator<Player> playerIterator = Main.listOfPlayers.iterator();


        Location spawnLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();


        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();

            // Clear inventory
            player.getInventory().clear();

            MyListener.giveCompass(player);

            player.teleport(spawnLocation);

            Main.portalLocations.put(player.getName(), null);
            Main.portalLocationsNether.put(player.getName(), null);

            //reset hunger and health
            player.setHealth(20);
            player.setFoodLevel(20);


        }

        // Set time to day
        World normalWorld = Bukkit.getServer().getWorlds().get(0);
        normalWorld.setTime(0);

        // Kill all dropped entities
        for (Entity en : normalWorld.getEntities()) {
            if (en instanceof Item) {
                en.remove();
            }
        }

        return true;

    }

}
