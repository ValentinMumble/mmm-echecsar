package mmm.jeu.model.interfaces;

import mmm.jeu.model.Coord;

public interface IPiece {

	Coord getCoord();
	char getColor();
	String getType();
	boolean getDejaBouge();
	void deplacer(Coord target);
	
	String getDraw();
	
}
