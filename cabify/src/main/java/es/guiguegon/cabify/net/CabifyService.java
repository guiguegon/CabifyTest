package es.guiguegon.cabify.net;

import es.guiguegon.cabify.models.Estimate;
import es.guiguegon.cabify.net.requests.EstimateRequest;
import java.util.List;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Guille on 12/10/2016.
 */

public interface CabifyService {

    @POST("/api/v2/estimate")
    @Headers({
            "Authorization:bearer 6o_FrppOEQ5RrCkBOEKaBM-puJleMKrRn5nW_cy7H9Y",
            "Content-Type:application/json"
    })
    Observable<List<Estimate>> estimateTrip(@Body EstimateRequest estimateRequest);
}
