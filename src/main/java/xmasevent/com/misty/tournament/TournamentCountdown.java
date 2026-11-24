package xmasevent.com.misty.tournament;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import java.util.UUID;
import java.util.function.Consumer;

import static xmasevent.com.misty.XMasMisty.COUNTDOWN;

public class TournamentCountdown
{
    private final ServerBossEvent bossBar = new ServerBossEvent(
            UUID.randomUUID(),
            Component.literal("Countdown"),
            BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS);

    private int totalTicks;
    private int remainingTicks;
    private boolean running = false;
    private boolean paused = false;
    private String message = "Time left";
    private Consumer<MinecraftServer> onFinish;

    public void register()
    {
        ServerTickEvents.END_SERVER_TICK.register(serverTickEvent ->
        {
            if (!running || paused)
            {
                return;
            }// end if
            remainingTicks --;

            updateBossBar();

            if (remainingTicks <= 0)
            {
                running = false;
                bossBar.removeAllPlayers();

                if (onFinish != null)
                {
                    Consumer<MinecraftServer> callback = onFinish;
                    onFinish = null;
                    callback.accept(serverTickEvent);
                }// end if
            }// end if
        });
    }// end register method

    public void start(MinecraftServer server, int seconds, String message) {

        if (seconds <= 0) {
            throw new IllegalArgumentException("Countdown must be greater than 0.");
        }// end if

        this.message = message;

        totalTicks = seconds * 20;
        remainingTicks = totalTicks;
        running = true;
        paused = false;

        bossBar.removeAllPlayers();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            bossBar.addPlayer(player);
        }// end for

        updateBossBar();
    }// end start method

    private void updateBossBar()
    {
        bossBar.setProgress((float) remainingTicks / totalTicks);

        int totalSeconds = (remainingTicks + 19) / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        bossBar.setName(Component.literal(String.format("Time left: %02d:%02d", minutes, seconds)));

        bossBar.setName(Component.literal(
                message + ": " +
                        String.format("%02d:%02d", minutes, seconds)
        ));
    }// end updateBossBar method

    public void pause() {
        if (running) {
            paused = true;
        }
    }

    public void resume() {
        if (running) {
            paused = false;
        }
    }

    public void start(MinecraftServer server, int seconds, String message, Consumer<MinecraftServer> onFinish)
    {
        this.onFinish = onFinish;

        start(server, seconds, message);
    }// end start method

    public void stop()
    {
        running = false;
        paused = false;

        totalTicks = 0;
        remainingTicks = 0;

        onFinish = null;
        bossBar.removeAllPlayers();
    }// end stop method
}// end class
