package xmasevent.com.misty.tournament;

import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.*;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    public static void awardPoints(MinecraftServer server, String teamName, int points)
    {
        ServerScoreboard scoreboard = server.getScoreboard();

        Objective objective = scoreboard.getObjective("tournament");

        if (objective == null)
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("ERROR: Tournament leaderboard hasn't been created"),false);
            return;
        }// end if
        addPoints(scoreboard, objective, teamName, points);
    }// end awardPoints method

    public static int getTeamScore(MinecraftServer server, String teamName)
    {
        ServerScoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective("tournament");

        if (objective == null)
        {
            return 0;
        }// end if
        ScoreAccess score = scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(teamName), objective);
        return score.get();
    }// end getTeamScore method

    public record TeamStanding(String teamName,int score,int position)
    {

    }// end TeamStanding

    public static List<TeamStanding> getTeamStandings(MinecraftServer server)
    {
        ServerScoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective("tournament");

        List<TeamStanding> teamStandings = new ArrayList<>();

        if (objective == null)
        {
            return teamStandings;
        }// end if

        String[] teams =
                {
                        "Red", "Blue", "Green",
                        "Purple", "Black", "White",
                        "Pink", "Yellow", "Orange"
                };

        for (String team : teams)
        {
            int score = getTeamScore(server, team);

            teamStandings.add(new TeamStanding(team, score, 0));
        }// end for

        teamStandings.sort(Comparator.comparingInt(TeamStanding::score).reversed());

        List<TeamStanding> rankedStanding = new ArrayList<>();

        int position = 1;

        for (int i = 0; i < teamStandings.size(); i++)
        {
            TeamStanding standing = teamStandings.get(i);

            if (i > 0 && standing.score() < teamStandings.get(i - 1).score())
            {
                position = i + 1;
            }// end if

            rankedStanding.add(
                    new TeamStanding(standing.teamName(),standing.score(),position));
        }// end for

        return rankedStanding;
    }// end getTeamStandings method

    public static void announceStandings(MinecraftServer server)
    {
        List<TeamStanding> teamStanding = getTeamStandings(server);

        server.getPlayerList().broadcastSystemMessage(Component.literal("===== TOURNAMENT LEADERBOARD ====="),false);

        for (TeamStanding standing : teamStanding)
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("#" + standing.position()+ " " + standing.teamName()+ " - " + standing.score() + " points"),false);
        }// end for
    }// end announceStandings

    public static List<String> getWinningTeams(MinecraftServer server)
    {
        List<TeamStanding> standings = getTeamStandings(server);
        List<String> winners = new ArrayList<>();

        if (standings.isEmpty())
        {
            return winners;
        }// end if

        int highestScore = standings.get(0).score();

        for (TeamStanding standing : standings)
        {
            if (standing.score() == highestScore)
            {
                winners.add(standing.teamName());
            }// end if
            else
            {
                break;
            }// end else
        }// end for

        return winners;
    }// end getWinningTeams method

    public static void announceWinner(MinecraftServer server)
    {
        List<String> winners = getWinningTeams(server);

        if (winners.isEmpty())
        {
            return;
        }// end if

        String winnerMessage;

        if (winners.size() == 1)
        {
            winnerMessage = "THE WINNING TEAM IS " + winners.get(0).toUpperCase() + "!";
        }// end if
        else
        {
            winnerMessage = "IT'S A TIE! THE WINNERS ARE: "
                    + String.join(", ", winners).toUpperCase() + "!";
        }// end else

        server.getPlayerList().broadcastSystemMessage(Component.literal("================================"),false);

        server.getPlayerList().broadcastSystemMessage(Component.literal(winnerMessage),false);

        server.getPlayerList().broadcastSystemMessage(Component.literal("================================"),false);
    }// end announceWinner method
}// end class
