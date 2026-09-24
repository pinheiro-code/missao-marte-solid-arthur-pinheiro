package solidexercicio10.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Nave do piloto: posição, vidas e passageiros a bordo.
 *
 * <p>SRP: cuida do próprio estado. Quem decide o que fazer quando ela perde
 * a última vida é o serviço, não ela.</p>
 */
public class Nave extends EntidadeMapa implements Movel {

    private static final int VIDAS_INICIAIS = 3;

    private final String id;
    private final int capacidade;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private int vidas = VIDAS_INICIAIS;

    public Nave(String id, int capacidade) {
        super(0, 0);
        this.id = id;
        this.capacidade = capacidade;
    }

    public String getId() {
        return id;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getVidas() {
        return vidas;
    }

    public boolean estaDestruida() {
        return vidas <= 0;
    }

    /**
     * Cópia imutável: o original devolvia a lista interna, e qualquer cliente
     * podia embarcar passageiros por fora da regra de capacidade.
     */
    public List<Passageiro> getPassageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    public int getQuantidadeEmbarcada() {
        return passageiros.size();
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

    /** @return {@code false} quando a nave já está na capacidade máxima */
    public boolean embarcar(Passageiro passageiro) {
        if (passageiros.size() >= capacidade) {
            return false;
        }
        passageiros.add(passageiro);
        return true;
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
    }

    public int pontuacaoEmbarcada() {
        return passageiros.stream().mapToInt(Passageiro::getPontuacao).sum();
    }

    @Override
    public char getSimbolo() {
        return '@';
    }
}
