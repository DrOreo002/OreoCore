package me.droreo002.oreocore.utils.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation")
public final class BlockUtils {

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
}
