package xmasevent.com.misty.games.presenthunt;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import xmasevent.com.misty.XMasMisty;
import xmasevent.com.misty.tournament.teams.TeamManager;
import xmasevent.com.misty.tournament.TournamentLocations;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PresentHuntManager
{
    private static Boolean gameRunning = false;
    private static boolean roundRunning = false;
    private static final Map<UUID, GameType> ORIGINAL_GAME_MODES = new HashMap<>();
    private static int completedRounds = 0;

    public static Boolean start(MinecraftServer server)
    {
        String seekerTeam = PresentHuntRotation.getCurrentSeekerTeam();

        return start(server, seekerTeam);
    }

    public static Boolean start(MinecraftServer server, String seekerTeamName)
    {
        if (gameRunning)
        {
            return false;
        }// end if

        BlockPos hiderSpawn = PresentHuntLocations.getHiderSpawn();
        BlockPos seekerCageSpawn = PresentHuntLocations.getSeekerSpawnCage();

        if (hiderSpawn == null || seekerCageSpawn == null)
        {
            return false;
        }// end if

        if (!assignRoles(server, seekerTeamName))
        {
            return false;
        }// end if

        ORIGINAL_GAME_MODES.clear();

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            ORIGINAL_GAME_MODES.put(player.getUUID(),player.gameMode.getGameModeForPlayer());
        }// end for

        PresentHuntTeleports.teleportHiders(server, hiderSpawn);
        PresentHuntTeleports.teleportSeekers(server, seekerCageSpawn);
        applySeekerBlindness(server);
        XMasMisty.COUNTDOWN.start(server,15,"Seekers released in ",PresentHuntManager::releaseSeekers);

        completedRounds = 0;
        gameRunning = true;
        roundRunning = true;


        return true;
    }// end start method

    public static void stop(MinecraftServer server)
    {

    }// end stop method

    public static Boolean assignRoles(MinecraftServer server, String seekerTeamName)
    {
        if (!TeamManager.TEAMS.containsKey(seekerTeamName))
        {
            return false;
        }// end if

        PresentHuntRoles.clearRoles();

        Scoreboard scoreboard = server.getScoreboard();

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            for (String teamName : TeamManager.TEAMS.keySet())
            {
                PlayerTeam team = scoreboard.getPlayerTeam(teamName);

                if (team != null && team.getPlayers().contains(player.getScoreboardName()))
                {
                    if (teamName.equals(seekerTeamName))
                    {
                        PresentHuntRoles.addSeeker(player.getUUID());
                    }// end if
                    else
                    {
                        PresentHuntRoles.addHider(player.getUUID());
                    }

                    break;
                }// end if
            }// end for
        }// end for
        return true;
    }// end assignRoles

    public static void applySeekerBlindness(MinecraftServer server)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            if (PresentHuntRoles.isSeeker(player.getUUID()))
            {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 30, 0));
            }// end if
        }// end for
    }// end applySeekerBlindness method

    public static void releaseSeekers(MinecraftServer server)
    {
        BlockPos releaseSpawn = PresentHuntLocations.getSeekerReleaseSpawn();

        if (releaseSpawn == null)
        {
            return;
        }// end if

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            if (PresentHuntRoles.isSeeker(player.getUUID()))
            {
                PresentHuntTeleports.teleportPlayer(player, releaseSpawn);
                player.removeEffect(MobEffects.BLINDNESS);
            }// end if


        }// end for
        // Start the 5-minute hunting timer
        XMasMisty.COUNTDOWN.start(server,300,"Time left",countdownServer -> endRound(countdownServer,false));
    }// end releaseSpawn method

    public static void endRound(MinecraftServer server, boolean seekersWon)
    {
        if (!gameRunning || !roundRunning)
        {
            return;
        }// end if

        roundRunning = false;

        XMasMisty.COUNTDOWN.stop();

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            player.removeEffect(MobEffects.BLINDNESS);
        }// end for

        String winnerMessage = seekersWon ? "The seekers have won the round!": "The hiders have won the round!";

        server.getPlayerList().broadcastSystemMessage(Component.literal(winnerMessage),false);

        PresentHuntRotation.advanceRotation();
        completedRounds++;

        if (completedRounds >= 9)
        {
            endGame(server);
            return;
        }// end if
        XMasMisty.COUNTDOWN.start(server,10,"Next round starts in", PresentHuntManager::startNextRound);
    }// end endRound Method

    public static void restorePlayers(MinecraftServer server)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            GameType originalGameMode = ORIGINAL_GAME_MODES.get(player.getUUID());

            if (originalGameMode != null)
            {
                player.setGameMode(originalGameMode);
            }// end if
        }// end for
    }// end restorePlayers method

    public static void registerDisconnectEvent()
    {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.player;

            ORIGINAL_GAME_MODES.remove(player.getUUID());

            if (!gameRunning)
            {
                return;
            }// end if

            if (PresentHuntRoles.isHider(player.getUUID()))
            {
                PresentHuntRoles.eliminateHider(player.getUUID());

                if (!PresentHuntRoles.hasHidersRemaining())
                {
                    endRound(server, true);
                }// end if
            }// end for
        });
    }// end registerDisconnectEvent method

    public static void endGame(MinecraftServer server)
    {
        if (!gameRunning)
        {
            return;
        }// end if

        gameRunning = false;
        roundRunning = false;

        XMasMisty.COUNTDOWN.stop();

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            player.removeEffect(MobEffects.BLINDNESS);
        }//  end for

        restorePlayers(server);

        ORIGINAL_GAME_MODES.clear();

        PresentHuntRoles.clearRoles();

        PresentHuntTeleports.teleportEveryoneToLobby(server);

        server.getPlayerList().broadcastSystemMessage(Component.literal("Present Hunt has finished!"),false);
    }// end endGame method

    public static void startNextRound(MinecraftServer server)
    {
        if (!gameRunning || roundRunning)
        {
            return;
        }// end if

        if (completedRounds >= 9)
        {
            endGame(server);
            return;
        }// end if

        String seekerTeam = PresentHuntRotation.getCurrentSeekerTeam();
        restorePlayers(server);

        if (!assignRoles(server, seekerTeam))
        {
            endGame(server);
            return;
        }// end if

        BlockPos hiderSpawn = PresentHuntLocations.getHiderSpawn();
        BlockPos seekerCageSpawn = PresentHuntLocations.getSeekerSpawnCage();

        if (hiderSpawn == null || seekerCageSpawn == null)
        {
            endGame(server);
            return;
        }// end if

        PresentHuntTeleports.teleportHiders(server, hiderSpawn);
        PresentHuntTeleports.teleportSeekers(server, seekerCageSpawn);

        applySeekerBlindness(server);
        roundRunning = true;

        XMasMisty.COUNTDOWN.start(server,15,"Seekers released in", PresentHuntManager::releaseSeekers);

    }// end startNextRound method
}// end class
