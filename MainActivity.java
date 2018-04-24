package com.example.student.telefony;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter mAdapterKursora;
    private ListView mLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLista = (ListView) findViewById(R.id.lista_telefony);
        wypelnijListe();
        mLista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mLista.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater pompka = actionMode.getMenuInflater();
                pompka.inflate(R.menu.pasek_kontekstowy_listy, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.kasuj_menu:
                        kasujZaznaczone();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        mLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent zamiar = new Intent(MainActivity.this,EdycjaActivity.class);
                zamiar.putExtra(PomocnikBD.ID,id);
                startActivityForResult(zamiar,0);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.pasek_akcji,menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.dodaj) {
            Intent zamiar = new Intent(this,EdycjaActivity.class);
            zamiar.putExtra(PomocnikBD.ID,(long)-1);
            startActivityForResult(zamiar,0);
        }
        return super.onOptionsItemSelected(item);
    }


    private void kasujZaznaczone() {
        long zaznaczone[] = mLista.getCheckedItemIds();
        for (int i = 0; i < zaznaczone.length; ++i) {
            getContentResolver().delete(ContentUris.withAppendedId(TelefonyProvider.URI_ZAWARTOSCI, zaznaczone[i]), null, null);
        }
    }

    private void wypelnijListe() {
        getLoaderManager().initLoader(0, null, this);
        String[] mapujZ = new String[]{PomocnikBD.PRODUCENT,PomocnikBD.MODEL};
        int[] mapujDo = new int[]{R.id.producent_wartosc, R.id.model_wartosc};
        mAdapterKursora = new SimpleCursorAdapter(this,
                R.layout.wiersz_listy, null, mapujZ, mapujDo, 0);
        mLista.setAdapter(mAdapterKursora);
    }



    //implementacja LoaderCallbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projekcja = {PomocnikBD.ID, PomocnikBD.PRODUCENT,PomocnikBD.MODEL};
        CursorLoader loaderKursora = new CursorLoader(this,
                TelefonyProvider.URI_ZAWARTOSCI, projekcja, null, null, null);
        return loaderKursora;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor dane) {
        mAdapterKursora.swapCursor(dane);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapterKursora.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getLoaderManager().restartLoader(0,null,this);
    }
}

