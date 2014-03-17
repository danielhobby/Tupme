package com.github.danielhobby.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
		
		Statics s = new Statics();
		s.setUpStatics(this);
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
