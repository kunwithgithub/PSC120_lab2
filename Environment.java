package freezingaggregation;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;

public class Environment extends SimState {
	public SparseGrid2D space;
	public boolean toroidal = true;
	public boolean collision = true;
	public int n = 50;
	public int gridWidth = 50;
	public int gridHeight = 50;
	public boolean narrowRule = false;
	public double p = 0.0;
	
	
	public boolean isToroidal() {
		return toroidal;
	}

	public void setToroidal(boolean toroidal) {
		this.toroidal = toroidal;
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}

	public boolean isNarrowRule() {
		return narrowRule;
	}

	public void setNarrowRule(boolean narrowRule) {
		this.narrowRule = narrowRule;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public Environment (long seed) {
		super(seed);
	}
	
	public void makeAgents() {
		Agent init = new Agent(gridWidth / 2, gridHeight / 2, 0, 0, true);
		space.setObjectLocation(init, gridWidth / 2, gridHeight / 2);
		this.schedule.scheduleRepeating(init);
		for (int i = 0; i < n - 1; i++) {
			int x = random.nextInt(gridWidth);
			int y = random.nextInt(gridHeight);
			Object o = space.getObjectsAtLocation(x, y);
			while (this.collision == true && o != null) {
				x = random.nextInt(gridWidth);
				y = random.nextInt(gridHeight);
				o = space.getObjectsAtLocation(x, y);
			}
			int dirX = random.nextInt(3) - 1;
			int dirY = random.nextInt(3) - 1;
			Agent a = new Agent(x, y, dirX, dirY, false);
			space.setObjectLocation(a, x, y);
			schedule.scheduleRepeating(a);
		
		}
	}
	
	public void start() {
		super.start();
		space = new SparseGrid2D(gridWidth, gridHeight);
		makeAgents();
	}
	
}
