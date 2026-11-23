package xmasevent.com.misty.games.presenthunt;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PresentHuntLocations
{
    private static BlockPos hiderSpawn;
    private static BlockPos seekerSpawnCage;
    private static BlockPos SeekerReleaseSpawn;

    public static void setHiderSpawn(BlockPos position)
    {
        hiderSpawn = position;
    }// end setHiderSpawn method

    public static void setSeekerSpawn(BlockPos position)
    {
        seekerSpawnCage = position;
    }// end setSeekerSpawn method

    public static void setSeekerReleaseSpawn(BlockPos position)
    {
        SeekerReleaseSpawn = position;
    }// end setSeekerReleaseSpawn method
}// end class

