package solidexercicio10.service;

import java.util.Optional;
import java.util.Random;
import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Direcao;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Nave;
import solidexercicio10.model.Passageiro;
import solidexercicio10.model.ResultadoPartida;
import solidexercicio10.presentation.EntradaPartida;
import solidexercicio10.presentation.SaidaPartida;

/**
 * Regras de uma partida, do briefing ao encerramento.
 *
 * <p>SRP: cuida do voo e nada mais. Não desenha o mapa, não formata texto e
 * não sabe que existe um arquivo de ranking.</p>
 *
 * <p>DIP: depende das interfaces {@code EntradaPartida} e
 * {@code SaidaPartida}, nunca de {@code Scanner} ou {@code System.out}.
 * É o que permite rodar esta classe em um teste automatizado.</p>
 */
public class PartidaService {

    private static final char COMANDO_EMBARCAR = 'c';
    private static final char COMANDO_SAIR = 'q';
    private static final char COMANDO_VAZIO = ' ';

    private final GeradorDeMissao geradorDeMissao;
    private final EntradaPartida entrada;
    private final SaidaPartida saida;
    private final Random random;

    public PartidaService(GeradorDeMissao geradorDeMissao, EntradaPartida entrada,
                          SaidaPartida saida, Random random) {
        this.geradorDeMissao = geradorDeMissao;
        this.entrada = entrada;
        this.saida = saida;
        this.random = random;
    }

    public ResultadoPartida executar() {
        String piloto = entrada.lerNomePiloto();
        Dificuldade dificuldade = entrada.lerDificuldade();
        int tamanhoMapa = ajustarTamanhoDoMapa(dificuldade, entrada.lerTamanhoMapa());

        saida.exibirInicioDaMissao(dificuldade);
        entrada.aguardarDecolagem();

        Missao missao = geradorDeMissao.gerar(dificuldade, tamanhoMapa);
        Nave nave = missao.getNave();
        int pontuacao = dificuldade.getPontuacaoInicial();
        int movimentos = 0;
        boolean vitoria = false;
        long inicio = System.currentTimeMillis();

        boolean emVoo = true;
        while (emVoo) {
            saida.exibirEstadoDaPartida(missao, pontuacao, piloto);

            char comando = entrada.lerComando();
            if (comando == COMANDO_VAZIO) {
                continue;
            }
            if (comando == COMANDO_SAIR) {
                saida.exibirMissaoAbortada();
                break;
            }

            if (comando == COMANDO_EMBARCAR) {
                pontuacao += tentarEmbarcar(missao);
            } else {
                Optional<Direcao> direcao = Direcao.doComando(comando);
                if (direcao.isEmpty()) {
                    saida.exibirComandoInvalido();
                    continue;
                }
                nave.mover(direcao.get(), missao.getLimites());
                // O original cobra o movimento mesmo quando a borda bloqueia.
                pontuacao--;
                movimentos++;
            }

            missao.moverInimigos(random);

            if (missao.verificaColisao()) {
                nave.perderVida();
                if (nave.estaDestruida()) {
                    saida.exibirNaveDestruida();
                    break;
                }
                saida.exibirColisao(nave.getVidas());
            }

            if (pontuacao <= 0) {
                saida.exibirPontuacaoZerada();
                break;
            }

            if (missao.todosEmbarcados()) {
                if (missao.naveNaPlataformaDePouso()) {
                    saida.exibirVitoria();
                    vitoria = true;
                    emVoo = false;
                } else {
                    saida.exibirTodosResgatados();
                }
            }
        }

        long tempoSegundos = (System.currentTimeMillis() - inicio) / 1000;
        return new ResultadoPartida(piloto, dificuldade, pontuacao, movimentos,
                tempoSegundos, nave.getQuantidadeEmbarcada(), vitoria);
    }

    /**
     * Garante que o cenário caiba no mapa.
     *
     * <p>Divergência deliberada do original: lá, um mapa menor que o cenário
     * faz o sorteio de posições livres rodar para sempre e o jogo trava sem
     * mensagem. Ver REVISAO-SOLID.md.</p>
     */
    private int ajustarTamanhoDoMapa(Dificuldade dificuldade, int tamanhoPedido) {
        int minimo = dificuldade.getTamanhoMinimoDeMapa();
        if (tamanhoPedido >= minimo) {
            return tamanhoPedido;
        }
        saida.exibirAjusteDeTamanhoDeMapa(dificuldade, minimo);
        return minimo;
    }

    /** @return o bônus de pontos do embarque, ou zero quando ele não acontece */
    private int tentarEmbarcar(Missao missao) {
        Optional<Passageiro> passageiro = missao.passageiroNaPosicaoDaNave();
        if (passageiro.isEmpty()) {
            saida.exibirSemPassageiroNaPosicao();
            return 0;
        }
        if (!missao.embarcarPassageiroNaPosicaoDaNave()) {
            saida.exibirNaveCheia();
            return 0;
        }
        int bonus = passageiro.get().getPontuacao();
        saida.exibirEmbarque(passageiro.get(), bonus);
        return bonus;
    }
}
