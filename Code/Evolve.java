import java.util.*;
import java.io.*;

public class Evolve
{   
    static final int
        mu = 4,              // Parents
        lambda = 28,         // Offspring
        termination = 100,   // when to stop run
        iteration = 7,       // AKA k, used in resetting sigma.
        n = 8;               // number of variables
        
    static final double
        c = .871,                   // Used in resetting sigma.
        tau1 = 1/Math.sqrt(2 * n),  // Tau prime
        tau2 = 1/Math.sqrt(2 * Math.sqrt(n)),
        minimum_sigma = .075;
        
    static double best_fitness = 0; // Honestly, not very meaningful. It just means by how much the best beat the second best.
        
    static WariV4 game = new WariV4(); // The variables are constantly switched.

    /*****************************************************************************
    *
    * main()
    *
    * Description: Main logic of program. Kicks off initial generation, creates
    *              new generation based on the best children, and carries on
    *              until termination count is reached.
    *
    * Input:       A long, for the random number seed.
    *
    * Output:      The best of run individual.
    *
    ****************************************************************************/
    
    public static void main(String[] args)
    {
        System.out.println("Enter a seed.");
        Scanner s = new Scanner(System.in);
        long seed = s.nextLong();
        
        int
            count = 1,
            better = 0,
            bestGeneration = 0; // Number of children better than parents
        
        Random r = new Random(seed);
        
        ArrayList<ArrayList<Double>>
            parents = new ArrayList<ArrayList<Double>>(),
            bestOfGen = new ArrayList<ArrayList<Double>>();
        ArrayList<Double>
            bestOfRun = new ArrayList<Double>();

        // Each individual arraylist will contain x and sigma values. The order is x1, x2, sigma1, sigma2
        
        for(int i = 0; i < mu; i++) // Initial generation
        {
            ArrayList<Double> temp = new ArrayList<Double>();
            for(int j = 0; j < n; j++)
            {
                temp.add(r.nextDouble()); // Initial x values
            }
            for(int j = 0; j < n; j++)
            {
                temp.add(1.0); // Initial sigma
            }
            parents.add(temp);
        }
        bestOfRun.addAll(parents.get(0));
            
        while(count != termination + 1) // Generation control
        {
            int offspring = 0;
            
            while(offspring != lambda) // Offspring creation
            {
                int chosen, random; // For first parent and random other parent
                ArrayList<Double> child = new ArrayList<Double>();
                
                for(int i = 0; i < n; i++) // Add x values
                {
                    chosen = r.nextInt(parents.size());
                    random = r.nextInt(parents.size() - 1);
                    if(random == chosen)
                        random += 1; // Can't choose the same parent twice
                    
                    if(r.nextDouble() > .5) // Pick from the "random" or "chosen" parent.
                        child.add(parents.get(random).get(i));
                    else
                        child.add(parents.get(chosen).get(i));
                }
                
                for(int i = n; i < (n * 2); i++) // Add sigmas
                {
                    chosen = r.nextInt(parents.size());
                    random = r.nextInt(parents.size() - 1);
                    if(random == chosen)
                        random += 1;
                    
                    child.add((parents.get(random).get(i) + parents.get(chosen).get(i)) / 2); // Average of parents
                }
                mutate(child);
                
                if(isValid(child)) // If child contains valid numbers...
                {
                    offspring++;
                    
                    if(game.play(child, parents.get(0)) > 0)
                        better++; // See if children can beat best parent
                    
                    if(bestOfGen.isEmpty()) // For first child.
                    {
                        bestOfGen.add(child);
                    }
                    else // Create list of the best.
                    {
                        int position = mu+1; // The "place" of the child. Default is last place.
                        for(int i = mu-1; i >= 0; i--)
                        {
                            if(bestOfGen.size() >= i+1) // This exists to prevent index problems.
                            {
                                if(game.play(child, bestOfGen.get(i)) > 0) // switch to wari playing stuff, an algorithm will claw its way to the top
                                {
                                    position = i; // Algorithm won
                                }
                                else
                                {
                                    i = -1; // loss; exit loop
                                }
                            }
                        }
                        if(position <= mu) // If the solution earned a place on the best of gen list...
                        {
                            bestOfGen.add(position, child);
                            if(bestOfGen.size() == mu+1) // Don't remove when list isn't full.
                                bestOfGen.remove(mu); // Where mu = last entry thanks to the quirks of starting at 0
                        }
                        else if(bestOfGen.size() < mu) // List isn't full
                            bestOfGen.add(child); // Child did not beat earlier entries
                    }
                }
            }
            double fit = game.play(bestOfGen.get(0), bestOfRun);
            if(fit > 0) // See if best in gen is better than best in run.
            {
                bestOfRun.clear();
                bestOfRun.addAll(bestOfGen.get(0));
                bestGeneration = count;
                best_fitness = fit;
            }
            
            if(count % iteration == 0) // Reset sigma
            {
                if((double)better / ((double)iteration * (double) lambda) > .2)
                {
                    for(int i = 0; i < bestOfGen.size(); i++)
                    {
                        bestOfGen.get(i).set(2, bestOfGen.get(i).get(2) / c); // Change sigma1 to sigma1/c
                        bestOfGen.get(i).set(3, bestOfGen.get(i).get(3) / c); 
                    }
                }
                else if((double)better / ((double)iteration * (double) lambda) < .2)
                {
                    for(int i = 0; i < bestOfGen.size(); i++)
                    {
                        bestOfGen.get(i).set(2, bestOfGen.get(i).get(2) * c); // Change sigma1 to sigma*c
                        bestOfGen.get(i).set(3, bestOfGen.get(i).get(3) * c); 
                    }
                }
                better = 0;
            }
            
            parents.clear();
            parents.addAll(bestOfGen);
            bestOfGen.clear();
            count++;
        }
        System.out.println("\nBest Individual:\t" + bestOfRun);
        System.out.println("Fitness:\t" + best_fitness);
        System.out.println("Generation:\t" + bestGeneration);
    }
    
    /*****************************************************************************
    *
    * evaluate()
    *
    * Description: Calculates value of equation.
    *
    * Input:       An ArrayList child.
    *
    * Output:      A double, showing what the child's values evaluate to.
    *
    ****************************************************************************/
    
    public static double evaluate(ArrayList<Double> child)
    {
        double x1 = child.get(0);
        double x2 = child.get(1);
        
        return (21.5 + (x1 * Math.sin(4.0 * Math.PI * x1)) + (x2 * Math.sin(20.0 * Math.PI * x2)));
    }
    
    /*****************************************************************************
    *
    * isValid()
    *
    * Description: Ensures values are within restraints. 
    *
    * Input:       An ArrayList child.
    *
    * Output:      A boolean describing whether the child's values are within
    *              boundaries.
    *
    ****************************************************************************/
    
    public static boolean isValid(ArrayList<Double> child)
    {
        for(int i = 0; i < child.size(); i++)
        {
            if(child.get(i) > 1.0 || child.get(i) < -1.0)
                return false;
        }
        return true;
    }
    
    /*****************************************************************************
    *
    * mutate()
    *
    * Description: Mutates sigma values, then x values.
    *
    * Input:       An ArrayList child.
    *
    * Output:      The mutated ArrayList child.
    *
    ****************************************************************************/
    
    public static void mutate(ArrayList<Double> child)
    {
        ArrayList<Double> mutant = new ArrayList<Double>();
        Random r = new Random();
        double overallRate = tau1 * r.nextGaussian();
        
        // Mutate sigma values. Assumes lists are even.
        for(int i = n; i < child.size(); i++)
        {
            child.set(i, child.get(i) * Math.exp(overallRate + tau2 * r.nextGaussian()));
            
            if(child.get(i) < minimum_sigma)
                child.set(i, minimum_sigma);
        }
            
         // Mutate x values
        for(int i = 0; i < n; i++)
        {
            child.set(i, child.get(i) + child.get(i+(child.size()/2)) * r.nextGaussian());
        }
    }
}