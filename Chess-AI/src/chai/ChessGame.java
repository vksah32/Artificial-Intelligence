package chai;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class ChessGame {

	public Position position;

	public int rows = 8;
	public int columns = 8;

	public ChessGame() {
		position = new Position(
//				"1r3k2/p1rR1P1p/6p1/2b5/2q5/2B4P/PP3QP1/3R2K1 w KQkq - 0 1"//win in 2 steps
//				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"//starting state
//				"r5k1/p3Qpbp/2p3p1/1p6/q3bN2/6PP/PP3P2/K2RR3 b - - 0 1");
//		"1Bb1q2r/1p1k1p1p/3p1Q2/2pP4/5P2/2P5/P5PP/2KnR3 w - - 2 30"
//		"2r2k2/8/8/8/8/8/1K5r/8 w KQkq - 0 1"
//		"8/3k4/8/8/8/8/8/2K5 w - - 0 1"

		//test cases
//		"r5k1/p3Qpbp/2p3p1/1p6/q3bN2/6PP/PP3P2/K2RR3 b - - 0 1"////, black wins in 3
//		"6k1/Q4pp1/2pq2rp/3p2n1/4r3/2PN4/P4PPP/2R2R1K b - - 0 1" //, black wins in 3
		"8/4B2p/7n/4pqpk/P2n1b2/R3P2P/2r3P1/1Q3R1K b - - 0 1" //, black wins in 3,
//		"2kr3r/1bbp1p2/p3pp2/1p4q1/4P3/PNN1PBP1/1PP3KP/1R1Q1R2 b - - 0 1" //, black wins in 3, wins in 5
//		"5rk1/p4ppp/2b2q1P/7B/4p1Q1/5PR1/PPPb2R1/1K6 b - - 0 1" //, black wins in 3
//		"3q2k1/1pp2pp1/7p/5b2/1P6/2Q2P2/r5PP/2BrRK1R b - - 2 25" //, black wins in 3

//		"r2qk2r/pp6/2pbp3/2Pp1p2/3PBPp1/4PRp1/PP1BQ1P1/4R1K1 b kq - 0 20"//, black wins in 4
//		"5r2/p1p1p1k1/1p2prpp/4R3/3P2P1/P1P1Q3/2q1R2P/6K1 b - - 1 28"//, black wins in 5
//		"2rr2k1/Qp4pp/4bp2/4n3/4B3/1P2B1P1/P3q2P/RK5R b - - 1 24"//, black wins in 5
//		"8/8/8/8/8/k1B5/BN6/K7 w - - 0 1"//, white wins in 6
//		"2b1r3/1p2qpkp/2p3p1/4r3/R4Q2/2P2RP1/P1B2P1P/6K1 b - - 0 1"//, black wins in 6
//		"2rr2k1/Qp4pp/4bp2/1q2n3/4B3/1P2B1P1/PK2P2P/R6R b - - 0 23"//, black wins in 9
//		"7r/kp1PQ3/p7/2p5/1q1np3/8/PP1R3P/2K2B2 b - - 6 34"//, black wins in 12
//		"B2n4/7p/4p2p/4P1np/k1KP3p/8/1P2p3/4Bb2 w - - 0 1"//, white wins in 24

		);


	}

	public int getStone(int col, int row) {
		return position.getStone(Chess.coorToSqi(col, row));
	}
	
	public boolean squareOccupied(int sqi) {
		return position.getStone(sqi) != 0;
		
	}

	public boolean legalMove(short move) {
		
		for(short m: position.getAllMoves()) {
			if(m == move) return true;
		}
		System.out.println(java.util.Arrays.toString(position.getAllMoves()));
		System.out.println(move);
		return false;
	
	}

	// find a move from the list of legal moves from fromSqi to toSqi
	// return 0 if none available
	public short findMove(int fromSqi, int toSqi) {
		
		for(short move: position.getAllMoves()) {
			if(Move.getFromSqi(move) == fromSqi && 
					Move.getToSqi(move) == toSqi) return move;
		}
		return 0;
	}
	
	public void doMove(short move) {
		try {

			System.out.println("making move " + move);

			position.doMove(move);
			System.out.println(position);
		} catch (IllegalMoveException e) {
			System.out.println("illegal move!");
		}
	}

	public static void main(String[] args) {
		System.out.println();

		// Create a starting position using "Forsyth–Edwards Notation". (See
		// Wikipedia.)
		Position position = new Position(
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

		System.out.println(position);

	}
	
	

}
