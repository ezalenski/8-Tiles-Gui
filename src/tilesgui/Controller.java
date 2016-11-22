package tilesgui;

import javafx.animation.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import java.net.URL;
import java.util.*;

/**
 * Controller class that controls all of the JavaFX objects and interaction with user.
 */
public class Controller implements Initializable {
    /**
     * State Enums
     */
    private enum gameTypes {RANDOMIZE_BOARD, CHOOSE_BOARD};
    private enum stateType {NO_GAME, BOARD_SELECTION, GAMEPLAY, SELF_SOLVE}
    private stateType currentState;
    private gameTypes gameType;

    /**
     * Game Objects
     */
    private Board gameBoard;
    private char[] chosenBoard = new char[9];
    private SearchTree decisionTree;

    /**
     * Gui Helper Vars
     */
    private int nextFill;
    private int turnNumber;
    private Timeline solver;
    private Rectangle buttonSquare;
    private HashMap<Button, Integer> buttonIndex;

    /**
     * FXML Node Objects
     */
    @FXML // fx:id="turnNumberLabel"
    private Label turnNumberLabel;
    @FXML // fx:id="gameMode"
    private ChoiceBox<String> gameMode;
    @FXML // fx:id="gameStart"
    private Button gameStart, exitButton;
    @FXML // fx:id="buttonList"
    private List<Button> buttonList;
    @FXML // fx:id="autoSolve"
    private ToggleButton autoSolve;

    /**
     * Event Handlers
     */
    private EventHandler<ActionEvent> solverHandler = new EventHandler<ActionEvent>() {
        /**
         * Handles solve Timeline event.
         * @param event
         */
        @Override
        public void handle(ActionEvent event) {
            if (currentState == stateType.SELF_SOLVE) {
                if (decisionTree.hasNextMove()) {
                    int move = decisionTree.nextMove();
                    gameBoard.move(move);
                    turnNumber++;
                    turnNumberLabel.setText(String.format("Turn Number: %d", turnNumber));
                    chosenBoard = gameBoard.toString().toCharArray();
                    Controller.this.updateButtons();
                } else if (!gameBoard.isSolved()) {
                    currentState = stateType.NO_GAME;
                    Controller.this.alertUnsolvable();
                } else {
                    currentState = stateType.NO_GAME;
                    Controller.this.alertSolved();
                }
            }
        }
    };

    private EventHandler<MouseEvent> autoSolveHandler = new EventHandler<MouseEvent>() {
        /**
         * Handles AutoSolve button click event.
         * @param event
         */
        @Override
        public void handle(MouseEvent event) {
            if (currentState == stateType.GAMEPLAY) {
                currentState = stateType.SELF_SOLVE;
                SearchTreeNode initialState = new SearchTreeNode(gameBoard.toString(), gameBoard.getValue());
                decisionTree = new SearchTree(initialState);
                decisionTree.solve();
                turnNumber = 0;
                turnNumberLabel.setText(String.format("Turn Number: %d", turnNumber));
                solver.play();
            } else if (currentState == stateType.SELF_SOLVE) {
                currentState = stateType.GAMEPLAY;
                solver.stop();
            } else {
                autoSolve.setSelected(false);
            }
        }
    };

    private ChangeListener<Number> gameModeListener = new ChangeListener<Number>() {
        /**
         * Listens to when the selection for board type changes and updates state accordingly.
         * @param observable
         * @param oldValue
         * @param newValue
         */
        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            if (newValue.intValue() == gameTypes.CHOOSE_BOARD.ordinal()) {
                gameType = gameTypes.CHOOSE_BOARD;
            } else {
                gameType = gameTypes.RANDOMIZE_BOARD;
            }
        }
    };

    private EventHandler<MouseEvent> gameStartHandler = new EventHandler<MouseEvent>() {
        /**
         * Handles mouse click event for the Start Game button
         * @param event
         */
        @Override
        public void handle(MouseEvent event) {
            if(currentState != stateType.SELF_SOLVE) {
                turnNumber = 0;
                turnNumberLabel.setText(String.format("Turn Number: %d", turnNumber));
                if (gameType == gameTypes.CHOOSE_BOARD) {
                    currentState = stateType.BOARD_SELECTION;
                    Controller.this.clearButtons();
                    nextFill = 0;
                } else {
                    Controller.this.setupGame();
                }
            }
        }
    };

    private EventHandler<MouseEvent> exitButtonHandler = new EventHandler<MouseEvent>() {
        /**
         * Handles mouse click event for the exit button.
         * @param event
         */
        @Override
        public void handle(MouseEvent event) {
            System.exit(0);
        }
    };

    private EventHandler<MouseEvent> tileButtonHandler = new EventHandler<MouseEvent>() {
        /**
         * handles the mouse click event for all of the tile buttons
         * @param event
         */
        @Override
        public void handle(MouseEvent event) {
            Button clicked = (Button) event.getSource();
            if (currentState == stateType.BOARD_SELECTION) {
                if (clicked.getText().isEmpty()) {
                    Integer nextFill = Controller.this.nextFill;
                    if (nextFill == 0) {
                        clicked.setVisible(false);
                        clicked.setText(" ");
                    } else {
                        clicked.setText(nextFill.toString());
                    }
                    chosenBoard[buttonIndex.get(clicked)] = nextFill.toString().charAt(0);
                    Controller.this.nextFill++;
                    if (nextFill == 8) {
                        Controller.this.setupGame(new String(Controller.this.chosenBoard));
                    }
                }
            } else if (currentState == stateType.GAMEPLAY) {
                int move = Character.getNumericValue(chosenBoard[buttonIndex.get(clicked)]);
                if (gameBoard.move(move)) {
                    turnNumber++;
                    turnNumberLabel.setText(String.format("Turn Number: %d", turnNumber));
                    chosenBoard = gameBoard.toString().toCharArray();
                    Controller.this.updateButtons();
                }
            }
        }
    };

    /**
     * Initializes the controller and handles setting up injected FXML objects
     * @param fxmlFileLocation
     * @param resources
     */
    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert gameStart != null : "fx:id=\"gameStart\" was not injected: check your FXML file '8tilesgui.fxml'.";
        assert gameMode != null : "fx:id=\"gameMode\" was not injected: check your FXML file '8tilesgui.fxml'.";

        gameMode.getItems().addAll("Randomize Board", "Choose Board");
        gameMode.getSelectionModel().select(0);

        turnNumberLabel.setContentDisplay(ContentDisplay.RIGHT);

        initializeButtons();
        attachHandlers();

        currentState = stateType.NO_GAME;
    }

    /**
     * attaches event handlers to respective FXML objects
     */
    private void attachHandlers() {
        gameMode.getSelectionModel().selectedIndexProperty().addListener(gameModeListener);

        gameStart.addEventHandler(MouseEvent.MOUSE_CLICKED, gameStartHandler);
        autoSolve.addEventHandler(MouseEvent.MOUSE_CLICKED, autoSolveHandler);
        exitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, exitButtonHandler);

        for(Button tileButton : buttonList) {
            tileButton.addEventHandler(MouseEvent.MOUSE_CLICKED, tileButtonHandler);
        }

        solver = new Timeline(new KeyFrame(Duration.millis(300), solverHandler));
        solver.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Initializes the tile buttons.
     */
    private void initializeButtons() {
        buttonIndex = new HashMap<>();
        for(int i = 0; i < buttonList.size(); i++) {
            buttonIndex.put(buttonList.get(i), i);
        }
    }

    /**
     * Resets tile buttons.
     */
    private void clearButtons() {
        for(Button tileButton : buttonList) {
            tileButton.setVisible(true);
            tileButton.setText("");
        }
    }

    /**
     * Setups up randomized board and starts game.
     */
    private void setupGame() {
        clearButtons();
        currentState = stateType.GAMEPLAY;
        gameBoard = new Board(3);
        chosenBoard = gameBoard.toString().toCharArray();
        updateButtons();
    }

    /**
     * setups up configured board and starts game.
     * @param configuration
     */
    private void setupGame(String configuration) {
        currentState = stateType.GAMEPLAY;
        gameBoard = new Board(configuration);
        chosenBoard = gameBoard.toString().toCharArray();
        updateButtons();
    }

    /**
     *  Updates gui values based off current board and triggers animation/end game message if game is on-going/finished
     */
    private void updateButtons() {
        Button sourceButton = null;
        Button targetButton = null;
        for(int i = 0; i < buttonList.size(); i++) {
            Button tileButton = buttonList.get(i);
            if(chosenBoard[i] == '0') {
                sourceButton = tileButton;
                tileButton.setVisible(false);
            } else if(!tileButton.isVisible()) {
                targetButton = tileButton;
            }
            String text = "";
            text += chosenBoard[i];
            tileButton.setText(text);
        }
        if(turnNumber > 0) {
            PathTransition animationTrans = createAnimation(sourceButton, targetButton, 225);
            if (animationTrans != null) {
                sourceButton.getParent().requestFocus();
                animationTrans.play();
            }
        }
        if(gameBoard.isSolved()) {
            currentState = stateType.NO_GAME;
            alertSolved();
        }
    }

    /**
     * Congratulates user on successfully completing the puzzle.
     */
    private void alertSolved() {
        solver.stop();
        autoSolve.setSelected(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setResizable(false);
        alert.setTitle("Victory!");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations, you won!!!");
        alert.show();
    }

    /**
     * Alerts user that board is unsolvable and ends the game.
     */
    private void alertUnsolvable() {
        solver.stop();
        autoSolve.setSelected(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setResizable(false);
        alert.setTitle("Unsolvable!");
        alert.setHeaderText(null);
        alert.setContentText("Unfortunately, this board configuration is unsolvable. :(");
        alert.show();
    }

    /**
     * Creates animation for moving a rectangle button mimic that moves from sourceButton to targetButton in duration
     * milliseconds time.
     * @param sourceButton
     * @param targetButton
     * @param duration
     * @return
     */
    private PathTransition createAnimation(Button sourceButton, Button targetButton, double duration) {
        if(sourceButton == null || targetButton == null) {
            return null;
        }
        PathTransition ret = new PathTransition();

        ArrayList<Double> beginPoint = getAbsPosition(sourceButton);
        double beginX = beginPoint.get(0);
        double beginY = beginPoint.get(1);

        ArrayList<Double> endPoint = getAbsPosition(targetButton);
        double endX = endPoint.get(0);
        double endY = endPoint.get(1);

        Rectangle animationRect = setupAnimationRect(sourceButton);

        Path path = new Path();
        path.getElements().add(new MoveTo(beginX, beginY));
        path.getElements().add(new LineTo(endX, endY));

        ret.setPath(path);
        ret.setNode(animationRect);
        ret.setDuration(Duration.millis(duration));
        ret.setOnFinished(new EventHandler<ActionEvent>() {
            Rectangle rect = animationRect;
            Button target = targetButton;

            /**
             * Handles animation cleanup.
             * @param event
             */
            @Override
            public void handle(ActionEvent event) {
                if(target != null) {
                    rect.setVisible(false);
                    target.setVisible(true);
                } else {
                    System.out.println("TARGET NULL");
                }
            }
        });
        return ret;
    }

    /**
     * Sets up the rectangle used as button look-alike in animation.
     * @param button
     * @return
     */
    private Rectangle setupAnimationRect(Button button) {
        Rectangle animationRect = (Rectangle)button.getScene().lookup("#animationRect");
        animationRect.setVisible(true);
        animationRect.setArcHeight(5);
        animationRect.setArcWidth(5);
        animationRect.setWidth(button.getWidth());
        animationRect.setHeight(button.getHeight());
        animationRect.relocate(50, 50);
        return animationRect;
    }

    /**
     * Gets the abs position of a button.
     * @param button
     * @return
     */
    private ArrayList<Double> getAbsPosition(Button button) {
        ArrayList<Double> ret = new ArrayList<>();
        Bounds parentBounds = button.getParent().getBoundsInParent();
        Bounds bound = button.getBoundsInParent();
        ret.add(bound.getMinX() + parentBounds.getMinX());
        ret.add(bound.getMinY() + parentBounds.getMinY());
        return ret;
    }
}
