package es.guiguegon.cabify.net.requests;

import es.guiguegon.cabify.models.Stop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Guille on 12/10/2016.
 */

public class EstimateRequest {

    private List<Stop> stops;
    private String startAt = "2016-10-13 22:59";

    private EstimateRequest(List<Stop> stops) {
        this.stops = stops;
    }

    public static EstimateRequest createEstimateRequest(Stop start, Stop stop) {
        List<Stop> stops = new ArrayList<>();
        Collections.addAll(stops, start, stop);
        return new EstimateRequest(stops);
    }
}
