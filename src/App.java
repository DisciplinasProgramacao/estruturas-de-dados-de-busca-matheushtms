import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

    /** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Quantidade de produtos cadastrados atualmente na lista */
    static int quantosProdutos = 0;

    static ABB<String, Produto> produtosCadastradosPorNome;
    
    static ABB<Integer, Produto> produtosCadastradosPorId;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
        T valor;
        
        System.out.println(mensagem);
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Carregar produtos por nome/descrição");
        System.out.println("3 - Carregar produtos por id");
        System.out.println("4 - Procurar produto, por nome");
        System.out.println("5 - Procurar produto, por id");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna uma árvore de produtos. Arquivo-texto no formato
     * N (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna uma árvore vazia em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Uma árvore com os produtos carregados, ou vazia em caso de problemas de leitura.
     */
    static <K> ABB<K, Produto> lerProdutos(String nomeArquivoDados, Function<Produto, K> extratorDeChave) {
        
        Scanner arquivo = null;
        int numProdutos;
        String linha;
        Produto produto;
        ABB<K, Produto> produtosCadastrados;
        K chave;
        
        try {
            arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
            
            numProdutos = Integer.parseInt(arquivo.nextLine());
            produtosCadastrados = new ABB<K, Produto>();
            
            for (int i = 0; i < numProdutos; i++) {
                linha = arquivo.nextLine();
                produto = Produto.criarDoTexto(linha);
                chave = extratorDeChave.apply(produto);
                produtosCadastrados.inserir(chave, produto);
            }
            quantosProdutos = numProdutos;
            System.out.println("Produtos carregados com sucesso: " + numProdutos);
            
        } catch (IOException excecaoArquivo) {
            System.out.println("Erro ao ler arquivo: " + excecaoArquivo.getMessage());
            produtosCadastrados = new ABB<>();
        } finally {
            if (arquivo != null) arquivo.close();
        }
        
        return produtosCadastrados;
    }
    
    /**
     * Método genérico para localizar produtos e imprimir estatísticas (Tarefa 3).
     */
    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {
        
        Produto produto = null;
        
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("A árvore de produtos não foi carregada ainda!");
            return null;
        }

        cabecalho();
        System.out.println("Localizando produto: " + procurado);
        
        try {
            produto = produtosCadastrados.pesquisar(procurado);
            System.out.println("Produto ENCONTRADO!");
        } catch (NoSuchElementException excecao) {
            produto = null;
            System.out.println("Produto NÃO localizado.");
        }
        
        // Estatísticas
        System.out.println("------------------------------------------------");
        System.out.println("Número de comparações realizadas: " + produtosCadastrados.getComparacoes());
        System.out.printf("Tempo de processamento da pesquisa: %.5f ms\n", produtosCadastrados.getTempo());
        System.out.println("------------------------------------------------");
        
        return produto;
    }
    
    /** * Localiza um produto na árvore de produtos organizados por id (Tarefa 3).
     */
    static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        System.out.print("\nDigite o ID do produto a pesquisar: ");
        try {
            int id = Integer.parseInt(teclado.nextLine());
            return localizarProduto(produtosCadastrados, id);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite apenas números.");
            return null;
        }
    }
    
    /** * Localiza um produto na árvore de produtos organizados por nome (Tarefa 3).
     */
    static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        System.out.print("\nDigite o Nome/Descrição do produto a pesquisar: ");
        String nome = teclado.nextLine();
        return localizarProduto(produtosCadastrados, nome);
    }
    
    private static void mostrarProduto(Produto produto) {
        
        cabecalho();
        String mensagem = "Nenhum produto para exibir.";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Lista todos os produtos cadastrados, numerados, um por linha */
    static <K> void listarTodosOsProdutos(ABB<K, Produto> produtosCadastrados) {
        
        cabecalho();
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("Nenhum produto cadastrado/carregado.");
        } else {
            System.out.println("\nPRODUTOS CADASTRADOS (Em Ordem):");
            System.out.println(produtosCadastrados.toString());
        }
    }
    
    public static void main(String[] args) {
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        
        produtosCadastradosPorNome = new ABB<>();
        produtosCadastradosPorId = new ABB<>();
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos(produtosCadastradosPorNome);
                case 2 -> produtosCadastradosPorNome = lerProdutos(nomeArquivoDados, (p -> p.descricao));
                case 3 -> produtosCadastradosPorId = lerProdutos(nomeArquivoDados, (p -> p.idProduto));
                case 4 -> mostrarProduto(localizarProdutoNome(produtosCadastradosPorNome));
                case 5 -> mostrarProduto(localizarProdutoID(produtosCadastradosPorId));
            }
            pausa();
        } while(opcao != 0);       

        teclado.close();    
    }
}