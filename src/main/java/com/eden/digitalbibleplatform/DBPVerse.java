package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.bible.Reference;
import com.caseyjbrooks.eden.bible.Verse;

public class DBPVerse extends Verse {
	public DBPVerse(Reference reference) {
		super(reference);
		this.formatter = new DBPFormatter();

		if(reference.getBook() instanceof DBPBook) {
			DBPBook DBPBook = (DBPBook) reference.getBook();
			this.id = DBPBook.getId() + "." + reference.getChapter();
		}
		else {
			this.id = "Matt.1";
		}
	}
}
