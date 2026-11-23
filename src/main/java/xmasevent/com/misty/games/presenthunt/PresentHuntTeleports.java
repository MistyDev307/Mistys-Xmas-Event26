package xmasevent.com.misty.games.presenthunt;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class PresentHuntTeleports
{
    public static void teleportPlayer(ServerPlayer player,BlockPos position)
    {
        player.teleportTo(position.getX() + 0.5 ,position.getY(),position.getZ() + 0.5);
    }// end teleportPlayer method
}// end class
