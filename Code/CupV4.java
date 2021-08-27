import java.util.ArrayList;

// The cup class is primarily maintained in this code for an easy way to have
// access to an opposite cup. It is a matter of convenience.

public class CupV4
{
    private int seedCount, whoseCup, i;
    private CupV4 nextCup, oppositeCup;

    // Captured cup constructor
    
    public CupV4(int seedCountin, int whoseCupin)
    {
        seedCount = seedCountin;
        whoseCup = whoseCupin;
        nextCup = null;
        oppositeCup = null;
    }
    
    // All other cups
    public CupV4(int seedCountin, int whoseCupin, CupV4 nextCupin)
    {
        seedCount = seedCountin;
        whoseCup = whoseCupin;
        nextCup = nextCupin;
        oppositeCup = null;
    }

    public int getSeedCount()
    {
        return seedCount;
    }

    public void addOneSeed()
    {
        seedCount++;
    }

    public void addSomeSeeds(int seedsin)
    {
        seedCount += seedsin;
    }

    public int getWhoseCup()
    {
        return whoseCup;
    }

    public CupV4 getNextCup()
    {
        return nextCup;
    }
    
    public CupV4 getOpposite() // Gets opposite cup from player's (empty) side
    {
        return oppositeCup;
    }

    public void setNextCup(CupV4 nextCupin)
    {
        nextCup = nextCupin;
    }
    public void setOpposite(CupV4 opposite)
    {
        oppositeCup = opposite;
    }

    public int removeSeeds()
    {   
        int temp = getSeedCount();
        seedCount = 0;
        
        return temp;
    }
    
    public int removeAll()
    {
        int temp = seedCount;
        seedCount = 0;
        return temp;
    }
}