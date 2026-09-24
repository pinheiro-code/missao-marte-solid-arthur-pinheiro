package solidexercicio10.model;

/**
 * Dificuldade da missão e a configuração de partida que ela implica.
 *
 * <p>SRP e OCP: no código original os valores de cada dificuldade estavam
 * espalhados em dois {@code switch} dentro de {@code Main}
 * ({@code definirPontuacaoInicial} e {@code criarNovaMissao}). Trazê-los para
 * o próprio enum faz com que uma dificuldade nova seja uma constante a mais,
 * sem nenhum {@code switch} para atualizar.</p>
 */
public enum Dificuldade {

    FACIL("Fácil", 30, 4, 1, 1),
    MEDIO("Médio", 20, 5, 2, 2),
    DIFICIL("Difícil", 15, 5, 3, 3);

    private final String rotulo;
    private final int pontuacaoInicial;
    private final int quantidadePassageiros;
    private final int quantidadeAsteroides;
    private final int quantidadeInimigos;

    Dificuldade(String rotulo, int pontuacaoInicial, int quantidadePassageiros,
                int quantidadeAsteroides, int quantidadeInimigos) {
        this.rotulo = rotulo;
        this.pontuacaoInicial = pontuacaoInicial;
        this.quantidadePassageiros = quantidadePassageiros;
        this.quantidadeAsteroides = quantidadeAsteroides;
        this.quantidadeInimigos = quantidadeInimigos;
    }

    public int getPontuacaoInicial() {
        return pontuacaoInicial;
    }

    public int getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public int getQuantidadeAsteroides() {
        return quantidadeAsteroides;
    }

    public int getQuantidadeInimigos() {
        return quantidadeInimigos;
    }

    /** Nave, passageiros e perigos: tudo que precisa de uma célula própria. */
    public int getTotalDeEntidades() {
        return 1 + quantidadePassageiros + quantidadeAsteroides + quantidadeInimigos;
    }

    /**
     * Menor mapa que comporta o cenário desta dificuldade.
     *
     * <p>Sem esse piso o sorteio de posições livres do gerador roda para
     * sempre: com mapa 1 (9 células) e dificuldade Médio (10 entidades) não
     * sobra posição para a última, e o jogo trava sem mensagem. O código
     * original tem o mesmo defeito, e esta é uma divergência deliberada,
     * registrada em REVISAO-SOLID.md.</p>
     */
    public int getTamanhoMinimoDeMapa() {
        int tamanho = 1;
        while (Limites.quadrado(tamanho).quantidadeDeCelulas() < getTotalDeEntidades()) {
            tamanho++;
        }
        return tamanho;
    }

    /** Aceita a entrada do piloto com ou sem acento; o padrão é {@code MEDIO}. */
    public static Dificuldade deString(String valor) {
        if (valor == null) {
            return MEDIO;
        }
        switch (valor.trim().toLowerCase()) {
            case "facil":
            case "fácil":
                return FACIL;
            case "dificil":
            case "difícil":
                return DIFICIL;
            default:
                return MEDIO;
        }
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
