package assignment5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
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


public class Main extends Application {
	
	public static Pane root = new Pane();
	public static Text stepCountText = null;
	public static ObservableList<String> critterSizes = FXCollections.observableArrayList();
	public static ComboBox<String> selectCritterSize = null;
	public static int critterWidth = 8;							//Represents the size of the critter shape (must be a multiple of 8)
	public static int critterHeight = 8;						//Represents the size of the critter shape (must be a multiple of 8)
	public static boolean worldFlag = true;				//Checks if proper nodes are created by displayWorld

	public static void main(String[] args) {
		//Launch controller
		
		//TODO: write controller code
		
		//End controller code
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
		int btnHeight = 30;
		int btnWidth = 150;
		
		ObservableList<String> crittersAvailable = FXCollections.observableArrayList();
		String path = System.getProperty("user.dir") + "\\src\\assignment5"; // on linux /src/assignment5
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

			String className = new String();
			//TODO: make this reflection work with all files (headers)
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()){
		    	className = listOfFiles[i].getName();
		    	Class<?> testClass = Class.forName("assignment5." + className.substring(0, className.length() - 5));
		    	//assert(false);
		    	
		    	if(Modifier.isAbstract(testClass.getModifiers()) || className.equals("InvalidCritterException.java")){
		    		continue;
		    	}

				Object newCrit = testClass.newInstance();
				if(newCrit instanceof Critter){
					crittersAvailable.add(listOfFiles[i].getName().substring(0, className.length() - 5));
				}
		      }
		    }
		    final ComboBox<String> selectCritter = new ComboBox<>(crittersAvailable);
		    selectCritter.setEditable(true);
			selectCritter.relocate(275, 250);
			root.getChildren().add(selectCritter);
			selectCritter.setValue("Craig");
			
			updateResolutions();
			
		Text titleText = new Text(25, 25, "Project 5 Critters 2\nRegan Stehle and Matthew Edwards");
		titleText.setFont(new Font(25));
		root.getChildren().add(titleText);
		
		Text makeCritterText = new Text(50, 245, "Enter number of critters");
		makeCritterText.setFont(new Font(15));
		root.getChildren().add(makeCritterText);
		
		Text selectCritterText = new Text(275, 245, "Select critter to make or run stats");
		selectCritterText.setFont(new Font(15));
		root.getChildren().add(selectCritterText);
		
		Text stepText = new Text(50, 395, "Enter number of steps");
		stepText.setFont(new Font(15));
		root.getChildren().add(stepText);
		
		stepCountText = new Text(50, 500, "Steps since start: 0");
		stepCountText.setFont(new Font(15));
		root.getChildren().add(stepCountText);
		
		Text seedText = new Text(50, 95, "Enter seed");
		stepText.setFont(new Font(15));
		root.getChildren().add(seedText);
		
	    ByteArrayOutputStream grabStats = new ByteArrayOutputStream();
		PrintStream statsOut = new PrintStream(grabStats);
		System.setOut(statsOut);
	    Text statsText = new Text();
	    statsText.relocate(275, 325);
	    stepText.setFont(new Font(15));
		root.getChildren().add(statsText);
		statsText.setWrappingWidth(200);
		
		Text makeErrorSeed = new Text(50, 180, "Invalid number");
		makeErrorSeed.setFont(new Font(15));
		makeErrorSeed.setFill(Color.RED);
		
		Text makeErrorMake = new Text(50, 330, "Invalid number");
		makeErrorMake.setFont(new Font(15));
		makeErrorMake.setFill(Color.RED);
		
		Text makeErrorStep = new Text(50, 480, "Invalid number");
		makeErrorStep.setFont(new Font(15));
		makeErrorStep.setFill(Color.RED);
		
		TextField numCritters = new TextField();
		root.getChildren().add(numCritters);
		numCritters.relocate(50, 250);
		
		TextField numTimeSteps = new TextField();
		root.getChildren().add(numTimeSteps);
		numTimeSteps.relocate(50, 400);
		
		TextField seedField = new TextField();
		root.getChildren().add(seedField);
		seedField.relocate(50, 100);
		
		Button makeCritterBtn = new Button();
		makeCritterBtn.relocate(50, 285);
		makeCritterBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(makeCritterBtn);
		makeCritterBtn.setText("Make Critter");			
		makeCritterBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	    	 public void handle(ActionEvent event) {
	    		try {
	    			int numToCreate = 1;
	    			String textBox = numCritters.getText();
	    			if(!textBox.isEmpty()){
		    			if(!checkIfInt(textBox.trim(), 0)){
		    				root.getChildren().add(makeErrorMake);
		    				return;
		    			}else{
		    				root.getChildren().remove(makeErrorMake);
		    				numToCreate = Integer.parseInt(textBox.trim());
		    				if(numToCreate == 0){
		    					numToCreate = 1;
		    				}
		    			}
	    			}
	    			for(int i = 0; i < numToCreate; i++)
	    				Critter.makeCritter(selectCritter.getValue());
	    			Critter.displayWorld();
				} catch (InvalidCritterException e) {
				}
	        }
		});    
		
		Button changeResolution = new Button();
		changeResolution.relocate(50, 550);
		changeResolution.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(changeResolution);
		changeResolution.setText("Change Critter Size");
		changeResolution.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	        public void handle(ActionEvent event) {    
    			int newRes = Integer.parseInt(selectCritterSize.getValue());
    			critterWidth = newRes;
    			critterHeight = newRes;
    			worldFlag = true;
    			Critter.displayWorld();
	    	}
		});
		
		Button quitBtn = new Button();
		quitBtn.relocate(50, 900);
		quitBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(quitBtn);
		quitBtn.setText("Quit");			
		quitBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	        public void handle(ActionEvent event) {    
	    		System.exit(1);
	    	}
		});

		Button timeStepBtn = new Button();
		timeStepBtn.relocate(50, 435);
		timeStepBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(timeStepBtn);
		timeStepBtn.setText("Do Time Step(s)");			
		timeStepBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	        public void handle(ActionEvent event) {
	    			int numToStep = 1;
	    			String textBox2 = numTimeSteps.getText();
	    			if(!textBox2.isEmpty()){
	    				if(!checkIfInt(textBox2.trim(), 0)){
		    				root.getChildren().add(makeErrorStep);
	    					return;
	    				}else{
		    				root.getChildren().remove(makeErrorStep);
	    					numToStep = Integer.parseInt(textBox2.trim());
	    					if(numToStep == 0){
	    						numToStep = 1;
	    					}
	    				}
	    			}
	    			for(int i = 0; i < numToStep; i++)
	    				Critter.worldTimeStep();
	    			Critter.displayWorld();
	        }
		});   
		
		Button displayBtn = new Button();
		displayBtn.relocate(275, 135);
		displayBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(displayBtn);
		displayBtn.setText("Display World");
		displayBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	        public void handle(ActionEvent event) {
	    		Critter.displayWorld();
	        }
		});    
		
		Button seedBtn = new Button();
		seedBtn.relocate(50, 135);
		seedBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(seedBtn);
		seedBtn.setText("Set Seed");
		seedBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	        public void handle(ActionEvent event) {
	    		String seed = seedField.getText();
	    		if(!checkIfInt(seed.trim(), 0)){ // checks if the second part of the current string is solely an integer
    				root.getChildren().add(makeErrorSeed);
	        		System.out.println("error processing: " + seed);
				}
	    		else{
    				root.getChildren().remove(makeErrorSeed);
        			Critter.setSeed(Integer.parseInt(seed.substring(0, seed.length())));
	    		}

	        }
		});    
		
		Button statsBtn = new Button();
		statsBtn.relocate(275, 285);
		statsBtn.setMinSize(btnWidth, btnHeight);
		root.getChildren().add(statsBtn);
		statsBtn.setText("Display Stats");
		statsBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	@Override
	    	 public void handle(ActionEvent event) {
	    		try {
					Class<?> critter = Class.forName("assignment5." + selectCritter.getValue());
					Method statsm = critter.getMethod("runStats", List.class);
					statsm.invoke(critter, Critter.getInstances(selectCritter.getValue()));
					statsText.setText("");
					String nextStatsLine = grabStats.toString();
					statsText.setText("Stats:\n" + nextStatsLine);
					grabStats.reset();

				} catch (InvalidCritterException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException  e) {}
	        }
		});    
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setScene(new Scene(root, primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight()));
		primaryStage.show();		
	}

	public static void updateResolutions(){

		int resolution = 8;
		critterSizes.clear();
		while((resolution+8)*Params.world_height < 8000 && (resolution+8)*Params.world_width < 8000 && resolution <= 64){
			critterSizes.add(String.valueOf(resolution));
			resolution += 8;
		}

		selectCritterSize = new ComboBox<>(critterSizes);
		selectCritterSize.setEditable(true);
		root.getChildren().add(selectCritterSize);
		selectCritterSize.relocate(275, 550);
		selectCritterSize.setValue("8");
	}
	
	public static boolean checkIfInt(String input, int index){
		if(index >= input.length()){
			return false;
		}
		charLoop:
			for(int i = index; i < input.length(); i++){
				for(char k = '0'; k <= '9'; k++){
	        		if(input.charAt(i) == k){
	        			continue charLoop;
	        		}     	
				}
				return false;
			}
			return true;
	}
    
}
