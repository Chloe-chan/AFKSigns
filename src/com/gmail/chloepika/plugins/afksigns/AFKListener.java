package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AFKListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (!event.isCancelled())
		{
			int movedX = event.getFrom().getBlockX() - event.getTo().getBlockX();
			int movedY = event.getFrom().getBlockY() - event.getTo().getBlockY();
			int movedZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();
			if (Math.abs(movedX) > 0 || Math.abs(movedZ) > 0 || Math.abs(movedY) > 0)
			{
				float movedYaw = event.getFrom().getYaw() - event.getTo().getYaw();
				float movedPitch = event.getFrom().getPitch() - event.getTo().getPitch();
				if (Math.abs(movedYaw) > 0 || Math.abs(movedPitch) > 0)
				{
					AFKManager.cancelAFK(event.getPlayer(), true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		{
			AFKManager.cancelAFK(event.getPlayer(), true);
		}
		{
			Block block = event.getBlock();
			if (block.getState() instanceof Sign)
			{
				Location location = block.getLocation();
				boolean cancel = AFKManager.isLocationUsed(location);
				if (cancel)
				{
					event.setCancelled(cancel);
					event.getPlayer().sendMessage(ChatColor.RED + "This sign is protected.");
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		AFKManager.cancelAFK(event.getPlayer(), true);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		AFKManager.cancelAFK(event.getPlayer(), true);
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		AFKManager.cancelAFK(event.getPlayer(), true);
	}

	@EventHandler
	public void onPlayerIgnite(BlockIgniteEvent event)
	{
		if (event.getCause() == IgniteCause.FLINT_AND_STEEL)
		{
			AFKManager.cancelAFK(event.getPlayer(), true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		AFKManager.cancelAFK(event.getPlayer(), true);
	}

	@EventHandler
	public void onPlayerShoot(EntityShootBowEvent event)
	{
		if (event.getEntityType() == EntityType.PLAYER)
		{
			Player player = (Player) event.getEntity();
			AFKManager.cancelAFK(player, true);
		}
	}
}