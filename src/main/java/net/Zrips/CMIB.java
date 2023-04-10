package net.Zrips;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

@Plugin(
        id = "cmivelocity",
        name = "CMIVelocity",
        version = "1.0.2.2"
)
public class CMIB {
    private static CMIB instance;

    static {
        instance = null;
    }

    private final Logger logger;
    private final ProxyServer server;

    private MinecraftChannelIdentifier fromProxy;
    private MinecraftChannelIdentifier fromServer;
    private MessageHandling messageHandling;

    @Inject
    public CMIB(Logger logger, ProxyServer server) {
        this.logger = logger;
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;
        fromProxy = MinecraftChannelIdentifier.from("cmib:fromproxy");
        fromServer = MinecraftChannelIdentifier.from("cmib:fromserver");

        server.getChannelRegistrar().register(fromProxy);
        consoleMessage("Registered channel!");

        server.getEventManager().register(this, new Events());
        server.getEventManager().register(this, new MessageListener());
        consoleMessage("Registered listeners!");
    }

    public void consoleMessage(final String message) {
        logger.info(message);
    }

    public MessageHandling getMessageHandling() {
        if (messageHandling == null)
            messageHandling = new MessageHandling();

        return messageHandling;
    }

    public MinecraftChannelIdentifier getFromProxyChannel() {
        return fromProxy;
    }

    public MinecraftChannelIdentifier getFromServerChannel() {
        return fromServer;
    }

    public ProxyServer getProxy() {
        return server;
    }

    public static CMIB getInstance() {
        return instance;
    }
}
