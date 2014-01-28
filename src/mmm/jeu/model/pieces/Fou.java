package mmm.jeu.model.pieces;

import mmm.jeu.model.Coord;
import mmm.jeu.model.interfaces.IFou;

public class Fou extends Piece implements IFou {

	public Fou(Coord position, char color) {
		super(position, color);
		this.type = "Fou";
	}

}
