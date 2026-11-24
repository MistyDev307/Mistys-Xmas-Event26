package xmasevent.com.misty.games.presenthunt;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.world.InteractionResult;

public class PresentHuntMechanics
{
    public static Boolean eliminatePlayer(ServerPlayer attacker, ServerPlayer target, MinecraftServer server)
    {
        if (!PresentHuntRoles.isSeeker(attacker.getUUID()))
        {
            return false;
        }// end if

        if (!PresentHuntRoles.isHider(target.getUUID()))
        {
            return false;
        }// end if

        target.setGameMode(GameType.SPECTATOR);

        PresentHuntRoles.eliminateHider(target.getUUID());

        if (!PresentHuntRoles.hasHidersRemaining())
        {
            PresentHuntManager.endRound(server,true);
        }// end if

        return true;
    }// end eliminatePlayer method

    public static void registerAttackEvent()
    {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
        {
            if (level.isClientSide())
            {
                return InteractionResult.PASS;
            }// end if
            if (!(entity instanceof ServerPlayer target))
            {
                return InteractionResult.PASS;
            }// end if

            if (!(player instanceof ServerPlayer attacker))
            {
                return InteractionResult.PASS;
            }// end if
            if (attacker.isSpectator())
            {
                return InteractionResult.PASS;
            }// end if


            MinecraftServer server = attacker.level().getServer();
            Boolean eliminated = eliminatePlayer(attacker, target, server);

            if (eliminated)
            {
                return InteractionResult.SUCCESS;
            }// end if

            return InteractionResult.PASS;
        });
    }// end registerAttackEvent method
}// end class
