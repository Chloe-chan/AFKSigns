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
		getServer().getPluginManager().registerEvents(new AFKGodListener(), this);
		getServer().getPluginManager().registerEvents(new AFKSignProtect(), this);
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
					if (!AFKManager.isPlayerAFK(player))
					{
						AFKManager.goAFK(player, true);
						return true;
					} else
					{
						AFKManager.cancelAFK(player, true);
						return true;
					}
				} else
				{
					sender.sendMessage(Messages.playeronly.getMessage());
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("messages") || args[0].equalsIgnoreCase("message") || args[0].equalsIgnoreCase("m"))
			{
				if (args.length < 2)
				{
					sender.sendMessage(error(Messages.notarget.getMessage(), "/afk messages [get/set/delete] <args>"));
					return true;
				}
				if (args[1].equalsIgnoreCase("others") || args[1].equalsIgnoreCase("other") || args[1].equalsIgnoreCase("o"))
				{
					// afk m o p args args2
					if (args.length < 4)
					{
						sender.sendMessage(error(Messages.notarget.getMessage(), "/afk messages others <player> [get/set/delete] <args>"));
						return true;
					} else
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							if (AFKPerm.messageOthers(player))
							{
								Player target = Bukkit.getPlayer(args[2]);
								if (target != null)
								{
									if (args[3].equalsIgnoreCase("get") || args[3].equalsIgnoreCase("g"))
									{
										if (AFKManager.containsPlayerMessage(target))
										{
											String playerMessage = AFKManager.getPlayerMessage(target);
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s message : " + ChatColor.BLUE + playerMessage);
										} else
										{
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + " does not have any message stored.");
										}
										return true;
									}
									if (args[3].equalsIgnoreCase("delete") || args[3].equalsIgnoreCase("d") || args[3].equalsIgnoreCase("del"))
									{
										if (AFKManager.containsPlayerMessage(target))
										{
											AFKManager.removePlayerMessage(target);
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s message have been deleted.");
											target.sendMessage(ChatColor.RED + "Your message have been deleted.");
										} else
										{
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + " does not have any message stored.");
										}
										return true;
									}
									if (args[3].equalsIgnoreCase("set") || args[3].equalsIgnoreCase("s"))
									{
										String spacedMessage = "";
										if (args.length > 4)
										{
											for (int ran = 4; ran < args.length; ran ++)
											{
												spacedMessage = (spacedMessage + " " + args[ran]);
											}
											String message = spacedMessage.substring(1);
											AFKManager.setPlayerMessage(target, message);
											sender.sendMessage(ChatColor.RED + "You have set " + ChatColor.AQUA + target.getName() + ChatColor.RED + "'s message : " + ChatColor.BLUE + message);
											target.sendMessage(ChatColor.RED + "Your message have been changed : " + ChatColor.BLUE + message);
										} else
										{
											sender.sendMessage(error(Messages.notarget.getMessage(), "/afk messages others <player> set <message>"));
										}
										return true;
									}
									{
										sender.sendMessage(error(Messages.invalidtarget.getMessage(), "/afk messages others <player> [get/set/delete] <args>"));
										return true;
									}
								} else
								{
									sender.sendMessage(ChatColor.AQUA + args[2] + ChatColor.RED + " is not online.");
									return true;
								}
							} else
							{
								player.sendMessage(Messages.noperm.getMessage());
								return true;
							}
						} else
						{
							Player target = Bukkit.getPlayer(args[2]);
							if (target != null)
							{
								if (args[3].equalsIgnoreCase("get") || args[3].equalsIgnoreCase("g") || args[3].equalsIgnoreCase("current") || args[3].equalsIgnoreCase("curr") || args[3].equalsIgnoreCase("cur") || args[3].equalsIgnoreCase("c"))
								{
									if (AFKManager.containsPlayerMessage(target))
									{
										String playerMessage = AFKManager.getPlayerMessage(target);
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s message : " + ChatColor.BLUE + playerMessage);
									} else
									{
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + " does not have any message stored.");
									}
									return true;
								}
								if (args[3].equalsIgnoreCase("delete") || args[3].equalsIgnoreCase("d") || args[3].equalsIgnoreCase("del"))
								{
									if (AFKManager.containsPlayerMessage(target))
									{
										AFKManager.removePlayerMessage(target);
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s message have been deleted.");
										target.sendMessage(ChatColor.RED + "Your message have been deleted.");
									} else
									{
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + " does not have any message stored.");
									}
									return true;
								}
								if (args[3].equalsIgnoreCase("set") || args[3].equalsIgnoreCase("s"))
								{
									String spacedMessage = "";
									if (args.length > 4)
									{
										for (int ran = 4; ran < args.length; ran ++)
										{
											spacedMessage = (spacedMessage + " " + args[ran]);
										}
										String message = spacedMessage.substring(1);
										AFKManager.setPlayerMessage(target, message);
										sender.sendMessage(ChatColor.RED + "You have set " + ChatColor.AQUA + target.getName() + ChatColor.RED + "'s message : " + ChatColor.BLUE + message);
										target.sendMessage(ChatColor.RED + "Your message have been changed : " + ChatColor.BLUE + message);
									} else
									{
										sender.sendMessage(error(Messages.notarget.getMessage(), "/afk messages others <player> set <message>"));
									}
									return true;
								}
								{
									sender.sendMessage(error(Messages.invalidtarget.getMessage(), "/afk messages others <player> [get/set/delete] <args>"));
									return true;
								}
							}
						}
					}
				} else
				{
					if (sender instanceof Player)
					{
						// afk m args args2
						Player player = (Player) sender;
						if (AFKPerm.messagePersonal(player))
						{
							if (args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("g") || args[1].equalsIgnoreCase("current") || args[1].equalsIgnoreCase("curr") || args[3].equalsIgnoreCase("cur") || args[3].equalsIgnoreCase("c"))
							{
								if (AFKManager.containsPlayerMessage(player))
								{
									String playerMessage = AFKManager.getPlayerMessage(player);
									player.sendMessage(ChatColor.RED + "Your message : " + ChatColor.BLUE + playerMessage);
								} else
								{
									player.sendMessage(ChatColor.RED + "You do not have any message stored.");
								}
								return true;
							}
							if (args[1].equalsIgnoreCase("delete") || args[1].equalsIgnoreCase("d") || args[1].equalsIgnoreCase("del"))
							{
								if (AFKManager.containsPlayerMessage(player))
								{
									AFKManager.removePlayerMessage(player);
									player.sendMessage(ChatColor.RED + "Your message have been deleted.");
								} else
								{
									player.sendMessage(ChatColor.RED + "You do not have any message stored.");
								}
								return true;
							}
							if (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("s"))
							{
								String spacedMessage = "";
								if (args.length > 2)
								{
									for (int ran = 2; ran < args.length; ran ++)
									{
										spacedMessage = (spacedMessage + " " + args[ran]);
									}
									String message = spacedMessage.substring(1);
									AFKManager.setPlayerMessage(player, message);
									player.sendMessage(ChatColor.RED + "You have set your message : " + ChatColor.BLUE + message);
								} else
								{
									player.sendMessage(error(Messages.notarget.getMessage(), "/afk messages set <message>"));
								}
								return true;
							}
							{
								player.sendMessage(error(Messages.invalidtarget.getMessage(), "/afk messages [get/set/delete] <args>"));
								return true;
							}
						} else
						{
							player.sendMessage(Messages.noperm.getMessage());
							return true;
						}
					} else
					{
						sender.sendMessage(Messages.playeronly.getMessage());
						return true;
					}
				}
			}
			if (args[0].equalsIgnoreCase("god") || args[0].equalsIgnoreCase("g"))
			{
				//afk god on/off
				//afk god o p o/f
				if (args.length < 2)
				{
					sender.sendMessage(error(Messages.notarget.getMessage(), "/afk god [on/off]"));
					return true;
				}
				if (args[1].equalsIgnoreCase("others") || args[1].equalsIgnoreCase("other") || args[1].equalsIgnoreCase("o"))
				{
					if (args.length < 4)
					{
						sender.sendMessage(error(Messages.notarget.getMessage(), "/afk god others <player> [get/on/off]"));
						return true;
					} else
					{
						Player target = Bukkit.getServer().getPlayer(args[2]);
						if (target != null)
						{
							if (sender instanceof Player)
							{
								Player player = (Player) sender;
								if (AFKPerm.godOthers(player))
								{
									if (args[3].equalsIgnoreCase("get") || args[3].equalsIgnoreCase("g") || args[3].equalsIgnoreCase("current") || args[3].equalsIgnoreCase("curr") || args[3].equalsIgnoreCase("cur") || args[3].equalsIgnoreCase("c"))
									{
										if (AFKManager.isPlayerGod(target))
										{
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
										} else
										{
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
										}
										return true;
									}
									if (args[3].equalsIgnoreCase("on") || args[3].equalsIgnoreCase("true"))
									{
										if (!AFKManager.isPlayerGod(target))
										{
											AFKManager.addGod(target);
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is now " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
											target.sendMessage(ChatColor.RED + "Your god-mode is now " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
										} else
										{
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is already " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
										}
										return true;
									}
									if (args[3].equalsIgnoreCase("off") || args[3].equalsIgnoreCase("false"))
									{
										if (AFKManager.isPlayerGod(target))
										{
											AFKManager.removeGod(target);
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is now " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
											target.sendMessage(ChatColor.RED + "Your god-mode is now " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
										} else
										{
											sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is already " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
										}
										return true;
									}
									{
										sender.sendMessage(error(Messages.invalidtarget.getMessage(), "/afk god others <player> [get/on/off]"));
										return true;
									}
								} else
								{
									player.sendMessage(Messages.noperm.getMessage());
									return true;
								}
							} else
							{
								if (args[3].equalsIgnoreCase("get") || args[3].equalsIgnoreCase("g") || args[3].equalsIgnoreCase("current") || args[3].equalsIgnoreCase("curr") || args[3].equalsIgnoreCase("cur") || args[3].equalsIgnoreCase("c"))
								{
									if (AFKManager.isPlayerGod(target))
									{
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
									} else
									{
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
									}
									return true;
								}
								if (args[3].equalsIgnoreCase("on") || args[3].equalsIgnoreCase("true"))
								{
									if (!AFKManager.isPlayerGod(target))
									{
										AFKManager.addGod(target);
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is now " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
										target.sendMessage(ChatColor.RED + "Your god-mode is now " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
									} else
									{
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is already " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
									}
									return true;
								}
								if (args[3].equalsIgnoreCase("off") || args[3].equalsIgnoreCase("false"))
								{
									if (AFKManager.isPlayerGod(target))
									{
										AFKManager.removeGod(target);
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is now " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
										target.sendMessage(ChatColor.RED + "Your god-mode is now " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
									} else
									{
										sender.sendMessage(ChatColor.AQUA + target.getName() + ChatColor.RED + "'s god-mode is already " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
									}
									return true;
								}
								{
									sender.sendMessage(error(Messages.invalidtarget.getMessage(), "/afk god others <player> [get/on/off]"));
									return true;
								}
							}
						} else
						{
							sender.sendMessage(ChatColor.AQUA + args[2] + ChatColor.RED + " is not online.");
							return true;
						}
					}
				} else
				{
					if (sender instanceof Player)
					{
						Player player = (Player) sender;
						if (AFKPerm.godPersonal(player))
						{
							if (args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("g") || args[1].equalsIgnoreCase("current") || args[1].equalsIgnoreCase("curr") || args[1].equalsIgnoreCase("cur") || args[1].equalsIgnoreCase("c"))
							{
								if (AFKManager.isPlayerGod(player))
								{
									player.sendMessage(ChatColor.RED + "Your god-mode is " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
								} else
								{
									player.sendMessage(ChatColor.RED + "Your god-mode is " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
								}
								return true;
							}
							if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true"))
							{
								if (!AFKManager.isPlayerGod(player))
								{
									AFKManager.addGod(player);
									player.sendMessage(ChatColor.RED + "Your god-mode is now " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
								} else
								{
									player.sendMessage(ChatColor.RED + "Your god-mode is already " + ChatColor.AQUA + "ON" + ChatColor.RED + ".");
								}
								return true;
							}
							if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false"))
							{
								if (AFKManager.isPlayerGod(player))
								{
									AFKManager.removeGod(player);
									player.sendMessage(ChatColor.RED + "Your god-mode is now " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
								} else
								{
									player.sendMessage(ChatColor.RED + "Your god-mode is already " + ChatColor.AQUA + "OFF" + ChatColor.RED + ".");
								}
								return true;
							}
							{
								player.sendMessage(error(Messages.invalidtarget.getMessage(), "/afk god [get/on/off]"));
								return true;
							}
						} else
						{
							player.sendMessage(Messages.noperm.getMessage());
							return true;
						}
					} else
					{
						sender.sendMessage(Messages.playeronly.getMessage());
						return true;
					}
				}
			}
			if (args[0].equalsIgnoreCase("where"))
			{
				if (args.length < 2)
				{
					sender.sendMessage(error(Messages.notarget.getMessage(), "/afk where <player>"));
					return true;
				}
				if (args.length > 2)
				{
					sender.sendMessage(error(Messages.manytarget.getMessage(), "/afk where <player>"));
					return true;
				}
				if (args.length == 2)
				{
					Player player = Bukkit.getServer().getPlayer(args[1]);
					if (player != null)
					{
						if (AFKManager.isPlayerAFK(player))
						{
							sender.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.RED + " have been AFK for " + ChatColor.GOLD + AFKManager.getTimeString(player));
							sender.sendMessage(ChatColor.BLUE + AFKManager.getPlayerMessage(player));
						} else
						{
							sender.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.RED + " is not AFK.");
						}
						return true;
					} else
					{
						sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.RED + " is not online.");
						return true;
					}
				}
			}
			if (args[0].equalsIgnoreCase("help"))
			{
				sender.sendMessage(help(sender));
				return true;
			}
		}
		return false;
	}

	public String[] help(CommandSender sender)
	{
		String header = (ChatColor.RED + "==========" + ChatColor.AQUA + "AFK Signs Help" + ChatColor.RED + "==========");
		List<String> help = new ArrayList<String>();
		help.add(header);
		help.add(helpMaker("afk", "Toggle AFK status."));
		help.add(helpMaker("afk help", "Displays this help page."));
		help.add(helpMaker("afk where <player>", "Shows if the player is AFK, if so, how long and if specified, the reason so."));
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (AFKPerm.messagePersonal(player))
			{
				help.add(helpMaker("afk message get", "Shows your AFK message."));
				help.add(helpMaker("afk message delete", "Deletes your AFK message."));
				help.add(helpMaker("afk message set <message>", "Sets your AFK message."));
			}
			if (AFKPerm.messageOthers(player))
			{
				help.add(helpMaker("afk message others <player> get", "Shows the player's AFK message."));
				help.add(helpMaker("afk message others <player> delete", "Deletes the player's AFK message."));
				help.add(helpMaker("afk message others <player> set <message>", "Sets the player's AFK message."));
			}
			if (AFKPerm.godPersonal(player))
			{
				help.add(helpMaker("afk god get", "Shows if god-mode is on during AFK."));
				help.add(helpMaker("afk god on", "Turns god-mode on during AFK."));
				help.add(helpMaker("afk god off", "Turns god-mode off during AFK."));
			}
			if (AFKPerm.godOthers(player))
			{
				help.add(helpMaker("afk god others <player> get", "Shows if god-mode is on for the player during AFK."));
				help.add(helpMaker("afk god others <player> on", "Turns god-mode on for the player during AFK."));
				help.add(helpMaker("afk god others <player> off", "Turns god-mode off for the player during AFK."));
			}
		} else
		{
			help.add(helpMaker("afk message others <player> get", "Shows the player's AFK message."));
			help.add(helpMaker("afk message others <player> delete", "Deletes the player's AFK message."));
			help.add(helpMaker("afk message others <player> set <message>", "Sets the player's AFK message."));
			help.add(helpMaker("afk god others <player> get", "Shows if god-mode is on for the player during AFK."));
			help.add(helpMaker("afk god others <player> on", "Turns god-mode on for the player during AFK."));
			help.add(helpMaker("afk god others <player> off", "Turns god-mode off for the player during AFK."));
		}
		String dash = "";
		for (int ran = 8; ran < header.length(); ran ++)
		{
			dash = (dash + "=");
		}
		help.add(ChatColor.RED + dash);
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