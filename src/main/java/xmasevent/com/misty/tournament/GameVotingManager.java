package xmasevent.com.misty.tournament;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import xmasevent.com.misty.XMasMisty;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameVotingManager
{
    private static class VotingPlatform
    {
        private final String gameName;
        private final String displayName;
        private final BlockPos position;
        private final Block block;
        private boolean available = true;


        public VotingPlatform(String gameName, String displayName, BlockPos position, Block block)
        {
            this.gameName = gameName;
            this.displayName = displayName;
            this.position = position;
            this.block = block;
        }// end VotingPlatform

        public boolean contains(BlockPos playerPosition)
        {
            return playerPosition.getY() == position.getY()
                    && playerPosition.getX() >= position.getX()
                    && playerPosition.getX() < position.getX() + 5
                    && playerPosition.getZ() >= position.getZ()
                    && playerPosition.getZ() < position.getZ() + 5;
        }// end contains
    }// end votingPlatform class
    private static final Map<String, VotingPlatform> VOTING_PLATFORMS = new HashMap<>();
    private static final Map<UUID, String> PLAYER_VOTES = new HashMap<>();
    private static boolean votingActive = false;


    public static void registerVotingPlatform(String gameName, String displayName, BlockPos position, Block block)
    {
        VotingPlatform platform = new VotingPlatform(gameName, displayName, position, block);

        VOTING_PLATFORMS.put(gameName, platform);
    }// end registerVotingPlatform

    public static void announceVotingOptions (MinecraftServer server)
    {
        votingActive = true;
        server.getPlayerList().broadcastSystemMessage(Component.literal("GAME VOTING HAS STARTED!"),false);

        server.getPlayerList().broadcastSystemMessage(Component.literal("Stand on a coloured block to vote"),false);

        for (VotingPlatform platform : VOTING_PLATFORMS.values())
        {
            if (platform.available)
            {
                String message = platform.block.getName().getString() + " - " + platform.displayName;

                Component announcement = Component.literal(message);

                server.getPlayerList().broadcastSystemMessage(announcement,false);
            }
        }// end for

    }// end announceVotingOptions method

    public static void checkPlayerPoints(MinecraftServer server)
    {
        if (!votingActive)
        {
            return;
        }// end if
        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            BlockPos playerPosistion = BlockPos.containing(player.getX(), player.getY() - 0.1, player.getZ());

            boolean standingOnPlatform = false;

            for (VotingPlatform platform : VOTING_PLATFORMS.values())
            {
                if (platform.available && platform.contains(playerPosistion))
                {
                    updateVotes(player, platform.gameName);

                    standingOnPlatform = true;

                    break;
                }// end if
            }// end for

            if (!standingOnPlatform)
            {
                removeVotes(player);
            }// end if
        }// end for
    }// end checkPlayerPoints method

    public static void registerTickEvents()
    {
        ServerTickEvents.END_SERVER_TICK.register(server -> {checkPlayerPoints(server);});
    }// end registerTickEvents method

    public static void updateVotes(ServerPlayer player, String gameName)
    {
        UUID playerUUID = player.getUUID();

        PLAYER_VOTES.put(playerUUID, gameName);
    }// end updateVotes method

    public static void removeVotes(ServerPlayer player)
    {
        PLAYER_VOTES.remove(player.getUUID());
    }// end removeVotes method

    public static Map<String, Integer> countVotes()
    {
        Map<String, Integer> votes = new HashMap<>();

        for (VotingPlatform platform : VOTING_PLATFORMS.values())
        {
            if (platform.available)
            {
                votes.put(platform.gameName, 0);
            }// end if
        }// end for

        for (String gameName : PLAYER_VOTES.values())
        {
            if (votes.containsKey(gameName))
            {
                votes.put(gameName, votes.get(gameName) + 1);
            }// end if
        }// end for

        return votes;
    }// end countVotes method

    public static String getWinningGame()
    {
        Map<String, Integer> votes = countVotes();

        String winningGame = null;
        int highestVote = 0;

        for (String gameName : VOTING_PLATFORMS.keySet())
        {
            int currentVotes = votes.get(gameName);

            if (currentVotes > highestVote)
            {
                highestVote = currentVotes;
                winningGame = gameName;
            }// end if
        }// end for

        return winningGame;
    }// end getWinningGame method

    public static String endVoting(MinecraftServer server)
    {
        votingActive = false;

        Map<String, Integer> votes = countVotes();

        int highestVote = 0;
        List<String> winningGames = new ArrayList<>();

        for (String gameName : VOTING_PLATFORMS.keySet())
        {
            int currentVotes = votes.get(gameName);

            if (currentVotes > highestVote)
            {
                highestVote = currentVotes;

                winningGames.clear();
                winningGames.add(gameName);
            }// end if
            else if (currentVotes == highestVote && currentVotes > 0 )
            {
                winningGames.add(gameName);
            }// end else if
        }// end for

        if (winningGames.isEmpty())
        {
            server.getPlayerList().broadcastSystemMessage(Component.literal("Nobody voted"),false);

            PLAYER_VOTES.clear();
            return null;
        }// end if

        String winningGame = winningGames.get(new  Random().nextInt(winningGames.size()));

        server.getPlayerList().broadcastSystemMessage(Component.literal("The next game is: " + winningGame),false);

        PLAYER_VOTES.clear();
        return winningGame;
    }// end endVoting method

    public static void markGamesAsPlayed(MinecraftServer server, String gameName)
    {
        VotingPlatform platform = VOTING_PLATFORMS.get(gameName);

        if (platform == null ||  !platform.available)
        {
            return;
        }// end if

        platform.available = false;

        PLAYER_VOTES.values().removeIf(vote -> !vote.contains(gameName));

        for (int x = 0; x < 5; x++)
        {
            for (int z = 0; z < 5; z++)
            {
                BlockPos blockPosition = platform.position.offset(x, 0, z);
                server.overworld().setBlock(blockPosition,Blocks.CONCRETE.gray().defaultBlockState(),3);
            }// end for z
        }// end for x
    }// end markGamesAsPlayed method

    public static void startingVoting(MinecraftServer server, int seconds)
    {
        PLAYER_VOTES.clear();
        announceVotingOptions(server);
        XMasMisty.COUNTDOWN.start(server,seconds,"Voting ends in:", completedServer -> endVoting(completedServer));
    }// end startingVoting method

    public static boolean hasAvailableGames()
    {
        for (VotingPlatform platform : VOTING_PLATFORMS.values())
        {
            if (platform.available)
            {
                return true;
            }// end if
        }// end for
        return false;
    }// end hasAvailableGames method
}// end class
