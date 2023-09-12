package hw2;

import api.PlayerPosition;

import api.BallType;
import static api.PlayerPosition.*;
import static api.BallType.*;

import java.util.Random;

/**
 * Class that models the game of three-cushion billiards.
 * 
 * @author Steven Bui
 */
public class ThreeCushion {

	// Instance Variables

	// tracks the player in play
	private PlayerPosition currentPlayer;

	// variable if its breakShot or not
	private boolean cueBreak = true;

	// tracks inning
	private int currentInning;

	// whichever cue ball that wasn't chosen first
	private boolean secondCueBall = false;

	// if shot is started
	private boolean shotStarted;

	// tracks which inning games on
	private boolean inningStatus;

	// player A score
	private int playerAtotal;

	// player B score
	private int playerBtotal;

	// which ball is cue
	private BallType cueBall;

	// amount of cushion impact
	private int cushionImpact;

	// total of points needed to win
	private int winTotal;

	// stops all code if game is over
	private boolean gameOver;

	// if shot has ended
	private boolean shotEnd = false;

	// if red ball was hit
	private boolean redBall = false;

	// if there was a bankshot
	private boolean bankShot;

	// whoever won lag
	private PlayerPosition lagWinner;

	/**
	 * 
	 * @param playerA
	 * @param winningTotal Creates a new game of three-cushion billiards with a
	 *                     given lag winner and the predetermined number of points
	 *                     required to win the game.
	 */

	public ThreeCushion(PlayerPosition lagWinner, int winningTotal) {
		this.lagWinner = lagWinner;
		currentInning = 1;
		cueBall = WHITE;
		currentPlayer = PLAYER_A;
		playerAtotal = 0;
		playerBtotal = 0;
		winTotal = winningTotal;
		cueBreak = true;

	}

	/**
	 * Prints out all status's of the game.
	 */

	public String toString() {
		String fmt = "Player A%s: %d, Player B%s: %d, Inning: %d %s%s";
		String playerATurn = "";
		String playerBTurn = "";
		String inningStatus = "";
		String gameStatus = "";

		if (getInningPlayer() == PLAYER_A) {
			playerATurn = "*";
		} else if (getInningPlayer() == PLAYER_B) {
			playerBTurn = "";
		}
		if (isInningStarted()) {
			inningStatus = "started";
		} else {
			inningStatus = "not started";
		}
		if (isGameOver()) {
			gameStatus = ", game result final";
		}
		return String.format(fmt, playerATurn, getPlayerAScore(), playerBTurn, getPlayerBScore(), getInning(),
				inningStatus, gameStatus);
	}

	/**
	 * returns Inning.
	 * 
	 * @return
	 */

	public int getInning() {

		return currentInning;
	}

	/**
	 * returns Player B's score.
	 * 
	 * @return
	 */

	public int getPlayerBScore() {

		return playerBtotal;
	}

	/**
	 * returns Player A's score.
	 * 
	 * @return
	 */

	public int getPlayerAScore() {

		return playerAtotal;
	}

	/**
	 * Sets whether the player that won the lag chooses to break (take first shot),
	 * or chooses the other player to break. If this method is called more than once
	 * it should have no effect. In other words, the lag winner can only choose
	 * these options once and may not change their mind afterwards.
	 * 
	 * @param breakChoice
	 * @param cue
	 */

	public void lagWinnerChooses(boolean breakChoice, BallType cue) {
		if (cueBreak) {
			if (lagWinner == PLAYER_A) {
				if (breakChoice == true) {
					cueBall = cue;
					currentPlayer = PLAYER_A;
					// if breakChoice is false it'll make the other player have the first shot and
					// other cue ball
				} else if (breakChoice == false) {
					if (currentPlayer == PLAYER_A && cue == WHITE) {
						currentPlayer = PLAYER_B;
						cue = YELLOW;
					} else if (currentPlayer == PLAYER_A && cue == YELLOW) {
						currentPlayer = PLAYER_B;
						cue = WHITE;
					}

				}
			} else if (lagWinner == PLAYER_B) {
				if (breakChoice == true) {
					cueBall = cue;
					currentPlayer = PLAYER_B;
					// if breakChoice is false it'll make the other player have the first shot and
					// other cue ball
				} else if (breakChoice == false) {
					if (currentPlayer == PLAYER_B && cue == WHITE) {
						currentPlayer = PLAYER_A;
						cue = YELLOW;
					} else if (currentPlayer == PLAYER_B && cue == YELLOW) {
						currentPlayer = PLAYER_A;
						cue = WHITE;
					}
				}
			}
		}

	}

	/**
	 * A ball strike cannot happen before a stick strike. If this method is called
	 * before the start of a shot (i.e., cueStickStrike() is called), it should do
	 * nothing. If this method is called after the game has ended, it should do
	 * nothing.
	 * 
	 * @param ball
	 */

	public void cueBallStrike(BallType ball) {
		if (gameOver != true) {
			if (shotStarted) {
				if (ball == RED) {
					redBall = true;
				} else if (ball == YELLOW || ball == WHITE) {
					secondCueBall = true;
				}
			}
		}
	}

	/**
	 * A cushion impact cannot happen before a stick strike. If this method is
	 * called before the start of a shot (i.e., cueStickStrike() is called), it
	 * should do nothing.
	 */

	public void cueBallImpactCushion() {
		if (shotStarted) {
			cushionImpact += 1;
		}
		if (redBall == false && secondCueBall == false && cushionImpact >= 3) {
			bankShot = true;
		}
		if (cueBreak == false && redBall == false) {
			foul();
		}

	}

	public PlayerPosition getInningPlayer() {
		return currentPlayer;
	}

	public void cueStickStrike(BallType ball) {
		cueBreak = true;
		if (cueBreak == true && gameOver == false) {
			shotStarted = true;
			inningStatus = true;
			// will be foul since the ball was not the cue ball
			if (shotEnd == true || ball != cueBall) {
				foul();

			}
		}

	}

	/**
	 * A foul immediately ends the player's inning, even if the current shot has not
	 * yet ended. When a foul is called, the player does not score a point for the
	 * shot. A foul may also be called before the inning has started. In that case
	 * the player whose turn it was to shot has their inning forfeited and the
	 * inning count is increased by one.
	 * 
	 * No foul can be called until the lag player has chosen who will break (see
	 * lagWinnerChooses()). If this method is called before the break is chosen, it
	 * should do nothing.
	 * 
	 * If this method is called after the game has ended, it should do nothing.
	 */

	public void foul() {
		shotStarted = false;
		inningStatus = false;
		if (currentPlayer == PLAYER_A) {
			currentPlayer = PLAYER_B;
		} else {
			currentPlayer = PLAYER_A;
		}
		if (cueBall == WHITE) {
			cueBall = YELLOW;
		} else {
			cueBall = WHITE;
		}
		currentInning += 1;
	}

	/**
	 * Indicates that all balls have stopped motion. If the shot was valid and no
	 * foul was committed, the player scores 1 point. The shot cannot end before it
	 * has started with a call to cueStickStrike. If this method is called before
	 * cueStickStrike, it should be ignored.
	 * 
	 * A shot cannot end before the start of a shot. If this method is called before
	 * the start of a shot (i.e., cueStickStrike() is called), it should do nothing.
	 */

	public void endShot() {
		if (gameOver != true) {
			if (inningStatus == true) {
				shotStarted = false;
				cueBreak = false;
				// scoring shot
				if (redBall == true && secondCueBall == true && cushionImpact >= 3) {
					if (currentPlayer == PLAYER_A) {
						playerAtotal += 1;
						if (winTotal == playerAtotal) {
							gameOver = true;
						}
						redBall = false;
						secondCueBall = false;
						cushionImpact = 0;
					}
					if (currentPlayer == PLAYER_B) {
						if (winTotal == playerBtotal) {
							gameOver = true;
						}
						playerBtotal += 1;
						redBall = false;
						secondCueBall = false;
						cushionImpact = 0;
					}
					// not a scoring shot
				} else {
					currentInning += 1;
					inningStatus = false;
					redBall = false;
					secondCueBall = false;
					cushionImpact = 0;
					bankShot = false;
					if (currentPlayer == PLAYER_A) {
						currentPlayer = PLAYER_B;
					} else {
						currentPlayer = PLAYER_B;
					}
					if (cueBall == WHITE) {
						cueBall = YELLOW;
					} else {
						cueBall = WHITE;
					}

				}
				if (playerAtotal >= winTotal || playerBtotal >= winTotal) {
					gameOver = true;
				}
			}
		}
	}

	/**
	 * returns boolean whether the shot started or not.
	 * 
	 * @return
	 */

	public boolean isShotStarted() {
		return shotStarted;

	}

	/**
	 * returns the cue ball
	 * 
	 * @return
	 */

	public BallType getCueBall() {
		return cueBall;
	}

	/**
	 * returns true or falase, whether or not its the break shot.
	 * 
	 * @return
	 */

	public boolean isBreakShot() {
		return cueBreak;
	}

	/**
	 * returns true or false, inning status.
	 * 
	 * @return
	 */

	public boolean isInningStarted() {
		return inningStatus;
	}

	/**
	 * returns true or false if the game is over.
	 * 
	 * @return
	 */

	public boolean isGameOver() {
		return gameOver;
	}

	/**
	 * returns true or false if the hit was a bank shot
	 * 
	 * @return
	 */

	public boolean isBankShot() {
		return bankShot;
	}

}
