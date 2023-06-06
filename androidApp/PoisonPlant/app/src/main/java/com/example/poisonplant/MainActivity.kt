package com.example.poisonplant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.poisonplant.ml.LitePoisonplantmodel
import com.example.poisonplant.ui.theme.PoisonPlantTheme
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    /* setting up the inputs for the plant class instances*/

    //these names are the same name as the labels for the machine learning classes
    // an extra null string is found in all these lists despite the app only handling 18 different plant classes because Lazy column back button was covering up the plant page button
    private val scientificNames = listOf("Actaea_pachypoda", "Ageratina_altiss", "Cnidoscolus_stimulosus", "Conium_maculatum",
                                "Datura_stramonium", "Datura_wrightii", "Heracleum_maximum", "Hippomane_mancinella",
                                "Nerium_oleander", "Pastinaca_sativa", "Sambucus_canadensis", "Sambucus_nigra",
                                "Solanum_dulcamara", "Toxicodendron_diversilobum", "Toxicodendron_pubescens", "Toxicodendron_radicans",
                                "Toxicodendron_vernix", "Urtica_dioica", "")

    //compiled list of common names for the scientific ones, however some of these might not be completely correct
    private val commonNames = listOf("White Bane Berry", "White Snake Root", "Spurge Nettle", "Poison Hemlock",
                            "Jimson Weed", "Angel's Trumpet", "Giant Hogweed", "Manchineel Tree", "Oleander",
                            "Wild Parsnip", "American Black Elderberry", "European Black Elderberry",
                            "Bittersweet Nightshade", "Pacific Poison Oak", "Alantic Poison Oak", "Poison Ivy",
                            "Poison Sumac", "Stinging Nettle", "")

    //brief description of why the plant is poisonous or toxic and what the symptoms of poisoning are
    private val descriptions = listOf("""Also known as Dolly's eyes, all parts of this shrub are poisonous, but especially its distinctive berries which cause burning of the mouth and throat and severe stomach cramps""",
    """This white flowered shrub is highly toxic if eaten, the toxins can even be passed through other vectors like cow milk which caused Milk Sickness in the 1820s.""",
        """This white flowered perennial native herb is covered in tiny needle-like spines that will cause severe stinging of the skin lasting a couple minutes.""",
        """This plant is often mistaken for wild carrot, however all parts of this plant are poisonous if ingested. In some cases the toxins can be absorbed through the skin. Poisoning symptoms include slowing heartbeat, paralysis, and death due to respiratory failure.""",
    """ All parts of this plant contains toxic alkaloids. The plant much like Angel's Trumpet is popular for recreational and medicinal use, however eating any part of the plant or making tea out of the leaves causes hallucinations, hyperthermia tachycardia, urinary retention, and photo phobia.""",
        """All parts of this plant are poisonous due to toxic alkaloids. However, the plant is still popular for recreational use, but many hospitalizations occur every year for users that become poisoned. Eating the plant can cause hallucination, hyperthermia tachycardia, urinary retention, and photo phobia.""",
        """The sap of this plant is photo-toxic, if it contacts the skin it causes severe photo-sensitivity, which can last for several days. This can lead to inflammation and discomfort. If you get sap on your skin immediately wash with soap and cold water and avoid sunlight for the next 48 hours. """,
        """Contact with the sap of this tree causes dermatitis and inflammation. Found in coastal, topical areas such as Florida.""",
    """While this plant is often used ornamentally especially on California highways, ingesting the plant in large quantities will cause discomfort and could lead to cardiac arrest. The plant however is bitter and unlikely to cause an unintentional death.""",
    """Contact with the sap of this plant will cause an intense localized burning rash and blistering on sunny days. Affected areas can remain discolored and sensitive to sunlight for up to two years, similar but not as severe as contact with Giant Hogweed.""",
    """Ingesting the berries of this plant raw often lead to stomach aches and diarrhea, however when cooked the berries are used to make pies and wines.""",
    """Much like the American variety the raw consumption of the plant's berries can lead to nausea, vomiting, and diarrhea, however cooked they are often made into a wide variety of treats.""",
    """This plant with distinctive purple flowers and red berries is toxic to livestock, pets, and people. The plant contains similar toxins to its more famous relative: Deadly Nightshade, however it has a very strong unpleasant odor so unintentional poisonings are infrequent.""",
    """Considered one of the most hazardous plants in the western United States, the plant contains urushiol which causes a severe skin rash when touched or severe respiratory irritation when the plant is burned.""",
    """Similar to its western variant this plant plant contains urushiol which causes severe inflammation upon contact with skin.""",
    """Direct skin contact with this plant will cause blistering similar to contact with poison oak or poison sumac. The blistering is caused by urushiol and will develop a rash that can be itchy and last for a couple weeks.""",
    """The plant contains urushiol which causes blistering upon direct skin contact. This toxin is the same toxin found in poison oak and poison ivy, however poison sumac is considered to generate the most severe reaction.""",
    """This plant is has thousands of stinging hairs which can cause an itchy burning rash that may last up to 12 hours.""", ""
    )

    //enum list that references the correct image to display for the app
    private val images = listOf(R.drawable.actaea_pachypoda1image131, R.drawable.ageratina_altissima1image162, R.drawable.cnidoscolus_stimulosus1image169,
                      R.drawable.conium_maculatum1image128, R.drawable.datura_stramonium1image142, R.drawable.datura_wrightii1image183,
                      R.drawable.heracleum_maximum1image161, R.drawable.hippomane_mancinella1image19, R.drawable.nerium_oleander2image26,
                      R.drawable.pastinaca_sativa1image122, R.drawable.sambucus_canadensis1image155, R.drawable.sambucus_nigra1image1109,
                      R.drawable.solanum_dulcamara1image120, R.drawable.toxicodendron_diversilobum1image193, R.drawable.toxicodendron_pubescens1image157,
                      R.drawable.toxicodendron_radicans1image1172, R.drawable.toxicodendron_vernix1image1132, R.drawable.urtica_dioica1image116, R.drawable.urtica_dioica1image116)

    //empty list that will hold the plant object instances
    private var plants = listOf<Plant>()

    //combined the above lists to generate the data for the plant classes
    private fun generatePlants(){
       val size = commonNames.size

        for( i in 0..size - 1) {
            val plant = Plant(commonNames[i], scientificNames[i], images[i], descriptions[i])
            //this could probably be made more efficient however I don't known how yet
            plants += plant
        }
    }

    /* ### camera initialization ### */

    //photo Uri is used heavily for the model inference generation
    private lateinit var photoUri: Uri

    //these two variables handle showing the active camera view
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    //camera internals
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false
        photoUri = uri
        shouldShowPhoto.value = true

    }

    //output the photo the apps directory
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    //handle shutting down the camera
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    //check if the app has permission to use the camera
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            shouldShowCamera.value = true
        } else {
        }
    }

    //get the permission from the user to use the camera with the app
    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("Poison Plant Predictor", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /* ### machine learning integration ### */

    private fun predict(context: Context, image: Bitmap): List<String> {

        //get the labels for the model from the labels in assets
        val fileName = "labels.txt"
        val inputString = application.assets.open(fileName).bufferedReader().use{it.readText()}
        //delimit by newline because their is one label per line in
        val plantlist = inputString.split("\n")

        //scale the image so that it fits the model
        val img = Bitmap.createScaledBitmap(image, 299, 299, true)

        //prepare the image for preprocessing
        val tensorImage = TensorImage(org.tensorflow.lite.DataType.FLOAT32)
        tensorImage.load(img)
        val byteBuffer = tensorImage.buffer

        //create a new object of the model to run the photo through
        val model = LitePoisonplantmodel.newInstance(context)
        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 299, 299, 3), org.tensorflow.lite.DataType.FLOAT32)

        inputFeature0.loadBuffer(byteBuffer)
        //run model
        val outputs = model.process(inputFeature0)
        //convert to float confidence percentage and also get the right plant type
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer
        val pred = outputFeature0.floatArray
        val maxIdx = pred.indices.maxBy { pred[it] } ?: -1

        model.close()

        return listOf(pred[maxIdx].toString(), plantlist[maxIdx])
    }

    //these composable are tied to the machine learning model and need to take advantage of the class private member to function
    @Composable
    private fun GenerateResults(navController: NavController){

        //create a dialog loading box for slower phones to give some indication to the user that the app is processing the image
       Dialog(
            onDismissRequest = {},
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                //contentAlignment= Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(White, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator()
            }
        }

        //convert the image into a bitmap
        val img = contentResolver.openInputStream(photoUri)
        val bitmap = BitmapFactory.decodeStream(img)
        img!!.close()

        //generate a prediction
        val prediction: List<String> = predict(applicationContext, bitmap)

        navController.navigate("predictionResults/" + prediction[0] + "/" + prediction[1])
    }

    //prediction class handler this a janky way for the program to not over call prediction method. This is touched by another variable in main
    private var predictHandler = true

    @Composable
    private fun PredictionResults(image: Uri, predictPercent: Float, predictedPlant: String, navController: NavController) {
        //shows the prediction results with the taken picture a confidence score, and a plant and a link to the wiki page
        Column(
            modifier = Modifier
                .background(Color(113, 187, 98))
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberImagePainter(image),
                contentDescription = null,
            )
            Text(text = "Plant Predicted: $predictedPlant", fontSize= 20.sp, fontWeight = FontWeight.Bold)

            Text(text = "Accuracy:$predictPercent", fontSize = 15.sp)

            //button to bring the user back to the camera to take a new picture
            Button(colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68)),    onClick = { shouldShowCamera.value = true; shouldShowPhoto.value = false; predictHandler = false; navController.navigate("camera") }) {
                Text(text = "Take Another Photo", color = Color.Black)
            }
            // get the index of the plant so the button can direct users to the right plant page
            val index = scientificNames.indexOf(predictedPlant)
            Button(colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68)),    onClick = {navController.navigate("plant/$index") }) {
                    Text(text = "$predictedPlant info", color = Color.Black)
                }

        }

    }


    /* ### main function ### */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //generate the plant objects on startup
        generatePlants()

        setContent {
            //internal variable that touches the private predictedHandler to control when a plant image is processed
            //this is to prevent unnesscary looping of the prediction method with could cause some serious skipping of frames
            var predicted by remember { mutableStateOf(false)}

            PoisonPlantTheme {
                //set up the navigation between screen, I tried to use a sealed class but I struggeled with making it work
                //so I ended up just using the simpler, but less extendable paradigm
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home"){
                    composable("home"){
                        //function for the home screen

                        //reset the camera defaults
                        shouldShowCamera.value = true
                        predicted = false
                        predictHandler = false


                        Home(navController)
                    }
                    composable("credits"){
                        //function for the credit screen
                        Credits(navController = navController)
                    }
                    composable("camera"){
                        //function for identifying with the camera
                        Log.d("HEY3", "We made it here")
                        if (shouldShowCamera.value) {
                            CameraView(
                                outputDirectory = outputDirectory,
                                executor = cameraExecutor,
                                onImageCaptured = ::handleImageCapture,
                                onError = { Log.e("kilo", "View error:", it) }
                            )
                        }
                        if (shouldShowPhoto.value) {
                            Image(
                                painter = rememberImagePainter(photoUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )


                            // if the model has already been run then the predicted var
                            // should be set the screen will not try to regenerate the results over and over
                            if(!predicted){
                                navController.navigate("generateResults")
                               //predicted = true
                                Log.d("HEY2", "We made it here")
                                shouldShowCamera.value = false
                                shouldShowPhoto.value = false
                            }

                        }
                    }
                    composable("generateResults"){
                        //check prediction again so it doesn't loop in this screen
                        if(!predicted) {
                            Log.d("HEY", "We made it here")
                            GenerateResults(navController = navController)
                            //reset the two prediction vars so that the composable doesn't loop again
                            predicted = true
                            predictHandler = true
                        }
                    }

                    composable("predictionResults/{prediction}/{plant}"){
                            naveBackStackEntry ->
                            //get the arguments from the navigation controller to display the right plant and confidence score
                            val prediction = naveBackStackEntry.arguments?.getString("prediction")
                            val plant = naveBackStackEntry.arguments?.getString("plant")

                        PredictionResults(
                            image = photoUri,
                            //forcing a float is probably not the best practice however I have no other idea how I should do this
                            predictPercent = prediction?.toFloat()!!,
                            predictedPlant = plant!!,
                            navController = navController
                        )
                        //reset prediction so another picture could be taken
                        predicted = predictHandler

                    }
                    composable("plantWiki"){
                        //need to pass in the plants list and correctly generate the lazy column
                        PlantWiki(plants,navController)
                    }
                    composable("plant/{plantID}"
                              ){ naveBackStackEntry ->
                                    //get the index of the plant from the navigation controller
                                    val pos = naveBackStackEntry.arguments?.getString("plantID")

                                    //super unsafe way to force an int from string to get the correct index
                                    val num = pos?.toInt()!!
                                    PlantInfo(plants[num], navController = navController)
                    }
                }
            }
        }
        //request the user if the app can use the camera
        requestCameraPermission()

        //set up some camera internals
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }
}

//need this for the back button placement
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantWiki(plants: List<Plant>, navController: NavController) {

    //use these to display the back button at the bottom
    val listState = rememberLazyListState()
    val displayButton = listState.firstVisibleItemIndex > 7

    Box {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .background(Color(113, 187, 98))
                .fillMaxWidth()
                .fillMaxHeight()

        ) {
            //header at the top of the plant wiki
            stickyHeader() {
                Text(
                    text = "Plant List",
                    //color = Color.White,
                    modifier = Modifier
                        .background(Color(22, 71, 56))
                        .padding(5.dp)
                        .fillMaxWidth()
                )
            }
            //binds each plant button to the correct navigation button
            itemsIndexed(plants) { index, item ->
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp),
                    shape = RectangleShape,
                    onClick = { navController.navigate("plant/$index") }
                    /*shape = CutCornerShape(10.dp)*/) {
                    Text(text = item.getCommonName(), color = Color.Black)
                }

            }

        }
        //makes the cool back button only visible when the user scrolls all the way down
        AnimatedVisibility(
            visible = displayButton,
            Modifier.align(Alignment.BottomCenter)
        ) {
            OutlinedButton(
                onClick = {
                    navController.navigate("home")
                },
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68)),
                modifier = Modifier.padding(5.dp)
            ) {
                Text(text = "Home", color = Color.Black)

            }
        }
    }

}


@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                Color(113, 187, 98)
            ),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(painter = painterResource(id = R.drawable.planthome2), contentDescription = null)
        Text("Welcome to the Poison Plant Predictor", textAlign = TextAlign.Center, fontSize = 30.sp)
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp),
            onClick = { navController.navigate("camera") },
            shape = CutCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68))) {
            Text(text = "camera", color = Color.Black)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp),
            onClick = { navController.navigate("plantWiki")},
            shape = CutCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68))) {
            Text(text = "Plant List", color = Color.Black)
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(50.dp),
            onClick = { navController.navigate("credits")},
            shape = CutCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68))) {
            Text(text = "credits", color = Color.Black)
        }
    }
}

@Composable
fun Credits(navController: NavController ){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(113, 187, 98))
            .fillMaxHeight()
            .fillMaxWidth()
            ){

        Text(text = "Poison Plant Predictor created by Jonah Thiele 2023", fontSize= 20.sp, fontWeight = FontWeight.Bold)
        //janky way of setting the color of the button and not the color of the button's background
        Button(colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68)),    onClick = { navController.navigate("home") }) {
            Text(text = "Back", color = Color.Black)
        }
    }
}

@Composable
fun PlantInfo(plant: Plant, navController: NavController) {
    Column(
        modifier = Modifier
            .background(Color(113, 187, 98))
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = plant.getCommonName(), fontWeight = FontWeight.Bold, fontSize = 30.sp)
        Text(text = plant.getScientificName())
        Spacer(modifier = Modifier.padding(15.dp))
        Image( modifier = Modifier.fillMaxWidth(),  painter = painterResource(plant.getImagePath()), contentDescription = "Image of " + plant.getCommonName(), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.padding(15.dp))
        Text(text = plant.getDescription())
        Spacer(modifier = Modifier.padding(10.dp))
        Button(colors = ButtonDefaults.outlinedButtonColors(Color(44, 125, 68)),    onClick = { navController.navigate("plantWiki") }) {
            Text(text = "Back to Menu", color = Color.Black)
        }
    }

}