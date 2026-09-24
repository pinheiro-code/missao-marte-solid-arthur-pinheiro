package solidexercicio10.model;

/**
 * O que sobrou de uma partida encerrada.
 *
 * <p>Fica no modelo, e não no serviço, para que a apresentação possa exibir
 * as estatísticas sem que a camada de apresentação passe a depender da
 * camada de serviço. Assim o grafo de pacotes continua sem ciclo.</p>
 */
public record ResultadoPartida(String piloto,
                               Dificuldade dificuldade,
                               int pontuacaoFinal,
                               int movimentos,
                               long tempoSegundos,
                               int passageirosResgatados,
                               boolean vitoria) {

    /** Só partidas vencidas com pontuação positiva disputam o ranking. */
    public boolean elegivelParaRanking() {
        return vitoria && pontuacaoFinal > 0;
    }
}
