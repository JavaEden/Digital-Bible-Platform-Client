package com.eden.digitalbibleplatform;

import com.eden.EdenRepository;
import com.eden.bible.Bible;

public class DBTRepository extends EdenRepository {
    public DBTRepository() {
        super();
    }

    @Override
    public Class<DBTBibleList> getBibleListClass() {
        return DBTBibleList.class;
    }

    @Override
    public Class<DBTBible> getBibleClass() {
        return DBTBible.class;
    }

    @Override
    public Class<DBTPassage> getPassageClass() {
        return DBTPassage.class;
    }

    @Override
    public DBTBible getSelectedBible() {
        return (DBTBible) super.getSelectedBible();
    }

    @Override
    public void setSelectedBible(Bible selectedBible) {
        super.setSelectedBible(selectedBible);
    }

    @Override
    public DBTPassage lookupVerse(String reference) {
        return (DBTPassage) super.lookupVerse(reference);
    }
}
