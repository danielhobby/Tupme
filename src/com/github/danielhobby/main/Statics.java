package com.github.danielhobby.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Statics {
	public static String SERVER_NAME = "SERVERNAME";
	public static String MESSAGE_HEADER = ChatColor.DARK_RED + "[" + SERVER_NAME + "] ";
	
	private FileConfiguration statics_ConfigurationFile = null;
	private File statics_ConfigFile = null;
	private Plugin local;
	
	public void setUpStatics(Plugin instance)
	{
		local = instance;
		getServerName();
	}
	
	public void getServerName()
	{
		SERVER_NAME = statics_ConfigurationFile.getString("servername");
	}
	
	public void reloadCustomConfig() {
	    if (statics_ConfigFile == null) {
	    	statics_ConfigFile = new File(local.getDataFolder(), "customConfig.yml");
	    }
	    statics_ConfigurationFile = YamlConfiguration.loadConfiguration(statics_ConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = local.getResource("customConfig.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        statics_ConfigurationFile.setDefaults(defConfig);
	    }
	}
	
	public void saveCustomConfig() {
	    if (statics_ConfigurationFile == null || statics_ConfigFile == null) {
	        return;
	    }
	    try {
	        getCustomConfig().save(statics_ConfigFile);
	    } catch (IOException ex) {
	    	local.getLogger().log(Level.SEVERE, "Could not save config to " + statics_ConfigFile, ex);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (statics_ConfigurationFile == null) {
	        reloadCustomConfig();
	    }
	    return statics_ConfigurationFile;
	}
}