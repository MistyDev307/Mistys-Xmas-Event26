package xmasevent.com.misty.tournament;

import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Optional;

public class TournamentLeaderboard
{
    public static void createLeaderBoard(MinecraftServer server)
    {
        ServerScoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective("tournament");

        if (objective == null)
        {
            objective = scoreboard.addObjective(
                    "tournament",
                    ObjectiveCriteria.DUMMY,
                    Component.literal("Christmas Games"),
                    ObjectiveCriteria.RenderType.INTEGER,
                    false,
                    null
            );
        }// end if

        scoreboard.setDisplayObjective(
                DisplaySlot.SIDEBAR,
                objective
        );

        PlayerTeam red = scoreboard.addPlayerTeam("red");
        PlayerTeam blue = scoreboard.addPlayerTeam("blue");
        PlayerTeam green = scoreboard.addPlayerTeam("green");
        PlayerTeam purple = scoreboard.addPlayerTeam("purple");
        PlayerTeam black = scoreboard.addPlayerTeam("black");
        PlayerTeam white = scoreboard.addPlayerTeam("white");
        PlayerTeam pink = scoreboard.addPlayerTeam("pink");
        PlayerTeam yellow = scoreboard.addPlayerTeam("yellow");
        PlayerTeam orange = scoreboard.addPlayerTeam("orange");

        red.setDisplayName(Component.literal("Red"));
        red.setColor(Optional.of(TeamColor.RED));

        blue.setDisplayName(Component.literal("Blue"));
        blue.setColor(Optional.of(TeamColor.BLUE));

        green.setDisplayName(Component.literal("Green"));
        green.setColor(Optional.of(TeamColor.GREEN));

        purple.setDisplayName(Component.literal("Purple"));
        purple.setColor(Optional.of(TeamColor.DARK_PURPLE));

        black.setDisplayName(Component.literal("Black"));
        black.setColor(Optional.of(TeamColor.BLACK));

        white.setDisplayName(Component.literal("White"));
        white.setColor(Optional.of(TeamColor.WHITE));

        pink.setDisplayName(Component.literal("Pink"));
        pink.setColor(Optional.of(TeamColor.LIGHT_PURPLE));

        yellow.setDisplayName(Component.literal("Yellow"));
        yellow.setColor(Optional.of(TeamColor.YELLOW));

        orange.setDisplayName(Component.literal("Orange"));
        orange.setColor(Optional.of(TeamColor.GOLD));


        initialiseTeamScore(scoreboard, objective, "Red");
        initialiseTeamScore(scoreboard, objective, "Blue");
        initialiseTeamScore(scoreboard, objective, "Green");
        initialiseTeamScore(scoreboard, objective, "Purple");
        initialiseTeamScore(scoreboard, objective, "Black");
        initialiseTeamScore(scoreboard, objective, "White");
        initialiseTeamScore(scoreboard, objective, "Pink");
        initialiseTeamScore(scoreboard, objective, "Yellow");
        initialiseTeamScore(scoreboard, objective, "Orange");
    }// end createLeaderBoard method

    private static void initialiseTeamScore(ServerScoreboard scoreboard,Objective objective,String teamName)
    {
        ScoreAccess score = scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(teamName), objective);
        score.set(0);
    }// end initialiseTeamScore

    public static void addPoints(Scoreboard scoreboard, Objective objective, String teamName, int points)
    {
        ScoreAccess score = scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(teamName), objective);
        score.add(points);
    }// end addPoints
}// end class
