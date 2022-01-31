package xyz.n7mn.dev.playerlistsync.TCP;

import java.util.UUID;

public class ServerPlayerListData {
    private String ServerName;
    private UUID[] PlayerList;

    public ServerPlayerListData(String serverName, UUID[] playerList){
        this.ServerName = serverName;
        this.PlayerList = playerList;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public UUID[] getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(UUID[] playerList) {
        PlayerList = playerList;
    }

}
