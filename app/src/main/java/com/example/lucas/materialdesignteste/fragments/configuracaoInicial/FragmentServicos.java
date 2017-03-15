package com.example.lucas.materialdesignteste.fragments.configuracaoInicial;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lucas.materialdesignteste.R;
import com.example.lucas.materialdesignteste.adapters.AdapterSpinnerIcones;
import com.example.lucas.materialdesignteste.adapters.RecyclerAdapter;
import com.example.lucas.materialdesignteste.dao.ServicoDAO;
import com.example.lucas.materialdesignteste.dao.model.Servico;
import com.example.lucas.materialdesignteste.interfaces.RecyclerViewOnClickListenerHack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 28/12/2016.
 */

public class FragmentServicos extends Fragment implements RecyclerViewOnClickListenerHack{
    private static String titulo = "Serviços";
    private List<Servico> servicoList;
    private List<String> nomesServicos;
    private AdapterSpinnerIcones adapter;
    private RecyclerView mRecyclerView;

    private AutoCompleteTextView nomeServico;
    private EditText precoServico;
    private Spinner spinnerIcones;
    private Spinner spinnerHoras;
    private Spinner spinnerMinutos;
    private AutoCompleteTextView descricaoServico;

    private ServicoDAO servicoDAO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_servicos,container,false);

        initViews(view);


        return view;
    }

    @Override
    public void onClickListener(View view, final int position) {
        //Toast.makeText(getActivity(),"POSITION " + position, Toast.LENGTH_SHORT).show();
        final int posiçao = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("SIM",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Servico servico = servicoList.get(position);
                        removerServico(servico);
                    }
                });
        builder.setNegativeButton("NÃO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //APENAS FECHA O DIALOG
                    }
                });

        builder.setTitle("Excluir Serviço ?");
        builder.setIcon(this.servicoList.get(position).getIcone());
        builder.setCancelable(true);
        alertDialogBuilderMessage(builder, this.servicoList.get(position).getNome(), this.servicoList.get(position).getPreco().toString(), this.servicoList.get(position).getDuracao().toString(), this.servicoList.get(position).getDescricao());



        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initViews(View view){
        this.nomeServico = (AutoCompleteTextView) view.findViewById(R.id.nome_servico);
        this.precoServico = (EditText) view.findViewById(R.id.preco_servico);
        this.precoServico.addTextChangedListener(new MascaraMonetaria(this.precoServico));
        this.precoServico.setText("0");
        this.spinnerHoras = (Spinner) view.findViewById(R.id.spinner_duracao_servico_horas);
        this.spinnerMinutos = (Spinner) view.findViewById(R.id.spinner_duracao_servico_minutos);
        this.descricaoServico = (AutoCompleteTextView) view.findViewById(R.id.descricao_servico);
        this.spinnerIcones = (Spinner) view.findViewById(R.id.spinner_icones);

        createSpinnerIcones();
        createRecyclerViewServicosAdicionados(view);
    }

    private void createSpinnerIcones(){
        ArrayList<Integer> icones = createArrayListIcones();
        this.adapter = new AdapterSpinnerIcones(getActivity(),icones);

        this.spinnerIcones.setAdapter(adapter);
    }

    private void createRecyclerViewServicosAdicionados(View view){


        if (this.servicoList == null){
            this.servicoList = new ArrayList<Servico>();
        }
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.servicos_recycler_view);
        this.mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.mRecyclerView.setLayoutManager(llm);

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(this.servicoList,getActivity());
        recyclerAdapter.setRecyclerViewOnClickListenerHack(this);
        this.mRecyclerView.setAdapter(recyclerAdapter);
    }

    public boolean preenchimentoIsValid(){
        if (this.nomeServico.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Adicione um nome ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if (this.servicoDAO == null){
                this.servicoDAO = new ServicoDAO(getActivity());
            }
            Servico servico = this.servicoDAO.buscarServicoPorNome(this.nomeServico.getText().toString());
            if (this.nomesServicos == null) {
                this.nomesServicos = new ArrayList<String>();
            }
            if (servico != null || this.nomesServicos.contains(this.nomeServico.getText().toString())){
                Toast.makeText(getActivity(),"Ja existe um serviço com este nome, escolha outro nome !",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (this.spinnerIcones.getSelectedItemPosition() == 0){
            Toast.makeText(getActivity(),"Adicione um icone ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.precoServico.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Adicione um preço ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.spinnerHoras.getSelectedItemPosition() == 0 && spinnerMinutos.getSelectedItemPosition() == 0){
            Toast.makeText(getActivity(),"Adicione um tempo de duração ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.descricaoServico.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Adicione uma descrição ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void addServico(Servico servico) {
        if (this.servicoList == null) {
            this.servicoList = new ArrayList<Servico>();
        }
        this.servicoList.add(servico);
        this.nomesServicos.add(servico.getNome());
        RecyclerAdapter recyclerAdapter = (RecyclerAdapter) this.mRecyclerView.getAdapter();
        recyclerAdapter.addItemList(this.servicoList.size());
        limparCampos();
    }

    public void removerServico(Servico servico){
        RecyclerAdapter recyclerAdapter = (RecyclerAdapter) this.mRecyclerView.getAdapter();
        int position = this.servicoList.indexOf(servico);
        nomesServicos.remove(servico.getNome());
        servicoList.remove(servico);
        recyclerAdapter.removeItemList(position);
    }

    //AUXILIARES
    private ArrayList<Integer> createArrayListIcones(){
        ArrayList<Integer> icones = new ArrayList<Integer>();
        icones.add(R.mipmap.ic_launcher);
        icones.add(R.mipmap.ic_launcher);
        icones.add(R.mipmap.ic_launcher);

        return icones;
    }

    public Servico criarServico(){
        Servico servico = new Servico();
        servico.setNome(this.nomeServico.getText().toString());
        servico.setIcone((Integer) this.adapter.getItem(spinnerIcones.getSelectedItemPosition()));
        servico.setPreco(gerarPrecoFloat());
        servico.setDuracao(gerarDuracao());
        servico.setDescricao(this.descricaoServico.getText().toString());

        return servico;
    }

    private int gerarDuracao(){
        int tempoMinutos = 0;
        String horas = (String) this.spinnerHoras.getSelectedItem();
        String horasConvertida = horas.replaceAll("[^0-9]*", "");
        String minutos = (String) this.spinnerMinutos.getSelectedItem();
        String minutosConvertido = minutos.replaceAll("[^0-9]*", "");

        tempoMinutos = ((Integer.valueOf(horasConvertida)) * 60) + Integer.valueOf(minutosConvertido);

        return tempoMinutos;
    }

    private Float gerarPrecoFloat(){
        String preco = this.precoServico.getText().toString();
        String precoConvertido = preco.replaceAll("[^0-9,]*", "");
        precoConvertido = precoConvertido.replace(",", ".");
        return Float.valueOf(precoConvertido);
    }

    private void limparCampos(){
        this.nomeServico.setText("");
        this.precoServico.setText("0");
        this.descricaoServico.setText("");
        this.spinnerIcones.setSelection(0);
        this.spinnerMinutos.setSelection(0);
        this.spinnerHoras.setSelection(0);
        this.nomeServico.requestFocus();
    }

    public void alertDialogBuilderMessage(AlertDialog.Builder builder, String nomeServico, String precoServico, String duracaoServico, String descricaoServico){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml("<p><b>" + getString(R.string.nome_servico_bold_html) +
                    "</b><br>" + nomeServico + "</p><p><b>" +
                    getString(R.string.preco_servico_bold_html) +
                    "</b><br>" + precoServico + "</p><p><b>" +
                    getString(R.string.duracao_servico_bold_html) +
                    "</b><br>" + duracaoServico + "</p><p><b>" +
                    getString(R.string.descricao_servico_bold_html) +
                    "</b><br>" + descricaoServico + "</p>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            builder.setMessage(Html.fromHtml("<p><b>" + getString(R.string.nome_servico_bold_html) +
                    "</b><br>" + nomeServico + "</p><p><b>" +
                    getString(R.string.preco_servico_bold_html) +
                    "</b><br>" + precoServico + "</p><p><b>" +
                    getString(R.string.duracao_servico_bold_html) +
                    "</b><br>" + duracaoServico + "</p><p><b>" +
                    getString(R.string.descricao_servico_bold_html) +
                    "</b><br>" + descricaoServico + "</p>"));
        }
    }




    //GETTERS AND SETTERS
    public static String getTitulo() {
        return titulo;
    }

    public List<Servico> getServicoList() {
        if (this.servicoList == null){
            this.servicoList = new ArrayList<Servico>();
        }
        return servicoList;
    }
    public void setServicoList(List<Servico> servicoList) {
        this.servicoList = servicoList;
    }


    //CLASSES
    private class MascaraMonetaria implements TextWatcher {
        EditText campo;

        public MascaraMonetaria(EditText campo) {
            super();
            this.campo = campo;
        }

        private boolean isUpdating = false;
        // Pega a formatacao do sistema, se for brasil R$ se EUA US$
        private NumberFormat nf = NumberFormat.getCurrencyInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int after) {
            // Evita que o método seja executado varias vezes.
            // Se tirar ele entre em loop
            if (isUpdating) {
                isUpdating = false;
                return;
            }
            isUpdating = true;
            String str = s.toString();
            // Verifica se já existe a máscara no texto.
            boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) && (str.indexOf(".") > -1 || str.indexOf(",") > -1));
            // Verificamos se existe máscara
            if (hasMask) {
                // Retiramos a máscara.
                str = str.replaceAll("[R$]", "").replaceAll("[,]", "").replaceAll("[.]", "");
            }
            try {
                // Transformamos o número que está escrito no EditText em
                // monetário.
                str = nf.format(Double.parseDouble(str) / 100);
                campo.setText(str);
                campo.setSelection(campo.getText().length());
            } catch (NumberFormatException e) {
                s = "";
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Não utilizado
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Não utilizado
        }



    }

}
