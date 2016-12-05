package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.Eden;
import com.caseyjbrooks.eden.bible.Passage;
import com.caseyjbrooks.eden.bible.Reference;
import com.caseyjbrooks.eden.bible.Verse;
import com.caseyjbrooks.eden.utils.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;

public class DBPPassage extends Passage implements JsonDeserializer<DBPPassage> {

    public DBPPassage(Reference reference) {
        super(reference);
        this.verseFormatter = new DBPFormatter();

        if (reference.getBook() instanceof DBPBook) {
            DBPBook DBPBook = (DBPBook) reference.getBook();
            this.id = DBPBook.getId() + "." + reference.getChapter();
        } else {
            this.id = "Matt.1";
        }
    }

    public boolean get() {
        String APIKey = Eden.getInstance().getMetadata().getString("DBT_ApiKey", null);

        if (TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException(
                    "API key not set in Eden metadata. Please add 'DBT_ApiKey' key to metadata."
            );
        }

        String url = "http://" + APIKey + ":x@bibles.org/v2/chapters/" + id + "/verses.js?include_marginalia=false";

        try {
            OkHttpClient client = new OkHttpClient();

            String encodedHeader = Base64.getEncoder().encodeToString((APIKey + ":x").getBytes("UTF-8"));

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Basic " + encodedHeader)
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            Gson gson = Eden.getInstance().getDeserializer().registerTypeAdapter(DBPPassage.class, this).create();
            gson.fromJson(body, DBPPassage.class);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getText() {
        if (reference.getBible() instanceof DBPBible)
            return super.getText() + "<br/><i>" + ((DBPBible) reference.getBible()).getCopyright() + "</i>";
        else
            return super.getText();
    }

    @Override
    public DBPPassage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray versesJSON = json.getAsJsonObject()
                .get("response").getAsJsonObject()
                .get("verses").getAsJsonArray();

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
                    .get("text").getAsString();

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
