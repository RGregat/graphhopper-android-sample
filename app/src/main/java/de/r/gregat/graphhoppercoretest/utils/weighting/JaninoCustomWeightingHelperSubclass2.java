
package de.r.gregat.graphhoppercoretest.utils.weighting;

import com.graphhopper.routing.weighting.custom.CustomWeightingHelper;
import com.graphhopper.routing.ev.EncodedValueLookup;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.routing.ev.*;
import java.util.Map;

public class JaninoCustomWeightingHelperSubclass2 extends CustomWeightingHelper {

    @Override public void init(
            EncodedValueLookup lookup,
            com.graphhopper.routing.ev.DecimalEncodedValue avgSpeedEnc,
            com.graphhopper.routing.ev.DecimalEncodedValue priorityEnc,
            Map<String, com.graphhopper.util.JsonFeature> areas
    ) {
        this.avg_speed_enc = avgSpeedEnc;
        this.priority_enc = priorityEnc;
        if (lookup.hasEncodedValue("surface")) this.surface_enc = (EnumEncodedValue) lookup.getEncodedValue("surface", EncodedValue.class);
        if (lookup.hasEncodedValue("road_class")) this.road_class_enc = (EnumEncodedValue) lookup.getEncodedValue("road_class", EncodedValue.class);
        if (lookup.hasEncodedValue("average_slope")) this.average_slope_enc = (DecimalEncodedValue) lookup.getEncodedValue("average_slope", EncodedValue.class);
    }

    @Override public double getPriority(EdgeIteratorState edge, boolean reverse) {
        double value = super.getRawPriority(edge, reverse);
        Enum road_class = reverse ? edge.getReverse((EnumEncodedValue) this.road_class_enc) : edge.get((EnumEncodedValue) this.road_class_enc);
        Enum surface = reverse ? edge.getReverse((EnumEncodedValue) this.surface_enc) : edge.get((EnumEncodedValue) this.surface_enc);
        double average_slope = reverse ? edge.getReverse((DecimalEncodedValue) this.average_slope_enc) : edge.get((DecimalEncodedValue) this.average_slope_enc);

        if (road_class == RoadClass.STEPS) {
            value *= 0.0;
        }
        if (road_class == RoadClass.FOOTWAY) {
            value *= 0.5;
        }
        if (surface == Surface.DIRT) {
            value *= 0.0;
        }
        if (surface == Surface.SAND) {
            value *= 0.0;
        }
        if (surface == Surface.PAVED) {
            value *= 1.0;
        }
        if (average_slope >= 5) {
            value *= 0.1;
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

    protected EnumEncodedValue surface_enc;

    protected EnumEncodedValue road_class_enc;

    protected DecimalEncodedValue average_slope_enc;
}
