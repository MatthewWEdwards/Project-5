
package assignment5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.*;
import javafx.scene.text.Font;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.animation.AnimationTimer;

public class Main extends Application {

	public static int critterWidth = 8; // Represents the size of the critter
										// shape (must be a multiple of 8)
	public static int critterHeight = 8;
	public static boolean worldFlag = true;
	public static boolean updateFlag = false;
	public static Pane root = new Pane();
	public static Text stepCountText = null;
	public static boolean go = false;
	public static ObservableList<String> critterSizes = FXCollections.observableArrayList();
	public static ComboBox<String> selectCritterSize = null;
	Timer timer = new Timer();
	public static int animationFrame = 1;
	public static double screenSizeHeight = Screen.getPrimary().getVisualBounds().getHeight();
	public static double screenSizeWidth = Screen.getPrimary().getVisualBounds().getWidth();
	public static ComboBox<String> mySelectCritter;
	public static Text myStatsText; 
	public static ByteArrayOutputStream myGrabStats;
	
	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		int btnHeight = (int) (.05 * screenSizeHeight); // 30
		int btnWidth = (int) (.1 * screenSizeWidth);// 150;

		ObservableList<String> crittersAvailable = FXCollections.observableArrayList();
		String path = System.getProperty("user.dir") + "/src/assignment5"; // on
																			// linux
																			// /src/assignment5
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		String className = new String();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				className = listOfFiles[i].getName();
				Class<?> testClass = Class.forName("assignment5." + className.substring(0, className.length() - 5));
				if (Modifier.isAbstract(testClass.getModifiers()) || className.equals("InvalidCritterException.java")) {
					continue;
				}

				Object newCrit = testClass.newInstance();
				if (newCrit instanceof Critter) {
					crittersAvailable.add(listOfFiles[i].getName().substring(0, className.length() - 5));
				}
			}
		}
		final ComboBox<String> selectCritter = new ComboBox<>(crittersAvailable);
		selectCritter.setEditable(true);
		selectCritter.relocate((.17 * screenSizeWidth), (.33 * screenSizeHeight)); // 275,
																					// 250
		root.getChildren().add(selectCritter);
		selectCritter.setValue("Craig");

		updateResolutions();
		Text titleText = new Text((.02 * screenSizeWidth), (.04 * screenSizeHeight),
				"Project 5 Critters 2\nRegan Stehle and Matthew Edwards");// 25,
																			// 25
		titleText.setFont(new Font(25));
		root.getChildren().add(titleText);

		Text makeCritterText = new Text((.02 * screenSizeWidth), (.32 * screenSizeHeight), "Enter number of critters"); // 50,
																														// 245
		makeCritterText.setFont(new Font(15));
		root.getChildren().add(makeCritterText);

		Text selectCritterText = new Text((.17 * screenSizeWidth), (.32 * screenSizeHeight),
				"Select critter to make or run stats"); // 275, 245
		selectCritterText.setFont(new Font(15));
		root.getChildren().add(selectCritterText);

		Text animationSpeedText = new Text((.17 * screenSizeWidth), (.62 * screenSizeHeight),
				"Enter number of steps per frame");
		animationSpeedText.setFont(new Font(15));
		root.getChildren().add(animationSpeedText);

		Text stepText = new Text((.02 * screenSizeWidth), (.52 * screenSizeHeight), "Enter number of steps"); // 50,
																												// 395
		stepText.setFont(new Font(15));
		root.getChildren().add(stepText);

		stepCountText = new Text((.02 * screenSizeWidth), (.68 * screenSizeHeight), "Steps since start: 0"); // 50,
																												// 500
		stepCountText.setFont(new Font(15));
		root.getChildren().add(stepCountText);

		Text seedText = new Text((.02 * screenSizeWidth), (.12 * screenSizeHeight), "Enter seed"); // 50,
																									// 95
		stepText.setFont(new Font(15));
		root.getChildren().add(seedText);

		ByteArrayOutputStream grabStats = new ByteArrayOutputStream();
		PrintStream statsOut = new PrintStream(grabStats);
		System.setOut(statsOut);
		Text statsText = new Text();
		statsText.relocate((.17 * screenSizeWidth), (.46 * screenSizeHeight)); // 275,
																				// 375
		stepText.setFont(new Font(15));
		root.getChildren().add(statsText);
		statsText.setWrappingWidth((.12 * screenSizeWidth));

		Text makeErrorSeed = new Text((.02 * screenSizeWidth), (.26 * screenSizeHeight), "Invalid number"); // 50,
																											// 180
		makeErrorSeed.setFont(new Font(15));
		makeErrorSeed.setFill(Color.RED);

		Text makeErrorMake = new Text((.02 * screenSizeWidth), (.46 * screenSizeHeight), "Invalid number"); // 50,
																											// 330
		makeErrorMake.setFont(new Font(15));
		makeErrorMake.setFill(Color.RED);

		Text makeErrorStep = new Text((.02 * screenSizeWidth), (.66 * screenSizeHeight), "Invalid number"); // 50,
																											// 480
		makeErrorStep.setFont(new Font(15));
		makeErrorStep.setFill(Color.RED);

		Text makeErrorAnimation = new Text((.17 * screenSizeWidth), (.675 * screenSizeHeight), "Invalid number"); // 50,
																													// 480
		makeErrorAnimation.setFont(new Font(15));
		makeErrorAnimation.setFill(Color.RED);
		
		Text resolutionText = new Text((.02 * screenSizeWidth), (.74 * screenSizeHeight), "Select Critter Resolution");
		root.getChildren().add(resolutionText);

		TextField numCritters = new TextField();
		root.getChildren().add(numCritters);
		numCritters.relocate((.02 * screenSizeWidth), (.33 * screenSizeHeight)); // 50,
																					// 250

		TextField numTimeSteps = new TextField();
		root.getChildren().add(numTimeSteps);
		numTimeSteps.relocate((.02 * screenSizeWidth), (.53 * screenSizeHeight)); // 50,
																					// 400

		TextField seedField = new TextField();
		root.getChildren().add(seedField);
		seedField.relocate((.02 * screenSizeWidth), (.13 * screenSizeHeight)); // 50,
																				// 100

		TextField animationField = new TextField();
		root.getChildren().add(animationField);
		animationField.relocate((.17 * screenSizeWidth), (.63 * screenSizeHeight));

		Button makeCritterBtn = new Button();
		makeCritterBtn.relocate((.02 * screenSizeWidth), (.38 * screenSizeHeight)); // 50,
																					// 285
		makeCritterBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(makeCritterBtn);
		makeCritterBtn.setText("Make Critter");
		makeCritterBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					int numToCreate = 1;
					String textBox = numCritters.getText();
					if (!textBox.isEmpty()) {
						if (!checkIfInt(textBox.trim(), 0)) {
							root.getChildren().add(makeErrorMake);
							return;
						} else {
							root.getChildren().remove(makeErrorMake);
							numToCreate = Integer.parseInt(textBox.trim());
							if (numToCreate == 0) {
								numToCreate = 1;
							}
						}
					}
					for (int i = 0; i < numToCreate; i++)
						Critter.makeCritter(selectCritter.getValue());
					Critter.displayWorld();
					updateStats(selectCritter, statsText, grabStats);
				} catch (InvalidCritterException e) {
				}
			}
		});

		Button quitBtn = new Button();
		quitBtn.relocate((.02 * screenSizeWidth), (.9 * screenSizeHeight)); // 50,
																			// 900
		quitBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(quitBtn);
		quitBtn.setText("Quit");
		quitBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(1);
			}
		});

		Button changeResolution = new Button();
		changeResolution.relocate(.02 * screenSizeWidth, .8 * screenSizeHeight);
		changeResolution.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(changeResolution);
		changeResolution.setText("Change Critter Size");
		changeResolution.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int newRes = Integer.parseInt(selectCritterSize.getValue());
				critterWidth = newRes;
				critterHeight = newRes;
				updateFlag = true;
				updateResolutions();
				Critter.displayWorld();
			}
		});

		Button timeStepBtn = new Button();
		timeStepBtn.relocate((.02 * screenSizeWidth), (.58 * screenSizeHeight)); // 50,
																					// 435
		timeStepBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(timeStepBtn);
		timeStepBtn.setText("Do Time Step(s)");
		timeStepBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int numToStep = 1;
				String textBox2 = numTimeSteps.getText();
				if (!textBox2.isEmpty()) {
					if (!checkIfInt(textBox2.trim(), 0)) {
						root.getChildren().add(makeErrorStep);
						return;
					} else {
						root.getChildren().remove(makeErrorStep);
						numToStep = Integer.parseInt(textBox2.trim());
						if (numToStep == 0) {
							numToStep = 1;
						}
					}
				}
				for (int i = 0; i < numToStep; i++)
					Critter.worldTimeStep();
				Critter.displayWorld();
				updateStats(selectCritter, statsText, grabStats);
			}
		});


		Button displayBtn = new Button();
		displayBtn.relocate((.17 * screenSizeWidth), (.19 * screenSizeHeight)); // 275,
																				// 135
		displayBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(displayBtn);
		displayBtn.setText("Display World");
		displayBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Critter.displayWorld();
				updateStats(selectCritter, statsText, grabStats);
			}
		});

		Button seedBtn = new Button();
		seedBtn.relocate((.02 * screenSizeWidth), (.19 * screenSizeHeight)); // 50,
																				// 135
		seedBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(seedBtn);
		seedBtn.setText("Set Seed");
		seedBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String seed = seedField.getText();
				if (!checkIfInt(seed.trim(), 0)) { // checks if the second part
													// of the current string is
													// solely an integer
					root.getChildren().add(makeErrorSeed);
					System.out.println("error processing: " + seed);
				} else {
					root.getChildren().remove(makeErrorSeed);
					Critter.setSeed(Integer.parseInt(seed.substring(0, seed.length())));
				}

			}
		});

		Button statsBtn = new Button();
		statsBtn.relocate((.17 * screenSizeWidth), (.38 * screenSizeHeight)); // 275,
																				// 285
		statsBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(statsBtn);
		statsBtn.setText("Display Stats");
		statsBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				updateStats(selectCritter, statsText, grabStats);
			}
		});

		startAnimationBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int numToStep = 1;
				String textBox2 = animationField.getText();
				if (!textBox2.isEmpty()) {
					if (!checkIfInt(textBox2.trim(), 0)) {
						root.getChildren().add(makeErrorAnimation);
						return;
					} else {
						root.getChildren().remove(makeErrorAnimation);
						numToStep = Integer.parseInt(textBox2.trim());
						if (numToStep == 0) {
							numToStep = 1;
						}
					}
				}
				makeCritterBtn.setDisable(true);
				quitBtn.setDisable(true);
				timeStepBtn.setDisable(true);
				displayBtn.setDisable(true);
				changeResolution.setDisable(true);
				seedBtn.setDisable(true);
				statsBtn.setDisable(true);
				startAnimationBtn.setDisable(true);
				selectCritter.setDisable(true);
				numCritters.setDisable(true); 
				numTimeSteps.setDisable(true); 
				seedField.setDisable(true);  
				animationField.setDisable(true);
				selectCritterSize.setDisable(true);
				mySelectCritter = selectCritter;
				myStatsText = statsText; 
				myGrabStats = grabStats;
				animationFrame = numToStep;
				go = true;
				 AnimationTimer timer = new MyTimer();
			        timer.start();
			}
		});

		Button stopAnimationBtn = new Button();
		stopAnimationBtn.relocate((.17 * screenSizeWidth), (.77 * screenSizeHeight)); // 50,
																						// 900
		stopAnimationBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(stopAnimationBtn);
		stopAnimationBtn.setText("Stop Animation");
		stopAnimationBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				makeCritterBtn.setDisable(false);
				quitBtn.setDisable(false);
				timeStepBtn.setDisable(false);
				displayBtn.setDisable(false);
				changeResolution.setDisable(false);
				seedBtn.setDisable(false);
				statsBtn.setDisable(false);
				startAnimationBtn.setDisable(false);
				selectCritter.setDisable(false);
				numCritters.setDisable(false); 
				numTimeSteps.setDisable(false);
				seedField.setDisable(false);  
				animationField.setDisable(false);
				selectCritterSize.setDisable(false);
				go = false;
			}
		});
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()));
		primaryStage.show();

	}

	
	public static boolean checkIfInt(String input, int index) {
		if (index >= input.length()) {
			return false;
		}
		charLoop: for (int i = index; i < input.length(); i++) {
			for (char k = '0'; k <= '9'; k++) {
				if (input.charAt(i) == k) {
					continue charLoop;
				}
			}
			return false;
		}
		return true;
	}

	public static void updateStats(ComboBox<String> selectCritter, Text statsText, ByteArrayOutputStream grabStats) {

		try {
			Class<?> critter = Class.forName("assignment5." + selectCritter.getValue());
			Method statsm = critter.getMethod("runStats", List.class);
			statsm.invoke(critter, Critter.getInstances(selectCritter.getValue()));
			statsText.setText("");
			String nextStatsLine = grabStats.toString();
			statsText.setText("Stats:\n" + nextStatsLine);
			grabStats.reset();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InvalidCritterException
				| ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			return;
		}
	}

	public static void updateResolutions() {

		int resolution = 8;
		critterSizes.clear();
		while ((resolution + 8) * Params.world_height < 8000 && (resolution + 8) * Params.world_width < 8000
				&& resolution <= 64) {
			critterSizes.add(String.valueOf(resolution));
			resolution += 8;
		}

		selectCritterSize = new ComboBox<>(critterSizes);
		selectCritterSize.setEditable(true);
		root.getChildren().add(selectCritterSize);
		selectCritterSize.relocate(.02 * screenSizeWidth, .75 * screenSizeHeight);
		selectCritterSize.setValue("8");
	}
	
	private class MyTimer extends AnimationTimer {
		int numToStep = animationFrame;
		ComboBox<String> selectCritter = mySelectCritter;
		Text statsText = myStatsText; 
		ByteArrayOutputStream grabStats = myGrabStats;
		
		@Override
        public void handle(long now) {
        
            doHandle();
        }

        private void doHandle() {
        	if (go == false)
        		stop();
        	for (int i = 0; i < numToStep; i++)
				Critter.worldTimeStep();
			Critter.displayWorld();
			updateStats(selectCritter, statsText, grabStats);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				go = false;
			}
           
        }
    }
}
