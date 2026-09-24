package solidexercicio10.model;

/**
 * Contrato mínimo de quem ocupa uma coordenada no mapa.
 *
 * <p>ISP: expõe apenas leitura de posição. O renderizador e as regras de
 * colisão precisam somente disso, e não do resto da entidade.</p>
 */
public interface Posicionavel {

    int getX();

    int getY();

    /**
     * Compara posição sem expor os campos, evitando que cada cliente
     * reescreva {@code getX() == x && getY() == y}.
     */
    default boolean estaEm(int x, int y) {
        return getX() == x && getY() == y;
    }

    default boolean estaNaMesmaPosicaoDe(Posicionavel outro) {
        return estaEm(outro.getX(), outro.getY());
    }
}
