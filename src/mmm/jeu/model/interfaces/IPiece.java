package mmm.jeu.model.interfaces;

import java.util.ArrayList;

import mmm.jeu.model.Coord;

public interface IPiece {

	Coord getCoord();
	char getColor();
	String getType();
	boolean getDejaBouge();
	ArrayList<Coord> mouvementPossibles();
	void deplacer(Coord target);
	
}
