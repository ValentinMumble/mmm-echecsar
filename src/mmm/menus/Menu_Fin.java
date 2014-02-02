package mmm.menus;

import mmm.EchecsAR.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Menu_Fin extends Activity{

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_fin_partie);
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	//((TextView)findViewById(R.id.clic_menu)).setText(“Fin de partie”);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu_fin_game, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.rejouer:
			//((TextView) findViewById(R.id.texte)).setText("Rejouer");
			return true;
		case R.id.menu_principal:
			//((TextView) findViewById(R.id.texte)).setText("Favoris");
			return true;
		case R.id.quitter:
			//((TextView) findViewById(R.id.texte)).setText("Stats");
			return true;
		}
    	
    	return false;
    }
}
