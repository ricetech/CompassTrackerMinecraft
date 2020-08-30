CompassTrackerMinecraft (Spigot 1.16.2 server plugin)

Allows players to track each other in minecraft with lodestone compasses!
Great for playing minecraft manhunt!

**The plugin .jar file can be found in the "target" folder!**

Commands:

/track [playerName or "block"]
- Makes compass point to the player with the matching player name, or toggles through tracking players if no playerName is given.
- ** IMPORTANT ** the compass tracks the player's location at the time of the command but doesn't update when the tracked player moves. To update the compass,
  right click with the compass in your primary or off hand.
- If "/track block" is run, the compass will switch to block mode and users can right click blocks that the compass will track.
- If no compass is held in the inventory, a compass will be given to the player.
- If players are in a different dimension, the compass will point to the nether portal they used.

/start [time in seconds] 
- Starts a head-start countdown for the speedrunners that displays the time left every 30 seconds, then at the 45, 30, 15, 10, 9, ..., 1 second mark.

/reset
- Sets time to day
- Teleports all players to world spawn
- Resets health and hunger
- Clears all inventories and item drops in the world
