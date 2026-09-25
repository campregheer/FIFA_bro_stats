package com.campregheer.fifa_bro_stats.entity;

public enum ChampionshipMatchupStatus {
    BYE,        // um dos lados passou direto, sem jogo
    PENDING,    // aguardando partida(s) serem jogadas
    COMPLETED   // vencedor definido
}