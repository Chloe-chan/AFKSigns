package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class AFKGodListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if (event.getEntityType() == EntityType.PLAYER)
		{
			Player player = (Player) event.getEntity();
			event.setCancelled(AFKManager.isPlayerAFK(player) && AFKManager.isPlayerGod(player));
		}
	}
}