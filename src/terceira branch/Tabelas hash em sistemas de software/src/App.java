import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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

    /** Tabela Hash para mapear Produtos -> Lista de Pedidos (Tarefa 2) */
    static TabelaHash<Produto, Lista<Pedido>> tabelaPedidos;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("\nDigite enter para continuar...");
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
        } catch (Exception e) { // Robustez genérica para erros de reflexão
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Carregar produtos (Arquivo -> Árvore por ID)");
        System.out.println("2 - Listar todos os produtos (por Nome)");
        System.out.println("3 - Gerar árvore por Nome (Tarefa 1)");
        System.out.println("4 - Procurar produto por Nome");
        System.out.println("5 - Procurar produto por ID");
        System.out.println("6 - Simular Vendas (Gerar Pedidos e preencher Tabela) (Tarefa 2)");
        System.out.println("7 - Relatório de Pedidos de um Produto (Tarefa 3)");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        
        // Tarefa 4: Robustez na leitura do menu
        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Opção inválida
        }
    }
    
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
            
        } catch (IOException excecaoArquivo) {
            System.out.println("Erro ao ler arquivo: " + excecaoArquivo.getMessage());
            produtosCadastrados = null;
        } catch (Exception e) {
            System.out.println("Erro nos dados do arquivo.");
            produtosCadastrados = null;
        } finally {
            if(arquivo != null) arquivo.close();
        }
        
        return produtosCadastrados;
    }
    
    /**
     * TAREFA 2: Implementação do método inserirNaTabela.
     * Insere o pedido na lista de pedidos do produto correspondente dentro da Tabela Hash.
     */
    private static void inserirNaTabela(Produto produto, Pedido pedido) {
        Lista<Pedido> listaPedidos;
        
        try {
            // Tenta buscar a lista de pedidos deste produto
            listaPedidos = tabelaPedidos.pesquisar(produto);
        } catch (NoSuchElementException e) {
            // Se não existe, cria nova lista e insere na tabela
            listaPedidos = new Lista<>();
            tabelaPedidos.inserir(produto, listaPedidos);
        }
        
        // Insere o pedido na lista (assumindo que a classe Lista tem inserirFinal)
        listaPedidos.inserir(pedido, listaPedidos.tamanho()); 
    }

    /**
     * TAREFA 3: Relatório de pedidos de um produto.
     * Gera um arquivo .txt com os pedidos.
     */
    static void pedidosDoProduto() {
        cabecalho();
        System.out.println("GERAR RELATÓRIO DE PEDIDOS");
        
        Produto p = localizarProdutoNome(produtosCadastradosPorNome);
        
        if (p == null) {
            System.out.println("Produto não selecionado.");
            return;
        }

        try {
            Lista<Pedido> lista = tabelaPedidos.pesquisar(p);
            
            String nomeArq = "relatorio_" + p.descricao.replaceAll("\\s+", "") + ".txt";
            FileWriter arq = new FileWriter(nomeArq);
            PrintWriter gravarArq = new PrintWriter(arq);
            
            gravarArq.printf("RELATÓRIO DE PEDIDOS: %s%n", p.descricao);
            gravarArq.println("====================================");
            gravarArq.println(lista.toString());
            
            arq.close();
            System.out.println("Relatório gerado: " + nomeArq);
            
        } catch (NoSuchElementException e) {
            System.out.println("Nenhum pedido encontrado para este produto.");
        } catch (IOException e) {
            System.out.println("Erro ao gravar arquivo: " + e.getMessage());
        }
    }

    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {
        Produto produto;
        try {
            produto = produtosCadastrados.pesquisar(procurado);
        } catch (NoSuchElementException excecao) {
            produto = null;
        }
        return produto;
    }
    
    /** Preenchimento do TODO: Localizar produto por ID com robustez */
    static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        if(produtosCadastrados == null || produtosCadastrados.vazia()){
            System.out.println("Árvore vazia. Carregue os dados primeiro.");
            return null;
        }
        
        System.out.print("Digite o ID do produto: ");
        try {
            int id = Integer.parseInt(teclado.nextLine());
            Produto p = localizarProduto(produtosCadastrados, id);
            if(p == null) System.out.println("Produto não encontrado.");
            return p;
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite apenas números.");
            return null;
        }
    }
    
    /** Preenchimento do TODO: Localizar produto por Nome */
    static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        if(produtosCadastrados == null || produtosCadastrados.vazia()){
            System.out.println("Árvore vazia.");
            return null;
        }
        
        System.out.print("Digite o nome (ou parte dele): ");
        String nome = teclado.nextLine();
        
        // Obs: Como é uma ABB exata, a busca deve ser pela chave exata usada na inserção
        Produto p = localizarProduto(produtosCadastrados, nome);
        if(p == null) System.out.println("Produto não encontrado.");
        return p;
    }
    
    private static void mostrarProduto(Produto produto) {
        cabecalho();
        if (produto != null){
            System.out.println("Dados do produto:\n" + produto);
        } else {
            System.out.println("Produto inválido ou não encontrado.");
        }
    }
    
    static <K> void listarTodosOsProdutos(ABB<K, Produto> produtosCadastrados) {
        cabecalho();
        if(produtosCadastrados != null) {
            System.out.println("\nPRODUTOS CADASTRADOS:");
            System.out.println(produtosCadastrados.toString());
        } else {
            System.out.println("Nenhum produto cadastrado.");
        }
    }
    
    /** * Simula a geração de pedidos para testar a Tarefa 2.
     * Como não temos o código do professor "gerador", criei este simples.
     */
    static void simularVendas(ABB<Integer, Produto> arvoreId) {
        if(arvoreId == null || arvoreId.vazia()) {
            System.out.println("Carregue os produtos por ID primeiro.");
            return;
        }
        
        System.out.println("Simulando geração de pedidos...");
        Random rand = new Random();
        
        // Simula 5 pedidos
        for(int i=0; i<5; i++) {
            Pedido pedido = new Pedido(java.time.LocalDate.now(), 1); // 1 = à vista
            
            // Adiciona 3 produtos aleatórios ao pedido
            for(int j=0; j<3; j++) {
                int idAleatorio = 10000 + rand.nextInt(quantosProdutos); // IDs começam em 10000
                Produto p = localizarProduto(arvoreId, idAleatorio);
                if(p != null) {
                    pedido.incluirProduto(p);
                    // AQUI CHAMAMOS O MÉTODO DA TAREFA 2
                    inserirNaTabela(p, pedido);
                }
            }
            System.out.println("Pedido " + pedido.getIdPedido() + " gerado e processado.");
        }
        System.out.println("Tabela hash populada com sucesso.");
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        
        // Inicializa tabela hash com capacidade (ex: 100)
        tabelaPedidos = new TabelaHash<>(100); 

        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> {
                    // Carrega por ID (Base principal)
                    produtosCadastradosPorId = lerProdutos(nomeArquivoDados, (p -> p.idProduto));
                    if(produtosCadastradosPorId != null) System.out.println("Produtos carregados por ID.");
                }
                case 2 -> listarTodosOsProdutos(produtosCadastradosPorNome);
                case 3 -> {
                    // TAREFA 1: Criar árvore por nome a partir da árvore de ID
                    if (produtosCadastradosPorId != null && !produtosCadastradosPorId.vazia()) {
                        produtosCadastradosPorNome = new ABB<>(
                            produtosCadastradosPorId, 
                            (p -> p.descricao), 
                            String::compareToIgnoreCase
                        );
                        System.out.println("Árvore por Nome criada com sucesso (Tarefa 1).");
                    } else {
                        System.out.println("Carregue a árvore por ID primeiro (Opção 1).");
                    }
                }
                case 4 -> mostrarProduto(localizarProdutoNome(produtosCadastradosPorNome));
                case 5 -> mostrarProduto(localizarProdutoID(produtosCadastradosPorId));
                case 6 -> simularVendas(produtosCadastradosPorId); // Simula gerador para Tarefa 2
                case 7 -> pedidosDoProduto(); // Tarefa 3
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
            pausa();
        } while(opcao != 0);       

        teclado.close();    
    }
}