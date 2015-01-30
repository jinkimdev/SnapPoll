package dev.jinkim.snappollandroid.web;


import android.os.Build;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import dev.jinkim.snappollandroid.model.Poll;
import dev.jinkim.snappollandroid.model.User;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Jin on 11/23/14.
 */
public class SnapPollRestClient {
    private ApiService apiService;
    private static String BASE_URL = "http://snappoll.herokuapp.com/api/";
    private final boolean USE_PRODUCTION_ENDPOINT = false;
    private static final String LOCAL_ENDPOINT = "http://192.168.56.1:5000/api/"; // genymotion host ip
    private static String TAG = "RestClient";

    public SnapPollRestClient() {

        String endpoint = chooseEndpoint(USE_PRODUCTION_ENDPOINT);

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

    private String chooseEndpoint(boolean useProduction) {

        if (!useProduction) {
            if (Build.FINGERPRINT.startsWith("generic")) {
                // if running on emulator
                Log.d(TAG, "Running on emulator - use local api end point");

                // Uncomment the following line to use the local backend and db
                return LOCAL_ENDPOINT;
            }
        }
        return BASE_URL;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public interface ApiService {
        @GET("/poll")
        void getPolls(Callback<List<Poll>> cb);

        @GET("/poll/{poll_id}")
        void getPoll(@Path("poll_id") int pollId, Callback<Poll> cb);

        @GET("/poll/my/{user_id}")
        void getMyPolls(@Path("user_id") String userId, Callback<List<Poll>> cb);

        @GET("/poll/invited/{user_id}")
        void getInvitedPolls(@Path("user_id") String userId, Callback<List<Poll>> cb);

        @FormUrlEncoded
        @POST("/poll")
        void postPoll(@Field("creator_id") String creatorId,
                      @Field("title") String title,
                      @Field("question") String question,
                      @Field("multiple_response_allowed") Boolean multiple,
                      @Field("reference_url") String url,
                      @Field("reference_delete_hash") String deleteHash, Callback<Poll> cb);

        @POST("/user")
        void loginUser(@Body User user, Callback<Object> cb);
    }
}
