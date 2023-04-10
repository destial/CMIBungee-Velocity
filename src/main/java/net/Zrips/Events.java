package net.Zrips;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

public class Events {

    @Subscribe
    public void onPlayerDisconnectEvent(final KickedFromServerEvent event) {
        final RegisteredServer server = event.getServer();
        String serverName = "unknown";
        if (event.getPlayer() == null)
            return;

        if (server != null) {
            final ServerInfo info = server.getServerInfo();
            if (info != null && info.getName() != null) {
                serverName = info.getName();
            }
        }
        CMIB.getInstance().getMessageHandling().sendServerEvent(event.getPlayer(), serverName, "PlayerDisconnectEvent");
    }

    @Subscribe
    public void onServerConnection(final ServerConnectedEvent event) {
        final RegisteredServer target = event.getServer();
        if (target == null)
            return;

        final RegisteredServer previous = event.getPreviousServer().orElse(null);
        if (previous == null) {
            String serverName = "unknown";
            final ServerInfo info = target.getServerInfo();
            if (info != null && info.getName() != null)
                serverName = info.getName();

            CMIB.getInstance().getMessageHandling().sendServerEvent(event.getPlayer(), serverName, "ServerConnectEvent");
            return;
        }

        onServerSwitchEvent(event.getPlayer(), previous);
    }

    public void onServerSwitchEvent(final Player player, final RegisteredServer from) {
        CMIB.getInstance().getMessageHandling().sendServerSwitchEvent(player, from);
    }
}
