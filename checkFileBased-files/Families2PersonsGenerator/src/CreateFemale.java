import java.time.LocalDate;
import java.util.Date;


import Families.FamiliesFactory;
import Families.Family;
import Families.FamilyMember;
import Persons.Female;
import Persons.PersonRegister;
import Persons.PersonsFactory;

public class CreateFemale extends Command<Family, PersonRegister> {

	public CreateFemale(FamiliesFactory familiesFactory, PersonsFactory personsFactory) {
		super(familiesFactory, personsFactory);
	}

	@Override
	public void execute(Family family, PersonRegister person) {
		FamilyMember member = getFamiliesFactory().createFamilyMember();
		Female female = getPersonsFactory().createFemale();
		if (family.getFather() == null) {
			member.setName("Manuela");
			female.setBirthday(new Date(LocalDate.of(1975, 3, 22).toEpochDay()));
			female.setName(member.getName() + " " + family.getName());
			person.getPersons().add(female);
			family.setMother(member);
		} else {
			member.setName("Mandy" + family.getDaughters().size());
			female.setBirthday(new Date(LocalDate.of(2001, 12, 24).toEpochDay()));
			female.setName(member.getName() + " " + family.getName());
			person.getPersons().add(female);
			family.getDaughters().add(member);
		}

	}

}
