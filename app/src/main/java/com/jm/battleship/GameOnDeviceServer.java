package com.jm.battleship;

import java.io.*;
import java.net.*;
import java.util.Random;

class GameOnDeviceServer implements Runnable {
	private int port = 5228;
	private boolean serverStarted = false;

	int start() {
		Thread t = new Thread(this);
		t.start();
		while (!serverStarted){}
		return port;
	}

	@Override
	public void run() {
		ServerSocket serverSock = null;
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
			while (connectedPlayerNum < 2) {
				players[connectedPlayerNum] = serverSock.accept();
				connectedPlayerNum++;
				if (connectedPlayerNum == 2) {
					new GamePlay(players);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class GamePlay {
		public static final String SHOOT = "Shoot";
		public static final String WAIT = "Wait";
		public static final String LOSE = "Lose";
		public static final String WIN = "Win";
		public static final String MISS = "Miss";
		public static final String OPPONENT_DISCONNECTED = "OpponentDisconnected";
		Socket[] players;
		PrintWriter[] out;
		BufferedReader[] in;
		int shootingPlayer;
		int waitingPlayer;
		boolean isPlaying = true;

		public GamePlay(Socket[] players) {
			this.players = players;
			setUpGame();
		}

		void setUpGame() {
			try {
				out = new PrintWriter[2];
				out[0] = new PrintWriter(players[0].getOutputStream());
				out[1] = new PrintWriter(players[1].getOutputStream());
				in = new BufferedReader[2];
				in[0] = new BufferedReader(new InputStreamReader(players[0].getInputStream()));
				in[1] = new BufferedReader(new InputStreamReader(players[1].getInputStream()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			Random rand = new Random();
			shootingPlayer = rand.nextInt(2);
			if (shootingPlayer == 0) {
				waitingPlayer = 1;
			} else {
				waitingPlayer = 0;
			}

			startGame();

			//getp1board
			//getp2board
			//send enemy board to each player
		}

		private void startGame() {
			while (isPlaying) {
				tellPlayerToShoot();
			}
		}

		private void write(int playerToWriteTo, String command) {
			out[playerToWriteTo].println(command);
			out[playerToWriteTo].flush();
		}

		private String read(int playerToReadFrom) {
			String command = null;
			try {
				command = in[playerToReadFrom].readLine();
				if (command == null) {
					// client disconnected
					int playToWrite = (playerToReadFrom == 0) ? 1 : 0;
					write(playToWrite, OPPONENT_DISCONNECTED);
					isPlaying = false;
				}
			} catch (Exception e) {
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
			}
			if (result.equals(MISS)) {
				endTurn();
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
		}
	}
}