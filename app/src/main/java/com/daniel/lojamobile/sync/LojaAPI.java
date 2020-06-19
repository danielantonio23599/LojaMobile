package com.daniel.lojamobile.sync;

import com.daniel.lojamobile.adapter.holder.Cliente;
import com.daniel.lojamobile.adapter.holder.Pedido;
import com.daniel.lojamobile.adapter.holder.ProdutosGravados;
import com.daniel.lojamobile.adapter.holder.Venda;
import com.daniel.lojamobile.modelo.beans.Despesa;
import com.daniel.lojamobile.modelo.beans.Empresa;
import com.daniel.lojamobile.modelo.beans.Produto;
import com.daniel.lojamobile.modelo.beans.Usuario;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface LojaAPI {
    // Servlets para testes no servidor local
    @FormUrlEncoded
    @POST("loja_server/LoginEmpresa")
    Call<Empresa> fazLoginEmpresa(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/FazLoginGarcom")
    Call<Usuario> fazLogin(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarVendas")
    Call<ArrayList<Venda>> listarVendas(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarClientes")
    Call<ArrayList<Cliente>> listarClientes(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/AdicionarClienteVenda")
    Call<Void> adicionarClienteVenda(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha, @Field("venda") String venda, @Field("cliente") String cliente);

    @FormUrlEncoded
    @POST("loja_server/CancelarPedido")
    Call<Void> cancelarPedido(@Field("pedido") String pedido, @Field("motivo") String motivo, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarPedidos")
    Call<ArrayList<Pedido>> listarPedidosPendentes(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/AlterarPedido")
    Call<ArrayList<Pedido>> alterarPedido(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha, @Field("pedido") String pedido);

    @FormUrlEncoded
    @POST("loja_server/ListarPedidosRealizados")
    Call<ArrayList<Pedido>> listarPedidosRealizados(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarVendasAbertas")
    Call<ArrayList<Venda>> getVendasAbertas(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarVenda")
    Call<Venda> listarVenda(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha, @Field("venda") String venda);

    @FormUrlEncoded
    @POST("loja_server/AdicionarFuncionario")
    Call<Void> insereFuncionario(@Field("funcionario") String funcionario, @Field("empresa") String empresa, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarProdutosVenda")
    Call<ArrayList<ProdutosGravados>> listarProdutosVenda(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha, @Field("venda") String mesa);

    @FormUrlEncoded
    @POST("loja_server/DevolucaoPedido")
    Call<Void> devolverPedido(@Field("devolucao") String devolucao, @Field("pedido") String pedido, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/GerarCupomVenda")
    Call<ResponseBody> geraCupomVenda(@Field("venda") String venda, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/IsCaixaAberto")
    Call<Void> isCaixaAberto(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/AbrirCaixa")
    Call<Void> abrirCaixa(@Field("caixa") String caixa, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarDespesas")
    Call<ArrayList<Despesa>> listarDespesas(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/GerarNotaVenda")
    Call<ResponseBody> geraNotaVenda(@Field("venda") String venda, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/EditarFuncionario")
    Call<Void> atualizarFuncionario(@Field("funcionario") String funcionario, @Field("nomeUsuario") String empresa, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/ListarProdutos")
    Call<ArrayList<Produto>> listarProdutos(@Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/AbrirVenda")
    Call<Void> abrirVenda(@Field("nomeUsuario") String empresa, @Field("senha") String senha);

    @FormUrlEncoded
    @POST("loja_server/RealizarVenda")
    Call<Void> enviarPedidos(@Field("pedido") String pedido, @Field("nomeUsuario") String nomeUsuario, @Field("senha") String senha);


}
