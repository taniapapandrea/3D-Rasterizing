//****************************************************************************
//       Infinite light source class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//	 Dec, 2015 Edited by Tania Papandrea (taniap@bu.edu)

public class InfiniteLight 
{
	public Vector3D direction; //direction for infinite light
	public Vector3D pl; //location for point light
	public ColorType color;
	boolean ambient; 
	boolean diffuse; 
	boolean specular;
	boolean isPoint;
	int type; //0=infinite, 1=point
	
	public InfiniteLight(ColorType _c, Vector3D _direction, boolean ambient_, boolean diffuse_, boolean specular_, int type_, Vector3D _location, boolean _isPoint)
	{
		color = new ColorType(_c);
		direction = new Vector3D(_direction);
		pl = new Vector3D(_location);
		ambient = ambient_;
		diffuse = diffuse_;
		specular = specular_;
		type = type_;
		isPoint = _isPoint;
	}
	
	public InfiniteLight(){
		color = new ColorType();
		direction = new Vector3D();
		ambient = false;
		diffuse = false;
		specular = false;
	}
	
	// apply this light source to the vertex / normal, given material
	// return resulting color value
	// v=viewing vector, n=normal, ps surface point
	public ColorType applyLight(Material mat, Vector3D v, Vector3D n, ColorType res, Vector3D ps){
		Vector3D direction_ = new Vector3D(direction);
		//change direction if point light
		if (this.isPoint){
			Vector3D result = pl.minus(ps);
			result.normalize();
			direction_=result;
		}
		
		//ambient component (only add once)
		if (mat.ambient && ambient){
			res.r += (float)(mat.ka.r*color.r);
			res.g += (float)(mat.ka.g*color.g);
			res.b += (float)(mat.ka.b*color.b);
		}
		
		// dot product between light direction and normal
		// light must be facing in the positive direction
		// dot <= 0.0 implies this light is facing away (not toward) this point
		// therefore, light only contributes if dot > 0.0
		double dot = direction_.dotProduct(n);
		if(dot>0.0)
		{
			// diffuse component
			if(mat.diffuse && diffuse)
			{
				res.r = (float)(dot*mat.kd.r*color.r);
				res.g = (float)(dot*mat.kd.g*color.g);
				res.b = (float)(dot*mat.kd.b*color.b);
			}
			// specular component
			if(mat.specular && specular)
			{
				Vector3D r = direction.reflect(n);
				dot = r.dotProduct(v);
				if(dot>0.0)
				{
					res.r += (float)Math.pow((dot*mat.ks.r*color.r),mat.ns);
					res.g += (float)Math.pow((dot*mat.ks.g*color.g),mat.ns);
					res.b += (float)Math.pow((dot*mat.ks.b*color.b),mat.ns);
				}
			}
			
		}
		// clamp so that allowable maximum illumination level is not exceeded
		res.r = (float) Math.min(1.0, res.r);
		res.g = (float) Math.min(1.0, res.g);
		res.b = (float) Math.min(1.0, res.b);
		return(res);
	}
	

	
}
