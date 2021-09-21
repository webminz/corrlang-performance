package run;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.emoflon.neo.api.familiestopersons.API_Common;

public class App {

	private static final Logger logger = Logger.getLogger(App.class);


	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.INFO);

		if (args.length == 0) {
			logger.error("No arguments provided! Terminating...");
			logger.error("Usage: <java-bin> ( CLEAR | IMPORT <mm1> <mm2> <m1> <m2> | CHECK <m1> <m2>)");
			System.exit(88);
		} else {
			String command = args[0];
			App app = new App();
			try {
			switch (command) {
			case "CLEAR": app.clear();
				break;
				case "IMPORT": app.imprt(args[1], args[2], args[3], args[4]);
				break;
				case "CHECK": app.check(args[1], args[2]);
				break;
				default:
					logger.error("Unknwon argument '" + args[0] + "'! Terminating...");
					logger.error("Usage: <java-bin> ( CLEAR | IMPORT | CHECK) <mm1> <mm2> <m1> <m2>");
					System.exit(88);

			}
			} catch (Exception e) {
				logger.error(e);
				System.exit(88);

			}
		}
	
	}
	
	public void check(String families, String persons) throws Exception {
		FamiliesToPersons_CC_Run app = new FamiliesToPersons_CC_Run("file://" + families, "file://" +  persons);
		app.run();
	}
	
	public void imprt(String famMMLocation, String persMMLocation, String famMLocation, String persMLocation) {
		FamiliesToPersons_Import_Run run = new FamiliesToPersons_Import_Run();
		run.loadMetamodels("file://" + famMMLocation, "file://" + persMMLocation);
		run.prepareDatabase();
		run.importXMI("file://" + famMLocation);
		run.importXMI("file://" + persMLocation);
	}

	
	public void clear() throws Exception {
		try (var builder = API_Common.createBuilder()) {
			builder.clearDataBase();
			logger.info("Database cleared");
		}
	}
	
}
