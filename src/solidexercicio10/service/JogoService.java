package solidexercicio10.service;

import java.util.List;
import solidexercicio10.model.ResultadoPartida;
import solidexercicio10.presentation.EntradaJogo;
import solidexercicio10.presentation.SaidaJogo;
import solidexercicio10.repository.RankingEntry;
import solidexercicio10.repository.RankingRepository;

/**
 * Fluxo do menu principal e a regra de entrada no ranking.
 *
 * <p>SRP: o {@code Main} original fazia menu, partida, desenho de mapa e
 * leitura e escrita de arquivo. Aqui sobrou só a navegação e a decisão de
 * quando uma pontuação merece ser registrada.</p>
 *
 * <p>DIP: recebe {@code RankingRepository}. Nunca soube, e não precisa
 * saber, que por trás existe um arquivo JSON.</p>
 */
public class JogoService {

    private static final int TAMANHO_DO_TOP = 5;

    private static final String OPCAO_JOGAR = "1";
    private static final String OPCAO_RANKING = "2";
    private static final String OPCAO_RESETAR = "3";
    private static final String OPCAO_SAIR = "4";

    private final PartidaService partidaService;
    private final RankingRepository rankingRepository;
    private final EntradaJogo entrada;
    private final SaidaJogo saida;

    public JogoService(PartidaService partidaService, RankingRepository rankingRepository,
                       EntradaJogo entrada, SaidaJogo saida) {
        this.partidaService = partidaService;
        this.rankingRepository = rankingRepository;
        this.entrada = entrada;
        this.saida = saida;
    }

    public void executarLoop() {
        saida.exibirBoasVindas();

        boolean rodando = true;
        while (rodando) {
            saida.exibirMenu();
            switch (entrada.lerOpcaoMenu()) {
                case OPCAO_JOGAR -> jogarPartida();
                case OPCAO_RANKING -> saida.exibirRanking(rankingRepository.listarTop(TAMANHO_DO_TOP));
                case OPCAO_RESETAR -> resetarRanking();
                case OPCAO_SAIR -> {
                    saida.exibirDespedida();
                    rodando = false;
                }
                default -> saida.exibirOpcaoInvalida();
            }
        }
    }

    private void jogarPartida() {
        ResultadoPartida resultado = partidaService.executar();
        if (!resultado.vitoria()) {
            return;
        }

        List<RankingEntry> topAtual = rankingRepository.listarTop(TAMANHO_DO_TOP);
        saida.exibirEstatisticas(resultado, topAtual);

        if (resultado.elegivelParaRanking() && entraNoTop(topAtual, resultado.pontuacaoFinal())) {
            rankingRepository.salvar(RankingEntry.agora(
                    resultado.piloto(),
                    resultado.pontuacaoFinal(),
                    resultado.dificuldade(),
                    resultado.passageirosResgatados(),
                    resultado.tempoSegundos()));
            saida.exibirEntradaNoRanking(TAMANHO_DO_TOP);
        }
    }

    /** Há vaga no Top, ou a pontuação supera a última colocada. */
    private boolean entraNoTop(List<RankingEntry> topAtual, int pontuacao) {
        return topAtual.size() < TAMANHO_DO_TOP
                || pontuacao > topAtual.get(topAtual.size() - 1).pontuacao();
    }

    private void resetarRanking() {
        if (entrada.confirmarResetDoRanking()) {
            rankingRepository.limpar();
            saida.exibirRankingResetado();
        } else {
            saida.exibirOperacaoCancelada();
        }
    }
}
