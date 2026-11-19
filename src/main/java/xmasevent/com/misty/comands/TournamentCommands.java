package xmasevent.com.misty.comands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import xmasevent.com.misty.XMasMisty;

import java.util.Map;

public class TournamentCommands {

    private static final Map<String, String> TEAMS = Map.of(
            "red", "Red",
            "blue", "Blue",
            "green", "Green",
            "purple", "Purple",
            "black", "Black",
            "white", "White",
            "pink", "Pink",
            "yellow", "Yellow",
            "orange", "Orange"
    );

    public static void register() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(
                    Commands.literal("teampoints")
                            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))

                            .then(Commands.argument("team", StringArgumentType.word())
                                    .suggests((context, builder) ->
                                            SharedSuggestionProvider.suggest(TEAMS.keySet(), builder))

                                    .then(Commands.argument("points", IntegerArgumentType.integer())
                                            .executes(context -> {

                                                String team = StringArgumentType.getString(context, "team").toLowerCase();
                                                int points = IntegerArgumentType.getInteger(context, "points");

                                                return addTeamPoints(context.getSource(), team, points);}))));
        });
    }// end register method

    private static int addTeamPoints(
            net.minecraft.commands.CommandSourceStack source,
            String team,
            int points) {

        String displayName = TEAMS.get(team);

        if (displayName == null) {
            source.sendFailure(Component.literal("That team does not exist!"));
            return 0;
        }

        var scoreboard = source.getServer().getScoreboard();

        var objective = scoreboard.getObjective("tournament");

        if (objective == null) {
            source.sendFailure(Component.literal("Tournament scoreboard has not been created!"));
            return 0;
        }

        if (scoreboard.getPlayerTeam(team) == null) {
            source.sendFailure(Component.literal("That team has not been registered!"));
            return 0;
        }

        var score = scoreboard.getOrCreatePlayerScore(
                net.minecraft.world.scores.ScoreHolder.forNameOnly(displayName),
                objective
        );

        score.add(points);

        source.sendSuccess(
                () -> Component.literal(
                        "Added " + points + " points to " + displayName +
                                "! New score: " + score.get()), true);

        return 1;
    }

    // Countdown commands
    public static void registerCountdownCommands() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(
                    Commands.literal("countdown")
                            .requires(source -> source.permissions()
                                    .hasPermission(Permissions.COMMANDS_MODERATOR))

                            // Pause countdown
                            .then(Commands.literal("pause")
                                    .executes(context -> {

                                        XMasMisty.COUNTDOWN.pause();

                                        context.getSource().sendSuccess(
                                                () -> Component.literal("Countdown paused!"),
                                                true
                                        );

                                        return 1;
                                    }))

                            // Resume countdown
                            .then(Commands.literal("resume")
                                    .executes(context -> {

                                        XMasMisty.COUNTDOWN.resume();

                                        context.getSource().sendSuccess(
                                                () -> Component.literal("Countdown resumed!"),
                                                true
                                        );

                                        return 1;
                                    }))

                            // Start countdown with chosen duration
                            .then(Commands.literal("start")
                                    .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                                            .executes(context -> {

                                                int seconds = IntegerArgumentType.getInteger(context, "seconds");

                                                XMasMisty.COUNTDOWN.start(
                                                        context.getSource().getServer(),
                                                        seconds
                                                );

                                                context.getSource().sendSuccess(
                                                        () -> Component.literal("Countdown started for " + seconds + " seconds!"),
                                                        true
                                                );

                                                return 1;}))));
        });
    } // end registerCountdownCommands method

} // end class