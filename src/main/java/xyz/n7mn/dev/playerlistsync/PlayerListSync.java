package xyz.n7mn.dev.playerlistsync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.n7mn.dev.playerlistsync.config.ConfigJson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public final class PlayerListSync extends Plugin {

    private Connection con = null;
    private Timer timer = new Timer();

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

        new Thread(()->{

            File file1 = new File(this.getDataFolder().getPath()+"/config.json");
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file1), StandardCharsets.UTF_8));
                String text;
                while ((text = reader.readLine()) != null) {
                    sb.append(text);
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
            ConfigJson config1 = new Gson().fromJson(sb.toString(), ConfigJson.class);

            try {
                con = DriverManager.getConnection("jdbc:mysql://" + config1.getMySQLServer() + ":" + config1.getMySQLPort() + "/" + config1.getMySQLDatabase() + config1.getMySQLOption(), config1.getMySQLUsername(), config1.getMySQLPassword());
                con.setAutoCommit(true);
            } catch (SQLException e){
                e.printStackTrace();
            }

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (con != null){
                        try {
                            con = DriverManager.getConnection("jdbc:mysql://" + config1.getMySQLServer() + ":" + config1.getMySQLPort() + "/" + config1.getMySQLDatabase() + config1.getMySQLOption(), config1.getMySQLUsername(), config1.getMySQLPassword());
                            con.setAutoCommit(true);
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }

                    try {
                        try {
                            PreparedStatement statement = con.prepareStatement("SELECT * FROM ServerList");
                            statement.execute();
                            statement.close();
                        } catch (SQLException ex1){
                            con = DriverManager.getConnection("jdbc:mysql://" + config1.getMySQLServer() + ":" + config1.getMySQLPort() + "/" + config1.getMySQLDatabase() + config1.getMySQLOption(), config1.getMySQLUsername(), config1.getMySQLPassword());
                            con.setAutoCommit(true);
                        }

                        PreparedStatement statement1 = con.prepareStatement("SELECT * FROM ServerList WHERE ServerName = ? AND ServerNo = ?");
                        statement1.setString(1, config1.getServerName());
                        statement1.setInt(2, config1.getServerNo());
                        ResultSet set = statement1.executeQuery();

                        boolean isFound = false;
                        if (set.next()){
                            isFound = true;
                            set.close();
                            statement1.close();
                        }

                        int playerCount = 0;
                        for (Map.Entry<String, ServerInfo> entry : getProxy().getServersCopy().entrySet()) {
                            ServerInfo server = entry.getValue();
                            playerCount = playerCount + server.getPlayers().size();
                        }

                        PreparedStatement statement2;
                        if (isFound){
                            statement2 = con.prepareStatement("UPDATE `ServerList` SET `PlayerCount`= ? WHERE ServerName = ? AND ServerNo = ?");
                            statement2.setInt(1, playerCount);
                            statement2.setString(2, config1.getServerName());
                            statement2.setInt(3, config1.getServerNo());
                        } else {
                            statement2 = con.prepareStatement("INSERT INTO `ServerList`(`UUID`, `ServerName`, `ServerNo`, `PlayerCount`) VALUES (?,?,?,?)");
                            statement2.setString(1, UUID.randomUUID().toString());
                            statement2.setString(2, config1.getServerName());
                            statement2.setInt(3, config1.getServerNo());
                            statement2.setInt(4, playerCount);
                        }

                        statement2.execute();
                        statement2.close();
                    } catch (SQLException ex){
                        ex.printStackTrace();
                    }
                }
            };


            timer.scheduleAtFixedRate(task, 0L, 100L);
        }).start();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            timer.cancel();
            if (con == null){
                return;
            }
            con.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
