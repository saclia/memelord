package com.example.memelord.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.example.memelord.R;
import com.example.memelord.customui.BubbleNavigationView;
import com.example.memelord.databinding.ActivityMainBinding;
import com.example.memelord.databinding.BnvMainBinding;
import com.example.memelord.databinding.ToolbarMainBinding;
import com.example.memelord.fragments.ComposeDialogFragment;
import com.example.memelord.fragments.ComposeFragment;
import com.example.memelord.fragments.DirectMessagesFragment;
import com.example.memelord.fragments.FeedFragment;
import com.example.memelord.fragments.ProfileFragment;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Post;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements Util.FragmentLoader {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_KEY_IMAGE_PATH = "editedMemePath";
    public static final String INTENT_KEY_FRAG = "fragmentToInit";
    public static final int REQUEST_CODE_PHOTO_EDIT = 30;

    private ActivityMainBinding mBinding;
    private ToolbarMainBinding mToolbarBinding;
    private BnvMainBinding mBottomNavBinding;
    private Toolbar mToolbar;

    private BubbleNavigationView mBottomNav;
    private MenuItem mProgressBar;
    private CardView cvCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        mToolbarBinding = mBinding.toolbarMain;
        mToolbar = mToolbarBinding.toolbar;
        setSupportActionBar(mToolbar);

        if(ParseUser.getCurrentUser() == null)
            navigateToLogin();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        }

        mBottomNavBinding = mBinding.bnv;
        mBottomNav = mBottomNavBinding.topNavigationConstraint;
        //TODO On view switch, switch the compose button to DM button depending on view
        mBottomNav.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                showProgressBar();
                switch(view.getId()) {
                    case R.id.c_item_home:
                        loadFragment(new FeedFragment(), null);
                        break;
                    case R.id.c_item_profile:
                        loadFragment(new ProfileFragment(), null);
                        break;
                }
            }
        });

        cvCompose = mBinding.cvCompose;
        cvCompose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.flDialog, new ComposeDialogFragment()).commit();
            }
        });
        BubbleToggleView home = view.findViewById(R.id.c_item_home);
        home.activate();
        home.setInitialState(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentContainer, new FeedFragment()).commit();
        setContentView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        mProgressBar = menu.findItem(R.id.miActionProgress);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                    Bundle bundle = new Bundle();
                    bundle.putString(FeedFragment.ARG_SEARCH_QUERY, query);
                    loadFragment(new FeedFragment(), bundle);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
            case R.id.action_search:
                break;
            case R.id.action_direct_message:
                loadFragment(new DirectMessagesFragment(), null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PHOTO_EDIT && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "User has made a meme");
            Bundle intentExtras = getIntent().getExtras();
            Bundle bundle = new Bundle();
            if(intentExtras != null) {
                String intentImagePath = intentExtras.getString(INTENT_KEY_IMAGE_PATH);
                if(intentImagePath != null) {
                    bundle.putString(ComposeFragment.ARG_IMAGE_PATH, intentImagePath);
                }
            }
            loadFragment(new ComposeFragment(), bundle);
        }

    }

    public void loadFragment(Fragment fragment, @Nullable Bundle bundle) {
        if(bundle != null)
            fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_left_in, R.anim.slide_right_out);
        ft.replace(R.id.flFragmentContainer, fragment).addToBackStack(fragment.getTag()).commit();
    }

    public void showProgressBar() {
        if(mProgressBar != null)
            mProgressBar.setVisible(true);
    }

    public void hideProgressBar() {
        if(mProgressBar != null)
            mProgressBar.setVisible(false);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}