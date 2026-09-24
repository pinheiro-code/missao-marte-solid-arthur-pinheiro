package solidexercicio10.presentation;

import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Passageiro;

/**
 * O que o serviço da partida precisa mostrar durante o voo.
 *
 * <p>É a maior interface do projeto, porque o jogo original conversa muito
 * com o piloto. Cada método corresponde a um evento da partida, e todos são
 * chamados por {@code PartidaService}. A alternativa, um único
 * {@code mensagem(String)}, deixaria os textos de tela de volta dentro do
 * serviço, que é justamente o acoplamento que esta camada remove.</p>
 */
public interface SaidaPartida {

    /** O mapa pedido não comportava o cenário e precisou ser aumentado. */
    void exibirAjusteDeTamanhoDeMapa(Dificuldade dificuldade, int tamanhoAjustado);

    void exibirInicioDaMissao(Dificuldade dificuldade);

    void exibirEstadoDaPartida(Missao missao, int pontuacao, String piloto);

    void exibirComandoInvalido();

    void exibirSemPassageiroNaPosicao();

    void exibirEmbarque(Passageiro passageiro, int bonus);

    void exibirNaveCheia();

    void exibirColisao(int vidasRestantes);

    void exibirNaveDestruida();

    void exibirPontuacaoZerada();

    void exibirMissaoAbortada();

    void exibirTodosResgatados();

    void exibirVitoria();
}
