import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class WariV4
{
    private static final int SEARCH_DEPTH = 6;
    private int playTurn, moveCount = 1;
    
    public int[] Scores;
    public BoardV4 playBoard;
    ArrayList<Double> variables1, variables2;
    
    public WariV4()
    {
        Scores = new int[2];
    }
    
    /*****************************************************************************
     *
     * play()
     *
     * Description: Overall logic of game. Handles turns, variable lists, boards,
     *              and when to create trees/utilize minimax.
     *
     * Input:       Two variable arraylists, to be passed to BoardV4.
     *
     * Output:      A double representing an overall win or an overall loss for P1.
     *
     ****************************************************************************/
    
    public double play(ArrayList<Double> v1, ArrayList<Double> v2)
    {
        variables1 = v1;
        variables2 = v2;
        
        for(int i = 1; i < 3; i++)
        {
            playTurn = i;
            moveCount = 1;
            playBoard = new BoardV4();
            
            while(playTurn < 3)
            { 
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(0);
                    
                    for(int x = 1; x < 7; x++)
                    {
                        BoardV4 copyBoard = new BoardV4(playBoard.getGameBoard(), playBoard.getCaptured());
                        if(playBoard.seedsInCup(playTurn, x) != 0)
                        {
                            root.add(new DefaultMutableTreeNode(x));
                        }
                    }
                    
                    if(root.getChildCount() > 0)
                    {
                        treeMaker(root, playBoard, playTurn); // Search depth cannot be smaller than 2.
                    
                        double a = Double.NEGATIVE_INFINITY;
                        double b = Double.POSITIVE_INFINITY;
                        DefaultMutableTreeNode best = miniMax(root, a, b, playBoard, playTurn);
                        int chosen = 0;
                        for(int x = 0; x < root.getChildCount(); x++)
                        {
                            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(x);
                            if(best.isNodeAncestor(child))
                                chosen = (int) child.getUserObject();
                            else if(child == best) // If depth = 1
                                chosen = (int) child.getUserObject();
                        }
                        
                        playBoard.moveBoard(playTurn, chosen);
                        int cup = 0;
                        //System.out.println(moveCount + ".) Player " + playTurn + "\tCup: " + chosen + ".\n");
                        moveCount++;
                        if(playTurn == 1)
                            playTurn = 2;
                        else
                            playTurn = 1;
                    }
                    else
                    {
                        playTurn = 3; // End game
                        //System.out.println(playBoard.capturedCount(1) + " " + playBoard.capturedCount(2));
                    }
            }
            Scores[i-1] = playBoard.getFitness();
            //System.out.println(Scores[i-1]);
        }
        //System.out.println(((double)Scores[0] + (double)Scores[1]) / 2.0);
        return (((double)Scores[0] + (double)Scores[1]) / 2); // Return average of two games
    }
    
    /*****************************************************************************
     *
     * treeMaker()
     *
     * Description: Creates up to 6^n nodes, starting at level 2.
     *
     * Input:       DefaultMutableTreeNode, representing the root node.
     *
     * Output:      Generates a tree up to a specified depth.
     *
     * Note:        Children of root are generated before this method.
     *
     ****************************************************************************/
    
    public void treeMaker(DefaultMutableTreeNode root, BoardV4 currentBoard, int player)
    {
        DefaultMutableTreeNode node = root.getFirstLeaf(); 
        int turn = 1;
        boolean done = false; // Prevents infinite loops when you can't reach the search depth.
        
        while(root.getDepth() != SEARCH_DEPTH && node != null && !done)
        {
            ArrayList<Boolean> check = new ArrayList<Boolean>();
            while(node != null)
            {
                int depth = node.getLevel();
                if(player == 2)
                {
                    if(depth % 2 == 0)
                        turn = 2;
                    else
                        turn = 1;
                }
                else
                {
                    if(depth % 2 == 0)
                        turn = 1;
                    else
                        turn = 2;
                }
                    
                BoardV4 nodeBoard = new BoardV4(currentBoard.getGameBoard(), currentBoard.getCaptured());
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                ArrayList<Integer> hold = new ArrayList<Integer>();
                hold.add((int) node.getUserObject());
                
                while(!parent.isRoot())
                {
                    hold.add((int) parent.getUserObject());
                    parent = (DefaultMutableTreeNode) parent.getParent();
                }
                for(int x = hold.size() - 1; x >= 0; x--)
                {
                    int t;
                    
                    if(player == 2)
                    {
                        if((hold.size()-1) % 2 == 0)
                        {
                            if(x % 2 == 0)
                                t = 2;
                            else
                                t = 1;
                        }
                        else
                        {
                            if(x % 2 != 0)
                                t = 2;
                            else
                                t = 1;
                        }
                    }
                    else
                    {
                        if((hold.size()-1) % 2 == 0)
                        {
                            if(x % 2 == 0)
                                t = 1;
                            else
                                t = 2;
                        }
                        else
                        {
                            if(x % 2 != 0)
                                t = 1;
                            else
                                t = 2;
                        }
                    }
                        
                    nodeBoard.moveBoard(t, hold.get(x));
                }
                    
                for(int x = 1; x < 7; x++)
                {
                    if(nodeBoard.seedsInCup(turn, x) != 0)
                    {
                        node.add(new DefaultMutableTreeNode(x));
                        check.add(true);
                    }
                }
                node = node.getNextLeaf();
            }
            if(check.isEmpty())
                done = true;
            else
                node = root.getFirstLeaf();
        }
    }
    
    /*****************************************************************************
     *
     * miniMax()
     *
     * Description: Starts at the root node, then continually works its way to
     *              the leaves, changing alpha or beta depending on depth. A node
     *              representing a move will eventually be chosen.
     *
     * Input:       DefaultMutableTreeNode, two ints representing alpha and beta
     *
     * Output:      The leaf node that represents the best course of action as
     *              determined by the difference of both scores.
     *
     ****************************************************************************/
    
    public DefaultMutableTreeNode miniMax(DefaultMutableTreeNode node, double a, double b, BoardV4 currentBoard, int player)
    {
        double
            alpha = a,
            beta = b,
            compare;
        
        if(node.isLeaf())
        {
            return node;
        }
        else
        {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getFirstChild();
            DefaultMutableTreeNode store = child;
            
            if(node.getLevel() % 2 == 0) // Max
            {
                while(child != null && alpha <= beta)
                {
                    DefaultMutableTreeNode miMax = miniMax(child, alpha, beta, currentBoard, player);
                    BoardV4 nodeBoard = new BoardV4(currentBoard.getGameBoard(), currentBoard.getCaptured());
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) miMax.getParent();
                    ArrayList<Integer> hold = new ArrayList<Integer>();
                    hold.add((int) miMax.getUserObject());
                    
                    while(!parent.isRoot())
                    {
                        hold.add((int) parent.getUserObject());
                        parent = (DefaultMutableTreeNode) parent.getParent();
                    }
                    for(int x = hold.size() - 1; x >= 0; x--)
                    {
                        int t;
                        
                        if(player == 2)
                        {
                            if((hold.size()-1) % 2 == 0)
                            {
                                if(x % 2 == 0)
                                    t = 2;
                                else
                                    t = 1;
                            }
                            else
                            {
                                if(x % 2 != 0)
                                    t = 2;
                                else
                                    t = 1;
                            }
                        }
                        else
                        {
                            if((hold.size()-1) % 2 == 0)
                            {
                                if(x % 2 == 0)
                                    t = 1;
                                else
                                    t = 2;
                            }
                            else
                            {
                                if(x % 2 != 0)
                                    t = 1;
                                else
                                    t = 2;
                            }
                        }
                            
                        nodeBoard.moveBoard(t, hold.get(x));
                    }
                    
                    if(playTurn == 1)
                        compare = nodeBoard.getNetScore(playTurn, variables1);
                    else
                        compare = nodeBoard.getNetScore(playTurn, variables2);
                    
                    if(alpha < compare)
                    {
                        alpha = compare;
                        store = miMax;
                    }
                    child = child.getNextSibling();
                }
                return store; // Get leaf whose score is represented by alpha
            }
            else // Min
            {
                while(child != null && beta >= alpha)
                {
                    DefaultMutableTreeNode miMax = miniMax(child, alpha, beta, currentBoard, playTurn);
                    BoardV4 nodeBoard = new BoardV4(currentBoard.getGameBoard(), currentBoard.getCaptured());
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) miMax.getParent();
                    ArrayList<Integer> hold = new ArrayList<Integer>();
                    hold.add((int) miMax.getUserObject());
                    
                    while(!parent.isRoot())
                    {
                        hold.add((int) parent.getUserObject());
                        parent = (DefaultMutableTreeNode) parent.getParent();
                    }
                    for(int x = hold.size() - 1; x >= 0; x--)
                    {
                        int t;
                        
                        if(player == 2)
                        {
                            if((hold.size()-1) % 2 == 0)
                            {
                                if(x % 2 == 0)
                                    t = 2;
                                else
                                    t = 1;
                            }
                            else
                            {
                                if(x % 2 != 0)
                                    t = 2;
                                else
                                    t = 1;
                            }
                        }
                        else
                        {
                            if((hold.size()-1) % 2 == 0)
                            {
                                if(x % 2 == 0)
                                    t = 1;
                                else
                                    t = 2;
                            }
                            else
                            {
                                if(x % 2 != 0)
                                    t = 1;
                                else
                                    t = 2;
                            }
                        }
                        
                        nodeBoard.moveBoard(t, hold.get(x));
                    }
                    
                    if(playTurn == 1)
                        compare = nodeBoard.getNetScore(playTurn, variables1);
                    else
                        compare = nodeBoard.getNetScore(playTurn, variables2);
                    
                    if(beta > compare)
                    {
                        beta = compare;
                        store = miMax;
                    }
                    child = child.getNextSibling();
                }
                return store;
            }
        }
    }
    
    public static void main(String[] args)
    {
        ArrayList<Double> a = new ArrayList<Double>();
        ArrayList<Double> b = new ArrayList<Double>();
        WariV4 game = new WariV4();
        game.play(a, a);
    }
}