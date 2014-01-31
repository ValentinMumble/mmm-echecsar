package mmm.jeu.control;

import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;
import mmm.jeu.model.interfaces.IPiece;
import mmm.jeu.model.pieces.*;

public class CEchiquier implements ICEchiquier {

	public Map<String, IPiece> etatPlateau;
	private String posRoiBlanc;
	private String posRoiNoir;
	//TODO : ajouter pour chaque mouvement possible si a la suite de ce mouvement l'un des roi est en echec
	// si le roi allié est en echec interdir le mouvement 
	// si le roi ennemi est en echec appel d'une fonction pour demander l'affichage de l'echec
	
	public char tourDeJoueur = ToolsModel.blanc;
	
	public CEchiquier(){
		initPlateau();
		/*for (Entry<String, IPiece> c : etatPlateau.entrySet()) {
			System.out.println(c.getKey().toString());
			System.out.println(((Piece)c.getValue()).toString());
		}
		
		System.out.println(etatPlateau.toString());*/
	}
	
	/**
	 * Params : Coordonnées de la case à tester
	 * Retour : Indique si la case dont les coord sont passées en param est occupée
	 */
	@Override
	public boolean isOccuped(Coord position) {
		return etatPlateau.get(position.toString())!=null;
	}
	
	
	/**
	 * Params  : Coord de la piece que l'on veut deplacer
	 * Retours : 
	 * 		null si la case fournit en parametre est vide ou si la piece appartient au joueur adverse
	 * 		une arrayList contenant les coup possibles
	 */
	@Override
	public ArrayList<Coord> mouvementPossibles(Coord coordonneePiece) {
		return mouvementPossiblesAux(coordonneePiece, true);
	}
	
	private ArrayList<Coord> mouvementPossiblesAux(Coord coordonneePiece, boolean validation) {
		if (!isOccuped(coordonneePiece) || etatPlateau.get(coordonneePiece.toString()).getColor() != tourDeJoueur)
			return null;

		ArrayList<Coord> mvt = new ArrayList<Coord>();
		
		IPiece pieceSelected =  etatPlateau.get(coordonneePiece.toString());
		String typePiece = pieceSelected.getType();
		
		if(typePiece.equals(ToolsModel.pion))
			coupPion(mvt, pieceSelected);
		else if (typePiece.equals(ToolsModel.tour))
			coupTour(mvt, pieceSelected);
		else if (typePiece.equals(ToolsModel.cavalier))
			coupCavalier(mvt, pieceSelected);
		else if (typePiece.equals(ToolsModel.fou))
			coupFou(mvt, pieceSelected);
		else if (typePiece.equals(ToolsModel.reine))
			coupReine(mvt, pieceSelected);
		else if (typePiece.equals(ToolsModel.roi))
			coupRoi(mvt, pieceSelected);
		
			
		/*if (validation){
			System.out.println("LIST COUP BASE = "+mvt.toString());
			//tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir;
			mvt = validationCoups(mvt, coordonneePiece);
			//tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir;
		}*/
		
		return mvt;
	}
	
	/**
	 * 
	 * Rmq  : la prise au passage n'est pas implementée
	 * Rmq2 : normalement pas d'erreur au bord du plateau si la promotion est bien implementée
	 * @param coups
	 * @param pion
	 */
	private void coupPion(ArrayList<Coord> coups, IPiece pion){
		int x = pion.getCoord().getX();
		int y = pion.getCoord().getY();
		
		if (pion.getColor() == ToolsModel.blanc)
		{	
			if (!isOccuped(new Coord(x+1, y)))
				coups.add(new Coord(x+1, y));
			if (!pion.getDejaBouge()&&!isOccuped(new Coord(x+2, y)))
				coups.add(new Coord(x+2, y));
			
			// Attention , pour les 2 cas suivants , ça marche car on test la presence AVANT
			// si on inverse les 2 elements du test on obtiendra un nullpointer !!
			if (isOccuped(new Coord(x+1, y+1)) && (etatPlateau.get(new Coord(x+1, y+1).toString()).getColor()!= pion.getColor()))
				coups.add(new Coord(x+1, y+1));
			if (isOccuped(new Coord(x+1, y-1)) && (etatPlateau.get(new Coord(x+1, y-1).toString()).getColor()!= pion.getColor()))
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
			if (isOccuped(new Coord(x-1, y+1)) && (etatPlateau.get(new Coord(x-1, y+1).toString()).getColor()!= pion.getColor()))
				coups.add(new Coord(x-1, y+1));
			if (isOccuped(new Coord(x-1, y-1)) && (etatPlateau.get(new Coord(x-1, y-1).toString()).getColor()!= pion.getColor()))
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
				if (etatPlateau.get(new Coord(i,y).toString()).getColor() != tour.getColor())
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
				if (etatPlateau.get(new Coord(x,i).toString()).getColor() != tour.getColor())
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
				if (etatPlateau.get(new Coord(i,y).toString()).getColor() != tour.getColor())
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
				if (etatPlateau.get(new Coord(x,i).toString()).getColor() != tour.getColor())
					coups.add(new Coord(x,i));
			}
		}
	}
	private void coupFou(ArrayList<Coord> coups, IPiece fou){
		int x = fou.getCoord().getX();
		int y = fou.getCoord().getY();
		boolean ok = true;
		int i = x;
		int j = y;

		while (ok && i>1 && j>1){
			i--;
			j--;
			if (!isOccuped(new Coord(i, j)))
				coups.add(new Coord(i, j));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(i,j).toString()).getColor() != fou.getColor())
					coups.add(new Coord(i,j));
			}
		}
		i=x;
		j=y;
		ok = true;
		while (ok && i>1 && j<8){
			i--;
			j++;
			if (!isOccuped(new Coord(i, j)))
				coups.add(new Coord(i, j));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(i,j).toString()).getColor() != fou.getColor())
					coups.add(new Coord(i,j));
			}
		}
		i=x;
		j=y;
		ok = true;
		while (ok && i<8 && j>1){
			i++;
			j--;
			if (!isOccuped(new Coord(i, j)))
				coups.add(new Coord(i, j));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(i,j).toString()).getColor() != fou.getColor())
					coups.add(new Coord(i,j));
			}
		}
		i=x;
		j=y;
		ok = true;
		while (ok && i<8 && j<8){
			i++;
			j++;
			if (!isOccuped(new Coord(i, j)))
				coups.add(new Coord(i, j));
			else {
				ok = false;
				if (etatPlateau.get(new Coord(i,j).toString()).getColor() != fou.getColor())
					coups.add(new Coord(i,j));
			}
		}
	}
	private void coupReine(ArrayList<Coord> coups, IPiece reine){
		this.coupTour(coups, reine);
		this.coupFou(coups, reine);
	}
	
	private void coupCavalier(ArrayList<Coord> coups, IPiece cavalier){
		int x = cavalier.getCoord().getX();
		int y = cavalier.getCoord().getY();
		
		char color = cavalier.getColor();

		int i,j;

		i = x-2;
		j = y-1;
		coupCavalierAux(coups, i, j, color);
		i = x+2;
		j = y-1;
		coupCavalierAux(coups, i, j, color);
		i = x-2;
		j = y+1;
		coupCavalierAux(coups, i, j, color);
		i = x+2;
		j = y+1;
		coupCavalierAux(coups, i, j, color);
		
		i = x-1;
		j = y-2;
		coupCavalierAux(coups, i, j, color);
		i = x+1;
		j = y-2;
		coupCavalierAux(coups, i, j, color);
		i = x-1;
		j = y+2;
		coupCavalierAux(coups, i, j, color);
		i = x+1;
		j = y+2;
		coupCavalierAux(coups, i, j, color);
		
	}
	private boolean isInPlateau(int x){
		return x<=8 && x>=1;
	}
	private void coupCavalierAux (ArrayList<Coord> coups, int i, int j, char color){
		if (isInPlateau(i)&&isInPlateau(j)){
			if (!isOccuped(new Coord(i, j)))
				coups.add(new Coord(i, j));
			else {
				if (etatPlateau.get(new Coord(i,j).toString()).getColor() != color)
					coups.add(new Coord(i,j));
			}
		}
	}
	
	private void coupRoi (ArrayList<Coord> coups, IPiece pion){
		
	}
	
	@Override
	public void deplacerPiece(Coord positionDepart, Coord positionArrivee) {
		
		//System.out.println("piece a bouger = "+etatPlateau.get(positionDepart.toString()).toString());
		
		IPiece pieceMove = etatPlateau.get(positionDepart.toString());
		
		etatPlateau.remove(pieceMove.getCoord().toString());
		pieceMove.deplacer(positionArrivee);
		if (isOccuped(positionArrivee))
			etatPlateau.remove(positionArrivee.toString());
		etatPlateau.put(pieceMove.getCoord().toString(), pieceMove);
		
		if (testPetitRock(pieceMove, positionDepart, positionArrivee)){
			 
		}
		else if (testGrandRock(pieceMove, positionDepart, positionArrivee)){
			
		}
		
		tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir ;
		
		//System.out.println("pos dep = "+etatPlateau.get(positionDepart.toString()));
		System.out.println("pos arr = "+etatPlateau.get(positionArrivee.toString()).toString());
		System.out.println();
		
		draw();
	}
	private boolean testPetitRock(IPiece roi, Coord dep, Coord arr){
		boolean sol = roi.getType().equals(ToolsModel.roi) && (
				(
					roi.getColor()== ToolsModel.blanc && 
					dep.equals(new Coord(1, 5)) && 
					arr.equals(new Coord(1, 3)) &&
					
					etatPlateau.get(new Coord(1, 8).toString()).getType().equals(ToolsModel.tour) &&
					! etatPlateau.get(new Coord(1, 8).toString()).getDejaBouge()
				) ||
				(
					roi.getColor()== ToolsModel.noir && 
					dep.equals(new Coord(8, 5)) && 
					arr.equals(new Coord(8, 3))
				)
			);
				
		return sol;
	}
	private boolean testGrandRock(IPiece roi, Coord dep, Coord arr){
		boolean sol = roi.getType().equals(ToolsModel.roi) && (
				(
					roi.getColor()== ToolsModel.blanc && 
					dep.equals(new Coord(1, 5)) && 
					arr.equals(new Coord(1, 3))
				) ||
				(
					roi.getColor()== ToolsModel.noir && 
					dep.equals(new Coord(8, 5)) && 
					arr.equals(new Coord(8, 3))
				)
			);
				
		return sol;
	}
	
	public boolean isEnEchec (char kingColor, Map<String, IPiece> plateau/*, Coord kingPos*/){
		
		Coord kingPos = (kingColor == ToolsModel.blanc) ? (new Coord(posRoiBlanc)) : (new Coord(posRoiNoir)) ;
		//System.out.println("kingPos = "+kingPos.toString());
		
		ArrayList<Coord> casesEnDanger = new ArrayList<Coord>();
		
		for (Entry<String, IPiece> c : plateau.entrySet()) {
			IPiece p = c.getValue();
			Coord coord = new Coord(c.getKey());

			tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir;
			casesEnDanger = mouvementPossiblesAux(coord, false);
			tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir;
			
			if (casesEnDanger != null) {
				for(Coord d : casesEnDanger){
					if (d.equals(kingPos))
						return true;
				}
			}
			
		}
		
		return false;
	}
	
	private ArrayList<Coord> validationCoups (ArrayList<Coord> coups, Coord depart){
		ArrayList<Coord> coupsValide = new ArrayList<Coord>();
		
		Map<String, IPiece> etatPlateauClone = new HashMap<String, IPiece>(etatPlateau);
		char myKing = (tourDeJoueur == ToolsModel.noir) ? (ToolsModel.noir) : (ToolsModel.blanc) ;
		IPiece pieceMove = new Piece(etatPlateau.get(depart.toString()));

		for (Coord c : coups) {
			//simule le deplacement
			etatPlateauClone.remove(depart.toString());
			pieceMove.deplacer(depart);
			if (isOccuped(c))
				etatPlateauClone.remove(c.toString());
			etatPlateauClone.put(c.toString(),pieceMove);
			//tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir;
			// test si le depacement laisse son roi en echec
			if (! isEnEchec(myKing, etatPlateauClone))
				coupsValide.add(c);
			
			// reinitialisation des clones
			etatPlateauClone = new HashMap<String, IPiece>(etatPlateau);
			pieceMove = new Piece(etatPlateau.get(depart.toString()));
			//tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir;
		}
		System.out.println("coup valide = "+coupsValide.toString());
		return coupsValide;		
	}
	
	

	@Override
	public IPiece getPiece(Coord position) {
		return etatPlateau.get(position.toString());
	}
	
	private void initPlateau(){
		etatPlateau = new HashMap<String, IPiece>();

		etatPlateau.put(new Coord(1, 1).toString(), new Tour	(new Coord(1, 1), ToolsModel.blanc));
		etatPlateau.put(new Coord(1, 2).toString(), new Cavalier(new Coord(1, 2), ToolsModel.blanc));
		etatPlateau.put(new Coord(1, 3).toString(), new Fou		(new Coord(1, 3), ToolsModel.blanc));
		etatPlateau.put(new Coord(1, 4).toString(), new Reine	(new Coord(1, 4), ToolsModel.blanc));
		etatPlateau.put(new Coord(1, 5).toString(), new Roi		(new Coord(1, 5), ToolsModel.blanc));
		etatPlateau.put(new Coord(1, 6).toString(), new Fou		(new Coord(1, 6), ToolsModel.blanc));
		etatPlateau.put(new Coord(1, 7).toString(), new Cavalier(new Coord(1, 7), ToolsModel.blanc));
		etatPlateau.put(new Coord(1, 8).toString(), new Tour	(new Coord(1, 8), ToolsModel.blanc));
		
		posRoiBlanc = new Coord(1, 5).toString();

		etatPlateau.put(new Coord(8, 1).toString(), new Tour	(new Coord(8, 1), ToolsModel.noir));
		etatPlateau.put(new Coord(8, 2).toString(), new Cavalier(new Coord(8, 2), ToolsModel.noir));
		etatPlateau.put(new Coord(8, 3).toString(), new Fou		(new Coord(8, 3), ToolsModel.noir));
		etatPlateau.put(new Coord(8, 4).toString(), new Reine	(new Coord(8, 4), ToolsModel.noir));
		etatPlateau.put(new Coord(8, 5).toString(), new Roi		(new Coord(8, 5), ToolsModel.noir));
		etatPlateau.put(new Coord(8, 6).toString(), new Fou		(new Coord(8, 6), ToolsModel.noir));
		etatPlateau.put(new Coord(8, 7).toString(), new Cavalier(new Coord(8, 7), ToolsModel.noir));
		etatPlateau.put(new Coord(8, 8).toString(), new Tour	(new Coord(8, 8), ToolsModel.noir));
		
		posRoiNoir = new Coord(8, 5).toString();
		
		for (int i = 1; i < 9; i++){
			etatPlateau.put(new Coord(2, i).toString(), new Pion(new Coord(2, i), ToolsModel.blanc));
			etatPlateau.put(new Coord(7, i).toString(), new Pion(new Coord(7, i), ToolsModel.noir));
		}
	}
	
	/**
	 * Renvoi en console une vue minimale de l'echiquier
	 */
	public void draw (){
		String draw = "";
		
		for (int i=8; i>0; i--){
			draw += i+"  ";
			for (int j=1; j<9; j++){
				if (isOccuped(new Coord(i, j)))
					draw += etatPlateau.get(new Coord(i, j).toString()).getDraw();
				else 
					draw += "...";
				draw += " ";
			}
			draw += "\n";
		}
		draw += "   "+" A  "+" B  "+" C  "+" D  "+" E  "+" F  "+" G  "+" H  ";
		draw += "\n";
		
		System.out.println(draw);
	}

}
