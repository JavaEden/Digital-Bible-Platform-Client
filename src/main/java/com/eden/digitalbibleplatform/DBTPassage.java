package com.eden.digitalbibleplatform;

import com.eden.Eden;
import com.eden.bible.Passage;
import com.eden.bible.Reference;
import com.eden.bible.Verse;
import com.eden.utils.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DBTPassage extends Passage implements JsonDeserializer<DBTPassage> {

    public DBTPassage(Reference reference) {
        super(reference);
        this.verseFormatter = new DBTFormatter();

        if (reference.getBook() instanceof DBTBook) {
            DBTBook DBTBook = (DBTBook) reference.getBook();
            this.id = DBTBook.getId() + "." + reference.getChapter();
        } else {
            this.id = "Matt.1";
        }
    }

    public boolean get() {
        String APIKey = Eden.getInstance().config().getString("DBT_ApiKey");

        if (TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException(
                    "API key not set in Eden metadata. Please add 'DBT_ApiKey' key to metadata."
            );
        }

        String dam_id = this.getReference().getBible().getId();
        String book_id = this.reference.getBook().getId();
        int chapter_id = this.reference.getChapter();

        String passageUrl = "http://dbt.io/text/verse?v=2&key=" + APIKey + "&dam_id=" + dam_id + "&book_id=" + book_id + "&chapter_id=" + chapter_id;

        try {
            OkHttpClient client = new OkHttpClient();

            // get the main data about the Bible version
            Request passageRequest = new Request.Builder()
                    .url(passageUrl)
                    .build();

            String bibleString = client
                    .newCall(passageRequest)
                    .execute()
                    .body()
                    .string();

            Gson gson = Eden.getInstance().getDeserializer().registerTypeAdapter(DBTPassage.class, this).create();
            gson.fromJson(bibleString, DBTPassage.class);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getText() {
        if (reference.getBible() instanceof DBTBible)
            return super.getText();
        else
            return super.getText();
    }

    @Override
    public DBTPassage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray versesJSON = json.getAsJsonArray();

        //add all verses to a map from which we can pick the individual verses we want
        HashMap<Integer, Verse> verseMap = new HashMap<>();
        for(int i = 0; i < versesJSON.size(); i++) {
            Reference verseReference = new Reference.Builder()
                    .setBible(reference.getBible())
                    .setBook(reference.getBook())
                    .setChapter(reference.getChapter())
                    .setVerses(i)
                    .create();
            Verse verse = new Verse(verseReference);

            String text = versesJSON
                    .get(i).getAsJsonObject()
                    .get("verse_text").getAsString()
                    .trim();

            verse.setText(text);

            verseMap.put((i+1), verse);
        }

        this.verses.clear();
        for(int i = 0; i < reference.getVerses().size(); i++) {
            Verse verseFromMap = verseMap.get(reference.getVerses().get(i));
            verses.add(verseFromMap);
        }

        return this;
    }
}
