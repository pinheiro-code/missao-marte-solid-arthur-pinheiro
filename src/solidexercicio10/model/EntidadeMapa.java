package solidexercicio10.model;

/**
 * Raiz das entidades que ocupam uma célula do mapa.
 *
 * <p>SRP: concentra o que é comum a nave, passageiros, asteroides e inimigos
 * (a coordenada e o símbolo no mapa), sem saber nada sobre pontuação,
 * ranking ou console.</p>
 *
 * <p>OCP: o símbolo é obtido por polimorfismo. O renderizador não precisa de
 * {@code instanceof} para desenhar um tipo novo, ao contrário do
 * {@code Main} original.</p>
 */
public abstract class EntidadeMapa implements Posicionavel {

    protected int x;
    protected int y;

    protected EntidadeMapa(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    /** Caractere usado para desenhar a entidade no mapa. */
    public abstract char getSimbolo();
}
