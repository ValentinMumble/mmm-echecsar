package mmm.jeu.model.pieces;

import mmm.jeu.model.Coord;
import mmm.jeu.model.interfaces.ICavalier;

public class Cavalier extends Piece implements ICavalier {

	public Cavalier(Coord position, char color) {
		super(position, color);
		this.type = "Cavalier";
	}

}
