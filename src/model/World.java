package model;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javax.swing.JOptionPane;

public class World {
	private int rows, columns;

	private boolean[][] grid;// tricky but basically a
								// matrix of primitive
								// boolean values which are
								// false by default
	private boolean[][] gridBuffer; // buffer grid

	public World(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;

		grid = new boolean[rows][columns]; // grid
											// initialized
											// with rows and
											// columns with
											// the default
											// value of
											// booleans
											// being false
		// gridBuffer = grid;// does this make two of the
		// same
		// or does it just create one
		// like the old i think objects
		// just pass the reference and
		// dont make a new one
		gridBuffer = new boolean[rows][columns];
	}

	public boolean getCell(int row, int col) {
		return grid[row][col];
	}

	public void setCell(int row, int col, boolean status) {
		grid[row][col] = status; // returns the status of
									// the cell in the
									// matrix
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public void randomize() {
		Random random = new Random();
		for (int i = 0; i < (rows * columns) / 10; i++) {
			int row = random.nextInt(rows);
			int col = random.nextInt(columns);
			setCell(row, col, true);
		}
	}

	public void clear() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				setCell(row, col, false);
			}
			/*
			 * alternative way for(int row = 0; row < rows;
			 * row++){ Arrays.fill(grid[row],false); can you
			 * access a 2d array object like grid with just
			 * the one element? guess so }
			 */
		}
	}

	public void next() {
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				int neighbors = countNeighbors(row, col);

				boolean status = false;

				if (neighbors < 2) {
					status = false;
				} else if (neighbors > 3) {
					status = false;
				} else if (neighbors == 3) {
					status = true;
				} else if (neighbors == 2) {
					status = getCell(row, col);
				}
				gridBuffer[row][col] = status; // copies
												// status to
												// the
												// buffer
			}
		}
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < columns; col++) {
				grid[row][col] = gridBuffer[row][col]; // updates
														// main
														// grid
														// based
														// on
														// buffer
			}
		}
	}

	public int countNeighbors(int row, int col) {
		int neighbors = 0;

		for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
			for (int colOffset = -1; colOffset <= 1; colOffset++) {
				if (rowOffset == 0 && colOffset == 0) {
					continue;
				}
				int gridRow = row + rowOffset;
				int gridCol = col + colOffset;

				if (gridRow < 0) {
					continue;
				} else if (gridRow == rows) {
					continue;
				}
				if (gridCol < 0) {
					continue;
				} else if (gridCol == columns) {
					continue;
				}
				boolean status = getCell(gridRow, gridCol);
				if (status) {
					neighbors++;
				}
			}
		}
		return neighbors;
	}

	public void save(File selectedFile) {
		try (var dos = new DataOutputStream(
				new FileOutputStream(selectedFile))) {
			dos.writeInt(rows);
			dos.writeInt(columns);
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < columns; col++) {
					dos.writeBoolean(grid[row][col]);
				}
			}
		} catch (IOException e) {
			errorReport(e); 
			System.out.println("Error saving file");
		}
	}

	private void errorReport(IOException e) { //prints error file
		String s = e.toString();

		try {
			Files.write(Paths.get("ErrorReport.txt"), s.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Error report created");
		
	}

	public void open(File selectedFile) throws IOException, RowColumnMismatchException{
		try (var dis = new DataInputStream(
				new FileInputStream(selectedFile))) {
			int fileRows = dis.readInt();
			int fileCols = dis.readInt();
	
			for (int row = 0; row < fileRows; row++) {
				for (int col = 0; col < fileCols; col++) {
					boolean status = dis.readBoolean();
					
					if(row >= rows || col >= columns) {
						continue;
					}
					grid[row][col] = status;
				}
			}
			if(fileRows != rows || fileCols != columns) {
				throw new RowColumnMismatchException();
			}
		} 

	}
}