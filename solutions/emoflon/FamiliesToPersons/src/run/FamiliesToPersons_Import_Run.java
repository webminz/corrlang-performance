package run;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.emoflon.neo.api.familiestopersons.API_Common;
import org.emoflon.neo.api.familiestopersons.API_Schema;
import org.emoflon.neo.emf.Neo4jImporter;

public class FamiliesToPersons_Import_Run {
	
	private static final Logger logger = Logger.getLogger(FamiliesToPersons_GEN_Run.class);


	
	public void loadMetamodels(String familiesEcoreFilePath, String personsEcoreFile) {
		// Registering the xmi deserializers for .xmi and .ecore file extensions
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
	    m.put("xmi", new XMIResourceFactoryImpl());
	    m.put("ecore", new EcoreResourceFactoryImpl());
	    
	    // registering the ECore Epackage
	    EcorePackage ecorePackage = EcorePackage.eINSTANCE;
	    
	    // Loading and regisering the Families EPackage from a file
	    URI familiesEcoreURI = URI.createURI(familiesEcoreFilePath);
		ResourceSet mm1ResourceSet = new ResourceSetImpl();
		EPackage familiesPackage = (EPackage) mm1ResourceSet.getResource(familiesEcoreURI, true).getContents().get(0);			
		EPackage.Registry.INSTANCE.put(familiesPackage.getNsURI(),familiesPackage);
		
		// Loading and regisering the Persons EPackage from a file
	    URI personsEcoreURI = URI.createURI(personsEcoreFile);
		ResourceSet mm2ResourceSet = new ResourceSetImpl();
		EPackage personsPackage = (EPackage) mm2ResourceSet.getResource(personsEcoreURI, true).getContents().get(0);			
		EPackage.Registry.INSTANCE.put(personsPackage.getNsURI(),personsPackage);
	    
	}
	
	public void prepareDatabase() {
		try (var builder = API_Common.createBuilder()) {
			//builder.clearDataBase();
			new API_Schema(builder).exportMetamodelsForFamiliesToPersons();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void importXMI(String file) {
	        URI modelFile = URI.createURI(file);
	        logger.info("Registering model '" + modelFile.toString() + "'!");
	        ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.getResource(modelFile, true);
		EObject eObject = resource.getContents().get(0);
		logger.info("Root object of type " + eObject.eClass().getName() + " detected!");
		new Neo4jImporter().importEMFModels(rs, "bolt://localhost:7687", "neo4j", "emoflon");
	}

}
