/*****************************************************************************
 PHONG VERSION of SketchBase.java
****************************************************************************
 This is the same SketchBase.java created by Jianming Zhang (jimmie33@gmail.com)
 and Stan Sclaroff, adapted by Tania Papandrea (taniap@bu.edu) to implement
 Phong shading. 
 
 All Vector3Ds have been replaced with PhongVector3Ds. 
 
 *****R,G,B COLOR CHANNELS NOW CONTAIN X,Y,Z SURFACE NORMAL COORDINATES*****
 
 Instead of using r,g and b to set the pixel's color, they are used to create a
 Vector3D for this pixel's surface normal. This normal is then used to apply
 the light equation, and obtain the pixel's real color.
 
  */

import java.awt.image.BufferedImage;
import java.util.*;

public class PhongSketchBase 
{
	public PhongSketchBase()
	{
		// deliberately left blank
	}
	
	/**********************************************************************
	 * Draws a point.
	 * This is achieved by changing the color of the buffer at the location
	 * corresponding to the point. 
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p
	 *          Point to be drawn.
	 */
	public static void drawPoint(BufferedImage buff, PhongVector3D p)
	{
//		if(p.x>=0 && p.x<buff.getWidth() && p.y>=0 && p.y < buff.getHeight())
//			buff.setRGB((int)p.x, (int)(buff.getHeight()-p.y-1), p.c.getRGB_int());	
	}
	
	/**********************************************************************
	 * Draws a line segment using Bresenham's algorithm, linearly 
	 * interpolating RGB color along line segment.
	 * This method only uses integer arithmetic.
	 * 
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given endpoint of the line.
	 * @param p2
	 *          Second given endpoint of the line.
	 */
	public static void drawLine(BufferedImage buff, int[][] d_buff, PhongVector3D p1, PhongVector3D p2, InfiniteLight light, Material material, Vector3D view_vector)
	{
	    int x0=(int)p1.x, y0=(int)p1.y, z0=(int)p1.z;
	    int xEnd=(int)p2.x, yEnd=(int)p2.y, zEnd=(int)p2.z;
	    int dx = Math.abs(xEnd - x0),  dy = Math.abs(yEnd - y0), dz = Math.abs(zEnd - z0);

	    if(dx==0 && dy==0)
	    {
	    	//check buff?
	    	drawPoint(buff,p1);
	    	return;
	    }
	    
	    // if slope is greater than 1, then swap the role of x and y
	    boolean x_y_role_swapped = (dy > dx); 
	    if(x_y_role_swapped)
	    {
	    	x0=(int)p1.y; 
	    	y0=(int)p1.x;
	    	xEnd=(int)p2.y; 
	    	yEnd=(int)p2.x;
	    	dx = Math.abs(xEnd - x0);
	    	dy = Math.abs(yEnd - y0);
	    }
	    
	    // initialize the decision parameter and increments
	    int p = 2 * dy - dx;
	    int twoDy = 2 * dy,  twoDyMinusDx = 2 * (dy - dx);
	    int x=x0, y=y0, z=z0;
	    
	    // set step increment to be positive or negative
	    int step_x = x0<xEnd ? 1 : -1;
	    int step_y = y0<yEnd ? 1 : -1;
	    int step_z = z0<zEnd ? 1 : -1;
	    
	    // deal with setup for color interpolation
	    // first get r,g,b integer values at the end points
	    float r0=p1.nx, rEnd=p2.nx;
	    float g0=p1.ny, gEnd=p2.ny;
	    float b0=p1.nz, bEnd=p2.nz;
	    
	    // compute the change in r,g,b 
	    float dr=Math.abs(rEnd-r0), dg=Math.abs(gEnd-g0), db=Math.abs(bEnd-b0);
	    
	    // set step increment to be positive or negative 
	    int step_r = r0<rEnd ? 1 : -1;
	    int step_g = g0<gEnd ? 1 : -1;
	    int step_b = b0<bEnd ? 1 : -1;
	    
	    // compute whole step in each color that is taken each time through loop
	    float whole_step_r = step_r*(dr/dx);
	    float whole_step_g = step_g*(dg/dx);
	    float whole_step_b = step_b*(db/dx);
	    
	    // compute remainder, which will be corrected depending on decision parameter
	    dr=dr%dx;
	    dg=dg%dx; 
	    db=db%dx;
	    
	    // initialize decision parameters for red, green, and blue
	    float p_r = 2 * dr - dx;
	    float twoDr = 2 * dr,  twoDrMinusDx = 2 * (dr - dx);
	    float r=r0;
	    
	    float p_g = 2 * dg - dx;
	    float twoDg = 2 * dg,  twoDgMinusDx = 2 * (dg - dx);
	    float g=g0;
	    
	    float p_b = 2 * db - dx;
	    float twoDb = 2 * db,  twoDbMinusDx = 2 * (db - dx);
	    float b=b0;
	    
	    // draw start pixel
	    if(x_y_role_swapped)
	    {
	    	if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()){
	    		//check depth buffer
	    		int new_z=z;
	    		int new_x=y;
	    		int new_y=buff.getHeight()-x-1;
	    		if (d_buff[new_x][new_y]<new_z){
	    			//r, g and b are interpreted as the x,y,z vector coordinates of the surface normal
	    			Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
	    			ColorType color = new ColorType();
	    			//the normal is used to apply the light equation
	    			color = light.applyLight(material, view_vector, normal, color, new Vector3D(new_x,new_y,new_z));
	    			//real r,g and b values are obtained for the pixel
	    			int r_ = color.getR_int();
	    			int g_ = color.getG_int();
	    			int b_ = color.getB_int();
	    			buff.setRGB(new_x, new_y, (r_<<16) | (g_<<8) | b_);
	    			d_buff[new_x][new_y]=new_z;
	    		}
	    	}
	    }
	    else
	    {
	    	if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()){
	    		//check depth buffer
	    		int new_z=z;
	    		int new_x=x;
	    		int new_y=buff.getHeight()-y-1;
	    		if (d_buff[new_x][new_y]<new_z){
	    			//r, g and b are interpreted as the x,y,z vector coordinates of the surface normal
    				Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
    				ColorType color = new ColorType();
    				//the normal is used to apply the light equation
    				color = light.applyLight(material, view_vector, normal, color, new Vector3D(new_x,new_y,new_z));
    				//real r,g and b values are obtained for the pixel
    				int r_ = color.getR_int();
    				int g_ = color.getG_int();
    				int b_ = color.getB_int();
    				buff.setRGB(new_x, new_y, (r_<<16) | (g_<<8) | b_);
	    			d_buff[new_x][new_y] =new_z;
	    		}
	    	}
	    }
	    
	    while (x != xEnd) 
	    {
	    	// increment x and y
	    	x+=step_x;
	    	z+=step_z;
	    	if (p < 0)
	    		p += twoDy;
	    	else 
	    	{
	    		y+=step_y;
	    		p += twoDyMinusDx;
	    	}
		        
	    	// increment r by whole amount slope_r, and correct for accumulated error if needed
	    	r+=whole_step_r;
	    	if (p_r < 0)
	    		p_r += twoDr;
	    	else 
	    	{
	    		r+=step_r;
	    		p_r += twoDrMinusDx;
	    	}
		    
	    	// increment g by whole amount slope_b, and correct for accumulated error if needed  
	    	g+=whole_step_g;
	    	if (p_g < 0)
	    		p_g += twoDg;
	    	else 
	    	{
	    		g+=step_g;
	    		p_g += twoDgMinusDx;
	    	}
		    
	    	// increment b by whole amount slope_b, and correct for accumulated error if needed
	    	b+=whole_step_b;
	    	if (p_b < 0)
	    		p_b += twoDb;
	    	else 
	    	{
	    		b+=step_b;
	    		p_b += twoDbMinusDx;
	    	}
		    
	    	if(x_y_role_swapped)
	    	{
	    		if(x>=0 && x<buff.getHeight() && y>=0 && y<buff.getWidth()){
	    			//check depth buff
	    			int new_x=y;
	    			int new_y=buff.getHeight()-x-1;
	    			int new_z=z;
		    		if (d_buff[new_x][new_y]<new_z){
		    			//r, g and b are interpreted as the x,y,z vector coordinates of the surface normal
	    				Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
	    				ColorType color = new ColorType();
	    				//the normal is used to apply the light equation
	    				color = light.applyLight(material, view_vector, normal, color, new Vector3D(new_x, new_y, new_z));
	    				int r_ = color.getR_int();
	    				int g_ = color.getG_int();
	    				int b_ = color.getB_int();
	    				//real r,g and b values are obtained for the pixel
	    				buff.setRGB(new_x, new_y, (r_<<16) | (g_<<8) | b_);
		    			d_buff[new_x][new_y]=new_z;
		    		}
	    		}
	    	}
	    	else
	    	{
	    		if(y>=0 && y<buff.getHeight() && x>=0 && x<buff.getWidth()){
	    			//check depth buff
	    			int new_x=x;
	    			int new_y=buff.getHeight()-y-1;
	    			int new_z=z;
		    		if (d_buff[new_x][new_y]<new_z){
		    			//r, g and b are interpreted as the x,y,z vector coordinates of the surface normal
	    				Vector3D normal = new Vector3D((float)r, (float)g, (float)b);
	    				ColorType color = new ColorType();
	    				//the normal is used to apply the light equation
	    				color = light.applyLight(material, view_vector, normal, color, new Vector3D(new_x,new_y,new_z));
	    				int r_ = color.getR_int();
	    				int g_ = color.getG_int();
	    				int b_ = color.getB_int();
	    				//real r,g and b values are obtained for the pixel
	    				buff.setRGB(new_x, new_y, (r_<<16) | (g_<<8) | b_);
		    			d_buff[new_x][new_y]=new_z;
		    		}
	    		}
	    	}
	    }
	}

	/**********************************************************************
	 * Draws a filled triangle. 
	 * The triangle may be filled using flat fill or smooth fill. 
	 * This routine fills columns of pixels within the left-hand part, 
	 * and then the right-hand part of the triangle.
	 *   
	 *	                         *
	 *	                        /|\
	 *	                       / | \
	 *	                      /  |  \
	 *	                     *---|---*
	 *	            left-hand       right-hand
	 *	              part             part
	 *
	 * @param buff
	 *          Buffer object.
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @param do_smooth
	 *          Flag indicating whether flat fill or smooth fill should be used.                   
	 */
	public static void drawTriangle(BufferedImage buff, int[][] d_buff, PhongVector3D p1, PhongVector3D p2, PhongVector3D p3, InfiniteLight light, Material material, Vector3D view_vector)
	{
	    // sort the triangle vertices by ascending x value
	    PhongVector3D p[] = sortTriangleVerts(p1,p2,p3);
	    
	    int x; 
	    float y_a, y_b;
	    float dy_a, dy_b;
	    float dr_a=0, dg_a=0, db_a=0, dr_b=0, dg_b=0, db_b=0;
	    
	    PhongVector3D side_a = new PhongVector3D(p[0]), side_b = new PhongVector3D(p[0]);
	    
	    y_b = p[0].y;
	    dy_b = ((float)(p[2].y - p[0].y))/(p[2].x - p[0].x);
	    
    	// calculate slopes in r, g, b for segment b
	    // using nx as r, ny as g and nz as b
    	dr_b = ((float)(p[2].nx - p[0].nx))/(p[2].x - p[0].x);
    	dg_b = ((float)(p[2].ny - p[0].ny))/(p[2].x - p[0].x);
    	db_b = ((float)(p[2].nz - p[0].nz))/(p[2].x - p[0].x);
	    
	    // if there is a left-hand part to the triangle then fill it
	    if(p[0].x != p[1].x)
	    {
	    	y_a = p[0].y;
	    	dy_a = ((float)(p[1].y - p[0].y))/(p[1].x - p[0].x);

    		// calculate slopes in r, g, b for segment a
	    	// using nx as r, ny as g and nz as b
    		dr_a = ((float)(p[1].nx - p[0].nx))/(p[1].x - p[0].x);
    		dg_a = ((float)(p[1].ny - p[0].ny))/(p[1].x - p[0].x);
    		db_a = ((float)(p[1].nz - p[0].nz))/(p[1].x - p[0].x);
		    
		    // loop over the columns for left-hand part of triangle
		    // filling from side a to side b of the span
		    for(x = (int)p[0].x; x < p[1].x; ++x)
		    {
		    	drawLine(buff, d_buff, side_a, side_b, light, material, view_vector);

		    	++side_a.x;
		    	++side_b.x;
		    	y_a += dy_a;
		    	y_b += dy_b;
		    	side_a.y = (int)y_a;
		    	side_b.y = (int)y_b;

	    		side_a.nx +=dr_a;
	    		side_b.nx +=dr_b;
	    		side_a.ny +=dg_a;
	    		side_b.ny +=dg_b;
	    		side_a.nz +=db_a;
	    		side_b.nz +=db_b;

		    }
	    }
	    
	    // there is no right-hand part of triangle
	    if(p[1].x == p[2].x)
	    	return;
	    
	    // set up to fill the right-hand part of triangle 
	    // replace segment a
	    side_a = new PhongVector3D(p[1]);
	    
	    y_a = p[1].y;
	    dy_a = ((float)(p[2].y - p[1].y))/(p[2].x - p[1].x);

    	// calculate slopes in r, g, b for replacement for segment a
	    // using nx as r, ny as g, nz as b
    	dr_a = ((float)(p[2].nx - p[1].nx))/(p[2].x - p[1].x);
    	dg_a = ((float)(p[2].ny - p[1].ny))/(p[2].x - p[1].x);
    	db_a = ((float)(p[2].nz - p[1].nz))/(p[2].x - p[1].x);

	    // loop over the columns for right-hand part of triangle
	    // filling from side a to side b of the span
	    for(x = (int)p[1].x; x <= p[2].x; ++x)
	    {
	    	drawLine(buff, d_buff, side_a, side_b, light, material, view_vector);
		    
	    	++side_a.x;
	    	++side_b.x;
	    	y_a += dy_a;
	    	y_b += dy_b;
	    	side_a.y = (int)y_a;
	    	side_b.y = (int)y_b;

    		side_a.nx +=dr_a;
    		side_b.nx +=dr_b;
    		side_a.ny +=dg_a;
    		side_b.ny +=dg_b;
    		side_a.nz +=db_a;
    		side_b.nz +=db_b;

	    }
	}

	/**********************************************************************
	 * Helper function to bubble sort triangle vertices by ascending x value.
	 * 
	 * @param p1
	 *          First given vertex of the triangle.
	 * @param p2
	 *          Second given vertex of the triangle.
	 * @param p3
	 *          Third given vertex of the triangle.
	 * @return 
	 *          Array of 3 points, sorted by ascending x value.
	 */
	private static PhongVector3D[] sortTriangleVerts(PhongVector3D p1, PhongVector3D p2, PhongVector3D p3)
	{
	    PhongVector3D pts[] = {p1, p2, p3};
	    PhongVector3D tmp;
	    int j=0;
	    boolean swapped = true;
	         
	    while (swapped) 
	    {
	    	swapped = false;
	    	j++;
	    	for (int i = 0; i < 3 - j; i++) 
	    	{                                       
	    		if (pts[i].x > pts[i + 1].x) 
	    		{                          
	    			tmp = pts[i];
	    			pts[i] = pts[i + 1];
	    			pts[i + 1] = tmp;
	    			swapped = true;
	    		}
	    	}                
	    }
	    return(pts);
	}

}