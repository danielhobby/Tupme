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
	
	Material[] starterItems = { Material.WOOD, Material.STORAGE_MINECART, Material.POWERED_MINECART, Material.HOPPER_MINECART, Material.EXPLOSIVE_MINECART };
	
	public CraftEventListener(Plugin instance)
	{
		local = instance;
	}
	
	@EventHandler
	public void CraftItemEvents(CraftItemEvent event)
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
		
		String bypass = local.getConfig().getString("players." + p.getName() + ".bypass");
		
		if (bypass == "TRUE")
		{
			allowed = true;
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
			else if(tryRemoveSomeFromInvent(p, amounts, event.getCurrentItem().getType()))
			{
				
			}
			else
			{
				event.setCancelled(true);
			}
		}
	}	
	
	private boolean tryRemoveSomeFromInvent(Player p, List<String> amounts, Material tryingToCraft) {
		
		//psuedo code
		// For the first item
		// Count the players invent to see if they have any of the first item.
		// Remove the amount that we counted from the players invent.
		
		
		
		int[] amountOfCurrentItem = new int[amounts.size() + 1];
		
		List<String> sacrificed = Tupme.recipesConfig.getStringList("players." + p.getName() + ".partialcrafts");
		
		
		
		for (int i = 0; i < amounts.size(); i++)
		{
			String[] items = amounts.get(i).split(",");
			ItemStack currentSearchItem = new ItemStack(Material.getMaterial(items[0]), 1);
			
			p.sendMessage("Current Search Item: " + currentSearchItem.getType().name());
			
			for (int j = 0; j < p.getInventory().getContents().length; j++)
			{
				if (p.getInventory().getItem(j) != null)
				{
					p.sendMessage(p.getInventory().getItem(j).getType().name());
					if (p.getInventory().getItem(j).getType().equals(currentSearchItem.getType()))
					{
						int amount = amountOfCurrentItem[i] + p.getInventory().getItem(j).getAmount();
						amountOfCurrentItem[i] = amount;
						p.sendMessage("Removed: " + p.getInventory().getItem(j).getType() + " : " + amountOfCurrentItem[i]);
						p.getInventory().setItem(j, new ItemStack(Material.AIR));
					}
				}
			}			
		}
		
		
		
		
		return false;
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
		
		//Update the players inventory to remove the items that were "removed".
		p.updateInventory();
		
		
		if (done)
			return true;
		else
			return false;
	}
}
