package mmm.jeu.model;

public class Coord {


	int x,y;
	
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Coord(Coord copie){
		this.x = copie.getX();
		this.y = copie.getY();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public String toString(){
		return "[" + x + ',' + y + ']';
	}
}
