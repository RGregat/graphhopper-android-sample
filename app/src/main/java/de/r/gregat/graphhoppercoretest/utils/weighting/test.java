package de.r.gregat.graphhoppercoretest.utils.weighting;

import androidx.annotation.NonNull;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.routing.DefaultWeightingFactory;
import com.graphhopper.routing.WeightingFactory;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.routing.weighting.custom.CustomWeighting;
import com.graphhopper.util.PMap;

public class test {

    public void test() {
        GraphHopper graphHopper = new GraphHopper() {
            @Override
            protected WeightingFactory createWeightingFactory() {
                return new CustomWeightingFactory(this.getBaseGraph(), this.getEncodingManager()) {
                    @NonNull
                    @Override
                    public Weighting createWeighting(@NonNull Profile profile, @NonNull PMap hints, boolean disableTurnCosts) {

                        return super.createWeighting(profile, hints, disableTurnCosts);
                    }
                };
            }
        };
    }

    /*
    GraphHopper graphHopper = new GraphHopper() {
  protected WeightingFactory createWeightingFactory() {
    return new DefaultWeightingFactory() {
       public Weighting createWeighting(Profile profile, PMap requestHints, boolean disableTurnCosts) {
             if("custom".equals(profile.getWeighting()))
                // see DefaultWeightingFactory on how to get the required objects like accessEnc, speedEnc, etc
                return myWeighting;
            return super.createWeighting(profile, requestHints, disableTurnCosts);
       }
    }
  }
}
     */
}
