package xmasevent.com.misty.games.presenthunt;

import net.minecraft.server.MinecraftServer;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import xmasevent.com.misty.tournament.teams.TeamManager;

public class PresentHuntManager
{
    private static Boolean gameRunning = false;

    public static void start(MinecraftServer server)
    {

    }// end start

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
}// end class
