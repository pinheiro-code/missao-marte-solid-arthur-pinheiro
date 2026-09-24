package solidexercicio10.model;

/** Obstáculo fixo. Colide com a nave quando ocupam a mesma célula. */
public class Asteroide extends EntidadeMapa {

    public Asteroide(int x, int y) {
        super(x, y);
    }

    @Override
    public char getSimbolo() {
        return '#';
    }
}
