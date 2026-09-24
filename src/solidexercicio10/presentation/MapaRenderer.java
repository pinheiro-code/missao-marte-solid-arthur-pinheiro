package solidexercicio10.presentation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import solidexercicio10.model.EntidadeMapa;
import solidexercicio10.model.FabricaPassageiro;
import solidexercicio10.model.Limites;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Passageiro;

/**
 * Desenha o mapa da missão.
 *
 * <p>SRP: só formata texto. Devolve uma {@code String} em vez de imprimir,
 * o que permite verificar o desenho em um teste sem capturar o console.</p>
 *
 * <p>OCP: o símbolo de cada entidade vem de {@code getSimbolo()}. O
 * {@code instanceof} encadeado do {@code Main} original sumiu, e a legenda é
 * derivada do mesmo catálogo de passageiros usado para gerar a missão, então
 * um tipo novo aparece no mapa e na legenda sem alterar esta classe.</p>
 */
public class MapaRenderer {

    private static final char CELULA_VAZIA = '.';
    private static final char SIMBOLO_PLATAFORMA = 'L';

    private final Map<Character, String> legendaPassageiros;

    public MapaRenderer(List<FabricaPassageiro> catalogoPassageiros) {
        this.legendaPassageiros = montarLegenda(catalogoPassageiros);
    }

    /** Uma entrada por tipo distinto, na ordem em que o catálogo os declara. */
    private static Map<Character, String> montarLegenda(List<FabricaPassageiro> catalogo) {
        Map<Character, String> legenda = new LinkedHashMap<>();
        for (FabricaPassageiro fabrica : catalogo) {
            Passageiro prototipo = fabrica.criar(0, 0);
            legenda.putIfAbsent(prototipo.getSimbolo(), prototipo.getTipo());
        }
        return legenda;
    }

    public String desenhar(Missao missao, int pontuacao, String piloto) {
        Limites limites = missao.getLimites();
        StringBuilder mapa = new StringBuilder();

        mapa.append(System.lineSeparator());
        mapa.append(String.format("Mapa da Missão (Pontos: %d) - Piloto: %s%n", pontuacao, piloto));

        mapa.append("    ");
        for (int x = limites.minX(); x <= limites.maxX(); x++) {
            mapa.append(String.format(" %2d", x));
        }
        mapa.append(System.lineSeparator());

        mapa.append("    ");
        for (int x = limites.minX(); x <= limites.maxX(); x++) {
            mapa.append(" __");
        }
        mapa.append(System.lineSeparator());

        for (int y = limites.minY(); y <= limites.maxY(); y++) {
            mapa.append(String.format("%3d|", y));
            for (int x = limites.minX(); x <= limites.maxX(); x++) {
                mapa.append(String.format(" %2c", simboloEm(missao, x, y)));
            }
            mapa.append(System.lineSeparator());
        }

        mapa.append(montarTextoLegenda()).append(System.lineSeparator());
        mapa.append("Comandos: w/s/a/d (mover), c (embarcar), q (sair)").append(System.lineSeparator());
        mapa.append("Passageiros na superfície marciana:").append(System.lineSeparator());
        for (Passageiro passageiro : missao.getPassageiros()) {
            mapa.append(String.format(" - %s (%s) em (%d,%d)%n",
                    passageiro.getNome(), passageiro.getTipo(),
                    passageiro.getX(), passageiro.getY()));
        }
        return mapa.toString();
    }

    /**
     * A primeira entidade que ocupa a célula define o símbolo. A ordem de
     * {@code getEntidades()} (nave, passageiros, asteroides, inimigos) é a
     * mesma prioridade de desenho do jogo original.
     */
    private char simboloEm(Missao missao, int x, int y) {
        for (EntidadeMapa entidade : missao.getEntidades()) {
            if (entidade.estaEm(x, y)) {
                return entidade.getSimbolo();
            }
        }
        if (x == Missao.PLATAFORMA_X && y == Missao.PLATAFORMA_Y) {
            return SIMBOLO_PLATAFORMA;
        }
        return CELULA_VAZIA;
    }

    private String montarTextoLegenda() {
        List<String> itens = new ArrayList<>();
        itens.add("@=Nave");
        legendaPassageiros.forEach((simbolo, tipo) -> itens.add(simbolo + "=" + tipo));
        itens.add("#=Asteroide");
        itens.add("X=Inimigo");
        itens.add("L=Plataforma de Pouso");
        itens.add(".=Vazio");
        return "Legenda: " + String.join(", ", itens);
    }
}
