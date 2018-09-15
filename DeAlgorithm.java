import java.util.*;
import java.io.*;

public class DeAlgorithm extends FitnessFunction
{
	private final static String algorithmName = "DeAlgorithm"; // Algo Name
	private final static double crossRate = 0.9;	// Cross-Over Rate.
	private final static double inertia = 0.5;	// Interia Associated
	private final static int iterNumber = 2000;	// Iteration Number
	private final static int popSize = 100;		// Number of chromosomes
	private final static int maxFunEval = 90000; // Max Allowable Function Evaluation
	private static int funEval = 0;	// Counting Function Evaluations
	private static double bestFitness = 99999999; // Store Best Fitness
	private static ArrayList<Double> bestChromosome;
	private final static String resultFileName = "Result"+algorithmName+".csv";
	private static final String fileHeader = "Iteration,Fitness,Chromosome"+"\n";
	private static FileWriter fileWriter = null;
	public static class Individual
	{
		ArrayList<Double> chromosome;
		double fitness;
	}
	private static ArrayList<Individual> pop ;	// Stores chromosome and fitness value
	public DeAlgorithm()
	{
		FitnessFunction fObj = new FitnessFunction();
		bestChromosome = new ArrayList<>(fObj.dim);
		bestChromosome = initializeList(bestChromosome);
	}

	public static void initializeAll()
	 {
		pop = new ArrayList<Individual>(popSize);	// initialized to 100 capacity

		FitnessFunction fObj = new FitnessFunction();

		for(int i=0;i<popSize;i++)
		{
			ArrayList<Double> chromosome = new ArrayList<>(fObj.dim);

			for(int j=0;j<fObj.dim;j++)
			{
			// Adding chromosome's value with rounded value
				chromosome.add(fObj.round(((Math.random() * (fObj.uBound - fObj.lBound)) + fObj.lBound),2));
			}

			Individual inst = new Individual();

			inst.fitness = fObj.fitnessFunction(chromosome);
			funEval++;
			inst.chromosome = chromosome;

			pop.add(inst);
		}
	}
	public ArrayList<Double> deepCopy(ArrayList<Double> copyTo,ArrayList<Double> copyFrom)
	{

		FitnessFunction fObj = new FitnessFunction();

		for(int i=0;i<copyFrom.size();i++)
		{
			copyTo.set(i,copyFrom.get(i));

		}
			return copyTo;
	}

	public void memorizeGlobalBest(){

		FitnessFunction fObj = new FitnessFunction();

		for(Individual p : pop)
		{
			if(p.fitness < bestFitness)
			{
				bestFitness = p.fitness;
				bestChromosome = deepCopy(bestChromosome, p.chromosome);
				System.out.println("New BestChromosome Found  "+bestChromosome);
			}

		}
	}

	public ArrayList<Double> initializeList(ArrayList<Double> list)
	{
		FitnessFunction fObj = new FitnessFunction();
		for(int i=0;i<fObj.dim;i++)
			list.add(i,0.00);

		return list;
	}
	public void deOperation()
	{
		FitnessFunction fObj = new FitnessFunction();

		for(int i=0;i<popSize;i++)
		{
			int indA = (int)(Math.random()*(popSize-1));
			int indB = (int)(Math.random()*(popSize-1));

			ArrayList<Double> newChild = new ArrayList<>();
			for(int j=0;j<fObj.dim;j++)
			{
				if(Math.random()<=crossRate)
				{
					double constValue = pop.get(i).chromosome.get(j) + inertia*( pop.get(indB).chromosome.get(j) - pop.get(indA).chromosome.get(j) );

					if(constValue<fObj.lBound)
						constValue = fObj.round(((Math.random() * (fObj.uBound - fObj.lBound)) + fObj.lBound),2);

					if(constValue>fObj.lBound)
						constValue = fObj.round(((Math.random() * (fObj.uBound - fObj.lBound)) + fObj.lBound),2);

					newChild.add(fObj.round(constValue,2));

				}
				else
				{
					newChild.add(pop.get(i).chromosome.get(j));
				}
			}

			double newChildFitness = fObj.fitnessFunction(newChild);

			funEval++;

			if(newChildFitness < pop.get(i).fitness)
			{
				pop.get(i).fitness = newChildFitness;
				pop.get(i).chromosome = deepCopy(pop.get(i).chromosome, newChild);
			}
		}


	}



	public static void main(String[] args)
	{
		DeAlgorithm deObj = new DeAlgorithm();
		deObj.initializeAll();

		deObj.bestChromosome = deObj.pop.get(0).chromosome;
		deObj.bestFitness = deObj.pop.get(0).fitness;


		try{
				deObj.fileWriter = new FileWriter(deObj.resultFileName);
				deObj.fileWriter.append(fileHeader);

				for(int i=0;i<iterNumber;i++)
				{
					deObj.deOperation();
					deObj.memorizeGlobalBest();

					if(i%1==0)
					{
						deObj.fileWriter.append(i+","+deObj.bestFitness+","+deObj.bestChromosome+"\n");
					}

					if(funEval>maxFunEval)
						break;

					funEval++;

				}

				System.out.println("CSV file created");

		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				fileWriter.flush();
			  	fileWriter.close();
			}
			catch(IOException e){
			    e.printStackTrace();
			 }
		}
	}

	
}
