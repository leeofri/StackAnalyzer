package solution;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.TwoDArrayWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.ipc.WritableRpcEngine;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class StockWritable extends Configured implements Writable,
		Comparable<StockWritable> {

	// DM
	private Text stockName;
	private TwoDArrayWritable stock;

	public enum type {
		OPEN(0), CLOSE(1), HIGH(2), LOW(3);
		private final int value;

		private type(int value) {
			this.value = value;
		}
	}

	// public StockWritable(String name,int n,int m)
	// {
	// this.stockName = new Text(name);
	//
	// // create the stack vector
	// this.stock = new TwoDArrayWritable(DoubleWritable.class);
	// DoubleWritable[][] v = new DoubleWritable[n][m];
	// this.stock.set(v);
	// }
	public StockWritable() {
		this.stockName = new Text();

		// create the stack vector
		this.set(new TwoDArrayWritable(DoubleWritable.class));
	}

	public StockWritable(DoubleWritable[][] v, Text name)
	{
		this.StockWritable(v,name);
	}
	
	private void StockWritable(Writable[][] v, Text name) {
		this.stockName = new Text(name);

		// create the stack vector
		// create the tmp 2D array
		DoubleWritable[][] tmp2DArray = new DoubleWritable[v.length][];
		
	    for (int r = 0; r < v.length; r++) {
	    	DoubleWritable[] tmpArray = new DoubleWritable[v[r].length];
	    	for  (int c = 0; c < v[r].length; c++)
	    		tmpArray[c] = new DoubleWritable(Double.parseDouble(v[r][c].toString()));
	    	tmp2DArray[r] = tmpArray;
	    }
	    
	    
	    this.stock = new TwoDArrayWritable(DoubleWritable.class, tmp2DArray);
	}

	public StockWritable(StockWritable stock) {
		this.StockWritable((Writable[][])stock.stock.get(),stock.stockName);
		
	}

	public int getDaysNumber() {
		return (this.stock.get().length);
	}

	public int getDataTypsNumber() {
		return this.stock.get()[0].length;
	}

	public Text getName() {
		return this.stockName;

	}

	public void setName(String name) {
		this.stockName.set(name);
	}

	public TwoDArrayWritable get() {
		return this.stock;
	}

	public void set(TwoDArrayWritable Stock) {
		this.stock = Stock;
	}

	public void set(DoubleWritable[][] Stock) {
		this.stock = new TwoDArrayWritable(DoubleWritable.class);
		this.stock.set(Stock);
	}

	public StockWritable(TwoDArrayWritable v, Text name) {
		this.stockName = name;
		this.stock = v;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// write the name of the stack
		this.stockName.write(out);

		// write all the vector
		this.stock.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		try {
			// read the name of the stack
			this.stockName.readFields(in);

			// read all the vector
			this.stock.readFields(in);

		} catch (ClassCastException cce) {
			throw new IOException(cce);
		}
	}

	@Override
	public int compareTo(StockWritable o) {
		return this.stockName.compareTo(o.stockName);
	}

	public double distance(StockWritable o) {
		// calc the distance between all the four parameter of each day
		double distance = 0;

		// TODO: take down after fix input
		int minDays = Math.min(this.getDaysNumber(), o.getDaysNumber());

		for (int day = 0; day < minDays; day++) {
			for (int parameter = 0; parameter < this.getDataTypsNumber(); parameter++) {
				try {
					distance += Math
							.abs(((DoubleWritable) this.stock.get()[day][parameter])
									.get()
									- ((DoubleWritable) o.stock.get()[day][parameter])
											.get());
				} catch (Exception ex) {
					System.out.println("this- size:" + this.stock.get().length
							+ "," + this.stock.get()[day].length
							+ " & second- size: " + o.stock.get().length + ","
							+ this.stock.get()[day].length + "-> " + day + "*"
							+ parameter + " = " + day * parameter);
				}
			}
		}

		return distance;
	}
}
