import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<T> {
    
    // Classe interna para o nó da lista
    private class NoLista<T> {
        T item;
        NoLista<T> proximo;
        NoLista(T item) { this.item = item; this.proximo = null; }
    }

    private NoLista<T> primeiro;
    private NoLista<T> ultimo;
    private int tamanho;

    public Lista() {
        primeiro = ultimo = null;
        tamanho = 0;
    }

    public boolean vazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

    // Usado pela TabelaHash
    public void inserirFinal(T item) {
        NoLista<T> novo = new NoLista<>(item);
        if (vazia()) {
            primeiro = ultimo = novo;
        } else {
            ultimo.proximo = novo;
            ultimo = novo;
        }
        tamanho++;
    }

    // Usado pelo Pedido (inserir em posição específica)
    public void inserir(T item, int posicao) {
        if (posicao < 0 || posicao > tamanho) 
            throw new IllegalArgumentException("Posição inválida");
        
        if (posicao == tamanho) {
            inserirFinal(item);
        } else {
            NoLista<T> novo = new NoLista<>(item);
            if (posicao == 0) {
                novo.proximo = primeiro;
                primeiro = novo;
            } else {
                NoLista<T> anterior = primeiro;
                for (int i = 0; i < posicao - 1; i++) {
                    anterior = anterior.proximo;
                }
                novo.proximo = anterior.proximo;
                anterior.proximo = novo;
            }
            tamanho++;
        }
    }

    public T pesquisar(T procurado) {
        NoLista<T> atual = primeiro;
        while (atual != null) {
            if (atual.item.equals(procurado)) {
                return atual.item;
            }
            atual = atual.proximo;
        }
        throw new NoSuchElementException("Item não encontrado na lista");
    }

    public T remover(T procurado) {
        if (vazia()) throw new NoSuchElementException("Lista vazia");

        if (primeiro.item.equals(procurado)) {
            T removido = primeiro.item;
            primeiro = primeiro.proximo;
            if (primeiro == null) ultimo = null;
            tamanho--;
            return removido;
        }

        NoLista<T> atual = primeiro;
        while (atual.proximo != null) {
            if (atual.proximo.item.equals(procurado)) {
                T removido = atual.proximo.item;
                atual.proximo = atual.proximo.proximo;
                if (atual.proximo == null) ultimo = atual;
                tamanho--;
                return removido;
            }
            atual = atual.proximo;
        }
        throw new NoSuchElementException("Item não encontrado para remoção");
    }

    // Usado para somar preços no Pedido
    public double calcularValorTotal(Function<T, Double> extratorValor) {
        double total = 0.0;
        NoLista<T> atual = primeiro;
        while (atual != null) {
            total += extratorValor.apply(atual.item);
            atual = atual.proximo;
        }
        return total;
    }

    // Usado para contar repetições no Pedido
    public int contarRepeticoes(Predicate<T> criterio) {
        int conta = 0;
        NoLista<T> atual = primeiro;
        while (atual != null) {
            if (criterio.test(atual.item)) {
                conta++;
            }
            atual = atual.proximo;
        }
        return conta;
    }
    
    public long getComparacoes() { return 0; } // Stub para TabelaHash

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        NoLista<T> atual = primeiro;
        while (atual != null) {
            sb.append(atual.item.toString()).append("\n");
            atual = atual.proximo;
        }
        return sb.toString();
    }
}