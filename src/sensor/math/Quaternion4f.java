package sensor.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Quaternion4f {

	/**
	 * The x coordinate, i.e., the x coordinate of the vector part of the Quaternion.
	 */
	public float x;

	/**
	 * The y coordinate, i.e., the y coordinate of the vector part of the Quaternion.
	 */
	public float y;

	/**
	 * The z coordinate, i.e., the z coordinate of the vector part of the Quaternion.
	 */
	public float z;

	/**
	 * The w coordinate which corresponds to the scalar part of the Quaternion.
	 */
	public float w;

	/**
	 * Constructs and initializes a Quaternion to (0.0,0.0,0.0,1.0), i.e., an identity rotation.
	 */
	public Quaternion4f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 1;
	}

	/**
	 * Default constructor for Quaternion(float x, float y, float z, float w, boolean normalize), with {@code normalize=true}.
	 * 
	 */
	public Quaternion4f(float x, float y, float z, float w) {
		this(x, y, z, w, true);
	}

	/**
	 * Constructs and initializes a Quaternion from the specified xyzw coordinates.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param z
	 *            the z coordinate
	 * @param w
	 *            the w scalar component
	 * @param normalize
	 *            tells whether or not the constructed Quaternion should be normalized.
	 */
	public Quaternion4f(float x, float y, float z, float w, boolean normalize) {
		if (normalize) {
			float mag = (float) Math.sqrt(x * x + y * y + z * z + w * w);
			if (mag > 0.0f) {
				this.x = x / mag;
				this.y = y / mag;
				this.z = z / mag;
				this.w = w / mag;
			} else {
				this.x = 0;
				this.y = 0;
				this.z = 0;
				this.w = 1;
			}
		} else {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}
	}

	/**
	 * Default constructor for Quaternion(float[] q, boolean normalize) with {@code normalize=true}.
	 * 
	 */
	public Quaternion4f(float[] q) {
		this(q, true);
	}

	/**
	 * Constructs and initializes a Quaternion from the array of length 4.
	 * 
	 * @param q
	 *            the array of length 4 containing xyzw in order
	 */
	public Quaternion4f(float[] q, boolean normalize) {
		if (normalize) {
			float mag = (float) Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
			if (mag > 0.0f) {
				this.x = q[0] / mag;
				this.y = q[1] / mag;
				this.z = q[2] / mag;
				this.w = q[3] / mag;
			} else {
				this.x = 0;
				this.y = 0;
				this.z = 0;
				this.w = 1;
			}
		} else {
			this.x = q[0];
			this.y = q[1];
			this.z = q[2];
			this.w = q[3];
		}
	}

	/**
	 * Copy constructor.
	 * 
	 * @param q1
	 *            the Quaternion containing the initialization x y z w data
	 */
	public Quaternion4f(Quaternion4f q1) {
		set(q1);
	}

	/**
	 * Copy constructor. If {@code normalize} is {@code true} this Quaternion is {@link #normalize()}.
	 * 
	 * @param q1
	 *            the Quaternion containing the initialization x y z w data
	 */
	public Quaternion4f(Quaternion4f q1, boolean normalize) {
		set(q1, normalize);
	}

	/**
	 * Convenience function that simply calls {@code set(q1, true);}
	 * 
	 * @see #set(Quaternion, boolean)
	 */
	public void set(Quaternion4f q1) {
		set(q1, true);
	}

	/**
	 * Set this Quaternion from quaternion {@code q1}. If {@code normalize} is {@code true} this Quaternion is {@link #normalize()}.
	 */
	public void set(Quaternion4f q1, boolean normalize) {
		this.x = q1.x;
		this.y = q1.y;
		this.z = q1.z;
		this.w = q1.w;
		if (normalize)
			this.normalize();
	}

	/**
	 * Sets the value of this Quaternion to the conjugate of itself.
	 */
	public final void conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	/**
	 * Sets the value of this Quaternion to the conjugate of Quaternion q1.
	 * 
	 * @param q1
	 *            the source vector
	 */
	public final void conjugate(Quaternion4f q1) {
		this.x = -q1.x;
		this.y = -q1.y;
		this.z = -q1.z;
		this.w = q1.w;
	}

	/**
	 * Negates all the coefficients of the Quaternion.
	 */
	public final void negate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		this.w = -this.w;
	}

	/**
	 * Returns the "dot" product of this Quaternion and {@code b}:
	 * <p>
	 * {@code this.x * b.x + this.y * b.y + this.z * b.z + this.w * b.w}
	 * 
	 * @param b
	 *            the Quaternion
	 */
	public final float dotProduct(Quaternion4f b) {
		return this.x * b.x + this.y * b.y + this.z * b.z + this.w * b.w;
	}

	/**
	 * Returns the "dot" product of {@code a} and {@code b}:
	 * <p>
	 * {@code a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w}
	 * 
	 * @param a
	 *            the first Quaternion
	 * @param b
	 *            the second Quaternion
	 */
	public final static float dotProduct(Quaternion4f a, Quaternion4f b) {
		return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
	}

	/**
	 * Sets the value of this Quaternion to the Quaternion product of itself and {@code q1}, (i.e., {@code this = this * q1}).
	 * 
	 * @param q1
	 *            the other Quaternion
	 */
	public final void multiply(Quaternion4f q1) {
		float x, y, w;

		w = this.w * q1.w - this.x * q1.x - this.y * q1.y - this.z * q1.z;
		x = this.w * q1.x + q1.w * this.x + this.y * q1.z - this.z * q1.y;
		y = this.w * q1.y + q1.w * this.y - this.x * q1.z + this.z * q1.x;
		this.z = this.w * q1.z + q1.w * this.z + this.x * q1.y - this.y * q1.x;
		this.w = w;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the Quaternion which is product of quaternions {@code q1} and {@code q2}.
	 * 
	 * @param q1
	 *            the first Quaternion
	 * @param q2
	 *            the second Quaternion
	 */
	public final static Quaternion4f multiply(Quaternion4f q1, Quaternion4f q2) {
		float x, y, z, w;
		w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
		x = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
		y = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
		z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
		return new Quaternion4f(x, y, z, w);
	}

	/**
	 * Normalizes the value of this Quaternion in place and return its {@code norm}.
	 */
	public final float normalize() {
		float norm = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		if (norm > 0.0f) {
			this.x /= norm;
			this.y /= norm;
			this.z /= norm;
			this.w /= norm;
		} else {
			this.x = (float) 0.0;
			this.y = (float) 0.0;
			this.z = (float) 0.0;
			this.w = (float) 1.0;
		}
		return norm;
	}

	public Vector3d eulerAngles() {
		float roll, pitch, yaw;
		float test = x * y + z * w;
		if (test > 0.499) { // singularity at north pole
			pitch = 2 * (float) Math.atan2(x, w);
			yaw = (float) Math.PI / 2;
			roll = 0;
			return new Vector3d(roll, pitch, yaw);
		}
		if (test < -0.499) { // singularity at south pole
			pitch = -2 * (float) Math.atan2(x, w);
			yaw = -(float) Math.PI / 2;
			roll = 0;
			return new Vector3d(roll, pitch, yaw);
		}
		float sqx = x * x;
		float sqy = y * y;
		float sqz = z * z;
		pitch = (float) Math.atan2(2 * y * w - 2 * x * z, 1 - 2 * sqy - 2 * sqz);
		yaw = (float) Math.asin(2 * test);
		roll = (float) Math.atan2(2 * x * w - 2 * y * z, 1 - 2 * sqx - 2 * sqz);
		return new Vector3d(roll, pitch, yaw);
	}

	public void createMatrix(double[] m) {
		// This function is a necessity when it comes to doing almost anything
		// with quaternions. Since we are working with OpenGL, which uses a 4x4
		// homogeneous matrix, we need to have a way to take our quaternion and
		// convert it to a rotation matrix to modify the current model view matrix.
		// We pass in a 4x4 matrix, which is a 1D array of 16 floats. This is how OpenGL
		// allows us to pass in a matrix to glMultMatrixf(), so we use a single dimensioned array.
		// After about 300 trees murdered and 20 packs of chalk depleted, the
		// mathematicians came up with these equations for a quaternion to matrix conversion:
		//
		// ¦ 2 2 ¦
		// ¦ 1 - (2y + 2z ) 2xy + 2zw 2xz - 2yw 0 ¦
		// ¦ ¦
		// ¦ 2 2 ¦
		// M = ¦ 2xy - 2zw 1 - (2x + 2z ) 2zy + 2xw 0 ¦
		// ¦ ¦
		// ¦ 2 2 ¦
		// ¦ 2xz + 2yw 2yz - 2xw 1 - (2x + 2y ) 0 ¦
		// ¦ ¦
		// ¦ ¦
		// ¦ 0 0 0 1 | ¦
		// ¦ ¦
		//
		// This is of course a 4x4 matrix. Notice that a rotational matrix can just
		// be a 3x3 matrix, but since OpenGL uses a 4x4 matrix, we need to conform to the man.
		// Remember that the identity matrix of a 4x4 matrix has a diagonal of 1's, where
		// the rest of the indices are 0. That is where we get the 0's lining the sides, and
		// the 1 at the bottom-right corner. Since OpenGL matrices are row by column, we fill
		// in our matrix accordingly below.

		// Calculate the first row.
		m[0] = 1.0f - 2.0f * (y * y + z * z);
		m[1] = 2.0f * (x * y + z * w);
		m[2] = 2.0f * (x * z - y * w);
		m[3] = 0.0f;

		m[4] = 2.0f * (x * y - z * w);
		m[5] = 1.0f - 2.0f * (x * x + z * z);
		m[6] = 2.0f * (z * y + x * w);
		m[7] = 0.0f;

		m[8] = 2.0f * (x * z + y * w);
		m[9] = 2.0f * (y * z - x * w);
		m[10] = 1.0f - 2.0f * (x * x + y * y);
		m[11] = 0.0f;

		m[12] = 0;
		m[13] = 0;
		m[14] = 0;
		m[15] = 1.0f;
	}

	public static Quaternion4f parse(byte[] buffer, ByteOrder order) {
		Quaternion4f ris = new Quaternion4f();

		ByteBuffer buff = ByteBuffer.wrap(buffer).order(order);
		ris.x = buff.getFloat();
		ris.y = buff.getFloat();
		ris.z = buff.getFloat();
		ris.w = buff.getFloat();
		
		return ris;
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "," + z + "," + w + "}";
	}

}
