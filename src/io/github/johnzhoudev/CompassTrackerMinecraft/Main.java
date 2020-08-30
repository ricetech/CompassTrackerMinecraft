package io.github.johnzhoudev.CompassTrackerMinecraft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {

	public static HashMap<String, String> playerCompassStatus = new HashMap<String, String>();
	public static LinkedList<Player> listOfPlayers = new LinkedList<Player>();
	public static HashMap<String, Location> portalLocations = new HashMap<String, Location>();
	public static HashMap<String, Location> portalLocationsNether = new HashMap<String, Location>();
	
	
	//These functions are run when the plugin is enabled and disabled
    @Override
    public void onEnable() {
    	getLogger().info("TestPlugin has been enabled.");
    	
    	//Setup right click listener
    	getServer().getPluginManager().registerEvents(new MyListener(), this);
    	
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("TestPlugin has been disabled.");
    }
    
    @Override
    // Sender may be Player or ConsoleCommandSender
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	// Handle givediamond command
    	if (cmd.getName().equalsIgnoreCase("givediamond")) {
    		
    		if (sender instanceof Player) {
    			Player player = (Player) sender;
    			givePlayerDiamonds(player);
    			
    		} else if (sender instanceof ConsoleCommandSender) {
    			if (args.length == 0) {
    				sender.sendMessage("/givediamond [player] requires a player to be supplied when run from the console");
    				return false;
    			}
    			
    			Player player = getServer().getPlayer(args[0]);
    			
    			// If player not found
    			if (player == null) {
    				sender.sendMessage("Player not found");
    				return false;
    			}
    			givePlayerDiamonds(player);
    		}
    		return true;
    	}
    	
    	// Handle reset command, clears all inventories and teleports players to spawn and resets portal locations
    	if (cmd.getName().equalsIgnoreCase("reset")) {
    		
    		if (args.length > 0) {
    			return false;
    		}
    		
    		Iterator<Player> playerIterator = listOfPlayers.iterator();
    		
    		
    		Location spawnLocation = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
    		
    		
    		while (playerIterator.hasNext()) {
    			Player player = playerIterator.next();
    			
    			// Clear inventory
    			player.getInventory().clear();
    			
    			MyListener.giveCompass(player);
    			
    			player.teleport(spawnLocation);
    			
    			portalLocations.put(player.getName(), null);
    			portalLocationsNether.put(player.getName(), null);
    			
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
    	
    	// Handle start command
    	if (cmd.getName().equalsIgnoreCase("start")) {
    		
    		int time = 60;
    		
    		if (args.length > 1) {
    			return false;
    		} else if (args.length == 1) {
    			time = Integer.parseInt(args[0]);
    		}
    		
    		final int final_time = time;
    		
//    		Iterator<Player> playerIterator = listOfPlayers.iterator();
//    		
//    		// Create scoreboard from world
//    		Scoreboard scoreboard = getServer().getScoreboardManager().getMainScoreboard();
//    		Objective o = scoreboard.registerNewObjective("Countdown:", "dummy", "CountDown:");
//    		o.setDisplayName("");
//    		o.setDisplaySlot(DisplaySlot.SIDEBAR);
//    		
//    		while (playerIterator.hasNext()) {
//    			Player player = playerIterator.next();
//    			player.setScoreboard(scoreboard);
//    		}
    		
    		Bukkit.broadcastMessage("Started Speedrunner Countdown");
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            Plugin plugin = this;
            scheduler.runTaskTimer(this, new Runnable() {
            	            	
            	int count_time = final_time;
            	
                @Override
                public void run() {
                	
                	if (count_time == 0) {
                		Bukkit.broadcastMessage("Manhunt Begins");
                		
//                		Iterator<Player> playerIterator = listOfPlayers.iterator();
//                		
//                		// Clear scoreboard
//                		while (playerIterator.hasNext()) {
//                			Player player = playerIterator.next();
//                			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
//                		}
//                		
                		getServer().getScheduler().cancelTasks(plugin);
//                		
                	} else if (count_time == 45){
                		Bukkit.broadcastMessage("45 seconds remain");
                	} else if ((count_time % 30) == 0) {
                		Bukkit.broadcastMessage(Integer.toString(count_time) + " seconds remain");
                	} else if (count_time == 15){
                		Bukkit.broadcastMessage("15 seconds remain");
                	} else if (count_time < 11){
                		Bukkit.broadcastMessage(Integer.toString(count_time) + " seconds remain");
                	}
                	
                	count_time--;
                    
                }
                
            }, 0L, 20L);
            
            return true;
    		
    	}
    	
    	// Handle track command
    	if (cmd.getName().equalsIgnoreCase("track")) {
    		
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
    			String currentlyTracking = playerCompassStatus.get(player.getName());
    			if (currentlyTracking == null) {
    				switchTrack(player, null);
    			} else {
    				switchTrack(player, Bukkit.getPlayer(currentlyTracking));
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
    			playerCompassStatus.put(player.getName(), "block");
    			
    			return true;
    		}
    		
    		else { //track player
    			String trackPlayerName = args[0];
    			
    			// If player not found
    			if (Bukkit.getPlayer(trackPlayerName) == null) {
    				player.sendMessage(trackPlayerName + " not found");
    			}
    			
    			// Track player
    			track(player, Bukkit.getPlayer(trackPlayerName));
    			
    			return true;
    		}
    	}
         
    	// Return false if no matching commands.
    	return false; 
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
    				
    				player.sendMessage("Tracking: " + target.getName() + " | Y: " + Integer.toString(portalLocation.getBlockY()));
    				    				
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
    				
    				player.sendMessage("Tracking: " + target.getName() + " | Y: " + Integer.toString(portalLocation.getBlockY()));
    				    	
    			}
    			
    			playerCompassStatus.put(player.getName(), target.getName());
    			return;
    		}
    		
    		// Get locations if both in same world
    		Location targetLocation = target.getLocation();
    		Location playerLocation = player.getLocation();
    	    
    	    trackLocation(player, targetLocation);
    		
    		player.sendMessage("Tracking: " + target.getName() + " | Y: " + Integer.toString(targetLocation.getBlockY()));
    		
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
