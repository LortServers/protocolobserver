package net.lortservers.protocolobserver;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProtocolObserverImpl implements ProtocolObserver {
    protected final Map<Class<?>, Method> packetHandlers = new HashMap<>();
    protected final Class<?> handlerClass;

    static {
        ByteBuddyAgent.install();
    }

    protected ProtocolObserverImpl(Class<?> handlerClass) {
        this.handlerClass = handlerClass;

        // scan for packet handlers
        for (final Method method : NetServerHandler.class.getDeclaredMethods()) {
            if (method.getReturnType() != void.class) { // only void returning methods
                continue;
            }

            // only single-param methods accepting a net.minecraft.server.Packet subclass
            final Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1 || !Packet.class.isAssignableFrom(paramTypes[0])) {
                continue;
            }

            packetHandlers.put(paramTypes[0], method);
        }
    }

    @Override
    public Map<Class<?>, Method> getPacketHandlers() {
        return Collections.unmodifiableMap(packetHandlers);
    }

    @Override
    public void interceptHandler(Class<?> packet, Class<?> advice) {
        new ByteBuddy()
            .redefine(handlerClass)
            .visit(
                Advice.to(advice)
                    .on(ElementMatchers.is(Objects.requireNonNull(packetHandlers.get(packet), "Unknown packet")))
            )
            .make()
            .load(
                ProtocolObserverImpl.class.getClassLoader(),
                ClassReloadingStrategy.fromInstalledAgent()
            );
    }
}
