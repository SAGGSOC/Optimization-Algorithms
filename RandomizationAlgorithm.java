import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;


public class RandomizationAlgorithm extends FitnessFunction
	{
	private static String algorithmName = "RandomizationAlgorithm";
	private final static int iterNumber = 2000;	// Iteration Number
	private static double bestFitness = 99999999; // Store Best Fitness
	private static ArrayList<Double> bestChromosome;
	private static final String fileHeader = "Iteration,Fitness,Chromosome"+"\n";
	private static FileWriter fileWriter = null;
	private final static String resultFileName = "Result"+algorithmName+".csv";
	private final static int maxFunEval = 90000; // Max Allowable Function Evaluation
	private static int funEval = 0;	// Counting Function Evaluations

	public RandomizationAlgorithm()
	{
		bestChromosome = new ArrayList<>();
	}


	public ArrayList<Double> genChromosome()
	{
		FitnessFunction fObj = new FitnessFunction();
		ArrayList<Double> solChrome = new ArrayList<>();

		for(int j=0;j<fObj.dim;j++)
			{
			// Adding chromosome's value with rounded value
				solChrome.add(fObj.round(((Math.random() * (fObj.uBound - fObj.lBound)) + fObj.lBound),2));
			}

		return solChrome;
	}


	public static void main(String[] args)
	{
		RandomizationAlgorithm ranObj = new RandomizationAlgorithm();
		FitnessFunction fObj = new FitnessFunction();


		try{
				ranObj.fileWriter = new FileWriter(ranObj.resultFileName);
				ranObj.fileWriter.append(fileHeader);

				ArrayList<Double> chromValue;
				for(int i=0;i<iterNumber;i++)
				{
					chromValue = new ArrayList<>();
					chromValue = ranObj.genChromosome();

					double fitness = fObj.fitnessFunction(chromValue);
					funEval++;
					if(fitness<ranObj.bestFitness)
					{
						ranObj.bestFitness = fitness;
						bestChromosome = chromValue;
					}

					if(i%1==0)
					{
						ranObj.fileWriter.append(i+","+ranObj.bestFitness+","+ranObj.bestChromosome+"\n");
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
