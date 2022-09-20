package net.lortservers.protocolobserver;

import net.minecraft.server.NetServerHandler;

import java.lang.reflect.Method;
import java.util.Map;

public interface ProtocolObserver {
    static ProtocolObserver ofNetServerHandler() {
        return ofHandlerClass(NetServerHandler.class);
    }

    static ProtocolObserver ofHandlerClass(Class<?> handlerClass) {
        return new ProtocolObserverImpl(handlerClass);
    }

    Map<Class<?>, Method> getPacketHandlers();

    void interceptHandler(Class<?> packet, Class<?> listener);
}
