package com.eden.digitalbibleplatform;

import com.caseyjbrooks.clog.Clog;
import com.eden.Eden;
import com.eden.annotations.EdenBible;
import com.eden.annotations.EdenBibleList;
import com.eden.bible.Passage;

public class DBTTest {

    public static void main(String[] args) {
        Eden eden = Eden.getInstance();
        eden.put("DBT_ApiKey", "82cb00ba8a8917d05e27c99facb8ff96");
        eden.put("com.eden.digitalbibleplatform.DBTRepository_selectedBibleId", "ENGESVN2ET");
        eden.registerRepository(new DBTRepository());

        // Get our repository as an injected repository, and use it to query for our Bible
        DBTRepository repo = (DBTRepository) eden.getRepository(DBTRepository.class);
        Passage passage = repo.lookupVerse("Galatians 2:19-21");

//        DBTBible bible = repo.getSelectedBible();

        System.out.println(passage.getReference().toString());
        System.out.println(passage.getFormattedText());

        DBTTest test = new DBTTest();
        test.testInjector();
    }

    @EdenBible(repository = DBTRepository.class)
    public DBTBible injectedBible;

    @EdenBibleList(repository = DBTRepository.class)
    public DBTBibleList injectedBibleList;

    public void testInjector() {
        Clog.i("\n\n");
        Clog.i("Is injectedBible currently null?: #{$1}", (injectedBible == null));
        Clog.i("Is injectedBibleList currently null?: #{$1}", (injectedBibleList == null));

        Eden.getInstance().inject(this);

        Clog.i("Is injectedBible currently null?: #{$1}", (injectedBible == null));
        Clog.i("Is injectedBibleList currently null?: #{$1}", (injectedBibleList == null));
    }
}
