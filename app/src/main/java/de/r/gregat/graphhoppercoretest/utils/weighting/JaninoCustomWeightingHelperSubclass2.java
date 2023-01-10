
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
        if (lookup.hasEncodedValue("road_class")) this.road_class_enc = (EnumEncodedValue) lookup.getEncodedValue("road_class", EncodedValue.class);
    }

    @Override public double getPriority(EdgeIteratorState edge, boolean reverse) {
        double value = super.getRawPriority(edge, reverse);

        return value;
    }

    @Override public double getSpeed(EdgeIteratorState edge, boolean reverse) {
        double value = super.getRawSpeed(edge, reverse);
        Enum road_class = reverse ? edge.getReverse((EnumEncodedValue) this.road_class_enc) : edge.get((EnumEncodedValue) this.road_class_enc);

        if (road_class == RoadClass.PRIMARY) {
            value = Math.min(value, 28);
        }
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

    protected EnumEncodedValue road_class_enc;
}
