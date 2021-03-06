package mmm.jeu.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mmm.EchecsAR.Adapter;
import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;
import mmm.jeu.model.interfaces.IPiece;
import mmm.jeu.model.pieces.*;

public class CEchiquier implements ICEchiquier {

	private Map<String, IPiece> etatPlateau;
	private String posRoiBlanc;
	private String posRoiNoir;
	
	private Adapter myAdapter;
	
	private char tourDeJoueur = ToolsModel.blanc;
	private int origineLineLastMoved = 0;
	private IPiece lastPieceMoved = null;
	
	public CEchiquier(Adapter myAdapter){
		initPlateau();
		this.myAdapter = myAdapter;
		/*for (Entry<String, IPiece> c : etatPlateau.entrySet()) {
			System.out.println(c.getKey().toString());
			System.out.println(((Piece)c.getValue()).toString());
		}
		
		System.out.println(etatPlateau.toString());*/
	}
	
	/**
	 * Params : CoordonnÃˆes de la case â€¡ tester
	 * Retour : Indique si la case dont les coord sont passÃˆes en param est occupÃˆe
	 */
	@Override
	public boolean isOccuped(Coord position) {
		return etatPlateau.get(position.toString())!=null;
	}

	@Override
	public char getTourDeJoueur() {
		return tourDeJoueur;
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
		
		if (validation)
			mvt = validationCoups(mvt, coordonneePiece);
		
		return mvt;
	}

	@Override
	public void deplacerPiece(Coord positionDepart, Coord positionArrivee) {
		
		IPiece pieceMove = etatPlateau.get(positionDepart.toString());

		if (testPetitRock(pieceMove, positionDepart, positionArrivee)){
			 IPiece tour = etatPlateau.get(new Coord(positionArrivee.getX(), 8).toString());
			 
			 etatPlateau.remove(tour.getCoord().toString());
			 tour.deplacer(new Coord(positionArrivee.getX(),6));
			 etatPlateau.put(tour.getCoord().toString(), tour);
			 myAdapter.movePiece(positionArrivee.getX(), 8, positionArrivee.getX(), 6);
			 
			 if (tourDeJoueur == ToolsModel.blanc) {
				 myAdapter.displayMessage("White player has made a small rock");
			 } else {
				 myAdapter.displayMessage("Black player has made a small rock");

			 }
		}
		else if (testGrandRock(pieceMove, positionDepart, positionArrivee)){
			 IPiece tour = etatPlateau.get(new Coord(positionArrivee.getX(), 1).toString());
			 
			 etatPlateau.remove(tour.getCoord().toString());
			 tour.deplacer(new Coord(positionArrivee.getX(),4));
			 etatPlateau.put(tour.getCoord().toString(), tour);
			 myAdapter.movePiece(positionArrivee.getX(), 1, positionArrivee.getX(), 4);
			 if (tourDeJoueur == ToolsModel.blanc) {
				 myAdapter.displayMessage("White player has made a big rock");
			 } else {
				 myAdapter.displayMessage("Black player has made a big rock");

			 }
		}

		// prise au passage , destruction du pion adverse
		if (pieceMove.getType().equals(ToolsModel.pion) && testPrisePassage(positionDepart)){
			etatPlateau.remove(lastPieceMoved.getCoord().toString());
			myAdapter.killPiece(lastPieceMoved.getCoord());
			 myAdapter.displayMessage("En passant capture");

		}
		
		etatPlateau.remove(pieceMove.getCoord().toString());
		pieceMove.deplacer(positionArrivee);
		if (isOccuped(positionArrivee))
			etatPlateau.remove(positionArrivee.toString());
		etatPlateau.put(pieceMove.getCoord().toString(), pieceMove);
		
		// promotion du pion
		if (pieceMove.getType().equals(ToolsModel.pion) && (positionArrivee.getX() == 1 || positionArrivee.getX() == 8)){
			IPiece newPiece;
			int pieceType = myAdapter.promotion();
			switch (pieceType) {
			case ToolsModel.promotionTour :
				newPiece = new Tour(positionArrivee, pieceMove.getColor()) ;
				break;
			case ToolsModel.promotionCavalier :
				newPiece = new Cavalier(positionArrivee, pieceMove.getColor()) ;
				break;
			case ToolsModel.promotionFou :
				newPiece = new Fou(positionArrivee, pieceMove.getColor()) ;
				break;
			case ToolsModel.promotionReine :
				newPiece = new Reine(positionArrivee, pieceMove.getColor()) ;
				break;
			default :
				newPiece = pieceMove;
			}
			
			etatPlateau.remove(pieceMove.getCoord().toString());
			etatPlateau.put(newPiece.getCoord().toString(), newPiece);
			
			myAdapter.replace(positionDepart, pieceType);
			 myAdapter.displayMessage("The pawn has been promoted as a Queen");

		}
		
		// maj info pour prise au passage
		lastPieceMoved = new Piece(pieceMove);
		origineLineLastMoved = positionDepart.getX();
				
		// changement de joueur
		tourDeJoueur = (tourDeJoueur == ToolsModel.noir) ? ToolsModel.blanc : ToolsModel.noir ;
		
		// maj pos roi
		if (pieceMove.getType().equals(ToolsModel.roi)){
			if (pieceMove.getColor() == ToolsModel.blanc)
				posRoiBlanc = pieceMove.getCoord().toString();
			else
				posRoiNoir = pieceMove.getCoord().toString();
		}
		
		// verif echec ou echec et mat adversaire
		VerifCheackMat();
		
		//affichage de debug
		System.out.println("pos arr = "+etatPlateau.get(positionArrivee.toString()).toString());
		System.out.println();
		draw();
	}
	
	/**
	 * 
	 * Rmq  : la prise au passage n'est pas implementÃˆe
	 * Rmq2 : normalement pas d'erreur au bord du plateau si la promotion est bien implementÃˆe
	 * @param coups
	 * @param pion
	 */
	private void coupPion(ArrayList<Coord> coups, IPiece pion){
		int x = pion.getCoord().getX();
		int y = pion.getCoord().getY();
		if (x<8 && 1<x){
			if (pion.getColor() == ToolsModel.blanc)
			{	
				if (!isOccuped(new Coord(x+1, y)))
					coups.add(new Coord(x+1, y));
				if (!pion.getDejaBouge()&&!isOccuped(new Coord(x+1, y))&&!isOccuped(new Coord(x+2, y)))
					coups.add(new Coord(x+2, y));
				
				// Attention , pour les 2 cas suivants , Ã�a marche car on test la presence AVANT
				// si on inverse les 2 elements du test on obtiendra un nullpointer !!
				if (isOccuped(new Coord(x+1, y+1)) && (etatPlateau.get(new Coord(x+1, y+1).toString()).getColor()!= pion.getColor()))
					coups.add(new Coord(x+1, y+1));
				if (isOccuped(new Coord(x+1, y-1)) && (etatPlateau.get(new Coord(x+1, y-1).toString()).getColor()!= pion.getColor()))
					coups.add(new Coord(x+1, y-1));
				
				// coups de la prise au passage
				if (x==5 && testPrisePassage(pion.getCoord())){
					coups.add(new Coord(6, lastPieceMoved.getCoord().getY()));
				}
			}
			else 
			{
				if (!isOccuped(new Coord(x-1, y)))
					coups.add(new Coord(x-1, y));
				if (!pion.getDejaBouge()&&!isOccuped(new Coord(x-1, y))&&!isOccuped(new Coord(x-2, y)))
					coups.add(new Coord(x-2, y));
				
				// Attention , pour les 2 cas suivants , Ã�a marche car on test la presence AVANT
				// si on inverse les 2 elements du test on obtiendra un nullpointer !!
				if (isOccuped(new Coord(x-1, y+1)) && (etatPlateau.get(new Coord(x-1, y+1).toString()).getColor()!= pion.getColor()))
					coups.add(new Coord(x-1, y+1));
				if (isOccuped(new Coord(x-1, y-1)) && (etatPlateau.get(new Coord(x-1, y-1).toString()).getColor()!= pion.getColor()))
					coups.add(new Coord(x-1, y-1));

				// coups de la prise au passage
				if (x==4 && testPrisePassage(pion.getCoord())){
					coups.add(new Coord(3, lastPieceMoved.getCoord().getY()));
				}
			}
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
		coupAux(coups, i, j, color);
		i = x+2;
		j = y-1;
		coupAux(coups, i, j, color);
		i = x-2;
		j = y+1;
		coupAux(coups, i, j, color);
		i = x+2;
		j = y+1;
		coupAux(coups, i, j, color);
		
		i = x-1;
		j = y-2;
		coupAux(coups, i, j, color);
		i = x+1;
		j = y-2;
		coupAux(coups, i, j, color);
		i = x-1;
		j = y+2;
		coupAux(coups, i, j, color);
		i = x+1;
		j = y+2;
		coupAux(coups, i, j, color);
		
	}
	private boolean isInPlateau(int x){
		return x<=8 && x>=1;
	}
	private void coupAux (ArrayList<Coord> coups, int i, int j, char color){
		if (isInPlateau(i)&&isInPlateau(j)){
			if (!isOccuped(new Coord(i, j)))
				coups.add(new Coord(i, j));
			else {
				if (etatPlateau.get(new Coord(i,j).toString()).getColor() != color)
					coups.add(new Coord(i,j));
			}
		}
	}
	private void coupRoi (ArrayList<Coord> coups, IPiece roi){
		int x = roi.getCoord().getX();
		int y = roi.getCoord().getY();

		coupAux(coups, (x+1), y, roi.getColor());
		coupAux(coups, (x-1), y, roi.getColor());

		coupAux(coups, (x+1), (y+1), roi.getColor());
		coupAux(coups, (x+1), (y-1), roi.getColor());
		coupAux(coups, (x-1), (y+1), roi.getColor());
		coupAux(coups, (x-1), (y-1), roi.getColor());
		
		coupAux(coups, x, (y+1), roi.getColor());
		coupAux(coups, x, (y-1), roi.getColor());
	
		if (!roi.getDejaBouge()){
			if (testPetitRock(roi, roi.getCoord(), new Coord(x, y+2)))
				coupAux(coups, x, y+2, roi.getColor());
			if (testGrandRock(roi, roi.getCoord(), new Coord(x, y-2)))
				coupAux(coups, x, y-2, roi.getColor());
		}
	}
	
	private boolean testPetitRock(IPiece roi, Coord dep, Coord arr){
		
		int x = (roi.getColor()== ToolsModel.blanc) ? 1 : 8;
		
		if (etatPlateau.get(new Coord(x, 8).toString()) == null)
			return false;
				
		boolean sol = roi.getType().equals(ToolsModel.roi) && ! roi.getDejaBouge() && (
				(
					dep.equals(new Coord(x, 5)) && 
					arr.equals(new Coord(x, 7)) &&
					
					etatPlateau.get(new Coord(x, 8).toString()).getType().equals(ToolsModel.tour) &&
					! etatPlateau.get(new Coord(x, 8).toString()).getDejaBouge()
				) &&
				(!isOccuped(new Coord(x, 6))) && (!isOccuped(new Coord(x, 7)))
				
				&& testRockNoEchec(new Coord(x, 6))
				&& testRockNoEchec(new Coord(x, 7))
			);
				
		return sol;
	}
	private boolean testGrandRock(IPiece roi, Coord dep, Coord arr){
		
		int x = (roi.getColor()== ToolsModel.blanc) ? 1 : 8;
		
		if (etatPlateau.get(new Coord(x, 1).toString()) == null)
			return false;
		
		boolean sol = roi.getType().equals(ToolsModel.roi) && ! roi.getDejaBouge() && (
				(
					dep.equals(new Coord(x, 5)) && 
					arr.equals(new Coord(x, 3)) &&
					
					etatPlateau.get(new Coord(x, 1).toString()).getType().equals(ToolsModel.tour) &&
					! etatPlateau.get(new Coord(x, 1).toString()).getDejaBouge()
				) &&
				(!isOccuped(new Coord(x, 2))) && (!isOccuped(new Coord(x, 3))) && (!isOccuped(new Coord(x, 4)))
				
				&& testRockNoEchec(new Coord(x, 2))
				&& testRockNoEchec(new Coord(x, 3))
				&& testRockNoEchec(new Coord(x, 4))
			);
				
		return sol;
	}
	private boolean testRockNoEchec(Coord passage){
		Coord start = new Coord(passage.getX(), 5);
		boolean pasEchec = true;
		
		Map<String, IPiece> etatPlateauSave = new HashMap<String, IPiece>(etatPlateau);
		char myKing = (tourDeJoueur == ToolsModel.noir) ? (ToolsModel.noir) : (ToolsModel.blanc) ;
		IPiece pieceMove = new Piece(etatPlateau.get(start.toString()));
				
		String savePosBlanc = posRoiBlanc;
		String savePosNoir = posRoiNoir;
		
		etatPlateau.remove(start.toString());
		pieceMove.deplacer(passage);
		if (isOccuped(passage))
			etatPlateau.remove(passage.toString());
		etatPlateau.put(passage.toString(),pieceMove);
		
		if (myKing == ToolsModel.blanc)
			posRoiBlanc = passage.toString();
		else
			posRoiNoir = passage.toString();
		
		pasEchec = !(isEnEchec(myKing));
		
		posRoiBlanc = savePosBlanc;
		posRoiNoir = savePosNoir;
		etatPlateau = new HashMap<String, IPiece>(etatPlateauSave);
		pieceMove = new Piece(etatPlateau.get(start.toString()));
		
		
		return pasEchec;
	}
	
	private boolean testPrisePassage(Coord dep){
		boolean priseOK = false;
		
		boolean isWhite = (tourDeJoueur == ToolsModel.blanc);
		if (lastPieceMoved != null && lastPieceMoved.getType().equals(ToolsModel.pion)){
			if (isWhite && lastPieceMoved.getCoord().getX() == 5 && origineLineLastMoved == 7 &&
					!isOccuped(new Coord(lastPieceMoved.getCoord().getX()+1, lastPieceMoved.getCoord().getY()))){
				if (dep.getX() == 5 && 
						(dep.getY() == lastPieceMoved.getCoord().getY()-1 || dep.getY() == lastPieceMoved.getCoord().getY()+1)){
					return etatPlateau.get(dep.toString()).getType().equals(ToolsModel.pion);
				}
			}
			else if (!isWhite && lastPieceMoved.getCoord().getX() == 4 && origineLineLastMoved == 2 &&
					!isOccuped(new Coord(lastPieceMoved.getCoord().getX()-1, lastPieceMoved.getCoord().getY()))){
				if (dep.getX() == 4 && 
						(dep.getY() == lastPieceMoved.getCoord().getY()-1 || dep.getY() == lastPieceMoved.getCoord().getY()+1)){
					return etatPlateau.get(dep.toString()).getType().equals(ToolsModel.pion);
				}
			}
		}
		
		return priseOK;
	}
	
	public boolean isEnEchec (char kingColor){
		
		Map<String, IPiece> clone = new HashMap<String, IPiece>(etatPlateau);
		
		Coord kingPos = (kingColor == ToolsModel.blanc) ? (new Coord(posRoiBlanc)) : (new Coord(posRoiNoir)) ;
		
		ArrayList<Coord> casesEnDanger = new ArrayList<Coord>();
		
		for (Entry<String, IPiece> c : clone.entrySet()) {
			//IPiece p = c.getValue();
			Coord coord = new Coord(c.getKey());

			//recuperation des coups de l'adversaire
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
	
	private void VerifCheackMat (){
		
		Map<String, IPiece> etatPlateauSave = new HashMap<String, IPiece>(etatPlateau);
		
		boolean echec = isEnEchec(tourDeJoueur);
		
		if (echec){
			boolean mat = true;
			for (Entry<String, IPiece> c : etatPlateauSave.entrySet()) {
				IPiece piece = c.getValue();
				if (piece.getColor() == tourDeJoueur){
					ArrayList<Coord> coups = mouvementPossibles(piece.getCoord());
					mat = mat && coups.size() == 0;
				}
				
			}
			String joueur = "Black";
			if (tourDeJoueur == ToolsModel.blanc) {
				joueur = "White";
			}
			if (!mat)
				// action lors de l'echec
				myAdapter.displayMessage(joueur+" player is in check");
			else
				// action lors du mat
				myAdapter.displayMessage(joueur+" player is in checkmate");
		}
		
	}
	
	/**
	 * Cette fonction renvoi la liste des coups valide , c'est â€¡ dire que le roi ne soit pas en echec aprÃ‹s le mouvement
	 * @param coups : liste des coups a valider
	 * @param depart : position de la piece a bouger
	 * @return la liste des coups validÃˆs
	 */
	private ArrayList<Coord> validationCoups (ArrayList<Coord> coups, Coord depart){
		ArrayList<Coord> coupsValide = new ArrayList<Coord>(coups);
		
		Map<String, IPiece> etatPlateauSave = new HashMap<String, IPiece>(etatPlateau);
		char myKing = (tourDeJoueur == ToolsModel.noir) ? (ToolsModel.noir) : (ToolsModel.blanc) ;
		IPiece pieceMove = new Piece(etatPlateau.get(depart.toString()));
				
		String savePosBlanc = posRoiBlanc;
		String savePosNoir = posRoiNoir;
			

		for (Coord c : coups) {
			
			//simule le deplacement
			etatPlateau.remove(depart.toString());
			pieceMove.deplacer(c);
			if (isOccuped(c))
				etatPlateau.remove(c.toString());
			etatPlateau.put(c.toString(),pieceMove);
			
			if(pieceMove.getType().equals(ToolsModel.roi)){
				if (myKing == ToolsModel.blanc)
					posRoiBlanc = c.toString();
				else
					posRoiNoir = c.toString();
			}
			
			// test si le depacement laisse son roi en echec
			if (isEnEchec(myKing))
				coupsValide.remove(c);
			
			// annulation de la simulation
			posRoiBlanc = savePosBlanc;
			posRoiNoir = savePosNoir;
			etatPlateau = new HashMap<String, IPiece>(etatPlateauSave);
			pieceMove = new Piece(etatPlateau.get(depart.toString()));
		}
		return coupsValide;		
	}
	
	
	/**
	 * Getter da la piece qui se situe au coordonnÃˆes passÃˆes en parametre
	 */
	@Override
	public IPiece getPiece(Coord position) {
		return etatPlateau.get(position.toString());
	}
	
	/**
	 * Cette fonction initialise la Map qui contient l'etat du plateau
	 */
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
