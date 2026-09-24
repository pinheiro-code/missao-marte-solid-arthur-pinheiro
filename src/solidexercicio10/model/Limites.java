package solidexercicio10.model;

/**
 * Fronteiras do mapa da missão.
 *
 * <p>No código original os quatro valores (minX, maxX, minY, maxY) viajavam
 * soltos em oito assinaturas diferentes. Agrupá-los em um objeto de valor
 * remove a lista longa de parâmetros e dá um lugar único para a regra
 * "esta coordenada pertence ao mapa".</p>
 */
public record Limites(int minX, int maxX, int minY, int maxY) {

    /** Mapa quadrado centrado na origem, como o jogo original monta. */
    public static Limites quadrado(int tamanho) {
        return new Limites(-tamanho, tamanho, -tamanho, tamanho);
    }

    public boolean contem(int x, int y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    /** Quantas posições o mapa oferece, usado para saber se o cenário cabe. */
    public int quantidadeDeCelulas() {
        return (maxX - minX + 1) * (maxY - minY + 1);
    }
}
