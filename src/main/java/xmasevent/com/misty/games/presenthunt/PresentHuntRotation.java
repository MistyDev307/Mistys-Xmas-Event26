package xmasevent.com.misty.games.presenthunt;

public class PresentHuntRotation
{
    private static final String[] SeekingOrder =
            {
                    "red",
                    "blue",
                    "black",
                    "green",
                    "purple",
                    "white",
                    "pink",
                    "yellow",
                    "orange"
            };

    private static int currentIndex = 0;

    public static String getCurrentSeekerTeam()
    {
        return SeekingOrder[currentIndex];
    }// end getCurrentSeekerTeam method

    public static void advanceRotation()
    {
        currentIndex++;

        if (currentIndex >= SeekingOrder.length)
        {
            currentIndex = 0;
        }// end if
    }// end advanceRotaion mehtod

}// end class
