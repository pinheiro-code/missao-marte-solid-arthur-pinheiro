package solidexercicio10.presentation;

import java.util.List;
import java.util.Scanner;
import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Passageiro;
import solidexercicio10.model.ResultadoPartida;
import solidexercicio10.repository.RankingEntry;

/**
 * Implementação das quatro interfaces de interação usando o console.
 *
 * <p>SRP: é o único ponto do projeto que chama {@code System.out} e
 * {@code Scanner}. No código original essas chamadas estavam espalhadas por
 * doze dos dezenove métodos de {@code Main}, misturadas com as regras do jogo.</p>
 *
 * <p>ISP na prática: a classe implementa as quatro interfaces, mas cada
 * serviço enxerga apenas o pedaço que usa.</p>
 */
public class ConsoleUI implements EntradaJogo, SaidaJogo, EntradaPartida, SaidaPartida {

    private static final String PILOTO_PADRAO = "Piloto Anônimo";
    private static final int TAMANHO_MAPA_PADRAO = 5;
    private static final String SEPARADOR =
            "================================================================";

    private final Scanner scanner;
    private final MapaRenderer mapaRenderer;

    public ConsoleUI(Scanner scanner, MapaRenderer mapaRenderer) {
        this.scanner = scanner;
        this.mapaRenderer = mapaRenderer;
    }

    // ----- EntradaJogo -----

    @Override
    public String lerOpcaoMenu() {
        return lerLinha("Escolha uma opção: ", "1");
    }

    @Override
    public boolean confirmarResetDoRanking() {
        String resposta = lerLinha(
                "Você realmente deseja limpar o histórico de ranking? (s/n): ", "n")
                .toLowerCase();
        return resposta.equals("s") || resposta.equals("sim");
    }

    // ----- EntradaPartida -----

    @Override
    public String lerNomePiloto() {
        String nome = lerLinha(System.lineSeparator() + "Digite o nome do piloto: ", PILOTO_PADRAO);
        return nome.isEmpty() ? PILOTO_PADRAO : nome;
    }

    @Override
    public Dificuldade lerDificuldade() {
        return Dificuldade.deString(
                lerLinha("Escolha a Dificuldade (facil/medio/dificil): ", "medio"));
    }

    @Override
    public int lerTamanhoMapa() {
        String entrada = lerLinha("Tamanho do mapa (ex: 5 para mapa de -5 a +5): ",
                String.valueOf(TAMANHO_MAPA_PADRAO));
        try {
            int tamanho = Integer.parseInt(entrada);
            return tamanho > 0 ? tamanho : TAMANHO_MAPA_PADRAO;
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida, usando tamanho padrão (" + TAMANHO_MAPA_PADRAO + ").");
            return TAMANHO_MAPA_PADRAO;
        }
    }

    @Override
    public void aguardarDecolagem() {
        System.out.println("Pressione Enter para decolar!");
        lerLinha("", "");
    }

    @Override
    public char lerComando() {
        String entrada = lerLinha("Comando (w/s/a/d/c/q): ", "").toLowerCase();
        return entrada.isEmpty() ? ' ' : entrada.charAt(0);
    }

    // ----- SaidaJogo -----

    @Override
    public void exibirBoasVindas() {
        System.out.println(SEPARADOR);
        System.out.println("             MISSÃO MARTE UNIFOR - VERSÃO SOLID                  ");
        System.out.println(SEPARADOR);
        System.out.println("  Pilote sua nave, salve os passageiros e desvie dos perigos!   ");
        System.out.println(SEPARADOR);
    }

    @Override
    public void exibirMenu() {
        System.out.println();
        System.out.println("--- MENU PRINCIPAL ---");
        System.out.println("1. Iniciar Nova Missão");
        System.out.println("2. Visualizar Ranking Top 5");
        System.out.println("3. Resetar Histórico de Ranking");
        System.out.println("4. Sair do Jogo");
        System.out.println("----------------------");
    }

    @Override
    public void exibirOpcaoInvalida() {
        System.out.println("Opção inválida. Tente novamente.");
    }

    @Override
    public void exibirRanking(List<RankingEntry> ranking) {
        System.out.println();
        System.out.println("====== RANKING TOP 5 PILOTOS ======");
        if (ranking.isEmpty()) {
            System.out.println(" - Nenhum registro encontrado. Seja o primeiro a jogar!");
        } else {
            int posicao = 1;
            for (RankingEntry entrada : ranking) {
                System.out.printf("%d. %s - %d pts | Dificuldade: %s | Coletados: %d | Tempo: %ds | %s%n",
                        posicao++, entrada.nome(), entrada.pontuacao(), entrada.dificuldade(),
                        entrada.passageirosColetados(), entrada.tempoJogoSegundos(), entrada.dataHora());
            }
        }
        System.out.println("===================================");
    }

    @Override
    public void exibirRankingResetado() {
        System.out.println("Ranking resetado com sucesso!");
    }

    @Override
    public void exibirOperacaoCancelada() {
        System.out.println("Operação cancelada.");
    }

    @Override
    public void exibirEstatisticas(ResultadoPartida resultado, List<RankingEntry> rankingAtual) {
        System.out.println("Estatísticas da Partida:");
        System.out.printf(" - Pontuação Final: %d pontos%n", resultado.pontuacaoFinal());
        System.out.printf(" - Movimentos Efetuados: %d%n", resultado.movimentos());
        System.out.printf(" - Tempo de Jogo: %d segundos%n", resultado.tempoSegundos());
        System.out.printf(" - Passageiros Resgatados: %d%n", resultado.passageirosResgatados());

        int recorde = rankingAtual.isEmpty() ? 0 : rankingAtual.get(0).pontuacao();
        if (resultado.pontuacaoFinal() > recorde && recorde > 0) {
            System.out.println("🏆 Novo recorde absoluto do sistema!");
        } else if (recorde > 0) {
            System.out.printf(" - Recorde atual a ser batido: %d pontos (Piloto: %s)%n",
                    recorde, rankingAtual.get(0).nome());
        }
        System.out.println(SEPARADOR);
    }

    @Override
    public void exibirEntradaNoRanking(int posicoesNoTop) {
        System.out.printf("Parabéns! Você entrou para o Top %d de pilotos!%n", posicoesNoTop);
    }

    @Override
    public void exibirDespedida() {
        System.out.println();
        System.out.println("Obrigado por jogar a Missão Marte Unifor!");
    }

    // ----- SaidaPartida -----

    @Override
    public void exibirAjusteDeTamanhoDeMapa(Dificuldade dificuldade, int tamanhoAjustado) {
        System.out.printf("A dificuldade %s precisa de %d posições e não cabe nesse mapa. "
                        + "Usando o tamanho %d.%n",
                dificuldade, dificuldade.getTotalDeEntidades(), tamanhoAjustado);
    }

    @Override
    public void exibirInicioDaMissao(Dificuldade dificuldade) {
        System.out.println();
        System.out.println("Iniciando missão na dificuldade " + dificuldade + "...");
    }

    @Override
    public void exibirEstadoDaPartida(Missao missao, int pontuacao, String piloto) {
        System.out.print(mapaRenderer.desenhar(missao, pontuacao, piloto));
        System.out.printf("Nave em (%d,%d) | Pontos: %d | Vidas: %d | A bordo: %d/%d | Restantes: %d%n",
                missao.getNave().getX(), missao.getNave().getY(), pontuacao,
                missao.getNave().getVidas(), missao.getNave().getQuantidadeEmbarcada(),
                missao.getNave().getCapacidade(), missao.getPassageiros().size());
    }

    @Override
    public void exibirComandoInvalido() {
        System.out.println("Comando inválido.");
    }

    @Override
    public void exibirSemPassageiroNaPosicao() {
        System.out.println("Nenhum passageiro nesta posição.");
    }

    @Override
    public void exibirEmbarque(Passageiro passageiro, int bonus) {
        System.out.printf("Passageiro %s embarcado com sucesso! +%d pontos!%n",
                passageiro.getNome(), bonus);
    }

    @Override
    public void exibirNaveCheia() {
        System.out.println("Nave cheia! Não há espaço para mais passageiros.");
    }

    @Override
    public void exibirColisao(int vidasRestantes) {
        System.out.printf("Alerta! Colisão detectada! Vidas restantes: %d%n", vidasRestantes);
    }

    @Override
    public void exibirNaveDestruida() {
        System.out.println("GAME OVER! A nave foi destruída.");
    }

    @Override
    public void exibirPontuacaoZerada() {
        System.out.println("Combustível/Pontuação zerada! Missão perdida.");
    }

    @Override
    public void exibirMissaoAbortada() {
        System.out.println("Missão abortada pelo piloto.");
    }

    @Override
    public void exibirTodosResgatados() {
        System.out.println("✨ ALERTA: Todos os passageiros resgatados! "
                + "Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.");
    }

    @Override
    public void exibirVitoria() {
        System.out.println();
        System.out.println(SEPARADOR);
        System.out.println("🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).");
        System.out.println("Retornando à órbita marciana com todos os passageiros. Missão cumprida!");
        System.out.println(SEPARADOR);
    }

    private String lerLinha(String prompt, String fallback) {
        if (prompt != null && !prompt.isEmpty()) {
            System.out.print(prompt);
        }
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return fallback;
    }
}
