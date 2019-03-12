/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.pkg3;

public class State {
    int aX, aY, bX, bY, cX, cY, pivotX, pivotY;
    int color;
    boolean isSquare;
    
    State() {
        aX = aY = bX = bY = cX = cY = pivotX = pivotY = color = 0;
        isSquare = false;
    }
    State(int c, int y0, int x0, int y2, int x2, 
                 int y3, int x3, int y4, int x4, boolean b) {
        color = c;
        pivotX = x0;
        pivotY = y0;
        aX = x2;
        aY = y2;
        bX = x3;
        bY = y3;
        cX = x4;
        cY = y4;
        isSquare = b;
    }
    State(int c, int y0, int x0, int y2, int x2, 
                 int y3, int x3, int y4, int x4) {
        color = c;
        pivotX = x0;
        pivotY = y0;
        aX = x2;
        aY = y2;
        bX = x3;
        bY = y3;
        cX = x4;
        cY = y4;
        isSquare = false;
    }
    
    public boolean contains(int x, int y) {
        return (x != aX && y != aY) || (x != bX && y != bY) ||
               (x != cX && y != cY) || (x != pivotX && y != pivotY);
    }
}
