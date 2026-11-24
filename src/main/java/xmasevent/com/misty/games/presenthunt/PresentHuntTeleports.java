package xmasevent.com.misty.games.presenthunt;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import xmasevent.com.misty.tournament.TournamentLocations;

public class PresentHuntTeleports
{
    public static void teleportPlayer(ServerPlayer player,BlockPos position)
    {
        player.teleportTo(position.getX() + 0.5 ,position.getY(),position.getZ() + 0.5);
    }// end teleportPlayer method

    public static void teleportHiders(MinecraftServer server, BlockPos position)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            if (PresentHuntRoles.isHider(player.getUUID()))
            {
                teleportPlayer(player,position);
            }// end if
        }// end for
    }// end teleportHiders method

    public static void teleportSeekers(MinecraftServer server, BlockPos position)
    {
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            if (PresentHuntRoles.isSeeker(player.getUUID()))
            {
                teleportPlayer(player,position);
            }// end if
        }// end for
    }// end teleportSeekers method

    public static void teleportEveryoneToLobby(MinecraftServer server)
    {
        BlockPos lobbySpawn = TournamentLocations.getLobbySpawn();

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            teleportPlayer(player,lobbySpawn);
        }// end for
    }// end teleportEveryoneToLobby method
}// end class
