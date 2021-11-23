package xyz.n7mn.dev.playerlistsync;

import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.File;
import java.io.PrintWriter;

public final class PlayerListSync extends Plugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        File file = getProxy().getPluginManager().getPlugin("PlayerListSync").getDataFolder();
        //System.out.println(file.getPath());
        if (!file.exists()){
            file.mkdir();
        }

        File config = new File(file.getPath()+"/config.json");
        if (!config.exists()){
            try {
                String json = new GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(new ConfigJson());

                PrintWriter writer = new PrintWriter(file.getPath()+"/config.json");
                writer.print(json);
                writer.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }


        getProxy().getPluginManager().registerListener(this, new WaterEventListener(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
