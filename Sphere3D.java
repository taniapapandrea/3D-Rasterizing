//****************************************************************************
//      Sphere class for PA4
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//	 Dec, 2015 Edited by Tania Papandrea (taniap@bu.edu)
//

public class Sphere3D
{
	private Vector3D center;
	private float r;
	private int m,n;
	public Mesh3D mesh;
	
	public Sphere3D(float _x, float _y, float _z, float _r, int _m, int _n)
	{
		center = new Vector3D(_x,_y,_z);
		r = _r;
		m = _m;
		n = _n;
		initMesh();
	}
	
	public void set_center(float _x, float _y, float _z)
	{
		center.x=_x;
		center.y=_y;
		center.z=_z;
		fillMesh();  // update the triangle mesh
	}
	
	public Vector3D get_center(){
		return center;
	}
	
	public void set_radius(float _r)
	{
		r = _r;
		fillMesh(); // update the triangle mesh
	}
	
	public void set_m(int _m)
	{
		m = _m;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public void set_n(int _n)
	{
		n = _n;
		initMesh(); // resized the mesh, must re-initialize
	}
	
	public int get_n()
	{
		return n;
	}
	
	public int get_m()
	{
		return m;
	}

	private void initMesh()
	{
		mesh = new Mesh3D(m,n);
		fillMesh();  // set the mesh vertices and normals
	}
		
	// fill the triangle mesh vertices and normals
	// using the current parameters for the sphere
	private void fillMesh()
	{
		int i,j;		
		float theta, phi;
		float d_theta=(float)(2.0*Math.PI)/ ((float)(m-1));
		float d_phi=(float)Math.PI / ((float)n-1);
		float c_theta,s_theta;
		float c_phi, s_phi;
		
		for(i=0,theta=-(float)Math.PI;i<m;++i,theta += d_theta)
	    {
			c_theta=(float)Math.cos(theta);
			s_theta=(float)Math.sin(theta);
			
			for(j=0,phi=(float)(-0.5*Math.PI);j<n;++j,phi += d_phi)
			{
				// vertex location
				c_phi = (float)Math.cos(phi);
				s_phi = (float)Math.sin(phi);
				mesh.v[i][j].x=center.x+r*c_phi*c_theta;
				mesh.v[i][j].y=center.y+r*c_phi*s_theta;
				mesh.v[i][j].z=center.z+r*s_phi;
				
				// unit normal to sphere at this vertex
				mesh.n[i][j].x = c_phi*c_theta;
				mesh.n[i][j].y = c_phi*s_theta;
				mesh.n[i][j].z=s_phi;
			}
	    }
	}
}