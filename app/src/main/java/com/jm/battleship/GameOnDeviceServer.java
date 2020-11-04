package com.jm.battleship;

import java.io.*;
import java.net.*;
import java.util.Random;

import static com.jm.battleship.GameManager.*;

class GameOnDeviceServer implements Runnable {
	private int port = 5228;
	private volatile boolean serverStarted = false;
	private ServerSocket serverSock;
	private GamePlay game;

	int start() {
		Thread t = new Thread(this);
		t.start();
		while (!serverStarted){}
		return port;
	}

	@Override
	public void run() {
		serverSock = null;
		while (serverSock == null && port <= 65535) {
			try {
				serverSock = new ServerSocket(port);
			} catch (BindException e) {
				// Port in use
				// Try another one
				port++;
			} catch (IOException e) {
				e.printStackTrace();
			}
			serverStarted = true;
		}
		try {
			int connectedPlayerNum = 0;
			Socket[] players = new Socket[2];
			PrintWriter[] writers = new PrintWriter[2];
			BufferedReader[] readers = new BufferedReader[2];
			while (connectedPlayerNum < 2) {
				players[connectedPlayerNum] = serverSock.accept();
				writers[connectedPlayerNum] = new PrintWriter(players[connectedPlayerNum].getOutputStream());
				readers[connectedPlayerNum] = new BufferedReader(new InputStreamReader(players[connectedPlayerNum].getInputStream()));
				connectedPlayerNum++;
				if (connectedPlayerNum == 2) {
					game = new GamePlay(writers, readers);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void endGame() {
		if (game != null) {
			game.endGame();
		}
		if (serverSock != null) {
			try {
				serverSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static class GamePlay {
		PrintWriter[] writers;
		BufferedReader[] readers;
		int shootingPlayer;
		int waitingPlayer;
		boolean isPlaying = true;

		public GamePlay(PrintWriter[] writers, BufferedReader[] readers) {
			this.writers = writers;
			this.readers = readers;
			setUpGame();
		}

		void setUpGame() {
			Random rand = new Random();
			shootingPlayer = rand.nextInt(2);
			if (shootingPlayer == 0) {
				waitingPlayer = 1;
			} else {
				waitingPlayer = 0;
			}

			startGame();
		}

		private void startGame() {
			write(0, START);
			write(1, START);
			while (isPlaying) {
				tellPlayerToShoot();
			}
		}

		private void write(int playerToWriteTo, String command) {
			writers[playerToWriteTo].println(command);
			writers[playerToWriteTo].flush();
		}

		private String read(int playerToReadFrom) {
			String command;
			try {
				command = readers[playerToReadFrom].readLine();
				if (command == null) {
					// client disconnected
					command = OPPONENT_DISCONNECTED;
					endGame();
				}
			} catch (IOException e) {
				command = OPPONENT_DISCONNECTED;
				endGame();
				e.printStackTrace();
			}
			return command;
		}

		private void tellPlayerToShoot() {
			write(shootingPlayer, SHOOT);
			write(waitingPlayer, WAIT);
			String cellLocation = read(shootingPlayer);
			String result = tellPlayerToCheckShootResult(cellLocation);
			if (result.equals(LOSE)) {
				write(shootingPlayer, WIN);
				write(waitingPlayer, LOSE);
				endGame();
			} else {
				write(shootingPlayer, result);
				if (result.equals(MISS)) {
					endTurn();
				}
			}
		}

		private String tellPlayerToCheckShootResult(String cellLocation) {
			write(waitingPlayer, cellLocation);
			return read(waitingPlayer);
		}

		private void endTurn() {
			if (shootingPlayer == 0) {
				shootingPlayer = 1;
				waitingPlayer = 0;
			} else {
				shootingPlayer = 0;
				waitingPlayer = 1;
			}
		}

		private void endGame() {
			isPlaying = false;
			if (writers[0] != null) {
				writers[0].close();
			}
			if (writers[1] != null) {
				writers[1].close();
			}
		}
	}
}