package xyz.n7mn.dev.playerlistsync.TCP;

import java.util.UUID;

public class ServerPlayerListData {
    private String ServerName;
    private String[] PlayerList;

    public ServerPlayerListData(String serverName, String[] playerList){
        this.ServerName = serverName;
        this.PlayerList = playerList;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public String[] getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(String[] playerList) {
        PlayerList = playerList;
    }

}
