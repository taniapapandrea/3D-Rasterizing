//****************************************************************************
//       material class
//****************************************************************************
// History :
//   Nov 6, 2014 Created by Stan Sclaroff
//
public class Material 
{
	public ColorType ka, kd, ks;
	public int ns;
	public boolean specular, diffuse, ambient;
	
	public Material(ColorType _ka, ColorType _kd, ColorType _ks, int _ns)
	{
		ks = new ColorType(_ks);  // specular coefficient for r,g,b
		ka = new ColorType(_ka);  // ambient coefficient for r,g,b
		kd = new ColorType(_kd);  // diffuse coefficient for r,g,b
		ns = _ns;  // specular exponent
		
		// set boolean variables 
		specular = (ns>0 && (ks.r > 0.0 || ks.g > 0.0 || ks.b > 0.0));
		diffuse = (kd.r > 0.0 || kd.g > 0.0 || kd.b > 0.0);
		ambient = (ka.r > 0.0 || ka.g > 0.0 || ka.b > 0.0);
	}
}