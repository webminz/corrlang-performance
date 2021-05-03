import java.time.LocalDate;
import java.util.Date;


import Families.FamiliesFactory;
import Families.Family;
import Families.FamilyMember;
import Persons.Female;
import Persons.PersonRegister;
import Persons.PersonsFactory;

public class CreateFirstnameInconsisten extends Command<Family, PersonRegister>  {

	
	
	public CreateFirstnameInconsisten(FamiliesFactory familiesFactory, PersonsFactory personsFactory) {
		super(familiesFactory, personsFactory);
	}

	@Override
	public void execute(Family family, PersonRegister person) {
		FamilyMember member = getFamiliesFactory().createFamilyMember();
		int no = family.getDaughters().size();
		family.getDaughters().add(member);
		Female female = getPersonsFactory().createFemale();
		person.getPersons().add(female);
		member.setName("Chantal" + no);
		female.setBirthday(new Date(LocalDate.of(2002, 5, 1).toEpochDay()));
		female.setName("Schantal" +  no + " " + family.getName());

	}

}
