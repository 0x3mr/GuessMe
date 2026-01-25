package org.zeroxamr.guessMe.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

import static org.bukkit.Bukkit.createChunkData;

public final class Utilities {
    private Utilities() {}

    public static Location[] deserializeLocations(String input) {
        if (input == null || input.isBlank()) {
            return new Location[0];
        }

        String[] parts = input.split(";");
        Location[] locations = new Location[parts.length];

        for (int i = 0; i < parts.length; i++) {
            String[] data = parts[i].split(",");

            if (data.length < 4) {
                throw new IllegalArgumentException("Invalid location format: " + parts[i]);
            }

            String worldName = data[0];
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                throw new IllegalStateException("World not found: " + worldName);
            }

            double x = Double.parseDouble(data[1]);
            double y = Double.parseDouble(data[2]);
            double z = Double.parseDouble(data[3]);

            float yaw = data.length > 4 ? Float.parseFloat(data[4]) : 0f;
            float pitch = data.length > 5 ? Float.parseFloat(data[5]) : 0f;

            locations[i] = new Location(world, x, y, z, yaw, pitch);
        }

        return locations;
    }


    public static String randomLetterGenerator(Integer amount) {
        StringBuilder letters = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < amount; i++) {
            char a = (char) ('A' + rand.nextInt(26));
            letters.append(a);
        }

        return letters.toString();
    }

    public static String randomNumberGenerator(Integer amount) {
        StringBuilder numbers = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < amount; i++) {
            int digit = rand.nextInt(10);
            numbers.append(digit);
        }

        return numbers.toString();
    }

    public static class VoidChunkGenerator extends ChunkGenerator {
        @Override
        public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biome) {
            return createChunkData(world);
        }

        @Override
        public boolean shouldGenerateNoise() {
            return false;
        }

        @Override
        public boolean shouldGenerateSurface() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }
    }
}
