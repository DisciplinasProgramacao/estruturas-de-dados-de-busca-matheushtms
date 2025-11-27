public class No<K, V> {

    private K chave;
    private V item;
    private No<K, V> esquerda;
    private No<K, V> direita;
    private int altura; // Necessário para AVL

    public No(K chave, V item) {
        this.chave = chave;
        this.item = item;
        this.esquerda = null;
        this.direita = null;
        this.altura = 0;
    }

    public K getChave() {
        return chave;
    }

    public void setChave(K chave) {
        this.chave = chave;
    }

    public V getItem() {
        return item;
    }

    public void setItem(V item) {
        this.item = item;
    }

    public No<K, V> getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(No<K, V> esquerda) {
        this.esquerda = esquerda;
    }

    public No<K, V> getDireita() {
        return direita;
    }

    public void setDireita(No<K, V> direita) {
        this.direita = direita;
    }

    // --- Métodos Específicos para AVL ---

    public int getAltura() {
        return altura;
    }

    private int altura(No<K, V> no) {
        return (no == null) ? -1 : no.getAltura();
    }

    public void setAltura() {
        this.altura = 1 + Math.max(altura(esquerda), altura(direita));
    }

    public int getFatorBalanceamento() {
        return altura(direita) - altura(esquerda);
    }
}