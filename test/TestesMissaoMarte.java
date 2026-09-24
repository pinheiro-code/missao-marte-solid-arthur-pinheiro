import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import solidexercicio10.model.Asteroide;
import solidexercicio10.model.Astronauta;
import solidexercicio10.model.Dificuldade;
import solidexercicio10.model.Direcao;
import solidexercicio10.model.Engenheiro;
import solidexercicio10.model.FabricaPassageiro;
import solidexercicio10.model.Inimigo;
import solidexercicio10.model.Limites;
import solidexercicio10.model.Missao;
import solidexercicio10.model.Nave;
import solidexercicio10.model.Passageiro;
import solidexercicio10.model.Professor;
import solidexercicio10.model.ResultadoPartida;
import solidexercicio10.presentation.EntradaPartida;
import solidexercicio10.presentation.MapaRenderer;
import solidexercicio10.presentation.SaidaPartida;
import solidexercicio10.repository.ArquivoRankingRepository;
import solidexercicio10.repository.MemoriaRankingRepository;
import solidexercicio10.repository.RankingEntry;
import solidexercicio10.repository.RankingRepository;
import solidexercicio10.service.GeradorDeMissao;
import solidexercicio10.service.PartidaService;

/**
 * Testes da versão refatorada, sem dependência de framework.
 *
 * <p>Rode com {@code java -cp out-solid;out-test TestesMissaoMarte}. O
 * processo termina com código 1 se qualquer verificação falhar.</p>
 *
 * <p>Estes testes só existem porque a refatoração tornou possível executar as
 * regras do jogo sem console e sem disco: {@code PartidaService} recebe
 * {@code EntradaPartida} e {@code SaidaPartida}, e o ranking entra pela
 * abstração {@code RankingRepository}. No código original, o mesmo fluxo
 * estava preso dentro de {@code Main} com {@code Scanner} e
 * {@code System.out} embutidos, e não havia como testá-lo.</p>
 */
public class TestesMissaoMarte {

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        testePontuacaoPorTipoDePassageiro();
        testeSubstituicaoDePassageiroRespeitaContrato();
        testeNaveRespeitaCapacidade();
        testeListaDePassageirosEhImutavelParaFora();
        testeMovelNaoUltrapassaOsLimites();
        testeColisaoComAsteroideEInimigo();
        testeEmbarqueSomenteNaPosicaoDaNave();
        testeRendererUsaSimboloPolimorfico();
        testeRendererAceitaTipoNovoSemAlteracao();
        testeRankingEmMemoriaOrdenaEDelimita();
        testeRankingEmArquivoPersisteELimpa();
        testeMapaPequenoDemaisNaoTravaOJogo();
        testePartidaCompletaVenceERegistraNoRanking();

        System.out.println();
        System.out.printf("%d verificações, %d falha(s).%n", total, falhas);
        if (falhas > 0) {
            System.exit(1);
        }
        System.out.println("TODOS OS TESTES PASSARAM");
    }

    // ----------------------------------------------------------------- LSP

    private static void testePontuacaoPorTipoDePassageiro() {
        checar("Professor vale 10 pontos, como no original",
                new Professor("Dr. Silva", 0, 0).getPontuacao() == 10);
        checar("Engenheiro vale 15 pontos, como no original",
                new Engenheiro("Eng. Rosa", 0, 0).getPontuacao() == 15);
        checar("Astronauta vale 20 pontos, como no original",
                new Astronauta("Ast. Maria", 0, 0).getPontuacao() == 20);
    }

    /** LSP: qualquer subtipo funciona onde a classe base é esperada. */
    private static void testeSubstituicaoDePassageiroRespeitaContrato() {
        List<Passageiro> passageiros = List.of(
                new Professor("Dr. Silva", 1, 1),
                new Engenheiro("Eng. Rosa", 2, 2),
                new Astronauta("Ast. Maria", 3, 3));

        for (Passageiro passageiro : passageiros) {
            checar("Passageiro " + passageiro.getTipo() + " informa pontuação positiva",
                    passageiro.getPontuacao() > 0);
            checar("Passageiro " + passageiro.getTipo() + " informa tipo preenchido",
                    !passageiro.getTipo().isBlank());
            checar("Passageiro " + passageiro.getTipo() + " informa símbolo de mapa",
                    passageiro.getSimbolo() != '\0');
        }
    }

    // --------------------------------------------------------------- Nave

    private static void testeNaveRespeitaCapacidade() {
        Nave nave = new Nave("A-1", 2);
        checar("Primeiro embarque aceito", nave.embarcar(new Professor("A", 0, 0)));
        checar("Segundo embarque aceito", nave.embarcar(new Professor("B", 0, 0)));
        checar("Terceiro embarque recusado com a nave cheia",
                !nave.embarcar(new Professor("C", 0, 0)));
        checar("Nave ficou com exatamente 2 passageiros", nave.getQuantidadeEmbarcada() == 2);
    }

    private static void testeListaDePassageirosEhImutavelParaFora() {
        Nave nave = new Nave("A-1", 1);
        nave.embarcar(new Professor("A", 0, 0));
        boolean bloqueou;
        try {
            nave.getPassageiros().add(new Professor("Clandestino", 0, 0));
            bloqueou = false;
        } catch (UnsupportedOperationException e) {
            bloqueou = true;
        }
        checar("getPassageiros() não permite furar a capacidade por fora", bloqueou);
    }

    /** LSP: o contrato de Movel vale para toda implementação. */
    private static void testeMovelNaoUltrapassaOsLimites() {
        Limites limites = new Limites(-1, 1, -1, 1);

        Nave nave = new Nave("A-1", 5);
        nave.mover(Direcao.DIREITA, limites);
        checar("Nave move dentro do mapa", nave.getX() == 1);
        checar("Nave recusa o movimento que sairia do mapa",
                !nave.mover(Direcao.DIREITA, limites));
        checar("Nave continua na borda", nave.getX() == 1);

        Inimigo inimigo = new Inimigo(-1, -1);
        checar("Inimigo recusa o movimento que sairia do mapa",
                !inimigo.mover(Direcao.ESQUERDA, limites));
        checar("Inimigo continua na borda", inimigo.getX() == -1);
    }

    // ------------------------------------------------------------- Missao

    private static void testeColisaoComAsteroideEInimigo() {
        Missao missaoComAsteroide = new Missao(new Nave("A-1", 5), Limites.quadrado(3));
        checar("Sem perigos não há colisão", !missaoComAsteroide.verificaColisao());
        missaoComAsteroide.addAsteroide(new Asteroide(0, 0));
        checar("Asteroide na posição da nave gera colisão", missaoComAsteroide.verificaColisao());

        Missao missaoComInimigo = new Missao(new Nave("A-1", 5), Limites.quadrado(3));
        missaoComInimigo.addInimigo(new Inimigo(0, 0));
        checar("Inimigo na posição da nave gera colisão", missaoComInimigo.verificaColisao());
    }

    private static void testeEmbarqueSomenteNaPosicaoDaNave() {
        Missao missao = new Missao(new Nave("A-1", 5), Limites.quadrado(3));
        missao.addPassageiro(new Professor("Dr. Silva", 2, 2));

        checar("Sem passageiro sob a nave não há quem embarcar",
                missao.passageiroNaPosicaoDaNave().isEmpty());
        checar("Embarque falha longe do passageiro",
                !missao.embarcarPassageiroNaPosicaoDaNave());
        checar("Passageiro continua na superfície", missao.getPassageiros().size() == 1);

        Missao missaoNoAlvo = new Missao(new Nave("A-1", 5), Limites.quadrado(3));
        missaoNoAlvo.addPassageiro(new Professor("Dr. Silva", 0, 0));
        checar("Embarque funciona sobre o passageiro",
                missaoNoAlvo.embarcarPassageiroNaPosicaoDaNave());
        checar("Passageiro saiu da superfície", missaoNoAlvo.getPassageiros().isEmpty());
        checar("Passageiro está a bordo",
                missaoNoAlvo.getNave().getQuantidadeEmbarcada() == 1);
        checar("Missão só termina com a nave na plataforma",
                missaoNoAlvo.missaoCumprida());
    }

    // ------------------------------------------------------- Apresentação

    private static void testeRendererUsaSimboloPolimorfico() {
        Missao missao = new Missao(new Nave("A-1", 5), Limites.quadrado(1));
        missao.addPassageiro(new Engenheiro("Eng. Rosa", 1, 1));
        missao.addAsteroide(new Asteroide(-1, -1));
        missao.addInimigo(new Inimigo(1, -1));

        String mapa = new MapaRenderer(catalogoPadrao()).desenhar(missao, 20, "Arthur");

        checar("Mapa desenha a nave", mapa.contains("@"));
        checar("Mapa desenha o engenheiro", mapa.contains("E"));
        checar("Mapa desenha o asteroide", mapa.contains("#"));
        checar("Mapa desenha o inimigo", mapa.contains("X"));
        checar("Mapa mostra a pontuação recebida", mapa.contains("Pontos: 20"));
        checar("Mapa mostra o piloto recebido", mapa.contains("Piloto: Arthur"));
        checar("Legenda mantém a ordem do original",
                mapa.contains("Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta,"
                        + " #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio"));
    }

    /** OCP: um tipo novo aparece na legenda sem alterar o renderizador. */
    private static void testeRendererAceitaTipoNovoSemAlteracao() {
        class Medico extends Passageiro {
            Medico(String nome, int x, int y) {
                super(nome, "Médico", x, y);
            }

            @Override
            public int getPontuacao() {
                return 25;
            }

            @Override
            public char getSimbolo() {
                return 'M';
            }
        }

        List<FabricaPassageiro> catalogo = new ArrayList<>(catalogoPadrao());
        catalogo.add((x, y) -> new Medico("Dra. Ana", x, y));

        Missao missao = new Missao(new Nave("A-1", 5), Limites.quadrado(1));
        missao.addPassageiro(new Medico("Dra. Ana", 1, 1));

        String mapa = new MapaRenderer(catalogo).desenhar(missao, 10, "Arthur");
        checar("Tipo novo aparece na legenda sem tocar no MapaRenderer",
                mapa.contains("M=Médico"));
        checar("Tipo novo é desenhado no mapa", mapa.contains(" M"));
    }

    // --------------------------------------------------------- Persistência

    private static void testeRankingEmMemoriaOrdenaEDelimita() {
        RankingRepository repositorio = new MemoriaRankingRepository();
        repositorio.salvar(entrada("Ana", 10));
        repositorio.salvar(entrada("Bruno", 30));
        repositorio.salvar(entrada("Caio", 20));

        List<RankingEntry> top = repositorio.listarTop(2);
        checar("Top respeita o limite pedido", top.size() == 2);
        checar("Maior pontuação vem primeiro", top.get(0).nome().equals("Bruno"));
        checar("Segunda colocação correta", top.get(1).nome().equals("Caio"));

        repositorio.limpar();
        checar("Limpar esvazia o ranking em memória", repositorio.listarTop(5).isEmpty());
    }

    private static void testeRankingEmArquivoPersisteELimpa() {
        Path arquivo = Paths.get("ranking-teste-temporario.json");
        try {
            Files.deleteIfExists(arquivo);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        RankingRepository repositorio = new ArquivoRankingRepository(arquivo.toString(), 2);
        checar("Ranking inexistente começa vazio", repositorio.listarTop(5).isEmpty());

        repositorio.salvar(new RankingEntry("Ana \"Foguete\"", 10, Dificuldade.FACIL,
                3, "2026-09-24 10:00:00", 42));
        repositorio.salvar(entrada("Bruno", 30));
        repositorio.salvar(entrada("Caio", 20));

        List<RankingEntry> top = repositorio.listarTop(5);
        checar("Arquivo guarda no máximo o limite configurado", top.size() == 2);
        checar("Arquivo mantém os melhores", top.get(0).nome().equals("Bruno"));

        // Releitura por uma instância nova prova que os dados foram para o disco.
        RankingRepository outraInstancia = new ArquivoRankingRepository(arquivo.toString(), 2);
        List<RankingEntry> relido = outraInstancia.listarTop(5);
        checar("Dados sobrevivem a uma instância nova", relido.size() == 2);
        checar("Pontuação persistida corretamente", relido.get(0).pontuacao() == 30);
        checar("Dificuldade persistida corretamente",
                relido.get(1).dificuldade() == Dificuldade.MEDIO);

        outraInstancia.limpar();
        checar("Limpar apaga o arquivo", !Files.exists(arquivo));
    }

    // --------------------------------------------- Mapa pequeno demais

    /**
     * Regressão do defeito herdado do original: mapa 1 com dificuldade Médio
     * (9 células para 10 entidades) fazia o sorteio de posição livre rodar
     * para sempre, e o jogo travava sem mensagem.
     */
    private static void testeMapaPequenoDemaisNaoTravaOJogo() {
        checar("Fácil precisa de 7 posições", Dificuldade.FACIL.getTotalDeEntidades() == 7);
        checar("Médio precisa de 10 posições", Dificuldade.MEDIO.getTotalDeEntidades() == 10);
        checar("Difícil precisa de 12 posições", Dificuldade.DIFICIL.getTotalDeEntidades() == 12);

        checar("Fácil cabe no mapa 1 (9 células)",
                Dificuldade.FACIL.getTamanhoMinimoDeMapa() == 1);
        checar("Médio exige mapa 2 (25 células)",
                Dificuldade.MEDIO.getTamanhoMinimoDeMapa() == 2);
        checar("Difícil exige mapa 2 (25 células)",
                Dificuldade.DIFICIL.getTamanhoMinimoDeMapa() == 2);

        GeradorDeMissao gerador = new GeradorDeMissao(catalogoPadrao(), new Random(1L));
        boolean recusou;
        try {
            gerador.gerar(Dificuldade.MEDIO, 1);
            recusou = false;
        } catch (IllegalArgumentException e) {
            recusou = true;
        }
        checar("Gerador recusa mapa que não comporta o cenário, em vez de travar", recusou);

        // O serviço ajusta antes de gerar, então a partida roda normalmente.
        PilotoQueDesiste piloto = new PilotoQueDesiste(Dificuldade.MEDIO, 1);
        ResultadoPartida resultado = new PartidaService(
                new GeradorDeMissao(catalogoPadrao(), new Random(1L)),
                piloto, piloto, new Random(1L)).executar();

        checar("Partida com mapa pequeno termina em vez de travar", resultado != null);
        checar("Piloto foi avisado do ajuste de tamanho", piloto.avisadoComTamanho == 2);
    }

    /** Pede um mapa pequeno demais e aborta na primeira jogada. */
    private static class PilotoQueDesiste implements EntradaPartida, SaidaPartida {

        private final Dificuldade dificuldade;
        private final int tamanhoPedido;
        private int avisadoComTamanho = -1;

        PilotoQueDesiste(Dificuldade dificuldade, int tamanhoPedido) {
            this.dificuldade = dificuldade;
            this.tamanhoPedido = tamanhoPedido;
        }

        @Override
        public String lerNomePiloto() {
            return "Piloto de Teste";
        }

        @Override
        public Dificuldade lerDificuldade() {
            return dificuldade;
        }

        @Override
        public int lerTamanhoMapa() {
            return tamanhoPedido;
        }

        @Override
        public void aguardarDecolagem() { }

        @Override
        public char lerComando() {
            return 'q';
        }

        @Override
        public void exibirAjusteDeTamanhoDeMapa(Dificuldade dificuldade, int tamanhoAjustado) {
            this.avisadoComTamanho = tamanhoAjustado;
        }

        @Override
        public void exibirEstadoDaPartida(Missao missao, int pontuacao, String piloto) { }

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

    // -------------------------------------------------------- Fluxo inteiro

    /**
     * Roda uma partida inteira até a vitória com um piloto automático,
     * sem console e sem arquivo, e confere o registro no ranking.
     */
    private static void testePartidaCompletaVenceERegistraNoRanking() {
        Random random = new Random(20260924L);
        GeradorDeMissao gerador = new GeradorDeMissao(catalogoPadrao(), random);
        PilotoAutomatico piloto = new PilotoAutomatico();
        PartidaService partidaService =
                new PartidaService(gerador, piloto, piloto, random);

        ResultadoPartida resultado = partidaService.executar();

        checar("Partida terminou em vitória", resultado.vitoria());
        checar("Todos os 4 passageiros da dificuldade Fácil foram resgatados",
                resultado.passageirosResgatados() == 4);
        checar("Pontuação final positiva", resultado.pontuacaoFinal() > 0);
        checar("Movimentos foram contados", resultado.movimentos() > 0);
        checar("Resultado é elegível para o ranking", resultado.elegivelParaRanking());
        checar("Piloto preservado no resultado", resultado.piloto().equals("Piloto Automático"));
        checar("Dificuldade preservada no resultado",
                resultado.dificuldade() == Dificuldade.FACIL);
        checar("Vitória exige nave na plataforma", piloto.ultimaMissao.naveNaPlataformaDePouso());

        RankingRepository repositorio = new MemoriaRankingRepository();
        repositorio.salvar(RankingEntry.agora(resultado.piloto(), resultado.pontuacaoFinal(),
                resultado.dificuldade(), resultado.passageirosResgatados(),
                resultado.tempoSegundos()));
        List<RankingEntry> top = repositorio.listarTop(5);
        checar("Resultado entrou no ranking", top.size() == 1);
        checar("Ranking guardou a pontuação da partida",
                top.get(0).pontuacao() == resultado.pontuacaoFinal());
    }

    /**
     * Piloto de teste: implementa as mesmas interfaces que o console e joga
     * sozinho, indo até o passageiro mais próximo e voltando à plataforma.
     */
    private static class PilotoAutomatico implements EntradaPartida, SaidaPartida {

        private Missao ultimaMissao;

        @Override
        public String lerNomePiloto() {
            return "Piloto Automático";
        }

        @Override
        public Dificuldade lerDificuldade() {
            return Dificuldade.FACIL;
        }

        @Override
        public int lerTamanhoMapa() {
            return 2;
        }

        @Override
        public void aguardarDecolagem() {
            // Nada a esperar fora do console.
        }

        @Override
        public char lerComando() {
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

    // ------------------------------------------------------------ Apoio

    private static List<FabricaPassageiro> catalogoPadrao() {
        return List.of(
                (x, y) -> new Professor("Dr. Silva", x, y),
                (x, y) -> new Engenheiro("Eng. Rosa", x, y),
                (x, y) -> new Professor("Dr. Lima", x, y),
                (x, y) -> new Engenheiro("Eng. Carlos", x, y),
                (x, y) -> new Astronauta("Ast. Maria", x, y));
    }

    private static RankingEntry entrada(String nome, int pontuacao) {
        return new RankingEntry(nome, pontuacao, Dificuldade.MEDIO, 2, "2026-09-24 10:00:00", 30);
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
