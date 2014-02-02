package mmm.jeu.model.pieces;

import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;
import mmm.jeu.model.interfaces.ITour;

public class Tour extends Piece implements ITour {

	public Tour(Coord position, char color){
		super(position, color);
		this.type = ToolsModel.tour;
	}
}
