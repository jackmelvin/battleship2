package com.jm.battleship;

import java.net.*;
import java.io.*;
import java.util.*;

class GameServer {
//	Socket[] players;
//	ObjectOutputStream[] out;
//	ObjectInputStream[] in;
//	int shootingPlayer;
//	int waitingPlayer;
//	boolean isPlaying = true;
//	MainActivity main;
//	int portToTry = 5229;
//
//	public GameServer(MainActivity main) {
//		this.main = main;
//		Thread t = new Thread(this);
//		t.start();
//	}
//
//	@Override
//	public void run() {
//		ServerSocket serverSock = null;
//		while (serverSock == null) {
//			try {
//				serverSock = new ServerSocket(portToTry);
//				int connectedClients = 0;
//				while (connectedClients < 2) {
//					players[connectedClients++] = serverSock.accept();
//				}
//				out = new ObjectOutputStream[2];
//				out[0] = new ObjectOutputStream(players[0].getOutputStream());
//				out[1] = new ObjectOutputStream(players[1].getOutputStream());
//				in = new ObjectInputStream[2];
//				in[0] = new ObjectInputStream(players[0].getInputStream());
//				in[1] = new ObjectInputStream(players[1].getInputStream());
//			} catch (BindException e) {
//				portToTry++;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		Random rand = new Random();
//		shootingPlayer = rand.nextInt(2);
//		if (shootingPlayer == 0) {
//			waitingPlayer = 1;
//		} else {
//			waitingPlayer = 0;
//		}
//
//		startGame();
//	}
//
//	private void startGame() {
//		main.startGame(portToTry);
//		while (isPlaying) {
//			tellPlayerToShoot();
//		}
//	}
//
//	private void write(int playerToWriteTo, Object obj) {
//		try {
//			out[playerToWriteTo].writeObject(obj);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private Object read(int playerToReadFrom) {
//		Object obj = null;
//		try {
//			obj = in[playerToReadFrom].readObject();
//		} catch (EOFException e) {
//			//client disconnected
//			endGame();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return obj;
//	}
//
//	private void tellPlayerToShoot() {
//		write(shootingPlayer, "SHOOT");
//		Object obj = read(shootingPlayer);
//		if (obj instanceof String) {
//			String cellLocation = (String) obj;
//			ShootResult result = tellPlayerToCheckShootResult(cellLocation);
//			write(shootingPlayer, result);
//			if (result == ShootResult.HIT || result == ShootResult.KILL || result == ShootResult.END) {
//				Object shipNameObj = read(waitingPlayer);
//				write(shootingPlayer, shipNameObj);
//			}
//			if (result == ShootResult.END) {
//				write(shootingPlayer, "WIN");
//				write(waitingPlayer, "LOSE");
//				endGame();
//			}
//			endTurn();
//		}
//	}
//
//	private ShootResult tellPlayerToCheckShootResult(String cellLocation) {
//		write(waitingPlayer, "CHECK");
//		write(waitingPlayer, cellLocation);
//		Object obj = read(waitingPlayer);
//		ShootResult result = null;
//		if (obj instanceof ShootResult) {
//			result = (ShootResult) obj;
//		}
//		return result;
//	}
//
//	private void endTurn() {
//		if (shootingPlayer == 0) {
//			shootingPlayer = 1;
//			waitingPlayer = 0;
//		} else {
//			shootingPlayer = 0;
//			waitingPlayer = 1;
//		}
//	}
//
//	private void endGame() {
//		isPlaying = false;
//	}
}