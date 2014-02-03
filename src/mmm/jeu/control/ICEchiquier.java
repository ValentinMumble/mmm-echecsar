package mmm.jeu.control;

import java.util.ArrayList;

import mmm.jeu.model.Coord;
import mmm.jeu.model.interfaces.IPiece;

public interface ICEchiquier {

	IPiece getPiece (Coord position);
	char getTourDeJoueur();
	
	boolean isOccuped(Coord position);
	ArrayList<Coord> mouvementPossibles(Coord coordonneePiece);
	void deplacerPiece(Coord positionDepart, Coord positionArrivee);
	
	void draw();
	
}
