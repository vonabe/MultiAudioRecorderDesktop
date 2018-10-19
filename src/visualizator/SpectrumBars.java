package visualizator;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * This class is designed to draw on a JavaFX canvas object based on
 * an array of magnitudes reflecting the volumes of the range of
 * frequencies in a song. It creates a series of vertical bars,
 * each made up of rectangles. The height of these bars corresponds
 * to the volume of the frequencies that bar represents.
 * 
 * @author Eden Doonan
 */
public class SpectrumBars extends Visualization {
	// In this visualization, a series of vertical bars, each made up of rectangles,
	// is drawn on a canvas. A color is stored for the top and bottom rectangles,
	// and a gradient is formed between them. At any given time, one of those colors
	// is shifting.
	
	// The maximum shit in R, G, or B for the color that's shifting.
	private final double COLOR_SHIFT_SPEED = 3;
	// The minimum vertical distance from the edge of any of the rectangles.
	private final int VERTICAL_PADDING = 6;
	// The minimum horizontal distance from the edge of any of the rectangles.
	private final int HORIZONTAL_PADDING = 8;
	// The amount of time that needs to have passed in seconds for
	// a color to be shifted.
	private final double COLOR_UPDATE_FREQUENCY = .1;
	
	// numBars - the number of vertical bars.
	// barHeight - the maximum number of rectangles in a bar.
	// rectangleWidth - the width of one rectangle (and therefore bar).
	// rectangleHeight - the height of one rectangle.
	// horizontalGap - the horizontal gap between each bar.
	// verticalGap - the vertical gap between each rectangle in a bar.
	// colorShiftIndex - keeps track of what the next color to be grabbed from colorShiftVals.
	private int numBars, barHeight, rectangleWidth, rectangleHeight, horizontalGap, verticalGap, colorShiftIndex;
	
	// The colors at the bottom and top of each bar, along with the background color.
	private Color bottomColor, topColor, backgroundColor;
	
	// The array of colors that either the top or bottom color is stepping through.
	private Color[] colorShiftVals;
	
	// Stores whether the bottom or top color is shifting.
	private boolean isbottomColorShifting;
	
	// When this is greater than or equal to COLOR_UPDATE_FREQUENCY,
	// we shift the top or bottom color. Regardless, we add the amount
	// of time that has passed since the last update to the
	// visualization.
	private double timeSinceColorUpdate;
	Canvas display;

	/**
	 * Initializes a SpectrumBars object with the values:
	 * maxVolume = 100;
	 * numBars = 8;
	 * barHeight = 12;
	 * rectangleWidth = 40;
	 * rectangleHeight = 15;
	 * horizontalGap = 7;
	 * verticalGap = 3;
	 */
	public SpectrumBars() {
		this(100,null);
	}
	
	/**
	 * Initializes a SpectrumBars object with the values:
	 * maxVolume = parameter volMax;
	 * numBars = 8;
	 * barHeight = 12;
	 * rectangleWidth = 40;
	 * rectangleHeight = 15;
	 * horizontalGap = 7;
	 * verticalGap = 3;
	 * @param volMax The value of the audioSpectrumThresholdProperty of the MediaPlayer object.
     * @param canvas
	 */
        
	public SpectrumBars(int volMax, Canvas canvas) {
            this(   volMax,
                    0,
                    0,
                    3, 3,
                    1, 1,
                    canvas   );
	}
        
	/**
	 * Initializes a SpectrumBars object with the values:
	 * maxVolume = parameter volMax;
	 * numBars = parameter bars;
	 * barHeight = 12;
	 * rectangleWidth = 40;
	 * rectangleHeight = 15;
	 * horizontalGap = 7;
	 * verticalGap = 3;
	 * @param volMax The value of the audioSpectrumThresholdProperty of the MediaPlayer object.
	 * @param bars The number of vertical bars to be generated.
	 */
	public SpectrumBars(int volMax, int bars) {
            this(volMax, bars, 12, 40, 15, 7, 3, null);
	}
	
	/**
	 * Initializes a SpectrumBars object with full user preferences.
	 * @param volMax The value of the audioSpectrumThresholdProperty of the MediaPlayer object.
	 * @param bars The number of vertical bars to be generated.
	 * @param height The maximum number of rectangles is any given bar.
	 * @param rectWidth The width (in pixels) of each rectangle (and therefore bar).
	 * @param rectHeight The height (in pixels) of each rectangle.
	 * @param hGap The horizontal gap between each bar.
	 * @param vGap The vertical gap between each rectangle.
         * @param canvas ????????
	 */
	public SpectrumBars(int volMax, int bars, int height, int rectWidth, int rectHeight, int hGap, int vGap,
                Canvas canvas) {
            
            if(canvas!=null){
                int w = (int)canvas.getWidth()-3;
                int h = (int)canvas.getHeight();
                
                int w0 = w/(rectWidth+hGap);
                int h0 = h/(rectHeight+vGap);
                
                bars = w0;
                height = h0;
            }
            if(canvas==null)
                this.display = new Canvas();
            else
                this.display = canvas;
            
            compressor = new FrequencyCompressor(volMax);
            isbottomColorShifting = false;
            maxVolume = volMax;
            numBars = bars;
            barHeight = height;
            rectangleWidth = rectWidth;
            rectangleHeight = rectHeight;
            horizontalGap = hGap;
            verticalGap = vGap;
            display.setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
            display.setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
            bottomColor = Color.GOLD;
            topColor = Color.BLUE;
            backgroundColor = Color.TRANSPARENT;
            timeSinceColorUpdate = 0;
            colorShiftIndex = 0;
            colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
            System.out.println("display - "+display.getWidth()+":"+display.getHeight());
//            resize();
	}
	
        private void resize(){
            display.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(event.getButton()==MouseButton.SECONDARY){
                        display.setWidth(display.getWidth()+5);
                        display.setHeight(display.getHeight()+5);
                        System.out.println("click secondary");
                    }
                    System.out.println("click");
                }
            });
        }
        
	/**
	 * Shifts the top or bottom color along the colorSiftVals array.
	 */
	private void incrementColors() {
		if(isbottomColorShifting)
			bottomColor = colorShiftVals[colorShiftIndex];
		else
			topColor = colorShiftVals[colorShiftIndex];
		colorShiftIndex += 1;
		if(colorShiftIndex == colorShiftVals.length) {
			colorShiftIndex = 0;
			if(isbottomColorShifting) {
				isbottomColorShifting = false;
				colorShiftVals = Gradient.buildRandomGradient(topColor, 210, COLOR_SHIFT_SPEED);
			}
			else {
				isbottomColorShifting = true;
				colorShiftVals = Gradient.buildRandomGradient(bottomColor, 210, COLOR_SHIFT_SPEED);
			}
		}
	}
	
	/**
	 * Takes the magnitudes array and, using a FrequencyCompressor object,
	 * converts that into an array of heights for each bar.
	 * @param magnitudes The array to be processed.
	 * @param timestamp The time in the song.
	 * @return an array of heights for each bar.
	 */
	private int[] processHeights(float[] magnitudes, double timestamp) {
		int[] heights = new int[numBars];
		float[] buckets = compressor.compressHeights(magnitudes, numBars, timestamp);
		for(int i = 0; i < numBars; ++i) {
			heights[i] = (int) ( (float) (buckets[i]* (float) barHeight));
		}
		return heights;
	}
	
	/**
	 * Runs the visualization. This function is called by a SpectrumListener object,
	 * and manages drawing to the screen and knowing when to shift the top or bottom
	 * color. It takes every value from spectrumDataUpdate, as demanded by inheriting
	 * from Visualization.
	 * @param timestamp Used in the FrequencyCompressor object.
	 * @param duration Used in knowing when to shift the top or bottom color.
	 * @param magnitudes The array that will be processed into heights.
	 * @param phases Unused.
	 */
        @Override
	public void Update(double timestamp, double duration, float[] magnitudes, float[] phases) {
		GraphicsContext context = display.getGraphicsContext2D();
		display.setWidth(rectangleWidth*numBars+horizontalGap*(numBars+1)+HORIZONTAL_PADDING);
		display.setHeight(rectangleHeight*barHeight+verticalGap*(barHeight+1)+VERTICAL_PADDING);
		timeSinceColorUpdate += duration;
		if(timeSinceColorUpdate >= COLOR_UPDATE_FREQUENCY) {
			timeSinceColorUpdate = 0;
			incrementColors();
		}
		Color[] colorVals = Gradient.buildGradient(bottomColor, topColor, barHeight);
		int[] heights = processHeights(magnitudes, timestamp);
		context.setFill(backgroundColor);
                context.clearRect(0, 0, display.getWidth(), display.getHeight());
		context.fillRect(0, 0, display.getWidth(), display.getHeight());
		for(int bar_index = 0; bar_index < numBars; ++bar_index) {
			if(heights[bar_index] >= barHeight)
				heights[bar_index] = barHeight-1;
			int x_val = rectangleWidth*bar_index+horizontalGap*(bar_index+1)+HORIZONTAL_PADDING/2;
			for(int rectangle_index = 1; rectangle_index <= heights[bar_index]; ++rectangle_index) {
				int y_val = (int) display.getHeight() - (rectangleHeight*rectangle_index+verticalGap*(rectangle_index+1)+VERTICAL_PADDING/2);
				context.setFill(colorVals[rectangle_index]);
				context.fillRect(x_val, y_val, rectangleWidth, rectangleHeight);
			}
		}
	}

	/**
	 * Gets the node to be added to the GUI.
	 * @return The node to be added to the GUI.
	 */
	@Override
	public Node getNode() {
		return display;
	}
	
}
