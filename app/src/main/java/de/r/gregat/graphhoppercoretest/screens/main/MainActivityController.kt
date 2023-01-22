package de.r.gregat.graphhoppercoretest.screens.main

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.graphhopper.GHRequest
import com.graphhopper.GHResponse
import com.graphhopper.GraphHopper
import com.graphhopper.GraphHopperConfig
import com.graphhopper.config.LMProfile
import com.graphhopper.config.Profile
import com.graphhopper.json.Statement
import com.graphhopper.json.Statement.If
import com.graphhopper.routing.DefaultWeightingFactory
import com.graphhopper.routing.WeightingFactory
import com.graphhopper.routing.ev.TurnCost
import com.graphhopper.routing.ev.VehicleAccess
import com.graphhopper.routing.ev.VehiclePriority
import com.graphhopper.routing.ev.VehicleSpeed
import com.graphhopper.routing.weighting.DefaultTurnCostProvider
import com.graphhopper.routing.weighting.TurnCostProvider
import com.graphhopper.routing.weighting.Weighting
import com.graphhopper.routing.weighting.custom.CustomProfile
import com.graphhopper.routing.weighting.custom.CustomWeighting
import com.graphhopper.util.*
import de.r.gregat.graphhoppercoretest.utils.BackgroundThreadHelper
import de.r.gregat.graphhoppercoretest.utils.UiThreadHelper
import de.r.gregat.graphhoppercoretest.utils.io.FileSelectionEntryPoint
import de.r.gregat.graphhoppercoretest.utils.io.SelectFileParams
import de.r.gregat.graphhoppercoretest.utils.io.StorageAccessFrameworkInteractor
import de.r.gregat.graphhoppercoretest.utils.weighting.JaninoCustomWeightingHelperSubclass2
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.absolutePathString


class MainActivityController(
    private val fragmentActivity: FragmentActivity,
    private val backgroundThreadHelper: BackgroundThreadHelper,
    private val uiThreadHelper: UiThreadHelper
) : DefaultLifecycleObserver,
    MainActivityMvcView.EventListener,
    FileSelectionEntryPoint {

    private lateinit var viewMvc: MainActivityMvcView
    /*private lateinit var graphHopper: GraphHopper*/

    val graphHopper: GraphHopper = object : GraphHopper() {
        override fun createWeightingFactory(): WeightingFactory {
            return object : DefaultWeightingFactory(baseGraph, encodingManager) {
                override fun createWeighting(
                    profile: Profile,
                    hints: PMap,
                    disableTurnCosts: Boolean
                ): Weighting {
                    if("custom" == profile.weighting) {
                        val vehicle = profile.vehicle
                        val accessEnc = encodingManager.getBooleanEncodedValue(VehicleAccess.key(vehicle))
                        val speedEnc = encodingManager.getDecimalEncodedValue(VehicleSpeed.key(vehicle))
                        val priorityEnc =
                            if (encodingManager.hasEncodedValue(VehiclePriority.key(vehicle))) encodingManager.getDecimalEncodedValue(
                                VehiclePriority.key(vehicle)
                            ) else null

                        val customModel = CustomModel()
                            .addToPriority(If("road_class == STEPS", Statement.Op.MULTIPLY, "0.0"))
                            .addToPriority(If("road_class == FOOTWAY", Statement.Op.MULTIPLY, "0.5"))
                            .addToPriority(If("surface == DIRT", Statement.Op.MULTIPLY, "0.0"))
                            .addToPriority(If("surface == SAND", Statement.Op.MULTIPLY, "0.0"))
                            .addToPriority(If("surface == PAVED", Statement.Op.MULTIPLY, "1.0"))
                            .addToPriority(If("average_slope >= 5", Statement.Op.MULTIPLY, "0.1"))
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


                        val turnCostProvider: TurnCostProvider = if (profile.isTurnCosts && !disableTurnCosts) {
                            val turnCostEnc = encodingManager.getDecimalEncodedValue(TurnCost.key(vehicle))
                                ?: throw IllegalArgumentException("Vehicle $vehicle does not support turn costs")
                            val uTurnCosts =
                                hints.getInt(Parameters.Routing.U_TURN_COSTS, Weighting.INFINITE_U_TURN_COSTS)
                            DefaultTurnCostProvider(turnCostEnc, baseGraph.turnCostStorage, uTurnCosts)
                        } else {
                            TurnCostProvider.NO_TURN_COST_PROVIDER
                        }


                        return CustomWeighting(accessEnc, speedEnc, turnCostProvider, parameters)
                    }
                    return super.createWeighting(profile, hints, disableTurnCosts)
                }
            }
        }
    }.apply {
        val externalAppStorageRoot = fragmentActivity.getExternalFilesDir(null)
        val graphopperCacheFolder = File(externalAppStorageRoot, "graphhopper")

        val config = GraphHopperConfig()
            .putObject("graph.vehicles", "bike,car,foot,wheelchair,roads")
            .putObject("prepare.min_network_size", 200)
            .putObject("graph.location", graphopperCacheFolder.getAbsolutePath())
            .putObject(
                "graph.encoded_values",
                "max_slope,road_class,road_class_link,road_environment,max_speed,road_access,track_type,surface,average_slope"
            )
            .putObject(
                "custom_model_folder",
                "./src/test/resources/com/graphhopper/application/resources"
            )
            .putObject("import.osm.ignored_highways", "")
        init(config)
    }

    fun bindViewMvc(view: MainActivityMvcView) {
        this.viewMvc = view
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        viewMvc.registerListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        viewMvc.unregisterListener(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewMvc.onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        viewMvc.onPause()
    }

    override val fileSelectionOwner: FragmentActivity
        get() = fragmentActivity

    private val fileSelectionInteractor: StorageAccessFrameworkInteractor =
        StorageAccessFrameworkInteractor(this)


    private fun onSelectFileClick(selectFileParams: SelectFileParams) =
        fileSelectionInteractor.beginSelectingFile(selectFileParams)

    override fun onFileSelected(fileDescriptor: FileDescriptor?) {
        viewMvc.startCopyProcess()

        backgroundThreadHelper.post {
            val fileInputStream = FileInputStream(fileDescriptor)

            val externalAppStorageRoot = fragmentActivity.getExternalFilesDir(null)

            val path = Paths.get(externalAppStorageRoot?.path, "osm.pbf")

            Files.copy(fileInputStream, path, StandardCopyOption.REPLACE_EXISTING);
            fileInputStream.close();

            uiThreadHelper.post {
                viewMvc.copyProcessDone()
            }
        }
    }

    override fun selectPbf() {
        onSelectFileClick(SelectFileParams("application/octet-stream"))
    }


    override fun createGraphhopperInstance() {
        viewMvc.startCreateGraphhopperInstanceProcess()

        backgroundThreadHelper.post {
            try {
                /*graphHopper = GraphHopper().apply {

                }*/

                /*val graphHopper: GraphHopper = object : GraphHopper() {
                    override fun createWeightingFactory(): WeightingFactory? {
                        return object : CustomWeightingFactory() {
                            fun createWeighting(
                                profile: Profile,
                                requestHints: PMap?,
                                disableTurnCosts: Boolean
                            ): Weighting? {
                                return if ("custom" == profile.weighting) myWeighting else super.createWeighting(
                                    profile,
                                    requestHints,
                                    disableTurnCosts
                                )
                            }
                        }
                    }
                }*/



                val externalAppStorageRoot = fragmentActivity.getExternalFilesDir(null)
                val graphopperCacheFolder = File(externalAppStorageRoot, "graphhopper")
                val path = Paths.get(externalAppStorageRoot?.path, "osm.pbf")

                graphHopper.osmFile = path.absolutePathString();
                // specify where to store graphhopper files
                graphHopper.graphHopperLocation = graphopperCacheFolder.absolutePath

                // see docs/core/profiles.md to learn more about profiles
                setProfiles()

                // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
                graphHopper.importOrLoad()
            } catch (e: Exception) {
                Log.d("Graphhopper-Core-Test", e.stackTraceToString())
            } finally {
                uiThreadHelper.post {
                    viewMvc.createGraphhopperInstanceDone()
                }
            }
        }
    }

    private fun setProfiles() {
        // new CustomProfile("bus").setVehicle("roads").putHint("custom_model_file", "bus.json"),
        /*val externalAppStorageRoot = fragmentActivity.getExternalFilesDir(null)
        val graphopperCacheFolder = File(externalAppStorageRoot, "graphhopper")
        val path = Paths.get(externalAppStorageRoot?.path, "custom_profile.json")*/

        /*val customModel = CustomModel()
            .addToPriority(If("road_class == TERTIARY", Statement.Op.MULTIPLY, "1.0"))
            .addToPriority(If("road_class == PRIMARY", Statement.Op.MULTIPLY, "0.1"))
            .addToSpeed(If("road_class == PRIMARY", Statement.Op.LIMIT, "28"))
            .setDistanceInfluence(69.0)
            .setHeadingPenalty(22.0)

        val profile = Profile("car")
            .setVehicle("car")
            .setWeighting("fastest")
            .setTurnCosts(false)

        profile.hints.putObject(CustomModel.KEY, customModel)*/


        graphHopper.setProfiles(
            /*Profile("car")
                .setVehicle("car")
                .setWeighting("fastest")
                .setTurnCosts(false),*/
            CustomProfile("custom_foot")
                .setCustomModel(CustomModel()
                    .addToPriority(If("road_class == STEPS", Statement.Op.MULTIPLY, "0.0"))
                    .addToPriority(If("road_class == FOOTWAY", Statement.Op.MULTIPLY, "0.5"))
                    .addToPriority(If("surface == DIRT", Statement.Op.MULTIPLY, "0.0"))
                    .addToPriority(If("surface == SAND", Statement.Op.MULTIPLY, "0.0"))
                    .addToPriority(If("surface == PAVED", Statement.Op.MULTIPLY, "1.0"))
                    .addToPriority(If("average_slope >= 5", Statement.Op.MULTIPLY, "0.1"))
                    .setDistanceInfluence(69.0)
                    .setHeadingPenalty(22.0)
                )
                .setVehicle("foot")
                .setWeighting("custom")
                .setTurnCosts(false)
        //profile
        )

        graphHopper.lmPreparationHandler.setLMProfiles(LMProfile("custom_foot"));
        //graphHopper.chPreparationHandler.setCHProfiles(CHProfile("car"))
    }


    override fun startRouting() {
        viewMvc.startRoutingProcess()

        backgroundThreadHelper.post {

            try {
                //val req = standardCarRouting()
                val req = customProfileRouting()

                val rsp: GHResponse = graphHopper.route(req)

                // handle errors
                if (rsp.hasErrors()) throw RuntimeException(rsp.errors.toString())

                // use the best path, see the GHResponse class for more possibilities.
                val path = rsp.best

                // points, distance in meters and time in millis of the full path
                val pointList = path.points
                val distance = path.distance
                val timeInMs = path.time

                uiThreadHelper.post {
                    viewMvc.setRoutingResult(distance, timeInMs)
                    viewMvc.setGeoPoints(pointList)
                }

                val tr: Translation = graphHopper.translationMap.getWithFallBack(Locale.GERMAN)
                val il: InstructionList = path.instructions

                // iterate over all turn instructions
                val processInstructionList = il
                    .stream()
                    .map { "distance " + it.distance + " for instruction: " + it.getTurnDescription(tr) }
                    .collect(Collectors.toList())
                viewMvc.setInstructionList(processInstructionList)

            } catch (e: java.lang.RuntimeException) {
                Log.d("Graphhopper-Core-Test", e.stackTraceToString())
            } finally {
                uiThreadHelper.post {
                    viewMvc.startRoutingProcessDone()
                }
            }
        }
    }

    fun standardCarRouting(): GHRequest {
        return GHRequest(
            52.506532501639114,
            13.416267775403348,
            52.544940065357245,
            13.354310290455304
        )
            .setProfile("car")
            .setLocale(Locale.GERMANY)
    }

    private fun customProfileRouting(): GHRequest {

        // ... but for the hybrid mode we can customize the route calculation even at request time:
        // 1. a request with default preferences
        val req =  GHRequest(
            52.506532501639114,
            13.416267775403348,
            52.544940065357245,
            13.354310290455304
        )
            .setProfile("custom_foot")
            .setLocale(Locale.GERMANY)
            .putHint(Parameters.CH.DISABLE, true)

        // 2. now avoid primary roads and reduce maximum speed, see docs/core/custom-models.md for an in-depth explanation
        // and also the blog posts https://www.graphhopper.com/?s=customizable+routing
        //val model = CustomModel()

        //model.addToPriority(Statement.If("road_class == PRIMARY", Statement.Op.MULTIPLY, "0.5"))
        //model.addToPriority(Statement.If("true", Statement.Op.LIMIT, "100"))

        /*val customModel = CustomModel()
            .addToPriority(Statement.If("road_class == TERTIARY", Statement.Op.MULTIPLY, "1.0"))
            .addToPriority(Statement.If("road_class == PRIMARY", Statement.Op.MULTIPLY, "0.0"))
            .addToSpeed(Statement.If("road_class == PRIMARY", Statement.Op.LIMIT, "50"))
            .addToSpeed(Statement.If("road_class == TERTIARY", Statement.Op.LIMIT, "30"))
            .setDistanceInfluence(69.0)
            .setHeadingPenalty(22.0)

        req.customModel = customModel*/

        return req
    }
}