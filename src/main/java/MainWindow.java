import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.*;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;
import com.esri.arcgisruntime.tasks.geocode.ReverseGeocodeParameters;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main window that display a map, the caninettes and the menu
 */
class MainWindow extends JFrame implements ActionListener {

    // Buttons
    private JButton btnConnexion, btnCaninettesHS, btnQuitter, btnListeCani;

    // CONSTANTS
    // Location in Geneva where the caninettes are located
    private static final double LAT_GVA = 46.20692080361156;
    private static final double LON_GVA = 6.142971280091718;

    // Map zoom level in order to reduce blank zones where there is no caninettes
    private static final int ZOOM_LEVEL = 15;

    // Window size
    private static final int SCENE_SIZE_X = 800;
    private static final int SCENE_SIZE_Y = 700;

    // Colors for the points
    private static final int HEX_RED = 0xFFFF0000;
    private static final int HEX_BLUE = 0xFF0000FF;

    private static final String EDIT_WINDOW_TITLE = "Formulaire d'édition";

    private static GraphicsOverlay graphicsOverlay;
    private ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;

    private ProgressIndicator progressIndicator;
    private LocatorTask locatorTask;
    private Connection connectionStatus;
    private MapView mapView;

    MainWindow(String aTitle) {
        setTitle(aTitle);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Window menu set-up
        JPanel jpHaut = new JPanel(new BorderLayout());
        add(jpHaut, "North");
        JPanel jpWest = new JPanel();
        jpHaut.add(jpWest, "West");

        btnConnexion = new JButton("Connection");
        btnConnexion.setName("btnConnexion");
        jpWest.add(btnConnexion);
        btnConnexion.addActionListener(this);

        this.connectionStatus = new Connection(btnConnexion);

        btnListeCani = new JButton("Liste des caninettes");
        btnListeCani.setName("btnListeCani");
        jpWest.add(btnListeCani);
        btnListeCani.addActionListener(this);

        btnCaninettesHS = new JButton("Caninettes hors service");
        btnCaninettesHS.setName("btnCaninettesHS");
        jpWest.add(btnCaninettesHS);
        btnCaninettesHS.addActionListener(this);

        btnQuitter = new JButton("Quitter");
        jpWest.add(btnQuitter);
        btnQuitter.addActionListener(this);

        // Create stack pane and application scene
        StackPane stackPane = new StackPane();
        JFXPanel jfxPanel = new JFXPanel();
        add(jfxPanel);
        Scene scene = new Scene(stackPane, SCENE_SIZE_X, SCENE_SIZE_Y);
        jfxPanel.setScene(scene);

        // Create a ArcGISMap with Location and Zoom settings
        ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, LAT_GVA, LON_GVA, ZOOM_LEVEL);

        // Set the map to be displayed in this view
        mapView = new MapView();
        mapView.setMap(map);

        setupGraphicsOverlay();

        /* Reverse Geocoding
         * https://developers.arcgis.com/java/latest/sample-code/reverse-geocode-online.htm
         */
        // create a locator task
        locatorTask = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
        // create geocode task parameters
        ReverseGeocodeParameters reverseGeocodeParameters = new ReverseGeocodeParameters();
        reverseGeocodeParameters.setOutputSpatialReference(mapView.getSpatialReference());

        // create a progress indicator
        progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setMaxSize(40, 40);
        progressIndicator.setStyle("-fx-progress-color: white;");
        progressIndicator.setVisible(false);

        // Adds a click listener to the map
        mapView.setOnMouseClicked(e -> {
            // Left mouse button : display caninette details
            if (e.getButton() == MouseButton.PRIMARY && e.isStillSincePress()) {

                // Create a point from location clicked
                Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

                // Identify graphics on the graphics overlay
                identifyGraphics = mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 1, false);
                identifyGraphics.addDoneListener(() -> Platform.runLater(this::openDialog));

                // Right mouse button if connected
            } else if (e.getButton() == MouseButton.SECONDARY && e.isStillSincePress() && connectionStatus.isConnected()) {

                /* Projection (conversion) of a map point to WKID 2056 instead of WKID 3857
                 * http://spatialreference.org/ref/epsg/ch1903-lv95/
                 * https://developers.arcgis.com/java/latest/guide/spatial-references.htm#ESRI_SECTION2_D54CD676246646358B2CFE64BC732796
                 * https://developers.arcgis.com/java/latest/sample-code/project.htm
                 * https://epsg.io/transform#s_srs=2056&t_srs=4326&x=2500066.3000000&y=1118127.1500000
                 */

                Point2D point2D = new Point2D(e.getX(), e.getY());

                // get clicked location on the map
                Point originalPoint = mapView.screenToLocation(point2D);
                System.out.println(originalPoint);
                // project the web mercator point to WGS84 (WKID 4326)
                Point projectedPoint = (Point) GeometryEngine.project(originalPoint, SpatialReference.create(2056));
                double px = projectedPoint.getX();
                double py = projectedPoint.getY();

                /* Reverse geocoding
                 * https://developers.arcgis.com/java/latest/sample-code/reverse-geocode-online.htm
                 */
                // show progress indicator
                progressIndicator.setVisible(true);

                // run the locator geocode task
                ListenableFuture<List<GeocodeResult>> results = locatorTask.reverseGeocodeAsync(originalPoint,
                        reverseGeocodeParameters);

                // add a listener to display the result when loaded
                results.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<GeocodeResult> geocodes = results.get();

                            if (geocodes.size() > 0) {
                                // get the top result
                                GeocodeResult geocode = geocodes.get(0);

                                // set the viewpoint to the marker
                                Point location = geocode.getDisplayLocation();
                                mapView.setViewpointCenterAsync(location);

                                // get attributes from the result
                                String address = geocode.getAttributes().get("Match_addr").toString();
                                String street = address.split(",")[0];

                                Platform.runLater(() -> {
                                    progressIndicator.setVisible(false);

                                    // Show the EditForm with the details filled in
                                    EditForm editform = new EditForm(EDIT_WINDOW_TITLE, connectionStatus.isConnected(), true);
                                    editform.setAddress(street);
                                    editform.setLatitude(py);
                                    editform.setLongitude(px);
                                    editform.setVisible(true);
                                });
                            }

                        } catch (Exception ex) {

                            // Handle address not found
                            progressIndicator.setVisible(false);
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setHeaderText(null);
                                alert.setContentText("No address found at this location");
                                alert.showAndWait();
                            });
                        }
                    }
                });
            }
        });

        // Add map view and progress indicator to stack pane
        Platform.runLater(() -> {
            stackPane.getChildren().addAll(mapView, progressIndicator);
            StackPane.setAlignment(progressIndicator, Pos.CENTER);
        });
    }

    /**
     * Menu action
     *
     * @param event Event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(btnConnexion)) {
            if (!connectionStatus.isConnected()) {
                LoginForm log = new LoginForm(connectionStatus);
                log.pack();
                log.setLocationRelativeTo(null);
                log.setVisible(true);

            } else {
                connectionStatus.setDeconnection();
            }
        }
        if (event.getSource().equals(btnQuitter)) {
            System.exit(0);
        }

        if (event.getSource().equals(btnListeCani)) {
            CaninetteListWindow fList = null;
            try {
                fList = new CaninetteListWindow("La liste de toutes les canninettes");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            fList.pack();
            fList.setLocationRelativeTo(null);
            fList.setVisible(true);
        }

        if (event.getSource().equals(btnCaninettesHS)) {
            CaninetteOooListWindow fList = null;
            try {
                fList = new CaninetteOooListWindow("La liste des canninettes hors service");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            fList.pack();
            fList.setLocationRelativeTo(null);
            fList.setVisible(true);
        }
    }

    /**
     * Create a caninettes list
     *
     * @return ArrayList
     */
    private static ArrayList<Caninette> getCaninettesList() {
        ArrayList<Caninette> caninetteList = null;
        try {
            DaoCaninette daoCani = new DaoCaninette(CaniCrottes.getSqliteConnection(false));
            caninetteList = daoCani.displayCaninettes();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return caninetteList;
    }

    /**
     * Creates a graphic overlay and adds the points on the map
     */
    private void setupGraphicsOverlay() {
        if (mapView != null) {
            graphicsOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay);
        }
        if (graphicsOverlay != null) {
            for (Caninette c : getCaninettesList()) {
                createCaninettePoint(c);
            }
        }

    }

    /**
     * Clears the map and add points
     */
    static void refreshMap() {
        graphicsOverlay.getGraphics().clear();
        for (Caninette c : getCaninettesList()) {
            createCaninettePoint(c);
        }
    }

    /**
     * Creates a caninette point for the Map
     *
     * @param caninette Caninette
     */
    static void createCaninettePoint(Caninette caninette) {

        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, HEX_RED, 5.0f);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, HEX_BLUE, 3.0f));

        SpatialReference wgs84 = SpatialReference.create(2056);

        double lat = caninette.getPositionN();
        double lon = caninette.getPositionE();

        Map<String, Object> attributes;

        attributes = new HashMap<>();
        attributes.put("Adresse", caninette.getAdresse());
        attributes.put("Etat", caninette.getEtat());
        attributes.put("Numero", caninette.getNumero());
        attributes.put("Remarque", caninette.getRemarque());
        attributes.put("Id", caninette.getId());

        com.esri.arcgisruntime.geometry.Point point = new Point(lon, lat, wgs84);

        Graphic pointGraphic = new Graphic(point, attributes, pointSymbol);
        graphicsOverlay.getGraphics().add(pointGraphic);
    }

    /**
     * Displays a dialog window when a point {@link Graphic} is clicked
     */
    private void openDialog() {

        try {
            // Get the list of graphics returned by identify
            IdentifyGraphicsOverlayResult result = identifyGraphics.get();
            List<Graphic> graphics = result.getGraphics();

            if (!graphics.isEmpty()) {

                // Shows the EditForm if a graphic was returned
                EditForm editform = new EditForm(EDIT_WINDOW_TITLE, connectionStatus.isConnected(), false);

                editform.setId(Integer.parseInt(graphics.get(0).getAttributes().get("Id").toString()));
                editform.setAddress(graphics.get(0).getAttributes().get("Adresse").toString());
                editform.setStatus(graphics.get(0).getAttributes().get("Etat").toString());
                editform.setNote(graphics.get(0).getAttributes().get("Remarque").toString());
                editform.setNumber(graphics.get(0).getAttributes().get("Numero").toString());

                editform.setVisible(true);
            }
        } catch (Exception e) {
            // On any error, display the stack trace
            e.printStackTrace();
        }
    }
}