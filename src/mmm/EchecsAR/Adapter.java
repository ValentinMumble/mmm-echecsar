package mmm.EchecsAR;

import mmm.jeu.model.Coord;

public interface Adapter {
	void movePiece(int fromrow, int fromcol, int torow, int tocol);
	void displayMessage (String message);
	int promotion();
	void replace(Coord coordPion, int pieceType);
	void killPiece(Coord coordPiece);
}
