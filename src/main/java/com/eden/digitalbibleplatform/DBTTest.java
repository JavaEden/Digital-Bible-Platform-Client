package com.eden.digitalbibleplatform;

import com.eden.Eden;
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
        System.out.println(passage.getText());
        System.out.println(passage.getFormattedText());
    }
}
