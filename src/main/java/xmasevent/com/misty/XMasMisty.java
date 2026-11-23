package xmasevent.com.misty;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xmasevent.com.misty.games.presenthunt.PresentHuntMechanics;
import xmasevent.com.misty.tournament.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import xmasevent.com.misty.comands.TournamentCommands;

public class XMasMisty implements ModInitializer {
	public static final String MOD_ID = "xmasmisty";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		TournamentManager.registerAllGames();

		GameVotingManager.registerTickEvents();

		RaceMechanics.register();
		COUNTDOWN.register();
		PresentHuntMechanics.registerAttackEvent();
		TournamentLeaderboard leaderboard = new TournamentLeaderboard();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			leaderboard.createLeaderBoard(server);
		});

		TournamentCommands.register();
		TournamentCommands.registerCountdownCommands();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static final TournamentCountdown COUNTDOWN = new TournamentCountdown();
}
