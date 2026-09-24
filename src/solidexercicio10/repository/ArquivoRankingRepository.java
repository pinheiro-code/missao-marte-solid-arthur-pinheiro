package solidexercicio10.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import solidexercicio10.model.Dificuldade;

/**
 * Ranking persistido em um arquivo JSON, no mesmo formato do jogo original.
 *
 * <p>SRP: é a única classe do projeto que sabe que existe um arquivo, um
 * caminho e um formato de serialização. O serviço do jogo nunca viu
 * {@code Files} nem {@code Path}, ao contrário do {@code Main} original, que
 * abria, lia, escrevia e apagava o arquivo no meio do fluxo da partida.</p>
 *
 * <p>Quantas linhas o arquivo guarda é decisão de quem é dono do arquivo,
 * por isso o limite chega pelo construtor e o corte acontece na gravação,
 * preservando o comportamento do original (Top 5 em disco).</p>
 */
public class ArquivoRankingRepository implements RankingRepository {

    private final Path arquivo;
    private final int limiteArmazenado;

    public ArquivoRankingRepository(String nomeArquivo, int limiteArmazenado) {
        this.arquivo = Paths.get(nomeArquivo);
        this.limiteArmazenado = limiteArmazenado;
    }

    @Override
    public void salvar(RankingEntry entrada) {
        List<RankingEntry> entradas = new ArrayList<>(carregar());
        entradas.add(entrada);
        List<RankingEntry> preservadas = entradas.stream()
                .sorted(Comparator.comparingInt(RankingEntry::pontuacao).reversed())
                .limit(limiteArmazenado)
                .toList();
        escrever(preservadas);
    }

    @Override
    public List<RankingEntry> listarTop(int limite) {
        return carregar().stream()
                .sorted(Comparator.comparingInt(RankingEntry::pontuacao).reversed())
                .limit(limite)
                .toList();
    }

    @Override
    public void limpar() {
        try {
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível resetar o ranking", e);
        }
    }

    private List<RankingEntry> carregar() {
        if (!Files.exists(arquivo)) {
            return List.of();
        }
        try {
            String json = Files.readString(arquivo, StandardCharsets.UTF_8).trim();
            return desserializar(json);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler o ranking", e);
        }
    }

    private void escrever(List<RankingEntry> entradas) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < entradas.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(serializar(entradas.get(i)));
        }
        json.append("]");
        try {
            Files.writeString(arquivo, json.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível salvar o ranking", e);
        }
    }

    private String serializar(RankingEntry entrada) {
        return "{\"name\":\"" + escapar(entrada.nome())
                + "\",\"score\":" + entrada.pontuacao()
                + ",\"dificuldade\":\"" + entrada.dificuldade().name()
                + "\",\"passageirosColetados\":" + entrada.passageirosColetados()
                + ",\"dataHora\":\"" + escapar(entrada.dataHora())
                + "\",\"tempoJogo\":" + entrada.tempoJogoSegundos()
                + "}";
    }

    private String escapar(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private List<RankingEntry> desserializar(String json) {
        List<RankingEntry> entradas = new ArrayList<>();
        int indice = 0;
        while (indice < json.length()) {
            int inicio = json.indexOf('{', indice);
            if (inicio < 0) {
                break;
            }
            int fim = json.indexOf('}', inicio);
            if (fim < 0) {
                break;
            }
            lerEntrada(json.substring(inicio + 1, fim)).ifPresent(entradas::add);
            indice = fim + 1;
        }
        return entradas;
    }

    private java.util.Optional<RankingEntry> lerEntrada(String corpo) {
        String nome = null;
        Integer pontuacao = null;
        Dificuldade dificuldade = Dificuldade.MEDIO;
        int passageiros = 0;
        String dataHora = "";
        long tempo = 0;

        for (String parte : corpo.split(",")) {
            String[] par = parte.split(":", 2);
            if (par.length != 2) {
                continue;
            }
            String chave = par[0].trim().replace("\"", "");
            String valor = par[1].trim();
            switch (chave) {
                case "name" -> nome = semAspas(valor);
                case "score" -> pontuacao = inteiro(valor);
                case "dificuldade" -> dificuldade = Dificuldade.deString(semAspas(valor));
                case "passageirosColetados" -> {
                    Integer lido = inteiro(valor);
                    passageiros = lido == null ? 0 : lido;
                }
                case "dataHora" -> dataHora = semAspas(valor);
                case "tempoJogo" -> {
                    Integer lido = inteiro(valor);
                    tempo = lido == null ? 0 : lido;
                }
                default -> { }
            }
        }

        if (nome == null || pontuacao == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(
                new RankingEntry(nome, pontuacao, dificuldade, passageiros, dataHora, tempo));
    }

    private String semAspas(String valor) {
        if (valor.length() >= 2 && valor.startsWith("\"") && valor.endsWith("\"")) {
            return valor.substring(1, valor.length() - 1).replace("\\\"", "\"");
        }
        return valor;
    }

    private Integer inteiro(String valor) {
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
