package com.example.kona.myapplication;


public class BenderBattle {

    PlayerStats player;
    EnemyStats enemy;

    /**
     * Calculates final attack power for player's attack.
     * The final attack power may be either less or more than player's
     * base power.
     * @param power
     */
    void attack(int power) {
        int attackPower;
        int variance = 5 + (int)(Math.random() * ((10-5) + 1));
        int plusOrMin = 1 + (int)(Math.random() * ((2-1) + 1));
        if (plusOrMin == 1) {
            attackPower = power + variance;
        } else {
            attackPower = power - variance;
        }
        Transaction transaction = new Transaction();
        transaction.attack(attackPower);
        int currHealth = player.health;
        player.health = currHealth - attackPower;
    }

    /**
     * Determines the enemy's attack power for the turn
     * and updates player's health.
     */
    void enemyAttack(int power) {
        int attackPower;
        int variance = 5 + (int)(Math.random() * ((10-5) + 1));
        int plusOrMin = 1 + (int)(Math.random() * ((2-1) + 1));
        if (plusOrMin == 1) {
            attackPower = power + variance;
        } else {
            attackPower = power - variance;
        }
        Transaction transaction = new Transaction();
        transaction.decreaseHealth(attackPower);
        int currHealth = enemy.health;
        enemy.health = currHealth - attackPower;
    }
}
