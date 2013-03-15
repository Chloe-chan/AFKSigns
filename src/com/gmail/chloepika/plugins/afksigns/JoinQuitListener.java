package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener
{

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		IdleManager.startTrack(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		IdleManager.stopTrack(event.getPlayer());
		AFKManager.cancelAFK(event.getPlayer(), false);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKicked(PlayerKickEvent event)
	{
		IdleManager.stopTrack(event.getPlayer());
		AFKManager.cancelAFK(event.getPlayer(), false);
	}
}