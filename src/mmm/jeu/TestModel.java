package mmm.jeu;

import java.util.ArrayList;

import mmm.jeu.control.CEchiquier;
import mmm.jeu.control.ICEchiquier;
import mmm.jeu.model.Coord;

public class TestModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ICEchiquier ech = new CEchiquier();
		
		ech.draw();
		
		System.out.println();
		
		System.out.println("[2,2] occupé ? "+ech.isOccuped(new Coord(2,2)));
		System.out.println("[5,5] occupé ? "+ech.isOccuped(new Coord(5,5))+"\n");
		
		// test mvt
		ArrayList<Coord> test = new ArrayList<Coord>();
		
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
		
	}

}
