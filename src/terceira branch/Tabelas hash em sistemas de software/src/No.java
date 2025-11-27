// --- ARQUIVO IMapeamento.java ---
public interface IMapeamento<K, V> {
    int inserir(K chave, V valor);
    V pesquisar(K chave);
    V remover(K chave);
    int tamanho();
    String percorrer();
    long getComparacoes();
    double getTempo();
}

// --- ARQUIVO No.java ---
public class No<K, V> {
    private K chave;
    private V item;
    private No<K, V> esquerda;
    private No<K, V> direita;

    public No(K chave, V item) {
        this.chave = chave;
        this.item = item;
        this.esquerda = null;
        this.direita = null;
    }
    
    public K getChave() { return chave; }
    public void setChave(K chave) { this.chave = chave; }
    public V getItem() { return item; }
    public void setItem(V item) { this.item = item; }
    public No<K, V> getEsquerda() { return esquerda; }
    public void setEsquerda(No<K, V> esquerda) { this.esquerda = esquerda; }
    public No<K, V> getDireita() { return direita; }
    public void setDireita(No<K, V> direita) { this.direita = direita; }
}

// --- ARQUIVO ABB.java ---
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K, V> implements IMapeamento<K, V> {

    private No<K, V> raiz;
    private Comparator<K> comparador;
    private int tamanho;

    private void init(Comparator<K> comparador) {
        raiz = null;
        tamanho = 0;
        this.comparador = comparador;
    }

    @SuppressWarnings("unchecked")
    public ABB() { init((Comparator<K>) Comparator.naturalOrder()); }

    public ABB(Comparator<K> comparador) { init(comparador); }

    public ABB(ABB<?, V> original, Function<V, K> funcaoChave, Comparator<K> comparador) {
        init(comparador);
        copiarArvore(original.raiz, funcaoChave);
    }
    
    private <T> void copiarArvore(No<?, V> raizOriginal, Function<V, K> funcaoChave) {
        if (raizOriginal != null) {
            copiarArvore(raizOriginal.getEsquerda(), funcaoChave);
            V item = raizOriginal.getItem();
            inserir(funcaoChave.apply(item), item);
            copiarArvore(raizOriginal.getDireita(), funcaoChave);
        }
    }

    public Boolean vazia() { return (this.raiz == null); }

    @Override
    public V pesquisar(K chave) { return pesquisar(raiz, chave); }

    private V pesquisar(No<K, V> r, K procurado) {
        if (r == null) throw new NoSuchElementException("Item não localizado!");
        int comp = comparador.compare(procurado, r.getChave());
        if (comp == 0) return r.getItem();
        else if (comp < 0) return pesquisar(r.getEsquerda(), procurado);
        else return pesquisar(r.getDireita(), procurado);
    }

    @Override
    public int inserir(K chave, V item) {
        this.raiz = inserir(this.raiz, chave, item);
        tamanho++;
        return tamanho;
    }

    protected No<K, V> inserir(No<K, V> r, K chave, V item) {
        if (r == null) r = new No<>(chave, item);
        else {
            int comp = comparador.compare(chave, r.getChave());
            if (comp < 0) r.setEsquerda(inserir(r.getEsquerda(), chave, item));
            else if (comp > 0) r.setDireita(inserir(r.getDireita(), chave, item));
            else throw new IllegalArgumentException("Item duplicado.");
        }
        return r;
    }

    @Override
    public String toString() { return percorrer(); }

    @Override
    public String percorrer() { return caminhamentoEmOrdem(raiz); }

    private String caminhamentoEmOrdem(No<K, V> r) {
        if (r == null) return "";
        return caminhamentoEmOrdem(r.getEsquerda()) + r.getItem() + "\n" + caminhamentoEmOrdem(r.getDireita());
    }

    @Override
    public V remover(K chave) {
        V removido = pesquisar(chave); // Garante que existe
        raiz = remover(raiz, chave);
        tamanho--;
        return removido;
    }

    protected No<K, V> remover(No<K, V> r, K chave) {
        if (r == null) return null;
        int comp = comparador.compare(chave, r.getChave());
        if (comp < 0) r.setEsquerda(remover(r.getEsquerda(), chave));
        else if (comp > 0) r.setDireita(remover(r.getDireita(), chave));
        else {
            // Encontrou
            if (r.getDireita() == null) return r.getEsquerda();
            if (r.getEsquerda() == null) return r.getDireita();
            r.setEsquerda(removerAntecessor(r, r.getEsquerda()));
        }
        return r;
    }

    protected No<K, V> removerAntecessor(No<K, V> aRemover, No<K, V> r) {
        if (r.getDireita() != null) {
            r.setDireita(removerAntecessor(aRemover, r.getDireita()));
        } else {
            aRemover.setChave(r.getChave());
            aRemover.setItem(r.getItem());
            return r.getEsquerda();
        }
        return r;
    }

    @Override public int tamanho() { return tamanho; }
    @Override public long getComparacoes() { return 0; }
    @Override public double getTempo() { return 0; }
}