package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.Eden;
import com.caseyjbrooks.eden.bible.Reference;
import com.google.gson.GsonBuilder;

public class DBPTest {

    public static void main(String[] args) {
        Eden.getInstance().getMetadata().put("ABS_ApiKey", "mDaM8REZFo6itplNpcv1ls8J5PkwEz1wbhJ7p9po");

        GsonBuilder builder = Eden.getInstance().getSerializer();

//        DBPBibleList obj = new DBPBibleList();
//        obj.download();

        DBPBible bible = new DBPBible();
        bible.setId("eng-NASB");
        bible.download();

        Reference ref = new Reference.Builder()
                .setBible(bible)
                .parseReference("Galatians 2:19-21")
                .create();

        DBPPassage passage = new DBPPassage(ref);
        passage.download();

        System.out.println(builder.create().toJson(passage));
        System.out.println(passage.getReference().toString());
        System.out.println(passage.getText());
        System.out.println(passage.getFormattedText());
    }
}
