#!/usr/bin/env bash
# Teste funcional do jogo Missao Marte, versao refatorada.
# Roda o console de verdade, com entrada roteirizada, e confere a saida.

CLONE="${1:-$(cd "$(dirname "$0")/.." && pwd)}"
cd "$CLONE" || exit 1

JOGO="java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out-solid solidexercicio10.Main"
RANKING="ranking-solid-exercicio10.json"
TMP=$(mktemp -d)
total=0
falhas=0

rodar() {
  # rodar "<entrada>" -> grava a saida em $TMP/saida.txt
  printf "$1" | timeout 30 $JOGO > "$TMP/saida.bin" 2>&1
  local codigo=$?
  tr -d '\000' < "$TMP/saida.bin" > "$TMP/saida.txt"
  return $codigo
}

checar() {
  # checar "<descricao>" "<texto esperado>"
  total=$((total + 1))
  if grep -qaF "$2" "$TMP/saida.txt"; then
    printf '  ok    %s\n' "$1"
  else
    falhas=$((falhas + 1))
    printf '  FALHA %s\n         esperava: %s\n' "$1" "$2"
  fi
}

checar_ausente() {
  total=$((total + 1))
  if grep -qaF "$2" "$TMP/saida.txt"; then
    falhas=$((falhas + 1))
    printf '  FALHA %s\n         nao deveria aparecer: %s\n' "$1" "$2"
  else
    printf '  ok    %s\n' "$1"
  fi
}

checar_bool() {
  total=$((total + 1))
  if [ "$2" = "0" ]; then
    printf '  ok    %s\n' "$1"
  else
    falhas=$((falhas + 1))
    printf '  FALHA %s\n' "$1"
  fi
}

titulo() { printf '\n%s\n' "$1"; }

rm -f "$RANKING"

# ---------------------------------------------------------------- MENU
titulo "[1] MENU PRINCIPAL"
rodar '4\n'
checar "abre com as boas-vindas" "MISSÃO MARTE UNIFOR"
checar "mostra as 4 opcoes do menu" "4. Sair do Jogo"
checar "sai pela opcao 4" "Obrigado por jogar a Missão Marte Unifor!"

rodar '9\n4\n'
checar "recusa opcao invalida" "Opção inválida. Tente novamente."

rodar 'abc\n4\n'
checar "recusa opcao nao numerica" "Opção inválida. Tente novamente."

# ------------------------------------------------------------- RANKING
titulo "[2] RANKING VAZIO"
rm -f "$RANKING"
rodar '2\n4\n'
checar "ranking vazio avisa que nao ha registro" "Nenhum registro encontrado"
checar "exibe o cabecalho do ranking" "====== RANKING TOP 5 PILOTOS ======"

titulo "[3] RANKING COM DADOS (leitura do arquivo)"
cat > "$RANKING" <<'JSON'
[{"name":"Ana","score":90,"dificuldade":"DIFICIL","passageirosColetados":5,"dataHora":"2026-09-20 10:00:00","tempoJogo":45},{"name":"Bruno","score":80,"dificuldade":"MEDIO","passageirosColetados":5,"dataHora":"2026-09-21 11:00:00","tempoJogo":50},{"name":"Caio","score":70,"dificuldade":"FACIL","passageirosColetados":4,"dataHora":"2026-09-22 12:00:00","tempoJogo":60},{"name":"Duda","score":60,"dificuldade":"FACIL","passageirosColetados":4,"dataHora":"2026-09-22 13:00:00","tempoJogo":55},{"name":"Elis","score":50,"dificuldade":"MEDIO","passageirosColetados":3,"dataHora":"2026-09-23 14:00:00","tempoJogo":70},{"name":"Fabio","score":40,"dificuldade":"FACIL","passageirosColetados":2,"dataHora":"2026-09-23 15:00:00","tempoJogo":80}]
JSON
rodar '2\n4\n'
checar "le o ranking do arquivo e ordena por pontuacao" "1. Ana - 90 pts"
checar "segunda colocacao correta" "2. Bruno - 80 pts"
checar "quinta colocacao correta" "5. Elis - 50 pts"
checar_ausente "corta no top 5, sexto nao aparece" "6. Fabio"
checar "exibe dificuldade na linha do ranking" "Dificuldade: Difícil"
checar "exibe passageiros coletados" "Coletados: 5"
checar "exibe tempo de jogo" "Tempo: 45s"
checar "exibe data e hora" "2026-09-20 10:00:00"

# --------------------------------------------------------------- RESET
titulo "[4] RESET DO RANKING"
rodar '3\nn\n4\n'
checar "reset cancelado com n" "Operação cancelada."
checar_bool "arquivo continua existindo apos cancelar" $([ -f "$RANKING" ] && echo 0 || echo 1)

rodar '3\ns\n4\n'
checar "reset confirmado com s" "Ranking resetado com sucesso!"
checar_bool "arquivo foi apagado" $([ -f "$RANKING" ] && echo 1 || echo 0)

printf '[]' > "$RANKING"
rodar '3\nsim\n4\n'
checar "reset confirmado com a palavra sim" "Ranking resetado com sucesso!"

rm -f "$RANKING"
rodar '3\ns\n2\n4\n'
checar "ranking fica vazio depois do reset" "Nenhum registro encontrado"

# ---------------------------------------------------- ENTRADAS DA PARTIDA
titulo "[5] ESCOLHA DE PILOTO"
rodar '1\n\nfacil\n3\n\nq\n4\n'
checar "nome vazio vira Piloto Anônimo" "Piloto: Piloto Anônimo"

rodar '1\nArthur Pinheiro\nfacil\n3\n\nq\n4\n'
checar "aceita nome com espaco" "Piloto: Arthur Pinheiro"

titulo "[6] ESCOLHA DE DIFICULDADE"
rodar '1\nA\nfacil\n3\n\nq\n4\n'
checar "dificuldade facil sem acento" "Iniciando missão na dificuldade Fácil"
checar "facil comeca com 30 pontos" "Pontos: 30"

rodar '1\nA\nfácil\n3\n\nq\n4\n'
checar "dificuldade facil com acento" "Iniciando missão na dificuldade Fácil"

rodar '1\nA\nMEDIO\n3\n\nq\n4\n'
checar "dificuldade medio em maiuscula" "Iniciando missão na dificuldade Médio"
checar "medio comeca com 20 pontos" "Pontos: 20"

rodar '1\nA\ndificil\n3\n\nq\n4\n'
checar "dificuldade dificil sem acento" "Iniciando missão na dificuldade Difícil"
checar "dificil comeca com 15 pontos" "Pontos: 15"

rodar '1\nA\ndifícil\n3\n\nq\n4\n'
checar "dificuldade dificil com acento" "Iniciando missão na dificuldade Difícil"

rodar '1\nA\nbanana\n3\n\nq\n4\n'
checar "dificuldade invalida cai em Medio" "Iniciando missão na dificuldade Médio"

titulo "[7] TAMANHO DO MAPA"
rodar '1\nA\nfacil\nabc\n\nq\n4\n'
checar "tamanho nao numerico avisa" "Entrada inválida, usando tamanho padrão (5)"
checar "tamanho nao numerico usa mapa 5" " -5 -4 -3 -2 -1  0  1  2  3  4  5"

rodar '1\nA\nfacil\n0\n\nq\n4\n'
checar "tamanho zero usa mapa 5" " -5 -4 -3 -2 -1  0  1  2  3  4  5"

rodar '1\nA\nfacil\n-3\n\nq\n4\n'
checar "tamanho negativo usa mapa 5" " -5 -4 -3 -2 -1  0  1  2  3  4  5"

rodar '1\nA\nfacil\n2\n\nq\n4\n'
checar "tamanho 2 monta mapa de -2 a 2" " -2 -1  0  1  2"

# ------------------------------------------------- CORRECAO DO TRAVAMENTO
titulo "[8] MAPA PEQUENO DEMAIS (defeito corrigido)"
rodar '1\nA\nmedio\n1\n\nq\n4\n'
codigo=$?
checar_bool "medio com mapa 1 NAO trava (terminou dentro do timeout)" $codigo
checar "avisa que o mapa nao comporta o cenario" "não cabe nesse mapa. Usando o tamanho 2"
checar "joga no mapa 2" " -2 -1  0  1  2"

rodar '1\nA\ndificil\n1\n\nq\n4\n'
codigo=$?
checar_bool "dificil com mapa 1 NAO trava" $codigo
checar "dificil tambem avisa o ajuste" "não cabe nesse mapa. Usando o tamanho 2"

rodar '1\nA\nfacil\n1\n\nq\n4\n'
checar_ausente "facil cabe no mapa 1 e nao e ajustado" "não cabe nesse mapa"

# ------------------------------------------------------ MAPA E LEGENDA
titulo "[9] DESENHO DO MAPA"
rodar '1\nArthur\nfacil\n3\n\nq\n4\n'
checar "cabecalho do mapa com pontos e piloto" "Mapa da Missão (Pontos: 30) - Piloto: Arthur"
checar "legenda completa na ordem do original" "Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio"
checar "lista de comandos" "Comandos: w/s/a/d (mover), c (embarcar), q (sair)"
checar "lista os passageiros na superficie" "Passageiros na superfície marciana:"
checar "linha de status da nave" "Nave em (0,0) | Pontos: 30 | Vidas: 3 | A bordo: 0/5 | Restantes: 4"
checar "desenha a nave" " @"

# ------------------------------------------------------------- COMANDOS
titulo "[10] COMANDOS DA PARTIDA"
rodar '1\nA\nfacil\n3\n\nz\nq\n4\n'
checar "comando invalido e recusado" "Comando inválido."

rodar '1\nA\nfacil\n3\n\nc\nq\n4\n'
checar "embarcar sem passageiro na posicao" "Nenhum passageiro nesta posição."

rodar '1\nA\nfacil\n3\n\nq\n4\n'
checar "abortar com q" "Missão abortada pelo piloto."

rodar '1\nA\nfacil\n3\n\nd\nq\n4\n'
checar "mover para a direita custa 1 ponto" "Pontos: 29"
checar "nave foi para (1,0)" "Nave em (1,0)"
checar "plataforma L aparece quando a nave sai" " L  @"

rodar '1\nA\nfacil\n3\n\ns\nq\n4\n'
checar "mover para baixo aumenta y" "Nave em (0,1)"

rodar '1\nA\nfacil\n3\n\nw\nq\n4\n'
checar "mover para cima diminui y" "Nave em (0,-1)"

rodar '1\nA\nfacil\n3\n\na\nq\n4\n'
checar "mover para a esquerda diminui x" "Nave em (-1,0)"

rodar '1\nA\nfacil\n1\n\nw\nw\nw\nq\n4\n'
checar "borda do mapa bloqueia o movimento" "Nave em (0,-1)"
checar "movimento bloqueado ainda custa ponto (como no original)" "Pontos: 27"

# -------------------------------------------------------- FIM DE PARTIDA
titulo "[11] ENCERRAMENTO POR PONTUACAO ZERADA"
rodar '1\nA\nfacil\n9\n\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\na\nq\n4\n'
checar "30 movimentos zeram a pontuacao da dificuldade Facil" "Combustível/Pontuação zerada! Missão perdida."
checar_ausente "partida perdida nao mostra estatisticas" "Estatísticas da Partida"

titulo "[12] VARIAS PARTIDAS SEGUIDAS NA MESMA EXECUCAO"
rodar '1\nA\nfacil\n3\n\nq\n1\nB\nmedio\n3\n\nq\n2\n4\n'
checar "primeira partida roda" "Piloto: A"
checar "segunda partida roda na mesma sessao" "Piloto: B"
checar "menu volta e o ranking ainda responde" "====== RANKING TOP 5 PILOTOS ======"
checar "sai normalmente no fim" "Obrigado por jogar"

# ------------------------------------------------------------ ROBUSTEZ
titulo "[13] ROBUSTEZ DO ARQUIVO DE RANKING"
printf '[]' > "$RANKING"
rodar '2\n4\n'
checar "arquivo com lista vazia nao quebra" "Nenhum registro encontrado"

printf '' > "$RANKING"
rodar '2\n4\n'
checar "arquivo vazio nao quebra" "Nenhum registro encontrado"

rm -f "$RANKING"
rodar '2\n4\n'
checar "arquivo inexistente nao quebra" "Nenhum registro encontrado"

# ----------------------------------------------------------- RESULTADO
rm -f "$RANKING"
rm -rf "$TMP"
printf '\n================================================\n'
printf '%d verificacoes, %d falha(s).\n' "$total" "$falhas"
if [ "$falhas" -gt 0 ]; then
  printf 'TESTE FUNCIONAL REPROVOU\n'
  exit 1
fi
printf 'TESTE FUNCIONAL PASSOU\n'
