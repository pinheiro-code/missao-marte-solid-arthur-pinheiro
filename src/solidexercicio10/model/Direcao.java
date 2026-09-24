package solidexercicio10.model;

import java.util.Optional;

/**
 * Direções de deslocamento no mapa.
 *
 * <p>Os deltas seguem o desenho do mapa original: {@code y} cresce para
 * baixo na tela, logo {@code CIMA} decrementa {@code y}.</p>
 */
public enum Direcao {

    CIMA(0, -1, 'w'),
    BAIXO(0, 1, 's'),
    ESQUERDA(-1, 0, 'a'),
    DIREITA(1, 0, 'd');

    private final int deltaX;
    private final int deltaY;
    private final char comando;

    Direcao(int deltaX, int deltaY, char comando) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.comando = comando;
    }

    public int deltaX() {
        return deltaX;
    }

    public int deltaY() {
        return deltaY;
    }

    public char comando() {
        return comando;
    }

    /** Converte a tecla digitada pelo piloto, se ela for uma direção. */
    public static Optional<Direcao> doComando(char tecla) {
        for (Direcao direcao : values()) {
            if (direcao.comando == tecla) {
                return Optional.of(direcao);
            }
        }
        return Optional.empty();
    }
}
