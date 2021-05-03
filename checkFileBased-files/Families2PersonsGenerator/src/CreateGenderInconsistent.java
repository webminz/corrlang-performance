
import java.time.LocalDate;
import java.util.Date;

import Families.FamiliesFactory;
import Families.Family;
import Families.FamilyMember;
import Persons.Female;
import Persons.PersonRegister;
import Persons.PersonsFactory;

public class CreateGenderInconsistent extends Command<Family, PersonRegister> {

	public CreateGenderInconsistent(FamiliesFactory familiesFactory, PersonsFactory personsFactory) {
		super(familiesFactory, personsFactory);
	}

	@Override
	public void execute(Family family, PersonRegister person) {
		FamilyMember member = getFamiliesFactory().createFamilyMember();
		member.setName("Juniper" + family.getSons().size());
		family.getSons().add(member);
		Female female = getPersonsFactory().createFemale();
		person.getPersons().add(female);
		female.setBirthday(new Date(LocalDate.of(2019, 2, 1).toEpochDay()));
		female.setName(member.getName() + " " + family.getName());
	}

}
