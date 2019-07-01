package me.droreo002.oreocore.utils.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation")
public final class BlockUtils {

    private static final BlockFace[] FACES = { BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    /**
     * Get the block facing, only support for those block faces (See code)
     *
     * @param block : The block to check
     * @return The BlockFace facing direction if one of those BlockFaces. Null otherwise
     */
    public static BlockFace getFacing(Block block) {
        if (block.getData() == (byte) 3) { //SOUTH
            return BlockFace.SOUTH;
        } else if (block.getData() == (byte) 2) { //North
            return BlockFace.NORTH;
        } else if (block.getData() == (byte) 4) { //East
            return BlockFace.EAST;
        } else if (block.getData() == (byte) 5) { //West
            return BlockFace.WEST;
        }
        return null;
    }

    /**
     * Get the direction of the BlockFace as vector, this is available
     * at 1.13. But not on the lower version
     *
     * @param blockFace : The block face
     * @return the direction as Vector
     */
    public static Vector getDirection(BlockFace blockFace) {
        int modX = blockFace.getModX();
        int modY = blockFace.getModY();
        int modZ = blockFace.getModZ();
        Vector direction = new Vector(modX, modY, modZ);
        if (modX != 0 || modY != 0 || modZ != 0) {
            direction.normalize();
        }
        return direction;
    }

    /**
     * Get the faced block's location
     *
     * @param block The block source
     * @param target The block target
     * @param checkTopAndBottom Should we also check for the top?
     * @return the location of the faced block
     */
    public static Location getFacedLocation(Block block, Material target, boolean checkTopAndBottom) {
        for (BlockFace face : FACES) {
            if (!checkTopAndBottom) {
                switch (face) {
                    case UP:
                    case DOWN:
                        continue;
                }
            }
            Block possible = block.getRelative(face);
            if (possible.getType().equals(target)) return possible.getLocation();
        }
        return null;
    }
}
