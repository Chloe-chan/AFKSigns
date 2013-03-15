package com.gmail.chloepika.plugins.afksigns;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class UpdateSign implements Runnable
{
	private final Player player;
	private final Sign sign;
	private String playerMessage;
	private String playerTimeMessage;

	private final int scrollPauseDefault = 3;
	private int scrollIndex = 0;
	private int scrollPause = scrollPauseDefault;

	public UpdateSign(Player player, Sign sign)
	{
		this.player = player;
		this.sign = sign;
		fetchMessage();
		fetchTimeMessage();
		sign.setLine(0, player.getName());
		sign.setLine(1, "is AFK for");
	}

	private void fetchMessage()
	{
		playerMessage = AFKManager.getPlayerMessage(player);
	}

	private void fetchTimeMessage()
	{
		playerTimeMessage = AFKManager.getTimeString(player);
	}

	private void updateTime()
	{
		fetchTimeMessage();
		sign.setLine(2, playerTimeMessage);
	}

	private void updateScroll()
	{
		int endIndex = (scrollIndex + 15);
		if (endIndex > playerMessage.length())
		{
			endIndex = playerMessage.length();
		}
		String scrollingText = playerMessage.substring(scrollIndex, endIndex);
		if (scrollingText.length() < 15)
		{
			int spaceLength = (15 - scrollingText.length());
			if (spaceLength > 15)
			{
				spaceLength = 15;
			}
			for (int ran = 0; ran < spaceLength; ran++)
			{
				scrollingText = (scrollingText + " ");
			}
		}
		sign.setLine(3, scrollingText);
		scrollPause --;
		if (scrollPause <= 0)
		{
			scrollIndex ++;
			if (scrollIndex > playerMessage.length())
			{
				scrollIndex = 0;
				scrollPause = scrollPauseDefault;
			}
		}
	}

	private void resetTime()
	{
		scrollIndex = 0;
		scrollPause = scrollPauseDefault;
	}

	public void run()
	{
		if (AFKManager.requiresUpdate(player))
		{
			fetchMessage();
			AFKManager.updated(player);
			resetTime();
		}
		updateTime();
		if (playerMessage.length() > 15)
		{
			updateScroll();
		} else
		{
			sign.setLine(3, playerMessage);
		}
		sign.update();
	}
}