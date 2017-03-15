package com.example.lucas.materialdesignteste.fragments.configuracaoInicial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.materialdesignteste.R;
import com.example.lucas.materialdesignteste.geral.FuncionamentoSalao;

/**
 * Created by Lucas on 28/12/2016.
 */

public class FragmentFuncionamento extends Fragment {
    private TextView abreSegunda;
    private TextView abreTerca;
    private TextView abreQuarta;
    private TextView abreQuinta;
    private TextView abreSexta;
    private TextView abreSabado;
    private TextView abreDomingo;
    private TextView fechaSegunda;
    private TextView fechaTerca;
    private TextView fechaQuarta;
    private TextView fechaQuinta;
    private TextView fechaSexta;
    private TextView fechaSabado;
    private TextView fechaDomingo;
    private CheckBox segunda;
    private CheckBox terca;
    private CheckBox quarta;
    private CheckBox quinta;
    private CheckBox sexta;
    private CheckBox sabado;
    private CheckBox domingo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_funcionamento,container,false);
        initViews(view);
        return view;
    }

    public static String getTitulo() {
        String titulo = "Funcionamento";
        return titulo;
    }

    private void initViews(View view){
        abreSegunda = (TextView) view.findViewById(R.id.abre_segunda);
        abreTerca =  (TextView) view.findViewById(R.id.abre_terca);
        abreQuarta = (TextView) view.findViewById(R.id.abre_quarta);
        abreQuinta = (TextView) view.findViewById(R.id.abre_quinta);
        abreSexta = (TextView) view.findViewById(R.id.abre_sexta);
        abreSabado = (TextView) view.findViewById(R.id.abre_sabado);
        abreDomingo = (TextView) view.findViewById(R.id.abre_domingo);
        fechaSegunda = (TextView) view.findViewById(R.id.fecha_segunda);
        fechaTerca =  (TextView) view.findViewById(R.id.fecha_terca);
        fechaQuarta = (TextView) view.findViewById(R.id.fecha_quarta);
        fechaQuinta = (TextView) view.findViewById(R.id.fecha_quinta);
        fechaSexta = (TextView) view.findViewById(R.id.fecha_sexta);
        fechaSabado = (TextView) view.findViewById(R.id.fecha_sabado);
        fechaDomingo = (TextView) view.findViewById(R.id.fecha_domingo);
        segunda =(CheckBox) view.findViewById(R.id.segunda);
        terca =(CheckBox) view.findViewById(R.id.terca);
        quarta =(CheckBox) view.findViewById(R.id.quarta);
        quinta =(CheckBox) view.findViewById(R.id.quinta);
        sexta =(CheckBox) view.findViewById(R.id.sexta);
        sabado =(CheckBox) view.findViewById(R.id.sabado);
        domingo =(CheckBox) view.findViewById(R.id.domingo);
    }

    public boolean preenchimentoIsValid(){
        Log.i("script","entrou validacao");
        if (segunda.isChecked()) {
            Log.i("script","segunda check");
            if (abreSegunda.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das segundas!");
                return false;
            }else if (fechaSegunda.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das segundas!");
                return false;
            }
        }
        if (terca.isChecked()) {
            Log.i("script","terca check");
            if (abreTerca.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das terças!");
                return false;
            }else if (fechaTerca.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das terças!");
                return false;
            }
        }
        if (quarta.isChecked()) {
            Log.i("script","quarta check");
            if (abreQuarta.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das quartas!");
                return false;
            }else if (fechaQuarta.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das quartas!");
                return false;
            }
        }
        if (quinta.isChecked()) {
            Log.i("script","quinta check");
            if (abreQuinta.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das quintas!");
                return false;
            }else if (fechaQuinta.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das quintas!");
                return false;
            }
        }
        if (sexta.isChecked()) {
            Log.i("script","sexta check");
            if (abreSexta.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das sextas!");
                return false;
            }else if (fechaSexta.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das sextas!");
                return false;
            }
        }
        if (sabado.isChecked()) {
            Log.i("script","sabado check");
            if (abreSabado.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura dos sabados!");
                return false;
            }else if (fechaSabado.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento dos sabados!");
                return false;
            }
        }
        if (domingo.isChecked()) {
            Log.i("script","domingo check");
            if (abreDomingo.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura dos domingos!");
                return false;
            }else if (fechaDomingo.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento dos domingos!");
                return false;
            }
        }

        if(!segunda.isChecked() && !terca.isChecked() && !quarta.isChecked() && !quinta.isChecked() && !sexta.isChecked() && !sabado.isChecked() && !domingo.isChecked()){
            showToast("Selecionar pelo menos um dia da semana!");
            return false;
        }
        return true;
    }

    public FuncionamentoSalao criarFuncionamentoSalao(){
        FuncionamentoSalao funcionamentoSalao = new FuncionamentoSalao();

        if (this.abreSegunda.getText() != null && !this.abreSegunda.getText().toString().isEmpty() && !this.abreSegunda.getText().toString().equals("--:--")){
            funcionamentoSalao.setAbreSegunda(this.abreSegunda.getText().toString());
        }
        if (this.fechaSegunda.getText() != null && !this.fechaSegunda.getText().toString().isEmpty() && !this.fechaSegunda.getText().toString().equals("--:--")){
            funcionamentoSalao.setFechaSegunda(this.fechaSegunda.getText().toString());
        }
        if (this.abreTerca.getText() != null && !this.abreTerca.getText().toString().isEmpty() && !this.abreTerca.getText().toString().equals("--:--")){
            funcionamentoSalao.setAbreTerca(this.abreTerca.getText().toString());
        }
        if (this.fechaTerca.getText() != null && !this.fechaTerca.getText().toString().isEmpty() && !this.fechaTerca.getText().toString().equals("--:--")){
            funcionamentoSalao.setFechaTerca(this.fechaTerca.getText().toString());
        }
        if (this.abreQuarta.getText() != null && !this.abreQuarta.getText().toString().isEmpty() && !this.abreQuarta.getText().toString().equals("--:--")){
            funcionamentoSalao.setAbreQuarta(this.abreQuarta.getText().toString());
        }
        if (this.fechaQuarta.getText() != null && !this.fechaQuarta.getText().toString().isEmpty() && !this.fechaQuarta.getText().toString().equals("--:--")){
            funcionamentoSalao.setFechaQuarta(this.fechaQuarta.getText().toString());
        }
        if (this.abreQuinta.getText() != null && !this.abreQuinta.getText().toString().isEmpty() && !this.abreQuinta.getText().toString().equals("--:--")){
            funcionamentoSalao.setAbreQuinta(this.abreQuinta.getText().toString());
        }
        if (this.fechaQuinta.getText() != null && !this.fechaQuinta.getText().toString().isEmpty() && !this.fechaQuinta.getText().toString().equals("--:--")){
            funcionamentoSalao.setFechaQuinta(this.fechaQuinta.getText().toString());
        }
        if (this.abreSexta.getText() != null && !this.abreSexta.getText().toString().isEmpty() && !this.abreSexta.getText().toString().equals("--:--")){
            funcionamentoSalao.setAbreSexta(this.abreSexta.getText().toString());
        }
        if (this.fechaSexta.getText() != null && !this.fechaSexta.getText().toString().isEmpty() && !this.fechaSexta.getText().toString().equals("--:--")){
            funcionamentoSalao.setFechaSexta(this.fechaSexta.getText().toString());
        }
        if (this.abreSabado.getText() != null && !this.abreSabado.getText().toString().isEmpty() && !this.abreSabado.getText().toString().equals("--:--")){
            funcionamentoSalao.setAbreSabado(this.abreSabado.getText().toString());
        }
        if (this.fechaSabado.getText() != null && !this.fechaSabado.getText().toString().isEmpty() && !this.fechaSabado.getText().toString().equals("--:--")){
            funcionamentoSalao.setFechaSabado(this.fechaSabado.getText().toString());
        }
        if (this.abreDomingo.getText() != null && !this.abreDomingo.getText().toString().isEmpty() && !this.abreDomingo.getText().toString().equals("--:--")){
            funcionamentoSalao.setAbreDomingo(this.abreDomingo.getText().toString());
        }
        if (this.fechaDomingo.getText() != null && !this.fechaDomingo.getText().toString().isEmpty() && !this.fechaDomingo.getText().toString().equals("--:--")){
            funcionamentoSalao.setFechaDomingo(this.fechaDomingo.getText().toString());
        }

        return funcionamentoSalao;
    }

    public void aplicaVisibilidadeHorarios(View view){
        CheckBox checkBox = (CheckBox) view;
        switch (view.getId()){
            case R.id.segunda:
                if (checkBox.isChecked()){
                    abreSegunda.setVisibility(View.VISIBLE);
                    fechaSegunda.setVisibility(View.VISIBLE);
                }else{
                    abreSegunda.setVisibility(View.INVISIBLE);
                    fechaSegunda.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.terca:
                if (checkBox.isChecked()){
                    abreTerca.setVisibility(View.VISIBLE);
                    fechaTerca.setVisibility(View.VISIBLE);
                }else {
                    abreTerca.setVisibility(View.INVISIBLE);
                    fechaTerca.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.quarta:
                if (checkBox.isChecked()){
                    abreQuarta.setVisibility(View.VISIBLE);
                    fechaQuarta.setVisibility(View.VISIBLE);
                }else{
                    abreQuarta.setVisibility(View.INVISIBLE);
                    fechaQuarta.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.quinta:
                if (checkBox.isChecked()){
                    abreQuinta.setVisibility(View.VISIBLE);
                    fechaQuinta.setVisibility(View.VISIBLE);
                }else {
                    abreQuinta.setVisibility(View.INVISIBLE);
                    fechaQuinta.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.sexta:
                if (checkBox.isChecked()){
                    abreSexta.setVisibility(View.VISIBLE);
                    fechaSexta.setVisibility(View.VISIBLE);
                }else {
                    abreSexta.setVisibility(View.INVISIBLE);
                    fechaSexta.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.sabado:
                if (checkBox.isChecked()){
                    abreSabado.setVisibility(View.VISIBLE);
                    fechaSabado.setVisibility(View.VISIBLE);
                }else {
                    abreSabado.setVisibility(View.INVISIBLE);
                    fechaSabado.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.domingo:
                if (checkBox.isChecked()){
                    abreDomingo.setVisibility(View.VISIBLE);
                    fechaDomingo.setVisibility(View.VISIBLE);
                }else {
                    abreDomingo.setVisibility(View.INVISIBLE);
                    fechaDomingo.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void showToast( String message ){
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    //Getters and Setters
    public TextView getAbreSegunda() {
        return abreSegunda;
    }

    public void setAbreSegunda(TextView abreSegunda) {
        this.abreSegunda = abreSegunda;
    }

    public TextView getAbreTerca() {
        return abreTerca;
    }

    public void setAbreTerca(TextView abreTerca) {
        this.abreTerca = abreTerca;
    }

    public TextView getAbreQuinta() {
        return abreQuinta;
    }

    public void setAbreQuinta(TextView abreQuinta) {
        this.abreQuinta = abreQuinta;
    }

    public TextView getAbreQuarta() {
        return abreQuarta;
    }

    public void setAbreQuarta(TextView abreQuarta) {
        this.abreQuarta = abreQuarta;
    }

    public TextView getAbreSexta() {
        return abreSexta;
    }

    public void setAbreSexta(TextView abreSexta) {
        this.abreSexta = abreSexta;
    }

    public TextView getAbreSabado() {
        return abreSabado;
    }

    public void setAbreSabado(TextView abreSabado) {
        this.abreSabado = abreSabado;
    }

    public TextView getAbreDomingo() {
        return abreDomingo;
    }

    public void setAbreDomingo(TextView abreDomingo) {
        this.abreDomingo = abreDomingo;
    }

    public TextView getFechaQuarta() {
        return fechaQuarta;
    }

    public void setFechaQuarta(TextView fechaQuarta) {
        this.fechaQuarta = fechaQuarta;
    }

    public TextView getFechaSegunda() {
        return fechaSegunda;
    }

    public void setFechaSegunda(TextView fechaSegunda) {
        this.fechaSegunda = fechaSegunda;
    }

    public TextView getFechaTerca() {
        return fechaTerca;
    }

    public void setFechaTerca(TextView fechaTerca) {
        this.fechaTerca = fechaTerca;
    }

    public TextView getFechaQuinta() {
        return fechaQuinta;
    }

    public void setFechaQuinta(TextView fechaQuinta) {
        this.fechaQuinta = fechaQuinta;
    }

    public TextView getFechaSexta() {
        return fechaSexta;
    }

    public void setFechaSexta(TextView fechaSexta) {
        this.fechaSexta = fechaSexta;
    }

    public TextView getFechaSabado() {
        return fechaSabado;
    }

    public void setFechaSabado(TextView fechaSabado) {
        this.fechaSabado = fechaSabado;
    }

    public TextView getFechaDomingo() {
        return fechaDomingo;
    }

    public void setFechaDomingo(TextView fechaDomingo) {
        this.fechaDomingo = fechaDomingo;
    }

    public CheckBox getSegunda() {
        return segunda;
    }

    public void setSegunda(CheckBox segunda) {
        this.segunda = segunda;
    }

    public CheckBox getTerca() {
        return terca;
    }

    public void setTerca(CheckBox terca) {
        this.terca = terca;
    }

    public CheckBox getQuinta() {
        return quinta;
    }

    public void setQuinta(CheckBox quinta) {
        this.quinta = quinta;
    }

    public CheckBox getQuarta() {
        return quarta;
    }

    public void setQuarta(CheckBox quarta) {
        this.quarta = quarta;
    }

    public CheckBox getSexta() {
        return sexta;
    }

    public void setSexta(CheckBox sexta) {
        this.sexta = sexta;
    }

    public CheckBox getSabado() {
        return sabado;
    }

    public void setSabado(CheckBox sabado) {
        this.sabado = sabado;
    }

    public CheckBox getDomingo() {
        return domingo;
    }

    public void setDomingo(CheckBox domingo) {
        this.domingo = domingo;
    }
}
