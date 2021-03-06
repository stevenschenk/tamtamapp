package com.example.steven.tamtam;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import com.annimon.stream.Stream;
import com.example.steven.tamtam.Adapters.ColleagueListAdapter;
import com.example.steven.tamtam.Fragments.MyListFragment;
import com.example.steven.tamtam.Httprequester.HttpParam;
import com.example.steven.tamtam.Httprequester.HttpParamManager;
import com.example.steven.tamtam.Httprequester.HttpRequestManager;
import com.example.steven.tamtam.Models.Colleague;
import com.example.steven.tamtam.Models.Person;
import com.example.steven.tamtam.Models.UserSession;
import com.example.steven.tamtam.apimanager.ModelParser;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import org.json.JSONArray;
import org.json.JSONException;




import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    boolean searchOpened = false;
    MenuItem searchItem;
    String searchQuery;
    Drawable iconOpenSearch;
    Drawable iconCloseSearch;
    ColleagueListAdapter adapterPlaying;
    ColleagueListAdapter adapterRookie;
    ColleagueListAdapter adapterAll;
    ArrayList<Person> personList = new ArrayList<>();
    ArrayList<Person> playingList = new ArrayList<>();
    ArrayList<Person> rookieList = new ArrayList<>();
    UserSession userSession;




    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userSession = new UserSession(getBaseContext());
        userSession.init();

        iconCloseSearch = getResources().getDrawable(R.drawable.close_icon);
        iconOpenSearch = getResources().getDrawable(R.drawable.search_icon);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        try {
            getUsers();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FloatingActionMenu materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        FloatingActionButton floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        FloatingActionButton floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        FloatingActionButton floatingActionButton4 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item4);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PartyActivity.class);
                startActivity(intent);

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PartyPendingActivity.class);
                startActivity(intent);

            }
        });
        floatingActionButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MySplashesActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getUsers() throws JSONException {
        HttpParamManager paramManager = new HttpParamManager();
        try {
            HttpRequestManager requestManager = new HttpRequestManager("http://145.24.222.151/users", paramManager);
            requestManager.setRequestMethod("GET");
            requestManager.setRequestProperty("Authorization:", userSession.getToken());
            requestManager.startRequest();

            if (requestManager.getResponseCode() == 200) {
                JSONArray ja = new JSONArray(requestManager.getResponse());
                for (int i = 0; i < ja.length(); i++) {
                    personList.add(ModelParser.parseUser(ja.get(i).toString()));
                }
            } else if (requestManager.getResponseCode() == 401) {
                userSession.refreshToken();
                getUsers();
            } else {
                //error
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            HttpRequestManager httpRequestManager = new HttpRequestManager("http://145.24.222.151/loggedin", null);
            httpRequestManager.setRequestMethod("GET");
            httpRequestManager.setRequestProperty("Authorization:", userSession.getToken());
            httpRequestManager.startRequest();

            if (httpRequestManager.getResponseCode() == 200) {
                JSONArray ja = new JSONArray(httpRequestManager.getResponse());
                for (int i = 0; i < ja.length(); i++) {
                    playingList.add(ModelParser.parseUser(ja.get(i).toString()));
                }
            } else if (httpRequestManager.getResponseCode() == 401) {
                userSession.refreshToken();
                getUsers();
            } else {
                //error
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchItem = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_search) {
            if (searchOpened) {
                closeSearchBar();
            } else {
                openSearchBar(searchQuery);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return new MyListFragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        View view = inflater.inflate(R.layout.fragment_main, container, false);

                        for (Person p:
                                personList) {
                            if (p.isRookie()) {
                                rookieList.add(p);
                            }
                        }

                        adapterRookie = new ColleagueListAdapter(getBaseContext(), R.layout.list_row, rookieList);
                        mAdapter = adapterRookie;

                        return view;
                    }
                };

            } else if (position == 1) {
                return new MyListFragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        View view = inflater.inflate(R.layout.fragment_main, container, false);

                        adapterPlaying = new ColleagueListAdapter(getBaseContext(), R.layout.list_row, playingList);
                        mAdapter = adapterPlaying;

                        return view;
                    }
                };
            } else {
                return new MyListFragment() {
                    @Override
                    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        View view = inflater.inflate(R.layout.fragment_main, container, false);

                        adapterAll = new ColleagueListAdapter(getBaseContext(), R.layout.list_row, personList);
                        mAdapter = adapterAll;

                        return view;
                    }
                };
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "RECRUITS";
                case 1:
                    return "NOW PLAYING";
                case 2:
                    return "ALL COLLEAGUES";
            }
            return null;
        }
    }

    private void openSearchBar(String queryText) {

        // Set custom view on action bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.search_bar);
        setSearchListener();

        // Change search icon accordingly.
        searchItem.setIcon(iconCloseSearch);
        searchOpened = true;

    }

    private void closeSearchBar() {

        // Remove custom view.
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        // Change search icon accordingly.
        searchItem.setIcon(iconOpenSearch);
        searchOpened = false;

    }

    public ArrayList<Person> getPersonList() {
        return personList;
    }


    private void setSearchListener() {
        final EditText searchField = (EditText) findViewById(R.id.etSearch);

        searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = searchField.getText().toString().toLowerCase(Locale.getDefault());
                if(mViewPager.getCurrentItem() == 0)
                    adapterPlaying.getFilter().filter(text);
                if(mViewPager.getCurrentItem() == 1)
                    adapterRookie.getFilter().filter(text);
                if(mViewPager.getCurrentItem() == 2)
                    adapterAll.getFilter().filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }


        });
    }

}
