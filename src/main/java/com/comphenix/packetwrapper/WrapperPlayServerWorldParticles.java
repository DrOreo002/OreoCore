/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.comphenix.packetwrapper;

import java.util.HashMap;
import java.util.Map;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedParticle;
import me.droreo002.oreocore.enums.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class WrapperPlayServerWorldParticles extends AbstractPacket {
	public static final PacketType TYPE =
			PacketType.Play.Server.WORLD_PARTICLES;

	public WrapperPlayServerWorldParticles() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerWorldParticles(PacketContainer packet) {
		super(packet, TYPE);
	}


	/**
	 * Set the particle.
	 *
	 * @param particleType - The particles!
	 */
	public void setParticleType(EnumWrappers.Particle particleType) {
		handle.getParticles().write(0, particleType);
	}

	/**
	 * Retrieve X.
	 * <p>
	 * Notes: x position of the particle
	 *
	 * @return The current X
	 */
	public float getX() {
		return handle.getFloat().read(0);
	}

	/**
	 * Set X.
	 *
	 * @param value - new value.
	 */
	public void setX(float value) {
		handle.getFloat().write(0, value);
	}

	/**
	 * Retrieve Y.
	 * <p>
	 * Notes: y position of the particle
	 *
	 * @return The current Y
	 */
	public float getY() {
		return handle.getFloat().read(1);
	}

	/**
	 * Set Y.
	 *
	 * @param value - new value.
	 */
	public void setY(float value) {
		handle.getFloat().write(1, value);
	}

	/**
	 * Retrieve Z.
	 * <p>
	 * Notes: z position of the particle
	 *
	 * @return The current Z
	 */
	public float getZ() {
		return handle.getFloat().read(2);
	}

	/**
	 * Set Z.
	 *
	 * @param value - new value.
	 */
	public void setZ(float value) {
		handle.getFloat().write(2, value);
	}

	/**
	 * Retrieve Offset X.
	 * <p>
	 * Notes: this is added to the X position after being multiplied by
	 * random.nextGaussian()
	 *
	 * @return The current Offset X
	 */
	public float getOffsetX() {
		return handle.getFloat().read(3);
	}

	/**
	 * Set Offset X.
	 *
	 * @param value - new value.
	 */
	public void setOffsetX(float value) {
		handle.getFloat().write(3, value);
	}

	/**
	 * Retrieve Offset Y.
	 * <p>
	 * Notes: this is added to the Y position after being multiplied by
	 * random.nextGaussian()
	 *
	 * @return The current Offset Y
	 */
	public float getOffsetY() {
		return handle.getFloat().read(4);
	}

	/**
	 * Set Offset Y.
	 *
	 * @param value - new value.
	 */
	public void setOffsetY(float value) {
		handle.getFloat().write(4, value);
	}

	/**
	 * Retrieve Offset Z.
	 * <p>
	 * Notes: this is added to the Z position after being multiplied by
	 * random.nextGaussian()
	 *
	 * @return The current Offset Z
	 */
	public float getOffsetZ() {
		return handle.getFloat().read(5);
	}

	/**
	 * Set Offset Z.
	 *
	 * @param value - new value.
	 */
	public void setOffsetZ(float value) {
		handle.getFloat().write(5, value);
	}

	/**
	 * Retrieve Particle data.
	 * <p>
	 * Notes: the data of each particle
	 *
	 * @return The current Particle data
	 */
	public float getParticleData() {
		return handle.getFloat().read(6);
	}

	/**
	 * Set Particle data.
	 *
	 * @param value - new value.
	 */
	public void setParticleData(float value) {
		handle.getFloat().write(6, value);
	}

	/**
	 * Retrieve Number of particles.
	 * <p>
	 * Notes: the number of particles to create
	 *
	 * @return The current Number of particles
	 */
	public int getNumberOfParticles() {
		return handle.getIntegers().read(0);
	}

	/**
	 * Set Number of particles.
	 *
	 * @param value - new value.
	 */
	public void setNumberOfParticles(int value) {
		handle.getIntegers().write(0, value);
	}

	/**
	 * Retrieve Long Distance.
	 * <p>
	 * Notes: if true, particle distance increases from 256 to 65536.
	 *
	 * @return The current Long Distance
	 */
	public boolean getLongDistance() {
		return handle.getBooleans().read(0);
	}

	/**
	 * Set Long Distance.
	 *
	 * @param value - new value.
	 */
	public void setLongDistance(boolean value) {
		handle.getBooleans().write(0, value);
	}

	/**
	 * Set the particle spawn location
	 *
	 * @param location : The location
	 */
	public void setLocation(Location location) {
		setX((float) location.getX());
		setY((float) location.getY());
		setZ((float) location.getZ());
	}

	/**
	 * Set the particle direction
	 *
	 * @param direction : The direction as Vector
	 */
	public void setDirection(Vector direction) {
		setOffsetX((float) direction.getX());
		setOffsetY((float) direction.getY());
		setOffsetZ((float) direction.getZ());
	}
}
