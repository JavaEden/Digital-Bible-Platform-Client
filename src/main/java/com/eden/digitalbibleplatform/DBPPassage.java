package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.Eden;
import com.caseyjbrooks.eden.bible.Passage;
import com.caseyjbrooks.eden.bible.Reference;
import com.caseyjbrooks.eden.utils.TextUtils;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;

public class DBPPassage extends Passage<DBPVerse> implements JsonDeserializer<DBPPassage> {

    public DBPPassage(Reference reference) {
        super(reference);
        this.formatter = new DBPFormatter();

        if (reference.getBook() instanceof DBPBook) {
            DBPBook DBPBook = (DBPBook) reference.getBook();
            this.id = DBPBook.getId() + "." + reference.getChapter();
        } else {
            this.id = "Matt.1";
        }
    }

    public DBPPassage download() {
        String APIKey = Eden.getInstance().getMetadata().getString("ABS_ApiKey", null);

        if (TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException(
                    "API key not set in ABT metadata. Please add 'ABS_ApiKey' key to metadata."
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
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
        HashMap<Integer, DBPVerse> verseMap = new HashMap<>();
        for(int i = 0; i < versesJSON.size(); i++) {
            Reference verseReference = new Reference.Builder()
                    .setBible(reference.getBible())
                    .setBook(reference.getBook())
                    .setChapter(reference.getChapter())
                    .setVerses(i)
                    .create();
            DBPVerse verse = new DBPVerse(verseReference);

            String text = versesJSON
                    .get(i).getAsJsonObject()
                    .get("text").getAsString();

            verse.setText(text);

            verseMap.put((i+1), verse);
        }

        this.verses.clear();
        for(int i = 0; i < reference.getVerses().size(); i++) {
            DBPVerse verseFromMap = verseMap.get(reference.getVerses().get(i));
            verses.add(verseFromMap);
        }

        return this;
    }
}
