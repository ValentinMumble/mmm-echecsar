package mmm.jeu.model.pieces;

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
	
	public Piece (IPiece clone){
		this.position = new Coord(clone.getCoord());
		this.color = clone.getColor();
		this.type = new String(clone.getType());
		this.dejaBouge = clone.getDejaBouge();
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
	public void deplacer(Coord target) {
		position = new Coord(target);
		this.actionSpeciale();
	}
	
	public String toString(){
		return "position = "+position.toString()+" "+type+" color = "+color;
	}
	
	private void actionSpeciale()
	{dejaBouge = true;}

	@Override
	public String getDraw() {
		return type.charAt(0)+""+type.charAt(1)+""+color;
	}
	
	

}
