package gameFiles;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	
	private boolean isLight = true;
	private Color lightColor = Color.valueOf("#ACE5DB");
	private Color lightColorDisc1 = Color.valueOf("#24303E");
	private Color lightColorDisc2 = Color.valueOf("#4CAA88");
	private Color darkColor = Color.valueOf("#32554F");
	private Color darkColorDisc1 = Color.BROWN;//#93F1E1
	private Color darkColorDisc2 = Color.DARKORCHID;//#50ADAD
	
	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private Color color1 = this.lightColorDisc1;
	private Color color2 = this.lightColorDisc2;
	
	
	private static String PLAYER_ONE = "Player 1";
	private static String PLAYER_TWO = "Player 2";
	
	private boolean isPlayerOneTurn = true;
	private boolean tieFlag = false;
	private Disc[][] insertedDicArray = new Disc[ROWS][COLUMNS];
	
	private boolean isAllowed = true;
	
	@FXML
	public GridPane rootGridPane = new GridPane();
	
	@FXML
	public Pane insertDiscPane;
	
	@FXML
	public Label playerName;
	
	@FXML
	public TextField playerOne;
	
	@FXML
	public  TextField playerTwo;
	
	@FXML
	public Button setButton;
	
	public void createPlayground() {
		
		Shape playground = createGameGrid();
		rootGridPane.add(playground, 0, 1);
		
		List<Rectangle> rectangleList = createClickableColumns();
		
		for (Rectangle rectangle: rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
	}
	
	private Shape createGameGrid() {
		
		Shape playground = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
		
		for (int row = 0; row < ROWS; row++) {
			
			for (int col = 0; col < COLUMNS; col++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				
				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setSmooth(true);
				
				playground = Shape.subtract(playground, circle);
			}
		}
		
		Color color = isLight ? lightColor : darkColor;
		playground.setFill(color);
		
		return playground;
	}
	
	private List<Rectangle> createClickableColumns() {
		
		List<Rectangle> columnList = new ArrayList<>();
		
		for (int col = 0; col < COLUMNS; col++) {
			
			Rectangle column = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			column.setFill(Color.TRANSPARENT);
			column.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
			
			column.setOnMouseEntered(event -> column.setFill(Color.valueOf("#eeeeee26")));
			column.setOnMouseExited(event -> column.setFill(Color.TRANSPARENT));
			
			final int tempCol = col;
			column.setOnMouseClicked(event -> {
				if(isAllowed) {
					isAllowed = false;
					insertDisc(new Disc(isPlayerOneTurn), tempCol);
				}
			});
			
			columnList.add(column);
		}
		
		return columnList;
	}
	
	private void insertDisc(Disc disc, int col) {
	
		int row = ROWS - 1;
		
		while (row >= 0){
			if (getIfPresent(row, col) == null)
				break;
			
			--row;
		}
		
		if(row < 0)
			return;
		
		insertedDicArray [row][col] = disc;
		insertDiscPane.getChildren().add(disc);
		
		disc.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		
		TranslateTransition fallAnimation = new TranslateTransition(Duration.seconds(0.65), disc);
		fallAnimation.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		final int tempRow = row;
		fallAnimation.setOnFinished(event -> {
			isAllowed = true;
			if (gameEnded (tempRow, col)){
					gameOver();
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerName.setText((isPlayerOneTurn)? PLAYER_ONE : PLAYER_TWO);
		});
		
		fallAnimation.play();
	}
	
	private void gameOver() {
		if (!tieFlag) {
			String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
			System.out.println("Game over with : Winner .... " + winner);
			Alert announceWinner = new Alert(Alert.AlertType.INFORMATION);
			announceWinner.setTitle("Connect Four");
			announceWinner.setHeaderText("The winner is " + winner);
			announceWinner.setContentText("Want to play again ?");
			
			ButtonType yesBtn = new ButtonType("Yes");
			ButtonType noBtn = new ButtonType("No. Exit");
			announceWinner.getButtonTypes().setAll(yesBtn, noBtn);
			
			Platform.runLater(() -> {
				
				Optional<ButtonType> btnClicked = announceWinner.showAndWait();
				
				if (btnClicked.isPresent() && btnClicked.get() == yesBtn) {
					resetGame();
				} else if (btnClicked.isPresent() && btnClicked.get() == noBtn) {
					Platform.exit();
					System.exit(0);
				}
				
			});
		}
		else {
			System.out.println("Game over with : Tie ....");
			Alert gameTie = new Alert(Alert.AlertType.INFORMATION);
			gameTie.setTitle("Connect Four");
			gameTie.setHeaderText("It's a tie !");
			gameTie.setContentText("Do you want to restart ?");
			gameTie.show();
			
			ButtonType yesBtn = new ButtonType("Yes");
			ButtonType noBtn = new ButtonType("No. Exit");
			gameTie.getButtonTypes().setAll(yesBtn, noBtn);
			
			
				Optional<ButtonType> btnClicked = gameTie.showAndWait();
				
				if (btnClicked.isPresent() && btnClicked.get() == yesBtn) {
					resetGame();
				}
				else if (btnClicked.isPresent() && btnClicked.get() == noBtn){
					Platform.exit();
					System.exit(0);
				}
		}
		
	}
	
	public void resetGame() {
		
		System.out.println("Resetting game.....");
		insertDiscPane.getChildren().clear();
		
		for (int row = 0; row < insertedDicArray.length; ++row){
			for (int col = 0; col < insertedDicArray[row].length; ++col)
				insertedDicArray[row][col] = null;
		}
		
		isPlayerOneTurn = true;
		playerName.setText(PLAYER_ONE);
		createPlayground();
	}
	
	private boolean gameEnded(int row, int col) {
		
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3).mapToObj(r -> new Point2D(r,col))
								    .collect(Collectors.toList());
		
		List<Point2D> horizontalPoints = IntStream.rangeClosed(col-3,col+3).mapToObj(c -> new Point2D(row,c))
				.collect(Collectors.toList());
		
		Point2D startPoint1 = new Point2D(row - 3, col + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint1.add(i,-i))
				.collect(Collectors.toList());
		
		Point2D startPoint2 = new Point2D(row - 3, col - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint2.add(i,i))
				.collect(Collectors.toList());
		
		boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints)
							|| checkCombination(diagonal1Points) || checkCombination(diagonal2Points);
		
		isEnded = (isEnded)? isEnded : !isMovesLeft();
		
		return isEnded;
	}
	
	private boolean isMovesLeft() {
		
		for (int r = 0; r < insertedDicArray.length; ++r){
			for (int c = 0; c < insertedDicArray[r].length; ++c)
				if (insertedDicArray[r][c] == null)
					return true;
					
			}
		tieFlag = true;
		return false;
	}
	
	private boolean checkCombination(List<Point2D> points) {
		
		int chain = 0;
		for (Point2D point : points){
			int rowIndex = (int) point.getX();
			int colIndex = (int) point.getY();
			
			Disc disc = getIfPresent(rowIndex,colIndex);
			
			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
				chain++;
				if (chain == 4)
					return true;
			}
			else
				chain = 0;
		}
		
		return false;
	}
	
	private Disc getIfPresent (int row, int col){
		if (row >= ROWS || col >= COLUMNS || row < 0 || col < 0)
			return null;
		return insertedDicArray[row][col];
	}
	
	private class Disc extends Circle {
		
		private final boolean isPlayerOneMove;
		
		public Disc(boolean isPlayerOneMove) {
			
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove? color1: color2);
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}
	
	public void setIsLight (boolean val){
		isLight = val;
	}
	
	public void setColorTheme (){
		if (isLight) {
			color1 = lightColorDisc1;
			color2 = lightColorDisc2;
		}
		else{
			color1 = darkColorDisc1;
			color2 = darkColorDisc2;
		}
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setButton.setOnAction(event -> {
			changeNames();
			playerName.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
		});
	}
	
	private void changeNames() {
		String player1Name = playerOne.getText();
		String player2Name = playerTwo.getText();
		PLAYER_ONE = player1Name;
		PLAYER_TWO = player2Name;
	}
	
	public void setNewGame(){
		PLAYER_ONE = "Player 1";
		PLAYER_TWO = "Player 2";
		playerOne.setText("Player 1");
		playerTwo.setText("Player 2");
		playerName.setText(PLAYER_ONE);
	}
}
