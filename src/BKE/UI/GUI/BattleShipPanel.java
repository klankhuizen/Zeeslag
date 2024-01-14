package BKE.UI.GUI;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BattleShipPanel extends JPanel {

    /**
     * Matrix with field values, used to determine which color the background of a square should be.
     */
    private int[][] _matrix;

    /**
     * The amount of horizontal squares
     */
    private int _numHorizontal = 1;

    /**
     * The amount of vertical squares
     */
    private int _numVertical = 1;

    /**
     * Size of the squares as drawn on the canvas. Updates every time you resize the window
     */
    private int _squareSize = 0;


    /**
     * Callback function that will be called when a valid x,y grid is clicked so the game can handle that input
     */
    private BiConsumer _callback;

    /**
     * Map of the different colors used per field value
     */
    private final Map<Integer, Color> _colorMap = Map.of(
            0, new Color(255, 255, 255), // WHITE
            1, new Color(255, 25, 25), // RED
            2, new Color(100, 150, 255) // BLUE
    );

    /**
     * Create a new battleship panel
     * @param numVertical The amount of vertical squares
     * @param numHorizontal The amount of horizontal squares
     * @param callback Function called when a valid grid coordinate is clicked.
     */
    public BattleShipPanel(int numVertical, int numHorizontal, BiConsumer<Integer, Integer> callback) {
        super();
        if (numHorizontal < 1 || numVertical < 1){
            throw new IllegalArgumentException("Min size of grid is 1x1");
        }
        _numHorizontal = numHorizontal;
        _numVertical = numVertical;
        _matrix = new int[numHorizontal][numVertical];
        _callback = callback;
        this.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                OnMouseClicked(e.getX(), e.getY());
            }
        });
    }

    /**
     * Handler for mouse clicks on the canvas.
     * @param x
     * @param y
     */
    protected void OnMouseClicked(int x, int y){
        // Calculate the grid coord
        int h = Math.floorDiv(x, _squareSize);
        int v = Math.floorDiv(y, _squareSize);

        // Do an out-of-bounds check
        if (h < _numHorizontal && v < _numVertical){
            _callback.accept(h, v);
        }
    }

    /**
     * Update the field with new field color values
     * @param matrix X -> Y field values
     */
    public void UpdateField(int[][] matrix){
        if (_matrix.length != matrix.length || _matrix[0].length != matrix[0].length) {
            throw new IllegalArgumentException("Matrix must be of the same size as field x and y");
        }
        _matrix = matrix;
        this.repaint(); // update canvas
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = null;
        try {
            graphics = (Graphics2D) g.create(); // Create canvas

            int height = getHeight();
            int width = getWidth();
            int size = Math.min(height, width);
            int maxRowLength = Math.max(_numHorizontal, _numVertical); // Get max num of either horizontal or vertical in order to always draw the biggest squares possible
            _squareSize = size / maxRowLength; // They are squares so both sides are equal, duh

            // Draw vertical -> horizontal.
            int x = 2;
            for (int v = 0; v < _numVertical; v++){
                int y = 2;
                for (int h = 0; h < _numHorizontal; h++){
                    int value = _matrix[h][v]; // Grab field value
                    graphics.setColor(new Color(0, 0, 0)); // Borders are always black
                    graphics.drawRect(x, y, _squareSize, _squareSize);
                    graphics.setColor(_colorMap.get(value)); // Draw BG color based on field value
                    graphics.fillRect(x + 1, y + 1, _squareSize - 1, _squareSize - 1); // 1 px offset so the border is visible
                    y += _squareSize;
                }
                x += _squareSize;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        } finally {
            // Clean up at all times
            if (graphics != null){
                graphics.dispose();
            }
        }
    }
}