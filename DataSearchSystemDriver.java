package nxg150630;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class DataSearchSystemDriver {
    public static void main(String[] args) throws Exception {
        Scanner in;
        if (args.length > 0 && !args[0].equals("-")) {
            File file = new File(args[0]);
            in = new Scanner(file);
        } else {
            in = new Scanner(System.in);
        }
        boolean VERBOSE = false;
        if (args.length > 1) { VERBOSE = Boolean.parseBoolean(args[1]); }

        String operation = "";
        int lineno = 0;

        DataSearchSystem dss = new DataSearchSystem();
        Timer timer = new Timer();
        int id, result, total = 0, price;
        List<Integer> name = new LinkedList<>();

        whileloop:
        while (in.hasNext()) {
	    lineno++;
	    result = 0;
	    operation = in.next();
	    if(operation.charAt(0) == '#') {
		in.nextLine();
		continue;
	    }
	    switch (operation) {
	    case "stop":
		break whileloop;
	    case "add":
		id = in.nextInt();
		price = in.nextInt();
		name.clear();
		while(true) {
		    int val = in.nextInt();
		    if(val == 0) { break; }
		    else { name.add(val); }
		}
		result = mds.insert(id, price, name);
//                              System.out.println("adding:\nid is " + id + "\nprice is " + price + "\nname is " + name);
                              mds.treeMap.get(id).printDesc();
                              System.out.println(result == 1 ? "The Item was new\n" : "The Item already existed\n");
		break;
	    case "find":
		id = in.nextInt();
		result = mds.find(id);
                              System.out.println("given the id:" + id + "\nthe value price is:" + result);
		break;
	    case "del":
		id = in.nextInt();
                              result = mds.delete(id);
                              System.out.println("The sum of deleted values is: " + result);
		break;
	    case "min":
		result = mds.findMinPrice(in.nextInt());
                              System.out.println("The min price containing the value is: " + result);
		break;
	    case "max":
		result = mds.findMaxPrice(in.nextInt());
                              System.out.println("The max price containing the value is: " + result);
		break;
	    case "range":
                              int n = in.nextInt();
                              int l = in.nextInt();
                              int h = in.nextInt();
		result = mds.findPriceRange(n, l, h);
                              System.out.println("The sum of descriptions that contained " + n + " in the price range from [" + l + " to " + h + "] is " + result);
		break;
	    case "remove":
		id = in.nextInt();
		name.clear();
		while(true) {
		    int val = in.nextInt();
		    if(val == 0) { break; }
		    else { name.add(val); }
		}
		result = mds.removeNames(id, name);
                              System.out.println("The removed names sum is: " + result);
		break;
	    default:
		System.out.println("Unknown operation: " + operation);
	    }
	    total += result;
	    if(VERBOSE) { System.out.println(lineno + "\t" + operation + "\t" + result + "\t" + total); }
	}
	System.out.println(total);
	System.out.println(timer.end());
    }

    public static class Timer {
	long startTime, endTime, elapsedTime, memAvailable, memUsed;

	public Timer() {
	    startTime = System.currentTimeMillis();
	}

	public void start() {
	    startTime = System.currentTimeMillis();
	}

	public Timer end() {
	    endTime = System.currentTimeMillis();
	    elapsedTime = endTime-startTime;
	    memAvailable = Runtime.getRuntime().totalMemory();
	    memUsed = memAvailable - Runtime.getRuntime().freeMemory();
	    return this;
	}

	public String toString() {
	    return "Time: " + elapsedTime + " msec.\n" + "Memory: " + (memUsed/1048576) + " MB / " + (memAvailable/1048576) + " MB.";
	}
    }
}

