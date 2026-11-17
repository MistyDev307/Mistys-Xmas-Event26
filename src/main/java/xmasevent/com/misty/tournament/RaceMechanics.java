package xmasevent.com.misty.tournament;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class RaceMechanics
{
    public static void register()
    {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers())
            {
                BlockPos blockBelow = player.blockPosition().below();

                if (player.level().getBlockState(blockBelow).is(Blocks.EMERALD_BLOCK))
                {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST,
                            100,
                            3));
                }// end if

                if (player.level().getBlockState(blockBelow).is(Blocks.GOLD_BLOCK))
                {
                    player.addEffect(new MobEffectInstance(MobEffects.SPEED,
                            100,
                            3));
                }// end if
            }// end for
        });

    }// end register method
}// end RaceMech method