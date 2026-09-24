package solidexercicio10;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import solidexercicio10.model.Astronauta;
import solidexercicio10.model.Engenheiro;
import solidexercicio10.model.FabricaPassageiro;
import solidexercicio10.model.Professor;
import solidexercicio10.presentation.ConsoleUI;
import solidexercicio10.presentation.MapaRenderer;
import solidexercicio10.repository.ArquivoRankingRepository;
import solidexercicio10.repository.RankingRepository;
import solidexercicio10.service.GeradorDeMissao;
import solidexercicio10.service.JogoService;
import solidexercicio10.service.PartidaService;

/**
 * Ponto de entrada e raiz de composição da versão refatorada.
 *
 * <p>SRP: a única responsabilidade desta classe é montar o grafo de objetos
 * e disparar o jogo. Ela não tem regra de jogo, não imprime mapa e não toca
 * em arquivo. Os 557 métodos e linhas de {@code exercicio10.Main} viraram as
 * quinze linhas de montagem abaixo.</p>
 *
 * <p>DIP: é aqui, e só aqui, que as implementações concretas aparecem. Para
 * rodar o jogo com o ranking apenas em memória, basta trocar
 * {@code ArquivoRankingRepository} por {@code MemoriaRankingRepository}
 * nesta linha; nenhuma outra classe muda.</p>
 *
 * <p>OCP: o catálogo de passageiros é declarado aqui. Um tipo novo entra com
 * uma classe nova no modelo e uma linha a mais nesta lista.</p>
 */
public class Main {

    private static final String ARQUIVO_RANKING = "ranking-solid-exercicio10.json";
    private static final int TAMANHO_DO_TOP = 5;

    public static void main(String[] args) {
        // A ordem da lista reproduz o rodízio de tipos do jogo original.
        List<FabricaPassageiro> catalogoPassageiros = List.of(
                (x, y) -> new Professor("Dr. Silva", x, y),
                (x, y) -> new Engenheiro("Eng. Rosa", x, y),
                (x, y) -> new Professor("Dr. Lima", x, y),
                (x, y) -> new Engenheiro("Eng. Carlos", x, y),
                (x, y) -> new Astronauta("Ast. Maria", x, y));

        Random random = new Random();
        RankingRepository rankingRepository =
                new ArquivoRankingRepository(ARQUIVO_RANKING, TAMANHO_DO_TOP);

        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleUI console = new ConsoleUI(scanner, new MapaRenderer(catalogoPassageiros));
            GeradorDeMissao gerador = new GeradorDeMissao(catalogoPassageiros, random);
            PartidaService partidaService = new PartidaService(gerador, console, console, random);

            new JogoService(partidaService, rankingRepository, console, console).executarLoop();
        }
    }
}
