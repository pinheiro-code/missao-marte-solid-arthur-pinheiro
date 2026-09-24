package solidexercicio10.service;

import java.util.List;
import java.util.Random;
import solidexercicio10.model.Asteroide;
import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.FabricaPassageiro;
import solidexercicio10.model.Inimigo;
import solidexercicio10.model.Limites;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Nave;

/**
 * Sorteia o cenário de uma missão nova.
 *
 * <p>SRP: no original, montar o mapa, sortear posições e escolher o tipo de
 * cada passageiro estava dentro de {@code Main}, junto com o loop da
 * partida. Separar as duas coisas deixa o serviço da partida livre para
 * cuidar só das regras de voo.</p>
 *
 * <p>OCP: o catálogo de passageiros chega pelo construtor. Esta classe nunca
 * cita {@code Professor}, {@code Engenheiro} ou {@code Astronauta}.</p>
 */
public class GeradorDeMissao {

    private static final String ID_NAVE = "A-1";
    private static final int CAPACIDADE_NAVE = 5;

    private final List<FabricaPassageiro> catalogoPassageiros;
    private final Random random;

    public GeradorDeMissao(List<FabricaPassageiro> catalogoPassageiros, Random random) {
        if (catalogoPassageiros.isEmpty()) {
            throw new IllegalArgumentException("É preciso ao menos um tipo de passageiro");
        }
        this.catalogoPassageiros = List.copyOf(catalogoPassageiros);
        this.random = random;
    }

    public Missao gerar(Dificuldade dificuldade, int tamanhoMapa) {
        Limites limites = Limites.quadrado(tamanhoMapa);
        if (limites.quantidadeDeCelulas() < dificuldade.getTotalDeEntidades()) {
            // Sem esta guarda o sorteio abaixo nunca acharia posição livre e travaria.
            throw new IllegalArgumentException("Mapa de tamanho " + tamanhoMapa
                    + " não comporta a dificuldade " + dificuldade);
        }
        Missao missao = new Missao(new Nave(ID_NAVE, CAPACIDADE_NAVE), limites);

        for (int i = 0; i < dificuldade.getQuantidadePassageiros(); i++) {
            int[] posicao = sortearPosicaoLivre(missao);
            FabricaPassageiro fabrica = catalogoPassageiros.get(i % catalogoPassageiros.size());
            missao.addPassageiro(fabrica.criar(posicao[0], posicao[1]));
        }
        for (int i = 0; i < dificuldade.getQuantidadeAsteroides(); i++) {
            int[] posicao = sortearPosicaoLivre(missao);
            missao.addAsteroide(new Asteroide(posicao[0], posicao[1]));
        }
        for (int i = 0; i < dificuldade.getQuantidadeInimigos(); i++) {
            int[] posicao = sortearPosicaoLivre(missao);
            missao.addInimigo(new Inimigo(posicao[0], posicao[1]));
        }
        return missao;
    }

    /** Sorteia até achar célula vazia, como no original; a nave ocupa a origem. */
    private int[] sortearPosicaoLivre(Missao missao) {
        Limites limites = missao.getLimites();
        while (true) {
            int x = random.nextInt(limites.maxX() - limites.minX() + 1) + limites.minX();
            int y = random.nextInt(limites.maxY() - limites.minY() + 1) + limites.minY();
            if (!missao.posicaoOcupada(x, y)) {
                return new int[] {x, y};
            }
        }
    }
}
