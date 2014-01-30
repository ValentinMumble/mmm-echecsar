package mmm.jeu.model.pieces;

import mmm.jeu.model.Coord;
import mmm.jeu.model.interfaces.IRoi;

public class Roi extends Piece implements IRoi {

	public Roi(Coord position, char color) {
		super(position, color);
		this.type = "Roi";
	}

}
