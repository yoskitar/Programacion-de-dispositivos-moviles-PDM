package e.oscarjimfer.pruebatfg;

import com.apollographql.apollo.ApolloClient;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by OSCAR on 13/02/2018.
 */

public class MyApolloClient {

   /* private static final String BASE_URL = "http://<YOUR_IP>/<YOUR_ROUTE>"; //DIRECCION DE RUTA EN SERVIDOR XAMPP
    private static final String BASE_URL_DOWNLOAD = "http://<YOUR_IP>/<YOUR_ROUTE>";*/
    private static ApolloClient myApolloClient;
    private static String X_TOKEN = null;

    public static ApolloClient getMyApolloClient(final String headerValue) {
        X_TOKEN = headerValue;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request().newBuilder().header("x-token", headerValue).build());
                    }
                })
                .build();

        myApolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();

        return myApolloClient;
    }
    public static ApolloClient getMyApolloClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        myApolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();

        return myApolloClient;
    }

    public static String getBaseUrl(){
        return BASE_URL;
    }
    public static String getBaseUrlDownload(){
        return BASE_URL_DOWNLOAD;
    }

    public void setTokenAuth(String token){
        X_TOKEN = token;
    }
}
