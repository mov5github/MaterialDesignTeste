package com.example.lucas.materialdesignteste;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lucas.materialdesignteste.fragments.FragmentCabeleireiros;
import com.example.lucas.materialdesignteste.fragments.FragmentFuncionamento;
import com.example.lucas.materialdesignteste.fragments.FragmentServicos;
import com.example.lucas.materialdesignteste.slidingTabLayout.SlidingTabLayout;
import com.example.lucas.materialdesignteste.slidingTabLayout.TabsAdapter;

public class TabsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tabs_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_item1:
                Toast.makeText(this,String.valueOf(id),Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
            mSlidingTabLayout.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    private void initView() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
        mToolbar.setTitle("TABS ACTIVITY");
        mToolbar.setSubtitle("Configuração inicial");
        mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);
        //TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);
        String[] titles = {FragmentFuncionamento.getTitulo(), FragmentServicos.getTitulo(),FragmentCabeleireiros.getTitulo()};
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(),this,titles));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));



    }


}
