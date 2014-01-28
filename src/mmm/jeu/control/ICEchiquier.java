package mmm.jeu.control;

import java.util.ArrayList;

import mmm.jeu.model.Coord;

public interface ICEchiquier {

	boolean isOccuped(Coord position);
	ArrayList<Coord> mouvementPossibles(Coord coordonneePiece);
	void deplacerPiece(Coord positionDepart, Coord positionArrivee);
	
}
