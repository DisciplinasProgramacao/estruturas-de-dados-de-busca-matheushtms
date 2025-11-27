// --- ARQUIVO Entrada.java ---
public class Entrada<K, V> {
    private K chave;
    private V valor;

    public Entrada(K chave, V valor) {
        this.chave = chave;
        this.valor = valor;
    }

    public K getChave() { return chave; }
    public V getValor() { return valor; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entrada<?, ?> entrada = (Entrada<?, ?>) obj;
        return chave.equals(entrada.chave);
    }
    
    @Override
    public String toString() {
        return "Chave: " + chave + " -> Valor: " + valor;
    }
}

// --- ARQUIVO TabelaHash.java ---
import java.util.NoSuchElementException;

public class TabelaHash<K, V> implements IMapeamento<K, V> {

    private Lista<Entrada<K, V>>[] tabelaHash;
    private int capacidade;
    private int comparacoes;
    private long inicio, termino;

    @SuppressWarnings("unchecked")
    public TabelaHash(int capacidade) {
        if (capacidade < 1) throw new IllegalStateException("Capacidade deve ser > 0");
        this.capacidade = capacidade;
        tabelaHash = (Lista<Entrada<K, V>>[]) new Lista[capacidade];
        for (int i = 0; i < capacidade; i++)
            tabelaHash[i] = new Lista<>();
    }

    private int funcaoHash(K chave) {
        return Math.abs(chave.hashCode() % capacidade);
    }

    @Override
    public int inserir(K chave, V item) {
        int posicao = funcaoHash(chave);
        Entrada<K, V> entrada = new Entrada<>(chave, item);
        try {
            tabelaHash[posicao].pesquisar(entrada);
            throw new IllegalArgumentException("Item já existe!");
        } catch (NoSuchElementException e) {
            tabelaHash[posicao].inserirFinal(entrada);
            return posicao;
        }
    }

    @Override
    public V pesquisar(K chave) {
        int posicao = funcaoHash(chave);
        Entrada<K, V> procurado = new Entrada<>(chave, null);
        Entrada<K, V> encontrado = tabelaHash[posicao].pesquisar(procurado);
        return encontrado.getValor();
    }

    @Override
    public V remover(K chave) {
        int posicao = funcaoHash(chave);
        Entrada<K, V> procurado = new Entrada<>(chave, null);
        return tabelaHash[posicao].remover(procurado).getValor();
    }
    
    @Override
    public String percorrer() {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<capacidade; i++){
            sb.append("Pos[").append(i).append("]: ");
            if(tabelaHash[i].vazia()) sb.append("vazia\n");
            else sb.append(tabelaHash[i].toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int tamanho() {
        int tam = 0;
        for(Lista<Entrada<K,V>> l : tabelaHash) tam += l.tamanho();
        return tam;
    }
    
    @Override public long getComparacoes() { return 0; }
    @Override public double getTempo() { return 0; }
}