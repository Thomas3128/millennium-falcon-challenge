package com.falcon.falcon.routes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

class GalaxyTest {

    private static final List<Route> EXAMPLE_ROUTES = List.of(
            new Route("Tatooine", "Dagobah", 6),
            new Route("Dagobah", "Endor", 4),
            new Route("Dagobah", "Hoth", 1),
            new Route("Hoth", "Endor", 1),
            new Route("Tatooine", "Hoth", 6));

    private final Galaxy galaxy = new Galaxy(EXAMPLE_ROUTES);

    @Test
    void lists_the_neighbours_of_the_departure_planet() {
        assertThat(galaxy.neighboursOf("Tatooine"))
                .containsExactlyInAnyOrder(new Jump("Dagobah", 6), new Jump("Hoth", 6));
    }

    @Test
    void travels_routes_backwards_too() {
        assertThat(galaxy.neighboursOf("Endor"))
                .containsExactlyInAnyOrder(new Jump("Dagobah", 4), new Jump("Hoth", 1));
    }

    @Test
    void lists_the_neighbours_of_a_planet_in_the_middle() {
        assertThat(galaxy.neighboursOf("Hoth"))
                .containsExactlyInAnyOrder(
                        new Jump("Dagobah", 1),
                        new Jump("Endor", 1),
                        new Jump("Tatooine", 6));
    }

    @Test
    void returns_no_neighbour_for_an_unknown_planet() {
        assertThat(galaxy.neighboursOf("earth")).isEmpty();
    }

    @Test
    void ignores_a_route_leading_back_to_its_own_origin() {
        Galaxy withSelfLoop = new Galaxy(List.of(new Route("Hoth", "Hoth", 2)));

        assertThat(withSelfLoop.neighboursOf("Hoth")).isEmpty();
    }

    @Test
    void keeps_several_routes_between_the_same_two_planets() {
        Galaxy withDuplicate = new Galaxy(List.of(
                new Route("Hoth", "Endor", 1),
                new Route("Hoth", "Endor", 5)));

        assertThat(withDuplicate.neighboursOf("Hoth"))
                .containsExactlyInAnyOrder(new Jump("Endor", 1), new Jump("Endor", 5));
    }

    @Test
    void cannot_be_altered_through_the_list_it_returns() {
        List<Jump> neighbours = galaxy.neighboursOf("Tatooine");

        assertThatThrownBy(() -> neighbours.add(new Jump("earth", 1)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejects_a_missing_route_list() {
        assertThatThrownBy(() -> new Galaxy(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
