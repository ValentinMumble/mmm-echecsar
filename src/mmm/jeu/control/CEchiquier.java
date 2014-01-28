package mmm.jeu.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.location.Address;

import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;
import mmm.jeu.model.interfaces.IPiece;
import mmm.jeu.model.pieces.*;

public class CEchiquier implements ICEchiquier {

	private Map<String, IPiece> etatPlateau;
	private String posRoiBlanc;
	private String posRoiNoir;
	
	private char tourDeJoueur = 'B';
	
	public CEchiquier(){
		initPlateau();
		for (Entry<String, IPiece> c : etatPlateau.entrySet()) {
			System.out.println(c.getKey().toString());
			System.out.println(((Piece)c.getValue()).toString());
		}
		
		System.out.println(etatPlateau.toString());
	}
	
	@Override
	public boolean isOccuped(Coord position) {
		return etatPlateau.get(position.toString())!=null;
	}
	
	
	/**
	 * Retours : 
	 * 		null si la case fournit en parametre est vide ou si la piece appartient au joueur adverse
	 * 		une arrayList contenant les coup possibles
	 */
	@Override
	public ArrayList<Coord> mouvementPossibles(Coord coordonneePiece) {
		if (!isOccuped(coordonneePiece) || etatPlateau.get(coordonneePiece.toString()).getColor() != tourDeJoueur)
			return null;

		ArrayList<Coord> mvt = new ArrayList<Coord>();
		
		IPiece pieceSelected =  etatPlateau.get(coordonneePiece.toString());
		String typePiece = pieceSelected.getType();
		
		if(typePiece.equals(ToolsModel.pion))
			coupPion(mvt, pieceSelected);
		else if (typePiece.equals(ToolsModel.tour))
			coupTour(mvt, pieceSelected);
			
		
		
		return mvt;
	}
	
	/**
	 * 
	 * Rmq  : la prise au passage n'est pas implementée
	 * Rmq2 : normalement pas d'erreur au bord du plateau si la promotion est bien implementée
	 * @param coups
	 * @param pion
	 */
	public void coupPion(ArrayList<Coord> coups, IPiece pion){
		int x = pion.getCoord().getX();
		int y = pion.getCoord().getY();
		
		if (pion.getColor() == 'W')
		{	
			if (!isOccuped(new Coord(x+1, y)))
				coups.add(new Coord(x+1, y));
			if (!pion.getDejaBouge()&&!isOccuped(new Coord(x+2, y)))
				coups.add(new Coord(x+2, y));
			
			// Attention , pour les 2 cas suivants , ça marche car on test la presence AVANT
			// si on inverse les 2 elements du test on obtiendra un nullpointer !!
			if (isOccuped(new Coord(x+1, y+1)) && (etatPlateau.get(new Coord(x+1, y+1).toString()).getColor()!= tourDeJoueur))
				coups.add(new Coord(x+1, y+1));
			if (isOccuped(new Coord(x+1, y-1)) && (etatPlateau.get(new Coord(x+1, y-1).toString()).getColor()!= tourDeJoueur))
				coups.add(new Coord(x+1, y-1));
		}
		else 
		{
			if (!isOccuped(new Coord(x-1, y)))
				coups.add(new Coord(x-1, y));
			if (!pion.getDejaBouge()&&!isOccuped(new Coord(x-2, y)))
				coups.add(new Coord(x-2, y));
			
			// Attention , pour les 2 cas suivants , ça marche car on test la presence AVANT
			// si on inverse les 2 elements du test on obtiendra un nullpointer !!
			if (isOccuped(new Coord(x-1, y+1)) && (etatPlateau.get(new Coord(x-1, y+1).toString()).getColor()!= tourDeJoueur))
				coups.add(new Coord(x-1, y+1));
			if (isOccuped(new Coord(x-1, y-1)) && (etatPlateau.get(new Coord(x-1, y-1).toString()).getColor()!= tourDeJoueur))
				coups.add(new Coord(x-1, y-1));
		}
	}
	private void coupTour(ArrayList<Coord> coups, IPiece tour){
		int x = tour.getCoord().getX();
		int y = tour.getCoord().getY();
		boolean ok = true;
		int i = x;

		while (ok && i>1){
			i--;
			if (!isOccuped(new Coord(i, y)))
				coups.add(new Coord(i, y));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(i,y)).getColor() != tourDeJoueur)
					coups.add(new Coord(i,y));
			}
		}
		i=y;
		ok = true;
		while (ok && i>1){
			i--;
			if (!isOccuped(new Coord(x, i)))
				coups.add(new Coord(x, i));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(x,i)).getColor() != tourDeJoueur)
					coups.add(new Coord(x,i));
			}
		}
		i=x;
		ok = true;
		while (ok && i<8){
			i++;
			if (!isOccuped(new Coord(i, y)))
				coups.add(new Coord(i, y));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(i,y)).getColor() != tourDeJoueur)
					coups.add(new Coord(i,y));
			}
		}
		i=y;
		ok = true;
		while (ok && i<8){
			i++;
			if (!isOccuped(new Coord(x, i)))
				coups.add(new Coord(x, i));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(x,i)).getColor() != tourDeJoueur)
					coups.add(new Coord(x,i));
			}
		}
	}
	
	
	@Override
	public void deplacerPiece(Coord positionDepart, Coord positionArrivee) {
		
		System.out.println("piece a bouger = "+etatPlateau.get(positionDepart.toString()).toString());
		
		IPiece pieceMove = etatPlateau.get(positionDepart.toString());
		etatPlateau.remove(pieceMove.getCoord().toString());
		pieceMove.deplacer(positionArrivee);
		etatPlateau.put(pieceMove.getCoord().toString(), pieceMove);
		
		System.out.println("pos dep = "+etatPlateau.get(positionDepart.toString()));
		System.out.println("pos arr = "+etatPlateau.get(positionArrivee.toString()).toString());
	}
	

	private void initPlateau(){
		etatPlateau = new HashMap<String, IPiece>();

		etatPlateau.put(new Coord(1, 1).toString(), new Tour	(new Coord(1, 1), 'W'));
		etatPlateau.put(new Coord(1, 2).toString(), new Cavalier(new Coord(1, 2), 'W'));
		etatPlateau.put(new Coord(1, 3).toString(), new Fou(new Coord(1, 3), 'W'));
		etatPlateau.put(new Coord(1, 4).toString(), new Reine(new Coord(1, 4), 'W'));
		etatPlateau.put(new Coord(1, 5).toString(), new Roi(new Coord(1, 5), 'W'));
		etatPlateau.put(new Coord(1, 6).toString(), new Fou(new Coord(1, 6), 'W'));
		etatPlateau.put(new Coord(1, 7).toString(), new Cavalier(new Coord(1, 7), 'W'));
		etatPlateau.put(new Coord(1, 8).toString(), new Tour	(new Coord(1, 8), 'W'));
		
		posRoiBlanc = new Coord(1, 5).toString();

		etatPlateau.put(new Coord(8, 1).toString(), new Tour	(new Coord(8, 1), 'B'));
		etatPlateau.put(new Coord(8, 2).toString(), new Cavalier(new Coord(8, 2), 'B'));
		etatPlateau.put(new Coord(8, 3).toString(), new Fou	(new Coord(8, 3), 'B'));
		etatPlateau.put(new Coord(8, 4).toString(), new Reine	(new Coord(8, 4), 'B'));
		etatPlateau.put(new Coord(8, 5).toString(), new Roi	(new Coord(8, 5), 'B'));
		etatPlateau.put(new Coord(8, 6).toString(), new Fou	(new Coord(8, 6), 'B'));
		etatPlateau.put(new Coord(8, 7).toString(), new Cavalier(new Coord(8, 7), 'B'));
		etatPlateau.put(new Coord(8, 8).toString(), new Tour	(new Coord(8, 8), 'B'));
		
		posRoiNoir = new Coord(8, 5).toString();
		
		for (int i = 1; i < 9; i++){
			etatPlateau.put(new Coord(2, i).toString(), new Pion(new Coord(2, i), 'W'));
			etatPlateau.put(new Coord(7, i).toString(), new Pion(new Coord(7, i), 'B'));
		}
	}


	

}
