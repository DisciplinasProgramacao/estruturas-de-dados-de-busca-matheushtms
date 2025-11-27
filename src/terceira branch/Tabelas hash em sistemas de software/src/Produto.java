import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class Produto implements Comparable<Produto>{
    
    private static final double MARGEM_PADRAO = 0.2;
    private static int ultimoID = 10_000;
    
    protected int idProduto;
    protected String descricao;
    protected double precoCusto;
    protected double margemLucro;
    
    private void init(String desc, double precoCusto, double margemLucro) {
        if ((desc.length() >= 3) && (precoCusto > 0.0) && (margemLucro > 0.0)) {
            descricao = desc;
            this.precoCusto = precoCusto;
            this.margemLucro = margemLucro;
            idProduto = ultimoID++;
        } else {
            throw new IllegalArgumentException("Valores inválidos para os dados do produto.");
        }
    }
    
    protected Produto(String desc, double precoCusto, double margemLucro) {
        init(desc, precoCusto, margemLucro);
    }
    
    protected Produto(String desc, double precoCusto) {
        init(desc, precoCusto, MARGEM_PADRAO);
    }
    
    public abstract double valorDeVenda();
    
    @Override
    public String toString() {
        NumberFormat moeda = NumberFormat.getCurrencyInstance();
        return String.format("IDENTIFICADOR: " + idProduto + " NOME: " + descricao + ": " + moeda.format(valorDeVenda()));
    }
    
    @Override
    public int hashCode(){
        return idProduto;
    }

    @Override
    public boolean equals(Object obj){
        try{
            Produto outro = (Produto)obj;
            return this.hashCode() == outro.hashCode();
        }catch (ClassCastException ex){
            return false;
        }
    }
    
    public int compareTo(Produto outro){
        return this.descricao.compareToIgnoreCase(outro.descricao);
    }
    
    static Produto criarDoTexto(String linha) {
        String[] dadosLinha;
        int tipo;
        String descricao;
        double precoCusto, margemLucro;
        LocalDate dataDeValidade;
        Produto produto;
        
        dadosLinha = linha.split(";");
        tipo = Integer.parseInt(dadosLinha[0]);
        descricao = dadosLinha[1];
        precoCusto = Double.parseDouble(dadosLinha[2].replace(",", "."));
        margemLucro = Double.parseDouble(dadosLinha[3].replace(",", "."));
        if (tipo == 2) {
            DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataDeValidade = LocalDate.parse(dadosLinha[4], formatoData);
            produto = new ProdutoPerecivel(descricao, precoCusto, margemLucro, dataDeValidade);
        } else {
            produto = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
        }
        
        return produto;
    }
        
    public abstract String gerarDadosTexto();
}

// --- CLASSE PRODUTO PERECIVEL ---
class ProdutoPerecivel extends Produto{
    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;
    private LocalDate dataDeValidade;
    
    public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
        super(desc, precoCusto, margemLucro);
        if (validade.isBefore(LocalDate.now())) throw new IllegalArgumentException("Data inválida!");
        dataDeValidade = validade;
    }
    
    public ProdutoPerecivel(String desc, double precoCusto, LocalDate validade) {
        super(desc, precoCusto);
        if (validade.isBefore(LocalDate.now())) throw new IllegalArgumentException("Data inválida!");
        dataDeValidade = validade;
    }

    @Override
    public double valorDeVenda() {
        double precoVenda = (precoCusto * (1.0 + margemLucro));
        if (LocalDate.now().until(dataDeValidade).getDays() <= PRAZO_DESCONTO) {
            precoVenda = precoVenda * (1.0 - DESCONTO);
        }
        return precoVenda;
    }
    
    @Override
    public String toString(){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return super.toString() + "\nVálido até " + formato.format(dataDeValidade);
    }
    
    @Override
    public String gerarDadosTexto() {
        String pc = String.format("%.2f", precoCusto).replace(",", ".");
        String ml = String.format("%.2f", margemLucro).replace(",", ".");
        String dv = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dataDeValidade);
        return String.format("2;%s;%s;%s;%s", descricao, pc, ml, dv);
    }
}

// --- CLASSE PRODUTO NAO PERECIVEL ---
class ProdutoNaoPerecivel extends Produto{
     public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro) {
         super(desc, precoCusto, margemLucro);
     }
     
     public ProdutoNaoPerecivel(String desc, double precoCusto) {
         super(desc, precoCusto);
     }

     @Override
     public double valorDeVenda() {
         return (precoCusto * (1.0 + margemLucro));
     }

     @Override
    public String gerarDadosTexto() {
         String pc = String.format("%.2f", precoCusto).replace(",", ".");
         String ml = String.format("%.2f", margemLucro).replace(",", ".");
         return String.format("1;%s;%s;%s", descricao, pc, ml);
    }
}