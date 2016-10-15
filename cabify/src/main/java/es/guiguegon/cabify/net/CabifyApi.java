package es.guiguegon.cabify.net;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.guiguegon.cabify.BuildConfig;
import es.guiguegon.cabify.models.Estimate;
import es.guiguegon.cabify.net.requests.EstimateRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Guille on 12/10/2016.
 */

public class CabifyApi {

    private String API_URL = "https://test.cabify.com";
    private CabifyService cabifyService;

    @Inject
    public CabifyApi() {
        initCabify();
    }

    private void initCabify() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        clientBuilder.readTimeout(30, TimeUnit.SECONDS);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(
                FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(clientBuilder.build())
                .build();
        cabifyService = retrofit.create(CabifyService.class);
    }

    public Observable<List<Estimate>> estimateTrip(EstimateRequest estimateRequest) {
        return cabifyService.estimateTrip(estimateRequest);
    }
}
