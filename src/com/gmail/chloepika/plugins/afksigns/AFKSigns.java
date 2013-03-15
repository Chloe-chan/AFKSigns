package com.gmail.chloepika.plugins.afksigns;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AFKSigns extends JavaPlugin
{
	public static final String
	pluginPrefix = (ChatColor.BLUE + "[" + ChatColor.GOLD + "AFK Signs" + ChatColor.BLUE + "] " + ChatColor.RESET),
	pluginPrefixNC = ("[AFK Signs] ");

	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new AFKListener(), this);
		getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);
		AFKManager.readNewPlayerMessage();
		AFKManager.readPlayerGod();
		saveDefaultConfig();
	}

	public void onDisable()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (AFKManager.isPlayerAFK(player))
			{
				AFKManager.cancelAFK(player, false);
			}
		}
		if (getConfig().getBoolean("save-message"))
		{
			AFKManager.savePlayerMessage();
		}
		AFKManager.savePlayerGod();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (command.getName().equalsIgnoreCase("afk"))
		{
			if (args.length == 0)
			{
				if (sender instanceof Player)
				{
					Player player = (Player) sender;
					AFKManager.goAFK(player);
					return true;
				} else
				{
					sender.sendMessage(Messages.playeronly.getMessage());
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("where"))
			{
				if (args.length == 2)
				{
					Player player = Bukkit.getServer().getPlayer(args[1]);
					if (player != null)
					{
						if (AFKManager.isPlayerAFK(player))
						{
							//
						} else
						{
							sender.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.RED + " is not AFK.");
						}
						return true;
					} else
					{
						sender.sendMessage("");
					}
				}
			}
		}
		return false;
	}

	public String[] help(CommandSender sender)
	{
		List<String> help = new ArrayList<String>();
		help.add(ChatColor.RED + "==========" + ChatColor.AQUA + "AFK Signs Help" + ChatColor.RED + "==========");
		help.add(helpMaker("afk help", "Displays this help page."));
		if (sender instanceof Player)
		{
			@SuppressWarnings("unused")
			Player player = (Player) sender;
		} else
		{
			//			help.add(helpMaker("health get <player>", "Displays the health of the player."));
		}
		help.add(ChatColor.RED + "=================================");
		String[] helpArray = help.toArray(new String[help.size()]);
		return helpArray;
	}

	public String[] error(String reason, String usage)
	{
		String[] string = {
				(ChatColor.RED + reason),
				(ChatColor.RED + "Correct usage:"),
				(ChatColor.AQUA + usage)};
		return string;
	}

	public String helpMaker(String command, String description)
	{
		return (ChatColor.AQUA + "/" + command + ChatColor.GOLD + " : " + ChatColor.RED + description);
	}
}