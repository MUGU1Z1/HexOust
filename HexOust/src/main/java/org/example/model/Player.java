package org.example.model;

public class Player {
    private final String name;
    private final PlayerColor color;

    public Player(String name, PlayerColor color) {
        this.name = name;
        this.color = color;

        System.out.println("Creating player - Name: " + name + ", Color: " + color);
    }

    public String getName() {
        return name;
    }

    public PlayerColor getColor() {
        return color;
    }
}