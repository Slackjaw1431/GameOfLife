package gui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.RowColumnMismatchException;
import model.World;

public class GamePanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private final static int CELLSIZE = 10;
	private final static Color backgroundColor = Color.BLACK;
	private final static Color foregroundColor = Color.GREEN;
	private final static Color gridColor = Color.GRAY;
	
	private int topBottomMargin;
	private int leftRightMargin;
	
	private World world;
	
	public GamePanel() {
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int col = (e.getX() - topBottomMargin)/CELLSIZE;//getting mouse coordinates and converting it into cellsize grid coordinates
				int row = (e.getY() - leftRightMargin)/CELLSIZE;
				System.out.println(col + " " + row + " ");			
				
				if(row >= world.getRows() || col >= world.getColumns()) {
					return;//ignoring margin areas so theres no error when clicking outside
				}
				boolean status = world.getCell(row,col);
				world.setCell(row, col, !status);//changes the value in the matrix at position to its opposite
				repaint();//recalls the paint method which redraws the whole thing
				}
			});
		//Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->next(), 250, 250, TimeUnit.MILLISECONDS);//keeps the next()function running creating an animation effect
	}
	

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		
		int w = getWidth();
		int h = getHeight();
		
		leftRightMargin = ((w % CELLSIZE) + CELLSIZE)/2; //first you divide total width by cellsize into cellsized chunks then you add cellsize and divide by 2 because margin has to be in both sides left and right
		topBottomMargin = ((h % CELLSIZE) + CELLSIZE)/2;
		
		int rows = (h - 2 * topBottomMargin)/CELLSIZE;
		int columns = (w - 2 * leftRightMargin)/CELLSIZE;//dividing the grid into rows and columns by taking out the top and bottom margin from the total height and dividing the remainder into cellsize chunks  
		
		if(world == null) {
			world = new World(rows, columns);//fixing resize bug and creating grid
		}
		else {
			if(world.getRows() != rows || world.getColumns() != columns) {
				world = new World(rows, columns);
			}
		}
		
		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, w, h);
		
		drawGrid(g2, w, h);

		for(int column = 0; column < columns; column++) {//whenever the paint component repaints it goes through these loops where it checks the status of the cells and updates the color
			for(int row = 0; row < rows; row++) {
				boolean status = world.getCell(row, column);
				fillCell(g2, row, column, status);
			}
		}
	}

	private void fillCell(Graphics2D g2, int row, int col, boolean status) {
		
		Color color = status ? foregroundColor : backgroundColor;
		g2.setColor(color);
		
		int x = leftRightMargin + (col * CELLSIZE);
		int y = topBottomMargin + (row * CELLSIZE);
		
		g2.fillRect(x+1, y+1, CELLSIZE-2, CELLSIZE-2);
	}
	
	private void drawGrid(Graphics2D g2, int w, int h) {
		g2.setColor(gridColor);
		for(int x = leftRightMargin; x <= w - leftRightMargin; x += CELLSIZE) {
			g2.drawLine(x,topBottomMargin,x, h - topBottomMargin);
		}
		for(int y = topBottomMargin; y <= w - topBottomMargin; y += CELLSIZE) {
			g2.drawLine(leftRightMargin, y, w - leftRightMargin,y);
		}
	}



	public void randomize() {
		world.randomize();
		repaint();
	}


	public void clear() {
		world.clear();
		repaint();
	}


	public void next() {
		world.next();
		repaint();		
	}
	
	public void save(File selectedFile) {
		world.save(selectedFile);
	}
	
	public void open(File selectedFile) {
		try {
			world.open(selectedFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Cannot open file", "Error", JOptionPane.ERROR_MESSAGE);
			errorReport(e);
		} catch (RowColumnMismatchException e) {
			JOptionPane.showMessageDialog(this, "Rows and Columns do not match with saved file", "Warning", JOptionPane.WARNING_MESSAGE);
			errorReport(e);
		}
		repaint();
	}


	private void errorReport(Exception e) { //prints error file with stacktrace as strings
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		e.printStackTrace(pw);
		String s = sw.toString();

		try {
			Files.write(Paths.get("ErrorReport.txt"), s.getBytes());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Error report created");
		
	}
}
