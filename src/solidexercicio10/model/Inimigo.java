package solidexercicio10.model;

import java.util.Random;

/**
 * Nave hostil que vagueia pelo mapa.
 *
 * <p>LSP: implementa {@code Movel} com a mesma garantia da nave do piloto,
 * ou seja, nunca ultrapassa os limites recebidos.</p>
 */
public class Inimigo extends EntidadeMapa implements Movel {

    public Inimigo(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean mover(Direcao direcao, Limites limites) {
        int novoX = x + direcao.deltaX();
        int novoY = y + direcao.deltaY();
        if (!limites.contem(novoX, novoY)) {
            return false;
        }
        x = novoX;
        y = novoY;
        return true;
    }

    /** Escolhe uma direção ao acaso, como no comportamento original. */
    public void moverAleatoriamente(Random random, Limites limites) {
        Direcao[] direcoes = Direcao.values();
        mover(direcoes[random.nextInt(direcoes.length)], limites);
    }

    @Override
    public char getSimbolo() {
        return 'X';
    }
}
