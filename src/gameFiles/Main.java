package gameFiles;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {
    
    
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameUI.fxml"));
        
        GridPane rootGridPane = loader.load();
        controller =(Controller) loader.getController();
        
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu() {

        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(event -> {
            if (showWarning("create new game. You'll lose all your progress.")) {
                resetGame();
                controller.setNewGame();
            }
        });

        MenuItem resetGame = new MenuItem("Reset game");
        resetGame.setOnAction(event -> {
            if (showWarning("restart the game."))
                resetGame();
        });

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit game");
        exitGame.setOnAction(event -> {
            if (showWarning("exit the game."))
                exitGame();
        });

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        // Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect4");
        aboutGame.setOnAction(event -> aboutConnect4());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separator, aboutMe);
        
        //Theme Menu
        Menu colorScheme = new Menu("Theme");
        MenuItem light = new MenuItem("Light");
        light.setOnAction(event -> {
            if (showWarning("reset the Game.")) {
                controller.setIsLight(true);
                controller.setColorTheme();
                resetGame();
            }
        });
        MenuItem dark = new MenuItem("Dark");
        dark.setOnAction(event -> {
            if (showWarning("reset the Game.")) {
                controller.setIsLight(false);
                controller.setColorTheme();
                resetGame();
            }
        });
        
        colorScheme.getItems().addAll(light, dark);
        
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, colorScheme, helpMenu);

        return menuBar;
    }

    private void aboutMe() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Sayan Paul");
        alert.setContentText("I am learning Java and it is a great Programming Language. " +
                "This Connect 4 game is made as a part of my Internshala Java training. " +
                "I will practice making more projects so that one day I can become a good Java developer.");

        alert.show();
    }

    private void aboutConnect4() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the " +
                "players first choose a color and then take turns dropping colored discs " +
                "from the top into a seven-column, six-row vertically suspended grid. "+
                "The pieces fall straight down, occupying the next available space within the column. "+
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.");

        alert.show();
    }

    private void exitGame() {

        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        controller.resetGame();
    }
    
    private boolean showWarning(String sentence){
        
        boolean ans = false;
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("This will " + sentence);
        
        alert.setContentText("Want to continue ?");
        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No");
        alert.getButtonTypes().setAll(yesBtn, noBtn);
        
        Optional<ButtonType> btnClicked = alert.showAndWait();
        
        if (btnClicked.isPresent() && btnClicked.get() == yesBtn)
            ans =  true;
        else if (btnClicked.isPresent() && btnClicked.get() == noBtn)
            ans = false;
        else if (!btnClicked.isPresent())
            ans = false;
        
        return ans;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
