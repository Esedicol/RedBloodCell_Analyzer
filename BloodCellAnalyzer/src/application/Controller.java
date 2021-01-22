package application;

/**
 * Controller Class
 *
 * @author Emmanuel Sedicol
 *
 */

import java.io.File;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Controller {
	/**
	 * FXML imports
	 */
	@FXML
	private ImageView imageView1;
	@FXML
	private ImageView imageView2;

	@FXML
	private Slider slider3;
	@FXML
	private Text display1;
	@FXML
	private Text display2;

	// File Chooser
	public void openImage(MouseEvent mouseEvent) {
		FileChooser fc = new FileChooser();
		File file = fc.showOpenDialog(null);
		openFile(file);
	}

	// Open image selected on application
	public void openFile(File file) {
		try {
			Image image = new Image(file.toURI().toString());
			imageView1.setImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// converts pixels to (red, white and purple)
	public void processImage(MouseEvent mouseEvent) {
		Color newColor;
		Image img = imageView1.getImage();

		WritableImage wImage = new WritableImage((int) img.getWidth(), (int) img.getHeight());
		PixelWriter pixelWriter = wImage.getPixelWriter();
		PixelReader pixelReader = img.getPixelReader();

		Double width = img.getWidth();
		Double height = img.getHeight();

		// initiate map size 
		Node[][] pixelMap = new Node[width.intValue()][height.intValue()];

		int idCount = 0;

		// iterate through columns and rows of pixels
		for (int xPos = 0; xPos < img.getWidth(); xPos++) {
			for (int yPos = 0; yPos < img.getHeight(); yPos++) {
				// read colour value of every pixel
				Color color = pixelReader.getColor(xPos, yPos);
				double hue = color.getHue();
				double b = color.getBlue() * 255;
				double g = color.getGreen() * 255;
				double r = color.getRed() * 255;

				// if in range of purple
				if ((hue >= 250 && hue <= 285)) {
					// assign new colour to pixel
					newColor = Color.PURPLE;
					pixelMap[xPos][yPos] = new Node(1,idCount);
				} else if(r < 170 && g < 170 && b < 170) {
					newColor = Color.WHITE;
					pixelMap[xPos][yPos] = null;
				} else {
					newColor = Color.RED;
					pixelMap[xPos][yPos] = new Node(2,idCount);
				}
				idCount +=1;
				pixelWriter.setColor(xPos, yPos, newColor);
			}
		}
		imageView2.setImage(wImage);
		unionFind(pixelMap);
	}

	/**
	 * Union function that creates disjointed sets
	 * @param valueMap
	 */
	public void unionFind(Node[][] valueMap){
		Image img = imageView2.getImage();
		Double width = img.getWidth();
		Double height = img.getHeight();
		Node OutOfBounds = null;
		
		for(int i = 0;i < width; i++){
			for(int j = 0; j < height; j++) {
				Node pixel = valueMap[i][j];
				if (pixel != null) {
					Node pixelDown, pixelRight;
					int count = 1;
					try {
						pixelDown = valueMap[i][j + count];
					} catch (Exception e) {
						pixelDown = OutOfBounds;
					}
					try {
						pixelRight = valueMap[i + count][j];
					} catch (Exception e) {
						pixelRight = OutOfBounds;
					}
					if (pixelDown != null) {
						union(pixel, pixelDown);
					}
					if (pixelRight != null) {
						union(pixel, pixelRight);
					}
				}
			}
		}
		valueMap = rootCompression(valueMap);
		valueMap = rootCheck(valueMap);
		count(valueMap);
	}

	/**
	 * Method used to union two pixels
	 * @param parent
	 * @param child
	 */
	public void union(Node parent, Node child){
		if(parent.getParent()==null) {
			parent.setRoot(true);
			parent.setParent(parent);
		}
		if(child.getParent() == null)
			child.setParent(parent);
		else
			find(parent).setParent(child);
	}
	/**
	 * Method to compress roots
	 * @param valueMap
	 * @return Compressed ValueMap
	 */
	public Node[][] rootCompression(Node[][] valueMap){
		Image img = imageView2.getImage();
		Double width = img.getWidth();
		Double height = img.getHeight();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(valueMap[i][j] !=null){
					Node child = valueMap[i][j];
					if(child.getParent() != null){
						Node root = find(child);
						child.setParent(root);
					}
				}
			}
		}
		return valueMap;
	}

	/**
	 * Method to check that pixels are linked directly to roots
	 * @param valueMap
	 * @return
	 */
	public Node[][] rootCheck(Node[][] valueMap){
		Image img = imageView2.getImage();
		Double width = img.getWidth();
		Double height = img.getHeight();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(valueMap[i][j]!=null){
					Node pixel = valueMap[i][j];
					if(pixel.isRoot()){
						if(!pixel.getParent().equals(pixel)){
							pixel.setParent(find(pixel.getParent()));
							pixel.setRoot(false);
						}
					}
				}
			}
		}
		return valueMap;
	}
	/**
	 * Method that takes in Node and finds the parent node
	 * @param child
	 * @return Parent of the child node
	 */
	public Node find(Node child){
		boolean found = false;
		Node parent = null;
		while(!found){
			parent = child.getParent();
			if(parent.isRoot() || parent == child){
				found = true;
			}
			else {
				child = child.getParent();
			}
		}
		return parent;
	}

	/**
	 * Method that goes through disjointed sets and counts number of pixels
	 * @param valueMap
	 */
	public void count(Node[][] valueMap) {
		Image img = imageView2.getImage();
		Double width = img.getWidth();
		Double height = img.getHeight();
		
		ArrayList<Node> sheepRoots = findRoots(valueMap);
		Integer[] sheep = new Integer[sheepRoots.size()];
		for(int m = 0; m <sheep.length; m+=1){
			sheep[m] = 0;
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (valueMap[i][j] != null) {
					Node pixel = valueMap[i][j];
					if (pixel.getParent() != null && !pixel.isRoot()) {
						for (int k = 0; k < sheepRoots.size(); k += 1) {
							if (pixel.getParent().equals(sheepRoots.get(k))) {
								sheep[k] += 1;
							}
						}
					}
				}
			}
		}
		processing(sheep, valueMap);
	}

	/**
	 * Method that goes through pixels and finds the roots
	 * @param valueMap
	 * @return A arraylist of all Roots
	 */
	public ArrayList<Node> findRoots(Node[][] valueMap){
		Image img = imageView2.getImage();
		Double width = img.getWidth();
		Double height = img.getHeight();
		
		ArrayList<Node> sheep = new ArrayList<Node>();
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (valueMap[i][j] != null) {
					if (valueMap[i][j].isRoot()) {
						sheep.add(valueMap[i][j]);
					}
				}
			}
		}
		return sheep;
	}

	/**
	 * Method that processes data from picture and
	 * refines the data to give number of sheep
	 * and cluters
	 * @param sheep
	 */
	public void processing(Integer[] sheep, Node[][] valueMap){
		Double threshold = slider3.getValue();
		ArrayList<Integer> refindedSheep = new ArrayList<>();
		for(int i = 0; i < sheep.length; i+=1){
			if(sheep[i] > threshold.intValue()){
				refindedSheep.add(sheep[i]);
			}
		}
		double avg;
		double sum = 0.0;
		double size =  refindedSheep.size();
		for(int i = 0; i < size; i+=1){
			sum += refindedSheep.get(i);
		}
		avg = sum/size;
		Double sqDiff = 0.0;
		for(int i =0; i <size; i+=1){
			Double differnce =  Math.pow((sheep[i] - avg),2);
			sqDiff = sqDiff + differnce;
		}
		Double variance = (1/size)*(sqDiff);
		Double standardDiv = Math.sqrt(variance);
		int sheepCount=0;
		double rangePlus = avg + (standardDiv);
		double rangeMinus = avg - (standardDiv);
		ArrayList<Integer> clusters = new ArrayList<>();
		for(int i = 0; i < refindedSheep.size(); i +=1){
			if( refindedSheep.get(i) >= rangeMinus &&  refindedSheep.get(i) <= rangePlus){
				sheepCount +=1;
			}
			if(refindedSheep.get(i) > rangePlus){
				Double cluster = refindedSheep.get(i)/avg;
				clusters.add(cluster.intValue());
				sheepCount += cluster.intValue();
			}
		}
		for(int i = 0; i < refindedSheep.size(); i+=1){
			System.out.println(refindedSheep.get(i));
		}
		display1.setText("Sheep: "+sheepCount);
		display2.setText("Clusters: " + clusters.size());
	}


	/**
	 * Method used to print 2D array used to analysis
	 * @param valueMap 2D array
	 * @param width width of array
	 * @param height height of array
	 */
	public void print2DArray(Node [][] valueMap, int width, int height){
		valueMap = rootCompression(valueMap);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if(valueMap[i][j]!=null){
					System.out.println(valueMap[i][j] + ":" + valueMap[i][j].getParent());
				}
			}
		}
	}
}





