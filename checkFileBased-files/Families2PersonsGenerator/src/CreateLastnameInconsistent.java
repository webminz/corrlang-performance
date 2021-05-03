import java.time.LocalDate;
import java.util.Date;

import Families.FamiliesFactory;
import Families.Family;
import Families.FamilyMember;
import Persons.Female;
import Persons.Male;
import Persons.PersonRegister;
import Persons.PersonsFactory;

public class CreateLastnameInconsistent extends Command<Family, PersonRegister> {

	@Override
	public void execute(Family family, PersonRegister person) {
		FamilyMember member = getFamiliesFactory().createFamilyMember();
		int no = family.getDaughters().size();
		family.getSons().add(member);
		Male female = getPersonsFactory().createMale();
		person.getPersons().add(female);
		member.setName("Ole" + no);
		female.setBirthday(new Date(LocalDate.of(1989, 1, 31).toEpochDay()));
		female.setName(member.getName() + " " + family.getName() + "-Nordmann");
		
	}

	public CreateLastnameInconsistent(FamiliesFactory familiesFactory, PersonsFactory personsFactory) {
		super(familiesFactory, personsFactory);
	}

}
