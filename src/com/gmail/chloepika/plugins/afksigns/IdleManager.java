package com.gmail.chloepika.plugins.afksigns;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IdleManager
{
	private final static AFKSigns plugin = (AFKSigns) Bukkit.getServer().getPluginManager().getPlugin("AFK Signs");

	private static HashMap<String, Integer> playerTaskMap = new HashMap<String, Integer>();

	private static long idleTime = (plugin.getConfig().getLong("afk-time", 5) * 60 * 20);

	public static void startTrack(Player player)
	{
		if (!isTracking(player))
		{
			String playerName = player.getName();
			int taskId = callScheduler(player);
			playerTaskMap.put(playerName, taskId);
		}
	}

	public static void refresh(Player player)
	{
		if (isTracking(player))
		{
			String playerName = player.getName();
			int taskId = playerTaskMap.get(playerName);
			Bukkit.getServer().getScheduler().cancelTask(taskId);
			taskId = callScheduler(player);
			playerTaskMap.put(playerName, taskId);
		}
	}

	public static void stopTrack(Player player)
	{
		if (isTracking(player))
		{
			String playerName = player.getName();
			int taskId = playerTaskMap.get(playerName);
			Bukkit.getServer().getScheduler().cancelTask(taskId);
			playerTaskMap.remove(playerName);
		}
	}

	public static boolean isTracking(Player player)
	{
		String playerName = player.getName();
		return playerTaskMap.containsKey(playerName);
	}

	private static int callScheduler(Player player)
	{
		return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new IdleToAFK(player), idleTime);
	}
}