import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;


public class PsoAlgorithm extends FitnessFunction
{
	private final static String algorithmName = "PsoAlgorithm"; // Algo Name
	private final static double constA = 1.5;	// Acceleration Const.
	private final static double constB = 1.5;	// Acceleration Const.
	private final static double iWeight = 0.8;	// Interia Weight
	private final static double velLowerBound = -1;	// Velocity Lower Bound
	private final static double velUpperBound = 1;		// Velocity Upper Bound
	private final static int iterNumber = 2000;	// Iteration Number
	private final static int popSize = 100;		// Number of chromosomes
	private final static int maxFunEval = 90000; // Max Allowable Function Evaluation
	private static int funEval = 0;	// Counting Function Evaluations
	private static double bestFitness = 99999999; // Store Best Fitness
	private static ArrayList<Double> bestParticle;
	private final static String resultFileName = "Result"+algorithmName+".csv";
	private static final String fileHeader = "Iteration,Fitness,Chromosome"+"\n";
	private static FileWriter fileWriter = null;

	public static class Individual{
		ArrayList<Double> particle;
		ArrayList<Double> velocity;
		ArrayList<Double> pBest;
		double fitness;
		double pBestCollective;

	}

	private static ArrayList<Individual> pop ;	// Stores chromosome and fitness value

	public PsoAlgorithm()
	{
		FitnessFunction fObj = new FitnessFunction();
		bestParticle = new ArrayList<>(fObj.dim);
		bestParticle = initializeList(bestParticle);

	}

	public static void initializeAll()
	{
		pop = new ArrayList<Individual>(popSize);	// initialized to 100 capacity

		FitnessFunction fObj = new FitnessFunction();

		for(int i=0;i<popSize;i++)
		{
			ArrayList<Double> chromosome = new ArrayList<>(fObj.dim);
			ArrayList<Double> velocity = new ArrayList<>(fObj.dim);
			for(int j=0;j<fObj.dim;j++)
			{
			// Adding chromosome's value with rounded value
				chromosome.add(fObj.round(((Math.random() * (fObj.uBound - fObj.lBound)) + fObj.lBound),2));

				velocity.add(fObj.round(((Math.random() * (velUpperBound - velLowerBound)) + velLowerBound),2));
			}

			Individual inst = new Individual();

			inst.fitness = fObj.fitnessFunction(chromosome);
			funEval++;
			inst.particle = chromosome;
			inst.velocity = velocity;
			inst.pBest = chromosome;
			inst.pBestCollective =  fObj.fitnessFunction(chromosome);
			pop.add(inst);
		}
	}


	public static void memorizeGlobalBest(){

		FitnessFunction fObj = new FitnessFunction();
		for(Individual p : pop)
		{
			if(p.pBestCollective < bestFitness)
			{
				bestFitness = p.pBestCollective;
				bestParticle = deepCopy(bestParticle, p.pBest);
				System.out.println("New BestChromosome Found  "+bestParticle);
			}
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


	public static ArrayList<Double> initializeList(ArrayList<Double> list)
	{
		FitnessFunction fObj = new FitnessFunction();
		for(int i=0;i<fObj.dim;i++)
			list.add(i,0.00);

		return list;
	}

	public static void psoOperation()
	{
		FitnessFunction fObj = new FitnessFunction();
		for(int i=0;i<popSize;i++)
		{
			for(int j=0;j<fObj.dim;j++)
			{
				double randA = Math.random();
				double randB = Math.random();

				pop.get(i).velocity.add(j,iWeight*pop.get(i).velocity.get(j) + constA*randA*(pop.get(i).pBest.get(j)-pop.get(i).particle.get(j)) + constB*randB*(bestParticle.get(j)-pop.get(i).particle.get(j)));

				if(pop.get(i).velocity.get(j)<velLowerBound)
					pop.get(i).velocity.add(j,Math.random() * (velUpperBound - velLowerBound) + velLowerBound);

				if(pop.get(i).velocity.get(j)>velUpperBound)
					pop.get(i).velocity.add(j,Math.random() * (velUpperBound - velLowerBound) + velLowerBound);

				pop.get(i).particle.set(j,fObj.round((pop.get(i).particle.get(j)+pop.get(i).velocity.get(j)),2));

				if(pop.get(i).particle.get(j)<fObj.lBound)
					pop.get(i).particle.set(j,fObj.round(((Math.random() * (fObj.uBound - fObj.lBound)) + fObj.lBound),2));

				if(pop.get(i).particle.get(j)>fObj.uBound)
					pop.get(i).particle.set(j,fObj.round(((Math.random() * (fObj.uBound - fObj.lBound)) + fObj.lBound),2));


			}

			pop.get(i).fitness = fObj.fitnessFunction(pop.get(i).particle);

			funEval++;

			if(pop.get(i).fitness<=pop.get(i).pBestCollective)
			{
				pop.get(i).pBestCollective = pop.get(i).fitness;
				pop.get(i).pBest = deepCopy(pop.get(i).pBest,pop.get(i).particle);
			}

		}

	}

	public static void main(String[] args)
	{
		PsoAlgorithm psoObj = new PsoAlgorithm();
		psoObj.initializeAll();

		psoObj.bestParticle = psoObj.pop.get(0).particle;
		psoObj.bestFitness = psoObj.pop.get(0).fitness;


		try{
				psoObj.fileWriter = new FileWriter(psoObj.resultFileName);
				psoObj.fileWriter.append(fileHeader);

				for(int i=0;i<iterNumber;i++)
				{
					psoObj.psoOperation();
					psoObj.memorizeGlobalBest();




					if(i%1==0)
					{
						psoObj.fileWriter.append(i+","+psoObj.bestFitness+","+psoObj.bestParticle+"\n");
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
