package xyz.n7mn.dev.playerlistsync.TCP;

import java.util.List;

public class ReceptionData {

    private String Status;
    private String Mode;
    private long PlayerList;
    private List<ServerPlayerListData> ServerPlayerListData;

    public ReceptionData(String status, String mode, long playerList, List<ServerPlayerListData> serverPlayerListData){
        this.Status = status;
        this.Mode = mode;
        this.PlayerList = playerList;
        this.ServerPlayerListData = serverPlayerListData;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public long getPlayerList() {
        return PlayerList;
    }

    public void setPlayerList(long playerList) {
        PlayerList = playerList;
    }

    public List<ServerPlayerListData> getServerPlayerList() {
        return ServerPlayerListData;
    }

    public void setServerPlayerList(List<ServerPlayerListData> serverPlayerListData) {
        ServerPlayerListData = serverPlayerListData;
    }

}
