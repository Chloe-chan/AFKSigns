package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.entity.Player;

public enum AFKPerm
{
	all("afksigns.*"),
	message("afksigns.message.*"),
	messagePersonal("afksigns.message.personal"),
	messageOthers("afksigns.message.others"),
	god("afksigns.god.*"),
	godPersonal("afksigns.god.personal"),
	godOthers("afksigns.god.others"),
	notification("afksigns.update-notification");

	private String perm;

	private AFKPerm(String perm)
	{
		this.perm = perm;
	}

	public String getPerm()
	{
		return perm;
	}

	public static boolean messagePersonal(Player player)
	{
		return (player.hasPermission(all.getPerm()) || 
				player.hasPermission(message.getPerm()) || 
				player.hasPermission(messagePersonal.getPerm()));
	}

	public static boolean messageOthers(Player player)
	{
		return (player.hasPermission(all.getPerm()) || 
				player.hasPermission(message.getPerm()) || 
				player.hasPermission(messageOthers.getPerm()));
	}

	public static boolean godPersonal(Player player)
	{
		return (player.hasPermission(all.getPerm()) || 
				player.hasPermission(god.getPerm()) || 
				player.hasPermission(godPersonal.getPerm()));
	}

	public static boolean godOthers(Player player)
	{
		return (player.hasPermission(all.getPerm()) || 
				player.hasPermission(god.getPerm()) || 
				player.hasPermission(godOthers.getPerm()));
	}

	public static boolean notification(Player player)
	{
		return (player.hasPermission(all.getPerm()) || 
				player.hasPermission(notification.getPerm()));
	}
}