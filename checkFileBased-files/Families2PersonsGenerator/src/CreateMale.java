import java.time.LocalDate;
import java.util.Date;

import org.eclipse.emf.ecore.EObject;

import Persons.Male;
import Persons.PersonRegister;
import Persons.PersonsFactory;
import Families.FamiliesFactory;
import Families.Family;
import Families.FamilyMember;

public class CreateMale extends Command<Family, PersonRegister> {

	public CreateMale(FamiliesFactory familiesFactory, PersonsFactory personsFactory) {
		super(familiesFactory, personsFactory);
	}

	@Override
	public void execute(Family family, PersonRegister person) {
		FamilyMember member = getFamiliesFactory().createFamilyMember();
		Male male = getPersonsFactory().createMale();
		if (family.getFather() == null) {
			member.setName("Hans");
			male.setBirthday(new Date(LocalDate.of(1970, 1, 1).toEpochDay()));
			male.setName(member.getName() + " " + family.getName());
			person.getPersons().add(male);
			family.setFather(member);
		} else {
			member.setName("Max" + family.getSons().size());
			male.setBirthday(new Date(LocalDate.of(2000, 9, 11).toEpochDay()));
			male.setName(member.getName() + " " + family.getName());
			person.getPersons().add(male);
			family.getSons().add(member);
		}

	}

}
