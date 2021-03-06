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
	
	public Coord(String coordString){
		
		String[] kk = coordString.split(",");
		
		this.x = Integer.parseInt(kk[0]);
		this.y = Integer.parseInt(kk[1]);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public String toString(){
		return x + "," + y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
	        return true;
	    if (o == null)
	        return false;
	    if (getClass() != o.getClass())
	        return false;
	    final Coord other = (Coord) o;
	    if (x != other.getX() || y != other.getY())
	        return false;
	    return true;
	}
}
