package xmasevent.com.misty.games.presenthunt;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PresentHuntRoles
{
    private static final Set<UUID> SEEKERS = new HashSet<>();
    private static final Set<UUID> HIDERS = new HashSet<>();
    private static final Set<UUID> ELIMINATED = new HashSet<>();

    public static void addSeeker(UUID playerUUID)
    {
        SEEKERS.add(playerUUID);
        HIDERS.remove(playerUUID);
        ELIMINATED.remove(playerUUID);
    }// end addSeeker method

    public static void addHider(UUID playerUUID)
    {
        HIDERS.add(playerUUID);
        SEEKERS.remove(playerUUID);
        ELIMINATED.remove(playerUUID);
    }// end addHider

    public static Boolean isSeeker(UUID playerUUID)
    {
        return SEEKERS.contains(playerUUID);
    }// end isSeeker method

    public static Boolean isHider(UUID playerUUID)
    {
        return HIDERS.contains(playerUUID);
    }// end isHider method

    public static Boolean isEliminated(UUID playerUUID)
    {
        return ELIMINATED.contains(playerUUID);
    }// end isEliminated method

    public static boolean hasHidersRemaining()
    {
        return !HIDERS.isEmpty();
    }// end hasHidersRemaining method

    public static void eliminateHider(UUID playerUUID)
    {
        HIDERS.remove(playerUUID);
        ELIMINATED.add(playerUUID);
    }// end eliminateHider method

    public static void clearRoles()
    {
        SEEKERS.clear();
        HIDERS.clear();
        ELIMINATED.clear();
    }// end clearRoles method
}// end class
