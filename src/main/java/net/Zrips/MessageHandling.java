package net.Zrips;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class MessageHandling {
    public void sendServerEvent(final Player player, final String serverName, final String eventType) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(eventType);
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(player.getUsername());
        out.writeUTF(serverName);
        for (final RegisteredServer one : CMIB.getInstance().getProxy().getAllServers()) {
            if (one.getPlayersConnected().isEmpty())
                continue;

            final Player proxyPlayer = one.getPlayersConnected().iterator().next();
            final ServerConnection connection = proxyPlayer.getCurrentServer().orElse(null);
            if (connection == null || connection.getServerInfo() == null)
                continue;

            connection.sendPluginMessage(CMIB.getInstance().getFromProxyChannel(), out.toByteArray());
        }
    }

    public void sendServerSwitchEvent(final Player player, final RegisteredServer serverFrom) {
        ServerConnection con = player.getCurrentServer().orElse(null);
        if (con == null)
            return;

        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("CMIServerSwitchEvent");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(player.getUsername());
        out.writeUTF(serverFrom == null ? "" : serverFrom.getServerInfo().getName());
        out.writeUTF(con.getServerInfo().getName());
        for (final RegisteredServer one : CMIB.getInstance().getProxy().getAllServers()) {
            if (one.getPlayersConnected().isEmpty())
                continue;

            final Player proxyPlayer = one.getPlayersConnected().iterator().next();
            final ServerConnection connection = proxyPlayer.getCurrentServer().orElse(null);
            if (connection == null || connection.getServerInfo() == null)
                continue;

            connection.sendPluginMessage(CMIB.getInstance().getFromProxyChannel(), out.toByteArray());
        }

        if (serverFrom == null)
            return;

        serverFrom.sendPluginMessage(CMIB.getInstance().getFromProxyChannel(), out.toByteArray());
    }
}
