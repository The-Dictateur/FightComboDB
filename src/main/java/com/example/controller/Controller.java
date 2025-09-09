package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.fight_combo_db.CharRepository;
import com.example.fight_combo_db.NoteRepository;
import com.example.model.Juego;
import com.example.model.Nota;
import com.example.model.NotaDTO;
import com.example.model.Personaje;
import com.example.service.CharService;
import com.example.service.GameService;
import com.example.service.NoteService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


@Component
public class Controller {

    @FXML
    private ComboBox<String> combo_game;

    @FXML
    private ComboBox<String> combo_char;

    @FXML
    private StackPane stackPaneGame;

    @FXML
    private StackPane stackPaneChar;

    @FXML
    private Button buttonNewEntry;

    @Autowired
    private GameService gameService;

    @Autowired
    private CharService charService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CharRepository charRepository;

    @FXML
    private ScrollPane scrollNotas;

    @FXML
    private VBox noteContainer;

    @FXML
    private MenuItem ItemImport;

    @FXML
    private MenuItem ItemExport;

    @FXML
    private MenuItem ItemUpdate;

    @FXML
    private Button buttonRefresh;

    public void initialize() {
        System.out.println("Controller initialized");
        noteContainer.getChildren().clear();
        cargarJuegos();
        
        combo_game.setOnAction(event -> {
            noteContainer.getChildren().clear();
            stackPaneGame.getChildren().clear(); // Limpiar logo del juego
            String selectedGame = combo_game.getSelectionModel().getSelectedItem();
            Juego juego = gameService.obtenerTodosLosJuegos().stream()
                .filter(j -> j.getNombre().equals(selectedGame))
                .findFirst()
                .orElse(null);
            mostrarLogoJuego(juego);
            combo_char.getSelectionModel().clearSelection(); // Limpiar selección de personaje
            combo_char.getItems().clear(); // Limpiar personajes al cambiar juego
            stackPaneChar.getChildren().clear(); // Limpiar logo del personaje
            cargarPersonajes();
        });

        combo_char.setOnAction(event -> {
            noteContainer.getChildren().clear();
            String selectedChar = combo_char.getSelectionModel().getSelectedItem();
            String selectedGame = combo_game.getSelectionModel().getSelectedItem();
            Personaje personaje = charService.obtenerPersonajePorNombreYJuego(selectedChar, selectedGame);
            mostrarLogoPersonaje(personaje);
            System.out.println("Personaje seleccionado: " + (personaje != null ? personaje.getId() : "Ninguno"));
            mostrarNotas(personaje);
        });

        buttonNewEntry.setOnAction(event -> {
            String selectedChar = combo_char.getSelectionModel().getSelectedItem();
            String selectedGame = combo_game.getSelectionModel().getSelectedItem();
            Personaje personaje = charService.obtenerPersonajePorNombreYJuego(selectedChar, selectedGame);

            if (personaje == null) {
                ControllerInfo.showInfo("No character selected or character does not exist.");
                return;
            }

            System.out.println("Botón Nueva Entrada presionado");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Note.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent root = null;
            try {
                root = fxmlLoader.load();
                ControllerNote controllerNote = fxmlLoader.getController();
                controllerNote.setPersonajeId(personaje.getId()); // Cogemos ID del personaje
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("FightComboDB");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Hace la ventana modal
            stage.showAndWait(); // Espera hasta que se cierre
            mostrarNotas(personaje);
        });

        ItemExport.setOnAction(event -> {
            List<Nota> notas = noteRepository.findAll();

            List<NotaDTO> notasDTO = notas.stream()
            .map(n -> new NotaDTO(n.getTitulo(), n.getContenido(), n.getPersonaje().getId()))
            .collect(Collectors.toList());

            String userHome = System.getProperty("user.home");
            Path downloadPath = Paths.get(userHome, "Downloads", "notas.json");
            ObjectMapper mapper = new ObjectMapper();

            try {
                mapper.writeValue(downloadPath.toFile(), notasDTO);
                System.out.println("Notas exportadas correctamente a " + downloadPath);
                ControllerInfo.showInfo("Notes exported successfully to " + downloadPath);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al exportar notas: " + e.getMessage());
            }
        });

        ItemImport.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo JSON de notas");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos JSON", "*.json")
            );

            Stage stage = (Stage) noteContainer.getScene().getWindow();

            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {

                ObjectMapper mapper = new ObjectMapper();

                try {

                    List<NotaDTO> notasDTO = Arrays.asList(
                    mapper.readValue(selectedFile, NotaDTO[].class)
                    );

                    for (NotaDTO dto : notasDTO) {
                        Nota nota = new Nota();
                        nota.setTitulo(dto.getTitulo());
                        nota.setContenido(dto.getContenido());

                        // Buscar personaje por ID
                        Personaje personaje = charRepository.findById(dto.getIdPersonaje())
                                .orElse(null); // si no existe, se deja null o manejar error
                        nota.setPersonaje(personaje);

                        noteRepository.save(nota); // se genera automáticamente el id
                    }
                    ControllerInfo.showInfo("Notes imported successfully.");
                    System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        });

        ItemUpdate.setOnAction(event -> {
            ControllerInfo.showInfo("Update feature is not implemented yet.");
        });

        buttonRefresh.setOnAction(event -> {
            String selectedChar = combo_char.getSelectionModel().getSelectedItem();
            String selectedGame = combo_game.getSelectionModel().getSelectedItem();
            Personaje personaje = charService.obtenerPersonajePorNombreYJuego(selectedChar, selectedGame);
            mostrarNotas(personaje);
        });
    }

    public void cargarJuegos() {
        combo_game.getItems().clear();
        gameService.obtenerTodosLosJuegos().forEach(juego -> combo_game.getItems().add(juego.getNombre()));
    }

    public void cargarPersonajes() {
        combo_char.getItems().clear();
        String selectedGame = combo_game.getSelectionModel().getSelectedItem();
        charService.obtenerPersonajePorJuego(selectedGame).forEach(personaje -> combo_char.getItems().add(personaje.getNombre()));
    }

    public void mostrarLogoJuego(Juego juego) {
        if (combo_game.getSelectionModel().isEmpty()) return;

        try {
            String ruta = "/logos/" + combo_game.getSelectionModel().getSelectedItem() + "/" + juego.getIcon();
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("No se encontró la imagen: " + ruta);
                return;
            }
            Image image = new Image(url.toExternalForm());

            // Crear un ImageView
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true); // Mantener proporciones
            imageView.setSmooth(true);

            // Enlazar tamaño al StackPane
            imageView.fitWidthProperty().bind(stackPaneGame.widthProperty());
            imageView.fitHeightProperty().bind(stackPaneGame.heightProperty());

            // Limpiar cualquier imagen anterior
            stackPaneGame.getChildren().clear();
            stackPaneGame.getChildren().add(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void mostrarLogoPersonaje(Personaje personaje) {
        System.out.println("Mostrando logo del personaje: " + personaje.getNombre());
        if (personaje == null || personaje.getIcon() == null) return;

        try {
            String ruta = "/logos/" + personaje.getJuego() + "/" + personaje.getIcon();
            java.net.URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("No se encontró la imagen: " + ruta);
                return;
            }
            Image image = new Image(url.toExternalForm());

        // Crear un ImageView
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false); // Mantener proporciones
        imageView.setSmooth(true);

        // Enlazar tamaño al StackPane
        imageView.fitWidthProperty().bind(stackPaneChar.widthProperty());
        imageView.fitHeightProperty().bind(stackPaneChar.heightProperty());

        Rectangle clip = new Rectangle();
        clip.arcWidthProperty().set(20);  // ancho del redondeo
        clip.arcHeightProperty().set(20); // alto del redondeo
        clip.widthProperty().bind(imageView.fitWidthProperty());
        clip.heightProperty().bind(imageView.fitHeightProperty());
        imageView.setClip(clip);

        // Limpiar cualquier imagen anterior
        stackPaneChar.getChildren().clear();
        stackPaneChar.getChildren().add(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mostrarNotas(Personaje personaje) {
        noteContainer.getChildren().clear();
        long idChar = personaje.getId();
        if (combo_char.getSelectionModel().isEmpty() || combo_game.getSelectionModel().isEmpty()) {
            return;
        }

        try {
            List<Nota> notas = noteRepository.findByPersonaje(personaje);

            for (Nota nota : notas) {
                // Aquí creas un HBox para UNA nota
                HBox notaBox = new HBox(10);
                notaBox.setPadding(new Insets(10, 10, 10, 30));
                notaBox.setStyle("-fx-background-color: #f0f0f0;");

                Label titulo = new Label(nota.getTitulo());
                titulo.setWrapText(true);
                titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                titulo.setPrefWidth(200);

                Label contenido = new Label(nota.getContenido());
                contenido.setWrapText(false);
                contenido.setPrefWidth(400);

                Button btnExpand = new Button("Expand");
                btnExpand.setOnAction(e -> {
                    // Aquí defines qué pasa al pulsar expand, por ejemplo mostrar todo el contenido si estaba cortado
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Note.fxml"));
                    fxmlLoader.setControllerFactory(applicationContext::getBean);
                    Parent root = null;
                    try {
                        root = fxmlLoader.load();
                        ControllerNote controller = fxmlLoader.getController();
                        controller.setNote(nota.getTitulo(), nota.getContenido(), nota.getId());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Nota");
                    stage.initModality(Modality.APPLICATION_MODAL); // Hace la ventana modal
                    stage.showAndWait();
                    System.out.println("Expandiendo nota: " + nota.getId());
                    mostrarNotas(personaje);
                });
                Button btnDelete = new Button("Delete");
                btnDelete.setOnAction(e -> {
                    ControllerWarning.showWarning("Are you sure you want to delete this note?\n" + nota.getTitulo());
                    if (ControllerWarning.response) {
                        NoteService noteService = applicationContext.getBean(NoteService.class);
                        noteService.deleteNote(nota.getId());
                        mostrarNotas(personaje);
                    }
                });

                notaBox.getChildren().addAll(titulo, contenido, btnExpand, btnDelete);

                // Añadir al VBox principal
                noteContainer.getChildren().add(notaBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Aquí puedes implementar la lógica para mostrar las notas del personaje
        System.out.println("Mostrando notas para el personaje: " + personaje.getNombre() + " Id: " + idChar);
    }
}
