
package de.r.gregat.graphhoppercoretest.utils.weighting;

import com.graphhopper.routing.weighting.custom.CustomWeightingHelper;
import com.graphhopper.routing.ev.EncodedValueLookup;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.routing.ev.*;
import java.util.Map;

public class JaninoCustomWeightingHelperSubclass5 extends CustomWeightingHelper {

    @Override public void init(
        EncodedValueLookup lookup,
        com.graphhopper.routing.ev.DecimalEncodedValue avgSpeedEnc,
        com.graphhopper.routing.ev.DecimalEncodedValue priorityEnc,
        Map<String, com.graphhopper.util.JsonFeature> areas
    ) {
        this.avg_speed_enc = avgSpeedEnc;
        this.priority_enc = priorityEnc;
        if (lookup.hasEncodedValue("bike_network")) this.bike_network_enc = (EnumEncodedValue) lookup.getEncodedValue("bike_network", EncodedValue.class);
        if (lookup.hasEncodedValue("smoothness")) this.smoothness_enc = (EnumEncodedValue) lookup.getEncodedValue("smoothness", EncodedValue.class);
        if (lookup.hasEncodedValue("surface")) this.surface_enc = (EnumEncodedValue) lookup.getEncodedValue("surface", EncodedValue.class);
        if (lookup.hasEncodedValue("road_class")) this.road_class_enc = (EnumEncodedValue) lookup.getEncodedValue("road_class", EncodedValue.class);
    }

    @Override public double getPriority(EdgeIteratorState edge, boolean reverse) {
        double value = super.getRawPriority(edge, reverse);
        Enum bike_network = reverse ? edge.getReverse((EnumEncodedValue) this.bike_network_enc) : edge.get((EnumEncodedValue) this.bike_network_enc);
        Enum road_class = reverse ? edge.getReverse((EnumEncodedValue) this.road_class_enc) : edge.get((EnumEncodedValue) this.road_class_enc);
        Enum surface = reverse ? edge.getReverse((EnumEncodedValue) this.surface_enc) : edge.get((EnumEncodedValue) this.surface_enc);
        Enum smoothness = reverse ? edge.getReverse((EnumEncodedValue) this.smoothness_enc) : edge.get((EnumEncodedValue) this.smoothness_enc);

        if (bike_network == RouteNetwork.INTERNATIONAL) {
            value *= 1.0;
        }
        if (bike_network == RouteNetwork.NATIONAL) {
            value *= 0.8;
        }
        if (bike_network == RouteNetwork.REGIONAL) {
            value *= 0.5;
        }
        if (bike_network == RouteNetwork.LOCAL) {
            value *= 0.3;
        }
        if (bike_network == RouteNetwork.OTHER) {
            value *= 0.1;
        }
        if (bike_network == RouteNetwork.MISSING) {
            value *= 0.1;
        }
        if (road_class == RoadClass.MOTORWAY) {
            value *= 0.0;
        }
        if (road_class == RoadClass.TRUNK) {
            value *= 0.0;
        }
        if (road_class == RoadClass.PRIMARY) {
            value *= 0.9;
        }
        if (road_class == RoadClass.SECONDARY) {
            value *= 0.6;
        }
        if (road_class == RoadClass.TERTIARY) {
            value *= 0.3;
        }
        if (road_class == RoadClass.RESIDENTIAL) {
            value *= 0.1;
        }
        if (road_class == RoadClass.UNCLASSIFIED) {
            value *= 0.5;
        }
        if (surface == Surface.ASPHALT) {
            value *= 1.0;
        }
        if (surface == Surface.CONCRETE) {
            value *= 0.9;
        }
        if (surface == Surface.PAVED) {
            value *= 0.8;
        }
        if (surface == Surface.COMPACTED) {
            value *= 0.6;
        }
        if (surface == Surface.UNPAVED) {
            value *= 0.1;
        }
        if (smoothness == Smoothness.EXCELLENT) {
            value *= 1.0;
        }
        if (smoothness == Smoothness.GOOD) {
            value *= 0.8;
        }
        if (smoothness == Smoothness.INTERMEDIATE) {
            value *= 0.6;
        }
        if (smoothness == Smoothness.BAD) {
            value *= 0.3;
        }
        if (smoothness == Smoothness.VERY_BAD) {
            value *= 0.3;
        }
        if (smoothness == Smoothness.HORRIBLE) {
            value *= 0.0;
        }
        if (smoothness == Smoothness.VERY_HORRIBLE) {
            value *= 0.0;
        }
        if (smoothness == Smoothness.IMPASSABLE) {
            value *= 0.0;
        }
        if (smoothness == Smoothness.OTHER) {
            value *= 0.3;
        }
        return value;
    }

    @Override public double getSpeed(EdgeIteratorState edge, boolean reverse) {
        double value = super.getRawSpeed(edge, reverse);

        return value;
    }

    @Override protected double getMaxSpeed() {
        return 135.0;
    }

    public double getMaxSpeedPublic() {return getMaxSpeed(); }

    @Override protected double getMaxPriority() {
        return 1.0;
    }

    public double getMaxPriorityPublic() { return getMaxPriority(); }

    protected EnumEncodedValue bike_network_enc;

    protected EnumEncodedValue smoothness_enc;

    protected EnumEncodedValue surface_enc;

    protected EnumEncodedValue road_class_enc;
}
