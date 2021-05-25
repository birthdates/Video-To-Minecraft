package com.birthdates.videotominecraft.movie;

import com.birthdates.videotominecraft.VideoToMinecraft;
import com.birthdates.videotominecraft.maps.Maps;
import com.birthdates.videotominecraft.maps.renderer.MapImageRenderer;
import com.birthdates.videotominecraft.movie.removable.IRemovable;
import com.birthdates.videotominecraft.movie.removable.impl.BlockRemovable;
import com.birthdates.videotominecraft.movie.removable.impl.EntityRemovable;
import com.birthdates.videotominecraft.worker.impl.FrameWorker;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Movie {

    public static final int GRID_SIZE = 2;
    private static final int CACHE_SIZE = 2;

    private final FrameWorker frameWorker;
    private final List<MovieBoard> boards = new ArrayList<>();
    private final List<IRemovable> toRemove = new ArrayList<>();
    private int cacheNum = -1;

    public Movie(Location location, String folder) {
        frameWorker = new FrameWorker(folder);
        populateBoards(location);
    }

    public void populateBoards(Location location) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = GRID_SIZE; j > 0; j--) {
                Location boardLocation = location.clone().add(GRID_SIZE - i, GRID_SIZE - j, 0); //TODO: fix that you have to be looking south
                testInvalidLocation(boardLocation);

                addToRemove(new BlockRemovable(boardLocation));
                MovieBoard movieBoard = new MovieBoard(boardLocation, j, i);
                boards.add(movieBoard);
            }
        }
    }

    private void testInvalidLocation(Location location) {
        if (location.getBlock().getType() == Material.AIR) return;
        stop();
        throw new IllegalArgumentException("Invalid board location");
    }

    public void start() {
        frameWorker.start(CACHE_SIZE, this::update, this::stop);
    }

    public void stop() {
        if (!Bukkit.isPrimaryThread()) VideoToMinecraft.getInstance().postToMainThread(this::remove);
        else remove();
        frameWorker.finish(); //in case of it isn't finished
    }

    private void remove() {
        for (IRemovable removable : toRemove) {
            removable.remove();
        }
    }

    private void update(BufferedImage image) {
        boolean cache = cacheNum++ != 0 && (cacheNum) % CACHE_SIZE != 0;
        if (!cache) cacheNum = 0; //prevent integer overflow

        for (MovieBoard board : boards) {
            board.update(image, cache);
            if (!cache) board.renderer.update();
        }
        if (cache) return;

        //update the board for each player
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (MovieBoard board : boards) {
                board.renderer.sendToPlayer(player);
            }
        }

    }

    private void addToRemove(IRemovable removable) {
        toRemove.add(removable);
    }

    /**
     * This class represents one map on a movie
     */
    private class MovieBoard {
        private final MapImageRenderer renderer;
        private final int x, y;
        private final LinkedList<BufferedImage> cache = new LinkedList<>();

        public MovieBoard(Location location, int x, int y) {
            renderer = new MapImageRenderer();
            this.x = x;
            this.y = y;
            spawnMap(location);
        }

        /**
         * Get our section of {@code bufferedImage}
         *
         * @param bufferedImage Target image
         * @return An image of size {@code gridWith} by {@code gridHeight} (if scaled correctly in ffmpeg, should be 128x128)
         */
        private BufferedImage section(BufferedImage bufferedImage) {
            int gridWith = (bufferedImage.getWidth() / GRID_SIZE);
            int gridHeight = (bufferedImage.getHeight() / GRID_SIZE);
            int x = this.x * gridWith;
            int y = this.y * gridHeight;

            return bufferedImage.getSubimage(x - gridWith, y, gridWith, gridHeight);
        }

        private void spawnMap(Location location) {
            World world = location.getWorld();
            ItemStack map = Maps.createMap(null, world, renderer);
            location.getBlock().setType(Material.BARRIER);
            Location frameLocation = location.clone().add(location.getDirection().getX(), 0, location.getDirection().getZ());
            testInvalidLocation(frameLocation);

            ItemFrame itemFrame;
            try {
                itemFrame = world.spawn(frameLocation, ItemFrame.class);
            } catch (IllegalArgumentException exception) { //this is thrown when a frame is hanging in the air
                stop();
                throw exception;
            }

            addToRemove(new EntityRemovable(itemFrame));
            itemFrame.setRotation(Rotation.CLOCKWISE_45);
            itemFrame.setItem(map);
        }

        public void update(BufferedImage bufferedImage, boolean toCache) {
            if (bufferedImage == null) {
                return;
            }
            if (toCache || cache.isEmpty()) {
                cache.addLast(renderer.getResizedImage(section(bufferedImage))); //resize here to save memory
                return;
            }
            renderer.setBufferedImage(cache.removeFirst(), false);
        }
    }
}
