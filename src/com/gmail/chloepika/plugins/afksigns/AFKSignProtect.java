package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class AFKSignProtect implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		if (block.getState() instanceof Sign)
		{
			if (block.hasMetadata("afkProtected"))
			{
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "This sign is protected.");
			}
		}
	}
}