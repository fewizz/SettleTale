package ru.settletale.util;

import java.util.Scanner;

import ru.settletale.command.CommandExecuter;

public class ConsoleThread extends Thread {
	
	
	@Override
	public void run() {
		Scanner scan = new Scanner(System.in);
		
		for(;;) {
			String str = scan.nextLine();
			CommandExecuter.execute(str);
			scan.close();
		}
	}
}
