package dev.jinkim.snappollandroidclient.web;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import dev.jinkim.snappollandroidclient.model.Poll;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Jin on 11/23/14.
 */
public class RestClient {
    private ApiService apiService;
    //    private static final String BASE_URL = "http://snappoll.herokuapp.com/api/";
    private static final String BASE_URL = "http://192.168.56.1:5000/api/"; // genymotion host ip

    public RestClient() {

        Gson gson = new GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

    public interface ApiService {
        @GET("/poll")
        void getPolls(Callback<List<Poll>> cb);

        @GET("/poll/{poll_id}")
        void getPoll(@Path("poll_id") int pollId, Callback<Poll> cb);
    }
}
