import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import Families.FamiliesFactory;
import Families.FamiliesPackage;
import Families.Family;
import Families.FamilyRegister;
import Persons.PersonRegister;
import Persons.PersonsFactory;
import Persons.PersonsPackage;

public class Generator {
	
	private long noOfPersonsElements = 1;
	private long noOfFamilies = 1;
	private long noOfInconsistencies = 0;
	
	
	private final String familiesFile;
	private final String personsFile;
	private final String statisticsFile;
	
	private long noOfElements;
	private int minNoOfAggregateChilds;
	private int maxNoOfAggregateChilds;
	private double inconsistencyProbability;
	
	private final Random random;
	
	private final List<Command> consistentCommands = new ArrayList<>();
	private final List<Command> inconsistentCommands = new ArrayList<>();

	public Generator(String familiesFile, String personsFile, String statisticsFile) {
		super();
		this.familiesFile = familiesFile;
		this.personsFile = personsFile;
		this.statisticsFile = statisticsFile;
		this.random = new Random(System.currentTimeMillis() ^ "Persons".hashCode() ^ "Families".hashCode());
	}


	public int getMinNoOfAggregateChilds() {
		return minNoOfAggregateChilds;
	}

	public void setMinNoOfAggregateChilds(int minNoOfAggregateChilds) {
		this.minNoOfAggregateChilds = minNoOfAggregateChilds;
	}

	public int getMaxNoOfAggregateChilds() {
		return maxNoOfAggregateChilds;
	}

	public void setMaxNoOfAggregateChilds(int maxNoOfAggregateChilds) {
		this.maxNoOfAggregateChilds = maxNoOfAggregateChilds;
	}

	public double getInconsistencyProbability() {
		return inconsistencyProbability;
	}

	public void setInconsistencyProbability(double inconsistencyProbability) {
		this.inconsistencyProbability = inconsistencyProbability;
	}
	
	public void generate() {
		
		// set up
		FamiliesPackage.eINSTANCE.eClass();
		PersonsPackage.eINSTANCE.eClass();
		
		
		FamiliesFactory familiesFactory = FamiliesFactory.eINSTANCE;
		PersonsFactory personsFactory = PersonsFactory.eINSTANCE;
		
		this.consistentCommands.add(new CreateMale(familiesFactory, personsFactory));
		this.consistentCommands.add(new CreateFemale(familiesFactory, personsFactory));
		
		this.inconsistentCommands.add(new CreateFirstnameInconsisten(familiesFactory, personsFactory));
		this.inconsistentCommands.add(new CreateGenderInconsistent(familiesFactory, personsFactory));
		this.inconsistentCommands.add(new CreateLastnameInconsistent(familiesFactory, personsFactory));
		
		FamilyRegister familyRegister = familiesFactory.createFamilyRegister();
		PersonRegister personRegister = personsFactory.createPersonRegister();
		
		
		// creation
		long counter = 0;
		while (noOfPersonsElements < noOfElements) {
			
			Family family = familiesFactory.createFamily();
			family.setName("Mustermann"+counter);
			familyRegister.getFamilies().add(family);
			
			noOfFamilies++;
			
			int aggregateChilds = random.nextInt(maxNoOfAggregateChilds - minNoOfAggregateChilds) + minNoOfAggregateChilds;
			if (aggregateChilds + noOfPersonsElements > noOfElements) {
				aggregateChilds = (int) (aggregateChilds + noOfPersonsElements - noOfElements);
			}
			for (int i = 0; i < aggregateChilds; i++) {
				double draw = random.nextDouble();
				noOfFamilies++;
				noOfPersonsElements++;
				if (draw > inconsistencyProbability) {
					this.consistentCommands.get(random.nextInt(consistentCommands.size())).execute(family, personRegister);
				} else {
					this.inconsistentCommands.get(random.nextInt(inconsistentCommands.size())).execute(family, personRegister);
					noOfInconsistencies++;
				}
			}
			counter++;
		}
		
		// persisting
		 Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
	     Map<String, Object> m = reg.getExtensionToFactoryMap();
	     m.put("families", new XMIResourceFactoryImpl());
	     m.put("persons", new XMIResourceFactoryImpl());

	     ResourceSet resSet = new ResourceSetImpl();
	     Resource resource = resSet.createResource(URI.createURI(familiesFile));
	     resource.getContents().add(familyRegister);
	     try {
	    	 resource.save(Collections.EMPTY_MAP);
	     } catch (IOException e) {
	    	 e.printStackTrace();
	     }
	     
	     resSet = new ResourceSetImpl();
	     resource = resSet.createResource(URI.createURI(personsFile));
	     resource.getContents().add(personRegister);
	     try {
	    	 resource.save(Collections.EMPTY_MAP);
	     } catch (IOException e) {
	    	 e.printStackTrace();
	     }
	     
	     
	     Properties p = new Properties();
	     p.setProperty("families.noOfElements", Long.toString(noOfFamilies));
	     p.setProperty("persons.noOfElements", Long.toString(noOfPersonsElements));
	     p.setProperty("noOfInconsistencies", Long.toString(noOfInconsistencies));
	     File statFile = new File(statisticsFile);
	     try {
	    	 FileOutputStream fileOutputStream = new FileOutputStream(statFile);
		     p.save(fileOutputStream, null);
		     fileOutputStream.close();
	     } catch (IOException e) {
	    	 e.printStackTrace();
	     }

	}
	
	public static void main(String[] args) {
		String outputDir = "output";
		if (args.length > 0) {
			outputDir = args[0];
		}
		long elements = 100;
		if (args.length > 1) {
			try {
				elements = Long.parseLong(args[1]);
			} catch (NumberFormatException e) {
			}
		}
		int minChilds = 2;
		if (args.length > 2) {
			try {
				minChilds = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
			}
		}
		int maxChilds = 8;
		if (args.length > 3) {
			try {
				maxChilds = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
			}
		}
		double inconsProb = 0.1;
		if (args.length > 4) {
			try {
				inconsProb = Double.parseDouble(args[4]);
			} catch (NumberFormatException e) {
			}
		}
		String exeDir = System.getProperty("user.dir");
		File famFile = new File(exeDir + "/" + outputDir + "/" + "Families.families");
		File persFile = new File(exeDir + "/" + outputDir + "/" + "Persons.persons");
		File statsFile = new File(exeDir + "/" + outputDir + "/" + "Stats.properties");
		if (famFile.exists()) {
			famFile.delete();
		}
		if (persFile.exists()) {
			persFile.delete();
		}
		if (statsFile.exists()) {
			statsFile.delete();
		}
		if (famFile.getParentFile().exists()) {
			famFile.getParentFile().mkdirs();

		}

		Generator generator = new Generator(famFile.getAbsolutePath(), persFile.getAbsolutePath(), statsFile.getAbsolutePath());
		generator.setInconsistencyProbability(inconsProb);
		generator.noOfElements = elements;
		generator.setMinNoOfAggregateChilds(minChilds);
		generator.setMaxNoOfAggregateChilds(maxChilds);
		generator.generate();
	}
	
	
	
	
	
	
	

}
