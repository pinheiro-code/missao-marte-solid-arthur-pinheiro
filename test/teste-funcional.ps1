# Teste funcional do jogo Missao Marte, versao refatorada.
# Roda o console de verdade, com entrada roteirizada, e confere a saida.
#
# Uso, a partir da raiz do repositorio:
#   powershell -ExecutionPolicy Bypass -File test\teste-funcional.ps1
#
# Equivalente ao test/teste-funcional.sh, para quem nao tem bash.

$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$raiz = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $raiz

if (-not (Test-Path 'out-solid')) {
    Write-Host "Compile antes: javac -encoding UTF-8 -d out-solid (Get-ChildItem -Recurse src\solidexercicio10\*.java | % FullName)"
    exit 1
}

$ranking = 'ranking-solid-exercicio10.json'
$tmp = Join-Path $env:TEMP ("missao-marte-teste-" + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $tmp | Out-Null
$arqEntrada = Join-Path $tmp 'entrada.txt'
$arqSaida = Join-Path $tmp 'saida.txt'

$script:total = 0
$script:falhas = 0
$script:saida = ''
$script:travou = $false

function Rodar([string[]]$linhas) {
    # Grava a entrada num arquivo e roda o jogo lendo dele, com limite de 30s.
    [System.IO.File]::WriteAllText($arqEntrada, ($linhas -join "`r`n") + "`r`n", [System.Text.UTF8Encoding]::new($false))
    $args = @(
        '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8',
        '-cp', 'out-solid', 'solidexercicio10.Main'
    )
    $p = Start-Process -FilePath 'java' -ArgumentList $args `
        -RedirectStandardInput $arqEntrada -RedirectStandardOutput $arqSaida `
        -NoNewWindow -PassThru
    if ($p.WaitForExit(30000)) {
        $script:travou = $false
    } else {
        try { $p.Kill() } catch { }
        $script:travou = $true
    }
    if (Test-Path $arqSaida) {
        $script:saida = [System.IO.File]::ReadAllText($arqSaida, [System.Text.Encoding]::UTF8)
    } else {
        $script:saida = ''
    }
}

function Checar([string]$descricao, [string]$esperado) {
    $script:total++
    if ($script:saida.Contains($esperado)) {
        Write-Host ("  ok    " + $descricao)
    } else {
        $script:falhas++
        Write-Host ("  FALHA " + $descricao) -ForegroundColor Red
        Write-Host ("         esperava: " + $esperado) -ForegroundColor Red
    }
}

function ChecarAusente([string]$descricao, [string]$naoEsperado) {
    $script:total++
    if ($script:saida.Contains($naoEsperado)) {
        $script:falhas++
        Write-Host ("  FALHA " + $descricao) -ForegroundColor Red
        Write-Host ("         nao deveria aparecer: " + $naoEsperado) -ForegroundColor Red
    } else {
        Write-Host ("  ok    " + $descricao)
    }
}

function ChecarCondicao([string]$descricao, [bool]$condicao) {
    $script:total++
    if ($condicao) {
        Write-Host ("  ok    " + $descricao)
    } else {
        $script:falhas++
        Write-Host ("  FALHA " + $descricao) -ForegroundColor Red
    }
}

function RodarSemColisao([string[]]$linhas) {
    # Alguns cenarios so fazem sentido se a nave nao colidir no caminho.
    # Inimigo anda ao acaso, entao repete ate sair uma partida limpa.
    for ($i = 0; $i -lt 20; $i++) {
        Rodar $linhas
        if (-not ($script:saida.Contains('Colisão detectada') -or $script:saida.Contains('GAME OVER'))) {
            return
        }
    }
}

function Titulo([string]$texto) { Write-Host ''; Write-Host $texto -ForegroundColor Cyan }

if (Test-Path $ranking) { Remove-Item $ranking -Force }

# ---------------------------------------------------------------- MENU
Titulo '[1] MENU PRINCIPAL'
Rodar @('4')
Checar 'abre com as boas-vindas' 'MISSÃO MARTE UNIFOR'
Checar 'mostra as 4 opcoes do menu' '4. Sair do Jogo'
Checar 'sai pela opcao 4' 'Obrigado por jogar a Missão Marte Unifor!'

Rodar @('9', '4')
Checar 'recusa opcao invalida' 'Opção inválida. Tente novamente.'

Rodar @('abc', '4')
Checar 'recusa opcao nao numerica' 'Opção inválida. Tente novamente.'

# ------------------------------------------------------------- RANKING
Titulo '[2] RANKING VAZIO'
if (Test-Path $ranking) { Remove-Item $ranking -Force }
Rodar @('2', '4')
Checar 'ranking vazio avisa que nao ha registro' 'Nenhum registro encontrado'
Checar 'exibe o cabecalho do ranking' '====== RANKING TOP 5 PILOTOS ======'

Titulo '[3] RANKING COM DADOS (leitura do arquivo)'
$json = '[{"name":"Ana","score":90,"dificuldade":"DIFICIL","passageirosColetados":5,"dataHora":"2026-09-20 10:00:00","tempoJogo":45},' +
        '{"name":"Bruno","score":80,"dificuldade":"MEDIO","passageirosColetados":5,"dataHora":"2026-09-21 11:00:00","tempoJogo":50},' +
        '{"name":"Caio","score":70,"dificuldade":"FACIL","passageirosColetados":4,"dataHora":"2026-09-22 12:00:00","tempoJogo":60},' +
        '{"name":"Duda","score":60,"dificuldade":"FACIL","passageirosColetados":4,"dataHora":"2026-09-22 13:00:00","tempoJogo":55},' +
        '{"name":"Elis","score":50,"dificuldade":"MEDIO","passageirosColetados":3,"dataHora":"2026-09-23 14:00:00","tempoJogo":70},' +
        '{"name":"Fabio","score":40,"dificuldade":"FACIL","passageirosColetados":2,"dataHora":"2026-09-23 15:00:00","tempoJogo":80}]'
[System.IO.File]::WriteAllText((Join-Path $raiz $ranking), $json, [System.Text.UTF8Encoding]::new($false))
Rodar @('2', '4')
Checar 'le o ranking do arquivo e ordena por pontuacao' '1. Ana - 90 pts'
Checar 'segunda colocacao correta' '2. Bruno - 80 pts'
Checar 'quinta colocacao correta' '5. Elis - 50 pts'
ChecarAusente 'corta no top 5, sexto nao aparece' '6. Fabio'
Checar 'exibe dificuldade na linha do ranking' 'Dificuldade: Difícil'
Checar 'exibe passageiros coletados' 'Coletados: 5'
Checar 'exibe tempo de jogo' 'Tempo: 45s'
Checar 'exibe data e hora' '2026-09-20 10:00:00'

# --------------------------------------------------------------- RESET
Titulo '[4] RESET DO RANKING'
Rodar @('3', 'n', '4')
Checar 'reset cancelado com n' 'Operação cancelada.'
ChecarCondicao 'arquivo continua existindo apos cancelar' (Test-Path $ranking)

Rodar @('3', 's', '4')
Checar 'reset confirmado com s' 'Ranking resetado com sucesso!'
ChecarCondicao 'arquivo foi apagado' (-not (Test-Path $ranking))

[System.IO.File]::WriteAllText((Join-Path $raiz $ranking), '[]', [System.Text.UTF8Encoding]::new($false))
Rodar @('3', 'sim', '4')
Checar 'reset confirmado com a palavra sim' 'Ranking resetado com sucesso!'

if (Test-Path $ranking) { Remove-Item $ranking -Force }
Rodar @('3', 's', '2', '4')
Checar 'ranking fica vazio depois do reset' 'Nenhum registro encontrado'

# ---------------------------------------------------- ENTRADAS DA PARTIDA
Titulo '[5] ESCOLHA DE PILOTO'
Rodar @('1', '', 'facil', '3', '', 'q', '4')
Checar 'nome vazio vira Piloto Anônimo' 'Piloto: Piloto Anônimo'

Rodar @('1', 'Arthur Pinheiro', 'facil', '3', '', 'q', '4')
Checar 'aceita nome com espaco' 'Piloto: Arthur Pinheiro'

Titulo '[6] ESCOLHA DE DIFICULDADE'
Rodar @('1', 'A', 'facil', '3', '', 'q', '4')
Checar 'dificuldade facil sem acento' 'Iniciando missão na dificuldade Fácil'
Checar 'facil comeca com 30 pontos' 'Pontos: 30'

Rodar @('1', 'A', 'fácil', '3', '', 'q', '4')
Checar 'dificuldade facil com acento' 'Iniciando missão na dificuldade Fácil'

Rodar @('1', 'A', 'MEDIO', '3', '', 'q', '4')
Checar 'dificuldade medio em maiuscula' 'Iniciando missão na dificuldade Médio'
Checar 'medio comeca com 20 pontos' 'Pontos: 20'

Rodar @('1', 'A', 'dificil', '3', '', 'q', '4')
Checar 'dificuldade dificil sem acento' 'Iniciando missão na dificuldade Difícil'
Checar 'dificil comeca com 15 pontos' 'Pontos: 15'

Rodar @('1', 'A', 'difícil', '3', '', 'q', '4')
Checar 'dificuldade dificil com acento' 'Iniciando missão na dificuldade Difícil'

Rodar @('1', 'A', 'banana', '3', '', 'q', '4')
Checar 'dificuldade invalida cai em Medio' 'Iniciando missão na dificuldade Médio'

Titulo '[7] TAMANHO DO MAPA'
Rodar @('1', 'A', 'facil', 'abc', '', 'q', '4')
Checar 'tamanho nao numerico avisa' 'Entrada inválida, usando tamanho padrão (5)'
Checar 'tamanho nao numerico usa mapa 5' ' -5 -4 -3 -2 -1  0  1  2  3  4  5'

Rodar @('1', 'A', 'facil', '0', '', 'q', '4')
Checar 'tamanho zero usa mapa 5' ' -5 -4 -3 -2 -1  0  1  2  3  4  5'

Rodar @('1', 'A', 'facil', '-3', '', 'q', '4')
Checar 'tamanho negativo usa mapa 5' ' -5 -4 -3 -2 -1  0  1  2  3  4  5'

Rodar @('1', 'A', 'facil', '2', '', 'q', '4')
Checar 'tamanho 2 monta mapa de -2 a 2' ' -2 -1  0  1  2'

# ------------------------------------------------- CORRECAO DO TRAVAMENTO
Titulo '[8] MAPA PEQUENO DEMAIS (defeito corrigido)'
Rodar @('1', 'A', 'medio', '1', '', 'q', '4')
ChecarCondicao 'medio com mapa 1 NAO trava (terminou dentro do limite)' (-not $script:travou)
Checar 'avisa que o mapa nao comporta o cenario' 'não cabe nesse mapa. Usando o tamanho 2'
Checar 'joga no mapa 2' ' -2 -1  0  1  2'

Rodar @('1', 'A', 'dificil', '1', '', 'q', '4')
ChecarCondicao 'dificil com mapa 1 NAO trava' (-not $script:travou)
Checar 'dificil tambem avisa o ajuste' 'não cabe nesse mapa. Usando o tamanho 2'

Rodar @('1', 'A', 'facil', '1', '', 'q', '4')
ChecarAusente 'facil cabe no mapa 1 e nao e ajustado' 'não cabe nesse mapa'

# ------------------------------------------------------ MAPA E LEGENDA
Titulo '[9] DESENHO DO MAPA'
Rodar @('1', 'Arthur', 'facil', '3', '', 'q', '4')
Checar 'cabecalho do mapa com pontos e piloto' 'Mapa da Missão (Pontos: 30) - Piloto: Arthur'
Checar 'legenda completa na ordem do original' 'Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio'
Checar 'lista de comandos' 'Comandos: w/s/a/d (mover), c (embarcar), q (sair)'
Checar 'lista os passageiros na superficie' 'Passageiros na superfície marciana:'
Checar 'linha de status da nave' 'Nave em (0,0) | Pontos: 30 | Vidas: 3 | A bordo: 0/5 | Restantes: 4'
Checar 'desenha a nave' ' @'

# ------------------------------------------------------------- COMANDOS
Titulo '[10] COMANDOS DA PARTIDA'
Rodar @('1', 'A', 'facil', '3', '', 'z', 'q', '4')
Checar 'comando invalido e recusado' 'Comando inválido.'

Rodar @('1', 'A', 'facil', '3', '', 'c', 'q', '4')
Checar 'embarcar sem passageiro na posicao' 'Nenhum passageiro nesta posição.'

Rodar @('1', 'A', 'facil', '3', '', 'q', '4')
Checar 'abortar com q' 'Missão abortada pelo piloto.'

Rodar @('1', 'A', 'facil', '3', '', 'd', 'q', '4')
Checar 'mover para a direita custa 1 ponto' 'Pontos: 29'
Checar 'nave foi para (1,0)' 'Nave em (1,0)'

Rodar @('1', 'A', 'facil', '3', '', 's', 'q', '4')
Checar 'mover para baixo aumenta y' 'Nave em (0,1)'

Rodar @('1', 'A', 'facil', '3', '', 'w', 'q', '4')
Checar 'mover para cima diminui y' 'Nave em (0,-1)'

Rodar @('1', 'A', 'facil', '3', '', 'a', 'q', '4')
Checar 'mover para a esquerda diminui x' 'Nave em (-1,0)'

RodarSemColisao @('1', 'A', 'facil', '2', '', 'w', 'w', 'w', 'q', '4')
Checar 'borda do mapa bloqueia o movimento' 'Nave em (0,-2)'
Checar 'movimento bloqueado ainda custa ponto (como no original)' 'Pontos: 27'

# -------------------------------------------------------- FIM DE PARTIDA
Titulo '[11] ENCERRAMENTO POR PONTUACAO ZERADA'
$trinta = @('1', 'A', 'facil', '9', '') + (1..30 | ForEach-Object { 'a' }) + @('q', '4')
RodarSemColisao $trinta
Checar '30 movimentos zeram a pontuacao da dificuldade Facil' 'Combustível/Pontuação zerada! Missão perdida.'
ChecarAusente 'partida perdida nao mostra estatisticas' 'Estatísticas da Partida'

Titulo '[12] VARIAS PARTIDAS SEGUIDAS NA MESMA EXECUCAO'
Rodar @('1', 'A', 'facil', '3', '', 'q', '1', 'B', 'medio', '3', '', 'q', '2', '4')
Checar 'primeira partida roda' 'Piloto: A'
Checar 'segunda partida roda na mesma sessao' 'Piloto: B'
Checar 'menu volta e o ranking ainda responde' '====== RANKING TOP 5 PILOTOS ======'
Checar 'sai normalmente no fim' 'Obrigado por jogar'

# ------------------------------------------------------------ ROBUSTEZ
Titulo '[13] ROBUSTEZ DO ARQUIVO DE RANKING'
[System.IO.File]::WriteAllText((Join-Path $raiz $ranking), '[]', [System.Text.UTF8Encoding]::new($false))
Rodar @('2', '4')
Checar 'arquivo com lista vazia nao quebra' 'Nenhum registro encontrado'

[System.IO.File]::WriteAllText((Join-Path $raiz $ranking), '', [System.Text.UTF8Encoding]::new($false))
Rodar @('2', '4')
Checar 'arquivo vazio nao quebra' 'Nenhum registro encontrado'

if (Test-Path $ranking) { Remove-Item $ranking -Force }
Rodar @('2', '4')
Checar 'arquivo inexistente nao quebra' 'Nenhum registro encontrado'

# ----------------------------------------------------------- RESULTADO
if (Test-Path $ranking) { Remove-Item $ranking -Force }
Remove-Item $tmp -Recurse -Force
Write-Host ''
Write-Host '================================================'
Write-Host ("{0} verificacoes, {1} falha(s)." -f $script:total, $script:falhas)
if ($script:falhas -gt 0) {
    Write-Host 'TESTE FUNCIONAL REPROVOU' -ForegroundColor Red
    exit 1
}
Write-Host 'TESTE FUNCIONAL PASSOU' -ForegroundColor Green
