package com.eden.digitalbibleplatform;

import com.caseyjbrooks.eden.Eden;
import com.caseyjbrooks.eden.bible.BibleList;
import com.caseyjbrooks.eden.utils.TextUtils;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DBPBibleList extends BibleList<DBPBible> implements JsonDeserializer<DBPBibleList> {
    public DBPBibleList() {

    }

    public DBPBibleList download() {
        String APIKey = Eden.getInstance().getMetadata().getString("ABS_ApiKey", null);

        if (TextUtils.isEmpty(APIKey)) {
            throw new IllegalStateException(
                    "API key not set in ABT metadata. Please add 'ABS_ApiKey' key to metadata."
            );
        }

        String url = "http://" + APIKey + ":x@api-v2.bibles.org/v2/versions.js";

        try {
            OkHttpClient client = new OkHttpClient();
            String encodedHeader = Base64.getEncoder().encodeToString((APIKey + ":x").getBytes("UTF-8"));

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Basic " + encodedHeader)
                    .build();

            Response response = client.newCall(request).execute();
            String body = response.body().string();

            Type listType = new TypeToken<Map<String, DBPBible>>() {
            }.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(DBPBibleList.class, this).create();
            gson.fromJson(body, DBPBibleList.class);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public DBPBibleList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonArray biblesJson = json.getAsJsonObject().get("response").getAsJsonObject().get("versions").getAsJsonArray();

        this.bibles = new HashMap<>();

        Gson gson = Eden.getInstance().getDeserializer().registerTypeAdapter(DBPBible.class, new DBPBible.ListJsonizer()).create();

        for (int i = 0; i < biblesJson.size(); i++) {
            DBPBible bible = gson.fromJson(biblesJson.get(i), DBPBible.class);
            bibles.put(bible.getId(), bible);
        }

        return this;
    }
}
