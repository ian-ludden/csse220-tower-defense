package gameEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import gameObjects.Enemy;
import gameObjects.enemies.Grunt;
import gameObjects.enemies.Heavy;

import java.io.File;

/**
 * Represents a level in the game. 
 * Each level has a distinct terrain, 
 * a budget for constructing or upgrading towers, 
 * and a series of waves of enemies to face. 
 */
public class Level {
    public static final int NUM_ROWS = 8;
    public static final int NUM_COLS = 10;
    
    private static final int DEFAULT_BUDGET = 0;

    private static final Color GRASS_COLOR = new Color(0, 228, 71);
    private static final Color PATH_COLOR = new Color(45, 47, 87);
    private static final Color PATH_START_COLOR = new Color(255, 236, 39);
    private static final Color SAND_COLOR = new Color(248, 121, 23, 180);
    
    private int levelNumber;
    private int budget;
    private int currentWaveIndex;
    private char[][] terrain;
    /**
     * Maps a wave and path string representation to a list of enemies. 
     * 
     * The string representation is of the form "Wave[WaveIndex]_Path[PathIndex]". 
     * 
     * 
     */
    private HashMap<String, ArrayList<Enemy>> waveAndPathToEnemies;
    /**
     * Maps a path index to the start cell of that path. 
     * 
     * 
     */
    private HashMap<Integer, Cell> pathToStartCell;

    /**
     * Constructs a new Level object from a file. 
     * 
     * @param filename
     */
    public Level(String filename) {
        this.budget = DEFAULT_BUDGET; 
        this.currentWaveIndex = 0;
        this.terrain = new char[NUM_ROWS][NUM_COLS];
        this.waveAndPathToEnemies = new HashMap<String, ArrayList<Enemy>>();
        this.pathToStartCell = null;

        this.loadMetadata(filename);
        this.loadTerrain(filename);
        this.findPathToStartCells();
        this.loadEnemies(filename);

    }

    public int getBudget() {
        return budget;
    }


    /**
     * Loads the metadata from the given file. 
     * 
     * The metadata is expected to be in the following format: 
     * 
     * ## START METADATA ##
     * LevelNumber,XXX
     * Budget,XXX
     * ## END METADATA ##
     * 
     * @param filename
     */
    private void loadMetadata(String filename) {
        File file = new File(filename);
        Scanner in;
        
        try {
            in = new Scanner(file);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("## START METADATA ##")) {
                    break;
                }
            }
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("## END METADATA ##")) {
                    break;
                }
                String[] tokens = line.split(",");
                if (tokens.length != 2) {
                    continue;
                }
                if (tokens[0].equals("LevelNumber")) {
                    this.levelNumber = Integer.parseInt(tokens[1]);
                } else if (tokens[0].equals("Budget")) {
                    this.budget = Integer.parseInt(tokens[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Loads the terrain from the given file. 
     * 
     * The terrain is expected to be in the following format: 
     * 
     * ## START TERRAIN ##
     * XXXXXXXXXXXXXXXX
     * XXXXXXXXXXXXXXXX
     * XXXXXXXXXXXXXXXX
     * ## END TERRAIN ##
     * 
     * The grid should be 8 rows by 10 columns. 
     * Allowed characters include 
     * '.' -> empty, grass, drawn as green square
     * 'P' -> path, drawn as gray square
     * '0-9' -> path starting location, drawn as white square with number
     * 'x' -> sand, blocked for tower placement, drawn as beige square
     * 
     * @param filename
     */
    private void loadTerrain(String filename) {
        File file = new File(filename);
        Scanner in;
        int i = 0;
        try {
            in = new Scanner(file);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("## START TERRAIN ##")) {
                    break;
                }
            }
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("## END TERRAIN ##")) {
                    break;
                }
                this.terrain[i++] = line.toCharArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Loads the enemies from the given file. 
     * 
     * The enemies are expected to be in the following format: 
     * 
     * ## START ENEMIES ##
     * EnemyType,Level,WaveIndex,PathIndex
     * EnemyType,Level,WaveIndex,PathIndex
     * ## END ENEMIES ##
     * 
     * @param filename
     */
    private void loadEnemies(String filename) {
        if (this.pathToStartCell == null) {
            this.findPathToStartCells();
        }
        
        File file = new File(filename);
        Scanner in;

        try {
            in = new Scanner(file);
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("## START ENEMIES ##")) {
                    break;
                }
            }
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("## END ENEMIES ##")) {
                    break;
                }
                String[] tokens = line.split(",");
                if (tokens.length != 4) {
                    continue;
                }

                String enemyType = tokens[0];
                if (enemyType.equals("Enemy Type")) {
                    continue; // Skip header line
                }

                int level = Integer.parseInt(tokens[1]);
                int waveIndex = Integer.parseInt(tokens[2]);
                int pathIndex = Integer.parseInt(tokens[3]);

                // Create a new empty list of enemies for this wave and path if it doesn't already exist
                String key = "Wave" + waveIndex + "_Path" + pathIndex;
                if (!this.waveAndPathToEnemies.containsKey(key)) {
                    this.waveAndPathToEnemies.put(key, new ArrayList<Enemy>());
                }
                this.waveAndPathToEnemies.get(key).add(this.createEnemy(enemyType, level, pathIndex));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Enemy createEnemy(String enemyType, int level, int pathIndex) {
        Cell startCell = this.pathToStartCell.get(pathIndex);
        if (enemyType.equals("Grunt")) {
            return new Grunt(startCell);
        } else if (enemyType.equals("Heavy")) {
            return new Heavy(startCell);
        } else {
            throw new IllegalArgumentException("Unsupported enemy type: " + enemyType);
        }
    }

    /**
     * Returns the next wave of enemies to face, 
     * as a map of path indices to lists of enemies. 
     * 
     * @return
     */
    public ArrayList<Enemy> getNextWave() {
        this.currentWaveIndex++;

        ArrayList<Enemy> wave = new ArrayList<Enemy>();

        String keyStart = "Wave" + this.currentWaveIndex + "_";

        for (String key : this.waveAndPathToEnemies.keySet()) {
            if (key.startsWith(keyStart)) {
                wave.addAll(this.waveAndPathToEnemies.get(key));
            }
        }

        return wave;
    }

    public int getWaveNumber() {
        return this.currentWaveIndex;
    }

    public int getTotalWaves() {
        return this.waveAndPathToEnemies.size();
    }

    /**
     * Returns true if the given cell is a valid tower location, false otherwise. 
     * 
     * The only type of terrain that is valid for tower placement is grass ('.'). 
     * 
     * @param cell
     * @return
     */
    public boolean isValidTowerLocation(Cell cell) {
        return this.terrain[cell.getRow()][cell.getColumn()] == '.';
    }

    /**
     * Finds the start cells for each path in the level. 
     */
    private void findPathToStartCells() {
        this.pathToStartCell = new HashMap<>();

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                if (this.terrain[row][col] >= '0' && this.terrain[row][col] <= '9') {
                    this.pathToStartCell.put(this.terrain[row][col] - '0', new Cell(row, col));
                }
            }
        }
    }

    /**
     * Returns true if the given cell is part of a path (including a numeric start cell).
     */
    public boolean isPathCell(Cell cell) {
        int r = cell.getRow();
        int c = cell.getColumn();
        if (r < 0 || r >= NUM_ROWS || c < 0 || c >= NUM_COLS) {
            return false;
        }
        char v = this.terrain[r][c];
        return v == 'P' || (v >= '0' && v <= '9');
    }

    /**
     * Draws the level on the given Graphics2D object.
     * 
     * The terrain is drawn first, where each cell is drawn according to its value in the terrain array. 
     * '.' -> empty, grass, drawn as green square
     * 'P' -> path, drawn as gray square
     * '0-9' -> path starting location, drawn as white square with number
     * 'x' -> sand, blocked for tower placement, drawn as beige square
     * 
     * @param g2d the Graphics2D object to draw on
     */
    public void drawOn(Graphics2D g2d) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                char cellValue = this.terrain[row][col];
                Color color = Color.WHITE;
                if (cellValue == '.') {
                    color = GRASS_COLOR;
                } else if (cellValue == 'P') {
                    color = PATH_COLOR;
                } else if (cellValue >= '0' && cellValue <= '9') {
                    color = PATH_START_COLOR;
                } else if (cellValue == 'x' || cellValue == 'X') {
                    color = SAND_COLOR;
                }
                g2d.setColor(color);
                g2d.fillRect(col * Cell.SQUARE_SIZE, row * Cell.SQUARE_SIZE, Cell.SQUARE_SIZE, Cell.SQUARE_SIZE);
                // Draw cell value if it's a path start
                if (cellValue >= '0' && cellValue <= '9') {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf(cellValue), col * Cell.SQUARE_SIZE + Cell.SQUARE_SIZE / 8,
                            row * Cell.SQUARE_SIZE + Cell.SQUARE_SIZE / 4);
                }
            }
        }

        // Draw thin black grid lines
        g2d.setColor(Color.BLACK);
        for (int row = 0; row < NUM_ROWS; row++) {
            g2d.drawLine(0, row * Cell.SQUARE_SIZE, NUM_COLS * Cell.SQUARE_SIZE, row * Cell.SQUARE_SIZE);
        }
        for (int col = 0; col < NUM_COLS; col++) {
            g2d.drawLine(col * Cell.SQUARE_SIZE, 0, col * Cell.SQUARE_SIZE, NUM_ROWS * Cell.SQUARE_SIZE);
        }
    }


    /**
     * Returns the next level in the game, or null if there is no next level. 
     * 
     * @return
     */
    public Level getNextLevel() {
        String nextLevelFilename = String.format("levels/level%02d.csv", this.levelNumber + 1);
        File nextLevelFile = new File(nextLevelFilename);
        if (!nextLevelFile.exists()) {
            return null;
        }
        return new Level(nextLevelFilename);
    }
}
