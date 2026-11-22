package xmasevent.com.misty.tournament.teams;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.TeamColor;

import java.util.Map;
import java.util.Optional;

public class TeamManager {

    public static final Map<String, String> TEAMS = Map.of(
            "red", "Red",
            "blue", "Blue",
            "black", "Black",
            "green", "Green",
            "purple", "Purple",
            "white", "White",
            "pink", "Pink",
            "yellow", "Yellow",
            "orange", "Orange");

    public static void registerTeams(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();

        for (String teamName : TEAMS.keySet()) {
            PlayerTeam playerTeam = scoreboard.getPlayerTeam(teamName);

            if (playerTeam == null) {
                playerTeam = scoreboard.addPlayerTeam(teamName);
            }// end if

            playerTeam.setDisplayName(Component.literal(TEAMS.get(teamName)));

            playerTeam.setColor(Optional.of(getTeamColor(teamName)));
        }// end for
    }// end registerTeams method

    public static boolean addPlayer(MinecraftServer server, String playerName, String teamName) {
        Scoreboard scoreboard = server.getScoreboard();

        PlayerTeam playerTeam = scoreboard.getPlayerTeam(teamName);

        if (playerTeam == null) {
            return false;
        }

        if (playerTeam.getPlayers().contains(playerName)) {
            return false;
        }

        if (playerTeam.getPlayers().size() >= 4) {
            return false;
        }

        scoreboard.removePlayerFromTeam(playerName);

        scoreboard.addPlayerToTeam(playerName, playerTeam);

        return true;
    }

    private static TeamColor getTeamColor(String team) {
        return switch (team) {
            case "red" -> TeamColor.RED;
            case "blue" -> TeamColor.BLUE;
            case "green" -> TeamColor.GREEN;
            case "purple" -> TeamColor.DARK_PURPLE;
            case "black" -> TeamColor.BLACK;
            case "white" -> TeamColor.WHITE;
            case "pink" -> TeamColor.LIGHT_PURPLE;
            case "yellow" -> TeamColor.YELLOW;
            case "orange" -> TeamColor.GOLD;
            default -> TeamColor.WHITE;};
    }// end teamColor method
}// end class
