package xyz.n7mn.dev.playerlistsync.TCP;

import java.util.UUID;

public class SendData {
    private String mode;
    private UUID uuid;
    private String ServerName;
    private int ServerNo;
    private String ServerDisplayName;
    private String PlayerList;

    public SendData(String mode, UUID uuid, String serverName, int serverNo, String serverDisplayName, String playerList){
        this.mode = mode;
        this.uuid = uuid;
        this.ServerName = serverName;
        this.ServerNo = serverNo;
        this.PlayerList = playerList;
        this.ServerDisplayName = serverDisplayName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getServerName() {
        return ServerName;
    }

    public void setServerName(String serverName) {
        ServerName = serverName;
    }

    public int getServerNo() {
        return ServerNo;
    }

    public void setServerNo(int serverNo) {
        ServerNo = serverNo;
    }

    public String getServerDisplayName() {
        return ServerDisplayName;
    }

    public void setServerDisplayName(String serverDisplayName) {
        ServerDisplayName = serverDisplayName;
    }

    public String getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(String playerList) {
        PlayerList = playerList;
    }

}
