import org.eclipse.emf.ecore.EObject;

import Families.FamiliesFactory;
import Persons.PersonsFactory;

public abstract class Command<F extends EObject, P extends EObject> {

	
	private final FamiliesFactory familiesFactory;
	private final PersonsFactory personsFactory;
	
	

	public Command(FamiliesFactory familiesFactory, PersonsFactory personsFactory) {
		super();
		this.familiesFactory = familiesFactory;
		this.personsFactory = personsFactory;
	}



	protected FamiliesFactory getFamiliesFactory() {
		return familiesFactory;
	}



	protected PersonsFactory getPersonsFactory() {
		return personsFactory;
	}



	public abstract void execute(F family, P person);
	
	
}
