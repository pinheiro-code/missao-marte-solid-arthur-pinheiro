package solidexercicio10.model;

/**
 * Contrato de quem se desloca pelo mapa.
 *
 * <p>LSP: os limites fazem parte da assinatura, então toda implementação
 * é obrigada a respeitá-los. Qualquer {@code Movel} pode ser usado no lugar
 * de outro sem que o chamador precise saber se aquele tipo específico
 * valida a borda do mapa.</p>
 */
public interface Movel extends Posicionavel {

    /**
     * Desloca a entidade, ignorando o movimento que sairia do mapa.
     *
     * @return {@code true} se a posição mudou de fato
     */
    boolean mover(Direcao direcao, Limites limites);
}
