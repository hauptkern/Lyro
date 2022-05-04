package com.hauptkern.lyro.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hauptkern.lyro.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<ArrayList> wlinks=new ArrayList<>();
    public search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search.
     */
    // TODO: Rename and change types and number of parameters
    public static search newInstance(String param1, String param2) {
        search fragment = new search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        SearchView searchbar=(SearchView) view.findViewById(R.id.search_bar);
        ListView searchtable= (ListView) view.findViewById(R.id.search_table);
        searchbar.setFocusable(false);
        searchbar.setIconified(false);
        searchbar.clearFocus();
        searchtable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tview=(TextView) view;
                String selectedSong=tview.getText().toString();
                Callable<String> findlyricsLink=()->{
                    String urlAppend ="";
                    for(ArrayList item:wlinks){
                        String songname=item.get(0).toString();
                        if (songname.equals(selectedSong)){
                            urlAppend=item.get(1).toString();
                            return urlAppend;
                        }
                    }
                    return urlAppend;
                };
                ExecutorService get_lyricsUrl_thread = Executors.newSingleThreadExecutor();
                Future<String> urlAppend_out=get_lyricsUrl_thread.submit(findlyricsLink);
                Callable<ArrayList> getlyrics=() ->{
                    ArrayList<String> pars = new ArrayList();
                    try{
                        String urlAppend=urlAppend_out.get().toString();
                        String url = "https://lyricstranslate.com"+urlAppend;
                        Document doc = Jsoup.connect(url).get();
                        Element song_body=doc.select("div.direction-ltr").first();
                        Elements table=song_body.select("div.par");
                        for(Element x:table){
                            StringBuilder par=new StringBuilder();
                            Integer count=0;
                            for(Element row:x.select("div")){
                               if (count==0){
                                   count+=1;
                                   continue;
                               }
                               else{
                                   par.append(row.text()+"\n");
                               }
                            }
                            pars.add(par.toString());
                        }
                        pars.add("\n\n\n");
                        return pars;
                    }
                    catch (Exception e){
                        System.out.println(e.toString());
                    }
                    return pars;
                };
                ExecutorService get_lyricsArray_thread = Executors.newSingleThreadExecutor();
                Future<ArrayList> lyrics_out=get_lyricsArray_thread.submit(getlyrics);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            ListView music_table =getActivity().findViewById(R.id.lyrics_table);
                            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,lyrics_out.get());
                            music_table.setAdapter(adapter);
                            View view = getActivity().findViewById(R.id.nav_music);
                            view.performClick();
                        }
                        catch (Exception e){

                        }
                    }
                });
            }
        });
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Callable<ArrayList> searchResults = () -> {
                    ArrayList<String> out = new ArrayList<>();
                    try {
                        String url = "https://lyricstranslate.com/tr/songs/0/none/" + s + "/0";
                        url = url.replaceAll(" ", "%20");
                        Document doc = Jsoup.connect(url).get();
                        Element table = doc.select("table.sticky_table").first();
                        Elements rows = doc.select("tr");
                        for (Element row : rows) {
                            String row_str = row.select("td.ltsearch-artisttitle").text() + " | " + row.select("td.ltsearch-songtitle").text();
                            if (row_str.length()>3){
                                out.add(row_str);
                                ArrayList<String> songdata=new ArrayList<>();
                                songdata.add(0,row_str);
                                songdata.add(1,row.select("td.ltsearch-songtitle").select("a").first().attr("href"));
                                wlinks.add(songdata);
                            }
                        }
                        return out;
                    }
                    catch(Exception e){
                        System.out.println(e.toString());
                        }
                    return null;
                };
                ExecutorService get_searchResults_thread = Executors.newSingleThreadExecutor();
                Future<ArrayList> searchResults_out=get_searchResults_thread.submit(searchResults);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> results = searchResults_out.get();
                            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,results);
                            searchtable.setAdapter(adapter);
                        }
                        catch (Exception e){
                            System.out.println(e.toString());
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    }
