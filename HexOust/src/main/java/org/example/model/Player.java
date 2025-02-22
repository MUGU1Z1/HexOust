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
        System.out.println("Getting color for player: " + name);
        System.out.println("Player color value: " + color);
        System.out.println("Color enum name: " + color.name());
        return color;
    }
}