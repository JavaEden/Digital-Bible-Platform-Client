package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.EdenRepository;
import com.caseyjbrooks.eden.bible.Bible;
import com.caseyjbrooks.eden.bible.Book;

public class DBPRepository extends EdenRepository {
    public DBPRepository() {
        super();
    }

    @Override
    public Class<DBPBibleList> getBibleListClass() {
        return DBPBibleList.class;
    }

    @Override
    public Class<DBPBible> getBibleClass() {
        return DBPBible.class;
    }

    @Override
    public Class<DBPPassage> getPassageClass() {
        return DBPPassage.class;
    }

    @Override
    public DBPBible getSelectedBible() {
        return (DBPBible) super.getSelectedBible();
    }

    @Override
    public void setSelectedBible(Bible<? extends Book> selectedBible) {
        super.setSelectedBible(selectedBible);
    }

    @Override
    public DBPPassage lookupVerse(String reference) {
        return (DBPPassage) super.lookupVerse(reference);
    }
}
