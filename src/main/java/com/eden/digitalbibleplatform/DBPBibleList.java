package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.Eden;
import com.caseyjbrooks.eden.bible.BibleList;
import com.caseyjbrooks.eden.utils.TextUtils;
import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DBPBibleList extends BibleList<DBPBible> implements JsonDeserializer<DBPBibleList> {
    public DBPBibleList() {

    }

    public DBPBibleList download() {
        String APIKey = Eden.getInstance().getMetadata().getString("DBT_ApiKey", null);

        if (TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException(
                    "API key not set in Eden metadata. Please add 'DBT_ApiKey' key to metadata."
            );
        }

        String url = "http://dbt.io/library/volume?key=" + APIKey + "&v=2";

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            Gson gson = new GsonBuilder().registerTypeAdapter(DBPBibleList.class, this).create();
            gson.fromJson(body, DBPBibleList.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public DBPBibleList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonArray biblesJson = json.getAsJsonArray();

        this.bibles = new HashMap<>();

        Gson gson = Eden.getInstance().getDeserializer().registerTypeAdapter(DBPBible.class, new DBPBible.ListJsonizer()).create();

        for (int i = 0; i < biblesJson.size(); i++) {
            DBPBible bible = gson.fromJson(biblesJson.get(i), DBPBible.class);
            bibles.put(bible.getId(), bible);
        }

        return this;
    }
}
