# Análise do código inicial (`src/exercicio10`)

Levantamento feito **antes** de escrever qualquer linha da versão refatorada, como o
tutorial pede. Serve de linha de base: tudo o que está listado em "comportamento a
preservar" precisa continuar funcionando depois da refatoração.

## Como o código inicial foi verificado

```bash
javac -encoding UTF-8 -d out src/exercicio10/*.java
java -cp out exercicio10.Main
```

Compila sem erros. O jogo foi executado e percorrido pelo menu, por uma partida com
movimentação, por uma tentativa de embarque, pelo ranking e pelo reset.

## Tamanho das classes

| Classe | Linhas | Observação |
| --- | ---: | --- |
| `Main` | **557** | concentra menu, partida, desenho de mapa, estatísticas e persistência |
| `Missao` | 74 | estado da partida, razoável |
| `Nave` | 57 | estado da nave, razoável |
| `Inimigo` | 43 | movimento aleatório |
| `Dificuldade` | 28 | enum, só conversão de texto |
| `Passageiro` | 24 | classe concreta com pontuação padrão de 10 |
| `Asteroide` | 18 | colisão |
| `Astronauta`, `Engenheiro`, `Professor` | 12 cada | só sobrescrevem `getPontuacao()` |

`Main` sozinha tem mais linhas que todas as outras nove classes somadas.

## Motivos diferentes que faziam `Main` mudar

Esta é a pergunta orientadora principal da atividade. Levantei **seis** motivos
independentes, e é por isso que a classe viola SRP:

1. **Mudou o fluxo do menu** (`main`, `exibirMenu`): acrescentar uma opção.
2. **Mudaram as regras da partida** (`jogarPartida`): pontuação, vidas, condição de vitória.
3. **Mudou o desenho da tela** (`desenharMapa`, `exibirEstatisticas`, `exibirRankingCompleto`): símbolos, legenda, formatação.
4. **Mudou a geração do cenário** (`criarNovaMissao`, `posicaoOcupada`, `criarPassageiroPolimorfico`): quantidade de perigos, tipos de passageiro.
5. **Mudou o formato de armazenamento** (`loadRanking`, `saveRanking`, `parseRankingJson`, `resetarRanking`): o JSON é lido e escrito na mão.
6. **Mudou a leitura de entrada** (`lerLinha`, `lerDificuldade`, `lerTamanhoMapa`): validação e valores padrão.

## Acoplamentos que impedem teste automatizado

- `System.out` aparece 65 vezes, em 12 dos 19 métodos de `Main`, misturado com regra de jogo.
- `Scanner` é lido dentro do mesmo laço que decide a partida.
- `Paths.get("ranking.json")` é constante estática: não há como apontar para outro destino.
- Nenhuma regra pode ser exercitada sem console e sem disco, então não existe teste possível.

## Pontos frágeis encontrados na leitura

| Onde | O que acontece |
| --- | --- |
| `Passageiro` | é concreta e devolve 10 pontos por padrão, então dá para criar um passageiro "genérico" cuja pontuação não significa nada |
| `desenharMapa` | usa `instanceof Engenheiro` / `instanceof Astronauta` para escolher o símbolo; um tipo novo exige editar este `if` |
| `criarPassageiroPolimorfico` | `switch (indice % 5)` fixo; um tipo novo exige editar o `switch` |
| `Nave.getPassageiros()` | devolve a lista interna; qualquer cliente pode embarcar por fora da regra de capacidade |
| `Missao.getPassageiros()` e demais | idem, expõem as listas internas |
| `minX/maxX/minY/maxY` | quatro parâmetros soltos repetidos em oito assinaturas |
| `Main.RankingEntry` | classe interna privada; o ranking não pode ser reaproveitado por ninguém |
| `loadRanking` | engole `IOException` e devolve lista vazia, escondendo arquivo corrompido |

## Comportamento a preservar

Lista usada como critério de aceite da refatoração. Cada item foi verificado nas duas
versões (ver `REVISAO-SOLID.md`, seção de testes).

1. Menu com as quatro opções e mensagem de opção inválida.
2. Nome do piloto, com `Piloto Anônimo` quando vazio.
3. Dificuldade por texto, aceitando com e sem acento, com `MEDIO` como padrão.
4. Tamanho do mapa, com 5 como padrão para entrada inválida ou não positiva.
5. Pontuação inicial: Fácil 30, Médio 20, Difícil 15.
6. Cenário por dificuldade: Fácil 4/1/1, Médio 5/2/2, Difícil 5/3/3 (passageiros/asteroides/inimigos).
7. Pontuação por tipo: Professor 10, Engenheiro 15, Astronauta 20.
8. Comandos `w/s/a/d` (mover), `c` (embarcar), `q` (sair). `w` sobe na tela, ou seja, diminui `y`.
9. Cada movimento custa 1 ponto, **inclusive** quando a borda bloqueia o deslocamento.
10. Nave com 5 lugares e 3 vidas; colisão com asteroide ou inimigo tira uma vida.
11. Mapa com cabeçalho de coordenadas, símbolos `@ P E T # X L .` e a legenda nesta ordem.
12. Plataforma de pouso `L` em (0,0), visível quando a nave não está lá.
13. Vitória exige todos embarcados **e** a nave de volta em (0,0).
14. Estatísticas exibidas apenas na vitória, com recorde a bater.
15. Ranking Top 5 em JSON, ordenado por pontuação, com reset confirmado por `s`/`sim`.

## Achado de infraestrutura

O `.gitignore` do repositório trazia as linhas:

```
src/solid*
src/exer*
```

Com elas, `git add src/solidexercicio10` não falha e `git status` não mostra nada: a
pasta inteira da refatoração é descartada em silêncio. Foi o que produziu um commit
vazio no histórico deste repositório em 23/09/2026. As duas linhas foram removidas,
porque a atividade exige justamente que essas pastas sejam entregues.
