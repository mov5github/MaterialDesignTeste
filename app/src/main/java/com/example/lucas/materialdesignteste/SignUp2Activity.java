package com.example.lucas.materialdesignteste;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lucas.materialdesignteste.domain.User;
import com.example.lucas.materialdesignteste.fragments.signUp2.FragmentCadastroCabeleireiro;
import com.example.lucas.materialdesignteste.fragments.signUp2.FragmentCadastroCliente;
import com.example.lucas.materialdesignteste.fragments.signUp2.FragmentCadastroSalao;
import com.example.lucas.materialdesignteste.slidingTabLayout.SlidingTabLayout;
import com.example.lucas.materialdesignteste.slidingTabLayout.TabsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class SignUp2Activity extends CommonActivity implements DatabaseReference.CompletionListener {
    private Toolbar mToolbar;
    private FloatingActionButton fab;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private User user;
    private AutoCompleteTextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        initViews();
        initUser();

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if( firebaseUser == null || user.getId() != null ){
                    return;
                }

                user.setId( firebaseUser.getUid() );
                user.saveDB( SignUp2Activity.this );
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("teste","onResume() SIGNUP");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mSlidingTabLayout.setElevation(4 * this.getResources().getDisplayMetrics().density);
            mViewPager.setElevation(4 * this.getResources().getDisplayMetrics().density);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mAuthStateListener != null ){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up_activity,menu);
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
    protected void initViews() {
        if (progressBar == null){
            progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
        }
        //TOOLBAR
        if (mToolbar == null){
            mToolbar = (Toolbar) findViewById(R.id.toolbar_sign_up);
            mToolbar.setTitle("SIGN_UP");
            mToolbar.setSubtitle("tela de cadastro");
            mToolbar.setLogo(R.mipmap.ic_launcher);
            setSupportActionBar(mToolbar);
        }
        //FLOATING ACTION BUTTON
        if (fab == null){
            fab = (FloatingActionButton) findViewById(R.id.fab_sign_up);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Snackbar.make(view, "teste", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                }
            });
        }
        //TABS
        if (mViewPager == null){
            mViewPager = (ViewPager) findViewById(R.id.vp_tabs_sign_up);
            String[] titles = {FragmentCadastroCliente.getTitulo(), FragmentCadastroSalao.getTitulo(), FragmentCadastroCabeleireiro.getTitulo()};
            mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(),this,titles,"SignUp2Activity"));
        }
        if (mSlidingTabLayout == null){
            mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_sign_up);
            mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setViewPager(mViewPager);
            mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
            mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        }
        //FORMULARIO
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> frags = null;
        Fragment frag = null;
        if(fragmentManager.getFragments() != null){
            Log.i("teste","initView() form salao fragmentManager.getFragments() != null");
            frags = fragmentManager.getFragments();
        }
        if (frags != null && frags.size() != 0) {
            Log.i("teste","initView() form salao frags != null");
            frag = frags.get(0);
        }

        if (frag != null && frag instanceof FragmentCadastroSalao){
            Log.i("teste","initView() form salao");
            name = ((FragmentCadastroSalao) frag).getName();
            email = ((FragmentCadastroSalao) frag).getEmail();
            password = ((FragmentCadastroSalao) frag).getPassword();
        }else if (frag != null && frag instanceof FragmentCadastroCliente){
            Log.i("teste","initView() form cliente");
            name = ((FragmentCadastroCliente) frag).getName();
            email = ((FragmentCadastroCliente) frag).getEmail();
            password = ((FragmentCadastroCliente) frag).getPassword();
        }else if (frag != null && frag instanceof FragmentCadastroCabeleireiro){
            Log.i("teste","initView() form cabeleireiro");
            name = ((FragmentCadastroCabeleireiro) frag).getName();
            email = ((FragmentCadastroCabeleireiro) frag).getEmail();
            password = ((FragmentCadastroCabeleireiro) frag).getPassword();
        }

    }

    @Override
    protected void initUser() {
        Log.i("teste","initUser() SIGNUP");
        if (user == null){
            Log.i("teste","initUser() SIGNUP user ==null");
            user = new User();
        }
        if (name != null && email != null && password != null){
            Log.i("teste","initUser() SIGNUP set name email password");
            user.setName( name.getText().toString() );
            user.setEmail( email.getText().toString() );
            user.setPassword( password.getText().toString() );
        }
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mAuth.signOut();

        showToast( "Conta criada com sucesso!" );
        closeProgressBar();
        finish();
    }

    private void saveUser(){

        if (user.getName().isEmpty()){
            Log.w("BrokenLogic","Nao foi possivel saveUser getName vazio");
            closeProgressBar();
        }else if (user.getEmail().isEmpty()){
            Log.w("BrokenLogic","Nao foi possivel saveUser getEmail vazio");
            closeProgressBar();
        }else if (user.getPassword().isEmpty()){
            Log.w("BrokenLogic","Nao foi possivel saveUser getPassword vazio");
            closeProgressBar();
        }else {
            mAuth.createUserWithEmailAndPassword(
                    user.getEmail(),
                    user.getPassword()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if( !task.isSuccessful() ){
                        closeProgressBar();
                    }
                }
            })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrash.report( e );
                            showSnackbar( e.getMessage() );
                        }
                    });
        }


    }



    public void salvarCadastro(View view) {
        Log.i("teste","salvarCadastro()");

        if (view.getId() == R.id.salvar_frag_salao){
            Log.i("teste","salvarCadastro() salao");
        }else if (view.getId() == R.id.salvar_frag_cliente){
            Log.i("teste","salvarCadastro() cliente");
        }else if(view.getId() == R.id.salvar_frag_cabeleireiros){
            Log.i("teste","salvarCadastro() cabeleireiorp");
        }
        openProgressBar();
        initViews();
        initUser();
        if (formularioIsValid()) {
            saveUser();
        }else closeProgressBar();
    }

    private Boolean formularioIsValid(){
        if (nameIsValid() && emailIsValid() && passwordIsvalid()){
            return true;
        }else return false;
    }

    private Boolean nameIsValid(){
        if (name.getText().length() >=5){
            return true;
        }else{
            name.setError("Preencha Corretamente");
            name.requestFocus();
            return false;
        }
    }


    private Boolean passwordIsValid(){
        if (password.getText().length() >=5){
            return true;
        }else {
            password.setError("Minimo de 5 caracters");
            password.requestFocus();
            return false;
        }
    }



}
