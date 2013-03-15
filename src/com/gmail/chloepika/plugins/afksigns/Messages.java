package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.ChatColor;

public enum Messages
{
	invalidmessage(ChatColor.RED + "You have entered an invalid "),
	playeronly(ChatColor.RED + "This command can only be run by a player."),
	notarget(ChatColor.RED + "Please include more arguments!"),
	manytarget(ChatColor.RED + "Too many arguments!"),
	invalidtarget(ChatColor.RED + "Invalid arguments!"),
	noperm(ChatColor.RED + "Sorry, I'm afraid you do not have the permission to do so.");

	private String message;

	private Messages(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}