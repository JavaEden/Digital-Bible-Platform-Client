package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.Eden;
import com.google.gson.GsonBuilder;

public class DBPTest {

    public static void main(String[] args) {
        Eden.getInstance().getMetadata().put("DBT_ApiKey", "82cb00ba8a8917d05e27c99facb8ff96");

        GsonBuilder builder = Eden.getInstance().getSerializer();

        DBPBibleList bibleList = new DBPBibleList();
        bibleList.download();

        DBPBible bible = new DBPBible();
        bible.setId("ENGESVN2DA");
        bible.download();

//        Reference ref = new Reference.Builder()
//                .setBible(bible)
//                .parseReference("Galatians 2:19-21")
//                .create();
//
//        DBPPassage passage = new DBPPassage(ref);
//        passage.download();

        System.out.println(builder.create().toJson(bibleList));
//        System.out.println(passage.getReference().toString());
//        System.out.println(passage.getText());
//        System.out.println(passage.getFormattedText());
    }
}
