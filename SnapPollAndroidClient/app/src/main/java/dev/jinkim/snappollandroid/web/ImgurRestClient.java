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
 * <p/>
 * Upload an image to Imgur hosting using Retrofit
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
        // Retrofit uses @Multipart for a file uploading
        @Multipart
        @POST("/image")
        // Imgur OAuth takes authorization header in form of ("Authorization Client-ID xxxxxxxx...")
        // for anonymous upload
        // Retrofit takes a file to upload in a TypedFile(file and MIME type)
        void uploadImage(@Header("Authorization") String clientId,
                         @Part("image") TypedFile file,
                         @Part("title") String title, @Part("description") String desc,
                         Callback<ImgurResponse> cb);
        // TODO: Add title and description to uploaded image
    }
}
