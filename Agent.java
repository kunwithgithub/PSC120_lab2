package freezingaggregation;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

public class Agent implements Steppable {

	int x, y, dirx, diry;
	boolean frozen;
	public Agent(int x, int y, int dirx, int diry, boolean frozen) {
		super();
		this.x = x;
		this.y = y;
		this.dirx = dirx;
		this.diry = diry;
		this.frozen = frozen;
	}
	
	public int bounceXY(Environment state, char direction) {
		int newLocation = 0;
		switch(direction){
			case 'x':
				int newX = this.dirx + this.x;
				if (newX >= state.gridWidth || newX < 0) {
					this.dirx = this.dirx - 1;
					newLocation = this.dirx+this.x;
				}else{
					newLocation = this.dirx + this.x;
				}
				break;
			case 'y':
				int newY = this.diry + this.y;
				if (newY >= state.gridWidth || newY < 0) {
					this.diry = this.diry - 1;
					newLocation = this.diry + this.y;
				}else{
					newLocation = this.diry + this.y;
				}
				break;
			default:
				newLocation = 0;
			
		}
		return newLocation;
	}
	
	
	public void placeAgent(Environment state) {
        if (state.toroidal) {               
             x = state.space.stx(x + dirx);
             y = state.space.sty(y + diry);
             state.space.setObjectLocation(this, x, y);
        } else {
        	x = this.bx(state);
        	y = this.by(state);
            state.space.setObjectLocation(this, x, y);
        }
   }
   
   public int decideXY(){
	   if (state.random.nextBoolean(state.p)) {
		   return state.random.nextInt(3) - 1;
	   }
	   
   }
	public void aggregate(Environment state) {
		if (this.frozen) {
			return;
		}
		
		
		dirx = decideXY();
		diry = decideXY();
		
		int newX;
		int newY;
		if (state.toroidal) {
			newX = state.space.stx(x + dirx);
            newY = state.space.sty(y + diry);
		} else {
			newX = bounceXY(state,'x');
			newY = bounceXY(state,'y');
		}
		
		if (state.narrowRule) {
			Agent a = null;
			if (state.space.getObjectsAtLocation(newX, newY) != null) {
				a = (Agent) state.space.getObjectsAtLocation(newX, newY).objs[0]; // Narrow rule
				
			}
			if (this.frozen != true) {
				if (a != null && a.frozen == true) {
					this.frozen = true;
				} else {
					this.x = newX;
					this.y = newY;
				}
			}
			
			
		} else {
			Bag neighbors = null;
			if (state.toroidal) {
				neighbors = state.space.getMooreNeighbors(newX, newY, 1, SparseGrid2D.TOROIDAL, false); // Broad rule
			} else {
				neighbors = state.space.getMooreNeighbors(newX, newY, 1, SparseGrid2D.BOUNDED, false);
			}
			if (this.frozen != true) {
				if (state.collision == true) {
					if (state.space.getObjectsAtLocation(newX, newY) == null) {
						this.x = newX;
						this.y = newY;
					}
				}else if ((state.collision == true && state.space.getObjectsAtLocation(newX, newY) == null) 
						|| state.collision == false)  {
					this.x = newX;
					this.y = newY;
				}
				for (int i = 0; i < neighbors.numObjs; i++) {
					Agent a = (Agent) neighbors.objs[i];
					if (a.frozen == true) {
						this.frozen = true;
					}
				}
			}
		}
		placeAgent(state);
	}
	
	public void step(SimState state) {
		Environment environment = (Environment)state;
		aggregate(environment);
	}

}
