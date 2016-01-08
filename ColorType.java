//****************************************************************************
// ColorType  
//****************************************************************************
// Comments : 
//   Class for RGB color 
//      Note: Color is stored in float r, g, b values in range [0,1] 
//
// History :
//   Aug 2014 Created by Jianming Zhang 
//   Sep 2015 Modified to support integer access by Stan Sclaroff and Sarah Adel Bargal

public class ColorType
{
    /** Color domain float [0,1] */
    public float r, g, b;

    public ColorType( float _r, float _g, float _b)
    {
    	r = _r;
    	g = _g;
    	b = _b;
    }
    
    public ColorType(ColorType c)
    {
    	r = c.r;
    	g = c.g;
    	b = c.b;
    }
    
	public ColorType()
	{
		r = g = b = (float)0.0;
	}
    
    /** Return color in a 3 byte RGB stored packed in one integer */
    public int getRGB_int()
    {
    	int _b = Math.round(b*255.0f); 
    	int _g = Math.round(g*255.0f); 
    	int _r = Math.round(r*255.0f);
    	int bb = (_r<<16) | (_g<<8) | _b;
    	return bb;
    }

    /** Return red in domain int [0,255] */
    public int getR_int()
    {
    	return Math.round(r*255.0f);
    }
    
    /** Return green in domain int [0,255] */
    public int getG_int()
    {
    	return Math.round(g*255.0f);
    }
    
    /** Return blue in domain int [0,255] */
    public int getB_int()
    {
    	return Math.round(b*255.0f);
    }
   
    /**
     * Sets red in domain float [0,1] corresponding to red in input domain. 
     * 
     * @param _r
     *          Red in domain int [0,255].
     */
    public void setR_int(int _r)
    {
    	r = ((float)_r)/255.0f;
    }

    /**
     * Sets green in domain float [0,1] corresponding to green in input domain. 
     * 
     * @param _g
     *          Green in domain int [0,255].
     */
    public void setG_int(int _g)
    {
    	g = ((float)_g)/255.0f;
    }
    
    /**
     * Sets blue in domain float [0,1] corresponding to blue in input domain. 
     * 
     * @param _b
     *          Blue in domain int [0,255].
     */
    public void setB_int(int _b)
    {
    	b = ((float)_b)/255.0f;
    }
    
}