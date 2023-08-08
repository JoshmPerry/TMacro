package macro;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.MouseInfo;
import java.util.Scanner;
import Txt.TEditor;
import java.awt.MouseInfo;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

public class MacroController {
	
	private String file;
	private double[] timer = new double[300];
	
	public MacroController(String file) {
		file = this.file;
		fixfileSource();
	}
	public MacroController() {
		this("null");
	}
	public void changeFile(String Nfile) {
		file = Nfile;
		fixfileSource();
	}
	public void fixfileSource() {
		try {
			if(!file.substring(file.length()-4).equals(".txt"))
				file+=".txt";
		}catch(Exception e) {
			file+=".txt";
		}
	}
	public void writeDelay(TEditor filer, long delay) {
		filer.lnwrite("delay"+delay);
	}
	public void writePress(TEditor filer, int letter) {
		filer.lnwrite("press"+letter);
	}
	public void writeRelease(TEditor filer, int letter) {
		filer.lnwrite("letgo"+letter);
	}
	public void write(TEditor filer,String sequence) {
		filer.lnwrite(sequence);
	}
	public int getChar(int i) {
		if(i==189)
			return 45;
		else if(i==187)
			return 61;
		else if(i==190)
			return 46;
		else if(i==191)
			return 47;
		else if(i==219)
			return 91;
		else if(i==221)
			return 93;
		else if(i==186)
			return 59;
		else if(i==188)
			return 44;
		return i;
	}
	public void keyboardAuto(char stop) {
		timer[200]=System.nanoTime();
		TEditor filer = new TEditor(file);
		GlobalKeyboardHook keyboard = new GlobalKeyboardHook(true);
		
		filer.eraseEntireFile();
		
		keyboard.addKeyListener(new GlobalKeyAdapter() {
			
			@Override public void keyPressed(GlobalKeyEvent event) {
				int Tstop = stop;
				if(Tstop>96&&Tstop<123)
					Tstop-=32;
				if(event.getVirtualKeyCode() == (Tstop))
					keyboard.shutdownHook();
				if(timer[event.getVirtualKeyCode()] == 0.0) {
					timer[event.getVirtualKeyCode()] = System.nanoTime();
					long tmp = (long) (System.nanoTime()-timer[200]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
					writePress(filer,event.getVirtualKeyCode());
					timer[200] = System.nanoTime();
				}
				
			}
			@Override public void keyReleased(GlobalKeyEvent event) {
				long tmp = (long) (System.nanoTime() - timer[event.getVirtualKeyCode()]);
				writeDelay(filer,tmp);
				writeRelease(filer,event.getVirtualKeyCode());
				timer[event.getVirtualKeyCode()]=0.0;
			}
		});
	}
	
	public void keyboardPlay() {
		String line="";
		try {
			Robot r = new Robot();
			TEditor filer = new TEditor(file);
			for(int i=1;i<=filer.numLines();i++) {
				try {
				if(filer.readLine(i).length()>6) {
				line=filer.readLine(i).substring(0,5);
				if(line.equals("delay")) {
					Thread.sleep((long)(Double.parseDouble(filer.readLine(i).substring(5))/1000000));
				}else if(line.equals("press")) {
					r.keyPress(Integer.parseInt(filer.readLine(i).substring(5)));
				}else if(line.equals("letgo")) {
					r.keyRelease(Integer.parseInt(filer.readLine(i).substring(5)));
				}}
				}catch(Exception e) {
					System.out.println("Error with line: "+i);
				}
			}
			
			
		} catch(Exception e) {
			System.out.println("Error: "+e);
		}
	}
	
	public void mouseAuto(char stop) {
		timer[201]=System.nanoTime();
		TEditor filer = new TEditor(file);
		GlobalKeyboardHook keyboard = new GlobalKeyboardHook(true);
		GlobalMouseHook mouse = new GlobalMouseHook(true);
		
		filer.eraseEntireFile();
		
		keyboard.addKeyListener(new GlobalKeyAdapter() {
			
			@Override public void keyPressed(GlobalKeyEvent event) {
				int Tstop = stop;
				if(Tstop>96&&Tstop<123)
					Tstop-=32;
				if(event.getVirtualKeyCode()==Tstop) {
					mouse.shutdownHook();
					keyboard.shutdownHook();
			}}});
		mouse.addMouseListener(new GlobalMouseAdapter() {
			@Override public void mousePressed(GlobalMouseEvent event)  {
				System.out.println(event);
				if((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT)!=GlobalMouseEvent.BUTTON_NO) {
					if(timer[202]==0) {
						//System.out.println("L1");
						write(filer,"lefty1");
					}
					timer[202]=System.nanoTime();
					long tmp = (long) (System.nanoTime()-timer[201]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
				}else if((event.getButtons()&GlobalMouseEvent.BUTTON_RIGHT)!=GlobalMouseEvent.BUTTON_NO) {
					if(timer[203]==0) {
						//System.out.println("R1");
						write(filer,"right1");
					}
					timer[203]=System.nanoTime();
					long tmp = (long) (System.nanoTime()-timer[201]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
					
				}
				
			}
			
			
			@Override public void mouseReleased(GlobalMouseEvent event)  {
				if((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT)==GlobalMouseEvent.BUTTON_NO) {
					//System.out.println("L0");
					write(filer,"lefty0");
					timer[202]=0;
					long tmp = (long) (System.nanoTime()-timer[201]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
				}else if((event.getButtons()&GlobalMouseEvent.BUTTON_RIGHT)==GlobalMouseEvent.BUTTON_NO) {
					//System.out.println("R0");
					write(filer,"right0");
					timer[203]=0;
					long tmp = (long) (System.nanoTime()-timer[201]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
					
				}
				}
			@Override public void mouseMoved(GlobalMouseEvent event) {
				if((int)event.getX()!=0||(int)event.getY()!=0) {
				long tmp = (long) (System.nanoTime()-timer[201]);
				if(tmp!=0.0)
					writeDelay(filer,tmp);
				write(filer,"mouse"+(int)MouseInfo.getPointerInfo().getLocation().getX()+","+(int)MouseInfo.getPointerInfo().getLocation().getY());
				timer[201]=System.nanoTime();
				
				
				}}
		});
		
		
	}
	
	public void mousePlay() {
		String line="";
		try {
			Robot r = new Robot();
			TEditor filer = new TEditor(file);
			for(int i=1;i<=filer.numLines();i++) {
				try {
				if(filer.readLine(i).length()>5) {
				line=filer.readLine(i).substring(0,5);
				if(line.equals("delay")) {
					Thread.sleep((long)(Double.parseDouble(filer.readLine(i).substring(5))/1000000));
				}else if(line.equals("mouse")) {
					r.mouseMove(Integer.parseInt(filer.readLineBeforeChar(i,",").substring(5)),Integer.parseInt(filer.readLineAfterChar(i,",")));
				}else if(line.equals("lefty")) {
					if(filer.readLine(i).substring(5).equals("1")) {
						r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					}else {
						r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					}
				}else if(line.equals("righty")) {
					if(filer.readLine(i).substring(5).equals("1")) {
						r.mousePress(InputEvent.BUTTON2_DOWN_MASK);
					}else {
						r.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
					}
				}}
				}catch(Exception e) {
					System.out.println("Error with line: "+i);
					e.printStackTrace();
				}
			}
			
			
		} catch(Exception e) {
			System.out.println("Error: "+e);
		}
	}
	
	public void auto(char stop) {
		timer[200]=System.nanoTime();
		TEditor filer = new TEditor(file);
		GlobalKeyboardHook keyboard = new GlobalKeyboardHook(true);
		GlobalMouseHook mouse = new GlobalMouseHook(true);
		
		filer.eraseEntireFile();
		
		keyboard.addKeyListener(new GlobalKeyAdapter() {
			
			@Override public void keyPressed(GlobalKeyEvent event) {
				int Tstop = stop;
				if(Tstop>96&&Tstop<123)
					Tstop-=32;
				if(event.getVirtualKeyCode() == (Tstop)) {
					mouse.shutdownHook();
					keyboard.shutdownHook();
				}
				if(timer[event.getVirtualKeyCode()] == 0.0) {
					timer[event.getVirtualKeyCode()] = System.nanoTime();
					long tmp = (long) (System.nanoTime()-timer[200]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
					writePress(filer,event.getVirtualKeyCode());
					timer[200] = System.nanoTime();
				}
				
			}
			@Override public void keyReleased(GlobalKeyEvent event) {
				long tmp = (long) (System.nanoTime() - timer[200]);
				writeDelay(filer,tmp);
				writeRelease(filer,event.getVirtualKeyCode());
				timer[200]=System.nanoTime();
				timer[event.getVirtualKeyCode()]=0.0;
			}
		});
		mouse.addMouseListener(new GlobalMouseAdapter() {
			@Override public void mousePressed(GlobalMouseEvent event)  {
				System.out.println(event);
				if((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT)!=GlobalMouseEvent.BUTTON_NO) {
					if(timer[202]==0) {
						//System.out.println("L1");
						write(filer,"lefty1");
					}
					timer[202]=System.nanoTime();
					long tmp = (long) (System.nanoTime()-timer[200]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
				}else if((event.getButtons()&GlobalMouseEvent.BUTTON_RIGHT)!=GlobalMouseEvent.BUTTON_NO) {
					if(timer[203]==0) {
						//System.out.println("R1");
						write(filer,"right1");
					}
					timer[203]=System.nanoTime();
					long tmp = (long) (System.nanoTime()-timer[200]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
					
				}
				
			}
			
			
			@Override public void mouseReleased(GlobalMouseEvent event)  {
				if((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT)==GlobalMouseEvent.BUTTON_NO) {
					//System.out.println("L0");
					write(filer,"lefty0");
					timer[202]=0;
					long tmp = (long) (System.nanoTime()-timer[200]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
				}else if((event.getButtons()&GlobalMouseEvent.BUTTON_RIGHT)==GlobalMouseEvent.BUTTON_NO) {
					//System.out.println("R0");
					write(filer,"right0");
					timer[203]=0;
					long tmp = (long) (System.nanoTime()-timer[200]);
					if(tmp!=0.0)
						writeDelay(filer,tmp);
					
				}
				}
			@Override public void mouseMoved(GlobalMouseEvent event) {
				if((int)event.getX()!=0||(int)event.getY()!=0) {
				long tmp = (long) (System.nanoTime()-timer[200]);
				if(tmp!=0.0)
					writeDelay(filer,tmp);
				write(filer,"mouse"+(int)MouseInfo.getPointerInfo().getLocation().getX()+","+(int)MouseInfo.getPointerInfo().getLocation().getY());
				timer[200]=System.nanoTime();
				
				
				}}
		});
	}
	
	public void play() {
		String line="";
		try {
			Robot r = new Robot();
			TEditor filer = new TEditor(file);
			for(int i=1;i<filer.numLines();i++) {
				try {
				if(filer.readLine(i).length()>5) {
				line=filer.readLine(i).substring(0,5);
				if(line.equals("delay")) {
					Thread.sleep((long)(Double.parseDouble(filer.readLine(i).substring(5))/1000000));
				}else if(line.equals("mouse")) {
					r.mouseMove(Integer.parseInt(filer.readLineBeforeChar(i,",").substring(5)),Integer.parseInt(filer.readLineAfterChar(i,",")));
				}else if(line.equals("lefty")) {
					if(filer.readLine(i).substring(5).equals("1")) {
						r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					}else {
						r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					}
				}else if(line.equals("righty")) {
					if(filer.readLine(i).substring(5).equals("1")) {
						r.mousePress(InputEvent.BUTTON2_DOWN_MASK);
					}else {
						r.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
					}
				}else if(line.equals("press")) {
					if(Integer.parseInt(filer.readLine(i).substring(5))!=13)
					r.keyPress(getChar(Integer.parseInt(filer.readLine(i).substring(5))));
				}else if(line.equals("letgo")) {
					if(Integer.parseInt(filer.readLine(i).substring(5))!=13)
					r.keyRelease(getChar(Integer.parseInt(filer.readLine(i).substring(5))));
				}
				
				}
				}catch(Exception e) {
					System.out.println("Error with line: "+i);
					System.out.println(Integer.parseInt(filer.readLine(i).substring(5)));
					//e.printStackTrace();
				}
			}
			
			
		} catch(Exception e) {
			System.out.println("Error: "+e);
		}
	}
	
	public static void main(String[] args){
		MacroController aa = new MacroController();
		System.out.println("To stop recording, press q");
		while(true) {
			System.out.println("Type r for record and p for play");
			Scanner scan = new Scanner(System.in);
			if(scan.nextLine().equals("r")) {
				aa.auto('q');
			}else {
				aa.play();
		}
		}
	}
	
}
