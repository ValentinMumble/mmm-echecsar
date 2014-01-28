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
		System.out.println("[2,2] occupé ? "+ech.isOccuped(new Coord(2,2)));
		System.out.println("[5,5] occupé ? "+ech.isOccuped(new Coord(5,5)));
		
		// test mvt
		ArrayList<Coord> test = new ArrayList<Coord>();
		
		// test mvt pion
		test = ech.mouvementPossibles(new Coord(7, 5));
		System.out.println("pion mouv ?"+ test.toString());
		System.out.println("mouvement pion");
		ech.deplacerPiece(new Coord(7, 5), new Coord(3, 5));
		
		test = ech.mouvementPossibles(new Coord(3, 5));
		System.out.println("pion mouv ?"+ test.toString());

		// test mvt tour
		test = ech.mouvementPossibles(new Coord(8, 1));
		System.out.println("tour mouv ?"+ test.toString());
		System.out.println("mouvement tour");
		ech.deplacerPiece(new Coord(8, 1), new Coord(6,1));
		
		test = ech.mouvementPossibles(new Coord(6,1));
		System.out.println("tour mouv ?"+ test.toString());
	}

}
