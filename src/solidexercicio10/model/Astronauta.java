package solidexercicio10.model;

/** Passageiro do tipo Astronauta: vale 20 pontos e aparece como 'T' no mapa. */
public class Astronauta extends Passageiro {

    public Astronauta(String nome, int x, int y) {
        super(nome, "Astronauta", x, y);
    }

    @Override
    public int getPontuacao() {
        return 20;
    }

    @Override
    public char getSimbolo() {
        return 'T';
    }
}
