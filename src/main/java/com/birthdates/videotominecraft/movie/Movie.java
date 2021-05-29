package com.birthdates.videotominecraft.movie;

import com.birthdates.videotominecraft.VideoToMinecraft;
import com.birthdates.videotominecraft.maps.Maps;
import com.birthdates.videotominecraft.maps.renderer.MapImageRenderer;
import com.birthdates.videotominecraft.movie.removable.IRemovable;
import com.birthdates.videotominecraft.movie.removable.impl.BlockRemovable;
import com.birthdates.videotominecraft.movie.removable.impl.EntityRemovable;
import com.birthdates.videotominecraft.utils.locations.Direction;
import com.birthdates.videotominecraft.utils.locations.Locations;
import com.birthdates.videotominecraft.worker.impl.FrameWorker;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to watch a video on multiple maps
 */
public class Movie {

    private final static int gridSizeSquared = VideoToMinecraft.getInstance().getConfiguration().getGridSize() * VideoToMinecraft.getInstance().getConfiguration().getGridSize();
    private final FrameWorker frameWorker;
    private final MovieBoard[] boards = new MovieBoard[gridSizeSquared];
    private final List<IRemovable> toRemove = new ArrayList<>();

    public Movie(Location location, String folder) {
        frameWorker = new FrameWorker(folder);
        populateBoards(location);
    }

    public void populateBoards(Location location) {
        int gridSize = VideoToMinecraft.getInstance().getConfiguration().getGridSize();

        int index = 0;
        Direction direction = Locations.getDirection(location);
        for (int i = 0; i < gridSize; ++i) {
            for (int j = gridSize; j > 0; --j, index++) {
                Location boardLocation = location.clone().add(getAddition(direction, gridSize, i, j));
                testInvalidLocation(boardLocation);

                addToRemove(new BlockRemovable(boardLocation));
                MovieBoard movieBoard = new MovieBoard(boardLocation, j, i);
                boards[index] = movieBoard;
            }
        }
    }

    /**
     * Get the base location of a theatre based off direction
     *
     * @param direction Location direction
     * @param gridSize  Theatre grid size
     * @param x2        Grid x
     * @param y2        Grid y
     * @return A Vector relative to the direction of the location
     */
    private Vector getAddition(Direction direction, int gridSize, int x2, int y2) {
        int x = direction == Direction.NORTH ? x2 - gridSize : gridSize - x2;
        int y = gridSize - y2;
        int z = direction == Direction.EAST ? x2 - gridSize : gridSize - x2;

        Vector vector = new Vector(0, y, 0);
        if (direction.isZ()) vector.setZ(z);
        else vector.setX(x);

        return vector;
    }

    private void testInvalidLocation(Location location) {
        if (location.getBlock().getType() == Material.AIR) return;
        stop();
        throw new IllegalArgumentException("Invalid board location");
    }

    public void start() {
        frameWorker.start(this::update, this::stop);
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

    private void update(byte[] image) {
        for (MovieBoard board : boards) {
            board.update(image);
        }

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

        public MovieBoard(Location location, int x, int y) {
            renderer = new MapImageRenderer();
            this.x = x;
            this.y = y;
            spawnMap(location);
        }

        /**
         * Get the resolution of the square image from pixel array
         *
         * @param bytes Pixel array
         * @return Resolution of pixel (i.e 256 for 256x256)
         */
        private int getResolution(byte[] bytes) {
            return (int) Math.sqrt(bytes.length);
        }

        /**
         * Get our section of {@code bytes}
         *
         * @param bytes Target image
         * @return An image of size {@code gridWith} by {@code gridHeight} (if scaled correctly in ffmpeg, should be 128x128)
         */
        private byte[] section(byte[] bytes) {
            int size = getResolution(bytes);
            int mapRes = Maps.getResolution();
            int gridSize = size / VideoToMinecraft.getInstance().getConfiguration().getGridSize();
            int x = this.x * gridSize;
            int y = this.y * gridSize;
            byte[] output = new byte[mapRes * mapRes];

            /*
             * This sets the corresponding pixel of output from bytes.
             * i.e if we're x,y (1,1) and the grid size is 2 (meaning bytes is of size 256*256),
             * we will get a 128x128 section in the top right of the image (128-256, 128-256) -> (0-128, 0-128)
             */
            for (int x2 = x; x2 < x + gridSize; ++x2) {
                for (int y2 = y; y2 < y + gridSize; ++y2) {
                    //i.e if size is 256 & we are at x 256, the output index would be x 128
                    output[(y2 - y) * mapRes + (x2 - x)] = bytes[y2 * size + (x2 - gridSize)];
                }
            }

            return output;
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
            itemFrame.setItem(map);
            itemFrame.setRotation(Rotation.CLOCKWISE_45);
        }

        public void update(byte[] bufferedImage) {
            if (bufferedImage == null) {
                return;
            }
            renderer.drawRawPixels(section(bufferedImage));
        }
    }
}
