package net.Zrips;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

public class MessageListener {

    @Subscribe
    public void on(final PluginMessageEvent event) {
        ChannelIdentifier tag = event.getIdentifier();
        if (!tag.getId().equalsIgnoreCase("BungeeCord") && !tag.getId().equalsIgnoreCase("bungeecord:main")) {
            return; // I have no idea if this is even gonna work
        }

        String senderAddress = "";
        if (event.getSource() instanceof Player) {
            Player player = (Player) event.getSource();
            senderAddress = player.getRemoteAddress().getAddress().getHostAddress() + ":" + player.getRemoteAddress().getPort();
        } else if (event.getSource() instanceof ServerConnection) {
            ServerConnection connection = (ServerConnection) event.getSource();
            senderAddress = connection.getServerInfo().getAddress().getAddress().getHostAddress() + ":" + connection.getServerInfo().getAddress().getPort();
        }

        final ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        final String subChannel = in.readUTF();
        if (subChannel.equalsIgnoreCase("CMIServerListRequest")) {
            RegisteredServer server = null;
            for (final RegisteredServer one : CMIB.getInstance().getProxy().getAllServers()) {
                String address = one.getServerInfo().getAddress().getAddress().getHostAddress() + ":" + one.getServerInfo().getAddress().getPort();
                if (address.equalsIgnoreCase(senderAddress)) {
                    server = one;
                }
            }
            if (server == null || server.getPlayersConnected().isEmpty()) {
                return;
            }
            for (final RegisteredServer srv : CMIB.getInstance().getProxy().getAllServers()) {
                final ServerInfo serverInfo = srv.getServerInfo();
                String info = serverInfo.getName() + ";:" + serverInfo.getAddress().getAddress().getHostAddress() + ";:" + serverInfo.getAddress().getPort() + ";:MOTD is not supported on Velocity!";
                final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("ServerListFeedback");
                out.writeUTF(serverInfo.getName());
                out.writeUTF(info);
                final Player proxyPlayer = server.getPlayersConnected().iterator().next();
                ServerConnection connection = proxyPlayer.getCurrentServer().orElse(null);
                if (connection != null && connection.getServerInfo() != null) {
                    connection.sendPluginMessage(CMIB.getInstance().getFromProxyChannel(), out.toByteArray());
                }
            }
        }
        else if (subChannel.equalsIgnoreCase("CMIPlayerListRequest")) {
            RegisteredServer server = null;
            for (final RegisteredServer one : CMIB.getInstance().getProxy().getAllServers()) {
                String address = one.getServerInfo().getAddress().getAddress().getHostAddress() + ":" + one.getServerInfo().getAddress().getPort();
                if (address.equalsIgnoreCase(senderAddress)) {
                    server = one;
                }
            }
            if (server == null || server.getPlayersConnected().isEmpty()) {
                return;
            }
            for (final RegisteredServer srv : CMIB.getInstance().getProxy().getAllServers()) {
                final ServerInfo serverInfo = srv.getServerInfo();
                final StringBuilder players = new StringBuilder();
                for (final Player oneP : srv.getPlayersConnected()) {
                    if (!players.toString().isEmpty()) {
                        players.append(";;");
                    }
                    players.append(oneP.getUsername())
                           .append("::")
                           .append(oneP.getUniqueId().toString());
                }
                String info = serverInfo.getName() + ";:" + players;
                final ByteArrayDataOutput out2 = ByteStreams.newDataOutput();
                out2.writeUTF("PlayerListFeedback");
                out2.writeUTF(serverInfo.getName());
                out2.writeUTF(info);
                final Player proxyPlayer = server.getPlayersConnected().iterator().next();
                ServerConnection connection = proxyPlayer.getCurrentServer().orElse(null);
                if (connection != null && connection.getServerInfo() != null) {
                    connection.sendPluginMessage(CMIB.getInstance().getFromProxyChannel(), out2.toByteArray());
                }
            }
        }
    }
}
