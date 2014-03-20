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
	
	int[] starterItems = { 5 };
	
	public CraftEventListener(Plugin instance)
	{
		local = instance;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void CraftItemEventsss(CraftItemEvent event)
	{
		Player p = (Player) event.getWhoClicked();
		//p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.RED + "You tried to craft: " + event.getCurrentItem().getType().name()+ " : " + event.getCurrentItem().getType().getId());

		List<String> canCraft = local.getConfig().getStringList("players." + p.getName() + ".items");
		
		boolean allowed = false;
		
		for (int i = 0; i < canCraft.size(); i++)
		{
			if (event.getCurrentItem().getType().getId() == Integer.parseInt(canCraft.get(i).trim()))
			{
				allowed = true;
			}
			
		}
		
		for (int i = 0; i < starterItems.length; i++)
		{
			if (event.getCurrentItem().getType().getId() == starterItems[i])
			{
				allowed = true;
			}
			
		}
		
		if (!allowed)
		{
			List<String> amounts = findAmounts(event.getCurrentItem().getType().getId());
			
			p.sendMessage("amount 1" + amounts.get(0));
			
			if (tryRemoveFromInvent(p, amounts))
			{
				canCraft.add(event.getCurrentItem().getType().getId() + "");
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
	
	private List<String> findAmounts(int id) 
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
		
		for (int i = 0; i < amounts.size(); i++)
		{
			String[] items = amounts.get(i).split(",");
			ItemStack test = new ItemStack(Integer.parseInt(items[0].trim()));
			test.setAmount(Integer.parseInt(items[1].trim()));
			
			if (!p.getInventory().containsAtLeast(test, Integer.parseInt(items[1].trim())))
			{
				if (successfull)
				{
					p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.RED + "You have not learned to craft that yet!");
					p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.YELLOW + "To learn it you will need:");
					successfull = false;
				}
				
				p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.YELLOW + "  - " + items[1] + " " + Material.getMaterial(Integer.parseInt(items[0])));
			}
		}
		
		if (!successfull)
			return false;
		
		//Remove all of them
		
		boolean done = false;
		
		for (int i = 0; i < amounts.size(); i++)
		{
			String[] items = amounts.get(i).split(",");
			ItemStack test = new ItemStack(Integer.parseInt(items[0].trim()));
			test.setAmount(Integer.parseInt(items[1].trim()));
			
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
