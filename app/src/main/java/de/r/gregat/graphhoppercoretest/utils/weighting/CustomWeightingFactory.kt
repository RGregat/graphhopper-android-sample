package de.r.gregat.graphhoppercoretest.utils.weighting

import com.graphhopper.config.Profile
import com.graphhopper.json.Statement
import com.graphhopper.routing.WeightingFactory
import com.graphhopper.routing.ev.TurnCost
import com.graphhopper.routing.ev.VehicleAccess
import com.graphhopper.routing.ev.VehiclePriority
import com.graphhopper.routing.ev.VehicleSpeed
import com.graphhopper.routing.util.EncodingManager
import com.graphhopper.routing.util.VehicleEncodedValues
import com.graphhopper.routing.weighting.DefaultTurnCostProvider
import com.graphhopper.routing.weighting.FastestWeighting
import com.graphhopper.routing.weighting.TurnCostProvider
import com.graphhopper.routing.weighting.Weighting
import com.graphhopper.routing.weighting.custom.CustomWeighting
import com.graphhopper.routing.weighting.custom.CustomWeighting.EdgeToDoubleMapping
import com.graphhopper.routing.weighting.custom.CustomWeightingHelper
import com.graphhopper.storage.BaseGraph
import com.graphhopper.util.CustomModel
import com.graphhopper.util.EdgeIteratorState
import com.graphhopper.util.PMap
import com.graphhopper.util.Parameters

open class CustomWeightingFactory(
    private val graph: BaseGraph,
    private val encodingManager: EncodingManager
) : WeightingFactory {
    override fun createWeighting(
        profile: Profile,
        hints: PMap,
        disableTurnCosts: Boolean
    ): Weighting {
        val vehicle = profile.vehicle
        val accessEnc = encodingManager.getBooleanEncodedValue(VehicleAccess.key(vehicle))
        val speedEnc = encodingManager.getDecimalEncodedValue(VehicleSpeed.key(vehicle))
        val priorityEnc =
            if (encodingManager.hasEncodedValue(VehiclePriority.key(vehicle))) encodingManager.getDecimalEncodedValue(
                VehiclePriority.key(vehicle)
            ) else null

        val customModel = CustomModel()
            .addToPriority(Statement.If("road_class == TERTIARY", Statement.Op.MULTIPLY, "1.0"))
            .addToPriority(Statement.If("road_class == PRIMARY", Statement.Op.MULTIPLY, "0.1"))
            .addToSpeed(Statement.If("road_class == PRIMARY", Statement.Op.LIMIT, "28"))
            .setDistanceInfluence(69.0)
            .setHeadingPenalty(22.0)

        val prio = JaninoCustomWeightingHelperSubclass2()

        prio.init(encodingManager, speedEnc, priorityEnc, customModel.areas)

        val parameters: CustomWeighting.Parameters = CustomWeighting.Parameters(
            { edge: EdgeIteratorState?, reverse: Boolean ->
                prio.getSpeed(
                    edge,
                    reverse
                )
            },
            { edge: EdgeIteratorState?, reverse: Boolean ->
                prio.getPriority(
                    edge,
                    reverse
                )
            },
            prio.maxSpeedPublic,
            prio.maxPriorityPublic,
            customModel.distanceInfluence,
            customModel.headingPenalty)


        /*
        val paramsReflection = Class.forName("com.graphhopper.routing.weighting.custom\$Parameters")

        val constructor = paramsReflection.getDeclaredConstructor()

        constructor.isAccessible = true

        val parameters = constructor.newInstance(EdgeToDoubleMapping { edge: EdgeIteratorState?, reverse: Boolean ->
            prio.getSpeed(
                edge,
                reverse
            )
        },
            EdgeToDoubleMapping { edge: EdgeIteratorState?, reverse: Boolean ->
                prio.getPriority(
                    edge,
                    reverse
                )
            },
            prio.maxSpeedPublic,
            prio.maxPriorityPublic,
            customModel.distanceInfluence,
            customModel.headingPenalty)
            */



        if (isOutdoorVehicle(vehicle)) {
            hints.putObject(
                FastestWeighting.PRIVATE_FACTOR,
                hints.getDouble(FastestWeighting.PRIVATE_FACTOR, 1.2)
            )
        } else {
            hints.putObject(
                FastestWeighting.DESTINATION_FACTOR,
                hints.getDouble(FastestWeighting.DESTINATION_FACTOR, 10.0)
            )
            hints.putObject(
                FastestWeighting.PRIVATE_FACTOR,
                hints.getDouble(FastestWeighting.PRIVATE_FACTOR, 10.0)
            )
        }
        val turnCostProvider: TurnCostProvider = if (profile.isTurnCosts && !disableTurnCosts) {
            val turnCostEnc = encodingManager.getDecimalEncodedValue(TurnCost.key(vehicle))
                ?: throw IllegalArgumentException("Vehicle $vehicle does not support turn costs")
            val uTurnCosts =
                hints.getInt(Parameters.Routing.U_TURN_COSTS, Weighting.INFINITE_U_TURN_COSTS)
            DefaultTurnCostProvider(turnCostEnc, graph.turnCostStorage, uTurnCosts)
        } else {
            TurnCostProvider.NO_TURN_COST_PROVIDER
        }




        return CustomWeighting(accessEnc, speedEnc, turnCostProvider, parameters)
    }


    private fun isOutdoorVehicle(name: String?): Boolean {
        return VehicleEncodedValues.OUTDOOR_VEHICLES.contains(name)
    }
}