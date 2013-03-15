package com.gmail.chloepika.plugins.afksigns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class AFKManager
{
	private final static AFKSigns plugin = (AFKSigns) Bukkit.getServer().getPluginManager().getPlugin("AFK Signs");

	private static HashMap<String, Long> playerStartMap = new HashMap<String, Long>();
	private static HashMap<String, Location> playerAFKLocationMap = new HashMap<String, Location>();

	private static HashMap<Location, Block> originalBlockMap = new HashMap<Location, Block>();

	private static HashMap<String, String> playerMessageMap = new HashMap<String, String>();
	private static List<String> playerGodList = new ArrayList<String>();

	private static HashMap<String, Integer> playerTaskMap = new HashMap<String, Integer>();

	private static List<String> demandedUpdateList = new ArrayList<String>();



	private static File getFolder()
	{
		File pluginFolder = new File("plugins");
		File modFolder = new File(pluginFolder, "AFK Signs");
		File saveFolder = new File(modFolder, "Save files");
		saveFolder.mkdirs();
		return saveFolder;
	}

	private static File getSaveGodPlayerFile(boolean createFile)
	{
		File originalFolder = getFolder();
		File saveGodFile = new File(originalFolder, "GodModeList.txt");
		if (!saveGodFile.exists())
		{
			if (createFile)
			{
				try
				{
					saveGodFile.createNewFile();
					return saveGodFile;
				} catch (IOException e)
				{
					e.printStackTrace();
					return null;
				}
			}
			{
				return null;
			}
		} else
		{
			return saveGodFile;
		}
	}

	private static File getSavePlayerMessageFile(boolean createFile)
	{
		File originalFolder = getFolder();
		File playerMessageFile = new File(originalFolder, "PlayerMessageSave.txt");
		if (!playerMessageFile.exists())
		{
			if (createFile)
			{
				try
				{
					playerMessageFile.createNewFile();
					return playerMessageFile;
				} catch (IOException e)
				{
					e.printStackTrace();
					return null;
				}
			}
			{
				return null;
			}
		} else
		{
			return playerMessageFile;
		}
	}

	public static void savePlayerMessage()
	{
		HashMap<String, String> data = playerMessageMap;
		File playerMessageFile = getSavePlayerMessageFile(true);
		Formatter formatter;
		try{
			formatter = new Formatter(playerMessageFile);
			for(String name : data.keySet())
			{
				formatter.format("%s : %s" +
						"%n", name, data.get(name));
			}
			formatter.close();
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void readNewPlayerMessage()
	{
		File playerMessageFile = getSavePlayerMessageFile(false);
		if (playerMessageFile != null)
		{
			HashMap<String, String> data = new HashMap<String, String>();
			Scanner scanner;
			try{
				scanner = new Scanner(playerMessageFile);
				boolean isOld = false;
				while(scanner.hasNextLine())
				{
					String[] stringLine = scanner.nextLine().split(" : ");
					data.put(stringLine[0], stringLine[1]);
					if (stringLine.length == 1)
					{
						isOld = true;
					}
				}
				scanner.close();
				if (isOld)
				{
					readOldPlayerMessage();
				} else
				{
					data = playerMessageMap;
				}
			}
			catch(FileNotFoundException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static void readOldPlayerMessage()
	{
		File playerMessageFile = getSavePlayerMessageFile(false);
		if (playerMessageFile != null)
		{
			HashMap<String, String> data = new HashMap<String, String>();
			Scanner scanner;
			try{
				scanner = new Scanner(playerMessageFile);
				while(scanner.hasNextLine())
				{
					String[] stringLine = scanner.nextLine().split(": ");
					int messageArgsLenth = stringLine.length;
					String message = "";
					for (int ran = 1; ran < messageArgsLenth; ran++)
					{
						message = (message + " " + stringLine[ran]);
					}
					message = message.substring(1);
					data.put(stringLine[0], message);
				}
				scanner.close();
				playerMessageMap = data;
			}
			catch(FileNotFoundException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static void savePlayerGod()
	{
		List<String> data = playerGodList;
		File godFile = getSaveGodPlayerFile(true);
		Formatter formatter;
		try{
			formatter = new Formatter(godFile);
			for(String name : data)
			{
				formatter.format("%s" +
						"%n", name);
			}
			formatter.close();
		}
		catch(FileNotFoundException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void readPlayerGod()
	{
		File godFile = getSaveGodPlayerFile(false);
		if (godFile != null)
		{
			List<String> data = new ArrayList<String>();
			Scanner scanner;
			try{
				scanner = new Scanner(godFile);
				while(scanner.hasNextLine())
				{
					String string = scanner.nextLine();
					data.add(string);
				}
				scanner.close();
				playerGodList = data;
			}
			catch(FileNotFoundException ex)
			{
				ex.printStackTrace();
			}
		}
	}







	public static void goAFK(Player player)
	{
		if (!isPlayerAFK(player))
		{
			String playerName = player.getName();
			Location location = player.getLocation();
			Block locationBlock = location.getBlock();
			long playerTime = player.getPlayerTime();

			if (locationBlock.getType() == Material.CHEST)
			{
				location = location.add(0, 1, 0);
				locationBlock = location.getBlock();
			}

			playerStartMap.put(playerName, playerTime);
			playerAFKLocationMap.put(playerName, location);
			originalBlockMap.put(location, locationBlock);

			IdleManager.stopTrack(player);

			locationBlock.setType(Material.SIGN_POST);
			Sign sign = (Sign) locationBlock.getState();

			callSignScheduler(player, sign);

			player.sendMessage(ChatColor.RED + "You are now AFK.");
		}
	}

	public static void callSignScheduler(Player player, Sign sign)
	{
		String playerName = player.getName();
		int taskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new UpdateSign(player, sign), 0, 10);
		playerTaskMap.put(playerName, taskId);
	}

	public static void cancelAFK(Player player, boolean track)
	{
		if (isPlayerAFK(player))
		{
			String playerName = player.getName();
			Location playerLocation = playerAFKLocationMap.get(playerName);
			Block playerLocationBlock = originalBlockMap.get(playerLocation);
			playerLocation.getBlock().setTypeIdAndData(playerLocationBlock.getTypeId(), playerLocationBlock.getData(), true);
			cancelScheduler(player);
			playerTaskMap.remove(playerName);
			playerStartMap.remove(playerName);
			playerAFKLocationMap.remove(playerName);
			originalBlockMap.remove(playerLocation);
			if (track)
			{
				IdleManager.startTrack(player);
			}

			player.sendMessage(ChatColor.RED + "You are now not AFK.");
		} else
		{
			IdleManager.refresh(player);
		}
	}

	public static void cancelScheduler(Player player)
	{
		String playerName = player.getName();
		int taskId = playerTaskMap.get(playerName);
		Bukkit.getServer().getScheduler().cancelTask(taskId);
	}



	public static void demandUpdate(Player player)
	{
		String playerName = player.getName();
		demandedUpdateList.add(playerName);
	}

	public static boolean requiresUpdate(Player player)
	{
		String playerName = player.getName();
		return demandedUpdateList.contains(playerName);
	}

	public static void updated(Player player)
	{
		String playerName = player.getName();
		demandedUpdateList.remove(playerName);
	}



	public static boolean isPlayerAFK(Player player)
	{
		String playerName = player.getName();
		return isPlayerAFK(playerName);
	}

	public static boolean isPlayerAFK(String playerName)
	{
		boolean playerTime = playerStartMap.containsKey(playerName);
		boolean playerLocation = playerAFKLocationMap.containsKey(playerName);
		boolean playerTask = playerTaskMap.containsKey(playerName);

		return (playerTime && playerLocation && playerTask);
	}



	public static void setPlayerMessage(Player player, String message)
	{
		String playerName = player.getName();
		playerMessageMap.put(playerName, message);
	}

	public static void removePlayerMessage(Player player)
	{
		String playerName = player.getName();
		playerMessageMap.remove(playerName);
	}

	public static String getPlayerMessage(Player player)
	{
		String playerName = player.getName();
		return playerMessageMap.get(playerName);
	}


	public static long getPlayerTime(Player player)
	{
		String playerName = player.getName();
		return playerStartMap.get(playerName);
	}

	public static long getPlayerTimeDiff(Player player)
	{
		return (player.getPlayerTime() - getPlayerTime(player));
	}

	public static String getTimeString(Player player)
	{
		long playerDiffTime = getPlayerTimeDiff(player);
		long sec = playerDiffTime / 20;
		if (sec >= 60)
		{
			long min = sec / 60;
			sec = sec % 60;
			if (min >= 60)
			{
				long hour = min / 60;
				min = min % 60;
				return (hour + "h " + min + "m " + sec + "s");
			} else
			{
				return (min + "m " + sec + "s");
			}
		} else
		{
			return (sec + "s");
		}
	}


	public static boolean isPlayerGod(Player player)
	{
		String playerName = player.getName();
		return playerGodList.contains(playerName);
	}

	public static void addGod(Player player)
	{
		String playerName = player.getName();
		if (!playerGodList.contains(playerName))
		{
			playerGodList.add(playerName);
		}
	}

	public static void removeGod(Player player)
	{
		String playerName = player.getName();
		playerGodList.remove(playerName);
	}



	public static boolean isLocationUsed(Location location)
	{
		return playerAFKLocationMap.containsValue(location);
	}
}