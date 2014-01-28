package mmm.jeu.model.pieces;

import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;
import mmm.jeu.model.interfaces.IPiece;

public class Pion extends Piece implements IPiece {
	
	public Pion(Coord position, char color){
		super(position, color);
		this.type = ToolsModel.pion;
	}
}
