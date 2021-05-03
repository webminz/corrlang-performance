

rule MatchRegistries 
	match l : Fam!FamilyRegister
	with r : Pers!PersonRegister {
		compare : true
	}

rule MatchMales  
	match l : Fam!FamilyMember
	with r : Pers!Male {
		guard : l.fatherInverse != null or l.sonsInverse != null
		compare : r.name = l.name + " " + l.getLastname() 
		
}

rule MatchFemales  
	match l : Fam!FamilyMember
	with r : Pers!Female {
		guard : l.motherInverse != null or l.daughtersInverse != null
		compare : r.name = l.name + " " + l.getLastname() 
		
}

operation Fam!FamilyMember getLastname(): String {
	if (self.fatherInverse != null) {
		return self.fatherInverse.name;
	} 
	if (self.motherInverse  != null) {
		return self.motherInverse.name;
	}
	if (self.sonsInverse  != null) {
		return self.sonsInverse.name;
	}
	if (self.daughtersInverse  != null) {
		return self.daughtersInverse.name;
	}
	return null;
}