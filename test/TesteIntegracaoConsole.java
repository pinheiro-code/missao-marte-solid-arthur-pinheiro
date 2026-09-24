import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import solidexercicio10.model.Astronauta;
import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Engenheiro;
import solidexercicio10.model.FabricaPassageiro;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Nave;
import solidexercicio10.model.Passageiro;
import solidexercicio10.model.Professor;
import solidexercicio10.model.ResultadoPartida;
import solidexercicio10.presentation.ConsoleUI;
import solidexercicio10.presentation.EntradaPartida;
import solidexercicio10.presentation.MapaRenderer;
import solidexercicio10.presentation.SaidaPartida;
import solidexercicio10.repository.ArquivoRankingRepository;
import solidexercicio10.repository.RankingEntry;
import solidexercicio10.repository.RankingRepository;
import solidexercicio10.service.GeradorDeMissao;
import solidexercicio10.service.JogoService;
import solidexercicio10.service.PartidaService;

/**
 * Teste de integração do jogo montado exatamente como no {@code Main}:
 * {@code ConsoleUI} de verdade lendo de um {@code Scanner}, ranking gravado
 * em arquivo de verdade, e o menu percorrido de ponta a ponta.
 *
 * <p>Estratégia: com uma semente fixa, uma primeira execução usa um piloto
 * automático apenas para descobrir a sequência de teclas que vence aquela
 * missão. Em seguida, o jogo real é executado com a mesma semente e aquelas
 * teclas chegando pela entrada padrão, de modo que a partida se repete
 * idêntica. Nada é simulado: o que roda na segunda parte é a mesma
 * composição de objetos do {@code Main}.</p>
 */
public class TesteIntegracaoConsole {

    private static final long SEMENTE = 20260924L;
    private static final Dificuldade DIFICULDADE = Dificuldade.FACIL;
    private static final int TAMANHO_MAPA = 2;
    private static final String PILOTO = "Arthur";
    private static final Path ARQUIVO = Paths.get("ranking-integracao-temporario.json");

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) throws Exception {
        List<Character> teclas = descobrirSequenciaVencedora();
        checar("Piloto automático produziu uma sequência de comandos", !teclas.isEmpty());

        Files.deleteIfExists(ARQUIVO);
        String saidaDoJogo = executarJogoReal(teclas);

        checar("Jogo real exibiu o menu principal", saidaDoJogo.contains("MENU PRINCIPAL"));
        checar("Jogo real desenhou o mapa", saidaDoJogo.contains("Mapa da Missão"));
        checar("Jogo real embarcou passageiro",
                saidaDoJogo.contains("embarcado com sucesso"));
        checar("Jogo real concluiu a missão",
                saidaDoJogo.contains("DECOLAGEM AUTORIZADA"));
        checar("Jogo real exibiu as estatísticas",
                saidaDoJogo.contains("Estatísticas da Partida"));
        checar("Jogo real anunciou a entrada no Top 5",
                saidaDoJogo.contains("entrou para o Top 5"));
        checar("Jogo real listou o ranking com o piloto",
                saidaDoJogo.contains("1. " + PILOTO + " - "));
        checar("Jogo real se despediu ao sair",
                saidaDoJogo.contains("Obrigado por jogar"));

        checar("Arquivo de ranking foi criado em disco", Files.exists(ARQUIVO));
        String json = Files.readString(ARQUIVO, StandardCharsets.UTF_8);
        checar("Arquivo contém o nome do piloto", json.contains("\"name\":\"" + PILOTO + "\""));
        checar("Arquivo contém a dificuldade da partida", json.contains("\"dificuldade\":\"FACIL\""));
        checar("Arquivo contém os passageiros coletados",
                json.contains("\"passageirosColetados\":4"));

        RankingRepository relido = new ArquivoRankingRepository(ARQUIVO.toString(), 5);
        List<RankingEntry> ranking = relido.listarTop(5);
        checar("Ranking relido tem uma entrada", ranking.size() == 1);
        checar("Ranking relido tem o piloto certo", ranking.get(0).nome().equals(PILOTO));
        checar("Ranking relido tem pontuação positiva", ranking.get(0).pontuacao() > 0);

        System.out.println();
        System.out.println("Trecho da saída real do jogo:");
        System.out.println(recortar(saidaDoJogo));
        System.out.println("Conteúdo do arquivo de ranking:");
        System.out.println("  " + json);

        Files.deleteIfExists(ARQUIVO);

        System.out.println();
        System.out.printf("%d verificações, %d falha(s).%n", total, falhas);
        if (falhas > 0) {
            System.exit(1);
        }
        System.out.println("INTEGRACAO OK");
    }

    /** Primeira execução: só para gravar as teclas que vencem esta missão. */
    private static List<Character> descobrirSequenciaVencedora() {
        GravadorDeComandos gravador = new GravadorDeComandos();
        PartidaService partida = new PartidaService(
                new GeradorDeMissao(catalogo(), new Random(SEMENTE)),
                gravador, gravador, new Random(SEMENTE));
        ResultadoPartida resultado = partida.executar();
        checar("Sequência descoberta termina em vitória", resultado.vitoria());
        return gravador.teclas;
    }

    /** Segunda execução: o jogo de verdade, igual ao Main, lendo as teclas. */
    private static String executarJogoReal(List<Character> teclas) {
        StringBuilder stdin = new StringBuilder();
        stdin.append("1").append(System.lineSeparator());
        stdin.append(PILOTO).append(System.lineSeparator());
        stdin.append("facil").append(System.lineSeparator());
        stdin.append(TAMANHO_MAPA).append(System.lineSeparator());
        stdin.append(System.lineSeparator());
        for (char tecla : teclas) {
            stdin.append(tecla).append(System.lineSeparator());
        }
        stdin.append("2").append(System.lineSeparator());
        stdin.append("4").append(System.lineSeparator());

        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(stdin.toString())) {
            System.setOut(new PrintStream(capturada, true, StandardCharsets.UTF_8));

            ConsoleUI console = new ConsoleUI(scanner, new MapaRenderer(catalogo()));
            RankingRepository repositorio = new ArquivoRankingRepository(ARQUIVO.toString(), 5);
            GeradorDeMissao gerador = new GeradorDeMissao(catalogo(), new Random(SEMENTE));
            PartidaService partida = new PartidaService(gerador, console, console, new Random(SEMENTE));
            new JogoService(partida, repositorio, console, console).executarLoop();
        } finally {
            System.setOut(saidaOriginal);
        }
        return capturada.toString(StandardCharsets.UTF_8);
    }

    /** Piloto automático que também guarda cada tecla que decidiu apertar. */
    private static class GravadorDeComandos implements EntradaPartida, SaidaPartida {

        private final List<Character> teclas = new ArrayList<>();
        private Missao ultimaMissao;

        @Override
        public String lerNomePiloto() {
            return PILOTO;
        }

        @Override
        public Dificuldade lerDificuldade() {
            return DIFICULDADE;
        }

        @Override
        public int lerTamanhoMapa() {
            return TAMANHO_MAPA;
        }

        @Override
        public void aguardarDecolagem() { }

        @Override
        public char lerComando() {
            char tecla = decidir();
            teclas.add(tecla);
            return tecla;
        }

        private char decidir() {
            Nave nave = ultimaMissao.getNave();
            if (ultimaMissao.passageiroNaPosicaoDaNave().isPresent()) {
                return 'c';
            }

            int alvoX = Missao.PLATAFORMA_X;
            int alvoY = Missao.PLATAFORMA_Y;
            int menorDistancia = Integer.MAX_VALUE;
            for (Passageiro passageiro : ultimaMissao.getPassageiros()) {
                int distancia = Math.abs(passageiro.getX() - nave.getX())
                        + Math.abs(passageiro.getY() - nave.getY());
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    alvoX = passageiro.getX();
                    alvoY = passageiro.getY();
                }
            }

            if (nave.getX() != alvoX) {
                return alvoX > nave.getX() ? 'd' : 'a';
            }
            if (nave.getY() != alvoY) {
                return alvoY > nave.getY() ? 's' : 'w';
            }
            return 'c';
        }

        @Override
        public void exibirEstadoDaPartida(Missao missao, int pontuacao, String piloto) {
            this.ultimaMissao = missao;
        }

        @Override
        public void exibirAjusteDeTamanhoDeMapa(Dificuldade dificuldade, int tamanhoAjustado) { }

        @Override
        public void exibirInicioDaMissao(Dificuldade dificuldade) { }

        @Override
        public void exibirComandoInvalido() { }

        @Override
        public void exibirSemPassageiroNaPosicao() { }

        @Override
        public void exibirEmbarque(Passageiro passageiro, int bonus) { }

        @Override
        public void exibirNaveCheia() { }

        @Override
        public void exibirColisao(int vidasRestantes) { }

        @Override
        public void exibirNaveDestruida() { }

        @Override
        public void exibirPontuacaoZerada() { }

        @Override
        public void exibirMissaoAbortada() { }

        @Override
        public void exibirTodosResgatados() { }

        @Override
        public void exibirVitoria() { }
    }

    private static List<FabricaPassageiro> catalogo() {
        return List.of(
                (x, y) -> new Professor("Dr. Silva", x, y),
                (x, y) -> new Engenheiro("Eng. Rosa", x, y),
                (x, y) -> new Professor("Dr. Lima", x, y),
                (x, y) -> new Engenheiro("Eng. Carlos", x, y),
                (x, y) -> new Astronauta("Ast. Maria", x, y));
    }

    /** Mostra da vitória em diante, que é a parte que interessa como evidência. */
    private static String recortar(String saida) {
        int inicio = saida.indexOf("DECOLAGEM AUTORIZADA");
        if (inicio < 0) {
            return saida;
        }
        int recuo = saida.lastIndexOf(System.lineSeparator(), Math.max(inicio - 80, 0));
        return saida.substring(Math.max(recuo, 0)).trim();
    }

    private static void checar(String descricao, boolean condicao) {
        total++;
        if (condicao) {
            System.out.println("  ok   " + descricao);
        } else {
            falhas++;
            System.out.println("  FALHA " + descricao);
        }
    }
}
