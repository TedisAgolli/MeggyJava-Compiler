/**
 * PA4nodef.java
 * 
 * call undefined method
 *   
 * WB, 3/12
 */

import meggy.Meggy;

class PA4noDef {

    public static void main(String[] whatever){
	new C().setP((byte)3,(byte)7,Meggy.Color.BLUE);
    }
}

class C {
    
    public void setP(byte x, byte y, Meggy.Color c) {
            Meggy.setPixel(x, y, c);  
	this.setP2( x, y,  c  );
    }

public void setP2(byte x, byte y, Meggy.Color c) {
            Meggy.setPixel(x, y, c);    
    }
    
}