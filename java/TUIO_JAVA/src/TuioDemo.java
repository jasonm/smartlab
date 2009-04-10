/*
	TUIO Java backend - part of the reacTIVision project
	http://reactivision.sourceforge.net/

	Copyright (c) 2005-2008 Martin Kaltenbrunner <mkalten@iua.upf.edu>

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import TUIO.*;

public class TuioDemo extends JComponent implements TuioListener {

	private Hashtable<Long,TuioDemoObject> objectList = new Hashtable<Long,TuioDemoObject>();
	private Hashtable<Long,TuioCursor> cursorList = new Hashtable<Long,TuioCursor>();
	
	private final int window_width  = 640;
	private final int window_height = 480;
    
	public static final int finger_size = 15;
	public static final int object_size = 60;
	public static final int table_size = 760;

	private boolean[] calibrationDone = new boolean[4];
	private Point2D[] calibrationPoint = new Point2D[4];
	private Point2D[] calibrationInput = new Point2D[4];
	private Point2D calibrationOffset;
	private Point2D calibrationScale;
	
	public static int width, height;
	float scale;
	
	private boolean fullscreen = false;
	private boolean verbose = false;
	private boolean calibration = false;
	
	private JFrame frame;
	private GraphicsDevice device;

	private BufferedImage offScreenImage;
	
	public TuioDemo() {
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		resetCalibration();
		setupWindow();
		showWindow();
	}
	
	public void addTuioObject(TuioObject tobj) {
		TuioDemoObject demo = new TuioDemoObject(tobj);
		objectList.put(tobj.getSessionID(),demo);
		
		if (verbose) 
			System.out.println("add obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle());	
	}

	public void updateTuioObject(TuioObject tobj) {

		TuioDemoObject demo = (TuioDemoObject)objectList.get(tobj.getSessionID());
		demo.update(tobj);
		
		if (verbose) 
			System.out.println("set obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+") "+tobj.getX()+" "+tobj.getY()+" "+tobj.getAngle()+" "+tobj.getMotionSpeed()+" "+tobj.getRotationSpeed()+" "+tobj.getMotionAccel()+" "+tobj.getRotationAccel()); 	
	}
	
	public void removeTuioObject(TuioObject tobj) {
		objectList.remove(tobj.getSessionID());
		
		if (verbose) 
			System.out.println("del obj "+tobj.getSymbolID()+" ("+tobj.getSessionID()+")");	
	}

	public void addTuioCursor(TuioCursor tcur) {
	
		if (calibration) {
			doCalibration(tcur.getX(),tcur.getY());
			return;
		}
	
		if (!cursorList.containsKey(tcur.getSessionID())) {
			cursorList.put(tcur.getSessionID(), tcur);
			repaint();
		}
		
		if (verbose) 
			System.out.println("add cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+") "+tcur.getX()+" "+tcur.getY());	
	}

	public void updateTuioCursor(TuioCursor tcur) {

		if (calibration) {
			doCalibration(tcur.getX(),tcur.getY());
			return;
		}
	
		repaint();
		
		if (verbose) 
			System.out.println("set cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+") "+tcur.getX()+" "+tcur.getY()+" "+tcur.getMotionSpeed()+" "+tcur.getMotionAccel()); 
	}
	
	public void removeTuioCursor(TuioCursor tcur) {
	
		cursorList.remove(tcur.getSessionID());	
		repaint();
		
		if (verbose) 
			System.out.println("del cur "+tcur.getCursorID()+" ("+tcur.getSessionID()+")"); 
	}

	public void refresh(TuioTime bundleTime) {
		repaint();
	}
	
	public void paint(Graphics g) {
		update(g);
	}


	private void resetCalibration() {

		calibrationPoint[0] = new Point2D.Float(0.30f,0.25f);
		calibrationPoint[1] = new Point2D.Float(0.70f,0.25f);
		calibrationPoint[2] = new Point2D.Float(0.30f,0.75f);
		calibrationPoint[3] = new Point2D.Float(0.70f,0.75f);

		calibrationInput[0] = new Point2D.Float(0.30f,0.25f);
		calibrationInput[1] = new Point2D.Float(0.70f,0.25f);
		calibrationInput[2] = new Point2D.Float(0.30f,0.75f);
		calibrationInput[3] = new Point2D.Float(0.70f,0.75f);
		
		calibrationDone[0] = false;
		calibrationDone[1] = false;
		calibrationDone[2] = false;
		calibrationDone[3] = false;
		
		calibrationOffset = new Point2D.Float(0.0f,0.0f);
		calibrationScale = new Point2D.Float(1.0f,1.0f);
	}

	private void doCalibration(float xpos, float ypos) {
	
		for (int i=0;i<calibrationPoint.length;i++) {
			float dx = (float)calibrationPoint[i].getX()-xpos;
			float dy = (float)calibrationPoint[i].getY()-ypos;
			
			if ((Math.abs(dx)<0.2) && (Math.abs(dy)<0.2)) {
			
				calibrationInput[i] = new Point2D.Float(xpos,ypos);
				
				calibrationOffset = new Point2D.Float((float)(calibrationPoint[0].getX()-calibrationInput[0].getX()),(float)(calibrationPoint[0].getY()-calibrationInput[0].getY()));
				float xscale = ((float)calibrationInput[1].getX() - (float)calibrationInput[0].getX())/0.4f;
				float yscale = ((float)calibrationInput[2].getY() - (float)calibrationInput[0].getY())/0.5f;
				calibrationScale = new Point2D.Float(xscale,yscale);
				
				calibrationDone[i] = true;
				repaint();
				return;
			}
		}
	}

	private void paintCalibration(Graphics2D g) {
		g.setColor(Color.white);
		g.fill(new Rectangle2D.Float(0,0,width,height));		
		g.setColor(Color.black);
		g.draw(new Ellipse2D.Float((width-height)/2,0,height,height));		
		
		for (int i=0;i<calibrationPoint.length;i++) {
			g.setColor(Color.black);
			g.fill(new Ellipse2D.Float((float)(calibrationPoint[i].getX()*width-10*scale),(float)(calibrationPoint[i].getY()*height-10*scale),20*scale,20*scale));
			if (calibrationDone[i]) g.setColor(Color.green);
			else g.setColor(Color.red);
			g.fill(new Ellipse2D.Float((float)(calibrationPoint[i].getX()*width-5*scale),(float)(calibrationPoint[i].getY()*height-5*scale),10*scale,10*scale));
		}
	}

	public void update(Graphics g) {
	
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		if (calibration) {
			paintCalibration(g2);
			return;
		}

		g2.setColor(Color.white);
		g2.fill(new Rectangle2D.Float(0,0,width,height));		
	
		Graphics2D bg = (Graphics2D)offScreenImage.getGraphics();
		bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		bg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		bg.setColor(Color.white);
		bg.fillRect(0,0,width,height);

		//bg.setColor(Color.black);
		//bg.draw(new Ellipse2D.Float((width-height)/2,0,height,height));		
		
		int w = (int)Math.round(width-scale*finger_size/2.0f);
		int h = (int)Math.round(height-scale*finger_size/2.0f);
		
		Enumeration<TuioCursor> cursors = cursorList.elements();
		while (cursors.hasMoreElements()) {
			TuioCursor tcur = cursors.nextElement();
			if (tcur==null) continue;
			Vector<TuioPoint> path = tcur.getPath();
			TuioPoint current_point = path.elementAt(0);
			if (current_point!=null) {
				// draw the cursor path
				bg.setPaint(Color.blue);
				for (int i=0;i<path.size();i++) {
					TuioPoint next_point = path.elementAt(i);
					bg.drawLine(current_point.getScreenX(w), current_point.getScreenY(h), next_point.getScreenX(w), next_point.getScreenY(h));
					current_point = next_point;
				}
			}
			
			// draw the finger tip
			bg.setPaint(Color.lightGray);
			int s = (int)(scale*finger_size);
			bg.fillOval(current_point.getScreenX(w-s/2),current_point.getScreenY(h-s/2),s,s);
			bg.setPaint(Color.black);
			bg.drawString(tcur.getCursorID()+"",current_point.getScreenX(w),current_point.getScreenY(h));
		}

		// draw the objects
		Enumeration<TuioDemoObject> objects = objectList.elements();
		while (objects.hasMoreElements()) {
			TuioDemoObject tobj = objects.nextElement();
			if (tobj!=null) tobj.paint(bg);
		}
		
		AffineTransform transform = new AffineTransform();
		transform.translate(-width*calibrationPoint[0].getX(),-height*calibrationPoint[0].getY());
		transform.scale(calibrationScale.getX(),calibrationScale.getY());
		transform.translate(width*calibrationInput[0].getX(),height*calibrationInput[0].getY());
		g2.drawImage(offScreenImage,transform,this);
	}

	public void setupWindow() {
	
		frame = new JFrame();
		frame.add(this);

		frame.setTitle("TuioDemo");
		frame.setResizable(false);

		frame.addWindowListener( new WindowAdapter() { public void windowClosing(WindowEvent evt) {
				System.exit(0);
			} });
		
		frame.addKeyListener( new KeyAdapter() { public void keyPressed(KeyEvent evt) {
			if (evt.getKeyCode()==KeyEvent.VK_ESCAPE) System.exit(0);
			else if (evt.getKeyCode()==KeyEvent.VK_F1) {
				destroyWindow();
				setupWindow();
				fullscreen = !fullscreen;
				showWindow();
			}
			else if (evt.getKeyCode()==KeyEvent.VK_V) verbose=!verbose;
			else if (evt.getKeyCode()==KeyEvent.VK_C) {
				if (!calibration) {
					resetCalibration();
					calibration = true;
				} else calibration = false;
				repaint();
			}
		} });
	}
	
	public void destroyWindow() {
	
		frame.setVisible(false);
		if (fullscreen) {
			device.setFullScreenWindow(null);		
		}
		frame = null;
	}
	
	public void showWindow() {
	
		if (fullscreen) {
			width  = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			scale  = height/(float)table_size;

			frame.setSize(width,height);
			frame.setUndecorated(true);
			device.setFullScreenWindow(frame);		
		} else {
			width  = window_width;
			height = window_height;
			scale = height/(float)table_size;

			frame.pack();
			Insets insets = frame.getInsets();			
			frame.setSize(width,height +insets.top);
		}
		
		offScreenImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		frame.setVisible(true);
		frame.repaint();

	}
	
	public static void main(String argv[]) {
		
		TuioDemo demo = new TuioDemo();
		TuioClient client = null;
 
		switch (argv.length) {
			case 1:
				try { client = new TuioClient( Integer.parseInt(argv[1])); }
				catch (Exception e) {}
				break;
			case 0:
				client = new TuioClient();
				break;
		}

		if (client!=null) {
			client.addTuioListener(demo);
			client.connect();
		} else {
			System.out.println("usage: java TuioDemo [port]");
			System.exit(0);
		}
	}
	
}
