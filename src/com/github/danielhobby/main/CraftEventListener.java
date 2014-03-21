package com.github.danielhobby.main;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CraftEventListener implements Listener {
	Plugin local;
	
	Material[] starterItems = { Material.WOOD };
	
	public CraftEventListener(Plugin instance)
	{
		local = instance;
	}
	
	@EventHandler
	public void CraftItemEventsss(CraftItemEvent event)
	{
		Player p = (Player) event.getWhoClicked();
		//p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.RED + "You tried to craft: " + event.getCurrentItem().getType().name()+ " : " + event.getCurrentItem().getType().getId());

		List<String> canCraft = local.getConfig().getStringList("players." + p.getName() + ".items");
		
		boolean allowed = false;
		
		for (int i = 0; i < canCraft.size(); i++)
		{
			if (event.getCurrentItem().getType() == Material.getMaterial(canCraft.get(i).trim()))
			{
				allowed = true;
			}			
		}
		
		for (int i = 0; i < starterItems.length; i++)
		{
			if (event.getCurrentItem().getType() == starterItems[i])
			{
				allowed = true;
			}
		}
		
		if (!allowed)
		{
			List<String> amounts = findAmounts(event.getCurrentItem().getType().name());
			
			if (tryRemoveFromInvent(p, amounts))
			{
				canCraft.add(event.getCurrentItem().getType().name() + "");
				local.getConfig().set("players." + p.getName() + ".items", canCraft);
				local.saveConfig();
				p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.GOLD + "You can now make " + event.getCurrentItem().getType() + "!");
			}
			else
			{
				event.setCancelled(true);
			}
		}
	}	
	
	private List<String> findAmounts(String id) 
	{			
		List<String> mats = Tupme.recipesConfig.getStringList(id + ".requiredItems");
		
		return mats;
	}

	@SuppressWarnings("deprecation")
	private boolean tryRemoveFromInvent(Player p, List<String> amounts)
	{
		if (amounts.size() == 0)
		{
			return false;
		}
		//Find out if they have all of the items
		
		boolean successfull = true;
		short itemMetaData = 0;
		
		for (int i = 0; i < amounts.size(); i++)
		{
			for (int j = 0; j <= 10; j++)
			{
				String[] items = amounts.get(i).split(",");
				ItemStack test = new ItemStack(Material.getMaterial(items[0].trim()), Integer.parseInt(items[1].trim()), (short) j);
				
				//p.sendMessage(j + " " + test.getType().name() + " " + test.getDurability());
				
				boolean testCondition = p.getInventory().containsAtLeast(test, Integer.parseInt(items[1].trim()));
				
				if (!testCondition)
				{
					successfull = false;
				}
				else
				{
					successfull = true;
					itemMetaData = (short)j;
					break;
				}
				
				//p.sendMessage(test.getType().name() + testCondition);
			}
		}
		
		// Enough of one of the times was not found
		// Tell the player what they require.
		
		if (!successfull)
		{
			p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.RED + "You have not learned to craft that yet!");
			p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.YELLOW + "To learn it you will need:");
			
			
			for (int i = 0; i < amounts.size(); i++)
			{
				String[] items = amounts.get(i).split(",");
				p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.YELLOW + "  - " + items[1] + " (" + (Double.parseDouble(items[1]) / 64) + ") " + Material.getMaterial(items[0]));
			}
			
			return false;
		}
		
		//Remove all of them
		
		boolean done = false;
		
		for (int i = 0; i < amounts.size(); i++)
		{
			String[] items = amounts.get(i).split(",");
			ItemStack test = new ItemStack(Material.getMaterial(items[0].trim()), Integer.parseInt(items[1].trim()), (short) itemMetaData);
			
			p.getInventory().removeItem(test);
			done = true;
		}
		
		p.updateInventory();
		
		
		if (done)
			return true;
		else
			return false;
	}
}
