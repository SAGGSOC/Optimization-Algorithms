import java.util.*;
import java.io.*;

public class GeneticAlgorithm extends FitnessFunction{

	private final static String algorithmName = "GeneticAlgorithm"; // Algo Name
	private final static double crossRate = 0.7;	// CrossOver Rate
	private final static double mutRate = 0.1;		// Mutation Rate
	private final static int iterNumber = 2000;	// Iteration Number
	private final static int popSize = 100;		// Number of chromosomes
	private final static int maxFunEval = 90000; // Max Allowable Function Evaluation
	private static int funEval = 0;	// Counting Function Evaluations
	private static double bestFitness = 99999999; // Store Best Fitness
	private static ArrayList<Double> bestChromosome;
	private static final String fileHeader = "Iteration,Fitness,Chromosome"+"\n";
	private static FileWriter fileWriter = null;

	public static class Individual
	{
		ArrayList<Double> chromosome ;
		double fitness ;
	}
	private static ArrayList<Individual> pop ;	// Stores chromosome and fitness value
	private final static String resultFileName = "Result"+algorithmName+".csv";

	public GeneticAlgorithm()
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


	public static ArrayList<Double> deepCopy(ArrayList<Double> copyTo,ArrayList<Double> copyFrom)
	{

		FitnessFunction fObj = new FitnessFunction();

		for(int i=0;i<copyFrom.size();i++)
		{
			copyTo.set(i,copyFrom.get(i));
		}

		return copyTo;
	}


	public static void memorizeGlobalBest(){

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


	public static ArrayList<Double> initializeList(ArrayList<Double> list)
	{
		FitnessFunction fObj = new FitnessFunction();
		for(int i=0;i<fObj.dim;i++)
			list.add(i,0.00);
		return list;
	}


	public static void crossGene(){

		FitnessFunction fObj = new FitnessFunction();

		for(int i=0;i<popSize;i++)
		{
			if(Math.random()<=crossRate)
			{
				int indA = (int)(Math.random()*(popSize-1));
				int indB = (int)(Math.random()*(popSize-1));

				int crossIndex = (int)(Math.random()*(fObj.dim-2));

				Individual parA = new Individual();
				parA.chromosome = new ArrayList<Double>(fObj.dim);

				parA.chromosome = initializeList(parA.chromosome);

				parA.chromosome = deepCopy(parA.chromosome, pop.get(indA).chromosome);

				Individual parB = new Individual();
				parB.chromosome = new ArrayList<Double>(fObj.dim);

				parB.chromosome = initializeList(parB.chromosome);

				parB.chromosome = deepCopy(parB.chromosome, pop.get(indB).chromosome);

				Individual childA = new Individual();
				childA.chromosome = new ArrayList<Double>(fObj.dim);
				Individual childB = new Individual();
				childB.chromosome = new ArrayList<Double>(fObj.dim);

				for(int j=0;j<fObj.dim;j++)
				{

					if(j<crossIndex)
					{
						childA.chromosome.add(j,parA.chromosome.get(j));
						childB.chromosome.add(j,parB.chromosome.get(j));
					}
					else
					{
						childA.chromosome.add(j,parB.chromosome.get(j));
						childB.chromosome.add(j,parA.chromosome.get(j));
					}

				}

				double childAFitness = fObj.fitnessFunction(childA.chromosome);
				funEval++;

				double childBFitness = fObj.fitnessFunction(childB.chromosome);
				funEval++;


				if(childAFitness < pop.get(indA).fitness)
				{
					pop.get(indA).fitness = childAFitness;
					pop.get(indA).chromosome = deepCopy(pop.get(indA).chromosome ,childA.chromosome);
			}

				if(childBFitness < pop.get(indB).fitness)
				{
					pop.get(indB).fitness = childBFitness;
					pop.get(indB).chromosome = deepCopy(pop.get(indB).chromosome , childB.chromosome);
				}
			}
		}
	}

	public static void mutateGene()
	{
		FitnessFunction fObj = new FitnessFunction();

		for(int i=0;i<popSize;i++)
		{
			if(Math.random()<=mutRate)
			{
				int ind = (int)(Math.random()*(popSize-1));

				Individual par = new Individual();
				par.chromosome = new ArrayList<Double>();

				par.chromosome = initializeList(par.chromosome);

				par.chromosome = deepCopy(par.chromosome, pop.get(ind).chromosome);

				int mutIndex = (int)(Math.random()*(fObj.dim-1));

				Individual child = new Individual();
				child.chromosome = new ArrayList<Double>(fObj.dim);

				child.chromosome = initializeList(child.chromosome);

				child.chromosome = deepCopy(child.chromosome, pop.get(ind).chromosome);

				child.chromosome.set(mutIndex, fObj.round(((Math.random()*(fObj.uBound - fObj.lBound))+fObj.lBound),2));

				double childFitness = fObj.fitnessFunction(child.chromosome);
				funEval++;

				if(childFitness < pop.get(ind).fitness)
				{
					pop.get(ind).fitness = childFitness;
					pop.get(ind).chromosome = deepCopy(pop.get(ind).chromosome,child.chromosome);
				}

			}

		}

	}

	public static void main(String[] args)
	{
		GeneticAlgorithm genObj = new GeneticAlgorithm();
		genObj.initializeAll();

		genObj.bestChromosome = genObj.pop.get(0).chromosome;
		genObj.bestFitness = genObj.pop.get(0).fitness;


		try{
				genObj.fileWriter = new FileWriter(genObj.resultFileName);
				genObj.fileWriter.append(fileHeader);

				for(int i=0;i<iterNumber;i++)
				{
					genObj.crossGene();
					genObj.mutateGene();
					genObj.memorizeGlobalBest();




					if(i%1==0)
					{
						genObj.fileWriter.append(i+","+genObj.bestFitness+","+genObj.bestChromosome+"\n");
					}

					if(funEval>maxFunEval)
						break;


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
