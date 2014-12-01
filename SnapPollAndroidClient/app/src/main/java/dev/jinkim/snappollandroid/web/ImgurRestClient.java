package dev.jinkim.snappollandroid.web;


import dev.jinkim.snappollandroid.model.ImgurResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by Jin on 11/23/14.
 */
public class ImgurRestClient {
    private ApiService apiService;
    private static String BASE_URL = "https://api.imgur.com/3";
    private static String TAG = "ImgurRestClient";

    public ImgurRestClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .build();

        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

    public interface ApiService {

        @Multipart
        @POST("/image")
//        void uploadImage(@Body TypedFile file, Callback<ImgurResponse> cb);
        void uploadImage(@Header("Authorization") String clientId,
                         @Part("image") TypedFile file, Callback<ImgurResponse> cb);
    }
}
