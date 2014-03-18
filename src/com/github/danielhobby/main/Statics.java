package com.github.danielhobby.main;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Statics {
	public static String SERVER_NAME = "SERVERNAME";
	public static String MESSAGE_HEADER = ChatColor.DARK_RED + "[" + SERVER_NAME + "] ";
	
	private Plugin local;
	
	File staticsFile = null;
	FileConfiguration statics = null;
	
	public void setUpStatics(Plugin instance)
	{
		local = instance;
		
		staticsFile = new File(local.getDataFolder(), "statics.yml");
		
		try {
	        setupFiles();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		statics = new YamlConfiguration();
		
		try {
			getServerName();
		} catch (Exception e) {
		}
	}
	
	private void setupFiles() throws Exception {
	    if(!staticsFile.exists()){
	    	staticsFile.getParentFile().mkdirs();
	        copy(local.getResource("statics.yml"), staticsFile);
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

	public void getServerName() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		statics.load(staticsFile);
		
		SERVER_NAME = statics.getString("servername");
		
		statics.save(staticsFile);
	}
}