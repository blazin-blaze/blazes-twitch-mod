package net.blazinblaze.blazetwitchmod.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;

public class CloseWindowTimer implements ClientTickEvents.EndTick {
    public static final CloseWindowTimer INSTANCE = new CloseWindowTimer();

    private static final ArrayList<Long> ongoingTimers = new ArrayList<Long>();

    public void setTimerAndUser(long ticksUntilSomething) {
        ongoingTimers.add(ticksUntilSomething);
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        for (var iter = ongoingTimers.listIterator(); iter.hasNext(); ) {
            long currentTimer = iter.next();
            if ((currentTimer - 1) <= 0L) {
                client.getWindow().close();
                iter.remove();
            }else {
                iter.set(currentTimer - 1);
            }
        }
    }
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE);
    }
}
