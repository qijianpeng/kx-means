package util;

import java.util.Comparator;

import entity.Side;

public class Compare implements Comparator<Side> {

	@Override
	public int compare(Side side1, Side side2) {
		// TODO Auto-generated method stub
		if(side1.getSideLength() < side2.getSideLength())return -1;
		else if(side1.getSideLength() == side2.getSideLength())return 0;
		else return 1;
	}

}
