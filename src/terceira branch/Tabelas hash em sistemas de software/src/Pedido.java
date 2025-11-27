import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class Pedido implements Comparable<Pedido>{

    private static int ultimoID = 1;
    private int idPedido;
    private static final double DESCONTO_PG_A_VISTA = 0.15;
    private Lista<Produto> produtos;
    private LocalDate dataPedido;
    private int quantProdutos = 0;
    private int formaDePagamento; // 1: à vista, 2: parcelado
    
    public Pedido(LocalDate dataPedido, int formaDePagamento) {
        idPedido = ultimoID++;
        produtos = new Lista<Produto>();
        quantProdutos = 0;
        this.dataPedido = dataPedido;
        this.formaDePagamento = formaDePagamento;
    }
    
    public boolean incluirProduto(Produto novo) {
        if (novo == null) return false;
        // Insere na posição atual (final da lista lógica)
        produtos.inserir(novo, quantProdutos);
        quantProdutos++;
        return true;
    }
    
    public double valorFinal() {
        // Usa lambda para somar valores de venda
        double valorPedido = produtos.calcularValorTotal((produto -> produto.valorDeVenda()));
        if (formaDePagamento == 1) {
            valorPedido = valorPedido * (1.0 - DESCONTO_PG_A_VISTA);
        }
        BigDecimal bd = new BigDecimal(valorPedido).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Número do pedido: %02d\n", idPedido));
        sb.append("Data do pedido: " + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dataPedido) + "\n");
        sb.append("Pedido com " + quantProdutos + " produtos.\n");
        sb.append("Produtos no pedido:\n" + produtos.toString());
        
        sb.append("Pedido pago ");
        if (formaDePagamento == 1) {
            sb.append("à vista. Percentual de desconto: " + String.format("%.2f", DESCONTO_PG_A_VISTA * 100) + "%\n");
        } else {
            sb.append("parcelado.\n");
        }
        sb.append("Valor total do pedido: R$ " + String.format("%.2f", valorFinal()) + "\n");
        return sb.toString();
    }
    
    @Override
    public int compareTo(Pedido outro) {
        return Integer.compare(this.idPedido, outro.idPedido);
    }
    
    // Getters
    public LocalDate getDataPedido() { return dataPedido; }
    public int getIdPedido() { return idPedido; }
    public int getQuantosProdutos() { return quantProdutos; }
    public Lista<Produto> getProdutos() { return produtos; }
    
    public int repeticoes(Produto produto){
        return produtos.contarRepeticoes(prod -> prod.descricao.equals(produto.descricao));
    }
}