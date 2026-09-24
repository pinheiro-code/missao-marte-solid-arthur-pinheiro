package solidexercicio10.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Estado de uma partida: a nave, quem ainda está na superfície e os perigos.
 *
 * <p>SRP: responde perguntas sobre o mundo do jogo (houve colisão? há
 * passageiro aqui? todos embarcaram?). Não imprime nada e não decide quando
 * a partida acaba; isso é do serviço.</p>
 *
 * <p>A missão é dona dos próprios limites, o que eliminou os quatro
 * parâmetros {@code minX/maxX/minY/maxY} repetidos em toda chamada do
 * código original.</p>
 */
public class Missao {

    /** Plataforma de pouso: a nave precisa voltar aqui para concluir. */
    public static final int PLATAFORMA_X = 0;
    public static final int PLATAFORMA_Y = 0;

    private final Nave nave;
    private final Limites limites;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Asteroide> asteroides = new ArrayList<>();
    private final List<Inimigo> inimigos = new ArrayList<>();

    public Missao(Nave nave, Limites limites) {
        this.nave = nave;
        this.limites = limites;
    }

    public Nave getNave() {
        return nave;
    }

    public Limites getLimites() {
        return limites;
    }

    public List<Passageiro> getPassageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    public List<Asteroide> getAsteroides() {
        return Collections.unmodifiableList(asteroides);
    }

    public List<Inimigo> getInimigos() {
        return Collections.unmodifiableList(inimigos);
    }

    public void addPassageiro(Passageiro passageiro) {
        passageiros.add(passageiro);
    }

    public void addAsteroide(Asteroide asteroide) {
        asteroides.add(asteroide);
    }

    public void addInimigo(Inimigo inimigo) {
        inimigos.add(inimigo);
    }

    /** Toda entidade desenhável, usada pelo renderizador e pelo sorteio de posições. */
    public List<EntidadeMapa> getEntidades() {
        List<EntidadeMapa> entidades = new ArrayList<>();
        entidades.add(nave);
        entidades.addAll(passageiros);
        entidades.addAll(asteroides);
        entidades.addAll(inimigos);
        return entidades;
    }

    public boolean posicaoOcupada(int x, int y) {
        return getEntidades().stream().anyMatch(entidade -> entidade.estaEm(x, y));
    }

    public boolean verificaColisao() {
        return asteroides.stream().anyMatch(nave::estaNaMesmaPosicaoDe)
                || inimigos.stream().anyMatch(nave::estaNaMesmaPosicaoDe);
    }

    public void moverInimigos(Random random) {
        for (Inimigo inimigo : inimigos) {
            inimigo.moverAleatoriamente(random, limites);
        }
    }

    public Optional<Passageiro> passageiroNaPosicaoDaNave() {
        return passageiros.stream().filter(nave::estaNaMesmaPosicaoDe).findFirst();
    }

    /**
     * Embarca quem estiver sob a nave.
     *
     * <p>O passageiro só sai da superfície se couber a bordo; com a nave
     * cheia ele continua disponível para uma próxima tentativa.</p>
     */
    public boolean embarcarPassageiroNaPosicaoDaNave() {
        Iterator<Passageiro> iterador = passageiros.iterator();
        while (iterador.hasNext()) {
            Passageiro passageiro = iterador.next();
            if (nave.estaNaMesmaPosicaoDe(passageiro)) {
                boolean embarcou = nave.embarcar(passageiro);
                if (embarcou) {
                    iterador.remove();
                }
                return embarcou;
            }
        }
        return false;
    }

    public boolean todosEmbarcados() {
        return passageiros.isEmpty();
    }

    public boolean naveNaPlataformaDePouso() {
        return nave.estaEm(PLATAFORMA_X, PLATAFORMA_Y);
    }

    /** Vitória: ninguém na superfície e a nave acoplada à plataforma. */
    public boolean missaoCumprida() {
        return todosEmbarcados() && naveNaPlataformaDePouso();
    }
}
