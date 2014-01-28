package mmm.jeu.model.pieces;

import java.util.ArrayList;

import mmm.jeu.model.Coord;
import mmm.jeu.model.interfaces.IPiece;

public class Piece implements IPiece {

	private Coord position;
	private char color;
	protected String type = "";
	private boolean dejaBouge = false;
	
	protected Piece(Coord position, char color){
		this.position = new Coord(position);
		this.color = color;
	}
	
	@Override
	public Coord getCoord() {
		return new Coord(position);
	}
	@Override
	public char getColor() {
		return color;
	}
	@Override
	public String getType() {
		return new String(type);
	}
	@Override
	public boolean getDejaBouge(){
		return dejaBouge;
	}

	@Override
	public ArrayList<Coord> mouvementPossibles() {
		return null;
	}

	@Override
	public void deplacer(Coord target) {
		position = new Coord(target);
		this.actionSpeciale();
	}
	
	public String toString(){
		return "position = "+position.toString()+" "+type+" color = "+color;
	}
	
	private void actionSpeciale()
	{dejaBouge = true;}

}
