package xmasevent.com.misty.tournament;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import xmasevent.com.misty.XMasMisty;


public class TournamentManager
{
    private static boolean tournamentRunning = false;
    private static String currentGame = null;
    private static final Map<String, GameStarter> REGISTERED_GAMES = new HashMap<>();
    private static TournamentPhase currentPhase = TournamentPhase.NOT_STARTED;
    private static final List<String> PLAYED_GAMES = new ArrayList<>();

    public static void startTournament(MinecraftServer server)
    {
        if (tournamentRunning)
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("A tournament is already running"),false);
            return;
        }// end if
        tournamentRunning = true;
        TournamentLeaderboard.createLeaderBoard(server);
        server.getPlayerList().broadcastSystemMessage(Component.literal("The tournament has started"),false);

        startNextVotingRound(server);
    }// end startTournament method

    public static void startNextVotingRound(MinecraftServer server)
    {
        if (currentGame != null)
        {
            return;
        }// end if
        if (!tournamentRunning)
        {
            return;
        }// end if
        currentPhase = TournamentPhase.VOTING;
        GameVotingManager.startingVoting(server,60);
    }// end startNextVotingRound

    public static void onVotingFinished(MinecraftServer server)
    {
        String winningGame = GameVotingManager.endVoting(server);

        if (winningGame == null)
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("Nobody voted, Voting will restart in 10 seconds."),false);

            XMasMisty.COUNTDOWN.start(server,10,"Restarting voting in",completedServer -> startNextVotingRound(completedServer));
            currentPhase = TournamentPhase.WAITING;
            return;
        }// end if
        currentGame = winningGame;

        server.getPlayerList().broadcastSystemMessage(Component.literal("Preparing " + currentGame + "!"),false);
        startGame(server,winningGame);
    }// end onVotingFinished method

    public static void finishCurrentGame(MinecraftServer server)
    {
        if (currentGame == null)
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("ERROR: There is no game currently running"),false);
            return;
        }// end if

        GameVotingManager.markGamesAsPlayed(server,currentGame);
        PLAYED_GAMES.add(currentGame);
        TournamentLeaderboard.announceStandings(server);
        currentGame = null;

        if (GameVotingManager.hasAvailableGames())
        {
            startNextVotingRound(server);
        }// end if
        else
        {
            endTournament(server);
        }
    }// end finishCurrentGame method

    public static String getCurrentGame()
    {
        return currentGame;
    }// end getCurrentGame method

    public static boolean isTournamentRunning ()
    {
        return tournamentRunning;
    }// end isTournamentRunning method

    public static void registerGame(String gameName, GameStarter gameStarter)
    {
        if (REGISTERED_GAMES.containsKey(gameName))
        {
            throw new IllegalStateException("Game " + gameName + " is already registered!");
        }// end if

        REGISTERED_GAMES.put(gameName, gameStarter);
    }// end registerGame method

    public static void startGame(MinecraftServer server,String gameName)
    {
        if (currentGame != null)
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("A game is already running"),false);
            return;
        }// end if

        GameStarter starter = REGISTERED_GAMES.get(gameName);

        if (starter == null)
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("ERROR: game not registered"),false);
            return;
        }// end if
        currentGame = gameName;
        currentPhase = TournamentPhase.PLAYING;
        starter.start(server);
    }// end startGame method

    public static void endTournament(MinecraftServer server)
    {
        tournamentRunning = false;
        currentPhase = TournamentPhase.ENDED;
        currentGame = null;

        TournamentLeaderboard.announceStandings(server);
        TournamentLeaderboard.announceWinner(server);

        server.getPlayerList().broadcastSystemMessage(Component.literal("The tournament has ended"),false);
    }// end endTournament method

    public static void registerTournamentGame(String gameName, GameStarter gameStarter, String displayName, BlockPos platformPosition, Block platformBlock)
    {
        registerGame(gameName,gameStarter);

        GameVotingManager.registerVotingPlatform(gameName,displayName,platformPosition,platformBlock);
    }// end registerTournamentGame method

    public static void registerAllGames()
    {

    }// end registerAllGames method

    public static String getTournamentStatus()
    {
        if (!tournamentRunning)
        {
            return "Not Running";
        }// end if

        if (currentGame != null)
        {
            return "Playing " + currentGame;
        }// end game

        return "Waiting for voters";
    }// end getTournamentStatus

    public static Boolean isGameRegistered()
    {
        return REGISTERED_GAMES.containsKey(currentGame);
    }// end isGameRegistered

    public static int getRegisteredGameCount()
    {
        return REGISTERED_GAMES.size();
    }// end getRegisteredGameCount

    public enum TournamentPhase
    {
        NOT_STARTED,
        VOTING,
        WAITING,
        PLAYING,
        ENDED
    }// end enum

    public static TournamentPhase getCurrentPhase()
    {
        return currentPhase;
    }// end getCurrentPhase method

    public static String getCurrentPhaseName()
    {
        return switch (currentPhase)
        {
            case NOT_STARTED -> "Not Started";
            case VOTING -> "Voting";
            case WAITING -> "Waiting";
            case PLAYING -> "Playing";
            case ENDED -> "Ended";
        };// end switch
    }// end getCurrentPhaseName method

    public static int getCompletedGameCount()
    {
        return PLAYED_GAMES.size();
    }// end getCompletedGameCount method

    public static String getLastPlayedGame()
    {
        if (PLAYED_GAMES.isEmpty())
        {
            return null;
        }// end if
        return PLAYED_GAMES.get(PLAYED_GAMES.size()-1);
    }// end getLastPlayedGame

    public static Boolean hasGameBeenPlayed()
    {
        return PLAYED_GAMES.contains(currentGame);
    }// end hasGameBeenPlayed

}// end class
