package com.falcon.falcon.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.falcon.falcon.utils.Require;

public class Galaxy {

    private final Map<String, List<Jump>> neighbours;

    public Galaxy(List<Route> routes){
        Require.that(routes != null, "routes are required");
        
        Map<String, List<Jump>> adjacency = new HashMap<>();
        for(Route route: routes){
            if(route.origin().equals(route.destination())){
                continue;
            }

            //Add route in both ways
            adjacency.computeIfAbsent(route.origin(), planet-> new ArrayList<>())
                .add(new Jump(route.destination(),route.travelTime()));

            adjacency.computeIfAbsent(route.destination(), planet-> new ArrayList<>())
                .add(new Jump(route.origin(),route.travelTime()));
        }

        adjacency.replaceAll((planet, jumps) -> List.copyOf(jumps));
        this.neighbours = Map.copyOf(adjacency);
    }

    public List<Jump> neighboursOf(String planet){
        return neighbours.getOrDefault(planet, List.of());
    }
}
