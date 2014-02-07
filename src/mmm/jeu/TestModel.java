package mmm.jeu;

import java.util.ArrayList;

import mmm.EchecsAR.Adapter;
import mmm.jeu.control.CEchiquier;
import mmm.jeu.control.ICEchiquier;
import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;

public class TestModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Adapter adapteur = new Adapter() {
			
			@Override
			public void movePiece(int fromrow, int fromcol, int torow, int tocol) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void displayMessage(String message) {
				System.out.println(message);
			}

			@Override
			public int promotion() {
				// TODO Auto-generated method stub
				return ToolsModel.promotionReine;
			}

			@Override
			public void replace(Coord coordPion, int pieceType) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void killPiece(Coord coordPiece) {
				// TODO Auto-generated method stub
				
			}
		};
		ICEchiquier ech = new CEchiquier(adapteur);
		CEchiquier echImpl = (CEchiquier)ech;
		
		ech.draw();
		
		System.out.println();
		
		System.out.println("[2,2] occupÈ ? "+ech.isOccuped(new Coord(2,2)));
		System.out.println("[5,5] occupÈ ? "+ech.isOccuped(new Coord(5,5))+"\n");
		
		// test mvt
		ArrayList<Coord> test = new ArrayList<Coord>();
		
		test = ech.mouvementPossibles(new Coord(1,2));
		if (test != null)
			System.out.println("cavalier mouv ?"+ test.toString());

		ech.deplacerPiece(new Coord(2,5) , new Coord(5,5));
		ech.deplacerPiece(new Coord(7,4) , new Coord(5,4));
		
		test = ech.mouvementPossibles(new Coord(5,5));
		System.out.println("prise ? "+test.toString());
		
		ech.deplacerPiece(new Coord(5,5), new Coord(6,4));
		
		
		
		/*System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(2,5), new Coord(4,5));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(7,5), new Coord(5,5));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(1,6), new Coord(3,8));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(7,2), new Coord(6,2));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(1,7), new Coord(3,6));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(8,3), new Coord(6,1));*/
		/*

		ech.deplacerPiece(new Coord(2, 1), new Coord(8, 1));
		ech.deplacerPiece(new Coord(7, 1), new Coord(6, 1));
		
		

		// pion blanc
		ech.deplacerPiece(new Coord(2,5), new Coord(4,5));
		//pion noir
		ech.deplacerPiece(new Coord(7,5), new Coord(5,5));
		// cavalier blanc
		ech.deplacerPiece(new Coord(1,7), new Coord(3,6));
		//pion noir
		ech.deplacerPiece(new Coord(7,4), new Coord(6,4));
		// cavalier blanc
		ech.deplacerPiece(new Coord(1,2), new Coord(3,3));
		// fou noir
		ech.deplacerPiece(new Coord(8,3), new Coord(4,7));
		//fou blanc 
		ech.deplacerPiece(new Coord(1,6), new Coord(4,3));
		// pion noir 
		ech.deplacerPiece(new Coord(7,8), new Coord(6,8));
		// cavalier blanc
		ech.deplacerPiece(new Coord(3,6), new Coord(5,5));
		// fou noir prend la reine
		ech.deplacerPiece(new Coord(4,7), new Coord(1,4));
		// fou blanc met roi en echec
		ech.deplacerPiece(new Coord(4,3), new Coord(7,6));
		// roi escape
		ech.deplacerPiece(new Coord(8,5), new Coord(7,5));
		// cavalier met echec et mat
		ech.deplacerPiece(new Coord(3,3), new Coord(5,4));*/
		
		
		
		//ech.draw();

		/*System.out.println("au tour de "+echImpl.tourDeJoueur);
		
		test = ech.mouvementPossibles(new Coord(1,5));
		if (test != null)
			System.out.println("Mouvement roi ? = "+test.toString());
		
		ech.deplacerPiece(new Coord(1,4), new Coord(3,7));
		
		ech.deplacerPiece(new Coord(8,4), new Coord(2,5));
		
		//ech.deplacerPiece(new Coord(1,5), new Coord(1,7));
		

		//System.out.println("blanc en echec ? "+((CEchiquier)ech).isEnEchec(ToolsModel.blanc));//, ((CEchiquier)ech).etatPlateau));
		//System.out.println("noir en echec ? "+((CEchiquier)ech).isEnEchec(ToolsModel.noir));//, ((CEchiquier)ech).etatPlateau));
		
		/*System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(2, 4), new Coord(4, 4));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(7, 5), new Coord(5, 5));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(4, 4), new Coord(5, 5));
		System.out.println("au tour de "+echImpl.tourDeJoueur);
		ech.deplacerPiece(new Coord(8, 6), new Coord(4, 2));
		System.out.println("au tour de "+echImpl.tourDeJoueur);*/


		/*test = ech.mouvementPossibles(new Coord(5,5));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,1));
		if (test != null)
		System.out.println("tour mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,2));
		if (test != null)
		System.out.println("cavalier mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,3));
		if (test != null)
		System.out.println("fou mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,4));
		if (test != null)
		System.out.println("reine mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,5));
		if (test != null)
		System.out.println("roi mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,6));
		if (test != null)
		System.out.println("fou mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,7));
		if (test != null)
		System.out.println("cavalier mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(1,8));
		if (test != null)
		System.out.println("tour mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		
		test = ech.mouvementPossibles(new Coord(2,1));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(2,2));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(2,3));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(2,5));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(2,6));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(2,7));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		test = ech.mouvementPossibles(new Coord(2,8));
		if (test != null)
		System.out.println("pion mouv ?"+ test.toString());
		else 
			System.out.println("Pas de MVT");
		
		ech.draw();
		
		test = ech.mouvementPossibles(new Coord(4,2));*/
		
		//System.out.println("coup du fou = "+test.toString());
		/*
		
		// test mvt pion
		test = ech.mouvementPossibles(new Coord(7, 5));
		System.out.println("pion mouv ?"+ test.toString());
		System.out.println("mouvement pion");
		ech.deplacerPiece(new Coord(7, 5), new Coord(3, 5));
		
		test = ech.mouvementPossibles(new Coord(3, 5));
		System.out.println("pion mouv ?"+ test.toString()+"\n");

		// test mvt tour
		test = ech.mouvementPossibles(new Coord(8, 8));
		System.out.println("tour mouv ?"+ test.toString());
		System.out.println("mouvement tour");
		ech.deplacerPiece(new Coord(8, 8), new Coord(5,1));
		
		test = ech.mouvementPossibles(new Coord(5,1));
		System.out.println("tour mouv ?"+ test.toString()+"\n");
		
		// test mvt fou
		test = ech.mouvementPossibles(new Coord(8, 3));
		System.out.println("fou mouv ?"+ test.toString());
		System.out.println("mouvement fou");
		ech.deplacerPiece(new Coord(8, 3), new Coord(5,4));
		
		test = ech.mouvementPossibles(new Coord(5,4));
		System.out.println("fou mouv ?"+ test.toString()+"\n");

		// test mvt reine
		test = ech.mouvementPossibles(new Coord(8, 4));
		System.out.println("reine mouv ?"+ test.toString());
		System.out.println("mouvement reine");
		ech.deplacerPiece(new Coord(8, 4), new Coord(5,5));
		
		test = ech.mouvementPossibles(new Coord(5,5));
		System.out.println("reine mouv ?"+ test.toString()+"\n");

		// test mvt cavalier
		test = ech.mouvementPossibles(new Coord(8, 2));
		System.out.println("cavalier mouv ?"+ test.toString());
		System.out.println("mouvement cavalier");
		ech.deplacerPiece(new Coord(8, 2), new Coord(4,4));
		
		test = ech.mouvementPossibles(new Coord(4,4));
		System.out.println("cavalier mouv ?"+ test.toString()+"\n");
		
		
		ech.draw();
		
		ech.deplacerPiece(new Coord(3,5), new Coord(2, 4));
		ech.draw();*/

		//System.out.println("echec ? "+ ((CEchiquier)ech).isEnEchec('W'));
		//System.out.println("echec ? "+ ((CEchiquier)ech).isEnEchec('B'));
		/*test = ech.mouvementPossibles(new Coord(2, 4));
		System.out.println("mouv pion = "+test.toString());*/
		
		
		
		ech.draw();
		
	}

}
