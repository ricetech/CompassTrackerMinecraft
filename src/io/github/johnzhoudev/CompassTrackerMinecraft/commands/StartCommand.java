package io.github.johnzhoudev.CompassTrackerMinecraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StartCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public StartCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
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

        new CountdownTask(final_time).runTaskTimer(plugin, 0L, 20L);

        return true;
    }

    private static class CountdownTask extends BukkitRunnable {

        private int count_time;

        public CountdownTask(int count_time) {
            this.count_time = count_time;
        }

        @Override
        public void run() {
            if (count_time <= 0) {
                Bukkit.broadcastMessage("Manhunt Begins");

//                		Iterator<Player> playerIterator = listOfPlayers.iterator();
//
//                		// Clear scoreboard
//                		while (playerIterator.hasNext()) {
//                			Player player = playerIterator.next();
//                			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
//                		}
//
                this.cancel();
//
            } else if (count_time == 45) {
                Bukkit.broadcastMessage("45 seconds remain");
            } else if ((count_time % 30) == 0) {
                Bukkit.broadcastMessage(count_time + " seconds remain");
            } else if (count_time == 15) {
                Bukkit.broadcastMessage("15 seconds remain");
            } else if (count_time < 11) {
                Bukkit.broadcastMessage(count_time + " seconds remain");
            }

            count_time--;
        }
    }
}
