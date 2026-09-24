package solidexercicio10.model;

/** Passageiro do tipo Professor: vale 10 pontos e aparece como 'P' no mapa. */
public class Professor extends Passageiro {

    public Professor(String nome, int x, int y) {
        super(nome, "Professor", x, y);
    }

    @Override
    public int getPontuacao() {
        return 10;
    }

    @Override
    public char getSimbolo() {
        return 'P';
    }
}
