package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.entity.Player;

public class IdleToAFK implements Runnable
{
	private Player player;

	public IdleToAFK(Player player)
	{
		this.player = player;
	}

	public void run()
	{
		AFKManager.goAFK(player);
	}
}