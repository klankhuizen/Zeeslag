//// import kruisjenulletje.Board;
//// import kruisjenulletje.Game;
//// import kruisjenulletje.Player;
//import java.util.List;
//import java.util.ArrayList;
//
//public class AIPlayer extends Player {
//    private static final int MAX_DEPTH = 5;
//    private char opponentChar;
//    public AIPlayer(int ID, String name, char character) {
//        super(ID, name, character);
//    }
//
//    // Berekent de zet voor de AI-speler op basis van het huidige bord.
//    public void calculateMove(Board board) {
//        int[] bestMove = minimax(board, this, 0);
//        board.makeMove(this, bestMove[0], bestMove[1]);
//    }
//
//    private int[] minimax(Board board, Player player, int depth) {
//        // Controleert of het spel voorbij is of de maximale diepte is bereikt.
//        if (board.checkGameOver(player) || depth >= MAX_DEPTH) {
//            int score = evaluate(board, player);
//            return new int[]{-1, -1, score};
//        }
//
//        List<int[]> moves = generateMoves(board);
//        int bestScore = (player == this) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
//        int[] bestMove = new int[]{-1, -1};
//
//        // De eerste zet in het midden te doen
//        if (depth == 0) {
//            if (board.getBoardWidth() % 2 == 1 && board.getBoardHeight() % 2 == 1) {
//
//                // Berekenen van de coordinaten van het middelste vak
//                int middleX = board.getBoardWidth() / 2;
//                int middleY = board.getBoardHeight() / 2;
//
//                // Controleerd of het middenste vak leeg is
//                if (board.getChar(middleX, middleY) == 0) {
//                    return new int[]{middleX, middleY, 0};
//                }
//            }
//        }
//
//        for (int[] move : moves) {
//            board.makeMove(player, move[0], move[1]);
//            Player opponent = (player == this) ? board.getOpponent(this) : this;
//            // Roept minimax recursief aan voor de volgende zet.
//            int[] currentMove = minimax(board, opponent, depth + 1);
//            // Zet ongedaan maken om alle mogelijkheden langs te gaan
//            board.undoMove(move[0], move[1]);
//
//
//            // bestMove en bestScore worden geupdate aan de hand van de huidige zet
//            if (player == this) {
//                if (currentMove[2] > bestScore) {
//                    bestScore = currentMove[2];
//                    bestMove[0] = move[0];
//                    bestMove[1] = move[1];
//                }
//            } else {
//                if (currentMove[2] < bestScore) {
//                    bestScore = currentMove[2];
//                    bestMove[0] = move[0];
//                    bestMove[1] = move[1];
//                }
//            }
//        }
//
//        // Als er geen eerste zet is gevonden plaats hem in een hoek (niet zeker of dit werkt)
//        if (depth == 0 && bestMove[0] == -1 && bestMove[1] == -1) {
//            int[][] cornerMoves = {{0, 0}, {0, board.getBoardHeight() - 1},
//                    {board.getBoardWidth() - 1, 0}, {board.getBoardWidth() - 1, board.getBoardHeight() - 1}};
//
//            for (int[] move : cornerMoves) {
//                if (board.getChar(move[0], move[1]) == 0) {
//                    return new int[]{move[0], move[1], 0};
//                }
//            }
//        }
//
//        return bestMove;
//    }
//
//    // Evalueert het bord en geeft een score terug voor de speler.
//    private int evaluate(Board board, Player player) {
//
//        if (board.checkWin(player)) {
//            return 10; // Winst voor de speler.
//        }
//
//        if (board.checkWin(opponentChar)) {
//            return -10;  // Winst voor de tegenstander (niet zeker of dit zo gaat werken)
//        }
//
//        if (board.checkBoardFull()) {
//            return 0; // Het bord is vol, het resultaat is een gelijkspel.
//        }
//
//        return 0; // Standaardwaarde als geen van de voorwaarden is voldaan.
//    }
//
//
//    // Geeft alle lege vakken weer
//    private List<int[]> generateMoves(Board board) {
//        List<int[]> moves = new ArrayList<>();
//        //Doormiddel van een for loop door alle vakken heen gaan
//        for (int row = 0; row < board.getBoardWidth() -1 ; row++) {
//
//            for (int col = 0; col < board.getBoardHeight() -1; col++) {
//                // Controleerd of het vak leeg is
//                if (board.getChar(row, col) == 0) {
//                    // Voeg lege posities toe aan de lijst van mogelijke zetten.
//                    moves.add(new int[]{row, col});
//                }
//            }
//        }
//
//        return moves; // Geeft de lijst van mogelijke zetten terug.
//    }
//
//    // ...
//
//    public void AIMove(Board board) {
//        if (currentMove.getType() == Player) {
//            int[] bestMove = minimax(board, currentMove);
//            int x = bestMove[0];
//            int y = bestMove[1];
//
//            if (board.getChar(x, y) == 0) {
//                board.setChar(x, y, currentMove.getTileType());
//            }
//        }
//
//        // Rest van de code ontbreekt en moet worden aangevuld om de functionaliteit van de AI-speler te begrijpen.
//    }
//
//    // ...
//
//    public int[] minimax(Board currentBoard, Player currentPlaying) {
//        int[] bestMove = new int[]{-1, -1};
//        int bestScore = Integer.MIN_VALUE;
//
//        for (int i = 0; i < currentBoard.getBoardWidth(); i++) {
//            for (int j = 0; j < currentBoard.getBoardHeight(); j++) {
//                if (currentBoard.isCellEmpty(i, j)) {
//                    currentBoard.setTile(i, j, currentPlayer.getTileType);
//                    int score = minimaxRecursive(currentBoard, currentPlayer, false);
//                    currentBoard.setTile(i, j, null);
//
//                    if (score > bestScore) {
//                        bestScore = score;
//                        bestMove[0] = i;
//                        bestMove[1] = j;
//                    }
//                }
//            }
//        }
//
//        return bestMove; // Geeft de beste zet terug op basis van het minimax-algoritme.
//    }
//}
//
//
