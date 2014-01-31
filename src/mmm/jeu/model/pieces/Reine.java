package mmm.jeu.model.pieces;

import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;
import mmm.jeu.model.interfaces.IReine;

public class Reine extends Piece implements IReine {

	public Reine(Coord position, char color) {
		super(position, color);
		this.type = ToolsModel.reine;
	}

}
