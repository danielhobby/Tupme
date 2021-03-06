package com.github.danielhobby.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Tupme extends JavaPlugin{
	public static boolean Allowed = false;
	public static FileConfiguration recipesConfig = null;
	public static File recipesConfigFile = null;
	
	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new CraftEventListener(this), this);
		
		recipesConfigFile = new File(getDataFolder(), "recipes.yml");
		try {
	        setupFiles();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		recipesConfig = new YamlConfiguration();
		
		try {
			recipesConfig.load(recipesConfigFile);
			getConfig().load("config.yml");
		} catch (Exception e) {
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player p = (Player)sender;
		
		if (label.equalsIgnoreCase("tup"))
		{
			if(args[0].equalsIgnoreCase("listrecipes"))
			{
				p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.YELLOW + " Learned recipes:");
				List<String> learnedrecipes = getConfig().getStringList("players." + p.getName() + ".items");
				
				for (int i = 0; i < learnedrecipes.size(); i++)
				{
					p.sendMessage("" + Material.getMaterial(learnedrecipes.get(i)));
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("bypass"))
			{
				if (p.isOp())
				{
					if (args[1] != null)
					{
						String currentState = getConfig().getString("players." + args[1] + ".bypass");
						if (currentState == "TRUE")
						{
							getConfig().set("players." + args[1] + ".bypass", "FALSE");
						}
						else
						{
							getConfig().set("players." + args[1] + ".bypass", "TRUE");
						}
					}
					else
					{
						String currentState = getConfig().getString("players." + p.getName() + ".bypass");
						if (currentState == "TRUE")
						{
							getConfig().set("players." + p.getName() + ".bypass", "FALSE");
						}
						else
						{
							getConfig().set("players." + p.getName() + ".bypass", "TRUE");
						}
					}
				}
				else
				{
					p.sendMessage(Statics.MESSAGE_HEADER + ChatColor.AQUA + "You must be an Operator to use this command.");
				}
			}
		}
		
		return false;
	}
	
	
	private void setupFiles() throws Exception {
	    if(!recipesConfigFile.exists()){
	    	recipesConfigFile.getParentFile().mkdirs();
	        copy(getResource("recipes.yml"), recipesConfigFile);
	    }
	}
	
	
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
