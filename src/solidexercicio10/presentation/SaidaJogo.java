package solidexercicio10.presentation;

import java.util.List;
import solidexercicio10.repository.RankingEntry;
import solidexercicio10.model.ResultadoPartida;

/**
 * O que o serviço do menu precisa mostrar ao piloto.
 *
 * <p>SRP e DIP: nenhum texto de tela vive dentro do serviço, e o serviço
 * depende desta abstração, não de {@code System.out}. Trocar o console por
 * uma interface gráfica é escrever outra implementação.</p>
 */
public interface SaidaJogo {

    void exibirBoasVindas();

    void exibirMenu();

    void exibirOpcaoInvalida();

    void exibirRanking(List<RankingEntry> ranking);

    void exibirRankingResetado();

    void exibirOperacaoCancelada();

    void exibirEstatisticas(ResultadoPartida resultado, List<RankingEntry> rankingAtual);

    void exibirEntradaNoRanking(int posicoesNoTop);

    void exibirDespedida();
}
