/*****************************************************************************
       PHONG VERSION of Vector3D.java
****************************************************************************
This is the same Vector3D created by Stan Sclaroff (sclaroff@bu.edu), adapted
by Tania Papandrea to implement Phong shading.

Fields have been added for nx, ny and nz.

If x,y,z represents a vector for the surface pixel of a polyhedral,
nx,ny,nz represents the vector for the pixel's surface normal.

*/

public class PhongVector3D
{
	public float x, y, z;
	public float nx, ny, nz;
	public static final float ROUNDOFF_THRESHOLD = 0.0001f;
	
	public PhongVector3D(float _x, float _y, float _z, float _nx, float _ny, float _nz)
	{
		x=_x;
		y=_y;
		z=_z;
		nx = _nx;
		ny = _ny;
		nz = _nz;
	}
	
	public PhongVector3D()
	{
		x=y=z=(float)0.0;
		nx=ny=nz=0;
	}
	
	public PhongVector3D(PhongVector3D _v)
	{
		x=_v.x;
		y=_v.y;
		z=_v.z;
		nx=_v.nx;
		ny=_v.ny;
		nz=_v.nz;
	}
	
	// compute the cross-product this (x) v and return result in out
	public void crossProduct(Vector3D v, Vector3D out)
	{
		Vector3D temp = new Vector3D();
		temp.x = this.y*v.z-this.z*v.y;
		temp.y = this.z*v.x-this.x*v.z;
		temp.z = this.x*v.y-this.y*v.x;
		
		out.x = temp.x;
		out.y = temp.y;
		out.z = temp.z;
	}
	
	// compute the cross-product this (x) v and return result
	public Vector3D crossProduct(Vector3D v)
	{
		Vector3D out = new Vector3D();
		out.x = this.y*v.z-this.z*v.y;
		out.y = this.z*v.x-this.x*v.z;
		out.z = this.x*v.y-this.y*v.x;
		return(out);
	}
	
	// compute dot product of v and this vector
	public float dotProduct(Vector3D v)
	{
		return(v.x*this.x+v.y*this.y+v.z*this.z);
	}
			
	// subtract vector v from this vector and return result in out
	public void minus(Vector3D v, Vector3D out)
	{
		out.x = this.x-v.x;
		out.y = this.y-v.y;
		out.z = this.z-v.z;
	}
		
	// subtract vector v from this vector and return result
	public Vector3D minus(Vector3D v)
	{
		Vector3D out = new Vector3D();
		out.x = this.x-v.x;
		out.y = this.y-v.y;
		out.z = this.z-v.z;
		return(out);
	}
	
	// scale this vector by s and return result
	public Vector3D scale(float s)
	{
		Vector3D out = new Vector3D();
		out.x = this.x*s;
		out.y = this.y*s;
		out.z = this.z*s;
		return(out);
	}
	
	// add the vector v to this vector and return result 
	public Vector3D plus(Vector3D v)
	{
		Vector3D out = new Vector3D();
		out.x = this.x+v.x;
		out.y = this.y+v.y;
		out.z = this.z+v.z;
		return(out);
	}
	
	// compute the length / magnitude
	public double magnitude()
	{
		double mag = Math.sqrt(dotProduct(new Vector3D(this.x, this.y, this.z)));
		return(mag);
	}
	
	// produce unit vector
	public void normalize()
	{
		double mag = this.magnitude();
		if(mag>ROUNDOFF_THRESHOLD)
		{
			this.x /= mag;
			this.y /= mag;
			this.z /= mag;
		}
		// should probably throw an error exception here for zero magnitude 
	}
	
	// compute the reflection of this vector around vector n
	public Vector3D reflect(Vector3D n)
	{

		float dot = 2*this.dotProduct(n);
		Vector3D out = n.scale(dot);
		out = out.minus(new Vector3D(this.x, this.y, this.z));
		
		return(out);
	}
}