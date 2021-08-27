import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;

// The game board for Wari.

public class BoardV4
{
    private CupV4[][] gameBoard = new CupV4[3][7];
    private CupV4[] captured = new CupV4[3];
    
    public CupV4[][] getGameBoard()
    {
        return gameBoard;
    }
    public CupV4[] getCaptured()
    {
        return captured;
    }
    

    // This is a copy constructor. When trees are generated, board copies are inserted
    // into the DefaultMutableTreeNodes, then new boards are made from the old ones.

    public BoardV4(CupV4[][] board, CupV4[] cap)
    {
        captured[1] = new CupV4(cap[1].getSeedCount(), 1);

        gameBoard[1][6] = new CupV4(board[1][6].getSeedCount(), 1);
        gameBoard[2][6] = new CupV4(board[2][6].getSeedCount(), 2);
        
        captured[2] = new CupV4(cap[2].getSeedCount(), 2);

        for(int i = 5; i > 0; i--)
        {
            gameBoard[1][i] = new CupV4(board[1][i].getSeedCount(), 1, gameBoard[1][i + 1]);
            gameBoard[2][i] = new CupV4(board[2][i].getSeedCount(), 2, gameBoard[2][i + 1]);
        }
        gameBoard[1][1].setOpposite(gameBoard[2][6]);
        gameBoard[1][2].setOpposite(gameBoard[2][5]);
        gameBoard[1][3].setOpposite(gameBoard[2][4]);
        gameBoard[1][4].setOpposite(gameBoard[2][3]);
        gameBoard[1][5].setOpposite(gameBoard[2][2]);
        gameBoard[1][6].setOpposite(gameBoard[2][1]);
        
        gameBoard[2][1].setOpposite(gameBoard[1][6]);
        gameBoard[2][2].setOpposite(gameBoard[1][5]);
        gameBoard[2][3].setOpposite(gameBoard[1][4]);
        gameBoard[2][4].setOpposite(gameBoard[1][3]);
        gameBoard[2][5].setOpposite(gameBoard[1][2]);
        gameBoard[2][6].setOpposite(gameBoard[1][1]);

        gameBoard[1][6].setNextCup(gameBoard[2][1]);
        gameBoard[2][6].setNextCup(gameBoard[1][1]);
    }
    
    
    // Default board constructor. Used to start the game.

    public BoardV4()
    {
        int left = 193;
        int right = 498;
        
        captured[1] = new CupV4(0, 1);

        gameBoard[1][6] = new CupV4(3, 1);
        gameBoard[2][6] = new CupV4(3, 2);
        
        captured[2] = new CupV4(0, 2);

        for(int i = 5; i > 0; i--)
        {
            gameBoard[1][i] = new CupV4(3, 1, gameBoard[1][i + 1]);
            gameBoard[2][i] = new CupV4(3, 2, gameBoard[2][i + 1]);
        }
        gameBoard[1][1].setOpposite(gameBoard[2][6]);
        gameBoard[1][2].setOpposite(gameBoard[2][5]);
        gameBoard[1][3].setOpposite(gameBoard[2][4]);
        gameBoard[1][4].setOpposite(gameBoard[2][3]);
        gameBoard[1][5].setOpposite(gameBoard[2][2]);
        gameBoard[1][6].setOpposite(gameBoard[2][1]);
        
        gameBoard[2][1].setOpposite(gameBoard[1][6]);
        gameBoard[2][2].setOpposite(gameBoard[1][5]);
        gameBoard[2][3].setOpposite(gameBoard[1][4]);
        gameBoard[2][4].setOpposite(gameBoard[1][3]);
        gameBoard[2][5].setOpposite(gameBoard[1][2]);
        gameBoard[2][6].setOpposite(gameBoard[1][1]);

        gameBoard[1][6].setNextCup(gameBoard[2][1]);
        gameBoard[2][6].setNextCup(gameBoard[1][1]);
    }
    
    public int capturedCount(int turn)
    {
        return captured[turn].getSeedCount();
    }
    
    // Assumes empty cups are not chosen; that's handled in WariV4.

    public void moveBoard(int whoseTurn, int whichCup)
	{
		CupV4 cuppy = gameBoard[whoseTurn][whichCup];
		int handfull = cuppy.removeSeeds();
        int count = 6;

		CupV4 tempReference = cuppy.getNextCup();

		while(handfull != 0)
		{
            if(tempReference.getWhoseCup() != cuppy.getWhoseCup()) // Adds one seed to captured cup.
            {
                if(count % 6 == 0) // Prevents adding to wrong side.
                {
                    captured[cuppy.getWhoseCup()].addOneSeed();
                    handfull--;
                    count = 0;
                }
                count++;
            }
            if(handfull != 0) // Typical case of adding seed to a cup.
            {
                tempReference.addOneSeed();
                handfull--;
            }
            if(tempReference.getWhoseCup() == cuppy.getWhoseCup() && handfull == 0)
            {
                if(tempReference.getSeedCount() == 1) // Last seed ends in empty cup
                {
                    captured[cuppy.getWhoseCup()].addSomeSeeds(tempReference.getOpposite().removeAll());
                }
            }
            tempReference = tempReference.getNextCup();
		}
        if(checkGameOver() > -1) // Removes all remaining seeds to that side's capture cup.
        {
            if(checkGameOver() == 1)
            {
                captured[2].addSomeSeeds(gameBoard[2][1].removeAll() + gameBoard[2][2].removeAll() + 
                    gameBoard[2][3].removeAll() + gameBoard[2][4].removeAll() + gameBoard[2][5].removeAll() + 
                    gameBoard[2][6].removeAll());
            }
            else
            {
                captured[1].addSomeSeeds(gameBoard[1][1].removeAll() + gameBoard[1][2].removeAll() + 
                    gameBoard[1][3].removeAll() + gameBoard[1][4].removeAll() + gameBoard[1][5].removeAll() + 
                    gameBoard[1][6].removeAll());
            }
        }
	}
    
    public int seedsInCup(int whoseTurn, int whichCup)
    {
        return gameBoard[whoseTurn][whichCup].getSeedCount();
    }
    
    public int checkGameOver()
    {
        int maxSeeds = -1;
        
        for(int i = 1; i < 7; i++)
        {
            int temp = gameBoard[1][i].getSeedCount();
            if(temp > maxSeeds)
                maxSeeds = temp;
        }
        if(maxSeeds == 0)
            return 1;
            
        maxSeeds = -1;
        
        for(int i = 1; i < 7; i++)
        {
            int temp = gameBoard[2][i].getSeedCount();
            if(temp > maxSeeds)
                maxSeeds = temp;
        }
        if(maxSeeds == 0)
            return 2;
        else
            return -1;
    }

	public int getCupNumber(CupV4 cupIn)
	{
		int retval = 1;
		for(int i = 1; i <= 6; i++)
		{
			if(gameBoard[1][i] == cupIn || gameBoard[2][i] == cupIn)
			  retval = i;
		}
		return retval;
	}
    
    // This form of the method was used for the function form of...
    // S1 Captured * x1 + S2 Captured * x2 + Cup1 * x3 + ... + Cup6 * x8
    // Essentially, it introduces a cup bias.
    
    public double getNetScore(int playTurn, ArrayList<Double> variables)
    {
        double
            x1 = variables.get(0),
            x2 = variables.get(1),
            x3 = variables.get(2),
            x4 = variables.get(3),
            x5 = variables.get(4),
            x6 = variables.get(5),
            x7 = variables.get(6),
            x8 = variables.get(7);
            
        if(playTurn == 1)
        {
            return captured[1].getSeedCount() * x1 + captured[2].getSeedCount() * x2 + gameBoard[1][1].getSeedCount() * x3
                + gameBoard[1][2].getSeedCount() * x4 + gameBoard[1][3].getSeedCount() * x5 + gameBoard[1][4].getSeedCount() * x6
                + gameBoard[1][5].getSeedCount() * x7 + gameBoard[1][6].getSeedCount() * x8;
        }
        else
        {
            return captured[2].getSeedCount() * x1 + captured[1].getSeedCount() * x2 + gameBoard[2][1].getSeedCount() * x3
                + gameBoard[2][2].getSeedCount() * x4 + gameBoard[2][3].getSeedCount() * x5 + gameBoard[2][4].getSeedCount() * x6
                + gameBoard[2][5].getSeedCount() * x7 + gameBoard[2][6].getSeedCount() * x8;
        }
    }
    
    // Since the two take turns going first, the scores need to be kept straight.
    // Negative values indicate a P2 win, and positive indicates a P1 win.
    // This value is passed to the ES part of this problem.
    
    public int getFitness()
    {
        return(captured[1].getSeedCount() - captured[2].getSeedCount());
    }
}

    // This form of the method was used for the function form of...
    // A * Total1 + B * Total2 + (seeds on players side) * C + (seeds on opponents side) * D
    
    // public double getNetScore(int playTurn, ArrayList<Double> variables)
    // {
        // int
            // count1 = 0,
            // count2 = 0;
        // double
            // x1 = variables.get(0),
            // x2 = variables.get(1),
            // x3 = variables.get(2),
            // x4 = variables.get(3);
            
        // for(int i = 1; i < 7; i++)
        // {
            // count1 += gameBoard[1][i].getSeedCount();
            // count2 += gameBoard[2][i].getSeedCount();
        // }
        // if(playTurn == 1)
            // return captured[1].getSeedCount() * x1 + captured[2].getSeedCount() * x2 + count1 * x3 + count2 * x4;
        // else
            // return captured[2].getSeedCount() * x1 + captured[1].getSeedCount() * x2 + count2 * x3 + count1 * x4;
    // }
    
    // This form of the method was used for the function form of...
    // (S1 captured - S2 Captured)^a  +  (S1 Total - S2 Total)^b
    
    // public double getNetScore(int playTurn, ArrayList<Double> variables)
    // {
        // int
            // count1 = 0,
            // count2 = 0;
        // double
            // x1 = variables.get(0),
            // x2 = variables.get(1);
            
        // for(int i = 1; i < 7; i++)
        // {
            // count1 += gameBoard[1][i].getSeedCount();
            // count2 += gameBoard[2][i].getSeedCount();
        // }
        // if(playTurn == 1)
            // return Math.pow(captured[1].getSeedCount() - captured[2].getSeedCount(), x1) + Math.pow(count1 - count2, x2);
        // else
            // return Math.pow(captured[2].getSeedCount() - captured[1].getSeedCount(), x1) + Math.pow(count2 - count1, x2);
    // }