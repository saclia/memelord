package com.example.memelord.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.memelord.R;
import com.example.memelord.databinding.ActivityMainBinding;
import com.example.memelord.databinding.BnvMainBinding;
import com.example.memelord.databinding.ToolbarMainBinding;
import com.example.memelord.fragments.FeedFragment;
import com.example.memelord.models.Post;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private ToolbarMainBinding mToolbarBinding;
    private BnvMainBinding mBottomNavBinding;
    private Toolbar mToolbar;

    private BubbleNavigationLinearView mBottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        mToolbarBinding = mBinding.toolbarMain;
        mToolbar = mToolbarBinding.toolbar;
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
            getSupportActionBar().setTitle("");
        }

        mBottomNavBinding = mBinding.bnv;
        mBottomNav = mBottomNavBinding.topNavigationConstraint;
        //TODO On view switch, switch the compose button to DM button depending on view
        mBottomNav.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                Bundle bundle;
                switch(view.getId()) {
                    case R.id.c_item_home:
                        loadFragment(new FeedFragment(), null);
                        break;
                    case R.id.c_item_trending:
                        bundle = new Bundle();
                        // TODO Set bundle arguments
                        loadFragment(new FeedFragment(), bundle);
                        break;
                    case R.id.c_item_profile:
                        Log.i(TAG, "Clicked profile.");
                        break;
                }
            }
        });

        loadFragment(new FeedFragment(), null);
        setContentView(view);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_back:
                break;
            case R.id.action_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment, @Nullable Bundle bundle) {
        if(bundle != null)
            fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragmentContainer, fragment).commit();
    }

    private void readComposeFragmentData(Post post) {
        // Will send post to the FeedFragment adapter
    }
}