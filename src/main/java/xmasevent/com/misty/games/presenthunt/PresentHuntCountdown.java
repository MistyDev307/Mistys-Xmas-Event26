package xmasevent.com.misty.games.presenthunt;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class PresentHuntCountdown
{
    private static int ticksRemaining = 0;
    private static Consumer<MinecraftServer> onFinished;
    private static boolean running = false;

    public static void register()
    {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {

            if (!running)
            {
                return;
            }// end if
            ticksRemaining--;

            if (ticksRemaining <= 0)
            {
                running = false;
                if (onFinished != null)
                {
                    onFinished.accept(server);
                }// end if
            }// end if
        });
    }// end register method

    public static void start(int seconds, Consumer<MinecraftServer> callback)
    {
        ticksRemaining = seconds * 20;
        onFinished = callback;
        running = true;
    }// end start method
}// end class
